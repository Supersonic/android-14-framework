package com.android.internal.telephony.nano;

import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class PersistAtomsProto$ImsRegistrationServiceDescStats extends ExtendableMessageNano<PersistAtomsProto$ImsRegistrationServiceDescStats> {
    private static volatile PersistAtomsProto$ImsRegistrationServiceDescStats[] _emptyArray;
    public int carrierId;
    public long publishedMillis;
    public int registrationTech;
    public int serviceIdName;
    public float serviceIdVersion;
    public int slotId;

    public static PersistAtomsProto$ImsRegistrationServiceDescStats[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$ImsRegistrationServiceDescStats[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$ImsRegistrationServiceDescStats() {
        clear();
    }

    public PersistAtomsProto$ImsRegistrationServiceDescStats clear() {
        this.carrierId = 0;
        this.slotId = 0;
        this.serviceIdName = 0;
        this.serviceIdVersion = 0.0f;
        this.registrationTech = 0;
        this.publishedMillis = 0L;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        int i = this.carrierId;
        if (i != 0) {
            codedOutputByteBufferNano.writeInt32(1, i);
        }
        int i2 = this.slotId;
        if (i2 != 0) {
            codedOutputByteBufferNano.writeInt32(2, i2);
        }
        int i3 = this.serviceIdName;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(3, i3);
        }
        if (Float.floatToIntBits(this.serviceIdVersion) != Float.floatToIntBits(0.0f)) {
            codedOutputByteBufferNano.writeFloat(4, this.serviceIdVersion);
        }
        int i4 = this.registrationTech;
        if (i4 != 0) {
            codedOutputByteBufferNano.writeInt32(5, i4);
        }
        long j = this.publishedMillis;
        if (j != 0) {
            codedOutputByteBufferNano.writeInt64(6, j);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        int i = this.carrierId;
        if (i != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
        }
        int i2 = this.slotId;
        if (i2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
        }
        int i3 = this.serviceIdName;
        if (i3 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
        }
        if (Float.floatToIntBits(this.serviceIdVersion) != Float.floatToIntBits(0.0f)) {
            computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(4, this.serviceIdVersion);
        }
        int i4 = this.registrationTech;
        if (i4 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, i4);
        }
        long j = this.publishedMillis;
        return j != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(6, j) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$ImsRegistrationServiceDescStats mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 8) {
                this.carrierId = codedInputByteBufferNano.readInt32();
            } else if (readTag == 16) {
                this.slotId = codedInputByteBufferNano.readInt32();
            } else if (readTag == 24) {
                this.serviceIdName = codedInputByteBufferNano.readInt32();
            } else if (readTag == 37) {
                this.serviceIdVersion = codedInputByteBufferNano.readFloat();
            } else if (readTag == 40) {
                this.registrationTech = codedInputByteBufferNano.readInt32();
            } else if (readTag != 48) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.publishedMillis = codedInputByteBufferNano.readInt64();
            }
        }
    }

    public static PersistAtomsProto$ImsRegistrationServiceDescStats parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$ImsRegistrationServiceDescStats) MessageNano.mergeFrom(new PersistAtomsProto$ImsRegistrationServiceDescStats(), bArr);
    }

    public static PersistAtomsProto$ImsRegistrationServiceDescStats parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$ImsRegistrationServiceDescStats().mergeFrom(codedInputByteBufferNano);
    }
}
