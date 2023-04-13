package android.telephony.ims;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Range;
@SystemApi
/* loaded from: classes3.dex */
public final class AudioCodecAttributes implements Parcelable {
    public static final Parcelable.Creator<AudioCodecAttributes> CREATOR = new Parcelable.Creator<AudioCodecAttributes>() { // from class: android.telephony.ims.AudioCodecAttributes.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioCodecAttributes createFromParcel(Parcel in) {
            return new AudioCodecAttributes(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioCodecAttributes[] newArray(int size) {
            return new AudioCodecAttributes[size];
        }
    };
    private float mBandwidthKhz;
    private Range<Float> mBandwidthRangeKhz;
    private float mBitrateKbps;
    private Range<Float> mBitrateRangeKbps;

    public AudioCodecAttributes(float bitrateKbps, Range<Float> bitrateRangeKbps, float bandwidthKhz, Range<Float> bandwidthRangeKhz) {
        this.mBitrateKbps = bitrateKbps;
        this.mBitrateRangeKbps = bitrateRangeKbps;
        this.mBandwidthKhz = bandwidthKhz;
        this.mBandwidthRangeKhz = bandwidthRangeKhz;
    }

    private AudioCodecAttributes(Parcel in) {
        this.mBitrateKbps = in.readFloat();
        this.mBitrateRangeKbps = new Range<>(Float.valueOf(in.readFloat()), Float.valueOf(in.readFloat()));
        this.mBandwidthKhz = in.readFloat();
        this.mBandwidthRangeKhz = new Range<>(Float.valueOf(in.readFloat()), Float.valueOf(in.readFloat()));
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeFloat(this.mBitrateKbps);
        out.writeFloat(this.mBitrateRangeKbps.getLower().floatValue());
        out.writeFloat(this.mBitrateRangeKbps.getUpper().floatValue());
        out.writeFloat(this.mBandwidthKhz);
        out.writeFloat(this.mBandwidthRangeKhz.getLower().floatValue());
        out.writeFloat(this.mBandwidthRangeKhz.getUpper().floatValue());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public float getBitrateKbps() {
        return this.mBitrateKbps;
    }

    public Range<Float> getBitrateRangeKbps() {
        return this.mBitrateRangeKbps;
    }

    public float getBandwidthKhz() {
        return this.mBandwidthKhz;
    }

    public Range<Float> getBandwidthRangeKhz() {
        return this.mBandwidthRangeKhz;
    }

    public String toString() {
        return "{ bitrateKbps=" + this.mBitrateKbps + ", bitrateRangeKbps=" + this.mBitrateRangeKbps + ", bandwidthKhz=" + this.mBandwidthKhz + ", bandwidthRangeKhz=" + this.mBandwidthRangeKhz + " }";
    }
}
