package com.android.server.accessibility;

import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.media.AudioPlaybackConfiguration;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.accessibility.FlashNotificationsController;
import com.android.server.backup.BackupAgentTimeoutParameters;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class FlashNotificationsController {
    @VisibleForTesting
    static final String ACTION_FLASH_NOTIFICATION_START_PREVIEW = "com.android.internal.intent.action.FLASH_NOTIFICATION_START_PREVIEW";
    @VisibleForTesting
    static final String ACTION_FLASH_NOTIFICATION_STOP_PREVIEW = "com.android.internal.intent.action.FLASH_NOTIFICATION_STOP_PREVIEW";
    @VisibleForTesting
    static final String EXTRA_FLASH_NOTIFICATION_PREVIEW_COLOR = "com.android.internal.intent.extra.FLASH_NOTIFICATION_PREVIEW_COLOR";
    @VisibleForTesting
    static final String EXTRA_FLASH_NOTIFICATION_PREVIEW_TYPE = "com.android.internal.intent.extra.FLASH_NOTIFICATION_PREVIEW_TYPE";
    @VisibleForTesting
    static final int PREVIEW_TYPE_LONG = 1;
    @VisibleForTesting
    static final int PREVIEW_TYPE_SHORT = 0;
    @VisibleForTesting
    static final String SETTING_KEY_CAMERA_FLASH_NOTIFICATION = "camera_flash_notification";
    @VisibleForTesting
    static final String SETTING_KEY_SCREEN_FLASH_NOTIFICATION = "screen_flash_notification";
    @VisibleForTesting
    static final String SETTING_KEY_SCREEN_FLASH_NOTIFICATION_COLOR = "screen_flash_notification_color_global";
    public final AudioManager.AudioPlaybackCallback mAudioPlaybackCallback;
    public final Handler mCallbackHandler;
    public String mCameraId;
    public CameraManager mCameraManager;
    public final Context mContext;
    public FlashNotification mCurrentFlashNotification;
    public final DisplayManager mDisplayManager;
    public int mDisplayState;
    @VisibleForTesting
    final FlashBroadcastReceiver mFlashBroadcastReceiver;
    public final Handler mFlashNotificationHandler;
    @GuardedBy({"mFlashNotifications"})
    public final LinkedList<FlashNotification> mFlashNotifications;
    public boolean mIsAlarming;
    public boolean mIsCameraFlashNotificationEnabled;
    public boolean mIsCameraOpened;
    public boolean mIsScreenFlashNotificationEnabled;
    public boolean mIsTorchOn;
    public boolean mIsTorchTouched;
    public final Handler mMainHandler;
    public View mScreenFlashNotificationOverlayView;
    public volatile FlashNotificationThread mThread;
    @VisibleForTesting
    final CameraManager.AvailabilityCallback mTorchAvailabilityCallback;
    public final CameraManager.TorchCallback mTorchCallback;
    public final PowerManager.WakeLock mWakeLock;

    /* renamed from: com.android.server.accessibility.FlashNotificationsController$3 */
    /* loaded from: classes.dex */
    public class C02763 extends AudioManager.AudioPlaybackCallback {
        public C02763() {
        }

        @Override // android.media.AudioManager.AudioPlaybackCallback
        public void onPlaybackConfigChanged(List<AudioPlaybackConfiguration> list) {
            boolean anyMatch = list != null ? list.stream().anyMatch(new Predicate() { // from class: com.android.server.accessibility.FlashNotificationsController$3$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onPlaybackConfigChanged$0;
                    lambda$onPlaybackConfigChanged$0 = FlashNotificationsController.C02763.lambda$onPlaybackConfigChanged$0((AudioPlaybackConfiguration) obj);
                    return lambda$onPlaybackConfigChanged$0;
                }
            }) : false;
            if (FlashNotificationsController.this.mIsAlarming != anyMatch) {
                Log.d("FlashNotifController", "alarm state changed: " + anyMatch);
                if (anyMatch) {
                    FlashNotificationsController.this.startFlashNotificationSequenceForAlarm();
                } else {
                    FlashNotificationsController.this.stopFlashNotificationSequenceForAlarm();
                }
                FlashNotificationsController.this.mIsAlarming = anyMatch;
            }
        }

        public static /* synthetic */ boolean lambda$onPlaybackConfigChanged$0(AudioPlaybackConfiguration audioPlaybackConfiguration) {
            return audioPlaybackConfiguration.isActive() && audioPlaybackConfiguration.getAudioAttributes().getUsage() == 4;
        }
    }

    public FlashNotificationsController(Context context) {
        this(context, getStartedHandler("FlashNotificationThread"), getStartedHandler("FlashNotifController"));
    }

    @VisibleForTesting
    public FlashNotificationsController(Context context, Handler handler, Handler handler2) {
        this.mFlashNotifications = new LinkedList<>();
        this.mIsTorchTouched = false;
        this.mIsTorchOn = false;
        this.mIsCameraFlashNotificationEnabled = false;
        this.mIsScreenFlashNotificationEnabled = false;
        this.mIsAlarming = false;
        this.mDisplayState = 1;
        this.mIsCameraOpened = false;
        this.mCameraId = null;
        this.mTorchCallback = new CameraManager.TorchCallback() { // from class: com.android.server.accessibility.FlashNotificationsController.1
            @Override // android.hardware.camera2.CameraManager.TorchCallback
            public void onTorchModeChanged(String str, boolean z) {
                if (FlashNotificationsController.this.mCameraId == null || !FlashNotificationsController.this.mCameraId.equals(str)) {
                    return;
                }
                FlashNotificationsController.this.mIsTorchOn = z;
                Log.d("FlashNotifController", "onTorchModeChanged, set mIsTorchOn=" + z);
            }
        };
        this.mTorchAvailabilityCallback = new CameraManager.AvailabilityCallback() { // from class: com.android.server.accessibility.FlashNotificationsController.2
            public void onCameraOpened(String str, String str2) {
                if (FlashNotificationsController.this.mCameraId == null || !FlashNotificationsController.this.mCameraId.equals(str)) {
                    return;
                }
                FlashNotificationsController.this.mIsCameraOpened = true;
            }

            public void onCameraClosed(String str) {
                if (FlashNotificationsController.this.mCameraId == null || !FlashNotificationsController.this.mCameraId.equals(str)) {
                    return;
                }
                FlashNotificationsController.this.mIsCameraOpened = false;
            }
        };
        this.mAudioPlaybackCallback = new C02763();
        this.mContext = context;
        Handler handler3 = new Handler(context.getMainLooper());
        this.mMainHandler = handler3;
        this.mFlashNotificationHandler = handler;
        this.mCallbackHandler = handler2;
        new FlashContentObserver(handler3).register(context.getContentResolver());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BOOT_COMPLETED");
        intentFilter.addAction(ACTION_FLASH_NOTIFICATION_START_PREVIEW);
        intentFilter.addAction(ACTION_FLASH_NOTIFICATION_STOP_PREVIEW);
        FlashBroadcastReceiver flashBroadcastReceiver = new FlashBroadcastReceiver();
        this.mFlashBroadcastReceiver = flashBroadcastReceiver;
        context.registerReceiver(flashBroadcastReceiver, intentFilter, 4);
        this.mWakeLock = ((PowerManager) context.getSystemService(PowerManager.class)).newWakeLock(1, "a11y:FlashNotificationsController");
        DisplayManager displayManager = (DisplayManager) context.getSystemService(DisplayManager.class);
        this.mDisplayManager = displayManager;
        DisplayManager.DisplayListener displayListener = new DisplayManager.DisplayListener() { // from class: com.android.server.accessibility.FlashNotificationsController.4
            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayAdded(int i) {
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayRemoved(int i) {
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayChanged(int i) {
                Display display;
                if (FlashNotificationsController.this.mDisplayManager == null || (display = FlashNotificationsController.this.mDisplayManager.getDisplay(i)) == null) {
                    return;
                }
                FlashNotificationsController.this.mDisplayState = display.getState();
            }
        };
        if (displayManager != null) {
            displayManager.registerDisplayListener(displayListener, null);
        }
    }

    public static Handler getStartedHandler(String str) {
        HandlerThread handlerThread = new HandlerThread(str);
        handlerThread.start();
        return handlerThread.getThreadHandler();
    }

    public boolean startFlashNotificationSequence(final String str, int i, IBinder iBinder) {
        FlashNotification flashNotification = new FlashNotification(str, 2, getScreenFlashColorPreference(i), iBinder, new IBinder.DeathRecipient() { // from class: com.android.server.accessibility.FlashNotificationsController$$ExternalSyntheticLambda0
            @Override // android.os.IBinder.DeathRecipient
            public final void binderDied() {
                FlashNotificationsController.this.lambda$startFlashNotificationSequence$0(str);
            }
        });
        if (flashNotification.tryLinkToDeath()) {
            requestStartFlashNotification(flashNotification);
            return true;
        }
        return false;
    }

    public boolean stopFlashNotificationSequence(String str) {
        lambda$startFlashNotificationSequence$0(str);
        return true;
    }

    public boolean startFlashNotificationEvent(String str, int i, String str2) {
        requestStartFlashNotification(new FlashNotification(str, 1, getScreenFlashColorPreference(i, str2)));
        return true;
    }

    public final void startFlashNotificationShortPreview() {
        requestStartFlashNotification(new FlashNotification("preview", 1, getScreenFlashColorPreference(4)));
    }

    public final void startFlashNotificationLongPreview(int i) {
        requestStartFlashNotification(new FlashNotification("preview", 3, i));
    }

    public final void stopFlashNotificationLongPreview() {
        lambda$startFlashNotificationSequence$0("preview");
    }

    public final void startFlashNotificationSequenceForAlarm() {
        requestStartFlashNotification(new FlashNotification("alarm", 2, getScreenFlashColorPreference(2)));
    }

    public final void stopFlashNotificationSequenceForAlarm() {
        lambda$startFlashNotificationSequence$0("alarm");
    }

    public final void requestStartFlashNotification(FlashNotification flashNotification) {
        Log.d("FlashNotifController", "requestStartFlashNotification");
        boolean isEnabled = FeatureFlagUtils.isEnabled(this.mContext, "settings_flash_notifications");
        boolean z = false;
        this.mIsCameraFlashNotificationEnabled = isEnabled && Settings.System.getIntForUser(this.mContext.getContentResolver(), SETTING_KEY_CAMERA_FLASH_NOTIFICATION, 0, -2) != 0;
        if (isEnabled && Settings.System.getIntForUser(this.mContext.getContentResolver(), SETTING_KEY_SCREEN_FLASH_NOTIFICATION, 0, -2) != 0) {
            z = true;
        }
        this.mIsScreenFlashNotificationEnabled = z;
        if (flashNotification.mType == 1 && this.mIsScreenFlashNotificationEnabled) {
            this.mMainHandler.sendMessageDelayed(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.FlashNotificationsController$$ExternalSyntheticLambda1
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((FlashNotificationsController) obj).startFlashNotification((FlashNotificationsController.FlashNotification) obj2);
                }
            }, this, flashNotification), 300L);
            Log.i("FlashNotifController", "give some delay for flash notification");
            return;
        }
        startFlashNotification(flashNotification);
    }

    /* renamed from: stopFlashNotification */
    public final void lambda$startFlashNotificationSequence$0(String str) {
        Log.i("FlashNotifController", "stopFlashNotification: tag=" + str);
        synchronized (this.mFlashNotifications) {
            FlashNotification removeFlashNotificationLocked = removeFlashNotificationLocked(str);
            FlashNotification flashNotification = this.mCurrentFlashNotification;
            if (flashNotification != null && removeFlashNotificationLocked == flashNotification) {
                stopFlashNotificationLocked();
                startNextFlashNotificationLocked();
            }
        }
    }

    public final void prepareForCameraFlashNotification() {
        CameraManager cameraManager = (CameraManager) this.mContext.getSystemService(CameraManager.class);
        this.mCameraManager = cameraManager;
        if (cameraManager != null) {
            try {
                this.mCameraId = getCameraId();
            } catch (CameraAccessException e) {
                Log.e("FlashNotifController", "CameraAccessException", e);
            }
            this.mCameraManager.registerTorchCallback(this.mTorchCallback, (Handler) null);
        }
    }

    public final String getCameraId() throws CameraAccessException {
        String[] cameraIdList;
        for (String str : this.mCameraManager.getCameraIdList()) {
            CameraCharacteristics cameraCharacteristics = this.mCameraManager.getCameraCharacteristics(str);
            Boolean bool = (Boolean) cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            Integer num = (Integer) cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
            if (bool != null && num != null && bool.booleanValue() && num.intValue() == 1) {
                Log.d("FlashNotifController", "Found valid camera, cameraId=" + str);
                return str;
            }
        }
        return null;
    }

    public final void showScreenNotificationOverlayView(int i) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.FlashNotificationsController$$ExternalSyntheticLambda4
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((FlashNotificationsController) obj).showScreenNotificationOverlayViewMainThread(((Integer) obj2).intValue());
            }
        }, this, Integer.valueOf(i)));
    }

    public final void hideScreenNotificationOverlayView() {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.server.accessibility.FlashNotificationsController$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((FlashNotificationsController) obj).fadeOutScreenNotificationOverlayViewMainThread();
            }
        }, this));
        this.mMainHandler.sendMessageDelayed(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.server.accessibility.FlashNotificationsController$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((FlashNotificationsController) obj).hideScreenNotificationOverlayViewMainThread();
            }
        }, this), 210L);
    }

    public final void showScreenNotificationOverlayViewMainThread(int i) {
        Log.d("FlashNotifController", "showScreenNotificationOverlayViewMainThread");
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, 2015, FrameworkStatsLog.f85x84af90cd, -3);
        layoutParams.privateFlags |= 16;
        layoutParams.layoutInDisplayCutoutMode = 1;
        layoutParams.inputFeatures |= 1;
        if (this.mScreenFlashNotificationOverlayView == null) {
            this.mScreenFlashNotificationOverlayView = getScreenNotificationOverlayView(i);
            ((WindowManager) this.mContext.getSystemService(WindowManager.class)).addView(this.mScreenFlashNotificationOverlayView, layoutParams);
            fadeScreenNotificationOverlayViewMainThread(this.mScreenFlashNotificationOverlayView, true);
        }
    }

    public final void fadeOutScreenNotificationOverlayViewMainThread() {
        Log.d("FlashNotifController", "fadeOutScreenNotificationOverlayViewMainThread");
        View view = this.mScreenFlashNotificationOverlayView;
        if (view != null) {
            fadeScreenNotificationOverlayViewMainThread(view, false);
        }
    }

    public final void fadeScreenNotificationOverlayViewMainThread(View view, boolean z) {
        float[] fArr = new float[2];
        fArr[0] = z ? 0.0f : 1.0f;
        fArr[1] = z ? 1.0f : 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, "alpha", fArr);
        ofFloat.setInterpolator(new AccelerateInterpolator());
        ofFloat.setAutoCancel(true);
        ofFloat.setDuration(200L);
        ofFloat.start();
    }

    public final void hideScreenNotificationOverlayViewMainThread() {
        Log.d("FlashNotifController", "hideScreenNotificationOverlayViewMainThread");
        View view = this.mScreenFlashNotificationOverlayView;
        if (view != null) {
            view.setVisibility(8);
            ((WindowManager) this.mContext.getSystemService(WindowManager.class)).removeView(this.mScreenFlashNotificationOverlayView);
            this.mScreenFlashNotificationOverlayView = null;
        }
    }

    public final View getScreenNotificationOverlayView(int i) {
        FrameLayout frameLayout = new FrameLayout(this.mContext);
        frameLayout.setBackgroundColor(i);
        frameLayout.setAlpha(0.0f);
        return frameLayout;
    }

    public final int getScreenFlashColorPreference(int i, String str) {
        return getScreenFlashColorPreference();
    }

    public final int getScreenFlashColorPreference(int i) {
        return getScreenFlashColorPreference();
    }

    public final int getScreenFlashColorPreference() {
        return Settings.System.getIntForUser(this.mContext.getContentResolver(), SETTING_KEY_SCREEN_FLASH_NOTIFICATION_COLOR, 1728052992, -2);
    }

    public final void startFlashNotification(FlashNotification flashNotification) {
        int i = flashNotification.mType;
        String str = flashNotification.mTag;
        Log.i("FlashNotifController", "startFlashNotification: type=" + i + ", tag=" + str);
        if (!this.mIsCameraFlashNotificationEnabled && !this.mIsScreenFlashNotificationEnabled && !flashNotification.mForceStartScreenFlash) {
            Log.d("FlashNotifController", "Flash notification is disabled");
        } else if (this.mIsCameraOpened) {
            Log.d("FlashNotifController", "Since camera for torch is opened, block notification.");
        } else {
            if (this.mIsCameraFlashNotificationEnabled && this.mCameraId == null) {
                prepareForCameraFlashNotification();
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (this.mFlashNotifications) {
                    if (i == 1 || i == 3) {
                        if (this.mCurrentFlashNotification != null) {
                            Log.i("FlashNotifController", "Default type of flash notification can not work because previous flash notification is working");
                        } else {
                            startFlashNotificationLocked(flashNotification);
                        }
                    } else if (i == 2) {
                        if (this.mCurrentFlashNotification != null) {
                            removeFlashNotificationLocked(str);
                            stopFlashNotificationLocked();
                        }
                        this.mFlashNotifications.addFirst(flashNotification);
                        startNextFlashNotificationLocked();
                    } else {
                        Log.e("FlashNotifController", "Unavailable flash notification type");
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    @GuardedBy({"mFlashNotifications"})
    public final FlashNotification removeFlashNotificationLocked(String str) {
        ListIterator<FlashNotification> listIterator = this.mFlashNotifications.listIterator(0);
        while (listIterator.hasNext()) {
            FlashNotification next = listIterator.next();
            if (next != null && next.mTag.equals(str)) {
                listIterator.remove();
                next.tryUnlinkToDeath();
                Log.i("FlashNotifController", "removeFlashNotificationLocked: tag=" + next.mTag);
                return next;
            }
        }
        FlashNotification flashNotification = this.mCurrentFlashNotification;
        if (flashNotification == null || !flashNotification.mTag.equals(str)) {
            return null;
        }
        this.mCurrentFlashNotification.tryUnlinkToDeath();
        return this.mCurrentFlashNotification;
    }

    @GuardedBy({"mFlashNotifications"})
    public final void stopFlashNotificationLocked() {
        if (this.mThread != null) {
            Log.i("FlashNotifController", "stopFlashNotificationLocked: tag=" + this.mThread.mFlashNotification.mTag);
            this.mThread.cancel();
            this.mThread = null;
        }
        doCameraFlashNotificationOff();
        doScreenFlashNotificationOff();
    }

    @GuardedBy({"mFlashNotifications"})
    public final void startNextFlashNotificationLocked() {
        Log.i("FlashNotifController", "startNextFlashNotificationLocked");
        if (this.mFlashNotifications.size() <= 0) {
            this.mCurrentFlashNotification = null;
        } else {
            startFlashNotificationLocked(this.mFlashNotifications.getFirst());
        }
    }

    @GuardedBy({"mFlashNotifications"})
    public final void startFlashNotificationLocked(FlashNotification flashNotification) {
        Log.i("FlashNotifController", "startFlashNotificationLocked: type=" + flashNotification.mType + ", tag=" + flashNotification.mTag);
        this.mCurrentFlashNotification = flashNotification;
        this.mThread = new FlashNotificationThread(flashNotification);
        this.mFlashNotificationHandler.post(this.mThread);
    }

    public final boolean isDozeMode() {
        int i = this.mDisplayState;
        return i == 3 || i == 4;
    }

    public final void doCameraFlashNotificationOn() {
        if (this.mIsCameraFlashNotificationEnabled && !this.mIsTorchOn) {
            doCameraFlashNotification(true);
        }
        Log.i("FlashNotifController", "doCameraFlashNotificationOn: isCameraFlashNotificationEnabled=" + this.mIsCameraFlashNotificationEnabled + ", isTorchOn=" + this.mIsTorchOn + ", isTorchTouched=" + this.mIsTorchTouched);
    }

    public final void doCameraFlashNotificationOff() {
        if (this.mIsTorchTouched) {
            doCameraFlashNotification(false);
        }
        Log.i("FlashNotifController", "doCameraFlashNotificationOff: isCameraFlashNotificationEnabled=" + this.mIsCameraFlashNotificationEnabled + ", isTorchOn=" + this.mIsTorchOn + ", isTorchTouched=" + this.mIsTorchTouched);
    }

    public final void doScreenFlashNotificationOn(int i, boolean z) {
        boolean isDozeMode = isDozeMode();
        if ((this.mIsScreenFlashNotificationEnabled || z) && !isDozeMode) {
            showScreenNotificationOverlayView(i);
        }
        Log.i("FlashNotifController", "doScreenFlashNotificationOn: isScreenFlashNotificationEnabled=" + this.mIsScreenFlashNotificationEnabled + ", isDozeMode=" + isDozeMode + ", color=" + Integer.toHexString(i));
    }

    public final void doScreenFlashNotificationOff() {
        hideScreenNotificationOverlayView();
        Log.i("FlashNotifController", "doScreenFlashNotificationOff: isScreenFlashNotificationEnabled=" + this.mIsScreenFlashNotificationEnabled);
    }

    public final void doCameraFlashNotification(boolean z) {
        String str;
        Log.d("FlashNotifController", "doCameraFlashNotification: " + z + " mCameraId : " + this.mCameraId);
        CameraManager cameraManager = this.mCameraManager;
        if (cameraManager != null && (str = this.mCameraId) != null) {
            try {
                cameraManager.setTorchMode(str, z);
                this.mIsTorchTouched = z;
                return;
            } catch (CameraAccessException e) {
                Log.e("FlashNotifController", "Failed to setTorchMode: " + e);
                return;
            }
        }
        Log.e("FlashNotifController", "Can not use camera flash notification, please check CameraManager!");
    }

    /* loaded from: classes.dex */
    public static class FlashNotification {
        public final int mColor;
        public final IBinder.DeathRecipient mDeathRecipient;
        public final boolean mForceStartScreenFlash;
        public final int mOffDuration;
        public final int mOnDuration;
        public int mRepeat;
        public final String mTag;
        public final IBinder mToken;
        public final int mType;

        public FlashNotification(String str, int i, int i2) {
            this(str, i, i2, null, null);
        }

        public FlashNotification(String str, int i, int i2, IBinder iBinder, IBinder.DeathRecipient deathRecipient) {
            this.mType = i;
            this.mTag = str;
            this.mColor = i2;
            this.mToken = iBinder;
            this.mDeathRecipient = deathRecipient;
            if (i == 2) {
                this.mOnDuration = 700;
                this.mOffDuration = 700;
                this.mRepeat = 0;
                this.mForceStartScreenFlash = false;
            } else if (i == 3) {
                this.mOnDuration = 5000;
                this.mOffDuration = 1000;
                this.mRepeat = 1;
                this.mForceStartScreenFlash = true;
            } else {
                this.mOnDuration = 350;
                this.mOffDuration = 250;
                this.mRepeat = 2;
                this.mForceStartScreenFlash = false;
            }
        }

        public boolean tryLinkToDeath() {
            IBinder.DeathRecipient deathRecipient;
            IBinder iBinder = this.mToken;
            if (iBinder != null && (deathRecipient = this.mDeathRecipient) != null) {
                try {
                    iBinder.linkToDeath(deathRecipient, 0);
                    return true;
                } catch (RemoteException e) {
                    Log.e("FlashNotifController", "RemoteException", e);
                }
            }
            return false;
        }

        public boolean tryUnlinkToDeath() {
            IBinder.DeathRecipient deathRecipient;
            IBinder iBinder = this.mToken;
            if (iBinder != null && (deathRecipient = this.mDeathRecipient) != null) {
                try {
                    iBinder.unlinkToDeath(deathRecipient, 0);
                    return true;
                } catch (Exception unused) {
                }
            }
            return false;
        }
    }

    /* loaded from: classes.dex */
    public class FlashNotificationThread extends Thread {
        public int mColor;
        public final FlashNotification mFlashNotification;
        public boolean mForceStop;
        public boolean mShouldDoCameraFlash;
        public boolean mShouldDoScreenFlash;

        public FlashNotificationThread(FlashNotification flashNotification) {
            this.mColor = 0;
            this.mShouldDoScreenFlash = false;
            this.mShouldDoCameraFlash = false;
            this.mFlashNotification = flashNotification;
            this.mForceStop = false;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Log.d("FlashNotifController", "run started: " + this.mFlashNotification.mTag);
            Process.setThreadPriority(-8);
            int i = this.mFlashNotification.mColor;
            this.mColor = i;
            this.mShouldDoScreenFlash = Color.alpha(i) != 0 || this.mFlashNotification.mForceStartScreenFlash;
            this.mShouldDoCameraFlash = this.mFlashNotification.mType != 3;
            synchronized (this) {
                FlashNotificationsController.this.mWakeLock.acquire(BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS);
                startFlashNotification();
                FlashNotificationsController.this.doScreenFlashNotificationOff();
                FlashNotificationsController.this.doCameraFlashNotificationOff();
                try {
                    FlashNotificationsController.this.mWakeLock.release();
                } catch (RuntimeException unused) {
                    Log.e("FlashNotifController", "Error while releasing FlashNotificationsController wakelock (already released by the system?)");
                }
            }
            synchronized (FlashNotificationsController.this.mFlashNotifications) {
                if (FlashNotificationsController.this.mThread == this) {
                    FlashNotificationsController.this.mThread = null;
                }
                if (!this.mForceStop) {
                    this.mFlashNotification.tryUnlinkToDeath();
                    FlashNotificationsController.this.mCurrentFlashNotification = null;
                }
            }
            Log.d("FlashNotifController", "run finished: " + this.mFlashNotification.mTag);
        }

        public final void startFlashNotification() {
            synchronized (this) {
                while (!this.mForceStop) {
                    if (this.mFlashNotification.mType != 2 && this.mFlashNotification.mRepeat >= 0) {
                        FlashNotification flashNotification = this.mFlashNotification;
                        int i = flashNotification.mRepeat;
                        flashNotification.mRepeat = i - 1;
                        if (i == 0) {
                            break;
                        }
                    }
                    if (this.mShouldDoScreenFlash) {
                        FlashNotificationsController.this.doScreenFlashNotificationOn(this.mColor, this.mFlashNotification.mForceStartScreenFlash);
                    }
                    if (this.mShouldDoCameraFlash) {
                        FlashNotificationsController.this.doCameraFlashNotificationOn();
                    }
                    delay(this.mFlashNotification.mOnDuration);
                    FlashNotificationsController.this.doScreenFlashNotificationOff();
                    FlashNotificationsController.this.doCameraFlashNotificationOff();
                    if (this.mForceStop) {
                        break;
                    }
                    delay(this.mFlashNotification.mOffDuration);
                }
            }
        }

        public void cancel() {
            Log.d("FlashNotifController", "run canceled: " + this.mFlashNotification.mTag);
            synchronized (this) {
                FlashNotificationsController.this.mThread.mForceStop = true;
                FlashNotificationsController.this.mThread.notify();
            }
        }

        public final void delay(long j) {
            if (j > 0) {
                long uptimeMillis = SystemClock.uptimeMillis() + j;
                do {
                    try {
                        wait(j);
                    } catch (InterruptedException unused) {
                    }
                    if (this.mForceStop) {
                        return;
                    }
                    j = uptimeMillis - SystemClock.uptimeMillis();
                } while (j > 0);
            }
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public class FlashBroadcastReceiver extends BroadcastReceiver {
        public FlashBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                if (UserHandle.myUserId() != ActivityManager.getCurrentUser()) {
                    return;
                }
                FlashNotificationsController flashNotificationsController = FlashNotificationsController.this;
                flashNotificationsController.mIsCameraFlashNotificationEnabled = Settings.System.getIntForUser(flashNotificationsController.mContext.getContentResolver(), FlashNotificationsController.SETTING_KEY_CAMERA_FLASH_NOTIFICATION, 0, -2) != 0;
                if (FlashNotificationsController.this.mIsCameraFlashNotificationEnabled) {
                    FlashNotificationsController.this.prepareForCameraFlashNotification();
                } else if (FlashNotificationsController.this.mCameraManager != null) {
                    FlashNotificationsController.this.mCameraManager.unregisterTorchCallback(FlashNotificationsController.this.mTorchCallback);
                }
                AudioManager audioManager = (AudioManager) FlashNotificationsController.this.mContext.getSystemService(AudioManager.class);
                if (audioManager != null) {
                    audioManager.registerAudioPlaybackCallback(FlashNotificationsController.this.mAudioPlaybackCallback, FlashNotificationsController.this.mCallbackHandler);
                }
                FlashNotificationsController flashNotificationsController2 = FlashNotificationsController.this;
                flashNotificationsController2.mCameraManager = (CameraManager) flashNotificationsController2.mContext.getSystemService(CameraManager.class);
                CameraManager cameraManager = FlashNotificationsController.this.mCameraManager;
                FlashNotificationsController flashNotificationsController3 = FlashNotificationsController.this;
                cameraManager.registerAvailabilityCallback(flashNotificationsController3.mTorchAvailabilityCallback, flashNotificationsController3.mCallbackHandler);
            } else if (FlashNotificationsController.ACTION_FLASH_NOTIFICATION_START_PREVIEW.equals(intent.getAction())) {
                Log.i("FlashNotifController", "ACTION_FLASH_NOTIFICATION_START_PREVIEW");
                int intExtra = intent.getIntExtra(FlashNotificationsController.EXTRA_FLASH_NOTIFICATION_PREVIEW_COLOR, 0);
                int intExtra2 = intent.getIntExtra(FlashNotificationsController.EXTRA_FLASH_NOTIFICATION_PREVIEW_TYPE, 0);
                if (intExtra2 == 1) {
                    FlashNotificationsController.this.startFlashNotificationLongPreview(intExtra);
                } else if (intExtra2 == 0) {
                    FlashNotificationsController.this.startFlashNotificationShortPreview();
                }
            } else if (FlashNotificationsController.ACTION_FLASH_NOTIFICATION_STOP_PREVIEW.equals(intent.getAction())) {
                Log.i("FlashNotifController", "ACTION_FLASH_NOTIFICATION_STOP_PREVIEW");
                FlashNotificationsController.this.stopFlashNotificationLongPreview();
            }
        }
    }

    /* loaded from: classes.dex */
    public final class FlashContentObserver extends ContentObserver {
        public final Uri mCameraFlashNotificationUri;
        public final Uri mScreenFlashNotificationUri;

        public FlashContentObserver(Handler handler) {
            super(handler);
            this.mCameraFlashNotificationUri = Settings.System.getUriFor(FlashNotificationsController.SETTING_KEY_CAMERA_FLASH_NOTIFICATION);
            this.mScreenFlashNotificationUri = Settings.System.getUriFor(FlashNotificationsController.SETTING_KEY_SCREEN_FLASH_NOTIFICATION);
        }

        public void register(ContentResolver contentResolver) {
            contentResolver.registerContentObserver(this.mCameraFlashNotificationUri, false, this, -1);
            contentResolver.registerContentObserver(this.mScreenFlashNotificationUri, false, this, -1);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            if (this.mCameraFlashNotificationUri.equals(uri)) {
                FlashNotificationsController flashNotificationsController = FlashNotificationsController.this;
                flashNotificationsController.mIsCameraFlashNotificationEnabled = Settings.System.getIntForUser(flashNotificationsController.mContext.getContentResolver(), FlashNotificationsController.SETTING_KEY_CAMERA_FLASH_NOTIFICATION, 0, -2) != 0;
                if (FlashNotificationsController.this.mIsCameraFlashNotificationEnabled) {
                    FlashNotificationsController.this.prepareForCameraFlashNotification();
                    return;
                }
                FlashNotificationsController.this.mIsTorchOn = false;
                if (FlashNotificationsController.this.mCameraManager != null) {
                    FlashNotificationsController.this.mCameraManager.unregisterTorchCallback(FlashNotificationsController.this.mTorchCallback);
                }
            } else if (this.mScreenFlashNotificationUri.equals(uri)) {
                FlashNotificationsController flashNotificationsController2 = FlashNotificationsController.this;
                flashNotificationsController2.mIsScreenFlashNotificationEnabled = Settings.System.getIntForUser(flashNotificationsController2.mContext.getContentResolver(), FlashNotificationsController.SETTING_KEY_SCREEN_FLASH_NOTIFICATION, 0, -2) != 0;
            }
        }
    }
}
