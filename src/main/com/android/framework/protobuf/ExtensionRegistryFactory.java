package com.android.framework.protobuf;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public final class ExtensionRegistryFactory {
    static final Class<?> EXTENSION_REGISTRY_CLASS = reflectExtensionRegistry();
    static final String FULL_REGISTRY_CLASS_NAME = "com.android.framework.protobuf.ExtensionRegistry";

    ExtensionRegistryFactory() {
    }

    static Class<?> reflectExtensionRegistry() {
        try {
            return Class.forName(FULL_REGISTRY_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static ExtensionRegistryLite create() {
        ExtensionRegistryLite result = invokeSubclassFactory("newInstance");
        return result != null ? result : new ExtensionRegistryLite();
    }

    public static ExtensionRegistryLite createEmpty() {
        ExtensionRegistryLite result = invokeSubclassFactory("getEmptyRegistry");
        return result != null ? result : ExtensionRegistryLite.EMPTY_REGISTRY_LITE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isFullRegistry(ExtensionRegistryLite registry) {
        Class<?> cls = EXTENSION_REGISTRY_CLASS;
        return cls != null && cls.isAssignableFrom(registry.getClass());
    }

    private static final ExtensionRegistryLite invokeSubclassFactory(String methodName) {
        Class<?> cls = EXTENSION_REGISTRY_CLASS;
        if (cls == null) {
            return null;
        }
        try {
            return (ExtensionRegistryLite) cls.getDeclaredMethod(methodName, new Class[0]).invoke(null, new Object[0]);
        } catch (Exception e) {
            return null;
        }
    }
}
