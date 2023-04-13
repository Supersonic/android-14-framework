package android.widget;

import android.content.Context;
import android.database.ContentObserver;
import android.p008os.Handler;
import android.p008os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import java.util.Calendar;
@Deprecated
/* loaded from: classes4.dex */
public class DigitalClock extends TextView {
    Calendar mCalendar;
    String mFormat;
    private FormatChangeObserver mFormatChangeObserver;
    private Handler mHandler;
    private Runnable mTicker;
    private boolean mTickerStopped;

    public DigitalClock(Context context) {
        super(context);
        this.mTickerStopped = false;
        initClock();
    }

    public DigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTickerStopped = false;
        initClock();
    }

    private void initClock() {
        if (this.mCalendar == null) {
            this.mCalendar = Calendar.getInstance();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        this.mTickerStopped = false;
        super.onAttachedToWindow();
        this.mFormatChangeObserver = new FormatChangeObserver();
        getContext().getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, this.mFormatChangeObserver);
        setFormat();
        this.mHandler = new Handler();
        Runnable runnable = new Runnable() { // from class: android.widget.DigitalClock.1
            @Override // java.lang.Runnable
            public void run() {
                if (DigitalClock.this.mTickerStopped) {
                    return;
                }
                DigitalClock.this.mCalendar.setTimeInMillis(System.currentTimeMillis());
                DigitalClock digitalClock = DigitalClock.this;
                digitalClock.setText(DateFormat.format(digitalClock.mFormat, DigitalClock.this.mCalendar));
                DigitalClock.this.invalidate();
                long now = SystemClock.uptimeMillis();
                long next = (1000 - (now % 1000)) + now;
                DigitalClock.this.mHandler.postAtTime(DigitalClock.this.mTicker, next);
            }
        };
        this.mTicker = runnable;
        runnable.run();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mTickerStopped = true;
        getContext().getContentResolver().unregisterContentObserver(this.mFormatChangeObserver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFormat() {
        this.mFormat = DateFormat.getTimeFormatString(getContext());
    }

    /* loaded from: classes4.dex */
    private class FormatChangeObserver extends ContentObserver {
        public FormatChangeObserver() {
            super(new Handler());
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            DigitalClock.this.setFormat();
        }
    }

    @Override // android.widget.TextView, android.view.View
    public CharSequence getAccessibilityClassName() {
        return DigitalClock.class.getName();
    }
}
