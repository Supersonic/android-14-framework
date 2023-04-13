package com.android.internal.display;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.hardware.display.DisplayManager;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.net.Uri;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.SystemClock;
import android.provider.Settings;
import android.util.MathUtils;
import android.util.Slog;
import com.android.internal.C4057R;
import java.io.PrintWriter;
/* loaded from: classes4.dex */
public class BrightnessSynchronizer {
    private static final boolean DEBUG = false;
    public static final float EPSILON = 0.001f;
    private static final int MSG_RUN_UPDATE = 1;
    private static final String TAG = "BrightnessSynchronizer";
    private static final long WAIT_FOR_RESPONSE_MILLIS = 200;
    private final BrightnessSyncObserver mBrightnessSyncObserver;
    private final Clock mClock;
    private final Context mContext;
    private BrightnessUpdate mCurrentUpdate;
    private DisplayManager mDisplayManager;
    private final Handler mHandler;
    private float mLatestFloatBrightness;
    private int mLatestIntBrightness;
    private BrightnessUpdate mPendingUpdate;
    private static final Uri BRIGHTNESS_URI = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
    private static int sBrightnessUpdateCount = 1;

    /* loaded from: classes4.dex */
    public interface Clock {
        long uptimeMillis();
    }

    public BrightnessSynchronizer(Context context) {
        this(context, Looper.getMainLooper(), new Clock() { // from class: com.android.internal.display.BrightnessSynchronizer$$ExternalSyntheticLambda0
            @Override // com.android.internal.display.BrightnessSynchronizer.Clock
            public final long uptimeMillis() {
                return SystemClock.uptimeMillis();
            }
        });
    }

    public BrightnessSynchronizer(Context context, Looper looper, Clock clock) {
        this.mContext = context;
        this.mClock = clock;
        this.mBrightnessSyncObserver = new BrightnessSyncObserver();
        this.mHandler = new BrightnessSynchronizerHandler(looper);
    }

    public void startSynchronizing() {
        if (this.mDisplayManager == null) {
            this.mDisplayManager = (DisplayManager) this.mContext.getSystemService(DisplayManager.class);
        }
        if (this.mBrightnessSyncObserver.isObserving()) {
            Slog.wtf(TAG, "Brightness sync observer requesting synchronization a second time.");
            return;
        }
        this.mLatestFloatBrightness = getScreenBrightnessFloat();
        this.mLatestIntBrightness = getScreenBrightnessInt();
        Slog.m94i(TAG, "Initial brightness readings: " + this.mLatestIntBrightness + "(int), " + this.mLatestFloatBrightness + "(float)");
        if (!Float.isNaN(this.mLatestFloatBrightness)) {
            this.mPendingUpdate = new BrightnessUpdate(2, this.mLatestFloatBrightness);
        } else {
            int i = this.mLatestIntBrightness;
            if (i != -1) {
                this.mPendingUpdate = new BrightnessUpdate(1, i);
            } else {
                float defaultBrightness = this.mContext.getResources().getFloat(C4057R.dimen.config_screenBrightnessSettingDefaultFloat);
                this.mPendingUpdate = new BrightnessUpdate(2, defaultBrightness);
                Slog.m94i(TAG, "Setting initial brightness to default value of: " + defaultBrightness);
            }
        }
        this.mBrightnessSyncObserver.startObserving(this.mHandler);
        this.mHandler.sendEmptyMessageAtTime(1, this.mClock.uptimeMillis());
    }

    public void dump(PrintWriter pw) {
        pw.println(TAG);
        pw.println("  mLatestIntBrightness=" + this.mLatestIntBrightness);
        pw.println("  mLatestFloatBrightness=" + this.mLatestFloatBrightness);
        pw.println("  mCurrentUpdate=" + this.mCurrentUpdate);
        pw.println("  mPendingUpdate=" + this.mPendingUpdate);
    }

    public static float brightnessIntToFloat(int brightnessInt) {
        if (brightnessInt == 0) {
            return -1.0f;
        }
        if (brightnessInt == -1) {
            return Float.NaN;
        }
        return MathUtils.constrainedMap(0.0f, 1.0f, 1.0f, 255.0f, brightnessInt);
    }

    public static int brightnessFloatToInt(float brightnessFloat) {
        return Math.round(brightnessFloatToIntRange(brightnessFloat));
    }

