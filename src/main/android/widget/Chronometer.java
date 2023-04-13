package android.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.icu.text.MeasureFormat;
import android.icu.util.Measure;
import android.icu.util.MeasureUnit;
import android.net.Uri;
import android.p008os.SystemClock;
import android.provider.Telephony;
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
import java.util.ArrayList;
import java.util.Formatter;
import java.util.IllegalFormatException;
import java.util.Locale;
@RemoteViews.RemoteView
/* loaded from: classes4.dex */
public class Chronometer extends TextView {
    private static final int HOUR_IN_SEC = 3600;
    private static final int MIN_IN_SEC = 60;
    private static final String TAG = "Chronometer";
    private long mBase;
    private boolean mCountDown;
    private String mFormat;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private Object[] mFormatterArgs;
    private Locale mFormatterLocale;
    private boolean mLogged;
    private long mNow;
    private OnChronometerTickListener mOnChronometerTickListener;
    private StringBuilder mRecycle;
    private boolean mRunning;
    private boolean mStarted;
    private final Runnable mTickRunnable;
    private boolean mVisible;

    /* loaded from: classes4.dex */
    public interface OnChronometerTickListener {
        void onChronometerTick(Chronometer chronometer);
    }

    /* loaded from: classes4.dex */
    public final class InspectionCompanion implements android.view.inspector.InspectionCompanion<Chronometer> {
        private int mCountDownId;
        private int mFormatId;
        private boolean mPropertiesMapped = false;

        @Override // android.view.inspector.InspectionCompanion
        public void mapProperties(PropertyMapper propertyMapper) {
            this.mCountDownId = propertyMapper.mapBoolean("countDown", 16844059);
            this.mFormatId = propertyMapper.mapObject(Telephony.CellBroadcasts.MESSAGE_FORMAT, 16843013);
            this.mPropertiesMapped = true;
        }

        @Override // android.view.inspector.InspectionCompanion
        public void readProperties(Chronometer node, PropertyReader propertyReader) {
            if (!this.mPropertiesMapped) {
                throw new InspectionCompanion.UninitializedPropertyMapException();
            }
            propertyReader.readBoolean(this.mCountDownId, node.isCountDown());
            propertyReader.readObject(this.mFormatId, node.getFormat());
        }
    }

    public Chronometer(Context context) {
        this(context, null, 0);
    }

