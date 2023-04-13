package com.google.protobuf;
@CheckReturnValue
/* loaded from: classes.dex */
final class ExtensionSchemas {
    private static final ExtensionSchema<?> LITE_SCHEMA = new ExtensionSchemaLite();
    private static final ExtensionSchema<?> FULL_SCHEMA = loadSchemaForFullRuntime();

    ExtensionSchemas() {
    }

    private static ExtensionSchema<?> loadSchemaForFullRuntime() {
        try {
            Class<?> clazz = Class.forName("com.google.protobuf.ExtensionSchemaFull");
            return (ExtensionSchema) clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ExtensionSchema<?> lite() {
        return LITE_SCHEMA;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ExtensionSchema<?> full() {
        ExtensionSchema<?> extensionSchema = FULL_SCHEMA;
        if (extensionSchema == null) {
            throw new IllegalStateException("Protobuf runtime is not correctly loaded.");
        }
        return extensionSchema;
    }
}
