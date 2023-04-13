package com.android.internal.telephony.nano;

import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class PersistAtomsProto$CellularDataServiceSwitch extends ExtendableMessageNano<PersistAtomsProto$CellularDataServiceSwitch> {
    private static volatile PersistAtomsProto$CellularDataServiceSwitch[] _emptyArray;
    public int carrierId;
    public boolean isMultiSim;
    public long lastUsedMillis;
    public int ratFrom;
    public int ratTo;
    public int simSlotIndex;
    public int switchCount;

    public static PersistAtomsProto$CellularDataServiceSwitch[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$CellularDataServiceSwitch[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$CellularDataServiceSwitch() {
        clear();
    }

    public PersistAtomsProto$CellularDataServiceSwitch clear() {
        this.ratFrom = 0;
        this.ratTo = 0;
        this.simSlotIndex = 0;
        this.isMultiSim = false;
        this.carrierId = 0;
        this.switchCount = 0;
        this.lastUsedMillis = 0L;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        int i = this.ratFrom;
        if (i != 0) {
            codedOutputByteBufferNano.writeInt32(1, i);
        }
        int i2 = this.ratTo;
        if (i2 != 0) {
            codedOutputByteBufferNano.writeInt32(2, i2);
        }
        int i3 = this.simSlotIndex;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(3, i3);
        }
        boolean z = this.isMultiSim;
        if (z) {
            codedOutputByteBufferNano.writeBool(4, z);
        }
        int i4 = this.carrierId;
        if (i4 != 0) {
            codedOutputByteBufferNano.writeInt32(5, i4);
        }
        int i5 = this.switchCount;
        if (i5 != 0) {
            codedOutputByteBufferNano.writeInt32(6, i5);
        }
        long j = this.lastUsedMillis;
        if (j != 0) {
            codedOutputByteBufferNano.writeInt64(10001, j);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        int i = this.ratFrom;
        if (i != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
        }
        int i2 = this.ratTo;
        if (i2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
        }
        int i3 = this.simSlotIndex;
        if (i3 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
        }
        boolean z = this.isMultiSim;
        if (z) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(4, z);
        }
        int i4 = this.carrierId;
        if (i4 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, i4);
        }
        int i5 = this.switchCount;
        if (i5 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i5);
        }
        long j = this.lastUsedMillis;
        return j != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(10001, j) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$CellularDataServiceSwitch mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 8) {
                this.ratFrom = codedInputByteBufferNano.readInt32();
            } else if (readTag == 16) {
                this.ratTo = codedInputByteBufferNano.readInt32();
            } else if (readTag == 24) {
                this.simSlotIndex = codedInputByteBufferNano.readInt32();
            } else if (readTag == 32) {
                this.isMultiSim = codedInputByteBufferNano.readBool();
            } else if (readTag == 40) {
                this.carrierId = codedInputByteBufferNano.readInt32();
            } else if (readTag == 48) {
                this.switchCount = codedInputByteBufferNano.readInt32();
            } else if (readTag != 80008) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.lastUsedMillis = codedInputByteBufferNano.readInt64();
            }
        }
    }

    public static PersistAtomsProto$CellularDataServiceSwitch parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$CellularDataServiceSwitch) MessageNano.mergeFrom(new PersistAtomsProto$CellularDataServiceSwitch(), bArr);
    }

    public static PersistAtomsProto$CellularDataServiceSwitch parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$CellularDataServiceSwitch().mergeFrom(codedInputByteBufferNano);
    }
}
