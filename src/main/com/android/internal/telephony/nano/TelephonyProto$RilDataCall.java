package com.android.internal.telephony.nano;

import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class TelephonyProto$RilDataCall extends ExtendableMessageNano<TelephonyProto$RilDataCall> {
    private static volatile TelephonyProto$RilDataCall[] _emptyArray;
    public int apnTypeBitmask;
    public int cid;
    public String ifname;
    public int state;
    public int type;

    /* loaded from: classes.dex */
    public interface State {
        public static final int CONNECTED = 1;
        public static final int DISCONNECTED = 2;
        public static final int UNKNOWN = 0;
    }

    public static TelephonyProto$RilDataCall[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new TelephonyProto$RilDataCall[0];
                }
            }
        }
        return _emptyArray;
    }

    public TelephonyProto$RilDataCall() {
        clear();
    }

    public TelephonyProto$RilDataCall clear() {
        this.cid = 0;
        this.type = 0;
        this.ifname = PhoneConfigurationManager.SSSS;
        this.state = 0;
        this.apnTypeBitmask = 0;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        int i = this.cid;
        if (i != 0) {
            codedOutputByteBufferNano.writeInt32(1, i);
        }
        int i2 = this.type;
        if (i2 != 0) {
            codedOutputByteBufferNano.writeInt32(2, i2);
        }
        if (!this.ifname.equals(PhoneConfigurationManager.SSSS)) {
            codedOutputByteBufferNano.writeString(3, this.ifname);
        }
        int i3 = this.state;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(4, i3);
        }
        int i4 = this.apnTypeBitmask;
        if (i4 != 0) {
            codedOutputByteBufferNano.writeInt32(5, i4);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        int i = this.cid;
        if (i != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
        }
        int i2 = this.type;
        if (i2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
        }
        if (!this.ifname.equals(PhoneConfigurationManager.SSSS)) {
            computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(3, this.ifname);
        }
        int i3 = this.state;
        if (i3 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i3);
        }
        int i4 = this.apnTypeBitmask;
        return i4 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(5, i4) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public TelephonyProto$RilDataCall mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 8) {
                this.cid = codedInputByteBufferNano.readInt32();
            } else if (readTag == 16) {
                int readInt32 = codedInputByteBufferNano.readInt32();
                switch (readInt32) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        this.type = readInt32;
                        continue;
                }
            } else if (readTag == 26) {
                this.ifname = codedInputByteBufferNano.readString();
            } else if (readTag == 32) {
                int readInt322 = codedInputByteBufferNano.readInt32();
                if (readInt322 == 0 || readInt322 == 1 || readInt322 == 2) {
                    this.state = readInt322;
                }
            } else if (readTag != 40) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.apnTypeBitmask = codedInputByteBufferNano.readInt32();
            }
        }
    }

    public static TelephonyProto$RilDataCall parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (TelephonyProto$RilDataCall) MessageNano.mergeFrom(new TelephonyProto$RilDataCall(), bArr);
    }

    public static TelephonyProto$RilDataCall parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new TelephonyProto$RilDataCall().mergeFrom(codedInputByteBufferNano);
    }
}
