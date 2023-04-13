package android.permission;

import android.app.AppOpsManager;
import android.content.AttributionSourceState;
import android.content.Context;
import android.content.p001pm.PackageManager;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.permission.IPermissionChecker;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes3.dex */
public class PermissionCheckerManager {
    public static final int PERMISSION_GRANTED = 0;
    public static final int PERMISSION_HARD_DENIED = 2;
    public static final int PERMISSION_SOFT_DENIED = 1;
    private final Context mContext;
    private final PackageManager mPackageManager;
    private final IPermissionChecker mService = IPermissionChecker.Stub.asInterface(ServiceManager.getServiceOrThrow(Context.PERMISSION_CHECKER_SERVICE));

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface PermissionResult {
    }

    public PermissionCheckerManager(Context context) throws ServiceManager.ServiceNotFoundException {
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
    }

    public int checkPermission(String permission, AttributionSourceState attributionSource, String message, boolean forDataDelivery, boolean startDataDelivery, boolean fromDatasource, int attributedOp) {
        Objects.requireNonNull(permission);
        Objects.requireNonNull(attributionSource);
        if (AppOpsManager.permissionToOpCode(permission) == -1) {
            if (!fromDatasource) {
                return this.mContext.checkPermission(permission, attributionSource.pid, attributionSource.uid) == 0 ? 0 : 2;
            } else if (attributionSource.next != null && attributionSource.next.length > 0) {
                return this.mContext.checkPermission(permission, attributionSource.next[0].pid, attributionSource.next[0].uid) == 0 ? 0 : 2;
            }
        }
        try {
            return this.mService.checkPermission(permission, attributionSource, message, forDataDelivery, startDataDelivery, fromDatasource, attributedOp);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return 2;
        }
    }

    public void finishDataDelivery(int op, AttributionSourceState attributionSource, boolean fromDatasource) {
        Objects.requireNonNull(attributionSource);
        try {
            this.mService.finishDataDelivery(op, attributionSource, fromDatasource);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public int checkOp(int op, AttributionSourceState attributionSource, String message, boolean forDataDelivery, boolean startDataDelivery) {
        Objects.requireNonNull(attributionSource);
        try {
            return this.mService.checkOp(op, attributionSource, message, forDataDelivery, startDataDelivery);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return 2;
        }
    }
}
