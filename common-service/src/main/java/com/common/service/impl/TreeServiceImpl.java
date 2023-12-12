package com.common.service.impl;


import com.common.service.handler.OperationException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.example.common.util.Consts;
import com.common.service.util.PathUtil;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.Field;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DAOImpl;
import org.jooq.impl.SQLDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * IService 实现类（ 泛型：M 是 dao 对象，T 是实体 ）
 *
 * @author cx
 */
@SuppressWarnings("unchecked")
@Slf4j
public abstract class TreeServiceImpl<M extends DAOImpl<R, T, String>, T, R extends UpdatableRecord<R>> {

    @Autowired
    public M baseDao;

    public T insert(T t) {
        baseDao.insert(t);
        return t;
    }

    public void insert(Collection<T> t) {
        baseDao.insert(t);
    }

    public T update(T t) {
        baseDao.update(t);
        return t;
    }

    public void update(List<T> t) {
        baseDao.update(t);
    }

    public List<T> findAll() {
        return baseDao.findAll();
    }

    public T findById(String id) {
        return baseDao.findById(id);
    }

    public void delete(T t) {
        baseDao.delete(t);
    }

    public void delete(List<T> t) {
        baseDao.delete(t);
    }

    public void deleteById(String id) {
        baseDao.deleteById(id);
    }

    public void deleteById(List<String> id) {
        baseDao.deleteById(id);
    }


    public static final String ID_STR = "id";

    public static final String PARENT_ID_STR = "parent_id";


    public static final String OUTLINE_STR = "outline";

    private Field idFiled = null;

    private Field getIdField() {
        if (idFiled == null) {
            idFiled = baseDao.getTable().fieldsRow().field(ID_STR, SQLDataType.CHAR(20));
        }
        return idFiled;
    }


    private Field parentIdFiled = null;

    private Field getParentIdFiled() {
        if (parentIdFiled == null) {
            parentIdFiled = baseDao.getTable().fieldsRow().field(PARENT_ID_STR, SQLDataType.CHAR(20));
        }
        return parentIdFiled;
    }

    private Field outlineFiled = null;

    private Field getOutlineFiled() {
        if (outlineFiled == null) {
            outlineFiled = baseDao.getTable().fieldsRow().field(OUTLINE_STR, SQLDataType.VARCHAR(300));
        }
        return outlineFiled;
    }


    public abstract String getId(T t);

    public abstract String getParentId(T t);

    public abstract String getOutline(T t);

    @Transactional
    public void move(@NotNull T source, T target) {
        move(source, target, Consts.STEP, Consts.DIGIT);
    }

    @SneakyThrows
    public void move(@NotNull T source, T target, int step, int digit) {
        String sourceParentId = getParentId(source);
        String sourceOutline = getOutline(source);

        String targetParentId = "";
        String targetOutline = "";
        if (target != null) {
            targetParentId = getParentId(target);
            targetOutline = getOutline(target);
        }
        if (target != null && !StringUtils.equals(sourceParentId, targetParentId)) {
            throw new OperationException("仅可在同一父级下移动!");
        }
        String parentOutline = PathUtil.getParentPath(sourceOutline);
        String preNum = target == null ? null : targetOutline;
        List<T> nodes = findMoveNodes(getId(source), sourceParentId, targetOutline);
        // 生成source新的outline
        String sourceNewOutline = PathUtil.next(parentOutline, preNum, 1, digit);
        // 需要更新的
        Map<T, String> map = Maps.newHashMap();
        // 父子级
        Map<T, List<T>> parentNodesMap = Maps.newHashMap();
        String temp = sourceNewOutline;
        // 处理
        for (T node : nodes) {
            int compare = getOutline(node).compareTo(temp);
            if (compare <= 0) {
                temp = PathUtil.next(parentOutline, temp, step, digit);
                // 放入map
                map.put(node, temp);
                // 查询所有子级
                parentNodesMap.put(node, findChildren(node));
            } else {
                // 排过序,只要有一个不匹配就可以直接跳出循环
                break;
            }
        }
        // 更新source
        updateOutline(source, findChildren(source), sourceNewOutline);
        // 更新后面的
        map.forEach((data, outline) -> updateOutline(data, parentNodesMap.get(data), outline));

    }

    public List<T> findChildren(T t) {
        return baseDao.ctx().select().from(baseDao.getTable()).where(getOutlineFiled().like(PathUtil.children(getOutline(t)))).orderBy(getOutlineFiled()).fetchInto(baseDao.getType());
    }


