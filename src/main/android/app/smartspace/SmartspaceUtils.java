package android.app.smartspace;

import android.app.smartspace.uitemplatedata.Text;
import android.text.TextUtils;
/* loaded from: classes.dex */
public final class SmartspaceUtils {
    private SmartspaceUtils() {
    }

    public static boolean isEmpty(Text text) {
        return text == null || TextUtils.isEmpty(text.getText());
    }

    public static boolean isEqual(Text text1, Text text2) {
        if (text1 == null && text2 == null) {
            return true;
        }
        if (text1 == null || text2 == null) {
            return false;
        }
        return text1.equals(text2);
    }

    public static boolean isEqual(CharSequence cs1, CharSequence cs2) {
        if (cs1 == null && cs2 == null) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        return cs1.toString().contentEquals(cs2);
    }
}
