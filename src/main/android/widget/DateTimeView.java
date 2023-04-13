package android.widget;

import android.app.ActivityThread;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.p008os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.PluralsMessageFormatter;
import android.view.RemotableViewMethod;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import android.widget.RemoteViews;
import com.android.internal.C4057R;
import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.JulianFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@RemoteViews.RemoteView
/* loaded from: classes4.dex */
public class DateTimeView extends TextView {
    private static final int SHOW_MONTH_DAY_YEAR = 1;
    private static final int SHOW_TIME = 0;
    private static final ThreadLocal<ReceiverInfo> sReceiverInfo = new ThreadLocal<>();
    int mLastDisplay;
    DateFormat mLastFormat;
    private LocalDateTime mLocalTime;
    private String mNowText;
    private boolean mShowRelativeTime;
    private long mTimeMillis;
    private long mUpdateTimeMillis;

    /* loaded from: classes4.dex */
    public final class InspectionCompanion implements android.view.inspector.InspectionCompanion<DateTimeView> {
        private boolean mPropertiesMapped = false;
        private int mShowReleativeId;

        @Override // android.view.inspector.InspectionCompanion
        public void mapProperties(PropertyMapper propertyMapper) {
            this.mShowReleativeId = propertyMapper.mapBoolean("showReleative", 0);
            this.mPropertiesMapped = true;
        }

        @Override // android.view.inspector.InspectionCompanion
        public void readProperties(DateTimeView node, PropertyReader propertyReader) {
            if (!this.mPropertiesMapped) {
                throw new InspectionCompanion.UninitializedPropertyMapException();
            }
            propertyReader.readBoolean(this.mShowReleativeId, node.isShowRelativeTime());
        }
    }

    public DateTimeView(Context context) {
        this(context, null);
    }

