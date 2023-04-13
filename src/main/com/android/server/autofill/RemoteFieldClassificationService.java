package com.android.server.autofill;

import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.os.ICancellationSignal;
import android.os.RemoteException;
import android.service.assist.classification.FieldClassificationRequest;
import android.service.assist.classification.FieldClassificationResponse;
import android.service.assist.classification.IFieldClassificationCallback;
import android.service.assist.classification.IFieldClassificationService;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.infra.ServiceConnector;
import java.util.function.Function;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class RemoteFieldClassificationService extends ServiceConnector.Impl<IFieldClassificationService> {
    public static final String TAG = "Autofill" + RemoteFieldClassificationService.class.getSimpleName();
    private final ComponentName mComponentName;

    /* loaded from: classes.dex */
    public interface FieldClassificationServiceCallbacks {
        void onClassificationRequestSuccess(FieldClassificationResponse fieldClassificationResponse);
    }

    public long getAutoDisconnectTimeoutMs() {
        return 0L;
    }

    public RemoteFieldClassificationService(Context context, ComponentName componentName, int i, int i2) {
        super(context, new Intent("android.service.assist.classification.FieldClassificationService").setComponent(componentName), 0, i2, new Function() { // from class: com.android.server.autofill.RemoteFieldClassificationService$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return IFieldClassificationService.Stub.asInterface((IBinder) obj);
            }
        });
        this.mComponentName = componentName;
        if (Helper.sDebug) {
            String str = TAG;
            Slog.d(str, "About to connect to serviceName: " + componentName);
        }
        connect();
    }

    public static Pair<ServiceInfo, ComponentName> getComponentName(String str, int i, boolean z) {
        int i2 = !z ? 1048704 : 128;
        try {
            ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
            ServiceInfo serviceInfo = AppGlobals.getPackageManager().getServiceInfo(unflattenFromString, i2, i);
            if (serviceInfo == null) {
                String str2 = TAG;
                Slog.e(str2, "Bad service name for flags " + i2 + ": " + str);
                return null;
            }
            return new Pair<>(serviceInfo, unflattenFromString);
        } catch (Exception e) {
            String str3 = TAG;
            Slog.e(str3, "Error getting service info for '" + str + "': " + e);
            return null;
        }
    }

    public void onServiceConnectionStatusChanged(IFieldClassificationService iFieldClassificationService, boolean z) {
        try {
            if (z) {
                iFieldClassificationService.onConnected(false, false);
            } else {
                iFieldClassificationService.onDisconnected();
            }
        } catch (Exception e) {
            String str = TAG;
            Slog.w(str, "Exception calling onServiceConnectionStatusChanged(" + z + "): ", e);
        }
    }

    public void onFieldClassificationRequest(final FieldClassificationRequest fieldClassificationRequest, final FieldClassificationServiceCallbacks fieldClassificationServiceCallbacks) {
        if (Helper.sVerbose) {
            String str = TAG;
            Slog.v(str, "onFieldClassificationRequest request:" + fieldClassificationRequest);
        }
        run(new ServiceConnector.VoidJob() { // from class: com.android.server.autofill.RemoteFieldClassificationService$$ExternalSyntheticLambda1
            public final void runNoResult(Object obj) {
                RemoteFieldClassificationService.this.lambda$onFieldClassificationRequest$0(fieldClassificationRequest, fieldClassificationServiceCallbacks, (IFieldClassificationService) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onFieldClassificationRequest$0(FieldClassificationRequest fieldClassificationRequest, final FieldClassificationServiceCallbacks fieldClassificationServiceCallbacks, IFieldClassificationService iFieldClassificationService) throws Exception {
        iFieldClassificationService.onFieldClassificationRequest(fieldClassificationRequest, new IFieldClassificationCallback.Stub() { // from class: com.android.server.autofill.RemoteFieldClassificationService.1
            public void cancel() throws RemoteException {
            }

            public boolean isCompleted() throws RemoteException {
                return false;
            }

            public void onCancellable(ICancellationSignal iCancellationSignal) {
                if (Helper.sDebug) {
                    Log.d(RemoteFieldClassificationService.TAG, "onCancellable");
                }
            }

            public void onSuccess(FieldClassificationResponse fieldClassificationResponse) {
                if (Helper.sDebug) {
                    String str = RemoteFieldClassificationService.TAG;
                    Log.d(str, "onSuccess Response: " + fieldClassificationResponse);
                }
                fieldClassificationServiceCallbacks.onClassificationRequestSuccess(fieldClassificationResponse);
            }

            public void onFailure() {
                if (Helper.sDebug) {
                    Log.d(RemoteFieldClassificationService.TAG, "onFailure");
                }
            }
        });
    }
}
