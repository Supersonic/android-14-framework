package com.android.server.ambientcontext;

import android.app.ambientcontext.AmbientContextEvent;
import android.app.ambientcontext.AmbientContextEventRequest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteCallback;
import android.service.wearable.IWearableSensingService;
import android.util.Slog;
import com.android.internal.infra.ServiceConnector;
import java.io.PrintWriter;
/* loaded from: classes.dex */
final class RemoteWearableSensingService extends ServiceConnector.Impl<IWearableSensingService> implements RemoteAmbientDetectionService {
    public static final String TAG = RemoteWearableSensingService.class.getSimpleName();

    public long getAutoDisconnectTimeoutMs() {
        return -1L;
    }

    public RemoteWearableSensingService(Context context, ComponentName componentName, int i) {
        super(context, new Intent("android.service.wearable.WearableSensingService").setComponent(componentName), 67112960, i, new RemoteWearableSensingService$$ExternalSyntheticLambda1());
        connect();
    }

    @Override // com.android.server.ambientcontext.RemoteAmbientDetectionService
    public void startDetection(final AmbientContextEventRequest ambientContextEventRequest, final String str, final RemoteCallback remoteCallback, final RemoteCallback remoteCallback2) {
        String str2 = TAG;
        Slog.i(str2, "Start detection for " + ambientContextEventRequest.getEventTypes());
        post(new ServiceConnector.VoidJob() { // from class: com.android.server.ambientcontext.RemoteWearableSensingService$$ExternalSyntheticLambda2
            public final void runNoResult(Object obj) {
                ((IWearableSensingService) obj).startDetection(ambientContextEventRequest, str, remoteCallback, remoteCallback2);
            }
        });
    }

    @Override // com.android.server.ambientcontext.RemoteAmbientDetectionService
    public void stopDetection(final String str) {
        String str2 = TAG;
        Slog.i(str2, "Stop detection for " + str);
        post(new ServiceConnector.VoidJob() { // from class: com.android.server.ambientcontext.RemoteWearableSensingService$$ExternalSyntheticLambda0
            public final void runNoResult(Object obj) {
                ((IWearableSensingService) obj).stopDetection(str);
            }
        });
    }

    @Override // com.android.server.ambientcontext.RemoteAmbientDetectionService
    public void queryServiceStatus(@AmbientContextEvent.EventCode final int[] iArr, final String str, final RemoteCallback remoteCallback) {
        String str2 = TAG;
        Slog.i(str2, "Query status for " + str);
        post(new ServiceConnector.VoidJob() { // from class: com.android.server.ambientcontext.RemoteWearableSensingService$$ExternalSyntheticLambda3
            public final void runNoResult(Object obj) {
                ((IWearableSensingService) obj).queryServiceStatus(iArr, str, remoteCallback);
            }
        });
    }

    @Override // com.android.server.ambientcontext.RemoteAmbientDetectionService
    public void dump(String str, PrintWriter printWriter) {
        super.dump(str, printWriter);
    }

    @Override // com.android.server.ambientcontext.RemoteAmbientDetectionService
    public void unbind() {
        super.unbind();
    }
}
