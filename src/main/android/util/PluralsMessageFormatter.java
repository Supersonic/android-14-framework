package android.util;

import android.content.res.Resources;
import android.icu.text.MessageFormat;
import java.util.Map;
/* loaded from: classes3.dex */
public class PluralsMessageFormatter {
    public static String format(Resources resources, Map<String, Object> arguments, int messageId) {
        return new MessageFormat(resources.getString(messageId)).format(arguments);
    }
}
