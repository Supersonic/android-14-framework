package com.google.protobuf;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/* JADX INFO: Access modifiers changed from: package-private */
@CheckReturnValue
/* loaded from: classes.dex */
public final class Protobuf {
    private static final Protobuf INSTANCE = new Protobuf();
    private final ConcurrentMap<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap();
    private final SchemaFactory schemaFactory = new ManifestSchemaFactory();

    public static Protobuf getInstance() {
        return INSTANCE;
    }

    public <T> void writeTo(T message, Writer writer) throws IOException {
        schemaFor((Protobuf) message).writeTo(message, writer);
    }

    public <T> void mergeFrom(T message, Reader reader) throws IOException {
        mergeFrom(message, reader, ExtensionRegistryLite.getEmptyRegistry());
    }

    public <T> void mergeFrom(T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws IOException {
        schemaFor((Protobuf) message).mergeFrom(message, reader, extensionRegistry);
    }

    public <T> void makeImmutable(T message) {
        schemaFor((Protobuf) message).makeImmutable(message);
    }

    <T> boolean isInitialized(T message) {
        return schemaFor((Protobuf) message).isInitialized(message);
    }

    public <T> Schema<T> schemaFor(Class<T> messageType) {
        Internal.checkNotNull(messageType, "messageType");
        Schema<T> schema = (Schema<T>) this.schemaCache.get(messageType);
        if (schema == null) {
            Schema<T> schema2 = this.schemaFactory.createSchema(messageType);
            Schema<T> previous = (Schema<T>) registerSchema(messageType, schema2);
            if (previous != null) {
                return previous;
            }
            return schema2;
        }
        return schema;
    }

    public <T> Schema<T> schemaFor(T message) {
        return schemaFor((Class) message.getClass());
    }

    public Schema<?> registerSchema(Class<?> messageType, Schema<?> schema) {
        Internal.checkNotNull(messageType, "messageType");
        Internal.checkNotNull(schema, "schema");
        return this.schemaCache.putIfAbsent(messageType, schema);
    }

    public Schema<?> registerSchemaOverride(Class<?> messageType, Schema<?> schema) {
        Internal.checkNotNull(messageType, "messageType");
        Internal.checkNotNull(schema, "schema");
        return this.schemaCache.put(messageType, schema);
    }

    private Protobuf() {
    }

    int getTotalSchemaSize() {
        int result = 0;
        for (Schema<?> schema : this.schemaCache.values()) {
            if (schema instanceof MessageSchema) {
                result += ((MessageSchema) schema).getSchemaSize();
            }
        }
        return result;
    }
}
