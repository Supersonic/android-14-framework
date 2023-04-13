package android.app;

import android.app.IAppTraceRetriever;
import android.app.IParcelFileDescriptorRetriever;
import android.icu.text.SimpleDateFormat;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.text.TextUtils;
import android.util.DebugUtils;
import android.util.proto.ProtoInputStream;
import android.util.proto.ProtoOutputStream;
import android.util.proto.WireTypeMismatchException;
import com.android.internal.accessibility.common.ShortcutConstants;
import com.android.internal.util.ArrayUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
/* loaded from: classes.dex */
public final class ApplicationExitInfo implements Parcelable {
    public static final Parcelable.Creator<ApplicationExitInfo> CREATOR = new Parcelable.Creator<ApplicationExitInfo>() { // from class: android.app.ApplicationExitInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ApplicationExitInfo createFromParcel(Parcel in) {
            return new ApplicationExitInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ApplicationExitInfo[] newArray(int size) {
            return new ApplicationExitInfo[size];
        }
    };
    public static final int REASON_ANR = 6;
    public static final int REASON_CRASH = 4;
    public static final int REASON_CRASH_NATIVE = 5;
    public static final int REASON_DEPENDENCY_DIED = 12;
    public static final int REASON_EXCESSIVE_RESOURCE_USAGE = 9;
    public static final int REASON_EXIT_SELF = 1;
    public static final int REASON_FREEZER = 14;
    public static final int REASON_INITIALIZATION_FAILURE = 7;
    public static final int REASON_LOW_MEMORY = 3;
    public static final int REASON_OTHER = 13;
    public static final int REASON_PACKAGE_STATE_CHANGE = 15;
    public static final int REASON_PACKAGE_UPDATED = 16;
    public static final int REASON_PERMISSION_CHANGE = 8;
    public static final int REASON_SIGNALED = 2;
    public static final int REASON_UNKNOWN = 0;
    public static final int REASON_USER_REQUESTED = 10;
    public static final int REASON_USER_STOPPED = 11;
    public static final int SUBREASON_CACHED_IDLE_FORCED_APP_STANDBY = 18;
    public static final int SUBREASON_EXCESSIVE_CPU = 7;
    public static final int SUBREASON_FORCE_STOP = 21;
    public static final int SUBREASON_FREEZER_BINDER_IOCTL = 19;
    public static final int SUBREASON_FREEZER_BINDER_TRANSACTION = 20;
    public static final int SUBREASON_IMPERCEPTIBLE = 15;
    public static final int SUBREASON_INVALID_START = 13;
    public static final int SUBREASON_INVALID_STATE = 14;
    public static final int SUBREASON_ISOLATED_NOT_NEEDED = 17;
    public static final int SUBREASON_KILL_ALL_BG_EXCEPT = 10;
    public static final int SUBREASON_KILL_ALL_FG = 9;
    public static final int SUBREASON_KILL_BACKGROUND = 24;
    public static final int SUBREASON_KILL_PID = 12;
    public static final int SUBREASON_KILL_UID = 11;
    public static final int SUBREASON_LARGE_CACHED = 5;
    public static final int SUBREASON_MEMORY_PRESSURE = 6;
    public static final int SUBREASON_PACKAGE_UPDATE = 25;
    public static final int SUBREASON_REMOVE_LRU = 16;
    public static final int SUBREASON_REMOVE_TASK = 22;
    public static final int SUBREASON_SDK_SANDBOX_DIED = 27;
    public static final int SUBREASON_SDK_SANDBOX_NOT_NEEDED = 28;
    public static final int SUBREASON_STOP_APP = 23;
    public static final int SUBREASON_SYSTEM_UPDATE_DONE = 8;
    public static final int SUBREASON_TOO_MANY_CACHED = 2;
    public static final int SUBREASON_TOO_MANY_EMPTY = 3;
    public static final int SUBREASON_TRIM_EMPTY = 4;
    public static final int SUBREASON_UNDELIVERED_BROADCAST = 26;
    public static final int SUBREASON_UNKNOWN = 0;
    public static final int SUBREASON_WAIT_FOR_DEBUGGER = 1;
    private IAppTraceRetriever mAppTraceRetriever;
    private int mConnectionGroup;
    private int mDefiningUid;
    private String mDescription;
    private boolean mHasForegroundServices;
    private int mImportance;
    private boolean mLoggedInStatsd;
    private IParcelFileDescriptorRetriever mNativeTombstoneRetriever;
    private String[] mPackageList;
    private String mPackageName;
    private int mPackageUid;
    private int mPid;
    private String mProcessName;
    private long mPss;
    private int mRealUid;
    private int mReason;
    private long mRss;
    private byte[] mState;
    private int mStatus;
    private int mSubReason;
    private long mTimestamp;
    private File mTraceFile;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Reason {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface SubReason {
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

    public int getStatus() {
        return this.mStatus;
    }

    public int getImportance() {
        return this.mImportance;
    }

    public long getPss() {
        return this.mPss;
    }

    public long getRss() {
        return this.mRss;
    }

    public long getTimestamp() {
        return this.mTimestamp;
    }

    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        if (this.mSubReason != 0) {
            sb.append(NavigationBarInflaterView.SIZE_MOD_START);
            sb.append(subreasonToString(this.mSubReason));
            sb.append(NavigationBarInflaterView.SIZE_MOD_END);
        }
        if (!TextUtils.isEmpty(this.mDescription)) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(this.mDescription);
        }
        return sb.toString();
    }

