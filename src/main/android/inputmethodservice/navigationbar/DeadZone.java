package android.inputmethodservice.navigationbar;

import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.p008os.SystemClock;
import android.util.FloatProperty;
import android.util.Log;
import android.view.MotionEvent;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class DeadZone {
    private static final boolean CHATTY = true;
    public static final boolean DEBUG = false;
    private static final FloatProperty<DeadZone> FLASH_PROPERTY = new FloatProperty<DeadZone>("DeadZoneFlash") { // from class: android.inputmethodservice.navigationbar.DeadZone.1
        @Override // android.util.FloatProperty
        public void setValue(DeadZone object, float value) {
            object.setFlash(value);
        }

        @Override // android.util.Property
        public Float get(DeadZone object) {
            return Float.valueOf(object.getFlash());
        }
    };
    public static final int HORIZONTAL = 0;
    public static final String TAG = "DeadZone";
    public static final int VERTICAL = 1;
    private int mDecay;
    private int mDisplayRotation;
    private int mHold;
    private long mLastPokeTime;
    private final NavigationBarView mNavigationBarView;
    private boolean mShouldFlash;
    private int mSizeMax;
    private int mSizeMin;
    private boolean mVertical;
    private float mFlashFrac = 0.0f;
    private final Runnable mDebugFlash = new Runnable() { // from class: android.inputmethodservice.navigationbar.DeadZone.2
        @Override // java.lang.Runnable
        public void run() {
            ObjectAnimator.ofFloat(DeadZone.this, DeadZone.FLASH_PROPERTY, 1.0f, 0.0f).setDuration(150L).start();
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public DeadZone(NavigationBarView view) {
        this.mNavigationBarView = view;
        onConfigurationChanged(0);
    }

    static float lerp(float a, float b, float f) {
        return ((b - a) * f) + a;
    }

    private float getSize(long now) {
        int i = this.mSizeMax;
        if (i == 0) {
            return 0.0f;
        }
        long dt = now - this.mLastPokeTime;
        int i2 = this.mHold;
        int i3 = this.mDecay;
        if (dt > i2 + i3) {
            return this.mSizeMin;
        }
        if (dt < i2) {
            return i;
        }
        return (int) lerp(i, this.mSizeMin, ((float) (dt - i2)) / i3);
    }

    public void setFlashOnTouchCapture(boolean dbg) {
        this.mShouldFlash = dbg;
        this.mFlashFrac = 0.0f;
        this.mNavigationBarView.postInvalidate();
    }

    public void onConfigurationChanged(int rotation) {
        this.mDisplayRotation = rotation;
        Resources res = this.mNavigationBarView.getResources();
        this.mHold = 333;
        this.mDecay = 333;
        this.mSizeMin = NavigationBarUtils.dpToPx(12.0f, res);
        this.mSizeMax = NavigationBarUtils.dpToPx(32.0f, res);
        this.mVertical = res.getConfiguration().orientation == 2;
        setFlashOnTouchCapture(false);
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean consumeEvent;
        if (event.getToolType(0) == 3) {
            return false;
        }
        int action = event.getAction();
        if (action == 4) {
            poke(event);
            return true;
        }
        if (action == 0) {
            int size = (int) getSize(event.getEventTime());
            if (this.mVertical) {
                if (this.mDisplayRotation == 3) {
                    consumeEvent = event.getX() > ((float) (this.mNavigationBarView.getWidth() - size));
                } else {
                    consumeEvent = event.getX() < ((float) size);
                }
            } else {
                consumeEvent = event.getY() < ((float) size);
            }
            if (consumeEvent) {
                Log.m106v(TAG, "consuming errant click: (" + event.getX() + "," + event.getY() + NavigationBarInflaterView.KEY_CODE_END);
                if (this.mShouldFlash) {
                    this.mNavigationBarView.post(this.mDebugFlash);
                    this.mNavigationBarView.postInvalidate();
                }
                return true;
            }
        }
        return false;
    }

    private void poke(MotionEvent event) {
        this.mLastPokeTime = event.getEventTime();
        if (this.mShouldFlash) {
            this.mNavigationBarView.postInvalidate();
        }
    }

    public void setFlash(float f) {
        this.mFlashFrac = f;
        this.mNavigationBarView.postInvalidate();
    }

    public float getFlash() {
        return this.mFlashFrac;
    }

    public void onDraw(Canvas can) {
        if (!this.mShouldFlash || this.mFlashFrac <= 0.0f) {
            return;
        }
        int size = (int) getSize(SystemClock.uptimeMillis());
        if (this.mVertical) {
            if (this.mDisplayRotation == 3) {
                can.clipRect(can.getWidth() - size, 0, can.getWidth(), can.getHeight());
            } else {
                can.clipRect(0, 0, size, can.getHeight());
            }
        } else {
            can.clipRect(0, 0, can.getWidth(), size);
        }
        float frac = this.mFlashFrac;
        can.drawARGB((int) (255.0f * frac), 221, 238, 170);
    }
}
