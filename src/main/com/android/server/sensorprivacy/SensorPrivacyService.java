package com.android.server.sensorprivacy;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityOptions;
import android.app.ActivityTaskManager;
import android.app.AppOpsManager;
import android.app.AppOpsManagerInternal;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.graphics.drawable.Icon;
import android.hardware.ISensorPrivacyListener;
import android.hardware.ISensorPrivacyManager;
import android.hardware.SensorPrivacyManager;
import android.hardware.SensorPrivacyManagerInternal;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.ShellCommand;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.service.voice.VoiceInteractionManagerInternal;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.telephony.emergency.EmergencyNumber;
import android.text.Html;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.util.Pair;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.FunctionalUtils;
import com.android.internal.util.dump.DualDumpOutputStream;
import com.android.internal.util.function.HexConsumer;
import com.android.internal.util.function.QuintConsumer;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.sensorprivacy.SensorPrivacyService;
import com.android.server.sensorprivacy.SensorPrivacyStateController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiConsumer;
/* loaded from: classes2.dex */
public final class SensorPrivacyService extends SystemService {
    public static final String ACTION_DISABLE_TOGGLE_SENSOR_PRIVACY = SensorPrivacyService.class.getName() + ".action.disable_sensor_privacy";
    public static final String TAG = "SensorPrivacyService";
    public final ActivityManager mActivityManager;
    public final ActivityManagerInternal mActivityManagerInternal;
    public final ActivityTaskManager mActivityTaskManager;
    public final AppOpsManager mAppOpsManager;
    public final AppOpsManagerInternal mAppOpsManagerInternal;
    public final IBinder mAppOpsRestrictionToken;
    public CallStateHelper mCallStateHelper;
    public CameraPrivacyLightController mCameraPrivacyLightController;
    public final Context mContext;
    public int mCurrentUser;
    public KeyguardManager mKeyguardManager;
    public final PackageManagerInternal mPackageManagerInternal;
    public SensorPrivacyManagerInternalImpl mSensorPrivacyManagerInternal;
    public final SensorPrivacyServiceImpl mSensorPrivacyServiceImpl;
    public final TelephonyManager mTelephonyManager;
    public final UserManagerInternal mUserManagerInternal;

