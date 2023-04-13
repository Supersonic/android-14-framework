package com.android.server.apphibernation;

import android.util.proto.ProtoInputStream;
import android.util.proto.ProtoOutputStream;
import java.io.IOException;
/* loaded from: classes.dex */
public interface ProtoReadWriter<T> {
    T readFromProto(ProtoInputStream protoInputStream) throws IOException;

    void writeToProto(ProtoOutputStream protoOutputStream, T t);
}
