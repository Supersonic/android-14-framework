package com.android.server.people;

import android.app.ActivityManager;
import android.app.people.ConversationChannel;
import android.app.people.ConversationStatus;
import android.app.people.IConversationListener;
import android.app.people.IPeopleManager;
import android.app.prediction.AppPredictionContext;
import android.app.prediction.AppPredictionSessionId;
import android.app.prediction.AppTarget;
import android.app.prediction.AppTargetEvent;
import android.app.prediction.IPredictionCallback;
import android.content.Context;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ParceledListSlice;
import android.content.pm.ShortcutInfo;
import android.os.Binder;
import android.os.CancellationSignal;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.people.PeopleService;
import com.android.server.people.data.DataManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public class PeopleService extends SystemService {
    @VisibleForTesting
    ConversationListenerHelper mConversationListenerHelper;
    public DataManager mDataManager;
    public PackageManagerInternal mPackageManagerInternal;
    @VisibleForTesting
    final IBinder mService;

    /* loaded from: classes2.dex */
    public interface ConversationsListener {
        default void onConversationsUpdate(List<ConversationChannel> list) {
        }
    }

    public PeopleService(Context context) {
        super(context);
        this.mService = new IPeopleManager.Stub() { // from class: com.android.server.people.PeopleService.1
            public ConversationChannel getConversation(String str, int i, String str2) {
                PeopleService peopleService = PeopleService.this;
                peopleService.enforceSystemRootOrSystemUI(peopleService.getContext(), "get conversation");
                return PeopleService.this.mDataManager.getConversation(str, i, str2);
            }

            public ParceledListSlice<ConversationChannel> getRecentConversations() {
                PeopleService peopleService = PeopleService.this;
                peopleService.enforceSystemRootOrSystemUI(peopleService.getContext(), "get recent conversations");
                return new ParceledListSlice<>(PeopleService.this.mDataManager.getRecentConversations(Binder.getCallingUserHandle().getIdentifier()));
            }

            public void removeRecentConversation(String str, int i, String str2) {
                PeopleService.enforceSystemOrRoot("remove a recent conversation");
                PeopleService.this.mDataManager.removeRecentConversation(str, i, str2, Binder.getCallingUserHandle().getIdentifier());
            }

            public void removeAllRecentConversations() {
                PeopleService.enforceSystemOrRoot("remove all recent conversations");
                PeopleService.this.mDataManager.removeAllRecentConversations(Binder.getCallingUserHandle().getIdentifier());
            }

            public boolean isConversation(String str, int i, String str2) {
                enforceHasReadPeopleDataPermission();
                PeopleService.this.handleIncomingUser(i);
                return PeopleService.this.mDataManager.isConversation(str, i, str2);
            }

            public final void enforceHasReadPeopleDataPermission() throws SecurityException {
                if (PeopleService.this.getContext().checkCallingPermission("android.permission.READ_PEOPLE_DATA") != 0) {
                    throw new SecurityException("Caller doesn't have READ_PEOPLE_DATA permission.");
                }
            }

            public long getLastInteraction(String str, int i, String str2) {
                PeopleService peopleService = PeopleService.this;
                peopleService.enforceSystemRootOrSystemUI(peopleService.getContext(), "get last interaction");
                return PeopleService.this.mDataManager.getLastInteraction(str, i, str2);
            }

            public void addOrUpdateStatus(String str, int i, String str2, ConversationStatus conversationStatus) {
                PeopleService.this.handleIncomingUser(i);
                PeopleService.this.checkCallerIsSameApp(str);
                if (conversationStatus.getStartTimeMillis() > System.currentTimeMillis()) {
                    throw new IllegalArgumentException("Start time must be in the past");
                }
                PeopleService.this.mDataManager.addOrUpdateStatus(str, i, str2, conversationStatus);
            }

            public void clearStatus(String str, int i, String str2, String str3) {
                PeopleService.this.handleIncomingUser(i);
                PeopleService.this.checkCallerIsSameApp(str);
                PeopleService.this.mDataManager.clearStatus(str, i, str2, str3);
            }

            public void clearStatuses(String str, int i, String str2) {
                PeopleService.this.handleIncomingUser(i);
                PeopleService.this.checkCallerIsSameApp(str);
                PeopleService.this.mDataManager.clearStatuses(str, i, str2);
            }

            public ParceledListSlice<ConversationStatus> getStatuses(String str, int i, String str2) {
                PeopleService.this.handleIncomingUser(i);
                if (!PeopleService.isSystemOrRoot()) {
                    PeopleService.this.checkCallerIsSameApp(str);
                }
                return new ParceledListSlice<>(PeopleService.this.mDataManager.getStatuses(str, i, str2));
            }

            public void registerConversationListener(String str, int i, String str2, IConversationListener iConversationListener) {
                PeopleService peopleService = PeopleService.this;
                peopleService.enforceSystemRootOrSystemUI(peopleService.getContext(), "register conversation listener");
                PeopleService.this.mConversationListenerHelper.addConversationListener(new ListenerKey(str, Integer.valueOf(i), str2), iConversationListener);
            }

            public void unregisterConversationListener(IConversationListener iConversationListener) {
                PeopleService peopleService = PeopleService.this;
                peopleService.enforceSystemRootOrSystemUI(peopleService.getContext(), "unregister conversation listener");
                PeopleService.this.mConversationListenerHelper.removeConversationListener(iConversationListener);
            }
        };
        this.mDataManager = new DataManager(context);
        ConversationListenerHelper conversationListenerHelper = new ConversationListenerHelper();
        this.mConversationListenerHelper = conversationListenerHelper;
        this.mDataManager.addConversationsListener(conversationListenerHelper);
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 500) {
            this.mDataManager.initialize();
        }
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        onStart(false);
    }

    @VisibleForTesting
    public void onStart(boolean z) {
        if (!z) {
            publishBinderService("people", this.mService);
        }
        publishLocalService(PeopleServiceInternal.class, new LocalService());
        this.mPackageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
    }

    @Override // com.android.server.SystemService
    public void onUserUnlocked(SystemService.TargetUser targetUser) {
        this.mDataManager.onUserUnlocked(targetUser.getUserIdentifier());
    }

    @Override // com.android.server.SystemService
    public void onUserStopping(SystemService.TargetUser targetUser) {
        this.mDataManager.onUserStopping(targetUser.getUserIdentifier());
    }

    public static void enforceSystemOrRoot(String str) {
        if (isSystemOrRoot()) {
            return;
        }
        throw new SecurityException("Only system may " + str);
    }

    public static boolean isSystemOrRoot() {
        int callingUid = Binder.getCallingUid();
        return UserHandle.isSameApp(callingUid, 1000) || callingUid == 0;
    }

    public final int handleIncomingUser(int i) {
        try {
            return ActivityManager.getService().handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, true, true, "", (String) null);
        } catch (RemoteException unused) {
            return i;
        }
    }

    public final void checkCallerIsSameApp(String str) {
        int callingUid = Binder.getCallingUid();
        if (this.mPackageManagerInternal.getPackageUid(str, 0L, UserHandle.getUserId(callingUid)) == callingUid) {
            return;
        }
        throw new SecurityException("Calling uid " + callingUid + " cannot query eventsfor package " + str);
    }

    @VisibleForTesting
    public void enforceSystemRootOrSystemUI(Context context, String str) {
        if (isSystemOrRoot()) {
            return;
        }
        context.enforceCallingPermission("android.permission.STATUS_BAR_SERVICE", str);
    }

    /* loaded from: classes2.dex */
    public static class ConversationListenerHelper implements ConversationsListener {
        @VisibleForTesting
        final RemoteCallbackList<IConversationListener> mListeners = new RemoteCallbackList<>();

        public synchronized void addConversationListener(ListenerKey listenerKey, IConversationListener iConversationListener) {
            this.mListeners.unregister(iConversationListener);
            this.mListeners.register(iConversationListener, listenerKey);
        }

        public synchronized void removeConversationListener(IConversationListener iConversationListener) {
            this.mListeners.unregister(iConversationListener);
        }

        @Override // com.android.server.people.PeopleService.ConversationsListener
        public void onConversationsUpdate(List<ConversationChannel> list) {
            int beginBroadcast = this.mListeners.beginBroadcast();
            if (beginBroadcast == 0) {
                return;
            }
            HashMap hashMap = new HashMap();
            for (ConversationChannel conversationChannel : list) {
                hashMap.put(getListenerKey(conversationChannel), conversationChannel);
            }
            for (int i = 0; i < beginBroadcast; i++) {
                ListenerKey listenerKey = (ListenerKey) this.mListeners.getBroadcastCookie(i);
                if (hashMap.containsKey(listenerKey)) {
                    try {
                        this.mListeners.getBroadcastItem(i).onConversationUpdate((ConversationChannel) hashMap.get(listenerKey));
                    } catch (RemoteException unused) {
                    }
                }
            }
            this.mListeners.finishBroadcast();
        }

        public final ListenerKey getListenerKey(ConversationChannel conversationChannel) {
            ShortcutInfo shortcutInfo = conversationChannel.getShortcutInfo();
            return new ListenerKey(shortcutInfo.getPackage(), Integer.valueOf(shortcutInfo.getUserId()), shortcutInfo.getId());
        }
    }

    /* loaded from: classes2.dex */
    public static class ListenerKey {
        public final String mPackageName;
        public final String mShortcutId;
        public final Integer mUserId;

        public ListenerKey(String str, Integer num, String str2) {
            this.mPackageName = str;
            this.mUserId = num;
            this.mShortcutId = str2;
        }

        public String getPackageName() {
            return this.mPackageName;
        }

        public Integer getUserId() {
            return this.mUserId;
        }

        public String getShortcutId() {
            return this.mShortcutId;
        }

        public boolean equals(Object obj) {
            ListenerKey listenerKey = (ListenerKey) obj;
            return listenerKey.getPackageName().equals(this.mPackageName) && Objects.equals(listenerKey.getUserId(), this.mUserId) && listenerKey.getShortcutId().equals(this.mShortcutId);
        }

        public int hashCode() {
            return this.mPackageName.hashCode() + this.mUserId.hashCode() + this.mShortcutId.hashCode();
        }
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public final class LocalService extends PeopleServiceInternal {
        public Map<AppPredictionSessionId, SessionInfo> mSessions = new ArrayMap();

        public LocalService() {
        }

        public void onCreatePredictionSession(AppPredictionContext appPredictionContext, AppPredictionSessionId appPredictionSessionId) {
            this.mSessions.put(appPredictionSessionId, new SessionInfo(appPredictionContext, PeopleService.this.mDataManager, appPredictionSessionId.getUserId(), PeopleService.this.getContext()));
        }

        public void notifyAppTargetEvent(AppPredictionSessionId appPredictionSessionId, final AppTargetEvent appTargetEvent) {
            runForSession(appPredictionSessionId, new Consumer() { // from class: com.android.server.people.PeopleService$LocalService$$ExternalSyntheticLambda6
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    PeopleService.LocalService.lambda$notifyAppTargetEvent$0(appTargetEvent, (SessionInfo) obj);
                }
            });
        }

        public static /* synthetic */ void lambda$notifyAppTargetEvent$0(AppTargetEvent appTargetEvent, SessionInfo sessionInfo) {
            sessionInfo.getPredictor().onAppTargetEvent(appTargetEvent);
        }

        public void notifyLaunchLocationShown(AppPredictionSessionId appPredictionSessionId, final String str, final ParceledListSlice parceledListSlice) {
            runForSession(appPredictionSessionId, new Consumer() { // from class: com.android.server.people.PeopleService$LocalService$$ExternalSyntheticLambda4
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    PeopleService.LocalService.lambda$notifyLaunchLocationShown$1(str, parceledListSlice, (SessionInfo) obj);
                }
            });
        }

        public static /* synthetic */ void lambda$notifyLaunchLocationShown$1(String str, ParceledListSlice parceledListSlice, SessionInfo sessionInfo) {
            sessionInfo.getPredictor().onLaunchLocationShown(str, parceledListSlice.getList());
        }

        public void sortAppTargets(AppPredictionSessionId appPredictionSessionId, final ParceledListSlice parceledListSlice, final IPredictionCallback iPredictionCallback) {
            runForSession(appPredictionSessionId, new Consumer() { // from class: com.android.server.people.PeopleService$LocalService$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    PeopleService.LocalService.this.lambda$sortAppTargets$3(parceledListSlice, iPredictionCallback, (SessionInfo) obj);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$sortAppTargets$3(ParceledListSlice parceledListSlice, final IPredictionCallback iPredictionCallback, SessionInfo sessionInfo) {
            sessionInfo.getPredictor().onSortAppTargets(parceledListSlice.getList(), new Consumer() { // from class: com.android.server.people.PeopleService$LocalService$$ExternalSyntheticLambda7
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    PeopleService.LocalService.this.lambda$sortAppTargets$2(iPredictionCallback, (List) obj);
                }
            });
        }

        public void registerPredictionUpdates(AppPredictionSessionId appPredictionSessionId, final IPredictionCallback iPredictionCallback) {
            runForSession(appPredictionSessionId, new Consumer() { // from class: com.android.server.people.PeopleService$LocalService$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((SessionInfo) obj).addCallback(iPredictionCallback);
                }
            });
        }

        public void unregisterPredictionUpdates(AppPredictionSessionId appPredictionSessionId, final IPredictionCallback iPredictionCallback) {
            runForSession(appPredictionSessionId, new Consumer() { // from class: com.android.server.people.PeopleService$LocalService$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((SessionInfo) obj).removeCallback(iPredictionCallback);
                }
            });
        }

        public void requestPredictionUpdate(AppPredictionSessionId appPredictionSessionId) {
            runForSession(appPredictionSessionId, new Consumer() { // from class: com.android.server.people.PeopleService$LocalService$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    PeopleService.LocalService.lambda$requestPredictionUpdate$6((SessionInfo) obj);
                }
            });
        }

        public static /* synthetic */ void lambda$requestPredictionUpdate$6(SessionInfo sessionInfo) {
            sessionInfo.getPredictor().onRequestPredictionUpdate();
        }

        public void onDestroyPredictionSession(final AppPredictionSessionId appPredictionSessionId) {
            runForSession(appPredictionSessionId, new Consumer() { // from class: com.android.server.people.PeopleService$LocalService$$ExternalSyntheticLambda5
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    PeopleService.LocalService.this.lambda$onDestroyPredictionSession$7(appPredictionSessionId, (SessionInfo) obj);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDestroyPredictionSession$7(AppPredictionSessionId appPredictionSessionId, SessionInfo sessionInfo) {
            sessionInfo.onDestroy();
            this.mSessions.remove(appPredictionSessionId);
        }

        @Override // com.android.server.people.PeopleServiceInternal
        public void pruneDataForUser(int i, CancellationSignal cancellationSignal) {
            PeopleService.this.mDataManager.pruneDataForUser(i, cancellationSignal);
        }

        @Override // com.android.server.people.PeopleServiceInternal
        public byte[] getBackupPayload(int i) {
            return PeopleService.this.mDataManager.getBackupPayload(i);
        }

        @Override // com.android.server.people.PeopleServiceInternal
        public void restore(int i, byte[] bArr) {
            PeopleService.this.mDataManager.restore(i, bArr);
        }

        @VisibleForTesting
        public SessionInfo getSessionInfo(AppPredictionSessionId appPredictionSessionId) {
            return this.mSessions.get(appPredictionSessionId);
        }

        public final void runForSession(AppPredictionSessionId appPredictionSessionId, Consumer<SessionInfo> consumer) {
            SessionInfo sessionInfo = this.mSessions.get(appPredictionSessionId);
            if (sessionInfo == null) {
                Slog.e("PeopleService", "Failed to find the session: " + appPredictionSessionId);
                return;
            }
            consumer.accept(sessionInfo);
        }

        /* renamed from: invokePredictionCallback */
        public final void lambda$sortAppTargets$2(IPredictionCallback iPredictionCallback, List<AppTarget> list) {
            try {
                iPredictionCallback.onResult(new ParceledListSlice(list));
            } catch (RemoteException e) {
                Slog.e("PeopleService", "Failed to calling callback" + e);
            }
        }
    }
}