    public SensorPrivacyService(Context context) {
        super(context);
        this.mAppOpsRestrictionToken = new Binder();
        this.mCurrentUser = -10000;
        this.mContext = context;
        this.mAppOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        this.mAppOpsManagerInternal = (AppOpsManagerInternal) getLocalService(AppOpsManagerInternal.class);
        this.mUserManagerInternal = (UserManagerInternal) getLocalService(UserManagerInternal.class);
        this.mActivityManager = (ActivityManager) context.getSystemService(ActivityManager.class);
        this.mActivityManagerInternal = (ActivityManagerInternal) getLocalService(ActivityManagerInternal.class);
        this.mActivityTaskManager = (ActivityTaskManager) context.getSystemService(ActivityTaskManager.class);
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        this.mPackageManagerInternal = (PackageManagerInternal) getLocalService(PackageManagerInternal.class);
        this.mSensorPrivacyServiceImpl = new SensorPrivacyServiceImpl();
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("sensor_privacy", this.mSensorPrivacyServiceImpl);
        SensorPrivacyManagerInternalImpl sensorPrivacyManagerInternalImpl = new SensorPrivacyManagerInternalImpl();
        this.mSensorPrivacyManagerInternal = sensorPrivacyManagerInternalImpl;
        publishLocalService(SensorPrivacyManagerInternal.class, sensorPrivacyManagerInternalImpl);
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 500) {
            this.mKeyguardManager = (KeyguardManager) this.mContext.getSystemService(KeyguardManager.class);
            this.mCallStateHelper = new CallStateHelper();
            this.mSensorPrivacyServiceImpl.registerSettingsObserver();
        } else if (i == 550) {
            this.mCameraPrivacyLightController = new CameraPrivacyLightController(this.mContext);
        }
    }

    @Override // com.android.server.SystemService
    public void onUserStarting(SystemService.TargetUser targetUser) {
        if (this.mCurrentUser == -10000) {
            this.mCurrentUser = targetUser.getUserIdentifier();
            this.mSensorPrivacyServiceImpl.userSwitching(-10000, targetUser.getUserIdentifier());
        }
    }

    @Override // com.android.server.SystemService
    public void onUserSwitching(SystemService.TargetUser targetUser, SystemService.TargetUser targetUser2) {
        this.mCurrentUser = targetUser2.getUserIdentifier();
        this.mSensorPrivacyServiceImpl.userSwitching(targetUser.getUserIdentifier(), targetUser2.getUserIdentifier());
    }

    /* loaded from: classes2.dex */
    public class SensorPrivacyServiceImpl extends ISensorPrivacyManager.Stub implements AppOpsManager.OnOpNotedInternalListener, AppOpsManager.OnOpStartedListener, IBinder.DeathRecipient, UserManagerInternal.UserRestrictionsListener {
        public final SensorPrivacyHandler mHandler;
        public SensorPrivacyStateController mSensorPrivacyStateController;
        public final Object mLock = new Object();
        @GuardedBy({"mLock"})
        public ArrayMap<Pair<Integer, UserHandle>, ArrayList<IBinder>> mSuppressReminders = new ArrayMap<>();
        public final ArrayMap<SensorUseReminderDialogInfo, ArraySet<Integer>> mQueuedSensorUseReminderDialogs = new ArrayMap<>();

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
        }

        /* loaded from: classes2.dex */
        public class SensorUseReminderDialogInfo {
            public String mPackageName;
            public int mTaskId;
            public UserHandle mUser;

            public SensorUseReminderDialogInfo(int i, UserHandle userHandle, String str) {
                this.mTaskId = i;
                this.mUser = userHandle;
                this.mPackageName = str;
            }

            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }
                if (obj == null || !(obj instanceof SensorUseReminderDialogInfo)) {
                    return false;
                }
                SensorUseReminderDialogInfo sensorUseReminderDialogInfo = (SensorUseReminderDialogInfo) obj;
                return this.mTaskId == sensorUseReminderDialogInfo.mTaskId && Objects.equals(this.mUser, sensorUseReminderDialogInfo.mUser) && Objects.equals(this.mPackageName, sensorUseReminderDialogInfo.mPackageName);
            }

            public int hashCode() {
                return Objects.hash(Integer.valueOf(this.mTaskId), this.mUser, this.mPackageName);
            }
        }

        public SensorPrivacyServiceImpl() {
            final SensorPrivacyHandler sensorPrivacyHandler = new SensorPrivacyHandler(FgThread.get().getLooper(), SensorPrivacyService.this.mContext);
            this.mHandler = sensorPrivacyHandler;
            this.mSensorPrivacyStateController = SensorPrivacyStateController.getInstance();
            int[] iArr = {27, 100, 26, 101, 121};
            SensorPrivacyService.this.mAppOpsManager.startWatchingNoted(iArr, this);
            SensorPrivacyService.this.mAppOpsManager.startWatchingStarted(iArr, this);
            SensorPrivacyService.this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.sensorprivacy.SensorPrivacyService.SensorPrivacyServiceImpl.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    SensorPrivacyServiceImpl.this.setToggleSensorPrivacy(((UserHandle) intent.getParcelableExtra("android.intent.extra.USER", UserHandle.class)).getIdentifier(), 5, intent.getIntExtra(SensorPrivacyManager.EXTRA_SENSOR, 0), false);
                }
            }, new IntentFilter(SensorPrivacyService.ACTION_DISABLE_TOGGLE_SENSOR_PRIVACY), "android.permission.MANAGE_SENSOR_PRIVACY", null, 2);
            SensorPrivacyService.this.mContext.registerReceiver(new C15942(SensorPrivacyService.this), new IntentFilter("android.intent.action.ACTION_SHUTDOWN"));
            SensorPrivacyService.this.mUserManagerInternal.addUserRestrictionsListener(this);
            SensorPrivacyStateController sensorPrivacyStateController = this.mSensorPrivacyStateController;
            Objects.requireNonNull(sensorPrivacyHandler);
            sensorPrivacyStateController.setAllSensorPrivacyListener(sensorPrivacyHandler, new SensorPrivacyStateController.AllSensorPrivacyListener() { // from class: com.android.server.sensorprivacy.SensorPrivacyService$SensorPrivacyServiceImpl$$ExternalSyntheticLambda0
                @Override // com.android.server.sensorprivacy.SensorPrivacyStateController.AllSensorPrivacyListener
                public final void onAllSensorPrivacyChanged(boolean z) {
                    SensorPrivacyService.SensorPrivacyHandler.this.handleSensorPrivacyChanged(z);
                }
            });
            this.mSensorPrivacyStateController.setSensorPrivacyListener(sensorPrivacyHandler, new SensorPrivacyStateController.SensorPrivacyListener() { // from class: com.android.server.sensorprivacy.SensorPrivacyService$SensorPrivacyServiceImpl$$ExternalSyntheticLambda1
                @Override // com.android.server.sensorprivacy.SensorPrivacyStateController.SensorPrivacyListener
                public final void onSensorPrivacyChanged(int i, int i2, int i3, SensorState sensorState) {
                    SensorPrivacyService.SensorPrivacyServiceImpl.this.lambda$new$0(i, i2, i3, sensorState);
                }
            });
        }

        /* renamed from: com.android.server.sensorprivacy.SensorPrivacyService$SensorPrivacyServiceImpl$2 */
        /* loaded from: classes2.dex */
        public class C15942 extends BroadcastReceiver {
            public final /* synthetic */ SensorPrivacyService val$this$0;

            public C15942(SensorPrivacyService sensorPrivacyService) {
                this.val$this$0 = sensorPrivacyService;
            }

            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                SensorPrivacyServiceImpl.this.mSensorPrivacyStateController.forEachState(new SensorPrivacyStateController.SensorPrivacyStateConsumer() { // from class: com.android.server.sensorprivacy.SensorPrivacyService$SensorPrivacyServiceImpl$2$$ExternalSyntheticLambda0
                    @Override // com.android.server.sensorprivacy.SensorPrivacyStateController.SensorPrivacyStateConsumer
                    public final void accept(int i, int i2, int i3, SensorState sensorState) {
                        SensorPrivacyService.SensorPrivacyServiceImpl.C15942.this.lambda$onReceive$0(i, i2, i3, sensorState);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onReceive$0(int i, int i2, int i3, SensorState sensorState) {
                SensorPrivacyServiceImpl.this.logSensorPrivacyToggle(5, i3, sensorState.isEnabled(), sensorState.getLastChange(), true);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(int i, int i2, int i3, SensorState sensorState) {
            this.mHandler.handleSensorPrivacyChanged(i2, i, i3, sensorState.isEnabled());
        }

        @Override // com.android.server.p011pm.UserManagerInternal.UserRestrictionsListener
        public void onUserRestrictionsChanged(int i, Bundle bundle, Bundle bundle2) {
            if (!bundle2.getBoolean("disallow_camera_toggle") && bundle.getBoolean("disallow_camera_toggle")) {
                setToggleSensorPrivacyUnchecked(1, i, 5, 2, false);
            }
            if (bundle2.getBoolean("disallow_microphone_toggle") || !bundle.getBoolean("disallow_microphone_toggle")) {
                return;
            }
            setToggleSensorPrivacyUnchecked(1, i, 5, 1, false);
        }

        public void onOpStarted(int i, int i2, String str, String str2, int i3, int i4) {
            onOpNoted(i, i2, str, str2, i3, i4);
        }

        public void onOpNoted(int i, int i2, String str, String str2, int i3, int i4) {
            if ((i3 & 13) == 0) {
                return;
            }
            int i5 = 1;
            if (i4 == 1) {
                if (i != 27 && i != 100 && i != 121) {
                    if (i != 26 && i != 101) {
                        return;
                    }
                    i5 = 2;
                }
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    onSensorUseStarted(i2, str, i5);
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        public final void onSensorUseStarted(int i, String str, int i2) {
            UserHandle of = UserHandle.of(SensorPrivacyService.this.mCurrentUser);
            if (isCombinedToggleSensorPrivacyEnabled(i2) && i != 1000) {
                synchronized (this.mLock) {
                    if (this.mSuppressReminders.containsKey(new Pair(Integer.valueOf(i2), of))) {
                        Log.d(SensorPrivacyService.TAG, "Suppressed sensor privacy reminder for " + str + "/" + of);
                        return;
                    }
                    ArrayList arrayList = new ArrayList();
                    List tasks = SensorPrivacyService.this.mActivityTaskManager.getTasks(Integer.MAX_VALUE);
                    int size = tasks.size();
                    for (int i3 = 0; i3 < size; i3++) {
                        ActivityManager.RunningTaskInfo runningTaskInfo = (ActivityManager.RunningTaskInfo) tasks.get(i3);
                        if (runningTaskInfo.isVisible) {
                            if (runningTaskInfo.topActivity.getPackageName().equals(str)) {
                                if (runningTaskInfo.isFocused) {
                                    enqueueSensorUseReminderDialogAsync(runningTaskInfo.taskId, of, str, i2);
                                    return;
                                }
                                arrayList.add(runningTaskInfo);
                            } else if (runningTaskInfo.topActivity.flattenToString().equals(getSensorUseActivityName(new ArraySet<>(Arrays.asList(Integer.valueOf(i2))))) && runningTaskInfo.isFocused) {
                                enqueueSensorUseReminderDialogAsync(runningTaskInfo.taskId, of, str, i2);
                            }
                        }
                    }
                    if (arrayList.size() == 1) {
                        enqueueSensorUseReminderDialogAsync(((ActivityManager.RunningTaskInfo) arrayList.get(0)).taskId, of, str, i2);
                    } else if (arrayList.size() > 1) {
                        showSensorUseReminderNotification(of, str, i2);
                    } else {
                        List<ActivityManager.RunningServiceInfo> runningServices = SensorPrivacyService.this.mActivityManager.getRunningServices(Integer.MAX_VALUE);
                        int size2 = runningServices.size();
                        for (int i4 = 0; i4 < size2; i4++) {
                            ActivityManager.RunningServiceInfo runningServiceInfo = runningServices.get(i4);
                            if (runningServiceInfo.foreground && runningServiceInfo.service.getPackageName().equals(str)) {
                                showSensorUseReminderNotification(of, str, i2);
                                return;
                            }
                        }
                        String stringForUser = Settings.Secure.getStringForUser(SensorPrivacyService.this.mContext.getContentResolver(), "default_input_method", of.getIdentifier());
                        String packageName = stringForUser != null ? ComponentName.unflattenFromString(stringForUser).getPackageName() : null;
                        try {
                            int uidCapability = SensorPrivacyService.this.mActivityManagerInternal.getUidCapability(i);
                            if (i2 == 1) {
                                VoiceInteractionManagerInternal voiceInteractionManagerInternal = (VoiceInteractionManagerInternal) LocalServices.getService(VoiceInteractionManagerInternal.class);
                                if (voiceInteractionManagerInternal != null && voiceInteractionManagerInternal.hasActiveSession(str)) {
                                    enqueueSensorUseReminderDialogAsync(-1, of, str, i2);
                                    return;
                                } else if (TextUtils.equals(str, packageName) && (uidCapability & 4) != 0) {
                                    enqueueSensorUseReminderDialogAsync(-1, of, str, i2);
                                    return;
                                }
                            }
                            if (i2 == 2 && TextUtils.equals(str, packageName) && (uidCapability & 2) != 0) {
                                enqueueSensorUseReminderDialogAsync(-1, of, str, i2);
                                return;
                            }
                            Log.i(SensorPrivacyService.TAG, str + "/" + i + " started using sensor " + i2 + " but no activity or foreground service was running. The user will not be informed. System components should check if sensor privacy is enabled for the sensor before accessing it.");
                        } catch (IllegalArgumentException e) {
                            Log.w(SensorPrivacyService.TAG, e);
                        }
                    }
                }
            }
        }

        public final void enqueueSensorUseReminderDialogAsync(int i, UserHandle userHandle, String str, int i2) {
            this.mHandler.sendMessage(PooledLambda.obtainMessage(new QuintConsumer() { // from class: com.android.server.sensorprivacy.SensorPrivacyService$SensorPrivacyServiceImpl$$ExternalSyntheticLambda4
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                    ((SensorPrivacyService.SensorPrivacyServiceImpl) obj).enqueueSensorUseReminderDialog(((Integer) obj2).intValue(), (UserHandle) obj3, (String) obj4, ((Integer) obj5).intValue());
                }
            }, this, Integer.valueOf(i), userHandle, str, Integer.valueOf(i2)));
        }

        public final void enqueueSensorUseReminderDialog(int i, UserHandle userHandle, String str, int i2) {
            SensorUseReminderDialogInfo sensorUseReminderDialogInfo = new SensorUseReminderDialogInfo(i, userHandle, str);
            if (!this.mQueuedSensorUseReminderDialogs.containsKey(sensorUseReminderDialogInfo)) {
                ArraySet<Integer> arraySet = new ArraySet<>();
                if ((i2 == 1 && this.mSuppressReminders.containsKey(new Pair(2, userHandle))) || (i2 == 2 && this.mSuppressReminders.containsKey(new Pair(1, userHandle)))) {
                    arraySet.add(1);
                    arraySet.add(2);
                } else {
                    arraySet.add(Integer.valueOf(i2));
                }
                this.mQueuedSensorUseReminderDialogs.put(sensorUseReminderDialogInfo, arraySet);
                this.mHandler.sendMessageDelayed(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.sensorprivacy.SensorPrivacyService$SensorPrivacyServiceImpl$$ExternalSyntheticLambda7
                    @Override // java.util.function.BiConsumer
                    public final void accept(Object obj, Object obj2) {
                        ((SensorPrivacyService.SensorPrivacyServiceImpl) obj).showSensorUserReminderDialog((SensorPrivacyService.SensorPrivacyServiceImpl.SensorUseReminderDialogInfo) obj2);
                    }
                }, this, sensorUseReminderDialogInfo), 500L);
                return;
            }
            this.mQueuedSensorUseReminderDialogs.get(sensorUseReminderDialogInfo).add(Integer.valueOf(i2));
        }

        public final void showSensorUserReminderDialog(SensorUseReminderDialogInfo sensorUseReminderDialogInfo) {
            ArraySet<Integer> arraySet = this.mQueuedSensorUseReminderDialogs.get(sensorUseReminderDialogInfo);
            this.mQueuedSensorUseReminderDialogs.remove(sensorUseReminderDialogInfo);
            if (arraySet == null) {
                Log.e(SensorPrivacyService.TAG, "Unable to show sensor use dialog because sensor set is null. Was the dialog queue modified from outside the handler thread?");
                return;
            }
            Intent intent = new Intent();
            intent.setComponent(ComponentName.unflattenFromString(getSensorUseActivityName(arraySet)));
            ActivityOptions makeBasic = ActivityOptions.makeBasic();
            makeBasic.setLaunchTaskId(sensorUseReminderDialogInfo.mTaskId);
            makeBasic.setTaskOverlay(true, true);
            intent.addFlags(8650752);
            intent.putExtra("android.intent.extra.PACKAGE_NAME", sensorUseReminderDialogInfo.mPackageName);
            if (arraySet.size() == 1) {
                intent.putExtra(SensorPrivacyManager.EXTRA_SENSOR, arraySet.valueAt(0));
            } else if (arraySet.size() == 2) {
                intent.putExtra(SensorPrivacyManager.EXTRA_ALL_SENSORS, true);
            } else {
                String str = SensorPrivacyService.TAG;
                Log.e(str, "Attempted to show sensor use dialog for " + arraySet.size() + " sensors");
                return;
            }
            SensorPrivacyService.this.mContext.startActivityAsUser(intent, makeBasic.toBundle(), UserHandle.SYSTEM);
        }

        public final String getSensorUseActivityName(ArraySet<Integer> arraySet) {
            Iterator<Integer> it = arraySet.iterator();
            while (it.hasNext()) {
                if (isToggleSensorPrivacyEnabled(2, it.next().intValue())) {
                    return SensorPrivacyService.this.mContext.getResources().getString(17040004);
                }
            }
            return SensorPrivacyService.this.mContext.getResources().getString(17040003);
        }

        public final void showSensorUseReminderNotification(UserHandle userHandle, String str, int i) {
            int i2;
            int i3;
            int i4;
            try {
                CharSequence loadLabel = SensorPrivacyService.this.getUiContext().getPackageManager().getApplicationInfoAsUser(str, 0, userHandle).loadLabel(SensorPrivacyService.this.mContext.getPackageManager());
                if (i == 1) {
                    i2 = 17302782;
                    i3 = 17041497;
                    i4 = 65;
                } else {
                    i2 = 17302365;
                    i3 = 17041495;
                    i4 = 66;
                }
                NotificationManager notificationManager = (NotificationManager) SensorPrivacyService.this.mContext.getSystemService(NotificationManager.class);
                NotificationChannel notificationChannel = new NotificationChannel("sensor_privacy", SensorPrivacyService.this.getUiContext().getString(17041494), 4);
                notificationChannel.setSound(null, null);
                notificationChannel.setBypassDnd(true);
                notificationChannel.enableVibration(false);
                notificationChannel.setBlockable(false);
                notificationManager.createNotificationChannel(notificationChannel);
                Icon createWithResource = Icon.createWithResource(SensorPrivacyService.this.getUiContext().getResources(), i2);
                String string = SensorPrivacyService.this.getUiContext().getString(i3);
                notificationManager.notify(i4, new Notification.Builder(SensorPrivacyService.this.mContext, "sensor_privacy").setContentTitle(string).setContentText(Html.fromHtml(SensorPrivacyService.this.getUiContext().getString(17041498, loadLabel), 0)).setSmallIcon(createWithResource).addAction(new Notification.Action.Builder(createWithResource, SensorPrivacyService.this.getUiContext().getString(17041496), PendingIntent.getBroadcast(SensorPrivacyService.this.mContext, i, new Intent(SensorPrivacyService.ACTION_DISABLE_TOGGLE_SENSOR_PRIVACY).setPackage(SensorPrivacyService.this.mContext.getPackageName()).putExtra(SensorPrivacyManager.EXTRA_SENSOR, i).putExtra("android.intent.extra.USER", userHandle), 201326592)).build()).setContentIntent(PendingIntent.getActivity(SensorPrivacyService.this.mContext, i, new Intent("android.settings.PRIVACY_SETTINGS"), 201326592)).extend(new Notification.TvExtender()).setTimeoutAfter(isTelevision(SensorPrivacyService.this.mContext) ? 1L : 0L).build());
            } catch (PackageManager.NameNotFoundException unused) {
                String str2 = SensorPrivacyService.TAG;
                Log.e(str2, "Cannot show sensor use notification for " + str);
            }
        }

        public final void showSensorStateChangedActivity(int i, int i2) {
            String string = SensorPrivacyService.this.mContext.getResources().getString(17040002);
            if (TextUtils.isEmpty(string)) {
                return;
            }
            Intent intent = new Intent();
            intent.setComponent(ComponentName.unflattenFromString(string));
            ActivityOptions makeBasic = ActivityOptions.makeBasic();
            makeBasic.setTaskOverlay(true, true);
            intent.addFlags(8650752);
            intent.putExtra(SensorPrivacyManager.EXTRA_SENSOR, i);
            intent.putExtra(SensorPrivacyManager.EXTRA_TOGGLE_TYPE, i2);
            SensorPrivacyService.this.mContext.startActivityAsUser(intent, makeBasic.toBundle(), UserHandle.SYSTEM);
        }

        public final boolean isTelevision(Context context) {
            return (context.getResources().getConfiguration().uiMode & 15) == 4;
        }

        public void setSensorPrivacy(boolean z) {
            enforceManageSensorPrivacyPermission();
            this.mSensorPrivacyStateController.setAllSensorState(z);
        }

        public void setToggleSensorPrivacy(int i, int i2, int i3, boolean z) {
            enforceManageSensorPrivacyPermission();
            if (i == -2) {
                i = SensorPrivacyService.this.mCurrentUser;
            }
            int i4 = i;
            if (canChangeToggleSensorPrivacy(i4, i3)) {
                setToggleSensorPrivacyUnchecked(1, i4, i2, i3, z);
            }
        }

        public final void setToggleSensorPrivacyUnchecked(final int i, final int i2, final int i3, final int i4, final boolean z) {
            final long[] jArr = new long[1];
            this.mSensorPrivacyStateController.atomic(new Runnable() { // from class: com.android.server.sensorprivacy.SensorPrivacyService$SensorPrivacyServiceImpl$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    SensorPrivacyService.SensorPrivacyServiceImpl.this.lambda$setToggleSensorPrivacyUnchecked$2(i, i2, i4, jArr, z, i3);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setToggleSensorPrivacyUnchecked$2(int i, final int i2, final int i3, final long[] jArr, final boolean z, final int i4) {
            jArr[0] = this.mSensorPrivacyStateController.getState(i, i2, i3).getLastChange();
            this.mSensorPrivacyStateController.setState(i, i2, i3, z, this.mHandler, new SensorPrivacyStateController.SetStateResultCallback() { // from class: com.android.server.sensorprivacy.SensorPrivacyService$SensorPrivacyServiceImpl$$ExternalSyntheticLambda8
                @Override // com.android.server.sensorprivacy.SensorPrivacyStateController.SetStateResultCallback
                public final void callback(boolean z2) {
                    SensorPrivacyService.SensorPrivacyServiceImpl.this.lambda$setToggleSensorPrivacyUnchecked$1(i2, i4, i3, z, jArr, z2);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setToggleSensorPrivacyUnchecked$1(int i, int i2, int i3, boolean z, long[] jArr, boolean z2) {
            if (z2 && i == SensorPrivacyService.this.mUserManagerInternal.getProfileParentId(i)) {
                this.mHandler.sendMessage(PooledLambda.obtainMessage(new HexConsumer() { // from class: com.android.server.sensorprivacy.SensorPrivacyService$SensorPrivacyServiceImpl$$ExternalSyntheticLambda9
                    public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
                        ((SensorPrivacyService.SensorPrivacyServiceImpl) obj).logSensorPrivacyToggle(((Integer) obj2).intValue(), ((Integer) obj3).intValue(), ((Boolean) obj4).booleanValue(), ((Long) obj5).longValue(), ((Boolean) obj6).booleanValue());
                    }
                }, this, Integer.valueOf(i2), Integer.valueOf(i3), Boolean.valueOf(z), Long.valueOf(jArr[0]), Boolean.FALSE));
            }
        }

        public final boolean canChangeToggleSensorPrivacy(int i, int i2) {
            if (i2 == 1 && SensorPrivacyService.this.mCallStateHelper.isInEmergencyCall()) {
                Log.i(SensorPrivacyService.TAG, "Can't change mic toggle during an emergency call");
                return false;
            } else if (requiresAuthentication() && SensorPrivacyService.this.mKeyguardManager != null && SensorPrivacyService.this.mKeyguardManager.isDeviceLocked(i)) {
                Log.i(SensorPrivacyService.TAG, "Can't change mic/cam toggle while device is locked");
                return false;
            } else if (i2 == 1 && SensorPrivacyService.this.mUserManagerInternal.getUserRestriction(i, "disallow_microphone_toggle")) {
                Log.i(SensorPrivacyService.TAG, "Can't change mic toggle due to admin restriction");
                return false;
            } else if (i2 == 2 && SensorPrivacyService.this.mUserManagerInternal.getUserRestriction(i, "disallow_camera_toggle")) {
                Log.i(SensorPrivacyService.TAG, "Can't change camera toggle due to admin restriction");
                return false;
            } else {
                return true;
            }
        }

        public final void logSensorPrivacyToggle(int i, int i2, boolean z, long j, boolean z2) {
            long max = Math.max(0L, (SensorPrivacyService.getCurrentTimeMillis() - j) / 60000);
            FrameworkStatsLog.write((int) FrameworkStatsLog.PRIVACY_SENSOR_TOGGLE_INTERACTION, i2 != 1 ? i2 != 2 ? 0 : 2 : 1, z2 ? 0 : z ? 2 : 1, i != 1 ? i != 2 ? i != 3 ? 0 : 1 : 2 : 3, max);
        }

        public void setToggleSensorPrivacyForProfileGroup(int i, final int i2, final int i3, final boolean z) {
            enforceManageSensorPrivacyPermission();
            if (i == -2) {
                i = SensorPrivacyService.this.mCurrentUser;
            }
            final int profileParentId = SensorPrivacyService.this.mUserManagerInternal.getProfileParentId(i);
            SensorPrivacyService.this.forAllUsers(new FunctionalUtils.ThrowingConsumer() { // from class: com.android.server.sensorprivacy.SensorPrivacyService$SensorPrivacyServiceImpl$$ExternalSyntheticLambda2
                public final void acceptOrThrow(Object obj) {
                    SensorPrivacyService.SensorPrivacyServiceImpl.this.lambda$setToggleSensorPrivacyForProfileGroup$3(profileParentId, i2, i3, z, (Integer) obj);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setToggleSensorPrivacyForProfileGroup$3(int i, int i2, int i3, boolean z, Integer num) throws Exception {
            if (i == SensorPrivacyService.this.mUserManagerInternal.getProfileParentId(num.intValue())) {
                setToggleSensorPrivacy(num.intValue(), i2, i3, z);
            }
        }

        public final void enforceManageSensorPrivacyPermission() {
            enforcePermission("android.permission.MANAGE_SENSOR_PRIVACY", "Changing sensor privacy requires the following permission: android.permission.MANAGE_SENSOR_PRIVACY");
        }

        public final void enforceObserveSensorPrivacyPermission() {
            if (UserHandle.getCallingAppId() == UserHandle.getAppId(SensorPrivacyService.this.mPackageManagerInternal.getPackageUid(SensorPrivacyService.this.mContext.getString(17039418), 1048576L, 0))) {
                return;
            }
            enforcePermission("android.permission.OBSERVE_SENSOR_PRIVACY", "Observing sensor privacy changes requires the following permission: android.permission.OBSERVE_SENSOR_PRIVACY");
        }

        public final void enforcePermission(String str, String str2) {
            if (SensorPrivacyService.this.mContext.checkCallingOrSelfPermission(str) != 0) {
                throw new SecurityException(str2);
            }
        }

        public boolean isSensorPrivacyEnabled() {
            enforceObserveSensorPrivacyPermission();
            return this.mSensorPrivacyStateController.getAllSensorState();
        }

        public boolean isToggleSensorPrivacyEnabled(int i, int i2) {
            enforceObserveSensorPrivacyPermission();
            return this.mSensorPrivacyStateController.getState(i, SensorPrivacyService.this.mCurrentUser, i2).isEnabled();
        }

        public boolean isCombinedToggleSensorPrivacyEnabled(int i) {
            return isToggleSensorPrivacyEnabled(1, i) || isToggleSensorPrivacyEnabled(2, i);
        }

        public final boolean isToggleSensorPrivacyEnabledInternal(int i, int i2, int i3) {
            return this.mSensorPrivacyStateController.getState(i2, i, i3).isEnabled();
        }

        public boolean supportsSensorToggle(int i, int i2) {
            if (i == 1) {
                if (i2 == 1) {
                    return SensorPrivacyService.this.mContext.getResources().getBoolean(17891831);
                }
                if (i2 == 2) {
                    return SensorPrivacyService.this.mContext.getResources().getBoolean(17891826);
                }
            } else if (i == 2) {
                if (i2 == 1) {
                    return SensorPrivacyService.this.mContext.getResources().getBoolean(17891829);
                }
                if (i2 == 2) {
                    return SensorPrivacyService.this.mContext.getResources().getBoolean(17891828);
                }
            }
            throw new IllegalArgumentException("Invalid arguments. toggleType=" + i + " sensor=" + i2);
        }

        public void addSensorPrivacyListener(ISensorPrivacyListener iSensorPrivacyListener) {
            enforceObserveSensorPrivacyPermission();
            if (iSensorPrivacyListener == null) {
                throw new NullPointerException("listener cannot be null");
            }
            this.mHandler.addListener(iSensorPrivacyListener);
        }

        public void addToggleSensorPrivacyListener(ISensorPrivacyListener iSensorPrivacyListener) {
            enforceObserveSensorPrivacyPermission();
            if (iSensorPrivacyListener == null) {
                throw new IllegalArgumentException("listener cannot be null");
            }
            this.mHandler.addToggleListener(iSensorPrivacyListener);
        }

        public void removeSensorPrivacyListener(ISensorPrivacyListener iSensorPrivacyListener) {
            enforceObserveSensorPrivacyPermission();
            if (iSensorPrivacyListener == null) {
                throw new NullPointerException("listener cannot be null");
            }
            this.mHandler.removeListener(iSensorPrivacyListener);
        }

        public void removeToggleSensorPrivacyListener(ISensorPrivacyListener iSensorPrivacyListener) {
            enforceObserveSensorPrivacyPermission();
            if (iSensorPrivacyListener == null) {
                throw new IllegalArgumentException("listener cannot be null");
            }
            this.mHandler.removeToggleListener(iSensorPrivacyListener);
        }

        public void suppressToggleSensorPrivacyReminders(int i, int i2, IBinder iBinder, boolean z) {
            enforceManageSensorPrivacyPermission();
            if (i == -2) {
                i = SensorPrivacyService.this.mCurrentUser;
            }
            Objects.requireNonNull(iBinder);
            Pair<Integer, UserHandle> pair = new Pair<>(Integer.valueOf(i2), UserHandle.of(i));
            synchronized (this.mLock) {
                if (z) {
                    try {
                        iBinder.linkToDeath(this, 0);
                        ArrayList<IBinder> arrayList = this.mSuppressReminders.get(pair);
                        if (arrayList == null) {
                            arrayList = new ArrayList<>(1);
                            this.mSuppressReminders.put(pair, arrayList);
                        }
                        arrayList.add(iBinder);
                    } catch (RemoteException e) {
                        Log.e(SensorPrivacyService.TAG, "Could not suppress sensor use reminder", e);
                    }
                } else {
                    this.mHandler.removeSuppressPackageReminderToken(pair, iBinder);
                }
            }
        }

        public boolean requiresAuthentication() {
            enforceObserveSensorPrivacyPermission();
            return SensorPrivacyService.this.mContext.getResources().getBoolean(17891782);
        }

        public void showSensorUseDialog(int i) {
            if (Binder.getCallingUid() != 1000) {
                throw new SecurityException("Can only be called by the system uid");
            }
            if (isCombinedToggleSensorPrivacyEnabled(i)) {
                enqueueSensorUseReminderDialogAsync(-1, UserHandle.of(SensorPrivacyService.this.mCurrentUser), PackageManagerShellCommandDataLoader.PACKAGE, i);
            }
        }

        public final void userSwitching(final int i, final int i2) {
            int i3;
            final boolean[] zArr = new boolean[2];
            final boolean[] zArr2 = new boolean[2];
            final boolean[] zArr3 = new boolean[2];
            final boolean[] zArr4 = new boolean[2];
            this.mSensorPrivacyStateController.atomic(new Runnable() { // from class: com.android.server.sensorprivacy.SensorPrivacyService$SensorPrivacyServiceImpl$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    SensorPrivacyService.SensorPrivacyServiceImpl.this.lambda$userSwitching$4(zArr3, i, zArr4, zArr, i2, zArr2);
                }
            });
            this.mSensorPrivacyStateController.atomic(new Runnable() { // from class: com.android.server.sensorprivacy.SensorPrivacyService$SensorPrivacyServiceImpl$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    SensorPrivacyService.SensorPrivacyServiceImpl.this.lambda$userSwitching$5(zArr3, i, zArr4, zArr, i2, zArr2);
                }
            });
            boolean z = false;
            if (i != -10000 && zArr3[0] == zArr[0] && zArr3[1] == zArr[1]) {
                i3 = i2;
            } else {
                i3 = i2;
                this.mHandler.handleSensorPrivacyChanged(i3, 1, 1, zArr[0]);
                this.mHandler.handleSensorPrivacyChanged(i3, 2, 1, zArr[1]);
                setGlobalRestriction(1, zArr[0] || zArr[1]);
            }
            if (i != -10000 && zArr4[0] == zArr2[0] && zArr4[1] == zArr2[1]) {
                return;
            }
            this.mHandler.handleSensorPrivacyChanged(i3, 1, 2, zArr2[0]);
            this.mHandler.handleSensorPrivacyChanged(i3, 2, 2, zArr2[1]);
            if (zArr2[0] || zArr2[1]) {
                z = true;
            }
            setGlobalRestriction(2, z);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$userSwitching$4(boolean[] zArr, int i, boolean[] zArr2, boolean[] zArr3, int i2, boolean[] zArr4) {
            zArr[0] = isToggleSensorPrivacyEnabledInternal(i, 1, 1);
            zArr2[0] = isToggleSensorPrivacyEnabledInternal(i, 1, 2);
            zArr3[0] = isToggleSensorPrivacyEnabledInternal(i2, 1, 1);
            zArr4[0] = isToggleSensorPrivacyEnabledInternal(i2, 1, 2);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$userSwitching$5(boolean[] zArr, int i, boolean[] zArr2, boolean[] zArr3, int i2, boolean[] zArr4) {
            zArr[1] = isToggleSensorPrivacyEnabledInternal(i, 2, 1);
            zArr2[1] = isToggleSensorPrivacyEnabledInternal(i, 2, 2);
            zArr3[1] = isToggleSensorPrivacyEnabledInternal(i2, 2, 1);
            zArr4[1] = isToggleSensorPrivacyEnabledInternal(i2, 2, 2);
        }

        public final void setGlobalRestriction(int i, boolean z) {
            boolean z2 = true;
            if (i != 1) {
                if (i != 2) {
                    return;
                }
                SensorPrivacyService.this.mAppOpsManagerInternal.setGlobalRestriction(26, z, SensorPrivacyService.this.mAppOpsRestrictionToken);
                SensorPrivacyService.this.mAppOpsManagerInternal.setGlobalRestriction(101, z, SensorPrivacyService.this.mAppOpsRestrictionToken);
                return;
            }
            SensorPrivacyService.this.mAppOpsManagerInternal.setGlobalRestriction(27, z, SensorPrivacyService.this.mAppOpsRestrictionToken);
            SensorPrivacyService.this.mAppOpsManagerInternal.setGlobalRestriction(100, z, SensorPrivacyService.this.mAppOpsRestrictionToken);
            SensorPrivacyService.this.mAppOpsManagerInternal.setGlobalRestriction(120, z, SensorPrivacyService.this.mAppOpsRestrictionToken);
            boolean z3 = Settings.Global.getInt(SensorPrivacyService.this.mContext.getContentResolver(), "receive_explicit_user_interaction_audio_enabled", 1) == 1;
            AppOpsManagerInternal appOpsManagerInternal = SensorPrivacyService.this.mAppOpsManagerInternal;
            if (!z || z3) {
                z2 = false;
            }
            appOpsManagerInternal.setGlobalRestriction(121, z2, SensorPrivacyService.this.mAppOpsRestrictionToken);
        }

        public final void removeSuppressPackageReminderToken(Pair<Integer, UserHandle> pair, IBinder iBinder) {
            synchronized (this.mLock) {
                ArrayList<IBinder> arrayList = this.mSuppressReminders.get(pair);
                if (arrayList == null) {
                    String str = SensorPrivacyService.TAG;
                    Log.e(str, "No tokens for " + pair);
                    return;
                }
                if (arrayList.remove(iBinder)) {
                    iBinder.unlinkToDeath(this, 0);
                    if (arrayList.isEmpty()) {
                        this.mSuppressReminders.remove(pair);
                    }
                } else {
                    String str2 = SensorPrivacyService.TAG;
                    Log.w(str2, "Could not remove sensor use reminder suppression token " + iBinder + " from " + pair);
                }
            }
        }

        public final void registerSettingsObserver() {
            SensorPrivacyService.this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("receive_explicit_user_interaction_audio_enabled"), false, new ContentObserver(this.mHandler) { // from class: com.android.server.sensorprivacy.SensorPrivacyService.SensorPrivacyServiceImpl.3
                @Override // android.database.ContentObserver
                public void onChange(boolean z) {
                    SensorPrivacyServiceImpl sensorPrivacyServiceImpl = SensorPrivacyServiceImpl.this;
                    sensorPrivacyServiceImpl.setGlobalRestriction(1, sensorPrivacyServiceImpl.isCombinedToggleSensorPrivacyEnabled(1));
                }
            });
        }

        public void binderDied(IBinder iBinder) {
            synchronized (this.mLock) {
                for (Pair<Integer, UserHandle> pair : this.mSuppressReminders.keySet()) {
                    removeSuppressPackageReminderToken(pair, iBinder);
                }
            }
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            String str;
            Objects.requireNonNull(fileDescriptor);
            if (DumpUtils.checkDumpPermission(SensorPrivacyService.this.mContext, SensorPrivacyService.TAG, printWriter)) {
                int i = 0;
                boolean z = false;
                while (i < strArr.length && (str = strArr[i]) != null && str.length() > 0 && str.charAt(0) == '-') {
                    i++;
                    if ("--proto".equals(str)) {
                        z = true;
                    } else {
                        printWriter.println("Unknown argument: " + str + "; use -h for help");
                    }
                }
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    if (z) {
                        this.mSensorPrivacyStateController.dump(new DualDumpOutputStream(new ProtoOutputStream(fileDescriptor)));
                    } else {
                        printWriter.println("SENSOR PRIVACY MANAGER STATE (dumpsys sensor_privacy)");
                        this.mSensorPrivacyStateController.dump(new DualDumpOutputStream(new IndentingPrintWriter(printWriter, "  ")));
                    }
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        public final int sensorStrToId(String str) {
            if (str == null) {
                return 0;
            }
            if (str.equals("camera")) {
                return 2;
            }
            return !str.equals("microphone") ? 0 : 1;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
            new ShellCommand() { // from class: com.android.server.sensorprivacy.SensorPrivacyService.SensorPrivacyServiceImpl.4
                public int onCommand(String str) {
                    if (str == null) {
                        return handleDefaultCommands(str);
                    }
                    int parseInt = Integer.parseInt(getNextArgRequired());
                    PrintWriter outPrintWriter = getOutPrintWriter();
                    if (str.equals("enable")) {
                        int sensorStrToId = SensorPrivacyServiceImpl.this.sensorStrToId(getNextArgRequired());
                        if (sensorStrToId == 0) {
                            outPrintWriter.println("Invalid sensor");
                            return -1;
                        }
                        SensorPrivacyServiceImpl.this.setToggleSensorPrivacy(parseInt, 4, sensorStrToId, true);
                    } else if (str.equals("disable")) {
                        int sensorStrToId2 = SensorPrivacyServiceImpl.this.sensorStrToId(getNextArgRequired());
                        if (sensorStrToId2 == 0) {
                            outPrintWriter.println("Invalid sensor");
                            return -1;
                        }
                        SensorPrivacyServiceImpl.this.setToggleSensorPrivacy(parseInt, 4, sensorStrToId2, false);
                    } else {
                        return handleDefaultCommands(str);
                    }
                    return 0;
                }

                public void onHelp() {
                    PrintWriter outPrintWriter = getOutPrintWriter();
                    outPrintWriter.println("Sensor privacy manager (sensor_privacy) commands:");
                    outPrintWriter.println("  help");
                    outPrintWriter.println("    Print this help text.");
                    outPrintWriter.println("");
                    outPrintWriter.println("  enable USER_ID SENSOR");
                    outPrintWriter.println("    Enable privacy for a certain sensor.");
                    outPrintWriter.println("");
                    outPrintWriter.println("  disable USER_ID SENSOR");
                    outPrintWriter.println("    Disable privacy for a certain sensor.");
                    outPrintWriter.println("");
                }
            }.exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
        }
    }

    /* loaded from: classes2.dex */
    public final class SensorPrivacyHandler extends Handler {
        public final Context mContext;
        @GuardedBy({"mListenerLock"})
        public final ArrayMap<ISensorPrivacyListener, Pair<DeathRecipient, Integer>> mDeathRecipients;
        public final Object mListenerLock;
        @GuardedBy({"mListenerLock"})
        public final RemoteCallbackList<ISensorPrivacyListener> mListeners;
        @GuardedBy({"mListenerLock"})
        public final RemoteCallbackList<ISensorPrivacyListener> mToggleSensorListeners;

        public SensorPrivacyHandler(Looper looper, Context context) {
            super(looper);
            this.mListenerLock = new Object();
            this.mListeners = new RemoteCallbackList<>();
            this.mToggleSensorListeners = new RemoteCallbackList<>();
            this.mDeathRecipients = new ArrayMap<>();
            this.mContext = context;
        }

        public void addListener(ISensorPrivacyListener iSensorPrivacyListener) {
            synchronized (this.mListenerLock) {
                if (this.mListeners.register(iSensorPrivacyListener)) {
                    addDeathRecipient(iSensorPrivacyListener);
                }
            }
        }

        public void addToggleListener(ISensorPrivacyListener iSensorPrivacyListener) {
            synchronized (this.mListenerLock) {
                if (this.mToggleSensorListeners.register(iSensorPrivacyListener)) {
                    addDeathRecipient(iSensorPrivacyListener);
                }
            }
        }

        public void removeListener(ISensorPrivacyListener iSensorPrivacyListener) {
            synchronized (this.mListenerLock) {
                if (this.mListeners.unregister(iSensorPrivacyListener)) {
                    removeDeathRecipient(iSensorPrivacyListener);
                }
            }
        }

        public void removeToggleListener(ISensorPrivacyListener iSensorPrivacyListener) {
            synchronized (this.mListenerLock) {
                if (this.mToggleSensorListeners.unregister(iSensorPrivacyListener)) {
                    removeDeathRecipient(iSensorPrivacyListener);
                }
            }
        }

        public void handleSensorPrivacyChanged(boolean z) {
            int beginBroadcast = this.mListeners.beginBroadcast();
            for (int i = 0; i < beginBroadcast; i++) {
                ISensorPrivacyListener broadcastItem = this.mListeners.getBroadcastItem(i);
                try {
                    broadcastItem.onSensorPrivacyChanged(-1, -1, z);
                } catch (RemoteException e) {
                    String str = SensorPrivacyService.TAG;
                    Log.e(str, "Caught an exception notifying listener " + broadcastItem + ": ", e);
                }
            }
            this.mListeners.finishBroadcast();
        }

        public void handleSensorPrivacyChanged(int i, int i2, int i3, boolean z) {
            SensorPrivacyService.this.mSensorPrivacyManagerInternal.dispatch(i, i3, z);
            if (i == SensorPrivacyService.this.mCurrentUser) {
                SensorPrivacyService.this.mSensorPrivacyServiceImpl.setGlobalRestriction(i3, SensorPrivacyService.this.mSensorPrivacyServiceImpl.isCombinedToggleSensorPrivacyEnabled(i3));
            }
            if (i != SensorPrivacyService.this.mCurrentUser) {
                return;
            }
            synchronized (this.mListenerLock) {
                int beginBroadcast = this.mToggleSensorListeners.beginBroadcast();
                for (int i4 = 0; i4 < beginBroadcast; i4++) {
                    ISensorPrivacyListener broadcastItem = this.mToggleSensorListeners.getBroadcastItem(i4);
                    try {
                        broadcastItem.onSensorPrivacyChanged(i2, i3, z);
                    } catch (RemoteException e) {
                        String str = SensorPrivacyService.TAG;
                        Log.e(str, "Caught an exception notifying listener " + broadcastItem + ": ", e);
                    }
                }
                this.mToggleSensorListeners.finishBroadcast();
            }
            SensorPrivacyService.this.mSensorPrivacyServiceImpl.showSensorStateChangedActivity(i3, i2);
        }

        public void removeSuppressPackageReminderToken(Pair<Integer, UserHandle> pair, IBinder iBinder) {
            sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.sensorprivacy.SensorPrivacyService$SensorPrivacyHandler$$ExternalSyntheticLambda0
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((SensorPrivacyService.SensorPrivacyServiceImpl) obj).removeSuppressPackageReminderToken((Pair) obj2, (IBinder) obj3);
                }
            }, SensorPrivacyService.this.mSensorPrivacyServiceImpl, pair, iBinder));
        }

        /* JADX WARN: Multi-variable type inference failed */
        public final void addDeathRecipient(ISensorPrivacyListener iSensorPrivacyListener) {
            Pair pair;
            Pair<DeathRecipient, Integer> pair2 = this.mDeathRecipients.get(iSensorPrivacyListener);
            if (pair2 == null) {
                pair = new Pair(new DeathRecipient(iSensorPrivacyListener), 1);
            } else {
                pair = new Pair((DeathRecipient) pair2.first, Integer.valueOf(((Integer) pair2.second).intValue() + 1));
            }
            this.mDeathRecipients.put(iSensorPrivacyListener, pair);
        }

        public final void removeDeathRecipient(ISensorPrivacyListener iSensorPrivacyListener) {
            Pair<DeathRecipient, Integer> pair = this.mDeathRecipients.get(iSensorPrivacyListener);
            if (pair == null) {
                return;
            }
            int intValue = ((Integer) pair.second).intValue() - 1;
            if (intValue == 0) {
                this.mDeathRecipients.remove(iSensorPrivacyListener);
                ((DeathRecipient) pair.first).destroy();
                return;
            }
            this.mDeathRecipients.put(iSensorPrivacyListener, new Pair<>((DeathRecipient) pair.first, Integer.valueOf(intValue)));
        }
    }

    /* loaded from: classes2.dex */
    public final class DeathRecipient implements IBinder.DeathRecipient {
        public ISensorPrivacyListener mListener;

        public DeathRecipient(ISensorPrivacyListener iSensorPrivacyListener) {
            this.mListener = iSensorPrivacyListener;
            try {
                iSensorPrivacyListener.asBinder().linkToDeath(this, 0);
            } catch (RemoteException unused) {
            }
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            SensorPrivacyService.this.mSensorPrivacyServiceImpl.removeSensorPrivacyListener(this.mListener);
        }

        public void destroy() {
            try {
                this.mListener.asBinder().unlinkToDeath(this, 0);
            } catch (NoSuchElementException unused) {
            }
        }
    }

    public final void forAllUsers(FunctionalUtils.ThrowingConsumer<Integer> throwingConsumer) {
        for (int i : this.mUserManagerInternal.getUserIds()) {
            throwingConsumer.accept(Integer.valueOf(i));
        }
    }

    /* loaded from: classes2.dex */
    public class SensorPrivacyManagerInternalImpl extends SensorPrivacyManagerInternal {
        public ArrayMap<Integer, ArraySet<SensorPrivacyManagerInternal.OnUserSensorPrivacyChangedListener>> mAllUserListeners;
        public ArrayMap<Integer, ArrayMap<Integer, ArraySet<SensorPrivacyManagerInternal.OnSensorPrivacyChangedListener>>> mListeners;
        public final Object mLock;

        public SensorPrivacyManagerInternalImpl() {
            this.mListeners = new ArrayMap<>();
            this.mAllUserListeners = new ArrayMap<>();
            this.mLock = new Object();
        }

        public final void dispatch(final int i, int i2, final boolean z) {
            ArraySet<SensorPrivacyManagerInternal.OnSensorPrivacyChangedListener> arraySet;
            synchronized (this.mLock) {
                ArraySet<SensorPrivacyManagerInternal.OnUserSensorPrivacyChangedListener> arraySet2 = this.mAllUserListeners.get(Integer.valueOf(i2));
                if (arraySet2 != null) {
                    for (int i3 = 0; i3 < arraySet2.size(); i3++) {
                        final SensorPrivacyManagerInternal.OnUserSensorPrivacyChangedListener valueAt = arraySet2.valueAt(i3);
                        BackgroundThread.getHandler().post(new Runnable() { // from class: com.android.server.sensorprivacy.SensorPrivacyService$SensorPrivacyManagerInternalImpl$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                valueAt.onSensorPrivacyChanged(i, z);
                            }
                        });
                    }
                }
                ArrayMap<Integer, ArraySet<SensorPrivacyManagerInternal.OnSensorPrivacyChangedListener>> arrayMap = this.mListeners.get(Integer.valueOf(i));
                if (arrayMap != null && (arraySet = arrayMap.get(Integer.valueOf(i2))) != null) {
                    for (int i4 = 0; i4 < arraySet.size(); i4++) {
                        final SensorPrivacyManagerInternal.OnSensorPrivacyChangedListener valueAt2 = arraySet.valueAt(i4);
                        BackgroundThread.getHandler().post(new Runnable() { // from class: com.android.server.sensorprivacy.SensorPrivacyService$SensorPrivacyManagerInternalImpl$$ExternalSyntheticLambda1
                            @Override // java.lang.Runnable
                            public final void run() {
                                valueAt2.onSensorPrivacyChanged(z);
                            }
                        });
                    }
                }
            }
        }

        public boolean isSensorPrivacyEnabled(int i, int i2) {
            return SensorPrivacyService.this.mSensorPrivacyServiceImpl.isToggleSensorPrivacyEnabledInternal(i, 1, i2);
        }

        public void addSensorPrivacyListener(int i, int i2, SensorPrivacyManagerInternal.OnSensorPrivacyChangedListener onSensorPrivacyChangedListener) {
            synchronized (this.mLock) {
                ArrayMap<Integer, ArraySet<SensorPrivacyManagerInternal.OnSensorPrivacyChangedListener>> arrayMap = this.mListeners.get(Integer.valueOf(i));
                if (arrayMap == null) {
                    arrayMap = new ArrayMap<>();
                    this.mListeners.put(Integer.valueOf(i), arrayMap);
                }
                ArraySet<SensorPrivacyManagerInternal.OnSensorPrivacyChangedListener> arraySet = arrayMap.get(Integer.valueOf(i2));
                if (arraySet == null) {
                    arraySet = new ArraySet<>();
                    arrayMap.put(Integer.valueOf(i2), arraySet);
                }
                arraySet.add(onSensorPrivacyChangedListener);
            }
        }

        public void addSensorPrivacyListenerForAllUsers(int i, SensorPrivacyManagerInternal.OnUserSensorPrivacyChangedListener onUserSensorPrivacyChangedListener) {
            synchronized (this.mLock) {
                ArraySet<SensorPrivacyManagerInternal.OnUserSensorPrivacyChangedListener> arraySet = this.mAllUserListeners.get(Integer.valueOf(i));
                if (arraySet == null) {
                    arraySet = new ArraySet<>();
                    this.mAllUserListeners.put(Integer.valueOf(i), arraySet);
                }
                arraySet.add(onUserSensorPrivacyChangedListener);
            }
        }

        public void setPhysicalToggleSensorPrivacy(int i, int i2, boolean z) {
            SensorPrivacyServiceImpl sensorPrivacyServiceImpl = SensorPrivacyService.this.mSensorPrivacyServiceImpl;
            if (i == -2) {
                i = SensorPrivacyService.this.mCurrentUser;
            }
            int userId = i == -10000 ? SensorPrivacyService.this.mContext.getUserId() : i;
            sensorPrivacyServiceImpl.setToggleSensorPrivacyUnchecked(2, userId, 5, i2, z);
            if (z) {
                return;
            }
            sensorPrivacyServiceImpl.setToggleSensorPrivacyUnchecked(1, userId, 5, i2, z);
        }
    }

    /* loaded from: classes2.dex */
    public class CallStateHelper {
        public boolean mIsInEmergencyCall;
        public boolean mMicUnmutedForEmergencyCall;
        public Object mCallStateLock = new Object();
        public OutgoingEmergencyStateCallback mEmergencyStateCallback = new OutgoingEmergencyStateCallback();
        public CallStateCallback mCallStateCallback = new CallStateCallback();

        public CallStateHelper() {
            SensorPrivacyService.this.mTelephonyManager.registerTelephonyCallback(FgThread.getExecutor(), this.mEmergencyStateCallback);
            SensorPrivacyService.this.mTelephonyManager.registerTelephonyCallback(FgThread.getExecutor(), this.mCallStateCallback);
        }

        public boolean isInEmergencyCall() {
            boolean z;
            synchronized (this.mCallStateLock) {
                z = this.mIsInEmergencyCall;
            }
            return z;
        }

        /* loaded from: classes2.dex */
        public class OutgoingEmergencyStateCallback extends TelephonyCallback implements TelephonyCallback.OutgoingEmergencyCallListener {
            public OutgoingEmergencyStateCallback() {
            }

            public void onOutgoingEmergencyCall(EmergencyNumber emergencyNumber, int i) {
                CallStateHelper.this.onEmergencyCall();
            }
        }

        /* loaded from: classes2.dex */
        public class CallStateCallback extends TelephonyCallback implements TelephonyCallback.CallStateListener {
            public CallStateCallback() {
            }

            @Override // android.telephony.TelephonyCallback.CallStateListener
            public void onCallStateChanged(int i) {
                if (i == 0) {
                    CallStateHelper.this.onCallOver();
                } else {
                    CallStateHelper.this.onCall();
                }
            }
        }

        public final void onEmergencyCall() {
            synchronized (this.mCallStateLock) {
                if (!this.mIsInEmergencyCall) {
                    this.mIsInEmergencyCall = true;
                    if (SensorPrivacyService.this.mSensorPrivacyServiceImpl.isToggleSensorPrivacyEnabled(1, 1)) {
                        SensorPrivacyService.this.mSensorPrivacyServiceImpl.setToggleSensorPrivacyUnchecked(1, SensorPrivacyService.this.mCurrentUser, 5, 1, false);
                        this.mMicUnmutedForEmergencyCall = true;
                    } else {
                        this.mMicUnmutedForEmergencyCall = false;
                    }
                }
            }
        }

        public final void onCall() {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (this.mCallStateLock) {
                    SensorPrivacyService.this.mSensorPrivacyServiceImpl.showSensorUseDialog(1);
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public final void onCallOver() {
            synchronized (this.mCallStateLock) {
                if (this.mIsInEmergencyCall) {
                    this.mIsInEmergencyCall = false;
                    if (this.mMicUnmutedForEmergencyCall) {
                        SensorPrivacyService.this.mSensorPrivacyServiceImpl.setToggleSensorPrivacyUnchecked(1, SensorPrivacyService.this.mCurrentUser, 5, 1, true);
                        this.mMicUnmutedForEmergencyCall = false;
                    }
                }
            }
        }
    }

    public static long getCurrentTimeMillis() {
        return SystemClock.elapsedRealtime();
    }
}
