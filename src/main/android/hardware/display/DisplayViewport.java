package android.hardware.display;

import android.graphics.Rect;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.text.TextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes.dex */
public final class DisplayViewport {
    public static final int VIEWPORT_EXTERNAL = 2;
    public static final int VIEWPORT_INTERNAL = 1;
    public static final int VIEWPORT_VIRTUAL = 3;
    public int deviceHeight;
    public int deviceWidth;
    public int displayId;
    public boolean isActive;
    public int orientation;
    public Integer physicalPort;
    public int type;
    public String uniqueId;
    public boolean valid;
    public final Rect logicalFrame = new Rect();
    public final Rect physicalFrame = new Rect();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ViewportType {
    }

    public void copyFrom(DisplayViewport viewport) {
        this.valid = viewport.valid;
        this.isActive = viewport.isActive;
        this.displayId = viewport.displayId;
        this.orientation = viewport.orientation;
        this.logicalFrame.set(viewport.logicalFrame);
        this.physicalFrame.set(viewport.physicalFrame);
        this.deviceWidth = viewport.deviceWidth;
        this.deviceHeight = viewport.deviceHeight;
        this.uniqueId = viewport.uniqueId;
        this.physicalPort = viewport.physicalPort;
        this.type = viewport.type;
    }

    public DisplayViewport makeCopy() {
        DisplayViewport dv = new DisplayViewport();
        dv.copyFrom(this);
        return dv;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof DisplayViewport) {
            DisplayViewport other = (DisplayViewport) o;
            return this.valid == other.valid && this.isActive == other.isActive && this.displayId == other.displayId && this.orientation == other.orientation && this.logicalFrame.equals(other.logicalFrame) && this.physicalFrame.equals(other.physicalFrame) && this.deviceWidth == other.deviceWidth && this.deviceHeight == other.deviceHeight && TextUtils.equals(this.uniqueId, other.uniqueId) && Objects.equals(this.physicalPort, other.physicalPort) && this.type == other.type;
        }
        return false;
    }

    public int hashCode() {
        int result = 1 + (1 * 31) + (this.valid ? 1 : 0);
        int result2 = result + (result * 31) + (this.isActive ? 1 : 0);
        int result3 = result2 + (result2 * 31) + this.displayId;
        int result4 = result3 + (result3 * 31) + this.orientation;
        int result5 = result4 + (result4 * 31) + this.logicalFrame.hashCode();
        int result6 = result5 + (result5 * 31) + this.physicalFrame.hashCode();
        int result7 = result6 + (result6 * 31) + this.deviceWidth;
        int result8 = result7 + (result7 * 31) + this.deviceHeight;
        int result9 = result8 + (result8 * 31) + this.uniqueId.hashCode();
        Integer num = this.physicalPort;
        if (num != null) {
            result9 += (result9 * 31) + num.hashCode();
        }
        return result9 + (result9 * 31) + this.type;
    }

    public String toString() {
        return "DisplayViewport{type=" + typeToString(this.type) + ", valid=" + this.valid + ", isActive=" + this.isActive + ", displayId=" + this.displayId + ", uniqueId='" + this.uniqueId + "', physicalPort=" + this.physicalPort + ", orientation=" + this.orientation + ", logicalFrame=" + this.logicalFrame + ", physicalFrame=" + this.physicalFrame + ", deviceWidth=" + this.deviceWidth + ", deviceHeight=" + this.deviceHeight + "}";
    }

    public static String typeToString(int viewportType) {
        switch (viewportType) {
            case 1:
                return "INTERNAL";
            case 2:
                return "EXTERNAL";
            case 3:
                return "VIRTUAL";
            default:
                return "UNKNOWN (" + viewportType + NavigationBarInflaterView.KEY_CODE_END;
        }
    }
}
