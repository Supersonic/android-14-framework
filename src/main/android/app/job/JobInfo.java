package android.app.job;

import android.compat.Compatibility;
import android.content.ClipData;
import android.content.ComponentName;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.net.NetworkRequest;
import android.net.Uri;
import android.p008os.BaseBundle;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.PersistableBundle;
import android.util.Log;
import android.util.TimeUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes.dex */
public class JobInfo implements Parcelable {
    public static final int BACKOFF_POLICY_EXPONENTIAL = 1;
    public static final int BACKOFF_POLICY_LINEAR = 0;
    public static final int BIAS_ADJ_ALWAYS_RUNNING = -80;
    public static final int BIAS_ADJ_OFTEN_RUNNING = -40;
    public static final int BIAS_BOUND_FOREGROUND_SERVICE = 30;
    public static final int BIAS_DEFAULT = 0;
    public static final int BIAS_FOREGROUND_SERVICE = 35;
    public static final int BIAS_SYNC_EXPEDITED = 10;
    public static final int BIAS_SYNC_INITIALIZATION = 20;
    public static final int BIAS_TOP_APP = 40;
    public static final int CONSTRAINT_FLAG_BATTERY_NOT_LOW = 2;
    public static final int CONSTRAINT_FLAG_CHARGING = 1;
    public static final int CONSTRAINT_FLAG_DEVICE_IDLE = 4;
    public static final int CONSTRAINT_FLAG_STORAGE_NOT_LOW = 8;
    public static final int DEFAULT_BACKOFF_POLICY = 1;
    public static final long DEFAULT_INITIAL_BACKOFF_MILLIS = 30000;
    public static final long DISALLOW_DEADLINES_FOR_PREFETCH_JOBS = 194532703;
    public static final int FLAG_EXEMPT_FROM_APP_STANDBY = 8;
    public static final int FLAG_EXPEDITED = 16;
    public static final int FLAG_IMPORTANT_WHILE_FOREGROUND = 2;
    public static final int FLAG_PREFETCH = 4;
    public static final int FLAG_USER_INITIATED = 32;
    public static final int FLAG_WILL_BE_FOREGROUND = 1;
    public static final long MAX_BACKOFF_DELAY_MILLIS = 18000000;
    public static final long MIN_BACKOFF_MILLIS = 10000;
    private static final long MIN_FLEX_MILLIS = 300000;
    private static final long MIN_PERIOD_MILLIS = 900000;
    public static final int NETWORK_BYTES_UNKNOWN = -1;
    public static final int NETWORK_TYPE_ANY = 1;
    public static final int NETWORK_TYPE_CELLULAR = 4;
    @Deprecated
    public static final int NETWORK_TYPE_METERED = 4;
    public static final int NETWORK_TYPE_NONE = 0;
    public static final int NETWORK_TYPE_NOT_ROAMING = 3;
    public static final int NETWORK_TYPE_UNMETERED = 2;
    public static final int PRIORITY_DEFAULT = 300;
    public static final int PRIORITY_FOREGROUND_APP = 30;
    public static final int PRIORITY_FOREGROUND_SERVICE = 35;
    public static final int PRIORITY_HIGH = 400;
    public static final int PRIORITY_LOW = 200;
    public static final int PRIORITY_MAX = 500;
    public static final int PRIORITY_MIN = 100;
    public static final long REJECT_NEGATIVE_NETWORK_ESTIMATES = 253665015;
    public static final long THROW_ON_INVALID_PRIORITY_VALUE = 140852299;
    private final int backoffPolicy;
    private final ClipData clipData;
    private final int clipGrantFlags;
    private final int constraintFlags;
    private final PersistableBundle extras;
    private final int flags;
    private final long flexMillis;
    private final boolean hasEarlyConstraint;
    private final boolean hasLateConstraint;
    private final long initialBackoffMillis;
    private final long intervalMillis;
    private final boolean isPeriodic;
    private final boolean isPersisted;
    private final int jobId;
    private final int mBias;
    private final int mPriority;
    private final long maxExecutionDelayMillis;
    private final long minLatencyMillis;
    private final long minimumNetworkChunkBytes;
    private final long networkDownloadBytes;
    private final NetworkRequest networkRequest;
    private final long networkUploadBytes;
    private final ComponentName service;
    private final Bundle transientExtras;
    private final long triggerContentMaxDelay;
    private final long triggerContentUpdateDelay;
    private final TriggerContentUri[] triggerContentUris;
    private static String TAG = "JobInfo";
    public static final Parcelable.Creator<JobInfo> CREATOR = new Parcelable.Creator<JobInfo>() { // from class: android.app.job.JobInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public JobInfo createFromParcel(Parcel in) {
            return new JobInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public JobInfo[] newArray(int size) {
            return new JobInfo[size];
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface BackoffPolicy {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface NetworkType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Priority {
    }

    public static final long getMinPeriodMillis() {
        return 900000L;
    }

    public static final long getMinFlexMillis() {
        return 300000L;
    }

    public static final long getMinBackoffMillis() {
        return MIN_BACKOFF_MILLIS;
    }

    public int getId() {
        return this.jobId;
    }

    public PersistableBundle getExtras() {
        return this.extras;
    }

    public Bundle getTransientExtras() {
        return this.transientExtras;
    }

    public ClipData getClipData() {
        return this.clipData;
    }

    public int getClipGrantFlags() {
        return this.clipGrantFlags;
    }

    public ComponentName getService() {
        return this.service;
    }

    public int getBias() {
        return this.mBias;
    }

    public int getPriority() {
        return this.mPriority;
    }

    public int getFlags() {
        return this.flags;
    }

    public boolean isExemptedFromAppStandby() {
        return ((this.flags & 8) == 0 || isPeriodic()) ? false : true;
    }

    public boolean isRequireCharging() {
        return (this.constraintFlags & 1) != 0;
    }

    public boolean isRequireBatteryNotLow() {
        return (this.constraintFlags & 2) != 0;
    }

    public boolean isRequireDeviceIdle() {
        return (this.constraintFlags & 4) != 0;
    }

    public boolean isRequireStorageNotLow() {
        return (this.constraintFlags & 8) != 0;
    }

    public int getConstraintFlags() {
        return this.constraintFlags;
    }

    public TriggerContentUri[] getTriggerContentUris() {
        return this.triggerContentUris;
    }

    public long getTriggerContentUpdateDelay() {
        return this.triggerContentUpdateDelay;
    }

    public long getTriggerContentMaxDelay() {
        return this.triggerContentMaxDelay;
    }

    @Deprecated
    public int getNetworkType() {
        NetworkRequest networkRequest = this.networkRequest;
        if (networkRequest == null) {
            return 0;
        }
        if (networkRequest.hasCapability(11)) {
            return 2;
        }
        if (this.networkRequest.hasCapability(18)) {
            return 3;
        }
        if (this.networkRequest.hasTransport(0)) {
            return 4;
        }
        return 1;
    }

    public NetworkRequest getRequiredNetwork() {
        return this.networkRequest;
    }

    public long getEstimatedNetworkDownloadBytes() {
        return this.networkDownloadBytes;
    }

    public long getEstimatedNetworkUploadBytes() {
        return this.networkUploadBytes;
    }

    public long getMinimumNetworkChunkBytes() {
        return this.minimumNetworkChunkBytes;
    }

    public long getMinLatencyMillis() {
        return this.minLatencyMillis;
    }

    public long getMaxExecutionDelayMillis() {
        return this.maxExecutionDelayMillis;
    }

    public boolean isPeriodic() {
        return this.isPeriodic;
    }

    public boolean isPersisted() {
        return this.isPersisted;
    }

    public long getIntervalMillis() {
        return this.intervalMillis;
    }

    public long getFlexMillis() {
        return this.flexMillis;
    }

    public long getInitialBackoffMillis() {
        return this.initialBackoffMillis;
    }

    public int getBackoffPolicy() {
        return this.backoffPolicy;
    }

    public boolean isExpedited() {
        return (this.flags & 16) != 0;
    }

    public boolean isUserInitiated() {
        return (this.flags & 32) != 0;
    }

    public boolean isImportantWhileForeground() {
        return (this.flags & 2) != 0;
    }

    public boolean isPrefetch() {
        return (this.flags & 4) != 0;
    }

    public boolean hasEarlyConstraint() {
        return this.hasEarlyConstraint;
    }

    public boolean hasLateConstraint() {
        return this.hasLateConstraint;
    }

    public boolean equals(Object o) {
        if (o instanceof JobInfo) {
            JobInfo j = (JobInfo) o;
            return this.jobId == j.jobId && BaseBundle.kindofEquals(this.extras, j.extras) && BaseBundle.kindofEquals(this.transientExtras, j.transientExtras) && this.clipData == j.clipData && this.clipGrantFlags == j.clipGrantFlags && Objects.equals(this.service, j.service) && this.constraintFlags == j.constraintFlags && Arrays.equals(this.triggerContentUris, j.triggerContentUris) && this.triggerContentUpdateDelay == j.triggerContentUpdateDelay && this.triggerContentMaxDelay == j.triggerContentMaxDelay && this.hasEarlyConstraint == j.hasEarlyConstraint && this.hasLateConstraint == j.hasLateConstraint && Objects.equals(this.networkRequest, j.networkRequest) && this.networkDownloadBytes == j.networkDownloadBytes && this.networkUploadBytes == j.networkUploadBytes && this.minimumNetworkChunkBytes == j.minimumNetworkChunkBytes && this.minLatencyMillis == j.minLatencyMillis && this.maxExecutionDelayMillis == j.maxExecutionDelayMillis && this.isPeriodic == j.isPeriodic && this.isPersisted == j.isPersisted && this.intervalMillis == j.intervalMillis && this.flexMillis == j.flexMillis && this.initialBackoffMillis == j.initialBackoffMillis && this.backoffPolicy == j.backoffPolicy && this.mBias == j.mBias && this.mPriority == j.mPriority && this.flags == j.flags;
        }
        return false;
    }

    public int hashCode() {
        int hashCode = this.jobId;
        PersistableBundle persistableBundle = this.extras;
        if (persistableBundle != null) {
            hashCode = (hashCode * 31) + persistableBundle.hashCode();
        }
        Bundle bundle = this.transientExtras;
        if (bundle != null) {
            hashCode = (hashCode * 31) + bundle.hashCode();
        }
        ClipData clipData = this.clipData;
        if (clipData != null) {
            hashCode = (hashCode * 31) + clipData.hashCode();
        }
        int hashCode2 = (hashCode * 31) + this.clipGrantFlags;
        ComponentName componentName = this.service;
        if (componentName != null) {
            hashCode2 = (hashCode2 * 31) + componentName.hashCode();
        }
        int hashCode3 = (hashCode2 * 31) + this.constraintFlags;
        TriggerContentUri[] triggerContentUriArr = this.triggerContentUris;
        if (triggerContentUriArr != null) {
            hashCode3 = (hashCode3 * 31) + Arrays.hashCode(triggerContentUriArr);
        }
        int hashCode4 = (((((((hashCode3 * 31) + Long.hashCode(this.triggerContentUpdateDelay)) * 31) + Long.hashCode(this.triggerContentMaxDelay)) * 31) + Boolean.hashCode(this.hasEarlyConstraint)) * 31) + Boolean.hashCode(this.hasLateConstraint);
        NetworkRequest networkRequest = this.networkRequest;
        if (networkRequest != null) {
            hashCode4 = (hashCode4 * 31) + networkRequest.hashCode();
        }
        return (((((((((((((((((((((((((((hashCode4 * 31) + Long.hashCode(this.networkDownloadBytes)) * 31) + Long.hashCode(this.networkUploadBytes)) * 31) + Long.hashCode(this.minimumNetworkChunkBytes)) * 31) + Long.hashCode(this.minLatencyMillis)) * 31) + Long.hashCode(this.maxExecutionDelayMillis)) * 31) + Boolean.hashCode(this.isPeriodic)) * 31) + Boolean.hashCode(this.isPersisted)) * 31) + Long.hashCode(this.intervalMillis)) * 31) + Long.hashCode(this.flexMillis)) * 31) + Long.hashCode(this.initialBackoffMillis)) * 31) + this.backoffPolicy) * 31) + this.mBias) * 31) + this.mPriority) * 31) + this.flags;
    }

    private JobInfo(Parcel in) {
        this.jobId = in.readInt();
        PersistableBundle persistableExtras = in.readPersistableBundle();
        this.extras = persistableExtras != null ? persistableExtras : PersistableBundle.EMPTY;
        this.transientExtras = in.readBundle();
        if (in.readInt() != 0) {
            this.clipData = ClipData.CREATOR.createFromParcel(in);
            this.clipGrantFlags = in.readInt();
        } else {
            this.clipData = null;
            this.clipGrantFlags = 0;
        }
        this.service = (ComponentName) in.readParcelable(null);
        this.constraintFlags = in.readInt();
        this.triggerContentUris = (TriggerContentUri[]) in.createTypedArray(TriggerContentUri.CREATOR);
        this.triggerContentUpdateDelay = in.readLong();
        this.triggerContentMaxDelay = in.readLong();
        if (in.readInt() != 0) {
            this.networkRequest = (NetworkRequest) NetworkRequest.CREATOR.createFromParcel(in);
        } else {
            this.networkRequest = null;
        }
        this.networkDownloadBytes = in.readLong();
        this.networkUploadBytes = in.readLong();
        this.minimumNetworkChunkBytes = in.readLong();
        this.minLatencyMillis = in.readLong();
        this.maxExecutionDelayMillis = in.readLong();
        this.isPeriodic = in.readInt() == 1;
        this.isPersisted = in.readInt() == 1;
        this.intervalMillis = in.readLong();
        this.flexMillis = in.readLong();
        this.initialBackoffMillis = in.readLong();
        this.backoffPolicy = in.readInt();
        this.hasEarlyConstraint = in.readInt() == 1;
        this.hasLateConstraint = in.readInt() == 1;
        this.mBias = in.readInt();
        this.mPriority = in.readInt();
        this.flags = in.readInt();
    }

    private JobInfo(Builder b) {
        TriggerContentUri[] triggerContentUriArr;
        this.jobId = b.mJobId;
        this.extras = b.mExtras.deepCopy();
        this.transientExtras = b.mTransientExtras.deepCopy();
        this.clipData = b.mClipData;
        this.clipGrantFlags = b.mClipGrantFlags;
        this.service = b.mJobService;
        this.constraintFlags = b.mConstraintFlags;
        if (b.mTriggerContentUris != null) {
            triggerContentUriArr = (TriggerContentUri[]) b.mTriggerContentUris.toArray(new TriggerContentUri[b.mTriggerContentUris.size()]);
        } else {
            triggerContentUriArr = null;
        }
        this.triggerContentUris = triggerContentUriArr;
        this.triggerContentUpdateDelay = b.mTriggerContentUpdateDelay;
        this.triggerContentMaxDelay = b.mTriggerContentMaxDelay;
        this.networkRequest = b.mNetworkRequest;
        this.networkDownloadBytes = b.mNetworkDownloadBytes;
        this.networkUploadBytes = b.mNetworkUploadBytes;
        this.minimumNetworkChunkBytes = b.mMinimumNetworkChunkBytes;
        this.minLatencyMillis = b.mMinLatencyMillis;
        this.maxExecutionDelayMillis = b.mMaxExecutionDelayMillis;
        this.isPeriodic = b.mIsPeriodic;
        this.isPersisted = b.mIsPersisted;
        this.intervalMillis = b.mIntervalMillis;
        this.flexMillis = b.mFlexMillis;
        this.initialBackoffMillis = b.mInitialBackoffMillis;
        this.backoffPolicy = b.mBackoffPolicy;
        this.hasEarlyConstraint = b.mHasEarlyConstraint;
        this.hasLateConstraint = b.mHasLateConstraint;
        this.mBias = b.mBias;
        this.mPriority = b.mPriority;
        this.flags = b.mFlags;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.jobId);
        out.writePersistableBundle(this.extras);
        out.writeBundle(this.transientExtras);
        if (this.clipData != null) {
            out.writeInt(1);
            this.clipData.writeToParcel(out, flags);
            out.writeInt(this.clipGrantFlags);
        } else {
            out.writeInt(0);
        }
        out.writeParcelable(this.service, flags);
        out.writeInt(this.constraintFlags);
        out.writeTypedArray(this.triggerContentUris, flags);
        out.writeLong(this.triggerContentUpdateDelay);
        out.writeLong(this.triggerContentMaxDelay);
        if (this.networkRequest != null) {
            out.writeInt(1);
            this.networkRequest.writeToParcel(out, flags);
        } else {
            out.writeInt(0);
        }
        out.writeLong(this.networkDownloadBytes);
        out.writeLong(this.networkUploadBytes);
        out.writeLong(this.minimumNetworkChunkBytes);
        out.writeLong(this.minLatencyMillis);
        out.writeLong(this.maxExecutionDelayMillis);
        out.writeInt(this.isPeriodic ? 1 : 0);
        out.writeInt(this.isPersisted ? 1 : 0);
        out.writeLong(this.intervalMillis);
        out.writeLong(this.flexMillis);
        out.writeLong(this.initialBackoffMillis);
        out.writeInt(this.backoffPolicy);
        out.writeInt(this.hasEarlyConstraint ? 1 : 0);
        out.writeInt(this.hasLateConstraint ? 1 : 0);
        out.writeInt(this.mBias);
        out.writeInt(this.mPriority);
        out.writeInt(this.flags);
    }

    public String toString() {
        return "(job:" + this.jobId + "/" + this.service.flattenToShortString() + NavigationBarInflaterView.KEY_CODE_END;
    }

    /* loaded from: classes.dex */
    public static final class TriggerContentUri implements Parcelable {
        public static final Parcelable.Creator<TriggerContentUri> CREATOR = new Parcelable.Creator<TriggerContentUri>() { // from class: android.app.job.JobInfo.TriggerContentUri.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public TriggerContentUri createFromParcel(Parcel in) {
                return new TriggerContentUri(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public TriggerContentUri[] newArray(int size) {
                return new TriggerContentUri[size];
            }
        };
        public static final int FLAG_NOTIFY_FOR_DESCENDANTS = 1;
        private final int mFlags;
        private final Uri mUri;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        public @interface Flags {
        }

        public TriggerContentUri(Uri uri, int flags) {
            this.mUri = (Uri) Objects.requireNonNull(uri);
            this.mFlags = flags;
        }

        public Uri getUri() {
            return this.mUri;
        }

        public int getFlags() {
            return this.mFlags;
        }

        public boolean equals(Object o) {
            if (o instanceof TriggerContentUri) {
                TriggerContentUri t = (TriggerContentUri) o;
                return Objects.equals(t.mUri, this.mUri) && t.mFlags == this.mFlags;
            }
            return false;
        }

        public int hashCode() {
            Uri uri = this.mUri;
            return (uri == null ? 0 : uri.hashCode()) ^ this.mFlags;
        }

        private TriggerContentUri(Parcel in) {
            this.mUri = Uri.CREATOR.createFromParcel(in);
            this.mFlags = in.readInt();
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            this.mUri.writeToParcel(out, flags);
            out.writeInt(this.mFlags);
        }
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private int mBackoffPolicy;
        private boolean mBackoffPolicySet;
        private int mBias;
        private ClipData mClipData;
        private int mClipGrantFlags;
        private int mConstraintFlags;
        private PersistableBundle mExtras;
        private int mFlags;
        private long mFlexMillis;
        private boolean mHasEarlyConstraint;
        private boolean mHasLateConstraint;
        private long mInitialBackoffMillis;
        private long mIntervalMillis;
        private boolean mIsPeriodic;
        private boolean mIsPersisted;
        private final int mJobId;
        private final ComponentName mJobService;
        private long mMaxExecutionDelayMillis;
        private long mMinLatencyMillis;
        private long mMinimumNetworkChunkBytes;
        private long mNetworkDownloadBytes;
        private NetworkRequest mNetworkRequest;
        private long mNetworkUploadBytes;
        private int mPriority;
        private Bundle mTransientExtras;
        private long mTriggerContentMaxDelay;
        private long mTriggerContentUpdateDelay;
        private ArrayList<TriggerContentUri> mTriggerContentUris;

        public Builder(int jobId, ComponentName jobService) {
            this.mExtras = PersistableBundle.EMPTY;
            this.mTransientExtras = Bundle.EMPTY;
            this.mBias = 0;
            this.mPriority = 300;
            this.mNetworkDownloadBytes = -1L;
            this.mNetworkUploadBytes = -1L;
            this.mMinimumNetworkChunkBytes = -1L;
            this.mTriggerContentUpdateDelay = -1L;
            this.mTriggerContentMaxDelay = -1L;
            this.mInitialBackoffMillis = JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS;
            this.mBackoffPolicy = 1;
            this.mBackoffPolicySet = false;
            this.mJobService = jobService;
            this.mJobId = jobId;
        }

        public Builder(JobInfo job) {
            this.mExtras = PersistableBundle.EMPTY;
            this.mTransientExtras = Bundle.EMPTY;
            this.mBias = 0;
            this.mPriority = 300;
            this.mNetworkDownloadBytes = -1L;
            this.mNetworkUploadBytes = -1L;
            this.mMinimumNetworkChunkBytes = -1L;
            this.mTriggerContentUpdateDelay = -1L;
            this.mTriggerContentMaxDelay = -1L;
            this.mInitialBackoffMillis = JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS;
            this.mBackoffPolicy = 1;
            this.mBackoffPolicySet = false;
            this.mJobId = job.getId();
            this.mJobService = job.getService();
            this.mExtras = job.getExtras();
            this.mTransientExtras = job.getTransientExtras();
            this.mClipData = job.getClipData();
            this.mClipGrantFlags = job.getClipGrantFlags();
            this.mBias = job.getBias();
            this.mFlags = job.getFlags();
            this.mConstraintFlags = job.getConstraintFlags();
            this.mNetworkRequest = job.getRequiredNetwork();
            this.mNetworkDownloadBytes = job.getEstimatedNetworkDownloadBytes();
            this.mNetworkUploadBytes = job.getEstimatedNetworkUploadBytes();
            this.mMinimumNetworkChunkBytes = job.getMinimumNetworkChunkBytes();
            this.mTriggerContentUris = job.getTriggerContentUris() != null ? new ArrayList<>(Arrays.asList(job.getTriggerContentUris())) : null;
            this.mTriggerContentUpdateDelay = job.getTriggerContentUpdateDelay();
            this.mTriggerContentMaxDelay = job.getTriggerContentMaxDelay();
            this.mIsPersisted = job.isPersisted();
            this.mMinLatencyMillis = job.getMinLatencyMillis();
            this.mMaxExecutionDelayMillis = job.getMaxExecutionDelayMillis();
            this.mIsPeriodic = job.isPeriodic();
            this.mHasEarlyConstraint = job.hasEarlyConstraint();
            this.mHasLateConstraint = job.hasLateConstraint();
            this.mIntervalMillis = job.getIntervalMillis();
            this.mFlexMillis = job.getFlexMillis();
            this.mInitialBackoffMillis = job.getInitialBackoffMillis();
            this.mBackoffPolicy = job.getBackoffPolicy();
            this.mPriority = job.getPriority();
        }

        public Builder setBias(int bias) {
            this.mBias = bias;
            return this;
        }

        public Builder setPriority(int priority) {
            if (priority > 500 || priority < 100) {
                if (Compatibility.isChangeEnabled((long) JobInfo.THROW_ON_INVALID_PRIORITY_VALUE)) {
                    throw new IllegalArgumentException("Invalid priority value");
                }
                return this;
            }
            this.mPriority = priority;
            return this;
        }

        public Builder setFlags(int flags) {
            this.mFlags = flags;
            return this;
        }

        public Builder setExtras(PersistableBundle extras) {
            this.mExtras = extras;
            return this;
        }

        public Builder setTransientExtras(Bundle extras) {
            this.mTransientExtras = extras;
            return this;
        }

        public Builder setClipData(ClipData clip, int grantFlags) {
            this.mClipData = clip;
            this.mClipGrantFlags = grantFlags;
            return this;
        }

        public Builder setRequiredNetworkType(int networkType) {
            if (networkType == 0) {
                return setRequiredNetwork(null);
            }
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            builder.addCapability(12);
            builder.addCapability(16);
            builder.removeCapability(15);
            builder.removeCapability(13);
            if (networkType != 1) {
                if (networkType == 2) {
                    builder.addCapability(11);
                } else if (networkType == 3) {
                    builder.addCapability(18);
                } else if (networkType == 4) {
                    builder.addTransportType(0);
                }
            }
            return setRequiredNetwork(builder.build());
        }

        public Builder setRequiredNetwork(NetworkRequest networkRequest) {
            this.mNetworkRequest = networkRequest;
            return this;
        }

        public Builder setEstimatedNetworkBytes(long downloadBytes, long uploadBytes) {
            this.mNetworkDownloadBytes = downloadBytes;
            this.mNetworkUploadBytes = uploadBytes;
            return this;
        }

        public Builder setMinimumNetworkChunkBytes(long chunkSizeBytes) {
            if (chunkSizeBytes != -1 && chunkSizeBytes <= 0) {
                throw new IllegalArgumentException("Minimum chunk size must be positive");
            }
            this.mMinimumNetworkChunkBytes = chunkSizeBytes;
            return this;
        }

        public Builder setRequiresCharging(boolean requiresCharging) {
            this.mConstraintFlags = (this.mConstraintFlags & (-2)) | (requiresCharging ? 1 : 0);
            return this;
        }

        public Builder setRequiresBatteryNotLow(boolean batteryNotLow) {
            this.mConstraintFlags = (this.mConstraintFlags & (-3)) | (batteryNotLow ? 2 : 0);
            return this;
        }

        public Builder setRequiresDeviceIdle(boolean requiresDeviceIdle) {
            this.mConstraintFlags = (this.mConstraintFlags & (-5)) | (requiresDeviceIdle ? 4 : 0);
            return this;
        }

        public Builder setRequiresStorageNotLow(boolean storageNotLow) {
            this.mConstraintFlags = (this.mConstraintFlags & (-9)) | (storageNotLow ? 8 : 0);
            return this;
        }

        public Builder addTriggerContentUri(TriggerContentUri uri) {
            if (this.mTriggerContentUris == null) {
                this.mTriggerContentUris = new ArrayList<>();
            }
            this.mTriggerContentUris.add(uri);
            return this;
        }

        public Builder setTriggerContentUpdateDelay(long durationMs) {
            this.mTriggerContentUpdateDelay = durationMs;
            return this;
        }

        public Builder setTriggerContentMaxDelay(long durationMs) {
            this.mTriggerContentMaxDelay = durationMs;
            return this;
        }

        public Builder setPeriodic(long intervalMillis) {
            return setPeriodic(intervalMillis, intervalMillis);
        }

        public Builder setPeriodic(long intervalMillis, long flexMillis) {
            long minPeriod = JobInfo.getMinPeriodMillis();
            if (intervalMillis < minPeriod) {
                Log.m104w(JobInfo.TAG, "Requested interval " + TimeUtils.formatDuration(intervalMillis) + " for job " + this.mJobId + " is too small; raising to " + TimeUtils.formatDuration(minPeriod));
                intervalMillis = minPeriod;
            }
            long percentClamp = (5 * intervalMillis) / 100;
            long minFlex = Math.max(percentClamp, JobInfo.getMinFlexMillis());
            if (flexMillis < minFlex) {
                Log.m104w(JobInfo.TAG, "Requested flex " + TimeUtils.formatDuration(flexMillis) + " for job " + this.mJobId + " is too small; raising to " + TimeUtils.formatDuration(minFlex));
                flexMillis = minFlex;
            }
            this.mIsPeriodic = true;
            this.mIntervalMillis = intervalMillis;
            this.mFlexMillis = flexMillis;
            this.mHasLateConstraint = true;
            this.mHasEarlyConstraint = true;
            return this;
        }

        public Builder setMinimumLatency(long minLatencyMillis) {
            this.mMinLatencyMillis = minLatencyMillis;
            this.mHasEarlyConstraint = true;
            return this;
        }

        public Builder setOverrideDeadline(long maxExecutionDelayMillis) {
            this.mMaxExecutionDelayMillis = maxExecutionDelayMillis;
            this.mHasLateConstraint = true;
            return this;
        }

        public Builder setBackoffCriteria(long initialBackoffMillis, int backoffPolicy) {
            long minBackoff = JobInfo.getMinBackoffMillis();
            if (initialBackoffMillis < minBackoff) {
                Log.m104w(JobInfo.TAG, "Requested backoff " + TimeUtils.formatDuration(initialBackoffMillis) + " for job " + this.mJobId + " is too small; raising to " + TimeUtils.formatDuration(minBackoff));
                initialBackoffMillis = minBackoff;
            }
            this.mBackoffPolicySet = true;
            this.mInitialBackoffMillis = initialBackoffMillis;
            this.mBackoffPolicy = backoffPolicy;
            return this;
        }

        public Builder setExpedited(boolean expedited) {
            if (expedited) {
                this.mFlags |= 16;
                if (this.mPriority == 300) {
                    this.mPriority = 500;
                }
            } else {
                if (this.mPriority == 500 && (this.mFlags & 16) != 0) {
                    this.mPriority = 300;
                }
                this.mFlags &= -17;
            }
            return this;
        }

        public Builder setUserInitiated(boolean userInitiated) {
            if (userInitiated) {
                this.mFlags |= 32;
                if (this.mPriority == 300) {
                    this.mPriority = 500;
                }
            } else {
                if (this.mPriority == 500 && (this.mFlags & 32) != 0) {
                    this.mPriority = 300;
                }
                this.mFlags &= -33;
            }
            return this;
        }

        @Deprecated
        public Builder setImportantWhileForeground(boolean importantWhileForeground) {
            if (importantWhileForeground) {
                this.mFlags |= 2;
                if (this.mPriority == 300) {
                    this.mPriority = 400;
                }
            } else {
                if (this.mPriority == 400 && (this.mFlags & 2) != 0) {
                    this.mPriority = 300;
                }
                this.mFlags &= -3;
            }
            return this;
        }

        public Builder setPrefetch(boolean prefetch) {
            if (prefetch) {
                this.mFlags |= 4;
            } else {
                this.mFlags &= -5;
            }
            return this;
        }

        public Builder setPersisted(boolean isPersisted) {
            this.mIsPersisted = isPersisted;
            return this;
        }

        public JobInfo build() {
            return build(Compatibility.isChangeEnabled((long) JobInfo.DISALLOW_DEADLINES_FOR_PREFETCH_JOBS), Compatibility.isChangeEnabled((long) JobInfo.REJECT_NEGATIVE_NETWORK_ESTIMATES));
        }

        public JobInfo build(boolean disallowPrefetchDeadlines, boolean rejectNegativeNetworkEstimates) {
            if (this.mBackoffPolicySet && (this.mConstraintFlags & 4) != 0) {
                throw new IllegalArgumentException("An idle mode job will not respect any back-off policy, so calling setBackoffCriteria with setRequiresDeviceIdle is an error.");
            }
            JobInfo jobInfo = new JobInfo(this);
            jobInfo.enforceValidity(disallowPrefetchDeadlines, rejectNegativeNetworkEstimates);
            return jobInfo;
        }

        public String summarize() {
            String service;
            ComponentName componentName = this.mJobService;
            if (componentName != null) {
                service = componentName.flattenToShortString();
            } else {
                service = "null";
            }
            return "JobInfo.Builder{job:" + this.mJobId + "/" + service + "}";
        }
    }

    public final void enforceValidity(boolean disallowPrefetchDeadlines, boolean rejectNegativeNetworkEstimates) {
        long estimatedTransfer;
        long j = this.networkDownloadBytes;
        if ((j > 0 || this.networkUploadBytes > 0 || this.minimumNetworkChunkBytes > 0) && this.networkRequest == null) {
            throw new IllegalArgumentException("Can't provide estimated network usage without requiring a network");
        }
        NetworkRequest networkRequest = this.networkRequest;
        if (networkRequest != null && rejectNegativeNetworkEstimates) {
            long j2 = this.networkUploadBytes;
            if (j2 != -1 && j2 < 0) {
                throw new IllegalArgumentException("Invalid network upload bytes: " + this.networkUploadBytes);
            }
            if (j != -1 && j < 0) {
                throw new IllegalArgumentException("Invalid network download bytes: " + this.networkDownloadBytes);
            }
        }
        long j3 = this.networkUploadBytes;
        if (j3 == -1) {
            estimatedTransfer = this.networkDownloadBytes;
        } else {
            if (j == -1) {
                j = 0;
            }
            estimatedTransfer = j + j3;
        }
        long j4 = this.minimumNetworkChunkBytes;
        if (j4 != -1 && estimatedTransfer != -1 && j4 > estimatedTransfer) {
            throw new IllegalArgumentException("Minimum chunk size can't be greater than estimated network usage");
        }
        if (j4 != -1 && j4 <= 0) {
            throw new IllegalArgumentException("Minimum chunk size must be positive");
        }
        boolean hasDeadline = this.maxExecutionDelayMillis != 0;
        if (this.isPeriodic) {
            if (hasDeadline) {
                throw new IllegalArgumentException("Can't call setOverrideDeadline() on a periodic job.");
            }
            if (this.minLatencyMillis != 0) {
                throw new IllegalArgumentException("Can't call setMinimumLatency() on a periodic job");
            }
            if (this.triggerContentUris != null) {
                throw new IllegalArgumentException("Can't call addTriggerContentUri() on a periodic job");
            }
        }
        if (disallowPrefetchDeadlines && hasDeadline && (this.flags & 4) != 0) {
            throw new IllegalArgumentException("Can't call setOverrideDeadline() on a prefetch job.");
        }
        if (this.isPersisted) {
            if (networkRequest != null && networkRequest.getNetworkSpecifier() != null) {
                throw new IllegalArgumentException("Network specifiers aren't supported for persistent jobs");
            }
            if (this.triggerContentUris != null) {
                throw new IllegalArgumentException("Can't call addTriggerContentUri() on a persisted job");
            }
            if (!this.transientExtras.isEmpty()) {
                throw new IllegalArgumentException("Can't call setTransientExtras() on a persisted job");
            }
            if (this.clipData != null) {
                throw new IllegalArgumentException("Can't call setClipData() on a persisted job");
            }
        }
        int i = this.flags;
        if ((i & 2) != 0) {
            if (this.hasEarlyConstraint) {
                throw new IllegalArgumentException("An important while foreground job cannot have a time delay");
            }
            int i2 = this.mPriority;
            if (i2 != 400 && i2 != 300) {
                throw new IllegalArgumentException("An important while foreground job must be high or default priority. Don't mark unimportant tasks as important while foreground.");
            }
        }
        boolean isExpedited = (i & 16) != 0;
        boolean isUserInitiated = (i & 32) != 0;
        int i3 = this.mPriority;
        switch (i3) {
            case 100:
            case 200:
            case 300:
                break;
            case 400:
                if ((i & 4) != 0) {
                    throw new IllegalArgumentException("Prefetch jobs cannot be high priority");
                }
                if (this.isPeriodic) {
                    throw new IllegalArgumentException("Periodic jobs cannot be high priority");
                }
                break;
            case 500:
                if (!isExpedited && !isUserInitiated) {
                    throw new IllegalArgumentException("Only expedited or user-initiated jobs can have max priority");
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid priority level provided: " + this.mPriority);
        }
        if (isExpedited) {
            if (this.hasEarlyConstraint) {
                throw new IllegalArgumentException("An expedited job cannot have a time delay");
            }
            if (this.hasLateConstraint) {
                throw new IllegalArgumentException("An expedited job cannot have a deadline");
            }
            if (this.isPeriodic) {
                throw new IllegalArgumentException("An expedited job cannot be periodic");
            }
            if (isUserInitiated) {
                throw new IllegalArgumentException("An expedited job cannot be user-initiated");
            }
            if (i3 != 500 && i3 != 400) {
                throw new IllegalArgumentException("An expedited job must be high or max priority. Don't use expedited jobs for unimportant tasks.");
            }
            if ((this.constraintFlags & (-9)) != 0 || (i & (-25)) != 0) {
                throw new IllegalArgumentException("An expedited job can only have network and storage-not-low constraints");
            }
            TriggerContentUri[] triggerContentUriArr = this.triggerContentUris;
            if (triggerContentUriArr != null && triggerContentUriArr.length > 0) {
                throw new IllegalArgumentException("Can't call addTriggerContentUri() on an expedited job");
            }
        }
        if (isUserInitiated) {
            if (this.hasEarlyConstraint) {
                throw new IllegalArgumentException("A user-initiated job cannot have a time delay");
            }
            if (this.hasLateConstraint) {
                throw new IllegalArgumentException("A user-initiated job cannot have a deadline");
            }
            if (this.isPeriodic) {
                throw new IllegalArgumentException("A user-initiated job cannot be periodic");
            }
            if ((i & 4) != 0) {
                throw new IllegalArgumentException("A user-initiated job cannot also be a prefetch job");
            }
            if (i3 != 500) {
                throw new IllegalArgumentException("A user-initiated job must be max priority.");
            }
            if ((this.constraintFlags & 4) != 0) {
                throw new IllegalArgumentException("A user-initiated job cannot have a device-idle constraint");
            }
            TriggerContentUri[] triggerContentUriArr2 = this.triggerContentUris;
            if (triggerContentUriArr2 != null && triggerContentUriArr2.length > 0) {
                throw new IllegalArgumentException("Can't call addTriggerContentUri() on a user-initiated job");
            }
            if (this.networkRequest == null) {
                throw new IllegalArgumentException("A user-initaited data transfer job must specify a valid network type");
            }
            if (this.backoffPolicy == 0) {
                throw new IllegalArgumentException("A user-initiated data transfer job cannot have a linear backoff policy.");
            }
        }
    }

    public static String getBiasString(int bias) {
        switch (bias) {
            case 0:
                return "0 [DEFAULT]";
            case 10:
                return "10 [SYNC_EXPEDITED]";
            case 20:
                return "20 [SYNC_INITIALIZATION]";
            case 30:
                return "30 [BFGS_APP]";
            case 35:
                return "35 [FGS_APP]";
            case 40:
                return "40 [TOP_APP]";
            default:
                return bias + " [UNKNOWN]";
        }
    }

    public static String getPriorityString(int priority) {
        switch (priority) {
            case 100:
                return priority + " [MIN]";
            case 200:
                return priority + " [LOW]";
            case 300:
                return priority + " [DEFAULT]";
            case 400:
                return priority + " [HIGH]";
            case 500:
                return priority + " [MAX]";
            default:
                return priority + " [UNKNOWN]";
        }
    }
}
