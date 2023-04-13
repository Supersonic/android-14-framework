package com.android.server.searchui;

import android.app.ActivityManagerInternal;
import android.app.search.ISearchCallback;
import android.app.search.ISearchUiManager;
import android.app.search.Query;
import android.app.search.SearchContext;
import android.app.search.SearchSessionId;
import android.app.search.SearchTargetEvent;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.util.Slog;
import com.android.server.LocalServices;
import com.android.server.infra.AbstractMasterSystemService;
import com.android.server.infra.FrameworkResourcesServiceNameResolver;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import java.io.FileDescriptor;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public class SearchUiManagerService extends AbstractMasterSystemService<SearchUiManagerService, SearchUiPerUserService> {
    public static final String TAG = "SearchUiManagerService";
    public ActivityTaskManagerInternal mActivityTaskManagerInternal;

    @Override // com.android.server.infra.AbstractMasterSystemService
    public int getMaximumTemporaryServiceDurationMs() {
        return 120000;
    }

    public SearchUiManagerService(Context context) {
        super(context, new FrameworkResourcesServiceNameResolver(context, 17039900), null, 17);
        this.mActivityTaskManagerInternal = (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public SearchUiPerUserService newServiceLocked(int i, boolean z) {
        return new SearchUiPerUserService(this, this.mLock, i);
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("search_ui", new SearchUiManagerStub());
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void enforceCallingPermissionForManagement() {
        getContext().enforceCallingPermission("android.permission.MANAGE_SEARCH_UI", TAG);
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void onServicePackageUpdatedLocked(int i) {
        SearchUiPerUserService peekServiceForUserLocked = peekServiceForUserLocked(i);
        if (peekServiceForUserLocked != null) {
            peekServiceForUserLocked.onPackageUpdatedLocked();
        }
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void onServicePackageRestartedLocked(int i) {
        SearchUiPerUserService peekServiceForUserLocked = peekServiceForUserLocked(i);
        if (peekServiceForUserLocked != null) {
            peekServiceForUserLocked.onPackageRestartedLocked();
        }
    }

    /* loaded from: classes2.dex */
    public class SearchUiManagerStub extends ISearchUiManager.Stub {
        public SearchUiManagerStub() {
        }

        public void createSearchSession(final SearchContext searchContext, final SearchSessionId searchSessionId, final IBinder iBinder) {
            runForUserLocked("createSearchSession", searchSessionId, new Consumer() { // from class: com.android.server.searchui.SearchUiManagerService$SearchUiManagerStub$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((SearchUiPerUserService) obj).onCreateSearchSessionLocked(searchContext, searchSessionId, iBinder);
                }
            });
        }

        public void notifyEvent(final SearchSessionId searchSessionId, final Query query, final SearchTargetEvent searchTargetEvent) {
            runForUserLocked("notifyEvent", searchSessionId, new Consumer() { // from class: com.android.server.searchui.SearchUiManagerService$SearchUiManagerStub$$ExternalSyntheticLambda5
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((SearchUiPerUserService) obj).notifyLocked(searchSessionId, query, searchTargetEvent);
                }
            });
        }

        public void query(final SearchSessionId searchSessionId, final Query query, final ISearchCallback iSearchCallback) {
            runForUserLocked("query", searchSessionId, new Consumer() { // from class: com.android.server.searchui.SearchUiManagerService$SearchUiManagerStub$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((SearchUiPerUserService) obj).queryLocked(searchSessionId, query, iSearchCallback);
                }
            });
        }

        public void registerEmptyQueryResultUpdateCallback(final SearchSessionId searchSessionId, final ISearchCallback iSearchCallback) {
            runForUserLocked("registerEmptyQueryResultUpdateCallback", searchSessionId, new Consumer() { // from class: com.android.server.searchui.SearchUiManagerService$SearchUiManagerStub$$ExternalSyntheticLambda4
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((SearchUiPerUserService) obj).registerEmptyQueryResultUpdateCallbackLocked(searchSessionId, iSearchCallback);
                }
            });
        }

        public void unregisterEmptyQueryResultUpdateCallback(final SearchSessionId searchSessionId, final ISearchCallback iSearchCallback) {
            runForUserLocked("unregisterEmptyQueryResultUpdateCallback", searchSessionId, new Consumer() { // from class: com.android.server.searchui.SearchUiManagerService$SearchUiManagerStub$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((SearchUiPerUserService) obj).unregisterEmptyQueryResultUpdateCallbackLocked(searchSessionId, iSearchCallback);
                }
            });
        }

        public void destroySearchSession(final SearchSessionId searchSessionId) {
            runForUserLocked("destroySearchSession", searchSessionId, new Consumer() { // from class: com.android.server.searchui.SearchUiManagerService$SearchUiManagerStub$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((SearchUiPerUserService) obj).onDestroyLocked(searchSessionId);
                }
            });
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
            new SearchUiManagerServiceShellCommand(SearchUiManagerService.this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
        }

        public final void runForUserLocked(String str, SearchSessionId searchSessionId, Consumer<SearchUiPerUserService> consumer) {
            int handleIncomingUser = ((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), searchSessionId.getUserId(), false, 0, (String) null, (String) null);
            if (!SearchUiManagerService.this.mServiceNameResolver.isTemporary(handleIncomingUser) && !SearchUiManagerService.this.mActivityTaskManagerInternal.isCallerRecents(Binder.getCallingUid())) {
                String str2 = "Permission Denial: " + str + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid();
                Slog.w(SearchUiManagerService.TAG, str2);
                throw new SecurityException(str2);
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (SearchUiManagerService.this.mLock) {
                    consumer.accept((SearchUiPerUserService) SearchUiManagerService.this.getServiceForUserLocked(handleIncomingUser));
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }
}
