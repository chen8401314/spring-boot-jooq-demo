package com.example.demo.service;

import com.example.demo.jooq.tables.daos.UserDao;
import com.example.demo.jooq.tables.pojos.UserEntity;
import com.example.demo.jooq.tables.records.UserRecord;
import com.huagui.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author chenx
 * @since 2020-11-09
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UserService extends ServiceImpl<UserDao, UserEntity, UserRecord> {

    private final UserDao userDao;

    public UserEntity findByUsername(String username) {
        List<UserEntity> list = userDao.fetchByUsernameTable(username);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

}
