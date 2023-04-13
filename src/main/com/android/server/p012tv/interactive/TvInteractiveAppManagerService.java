package com.android.server.p012tv.interactive;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.graphics.Rect;
import android.media.PlaybackParams;
import android.media.tv.AdBuffer;
import android.media.tv.AdRequest;
import android.media.tv.AdResponse;
import android.media.tv.BroadcastInfoRequest;
import android.media.tv.BroadcastInfoResponse;
import android.media.tv.TvRecordingInfo;
import android.media.tv.TvTrackInfo;
import android.media.tv.interactive.AppLinkInfo;
import android.media.tv.interactive.ITvInteractiveAppClient;
import android.media.tv.interactive.ITvInteractiveAppManager;
import android.media.tv.interactive.ITvInteractiveAppManagerCallback;
import android.media.tv.interactive.ITvInteractiveAppService;
import android.media.tv.interactive.ITvInteractiveAppServiceCallback;
import android.media.tv.interactive.ITvInteractiveAppSession;
import android.media.tv.interactive.ITvInteractiveAppSessionCallback;
import android.media.tv.interactive.TvInteractiveAppServiceInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArrayMap;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.view.InputChannel;
import android.view.Surface;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.PackageMonitor;
import com.android.server.SystemService;
import com.android.server.utils.Slogf;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
/* renamed from: com.android.server.tv.interactive.TvInteractiveAppManagerService */
/* loaded from: classes2.dex */
public class TvInteractiveAppManagerService extends SystemService {
    public final Context mContext;
    @GuardedBy({"mLock"})
    public int mCurrentUserId;
    @GuardedBy({"mLock"})
    public boolean mGetAppLinkInfoListCalled;
    @GuardedBy({"mLock"})
    public boolean mGetServiceListCalled;
    public final Object mLock;
    @GuardedBy({"mLock"})
    public final Set<Integer> mRunningProfiles;
    public final UserManager mUserManager;
    @GuardedBy({"mLock"})
    public final SparseArray<UserState> mUserStates;

    public TvInteractiveAppManagerService(Context context) {
        super(context);
        this.mLock = new Object();
        this.mCurrentUserId = 0;
        this.mRunningProfiles = new HashSet();
        this.mUserStates = new SparseArray<>();
        this.mGetServiceListCalled = false;
        this.mGetAppLinkInfoListCalled = false;
        this.mContext = context;
        this.mUserManager = (UserManager) getContext().getSystemService("user");
    }

