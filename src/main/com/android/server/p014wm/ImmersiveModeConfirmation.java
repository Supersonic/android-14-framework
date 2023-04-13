package com.android.server.p014wm;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.ActivityThread;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Insets;
import android.graphics.Region;
import android.graphics.drawable.ColorDrawable;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Slog;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
/* renamed from: com.android.server.wm.ImmersiveModeConfirmation */
/* loaded from: classes2.dex */
public class ImmersiveModeConfirmation {
    public static boolean sConfirmed;
    public boolean mCanSystemBarsBeShownByUser;
    public ClingWindowView mClingWindow;
    public final Context mContext;
    public final HandlerC1875H mHandler;
    public final long mPanicThresholdMs;
    public long mPanicTime;
    public final long mShowDelayMs;
    public boolean mVrModeEnabled;
    public Context mWindowContext;
    public WindowManager mWindowManager;
    public final IBinder mWindowToken = new Binder();
    public int mLockTaskState = 0;
    public final Runnable mConfirm = new Runnable() { // from class: com.android.server.wm.ImmersiveModeConfirmation.1
        @Override // java.lang.Runnable
        public void run() {
            if (!ImmersiveModeConfirmation.sConfirmed) {
                ImmersiveModeConfirmation.sConfirmed = true;
                ImmersiveModeConfirmation.saveSetting(ImmersiveModeConfirmation.this.mContext);
            }
            ImmersiveModeConfirmation.this.handleHide();
        }
    };

    public ImmersiveModeConfirmation(Context context, Looper looper, boolean z, boolean z2) {
        Display display = context.getDisplay();
        Context systemUiContext = ActivityThread.currentActivityThread().getSystemUiContext();
        this.mContext = display.getDisplayId() != 0 ? systemUiContext.createDisplayContext(display) : systemUiContext;
        this.mHandler = new HandlerC1875H(looper);
        this.mShowDelayMs = context.getResources().getInteger(17695034) * 3;
        this.mPanicThresholdMs = context.getResources().getInteger(17694850);
        this.mVrModeEnabled = z;
        this.mCanSystemBarsBeShownByUser = z2;
    }

    public static boolean loadSetting(int i, Context context) {
        boolean z = sConfirmed;
        sConfirmed = false;
        String str = null;
        try {
            str = Settings.Secure.getStringForUser(context.getContentResolver(), "immersive_mode_confirmations", -2);
            sConfirmed = "confirmed".equals(str);
        } catch (Throwable th) {
            Slog.w("ImmersiveModeConfirmation", "Error loading confirmations, value=" + str, th);
        }
        return sConfirmed != z;
    }

    public static void saveSetting(Context context) {
        try {
            Settings.Secure.putStringForUser(context.getContentResolver(), "immersive_mode_confirmations", sConfirmed ? "confirmed" : null, -2);
        } catch (Throwable th) {
            Slog.w("ImmersiveModeConfirmation", "Error saving confirmations, sConfirmed=" + sConfirmed, th);
        }
    }

    public void release() {
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(2);
    }

    public boolean onSettingChanged(int i) {
        boolean loadSetting = loadSetting(i, this.mContext);
        if (loadSetting && sConfirmed) {
            this.mHandler.sendEmptyMessage(2);
        }
        return loadSetting;
    }

    public void immersiveModeChangedLw(int i, boolean z, boolean z2, boolean z3) {
        this.mHandler.removeMessages(1);
        if (z) {
            if (sConfirmed || !z2 || this.mVrModeEnabled || !this.mCanSystemBarsBeShownByUser || z3 || UserManager.isDeviceInDemoMode(this.mContext) || this.mLockTaskState == 1) {
                return;
            }
            Message obtainMessage = this.mHandler.obtainMessage(1);
            obtainMessage.arg1 = i;
            this.mHandler.sendMessageDelayed(obtainMessage, this.mShowDelayMs);
            return;
        }
        this.mHandler.sendEmptyMessage(2);
    }

    public boolean onPowerKeyDown(boolean z, long j, boolean z2, boolean z3) {
        if (!z && j - this.mPanicTime < this.mPanicThresholdMs) {
            return this.mClingWindow == null;
        }
        if (z && z2 && !z3) {
            this.mPanicTime = j;
        } else {
            this.mPanicTime = 0L;
        }
        return false;
    }