    public UserHandle getUserHandle() {
        return UserHandle.m145of(UserHandle.getUserId(this.mRealUid));
    }

    public byte[] getProcessStateSummary() {
        return this.mState;
    }

    public InputStream getTraceInputStream() throws IOException {
        IAppTraceRetriever iAppTraceRetriever = this.mAppTraceRetriever;
        if (iAppTraceRetriever == null && this.mNativeTombstoneRetriever == null) {
            return null;
        }
        try {
            IParcelFileDescriptorRetriever iParcelFileDescriptorRetriever = this.mNativeTombstoneRetriever;
            if (iParcelFileDescriptorRetriever != null) {
                ParcelFileDescriptor pfd = iParcelFileDescriptorRetriever.getPfd();
                if (pfd == null) {
                    return null;
                }
                return new ParcelFileDescriptor.AutoCloseInputStream(pfd);
            }
            ParcelFileDescriptor fd = iAppTraceRetriever.getTraceFileDescriptor(this.mPackageName, this.mPackageUid, this.mPid);
            if (fd == null) {
                return null;
            }
            return new GZIPInputStream(new ParcelFileDescriptor.AutoCloseInputStream(fd));
        } catch (RemoteException e) {
            return null;
        }
    }

    public File getTraceFile() {
        return this.mTraceFile;
    }

    public int getSubReason() {
        return this.mSubReason;
    }

