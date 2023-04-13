package android.widget;

import android.app.AppGlobals;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.p008os.Process;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import android.widget.RemoteViews;
import com.android.internal.C4057R;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Formatter;
import java.util.Locale;
@RemoteViews.RemoteView
@Deprecated
/* loaded from: classes4.dex */
public class AnalogClock extends View {
    private static final String LOG_TAG = "AnalogClock";
    private boolean mChanged;
    private Clock mClock;
    private Drawable mDial;
    private int mDialHeight;
    private final TintInfo mDialTintInfo;
    private int mDialWidth;
    private float mHour;
    private Drawable mHourHand;
    private final TintInfo mHourHandTintInfo;
    private final BroadcastReceiver mIntentReceiver;
    private Drawable mMinuteHand;
    private final TintInfo mMinuteHandTintInfo;
    private float mMinutes;
    private boolean mReceiverAttached;
    private Drawable mSecondHand;
    private final TintInfo mSecondHandTintInfo;
    private float mSeconds;
    private final int mSecondsHandFps;
    private final Runnable mTick;
    private ZoneId mTimeZone;
    private boolean mVisible;

    /* loaded from: classes4.dex */
    public final class InspectionCompanion implements android.view.inspector.InspectionCompanion<AnalogClock> {
        private int mDialTintBlendModeId;
        private int mDialTintListId;
        private int mHourHandTintBlendModeId;
        private int mHourHandTintListId;
        private int mMinuteHandTintBlendModeId;
        private int mMinuteHandTintListId;
        private boolean mPropertiesMapped = false;
        private int mSecondHandTintBlendModeId;
        private int mSecondHandTintListId;
        private int mTimeZoneId;

        @Override // android.view.inspector.InspectionCompanion
        public void mapProperties(PropertyMapper propertyMapper) {
            this.mDialTintBlendModeId = propertyMapper.mapObject("dialTintBlendMode", 6);
            this.mDialTintListId = propertyMapper.mapObject("dialTintList", 5);
            this.mHourHandTintBlendModeId = propertyMapper.mapObject("hourHandTintBlendMode", 8);
            this.mHourHandTintListId = propertyMapper.mapObject("hourHandTintList", 7);
            this.mMinuteHandTintBlendModeId = propertyMapper.mapObject("minuteHandTintBlendMode", 10);
            this.mMinuteHandTintListId = propertyMapper.mapObject("minuteHandTintList", 9);
            this.mSecondHandTintBlendModeId = propertyMapper.mapObject("secondHandTintBlendMode", 12);
            this.mSecondHandTintListId = propertyMapper.mapObject("secondHandTintList", 11);
            this.mTimeZoneId = propertyMapper.mapObject("timeZone", 16843724);
            this.mPropertiesMapped = true;
        }

        @Override // android.view.inspector.InspectionCompanion
        public void readProperties(AnalogClock node, PropertyReader propertyReader) {
            if (!this.mPropertiesMapped) {
                throw new InspectionCompanion.UninitializedPropertyMapException();
            }
            propertyReader.readObject(this.mDialTintBlendModeId, node.getDialTintBlendMode());
            propertyReader.readObject(this.mDialTintListId, node.getDialTintList());
            propertyReader.readObject(this.mHourHandTintBlendModeId, node.getHourHandTintBlendMode());
            propertyReader.readObject(this.mHourHandTintListId, node.getHourHandTintList());
            propertyReader.readObject(this.mMinuteHandTintBlendModeId, node.getMinuteHandTintBlendMode());
            propertyReader.readObject(this.mMinuteHandTintListId, node.getMinuteHandTintList());
            propertyReader.readObject(this.mSecondHandTintBlendModeId, node.getSecondHandTintBlendMode());
            propertyReader.readObject(this.mSecondHandTintListId, node.getSecondHandTintList());
            propertyReader.readObject(this.mTimeZoneId, node.getTimeZone());
        }
    }

    public AnalogClock(Context context) {
        this(context, null);
    }

