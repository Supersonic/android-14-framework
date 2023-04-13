package android.hardware.p002tv.cec.V1_0;
/* renamed from: android.hardware.tv.cec.V1_0.HdmiPortType */
/* loaded from: classes.dex */
public final class HdmiPortType {
    public static final String toString(int i) {
        if (i == 0) {
            return "INPUT";
        }
        if (i == 1) {
            return "OUTPUT";
        }
        return "0x" + Integer.toHexString(i);
    }
}