    public int getConnectionGroup() {
        return this.mConnectionGroup;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String[] getPackageList() {
        return this.mPackageList;
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

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public void setImportance(int importance) {
        this.mImportance = importance;
    }

    public void setPss(long pss) {
        this.mPss = pss;
    }

    public void setRss(long rss) {
        this.mRss = rss;
    }

    public void setTimestamp(long timestamp) {
        this.mTimestamp = timestamp;
    }

    public void setDescription(String description) {
        this.mDescription = intern(description);
    }

    public void setSubReason(int subReason) {
        this.mSubReason = subReason;
    }

    public void setConnectionGroup(int connectionGroup) {
        this.mConnectionGroup = connectionGroup;
    }

    public void setPackageName(String packageName) {
        this.mPackageName = intern(packageName);
    }

    public void setPackageList(String[] packageList) {
        this.mPackageList = packageList;
    }

    public void setProcessStateSummary(byte[] state) {
        this.mState = state;
    }

    public void setTraceFile(File traceFile) {
        this.mTraceFile = traceFile;
    }

    public void setAppTraceRetriever(IAppTraceRetriever retriever) {
        this.mAppTraceRetriever = retriever;
    }

    public void setNativeTombstoneRetriever(IParcelFileDescriptorRetriever retriever) {
        this.mNativeTombstoneRetriever = retriever;
    }

    public boolean isLoggedInStatsd() {
        return this.mLoggedInStatsd;
    }

    public void setLoggedInStatsd(boolean loggedInStatsd) {
        this.mLoggedInStatsd = loggedInStatsd;
    }

    public boolean hasForegroundServices() {
        return this.mHasForegroundServices;
    }

    public void setHasForegroundServices(boolean hasForegroundServices) {
        this.mHasForegroundServices = hasForegroundServices;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mPid);
        dest.writeInt(this.mRealUid);
        dest.writeInt(this.mPackageUid);
        dest.writeInt(this.mDefiningUid);
        dest.writeString(this.mProcessName);
        dest.writeString(this.mPackageName);
        dest.writeInt(this.mConnectionGroup);
        dest.writeInt(this.mReason);
        dest.writeInt(this.mSubReason);
        dest.writeInt(this.mStatus);
        dest.writeInt(this.mImportance);
        dest.writeLong(this.mPss);
        dest.writeLong(this.mRss);
        dest.writeLong(this.mTimestamp);
        dest.writeString(this.mDescription);
        dest.writeByteArray(this.mState);
        if (this.mAppTraceRetriever != null) {
            dest.writeInt(1);
            dest.writeStrongBinder(this.mAppTraceRetriever.asBinder());
        } else {
            dest.writeInt(0);
        }
        if (this.mNativeTombstoneRetriever != null) {
            dest.writeInt(1);
            dest.writeStrongBinder(this.mNativeTombstoneRetriever.asBinder());
            return;
        }
        dest.writeInt(0);
    }

    public ApplicationExitInfo() {
    }

    public ApplicationExitInfo(ApplicationExitInfo other) {
        this.mPid = other.mPid;
        this.mRealUid = other.mRealUid;
        this.mPackageUid = other.mPackageUid;
        this.mDefiningUid = other.mDefiningUid;
        this.mProcessName = other.mProcessName;
        this.mPackageName = other.mPackageName;
        this.mConnectionGroup = other.mConnectionGroup;
        this.mReason = other.mReason;
        this.mStatus = other.mStatus;
        this.mSubReason = other.mSubReason;
        this.mImportance = other.mImportance;
        this.mPss = other.mPss;
        this.mRss = other.mRss;
        this.mTimestamp = other.mTimestamp;
        this.mDescription = other.mDescription;
        this.mPackageName = other.mPackageName;
        this.mPackageList = other.mPackageList;
        this.mState = other.mState;
        this.mTraceFile = other.mTraceFile;
        this.mAppTraceRetriever = other.mAppTraceRetriever;
        this.mNativeTombstoneRetriever = other.mNativeTombstoneRetriever;
        this.mLoggedInStatsd = other.mLoggedInStatsd;
        this.mHasForegroundServices = other.mHasForegroundServices;
    }

    private ApplicationExitInfo(Parcel in) {
        this.mPid = in.readInt();
        this.mRealUid = in.readInt();
        this.mPackageUid = in.readInt();
        this.mDefiningUid = in.readInt();
        this.mProcessName = intern(in.readString());
        this.mPackageName = intern(in.readString());
        this.mConnectionGroup = in.readInt();
        this.mReason = in.readInt();
        this.mSubReason = in.readInt();
        this.mStatus = in.readInt();
        this.mImportance = in.readInt();
        this.mPss = in.readLong();
        this.mRss = in.readLong();
        this.mTimestamp = in.readLong();
        this.mDescription = intern(in.readString());
        this.mState = in.createByteArray();
        if (in.readInt() == 1) {
            this.mAppTraceRetriever = IAppTraceRetriever.Stub.asInterface(in.readStrongBinder());
        }
        if (in.readInt() == 1) {
            this.mNativeTombstoneRetriever = IParcelFileDescriptorRetriever.Stub.asInterface(in.readStrongBinder());
        }
    }

    private static String intern(String source) {
        if (source != null) {
            return source.intern();
        }
        return null;
    }

    public void dump(PrintWriter pw, String prefix, String seqSuffix, SimpleDateFormat sdf) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix).append("ApplicationExitInfo ").append(seqSuffix).append(ShortcutConstants.SERVICES_SEPARATOR).append('\n');
        sb.append(prefix).append(' ').append(" timestamp=").append(sdf.format(new Date(this.mTimestamp))).append(" pid=").append(this.mPid).append(" realUid=").append(this.mRealUid).append(" packageUid=").append(this.mPackageUid).append(" definingUid=").append(this.mDefiningUid).append(" user=").append(UserHandle.getUserId(this.mPackageUid)).append('\n');
        sb.append(prefix).append(' ').append(" process=").append(this.mProcessName).append(" reason=").append(this.mReason).append(" (").append(reasonCodeToString(this.mReason)).append(NavigationBarInflaterView.KEY_CODE_END).append(" subreason=").append(this.mSubReason).append(" (").append(subreasonToString(this.mSubReason)).append(NavigationBarInflaterView.KEY_CODE_END).append(" status=").append(this.mStatus).append('\n');
        sb.append(prefix).append(' ').append(" importance=").append(this.mImportance).append(" pss=");
        DebugUtils.sizeValueToString(this.mPss << 10, sb);
        sb.append(" rss=");
        DebugUtils.sizeValueToString(this.mRss << 10, sb);
        sb.append(" description=").append(this.mDescription).append(" state=").append(ArrayUtils.isEmpty(this.mState) ? "empty" : Integer.toString(this.mState.length) + " bytes").append(" trace=").append(this.mTraceFile).append('\n');
        pw.print(sb.toString());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ApplicationExitInfo(timestamp=");
        sb.append(new SimpleDateFormat().format(new Date(this.mTimestamp)));
        sb.append(" pid=").append(this.mPid);
        sb.append(" realUid=").append(this.mRealUid);
        sb.append(" packageUid=").append(this.mPackageUid);
        sb.append(" definingUid=").append(this.mDefiningUid);
        sb.append(" user=").append(UserHandle.getUserId(this.mPackageUid));
        sb.append(" process=").append(this.mProcessName);
        sb.append(" reason=").append(this.mReason).append(" (").append(reasonCodeToString(this.mReason)).append(NavigationBarInflaterView.KEY_CODE_END);
        sb.append(" subreason=").append(this.mSubReason).append(" (").append(subreasonToString(this.mSubReason)).append(NavigationBarInflaterView.KEY_CODE_END);
        sb.append(" status=").append(this.mStatus);
        sb.append(" importance=").append(this.mImportance);
        sb.append(" pss=");
        DebugUtils.sizeValueToString(this.mPss << 10, sb);
        sb.append(" rss=");
        DebugUtils.sizeValueToString(this.mRss << 10, sb);
        sb.append(" description=").append(this.mDescription);
        sb.append(" state=").append(ArrayUtils.isEmpty(this.mState) ? "empty" : Integer.toString(this.mState.length) + " bytes");
        sb.append(" trace=").append(this.mTraceFile);
        return sb.toString();
    }

