package com.android.internal.telephony.nano;

import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class PersistAtomsProto$VoiceCallRatUsage extends ExtendableMessageNano<PersistAtomsProto$VoiceCallRatUsage> {
    private static volatile PersistAtomsProto$VoiceCallRatUsage[] _emptyArray;
    public long callCount;
    public int carrierId;
    public int rat;
    public long totalDurationMillis;

    public static PersistAtomsProto$VoiceCallRatUsage[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$VoiceCallRatUsage[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$VoiceCallRatUsage() {
        clear();
    }

    public PersistAtomsProto$VoiceCallRatUsage clear() {
        this.carrierId = 0;
        this.rat = 0;
        this.totalDurationMillis = 0L;
        this.callCount = 0L;
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
        int i2 = this.rat;
        if (i2 != 0) {
            codedOutputByteBufferNano.writeInt32(2, i2);
        }
        long j = this.totalDurationMillis;
        if (j != 0) {
            codedOutputByteBufferNano.writeInt64(3, j);
        }
        long j2 = this.callCount;
        if (j2 != 0) {
            codedOutputByteBufferNano.writeInt64(4, j2);
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
        int i2 = this.rat;
        if (i2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
        }
        long j = this.totalDurationMillis;
        if (j != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(3, j);
        }
        long j2 = this.callCount;
        return j2 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(4, j2) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$VoiceCallRatUsage mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 8) {
                this.carrierId = codedInputByteBufferNano.readInt32();
            } else if (readTag == 16) {
                this.rat = codedInputByteBufferNano.readInt32();
            } else if (readTag == 24) {
                this.totalDurationMillis = codedInputByteBufferNano.readInt64();
            } else if (readTag != 32) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.callCount = codedInputByteBufferNano.readInt64();
            }
        }
    }

    public static PersistAtomsProto$VoiceCallRatUsage parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$VoiceCallRatUsage) MessageNano.mergeFrom(new PersistAtomsProto$VoiceCallRatUsage(), bArr);
    }

    public static PersistAtomsProto$VoiceCallRatUsage parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$VoiceCallRatUsage().mergeFrom(codedInputByteBufferNano);
    }
}
