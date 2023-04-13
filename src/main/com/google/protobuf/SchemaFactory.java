package com.google.protobuf;
/* JADX INFO: Access modifiers changed from: package-private */
@CheckReturnValue
/* loaded from: classes.dex */
public interface SchemaFactory {
    <T> Schema<T> createSchema(Class<T> cls);
}
