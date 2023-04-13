package android.hardware.location;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.MediaMetrics;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.Spanned;
@SystemApi
/* loaded from: classes2.dex */
public final class NanoAppRpcService implements Parcelable {
    public static final Parcelable.Creator<NanoAppRpcService> CREATOR = new Parcelable.Creator<NanoAppRpcService>() { // from class: android.hardware.location.NanoAppRpcService.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NanoAppRpcService createFromParcel(Parcel in) {
            return new NanoAppRpcService(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NanoAppRpcService[] newArray(int size) {
            return new NanoAppRpcService[size];
        }
    };
    private long mServiceId;
    private int mServiceVersion;

    public NanoAppRpcService(long serviceId, int serviceVersion) {
        this.mServiceId = serviceId;
        this.mServiceVersion = serviceVersion;
    }

    public long getId() {
        return this.mServiceId;
    }

    public int getVersion() {
        return this.mServiceVersion;
    }

    private int getMajorVersion() {
        return (this.mServiceVersion & (-16777216)) >>> 24;
    }

    private int getMinorVersion() {
        return (this.mServiceVersion & Spanned.SPAN_PRIORITY) >>> 16;
    }

    private int getPatchVersion() {
        return this.mServiceVersion & 65535;
    }

    private NanoAppRpcService(Parcel in) {
        this.mServiceId = in.readLong();
        this.mServiceVersion = in.readInt();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(this.mServiceId);
        out.writeInt(this.mServiceVersion);
    }

    public String toString() {
        return "NanoAppRpcService[Id = " + Long.toHexString(this.mServiceId) + ", version = v" + getMajorVersion() + MediaMetrics.SEPARATOR + getMinorVersion() + MediaMetrics.SEPARATOR + getPatchVersion() + NavigationBarInflaterView.SIZE_MOD_END;
    }

    public boolean equals(Object object) {
        boolean z = true;
        if (object == this) {
            return true;
        }
        if (!(object instanceof NanoAppRpcService)) {
            return false;
        }
        NanoAppRpcService other = (NanoAppRpcService) object;
        boolean isEqual = (other.getId() == this.mServiceId && other.getVersion() == this.mServiceVersion) ? false : false;
        return isEqual;
    }

    public int hashCode() {
        return (int) getId();
    }
}
