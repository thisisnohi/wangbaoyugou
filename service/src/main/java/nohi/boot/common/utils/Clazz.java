package nohi.boot.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author NOHI
 * 2021-02-16 19:48
 **/
@Slf4j
public class Clazz {
    private static String SET = "set";
    private static String GET = "get";
    private static String OTHER = "other";
    private static String LEFT_KH = "[";
    private static String RIGHT_KH = "]";
    public Clazz() {
    }

    public static Method getMethod(Class c, String fileName, String methodType, Class parameterTypes) throws Exception {
        Method m = null;

        try {
            if (SET.equals(methodType)) {
                m = c.getMethod(SET+ covertFirstChar2Upper(fileName), parameterTypes);
            } else if (OTHER.equals(methodType)) {
                m = c.getMethod(fileName, parameterTypes);
            } else {
                m = c.getMethod(GET + covertFirstChar2Upper(fileName));
            }

            return m;
        } catch (Exception var6) {
            log.error(var6.getMessage(), var6);
            throw new Exception("获取对象[" + c + "]属性[" + fileName + "][" + methodType + "]方法异常", var6);
        }
    }

    public static String covertFirstChar2Upper(String str) {
        if (null != str && !"".equals(str.trim())) {
            StringBuffer sb = new StringBuffer();
            sb.append(Character.toUpperCase(str.charAt(0)));
            if (str.length() > 1) {
                sb.append(str.substring(1));
            }

            return sb.toString();
        } else {
            return "";
        }
    }

    public static Object getValue(Object obj, String property) throws Exception {
        String[] vm = property.split("\\.");
        int index = property.indexOf(".");

        try {
            if (null == obj) {
                return null;
            } else {
                Method method;
                if (index == -1) {
                    if (property.indexOf(LEFT_KH) >= 0 && property.endsWith(RIGHT_KH)) {
                        return getMapValue(obj, property);
                    } else {
                        method = getMethod(obj.getClass(), property, GET, (Class)null);
                        return null == method ? null : method.invoke(obj);
                    }
                } else {
                    method = getMethod(obj.getClass(), vm[0], GET, (Class)null);
                    Object temp = method.invoke(obj);
                    return getValue(temp, property.substring(index + 1));
                }
            }
        } catch (Exception var6) {
            log.error(var6.getMessage(), var6);
            throw new Exception("取类[" + obj.getClass() + "]的属性[" + property + "] 错误", var6);
        }
    }

    public static Object getMapValue(Object obj, String property) throws Exception {
        String key = property.substring(property.indexOf(LEFT_KH) + 1, property.indexOf(RIGHT_KH));
        if (null != key) {
            key = key.replace("'", "");
        }

        if (obj instanceof Map) {
            return ((Map)obj).get(key);
        } else {
            if (obj instanceof List) {
                System.out.println("is list");
            } else {
                String mapProperty = property.substring(0, property.indexOf(LEFT_KH));
                Method method = getMethod(obj.getClass(), mapProperty, GET, (Class)null);
                if (null == method) {
                    return null;
                }

                Object mapObj = method.invoke(obj);
                if (null != mapObj && mapObj instanceof Map) {
                    Map m = (Map)mapObj;
                    return m.get(key);
                }
            }

            return null;
        }
    }
}
