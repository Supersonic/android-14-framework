package com.android.framework.protobuf;
@CheckReturnValue
/* loaded from: classes4.dex */
final class NewInstanceSchemaLite implements NewInstanceSchema {
    @Override // com.android.framework.protobuf.NewInstanceSchema
    public Object newInstance(Object defaultInstance) {
        return ((GeneratedMessageLite) defaultInstance).newMutableInstance();
    }
}
