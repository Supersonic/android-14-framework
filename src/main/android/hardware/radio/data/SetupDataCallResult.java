package android.hardware.radio.data;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class SetupDataCallResult implements Parcelable {
    public static final Parcelable.Creator<SetupDataCallResult> CREATOR = new Parcelable.Creator<SetupDataCallResult>() { // from class: android.hardware.radio.data.SetupDataCallResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SetupDataCallResult createFromParcel(Parcel _aidl_source) {
            SetupDataCallResult _aidl_out = new SetupDataCallResult();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SetupDataCallResult[] newArray(int _aidl_size) {
            return new SetupDataCallResult[_aidl_size];
        }
    };
    public static final int DATA_CONNECTION_STATUS_ACTIVE = 2;
    public static final int DATA_CONNECTION_STATUS_DORMANT = 1;
    public static final int DATA_CONNECTION_STATUS_INACTIVE = 0;
    public static final byte HANDOVER_FAILURE_MODE_DO_FALLBACK = 1;
    public static final byte HANDOVER_FAILURE_MODE_LEGACY = 0;
    public static final byte HANDOVER_FAILURE_MODE_NO_FALLBACK_RETRY_HANDOVER = 2;
    public static final byte HANDOVER_FAILURE_MODE_NO_FALLBACK_RETRY_SETUP_NORMAL = 3;
    public LinkAddress[] addresses;
    public int cause;
    public Qos defaultQos;
    public String[] dnses;
    public String[] gateways;
    public String ifname;
    public String[] pcscf;
    public QosSession[] qosSessions;
    public SliceInfo sliceInfo;
    public TrafficDescriptor[] trafficDescriptors;
    public int type;
    public long suggestedRetryTime = 0;
    public int cid = 0;
    public int active = 0;
    public int mtuV4 = 0;
    public int mtuV6 = 0;
    public byte handoverFailureMode = 0;
    public int pduSessionId = 0;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeInt(this.cause);
        _aidl_parcel.writeLong(this.suggestedRetryTime);
        _aidl_parcel.writeInt(this.cid);
        _aidl_parcel.writeInt(this.active);
        _aidl_parcel.writeInt(this.type);
        _aidl_parcel.writeString(this.ifname);
        _aidl_parcel.writeTypedArray(this.addresses, _aidl_flag);
        _aidl_parcel.writeStringArray(this.dnses);
        _aidl_parcel.writeStringArray(this.gateways);
        _aidl_parcel.writeStringArray(this.pcscf);
        _aidl_parcel.writeInt(this.mtuV4);
        _aidl_parcel.writeInt(this.mtuV6);
        _aidl_parcel.writeTypedObject(this.defaultQos, _aidl_flag);
        _aidl_parcel.writeTypedArray(this.qosSessions, _aidl_flag);
        _aidl_parcel.writeByte(this.handoverFailureMode);
        _aidl_parcel.writeInt(this.pduSessionId);
        _aidl_parcel.writeTypedObject(this.sliceInfo, _aidl_flag);
        _aidl_parcel.writeTypedArray(this.trafficDescriptors, _aidl_flag);
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
            this.cause = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.suggestedRetryTime = _aidl_parcel.readLong();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.cid = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.active = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.type = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.ifname = _aidl_parcel.readString();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.addresses = (LinkAddress[]) _aidl_parcel.createTypedArray(LinkAddress.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.dnses = _aidl_parcel.createStringArray();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.gateways = _aidl_parcel.createStringArray();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.pcscf = _aidl_parcel.createStringArray();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.mtuV4 = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.mtuV6 = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.defaultQos = (Qos) _aidl_parcel.readTypedObject(Qos.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.qosSessions = (QosSession[]) _aidl_parcel.createTypedArray(QosSession.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.handoverFailureMode = _aidl_parcel.readByte();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.pduSessionId = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.sliceInfo = (SliceInfo) _aidl_parcel.readTypedObject(SliceInfo.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.trafficDescriptors = (TrafficDescriptor[]) _aidl_parcel.createTypedArray(TrafficDescriptor.CREATOR);
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
        _aidl_sj.add("cause: " + DataCallFailCause$$.toString(this.cause));
        _aidl_sj.add("suggestedRetryTime: " + this.suggestedRetryTime);
        _aidl_sj.add("cid: " + this.cid);
        _aidl_sj.add("active: " + this.active);
        _aidl_sj.add("type: " + PdpProtocolType$$.toString(this.type));
        _aidl_sj.add("ifname: " + Objects.toString(this.ifname));
        _aidl_sj.add("addresses: " + Arrays.toString(this.addresses));
        _aidl_sj.add("dnses: " + Arrays.toString(this.dnses));
        _aidl_sj.add("gateways: " + Arrays.toString(this.gateways));
        _aidl_sj.add("pcscf: " + Arrays.toString(this.pcscf));
        _aidl_sj.add("mtuV4: " + this.mtuV4);
        _aidl_sj.add("mtuV6: " + this.mtuV6);
        _aidl_sj.add("defaultQos: " + Objects.toString(this.defaultQos));
        _aidl_sj.add("qosSessions: " + Arrays.toString(this.qosSessions));
        _aidl_sj.add("handoverFailureMode: " + ((int) this.handoverFailureMode));
        _aidl_sj.add("pduSessionId: " + this.pduSessionId);
        _aidl_sj.add("sliceInfo: " + Objects.toString(this.sliceInfo));
        _aidl_sj.add("trafficDescriptors: " + Arrays.toString(this.trafficDescriptors));
        return "android.hardware.radio.data.SetupDataCallResult" + _aidl_sj.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        int _mask = 0 | describeContents(this.addresses);
        return _mask | describeContents(this.defaultQos) | describeContents(this.qosSessions) | describeContents(this.sliceInfo) | describeContents(this.trafficDescriptors);
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
