package com.android.server.p011pm;

import android.os.Handler;
import android.os.UserManager;
import android.util.DebugUtils;
import android.util.Dumpable;
import android.util.EventLog;
import android.util.IndentingPrintWriter;
import android.util.IntArray;
import android.util.Log;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.Preconditions;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.utils.Slogf;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
/* renamed from: com.android.server.pm.UserVisibilityMediator */
/* loaded from: classes2.dex */
public final class UserVisibilityMediator implements Dumpable {
    public static final boolean DBG;
    @VisibleForTesting
    static final int INITIAL_CURRENT_USER_ID = 0;
    public static final String TAG;
    @GuardedBy({"mLock"})
    public int mCurrentUserId;
    @GuardedBy({"mLock"})
    public final SparseIntArray mExtraDisplaysAssignedToUsers;
    public final Handler mHandler;
    public final CopyOnWriteArrayList<UserManagerInternal.UserVisibilityListener> mListeners;
    public final Object mLock;
    @GuardedBy({"mLock"})
    public final List<Integer> mStartedInvisibleProfileUserIds;
    @GuardedBy({"mLock"})
    public final SparseIntArray mStartedVisibleProfileGroupIds;
    @GuardedBy({"mLock"})
    public final SparseIntArray mUsersAssignedToDisplayOnStart;
    public final boolean mVisibleBackgroundUserOnDefaultDisplayAllowed;
    public final boolean mVisibleBackgroundUsersEnabled;

    public static boolean isProfile(int i, int i2) {
        return (i2 == -10000 || i2 == i) ? false : true;
    }

    public static boolean isSpecialUserId(int i) {
        return i == -10000 || i == -3 || i == -2 || i == -1;
    }

    static {
        String simpleName = UserVisibilityMediator.class.getSimpleName();
        TAG = simpleName;
        DBG = Log.isLoggable(simpleName, 3);
    }

    public UserVisibilityMediator(Handler handler) {
        this(UserManager.isVisibleBackgroundUsersEnabled(), UserManager.isVisibleBackgroundUsersOnDefaultDisplayEnabled(), handler);
    }