    public Chronometer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Chronometer(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public Chronometer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mFormatterArgs = new Object[1];
        this.mRecycle = new StringBuilder(8);
        this.mTickRunnable = new Runnable() { // from class: android.widget.Chronometer.1
            @Override // java.lang.Runnable
            public void run() {
                if (Chronometer.this.mRunning) {
                    Chronometer.this.updateText(SystemClock.elapsedRealtime());
                    Chronometer.this.dispatchChronometerTick();
                    Chronometer chronometer = Chronometer.this;
                    chronometer.postDelayed(chronometer.mTickRunnable, 1000L);
                }
            }
        };
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.Chronometer, defStyleAttr, defStyleRes);
        saveAttributeDataForStyleable(context, C4057R.styleable.Chronometer, attrs, a, defStyleAttr, defStyleRes);
        setFormat(a.getString(0));
        setCountDown(a.getBoolean(1, false));
        a.recycle();
        init();
    }

    private void init() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        this.mBase = elapsedRealtime;
        updateText(elapsedRealtime);
    }

    @RemotableViewMethod
    public void setCountDown(boolean countDown) {
        this.mCountDown = countDown;
        updateText(SystemClock.elapsedRealtime());
    }

    public boolean isCountDown() {
        return this.mCountDown;
    }

    public boolean isTheFinalCountDown() {
        try {
            getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://youtu.be/9jK-NcRmVcw")).addCategory(Intent.CATEGORY_BROWSABLE).addFlags(528384));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @RemotableViewMethod
    public void setBase(long base) {
        this.mBase = base;
        dispatchChronometerTick();
        updateText(SystemClock.elapsedRealtime());
    }

    public long getBase() {
        return this.mBase;
    }

    @RemotableViewMethod
    public void setFormat(String format) {
        this.mFormat = format;
        if (format != null && this.mFormatBuilder == null) {
            this.mFormatBuilder = new StringBuilder(format.length() * 2);
        }
    }

    public String getFormat() {
        return this.mFormat;
    }

    public void setOnChronometerTickListener(OnChronometerTickListener listener) {
        this.mOnChronometerTickListener = listener;
    }

    public OnChronometerTickListener getOnChronometerTickListener() {
        return this.mOnChronometerTickListener;
    }

    public void start() {
        this.mStarted = true;
        updateRunning();
    }

    public void stop() {
        this.mStarted = false;
        updateRunning();
    }

    @RemotableViewMethod
    public void setStarted(boolean started) {
        this.mStarted = started;
        updateRunning();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mVisible = false;
        updateRunning();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        this.mVisible = visibility == 0;
        updateRunning();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        updateRunning();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void updateText(long now) {
        this.mNow = now;
        long seconds = (this.mCountDown ? this.mBase - now : now - this.mBase) / 1000;
        boolean negative = false;
        if (seconds < 0) {
            seconds = -seconds;
            negative = true;
        }
        String text = DateUtils.formatElapsedTime(this.mRecycle, seconds);
        if (negative) {
            text = getResources().getString(C4057R.string.negative_duration, text);
        }
        if (this.mFormat != null) {
            Locale loc = Locale.getDefault();
            if (this.mFormatter == null || !loc.equals(this.mFormatterLocale)) {
                this.mFormatterLocale = loc;
                this.mFormatter = new Formatter(this.mFormatBuilder, loc);
            }
            this.mFormatBuilder.setLength(0);
            Object[] objArr = this.mFormatterArgs;
            objArr[0] = text;
            try {
                this.mFormatter.format(this.mFormat, objArr);
                text = this.mFormatBuilder.toString();
            } catch (IllegalFormatException e) {
                if (!this.mLogged) {
                    Log.m104w(TAG, "Illegal format string: " + this.mFormat);
                    this.mLogged = true;
                }
            }
        }
        setText(text);
    }

    private void updateRunning() {
        boolean running = this.mVisible && this.mStarted && isShown();
        if (running != this.mRunning) {
            if (running) {
                updateText(SystemClock.elapsedRealtime());
                dispatchChronometerTick();
                postDelayed(this.mTickRunnable, 1000L);
            } else {
                removeCallbacks(this.mTickRunnable);
            }
            this.mRunning = running;
        }
    }

    void dispatchChronometerTick() {
        OnChronometerTickListener onChronometerTickListener = this.mOnChronometerTickListener;
        if (onChronometerTickListener != null) {
            onChronometerTickListener.onChronometerTick(this);
        }
    }

    private static String formatDuration(long ms) {
        int duration = (int) (ms / 1000);
        if (duration < 0) {
            duration = -duration;
        }
        int h = 0;
        int m = 0;
        if (duration >= 3600) {
            h = duration / 3600;
            duration -= h * 3600;
        }
        if (duration >= 60) {
            m = duration / 60;
            duration -= m * 60;
        }
        int s = duration;
        ArrayList<Measure> measures = new ArrayList<>();
        if (h > 0) {
            measures.add(new Measure(Integer.valueOf(h), MeasureUnit.HOUR));
        }
        if (m > 0) {
            measures.add(new Measure(Integer.valueOf(m), MeasureUnit.MINUTE));
        }
        measures.add(new Measure(Integer.valueOf(s), MeasureUnit.SECOND));
        return MeasureFormat.getInstance(Locale.getDefault(), MeasureFormat.FormatWidth.WIDE).formatMeasures((Measure[]) measures.toArray(new Measure[measures.size()]));
    }

    @Override // android.view.View
    public CharSequence getContentDescription() {
        return formatDuration(this.mNow - this.mBase);
    }

    @Override // android.widget.TextView, android.view.View
    public CharSequence getAccessibilityClassName() {
        return Chronometer.class.getName();
    }
}
