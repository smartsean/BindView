package cn.smartsean.compile;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * @author SmartSean
 */

public class ProxyInfo {
    /**
     * 包名
     */
    private String mPackageName;
    /**
     * 类名
     */
    private String mClassName;
    /**
     * 类或接口程序元素
     */
    private TypeElement mTypeElement;
    /**
     * 用于存储id和元素的HashMap
     */
    public Map<Integer, VariableElement> injectVariables = new HashMap<>();

    /**
     * 构造方法中初始化成员变量
     * @param elements
     * @param classElement
     */
    public ProxyInfo(Elements elements, TypeElement classElement) {
        this.mTypeElement = classElement;
        //获取包名
        PackageElement packageElement = elements.getPackageOf(classElement);
        String packageName = packageElement.getQualifiedName().toString();
        //获取类名
        String className = ClassUtils.getClassName(classElement, packageName);
        this.mClassName = className+"$$"+Constants.SUFFIX;
        this.mPackageName = packageName;
    }

    /**
     * 生成Java代码
     * @return
     */
    public String generatedJavaCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("//Generated code,Do not modify!")
                .append("\npackage ").append(mPackageName).append(";\n\n")
                .append("import cn.smartsean.api.*;\n\n")
                .append("public class ").append(mClassName).append(" implements " + Constants.SUFFIX + "<" + mTypeElement.getQualifiedName() + ">{\n\n");
        generatedMethods(builder);
        builder.append("\n}\n");
        return builder.toString();
    }

    /**
     * 生成方法
     * @param builder
     */
    private void generatedMethods(StringBuilder builder) {
        builder.append("\t@Override\n")
                .append("\tpublic void inject(" + mTypeElement.getQualifiedName() + " host,Object source) { \n ");
        for (Integer integer : injectVariables.keySet()) {
            VariableElement element = injectVariables.get(integer);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
            builder.append("\t\tif (source instanceof android.app.Activity){ \n")
                    .append("\t\t\thost." + name + " = (" + type + ")(((android.app.Activity)source).findViewById(" + integer + "));\n")
                    .append("\t\t}else {\n")
                    .append("\t\t\thost." + name + " = (" + type + ")(((android.view.View)source).findViewById(" + integer + "));\n")
                    .append("\t\t}");
        }
        builder.append("\n\t}");
    }

    /**
     * 获取全称
     * @return
     */
    public String getProxyClassFullName() {
        return this.mPackageName + "." + this.mClassName;
    }

    /**
     * 获取类型元素
     * @return
     */
    public TypeElement getTypeElement() {
        return mTypeElement;
    }
}