    @VisibleForTesting
    public UserVisibilityMediator(boolean z, boolean z2, Handler handler) {
        this.mLock = new Object();
        this.mCurrentUserId = 0;
        SparseIntArray sparseIntArray = new SparseIntArray();
        this.mStartedVisibleProfileGroupIds = sparseIntArray;
        this.mListeners = new CopyOnWriteArrayList<>();
        this.mVisibleBackgroundUsersEnabled = z;
        this.mVisibleBackgroundUserOnDefaultDisplayAllowed = z2;
        if (z) {
            this.mUsersAssignedToDisplayOnStart = new SparseIntArray();
            this.mExtraDisplaysAssignedToUsers = new SparseIntArray();
        } else {
            this.mUsersAssignedToDisplayOnStart = null;
            this.mExtraDisplaysAssignedToUsers = null;
        }
        boolean z3 = DBG;
        this.mStartedInvisibleProfileUserIds = z3 ? new ArrayList(4) : null;
        this.mHandler = handler;
        sparseIntArray.put(0, 0);
        if (z3) {
            Slogf.m22i(TAG, "UserVisibilityMediator created with DBG on");
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:40:0x00e4  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x0100  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x0126  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int assignUserToDisplayOnStart(int i, int i2, int i3, int i4) {
        Preconditions.checkArgument(!isSpecialUserId(i), "user id cannot be generic: %d", new Object[]{Integer.valueOf(i)});
        validateUserStartMode(i3);
        int i5 = i2 == -10000 ? i : i2;
        boolean z = DBG;
        if (z) {
            Slogf.m28d(TAG, "assignUserToDisplayOnStart(%d, %d, %s, %d): actualProfileGroupId=%d", Integer.valueOf(i), Integer.valueOf(i2), UserManagerInternal.userStartModeToString(i3), Integer.valueOf(i4), Integer.valueOf(i5));
        }
        synchronized (this.mLock) {
            int userVisibilityOnStartLocked = getUserVisibilityOnStartLocked(i, i5, i3, i4);
            if (z) {
                Slogf.m28d(TAG, "result of getUserVisibilityOnStartLocked(%s)", UserManagerInternal.userAssignmentResultToString(userVisibilityOnStartLocked));
            }
            if (userVisibilityOnStartLocked != -1 && userVisibilityOnStartLocked != 3) {
                int canAssignUserToDisplayLocked = canAssignUserToDisplayLocked(i, i5, i3, i4);
                if (z) {
                    Slogf.m28d(TAG, "mapping result: %s", secondaryDisplayMappingStatusToString(canAssignUserToDisplayLocked));
                }
                if (canAssignUserToDisplayLocked == -1) {
                    return -1;
                }
                IntArray visibleUsers = getVisibleUsers();
                if (i3 == 1) {
                    this.mCurrentUserId = i;
                } else {
                    if (i3 != 2) {
                        if (i3 != 3) {
                            Slogf.wtf(TAG, "invalid userStartMode passed to assignUserToDisplayOnStart: %d", Integer.valueOf(i3));
                        }
                    } else if (this.mStartedInvisibleProfileUserIds != null && isProfile(i, i5)) {
                        Slogf.m28d(TAG, "adding user %d to list of invisible profiles", Integer.valueOf(i));
                        this.mStartedInvisibleProfileUserIds.add(Integer.valueOf(i));
                    }
                    if (canAssignUserToDisplayLocked != 1) {
                        if (z) {
                            Slogf.m28d(TAG, "adding user / display mapping (%d -> %d)", Integer.valueOf(i), Integer.valueOf(i4));
                        }
                        this.mUsersAssignedToDisplayOnStart.put(i, i4);
                    } else if (canAssignUserToDisplayLocked != 2) {
                        Slogf.wtf(TAG, "invalid resut from canAssignUserToDisplayLocked: %d", Integer.valueOf(canAssignUserToDisplayLocked));
                    } else if (z) {
                        Slogf.m30d(TAG, "don't need to update mUsersOnSecondaryDisplays");
                    }
                    dispatchVisibilityChanged(visibleUsers, getVisibleUsers());
                    if (z) {
                        Slogf.m28d(TAG, "returning %s", UserManagerInternal.userAssignmentResultToString(userVisibilityOnStartLocked));
                    }
                    return userVisibilityOnStartLocked;
                }
                if (z) {
                    Slogf.m28d(TAG, "adding visible user / profile group id mapping (%d -> %d)", Integer.valueOf(i), Integer.valueOf(i5));
                }
                this.mStartedVisibleProfileGroupIds.put(i, i5);
                if (canAssignUserToDisplayLocked != 1) {
                }
                dispatchVisibilityChanged(visibleUsers, getVisibleUsers());
                if (z) {
                }
                return userVisibilityOnStartLocked;
            }
            return userVisibilityOnStartLocked;
        }
    }

    @GuardedBy({"mLock"})
    public final int getUserVisibilityOnStartLocked(int i, int i2, int i3, int i4) {
        if (i3 == 2 && i4 != 0) {
            Slogf.wtf(TAG, "cannot start user (%d) as BACKGROUND_USER on secondary display (%d) (it should be BACKGROUND_USER_VISIBLE", Integer.valueOf(i), Integer.valueOf(i4));
            return -1;
        }
        boolean z = i3 == 3;
        if (i4 == 0 && z) {
            if (this.mVisibleBackgroundUserOnDefaultDisplayAllowed && isCurrentUserLocked(i)) {
                Slogf.wtf(TAG, "trying to start current user (%d) visible in background on default display", Integer.valueOf(i));
                return 3;
            } else if (!this.mVisibleBackgroundUserOnDefaultDisplayAllowed && !isProfile(i, i2)) {
                Slogf.wtf(TAG, "cannot start full user (%d) visible on default display", Integer.valueOf(i));
                return -1;
            }
        }
        boolean z2 = i3 == 1;
        if (i4 != 0) {
            if (z2) {
                Slogf.m12w(TAG, "getUserVisibilityOnStartLocked(%d, %d, %s, %d) failed: cannot start foreground user on secondary display", Integer.valueOf(i), Integer.valueOf(i2), UserManagerInternal.userStartModeToString(i3), Integer.valueOf(i4));
                return -1;
            } else if (!this.mVisibleBackgroundUsersEnabled) {
                Slogf.m12w(TAG, "getUserVisibilityOnStartLocked(%d, %d, %s, %d) failed: called on device that doesn't support multiple users on multiple displays", Integer.valueOf(i), Integer.valueOf(i2), UserManagerInternal.userStartModeToString(i3), Integer.valueOf(i4));
                return -1;
            }
        }
        if (isProfile(i, i2)) {
            if (i4 != 0) {
                Slogf.m12w(TAG, "canStartUserLocked(%d, %d, %s, %d) failed: cannot start profile user on secondary display", Integer.valueOf(i), Integer.valueOf(i2), UserManagerInternal.userStartModeToString(i3), Integer.valueOf(i4));
                return -1;
            } else if (i3 == 1) {
                Slogf.m12w(TAG, "startUser(%d, %d, %s, %d) failed: cannot start profile user in foreground", Integer.valueOf(i), Integer.valueOf(i2), UserManagerInternal.userStartModeToString(i3), Integer.valueOf(i4));
                return -1;
            } else if (i3 == 2) {
                return 2;
            } else {
                if (i3 == 3) {
                    if (isUserVisible(i2, i4)) {
                        return 1;
                    }
                    Slogf.m12w(TAG, "getUserVisibilityOnStartLocked(%d, %d, %s, %d) failed: cannot start profile user visible when its parent is not visible in that display", Integer.valueOf(i), Integer.valueOf(i2), UserManagerInternal.userStartModeToString(i3), Integer.valueOf(i4));
                    return -1;
                }
            }
        } else if (this.mUsersAssignedToDisplayOnStart != null && isUserAssignedToDisplayOnStartLocked(i, i4)) {
            if (DBG) {
                Slogf.m28d(TAG, "full user %d is already visible on display %d", Integer.valueOf(i), Integer.valueOf(i4));
            }
            return 3;
        }
        return (z2 || i4 != 0 || (z && this.mVisibleBackgroundUserOnDefaultDisplayAllowed)) ? 1 : 2;
    }

    @GuardedBy({"mLock"})
    public final int canAssignUserToDisplayLocked(int i, int i2, int i3, int i4) {
        boolean z;
        if (i4 == 0) {
            if (this.mVisibleBackgroundUserOnDefaultDisplayAllowed && i3 == 3) {
                int userStartedOnDisplay = getUserStartedOnDisplay(0);
                if (userStartedOnDisplay != -10000 && userStartedOnDisplay != i2) {
                    Slogf.m12w(TAG, "canAssignUserToDisplayLocked(): cannot start user %d visible on default display because user %d already did so", Integer.valueOf(i), Integer.valueOf(userStartedOnDisplay));
                    return -1;
                }
                z = true;
            } else {
                z = false;
            }
            if (!z && this.mVisibleBackgroundUsersEnabled && isProfile(i, i2)) {
                z = true;
            }
            if (!z) {
                if (DBG) {
                    Slogf.m28d(TAG, "Ignoring mapping for default display for user %d starting as %s", Integer.valueOf(i), UserManagerInternal.userStartModeToString(i3));
                }
                return 2;
            }
        }
        if (i == 0) {
            Slogf.m12w(TAG, "Cannot assign system user to secondary display (%d)", Integer.valueOf(i4));
            return -1;
        } else if (i4 == -1) {
            Slogf.m12w(TAG, "Cannot assign to INVALID_DISPLAY (%d)", Integer.valueOf(i4));
            return -1;
        } else if (i == this.mCurrentUserId) {
            Slogf.m12w(TAG, "Cannot assign current user (%d) to other displays", Integer.valueOf(i));
            return -1;
        } else if (isProfile(i, i2)) {
            if (i4 != 0) {
                Slogf.m14w(TAG, "Profile user can only be started in the default display");
                return -1;
            }
            if (DBG) {
                Slogf.m28d(TAG, "Don't need to map profile user %d to default display", Integer.valueOf(i));
            }
            return 2;
        } else if (this.mUsersAssignedToDisplayOnStart == null) {
            Slogf.wtf(TAG, "canAssignUserToDisplayLocked(%d, %d, %d, %d) is trying to check mUsersAssignedToDisplayOnStart when it's not set", Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4));
            return -1;
        } else {
            for (int i5 = 0; i5 < this.mUsersAssignedToDisplayOnStart.size(); i5++) {
                int keyAt = this.mUsersAssignedToDisplayOnStart.keyAt(i5);
                int valueAt = this.mUsersAssignedToDisplayOnStart.valueAt(i5);
                if (DBG) {
                    Slogf.m28d(TAG, "%d: assignedUserId=%d, assignedDisplayId=%d", Integer.valueOf(i5), Integer.valueOf(keyAt), Integer.valueOf(valueAt));
                }
                if (i4 == valueAt) {
                    Slogf.m12w(TAG, "Cannot assign user %d to display %d because such display is already assigned to user %d", Integer.valueOf(i), Integer.valueOf(i4), Integer.valueOf(keyAt));
                    return -1;
                } else if (i == keyAt) {
                    Slogf.m12w(TAG, "Cannot assign user %d to display %d because such user is as already assigned to display %d", Integer.valueOf(i), Integer.valueOf(i4), Integer.valueOf(keyAt));
                    return -1;
                }
            }
            return 1;
        }
    }

    public void unassignUserFromDisplayOnStop(int i) {
        IntArray visibleUsers;
        IntArray visibleUsers2;
        if (DBG) {
            Slogf.m28d(TAG, "unassignUserFromDisplayOnStop(%d)", Integer.valueOf(i));
        }
        synchronized (this.mLock) {
            visibleUsers = getVisibleUsers();
            unassignUserFromAllDisplaysOnStopLocked(i);
            visibleUsers2 = getVisibleUsers();
        }
        dispatchVisibilityChanged(visibleUsers, visibleUsers2);
    }

    @GuardedBy({"mLock"})
    public final void unassignUserFromAllDisplaysOnStopLocked(int i) {
        boolean z = DBG;
        if (z) {
            Slogf.m28d(TAG, "Removing %d from mStartedVisibleProfileGroupIds (%s)", Integer.valueOf(i), this.mStartedVisibleProfileGroupIds);
        }
        this.mStartedVisibleProfileGroupIds.delete(i);
        if (this.mStartedInvisibleProfileUserIds != null) {
            Slogf.m28d(TAG, "Removing %d from list of invisible profiles", Integer.valueOf(i));
            this.mStartedInvisibleProfileUserIds.remove(Integer.valueOf(i));
        }
        if (this.mVisibleBackgroundUsersEnabled) {
            if (z) {
                Slogf.m28d(TAG, "Removing user %d from mUsersOnDisplaysMap (%s)", Integer.valueOf(i), this.mUsersAssignedToDisplayOnStart);
            }
            this.mUsersAssignedToDisplayOnStart.delete(i);
            for (int size = this.mExtraDisplaysAssignedToUsers.size() - 1; size >= 0; size--) {
                if (this.mExtraDisplaysAssignedToUsers.valueAt(size) == i) {
                    if (DBG) {
                        Slogf.m28d(TAG, "Removing display %d from mExtraDisplaysAssignedToUsers (%s)", Integer.valueOf(this.mExtraDisplaysAssignedToUsers.keyAt(size)), this.mExtraDisplaysAssignedToUsers);
                    }
                    this.mExtraDisplaysAssignedToUsers.removeAt(size);
                }
            }
        }
    }

    public boolean isUserVisible(int i) {
        int i2;
        if (isCurrentUserOrRunningProfileOfCurrentUser(i)) {
            return true;
        }
        if (this.mVisibleBackgroundUsersEnabled) {
            synchronized (this.mLock) {
                synchronized (this.mLock) {
                    i2 = this.mStartedVisibleProfileGroupIds.get(i, -10000);
                }
                if (isProfile(i, i2)) {
                    return isUserAssignedToDisplayOnStartLocked(i2);
                }
                return isUserAssignedToDisplayOnStartLocked(i);
            }
        }
        return false;
    }

    @GuardedBy({"mLock"})
    public final boolean isUserAssignedToDisplayOnStartLocked(int i) {
        return this.mUsersAssignedToDisplayOnStart.indexOfKey(i) >= 0;
    }

    @GuardedBy({"mLock"})
    public final boolean isUserAssignedToDisplayOnStartLocked(int i, int i2) {
        SparseIntArray sparseIntArray = this.mUsersAssignedToDisplayOnStart;
        if (sparseIntArray != null) {
            return i2 != -1 && sparseIntArray.get(i, -1) == i2;
        }
        Slogf.wtf(TAG, "isUserAssignedToDisplayOnStartLocked(%d, %d): called when mUsersAssignedToDisplayOnStart is null", Integer.valueOf(i), Integer.valueOf(i2));
        return false;
    }

    public boolean isUserVisible(int i, int i2) {
        int i3;
        if (i2 == -1) {
            return false;
        }
        if (!isCurrentUserOrRunningProfileOfCurrentUser(i) || (i2 != 0 && this.mVisibleBackgroundUsersEnabled)) {
            if (!this.mVisibleBackgroundUsersEnabled) {
                if (DBG) {
                    Slogf.m28d(TAG, "isUserVisible(%d, %d): returning false as device does not support visible background users", Integer.valueOf(i), Integer.valueOf(i2));
                }
                return false;
            }
            synchronized (this.mLock) {
                synchronized (this.mLock) {
                    i3 = this.mStartedVisibleProfileGroupIds.get(i, -10000);
                }
                if (isProfile(i, i3)) {
                    return isFullUserVisibleOnBackgroundLocked(i3, i2);
                }
                return isFullUserVisibleOnBackgroundLocked(i, i2);
            }
        }
        return true;
    }

    @GuardedBy({"mLock"})
    public final boolean isFullUserVisibleOnBackgroundLocked(int i, int i2) {
        return this.mUsersAssignedToDisplayOnStart.get(i, -1) == i2 || this.mExtraDisplaysAssignedToUsers.get(i2, -10000) == i;
    }

    public int getDisplayAssignedToUser(int i) {
        int i2;
        int userStartedOnDisplay;
        if (isCurrentUserOrRunningProfileOfCurrentUser(i)) {
            if (this.mVisibleBackgroundUserOnDefaultDisplayAllowed) {
                synchronized (this.mLock) {
                    userStartedOnDisplay = getUserStartedOnDisplay(0);
                }
                if (userStartedOnDisplay != -10000) {
                    if (DBG) {
                        Slogf.m28d(TAG, "getDisplayAssignedToUser(%d): returning INVALID_DISPLAY for current user user %d was started on DEFAULT_DISPLAY", Integer.valueOf(i), Integer.valueOf(userStartedOnDisplay));
                    }
                    return -1;
                }
            }
            return 0;
        } else if (this.mVisibleBackgroundUsersEnabled) {
            synchronized (this.mLock) {
                i2 = this.mUsersAssignedToDisplayOnStart.get(i, -1);
            }
            return i2;
        } else {
            return -1;
        }
    }

    public final int getUserStartedOnDisplay(int i) {
        return getUserAssignedToDisplay(i, false);
    }

    public final int getUserAssignedToDisplay(int i, boolean z) {
        if (z && ((i == 0 && !this.mVisibleBackgroundUserOnDefaultDisplayAllowed) || !this.mVisibleBackgroundUsersEnabled)) {
            return getCurrentUserId();
        }
        synchronized (this.mLock) {
            for (int i2 = 0; i2 < this.mUsersAssignedToDisplayOnStart.size(); i2++) {
                if (this.mUsersAssignedToDisplayOnStart.valueAt(i2) == i) {
                    int keyAt = this.mUsersAssignedToDisplayOnStart.keyAt(i2);
                    if (!isStartedVisibleProfileLocked(keyAt)) {
                        return keyAt;
                    }
                    if (DBG) {
                        Slogf.m28d(TAG, "getUserAssignedToDisplay(%d): skipping user %d because it's a profile", Integer.valueOf(i), Integer.valueOf(keyAt));
                    }
                }
            }
            if (!z) {
                if (DBG) {
                    Slogf.m28d(TAG, "getUserAssignedToDisplay(%d): no user assigned to display, returning USER_NULL instead", Integer.valueOf(i));
                    return -10000;
                }
                return -10000;
            }
            int currentUserId = getCurrentUserId();
            if (DBG) {
                Slogf.m28d(TAG, "getUserAssignedToDisplay(%d): no user assigned to display, returning current user (%d) instead", Integer.valueOf(i), Integer.valueOf(currentUserId));
            }
            return currentUserId;
        }
    }

    public IntArray getVisibleUsers() {
        IntArray intArray = new IntArray();
        synchronized (this.mLock) {
            for (int i = 0; i < this.mStartedVisibleProfileGroupIds.size(); i++) {
                int keyAt = this.mStartedVisibleProfileGroupIds.keyAt(i);
                if (isUserVisible(keyAt)) {
                    intArray.add(keyAt);
                }
            }
        }
        return intArray;
    }

    public void addListener(UserManagerInternal.UserVisibilityListener userVisibilityListener) {
        if (DBG) {
            Slogf.m28d(TAG, "adding listener %s", userVisibilityListener);
        }
        synchronized (this.mLock) {
            this.mListeners.add(userVisibilityListener);
        }
    }

    public void onSystemUserVisibilityChanged(boolean z) {
        dispatchVisibilityChanged(this.mListeners, 0, z);
    }

    public final void dispatchVisibilityChanged(IntArray intArray, IntArray intArray2) {
        if (intArray == null) {
            if (DBG) {
                Slogf.m30d(TAG, "dispatchVisibilityChanged(): ignoring, no listeners");
                return;
            }
            return;
        }
        CopyOnWriteArrayList<UserManagerInternal.UserVisibilityListener> copyOnWriteArrayList = this.mListeners;
        if (DBG) {
            Slogf.m28d(TAG, "dispatchVisibilityChanged(): visibleUsersBefore=%s, visibleUsersAfter=%s, %d listeners (%s)", intArray, intArray2, Integer.valueOf(copyOnWriteArrayList.size()), copyOnWriteArrayList);
        }
        for (int i = 0; i < intArray.size(); i++) {
            int i2 = intArray.get(i);
            if (intArray2.indexOf(i2) == -1) {
                dispatchVisibilityChanged(copyOnWriteArrayList, i2, false);
            }
        }
        for (int i3 = 0; i3 < intArray2.size(); i3++) {
            int i4 = intArray2.get(i3);
            if (intArray.indexOf(i4) == -1) {
                dispatchVisibilityChanged(copyOnWriteArrayList, i4, true);
            }
        }
    }

    public final void dispatchVisibilityChanged(CopyOnWriteArrayList<UserManagerInternal.UserVisibilityListener> copyOnWriteArrayList, final int i, final boolean z) {
        EventLog.writeEvent(30091, Integer.valueOf(i), Integer.valueOf(z ? 1 : 0));
        if (DBG) {
            Slogf.m28d(TAG, "dispatchVisibilityChanged(%d -> %b): sending to %d listeners", Integer.valueOf(i), Boolean.valueOf(z), Integer.valueOf(copyOnWriteArrayList.size()));
        }
        for (int i2 = 0; i2 < this.mListeners.size(); i2++) {
            final UserManagerInternal.UserVisibilityListener userVisibilityListener = this.mListeners.get(i2);
            this.mHandler.post(new Runnable() { // from class: com.android.server.pm.UserVisibilityMediator$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    UserManagerInternal.UserVisibilityListener.this.onUserVisibilityChanged(i, z);
                }
            });
        }
    }