    @GuardedBy({"mLock"})
    public final void buildAppLinkInfoLocked(int i) {
        UserState orCreateUserStateLocked = getOrCreateUserStateLocked(i);
        List<ApplicationInfo> installedApplicationsAsUser = this.mContext.getPackageManager().getInstalledApplicationsAsUser(PackageManager.ApplicationInfoFlags.of(128L), i);
        ArrayList arrayList = new ArrayList();
        for (ApplicationInfo applicationInfo : installedApplicationsAsUser) {
            AppLinkInfo buildAppLinkInfoLocked = buildAppLinkInfoLocked(applicationInfo);
            if (buildAppLinkInfoLocked != null) {
                arrayList.add(buildAppLinkInfoLocked);
            }
        }
        Collections.sort(arrayList, Comparator.comparing(new Function() { // from class: com.android.server.tv.interactive.TvInteractiveAppManagerService$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((AppLinkInfo) obj).getComponentName();
            }
        }));
        orCreateUserStateLocked.mAppLinkInfoList.clear();
        orCreateUserStateLocked.mAppLinkInfoList.addAll(arrayList);
    }

    @GuardedBy({"mLock"})
    public final AppLinkInfo buildAppLinkInfoLocked(ApplicationInfo applicationInfo) {
        Bundle bundle = applicationInfo.metaData;
        if (bundle == null || applicationInfo.packageName == null) {
            return null;
        }
        String string = bundle.getString("android.media.tv.interactive.AppLinkInfo.ClassName", null);
        String string2 = applicationInfo.metaData.getString("android.media.tv.interactive.AppLinkInfo.Uri", null);
        if (string == null || string2 == null) {
            return null;
        }
        return new AppLinkInfo(applicationInfo.packageName, string, string2);
    }

    @GuardedBy({"mLock"})
    public final void buildTvInteractiveAppServiceListLocked(int i, String[] strArr) {
        UserState orCreateUserStateLocked = getOrCreateUserStateLocked(i);
        orCreateUserStateLocked.mPackageSet.clear();
        List<ResolveInfo> queryIntentServicesAsUser = this.mContext.getPackageManager().queryIntentServicesAsUser(new Intent("android.media.tv.interactive.TvInteractiveAppService"), 132, i);
        ArrayList<TvInteractiveAppServiceInfo> arrayList = new ArrayList();
        for (ResolveInfo resolveInfo : queryIntentServicesAsUser) {
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if ("android.permission.BIND_TV_INTERACTIVE_APP".equals(serviceInfo.permission)) {
                try {
                    arrayList.add(new TvInteractiveAppServiceInfo(this.mContext, new ComponentName(serviceInfo.packageName, serviceInfo.name)));
                    orCreateUserStateLocked.mPackageSet.add(serviceInfo.packageName);
                } catch (Exception e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "failed to load TV Interactive App service " + serviceInfo.name, e);
                }
            } else {
                Slog.w("TvInteractiveAppManagerService", "Skipping TV interactiva app service " + serviceInfo.name + ": it does not require the permission android.permission.BIND_TV_INTERACTIVE_APP");
            }
        }
        Collections.sort(arrayList, Comparator.comparing(new Function() { // from class: com.android.server.tv.interactive.TvInteractiveAppManagerService$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((TvInteractiveAppServiceInfo) obj).getId();
            }
        }));
        HashMap hashMap = new HashMap();
        ArrayMap arrayMap = new ArrayMap(hashMap.size());
        for (TvInteractiveAppServiceInfo tvInteractiveAppServiceInfo : arrayList) {
            String id = tvInteractiveAppServiceInfo.getId();
            Integer num = (Integer) arrayMap.get(id);
            Integer valueOf = Integer.valueOf(num != null ? 1 + num.intValue() : 1);
            arrayMap.put(id, valueOf);
            TvInteractiveAppState tvInteractiveAppState = (TvInteractiveAppState) orCreateUserStateLocked.mIAppMap.get(id);
            if (tvInteractiveAppState == null) {
                tvInteractiveAppState = new TvInteractiveAppState();
            }
            tvInteractiveAppState.mInfo = tvInteractiveAppServiceInfo;
            tvInteractiveAppState.mUid = getInteractiveAppUid(tvInteractiveAppServiceInfo);
            tvInteractiveAppState.mComponentName = tvInteractiveAppServiceInfo.getComponent();
            hashMap.put(id, tvInteractiveAppState);
            tvInteractiveAppState.mIAppNumber = valueOf.intValue();
        }
        for (String str : hashMap.keySet()) {
            if (!orCreateUserStateLocked.mIAppMap.containsKey(str)) {
                notifyInteractiveAppServiceAddedLocked(orCreateUserStateLocked, str);
            } else if (strArr != null) {
                ComponentName component = ((TvInteractiveAppState) hashMap.get(str)).mInfo.getComponent();
                int length = strArr.length;
                int i2 = 0;
                while (true) {
                    if (i2 < length) {
                        if (component.getPackageName().equals(strArr[i2])) {
                            updateServiceConnectionLocked(component, i);
                            notifyInteractiveAppServiceUpdatedLocked(orCreateUserStateLocked, str);
                            break;
                        }
                        i2++;
                    }
                }
            }
        }
        for (String str2 : orCreateUserStateLocked.mIAppMap.keySet()) {
            if (!hashMap.containsKey(str2)) {
                ServiceState serviceState = (ServiceState) orCreateUserStateLocked.mServiceStateMap.get(((TvInteractiveAppState) orCreateUserStateLocked.mIAppMap.get(str2)).mInfo.getComponent());
                if (serviceState != null) {
                    abortPendingCreateSessionRequestsLocked(serviceState, str2, i);
                }
                notifyInteractiveAppServiceRemovedLocked(orCreateUserStateLocked, str2);
            }
        }
        orCreateUserStateLocked.mIAppMap.clear();
        orCreateUserStateLocked.mIAppMap = hashMap;
    }

    @GuardedBy({"mLock"})
    public final void notifyInteractiveAppServiceAddedLocked(UserState userState, String str) {
        int beginBroadcast = userState.mCallbacks.beginBroadcast();
        for (int i = 0; i < beginBroadcast; i++) {
            try {
                userState.mCallbacks.getBroadcastItem(i).onInteractiveAppServiceAdded(str);
            } catch (RemoteException e) {
                Slog.e("TvInteractiveAppManagerService", "failed to report added Interactive App service to callback", e);
            }
        }
        userState.mCallbacks.finishBroadcast();
    }

    @GuardedBy({"mLock"})
    public final void notifyInteractiveAppServiceRemovedLocked(UserState userState, String str) {
        int beginBroadcast = userState.mCallbacks.beginBroadcast();
        for (int i = 0; i < beginBroadcast; i++) {
            try {
                userState.mCallbacks.getBroadcastItem(i).onInteractiveAppServiceRemoved(str);
            } catch (RemoteException e) {
                Slog.e("TvInteractiveAppManagerService", "failed to report removed Interactive App service to callback", e);
            }
        }
        userState.mCallbacks.finishBroadcast();
    }

    @GuardedBy({"mLock"})
    public final void notifyInteractiveAppServiceUpdatedLocked(UserState userState, String str) {
        int beginBroadcast = userState.mCallbacks.beginBroadcast();
        for (int i = 0; i < beginBroadcast; i++) {
            try {
                userState.mCallbacks.getBroadcastItem(i).onInteractiveAppServiceUpdated(str);
            } catch (RemoteException e) {
                Slog.e("TvInteractiveAppManagerService", "failed to report updated Interactive App service to callback", e);
            }
        }
        userState.mCallbacks.finishBroadcast();
    }

    @GuardedBy({"mLock"})
    public final void notifyStateChangedLocked(UserState userState, String str, int i, int i2, int i3) {
        int beginBroadcast = userState.mCallbacks.beginBroadcast();
        for (int i4 = 0; i4 < beginBroadcast; i4++) {
            try {
                userState.mCallbacks.getBroadcastItem(i4).onStateChanged(str, i, i2, i3);
            } catch (RemoteException e) {
                Slog.e("TvInteractiveAppManagerService", "failed to report RTE state changed", e);
            }
        }
        userState.mCallbacks.finishBroadcast();
    }

    public final int getInteractiveAppUid(TvInteractiveAppServiceInfo tvInteractiveAppServiceInfo) {
        try {
            return getContext().getPackageManager().getApplicationInfo(tvInteractiveAppServiceInfo.getServiceInfo().packageName, 0).uid;
        } catch (PackageManager.NameNotFoundException e) {
            Slogf.m13w("TvInteractiveAppManagerService", "Unable to get UID for  " + tvInteractiveAppServiceInfo, e);
            return -1;
        }
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("tv_interactive_app", new BinderService());
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 500) {
            registerBroadcastReceivers();
        } else if (i == 600) {
            synchronized (this.mLock) {
                buildTvInteractiveAppServiceListLocked(this.mCurrentUserId, null);
                buildAppLinkInfoLocked(this.mCurrentUserId);
            }
        }
    }

    public final void registerBroadcastReceivers() {
        new PackageMonitor() { // from class: com.android.server.tv.interactive.TvInteractiveAppManagerService.1
            public boolean onPackageChanged(String str, int i, String[] strArr) {
                return true;
            }

            public final void buildTvInteractiveAppServiceList(String[] strArr) {
                int changingUserId = getChangingUserId();
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    if (TvInteractiveAppManagerService.this.mCurrentUserId == changingUserId || TvInteractiveAppManagerService.this.mRunningProfiles.contains(Integer.valueOf(changingUserId))) {
                        TvInteractiveAppManagerService.this.buildTvInteractiveAppServiceListLocked(changingUserId, strArr);
                        TvInteractiveAppManagerService.this.buildAppLinkInfoLocked(changingUserId);
                    }
                }
            }

            public void onPackageUpdateFinished(String str, int i) {
                buildTvInteractiveAppServiceList(new String[]{str});
            }

            public void onPackagesAvailable(String[] strArr) {
                if (isReplacing()) {
                    buildTvInteractiveAppServiceList(strArr);
                }
            }

            public void onPackagesUnavailable(String[] strArr) {
                if (isReplacing()) {
                    buildTvInteractiveAppServiceList(strArr);
                }
            }

            public void onSomePackagesChanged() {
                if (isReplacing()) {
                    return;
                }
                buildTvInteractiveAppServiceList(null);
            }
        }.register(this.mContext, (Looper) null, UserHandle.ALL, true);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        intentFilter.addAction("android.intent.action.USER_REMOVED");
        intentFilter.addAction("android.intent.action.USER_STARTED");
        intentFilter.addAction("android.intent.action.USER_STOPPED");
        this.mContext.registerReceiverAsUser(new BroadcastReceiver() { // from class: com.android.server.tv.interactive.TvInteractiveAppManagerService.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("android.intent.action.USER_SWITCHED".equals(action)) {
                    TvInteractiveAppManagerService.this.switchUser(intent.getIntExtra("android.intent.extra.user_handle", 0));
                } else if ("android.intent.action.USER_REMOVED".equals(action)) {
                    TvInteractiveAppManagerService.this.removeUser(intent.getIntExtra("android.intent.extra.user_handle", 0));
                } else if ("android.intent.action.USER_STARTED".equals(action)) {
                    TvInteractiveAppManagerService.this.startUser(intent.getIntExtra("android.intent.extra.user_handle", 0));
                } else if ("android.intent.action.USER_STOPPED".equals(action)) {
                    TvInteractiveAppManagerService.this.stopUser(intent.getIntExtra("android.intent.extra.user_handle", 0));
                }
            }
        }, UserHandle.ALL, intentFilter, null, null);
    }

    public final void switchUser(int i) {
        synchronized (this.mLock) {
            if (this.mCurrentUserId == i) {
                return;
            }
            if (this.mUserManager.getUserInfo(i).isProfile()) {
                Slog.w("TvInteractiveAppManagerService", "cannot switch to a profile!");
                return;
            }
            for (Integer num : this.mRunningProfiles) {
                int intValue = num.intValue();
                releaseSessionOfUserLocked(intValue);
                unbindServiceOfUserLocked(intValue);
            }
            this.mRunningProfiles.clear();
            releaseSessionOfUserLocked(this.mCurrentUserId);
            unbindServiceOfUserLocked(this.mCurrentUserId);
            this.mCurrentUserId = i;
            buildTvInteractiveAppServiceListLocked(i, null);
            buildAppLinkInfoLocked(i);
        }
    }

    public final void removeUser(int i) {
        synchronized (this.mLock) {
            UserState userStateLocked = getUserStateLocked(i);
            if (userStateLocked == null) {
                return;
            }
            for (SessionState sessionState : userStateLocked.mSessionStateMap.values()) {
                if (sessionState.mSession != null) {
                    try {
                        sessionState.mSession.release();
                    } catch (RemoteException e) {
                        Slog.e("TvInteractiveAppManagerService", "error in release", e);
                    }
                }
            }
            userStateLocked.mSessionStateMap.clear();
            for (ServiceState serviceState : userStateLocked.mServiceStateMap.values()) {
                if (serviceState.mService != null) {
                    if (serviceState.mCallback != null) {
                        try {
                            serviceState.mService.unregisterCallback(serviceState.mCallback);
                        } catch (RemoteException e2) {
                            Slog.e("TvInteractiveAppManagerService", "error in unregisterCallback", e2);
                        }
                    }
                    this.mContext.unbindService(serviceState.mConnection);
                }
            }
            userStateLocked.mServiceStateMap.clear();
            userStateLocked.mIAppMap.clear();
            userStateLocked.mPackageSet.clear();
            userStateLocked.mClientStateMap.clear();
            userStateLocked.mCallbacks.kill();
            this.mRunningProfiles.remove(Integer.valueOf(i));
            this.mUserStates.remove(i);
            if (i == this.mCurrentUserId) {
                switchUser(0);
            }
        }
    }

    public final void startUser(int i) {
        synchronized (this.mLock) {
            if (i != this.mCurrentUserId && !this.mRunningProfiles.contains(Integer.valueOf(i))) {
                UserInfo userInfo = this.mUserManager.getUserInfo(i);
                UserInfo profileParent = this.mUserManager.getProfileParent(i);
                if (userInfo.isProfile() && profileParent != null && profileParent.id == this.mCurrentUserId) {
                    startProfileLocked(i);
                }
            }
        }
    }

    public final void stopUser(int i) {
        synchronized (this.mLock) {
            if (i == this.mCurrentUserId) {
                switchUser(ActivityManager.getCurrentUser());
                return;
            }
            releaseSessionOfUserLocked(i);
            unbindServiceOfUserLocked(i);
            this.mRunningProfiles.remove(Integer.valueOf(i));
        }
    }

    @GuardedBy({"mLock"})
    public final void startProfileLocked(int i) {
        this.mRunningProfiles.add(Integer.valueOf(i));
        buildTvInteractiveAppServiceListLocked(i, null);
        buildAppLinkInfoLocked(i);
    }

    @GuardedBy({"mLock"})
    public final void releaseSessionOfUserLocked(int i) {
        UserState userStateLocked = getUserStateLocked(i);
        if (userStateLocked == null) {
            return;
        }
        ArrayList<SessionState> arrayList = new ArrayList();
        for (SessionState sessionState : userStateLocked.mSessionStateMap.values()) {
            if (sessionState.mSession != null) {
                arrayList.add(sessionState);
            }
        }
        for (SessionState sessionState2 : arrayList) {
            try {
                sessionState2.mSession.release();
            } catch (RemoteException e) {
                Slog.e("TvInteractiveAppManagerService", "error in release", e);
            }
            clearSessionAndNotifyClientLocked(sessionState2);
        }
    }

    @GuardedBy({"mLock"})
    public final void unbindServiceOfUserLocked(int i) {
        UserState userStateLocked = getUserStateLocked(i);
        if (userStateLocked == null) {
            return;
        }
        Iterator it = userStateLocked.mServiceStateMap.keySet().iterator();
        while (it.hasNext()) {
            ServiceState serviceState = (ServiceState) userStateLocked.mServiceStateMap.get((ComponentName) it.next());
            if (serviceState != null && serviceState.mSessionTokens.isEmpty()) {
                if (serviceState.mCallback != null) {
                    try {
                        serviceState.mService.unregisterCallback(serviceState.mCallback);
                    } catch (RemoteException e) {
                        Slog.e("TvInteractiveAppManagerService", "error in unregisterCallback", e);
                    }
                }
                this.mContext.unbindService(serviceState.mConnection);
                it.remove();
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void clearSessionAndNotifyClientLocked(SessionState sessionState) {
        if (sessionState.mClient != null) {
            try {
                sessionState.mClient.onSessionReleased(sessionState.mSeq);
            } catch (RemoteException e) {
                Slog.e("TvInteractiveAppManagerService", "error in onSessionReleased", e);
            }
        }
        removeSessionStateLocked(sessionState.mSessionToken, sessionState.mUserId);
    }

    public final int resolveCallingUserId(int i, int i2, int i3, String str) {
        return ActivityManager.handleIncomingUser(i, i2, i3, false, false, str, null);
    }

    @GuardedBy({"mLock"})
    public final UserState getOrCreateUserStateLocked(int i) {
        UserState userStateLocked = getUserStateLocked(i);
        if (userStateLocked == null) {
            UserState userState = new UserState(i);
            this.mUserStates.put(i, userState);
            return userState;
        }
        return userStateLocked;
    }

    @GuardedBy({"mLock"})
    public final UserState getUserStateLocked(int i) {
        return this.mUserStates.get(i);
    }

    @GuardedBy({"mLock"})
    public final ServiceState getServiceStateLocked(ComponentName componentName, int i) {
        ServiceState serviceState = (ServiceState) getOrCreateUserStateLocked(i).mServiceStateMap.get(componentName);
        if (serviceState != null) {
            return serviceState;
        }
        throw new IllegalStateException("Service state not found for " + componentName + " (userId=" + i + ")");
    }

    @GuardedBy({"mLock"})
    public final SessionState getSessionStateLocked(IBinder iBinder, int i, int i2) {
        return getSessionStateLocked(iBinder, i, getOrCreateUserStateLocked(i2));
    }

    @GuardedBy({"mLock"})
    public final SessionState getSessionStateLocked(IBinder iBinder, int i, UserState userState) {
        SessionState sessionState = (SessionState) userState.mSessionStateMap.get(iBinder);
        if (sessionState == null) {
            throw new SessionNotFoundException("Session state not found for token " + iBinder);
        } else if (i == 1000 || i == sessionState.mCallingUid) {
            return sessionState;
        } else {
            throw new SecurityException("Illegal access to the session with token " + iBinder + " from uid " + i);
        }
    }

    @GuardedBy({"mLock"})
    public final ITvInteractiveAppSession getSessionLocked(IBinder iBinder, int i, int i2) {
        return getSessionLocked(getSessionStateLocked(iBinder, i, i2));
    }

    @GuardedBy({"mLock"})
    public final ITvInteractiveAppSession getSessionLocked(SessionState sessionState) {
        ITvInteractiveAppSession iTvInteractiveAppSession = sessionState.mSession;
        if (iTvInteractiveAppSession != null) {
            return iTvInteractiveAppSession;
        }
        throw new IllegalStateException("Session not yet created for token " + sessionState.mSessionToken);
    }

    /* renamed from: com.android.server.tv.interactive.TvInteractiveAppManagerService$BinderService */
    /* loaded from: classes2.dex */
    public final class BinderService extends ITvInteractiveAppManager.Stub {
        public BinderService() {
        }

        public List<TvInteractiveAppServiceInfo> getTvInteractiveAppServiceList(int i) {
            ArrayList arrayList;
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), Binder.getCallingUid(), i, "getTvInteractiveAppServiceList");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    if (!TvInteractiveAppManagerService.this.mGetServiceListCalled) {
                        TvInteractiveAppManagerService.this.buildTvInteractiveAppServiceListLocked(i, null);
                        TvInteractiveAppManagerService.this.mGetServiceListCalled = true;
                    }
                    UserState orCreateUserStateLocked = TvInteractiveAppManagerService.this.getOrCreateUserStateLocked(resolveCallingUserId);
                    arrayList = new ArrayList();
                    for (TvInteractiveAppState tvInteractiveAppState : orCreateUserStateLocked.mIAppMap.values()) {
                        arrayList.add(tvInteractiveAppState.mInfo);
                    }
                }
                return arrayList;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public List<AppLinkInfo> getAppLinkInfoList(int i) {
            ArrayList arrayList;
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), Binder.getCallingUid(), i, "getAppLinkInfoList");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    if (!TvInteractiveAppManagerService.this.mGetAppLinkInfoListCalled) {
                        TvInteractiveAppManagerService.this.buildAppLinkInfoLocked(i);
                        TvInteractiveAppManagerService.this.mGetAppLinkInfoListCalled = true;
                    }
                    arrayList = new ArrayList(TvInteractiveAppManagerService.this.getOrCreateUserStateLocked(resolveCallingUserId).mAppLinkInfoList);
                }
                return arrayList;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void registerAppLinkInfo(String str, AppLinkInfo appLinkInfo, int i) {
            TvInteractiveAppManagerService tvInteractiveAppManagerService = TvInteractiveAppManagerService.this;
            int callingPid = Binder.getCallingPid();
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = tvInteractiveAppManagerService.resolveCallingUserId(callingPid, callingUid, i, "registerAppLinkInfo: " + appLinkInfo);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                try {
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in registerAppLinkInfo", e);
                }
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    UserState orCreateUserStateLocked = TvInteractiveAppManagerService.this.getOrCreateUserStateLocked(resolveCallingUserId);
                    TvInteractiveAppState tvInteractiveAppState = (TvInteractiveAppState) orCreateUserStateLocked.mIAppMap.get(str);
                    if (tvInteractiveAppState == null) {
                        Slogf.m26e("TvInteractiveAppManagerService", "failed to registerAppLinkInfo - unknown TIAS id " + str);
                        return;
                    }
                    ComponentName component = tvInteractiveAppState.mInfo.getComponent();
                    ServiceState serviceState = (ServiceState) orCreateUserStateLocked.mServiceStateMap.get(component);
                    if (serviceState == null) {
                        ServiceState serviceState2 = new ServiceState(component, str, resolveCallingUserId);
                        serviceState2.addPendingAppLink(appLinkInfo, true);
                        orCreateUserStateLocked.mServiceStateMap.put(component, serviceState2);
                        TvInteractiveAppManagerService.this.updateServiceConnectionLocked(component, resolveCallingUserId);
                    } else if (serviceState.mService != null) {
                        serviceState.mService.registerAppLinkInfo(appLinkInfo);
                    } else {
                        serviceState.addPendingAppLink(appLinkInfo, true);
                        TvInteractiveAppManagerService.this.updateServiceConnectionLocked(component, resolveCallingUserId);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void unregisterAppLinkInfo(String str, AppLinkInfo appLinkInfo, int i) {
            TvInteractiveAppManagerService tvInteractiveAppManagerService = TvInteractiveAppManagerService.this;
            int callingPid = Binder.getCallingPid();
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = tvInteractiveAppManagerService.resolveCallingUserId(callingPid, callingUid, i, "unregisterAppLinkInfo: " + appLinkInfo);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                try {
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in unregisterAppLinkInfo", e);
                }
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    UserState orCreateUserStateLocked = TvInteractiveAppManagerService.this.getOrCreateUserStateLocked(resolveCallingUserId);
                    TvInteractiveAppState tvInteractiveAppState = (TvInteractiveAppState) orCreateUserStateLocked.mIAppMap.get(str);
                    if (tvInteractiveAppState == null) {
                        Slogf.m26e("TvInteractiveAppManagerService", "failed to unregisterAppLinkInfo - unknown TIAS id " + str);
                        return;
                    }
                    ComponentName component = tvInteractiveAppState.mInfo.getComponent();
                    ServiceState serviceState = (ServiceState) orCreateUserStateLocked.mServiceStateMap.get(component);
                    if (serviceState == null) {
                        ServiceState serviceState2 = new ServiceState(component, str, resolveCallingUserId);
                        serviceState2.addPendingAppLink(appLinkInfo, false);
                        orCreateUserStateLocked.mServiceStateMap.put(component, serviceState2);
                        TvInteractiveAppManagerService.this.updateServiceConnectionLocked(component, resolveCallingUserId);
                    } else if (serviceState.mService != null) {
                        serviceState.mService.unregisterAppLinkInfo(appLinkInfo);
                    } else {
                        serviceState.addPendingAppLink(appLinkInfo, false);
                        TvInteractiveAppManagerService.this.updateServiceConnectionLocked(component, resolveCallingUserId);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void sendAppLinkCommand(String str, Bundle bundle, int i) {
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), Binder.getCallingUid(), i, "sendAppLinkCommand");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                try {
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in sendAppLinkCommand", e);
                }
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    UserState orCreateUserStateLocked = TvInteractiveAppManagerService.this.getOrCreateUserStateLocked(resolveCallingUserId);
                    TvInteractiveAppState tvInteractiveAppState = (TvInteractiveAppState) orCreateUserStateLocked.mIAppMap.get(str);
                    if (tvInteractiveAppState == null) {
                        Slogf.m26e("TvInteractiveAppManagerService", "failed to sendAppLinkCommand - unknown TIAS id " + str);
                        return;
                    }
                    ComponentName component = tvInteractiveAppState.mInfo.getComponent();
                    ServiceState serviceState = (ServiceState) orCreateUserStateLocked.mServiceStateMap.get(component);
                    if (serviceState == null) {
                        ServiceState serviceState2 = new ServiceState(component, str, resolveCallingUserId);
                        serviceState2.addPendingAppLinkCommand(bundle);
                        orCreateUserStateLocked.mServiceStateMap.put(component, serviceState2);
                        TvInteractiveAppManagerService.this.updateServiceConnectionLocked(component, resolveCallingUserId);
                    } else if (serviceState.mService != null) {
                        serviceState.mService.sendAppLinkCommand(bundle);
                    } else {
                        serviceState.addPendingAppLinkCommand(bundle);
                        TvInteractiveAppManagerService.this.updateServiceConnectionLocked(component, resolveCallingUserId);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void createSession(ITvInteractiveAppClient iTvInteractiveAppClient, String str, int i, int i2, int i3) {
            long j;
            ServiceState serviceState;
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(callingPid, callingUid, i3, "createSession");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                try {
                    synchronized (TvInteractiveAppManagerService.this.mLock) {
                        try {
                            if (i3 != TvInteractiveAppManagerService.this.mCurrentUserId && !TvInteractiveAppManagerService.this.mRunningProfiles.contains(Integer.valueOf(i3))) {
                                TvInteractiveAppManagerService.this.sendSessionTokenToClientLocked(iTvInteractiveAppClient, str, null, null, i2);
                                Binder.restoreCallingIdentity(clearCallingIdentity);
                                return;
                            }
                            UserState orCreateUserStateLocked = TvInteractiveAppManagerService.this.getOrCreateUserStateLocked(resolveCallingUserId);
                            TvInteractiveAppState tvInteractiveAppState = (TvInteractiveAppState) orCreateUserStateLocked.mIAppMap.get(str);
                            if (tvInteractiveAppState == null) {
                                Slogf.m14w("TvInteractiveAppManagerService", "Failed to find state for iAppServiceId=" + str);
                                TvInteractiveAppManagerService.this.sendSessionTokenToClientLocked(iTvInteractiveAppClient, str, null, null, i2);
                                Binder.restoreCallingIdentity(clearCallingIdentity);
                                return;
                            }
                            ServiceState serviceState2 = (ServiceState) orCreateUserStateLocked.mServiceStateMap.get(tvInteractiveAppState.mComponentName);
                            if (serviceState2 == null) {
                                int i4 = PackageManager.getApplicationInfoAsUserCached(tvInteractiveAppState.mComponentName.getPackageName(), 0L, resolveCallingUserId).uid;
                                ServiceState serviceState3 = new ServiceState(tvInteractiveAppState.mComponentName, str, resolveCallingUserId);
                                orCreateUserStateLocked.mServiceStateMap.put(tvInteractiveAppState.mComponentName, serviceState3);
                                serviceState = serviceState3;
                            } else {
                                serviceState = serviceState2;
                            }
                            if (serviceState.mReconnecting) {
                                TvInteractiveAppManagerService.this.sendSessionTokenToClientLocked(iTvInteractiveAppClient, str, null, null, i2);
                                Binder.restoreCallingIdentity(clearCallingIdentity);
                                return;
                            }
                            Binder binder = new Binder();
                            orCreateUserStateLocked.mSessionStateMap.put(binder, new SessionState(binder, str, i, tvInteractiveAppState.mComponentName, iTvInteractiveAppClient, i2, callingUid, callingPid, resolveCallingUserId));
                            serviceState.mSessionTokens.add(binder);
                            if (serviceState.mService == null) {
                                TvInteractiveAppManagerService.this.updateServiceConnectionLocked(tvInteractiveAppState.mComponentName, resolveCallingUserId);
                            } else if (!TvInteractiveAppManagerService.this.createSessionInternalLocked(serviceState.mService, binder, resolveCallingUserId)) {
                                TvInteractiveAppManagerService.this.removeSessionStateLocked(binder, resolveCallingUserId);
                            }
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                        } catch (Throwable th) {
                            th = th;
                            j = clearCallingIdentity;
                            try {
                                throw th;
                            } catch (Throwable th2) {
                                th = th2;
                                Binder.restoreCallingIdentity(j);
                                throw th;
                            }
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                }
            } catch (Throwable th4) {
                th = th4;
                j = clearCallingIdentity;
            }
        }

        public void releaseSession(IBinder iBinder, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "releaseSession");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    TvInteractiveAppManagerService.this.releaseSessionLocked(iBinder, callingUid, resolveCallingUserId);
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyTuned(IBinder iBinder, Uri uri, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "notifyTuned");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifyTuned(uri);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifyTuned", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyTrackSelected(IBinder iBinder, int i, String str, int i2) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i2, "notifyTrackSelected");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifyTrackSelected(i, str);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifyTrackSelected", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyTracksChanged(IBinder iBinder, List<TvTrackInfo> list, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "notifyTracksChanged");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifyTracksChanged(list);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifyTracksChanged", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyVideoAvailable(IBinder iBinder, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "notifyVideoAvailable");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifyVideoAvailable();
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifyVideoAvailable", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyVideoUnavailable(IBinder iBinder, int i, int i2) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i2, "notifyVideoUnavailable");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifyVideoUnavailable(i);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifyVideoUnavailable", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyContentAllowed(IBinder iBinder, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "notifyContentAllowed");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifyContentAllowed();
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifyContentAllowed", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyContentBlocked(IBinder iBinder, String str, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "notifyContentBlocked");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifyContentBlocked(str);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifyContentBlocked", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifySignalStrength(IBinder iBinder, int i, int i2) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i2, "notifySignalStrength");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifySignalStrength(i);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifySignalStrength", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyTvMessage(IBinder iBinder, String str, Bundle bundle, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "notifyTvMessage");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifyTvMessage(str, bundle);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifyTvMessage", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyRecordingStarted(IBinder iBinder, String str, String str2, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "notifyRecordingStarted");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifyRecordingStarted(str, str2);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifyRecordingStarted", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyRecordingStopped(IBinder iBinder, String str, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "notifyRecordingStopped");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifyRecordingStopped(str);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifyRecordingStopped", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void startInteractiveApp(IBinder iBinder, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "startInteractiveApp");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).startInteractiveApp();
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in start", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void stopInteractiveApp(IBinder iBinder, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "stopInteractiveApp");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).stopInteractiveApp();
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in stop", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void resetInteractiveApp(IBinder iBinder, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "resetInteractiveApp");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).resetInteractiveApp();
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in reset", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void createBiInteractiveApp(IBinder iBinder, Uri uri, Bundle bundle, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "createBiInteractiveApp");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).createBiInteractiveApp(uri, bundle);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in createBiInteractiveApp", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void destroyBiInteractiveApp(IBinder iBinder, String str, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "destroyBiInteractiveApp");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).destroyBiInteractiveApp(str);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in destroyBiInteractiveApp", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void setTeletextAppEnabled(IBinder iBinder, boolean z, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "setTeletextAppEnabled");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).setTeletextAppEnabled(z);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in setTeletextAppEnabled", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void sendCurrentVideoBounds(IBinder iBinder, Rect rect, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "sendCurrentVideoBounds");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).sendCurrentVideoBounds(rect);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in sendCurrentVideoBounds", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void sendCurrentChannelUri(IBinder iBinder, Uri uri, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "sendCurrentChannelUri");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).sendCurrentChannelUri(uri);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in sendCurrentChannelUri", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void sendCurrentChannelLcn(IBinder iBinder, int i, int i2) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i2, "sendCurrentChannelLcn");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).sendCurrentChannelLcn(i);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in sendCurrentChannelLcn", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void sendStreamVolume(IBinder iBinder, float f, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "sendStreamVolume");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).sendStreamVolume(f);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in sendStreamVolume", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void sendTrackInfoList(IBinder iBinder, List<TvTrackInfo> list, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "sendTrackInfoList");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).sendTrackInfoList(list);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in sendTrackInfoList", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void sendCurrentTvInputId(IBinder iBinder, String str, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "sendCurrentTvInputId");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).sendCurrentTvInputId(str);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in sendCurrentTvInputId", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void sendTimeShiftMode(IBinder iBinder, int i, int i2) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i2, "sendTimeShiftMode");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).sendTimeShiftMode(i);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in sendTimeShiftMode", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void sendAvailableSpeeds(IBinder iBinder, float[] fArr, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "sendAvailableSpeeds");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).sendAvailableSpeeds(fArr);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in sendAvailableSpeeds", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void sendTvRecordingInfo(IBinder iBinder, TvRecordingInfo tvRecordingInfo, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "sendTvRecordingInfo");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).sendTvRecordingInfo(tvRecordingInfo);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in sendTvRecordingInfo", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void sendTvRecordingInfoList(IBinder iBinder, List<TvRecordingInfo> list, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "sendTvRecordingInfoList");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).sendTvRecordingInfoList(list);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in sendTvRecordingInfoList", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void sendSigningResult(IBinder iBinder, String str, byte[] bArr, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "sendSigningResult");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).sendSigningResult(str, bArr);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in sendSigningResult", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyError(IBinder iBinder, String str, Bundle bundle, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "notifyError");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifyError(str, bundle);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifyError", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyTimeShiftPlaybackParams(IBinder iBinder, PlaybackParams playbackParams, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "notifyTimeShiftPlaybackParams");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifyTimeShiftPlaybackParams(playbackParams);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifyTimeShiftPlaybackParams", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyTimeShiftStatusChanged(IBinder iBinder, String str, int i, int i2) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i2, "notifyTimeShiftStatusChanged");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifyTimeShiftStatusChanged(str, i);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifyTimeShiftStatusChanged", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyTimeShiftStartPositionChanged(IBinder iBinder, String str, long j, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "notifyTimeShiftStartPositionChanged");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifyTimeShiftStartPositionChanged(str, j);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifyTimeShiftStartPositionChanged", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyTimeShiftCurrentPositionChanged(IBinder iBinder, String str, long j, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "notifyTimeShiftCurrentPositionChanged");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifyTimeShiftCurrentPositionChanged(str, j);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifyTimeShiftCurrentPositionChanged", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyRecordingConnectionFailed(IBinder iBinder, String str, String str2, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "notifyRecordingConnectionFailed");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifyRecordingConnectionFailed(str, str2);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifyRecordingConnectionFailed", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyRecordingDisconnected(IBinder iBinder, String str, String str2, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "notifyRecordingDisconnected");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifyRecordingDisconnected(str, str2);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifyRecordingDisconnected", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyRecordingTuned(IBinder iBinder, String str, Uri uri, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "notifyRecordingTuned");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifyRecordingTuned(str, uri);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifyRecordingTuned", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyRecordingError(IBinder iBinder, String str, int i, int i2) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i2, "notifyRecordingError");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifyRecordingError(str, i);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifyRecordingError", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyRecordingScheduled(IBinder iBinder, String str, String str2, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "notifyRecordingScheduled");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifyRecordingScheduled(str, str2);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifyRecordingScheduled", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void setSurface(IBinder iBinder, Surface surface, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "setSurface");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).setSurface(surface);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in setSurface", e);
                    }
                }
            } finally {
                if (surface != null) {
                    surface.release();
                }
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void dispatchSurfaceChanged(IBinder iBinder, int i, int i2, int i3, int i4) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i4, "dispatchSurfaceChanged");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).dispatchSurfaceChanged(i, i2, i3);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in dispatchSurfaceChanged", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyBroadcastInfoResponse(IBinder iBinder, BroadcastInfoResponse broadcastInfoResponse, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "notifyBroadcastInfoResponse");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifyBroadcastInfoResponse(broadcastInfoResponse);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifyBroadcastInfoResponse", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyAdResponse(IBinder iBinder, AdResponse adResponse, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "notifyAdResponse");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifyAdResponse(adResponse);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifyAdResponse", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyAdBufferConsumed(IBinder iBinder, AdBuffer adBuffer, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "notifyAdBufferConsumed");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(TvInteractiveAppManagerService.this.getSessionStateLocked(iBinder, callingUid, resolveCallingUserId)).notifyAdBufferConsumed(adBuffer);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "error in notifyAdBufferConsumed", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void registerCallback(ITvInteractiveAppManagerCallback iTvInteractiveAppManagerCallback, int i) {
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), Binder.getCallingUid(), i, "registerCallback");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    if (!TvInteractiveAppManagerService.this.getOrCreateUserStateLocked(resolveCallingUserId).mCallbacks.register(iTvInteractiveAppManagerCallback)) {
                        Slog.e("TvInteractiveAppManagerService", "client process has already died");
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void unregisterCallback(ITvInteractiveAppManagerCallback iTvInteractiveAppManagerCallback, int i) {
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), Binder.getCallingUid(), i, "unregisterCallback");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    TvInteractiveAppManagerService.this.getOrCreateUserStateLocked(resolveCallingUserId).mCallbacks.unregister(iTvInteractiveAppManagerCallback);
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void createMediaView(IBinder iBinder, IBinder iBinder2, Rect rect, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "createMediaView");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(iBinder, callingUid, resolveCallingUserId).createMediaView(iBinder2, rect);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slog.e("TvInteractiveAppManagerService", "error in createMediaView", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void relayoutMediaView(IBinder iBinder, Rect rect, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "relayoutMediaView");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(iBinder, callingUid, resolveCallingUserId).relayoutMediaView(rect);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slog.e("TvInteractiveAppManagerService", "error in relayoutMediaView", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void removeMediaView(IBinder iBinder, int i) {
            int callingUid = Binder.getCallingUid();
            int resolveCallingUserId = TvInteractiveAppManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, i, "removeMediaView");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    try {
                        TvInteractiveAppManagerService.this.getSessionLocked(iBinder, callingUid, resolveCallingUserId).removeMediaView();
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slog.e("TvInteractiveAppManagerService", "error in removeMediaView", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void sendSessionTokenToClientLocked(ITvInteractiveAppClient iTvInteractiveAppClient, String str, IBinder iBinder, InputChannel inputChannel, int i) {
        try {
            iTvInteractiveAppClient.onSessionCreated(str, iBinder, inputChannel, i);
        } catch (RemoteException e) {
            Slogf.m25e("TvInteractiveAppManagerService", "error in onSessionCreated", e);
        }
    }

    @GuardedBy({"mLock"})
    public final boolean createSessionInternalLocked(ITvInteractiveAppService iTvInteractiveAppService, IBinder iBinder, int i) {
        boolean z;
        SessionState sessionState = (SessionState) getOrCreateUserStateLocked(i).mSessionStateMap.get(iBinder);
        InputChannel[] openInputChannelPair = InputChannel.openInputChannelPair(iBinder.toString());
        try {
            iTvInteractiveAppService.createSession(openInputChannelPair[1], new SessionCallback(sessionState, openInputChannelPair), sessionState.mIAppServiceId, sessionState.mType);
            z = true;
        } catch (RemoteException e) {
            Slogf.m25e("TvInteractiveAppManagerService", "error in createSession", e);
            sendSessionTokenToClientLocked(sessionState.mClient, sessionState.mIAppServiceId, null, null, sessionState.mSeq);
            z = false;
        }
        openInputChannelPair[1].dispose();
        return z;
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x0037, code lost:
        if (r6 == null) goto L11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x003a, code lost:
        removeSessionStateLocked(r5, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x003d, code lost:
        return r6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x0021, code lost:
        if (r6 != null) goto L10;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0023, code lost:
        r6.mSession = null;
     */
    /* JADX WARN: Removed duplicated region for block: B:23:0x0040  */
    @GuardedBy({"mLock"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final SessionState releaseSessionLocked(IBinder iBinder, int i, int i2) {
        SessionState sessionState;
        try {
            sessionState = getSessionStateLocked(iBinder, i, i2);
            try {
                try {
                    getOrCreateUserStateLocked(i2);
                    if (sessionState.mSession != null) {
                        sessionState.mSession.asBinder().unlinkToDeath(sessionState, 0);
                        sessionState.mSession.release();
                    }
                } catch (RemoteException | SessionNotFoundException e) {
                    e = e;
                    Slogf.m25e("TvInteractiveAppManagerService", "error in releaseSession", e);
                }
            } catch (Throwable th) {
                th = th;
                if (sessionState != null) {
                    sessionState.mSession = null;
                }
                throw th;
            }
        } catch (RemoteException | SessionNotFoundException e2) {
            e = e2;
            sessionState = null;
        } catch (Throwable th2) {
            th = th2;
            sessionState = null;
            if (sessionState != null) {
            }
            throw th;
        }
    }

    @GuardedBy({"mLock"})
    public final void removeSessionStateLocked(IBinder iBinder, int i) {
        UserState orCreateUserStateLocked = getOrCreateUserStateLocked(i);
        SessionState sessionState = (SessionState) orCreateUserStateLocked.mSessionStateMap.remove(iBinder);
        if (sessionState == null) {
            Slogf.m26e("TvInteractiveAppManagerService", "sessionState null, no more remove session action!");
            return;
        }
        ClientState clientState = (ClientState) orCreateUserStateLocked.mClientStateMap.get(sessionState.mClient.asBinder());
        if (clientState != null) {
            clientState.mSessionTokens.remove(iBinder);
            if (clientState.isEmpty()) {
                orCreateUserStateLocked.mClientStateMap.remove(sessionState.mClient.asBinder());
                sessionState.mClient.asBinder().unlinkToDeath(clientState, 0);
            }
        }
        ServiceState serviceState = (ServiceState) orCreateUserStateLocked.mServiceStateMap.get(sessionState.mComponent);
        if (serviceState != null) {
            serviceState.mSessionTokens.remove(iBinder);
        }
        updateServiceConnectionLocked(sessionState.mComponent, i);
    }

    @GuardedBy({"mLock"})
    public final void abortPendingCreateSessionRequestsLocked(ServiceState serviceState, String str, int i) {
        UserState orCreateUserStateLocked = getOrCreateUserStateLocked(i);
        ArrayList<SessionState> arrayList = new ArrayList();
        for (IBinder iBinder : serviceState.mSessionTokens) {
            SessionState sessionState = (SessionState) orCreateUserStateLocked.mSessionStateMap.get(iBinder);
            if (sessionState.mSession == null && (str == null || sessionState.mIAppServiceId.equals(str))) {
                arrayList.add(sessionState);
            }
        }
        for (SessionState sessionState2 : arrayList) {
            removeSessionStateLocked(sessionState2.mSessionToken, sessionState2.mUserId);
            sendSessionTokenToClientLocked(sessionState2.mClient, sessionState2.mIAppServiceId, null, null, sessionState2.mSeq);
        }
        updateServiceConnectionLocked(serviceState.mComponent, i);
    }

    @GuardedBy({"mLock"})
    public final void updateServiceConnectionLocked(ComponentName componentName, int i) {
        UserState orCreateUserStateLocked = getOrCreateUserStateLocked(i);
        ServiceState serviceState = (ServiceState) orCreateUserStateLocked.mServiceStateMap.get(componentName);
        if (serviceState == null) {
            return;
        }
        boolean z = false;
        if (serviceState.mReconnecting) {
            if (!serviceState.mSessionTokens.isEmpty()) {
                return;
            }
            serviceState.mReconnecting = false;
        }
        z = (serviceState.mSessionTokens.isEmpty() && serviceState.mPendingAppLinkInfo.isEmpty() && serviceState.mPendingAppLinkCommand.isEmpty()) ? true : true;
        if (serviceState.mService == null && z) {
            if (serviceState.mBound) {
                return;
            }
            serviceState.mBound = this.mContext.bindServiceAsUser(new Intent("android.media.tv.interactive.TvInteractiveAppService").setComponent(componentName), serviceState.mConnection, 33554433, new UserHandle(i));
        } else if (serviceState.mService == null || z) {
        } else {
            this.mContext.unbindService(serviceState.mConnection);
            orCreateUserStateLocked.mServiceStateMap.remove(componentName);
        }
    }

    /* renamed from: com.android.server.tv.interactive.TvInteractiveAppManagerService$UserState */
    /* loaded from: classes2.dex */
    public static final class UserState {
        public final List<AppLinkInfo> mAppLinkInfoList;
        public final RemoteCallbackList<ITvInteractiveAppManagerCallback> mCallbacks;
        public final Map<IBinder, ClientState> mClientStateMap;
        public Map<String, TvInteractiveAppState> mIAppMap;
        public final Set<String> mPackageSet;
        public final Map<ComponentName, ServiceState> mServiceStateMap;
        public final Map<IBinder, SessionState> mSessionStateMap;
        public final int mUserId;

        public UserState(int i) {
            this.mIAppMap = new HashMap();
            this.mClientStateMap = new HashMap();
            this.mServiceStateMap = new HashMap();
            this.mSessionStateMap = new HashMap();
            this.mPackageSet = new HashSet();
            this.mAppLinkInfoList = new ArrayList();
            this.mCallbacks = new RemoteCallbackList<>();
            this.mUserId = i;
        }
    }

    /* renamed from: com.android.server.tv.interactive.TvInteractiveAppManagerService$TvInteractiveAppState */
    /* loaded from: classes2.dex */
    public static final class TvInteractiveAppState {
        public ComponentName mComponentName;
        public int mIAppNumber;
        public TvInteractiveAppServiceInfo mInfo;
        public int mUid;

        public TvInteractiveAppState() {
        }
    }

    /* renamed from: com.android.server.tv.interactive.TvInteractiveAppManagerService$SessionState */
    /* loaded from: classes2.dex */
    public final class SessionState implements IBinder.DeathRecipient {
        public final int mCallingPid;
        public final int mCallingUid;
        public final ITvInteractiveAppClient mClient;
        public final ComponentName mComponent;
        public final String mIAppServiceId;
        public final int mSeq;
        public ITvInteractiveAppSession mSession;
        public final IBinder mSessionToken;
        public final int mType;
        public final int mUserId;

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
        }

        public SessionState(IBinder iBinder, String str, int i, ComponentName componentName, ITvInteractiveAppClient iTvInteractiveAppClient, int i2, int i3, int i4, int i5) {
            this.mSessionToken = iBinder;
            this.mIAppServiceId = str;
            this.mComponent = componentName;
            this.mType = i;
            this.mClient = iTvInteractiveAppClient;
            this.mSeq = i2;
            this.mCallingUid = i3;
            this.mCallingPid = i4;
            this.mUserId = i5;
        }
    }

    /* renamed from: com.android.server.tv.interactive.TvInteractiveAppManagerService$ClientState */
    /* loaded from: classes2.dex */
    public final class ClientState implements IBinder.DeathRecipient {
        public IBinder mClientToken;
        public final List<IBinder> mSessionTokens = new ArrayList();
        public final int mUserId;

        public ClientState(IBinder iBinder, int i) {
            this.mClientToken = iBinder;
            this.mUserId = i;
        }

        public boolean isEmpty() {
            return this.mSessionTokens.isEmpty();
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                ClientState clientState = (ClientState) TvInteractiveAppManagerService.this.getOrCreateUserStateLocked(this.mUserId).mClientStateMap.get(this.mClientToken);
                if (clientState != null) {
                    while (clientState.mSessionTokens.size() > 0) {
                        IBinder iBinder = clientState.mSessionTokens.get(0);
                        TvInteractiveAppManagerService.this.releaseSessionLocked(iBinder, 1000, this.mUserId);
                        if (clientState.mSessionTokens.contains(iBinder)) {
                            Slogf.m30d("TvInteractiveAppManagerService", "remove sessionToken " + iBinder + " for " + this.mClientToken);
                            clientState.mSessionTokens.remove(iBinder);
                        }
                    }
                }
                this.mClientToken = null;
            }
        }
    }

    /* renamed from: com.android.server.tv.interactive.TvInteractiveAppManagerService$ServiceState */
    /* loaded from: classes2.dex */
    public final class ServiceState {
        public boolean mBound;
        public ServiceCallback mCallback;
        public final ComponentName mComponent;
        public final ServiceConnection mConnection;
        public final String mIAppServiceId;
        public final List<Bundle> mPendingAppLinkCommand;
        public final List<Pair<AppLinkInfo, Boolean>> mPendingAppLinkInfo;
        public boolean mReconnecting;
        public ITvInteractiveAppService mService;
        public final List<IBinder> mSessionTokens;

        public ServiceState(ComponentName componentName, String str, int i) {
            this.mSessionTokens = new ArrayList();
            this.mPendingAppLinkInfo = new ArrayList();
            this.mPendingAppLinkCommand = new ArrayList();
            this.mComponent = componentName;
            this.mConnection = new InteractiveAppServiceConnection(componentName, i);
            this.mIAppServiceId = str;
        }

        public final void addPendingAppLink(AppLinkInfo appLinkInfo, boolean z) {
            this.mPendingAppLinkInfo.add(Pair.create(appLinkInfo, Boolean.valueOf(z)));
        }

        public final void addPendingAppLinkCommand(Bundle bundle) {
            this.mPendingAppLinkCommand.add(bundle);
        }
    }

    /* renamed from: com.android.server.tv.interactive.TvInteractiveAppManagerService$InteractiveAppServiceConnection */
    /* loaded from: classes2.dex */
    public final class InteractiveAppServiceConnection implements ServiceConnection {
        public final ComponentName mComponent;
        public final int mUserId;

        public InteractiveAppServiceConnection(ComponentName componentName, int i) {
            this.mComponent = componentName;
            this.mUserId = i;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                UserState userStateLocked = TvInteractiveAppManagerService.this.getUserStateLocked(this.mUserId);
                if (userStateLocked == null) {
                    TvInteractiveAppManagerService.this.mContext.unbindService(this);
                    return;
                }
                ServiceState serviceState = (ServiceState) userStateLocked.mServiceStateMap.get(this.mComponent);
                serviceState.mService = ITvInteractiveAppService.Stub.asInterface(iBinder);
                if (serviceState.mCallback == null) {
                    serviceState.mCallback = new ServiceCallback(this.mComponent, this.mUserId);
                    try {
                        serviceState.mService.registerCallback(serviceState.mCallback);
                    } catch (RemoteException e) {
                        Slog.e("TvInteractiveAppManagerService", "error in registerCallback", e);
                    }
                }
                if (!serviceState.mPendingAppLinkInfo.isEmpty()) {
                    Iterator it = serviceState.mPendingAppLinkInfo.iterator();
                    while (it.hasNext()) {
                        Pair pair = (Pair) it.next();
                        long clearCallingIdentity = Binder.clearCallingIdentity();
                        try {
                            if (((Boolean) pair.second).booleanValue()) {
                                serviceState.mService.registerAppLinkInfo((AppLinkInfo) pair.first);
                            } else {
                                serviceState.mService.unregisterAppLinkInfo((AppLinkInfo) pair.first);
                            }
                            it.remove();
                        } catch (RemoteException e2) {
                            Slogf.m25e("TvInteractiveAppManagerService", "error in notifyAppLinkInfo(" + pair + ") when onServiceConnected", e2);
                        }
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                    }
                }
                if (!serviceState.mPendingAppLinkCommand.isEmpty()) {
                    Iterator it2 = serviceState.mPendingAppLinkCommand.iterator();
                    while (it2.hasNext()) {
                        Bundle bundle = (Bundle) it2.next();
                        long clearCallingIdentity2 = Binder.clearCallingIdentity();
                        try {
                            serviceState.mService.sendAppLinkCommand(bundle);
                            it2.remove();
                        } catch (RemoteException e3) {
                            Slogf.m25e("TvInteractiveAppManagerService", "error in sendAppLinkCommand(" + bundle + ") when onServiceConnected", e3);
                        }
                        Binder.restoreCallingIdentity(clearCallingIdentity2);
                    }
                }
                ArrayList<IBinder> arrayList = new ArrayList();
                for (IBinder iBinder2 : serviceState.mSessionTokens) {
                    if (!TvInteractiveAppManagerService.this.createSessionInternalLocked(serviceState.mService, iBinder2, this.mUserId)) {
                        arrayList.add(iBinder2);
                    }
                }
                for (IBinder iBinder3 : arrayList) {
                    TvInteractiveAppManagerService.this.removeSessionStateLocked(iBinder3, this.mUserId);
                }
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            if (!this.mComponent.equals(componentName)) {
                throw new IllegalArgumentException("Mismatched ComponentName: " + this.mComponent + " (expected), " + componentName + " (actual).");
            }
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                ServiceState serviceState = (ServiceState) TvInteractiveAppManagerService.this.getOrCreateUserStateLocked(this.mUserId).mServiceStateMap.get(this.mComponent);
                if (serviceState != null) {
                    serviceState.mReconnecting = true;
                    serviceState.mBound = false;
                    serviceState.mService = null;
                    serviceState.mCallback = null;
                    TvInteractiveAppManagerService.this.abortPendingCreateSessionRequestsLocked(serviceState, null, this.mUserId);
                }
            }
        }
    }

    /* renamed from: com.android.server.tv.interactive.TvInteractiveAppManagerService$ServiceCallback */
    /* loaded from: classes2.dex */
    public final class ServiceCallback extends ITvInteractiveAppServiceCallback.Stub {
        public final ComponentName mComponent;
        public final int mUserId;

        public ServiceCallback(ComponentName componentName, int i) {
            this.mComponent = componentName;
            this.mUserId = i;
        }

        public void onStateChanged(int i, int i2, int i3) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInteractiveAppManagerService.this.mLock) {
                    String str = TvInteractiveAppManagerService.this.getServiceStateLocked(this.mComponent, this.mUserId).mIAppServiceId;
                    TvInteractiveAppManagerService.this.notifyStateChangedLocked(TvInteractiveAppManagerService.this.getUserStateLocked(this.mUserId), str, i, i2, i3);
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    /* renamed from: com.android.server.tv.interactive.TvInteractiveAppManagerService$SessionCallback */
    /* loaded from: classes2.dex */
    public final class SessionCallback extends ITvInteractiveAppSessionCallback.Stub {
        public final InputChannel[] mInputChannels;
        public final SessionState mSessionState;

        public SessionCallback(SessionState sessionState, InputChannel[] inputChannelArr) {
            this.mSessionState = sessionState;
            this.mInputChannels = inputChannelArr;
        }

        public void onSessionCreated(ITvInteractiveAppSession iTvInteractiveAppSession) {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                this.mSessionState.mSession = iTvInteractiveAppSession;
                if (iTvInteractiveAppSession != null && addSessionTokenToClientStateLocked(iTvInteractiveAppSession)) {
                    TvInteractiveAppManagerService.this.sendSessionTokenToClientLocked(this.mSessionState.mClient, this.mSessionState.mIAppServiceId, this.mSessionState.mSessionToken, this.mInputChannels[0], this.mSessionState.mSeq);
                } else {
                    TvInteractiveAppManagerService.this.removeSessionStateLocked(this.mSessionState.mSessionToken, this.mSessionState.mUserId);
                    TvInteractiveAppManagerService.this.sendSessionTokenToClientLocked(this.mSessionState.mClient, this.mSessionState.mIAppServiceId, null, null, this.mSessionState.mSeq);
                }
                this.mInputChannels[0].dispose();
            }
        }

        public void onLayoutSurface(int i, int i2, int i3, int i4) {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onLayoutSurface(i, i2, i3, i4, this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onLayoutSurface", e);
                }
            }
        }

        public void onBroadcastInfoRequest(BroadcastInfoRequest broadcastInfoRequest) {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onBroadcastInfoRequest(broadcastInfoRequest, this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onBroadcastInfoRequest", e);
                }
            }
        }

        public void onRemoveBroadcastInfo(int i) {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onRemoveBroadcastInfo(i, this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onRemoveBroadcastInfo", e);
                }
            }
        }

        public void onCommandRequest(String str, Bundle bundle) {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onCommandRequest(str, bundle, this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onCommandRequest", e);
                }
            }
        }

        public void onTimeShiftCommandRequest(String str, Bundle bundle) {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onTimeShiftCommandRequest(str, bundle, this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onTimeShiftCommandRequest", e);
                }
            }
        }

        public void onSetVideoBounds(Rect rect) {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onSetVideoBounds(rect, this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onSetVideoBounds", e);
                }
            }
        }

        public void onRequestCurrentVideoBounds() {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onRequestCurrentVideoBounds(this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onRequestCurrentVideoBounds", e);
                }
            }
        }

        public void onRequestCurrentChannelUri() {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onRequestCurrentChannelUri(this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onRequestCurrentChannelUri", e);
                }
            }
        }

        public void onRequestCurrentChannelLcn() {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onRequestCurrentChannelLcn(this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onRequestCurrentChannelLcn", e);
                }
            }
        }

        public void onRequestStreamVolume() {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onRequestStreamVolume(this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onRequestStreamVolume", e);
                }
            }
        }

        public void onRequestTrackInfoList() {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onRequestTrackInfoList(this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onRequestTrackInfoList", e);
                }
            }
        }

        public void onRequestCurrentTvInputId() {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onRequestCurrentTvInputId(this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onRequestCurrentTvInputId", e);
                }
            }
        }

        public void onRequestTimeShiftMode() {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onRequestTimeShiftMode(this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onRequestTimeShiftMode", e);
                }
            }
        }

        public void onRequestAvailableSpeeds() {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onRequestAvailableSpeeds(this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onRequestAvailableSpeeds", e);
                }
            }
        }

        public void onRequestStartRecording(String str, Uri uri) {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onRequestStartRecording(str, uri, this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onRequestStartRecording", e);
                }
            }
        }

        public void onRequestStopRecording(String str) {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onRequestStopRecording(str, this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onRequestStopRecording", e);
                }
            }
        }

        public void onRequestScheduleRecording(String str, String str2, Uri uri, Uri uri2, Bundle bundle) {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onRequestScheduleRecording(str, str2, uri, uri2, bundle, this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onRequestScheduleRecording", e);
                }
            }
        }

        public void onRequestScheduleRecording2(String str, String str2, Uri uri, long j, long j2, int i, Bundle bundle) {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onRequestScheduleRecording2(str2, str, uri, j, j2, i, bundle, this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onRequestScheduleRecording2", e);
                }
            }
        }

        public void onSetTvRecordingInfo(String str, TvRecordingInfo tvRecordingInfo) {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onSetTvRecordingInfo(str, tvRecordingInfo, this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onSetTvRecordingInfo", e);
                }
            }
        }

        public void onRequestTvRecordingInfo(String str) {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onRequestTvRecordingInfo(str, this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onRequestTvRecordingInfo", e);
                }
            }
        }

        public void onRequestTvRecordingInfoList(int i) {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onRequestTvRecordingInfoList(i, this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onRequestTvRecordingInfoList", e);
                }
            }
        }

        public void onRequestSigning(String str, String str2, String str3, byte[] bArr) {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onRequestSigning(str, str2, str3, bArr, this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onRequestSigning", e);
                }
            }
        }

        public void onAdRequest(AdRequest adRequest) {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onAdRequest(adRequest, this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onAdRequest", e);
                }
            }
        }

        public void onSessionStateChanged(int i, int i2) {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onSessionStateChanged(i, i2, this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onSessionStateChanged", e);
                }
            }
        }

        public void onBiInteractiveAppCreated(Uri uri, String str) {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onBiInteractiveAppCreated(uri, str, this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onBiInteractiveAppCreated", e);
                }
            }
        }

        public void onTeletextAppStateChanged(int i) {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onTeletextAppStateChanged(i, this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onTeletextAppStateChanged", e);
                }
            }
        }

        public void onAdBuffer(AdBuffer adBuffer) {
            synchronized (TvInteractiveAppManagerService.this.mLock) {
                if (this.mSessionState.mSession == null || this.mSessionState.mClient == null) {
                    return;
                }
                try {
                    this.mSessionState.mClient.onAdBuffer(adBuffer, this.mSessionState.mSeq);
                } catch (RemoteException e) {
                    Slogf.m25e("TvInteractiveAppManagerService", "error in onAdBuffer", e);
                }
            }
        }

        @GuardedBy({"mLock"})
        public final boolean addSessionTokenToClientStateLocked(ITvInteractiveAppSession iTvInteractiveAppSession) {
            try {
                iTvInteractiveAppSession.asBinder().linkToDeath(this.mSessionState, 0);
                IBinder asBinder = this.mSessionState.mClient.asBinder();
                UserState orCreateUserStateLocked = TvInteractiveAppManagerService.this.getOrCreateUserStateLocked(this.mSessionState.mUserId);
                ClientState clientState = (ClientState) orCreateUserStateLocked.mClientStateMap.get(asBinder);
                if (clientState == null) {
                    clientState = new ClientState(asBinder, this.mSessionState.mUserId);
                    try {
                        asBinder.linkToDeath(clientState, 0);
                        orCreateUserStateLocked.mClientStateMap.put(asBinder, clientState);
                    } catch (RemoteException e) {
                        Slogf.m25e("TvInteractiveAppManagerService", "client process has already died", e);
                        return false;
                    }
                }
                clientState.mSessionTokens.add(this.mSessionState.mSessionToken);
                return true;
            } catch (RemoteException e2) {
                Slogf.m25e("TvInteractiveAppManagerService", "session process has already died", e2);
                return false;
            }
        }
    }

    /* renamed from: com.android.server.tv.interactive.TvInteractiveAppManagerService$SessionNotFoundException */
    /* loaded from: classes2.dex */
    public static class SessionNotFoundException extends IllegalArgumentException {
        public SessionNotFoundException(String str) {
            super(str);
        }
    }
}
