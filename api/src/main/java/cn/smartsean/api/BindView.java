package cn.smartsean.api;

import android.app.Activity;
import android.view.View;

/**
 * @author SmartSean
 */

public class BindView {
    public static final String SUFFIX = "$$ViewInject";
    /**
     * 在Activity中使用
     * @param activity
     */
    public static void bind(Activity activity) {
        //根据传入的activity得到viewInject对象，并注入
        ViewInject viewInject = findProxyActivity(activity);
        viewInject.inject(activity, activity);
    }

    public static void bind(Object object, View view) {
        ViewInject viewInject = findProxyActivity(object);
        viewInject.inject(object, view);
    }

    /**
     * 查找我们生成的类
     * @param object
     * @return
     */
    private static ViewInject findProxyActivity(Object object) {
        try {
            Class clz = object.getClass();
            Class bindViewClass = Class.forName(clz.getName() + SUFFIX);
            return (ViewInject) bindViewClass.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        throw new RuntimeException(String.format("can not find %s , something when compiler.", object.getClass().getSimpleName() + SUFFIX));
    }
}