    public final void dump(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.println("UserVisibilityMediator");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.print("DBG: ");
        indentingPrintWriter.println(DBG);
        synchronized (this.mLock) {
            indentingPrintWriter.print("Current user id: ");
            indentingPrintWriter.println(this.mCurrentUserId);
            indentingPrintWriter.print("Visible users: ");
            indentingPrintWriter.println(getVisibleUsers());
            dumpSparseIntArray(indentingPrintWriter, this.mStartedVisibleProfileGroupIds, "started visible user / profile group", "u", "pg");
            if (this.mStartedInvisibleProfileUserIds != null) {
                indentingPrintWriter.print("Profiles started invisible: ");
                indentingPrintWriter.println(this.mStartedInvisibleProfileUserIds);
            }
            indentingPrintWriter.print("Supports visible background users on displays: ");
            indentingPrintWriter.println(this.mVisibleBackgroundUsersEnabled);
            indentingPrintWriter.print("Allows visible background users on default display: ");
            indentingPrintWriter.println(this.mVisibleBackgroundUserOnDefaultDisplayAllowed);
            dumpSparseIntArray(indentingPrintWriter, this.mUsersAssignedToDisplayOnStart, "user / display", "u", "d");
            dumpSparseIntArray(indentingPrintWriter, this.mExtraDisplaysAssignedToUsers, "extra display / user", "d", "u");
            int size = this.mListeners.size();
            indentingPrintWriter.print("Number of listeners: ");
            indentingPrintWriter.println(size);
            if (size > 0) {
                indentingPrintWriter.increaseIndent();
                for (int i = 0; i < size; i++) {
                    indentingPrintWriter.print(i);
                    indentingPrintWriter.print(": ");
                    indentingPrintWriter.println(this.mListeners.get(i));
                }
                indentingPrintWriter.decreaseIndent();
            }
        }
        indentingPrintWriter.decreaseIndent();
    }

