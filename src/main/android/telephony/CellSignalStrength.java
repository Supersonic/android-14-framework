package android.telephony;

import android.annotation.SystemApi;
import android.p008os.PersistableBundle;
/* loaded from: classes3.dex */
public abstract class CellSignalStrength {
    public static final int NUM_SIGNAL_STRENGTH_BINS = 5;
    protected static final int NUM_SIGNAL_STRENGTH_THRESHOLDS = 4;
    public static final int SIGNAL_STRENGTH_GOOD = 3;
    public static final int SIGNAL_STRENGTH_GREAT = 4;
    public static final int SIGNAL_STRENGTH_MODERATE = 2;
    public static final int SIGNAL_STRENGTH_NONE_OR_UNKNOWN = 0;
    public static final int SIGNAL_STRENGTH_POOR = 1;

    public abstract CellSignalStrength copy();

    public abstract boolean equals(Object obj);

    public abstract int getAsuLevel();

    public abstract int getDbm();

    public abstract int getLevel();

    public abstract int hashCode();

    public abstract boolean isValid();

    public abstract void setDefaultValues();

    public abstract void updateLevel(PersistableBundle persistableBundle, ServiceState serviceState);

    public static final int getRssiDbmFromAsu(int asu) {
        if (asu > 31 || asu < 0) {
            return Integer.MAX_VALUE;
        }
        return (asu * 2) - 113;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final int getAsuFromRssiDbm(int dbm) {
        if (dbm == Integer.MAX_VALUE) {
            return 99;
        }
        return (dbm + 113) / 2;
    }

    public static final int getRscpDbmFromAsu(int asu) {
        if (asu > 96 || asu < 0) {
            return Integer.MAX_VALUE;
        }
        return asu - 120;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final int getAsuFromRscpDbm(int dbm) {
        if (dbm == Integer.MAX_VALUE) {
            return 255;
        }
        return dbm + 120;
    }

    public static final int getEcNoDbFromAsu(int asu) {
        if (asu > 49 || asu < 0) {
            return Integer.MAX_VALUE;
        }
        return (asu / 2) - 24;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final int inRangeOrUnavailable(int value, int rangeMin, int rangeMax) {
        if (value < rangeMin || value > rangeMax) {
            return Integer.MAX_VALUE;
        }
        return value;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final int inRangeOrUnavailable(int value, int rangeMin, int rangeMax, int special) {
        if ((value < rangeMin || value > rangeMax) && value != special) {
            return Integer.MAX_VALUE;
        }
        return value;
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static int getNumSignalStrengthLevels() {
        return 5;
    }
}
