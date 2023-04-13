package android.p008os;

import android.content.AttributionSource;
import android.content.Context;
import android.content.PermissionChecker;
/* renamed from: android.os.PermissionEnforcer */
/* loaded from: classes3.dex */
public class PermissionEnforcer {
    private final Context mContext;

    protected PermissionEnforcer() {
        this.mContext = null;
    }

    public PermissionEnforcer(Context context) {
        this.mContext = context;
    }

    protected int checkPermission(String permission, AttributionSource source) {
        return PermissionChecker.checkPermissionForDataDelivery(this.mContext, permission, -1, source, "");
    }

    public void enforcePermission(String permission, AttributionSource source) throws SecurityException {
        int result = checkPermission(permission, source);
        if (result != 0) {
            throw new SecurityException("Access denied, requires: " + permission);
        }
    }

    public void enforcePermissionAllOf(String[] permissions, AttributionSource source) throws SecurityException {
        for (String permission : permissions) {
            int result = checkPermission(permission, source);
            if (result != 0) {
                throw new SecurityException("Access denied, requires: allOf={" + String.join(", ", permissions) + "}");
            }
        }
    }

    public void enforcePermissionAnyOf(String[] permissions, AttributionSource source) throws SecurityException {
        for (String permission : permissions) {
            int result = checkPermission(permission, source);
            if (result == 0) {
                return;
            }
        }
        throw new SecurityException("Access denied, requires: anyOf={" + String.join(", ", permissions) + "}");
    }

    public static PermissionEnforcer fromContext(Context context) {
        return (PermissionEnforcer) context.getSystemService(PermissionEnforcer.class);
    }
}
