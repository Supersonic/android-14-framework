package com.android.server.voiceinteraction;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.app.UriGrantsManager;
import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManagerInternal;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.service.voice.IVoiceInteractionSession;
import android.service.voice.IVoiceInteractionSessionService;
import android.service.voice.VisibleActivityInfo;
import android.util.ArrayMap;
import android.util.Slog;
import android.view.IWindowManager;
import com.android.internal.app.AssistUtils;
import com.android.internal.app.IVoiceInteractionSessionShowCallback;
import com.android.internal.app.IVoiceInteractor;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.p006am.AssistDataRequester;
import com.android.server.p014wm.ActivityAssistInfo;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import com.android.server.power.LowPowerStandbyControllerInternal;
import com.android.server.statusbar.StatusBarManagerInternal;
import com.android.server.uri.UriGrantsManagerInternal;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
/* loaded from: classes2.dex */
public final class VoiceInteractionSessionConnection implements ServiceConnection, AssistDataRequester.AssistDataRequesterCallbacks {
    public static final int POWER_BOOST_TIMEOUT_MS = Integer.parseInt(System.getProperty("vendor.powerhal.interaction.max", "200"));
    public final IActivityManager mAm;
    public final AppOpsManager mAppOps;
    public AssistDataRequester mAssistDataRequester;
    public final Intent mBindIntent;
    public boolean mBound;
    public final Callback mCallback;
    public final int mCallingUid;
    public boolean mCanceled;
    public final Context mContext;
    public final Handler mFgHandler;
    public final ServiceConnection mFullConnection;
    public boolean mFullyBound;
    public final Handler mHandler;
    public final IWindowManager mIWindowManager;
    public IVoiceInteractor mInteractor;
    public boolean mListeningVisibleActivity;
    public final Object mLock;
    public boolean mLowPowerStandbyAllowlisted;
    public final LowPowerStandbyControllerInternal mLowPowerStandbyControllerInternal;
    public List<ActivityAssistInfo> mPendingHandleAssistWithoutData;
    public ArrayList<IVoiceInteractionSessionShowCallback> mPendingShowCallbacks;
    public final IBinder mPermissionOwner;
    public final PowerManagerInternal mPowerManagerInternal;
    public final Runnable mRemoveFromLowPowerStandbyAllowlistRunnable;
    public final ScheduledExecutorService mScheduledExecutorService;
    public IVoiceInteractionSessionService mService;
    public IVoiceInteractionSession mSession;
    public final ComponentName mSessionComponentName;
    public PowerBoostSetter mSetPowerBoostRunnable;
    public Bundle mShowArgs;
    public Runnable mShowAssistDisclosureRunnable;
    public IVoiceInteractionSessionShowCallback mShowCallback;
    public int mShowFlags;
    public boolean mShown;
    public final IBinder mToken;
    public final UriGrantsManagerInternal mUgmInternal;
    public final int mUser;
    public final ArrayMap<IBinder, VisibleActivityInfo> mVisibleActivityInfoForToken;

    /* loaded from: classes2.dex */
    public interface Callback {
        void onSessionHidden(VoiceInteractionSessionConnection voiceInteractionSessionConnection);

        void onSessionShown(VoiceInteractionSessionConnection voiceInteractionSessionConnection);

        void sessionConnectionGone(VoiceInteractionSessionConnection voiceInteractionSessionConnection);
    }

    /* loaded from: classes2.dex */
    public class PowerBoostSetter implements Runnable {
        public boolean mCanceled;
        public final Instant mExpiryTime;

        public PowerBoostSetter(Instant instant) {
            this.mExpiryTime = instant;
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (VoiceInteractionSessionConnection.this.mLock) {
                if (this.mCanceled) {
                    return;
                }
                if (Instant.now().isBefore(this.mExpiryTime)) {
                    VoiceInteractionSessionConnection.this.mPowerManagerInternal.setPowerBoost(0, 300);
                    if (VoiceInteractionSessionConnection.this.mSetPowerBoostRunnable != null) {
                        VoiceInteractionSessionConnection.this.mFgHandler.postDelayed(VoiceInteractionSessionConnection.this.mSetPowerBoostRunnable, VoiceInteractionSessionConnection.POWER_BOOST_TIMEOUT_MS);
                    }
                } else {
                    Slog.w("VoiceInteractionServiceManager", "Reset power boost INTERACTION because reaching max timeout.");
                    VoiceInteractionSessionConnection.this.mPowerManagerInternal.setPowerBoost(0, -1);
                }
            }
        }

