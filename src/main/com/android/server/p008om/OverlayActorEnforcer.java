package com.android.server.p008om;

import android.content.om.OverlayInfo;
import android.content.om.OverlayableInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.CollectionUtils;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageState;
import java.io.IOException;
import java.util.List;
import java.util.Map;
/* renamed from: com.android.server.om.OverlayActorEnforcer */
/* loaded from: classes2.dex */
public class OverlayActorEnforcer {
    public final PackageManagerHelper mPackageManager;

    /* renamed from: com.android.server.om.OverlayActorEnforcer$ActorState */
    /* loaded from: classes2.dex */
    public enum ActorState {
        TARGET_NOT_FOUND,
        NO_PACKAGES_FOR_UID,
        MISSING_TARGET_OVERLAYABLE_NAME,
        MISSING_LEGACY_PERMISSION,
        ERROR_READING_OVERLAYABLE,
        UNABLE_TO_GET_TARGET_OVERLAYABLE,
        MISSING_OVERLAYABLE,
        INVALID_OVERLAYABLE_ACTOR_NAME,
        NO_NAMED_ACTORS,
        MISSING_NAMESPACE,
        MISSING_ACTOR_NAME,
        ACTOR_NOT_FOUND,
        ACTOR_NOT_PREINSTALLED,
        INVALID_ACTOR,
        ALLOWED
    }

    public static Pair<String, ActorState> getPackageNameForActor(String str, Map<String, Map<String, String>> map) {
        Uri parse = Uri.parse(str);
        String scheme = parse.getScheme();
        List<String> pathSegments = parse.getPathSegments();
        if (!"overlay".equals(scheme) || CollectionUtils.size(pathSegments) != 1) {
            return Pair.create(null, ActorState.INVALID_OVERLAYABLE_ACTOR_NAME);
        }
        if (map.isEmpty()) {
            return Pair.create(null, ActorState.NO_NAMED_ACTORS);
        }
        Map<String, String> map2 = map.get(parse.getAuthority());
        if (ArrayUtils.isEmpty(map2)) {
            return Pair.create(null, ActorState.MISSING_NAMESPACE);
        }
        String str2 = map2.get(pathSegments.get(0));
        if (TextUtils.isEmpty(str2)) {
            return Pair.create(null, ActorState.MISSING_ACTOR_NAME);
        }
        return Pair.create(str2, ActorState.ALLOWED);
    }

    public OverlayActorEnforcer(PackageManagerHelper packageManagerHelper) {
        this.mPackageManager = packageManagerHelper;
    }

    public void enforceActor(OverlayInfo overlayInfo, String str, int i, int i2) throws SecurityException {
        String str2;
        ActorState isAllowedActor = isAllowedActor(str, overlayInfo, i, i2);
        if (isAllowedActor == ActorState.ALLOWED) {
            return;
        }
        String str3 = overlayInfo.targetOverlayableName;
        StringBuilder sb = new StringBuilder();
        sb.append("UID");
        sb.append(i);
        sb.append(" is not allowed to call ");
        sb.append(str);
        sb.append(" for ");
        if (TextUtils.isEmpty(str3)) {
            str2 = "";
        } else {
            str2 = str3 + " in ";
        }
        sb.append(str2);
        sb.append(overlayInfo.targetPackageName);
        sb.append(" for user ");
        sb.append(i2);
        String sb2 = sb.toString();
        Slog.w("OverlayManager", sb2 + " because " + isAllowedActor);
        throw new SecurityException(sb2);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public ActorState isAllowedActor(String str, OverlayInfo overlayInfo, int i, int i2) {
        if (i == 0 || i == 1000) {
            return ActorState.ALLOWED;
        }
        String str2 = overlayInfo.targetPackageName;
        PackageState packageStateForUser = this.mPackageManager.getPackageStateForUser(str2, i2);
        AndroidPackage androidPackage = packageStateForUser == null ? null : packageStateForUser.getAndroidPackage();
        if (androidPackage == null) {
            return ActorState.TARGET_NOT_FOUND;
        }
        if (androidPackage.isDebuggable()) {
            return ActorState.ALLOWED;
        }
        String[] packagesForUid = this.mPackageManager.getPackagesForUid(i);
        if (ArrayUtils.isEmpty(packagesForUid)) {
            return ActorState.NO_PACKAGES_FOR_UID;
        }
        if (ArrayUtils.contains(packagesForUid, str2)) {
            return ActorState.ALLOWED;
        }
        String str3 = overlayInfo.targetOverlayableName;
        if (TextUtils.isEmpty(str3)) {
            try {
                if (this.mPackageManager.doesTargetDefineOverlayable(str2, i2)) {
                    return ActorState.MISSING_TARGET_OVERLAYABLE_NAME;
                }
                try {
                    this.mPackageManager.enforcePermission("android.permission.CHANGE_OVERLAY_PACKAGES", str);
                    return ActorState.ALLOWED;
                } catch (SecurityException unused) {
                    return ActorState.MISSING_LEGACY_PERMISSION;
                }
            } catch (IOException unused2) {
                return ActorState.ERROR_READING_OVERLAYABLE;
            }
        }
        try {
            OverlayableInfo overlayableForTarget = this.mPackageManager.getOverlayableForTarget(str2, str3, i2);
            if (overlayableForTarget == null) {
                return ActorState.MISSING_OVERLAYABLE;
            }
            String str4 = overlayableForTarget.actor;
            if (TextUtils.isEmpty(str4)) {
                try {
                    this.mPackageManager.enforcePermission("android.permission.CHANGE_OVERLAY_PACKAGES", str);
                    return ActorState.ALLOWED;
                } catch (SecurityException unused3) {
                    return ActorState.MISSING_LEGACY_PERMISSION;
                }
            }
            Pair<String, ActorState> packageNameForActor = getPackageNameForActor(str4, this.mPackageManager.getNamedActors());
            ActorState actorState = (ActorState) packageNameForActor.second;
            ActorState actorState2 = ActorState.ALLOWED;
            if (actorState != actorState2) {
                return actorState;
            }
            String str5 = (String) packageNameForActor.first;
            PackageState packageStateForUser2 = this.mPackageManager.getPackageStateForUser(str5, i2);
            if (packageStateForUser2 == null || packageStateForUser2.getAndroidPackage() == null) {
                return ActorState.ACTOR_NOT_FOUND;
            }
            if (packageStateForUser2.isSystem()) {
                return ArrayUtils.contains(packagesForUid, str5) ? actorState2 : ActorState.INVALID_ACTOR;
            }
            return ActorState.ACTOR_NOT_PREINSTALLED;
        } catch (IOException unused4) {
            return ActorState.UNABLE_TO_GET_TARGET_OVERLAYABLE;
        }
    }
}
