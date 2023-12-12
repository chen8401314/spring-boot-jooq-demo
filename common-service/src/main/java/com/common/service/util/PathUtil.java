package com.common.service.util;

import com.common.service.handler.OperationException;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.example.common.util.Consts.*;


public class PathUtil {


    private static final List<Character> OPTIONS = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z');

    private static final int OPTIONS_SIZE = OPTIONS.size();

    private PathUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 下一个path
     *
     * @param path
     * @param step
     * @return
     */
    private static String next(String path, int step) {
        char[] chars = path.toCharArray();
        for (int i = chars.length - 1; i >= 0; i--) {
            if (step > 0) {
                int index = OPTIONS.indexOf(chars[i]);
                int sum = index + step;
                int nextCharIndex = sum % OPTIONS_SIZE;
                chars[i] = OPTIONS.get(nextCharIndex);
                step = sum / OPTIONS_SIZE;
            } else {
                break;
            }
        }
        // 循环结束了还有进位,超出限制了
        if (step != 0) {
            throw new OperationException("path超过限制");
        }
        return String.valueOf(chars);
    }

    public static String next(String parentPath, String prePath) {
        return next(parentPath, prePath, STEP, DIGIT);
    }

    public static String next(String parentPath, String prePath, int digit) {
        return next(parentPath, prePath, STEP, digit);
    }

    public static String next(String parentPath, String prePath, int step, int digit) {
        if (step <= 0) {
            // 默认步长
            step = STEP;
        }
        if (digit <= 0) {
            // 默认位数
            digit = DIGIT;
        }
        if (StringUtils.isBlank(parentPath)) {
            parentPath = "";
        } else {
            parentPath = parentPath + POINT;
        }
        if (StringUtils.isBlank(prePath)) {
            prePath = parentPath + StringUtils.leftPad("", digit, "0");
        }
        if (parentPath.length() >= prePath.length()) {
            // 父级长度大于前一个,有问题的
            throw new OperationException("参数错误");
        }
        String pre = prePath.substring(parentPath.length());
        if (pre.length() != digit) {
            // 前一个path截取到父级之后与传入的位数不一致,有问题
            throw new OperationException("位数不匹配");
        }
        return parentPath + next(pre, step);
    }

    public static String children(String path) {
        return path + "__%";
    }

    public static String childrenSelf(String path) {
        return path + "%";
    }

    public static String getLevelPath(String path, int level) {
        String[] split = path.split(REG_POINT);
        if (level < 1 || level > split.length) {
            throw new OperationException("错误层级");
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < level - 1; i++) {
            result.append(split[i]).append(POINT);
        }
        return result.append(split[level - 1]).toString();
    }

    public static int level(String path) {
        return StringUtils.countMatches(path, POINT) + 1;
    }

    public static Set<String> getAllParentPath(Collection<String> paths) {
        Set<String> result = Sets.newHashSet();
        paths.forEach(path -> result.addAll(getAllParentPath(path)));
        return result;
    }

    public static Set<String> allPath(Collection<String> paths) {
        Set<String> result = Sets.newHashSet();
        paths.forEach(path -> result.addAll(allPath(path)));
        return result;
    }

    public static List<String> getAllParentPath(String path) {
        return getAllParentPath(path, false);
    }

    public static List<String> allPath(String path) {
        List<String> result = getAllParentPath(path);
        result.add(path);
        return result;
    }

    public static List<String> getAllParentPath(String path, boolean asc) {
        return getAllParentPath(path, false, asc);
    }

    public static List<String> getAllParentPath(String path, boolean self, boolean asc) {
        List<String> result = Lists.newArrayList();
        if (self) {
            result.add(path);
        }
        while (path.lastIndexOf(POINT) > 0) {
            path = path.substring(0, path.lastIndexOf(POINT));
            result.add(path);
        }
        if (asc) {
            Collections.sort(result);
        }
        return result;
    }

    public static List<String> allPath(String path, boolean asc) {
        List<String> result = getAllParentPath(path);
        result.add(path);
        Collections.sort(result);
        if (!asc) {
            Collections.reverse(result);
        }
        return result;
    }


    public static String getParentPath(String path) {
        int lastIndexOf = path.lastIndexOf(POINT);
        if (lastIndexOf < 0) {
            return "";
        }
        return path.substring(0, lastIndexOf);
    }

    public static String decimalToThirtySixOutLineNum(String iSrcValue, String separatorChar, int levelLength) {
        List<String> elements = new ArrayList<>();
        for (String s : StringUtils.split(iSrcValue, separatorChar)) {
            String newElement = StringUtils.leftPad(decimalToThirtySix(Integer.parseInt(s)), levelLength, '0');
            if (StringUtils.length(newElement) > levelLength) {
                throw new OperationException(String.format("%s超出有效步长,无法转换", iSrcValue));
            }
            elements.add(newElement);
        }
        return String.join(".", elements);
    }

    public static String decimalToThirtySix(int iSrc) {
        String result = "";
        int key = iSrc / BASE_VALUE;
        int value = iSrc - key * BASE_VALUE;
        if (key != 0) {
            //递归
            result = result + decimalToThirtySix(key);
        }
        return result + OPTIONS.get(value).toString();
    }

}
