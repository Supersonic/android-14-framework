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
public final class PersistAtomsProto$CarrierIdMismatch extends ExtendableMessageNano<PersistAtomsProto$CarrierIdMismatch> {
    private static volatile PersistAtomsProto$CarrierIdMismatch[] _emptyArray;
    public String gid1;
    public String mccMnc;
    public String pnn;
    public String spn;

    public static PersistAtomsProto$CarrierIdMismatch[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$CarrierIdMismatch[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$CarrierIdMismatch() {
        clear();
    }

    public PersistAtomsProto$CarrierIdMismatch clear() {
        this.mccMnc = PhoneConfigurationManager.SSSS;
        this.gid1 = PhoneConfigurationManager.SSSS;
        this.spn = PhoneConfigurationManager.SSSS;
        this.pnn = PhoneConfigurationManager.SSSS;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        if (!this.mccMnc.equals(PhoneConfigurationManager.SSSS)) {
            codedOutputByteBufferNano.writeString(1, this.mccMnc);
        }
        if (!this.gid1.equals(PhoneConfigurationManager.SSSS)) {
            codedOutputByteBufferNano.writeString(2, this.gid1);
        }
        if (!this.spn.equals(PhoneConfigurationManager.SSSS)) {
            codedOutputByteBufferNano.writeString(3, this.spn);
        }
        if (!this.pnn.equals(PhoneConfigurationManager.SSSS)) {
            codedOutputByteBufferNano.writeString(4, this.pnn);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        if (!this.mccMnc.equals(PhoneConfigurationManager.SSSS)) {
            computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.mccMnc);
        }
        if (!this.gid1.equals(PhoneConfigurationManager.SSSS)) {
            computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(2, this.gid1);
        }
        if (!this.spn.equals(PhoneConfigurationManager.SSSS)) {
            computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(3, this.spn);
        }
        return !this.pnn.equals(PhoneConfigurationManager.SSSS) ? computeSerializedSize + CodedOutputByteBufferNano.computeStringSize(4, this.pnn) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$CarrierIdMismatch mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 10) {
                this.mccMnc = codedInputByteBufferNano.readString();
            } else if (readTag == 18) {
                this.gid1 = codedInputByteBufferNano.readString();
            } else if (readTag == 26) {
                this.spn = codedInputByteBufferNano.readString();
            } else if (readTag != 34) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.pnn = codedInputByteBufferNano.readString();
            }
        }
    }

    public static PersistAtomsProto$CarrierIdMismatch parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$CarrierIdMismatch) MessageNano.mergeFrom(new PersistAtomsProto$CarrierIdMismatch(), bArr);
    }

    public static PersistAtomsProto$CarrierIdMismatch parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$CarrierIdMismatch().mergeFrom(codedInputByteBufferNano);
    }
}
