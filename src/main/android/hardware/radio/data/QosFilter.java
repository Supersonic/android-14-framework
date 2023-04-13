package android.hardware.radio.data;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class QosFilter implements Parcelable {
    public static final Parcelable.Creator<QosFilter> CREATOR = new Parcelable.Creator<QosFilter>() { // from class: android.hardware.radio.data.QosFilter.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public QosFilter createFromParcel(Parcel _aidl_source) {
            QosFilter _aidl_out = new QosFilter();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public QosFilter[] newArray(int _aidl_size) {
            return new QosFilter[_aidl_size];
        }
    };
    public static final byte DIRECTION_BIDIRECTIONAL = 2;
    public static final byte DIRECTION_DOWNLINK = 0;
    public static final byte DIRECTION_UPLINK = 1;
    public static final byte PROTOCOL_AH = 51;
    public static final byte PROTOCOL_ESP = 50;
    public static final byte PROTOCOL_TCP = 6;
    public static final byte PROTOCOL_UDP = 17;
    public static final byte PROTOCOL_UNSPECIFIED = -1;
    public QosFilterIpv6FlowLabel flowLabel;
    public String[] localAddresses;
    public PortRange localPort;
    public String[] remoteAddresses;
    public PortRange remotePort;
    public QosFilterIpsecSpi spi;
    public QosFilterTypeOfService tos;
    public byte protocol = 0;
    public byte direction = 0;
    public int precedence = 0;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeStringArray(this.localAddresses);
        _aidl_parcel.writeStringArray(this.remoteAddresses);
        _aidl_parcel.writeTypedObject(this.localPort, _aidl_flag);
        _aidl_parcel.writeTypedObject(this.remotePort, _aidl_flag);
        _aidl_parcel.writeByte(this.protocol);
        _aidl_parcel.writeTypedObject(this.tos, _aidl_flag);
        _aidl_parcel.writeTypedObject(this.flowLabel, _aidl_flag);
        _aidl_parcel.writeTypedObject(this.spi, _aidl_flag);
        _aidl_parcel.writeByte(this.direction);
        _aidl_parcel.writeInt(this.precedence);
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
            this.localAddresses = _aidl_parcel.createStringArray();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.remoteAddresses = _aidl_parcel.createStringArray();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.localPort = (PortRange) _aidl_parcel.readTypedObject(PortRange.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.remotePort = (PortRange) _aidl_parcel.readTypedObject(PortRange.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.protocol = _aidl_parcel.readByte();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.tos = (QosFilterTypeOfService) _aidl_parcel.readTypedObject(QosFilterTypeOfService.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.flowLabel = (QosFilterIpv6FlowLabel) _aidl_parcel.readTypedObject(QosFilterIpv6FlowLabel.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.spi = (QosFilterIpsecSpi) _aidl_parcel.readTypedObject(QosFilterIpsecSpi.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.direction = _aidl_parcel.readByte();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.precedence = _aidl_parcel.readInt();
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
        _aidl_sj.add("localAddresses: " + Arrays.toString(this.localAddresses));
        _aidl_sj.add("remoteAddresses: " + Arrays.toString(this.remoteAddresses));
        _aidl_sj.add("localPort: " + Objects.toString(this.localPort));
        _aidl_sj.add("remotePort: " + Objects.toString(this.remotePort));
        _aidl_sj.add("protocol: " + ((int) this.protocol));
        _aidl_sj.add("tos: " + Objects.toString(this.tos));
        _aidl_sj.add("flowLabel: " + Objects.toString(this.flowLabel));
        _aidl_sj.add("spi: " + Objects.toString(this.spi));
        _aidl_sj.add("direction: " + ((int) this.direction));
        _aidl_sj.add("precedence: " + this.precedence);
        return "android.hardware.radio.data.QosFilter" + _aidl_sj.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        int _mask = 0 | describeContents(this.localPort);
        return _mask | describeContents(this.remotePort) | describeContents(this.tos) | describeContents(this.flowLabel) | describeContents(this.spi);
    }

    private int describeContents(Object _v) {
        if (_v == null || !(_v instanceof Parcelable)) {
            return 0;
        }
        return ((Parcelable) _v).describeContents();
    }
}
