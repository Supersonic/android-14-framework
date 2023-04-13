package com.android.server.display;

import android.content.Context;
import android.os.Handler;
import android.os.Trace;
import android.util.FloatProperty;
import android.view.Choreographer;
import android.view.Display;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public final class DisplayPowerState {
    public static String COUNTER_COLOR_FADE = "ColorFadeLevel";
    public final DisplayBlanker mBlanker;
    public Runnable mCleanListener;
    public final ColorFade mColorFade;
    public boolean mColorFadeDrawPending;
    public float mColorFadeLevel;
    public boolean mColorFadePrepared;
    public boolean mColorFadeReady;
    public final int mDisplayId;
    public final PhotonicModulator mPhotonicModulator;
    public float mScreenBrightness;
    public boolean mScreenReady;
    public int mScreenState;
    public boolean mScreenUpdatePending;
    public float mSdrScreenBrightness;
    public volatile boolean mStopped;
    public static final FloatProperty<DisplayPowerState> COLOR_FADE_LEVEL = new FloatProperty<DisplayPowerState>("electronBeamLevel") { // from class: com.android.server.display.DisplayPowerState.1
        @Override // android.util.FloatProperty
        public void setValue(DisplayPowerState displayPowerState, float f) {
            displayPowerState.setColorFadeLevel(f);
        }

        @Override // android.util.Property
        public Float get(DisplayPowerState displayPowerState) {
            return Float.valueOf(displayPowerState.getColorFadeLevel());
        }
    };
    public static final FloatProperty<DisplayPowerState> SCREEN_BRIGHTNESS_FLOAT = new FloatProperty<DisplayPowerState>("screenBrightnessFloat") { // from class: com.android.server.display.DisplayPowerState.2
        @Override // android.util.FloatProperty
        public void setValue(DisplayPowerState displayPowerState, float f) {
            displayPowerState.setScreenBrightness(f);
        }

        @Override // android.util.Property
        public Float get(DisplayPowerState displayPowerState) {
            return Float.valueOf(displayPowerState.getScreenBrightness());
        }
    };
    public static final FloatProperty<DisplayPowerState> SCREEN_SDR_BRIGHTNESS_FLOAT = new FloatProperty<DisplayPowerState>("sdrScreenBrightnessFloat") { // from class: com.android.server.display.DisplayPowerState.3
        @Override // android.util.FloatProperty
        public void setValue(DisplayPowerState displayPowerState, float f) {
            displayPowerState.setSdrScreenBrightness(f);
        }

        @Override // android.util.Property
        public Float get(DisplayPowerState displayPowerState) {
            return Float.valueOf(displayPowerState.getSdrScreenBrightness());
        }
    };
    public final Runnable mScreenUpdateRunnable = new Runnable() { // from class: com.android.server.display.DisplayPowerState.4
        @Override // java.lang.Runnable
        public void run() {
            DisplayPowerState.this.mScreenUpdatePending = false;
            float f = -1.0f;
            float f2 = (DisplayPowerState.this.mScreenState == 1 || DisplayPowerState.this.mColorFadeLevel <= 0.0f) ? -1.0f : DisplayPowerState.this.mScreenBrightness;
            if (DisplayPowerState.this.mScreenState != 1 && DisplayPowerState.this.mColorFadeLevel > 0.0f) {
                f = DisplayPowerState.this.mSdrScreenBrightness;
            }
            if (DisplayPowerState.this.mPhotonicModulator.setState(DisplayPowerState.this.mScreenState, f2, f)) {
                DisplayPowerState.this.mScreenReady = true;
                DisplayPowerState.this.invokeCleanListenerIfNeeded();
            }
        }
    };
    public final Runnable mColorFadeDrawRunnable = new Runnable() { // from class: com.android.server.display.DisplayPowerState.5
        @Override // java.lang.Runnable
        public void run() {
            DisplayPowerState.this.mColorFadeDrawPending = false;
            if (DisplayPowerState.this.mColorFadePrepared) {
                DisplayPowerState.this.mColorFade.draw(DisplayPowerState.this.mColorFadeLevel);
                Trace.traceCounter(131072L, DisplayPowerState.COUNTER_COLOR_FADE, Math.round(DisplayPowerState.this.mColorFadeLevel * 100.0f));
            }
            DisplayPowerState.this.mColorFadeReady = true;
            DisplayPowerState.this.invokeCleanListenerIfNeeded();
        }
    };
    public final Handler mHandler = new Handler(true);
    public final Choreographer mChoreographer = Choreographer.getInstance();

    public DisplayPowerState(DisplayBlanker displayBlanker, ColorFade colorFade, int i, int i2) {
        this.mBlanker = displayBlanker;
        this.mColorFade = colorFade;
        PhotonicModulator photonicModulator = new PhotonicModulator();
        this.mPhotonicModulator = photonicModulator;
        photonicModulator.start();
        this.mDisplayId = i;
        this.mScreenState = i2;
        float f = i2 != 1 ? 1.0f : -1.0f;
        this.mScreenBrightness = f;
        this.mSdrScreenBrightness = f;
        scheduleScreenUpdate();
        this.mColorFadePrepared = false;
        this.mColorFadeLevel = 1.0f;
        this.mColorFadeReady = true;
    }

    public void setScreenState(int i) {
        if (this.mScreenState != i) {
            this.mScreenState = i;
            this.mScreenReady = false;
            scheduleScreenUpdate();
        }
    }

    public int getScreenState() {
        return this.mScreenState;
    }

    public void setSdrScreenBrightness(float f) {
        if (this.mSdrScreenBrightness != f) {
            this.mSdrScreenBrightness = f;
            if (this.mScreenState != 1) {
                this.mScreenReady = false;
                scheduleScreenUpdate();
            }
        }
    }

    public float getSdrScreenBrightness() {
        return this.mSdrScreenBrightness;
    }

    public void setScreenBrightness(float f) {
        if (this.mScreenBrightness != f) {
            this.mScreenBrightness = f;
            if (this.mScreenState != 1) {
                this.mScreenReady = false;
                scheduleScreenUpdate();
            }
        }
    }

    public float getScreenBrightness() {
        return this.mScreenBrightness;
    }

    public boolean prepareColorFade(Context context, int i) {
        ColorFade colorFade = this.mColorFade;
        if (colorFade == null || !colorFade.prepare(context, i)) {
            this.mColorFadePrepared = false;
            this.mColorFadeReady = true;
            return false;
        }
        this.mColorFadePrepared = true;
        this.mColorFadeReady = false;
        scheduleColorFadeDraw();
        return true;
    }

    public void dismissColorFade() {
        Trace.traceCounter(131072L, COUNTER_COLOR_FADE, 100);
        ColorFade colorFade = this.mColorFade;
        if (colorFade != null) {
            colorFade.dismiss();
        }
        this.mColorFadePrepared = false;
        this.mColorFadeReady = true;
    }

    public void dismissColorFadeResources() {
        ColorFade colorFade = this.mColorFade;
        if (colorFade != null) {
            colorFade.dismissResources();
        }
    }

    public void setColorFadeLevel(float f) {
        if (this.mColorFadeLevel != f) {
            this.mColorFadeLevel = f;
            if (this.mScreenState != 1) {
                this.mScreenReady = false;
                scheduleScreenUpdate();
            }
            if (this.mColorFadePrepared) {
                this.mColorFadeReady = false;
                scheduleColorFadeDraw();
            }
        }
    }

    public float getColorFadeLevel() {
        return this.mColorFadeLevel;
    }

    public boolean waitUntilClean(Runnable runnable) {
        if (!this.mScreenReady || !this.mColorFadeReady) {
            this.mCleanListener = runnable;
            return false;
        }
        this.mCleanListener = null;
        return true;
    }

    public void stop() {
        this.mStopped = true;
        this.mPhotonicModulator.interrupt();
        dismissColorFade();
        this.mCleanListener = null;
        this.mHandler.removeCallbacksAndMessages(null);
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("Display Power State:");
        printWriter.println("  mStopped=" + this.mStopped);
        printWriter.println("  mScreenState=" + Display.stateToString(this.mScreenState));
        printWriter.println("  mScreenBrightness=" + this.mScreenBrightness);
        printWriter.println("  mSdrScreenBrightness=" + this.mSdrScreenBrightness);
        printWriter.println("  mScreenReady=" + this.mScreenReady);
        printWriter.println("  mScreenUpdatePending=" + this.mScreenUpdatePending);
        printWriter.println("  mColorFadePrepared=" + this.mColorFadePrepared);
        printWriter.println("  mColorFadeLevel=" + this.mColorFadeLevel);
        printWriter.println("  mColorFadeReady=" + this.mColorFadeReady);
        printWriter.println("  mColorFadeDrawPending=" + this.mColorFadeDrawPending);
        this.mPhotonicModulator.dump(printWriter);
        ColorFade colorFade = this.mColorFade;
        if (colorFade != null) {
            colorFade.dump(printWriter);
        }
    }

    public void resetScreenState() {
        this.mScreenState = 0;
        this.mScreenReady = false;
    }

    public final void scheduleScreenUpdate() {
        if (this.mScreenUpdatePending) {
            return;
        }
        this.mScreenUpdatePending = true;
        postScreenUpdateThreadSafe();
    }

    public final void postScreenUpdateThreadSafe() {
        this.mHandler.removeCallbacks(this.mScreenUpdateRunnable);
        this.mHandler.post(this.mScreenUpdateRunnable);
    }

    public final void scheduleColorFadeDraw() {
        if (this.mColorFadeDrawPending) {
            return;
        }
        this.mColorFadeDrawPending = true;
        this.mChoreographer.postCallback(3, this.mColorFadeDrawRunnable, null);
    }

    public final void invokeCleanListenerIfNeeded() {
        Runnable runnable = this.mCleanListener;
        if (runnable != null && this.mScreenReady && this.mColorFadeReady) {
            this.mCleanListener = null;
            runnable.run();
        }
    }

    /* loaded from: classes.dex */
    public final class PhotonicModulator extends Thread {
        public float mActualBacklight;
        public float mActualSdrBacklight;
        public int mActualState;
        public boolean mBacklightChangeInProgress;
        public final Object mLock;
        public float mPendingBacklight;
        public float mPendingSdrBacklight;
        public int mPendingState;
        public boolean mStateChangeInProgress;

        public PhotonicModulator() {
            super("PhotonicModulator");
            this.mLock = new Object();
            this.mPendingState = 0;
            this.mPendingBacklight = Float.NaN;
            this.mPendingSdrBacklight = Float.NaN;
            this.mActualState = 0;
            this.mActualBacklight = Float.NaN;
            this.mActualSdrBacklight = Float.NaN;
        }

        /* JADX WARN: Removed duplicated region for block: B:25:0x0034 A[ADDED_TO_REGION] */
        /* JADX WARN: Removed duplicated region for block: B:31:0x003e A[Catch: all -> 0x0057, TryCatch #0 {, blocks: (B:4:0x0003, B:8:0x000c, B:10:0x0012, B:39:0x004f, B:43:0x0055, B:17:0x0020, B:19:0x002a, B:29:0x003a, B:31:0x003e, B:36:0x0046, B:38:0x004a), top: B:48:0x0003 }] */
        /* JADX WARN: Removed duplicated region for block: B:38:0x004a A[Catch: all -> 0x0057, TryCatch #0 {, blocks: (B:4:0x0003, B:8:0x000c, B:10:0x0012, B:39:0x004f, B:43:0x0055, B:17:0x0020, B:19:0x002a, B:29:0x003a, B:31:0x003e, B:36:0x0046, B:38:0x004a), top: B:48:0x0003 }] */
        /* JADX WARN: Removed duplicated region for block: B:41:0x0053  */
        /* JADX WARN: Removed duplicated region for block: B:42:0x0054  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public boolean setState(int i, float f, float f2) {
            boolean z;
            boolean z2;
            boolean z3;
            boolean z4;
            boolean z5;
            boolean z6;
            synchronized (this.mLock) {
                z = true;
                boolean z7 = i != this.mPendingState;
                if (f == this.mPendingBacklight && f2 == this.mPendingSdrBacklight) {
                    z2 = false;
                    if (!z7 || z2) {
                        this.mPendingState = i;
                        this.mPendingBacklight = f;
                        this.mPendingSdrBacklight = f2;
                        z3 = this.mStateChangeInProgress;
                        if (!z3 && !this.mBacklightChangeInProgress) {
                            z4 = false;
                            if (!z7 && !z3) {
                                z5 = false;
                                this.mStateChangeInProgress = z5;
                                if (!z2 && !this.mBacklightChangeInProgress) {
                                    z6 = false;
                                    this.mBacklightChangeInProgress = z6;
                                    if (!z4) {
                                        this.mLock.notifyAll();
                                    }
                                }
                                z6 = true;
                                this.mBacklightChangeInProgress = z6;
                                if (!z4) {
                                }
                            }
                            z5 = true;
                            this.mStateChangeInProgress = z5;
                            if (!z2) {
                                z6 = false;
                                this.mBacklightChangeInProgress = z6;
                                if (!z4) {
                                }
                            }
                            z6 = true;
                            this.mBacklightChangeInProgress = z6;
                            if (!z4) {
                            }
                        }
                        z4 = true;
                        if (!z7) {
                            z5 = false;
                            this.mStateChangeInProgress = z5;
                            if (!z2) {
                            }
                            z6 = true;
                            this.mBacklightChangeInProgress = z6;
                            if (!z4) {
                            }
                        }
                        z5 = true;
                        this.mStateChangeInProgress = z5;
                        if (!z2) {
                        }
                        z6 = true;
                        this.mBacklightChangeInProgress = z6;
                        if (!z4) {
                        }
                    }
                    if (this.mStateChangeInProgress) {
                        z = false;
                    }
                }
                z2 = true;
                if (!z7) {
                }
                this.mPendingState = i;
                this.mPendingBacklight = f;
                this.mPendingSdrBacklight = f2;
                z3 = this.mStateChangeInProgress;
                if (!z3) {
                    z4 = false;
                    if (!z7) {
                    }
                    z5 = true;
                    this.mStateChangeInProgress = z5;
                    if (!z2) {
                    }
                    z6 = true;
                    this.mBacklightChangeInProgress = z6;
                    if (!z4) {
                    }
                    if (this.mStateChangeInProgress) {
                    }
                }
                z4 = true;
                if (!z7) {
                }
                z5 = true;
                this.mStateChangeInProgress = z5;
                if (!z2) {
                }
                z6 = true;
                this.mBacklightChangeInProgress = z6;
                if (!z4) {
                }
                if (this.mStateChangeInProgress) {
                }
            }
            return z;
        }

        public void dump(PrintWriter printWriter) {
            synchronized (this.mLock) {
                printWriter.println();
                printWriter.println("Photonic Modulator State:");
                printWriter.println("  mPendingState=" + Display.stateToString(this.mPendingState));
                printWriter.println("  mPendingBacklight=" + this.mPendingBacklight);
                printWriter.println("  mPendingSdrBacklight=" + this.mPendingSdrBacklight);
                printWriter.println("  mActualState=" + Display.stateToString(this.mActualState));
                printWriter.println("  mActualBacklight=" + this.mActualBacklight);
                printWriter.println("  mActualSdrBacklight=" + this.mActualSdrBacklight);
                printWriter.println("  mStateChangeInProgress=" + this.mStateChangeInProgress);
                printWriter.println("  mBacklightChangeInProgress=" + this.mBacklightChangeInProgress);
            }
        }

        /* JADX WARN: Can't wrap try/catch for region: R(18:3|4|(1:6)(1:52)|7|(12:12|(1:14)|(1:16)|(1:50)(1:20)|(1:24)|49|29|30|31|32|33|34)|51|(0)|(0)|(1:18)|50|(1:24)|49|29|30|31|32|33|34) */
        /* JADX WARN: Code restructure failed: missing block: B:29:0x0042, code lost:
            if (r3 != false) goto L44;
         */
        /* JADX WARN: Code restructure failed: missing block: B:31:0x0045, code lost:
            r9.mActualState = r1;
            r9.mActualBacklight = r5;
            r9.mActualSdrBacklight = r6;
         */
        /* JADX WARN: Code restructure failed: missing block: B:33:0x004c, code lost:
            r9.this$0.mBlanker.requestDisplayState(r9.this$0.mDisplayId, r1, r5, r6);
         */
        /* JADX WARN: Code restructure failed: missing block: B:38:0x006c, code lost:
            if (r9.this$0.mStopped != false) goto L39;
         */
        /* JADX WARN: Code restructure failed: missing block: B:40:0x006f, code lost:
            return;
         */
        /* JADX WARN: Removed duplicated region for block: B:16:0x0024 A[Catch: all -> 0x0072, TryCatch #1 {, blocks: (B:4:0x0003, B:8:0x000e, B:10:0x0018, B:16:0x0024, B:18:0x002d, B:20:0x0031, B:31:0x0045, B:32:0x004b, B:34:0x005c, B:35:0x0060, B:41:0x0070, B:37:0x0066, B:39:0x006e), top: B:48:0x0003, inners: #0 }] */
        /* JADX WARN: Removed duplicated region for block: B:18:0x002d A[Catch: all -> 0x0072, TryCatch #1 {, blocks: (B:4:0x0003, B:8:0x000e, B:10:0x0018, B:16:0x0024, B:18:0x002d, B:20:0x0031, B:31:0x0045, B:32:0x004b, B:34:0x005c, B:35:0x0060, B:41:0x0070, B:37:0x0066, B:39:0x006e), top: B:48:0x0003, inners: #0 }] */
        @Override // java.lang.Thread, java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void run() {
            boolean z;
            while (true) {
                synchronized (this.mLock) {
                    int i = this.mPendingState;
                    boolean z2 = true;
                    boolean z3 = i != this.mActualState;
                    float f = this.mPendingBacklight;
                    float f2 = this.mPendingSdrBacklight;
                    if (f == this.mActualBacklight && f2 == this.mActualSdrBacklight) {
                        z = false;
                        if (!z3) {
                            DisplayPowerState.this.postScreenUpdateThreadSafe();
                            this.mStateChangeInProgress = false;
                        }
                        if (!z) {
                            this.mBacklightChangeInProgress = false;
                        }
                        boolean z4 = i == 0 && !Float.isNaN(f);
                        if (!z3 && !z) {
                            z2 = false;
                        }
                        this.mStateChangeInProgress = false;
                        this.mBacklightChangeInProgress = false;
                        this.mLock.wait();
                    }
                    z = true;
                    if (!z3) {
                    }
                    if (!z) {
                    }
                    if (i == 0) {
                    }
                    if (!z3) {
                        z2 = false;
                    }
                    this.mStateChangeInProgress = false;
                    this.mBacklightChangeInProgress = false;
                    this.mLock.wait();
                }
            }
        }
    }
}
