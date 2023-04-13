package android.widget;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.icu.text.DateTimePatternGenerator;
import android.net.Uri;
import android.p008os.Handler;
import android.p008os.Process;
import android.p008os.UserHandle;
import android.provider.Settings;
import android.provider.Telephony;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.RemotableViewMethod;
import android.view.ViewDebug;
import android.view.ViewHierarchyEncoder;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import android.widget.RemoteViews;
import com.android.internal.C4057R;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;
@RemoteViews.RemoteView
/* loaded from: classes4.dex */
public class TextClock extends TextView {
    @Deprecated
    public static final CharSequence DEFAULT_FORMAT_12_HOUR = "h:mm a";
    @Deprecated
    public static final CharSequence DEFAULT_FORMAT_24_HOUR = "H:mm";
    private CharSequence mDescFormat;
    private CharSequence mDescFormat12;
    private CharSequence mDescFormat24;
    @ViewDebug.ExportedProperty
    private CharSequence mFormat;
    private CharSequence mFormat12;
    private CharSequence mFormat24;
    private ContentObserver mFormatChangeObserver;
    @ViewDebug.ExportedProperty
    private boolean mHasSeconds;
    private final BroadcastReceiver mIntentReceiver;
    private boolean mRegistered;
    private boolean mShouldRunTicker;
    private boolean mShowCurrentUserTime;
    private boolean mStopTicking;
    private final Runnable mTicker;
    private Calendar mTime;
    private String mTimeZone;

    /* loaded from: classes4.dex */
    public final class InspectionCompanion implements android.view.inspector.InspectionCompanion<TextClock> {
        private int mFormat12HourId;
        private int mFormat24HourId;
        private int mIs24HourModeEnabledId;
        private boolean mPropertiesMapped = false;
        private int mTimeZoneId;

        @Override // android.view.inspector.InspectionCompanion
        public void mapProperties(PropertyMapper propertyMapper) {
            this.mFormat12HourId = propertyMapper.mapObject("format12Hour", 16843722);
            this.mFormat24HourId = propertyMapper.mapObject("format24Hour", 16843723);
            this.mIs24HourModeEnabledId = propertyMapper.mapBoolean("is24HourModeEnabled", 0);
            this.mTimeZoneId = propertyMapper.mapObject("timeZone", 16843724);
            this.mPropertiesMapped = true;
        }

        @Override // android.view.inspector.InspectionCompanion
        public void readProperties(TextClock node, PropertyReader propertyReader) {
            if (!this.mPropertiesMapped) {
                throw new InspectionCompanion.UninitializedPropertyMapException();
            }
            propertyReader.readObject(this.mFormat12HourId, node.getFormat12Hour());
            propertyReader.readObject(this.mFormat24HourId, node.getFormat24Hour());
            propertyReader.readBoolean(this.mIs24HourModeEnabledId, node.is24HourModeEnabled());
            propertyReader.readObject(this.mTimeZoneId, node.getTimeZone());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class FormatChangeObserver extends ContentObserver {
        public FormatChangeObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            TextClock.this.chooseFormat();
            TextClock.this.onTimeChanged();
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            TextClock.this.chooseFormat();
            TextClock.this.onTimeChanged();
        }
    }

