package android.app.job;

import android.compat.Compatibility;
import android.content.Intent;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.PersistableBundle;
/* loaded from: classes.dex */
public final class JobWorkItem implements Parcelable {
    public static final Parcelable.Creator<JobWorkItem> CREATOR = new Parcelable.Creator<JobWorkItem>() { // from class: android.app.job.JobWorkItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public JobWorkItem createFromParcel(Parcel in) {
            return new JobWorkItem(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public JobWorkItem[] newArray(int size) {
            return new JobWorkItem[size];
        }
    };
    int mDeliveryCount;
    private final PersistableBundle mExtras;
    Object mGrants;
    final Intent mIntent;
    private final long mMinimumChunkBytes;
    private final long mNetworkDownloadBytes;
    private final long mNetworkUploadBytes;
    int mWorkId;

    public JobWorkItem(Intent intent) {
        this(intent, -1L, -1L);
    }

    public JobWorkItem(Intent intent, long downloadBytes, long uploadBytes) {
        this(intent, downloadBytes, uploadBytes, -1L);
    }

    public JobWorkItem(Intent intent, long downloadBytes, long uploadBytes, long minimumChunkBytes) {
        this.mExtras = PersistableBundle.EMPTY;
        this.mIntent = intent;
        this.mNetworkDownloadBytes = downloadBytes;
        this.mNetworkUploadBytes = uploadBytes;
        this.mMinimumChunkBytes = minimumChunkBytes;
        enforceValidity(Compatibility.isChangeEnabled((long) JobInfo.REJECT_NEGATIVE_NETWORK_ESTIMATES));
    }

    private JobWorkItem(Builder builder) {
        this.mDeliveryCount = builder.mDeliveryCount;
        this.mExtras = builder.mExtras.deepCopy();
        this.mIntent = builder.mIntent;
        this.mNetworkDownloadBytes = builder.mNetworkDownloadBytes;
        this.mNetworkUploadBytes = builder.mNetworkUploadBytes;
        this.mMinimumChunkBytes = builder.mMinimumNetworkChunkBytes;
    }

    public PersistableBundle getExtras() {
        return this.mExtras;
    }

    public Intent getIntent() {
        return this.mIntent;
    }

    public long getEstimatedNetworkDownloadBytes() {
        return this.mNetworkDownloadBytes;
    }

    public long getEstimatedNetworkUploadBytes() {
        return this.mNetworkUploadBytes;
    }

    public long getMinimumNetworkChunkBytes() {
        return this.mMinimumChunkBytes;
    }

    public int getDeliveryCount() {
        return this.mDeliveryCount;
    }

    public void bumpDeliveryCount() {
        this.mDeliveryCount++;
    }

    public void setWorkId(int id) {
        this.mWorkId = id;
    }

    public int getWorkId() {
        return this.mWorkId;
    }

    public void setGrants(Object grants) {
        this.mGrants = grants;
    }

    public Object getGrants() {
        return this.mGrants;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        sb.append("JobWorkItem{id=");
        sb.append(this.mWorkId);
        sb.append(" intent=");
        sb.append(this.mIntent);
        sb.append(" extras=");
        sb.append(this.mExtras);
        if (this.mNetworkDownloadBytes != -1) {
            sb.append(" downloadBytes=");
            sb.append(this.mNetworkDownloadBytes);
        }
        if (this.mNetworkUploadBytes != -1) {
            sb.append(" uploadBytes=");
            sb.append(this.mNetworkUploadBytes);
        }
        if (this.mMinimumChunkBytes != -1) {
            sb.append(" minimumChunkBytes=");
            sb.append(this.mMinimumChunkBytes);
        }
        if (this.mDeliveryCount != 0) {
            sb.append(" dcount=");
            sb.append(this.mDeliveryCount);
        }
        sb.append("}");
        return sb.toString();
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private int mDeliveryCount;
        private Intent mIntent;
        private PersistableBundle mExtras = PersistableBundle.EMPTY;
        private long mNetworkDownloadBytes = -1;
        private long mNetworkUploadBytes = -1;
        private long mMinimumNetworkChunkBytes = -1;

        public Builder setDeliveryCount(int deliveryCount) {
            this.mDeliveryCount = deliveryCount;
            return this;
        }

        public Builder setExtras(PersistableBundle extras) {
            if (extras == null) {
                throw new IllegalArgumentException("extras cannot be null");
            }
            this.mExtras = extras;
            return this;
        }

        public Builder setIntent(Intent intent) {
            this.mIntent = intent;
            return this;
        }

        public Builder setEstimatedNetworkBytes(long downloadBytes, long uploadBytes) {
            if (downloadBytes != -1 && downloadBytes < 0) {
                throw new IllegalArgumentException("Invalid network download bytes: " + downloadBytes);
            }
            if (uploadBytes != -1 && uploadBytes < 0) {
                throw new IllegalArgumentException("Invalid network upload bytes: " + uploadBytes);
            }
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

        public JobWorkItem build() {
            return build(Compatibility.isChangeEnabled((long) JobInfo.REJECT_NEGATIVE_NETWORK_ESTIMATES));
        }

        public JobWorkItem build(boolean rejectNegativeNetworkEstimates) {
            JobWorkItem jobWorkItem = new JobWorkItem(this);
            jobWorkItem.enforceValidity(rejectNegativeNetworkEstimates);
            return jobWorkItem;
        }
    }

    public void enforceValidity(boolean rejectNegativeNetworkEstimates) {
        long estimatedTransfer;
        if (rejectNegativeNetworkEstimates) {
            long j = this.mNetworkUploadBytes;
            if (j != -1 && j < 0) {
                throw new IllegalArgumentException("Invalid network upload bytes: " + this.mNetworkUploadBytes);
            }
            long j2 = this.mNetworkDownloadBytes;
            if (j2 != -1 && j2 < 0) {
                throw new IllegalArgumentException("Invalid network download bytes: " + this.mNetworkDownloadBytes);
            }
        }
        long j3 = this.mNetworkUploadBytes;
        if (j3 == -1) {
            estimatedTransfer = this.mNetworkDownloadBytes;
        } else {
            long j4 = this.mNetworkDownloadBytes;
            if (j4 == -1) {
                j4 = 0;
            }
            estimatedTransfer = j3 + j4;
        }
        long j5 = this.mMinimumChunkBytes;
        if (j5 != -1 && estimatedTransfer != -1 && j5 > estimatedTransfer) {
            throw new IllegalArgumentException("Minimum chunk size can't be greater than estimated network usage");
        }
        if (j5 != -1 && j5 <= 0) {
            throw new IllegalArgumentException("Minimum chunk size must be positive");
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        if (this.mIntent != null) {
            out.writeInt(1);
            this.mIntent.writeToParcel(out, 0);
        } else {
            out.writeInt(0);
        }
        out.writePersistableBundle(this.mExtras);
        out.writeLong(this.mNetworkDownloadBytes);
        out.writeLong(this.mNetworkUploadBytes);
        out.writeLong(this.mMinimumChunkBytes);
        out.writeInt(this.mDeliveryCount);
        out.writeInt(this.mWorkId);
    }

    JobWorkItem(Parcel in) {
        if (in.readInt() != 0) {
            this.mIntent = Intent.CREATOR.createFromParcel(in);
        } else {
            this.mIntent = null;
        }
        PersistableBundle extras = in.readPersistableBundle();
        this.mExtras = extras != null ? extras : PersistableBundle.EMPTY;
        this.mNetworkDownloadBytes = in.readLong();
        this.mNetworkUploadBytes = in.readLong();
        this.mMinimumChunkBytes = in.readLong();
        this.mDeliveryCount = in.readInt();
        this.mWorkId = in.readInt();
    }
}
