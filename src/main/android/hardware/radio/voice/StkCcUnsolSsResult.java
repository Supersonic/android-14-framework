package android.hardware.radio.voice;

import android.hardware.radio.RadioError$$;
import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class StkCcUnsolSsResult implements Parcelable {
    public static final Parcelable.Creator<StkCcUnsolSsResult> CREATOR = new Parcelable.Creator<StkCcUnsolSsResult>() { // from class: android.hardware.radio.voice.StkCcUnsolSsResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StkCcUnsolSsResult createFromParcel(Parcel _aidl_source) {
            StkCcUnsolSsResult _aidl_out = new StkCcUnsolSsResult();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StkCcUnsolSsResult[] newArray(int _aidl_size) {
            return new StkCcUnsolSsResult[_aidl_size];
        }
    };
    public static final int REQUEST_TYPE_ACTIVATION = 0;
    public static final int REQUEST_TYPE_DEACTIVATION = 1;
    public static final int REQUEST_TYPE_ERASURE = 4;
    public static final int REQUEST_TYPE_INTERROGATION = 2;
    public static final int REQUEST_TYPE_REGISTRATION = 3;
    public static final int SERVICE_TYPE_ALL_BARRING = 16;
    public static final int SERVICE_TYPE_BAIC = 14;
    public static final int SERVICE_TYPE_BAIC_ROAMING = 15;
    public static final int SERVICE_TYPE_BAOC = 11;
    public static final int SERVICE_TYPE_BAOIC = 12;
    public static final int SERVICE_TYPE_BAOIC_EXC_HOME = 13;
    public static final int SERVICE_TYPE_CFU = 0;
    public static final int SERVICE_TYPE_CF_ALL = 4;
    public static final int SERVICE_TYPE_CF_ALL_CONDITIONAL = 5;
    public static final int SERVICE_TYPE_CF_BUSY = 1;
    public static final int SERVICE_TYPE_CF_NOT_REACHABLE = 3;
    public static final int SERVICE_TYPE_CF_NO_REPLY = 2;
    public static final int SERVICE_TYPE_CLIP = 6;
    public static final int SERVICE_TYPE_CLIR = 7;
    public static final int SERVICE_TYPE_COLP = 8;
    public static final int SERVICE_TYPE_COLR = 9;
    public static final int SERVICE_TYPE_INCOMING_BARRING = 18;
    public static final int SERVICE_TYPE_OUTGOING_BARRING = 17;
    public static final int SERVICE_TYPE_WAIT = 10;
    public static final int SUPP_SERVICE_CLASS_DATA = 2;
    public static final int SUPP_SERVICE_CLASS_DATA_ASYNC = 32;
    public static final int SUPP_SERVICE_CLASS_DATA_SYNC = 16;
    public static final int SUPP_SERVICE_CLASS_FAX = 4;
    public static final int SUPP_SERVICE_CLASS_MAX = 128;
    public static final int SUPP_SERVICE_CLASS_NONE = 0;
    public static final int SUPP_SERVICE_CLASS_PACKET = 64;
    public static final int SUPP_SERVICE_CLASS_PAD = 128;
    public static final int SUPP_SERVICE_CLASS_SMS = 8;
    public static final int SUPP_SERVICE_CLASS_VOICE = 1;
    public static final int TELESERVICE_TYPE_ALL_DATA_TELESERVICES = 3;
    public static final int TELESERVICE_TYPE_ALL_TELESERVICES_EXCEPT_SMS = 5;
    public static final int TELESERVICE_TYPE_ALL_TELESEVICES = 1;
    public static final int TELESERVICE_TYPE_ALL_TELE_AND_BEARER_SERVICES = 0;
    public static final int TELESERVICE_TYPE_SMS_SERVICES = 4;
    public static final int TELESERVICE_TYPE_TELEPHONY = 2;
    public CfData[] cfData;
    public int result;
    public SsInfoData[] ssInfo;
    public int serviceType = 0;
    public int requestType = 0;
    public int teleserviceType = 0;
    public int serviceClass = 0;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeInt(this.serviceType);
        _aidl_parcel.writeInt(this.requestType);
        _aidl_parcel.writeInt(this.teleserviceType);
        _aidl_parcel.writeInt(this.serviceClass);
        _aidl_parcel.writeInt(this.result);
        _aidl_parcel.writeTypedArray(this.ssInfo, _aidl_flag);
        _aidl_parcel.writeTypedArray(this.cfData, _aidl_flag);
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
            this.serviceType = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.requestType = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.teleserviceType = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.serviceClass = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.result = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.ssInfo = (SsInfoData[]) _aidl_parcel.createTypedArray(SsInfoData.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.cfData = (CfData[]) _aidl_parcel.createTypedArray(CfData.CREATOR);
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
        _aidl_sj.add("serviceType: " + this.serviceType);
        _aidl_sj.add("requestType: " + this.requestType);
        _aidl_sj.add("teleserviceType: " + this.teleserviceType);
        _aidl_sj.add("serviceClass: " + this.serviceClass);
        _aidl_sj.add("result: " + RadioError$$.toString(this.result));
        _aidl_sj.add("ssInfo: " + Arrays.toString(this.ssInfo));
        _aidl_sj.add("cfData: " + Arrays.toString(this.cfData));
        return "android.hardware.radio.voice.StkCcUnsolSsResult" + _aidl_sj.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        int _mask = 0 | describeContents(this.ssInfo);
        return _mask | describeContents(this.cfData);
    }

    private int describeContents(Object _v) {
        Object[] objArr;
        if (_v == null) {
            return 0;
        }
        if (_v instanceof Object[]) {
            int _mask = 0;
            for (Object o : (Object[]) _v) {
                _mask |= describeContents(o);
            }
            return _mask;
        } else if (!(_v instanceof Parcelable)) {
            return 0;
        } else {
            return ((Parcelable) _v).describeContents();
        }
    }
}
