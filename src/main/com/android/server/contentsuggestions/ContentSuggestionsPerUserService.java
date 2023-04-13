package com.android.server.contentsuggestions;

import android.app.AppGlobals;
import android.app.contentsuggestions.ClassificationsRequest;
import android.app.contentsuggestions.IClassificationsCallback;
import android.app.contentsuggestions.ISelectionsCallback;
import android.app.contentsuggestions.SelectionsRequest;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.hardware.HardwareBuffer;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.server.LocalServices;
import com.android.server.contentsuggestions.RemoteContentSuggestionsService;
import com.android.server.infra.AbstractPerUserSystemService;
import com.android.server.p014wm.ActivityTaskManagerInternal;
/* loaded from: classes.dex */
public final class ContentSuggestionsPerUserService extends AbstractPerUserSystemService<ContentSuggestionsPerUserService, ContentSuggestionsManagerService> {
    public static final String TAG = "ContentSuggestionsPerUserService";
    public final ActivityTaskManagerInternal mActivityTaskManagerInternal;
    @GuardedBy({"mLock"})
    public RemoteContentSuggestionsService mRemoteService;

    public ContentSuggestionsPerUserService(ContentSuggestionsManagerService contentSuggestionsManagerService, Object obj, int i) {
        super(contentSuggestionsManagerService, obj, i);
        this.mActivityTaskManagerInternal = (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);
    }

    @Override // com.android.server.infra.AbstractPerUserSystemService
    @GuardedBy({"mLock"})
    public ServiceInfo newServiceInfoLocked(ComponentName componentName) throws PackageManager.NameNotFoundException {
        try {
            ServiceInfo serviceInfo = AppGlobals.getPackageManager().getServiceInfo(componentName, 128L, this.mUserId);
            if ("android.permission.BIND_CONTENT_SUGGESTIONS_SERVICE".equals(serviceInfo.permission)) {
                return serviceInfo;
            }
            String str = TAG;
            Slog.w(str, "ContentSuggestionsService from '" + serviceInfo.packageName + "' does not require permission android.permission.BIND_CONTENT_SUGGESTIONS_SERVICE");
            throw new SecurityException("Service does not require permission android.permission.BIND_CONTENT_SUGGESTIONS_SERVICE");
        } catch (RemoteException unused) {
            throw new PackageManager.NameNotFoundException("Could not get service for " + componentName);
        }
    }

    @Override // com.android.server.infra.AbstractPerUserSystemService
    @GuardedBy({"mLock"})
    public boolean updateLocked(boolean z) {
        boolean updateLocked = super.updateLocked(z);
        updateRemoteServiceLocked();
        return updateLocked;
    }

    @GuardedBy({"mLock"})
    public void provideContextImageFromBitmapLocked(Bundle bundle) {
        provideContextImageLocked(-1, null, 0, bundle);
    }

    @GuardedBy({"mLock"})
    public void provideContextImageLocked(int i, HardwareBuffer hardwareBuffer, int i2, Bundle bundle) {
        RemoteContentSuggestionsService ensureRemoteServiceLocked = ensureRemoteServiceLocked();
        if (ensureRemoteServiceLocked != null) {
            ensureRemoteServiceLocked.provideContextImage(i, hardwareBuffer, i2, bundle);
        }
    }

    @GuardedBy({"mLock"})
    public void suggestContentSelectionsLocked(SelectionsRequest selectionsRequest, ISelectionsCallback iSelectionsCallback) {
        RemoteContentSuggestionsService ensureRemoteServiceLocked = ensureRemoteServiceLocked();
        if (ensureRemoteServiceLocked != null) {
            ensureRemoteServiceLocked.suggestContentSelections(selectionsRequest, iSelectionsCallback);
        }
    }

    @GuardedBy({"mLock"})
    public void classifyContentSelectionsLocked(ClassificationsRequest classificationsRequest, IClassificationsCallback iClassificationsCallback) {
        RemoteContentSuggestionsService ensureRemoteServiceLocked = ensureRemoteServiceLocked();
        if (ensureRemoteServiceLocked != null) {
            ensureRemoteServiceLocked.classifyContentSelections(classificationsRequest, iClassificationsCallback);
        }
    }

    @GuardedBy({"mLock"})
    public void notifyInteractionLocked(String str, Bundle bundle) {
        RemoteContentSuggestionsService ensureRemoteServiceLocked = ensureRemoteServiceLocked();
        if (ensureRemoteServiceLocked != null) {
            ensureRemoteServiceLocked.notifyInteraction(str, bundle);
        }
    }

    @GuardedBy({"mLock"})
    public final void updateRemoteServiceLocked() {
        RemoteContentSuggestionsService remoteContentSuggestionsService = this.mRemoteService;
        if (remoteContentSuggestionsService != null) {
            remoteContentSuggestionsService.destroy();
            this.mRemoteService = null;
        }
    }

    @GuardedBy({"mLock"})
    public final RemoteContentSuggestionsService ensureRemoteServiceLocked() {
        if (this.mRemoteService == null) {
            String componentNameLocked = getComponentNameLocked();
            if (componentNameLocked == null) {
                if (((ContentSuggestionsManagerService) this.mMaster).verbose) {
                    Slog.v(TAG, "ensureRemoteServiceLocked(): not set");
                    return null;
                }
                return null;
            }
            this.mRemoteService = new RemoteContentSuggestionsService(getContext(), ComponentName.unflattenFromString(componentNameLocked), this.mUserId, new RemoteContentSuggestionsService.Callbacks() { // from class: com.android.server.contentsuggestions.ContentSuggestionsPerUserService.1
                public void onServiceDied(RemoteContentSuggestionsService remoteContentSuggestionsService) {
                    Slog.w(ContentSuggestionsPerUserService.TAG, "remote content suggestions service died");
                    ContentSuggestionsPerUserService.this.updateRemoteServiceLocked();
                }
            }, ((ContentSuggestionsManagerService) this.mMaster).isBindInstantServiceAllowed(), ((ContentSuggestionsManagerService) this.mMaster).verbose);
        }
        return this.mRemoteService;
    }
}
