package cn.smartsean.compile;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import static java.lang.reflect.Modifier.PRIVATE;

/**
 * @author SmartSean
 */

public class ClassUtils {
    /**
     * 是否是私有的
     * @param annotatedClass
     * @return
     */
    public static boolean isPrivate(Element annotatedClass) {
        return annotatedClass.getModifiers().contains(PRIVATE);
    }

    /**
     * 类名
     * @param type
     * @param packageName
     * @return
     */
    public static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen)
                .replace('.', '$');
    }
}
