package com.android.server.searchui;

import android.app.AppGlobals;
import android.app.search.ISearchCallback;
import android.app.search.Query;
import android.app.search.SearchContext;
import android.app.search.SearchSessionId;
import android.app.search.SearchTargetEvent;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.service.search.ISearchUiService;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.infra.AbstractRemoteService;
import com.android.server.infra.AbstractPerUserSystemService;
import com.android.server.searchui.RemoteSearchUiService;
import com.android.server.searchui.SearchUiPerUserService;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public class SearchUiPerUserService extends AbstractPerUserSystemService<SearchUiPerUserService, SearchUiManagerService> implements RemoteSearchUiService.RemoteSearchUiServiceCallbacks {
    public static final String TAG = SearchUiPerUserService.class.getSimpleName();
    @GuardedBy({"mLock"})
    public RemoteSearchUiService mRemoteService;
    @GuardedBy({"mLock"})
    public final ArrayMap<SearchSessionId, SearchSessionInfo> mSessionInfos;
    @GuardedBy({"mLock"})
    public boolean mZombie;

    public SearchUiPerUserService(SearchUiManagerService searchUiManagerService, Object obj, int i) {
        super(searchUiManagerService, obj, i);
        this.mSessionInfos = new ArrayMap<>();
    }

    @Override // com.android.server.infra.AbstractPerUserSystemService
    public ServiceInfo newServiceInfoLocked(ComponentName componentName) throws PackageManager.NameNotFoundException {
        try {
            return AppGlobals.getPackageManager().getServiceInfo(componentName, 128L, this.mUserId);
        } catch (RemoteException unused) {
            throw new PackageManager.NameNotFoundException("Could not get service for " + componentName);
        }
    }

    @Override // com.android.server.infra.AbstractPerUserSystemService
    @GuardedBy({"mLock"})
    public boolean updateLocked(boolean z) {
        boolean updateLocked = super.updateLocked(z);
        if (updateLocked && !isEnabledLocked()) {
            updateRemoteServiceLocked();
        }
        return updateLocked;
    }

    @GuardedBy({"mLock"})
    public void onCreateSearchSessionLocked(final SearchContext searchContext, final SearchSessionId searchSessionId, IBinder iBinder) {
        if (!resolveService(searchSessionId, new AbstractRemoteService.AsyncRequest() { // from class: com.android.server.searchui.SearchUiPerUserService$$ExternalSyntheticLambda2
            public final void run(IInterface iInterface) {
                ((ISearchUiService) iInterface).onCreateSearchSession(searchContext, searchSessionId);
            }
        }) || this.mSessionInfos.containsKey(searchSessionId)) {
            return;
        }
        SearchSessionInfo searchSessionInfo = new SearchSessionInfo(searchSessionId, searchContext, iBinder, new IBinder.DeathRecipient() { // from class: com.android.server.searchui.SearchUiPerUserService$$ExternalSyntheticLambda3
            @Override // android.os.IBinder.DeathRecipient
            public final void binderDied() {
                SearchUiPerUserService.this.lambda$onCreateSearchSessionLocked$1(searchSessionId);
            }
        });
        if (searchSessionInfo.linkToDeath()) {
            this.mSessionInfos.put(searchSessionId, searchSessionInfo);
        } else {
            onDestroyLocked(searchSessionId);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateSearchSessionLocked$1(SearchSessionId searchSessionId) {
        synchronized (this.mLock) {
            onDestroyLocked(searchSessionId);
        }
    }

    @GuardedBy({"mLock"})
    public void notifyLocked(final SearchSessionId searchSessionId, final Query query, final SearchTargetEvent searchTargetEvent) {
        if (this.mSessionInfos.get(searchSessionId) == null) {
            return;
        }
        resolveService(searchSessionId, new AbstractRemoteService.AsyncRequest() { // from class: com.android.server.searchui.SearchUiPerUserService$$ExternalSyntheticLambda4
            public final void run(IInterface iInterface) {
                ((ISearchUiService) iInterface).onNotifyEvent(searchSessionId, query, searchTargetEvent);
            }
        });
    }

    @GuardedBy({"mLock"})
    public void queryLocked(final SearchSessionId searchSessionId, final Query query, final ISearchCallback iSearchCallback) {
        if (this.mSessionInfos.get(searchSessionId) == null) {
            return;
        }
        resolveService(searchSessionId, new AbstractRemoteService.AsyncRequest() { // from class: com.android.server.searchui.SearchUiPerUserService$$ExternalSyntheticLambda1
            public final void run(IInterface iInterface) {
                ((ISearchUiService) iInterface).onQuery(searchSessionId, query, iSearchCallback);
            }
        });
    }

    @GuardedBy({"mLock"})
    public void registerEmptyQueryResultUpdateCallbackLocked(final SearchSessionId searchSessionId, final ISearchCallback iSearchCallback) {
        SearchSessionInfo searchSessionInfo = this.mSessionInfos.get(searchSessionId);
        if (searchSessionInfo != null && resolveService(searchSessionId, new AbstractRemoteService.AsyncRequest() { // from class: com.android.server.searchui.SearchUiPerUserService$$ExternalSyntheticLambda0
            public final void run(IInterface iInterface) {
                ((ISearchUiService) iInterface).onRegisterEmptyQueryResultUpdateCallback(searchSessionId, iSearchCallback);
            }
        })) {
            searchSessionInfo.addCallbackLocked(iSearchCallback);
        }
    }

    @GuardedBy({"mLock"})
    public void unregisterEmptyQueryResultUpdateCallbackLocked(final SearchSessionId searchSessionId, final ISearchCallback iSearchCallback) {
        SearchSessionInfo searchSessionInfo = this.mSessionInfos.get(searchSessionId);
        if (searchSessionInfo != null && resolveService(searchSessionId, new AbstractRemoteService.AsyncRequest() { // from class: com.android.server.searchui.SearchUiPerUserService$$ExternalSyntheticLambda6
            public final void run(IInterface iInterface) {
                ((ISearchUiService) iInterface).onUnregisterEmptyQueryResultUpdateCallback(searchSessionId, iSearchCallback);
            }
        })) {
            searchSessionInfo.removeCallbackLocked(iSearchCallback);
        }
    }

    @GuardedBy({"mLock"})
    public void onDestroyLocked(final SearchSessionId searchSessionId) {
        if (isDebug()) {
            String str = TAG;
            Slog.d(str, "onDestroyLocked(): sessionId=" + searchSessionId);
        }
        SearchSessionInfo remove = this.mSessionInfos.remove(searchSessionId);
        if (remove == null) {
            return;
        }
        resolveService(searchSessionId, new AbstractRemoteService.AsyncRequest() { // from class: com.android.server.searchui.SearchUiPerUserService$$ExternalSyntheticLambda5
            public final void run(IInterface iInterface) {
                ((ISearchUiService) iInterface).onDestroy(searchSessionId);
            }
        });
        remove.destroy();
    }

    @Override // com.android.server.searchui.RemoteSearchUiService.RemoteSearchUiServiceCallbacks
    public void onConnectedStateChanged(boolean z) {
        if (isDebug()) {
            String str = TAG;
            Slog.d(str, "onConnectedStateChanged(): connected=" + z);
        }
        if (z) {
            synchronized (this.mLock) {
                if (this.mZombie) {
                    if (this.mRemoteService == null) {
                        Slog.w(TAG, "Cannot resurrect sessions because remote service is null");
                    } else {
                        this.mZombie = false;
                        resurrectSessionsLocked();
                    }
                }
            }
        }
    }

    public void onServiceDied(RemoteSearchUiService remoteSearchUiService) {
        if (isDebug()) {
            String str = TAG;
            Slog.w(str, "onServiceDied(): service=" + remoteSearchUiService);
        }
        synchronized (this.mLock) {
            this.mZombie = true;
        }
        updateRemoteServiceLocked();
    }

    @GuardedBy({"mLock"})
    public final void updateRemoteServiceLocked() {
        RemoteSearchUiService remoteSearchUiService = this.mRemoteService;
        if (remoteSearchUiService != null) {
            remoteSearchUiService.destroy();
            this.mRemoteService = null;
        }
    }

    public void onPackageUpdatedLocked() {
        if (isDebug()) {
            Slog.v(TAG, "onPackageUpdatedLocked()");
        }
        destroyAndRebindRemoteService();
    }

    public void onPackageRestartedLocked() {
        if (isDebug()) {
            Slog.v(TAG, "onPackageRestartedLocked()");
        }
        destroyAndRebindRemoteService();
    }

    public final void destroyAndRebindRemoteService() {
        if (this.mRemoteService == null) {
            return;
        }
        if (isDebug()) {
            Slog.d(TAG, "Destroying the old remote service.");
        }
        this.mRemoteService.destroy();
        this.mRemoteService = null;
        synchronized (this.mLock) {
            this.mZombie = true;
        }
        RemoteSearchUiService remoteServiceLocked = getRemoteServiceLocked();
        this.mRemoteService = remoteServiceLocked;
        if (remoteServiceLocked != null) {
            if (isDebug()) {
                Slog.d(TAG, "Rebinding to the new remote service.");
            }
            this.mRemoteService.reconnect();
        }
    }

    public final void resurrectSessionsLocked() {
        int size = this.mSessionInfos.size();
        if (isDebug()) {
            String str = TAG;
            Slog.d(str, "Resurrecting remote service (" + this.mRemoteService + ") on " + size + " sessions.");
        }
        for (SearchSessionInfo searchSessionInfo : this.mSessionInfos.values()) {
            searchSessionInfo.resurrectSessionLocked(this, searchSessionInfo.mToken);
        }
    }

    @GuardedBy({"mLock"})
    public boolean resolveService(SearchSessionId searchSessionId, AbstractRemoteService.AsyncRequest<ISearchUiService> asyncRequest) {
        RemoteSearchUiService remoteServiceLocked = getRemoteServiceLocked();
        if (remoteServiceLocked != null) {
            remoteServiceLocked.executeOnResolvedService(asyncRequest);
        }
        return remoteServiceLocked != null;
    }

    @GuardedBy({"mLock"})
    public final RemoteSearchUiService getRemoteServiceLocked() {
        if (this.mRemoteService == null) {
            String componentNameLocked = getComponentNameLocked();
            if (componentNameLocked == null) {
                if (((SearchUiManagerService) this.mMaster).verbose) {
                    Slog.v(TAG, "getRemoteServiceLocked(): not set");
                    return null;
                }
                return null;
            }
            this.mRemoteService = new RemoteSearchUiService(getContext(), "android.service.search.SearchUiService", ComponentName.unflattenFromString(componentNameLocked), this.mUserId, this, ((SearchUiManagerService) this.mMaster).isBindInstantServiceAllowed(), ((SearchUiManagerService) this.mMaster).verbose);
        }
        return this.mRemoteService;
    }

    /* loaded from: classes2.dex */
    public static final class SearchSessionInfo {
        public final RemoteCallbackList<ISearchCallback> mCallbacks = new RemoteCallbackList<>();
        public final IBinder.DeathRecipient mDeathRecipient;
        public final SearchContext mSearchContext;
        public final SearchSessionId mSessionId;
        public final IBinder mToken;

        public SearchSessionInfo(SearchSessionId searchSessionId, SearchContext searchContext, IBinder iBinder, IBinder.DeathRecipient deathRecipient) {
            String str = SearchUiPerUserService.TAG;
            Slog.d(str, "Creating SearchSessionInfo for session Id=" + searchSessionId);
            this.mSessionId = searchSessionId;
            this.mSearchContext = searchContext;
            this.mToken = iBinder;
            this.mDeathRecipient = deathRecipient;
        }

        public void addCallbackLocked(ISearchCallback iSearchCallback) {
            String str = SearchUiPerUserService.TAG;
            Slog.d(str, "Storing callback for session Id=" + this.mSessionId + " and callback=" + iSearchCallback.asBinder());
            this.mCallbacks.register(iSearchCallback);
        }

        public void removeCallbackLocked(ISearchCallback iSearchCallback) {
            String str = SearchUiPerUserService.TAG;
            Slog.d(str, "Removing callback for session Id=" + this.mSessionId + " and callback=" + iSearchCallback.asBinder());
            this.mCallbacks.unregister(iSearchCallback);
        }

        public boolean linkToDeath() {
            try {
                this.mToken.linkToDeath(this.mDeathRecipient, 0);
                return true;
            } catch (RemoteException unused) {
                String str = SearchUiPerUserService.TAG;
                Slog.w(str, "Caller is dead before session can be started, sessionId: " + this.mSessionId);
                return false;
            }
        }

        public void destroy() {
            String str = SearchUiPerUserService.TAG;
            Slog.d(str, "Removing all callbacks for session Id=" + this.mSessionId + " and " + this.mCallbacks.getRegisteredCallbackCount() + " callbacks.");
            IBinder iBinder = this.mToken;
            if (iBinder != null) {
                iBinder.unlinkToDeath(this.mDeathRecipient, 0);
            }
            this.mCallbacks.kill();
        }

        public void resurrectSessionLocked(final SearchUiPerUserService searchUiPerUserService, IBinder iBinder) {
            int registeredCallbackCount = this.mCallbacks.getRegisteredCallbackCount();
            String str = SearchUiPerUserService.TAG;
            Slog.d(str, "Resurrecting remote service (" + searchUiPerUserService.getRemoteServiceLocked() + ") for session Id=" + this.mSessionId + " and " + registeredCallbackCount + " callbacks.");
            searchUiPerUserService.onCreateSearchSessionLocked(this.mSearchContext, this.mSessionId, iBinder);
            this.mCallbacks.broadcast(new Consumer() { // from class: com.android.server.searchui.SearchUiPerUserService$SearchSessionInfo$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    SearchUiPerUserService.SearchSessionInfo.this.lambda$resurrectSessionLocked$0(searchUiPerUserService, (ISearchCallback) obj);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$resurrectSessionLocked$0(SearchUiPerUserService searchUiPerUserService, ISearchCallback iSearchCallback) {
            searchUiPerUserService.registerEmptyQueryResultUpdateCallbackLocked(this.mSessionId, iSearchCallback);
        }
    }
}
