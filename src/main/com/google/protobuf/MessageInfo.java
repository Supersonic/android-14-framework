package com.google.protobuf;
@CheckReturnValue
/* loaded from: classes.dex */
interface MessageInfo {
    MessageLite getDefaultInstance();

    ProtoSyntax getSyntax();

    boolean isMessageSetWireFormat();
}
