package android.net.wifi.nl80211;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes2.dex */
public final class PnoSettings implements Parcelable {
    public static final Parcelable.Creator<PnoSettings> CREATOR = new Parcelable.Creator<PnoSettings>() { // from class: android.net.wifi.nl80211.PnoSettings.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PnoSettings createFromParcel(Parcel in) {
            PnoSettings result = new PnoSettings();
            result.mIntervalMs = in.readLong();
            result.mMin2gRssi = in.readInt();
            result.mMin5gRssi = in.readInt();
            result.mMin6gRssi = in.readInt();
            result.mScanIterations = in.readInt();
            result.mScanIntervalMultiplier = in.readInt();
            result.mPnoNetworks = new ArrayList();
            in.readTypedList(result.mPnoNetworks, PnoNetwork.CREATOR);
            return result;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PnoSettings[] newArray(int size) {
            return new PnoSettings[size];
        }
    };
    private long mIntervalMs;
    private int mMin2gRssi;
    private int mMin5gRssi;
    private int mMin6gRssi;
    private List<PnoNetwork> mPnoNetworks;
    private int mScanIntervalMultiplier;
    private int mScanIterations;

    public long getIntervalMillis() {
        return this.mIntervalMs;
    }

    public void setIntervalMillis(long intervalMillis) {
        this.mIntervalMs = intervalMillis;
    }

    public int getMin2gRssiDbm() {
        return this.mMin2gRssi;
    }

    public void setMin2gRssiDbm(int min2gRssiDbm) {
        this.mMin2gRssi = min2gRssiDbm;
    }

    public int getMin5gRssiDbm() {
        return this.mMin5gRssi;
    }

    public void setMin5gRssiDbm(int min5gRssiDbm) {
        this.mMin5gRssi = min5gRssiDbm;
    }

    public int getMin6gRssiDbm() {
        return this.mMin6gRssi;
    }

    public void setMin6gRssiDbm(int min6gRssiDbm) {
        this.mMin6gRssi = min6gRssiDbm;
    }

    public int getScanIterations() {
        return this.mScanIterations;
    }

    public void setScanIterations(int scanIterations) {
        this.mScanIterations = scanIterations;
    }

    public int getScanIntervalMultiplier() {
        return this.mScanIntervalMultiplier;
    }

    public void setScanIntervalMultiplier(int scanIntervalMultiplier) {
        this.mScanIntervalMultiplier = scanIntervalMultiplier;
    }

    public List<PnoNetwork> getPnoNetworks() {
        return this.mPnoNetworks;
    }

    public void setPnoNetworks(List<PnoNetwork> pnoNetworks) {
        this.mPnoNetworks = pnoNetworks;
    }

    public boolean equals(Object rhs) {
        PnoSettings settings;
        if (this == rhs) {
            return true;
        }
        if ((rhs instanceof PnoSettings) && (settings = (PnoSettings) rhs) != null) {
            return this.mIntervalMs == settings.mIntervalMs && this.mMin2gRssi == settings.mMin2gRssi && this.mMin5gRssi == settings.mMin5gRssi && this.mMin6gRssi == settings.mMin6gRssi && this.mScanIterations == settings.mScanIterations && this.mScanIntervalMultiplier == settings.mScanIntervalMultiplier && this.mPnoNetworks.equals(settings.mPnoNetworks);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Long.valueOf(this.mIntervalMs), Integer.valueOf(this.mMin2gRssi), Integer.valueOf(this.mMin5gRssi), Integer.valueOf(this.mMin6gRssi), Integer.valueOf(this.mScanIterations), Integer.valueOf(this.mScanIntervalMultiplier), this.mPnoNetworks);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(this.mIntervalMs);
        out.writeInt(this.mMin2gRssi);
        out.writeInt(this.mMin5gRssi);
        out.writeInt(this.mMin6gRssi);
        out.writeInt(this.mScanIterations);
        out.writeInt(this.mScanIntervalMultiplier);
        out.writeTypedList(this.mPnoNetworks);
    }
}