    public static String reasonCodeToString(int reason) {
        switch (reason) {
            case 1:
                return "EXIT_SELF";
            case 2:
                return "SIGNALED";
            case 3:
                return "LOW_MEMORY";
            case 4:
                return "APP CRASH(EXCEPTION)";
            case 5:
                return "APP CRASH(NATIVE)";
            case 6:
                return "ANR";
            case 7:
                return "INITIALIZATION FAILURE";
            case 8:
                return "PERMISSION CHANGE";
            case 9:
                return "EXCESSIVE RESOURCE USAGE";
            case 10:
                return "USER REQUESTED";
            case 11:
                return "USER STOPPED";
            case 12:
                return "DEPENDENCY DIED";
            case 13:
                return "OTHER KILLS BY SYSTEM";
            case 14:
                return "FREEZER";
            case 15:
                return "STATE CHANGE";
            case 16:
                return "PACKAGE UPDATED";
            default:
                return "UNKNOWN";
        }
    }

    public static String subreasonToString(int subreason) {
        switch (subreason) {
            case 1:
                return "WAIT FOR DEBUGGER";
            case 2:
                return "TOO MANY CACHED PROCS";
            case 3:
                return "TOO MANY EMPTY PROCS";
            case 4:
                return "TRIM EMPTY";
            case 5:
                return "LARGE CACHED";
            case 6:
                return "MEMORY PRESSURE";
            case 7:
                return "EXCESSIVE CPU USAGE";
            case 8:
                return "SYSTEM UPDATE_DONE";
            case 9:
                return "KILL ALL FG";
            case 10:
                return "KILL ALL BG EXCEPT";
            case 11:
                return "KILL UID";
            case 12:
                return "KILL PID";
            case 13:
                return "INVALID START";
            case 14:
                return "INVALID STATE";
            case 15:
                return "IMPERCEPTIBLE";
            case 16:
                return "REMOVE LRU";
            case 17:
                return "ISOLATED NOT NEEDED";
            case 18:
            default:
                return "UNKNOWN";
            case 19:
                return "FREEZER BINDER IOCTL";
            case 20:
                return "FREEZER BINDER TRANSACTION";
            case 21:
                return "FORCE STOP";
            case 22:
                return "REMOVE TASK";
            case 23:
                return "STOP APP";
            case 24:
                return "KILL BACKGROUND";
            case 25:
                return "PACKAGE UPDATE";
            case 26:
                return "UNDELIVERED BROADCAST";
        }
    }