        public void cancel() {
            synchronized (VoiceInteractionSessionConnection.this.mLock) {
                this.mCanceled = true;
            }
        }
    }

    public VoiceInteractionSessionConnection(Object obj, ComponentName componentName, int i, Context context, Callback callback, int i2, Handler handler) {
        Binder binder = new Binder();
        this.mToken = binder;
        this.mPendingShowCallbacks = new ArrayList<>();
        this.mPendingHandleAssistWithoutData = new ArrayList();
        this.mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.mVisibleActivityInfoForToken = new ArrayMap<>();
        this.mRemoveFromLowPowerStandbyAllowlistRunnable = new Runnable() { // from class: com.android.server.voiceinteraction.VoiceInteractionSessionConnection$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                VoiceInteractionSessionConnection.this.removeFromLowPowerStandbyAllowlist();
            }
        };
        this.mShowCallback = new IVoiceInteractionSessionShowCallback.Stub() { // from class: com.android.server.voiceinteraction.VoiceInteractionSessionConnection.1
            public void onFailed() throws RemoteException {
                synchronized (VoiceInteractionSessionConnection.this.mLock) {
                    VoiceInteractionSessionConnection.this.notifyPendingShowCallbacksFailedLocked();
                }
            }

            public void onShown() throws RemoteException {
                synchronized (VoiceInteractionSessionConnection.this.mLock) {
                    VoiceInteractionSessionConnection.this.notifyPendingShowCallbacksShownLocked();
                }
            }
        };
        this.mFullConnection = new ServiceConnection() { // from class: com.android.server.voiceinteraction.VoiceInteractionSessionConnection.2
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName2, IBinder iBinder) {
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName2) {
            }
        };
        this.mShowAssistDisclosureRunnable = new Runnable() { // from class: com.android.server.voiceinteraction.VoiceInteractionSessionConnection.3
            @Override // java.lang.Runnable
            public void run() {
                StatusBarManagerInternal statusBarManagerInternal = (StatusBarManagerInternal) LocalServices.getService(StatusBarManagerInternal.class);
                if (statusBarManagerInternal != null) {
                    statusBarManagerInternal.showAssistDisclosure();
                }
            }
        };
        this.mLock = obj;
        this.mSessionComponentName = componentName;
        this.mUser = i;
        this.mContext = context;
        this.mCallback = callback;
        this.mCallingUid = i2;
        this.mHandler = handler;
        this.mAm = ActivityManager.getService();
        UriGrantsManagerInternal uriGrantsManagerInternal = (UriGrantsManagerInternal) LocalServices.getService(UriGrantsManagerInternal.class);
        this.mUgmInternal = uriGrantsManagerInternal;
        IWindowManager asInterface = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        this.mIWindowManager = asInterface;
        this.mPowerManagerInternal = (PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class);
        this.mLowPowerStandbyControllerInternal = (LowPowerStandbyControllerInternal) LocalServices.getService(LowPowerStandbyControllerInternal.class);
        this.mAppOps = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        this.mFgHandler = FgThread.getHandler();
        this.mAssistDataRequester = new AssistDataRequester(context, asInterface, (AppOpsManager) context.getSystemService("appops"), this, obj, 49, 50);
        this.mPermissionOwner = uriGrantsManagerInternal.newUriPermissionOwner("voicesession:" + componentName.flattenToShortString());
        Intent intent = new Intent("android.service.voice.VoiceInteractionService");
        this.mBindIntent = intent;
        intent.setComponent(componentName);
        boolean bindServiceAsUser = context.bindServiceAsUser(intent, this, 1048625, new UserHandle(i));
        this.mBound = bindServiceAsUser;
        if (bindServiceAsUser) {
            try {
                asInterface.addWindowToken(binder, 2031, 0, (Bundle) null);
                return;
            } catch (RemoteException e) {
                Slog.w("VoiceInteractionServiceManager", "Failed adding window token", e);
                return;
            }
        }
        Slog.w("VoiceInteractionServiceManager", "Failed binding to voice interaction session service " + componentName);
    }

    public int getUserDisabledShowContextLocked() {
        int i = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "assist_structure_enabled", 1, this.mUser) == 0 ? 1 : 0;
        return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "assist_screenshot_enabled", 1, this.mUser) == 0 ? i | 2 : i;
    }

    public boolean showLocked(Bundle bundle, int i, String str, int i2, IVoiceInteractionSessionShowCallback iVoiceInteractionSessionShowCallback, List<ActivityAssistInfo> list) {
        if (!this.mBound) {
            if (iVoiceInteractionSessionShowCallback != null) {
                try {
                    iVoiceInteractionSessionShowCallback.onFailed();
                } catch (RemoteException unused) {
                }
            }
            return false;
        }
        if (!this.mFullyBound) {
            this.mFullyBound = this.mContext.bindServiceAsUser(this.mBindIntent, this.mFullConnection, 135790593, new UserHandle(this.mUser));
        }
        this.mShown = true;
        this.mShowArgs = bundle;
        this.mShowFlags = i;
        int userDisabledShowContextLocked = i2 | getUserDisabledShowContextLocked();
        boolean z = (i & 1) != 0;
        boolean z2 = (i & 2) != 0;
        boolean z3 = z || z2;
        if (z3) {
            int size = list.size();
            ArrayList arrayList = new ArrayList(size);
            for (int i3 = 0; i3 < size; i3++) {
                arrayList.add(list.get(i3).getActivityToken());
            }
            this.mAssistDataRequester.requestAssistData(arrayList, z, z2, (userDisabledShowContextLocked & 1) == 0, (userDisabledShowContextLocked & 2) == 0, this.mCallingUid, this.mSessionComponentName.getPackageName(), str);
            if ((this.mAssistDataRequester.getPendingDataCount() > 0 || this.mAssistDataRequester.getPendingScreenshotCount() > 0) && AssistUtils.shouldDisclose(this.mContext, this.mSessionComponentName)) {
                this.mHandler.post(this.mShowAssistDisclosureRunnable);
            }
        }
        IVoiceInteractionSession iVoiceInteractionSession = this.mSession;
        if (iVoiceInteractionSession != null) {
            try {
                iVoiceInteractionSession.show(this.mShowArgs, this.mShowFlags, iVoiceInteractionSessionShowCallback);
                this.mShowArgs = null;
                this.mShowFlags = 0;
            } catch (RemoteException unused2) {
            }
            if (z3) {
                this.mAssistDataRequester.processPendingAssistData();
            } else {
                doHandleAssistWithoutData(list);
            }
        } else {
            if (iVoiceInteractionSessionShowCallback != null) {
                this.mPendingShowCallbacks.add(iVoiceInteractionSessionShowCallback);
            }
            if (!z3) {
                this.mPendingHandleAssistWithoutData = list;
            }
        }
        PowerBoostSetter powerBoostSetter = this.mSetPowerBoostRunnable;
        if (powerBoostSetter != null) {
            powerBoostSetter.cancel();
        }
        PowerBoostSetter powerBoostSetter2 = new PowerBoostSetter(Instant.now().plusMillis(10000L));
        this.mSetPowerBoostRunnable = powerBoostSetter2;
        this.mFgHandler.post(powerBoostSetter2);
        LowPowerStandbyControllerInternal lowPowerStandbyControllerInternal = this.mLowPowerStandbyControllerInternal;
        if (lowPowerStandbyControllerInternal != null) {
            lowPowerStandbyControllerInternal.addToAllowlist(this.mCallingUid, 1);
            this.mLowPowerStandbyAllowlisted = true;
            this.mFgHandler.removeCallbacks(this.mRemoveFromLowPowerStandbyAllowlistRunnable);
            this.mFgHandler.postDelayed(this.mRemoveFromLowPowerStandbyAllowlistRunnable, 120000L);
        }
        this.mCallback.onSessionShown(this);
        return true;
    }

    public final void doHandleAssistWithoutData(List<ActivityAssistInfo> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            ActivityAssistInfo activityAssistInfo = list.get(i);
            try {
                this.mSession.handleAssist(activityAssistInfo.getTaskId(), activityAssistInfo.getAssistToken(), (Bundle) null, (AssistStructure) null, (AssistContent) null, i, size);
            } catch (RemoteException unused) {
            }
        }
    }

    @Override // com.android.server.p006am.AssistDataRequester.AssistDataRequesterCallbacks
    public boolean canHandleReceivedAssistDataLocked() {
        return this.mSession != null;
    }

    @Override // com.android.server.p006am.AssistDataRequester.AssistDataRequesterCallbacks
    public void onAssistDataReceivedLocked(Bundle bundle, int i, int i2) {
        ClipData clipData;
        IVoiceInteractionSession iVoiceInteractionSession = this.mSession;
        if (iVoiceInteractionSession == null) {
            return;
        }
        try {
            if (bundle == null) {
                iVoiceInteractionSession.handleAssist(-1, (IBinder) null, (Bundle) null, (AssistStructure) null, (AssistContent) null, 0, 0);
            } else {
                int i3 = bundle.getInt("taskId");
                IBinder binder = bundle.getBinder("activityId");
                Bundle bundle2 = bundle.getBundle("data");
                AssistStructure assistStructure = (AssistStructure) bundle.getParcelable("structure", AssistStructure.class);
                AssistContent assistContent = (AssistContent) bundle.getParcelable("content", AssistContent.class);
                int i4 = bundle2 != null ? bundle2.getInt("android.intent.extra.ASSIST_UID", -1) : -1;
                if (i4 >= 0 && assistContent != null) {
                    Intent intent = assistContent.getIntent();
                    if (intent != null && (clipData = intent.getClipData()) != null && Intent.isAccessUriMode(intent.getFlags())) {
                        grantClipDataPermissions(clipData, intent.getFlags(), i4, this.mCallingUid, this.mSessionComponentName.getPackageName());
                    }
                    ClipData clipData2 = assistContent.getClipData();
                    if (clipData2 != null) {
                        grantClipDataPermissions(clipData2, 1, i4, this.mCallingUid, this.mSessionComponentName.getPackageName());
                    }
                }
                this.mSession.handleAssist(i3, binder, bundle2, assistStructure, assistContent, i, i2);
            }
        } catch (RemoteException unused) {
        }
    }

    @Override // com.android.server.p006am.AssistDataRequester.AssistDataRequesterCallbacks
    public void onAssistScreenshotReceivedLocked(Bitmap bitmap) {
        IVoiceInteractionSession iVoiceInteractionSession = this.mSession;
        if (iVoiceInteractionSession == null) {
            return;
        }
        try {
            iVoiceInteractionSession.handleScreenshot(bitmap);
        } catch (RemoteException unused) {
        }
    }

    public void grantUriPermission(Uri uri, int i, int i2, int i3, String str) {
        if ("content".equals(uri.getScheme())) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                try {
                    this.mUgmInternal.checkGrantUriPermission(i2, null, ContentProvider.getUriWithoutUserId(uri), i, ContentProvider.getUserIdFromUri(uri, UserHandle.getUserId(i2)));
                    int userIdFromUri = ContentProvider.getUserIdFromUri(uri, this.mUser);
                    UriGrantsManager.getService().grantUriPermissionFromOwner(this.mPermissionOwner, i2, str, ContentProvider.getUriWithoutUserId(uri), 1, userIdFromUri, this.mUser);
                } catch (RemoteException unused) {
                } catch (SecurityException e) {
                    Slog.w("VoiceInteractionServiceManager", "Can't propagate permission", e);
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public void grantClipDataItemPermission(ClipData.Item item, int i, int i2, int i3, String str) {
        if (item.getUri() != null) {
            grantUriPermission(item.getUri(), i, i2, i3, str);
        }
        Intent intent = item.getIntent();
        if (intent == null || intent.getData() == null) {
            return;
        }
        grantUriPermission(intent.getData(), i, i2, i3, str);
    }

    public void grantClipDataPermissions(ClipData clipData, int i, int i2, int i3, String str) {
        int itemCount = clipData.getItemCount();
        for (int i4 = 0; i4 < itemCount; i4++) {
            grantClipDataItemPermission(clipData.getItemAt(i4), i, i2, i3, str);
        }
    }

    public boolean hideLocked() {
        if (this.mBound) {
            if (this.mShown) {
                this.mShown = false;
                this.mShowArgs = null;
                this.mShowFlags = 0;
                this.mAssistDataRequester.cancel();
                this.mPendingShowCallbacks.clear();
                IVoiceInteractionSession iVoiceInteractionSession = this.mSession;
                if (iVoiceInteractionSession != null) {
                    try {
                        iVoiceInteractionSession.hide();
                    } catch (RemoteException unused) {
                    }
                }
                this.mUgmInternal.revokeUriPermissionFromOwner(this.mPermissionOwner, null, 3, this.mUser);
                if (this.mSession != null) {
                    try {
                        ActivityTaskManager.getService().finishVoiceTask(this.mSession);
                    } catch (RemoteException unused2) {
                    }
                }
                PowerBoostSetter powerBoostSetter = this.mSetPowerBoostRunnable;
                if (powerBoostSetter != null) {
                    powerBoostSetter.cancel();
                    this.mSetPowerBoostRunnable = null;
                }
                this.mPowerManagerInternal.setPowerBoost(0, -1);
                if (this.mLowPowerStandbyControllerInternal != null) {
                    removeFromLowPowerStandbyAllowlist();
                }
                this.mCallback.onSessionHidden(this);
            }
            if (this.mFullyBound) {
                this.mContext.unbindService(this.mFullConnection);
                this.mFullyBound = false;
                return true;
            }
            return true;
        }
        return false;
    }

    public void cancelLocked(boolean z) {
        this.mListeningVisibleActivity = false;
        this.mVisibleActivityInfoForToken.clear();
        hideLocked();
        this.mCanceled = true;
        if (this.mBound) {
            IVoiceInteractionSession iVoiceInteractionSession = this.mSession;
            if (iVoiceInteractionSession != null) {
                try {
                    iVoiceInteractionSession.destroy();
                } catch (RemoteException unused) {
                    Slog.w("VoiceInteractionServiceManager", "Voice interation session already dead");
                }
            }
            if (z && this.mSession != null) {
                try {
                    ActivityTaskManager.getService().finishVoiceTask(this.mSession);
                } catch (RemoteException unused2) {
                }
            }
            this.mContext.unbindService(this);
            try {
                this.mIWindowManager.removeWindowToken(this.mToken, 0);
            } catch (RemoteException e) {
                Slog.w("VoiceInteractionServiceManager", "Failed removing window token", e);
            }
            this.mBound = false;
            this.mService = null;
            this.mSession = null;
            this.mInteractor = null;
        }
        if (this.mFullyBound) {
            this.mContext.unbindService(this.mFullConnection);
            this.mFullyBound = false;
        }
    }

    public boolean deliverNewSessionLocked(IVoiceInteractionSession iVoiceInteractionSession, IVoiceInteractor iVoiceInteractor) {
        this.mSession = iVoiceInteractionSession;
        this.mInteractor = iVoiceInteractor;
        if (this.mShown) {
            try {
                iVoiceInteractionSession.show(this.mShowArgs, this.mShowFlags, this.mShowCallback);
                this.mShowArgs = null;
                this.mShowFlags = 0;
            } catch (RemoteException unused) {
            }
            this.mAssistDataRequester.processPendingAssistData();
            if (this.mPendingHandleAssistWithoutData.isEmpty()) {
                return true;
            }
            doHandleAssistWithoutData(this.mPendingHandleAssistWithoutData);
            this.mPendingHandleAssistWithoutData.clear();
            return true;
        }
        return true;
    }

    public final void notifyPendingShowCallbacksShownLocked() {
        for (int i = 0; i < this.mPendingShowCallbacks.size(); i++) {
            try {
                this.mPendingShowCallbacks.get(i).onShown();
            } catch (RemoteException unused) {
            }
        }
        this.mPendingShowCallbacks.clear();
    }

    public final void notifyPendingShowCallbacksFailedLocked() {
        for (int i = 0; i < this.mPendingShowCallbacks.size(); i++) {
            try {
                this.mPendingShowCallbacks.get(i).onFailed();
            } catch (RemoteException unused) {
            }
        }
        this.mPendingShowCallbacks.clear();
    }

    public void startListeningVisibleActivityChangedLocked() {
        if (!this.mShown || this.mCanceled || this.mSession == null) {
            return;
        }
        this.mListeningVisibleActivity = true;
        this.mVisibleActivityInfoForToken.clear();
        ArrayMap<IBinder, VisibleActivityInfo> topVisibleActivityInfosLocked = getTopVisibleActivityInfosLocked();
        if (topVisibleActivityInfosLocked == null || topVisibleActivityInfosLocked.isEmpty()) {
            return;
        }
        notifyVisibleActivitiesChangedLocked(topVisibleActivityInfosLocked, 1);
        this.mVisibleActivityInfoForToken.putAll((ArrayMap<? extends IBinder, ? extends VisibleActivityInfo>) topVisibleActivityInfosLocked);
    }

    public void stopListeningVisibleActivityChangedLocked() {
        this.mListeningVisibleActivity = false;
        this.mVisibleActivityInfoForToken.clear();
    }

    public void notifyActivityEventChangedLocked(final IBinder iBinder, final int i) {
        if (this.mListeningVisibleActivity) {
            this.mScheduledExecutorService.execute(new Runnable() { // from class: com.android.server.voiceinteraction.VoiceInteractionSessionConnection$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    VoiceInteractionSessionConnection.this.lambda$notifyActivityEventChangedLocked$0(iBinder, i);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyActivityEventChangedLocked$0(IBinder iBinder, int i) {
        synchronized (this.mLock) {
            handleVisibleActivitiesLocked(iBinder, i);
        }
    }

    public final ArrayMap<IBinder, VisibleActivityInfo> getTopVisibleActivityInfosLocked() {
        List<ActivityAssistInfo> topVisibleActivities = ((ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class)).getTopVisibleActivities();
        if (topVisibleActivities.isEmpty()) {
            Slog.w("VoiceInteractionServiceManager", "no visible activity");
            return null;
        }
        int size = topVisibleActivities.size();
        ArrayMap<IBinder, VisibleActivityInfo> arrayMap = new ArrayMap<>(size);
        for (int i = 0; i < size; i++) {
            ActivityAssistInfo activityAssistInfo = topVisibleActivities.get(i);
            arrayMap.put(activityAssistInfo.getActivityToken(), new VisibleActivityInfo(activityAssistInfo.getTaskId(), activityAssistInfo.getAssistToken()));
        }
        return arrayMap;
    }

    public final void handleVisibleActivitiesLocked(IBinder iBinder, int i) {
        VisibleActivityInfo visibleActivityInfoFromTopVisibleActivity;
        boolean z;
        if (this.mListeningVisibleActivity && this.mShown && !this.mCanceled && this.mSession != null) {
            if (i != 1 && i != 2) {
                z = false;
                if (i == 3) {
                    if (getVisibleActivityInfoFromTopVisibleActivity(iBinder) != null || (visibleActivityInfoFromTopVisibleActivity = this.mVisibleActivityInfoForToken.get(iBinder)) == null) {
                        return;
                    }
                } else if (i == 4) {
                    visibleActivityInfoFromTopVisibleActivity = this.mVisibleActivityInfoForToken.get(iBinder);
                    if (visibleActivityInfoFromTopVisibleActivity == null) {
                        return;
                    }
                } else {
                    Slog.w("VoiceInteractionServiceManager", "notifyActivityEventChangedLocked unexpected type=" + i);
                    return;
                }
            } else if (this.mVisibleActivityInfoForToken.containsKey(iBinder) || (visibleActivityInfoFromTopVisibleActivity = getVisibleActivityInfoFromTopVisibleActivity(iBinder)) == null) {
                return;
            } else {
                z = true;
            }
            try {
                this.mSession.notifyVisibleActivityInfoChanged(visibleActivityInfoFromTopVisibleActivity, z ? 1 : 2);
            } catch (RemoteException unused) {
            }
            if (z) {
                this.mVisibleActivityInfoForToken.put(iBinder, visibleActivityInfoFromTopVisibleActivity);
            } else {
                this.mVisibleActivityInfoForToken.remove(iBinder);
            }
        }
    }

    public final void notifyVisibleActivitiesChangedLocked(ArrayMap<IBinder, VisibleActivityInfo> arrayMap, int i) {
        if (arrayMap == null || arrayMap.isEmpty() || this.mSession == null) {
            return;
        }
        for (int i2 = 0; i2 < arrayMap.size(); i2++) {
            try {
                this.mSession.notifyVisibleActivityInfoChanged(arrayMap.valueAt(i2), i);
            } catch (RemoteException unused) {
                return;
            }
        }
    }

    public final VisibleActivityInfo getVisibleActivityInfoFromTopVisibleActivity(IBinder iBinder) {
        ArrayMap<IBinder, VisibleActivityInfo> topVisibleActivityInfosLocked = getTopVisibleActivityInfosLocked();
        if (topVisibleActivityInfosLocked == null) {
            return null;
        }
        return topVisibleActivityInfosLocked.get(iBinder);
    }

    public void notifyActivityDestroyedLocked(final IBinder iBinder) {
        if (this.mListeningVisibleActivity) {
            this.mScheduledExecutorService.execute(new Runnable() { // from class: com.android.server.voiceinteraction.VoiceInteractionSessionConnection$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    VoiceInteractionSessionConnection.this.lambda$notifyActivityDestroyedLocked$1(iBinder);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyActivityDestroyedLocked$1(IBinder iBinder) {
        synchronized (this.mLock) {
            if (this.mListeningVisibleActivity) {
                if (this.mShown && !this.mCanceled && this.mSession != null) {
                    VisibleActivityInfo remove = this.mVisibleActivityInfoForToken.remove(iBinder);
                    if (remove != null) {
                        try {
                            this.mSession.notifyVisibleActivityInfoChanged(remove, 2);
                        } catch (RemoteException unused) {
                        }
                    }
                }
            }
        }
    }

    public final void removeFromLowPowerStandbyAllowlist() {
        synchronized (this.mLock) {
            if (this.mLowPowerStandbyAllowlisted) {
                this.mFgHandler.removeCallbacks(this.mRemoveFromLowPowerStandbyAllowlistRunnable);
                this.mLowPowerStandbyControllerInternal.removeFromAllowlist(this.mCallingUid, 1);
                this.mLowPowerStandbyAllowlisted = false;
            }
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        synchronized (this.mLock) {
            IVoiceInteractionSessionService asInterface = IVoiceInteractionSessionService.Stub.asInterface(iBinder);
            this.mService = asInterface;
            if (!this.mCanceled) {
                try {
                    asInterface.newSession(this.mToken, this.mShowArgs, this.mShowFlags);
                } catch (RemoteException e) {
                    Slog.w("VoiceInteractionServiceManager", "Failed adding window token", e);
                }
            }
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName componentName) {
        this.mCallback.sessionConnectionGone(this);
        synchronized (this.mLock) {
            this.mService = null;
        }
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.print(str);
        printWriter.print("mToken=");
        printWriter.println(this.mToken);
        printWriter.print(str);
        printWriter.print("mShown=");
        printWriter.println(this.mShown);
        printWriter.print(str);
        printWriter.print("mShowArgs=");
        printWriter.println(this.mShowArgs);
        printWriter.print(str);
        printWriter.print("mShowFlags=0x");
        printWriter.println(Integer.toHexString(this.mShowFlags));
        printWriter.print(str);
        printWriter.print("mBound=");
        printWriter.println(this.mBound);
        if (this.mBound) {
            printWriter.print(str);
            printWriter.print("mService=");
            printWriter.println(this.mService);
            printWriter.print(str);
            printWriter.print("mSession=");
            printWriter.println(this.mSession);
            printWriter.print(str);
            printWriter.print("mInteractor=");
            printWriter.println(this.mInteractor);
        }
        this.mAssistDataRequester.dump(str, printWriter);
    }
}
