package android.media.permission;

import android.content.Context;
import android.content.PermissionChecker;
import android.p008os.Binder;
import java.util.Objects;
/* loaded from: classes2.dex */
public class PermissionUtil {
    public static SafeCloseable establishIdentityIndirect(Context context, String middlemanPermission, Identity middlemanIdentity, Identity originatorIdentity) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(middlemanPermission);
        Objects.requireNonNull(middlemanIdentity);
        Objects.requireNonNull(originatorIdentity);
        middlemanIdentity.pid = Binder.getCallingPid();
        middlemanIdentity.uid = Binder.getCallingUid();
        context.enforcePermission(middlemanPermission, middlemanIdentity.pid, middlemanIdentity.uid, String.format("Middleman must have the %s permision.", middlemanPermission));
        return new CompositeSafeCloseable(IdentityContext.create(originatorIdentity), ClearCallingIdentityContext.create());
    }

    public static SafeCloseable establishIdentityDirect(Identity originatorIdentity) {
        Objects.requireNonNull(originatorIdentity);
        originatorIdentity.uid = Binder.getCallingUid();
        originatorIdentity.pid = Binder.getCallingPid();
        return new CompositeSafeCloseable(IdentityContext.create(originatorIdentity), ClearCallingIdentityContext.create());
    }

    public static int checkPermissionForDataDelivery(Context context, Identity identity, String permission, String reason) {
        return PermissionChecker.checkPermissionForDataDelivery(context, permission, identity.pid, identity.uid, identity.packageName, identity.attributionTag, reason);
    }

    public static int checkPermissionForPreflight(Context context, Identity identity, String permission) {
        return PermissionChecker.checkPermissionForPreflight(context, permission, identity.pid, identity.uid, identity.packageName);
    }
}
