package android.text.format;

import android.icu.text.DateTimePatternGenerator;
import android.icu.text.DisplayContext;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.ULocale;
import android.util.LruCache;
/* loaded from: classes3.dex */
class DateTimeFormat {
    private static final FormatterCache CACHED_FORMATTERS = new FormatterCache();

    /* loaded from: classes3.dex */
    static class FormatterCache extends LruCache<String, android.icu.text.DateFormat> {
        FormatterCache() {
            super(8);
        }
    }

    private DateTimeFormat() {
    }

    public static String format(ULocale icuLocale, Calendar time, int flags, DisplayContext displayContext) {
        String format;
        String skeleton = DateUtilsBridge.toSkeleton(time, flags);
        String key = skeleton + "\t" + icuLocale + "\t" + time.getTimeZone();
        FormatterCache formatterCache = CACHED_FORMATTERS;
        synchronized (formatterCache) {
            android.icu.text.DateFormat formatter = formatterCache.get(key);
            if (formatter == null) {
                DateTimePatternGenerator generator = DateTimePatternGenerator.getInstance(icuLocale);
                formatter = new SimpleDateFormat(generator.getBestPattern(skeleton), icuLocale);
                formatterCache.put(key, formatter);
            }
            formatter.setContext(displayContext);
            format = formatter.format(time);
        }
        return format;
    }
}
