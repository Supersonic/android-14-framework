package com.android.framework.protobuf;
@CheckReturnValue
/* loaded from: classes4.dex */
final class MapFieldSchemas {
    private static final MapFieldSchema FULL_SCHEMA = loadSchemaForFullRuntime();
    private static final MapFieldSchema LITE_SCHEMA = new MapFieldSchemaLite();

    MapFieldSchemas() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static MapFieldSchema full() {
        return FULL_SCHEMA;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static MapFieldSchema lite() {
        return LITE_SCHEMA;
    }

    private static MapFieldSchema loadSchemaForFullRuntime() {
        try {
            Class<?> clazz = Class.forName("com.android.framework.protobuf.MapFieldSchemaFull");
            return (MapFieldSchema) clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception e) {
            return null;
        }
    }
}
