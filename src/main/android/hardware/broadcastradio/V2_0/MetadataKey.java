package android.hardware.broadcastradio.V2_0;
/* loaded from: classes.dex */
public final class MetadataKey {
    public static final String toString(int i) {
        if (i == 1) {
            return "RDS_PS";
        }
        if (i == 2) {
            return "RDS_PTY";
        }
        if (i == 3) {
            return "RBDS_PTY";
        }
        if (i == 4) {
            return "RDS_RT";
        }
        if (i == 5) {
            return "SONG_TITLE";
        }
        if (i == 6) {
            return "SONG_ARTIST";
        }
        if (i == 7) {
            return "SONG_ALBUM";
        }
        if (i == 8) {
            return "STATION_ICON";
        }
        if (i == 9) {
            return "ALBUM_ART";
        }
        if (i == 10) {
            return "PROGRAM_NAME";
        }
        if (i == 11) {
            return "DAB_ENSEMBLE_NAME";
        }
        if (i == 12) {
            return "DAB_ENSEMBLE_NAME_SHORT";
        }
        if (i == 13) {
            return "DAB_SERVICE_NAME";
        }
        if (i == 14) {
            return "DAB_SERVICE_NAME_SHORT";
        }
        if (i == 15) {
            return "DAB_COMPONENT_NAME";
        }
        if (i == 16) {
            return "DAB_COMPONENT_NAME_SHORT";
        }
        return "0x" + Integer.toHexString(i);
    }
}