    public static void dumpSparseIntArray(IndentingPrintWriter indentingPrintWriter, SparseIntArray sparseIntArray, String str, String str2, String str3) {
        if (sparseIntArray == null) {
            indentingPrintWriter.print("No ");
            indentingPrintWriter.print(str);
            indentingPrintWriter.println(" mappings");
            return;
        }
        indentingPrintWriter.print("Number of ");
        indentingPrintWriter.print(str);
        indentingPrintWriter.print(" mappings: ");
        indentingPrintWriter.println(sparseIntArray.size());
        if (sparseIntArray.size() <= 0) {
            return;
        }
        indentingPrintWriter.increaseIndent();
        for (int i = 0; i < sparseIntArray.size(); i++) {
            indentingPrintWriter.print(str2);
            indentingPrintWriter.print(':');
            indentingPrintWriter.print(sparseIntArray.keyAt(i));
            indentingPrintWriter.print(" -> ");
            indentingPrintWriter.print(str3);
            indentingPrintWriter.print(':');
            indentingPrintWriter.println(sparseIntArray.valueAt(i));
        }
        indentingPrintWriter.decreaseIndent();
    }

    public void dump(PrintWriter printWriter, String[] strArr) {
        if (printWriter instanceof IndentingPrintWriter) {
            dump((IndentingPrintWriter) printWriter);
        } else {
            dump(new IndentingPrintWriter(printWriter));
        }
    }

