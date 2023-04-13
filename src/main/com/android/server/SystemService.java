package com.android.server;

import android.annotation.SystemApi;
import android.app.ActivityThread;
import android.content.Context;
import android.content.pm.UserInfo;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.internal.annotations.VisibleForTesting;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
@SystemApi(client = SystemApi.Client.SYSTEM_SERVER)
/* loaded from: classes.dex */
public abstract class SystemService {
    protected static final boolean DEBUG_USER = false;
    public static final int PHASE_ACTIVITY_MANAGER_READY = 550;
    public static final int PHASE_BOOT_COMPLETED = 1000;
    public static final int PHASE_DEVICE_SPECIFIC_SERVICES_READY = 520;
    public static final int PHASE_LOCK_SETTINGS_READY = 480;
    public static final int PHASE_SYSTEM_SERVICES_READY = 500;
    public static final int PHASE_THIRD_PARTY_APPS_CAN_START = 600;
    public static final int PHASE_WAIT_FOR_DEFAULT_DISPLAY = 100;
    public static final int PHASE_WAIT_FOR_SENSOR_SERVICE = 200;
    private final Context mContext;

    public boolean isUserSupported(TargetUser targetUser) {
        return true;
    }

    public void onBootPhase(int i) {
    }

    public abstract void onStart();

    public void onUserCompletedEvent(TargetUser targetUser, UserCompletedEventType userCompletedEventType) {
    }

    public void onUserStarting(TargetUser targetUser) {
    }

    public void onUserStopped(TargetUser targetUser) {
    }

    public void onUserStopping(TargetUser targetUser) {
    }

    public void onUserSwitching(TargetUser targetUser, TargetUser targetUser2) {
    }

    public void onUserUnlocked(TargetUser targetUser) {
    }

    public void onUserUnlocking(TargetUser targetUser) {
    }

    @SystemApi(client = SystemApi.Client.SYSTEM_SERVER)
    /* loaded from: classes.dex */
    public static final class TargetUser {
        public final boolean mFull;
        public final boolean mPreCreated;
        public final boolean mProfile;
        public final int mUserId;
        public final String mUserType;

        public TargetUser(UserInfo userInfo) {
            this.mUserId = userInfo.id;
            this.mFull = userInfo.isFull();
            this.mProfile = userInfo.isProfile();
            this.mUserType = userInfo.userType;
            this.mPreCreated = userInfo.preCreated;
        }

        public boolean isFull() {
            return this.mFull;
        }

        public boolean isProfile() {
            return this.mProfile;
        }

        public boolean isManagedProfile() {
            return UserManager.isUserTypeManagedProfile(this.mUserType);
        }

        public boolean isPreCreated() {
            return this.mPreCreated;
        }

        public UserHandle getUserHandle() {
            return UserHandle.of(this.mUserId);
        }

        public int getUserIdentifier() {
            return this.mUserId;
        }

        public String toString() {
            return Integer.toString(this.mUserId);
        }

        public void dump(PrintWriter printWriter) {
            printWriter.print(getUserIdentifier());
            if (isFull() || isProfile()) {
                printWriter.print('(');
                if (isFull()) {
                    printWriter.print("full");
                }
                if (isProfile()) {
                    printWriter.print("profile");
                }
                printWriter.print(')');
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class UserCompletedEventType {
        public final int mEventType;

        public UserCompletedEventType(int i) {
            this.mEventType = i;
        }

        @VisibleForTesting
        public static UserCompletedEventType newUserCompletedEventTypeForTest(int i) {
            return new UserCompletedEventType(i);
        }

        public boolean includesOnUserStarting() {
            return (this.mEventType & 1) != 0;
        }

        public boolean includesOnUserUnlocked() {
            return (this.mEventType & 2) != 0;
        }

        public boolean includesOnUserSwitching() {
            return (this.mEventType & 4) != 0;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("{");
            if (includesOnUserSwitching()) {
                sb.append("|Switching");
            }
            if (includesOnUserUnlocked()) {
                sb.append("|Unlocked");
            }
            if (includesOnUserStarting()) {
                sb.append("|Starting");
            }
            if (sb.length() > 1) {
                sb.append("|");
            }
            sb.append("}");
            return sb.toString();
        }
    }

    public SystemService(Context context) {
        this.mContext = context;
    }

    public final Context getContext() {
        return this.mContext;
    }

    public final Context getUiContext() {
        return ActivityThread.currentActivityThread().getSystemUiContext();
    }

    public final boolean isSafeMode() {
        return getManager().isSafeMode();
    }

    public void dumpSupportedUsers(PrintWriter printWriter, String str) {
        List users = UserManager.get(this.mContext).getUsers();
        ArrayList arrayList = new ArrayList(users.size());
        for (int i = 0; i < users.size(); i++) {
            UserInfo userInfo = (UserInfo) users.get(i);
            if (isUserSupported(new TargetUser(userInfo))) {
                arrayList.add(Integer.valueOf(userInfo.id));
            }
        }
        if (arrayList.isEmpty()) {
            printWriter.print(str);
            printWriter.println("No supported users");
            return;
        }
        int size = arrayList.size();
        printWriter.print(str);
        printWriter.print(size);
        printWriter.print(" supported user");
        if (size > 1) {
            printWriter.print("s");
        }
        printWriter.print(": ");
        printWriter.println(arrayList);
    }

    public final void publishBinderService(String str, IBinder iBinder) {
        publishBinderService(str, iBinder, false);
    }

    public final void publishBinderService(String str, IBinder iBinder, boolean z) {
        publishBinderService(str, iBinder, z, 8);
    }

    public final void publishBinderService(String str, IBinder iBinder, boolean z, int i) {
        ServiceManager.addService(str, iBinder, z, i);
    }

    public final IBinder getBinderService(String str) {
        return ServiceManager.getService(str);
    }

    public final <T> void publishLocalService(Class<T> cls, T t) {
        LocalServices.addService(cls, t);
    }

    public final <T> T getLocalService(Class<T> cls) {
        return (T) LocalServices.getService(cls);
    }

    private SystemServiceManager getManager() {
        return (SystemServiceManager) LocalServices.getService(SystemServiceManager.class);
    }
}
