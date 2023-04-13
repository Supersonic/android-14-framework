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
public final class PersistAtomsProto$ImsRegistrationTermination extends ExtendableMessageNano<PersistAtomsProto$ImsRegistrationTermination> {
    private static volatile PersistAtomsProto$ImsRegistrationTermination[] _emptyArray;
    public int carrierId;
    public int count;
    public int extraCode;
    public String extraMessage;
    public boolean isMultiSim;
    public long lastUsedMillis;
    public int ratAtEnd;
    public int reasonCode;
    public boolean setupFailed;

    public static PersistAtomsProto$ImsRegistrationTermination[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$ImsRegistrationTermination[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$ImsRegistrationTermination() {
        clear();
    }

    public PersistAtomsProto$ImsRegistrationTermination clear() {
        this.carrierId = 0;
        this.isMultiSim = false;
        this.ratAtEnd = 0;
        this.setupFailed = false;
        this.reasonCode = 0;
        this.extraCode = 0;
        this.extraMessage = PhoneConfigurationManager.SSSS;
        this.count = 0;
        this.lastUsedMillis = 0L;
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
        boolean z = this.isMultiSim;
        if (z) {
            codedOutputByteBufferNano.writeBool(2, z);
        }
        int i2 = this.ratAtEnd;
        if (i2 != 0) {
            codedOutputByteBufferNano.writeInt32(3, i2);
        }
        boolean z2 = this.setupFailed;
        if (z2) {
            codedOutputByteBufferNano.writeBool(4, z2);
        }
        int i3 = this.reasonCode;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(5, i3);
        }
        int i4 = this.extraCode;
        if (i4 != 0) {
            codedOutputByteBufferNano.writeInt32(6, i4);
        }
        if (!this.extraMessage.equals(PhoneConfigurationManager.SSSS)) {
            codedOutputByteBufferNano.writeString(7, this.extraMessage);
        }
        int i5 = this.count;
        if (i5 != 0) {
            codedOutputByteBufferNano.writeInt32(8, i5);
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
        int i = this.carrierId;
        if (i != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
        }
        boolean z = this.isMultiSim;
        if (z) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(2, z);
        }
        int i2 = this.ratAtEnd;
        if (i2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i2);
        }
        boolean z2 = this.setupFailed;
        if (z2) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(4, z2);
        }
        int i3 = this.reasonCode;
        if (i3 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, i3);
        }
        int i4 = this.extraCode;
        if (i4 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i4);
        }
        if (!this.extraMessage.equals(PhoneConfigurationManager.SSSS)) {
            computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(7, this.extraMessage);
        }
        int i5 = this.count;
        if (i5 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(8, i5);
        }
        long j = this.lastUsedMillis;
        return j != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(10001, j) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$ImsRegistrationTermination mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 8) {
                this.carrierId = codedInputByteBufferNano.readInt32();
            } else if (readTag == 16) {
                this.isMultiSim = codedInputByteBufferNano.readBool();
            } else if (readTag == 24) {
                this.ratAtEnd = codedInputByteBufferNano.readInt32();
            } else if (readTag == 32) {
                this.setupFailed = codedInputByteBufferNano.readBool();
            } else if (readTag == 40) {
                this.reasonCode = codedInputByteBufferNano.readInt32();
            } else if (readTag == 48) {
                this.extraCode = codedInputByteBufferNano.readInt32();
            } else if (readTag == 58) {
                this.extraMessage = codedInputByteBufferNano.readString();
            } else if (readTag == 64) {
                this.count = codedInputByteBufferNano.readInt32();
            } else if (readTag != 80008) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.lastUsedMillis = codedInputByteBufferNano.readInt64();
            }
        }
    }

    public static PersistAtomsProto$ImsRegistrationTermination parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$ImsRegistrationTermination) MessageNano.mergeFrom(new PersistAtomsProto$ImsRegistrationTermination(), bArr);
    }

    public static PersistAtomsProto$ImsRegistrationTermination parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$ImsRegistrationTermination().mergeFrom(codedInputByteBufferNano);
    }
}
