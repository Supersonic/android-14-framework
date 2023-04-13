package android.app;

import android.content.Intent;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public final class ApplicationStartInfo implements Parcelable {
    public static final Parcelable.Creator<ApplicationStartInfo> CREATOR = new Parcelable.Creator<ApplicationStartInfo>() { // from class: android.app.ApplicationStartInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ApplicationStartInfo createFromParcel(Parcel in) {
            return new ApplicationStartInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ApplicationStartInfo[] newArray(int size) {
            return new ApplicationStartInfo[size];
        }
    };
    public static final int LAUNCH_MODE_SINGLE_INSTANCE = 2;
    public static final int LAUNCH_MODE_SINGLE_INSTANCE_PER_TASK = 4;
    public static final int LAUNCH_MODE_SINGLE_TASK = 3;
    public static final int LAUNCH_MODE_SINGLE_TOP = 1;
    public static final int LAUNCH_MODE_STANDARD = 0;
    public static final int STARTUP_STATE_ERROR = 1;
    public static final int STARTUP_STATE_FIRST_FRAME_DRAWN = 2;
    public static final int STARTUP_STATE_STARTED = 0;
    public static final int START_REASON_ALARM = 0;
    public static final int START_REASON_BACKUP = 1;
    public static final int START_REASON_BOOT_COMPLETE = 2;
    public static final int START_REASON_BROADCAST = 3;
    public static final int START_REASON_CONTENT_PROVIDER = 4;
    public static final int START_REASON_JOB = 5;
    public static final int START_REASON_LAUNCHER = 6;
    public static final int START_REASON_OTHER = 7;
    public static final int START_REASON_PUSH = 8;
    public static final int START_REASON_RESUMED_ACTIVITY = 9;
    public static final int START_REASON_SERVICE = 10;
    public static final int START_REASON_START_ACTIVITY = 11;
    public static final int START_TIMESTAMP_APPLICATION_ONCREATE = 2;
    public static final int START_TIMESTAMP_BIND_APPLICATION = 3;
    public static final int START_TIMESTAMP_FIRST_FRAME = 4;
    public static final int START_TIMESTAMP_FULLY_DRAWN = 5;
    public static final int START_TIMESTAMP_JAVA_CLASSLOADING_COMPLETE = 1;
    public static final int START_TIMESTAMP_LAUNCH = 0;
    public static final int START_TYPE_COLD = 0;
    public static final int START_TYPE_HOT = 2;
    public static final int START_TYPE_WARM = 1;
    private int mDefiningUid;
    private int mLaunchMode;
    private int mPackageUid;
    private int mPid;
    private String mProcessName;
    private int mRealUid;
    private int mReason;
    private Intent mStartIntent;
    private int mStartType;
    private int mStartupState;
    private Map<Integer, Long> mStartupTimestampsNs;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface LaunchMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface StartReason {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface StartType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface StartupState {
    }

    @Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface StartupTimestamp {
    }

    public void setStartupState(int startupState) {
        this.mStartupState = startupState;
    }

    public void setPid(int pid) {
        this.mPid = pid;
    }

    public void setRealUid(int uid) {
        this.mRealUid = uid;
    }

    public void setPackageUid(int uid) {
        this.mPackageUid = uid;
    }

    public void setDefiningUid(int uid) {
        this.mDefiningUid = uid;
    }

    public void setProcessName(String processName) {
        this.mProcessName = intern(processName);
    }

    public void setReason(int reason) {
        this.mReason = reason;
    }

    public void addStartupTimestamp(int key, long timestampNs) {
        if (this.mStartupTimestampsNs == null) {
            this.mStartupTimestampsNs = new HashMap();
        }
        this.mStartupTimestampsNs.put(Integer.valueOf(key), Long.valueOf(timestampNs));
    }

    public void setStartType(int startType) {
        this.mStartType = startType;
    }

    public void setIntent(Intent startIntent) {
        this.mStartIntent = startIntent;
    }

    public void setLaunchMode(int launchMode) {
        this.mLaunchMode = launchMode;
    }

    public int getStartupState() {
        return this.mStartupState;
    }

    public int getPid() {
        return this.mPid;
    }

    public int getRealUid() {
        return this.mRealUid;
    }

    public int getPackageUid() {
        return this.mPackageUid;
    }

    public int getDefiningUid() {
        return this.mDefiningUid;
    }

    public String getProcessName() {
        return this.mProcessName;
    }

    public int getReason() {
        return this.mReason;
    }

    public Map<Integer, Long> getStartupTimestamps() {
        if (this.mStartupTimestampsNs == null) {
            this.mStartupTimestampsNs = new HashMap();
        }
        return this.mStartupTimestampsNs;
    }

    public int getStartType() {
        return this.mStartType;
    }

    public Intent getIntent() {
        return this.mStartIntent;
    }

    public int getLaunchMode() {
        return this.mLaunchMode;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mStartupState);
        dest.writeInt(this.mPid);
        dest.writeInt(this.mRealUid);
        dest.writeInt(this.mPackageUid);
        dest.writeInt(this.mDefiningUid);
        dest.writeString(this.mProcessName);
        dest.writeInt(this.mReason);
        dest.writeInt(this.mStartupTimestampsNs.size());
        for (Integer num : this.mStartupTimestampsNs.keySet()) {
            int key = num.intValue();
            dest.writeInt(key);
            dest.writeLong(this.mStartupTimestampsNs.get(Integer.valueOf(key)).longValue());
        }
        dest.writeInt(this.mStartType);
        dest.writeParcelable(this.mStartIntent, flags);
        dest.writeInt(this.mLaunchMode);
    }

    public ApplicationStartInfo() {
    }

    public ApplicationStartInfo(ApplicationStartInfo other) {
        this.mStartupState = other.mStartupState;
        this.mPid = other.mPid;
        this.mRealUid = other.mRealUid;
        this.mPackageUid = other.mPackageUid;
        this.mDefiningUid = other.mDefiningUid;
        this.mProcessName = other.mProcessName;
        this.mReason = other.mReason;
        this.mStartupTimestampsNs = other.mStartupTimestampsNs;
        this.mStartType = other.mStartType;
        this.mStartIntent = other.mStartIntent;
        this.mLaunchMode = other.mLaunchMode;
    }

    private ApplicationStartInfo(Parcel in) {
        this.mStartupState = in.readInt();
        this.mPid = in.readInt();
        this.mRealUid = in.readInt();
        this.mPackageUid = in.readInt();
        this.mDefiningUid = in.readInt();
        this.mProcessName = intern(in.readString());
        this.mReason = in.readInt();
        int starupTimestampCount = in.readInt();
        for (int i = 0; i < starupTimestampCount; i++) {
            int key = in.readInt();
            long val = in.readLong();
            addStartupTimestamp(key, val);
        }
        int i2 = in.readInt();
        this.mStartType = i2;
        this.mStartIntent = (Intent) in.readParcelable(Intent.class.getClassLoader(), Intent.class);
        this.mLaunchMode = in.readInt();
    }

    private static String intern(String source) {
        if (source != null) {
            return source.intern();
        }
        return null;
    }
}
