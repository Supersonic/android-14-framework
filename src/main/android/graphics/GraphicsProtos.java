package android.graphics;

import android.util.proto.ProtoOutputStream;
/* loaded from: classes.dex */
public final class GraphicsProtos {
    private GraphicsProtos() {
    }

    public static void dumpPointProto(Point point, ProtoOutputStream protoOutputStream, long fieldId) {
        long token = protoOutputStream.start(fieldId);
        protoOutputStream.write(1120986464257L, point.f76x);
        protoOutputStream.write(1120986464258L, point.f77y);
        protoOutputStream.end(token);
    }
}
