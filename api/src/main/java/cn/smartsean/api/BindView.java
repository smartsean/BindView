package cn.smartsean.api;

import android.app.Activity;
import android.view.View;

/**
 * @author SmartSean
 */

public class BindView {

    public static final String SUFFIX = "$$ViewInject";

    public static void bind(Activity activity) {
        ViewInject viewInject = findProxyActivity(activity);
        viewInject.inject(activity, activity);
    }

    public static void bind(Object object, View view) {
        ViewInject viewInject = findProxyActivity(object);
        viewInject.inject(object, view);
    }

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
