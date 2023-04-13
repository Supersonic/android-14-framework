package com.android.internal.telephony.nano;

import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class TelephonyProto$ImsCapabilities extends ExtendableMessageNano<TelephonyProto$ImsCapabilities> {
    private static volatile TelephonyProto$ImsCapabilities[] _emptyArray;
    public boolean utOverLte;
    public boolean utOverWifi;
    public boolean videoOverLte;
    public boolean videoOverWifi;
    public boolean voiceOverLte;
    public boolean voiceOverWifi;

    public static TelephonyProto$ImsCapabilities[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new TelephonyProto$ImsCapabilities[0];
                }
            }
        }
        return _emptyArray;
    }

    public TelephonyProto$ImsCapabilities() {
        clear();
    }

    public TelephonyProto$ImsCapabilities clear() {
        this.voiceOverLte = false;
        this.voiceOverWifi = false;
        this.videoOverLte = false;
        this.videoOverWifi = false;
        this.utOverLte = false;
        this.utOverWifi = false;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        boolean z = this.voiceOverLte;
        if (z) {
            codedOutputByteBufferNano.writeBool(1, z);
        }
        boolean z2 = this.voiceOverWifi;
        if (z2) {
            codedOutputByteBufferNano.writeBool(2, z2);
        }
        boolean z3 = this.videoOverLte;
        if (z3) {
            codedOutputByteBufferNano.writeBool(3, z3);
        }
        boolean z4 = this.videoOverWifi;
        if (z4) {
            codedOutputByteBufferNano.writeBool(4, z4);
        }
        boolean z5 = this.utOverLte;
        if (z5) {
            codedOutputByteBufferNano.writeBool(5, z5);
        }
        boolean z6 = this.utOverWifi;
        if (z6) {
            codedOutputByteBufferNano.writeBool(6, z6);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        boolean z = this.voiceOverLte;
        if (z) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(1, z);
        }
        boolean z2 = this.voiceOverWifi;
        if (z2) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(2, z2);
        }
        boolean z3 = this.videoOverLte;
        if (z3) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(3, z3);
        }
        boolean z4 = this.videoOverWifi;
        if (z4) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(4, z4);
        }
        boolean z5 = this.utOverLte;
        if (z5) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(5, z5);
        }
        boolean z6 = this.utOverWifi;
        return z6 ? computeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(6, z6) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public TelephonyProto$ImsCapabilities mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 8) {
                this.voiceOverLte = codedInputByteBufferNano.readBool();
            } else if (readTag == 16) {
                this.voiceOverWifi = codedInputByteBufferNano.readBool();
            } else if (readTag == 24) {
                this.videoOverLte = codedInputByteBufferNano.readBool();
            } else if (readTag == 32) {
                this.videoOverWifi = codedInputByteBufferNano.readBool();
            } else if (readTag == 40) {
                this.utOverLte = codedInputByteBufferNano.readBool();
            } else if (readTag != 48) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.utOverWifi = codedInputByteBufferNano.readBool();
            }
        }
    }

    public static TelephonyProto$ImsCapabilities parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (TelephonyProto$ImsCapabilities) MessageNano.mergeFrom(new TelephonyProto$ImsCapabilities(), bArr);
    }

    public static TelephonyProto$ImsCapabilities parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new TelephonyProto$ImsCapabilities().mergeFrom(codedInputByteBufferNano);
    }
}
