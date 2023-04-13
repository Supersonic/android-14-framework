package com.android.server.search;

import android.app.ISearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.search.SearchManagerService;
import com.android.server.statusbar.StatusBarManagerInternal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
/* loaded from: classes2.dex */
public class SearchManagerService extends ISearchManager.Stub {
    public final Context mContext;
    public final Handler mHandler;
    @GuardedBy({"mSearchables"})
    public final SparseArray<Searchables> mSearchables = new SparseArray<>();

    /* loaded from: classes2.dex */
    public static class Lifecycle extends SystemService {
        public SearchManagerService mService;

        public Lifecycle(Context context) {
            super(context);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v0, types: [com.android.server.search.SearchManagerService, android.os.IBinder] */
        @Override // com.android.server.SystemService
        public void onStart() {
            ?? searchManagerService = new SearchManagerService(getContext());
            this.mService = searchManagerService;
            publishBinderService("search", searchManagerService);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onUserUnlocking$0(SystemService.TargetUser targetUser) {
            this.mService.onUnlockUser(targetUser.getUserIdentifier());
        }

        @Override // com.android.server.SystemService
        public void onUserUnlocking(final SystemService.TargetUser targetUser) {
            this.mService.mHandler.post(new Runnable() { // from class: com.android.server.search.SearchManagerService$Lifecycle$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SearchManagerService.Lifecycle.this.lambda$onUserUnlocking$0(targetUser);
                }
            });
        }

        @Override // com.android.server.SystemService
        public void onUserStopped(SystemService.TargetUser targetUser) {
            this.mService.onCleanupUser(targetUser.getUserIdentifier());
        }
    }

    public SearchManagerService(Context context) {
        this.mContext = context;
        new MyPackageMonitor().register(context, (Looper) null, UserHandle.ALL, true);
        new GlobalSearchProviderObserver(context.getContentResolver());
        this.mHandler = BackgroundThread.getHandler();
    }

    public final Searchables getSearchables(int i) {
        return getSearchables(i, false);
    }

    public final Searchables getSearchables(int i, boolean z) {
        Searchables searchables;
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            UserManager userManager = (UserManager) this.mContext.getSystemService(UserManager.class);
            if (userManager.getUserInfo(i) == null) {
                throw new IllegalStateException("User " + i + " doesn't exist");
            } else if (!userManager.isUserUnlockingOrUnlocked(i)) {
                throw new IllegalStateException("User " + i + " isn't unlocked");
            } else {
                Binder.restoreCallingIdentity(clearCallingIdentity);
                synchronized (this.mSearchables) {
                    searchables = this.mSearchables.get(i);
                    if (searchables == null) {
                        searchables = new Searchables(this.mContext, i);
                        searchables.updateSearchableList();
                        this.mSearchables.append(i, searchables);
                    } else if (z) {
                        searchables.updateSearchableList();
                    }
                }
                return searchables;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public final void onUnlockUser(int i) {
        try {
            getSearchables(i, true);
        } catch (IllegalStateException unused) {
        }
    }

    public final void onCleanupUser(int i) {
        synchronized (this.mSearchables) {
            this.mSearchables.remove(i);
        }
    }

    /* loaded from: classes2.dex */
    public class MyPackageMonitor extends PackageMonitor {
        public MyPackageMonitor() {
        }

        public void onSomePackagesChanged() {
            updateSearchables();
        }

        public void onPackageModified(String str) {
            updateSearchables();
        }

        public final void updateSearchables() {
            int changingUserId = getChangingUserId();
            synchronized (SearchManagerService.this.mSearchables) {
                int i = 0;
                while (true) {
                    if (i >= SearchManagerService.this.mSearchables.size()) {
                        break;
                    } else if (changingUserId == SearchManagerService.this.mSearchables.keyAt(i)) {
                        ((Searchables) SearchManagerService.this.mSearchables.valueAt(i)).updateSearchableList();
                        break;
                    } else {
                        i++;
                    }
                }
            }
            Intent intent = new Intent("android.search.action.SEARCHABLES_CHANGED");
            intent.addFlags(603979776);
            SearchManagerService.this.mContext.sendBroadcastAsUser(intent, new UserHandle(changingUserId));
        }
    }

    /* loaded from: classes2.dex */
    public class GlobalSearchProviderObserver extends ContentObserver {
        public final ContentResolver mResolver;

        public GlobalSearchProviderObserver(ContentResolver contentResolver) {
            super(null);
            this.mResolver = contentResolver;
            contentResolver.registerContentObserver(Settings.Secure.getUriFor("search_global_search_activity"), false, this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            synchronized (SearchManagerService.this.mSearchables) {
                for (int i = 0; i < SearchManagerService.this.mSearchables.size(); i++) {
                    ((Searchables) SearchManagerService.this.mSearchables.valueAt(i)).updateSearchableList();
                }
            }
            Intent intent = new Intent("android.search.action.GLOBAL_SEARCH_ACTIVITY_CHANGED");
            intent.addFlags(536870912);
            SearchManagerService.this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
        }
    }

    public SearchableInfo getSearchableInfo(ComponentName componentName) {
        if (componentName == null) {
            Log.e("SearchManagerService", "getSearchableInfo(), activity == null");
            return null;
        }
        return getSearchables(UserHandle.getCallingUserId()).getSearchableInfo(componentName);
    }

    public List<SearchableInfo> getSearchablesInGlobalSearch() {
        return getSearchables(UserHandle.getCallingUserId()).getSearchablesInGlobalSearchList();
    }

    public List<ResolveInfo> getGlobalSearchActivities() {
        return getSearchables(UserHandle.getCallingUserId()).getGlobalSearchActivities();
    }

    public ComponentName getGlobalSearchActivity() {
        return getSearchables(UserHandle.getCallingUserId()).getGlobalSearchActivity();
    }

    public ComponentName getWebSearchActivity() {
        return getSearchables(UserHandle.getCallingUserId()).getWebSearchActivity();
    }

    public void launchAssist(int i, Bundle bundle) {
        StatusBarManagerInternal statusBarManagerInternal = (StatusBarManagerInternal) LocalServices.getService(StatusBarManagerInternal.class);
        if (statusBarManagerInternal != null) {
            statusBarManagerInternal.startAssist(bundle);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, "SearchManagerService", printWriter)) {
            PrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
            synchronized (this.mSearchables) {
                for (int i = 0; i < this.mSearchables.size(); i++) {
                    indentingPrintWriter.print("\nUser: ");
                    indentingPrintWriter.println(this.mSearchables.keyAt(i));
                    indentingPrintWriter.increaseIndent();
                    this.mSearchables.valueAt(i).dump(fileDescriptor, indentingPrintWriter, strArr);
                    indentingPrintWriter.decreaseIndent();
                }
            }
        }
    }
}