    public DateTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mLastDisplay = -1;
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.DateTimeView, 0, 0);
        int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case 0:
                    boolean relative = a.getBoolean(i, false);
                    setShowRelativeTime(relative);
                    break;
            }
        }
        a.recycle();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ThreadLocal<ReceiverInfo> threadLocal = sReceiverInfo;
        ReceiverInfo ri = threadLocal.get();
        if (ri == null) {
            ri = new ReceiverInfo();
            threadLocal.set(ri);
        }
        ri.addView(this);
        if (this.mShowRelativeTime) {
            update();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ReceiverInfo ri = sReceiverInfo.get();
        if (ri != null) {
            ri.removeView(this);
        }
    }

    @RemotableViewMethod
    public void setTime(long timeMillis) {
        this.mTimeMillis = timeMillis;
        LocalDateTime dateTime = toLocalDateTime(timeMillis, ZoneId.systemDefault());
        this.mLocalTime = dateTime.withSecond(0);
        update();
    }

    @RemotableViewMethod
    public void setShowRelativeTime(boolean showRelativeTime) {
        this.mShowRelativeTime = showRelativeTime;
        updateNowText();
        update();
    }

    public boolean isShowRelativeTime() {
        return this.mShowRelativeTime;
    }

    @Override // android.view.View
    @RemotableViewMethod
    public void setVisibility(int visibility) {
        boolean gotVisible = visibility != 8 && getVisibility() == 8;
        super.setVisibility(visibility);
        if (gotVisible) {
            update();
        }
    }

    void update() {
        int display;
        DateFormat format;
        if (this.mLocalTime == null || getVisibility() == 8) {
            return;
        }
        if (this.mShowRelativeTime) {
            updateRelativeTime();
            return;
        }
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localTime = this.mLocalTime;
        LocalDateTime localStartOfDay = LocalDateTime.of(localTime.toLocalDate(), LocalTime.MIDNIGHT);
        LocalDateTime localTomorrowStartOfDay = localStartOfDay.plusDays(1L);
        LocalDateTime localNow = LocalDateTime.now(zoneId).withSecond(0);
        long twelveHoursBefore = toEpochMillis(localTime.minusHours(12L), zoneId);
        long twelveHoursAfter = toEpochMillis(localTime.plusHours(12L), zoneId);
        long midnightBefore = toEpochMillis(localStartOfDay, zoneId);
        long midnightAfter = toEpochMillis(localTomorrowStartOfDay, zoneId);
        long time = toEpochMillis(localTime, zoneId);
        long now = toEpochMillis(localNow, zoneId);
        if ((now >= midnightBefore && now < midnightAfter) || (now >= twelveHoursBefore && now < twelveHoursAfter)) {
            display = 0;
        } else {
            display = 1;
        }
        if (display == this.mLastDisplay && this.mLastFormat != null) {
            format = this.mLastFormat;
        } else {
            switch (display) {
                case 0:
                    format = getTimeFormat();
                    break;
                case 1:
                    format = DateFormat.getDateInstance(3);
                    break;
                default:
                    throw new RuntimeException("unknown display value: " + display);
            }
            this.mLastFormat = format;
        }
        String text = format.format(new Date(time));
        maybeSetText(text);
        if (display == 0) {
            this.mUpdateTimeMillis = twelveHoursAfter > midnightAfter ? twelveHoursAfter : midnightAfter;
        } else if (this.mTimeMillis < now) {
            this.mUpdateTimeMillis = 0L;
        } else {
            this.mUpdateTimeMillis = twelveHoursBefore < midnightBefore ? twelveHoursBefore : midnightBefore;
        }
    }

    private void updateRelativeTime() {
        int count;
        int i;
        String result;
        long millisIncrease;
        int i2;
        int i3;
        int i4;
        long now = System.currentTimeMillis();
        long duration = Math.abs(now - this.mTimeMillis);
        boolean past = now >= this.mTimeMillis;
        if (duration < 60000) {
            maybeSetText(this.mNowText);
            this.mUpdateTimeMillis = this.mTimeMillis + 60000 + 1;
            return;
        }
        if (duration < 3600000) {
            count = (int) (duration / 60000);
            Resources resources = getContext().getResources();
            if (past) {
                i4 = C4057R.string.duration_minutes_shortest;
            } else {
                i4 = C4057R.string.duration_minutes_shortest_future;
            }
            result = resources.getString(i4, Integer.valueOf(count));
            millisIncrease = 60000;
        } else if (duration < 86400000) {
            count = (int) (duration / 3600000);
            Resources resources2 = getContext().getResources();
            if (past) {
                i3 = C4057R.string.duration_hours_shortest;
            } else {
                i3 = C4057R.string.duration_hours_shortest_future;
            }
            result = resources2.getString(i3, Integer.valueOf(count));
            millisIncrease = 3600000;
        } else if (duration < 31449600000L) {
            LocalDateTime localDateTime = this.mLocalTime;
            ZoneId zoneId = ZoneId.systemDefault();
            LocalDateTime localNow = toLocalDateTime(now, zoneId);
            int count2 = Math.max(Math.abs(dayDistance(localDateTime, localNow)), 1);
            Resources resources3 = getContext().getResources();
            if (past) {
                i2 = C4057R.string.duration_days_shortest;
            } else {
                i2 = C4057R.string.duration_days_shortest_future;
            }
            String result2 = resources3.getString(i2, Integer.valueOf(count2));
            if (past || count2 != 1) {
                long millisIncrease2 = computeNextMidnight(localNow, zoneId);
                this.mUpdateTimeMillis = millisIncrease2;
                millisIncrease = -1;
            } else {
                millisIncrease = 86400000;
            }
            count = count2;
            result = result2;
        } else {
            count = (int) (duration / 31449600000L);
            Resources resources4 = getContext().getResources();
            if (past) {
                i = C4057R.string.duration_years_shortest;
            } else {
                i = C4057R.string.duration_years_shortest_future;
            }
            result = resources4.getString(i, Integer.valueOf(count));
            millisIncrease = 31449600000L;
        }
        if (millisIncrease != -1) {
            if (past) {
                this.mUpdateTimeMillis = this.mTimeMillis + ((count + 1) * millisIncrease) + 1;
            } else {
                this.mUpdateTimeMillis = (this.mTimeMillis - (count * millisIncrease)) + 1;
            }
        }
        maybeSetText(result);
    }

    private void maybeSetText(String text) {
        if (TextUtils.equals(getText(), text)) {
            return;
        }
        setText(text);
    }

    private static long computeNextMidnight(LocalDateTime time, ZoneId zoneId) {
        LocalDate tomorrow = time.toLocalDate().plusDays(1L);
        LocalDateTime nextMidnight = LocalDateTime.of(tomorrow, LocalTime.MIDNIGHT);
        return toEpochMillis(nextMidnight, zoneId);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateNowText();
        update();
    }

    private void updateNowText() {
        if (!this.mShowRelativeTime) {
            return;
        }
        this.mNowText = getContext().getResources().getString(C4057R.string.now_string_shortest);
    }

    private static int dayDistance(LocalDateTime start, LocalDateTime end) {
        return (int) (end.getLong(JulianFields.JULIAN_DAY) - start.getLong(JulianFields.JULIAN_DAY));
    }

    private DateFormat getTimeFormat() {
        return android.text.format.DateFormat.getTimeFormat(getContext());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearFormatAndUpdate() {
        this.mLastFormat = null;
        update();
    }

    @Override // android.widget.TextView, android.view.View
    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo info) {
        String result;
        super.onInitializeAccessibilityNodeInfoInternal(info);
        if (this.mShowRelativeTime) {
            long now = System.currentTimeMillis();
            long duration = Math.abs(now - this.mTimeMillis);
            boolean past = now >= this.mTimeMillis;
            Map<String, Object> arguments = new HashMap<>();
            if (duration < 60000) {
                result = this.mNowText;
            } else if (duration < 3600000) {
                int count = (int) (duration / 60000);
                arguments.put("count", Integer.valueOf(count));
                result = PluralsMessageFormatter.format(getContext().getResources(), arguments, past ? C4057R.string.duration_minutes_relative : C4057R.string.duration_minutes_relative_future);
            } else if (duration < 86400000) {
                int count2 = (int) (duration / 3600000);
                arguments.put("count", Integer.valueOf(count2));
                result = PluralsMessageFormatter.format(getContext().getResources(), arguments, past ? C4057R.string.duration_hours_relative : C4057R.string.duration_hours_relative_future);
            } else if (duration < 31449600000L) {
                LocalDateTime localDateTime = this.mLocalTime;
                ZoneId zoneId = ZoneId.systemDefault();
                LocalDateTime localNow = toLocalDateTime(now, zoneId);
                int count3 = Math.max(Math.abs(dayDistance(localDateTime, localNow)), 1);
                arguments.put("count", Integer.valueOf(count3));
                String result2 = PluralsMessageFormatter.format(getContext().getResources(), arguments, past ? C4057R.string.duration_days_relative : C4057R.string.duration_days_relative_future);
                result = result2;
            } else {
                int count4 = (int) (duration / 31449600000L);
                arguments.put("count", Integer.valueOf(count4));
                result = PluralsMessageFormatter.format(getContext().getResources(), arguments, past ? C4057R.string.duration_years_relative : C4057R.string.duration_years_relative_future);
            }
            info.setText(result);
        }
    }

    public static void setReceiverHandler(Handler handler) {
        ThreadLocal<ReceiverInfo> threadLocal = sReceiverInfo;
        ReceiverInfo ri = threadLocal.get();
        if (ri == null) {
            ri = new ReceiverInfo();
            threadLocal.set(ri);
        }
        ri.setHandler(handler);
    }

    /* loaded from: classes4.dex */
    private static class ReceiverInfo {
        private final ArrayList<DateTimeView> mAttachedViews;
        private Handler mHandler;
        private final ContentObserver mObserver;
        private final BroadcastReceiver mReceiver;

        private ReceiverInfo() {
            this.mAttachedViews = new ArrayList<>();
            this.mReceiver = new BroadcastReceiver() { // from class: android.widget.DateTimeView.ReceiverInfo.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (Intent.ACTION_TIME_TICK.equals(action) && System.currentTimeMillis() < ReceiverInfo.this.getSoonestUpdateTime()) {
                        return;
                    }
                    ReceiverInfo.this.updateAll();
                }
            };
            this.mObserver = new ContentObserver(new Handler()) { // from class: android.widget.DateTimeView.ReceiverInfo.2
                @Override // android.database.ContentObserver
                public void onChange(boolean selfChange) {
                    ReceiverInfo.this.updateAll();
                }
            };
            this.mHandler = new Handler();
        }

        public void addView(DateTimeView v) {
            synchronized (this.mAttachedViews) {
                boolean register = this.mAttachedViews.isEmpty();
                this.mAttachedViews.add(v);
                if (register) {
                    register(getApplicationContextIfAvailable(v.getContext()));
                }
            }
        }

        public void removeView(DateTimeView v) {
            synchronized (this.mAttachedViews) {
                boolean removed = this.mAttachedViews.remove(v);
                if (removed && this.mAttachedViews.isEmpty()) {
                    unregister(getApplicationContextIfAvailable(v.getContext()));
                }
            }
        }

        void updateAll() {
            synchronized (this.mAttachedViews) {
                int count = this.mAttachedViews.size();
                for (int i = 0; i < count; i++) {
                    final DateTimeView view = this.mAttachedViews.get(i);
                    view.post(new Runnable() { // from class: android.widget.DateTimeView$ReceiverInfo$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            DateTimeView.this.clearFormatAndUpdate();
                        }
                    });
                }
            }
        }

        long getSoonestUpdateTime() {
            long result = Long.MAX_VALUE;
            synchronized (this.mAttachedViews) {
                int count = this.mAttachedViews.size();
                for (int i = 0; i < count; i++) {
                    long time = this.mAttachedViews.get(i).mUpdateTimeMillis;
                    if (time < result) {
                        result = time;
                    }
                }
            }
            return result;
        }

        static final Context getApplicationContextIfAvailable(Context context) {
            Context ac = context.getApplicationContext();
            return ac != null ? ac : ActivityThread.currentApplication().getApplicationContext();
        }

        void register(Context context) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            context.registerReceiver(this.mReceiver, filter, null, this.mHandler);
        }

        void unregister(Context context) {
            context.unregisterReceiver(this.mReceiver);
        }

        public void setHandler(Handler handler) {
            this.mHandler = handler;
            synchronized (this.mAttachedViews) {
                if (!this.mAttachedViews.isEmpty()) {
                    unregister(this.mAttachedViews.get(0).getContext());
                    register(this.mAttachedViews.get(0).getContext());
                }
            }
        }
    }

    private static LocalDateTime toLocalDateTime(long timeMillis, ZoneId zoneId) {
        Instant instant = Instant.ofEpochMilli(timeMillis);
        return LocalDateTime.ofInstant(instant, zoneId);
    }

    private static long toEpochMillis(LocalDateTime time, ZoneId zoneId) {
        Instant instant = time.toInstant(zoneId.getRules().getOffset(time));
        return instant.toEpochMilli();
    }
}