    public void confirmCurrentPrompt() {
        if (this.mClingWindow != null) {
            this.mHandler.post(this.mConfirm);
        }
    }

    public final void handleHide() {
        if (this.mClingWindow != null) {
            try {
                getWindowManager(-1).removeView(this.mClingWindow);
                this.mClingWindow = null;
            } catch (WindowManager.InvalidDisplayException e) {
                Slog.w("ImmersiveModeConfirmation", "Fail to hide the immersive confirmation window because of " + e);
            }
        }
    }

    public final WindowManager.LayoutParams getClingWindowLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, 2017, 16777504, -3);
        layoutParams.setFitInsetsTypes(layoutParams.getFitInsetsTypes() & (~WindowInsets.Type.statusBars()));
        layoutParams.privateFlags |= 536870928;
        layoutParams.setTitle("ImmersiveModeConfirmation");
        layoutParams.windowAnimations = 16974586;
        layoutParams.token = getWindowToken();
        return layoutParams;
    }

    public final FrameLayout.LayoutParams getBubbleLayoutParams() {
        return new FrameLayout.LayoutParams(this.mContext.getResources().getDimensionPixelSize(17105259), -2, 49);
    }

    public IBinder getWindowToken() {
        return this.mWindowToken;
    }

    /* renamed from: com.android.server.wm.ImmersiveModeConfirmation$ClingWindowView */
    /* loaded from: classes2.dex */
    public class ClingWindowView extends FrameLayout {
        public ViewGroup mClingLayout;
        public final ColorDrawable mColor;
        public ValueAnimator mColorAnim;
        public final Runnable mConfirm;
        public ViewTreeObserver.OnComputeInternalInsetsListener mInsetsListener;
        public final Interpolator mInterpolator;
        public BroadcastReceiver mReceiver;
        public Runnable mUpdateLayoutRunnable;

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            return true;
        }

        public ClingWindowView(Context context, Runnable runnable) {
            super(context);
            ColorDrawable colorDrawable = new ColorDrawable(0);
            this.mColor = colorDrawable;
            this.mUpdateLayoutRunnable = new Runnable() { // from class: com.android.server.wm.ImmersiveModeConfirmation.ClingWindowView.1
                @Override // java.lang.Runnable
                public void run() {
                    if (ClingWindowView.this.mClingLayout == null || ClingWindowView.this.mClingLayout.getParent() == null) {
                        return;
                    }
                    ClingWindowView.this.mClingLayout.setLayoutParams(ImmersiveModeConfirmation.this.getBubbleLayoutParams());
                }
            };
            this.mInsetsListener = new ViewTreeObserver.OnComputeInternalInsetsListener() { // from class: com.android.server.wm.ImmersiveModeConfirmation.ClingWindowView.2
                public final int[] mTmpInt2 = new int[2];

                public void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo internalInsetsInfo) {
                    ClingWindowView.this.mClingLayout.getLocationInWindow(this.mTmpInt2);
                    internalInsetsInfo.setTouchableInsets(3);
                    Region region = internalInsetsInfo.touchableRegion;
                    int[] iArr = this.mTmpInt2;
                    int i = iArr[0];
                    region.set(i, iArr[1], ClingWindowView.this.mClingLayout.getWidth() + i, this.mTmpInt2[1] + ClingWindowView.this.mClingLayout.getHeight());
                }
            };
            this.mReceiver = new BroadcastReceiver() { // from class: com.android.server.wm.ImmersiveModeConfirmation.ClingWindowView.3
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context2, Intent intent) {
                    if (intent.getAction().equals("android.intent.action.CONFIGURATION_CHANGED")) {
                        ClingWindowView clingWindowView = ClingWindowView.this;
                        clingWindowView.post(clingWindowView.mUpdateLayoutRunnable);
                    }
                }
            };
            this.mConfirm = runnable;
            setBackground(colorDrawable);
            setImportantForAccessibility(2);
            this.mInterpolator = AnimationUtils.loadInterpolator(((FrameLayout) this).mContext, 17563662);
        }

        @Override // android.view.ViewGroup, android.view.View
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((FrameLayout) this).mContext.getDisplay().getMetrics(displayMetrics);
            float f = displayMetrics.density;
            getViewTreeObserver().addOnComputeInternalInsetsListener(this.mInsetsListener);
            ViewGroup viewGroup = (ViewGroup) View.inflate(getContext(), 17367179, null);
            this.mClingLayout = viewGroup;
            ((Button) viewGroup.findViewById(16909314)).setOnClickListener(new View.OnClickListener() { // from class: com.android.server.wm.ImmersiveModeConfirmation.ClingWindowView.4
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ClingWindowView.this.mConfirm.run();
                }
            });
            addView(this.mClingLayout, ImmersiveModeConfirmation.this.getBubbleLayoutParams());
            if (ActivityManager.isHighEndGfx()) {
                final ViewGroup viewGroup2 = this.mClingLayout;
                viewGroup2.setAlpha(0.0f);
                viewGroup2.setTranslationY(f * (-96.0f));
                postOnAnimation(new Runnable() { // from class: com.android.server.wm.ImmersiveModeConfirmation.ClingWindowView.5
                    @Override // java.lang.Runnable
                    public void run() {
                        viewGroup2.animate().alpha(1.0f).translationY(0.0f).setDuration(250L).setInterpolator(ClingWindowView.this.mInterpolator).withLayer().start();
                        ClingWindowView.this.mColorAnim = ValueAnimator.ofObject(new ArgbEvaluator(), 0, Integer.MIN_VALUE);
                        ClingWindowView.this.mColorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.server.wm.ImmersiveModeConfirmation.ClingWindowView.5.1
                            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                ClingWindowView.this.mColor.setColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
                            }
                        });
                        ClingWindowView.this.mColorAnim.setDuration(250L);
                        ClingWindowView.this.mColorAnim.setInterpolator(ClingWindowView.this.mInterpolator);
                        ClingWindowView.this.mColorAnim.start();
                    }
                });
            } else {
                this.mColor.setColor(Integer.MIN_VALUE);
            }
            ((FrameLayout) this).mContext.registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.CONFIGURATION_CHANGED"));
        }

        @Override // android.view.ViewGroup, android.view.View
        public void onDetachedFromWindow() {
            ((FrameLayout) this).mContext.unregisterReceiver(this.mReceiver);
        }

        @Override // android.view.View
        public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
            return new WindowInsets.Builder(windowInsets).setInsets(WindowInsets.Type.systemBars(), Insets.NONE).build();
        }
    }

    public final WindowManager getWindowManager(int i) {
        if (this.mWindowManager == null || this.mWindowContext == null) {
            Context createWindowContext = this.mContext.createWindowContext(2017, getOptionsForWindowContext(i));
            this.mWindowContext = createWindowContext;
            WindowManager windowManager = (WindowManager) createWindowContext.getSystemService(WindowManager.class);
            this.mWindowManager = windowManager;
            return windowManager;
        }
        Bundle optionsForWindowContext = getOptionsForWindowContext(i);
        try {
            WindowManagerGlobal.getWindowManagerService().attachWindowContextToDisplayArea(this.mWindowContext.getWindowContextToken(), 2017, this.mContext.getDisplayId(), optionsForWindowContext);
            return this.mWindowManager;
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public final Bundle getOptionsForWindowContext(int i) {
        if (i == -1) {
            return null;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("root_display_area_id", i);
        return bundle;
    }

    public final void handleShow(int i) {
        this.mClingWindow = new ClingWindowView(this.mContext, this.mConfirm);
        try {
            getWindowManager(i).addView(this.mClingWindow, getClingWindowLayoutParams());
        } catch (WindowManager.InvalidDisplayException e) {
            Slog.w("ImmersiveModeConfirmation", "Fail to show the immersive confirmation window because of " + e);
        }
    }

    /* renamed from: com.android.server.wm.ImmersiveModeConfirmation$H */
    /* loaded from: classes2.dex */
    public final class HandlerC1875H extends Handler {
        public HandlerC1875H(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                ImmersiveModeConfirmation.this.handleShow(message.arg1);
            } else if (i != 2) {
            } else {
                ImmersiveModeConfirmation.this.handleHide();
            }
        }
    }

    public void onVrStateChangedLw(boolean z) {
        this.mVrModeEnabled = z;
        if (z) {
            this.mHandler.removeMessages(1);
            this.mHandler.sendEmptyMessage(2);
        }
    }

    public void onLockTaskModeChangedLw(int i) {
        this.mLockTaskState = i;
    }
}
