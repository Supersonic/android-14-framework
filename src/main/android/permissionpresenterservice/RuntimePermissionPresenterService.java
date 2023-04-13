package android.permissionpresenterservice;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.p001pm.permission.IRuntimePermissionPresenter;
import android.content.p001pm.permission.RuntimePermissionPresentationInfo;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.RemoteCallback;
import com.android.internal.util.Preconditions;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import java.util.List;
@SystemApi
@Deprecated
/* loaded from: classes3.dex */
public abstract class RuntimePermissionPresenterService extends Service {
    private static final String KEY_RESULT = "android.content.pm.permission.RuntimePermissionPresenter.key.result";
    public static final String SERVICE_INTERFACE = "android.permissionpresenterservice.RuntimePermissionPresenterService";
    private Handler mHandler;

    public abstract List<RuntimePermissionPresentationInfo> onGetAppPermissions(String str);

    @Override // android.app.Service, android.content.ContextWrapper
    public final void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        this.mHandler = new Handler(base.getMainLooper());
    }

    /* renamed from: android.permissionpresenterservice.RuntimePermissionPresenterService$1 */
    /* loaded from: classes3.dex */
    class BinderC23121 extends IRuntimePermissionPresenter.Stub {
        BinderC23121() {
        }

        @Override // android.content.p001pm.permission.IRuntimePermissionPresenter
        public void getAppPermissions(String packageName, RemoteCallback callback) {
            Preconditions.checkNotNull(packageName, "packageName");
            Preconditions.checkNotNull(callback, "callback");
            RuntimePermissionPresenterService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: android.permissionpresenterservice.RuntimePermissionPresenterService$1$$ExternalSyntheticLambda0
                @Override // com.android.internal.util.function.TriConsumer
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((RuntimePermissionPresenterService) obj).getAppPermissions((String) obj2, (RemoteCallback) obj3);
                }
            }, RuntimePermissionPresenterService.this, packageName, callback));
        }
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return new BinderC23121();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getAppPermissions(String packageName, RemoteCallback callback) {
        List<RuntimePermissionPresentationInfo> permissions = onGetAppPermissions(packageName);
        if (permissions != null && !permissions.isEmpty()) {
            Bundle result = new Bundle();
            result.putParcelableList(KEY_RESULT, permissions);
            callback.sendResult(result);
            return;
        }
        callback.sendResult(null);
    }
}
