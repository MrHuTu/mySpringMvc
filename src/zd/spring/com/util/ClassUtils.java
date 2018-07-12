package zd.spring.com.util;


/**
 * 软谋教育Java VIP课程
 * <pre>
 *    Class常用操作的工具类型
 * </pre>
 *
 * @author gerry
 * @date 2018-07-02
 */
public class ClassUtils {
    /**
     * 判断一个类是否是系统类
     * @param cl
     * @return
     */
    public static boolean isSystemClass(Class<?> cl) {
        System.out.println(cl.getClassLoader());
        return cl != null && cl.getClassLoader() == null;
    }
}
