package com.android.framework.protobuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@CheckReturnValue
/* loaded from: classes4.dex */
final class StructuralMessageInfo implements MessageInfo {
    private final int[] checkInitialized;
    private final MessageLite defaultInstance;
    private final FieldInfo[] fields;
    private final boolean messageSetWireFormat;
    private final ProtoSyntax syntax;

    StructuralMessageInfo(ProtoSyntax syntax, boolean messageSetWireFormat, int[] checkInitialized, FieldInfo[] fields, Object defaultInstance) {
        this.syntax = syntax;
        this.messageSetWireFormat = messageSetWireFormat;
        this.checkInitialized = checkInitialized;
        this.fields = fields;
        this.defaultInstance = (MessageLite) Internal.checkNotNull(defaultInstance, "defaultInstance");
    }

    @Override // com.android.framework.protobuf.MessageInfo
    public ProtoSyntax getSyntax() {
        return this.syntax;
    }

    @Override // com.android.framework.protobuf.MessageInfo
    public boolean isMessageSetWireFormat() {
        return this.messageSetWireFormat;
    }

    public int[] getCheckInitialized() {
        return this.checkInitialized;
    }

    public FieldInfo[] getFields() {
        return this.fields;
    }

    @Override // com.android.framework.protobuf.MessageInfo
    public MessageLite getDefaultInstance() {
        return this.defaultInstance;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(int numFields) {
        return new Builder(numFields);
    }

    /* loaded from: classes4.dex */
    public static final class Builder {
        private int[] checkInitialized;
        private Object defaultInstance;
        private final List<FieldInfo> fields;
        private boolean messageSetWireFormat;
        private ProtoSyntax syntax;
        private boolean wasBuilt;

        public Builder() {
            this.checkInitialized = null;
            this.fields = new ArrayList();
        }

        public Builder(int numFields) {
            this.checkInitialized = null;
            this.fields = new ArrayList(numFields);
        }

        public void withDefaultInstance(Object defaultInstance) {
            this.defaultInstance = defaultInstance;
        }

        public void withSyntax(ProtoSyntax syntax) {
            this.syntax = (ProtoSyntax) Internal.checkNotNull(syntax, "syntax");
        }

        public void withMessageSetWireFormat(boolean messageSetWireFormat) {
            this.messageSetWireFormat = messageSetWireFormat;
        }

        public void withCheckInitialized(int[] checkInitialized) {
            this.checkInitialized = checkInitialized;
        }

        public void withField(FieldInfo field) {
            if (this.wasBuilt) {
                throw new IllegalStateException("Builder can only build once");
            }
            this.fields.add(field);
        }

        public StructuralMessageInfo build() {
            if (this.wasBuilt) {
                throw new IllegalStateException("Builder can only build once");
            }
            if (this.syntax == null) {
                throw new IllegalStateException("Must specify a proto syntax");
            }
            this.wasBuilt = true;
            Collections.sort(this.fields);
            return new StructuralMessageInfo(this.syntax, this.messageSetWireFormat, this.checkInitialized, (FieldInfo[]) this.fields.toArray(new FieldInfo[0]), this.defaultInstance);
        }
    }
}
