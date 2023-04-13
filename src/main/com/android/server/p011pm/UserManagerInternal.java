package com.android.server.p011pm;

import android.content.pm.UserInfo;
import android.content.pm.UserProperties;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.UserManager;
import android.util.DebugUtils;
import com.android.internal.annotations.Keep;
import java.util.List;
/* renamed from: com.android.server.pm.UserManagerInternal */
/* loaded from: classes2.dex */
public abstract class UserManagerInternal {
    @Keep
    public static final int USER_ASSIGNMENT_RESULT_FAILURE = -1;
    @Keep
    public static final int USER_ASSIGNMENT_RESULT_SUCCESS_ALREADY_VISIBLE = 3;
    @Keep
    public static final int USER_ASSIGNMENT_RESULT_SUCCESS_INVISIBLE = 2;
    @Keep
    public static final int USER_ASSIGNMENT_RESULT_SUCCESS_VISIBLE = 1;
    @Keep
    public static final int USER_START_MODE_BACKGROUND = 2;
    @Keep
    public static final int USER_START_MODE_BACKGROUND_VISIBLE = 3;
    @Keep
    public static final int USER_START_MODE_FOREGROUND = 1;

    /* renamed from: com.android.server.pm.UserManagerInternal$UserLifecycleListener */
    /* loaded from: classes2.dex */
    public interface UserLifecycleListener {
        default void onUserCreated(UserInfo userInfo, Object obj) {
        }

        default void onUserRemoved(UserInfo userInfo) {
        }
    }

    /* renamed from: com.android.server.pm.UserManagerInternal$UserRestrictionsListener */
    /* loaded from: classes2.dex */
    public interface UserRestrictionsListener {
        void onUserRestrictionsChanged(int i, Bundle bundle, Bundle bundle2);
    }

    /* renamed from: com.android.server.pm.UserManagerInternal$UserVisibilityListener */
    /* loaded from: classes2.dex */
    public interface UserVisibilityListener {
        void onUserVisibilityChanged(int i, boolean z);
    }

    public abstract void addUserLifecycleListener(UserLifecycleListener userLifecycleListener);

    public abstract void addUserRestrictionsListener(UserRestrictionsListener userRestrictionsListener);

    public abstract void addUserVisibilityListener(UserVisibilityListener userVisibilityListener);

    public abstract int assignUserToDisplayOnStart(int i, int i2, int i3, int i4);

    public abstract UserInfo createUserEvenWhenDisallowed(String str, String str2, int i, String[] strArr, Object obj) throws UserManager.CheckedUserOperationException;

    public abstract boolean exists(int i);

    public abstract int getBootUser() throws UserManager.CheckedUserOperationException;

    public abstract int getMainUserId();

    public abstract int[] getProfileIds(int i, boolean z);

    public abstract int getProfileParentId(int i);

    public abstract int[] getUserIds();

    public abstract UserInfo getUserInfo(int i);

    public abstract UserInfo[] getUserInfos();

    public abstract UserProperties getUserProperties(int i);

    public abstract boolean getUserRestriction(int i, String str);

    public abstract int[] getUserTypesForStatsd(int[] iArr);

    public abstract List<UserInfo> getUsers(boolean z);

    public abstract List<UserInfo> getUsers(boolean z, boolean z2, boolean z3);

    public abstract boolean hasUserRestriction(String str, int i);

    @Deprecated
    public abstract boolean isDeviceManaged();

    public abstract boolean isProfileAccessible(int i, int i2, String str, boolean z);

    @Deprecated
    public abstract boolean isUserManaged(int i);

    public abstract boolean isUserRunning(int i);

    public abstract boolean isUserUnlocked(int i);

    public abstract boolean isUserUnlockingOrUnlocked(int i);

    public abstract boolean isUserVisible(int i);

    public abstract boolean isUserVisible(int i, int i2);

    public abstract void onEphemeralUserStop(int i);

    public abstract void onSystemUserVisibilityChanged(boolean z);

    public abstract boolean removeUserEvenWhenDisallowed(int i);

    public abstract void removeUserLifecycleListener(UserLifecycleListener userLifecycleListener);

    public abstract void removeUserState(int i);

    public abstract void setDefaultCrossProfileIntentFilters(int i, int i2);

    @Deprecated
    public abstract void setDeviceManaged(boolean z);

    public abstract void setDevicePolicyUserRestrictions(int i, Bundle bundle, RestrictionsSet restrictionsSet, boolean z);

    public abstract void setForceEphemeralUsers(boolean z);

    public abstract void setUserIcon(int i, Bitmap bitmap);

    @Deprecated
    public abstract void setUserManaged(int i, boolean z);

    public abstract void setUserRestriction(int i, String str, boolean z);

    public abstract void setUserState(int i, int i2);

    public abstract boolean shouldIgnorePrepareStorageErrors(int i);

    public abstract void unassignUserFromDisplayOnStop(int i);

    public static String userAssignmentResultToString(int i) {
        return DebugUtils.constantToString(UserManagerInternal.class, "USER_ASSIGNMENT_RESULT_", i);
    }

    public static String userStartModeToString(int i) {
        return DebugUtils.constantToString(UserManagerInternal.class, "USER_START_MODE_", i);
    }
}
