package com.android.server.translation;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.service.translation.ITranslationService;
import android.util.Slog;
import android.view.translation.TranslationContext;
import com.android.internal.infra.ServiceConnector;
import com.android.internal.os.IResultReceiver;
import java.util.function.Function;
/* loaded from: classes2.dex */
final class RemoteTranslationService extends ServiceConnector.Impl<ITranslationService> {
    public static final String TAG = RemoteTranslationService.class.getSimpleName();
    private final ComponentName mComponentName;
    private final long mIdleUnbindTimeoutMs;
    private final IBinder mRemoteCallback;
    private final int mRequestTimeoutMs;

    public RemoteTranslationService(Context context, ComponentName componentName, int i, boolean z, IBinder iBinder) {
        super(context, new Intent("android.service.translation.TranslationService").setComponent(componentName), z ? 4194304 : 0, i, new Function() { // from class: com.android.server.translation.RemoteTranslationService$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ITranslationService.Stub.asInterface((IBinder) obj);
            }
        });
        this.mIdleUnbindTimeoutMs = 0L;
        this.mRequestTimeoutMs = 5000;
        this.mComponentName = componentName;
        this.mRemoteCallback = iBinder;
        connect();
    }

    public void onServiceConnectionStatusChanged(ITranslationService iTranslationService, boolean z) {
        try {
            if (z) {
                iTranslationService.onConnected(this.mRemoteCallback);
            } else {
                iTranslationService.onDisconnected();
            }
        } catch (Exception e) {
            String str = TAG;
            Slog.w(str, "Exception calling onServiceConnectionStatusChanged(" + z + "): ", e);
        }
    }

    public long getAutoDisconnectTimeoutMs() {
        return this.mIdleUnbindTimeoutMs;
    }

    public void onSessionCreated(final TranslationContext translationContext, final int i, final IResultReceiver iResultReceiver) {
        run(new ServiceConnector.VoidJob() { // from class: com.android.server.translation.RemoteTranslationService$$ExternalSyntheticLambda1
            public final void runNoResult(Object obj) {
                ((ITranslationService) obj).onCreateTranslationSession(translationContext, i, iResultReceiver);
            }
        });
    }

    public void onTranslationCapabilitiesRequest(final int i, final int i2, final ResultReceiver resultReceiver) {
        run(new ServiceConnector.VoidJob() { // from class: com.android.server.translation.RemoteTranslationService$$ExternalSyntheticLambda2
            public final void runNoResult(Object obj) {
                ((ITranslationService) obj).onTranslationCapabilitiesRequest(i, i2, resultReceiver);
            }
        });
    }
}
