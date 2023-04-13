package android.location.util.identity;

import android.content.Context;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Binder;
import android.p008os.Process;
import android.p008os.UserHandle;
import android.p008os.WorkSource;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.HexDump;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class CallerIdentity {
    private final String mAttributionTag;
    private final String mListenerId;
    private final String mPackageName;
    private final int mPid;
    private final int mUid;

    public static CallerIdentity forTest(int uid, int pid, String packageName, String attributionTag) {
        return forTest(uid, pid, packageName, attributionTag, null);
    }

    public static CallerIdentity forTest(int uid, int pid, String packageName, String attributionTag, String listenerId) {
        return new CallerIdentity(uid, pid, packageName, attributionTag, listenerId);
    }

    public static CallerIdentity forAggregation(CallerIdentity callerIdentity) {
        if (callerIdentity.getPid() == 0 && callerIdentity.getListenerId() == null) {
            return callerIdentity;
        }
        return new CallerIdentity(callerIdentity.getUid(), 0, callerIdentity.getPackageName(), callerIdentity.getAttributionTag(), null);
    }

    public static CallerIdentity fromContext(Context context) {
        return new CallerIdentity(Process.myUid(), Process.myPid(), context.getPackageName(), context.getAttributionTag(), null);
    }

    public static CallerIdentity fromBinder(Context context, String packageName, String attributionTag) {
        return fromBinder(context, packageName, attributionTag, null);
    }

    public static CallerIdentity fromBinder(Context context, String packageName, String attributionTag, String listenerId) {
        int uid = Binder.getCallingUid();
        if (!ArrayUtils.contains(context.getPackageManager().getPackagesForUid(uid), packageName)) {
            throw new SecurityException("invalid package \"" + packageName + "\" for uid " + uid);
        }
        return fromBinderUnsafe(packageName, attributionTag, listenerId);
    }

    public static CallerIdentity fromBinderUnsafe(String packageName, String attributionTag) {
        return fromBinderUnsafe(packageName, attributionTag, null);
    }

    public static CallerIdentity fromBinderUnsafe(String packageName, String attributionTag, String listenerId) {
        return new CallerIdentity(Binder.getCallingUid(), Binder.getCallingPid(), packageName, attributionTag, listenerId);
    }

    /* loaded from: classes2.dex */
    private static class Loader {
        private static final int MY_UID = Process.myUid();
        private static final int MY_PID = Process.myPid();

        private Loader() {
        }
    }

    private CallerIdentity(int uid, int pid, String packageName, String attributionTag, String listenerId) {
        this.mUid = uid;
        this.mPid = pid;
        this.mPackageName = (String) Objects.requireNonNull(packageName);
        this.mAttributionTag = attributionTag;
        this.mListenerId = listenerId;
    }

    public int getUid() {
        return this.mUid;
    }

    public int getPid() {
        return this.mPid;
    }

    public int getUserId() {
        return UserHandle.getUserId(this.mUid);
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getAttributionTag() {
        return this.mAttributionTag;
    }

    public String getListenerId() {
        return this.mListenerId;
    }

    public boolean isSystemServer() {
        return this.mUid == 1000;
    }

    public boolean isMyUser() {
        return UserHandle.getUserId(this.mUid) == UserHandle.getUserId(Loader.MY_UID);
    }

    public boolean isMyUid() {
        return this.mUid == Loader.MY_UID;
    }

    public boolean isMyProcess() {
        return this.mPid == Loader.MY_PID;
    }

    public WorkSource addToWorkSource(WorkSource workSource) {
        if (workSource == null) {
            return new WorkSource(this.mUid, this.mPackageName);
        }
        workSource.add(this.mUid, this.mPackageName);
        return workSource;
    }

    public String toString() {
        int length = this.mPackageName.length() + 10;
        String str = this.mAttributionTag;
        if (str != null) {
            length += str.length();
        }
        StringBuilder builder = new StringBuilder(length);
        builder.append(this.mUid).append("/").append(this.mPackageName);
        if (this.mAttributionTag != null) {
            builder.append(NavigationBarInflaterView.SIZE_MOD_START);
            if (this.mAttributionTag.startsWith(this.mPackageName)) {
                builder.append(this.mAttributionTag.substring(this.mPackageName.length()));
            } else {
                builder.append(this.mAttributionTag);
            }
            builder.append(NavigationBarInflaterView.SIZE_MOD_END);
        }
        if (this.mListenerId != null) {
            builder.append("/").append(HexDump.toHexString(this.mListenerId.hashCode()));
        }
        return builder.toString();
    }

    public boolean equals(Object o) {
        String str;
        String str2;
        if (this == o) {
            return true;
        }
        if (o instanceof CallerIdentity) {
            CallerIdentity that = (CallerIdentity) o;
            return this.mUid == that.mUid && this.mPid == that.mPid && this.mPackageName.equals(that.mPackageName) && Objects.equals(this.mAttributionTag, that.mAttributionTag) && ((str = this.mListenerId) == null || (str2 = that.mListenerId) == null || str.equals(str2));
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mUid), Integer.valueOf(this.mPid), this.mPackageName, this.mAttributionTag);
    }
}
