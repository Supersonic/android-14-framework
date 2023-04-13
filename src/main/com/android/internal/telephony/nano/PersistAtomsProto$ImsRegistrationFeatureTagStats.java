package com.android.internal.telephony.nano;

import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class PersistAtomsProto$ImsRegistrationFeatureTagStats extends ExtendableMessageNano<PersistAtomsProto$ImsRegistrationFeatureTagStats> {
    private static volatile PersistAtomsProto$ImsRegistrationFeatureTagStats[] _emptyArray;
    public int carrierId;
    public int featureTagName;
    public long registeredMillis;
    public int registrationTech;
    public int slotId;

    public static PersistAtomsProto$ImsRegistrationFeatureTagStats[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$ImsRegistrationFeatureTagStats[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$ImsRegistrationFeatureTagStats() {
        clear();
    }

    public PersistAtomsProto$ImsRegistrationFeatureTagStats clear() {
        this.carrierId = 0;
        this.slotId = 0;
        this.featureTagName = 0;
        this.registrationTech = 0;
        this.registeredMillis = 0L;
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
        int i3 = this.featureTagName;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(3, i3);
        }
        int i4 = this.registrationTech;
        if (i4 != 0) {
            codedOutputByteBufferNano.writeInt32(4, i4);
        }
        long j = this.registeredMillis;
        if (j != 0) {
            codedOutputByteBufferNano.writeInt64(5, j);
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
        int i3 = this.featureTagName;
        if (i3 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
        }
        int i4 = this.registrationTech;
        if (i4 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i4);
        }
        long j = this.registeredMillis;
        return j != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(5, j) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$ImsRegistrationFeatureTagStats mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                this.featureTagName = codedInputByteBufferNano.readInt32();
            } else if (readTag == 32) {
                this.registrationTech = codedInputByteBufferNano.readInt32();
            } else if (readTag != 40) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.registeredMillis = codedInputByteBufferNano.readInt64();
            }
        }
    }

    public static PersistAtomsProto$ImsRegistrationFeatureTagStats parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$ImsRegistrationFeatureTagStats) MessageNano.mergeFrom(new PersistAtomsProto$ImsRegistrationFeatureTagStats(), bArr);
    }

    public static PersistAtomsProto$ImsRegistrationFeatureTagStats parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$ImsRegistrationFeatureTagStats().mergeFrom(codedInputByteBufferNano);
    }
}
