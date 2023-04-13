package com.android.internal.telephony.nano;

import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class PersistAtomsProto$PresenceNotifyEvent extends ExtendableMessageNano<PersistAtomsProto$PresenceNotifyEvent> {
    private static volatile PersistAtomsProto$PresenceNotifyEvent[] _emptyArray;
    public int carrierId;
    public boolean contentBodyReceived;
    public int count;
    public int mmtelCapsCount;
    public int noCapsCount;
    public int rcsCapsCount;
    public int reason;
    public int slotId;

    public static PersistAtomsProto$PresenceNotifyEvent[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$PresenceNotifyEvent[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$PresenceNotifyEvent() {
        clear();
    }

    public PersistAtomsProto$PresenceNotifyEvent clear() {
        this.carrierId = 0;
        this.slotId = 0;
        this.reason = 0;
        this.contentBodyReceived = false;
        this.rcsCapsCount = 0;
        this.mmtelCapsCount = 0;
        this.noCapsCount = 0;
        this.count = 0;
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
        int i3 = this.reason;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(3, i3);
        }
        boolean z = this.contentBodyReceived;
        if (z) {
            codedOutputByteBufferNano.writeBool(4, z);
        }
        int i4 = this.rcsCapsCount;
        if (i4 != 0) {
            codedOutputByteBufferNano.writeInt32(5, i4);
        }
        int i5 = this.mmtelCapsCount;
        if (i5 != 0) {
            codedOutputByteBufferNano.writeInt32(6, i5);
        }
        int i6 = this.noCapsCount;
        if (i6 != 0) {
            codedOutputByteBufferNano.writeInt32(7, i6);
        }
        int i7 = this.count;
        if (i7 != 0) {
            codedOutputByteBufferNano.writeInt32(8, i7);
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
        int i3 = this.reason;
        if (i3 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
        }
        boolean z = this.contentBodyReceived;
        if (z) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(4, z);
        }
        int i4 = this.rcsCapsCount;
        if (i4 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, i4);
        }
        int i5 = this.mmtelCapsCount;
        if (i5 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i5);
        }
        int i6 = this.noCapsCount;
        if (i6 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(7, i6);
        }
        int i7 = this.count;
        return i7 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(8, i7) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$PresenceNotifyEvent mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                this.reason = codedInputByteBufferNano.readInt32();
            } else if (readTag == 32) {
                this.contentBodyReceived = codedInputByteBufferNano.readBool();
            } else if (readTag == 40) {
                this.rcsCapsCount = codedInputByteBufferNano.readInt32();
            } else if (readTag == 48) {
                this.mmtelCapsCount = codedInputByteBufferNano.readInt32();
            } else if (readTag == 56) {
                this.noCapsCount = codedInputByteBufferNano.readInt32();
            } else if (readTag != 64) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.count = codedInputByteBufferNano.readInt32();
            }
        }
    }

    public static PersistAtomsProto$PresenceNotifyEvent parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$PresenceNotifyEvent) MessageNano.mergeFrom(new PersistAtomsProto$PresenceNotifyEvent(), bArr);
    }

    public static PersistAtomsProto$PresenceNotifyEvent parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$PresenceNotifyEvent().mergeFrom(codedInputByteBufferNano);
    }
}
