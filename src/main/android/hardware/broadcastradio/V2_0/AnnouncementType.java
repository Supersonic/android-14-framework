package android.hardware.broadcastradio.V2_0;
/* loaded from: classes.dex */
public final class AnnouncementType {
    public static final String toString(byte b) {
        if (b == 1) {
            return "EMERGENCY";
        }
        if (b == 2) {
            return "WARNING";
        }
        if (b == 3) {
            return "TRAFFIC";
        }
        if (b == 4) {
            return "WEATHER";
        }
        if (b == 5) {
            return "NEWS";
        }
        if (b == 6) {
            return "EVENT";
        }
        if (b == 7) {
            return "SPORT";
        }
        if (b == 8) {
            return "MISC";
        }
        return "0x" + Integer.toHexString(Byte.toUnsignedInt(b));
    }
}