    private void updateOutline(T node, List<T> children, String outline) {
        if (CollectionUtils.isNotEmpty(children)) {
            children.forEach(child ->
                    updateOutline(getId(child), getOutline(child).replaceFirst(getOutline(node), outline)));
        }
        updateOutline(getId(node), outline);
    }

    private void updateOutline(String id, String outline) {
        baseDao.ctx().update(baseDao.getTable()).set(getOutlineFiled(), outline).where(getIdField().eq(id)).execute();
    }

    @SneakyThrows
    public List<T> findMoveNodes(String sourceId, String sourceParentId, String targetOutline) {
        var condition = getParentIdFiled().eq(sourceParentId).and(getIdField().ne(sourceId));
        if (StringUtils.isNotEmpty(targetOutline)) {
            condition = condition.and(getOutlineFiled().gt(targetOutline));
        }
        return baseDao.ctx().select().from(baseDao.getTable()).where(condition).orderBy(getOutlineFiled()).fetchInto(baseDao.getType());
    }

    @Transactional
    public void maintain() {
        maintain(findAll(), Consts.STEP, Consts.DIGIT);
    }

    private void maintain(List<T> tree, int step, int digit) {
        Pair<List<T>, Map<String, List<T>>> pair = handle(tree);
        // 处理
        String preOutLine = null;
        for (T root : pair.getLeft()) {
            String outLine = PathUtil.next(null, preOutLine, step, digit);
            updateOutline(getId(root), outLine);
            // 处理子级
            preprocessChildren(root, outLine, pair.getRight(), step, digit);
            preOutLine = outLine;
        }
    }

    private Pair<List<T>, Map<String, List<T>>> handle(List<T> tree) {
        // 根节点,parentId是空的
        List<T> roots = Lists.newArrayList();
        // 父子级数据map
        Map<String, List<T>> parentChildrenMap = Maps.newHashMap();
        tree.forEach(node -> {
            if (StringUtils.isBlank(getParentId(node))) {
                roots.add(node);
            } else {
                parentChildrenMap.computeIfAbsent(getParentId(node), v -> Lists.newArrayList()).add(node);
            }
        });
        // 排序
        roots.sort((s1, s2) -> {
            if (StringUtils.equals(getOutline(s1), getOutline(s1))) {
                return getId(s1).compareTo(getId(s2));
            } else {
                return getOutline(s1).compareTo(getOutline(s2));
            }
        });
        return Pair.of(roots, parentChildrenMap);
    }

    private void preprocessChildren(T parent, String outline, Map<String, List<T>> parentChildrenMap, int step, int digit) {
        List<T> children = parentChildrenMap.get(getId(parent));
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        // 根据原来的outLine进行排序,保证以前的顺序,避免原数据没有outLine
        children.sort((s1, s2) -> {
            if (StringUtils.equals(getOutline(s1), getOutline(s1))) {
                return getId(s1).compareTo(getId(s2));
            } else {
                return getOutline(s1).compareTo(getOutline(s2));
            }
        });
        String preOutLine = null;
        for (T child : children) {
            String outLine = PathUtil.next(outline, preOutLine, step, digit);
            updateOutline(getId(child), outLine);
            // 递归处理子级
            preprocessChildren(child, outLine, parentChildrenMap, step, digit);
            preOutLine = outLine;
        }
    }


    public T findFirstByParentIdOrderByOutlineDesc(String parentId) {
        if (StringUtils.isBlank(parentId)) {
            parentId = "";
        }
        List<T> list = baseDao.ctx().select().from(baseDao.getTable()).where(getParentIdFiled().eq(parentId)).orderBy(getOutlineFiled().desc()).fetchInto(baseDao.getType());
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public boolean existsByParentId(String parentId) {
        return baseDao.ctx().selectCount().from(baseDao.getTable()).where(getParentIdFiled().eq(parentId)).fetchOneInto(Integer.class) > 0;
    }

    public List<T> findByOutlineStartsWith(String outline) {
        return baseDao.ctx().select().from(baseDao.getTable()).where(getOutlineFiled().like(PathUtil.childrenSelf(outline))).fetchInto(baseDao.getType());
    }

    public List<T> findAllNodes(List<T> parents) {
        // 排序
        parents.sort(Comparator.comparing(t -> getOutline(t)));
        var temp = " ";
        List<T> nodes = Lists.newArrayList();
        for (T parent : parents) {
            // 父级查过了就不查了
            if (!StringUtils.startsWith(getOutline(parent), temp)) {
                temp = getOutline(parent);
                nodes.add(parent);
                nodes.addAll(findChildren(parent));
            }
        }
        return nodes;
    }

}

