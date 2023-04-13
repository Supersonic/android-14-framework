package com.android.server.apphibernation;

import android.util.Slog;
import android.util.proto.ProtoInputStream;
import android.util.proto.ProtoOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public final class UserLevelHibernationProto implements ProtoReadWriter<List<UserLevelState>> {
    @Override // com.android.server.apphibernation.ProtoReadWriter
    public void writeToProto(ProtoOutputStream protoOutputStream, List<UserLevelState> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            long start = protoOutputStream.start(2246267895809L);
            UserLevelState userLevelState = list.get(i);
            protoOutputStream.write(1138166333441L, userLevelState.packageName);
            protoOutputStream.write(1133871366146L, userLevelState.hibernated);
            protoOutputStream.end(start);
        }
    }

    @Override // com.android.server.apphibernation.ProtoReadWriter
    public List<UserLevelState> readFromProto(ProtoInputStream protoInputStream) throws IOException {
        ArrayList arrayList = new ArrayList();
        while (protoInputStream.nextField() != -1) {
            if (protoInputStream.getFieldNumber() == 1) {
                UserLevelState userLevelState = new UserLevelState();
                long start = protoInputStream.start(2246267895809L);
                while (protoInputStream.nextField() != -1) {
                    int fieldNumber = protoInputStream.getFieldNumber();
                    if (fieldNumber == 1) {
                        userLevelState.packageName = protoInputStream.readString(1138166333441L);
                    } else if (fieldNumber == 2) {
                        userLevelState.hibernated = protoInputStream.readBoolean(1133871366146L);
                    } else {
                        Slog.w("UserLevelHibernationProtoReadWriter", "Undefined field in proto: " + protoInputStream.getFieldNumber());
                    }
                }
                protoInputStream.end(start);
                arrayList.add(userLevelState);
            }
        }
        return arrayList;
    }
}