    public TextClock(Context context) {
        super(context);
        this.mIntentReceiver = new BroadcastReceiver() { // from class: android.widget.TextClock.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (TextClock.this.mStopTicking) {
                    return;
                }
                if (TextClock.this.mTimeZone == null && Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
                    String timeZone = intent.getStringExtra(Intent.EXTRA_TIMEZONE);
                    TextClock.this.createTime(timeZone);
                } else if (!TextClock.this.mShouldRunTicker && (Intent.ACTION_TIME_TICK.equals(intent.getAction()) || Intent.ACTION_TIME_CHANGED.equals(intent.getAction()))) {
                    return;
                }
                TextClock.this.onTimeChanged();
            }
        };
        this.mTicker = new Runnable() { // from class: android.widget.TextClock.2
            @Override // java.lang.Runnable
            public void run() {
                ZonedDateTime nextTick;
                TextClock.this.removeCallbacks(this);
                if (TextClock.this.mStopTicking || !TextClock.this.mShouldRunTicker) {
                    return;
                }
                TextClock.this.onTimeChanged();
                Instant now = TextClock.this.mTime.toInstant();
                ZoneId zone = TextClock.this.mTime.getTimeZone().toZoneId();
                if (TextClock.this.mHasSeconds) {
                    nextTick = now.atZone(zone).plusSeconds(1L).withNano(0);
                } else {
                    ZonedDateTime nextTick2 = now.atZone(zone);
                    nextTick = nextTick2.plusMinutes(1L).withSecond(0).withNano(0);
                }
                long millisUntilNextTick = Duration.between(now, nextTick.toInstant()).toMillis();
                if (millisUntilNextTick <= 0) {
                    millisUntilNextTick = 1000;
                }
                TextClock.this.postDelayed(this, millisUntilNextTick);
            }
        };
        init();
    }

    public TextClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextClock(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TextClock(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mIntentReceiver = new BroadcastReceiver() { // from class: android.widget.TextClock.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (TextClock.this.mStopTicking) {
                    return;
                }
                if (TextClock.this.mTimeZone == null && Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
                    String timeZone = intent.getStringExtra(Intent.EXTRA_TIMEZONE);
                    TextClock.this.createTime(timeZone);
                } else if (!TextClock.this.mShouldRunTicker && (Intent.ACTION_TIME_TICK.equals(intent.getAction()) || Intent.ACTION_TIME_CHANGED.equals(intent.getAction()))) {
                    return;
                }
                TextClock.this.onTimeChanged();
            }
        };
        this.mTicker = new Runnable() { // from class: android.widget.TextClock.2
            @Override // java.lang.Runnable
            public void run() {
                ZonedDateTime nextTick;
                TextClock.this.removeCallbacks(this);
                if (TextClock.this.mStopTicking || !TextClock.this.mShouldRunTicker) {
                    return;
                }
                TextClock.this.onTimeChanged();
                Instant now = TextClock.this.mTime.toInstant();
                ZoneId zone = TextClock.this.mTime.getTimeZone().toZoneId();
                if (TextClock.this.mHasSeconds) {
                    nextTick = now.atZone(zone).plusSeconds(1L).withNano(0);
                } else {
                    ZonedDateTime nextTick2 = now.atZone(zone);
                    nextTick = nextTick2.plusMinutes(1L).withSecond(0).withNano(0);
                }
                long millisUntilNextTick = Duration.between(now, nextTick.toInstant()).toMillis();
                if (millisUntilNextTick <= 0) {
                    millisUntilNextTick = 1000;
                }
                TextClock.this.postDelayed(this, millisUntilNextTick);
            }
        };
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.TextClock, defStyleAttr, defStyleRes);
        saveAttributeDataForStyleable(context, C4057R.styleable.TextClock, attrs, a, defStyleAttr, defStyleRes);
        try {
            this.mFormat12 = a.getText(0);
            this.mFormat24 = a.getText(1);
            this.mTimeZone = a.getString(2);
            a.recycle();
            init();
        } catch (Throwable th) {
            a.recycle();
            throw th;
        }
    }

    private void init() {
        if (this.mFormat12 == null) {
            this.mFormat12 = getBestDateTimePattern("hm");
        }
        if (this.mFormat24 == null) {
            this.mFormat24 = getBestDateTimePattern("Hm");
        }
        createTime(this.mTimeZone);
        chooseFormat();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createTime(String timeZone) {
        if (timeZone != null) {
            this.mTime = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
        } else {
            this.mTime = Calendar.getInstance();
        }
    }

    @ViewDebug.ExportedProperty
    public CharSequence getFormat12Hour() {
        return this.mFormat12;
    }

    @RemotableViewMethod
    public void setFormat12Hour(CharSequence format) {
        this.mFormat12 = format;
        chooseFormat();
        onTimeChanged();
    }

    public void setContentDescriptionFormat12Hour(CharSequence format) {
        this.mDescFormat12 = format;
        chooseFormat();
        onTimeChanged();
    }

    @ViewDebug.ExportedProperty
    public CharSequence getFormat24Hour() {
        return this.mFormat24;
    }

    @RemotableViewMethod
    public void setFormat24Hour(CharSequence format) {
        this.mFormat24 = format;
        chooseFormat();
        onTimeChanged();
    }

    public void setContentDescriptionFormat24Hour(CharSequence format) {
        this.mDescFormat24 = format;
        chooseFormat();
        onTimeChanged();
    }

    public void setShowCurrentUserTime(boolean showCurrentUserTime) {
        this.mShowCurrentUserTime = showCurrentUserTime;
        chooseFormat();
        onTimeChanged();
        unregisterObserver();
        registerObserver();
    }

    public void refreshTime() {
        onTimeChanged();
        invalidate();
    }

    public boolean is24HourModeEnabled() {
        if (this.mShowCurrentUserTime) {
            return DateFormat.is24HourFormat(getContext(), ActivityManager.getCurrentUser());
        }
        return DateFormat.is24HourFormat(getContext());
    }

    public String getTimeZone() {
        return this.mTimeZone;
    }

    @RemotableViewMethod
    public void setTimeZone(String timeZone) {
        this.mTimeZone = timeZone;
        createTime(timeZone);
        onTimeChanged();
    }

    public CharSequence getFormat() {
        return this.mFormat;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void chooseFormat() {
        boolean format24Requested = is24HourModeEnabled();
        if (format24Requested) {
            CharSequence abc = abc(this.mFormat24, this.mFormat12, getBestDateTimePattern("Hm"));
            this.mFormat = abc;
            this.mDescFormat = abc(this.mDescFormat24, this.mDescFormat12, abc);
        } else {
            CharSequence abc2 = abc(this.mFormat12, this.mFormat24, getBestDateTimePattern("hm"));
            this.mFormat = abc2;
            this.mDescFormat = abc(this.mDescFormat12, this.mDescFormat24, abc2);
        }
        boolean hadSeconds = this.mHasSeconds;
        boolean hasSeconds = DateFormat.hasSeconds(this.mFormat);
        this.mHasSeconds = hasSeconds;
        if (this.mShouldRunTicker && hadSeconds != hasSeconds) {
            this.mTicker.run();
        }
    }

    private String getBestDateTimePattern(String skeleton) {
        DateTimePatternGenerator dtpg = DateTimePatternGenerator.getInstance(getContext().getResources().getConfiguration().locale);
        return dtpg.getBestPattern(skeleton);
    }

    private static CharSequence abc(CharSequence a, CharSequence b, CharSequence c) {
        return a == null ? b == null ? c : b : a;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.mRegistered) {
            this.mRegistered = true;
            registerReceiver();
            registerObserver();
            createTime(this.mTimeZone);
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        boolean z = this.mShouldRunTicker;
        if (!z && isVisible) {
            this.mShouldRunTicker = true;
            this.mTicker.run();
        } else if (z && !isVisible) {
            this.mShouldRunTicker = false;
            removeCallbacks(this.mTicker);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mRegistered) {
            unregisterReceiver();
            unregisterObserver();
            this.mRegistered = false;
        }
    }

    public void disableClockTick() {
        this.mStopTicking = true;
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        getContext().registerReceiverAsUser(this.mIntentReceiver, Process.myUserHandle(), filter, null, getHandler());
    }

    private void registerObserver() {
        if (this.mRegistered) {
            if (this.mFormatChangeObserver == null) {
                this.mFormatChangeObserver = new FormatChangeObserver(getHandler());
            }
            ContentResolver resolver = getContext().getContentResolver();
            Uri uri = Settings.System.getUriFor(Settings.System.TIME_12_24);
            if (this.mShowCurrentUserTime) {
                resolver.registerContentObserver(uri, true, this.mFormatChangeObserver, -1);
            } else {
                resolver.registerContentObserver(uri, true, this.mFormatChangeObserver, UserHandle.myUserId());
            }
        }
    }

    private void unregisterReceiver() {
        getContext().unregisterReceiver(this.mIntentReceiver);
    }

    private void unregisterObserver() {
        if (this.mFormatChangeObserver != null) {
            ContentResolver resolver = getContext().getContentResolver();
            resolver.unregisterContentObserver(this.mFormatChangeObserver);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTimeChanged() {
        this.mTime.setTimeInMillis(System.currentTimeMillis());
        setText(DateFormat.format(this.mFormat, this.mTime));
        setContentDescription(DateFormat.format(this.mDescFormat, this.mTime));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void encodeProperties(ViewHierarchyEncoder stream) {
        super.encodeProperties(stream);
        CharSequence s = getFormat12Hour();
        stream.addProperty("format12Hour", s == null ? null : s.toString());
        CharSequence s2 = getFormat24Hour();
        stream.addProperty("format24Hour", s2 == null ? null : s2.toString());
        CharSequence charSequence = this.mFormat;
        stream.addProperty(Telephony.CellBroadcasts.MESSAGE_FORMAT, charSequence != null ? charSequence.toString() : null);
        stream.addProperty("hasSeconds", this.mHasSeconds);
    }
}
