package com.android.server.display.brightness;

import android.util.Slog;
import java.util.Objects;
/* loaded from: classes.dex */
public final class BrightnessReason {
    public int mModifier;
    public int mReason;

    public void set(BrightnessReason brightnessReason) {
        setReason(brightnessReason == null ? 0 : brightnessReason.mReason);
        setModifier(brightnessReason != null ? brightnessReason.mModifier : 0);
    }

    public void addModifier(int i) {
        setModifier(i | this.mModifier);
    }

    public boolean equals(Object obj) {
        if (obj instanceof BrightnessReason) {
            BrightnessReason brightnessReason = (BrightnessReason) obj;
            return brightnessReason.mReason == this.mReason && brightnessReason.mModifier == this.mModifier;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mReason), Integer.valueOf(this.mModifier));
    }

    public String toString() {
        return toString(0);
    }

    public String toString(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append(reasonToString(this.mReason));
        sb.append(" [");
        if ((i & 1) != 0) {
            sb.append(" temp_adj");
        }
        if ((i & 2) != 0) {
            sb.append(" auto_adj");
        }
        if ((this.mModifier & 2) != 0) {
            sb.append(" low_pwr");
        }
        if ((this.mModifier & 1) != 0) {
            sb.append(" dim");
        }
        if ((this.mModifier & 4) != 0) {
            sb.append(" hdr");
        }
        if ((this.mModifier & 8) != 0) {
            sb.append(" throttled");
        }
        int length = sb.length();
        if (sb.charAt(length - 1) == '[') {
            sb.setLength(length - 2);
        } else {
            sb.append(" ]");
        }
        return sb.toString();
    }

    public void setReason(int i) {
        if (i < 0 || i > 10) {
            Slog.w("BrightnessReason", "brightness reason out of bounds: " + i);
            return;
        }
        this.mReason = i;
    }

    public int getReason() {
        return this.mReason;
    }

    public int getModifier() {
        return this.mModifier;
    }

    public void setModifier(int i) {
        if ((i & (-16)) != 0) {
            Slog.w("BrightnessReason", "brightness modifier out of bounds: 0x" + Integer.toHexString(i));
            return;
        }
        this.mModifier = i;
    }

    public final String reasonToString(int i) {
        switch (i) {
            case 1:
                return "manual";
            case 2:
                return "doze";
            case 3:
                return "doze_default";
            case 4:
                return "automatic";
            case 5:
                return "screen_off";
            case 6:
                return "override";
            case 7:
                return "temporary";
            case 8:
                return "boost";
            case 9:
                return "screen_off_brightness_sensor";
            case 10:
                return "follower";
            default:
                return Integer.toString(i);
        }
    }
}
