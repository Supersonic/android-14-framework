package com.android.server.autofill;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteCallback;
import android.service.autofill.IInlineSuggestionRenderService;
import android.service.autofill.IInlineSuggestionUiCallback;
import android.service.autofill.InlinePresentation;
import android.util.Slog;
import com.android.internal.infra.AbstractMultiplePendingRequestsRemoteService;
import com.android.internal.infra.AbstractRemoteService;
/* loaded from: classes.dex */
public final class RemoteInlineSuggestionRenderService extends AbstractMultiplePendingRequestsRemoteService<RemoteInlineSuggestionRenderService, IInlineSuggestionRenderService> {
    public final long mIdleUnbindTimeoutMs;

    /* loaded from: classes.dex */
    public interface InlineSuggestionRenderCallbacks extends AbstractRemoteService.VultureCallback<RemoteInlineSuggestionRenderService> {
    }

    public long getTimeoutIdleBindMillis() {
        return 0L;
    }

    public RemoteInlineSuggestionRenderService(Context context, ComponentName componentName, String str, int i, InlineSuggestionRenderCallbacks inlineSuggestionRenderCallbacks, boolean z, boolean z2) {
        super(context, str, componentName, i, inlineSuggestionRenderCallbacks, context.getMainThreadHandler(), z ? 4194304 : 0, z2, 2);
        this.mIdleUnbindTimeoutMs = 0L;
        ensureBound();
    }

    public IInlineSuggestionRenderService getServiceInterface(IBinder iBinder) {
        return IInlineSuggestionRenderService.Stub.asInterface(iBinder);
    }

    public void handleOnConnectedStateChanged(boolean z) {
        if (z && getTimeoutIdleBindMillis() != 0) {
            scheduleUnbind();
        }
        super.handleOnConnectedStateChanged(z);
    }

    public void ensureBound() {
        scheduleBind();
    }

    public void renderSuggestion(final IInlineSuggestionUiCallback iInlineSuggestionUiCallback, final InlinePresentation inlinePresentation, final int i, final int i2, final IBinder iBinder, final int i3, final int i4, final int i5) {
        scheduleAsyncRequest(new AbstractRemoteService.AsyncRequest() { // from class: com.android.server.autofill.RemoteInlineSuggestionRenderService$$ExternalSyntheticLambda2
            public final void run(IInterface iInterface) {
                ((IInlineSuggestionRenderService) iInterface).renderSuggestion(iInlineSuggestionUiCallback, inlinePresentation, i, i2, iBinder, i3, i4, i5);
            }
        });
    }

    public void getInlineSuggestionsRendererInfo(final RemoteCallback remoteCallback) {
        scheduleAsyncRequest(new AbstractRemoteService.AsyncRequest() { // from class: com.android.server.autofill.RemoteInlineSuggestionRenderService$$ExternalSyntheticLambda1
            public final void run(IInterface iInterface) {
                ((IInlineSuggestionRenderService) iInterface).getInlineSuggestionsRendererInfo(remoteCallback);
            }
        });
    }

    public void destroySuggestionViews(final int i, final int i2) {
        scheduleAsyncRequest(new AbstractRemoteService.AsyncRequest() { // from class: com.android.server.autofill.RemoteInlineSuggestionRenderService$$ExternalSyntheticLambda0
            public final void run(IInterface iInterface) {
                ((IInlineSuggestionRenderService) iInterface).destroySuggestionViews(i, i2);
            }
        });
    }

    public static ServiceInfo getServiceInfo(Context context, int i) {
        String servicesSystemSharedLibraryPackageName = context.getPackageManager().getServicesSystemSharedLibraryPackageName();
        if (servicesSystemSharedLibraryPackageName == null) {
            Slog.w("RemoteInlineSuggestionRenderService", "no external services package!");
            return null;
        }
        Intent intent = new Intent("android.service.autofill.InlineSuggestionRenderService");
        intent.setPackage(servicesSystemSharedLibraryPackageName);
        ResolveInfo resolveServiceAsUser = context.getPackageManager().resolveServiceAsUser(intent, 132, i);
        ServiceInfo serviceInfo = resolveServiceAsUser == null ? null : resolveServiceAsUser.serviceInfo;
        if (resolveServiceAsUser == null || serviceInfo == null) {
            Slog.w("RemoteInlineSuggestionRenderService", "No valid components found.");
            return null;
        } else if ("android.permission.BIND_INLINE_SUGGESTION_RENDER_SERVICE".equals(serviceInfo.permission)) {
            return serviceInfo;
        } else {
            Slog.w("RemoteInlineSuggestionRenderService", serviceInfo.name + " does not require permission android.permission.BIND_INLINE_SUGGESTION_RENDER_SERVICE");
            return null;
        }
    }

    public static ComponentName getServiceComponentName(Context context, int i) {
        ServiceInfo serviceInfo = getServiceInfo(context, i);
        if (serviceInfo == null) {
            return null;
        }
        ComponentName componentName = new ComponentName(serviceInfo.packageName, serviceInfo.name);
        if (Helper.sVerbose) {
            Slog.v("RemoteInlineSuggestionRenderService", "getServiceComponentName(): " + componentName);
        }
        return componentName;
    }
}
