package android.media.audio.common;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class MicrophoneDynamicInfo implements Parcelable {
    public static final Parcelable.Creator<MicrophoneDynamicInfo> CREATOR = new Parcelable.Creator<MicrophoneDynamicInfo>() { // from class: android.media.audio.common.MicrophoneDynamicInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MicrophoneDynamicInfo createFromParcel(Parcel _aidl_source) {
            MicrophoneDynamicInfo _aidl_out = new MicrophoneDynamicInfo();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MicrophoneDynamicInfo[] newArray(int _aidl_size) {
            return new MicrophoneDynamicInfo[_aidl_size];
        }
    };
    public int[] channelMapping;

    /* renamed from: id */
    public String f290id;

    /* loaded from: classes2.dex */
    public @interface ChannelMapping {
        public static final int DIRECT = 1;
        public static final int PROCESSED = 2;
        public static final int UNUSED = 0;
    }

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeString(this.f290id);
        _aidl_parcel.writeIntArray(this.channelMapping);
        int _aidl_end_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.setDataPosition(_aidl_start_pos);
        _aidl_parcel.writeInt(_aidl_end_pos - _aidl_start_pos);
        _aidl_parcel.setDataPosition(_aidl_end_pos);
    }

    public final void readFromParcel(Parcel _aidl_parcel) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        int _aidl_parcelable_size = _aidl_parcel.readInt();
        try {
            if (_aidl_parcelable_size < 4) {
                throw new BadParcelableException("Parcelable too small");
            }
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.f290id = _aidl_parcel.readString();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.channelMapping = _aidl_parcel.createIntArray();
            if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                throw new BadParcelableException("Overflow in the size of parcelable");
            }
            _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
        } catch (Throwable th) {
            if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                throw new BadParcelableException("Overflow in the size of parcelable");
            }
            _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
            throw th;
        }
    }

    public String toString() {
        StringJoiner _aidl_sj = new StringJoiner(", ", "{", "}");
        _aidl_sj.add("id: " + Objects.toString(this.f290id));
        _aidl_sj.add("channelMapping: " + Arrays.toString(this.channelMapping));
        return "android.media.audio.common.MicrophoneDynamicInfo" + _aidl_sj.toString();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof MicrophoneDynamicInfo)) {
            return false;
        }
        MicrophoneDynamicInfo that = (MicrophoneDynamicInfo) other;
        if (Objects.deepEquals(this.f290id, that.f290id) && Objects.deepEquals(this.channelMapping, that.channelMapping)) {
            return true;
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(this.f290id, this.channelMapping).toArray());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
