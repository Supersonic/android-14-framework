package com.android.server.apphibernation;

import android.util.Slog;
import android.util.proto.ProtoInputStream;
import android.util.proto.ProtoOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public final class GlobalLevelHibernationProto implements ProtoReadWriter<List<GlobalLevelState>> {
    @Override // com.android.server.apphibernation.ProtoReadWriter
    public void writeToProto(ProtoOutputStream protoOutputStream, List<GlobalLevelState> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            long start = protoOutputStream.start(2246267895809L);
            GlobalLevelState globalLevelState = list.get(i);
            protoOutputStream.write(1138166333441L, globalLevelState.packageName);
            protoOutputStream.write(1133871366146L, globalLevelState.hibernated);
            protoOutputStream.write(1112396529667L, globalLevelState.savedByte);
            protoOutputStream.end(start);
        }
    }

    @Override // com.android.server.apphibernation.ProtoReadWriter
    public List<GlobalLevelState> readFromProto(ProtoInputStream protoInputStream) throws IOException {
        ArrayList arrayList = new ArrayList();
        while (protoInputStream.nextField() != -1) {
            if (protoInputStream.getFieldNumber() == 1) {
                GlobalLevelState globalLevelState = new GlobalLevelState();
                long start = protoInputStream.start(2246267895809L);
                while (protoInputStream.nextField() != -1) {
                    int fieldNumber = protoInputStream.getFieldNumber();
                    if (fieldNumber == 1) {
                        globalLevelState.packageName = protoInputStream.readString(1138166333441L);
                    } else if (fieldNumber == 2) {
                        globalLevelState.hibernated = protoInputStream.readBoolean(1133871366146L);
                    } else if (fieldNumber == 3) {
                        globalLevelState.savedByte = protoInputStream.readLong(1112396529667L);
                    } else {
                        Slog.w("GlobalLevelHibernationProtoReadWriter", "Undefined field in proto: " + protoInputStream.getFieldNumber());
                    }
                }
                protoInputStream.end(start);
                arrayList.add(globalLevelState);
            }
        }
        return arrayList;
    }
}
