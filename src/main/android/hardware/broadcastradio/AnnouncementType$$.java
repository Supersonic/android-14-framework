package android.hardware.broadcastradio;
/* loaded from: classes.dex */
public interface AnnouncementType$$ {
    static String toString(byte b) {
        return b == 0 ? "INVALID" : b == 1 ? "EMERGENCY" : b == 2 ? "WARNING" : b == 3 ? "TRAFFIC" : b == 4 ? "WEATHER" : b == 5 ? "NEWS" : b == 6 ? "EVENT" : b == 7 ? "SPORT" : b == 8 ? "MISC" : Byte.toString(b);
    }
}