    public final int getCurrentUserId() {
        int i;
        synchronized (this.mLock) {
            i = this.mCurrentUserId;
        }
        return i;
    }

    @GuardedBy({"mLock"})
    public final boolean isCurrentUserLocked(int i) {
        int i2;
        return (i == -10000 || (i2 = this.mCurrentUserId) == -10000 || i2 != i) ? false : true;
    }

    public final boolean isCurrentUserOrRunningProfileOfCurrentUser(int i) {
        synchronized (this.mLock) {
            if (i != -10000) {
                int i2 = this.mCurrentUserId;
                if (i2 != -10000) {
                    if (i2 == i) {
                        return true;
                    }
                    return this.mStartedVisibleProfileGroupIds.get(i, -10000) == this.mCurrentUserId;
                }
            }
            return false;
        }
    }

    @GuardedBy({"mLock"})
    public final boolean isStartedVisibleProfileLocked(int i) {
        return isProfile(i, this.mStartedVisibleProfileGroupIds.get(i, -10000));
    }

    public final void validateUserStartMode(int i) {
        if (i == 1 || i == 2 || i == 3) {
            return;
        }
        throw new IllegalArgumentException("Invalid user start mode: " + i);
    }

    public static String secondaryDisplayMappingStatusToString(int i) {
        return DebugUtils.constantToString(UserVisibilityMediator.class, "SECONDARY_DISPLAY_MAPPING_", i);
    }
}