    public void writeToProto(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        proto.write(1120986464257L, this.mPid);
        proto.write(1120986464258L, this.mRealUid);
        proto.write(1120986464259L, this.mPackageUid);
        proto.write(1120986464260L, this.mDefiningUid);
        proto.write(1138166333445L, this.mProcessName);
        proto.write(1120986464262L, this.mConnectionGroup);
        proto.write(1159641169927L, this.mReason);
        proto.write(1159641169928L, this.mSubReason);
        proto.write(1120986464265L, this.mStatus);
        proto.write(1159641169930L, this.mImportance);
        proto.write(1112396529675L, this.mPss);
        proto.write(1112396529676L, this.mRss);
        proto.write(1112396529677L, this.mTimestamp);
        proto.write(1138166333454L, this.mDescription);
        proto.write(ApplicationExitInfoProto.STATE, this.mState);
        File file = this.mTraceFile;
        proto.write(1138166333456L, file == null ? null : file.getAbsolutePath());
        proto.end(token);
    }

    public void readFromProto(ProtoInputStream proto, long fieldId) throws IOException, WireTypeMismatchException {
        long token = proto.start(fieldId);
        while (proto.nextField() != -1) {
            switch (proto.getFieldNumber()) {
                case 1:
                    this.mPid = proto.readInt(1120986464257L);
                    break;
                case 2:
                    this.mRealUid = proto.readInt(1120986464258L);
                    break;
                case 3:
                    this.mPackageUid = proto.readInt(1120986464259L);
                    break;
                case 4:
                    this.mDefiningUid = proto.readInt(1120986464260L);
                    break;
                case 5:
                    this.mProcessName = intern(proto.readString(1138166333445L));
                    break;
                case 6:
                    this.mConnectionGroup = proto.readInt(1120986464262L);
                    break;
                case 7:
                    this.mReason = proto.readInt(1159641169927L);
                    break;
                case 8:
                    this.mSubReason = proto.readInt(1159641169928L);
                    break;
                case 9:
                    this.mStatus = proto.readInt(1120986464265L);
                    break;
                case 10:
                    this.mImportance = proto.readInt(1159641169930L);
                    break;
                case 11:
                    this.mPss = proto.readLong(1112396529675L);
                    break;
                case 12:
                    this.mRss = proto.readLong(1112396529676L);
                    break;
                case 13:
                    this.mTimestamp = proto.readLong(1112396529677L);
                    break;
                case 14:
                    this.mDescription = intern(proto.readString(1138166333454L));
                    break;
                case 15:
                    this.mState = proto.readBytes(ApplicationExitInfoProto.STATE);
                    break;
                case 16:
                    String path = proto.readString(1138166333456L);
                    if (!TextUtils.isEmpty(path)) {
                        this.mTraceFile = new File(path);
                        break;
                    } else {
                        break;
                    }
            }
        }
        proto.end(token);
    }

    public boolean equals(Object other) {
        if (other == null || !(other instanceof ApplicationExitInfo)) {
            return false;
        }
        ApplicationExitInfo o = (ApplicationExitInfo) other;
        return this.mPid == o.mPid && this.mRealUid == o.mRealUid && this.mPackageUid == o.mPackageUid && this.mDefiningUid == o.mDefiningUid && this.mConnectionGroup == o.mConnectionGroup && this.mReason == o.mReason && this.mSubReason == o.mSubReason && this.mImportance == o.mImportance && this.mStatus == o.mStatus && this.mTimestamp == o.mTimestamp && this.mPss == o.mPss && this.mRss == o.mRss && TextUtils.equals(this.mProcessName, o.mProcessName) && TextUtils.equals(this.mDescription, o.mDescription);
    }

    public int hashCode() {
        int result = this.mPid;
        return (((((((((((((((((((((((((result * 31) + this.mRealUid) * 31) + this.mPackageUid) * 31) + this.mDefiningUid) * 31) + this.mConnectionGroup) * 31) + this.mReason) * 31) + this.mSubReason) * 31) + this.mImportance) * 31) + this.mStatus) * 31) + ((int) this.mPss)) * 31) + ((int) this.mRss)) * 31) + Long.hashCode(this.mTimestamp)) * 31) + Objects.hashCode(this.mProcessName)) * 31) + Objects.hashCode(this.mDescription);
    }
}