    public AnalogClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnalogClock(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AnalogClock(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TintInfo tintInfo = new TintInfo();
        this.mHourHandTintInfo = tintInfo;
        TintInfo tintInfo2 = new TintInfo();
        this.mMinuteHandTintInfo = tintInfo2;
        TintInfo tintInfo3 = new TintInfo();
        this.mSecondHandTintInfo = tintInfo3;
        TintInfo tintInfo4 = new TintInfo();
        this.mDialTintInfo = tintInfo4;
        this.mIntentReceiver = new BroadcastReceiver() { // from class: android.widget.AnalogClock.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
                    AnalogClock.this.createClock();
                }
                AnalogClock.this.mTick.run();
            }
        };
        this.mTick = new Runnable() { // from class: android.widget.AnalogClock.2
            @Override // java.lang.Runnable
            public void run() {
                long millisUntilNextTick;
                AnalogClock.this.removeCallbacks(this);
                if (!AnalogClock.this.mVisible) {
                    return;
                }
                Instant now = AnalogClock.this.now();
                ZonedDateTime zonedDateTime = now.atZone(AnalogClock.this.mClock.getZone());
                LocalTime localTime = zonedDateTime.toLocalTime();
                if (AnalogClock.this.mSecondHand == null || AnalogClock.this.mSecondsHandFps <= 0) {
                    Instant startOfNextMinute = zonedDateTime.plusMinutes(1L).withSecond(0).toInstant();
                    long millisUntilNextTick2 = Duration.between(now, startOfNextMinute).toMillis();
                    if (millisUntilNextTick2 > 0) {
                        millisUntilNextTick = millisUntilNextTick2;
                    } else {
                        millisUntilNextTick = Duration.ofMinutes(1L).toMillis();
                    }
                } else {
                    long millisOfSecond = Duration.ofNanos(localTime.getNano()).toMillis();
                    double millisPerTick = 1000.0d / AnalogClock.this.mSecondsHandFps;
                    long millisPastLastTick = Math.round(millisOfSecond % millisPerTick);
                    millisUntilNextTick = Math.round(millisPerTick - millisPastLastTick);
                    if (millisUntilNextTick <= 0) {
                        millisUntilNextTick = Math.round(millisPerTick);
                    }
                }
                AnalogClock.this.postDelayed(this, millisUntilNextTick);
                AnalogClock.this.onTimeChanged(localTime, now.toEpochMilli());
                AnalogClock.this.invalidate();
            }
        };
        this.mSecondsHandFps = AppGlobals.getIntCoreSetting(WidgetFlags.KEY_ANALOG_CLOCK_SECONDS_HAND_FPS, context.getResources().getInteger(C4057R.integer.config_defaultAnalogClockSecondsHandFps));
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.AnalogClock, defStyleAttr, defStyleRes);
        saveAttributeDataForStyleable(context, C4057R.styleable.AnalogClock, attrs, a, defStyleAttr, defStyleRes);
        Drawable drawable = a.getDrawable(0);
        this.mDial = drawable;
        if (drawable == null) {
            this.mDial = context.getDrawable(C4057R.C4058drawable.clock_dial);
        }
        ColorStateList dialTintList = a.getColorStateList(5);
        if (dialTintList != null) {
            tintInfo4.mTintList = dialTintList;
            tintInfo4.mHasTintList = true;
        }
        BlendMode dialTintMode = Drawable.parseBlendMode(a.getInt(6, -1), null);
        if (dialTintMode != null) {
            tintInfo4.mTintBlendMode = dialTintMode;
            tintInfo4.mHasTintBlendMode = true;
        }
        if (tintInfo4.mHasTintList || tintInfo4.mHasTintBlendMode) {
            this.mDial = tintInfo4.apply(this.mDial);
        }
        Drawable drawable2 = a.getDrawable(1);
        this.mHourHand = drawable2;
        if (drawable2 == null) {
            this.mHourHand = context.getDrawable(C4057R.C4058drawable.clock_hand_hour);
        }
        ColorStateList hourHandTintList = a.getColorStateList(7);
        if (hourHandTintList != null) {
            tintInfo.mTintList = hourHandTintList;
            tintInfo.mHasTintList = true;
        }
        BlendMode hourHandTintMode = Drawable.parseBlendMode(a.getInt(8, -1), null);
        if (hourHandTintMode != null) {
            tintInfo.mTintBlendMode = hourHandTintMode;
            tintInfo.mHasTintBlendMode = true;
        }
        if (tintInfo.mHasTintList || tintInfo.mHasTintBlendMode) {
            this.mHourHand = tintInfo.apply(this.mHourHand);
        }
        Drawable drawable3 = a.getDrawable(2);
        this.mMinuteHand = drawable3;
        if (drawable3 == null) {
            this.mMinuteHand = context.getDrawable(C4057R.C4058drawable.clock_hand_minute);
        }
        ColorStateList minuteHandTintList = a.getColorStateList(9);
        if (minuteHandTintList != null) {
            tintInfo2.mTintList = minuteHandTintList;
            tintInfo2.mHasTintList = true;
        }
        BlendMode minuteHandTintMode = Drawable.parseBlendMode(a.getInt(10, -1), null);
        if (minuteHandTintMode != null) {
            tintInfo2.mTintBlendMode = minuteHandTintMode;
            tintInfo2.mHasTintBlendMode = true;
        }
        if (tintInfo2.mHasTintList || tintInfo2.mHasTintBlendMode) {
            this.mMinuteHand = tintInfo2.apply(this.mMinuteHand);
        }
        this.mSecondHand = a.getDrawable(4);
        ColorStateList secondHandTintList = a.getColorStateList(11);
        if (secondHandTintList != null) {
            tintInfo3.mTintList = secondHandTintList;
            tintInfo3.mHasTintList = true;
        }
        BlendMode secondHandTintMode = Drawable.parseBlendMode(a.getInt(12, -1), null);
        if (secondHandTintMode != null) {
            tintInfo3.mTintBlendMode = secondHandTintMode;
            tintInfo3.mHasTintBlendMode = true;
        }
        if (tintInfo3.mHasTintList || tintInfo3.mHasTintBlendMode) {
            this.mSecondHand = tintInfo3.apply(this.mSecondHand);
        }
        this.mTimeZone = toZoneId(a.getString(3));
        createClock();
        a.recycle();
        this.mDialWidth = this.mDial.getIntrinsicWidth();
        this.mDialHeight = this.mDial.getIntrinsicHeight();
    }

    @RemotableViewMethod
    public void setDial(Icon icon) {
        Drawable loadDrawable = icon.loadDrawable(getContext());
        this.mDial = loadDrawable;
        this.mDialWidth = loadDrawable.getIntrinsicWidth();
        this.mDialHeight = this.mDial.getIntrinsicHeight();
        if (this.mDialTintInfo.mHasTintList || this.mDialTintInfo.mHasTintBlendMode) {
            this.mDial = this.mDialTintInfo.apply(this.mDial);
        }
        this.mChanged = true;
        invalidate();
    }

    @RemotableViewMethod
    public void setDialTintList(ColorStateList tint) {
        this.mDialTintInfo.mTintList = tint;
        this.mDialTintInfo.mHasTintList = true;
        this.mDial = this.mDialTintInfo.apply(this.mDial);
    }

    public ColorStateList getDialTintList() {
        return this.mDialTintInfo.mTintList;
    }

    @RemotableViewMethod
    public void setDialTintBlendMode(BlendMode blendMode) {
        this.mDialTintInfo.mTintBlendMode = blendMode;
        this.mDialTintInfo.mHasTintBlendMode = true;
        this.mDial = this.mDialTintInfo.apply(this.mDial);
    }

    public BlendMode getDialTintBlendMode() {
        return this.mDialTintInfo.mTintBlendMode;
    }

    @RemotableViewMethod
    public void setHourHand(Icon icon) {
        this.mHourHand = icon.loadDrawable(getContext());
        if (this.mHourHandTintInfo.mHasTintList || this.mHourHandTintInfo.mHasTintBlendMode) {
            this.mHourHand = this.mHourHandTintInfo.apply(this.mHourHand);
        }
        this.mChanged = true;
        invalidate();
    }

    @RemotableViewMethod
    public void setHourHandTintList(ColorStateList tint) {
        this.mHourHandTintInfo.mTintList = tint;
        this.mHourHandTintInfo.mHasTintList = true;
        this.mHourHand = this.mHourHandTintInfo.apply(this.mHourHand);
    }

    public ColorStateList getHourHandTintList() {
        return this.mHourHandTintInfo.mTintList;
    }

    @RemotableViewMethod
    public void setHourHandTintBlendMode(BlendMode blendMode) {
        this.mHourHandTintInfo.mTintBlendMode = blendMode;
        this.mHourHandTintInfo.mHasTintBlendMode = true;
        this.mHourHand = this.mHourHandTintInfo.apply(this.mHourHand);
    }

    public BlendMode getHourHandTintBlendMode() {
        return this.mHourHandTintInfo.mTintBlendMode;
    }

    @RemotableViewMethod
    public void setMinuteHand(Icon icon) {
        this.mMinuteHand = icon.loadDrawable(getContext());
        if (this.mMinuteHandTintInfo.mHasTintList || this.mMinuteHandTintInfo.mHasTintBlendMode) {
            this.mMinuteHand = this.mMinuteHandTintInfo.apply(this.mMinuteHand);
        }
        this.mChanged = true;
        invalidate();
    }

    @RemotableViewMethod
    public void setMinuteHandTintList(ColorStateList tint) {
        this.mMinuteHandTintInfo.mTintList = tint;
        this.mMinuteHandTintInfo.mHasTintList = true;
        this.mMinuteHand = this.mMinuteHandTintInfo.apply(this.mMinuteHand);
    }

    public ColorStateList getMinuteHandTintList() {
        return this.mMinuteHandTintInfo.mTintList;
    }

    @RemotableViewMethod
    public void setMinuteHandTintBlendMode(BlendMode blendMode) {
        this.mMinuteHandTintInfo.mTintBlendMode = blendMode;
        this.mMinuteHandTintInfo.mHasTintBlendMode = true;
        this.mMinuteHand = this.mMinuteHandTintInfo.apply(this.mMinuteHand);
    }

    public BlendMode getMinuteHandTintBlendMode() {
        return this.mMinuteHandTintInfo.mTintBlendMode;
    }

    @RemotableViewMethod
    public void setSecondHand(Icon icon) {
        this.mSecondHand = icon == null ? null : icon.loadDrawable(getContext());
        if (this.mSecondHandTintInfo.mHasTintList || this.mSecondHandTintInfo.mHasTintBlendMode) {
            this.mSecondHand = this.mSecondHandTintInfo.apply(this.mSecondHand);
        }
        this.mTick.run();
        this.mChanged = true;
        invalidate();
    }

    @RemotableViewMethod
    public void setSecondHandTintList(ColorStateList tint) {
        this.mSecondHandTintInfo.mTintList = tint;
        this.mSecondHandTintInfo.mHasTintList = true;
        this.mSecondHand = this.mSecondHandTintInfo.apply(this.mSecondHand);
    }

    public ColorStateList getSecondHandTintList() {
        return this.mSecondHandTintInfo.mTintList;
    }

    @RemotableViewMethod
    public void setSecondHandTintBlendMode(BlendMode blendMode) {
        this.mSecondHandTintInfo.mTintBlendMode = blendMode;
        this.mSecondHandTintInfo.mHasTintBlendMode = true;
        this.mSecondHand = this.mSecondHandTintInfo.apply(this.mSecondHand);
    }

    public BlendMode getSecondHandTintBlendMode() {
        return this.mSecondHandTintInfo.mTintBlendMode;
    }

    public String getTimeZone() {
        ZoneId zoneId = this.mTimeZone;
        if (zoneId == null) {
            return null;
        }
        return zoneId.getId();
    }

    @RemotableViewMethod
    public void setTimeZone(String timeZone) {
        this.mTimeZone = toZoneId(timeZone);
        createClock();
        onTimeChanged();
    }

    @Override // android.view.View
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        if (isVisible) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter filter = new IntentFilter();
        if (!this.mReceiverAttached) {
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            getContext().registerReceiverAsUser(this.mIntentReceiver, Process.myUserHandle(), filter, null, getHandler());
            this.mReceiverAttached = true;
        }
        createClock();
        onTimeChanged();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDetachedFromWindow() {
        if (this.mReceiverAttached) {
            getContext().unregisterReceiver(this.mIntentReceiver);
            this.mReceiverAttached = false;
        }
        super.onDetachedFromWindow();
    }

    private void onVisible() {
        if (!this.mVisible) {
            this.mVisible = true;
            this.mTick.run();
        }
    }

    private void onInvisible() {
        if (this.mVisible) {
            removeCallbacks(this.mTick);
            this.mVisible = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i;
        int i2;
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        float hScale = 1.0f;
        float vScale = 1.0f;
        if (widthMode != 0 && widthSize < (i2 = this.mDialWidth)) {
            hScale = widthSize / i2;
        }
        if (heightMode != 0 && heightSize < (i = this.mDialHeight)) {
            vScale = heightSize / i;
        }
        float scale = Math.min(hScale, vScale);
        setMeasuredDimension(resolveSizeAndState((int) (this.mDialWidth * scale), widthMeasureSpec, 0), resolveSizeAndState((int) (this.mDialHeight * scale), heightMeasureSpec, 0));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mChanged = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean changed = this.mChanged;
        if (changed) {
            this.mChanged = false;
        }
        int availableWidth = this.mRight - this.mLeft;
        int availableHeight = this.mBottom - this.mTop;
        int x = availableWidth / 2;
        int y = availableHeight / 2;
        Drawable dial = this.mDial;
        int w = dial.getIntrinsicWidth();
        int h = dial.getIntrinsicHeight();
        boolean scaled = false;
        if (availableWidth < w || availableHeight < h) {
            scaled = true;
            float scale = Math.min(availableWidth / w, availableHeight / h);
            canvas.save();
            canvas.scale(scale, scale, x, y);
        }
        if (changed) {
            dial.setBounds(x - (w / 2), y - (h / 2), (w / 2) + x, (h / 2) + y);
        }
        dial.draw(canvas);
        canvas.save();
        canvas.rotate((this.mHour / 12.0f) * 360.0f, x, y);
        Drawable hourHand = this.mHourHand;
        if (changed) {
            int w2 = hourHand.getIntrinsicWidth();
            int h2 = hourHand.getIntrinsicHeight();
            hourHand.setBounds(x - (w2 / 2), y - (h2 / 2), (w2 / 2) + x, y + (h2 / 2));
        }
        hourHand.draw(canvas);
        canvas.restore();
        canvas.save();
        canvas.rotate((this.mMinutes / 60.0f) * 360.0f, x, y);
        Drawable minuteHand = this.mMinuteHand;
        if (changed) {
            int w3 = minuteHand.getIntrinsicWidth();
            int h3 = minuteHand.getIntrinsicHeight();
            minuteHand.setBounds(x - (w3 / 2), y - (h3 / 2), x + (w3 / 2), y + (h3 / 2));
        }
        minuteHand.draw(canvas);
        canvas.restore();
        Drawable secondHand = this.mSecondHand;
        if (secondHand != null && this.mSecondsHandFps > 0) {
            canvas.save();
            canvas.rotate((this.mSeconds / 60.0f) * 360.0f, x, y);
            if (changed) {
                int w4 = secondHand.getIntrinsicWidth();
                int h4 = secondHand.getIntrinsicHeight();
                secondHand.setBounds(x - (w4 / 2), y - (h4 / 2), (w4 / 2) + x, y + (h4 / 2));
            }
            secondHand.draw(canvas);
            canvas.restore();
        }
        if (scaled) {
            canvas.restore();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Instant now() {
        return this.mClock.instant();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onTimeChanged() {
        Instant now = now();
        onTimeChanged(now.atZone(this.mClock.getZone()).toLocalTime(), now.toEpochMilli());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTimeChanged(LocalTime localTime, long nowMillis) {
        float round;
        float previousHour = this.mHour;
        float previousMinutes = this.mMinutes;
        float rawSeconds = localTime.getSecond() + (localTime.getNano() / 1.0E9f);
        int i = this.mSecondsHandFps;
        if (i <= 0) {
            round = rawSeconds;
        } else {
            round = Math.round(i * rawSeconds) / this.mSecondsHandFps;
        }
        this.mSeconds = round;
        this.mMinutes = localTime.getMinute() + (this.mSeconds / 60.0f);
        float f = this.mMinutes;
        float hour = localTime.getHour() + (f / 60.0f);
        this.mHour = hour;
        this.mChanged = true;
        if (((int) previousHour) != ((int) hour) || ((int) previousMinutes) != ((int) f)) {
            updateContentDescription(nowMillis);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createClock() {
        ZoneId zoneId = this.mTimeZone;
        if (zoneId == null) {
            this.mClock = Clock.systemDefaultZone();
        } else {
            this.mClock = Clock.system(zoneId);
        }
    }

    private void updateContentDescription(long timeMillis) {
        String contentDescription = DateUtils.formatDateRange(this.mContext, new Formatter(new StringBuilder(50), Locale.getDefault()), timeMillis, timeMillis, 129, getTimeZone()).toString();
        setContentDescription(contentDescription);
    }

    private static ZoneId toZoneId(String timeZone) {
        if (timeZone == null) {
            return null;
        }
        try {
            return ZoneId.of(timeZone);
        } catch (DateTimeException e) {
            Log.m103w(LOG_TAG, "Failed to parse time zone from " + timeZone, e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public final class TintInfo {
        boolean mHasTintBlendMode;
        boolean mHasTintList;
        BlendMode mTintBlendMode;
        ColorStateList mTintList;

        private TintInfo() {
        }

        Drawable apply(Drawable drawable) {
            if (drawable == null) {
                return null;
            }
            Drawable newDrawable = drawable.mutate();
            if (this.mHasTintList) {
                newDrawable.setTintList(this.mTintList);
            }
            if (this.mHasTintBlendMode) {
                newDrawable.setTintBlendMode(this.mTintBlendMode);
            }
            if (drawable.isStateful()) {
                newDrawable.setState(AnalogClock.this.getDrawableState());
            }
            return newDrawable;
        }
    }
}
