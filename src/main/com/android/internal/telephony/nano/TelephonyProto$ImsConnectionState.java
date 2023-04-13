package com.android.internal.telephony.nano;

import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class TelephonyProto$ImsConnectionState extends ExtendableMessageNano<TelephonyProto$ImsConnectionState> {
    private static volatile TelephonyProto$ImsConnectionState[] _emptyArray;
    public TelephonyProto$ImsReasonInfo reasonInfo;
    public int state;

    /* loaded from: classes.dex */
    public interface State {
        public static final int CONNECTED = 1;
        public static final int DISCONNECTED = 3;
        public static final int PROGRESSING = 2;
        public static final int RESUMED = 4;
        public static final int STATE_UNKNOWN = 0;
        public static final int SUSPENDED = 5;
    }

    public static TelephonyProto$ImsConnectionState[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new TelephonyProto$ImsConnectionState[0];
                }
            }
        }
        return _emptyArray;
    }

    public TelephonyProto$ImsConnectionState() {
        clear();
    }

    public TelephonyProto$ImsConnectionState clear() {
        this.state = 0;
        this.reasonInfo = null;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        int i = this.state;
        if (i != 0) {
            codedOutputByteBufferNano.writeInt32(1, i);
        }
        TelephonyProto$ImsReasonInfo telephonyProto$ImsReasonInfo = this.reasonInfo;
        if (telephonyProto$ImsReasonInfo != null) {
            codedOutputByteBufferNano.writeMessage(2, telephonyProto$ImsReasonInfo);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        int i = this.state;
        if (i != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
        }
        TelephonyProto$ImsReasonInfo telephonyProto$ImsReasonInfo = this.reasonInfo;
        return telephonyProto$ImsReasonInfo != null ? computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(2, telephonyProto$ImsReasonInfo) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public TelephonyProto$ImsConnectionState mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 8) {
                int readInt32 = codedInputByteBufferNano.readInt32();
                if (readInt32 == 0 || readInt32 == 1 || readInt32 == 2 || readInt32 == 3 || readInt32 == 4 || readInt32 == 5) {
                    this.state = readInt32;
                }
            } else if (readTag != 18) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                if (this.reasonInfo == null) {
                    this.reasonInfo = new TelephonyProto$ImsReasonInfo();
                }
                codedInputByteBufferNano.readMessage(this.reasonInfo);
            }
        }
    }

    public static TelephonyProto$ImsConnectionState parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (TelephonyProto$ImsConnectionState) MessageNano.mergeFrom(new TelephonyProto$ImsConnectionState(), bArr);
    }

    public static TelephonyProto$ImsConnectionState parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new TelephonyProto$ImsConnectionState().mergeFrom(codedInputByteBufferNano);
    }
}
