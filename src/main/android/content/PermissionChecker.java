package android.content;

import android.app.AppOpsManager;
import android.p008os.Binder;
import android.p008os.Process;
import android.permission.IPermissionChecker;
import android.permission.PermissionCheckerManager;
/* loaded from: classes.dex */
public final class PermissionChecker {
    public static final int PERMISSION_GRANTED = 0;
    public static final int PERMISSION_HARD_DENIED = 2;
    public static final int PERMISSION_SOFT_DENIED = 1;
    public static final int PID_UNKNOWN = -1;
    private static volatile IPermissionChecker sService;

    private PermissionChecker() {
    }

    public static int checkPermissionForDataDelivery(Context context, String permission, int pid, int uid, String packageName, String attributionTag, String message, boolean startDataDelivery) {
        return checkPermissionForDataDelivery(context, permission, pid, new AttributionSource(uid, packageName, attributionTag), message, startDataDelivery);
    }

    public static int checkPermissionForDataDelivery(Context context, String permission, int pid, int uid, String packageName, String attributionTag, String message) {
        return checkPermissionForDataDelivery(context, permission, pid, uid, packageName, attributionTag, message, false);
    }

    public static int checkPermissionForDataDeliveryFromDataSource(Context context, String permission, int pid, AttributionSource attributionSource, String message) {
        return checkPermissionForDataDeliveryCommon(context, permission, attributionSource, message, false, true);
    }

    public static int checkPermissionForDataDelivery(Context context, String permission, int pid, AttributionSource attributionSource, String message) {
        return checkPermissionForDataDelivery(context, permission, pid, attributionSource, message, false);
    }

    public static int checkPermissionForDataDelivery(Context context, String permission, int pid, AttributionSource attributionSource, String message, boolean startDataDelivery) {
        return checkPermissionForDataDeliveryCommon(context, permission, attributionSource, message, startDataDelivery, false);
    }

    private static int checkPermissionForDataDeliveryCommon(Context context, String permission, AttributionSource attributionSource, String message, boolean startDataDelivery, boolean fromDatasource) {
        return ((PermissionCheckerManager) context.getSystemService(PermissionCheckerManager.class)).checkPermission(permission, attributionSource.asState(), message, true, startDataDelivery, fromDatasource, -1);
    }

    public static int checkPermissionAndStartDataDelivery(Context context, String permission, AttributionSource attributionSource, String message) {
        return ((PermissionCheckerManager) context.getSystemService(PermissionCheckerManager.class)).checkPermission(permission, attributionSource.asState(), message, true, true, false, -1);
    }

    public static int startOpForDataDelivery(Context context, String opName, AttributionSource attributionSource, String message) {
        return ((PermissionCheckerManager) context.getSystemService(PermissionCheckerManager.class)).checkOp(AppOpsManager.strOpToOp(opName), attributionSource.asState(), message, true, true);
    }

    public static void finishDataDelivery(Context context, String op, AttributionSource attributionSource) {
        ((PermissionCheckerManager) context.getSystemService(PermissionCheckerManager.class)).finishDataDelivery(AppOpsManager.strOpToOp(op), attributionSource.asState(), false);
    }

    public static void finishDataDeliveryFromDatasource(Context context, String op, AttributionSource attributionSource) {
        ((PermissionCheckerManager) context.getSystemService(PermissionCheckerManager.class)).finishDataDelivery(AppOpsManager.strOpToOp(op), attributionSource.asState(), true);
    }

    public static int checkOpForPreflight(Context context, String opName, AttributionSource attributionSource, String message) {
        return ((PermissionCheckerManager) context.getSystemService(PermissionCheckerManager.class)).checkOp(AppOpsManager.strOpToOp(opName), attributionSource.asState(), message, false, false);
    }

    public static int checkOpForDataDelivery(Context context, String opName, AttributionSource attributionSource, String message) {
        return ((PermissionCheckerManager) context.getSystemService(PermissionCheckerManager.class)).checkOp(AppOpsManager.strOpToOp(opName), attributionSource.asState(), message, true, false);
    }

    public static int checkPermissionForPreflight(Context context, String permission, int pid, int uid, String packageName) {
        return checkPermissionForPreflight(context, permission, new AttributionSource(uid, packageName, null));
    }

    public static int checkPermissionForPreflight(Context context, String permission, AttributionSource attributionSource) {
        return ((PermissionCheckerManager) context.getSystemService(PermissionCheckerManager.class)).checkPermission(permission, attributionSource.asState(), null, false, false, false, -1);
    }

    public static int checkSelfPermissionForDataDelivery(Context context, String permission, String message) {
        return checkPermissionForDataDelivery(context, permission, Process.myPid(), Process.myUid(), context.getPackageName(), context.getAttributionTag(), message, false);
    }

    public static int checkSelfPermissionForPreflight(Context context, String permission) {
        return checkPermissionForPreflight(context, permission, Process.myPid(), Process.myUid(), context.getPackageName());
    }

    public static int checkCallingPermissionForDataDelivery(Context context, String permission, String callingPackageName, String callingAttributionTag, String message) {
        if (Binder.getCallingPid() == Process.myPid()) {
            return 2;
        }
        return checkPermissionForDataDelivery(context, permission, Binder.getCallingPid(), Binder.getCallingUid(), callingPackageName, callingAttributionTag, message, false);
    }

    public static int checkCallingPermissionForPreflight(Context context, String permission, String packageName) {
        if (Binder.getCallingPid() == Process.myPid()) {
            return 2;
        }
        return checkPermissionForPreflight(context, permission, Binder.getCallingPid(), Binder.getCallingUid(), packageName);
    }

    public static int checkCallingOrSelfPermissionForDataDelivery(Context context, String permission, String callingPackageName, String callingAttributionTag, String message) {
        return checkPermissionForDataDelivery(context, permission, Binder.getCallingPid(), Binder.getCallingUid(), Binder.getCallingPid() == Process.myPid() ? context.getPackageName() : callingPackageName, Binder.getCallingPid() == Process.myPid() ? context.getAttributionTag() : callingAttributionTag, message, false);
    }

    public static int checkCallingOrSelfPermissionForPreflight(Context context, String permission) {
        String packageName = Binder.getCallingPid() == Process.myPid() ? context.getPackageName() : null;
        return checkPermissionForPreflight(context, permission, Binder.getCallingPid(), Binder.getCallingUid(), packageName);
    }
}
