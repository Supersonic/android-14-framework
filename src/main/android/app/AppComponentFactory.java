package android.app;

import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.Intent;
import android.content.p001pm.ApplicationInfo;
/* loaded from: classes.dex */
public class AppComponentFactory {
    public static final AppComponentFactory DEFAULT = new AppComponentFactory();

    public ClassLoader instantiateClassLoader(ClassLoader cl, ApplicationInfo aInfo) {
        return cl;
    }

    public Application instantiateApplication(ClassLoader cl, String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return (Application) cl.loadClass(className).newInstance();
    }

    public Activity instantiateActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return (Activity) cl.loadClass(className).newInstance();
    }

    public BroadcastReceiver instantiateReceiver(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return (BroadcastReceiver) cl.loadClass(className).newInstance();
    }

    public Service instantiateService(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return (Service) cl.loadClass(className).newInstance();
    }

    public ContentProvider instantiateProvider(ClassLoader cl, String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return (ContentProvider) cl.loadClass(className).newInstance();
    }
}
