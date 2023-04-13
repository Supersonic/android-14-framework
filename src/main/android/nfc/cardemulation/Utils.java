package android.nfc.cardemulation;

import android.content.ComponentName;
import android.util.proto.ProtoOutputStream;
/* loaded from: classes2.dex */
public final class Utils {
    private Utils() {
    }

    public static void dumpDebugComponentName(ComponentName componentName, ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        proto.write(1138166333441L, componentName.getPackageName());
        proto.write(1138166333442L, componentName.getClassName());
        proto.end(token);
    }
}