    public static float brightnessFloatToIntRange(float brightnessFloat) {
        if (floatEquals(brightnessFloat, -1.0f)) {
            return 0.0f;
        }
        if (Float.isNaN(brightnessFloat)) {
            return -1.0f;
        }
        return MathUtils.constrainedMap(1.0f, 255.0f, 0.0f, 1.0f, brightnessFloat);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBrightnessChangeFloat(float brightness) {
        this.mLatestFloatBrightness = brightness;
        handleBrightnessChange(2, brightness);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBrightnessChangeInt(int brightness) {
        this.mLatestIntBrightness = brightness;
        handleBrightnessChange(1, brightness);
    }

    private void handleBrightnessChange(int type, float brightness) {
        BrightnessUpdate brightnessUpdate = this.mCurrentUpdate;
        boolean swallowUpdate = brightnessUpdate != null && brightnessUpdate.swallowUpdate(type, brightness);
        BrightnessUpdate prevUpdate = null;
        if (!swallowUpdate) {
            prevUpdate = this.mPendingUpdate;
            this.mPendingUpdate = new BrightnessUpdate(type, brightness);
        }
        runUpdate();
        if (!swallowUpdate && this.mPendingUpdate != null) {
            Slog.m94i(TAG, "New PendingUpdate: " + this.mPendingUpdate + ", prev=" + prevUpdate);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runUpdate() {
        BrightnessUpdate brightnessUpdate;
        do {
            BrightnessUpdate brightnessUpdate2 = this.mCurrentUpdate;
            if (brightnessUpdate2 != null) {
                brightnessUpdate2.update();
                if (!this.mCurrentUpdate.isRunning()) {
                    if (this.mCurrentUpdate.isCompleted()) {
                        if (this.mCurrentUpdate.madeUpdates()) {
                            Slog.m94i(TAG, "Completed Update: " + this.mCurrentUpdate);
                        }
                        this.mCurrentUpdate = null;
                    }
                } else {
                    return;
                }
            }
            if (this.mCurrentUpdate == null && (brightnessUpdate = this.mPendingUpdate) != null) {
                this.mCurrentUpdate = brightnessUpdate;
                this.mPendingUpdate = null;
            }
        } while (this.mCurrentUpdate != null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getScreenBrightnessFloat() {
        return this.mDisplayManager.getBrightness(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getScreenBrightnessInt() {
        return Settings.System.getIntForUser(this.mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, -1, -2);
    }

    public static boolean floatEquals(float a, float b) {
        if (a == b) {
            return true;
        }
        return (Float.isNaN(a) && Float.isNaN(b)) || Math.abs(a - b) < 0.001f;
    }

    /* loaded from: classes4.dex */
    public class BrightnessUpdate {
        private static final int STATE_COMPLETED = 3;
        private static final int STATE_NOT_STARTED = 1;
        private static final int STATE_RUNNING = 2;
        static final int TYPE_FLOAT = 2;
        static final int TYPE_INT = 1;
        private final float mBrightness;
        private int mConfirmedTypes;
        private int mId;
        private final int mSourceType;
        private int mState;
        private long mTimeUpdated;
        private int mUpdatedTypes;

        BrightnessUpdate(int sourceType, float brightness) {
            int i = BrightnessSynchronizer.sBrightnessUpdateCount;
            BrightnessSynchronizer.sBrightnessUpdateCount = i + 1;
            this.mId = i;
            this.mSourceType = sourceType;
            this.mBrightness = brightness;
            this.mTimeUpdated = 0L;
            this.mUpdatedTypes = 0;
            this.mConfirmedTypes = 0;
            this.mState = 1;
        }

        public String toString() {
            return "{[" + this.mId + "] " + toStringLabel(this.mSourceType, this.mBrightness) + ", mUpdatedTypes=" + this.mUpdatedTypes + ", mConfirmedTypes=" + this.mConfirmedTypes + ", mTimeUpdated=" + this.mTimeUpdated + "}";
        }

        void update() {
            if (this.mState == 1) {
                this.mState = 2;
                int brightnessInt = getBrightnessAsInt();
                if (BrightnessSynchronizer.this.mLatestIntBrightness != brightnessInt) {
                    Settings.System.putIntForUser(BrightnessSynchronizer.this.mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightnessInt, -2);
                    BrightnessSynchronizer.this.mLatestIntBrightness = brightnessInt;
                    this.mUpdatedTypes |= 1;
                }
                float brightnessFloat = getBrightnessAsFloat();
                if (!BrightnessSynchronizer.floatEquals(BrightnessSynchronizer.this.mLatestFloatBrightness, brightnessFloat)) {
                    BrightnessSynchronizer.this.mDisplayManager.setBrightness(0, brightnessFloat);
                    BrightnessSynchronizer.this.mLatestFloatBrightness = brightnessFloat;
                    this.mUpdatedTypes |= 2;
                }
                if (this.mUpdatedTypes != 0) {
                    Slog.m94i(BrightnessSynchronizer.TAG, NavigationBarInflaterView.SIZE_MOD_START + this.mId + "] New Update " + toStringLabel(this.mSourceType, this.mBrightness) + " set brightness values: " + toStringLabel(this.mUpdatedTypes & 2, brightnessFloat) + " " + toStringLabel(this.mUpdatedTypes & 1, brightnessInt));
                    BrightnessSynchronizer.this.mHandler.sendEmptyMessageAtTime(1, BrightnessSynchronizer.this.mClock.uptimeMillis() + BrightnessSynchronizer.WAIT_FOR_RESPONSE_MILLIS);
                }
                this.mTimeUpdated = BrightnessSynchronizer.this.mClock.uptimeMillis();
            }
            if (this.mState == 2) {
                if (this.mConfirmedTypes == this.mUpdatedTypes || this.mTimeUpdated + BrightnessSynchronizer.WAIT_FOR_RESPONSE_MILLIS < BrightnessSynchronizer.this.mClock.uptimeMillis()) {
                    this.mState = 3;
                }
            }
        }

        boolean swallowUpdate(int type, float brightness) {
            if ((this.mUpdatedTypes & type) == type && (this.mConfirmedTypes & type) == 0) {
                boolean floatUpdateConfirmed = type == 2 && BrightnessSynchronizer.floatEquals(getBrightnessAsFloat(), brightness);
                boolean intUpdateConfirmed = type == 1 && getBrightnessAsInt() == ((int) brightness);
                if (floatUpdateConfirmed || intUpdateConfirmed) {
                    this.mConfirmedTypes |= type;
                    Slog.m94i(BrightnessSynchronizer.TAG, "Swallowing update of " + toStringLabel(type, brightness) + " by update: " + this);
                    return true;
                }
                return false;
            }
            return false;
        }

        boolean isRunning() {
            return this.mState == 2;
        }

        boolean isCompleted() {
            return this.mState == 3;
        }

        boolean madeUpdates() {
            return this.mUpdatedTypes != 0;
        }

        private int getBrightnessAsInt() {
            if (this.mSourceType == 1) {
                return (int) this.mBrightness;
            }
            return BrightnessSynchronizer.brightnessFloatToInt(this.mBrightness);
        }

        private float getBrightnessAsFloat() {
            if (this.mSourceType == 2) {
                return this.mBrightness;
            }
            return BrightnessSynchronizer.brightnessIntToFloat((int) this.mBrightness);
        }

        private String toStringLabel(int type, float brightness) {
            return type == 1 ? ((int) brightness) + "(i)" : type == 2 ? brightness + "(f)" : "";
        }
    }

    /* loaded from: classes4.dex */
    class BrightnessSynchronizerHandler extends Handler {
        BrightnessSynchronizerHandler(Looper looper) {
            super(looper);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    BrightnessSynchronizer.this.runUpdate();
                    return;
                default:
                    super.handleMessage(msg);
                    return;
            }
        }
    }

    /* loaded from: classes4.dex */
    private class BrightnessSyncObserver {
        private boolean mIsObserving;
        private final DisplayManager.DisplayListener mListener;

        private BrightnessSyncObserver() {
            this.mListener = new DisplayManager.DisplayListener() { // from class: com.android.internal.display.BrightnessSynchronizer.BrightnessSyncObserver.1
                @Override // android.hardware.display.DisplayManager.DisplayListener
                public void onDisplayAdded(int displayId) {
                }

                @Override // android.hardware.display.DisplayManager.DisplayListener
                public void onDisplayRemoved(int displayId) {
                }

                @Override // android.hardware.display.DisplayManager.DisplayListener
                public void onDisplayChanged(int displayId) {
                    BrightnessSynchronizer.this.handleBrightnessChangeFloat(BrightnessSynchronizer.this.getScreenBrightnessFloat());
                }
            };
        }

        private ContentObserver createBrightnessContentObserver(Handler handler) {
            return new ContentObserver(handler) { // from class: com.android.internal.display.BrightnessSynchronizer.BrightnessSyncObserver.2
                @Override // android.database.ContentObserver
                public void onChange(boolean selfChange, Uri uri) {
                    if (!selfChange && BrightnessSynchronizer.BRIGHTNESS_URI.equals(uri)) {
                        BrightnessSynchronizer.this.handleBrightnessChangeInt(BrightnessSynchronizer.this.getScreenBrightnessInt());
                    }
                }
            };
        }

        boolean isObserving() {
            return this.mIsObserving;
        }

        void startObserving(Handler handler) {
            ContentResolver cr = BrightnessSynchronizer.this.mContext.getContentResolver();
            cr.registerContentObserver(BrightnessSynchronizer.BRIGHTNESS_URI, false, createBrightnessContentObserver(handler), -1);
            BrightnessSynchronizer.this.mDisplayManager.registerDisplayListener(this.mListener, handler, 8L);
            this.mIsObserving = true;
        }
    }
}
