package android.util;

import android.content.res.Resources;
import android.view.Display;
import com.android.internal.C4057R;
/* loaded from: classes3.dex */
public class DisplayUtils {
    public static int getDisplayUniqueIdConfigIndex(Resources res, String displayUniqueId) {
        if (displayUniqueId == null || displayUniqueId.isEmpty()) {
            return -1;
        }
        String[] ids = res.getStringArray(C4057R.array.config_displayUniqueIdArray);
        int size = ids.length;
        for (int i = 0; i < size; i++) {
            if (displayUniqueId.equals(ids[i])) {
                int index = i;
                return index;
            }
        }
        return -1;
    }

    public static Display.Mode getMaximumResolutionDisplayMode(Display.Mode[] modes) {
        if (modes == null || modes.length == 0) {
            return null;
        }
        int maxWidth = 0;
        Display.Mode target = null;
        for (Display.Mode mode : modes) {
            if (mode.getPhysicalWidth() > maxWidth) {
                maxWidth = mode.getPhysicalWidth();
                target = mode;
            }
        }
        return target;
    }

    public static float getPhysicalPixelDisplaySizeRatio(int physicalWidth, int physicalHeight, int currentWidth, int currentHeight) {
        if (physicalWidth == currentWidth && physicalHeight == currentHeight) {
            return 1.0f;
        }
        float widthRatio = currentWidth / physicalWidth;
        float heightRatio = currentHeight / physicalHeight;
        return Math.min(widthRatio, heightRatio);
    }
}
