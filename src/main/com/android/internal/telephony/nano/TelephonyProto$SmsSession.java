package com.android.internal.telephony.nano;

import com.android.internal.telephony.CallFailCause;
import com.android.internal.telephony.RadioNVItems;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyEvent;
import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import com.android.internal.telephony.protobuf.nano.WireFormatNano;
import com.android.internal.telephony.util.NetworkStackConstants;
import java.io.IOException;
/* loaded from: classes.dex */
public final class TelephonyProto$SmsSession extends ExtendableMessageNano<TelephonyProto$SmsSession> {
    private static volatile TelephonyProto$SmsSession[] _emptyArray;
    public Event[] events;
    public boolean eventsDropped;
    public int phoneId;
    public int startTimeMinutes;

    /* loaded from: classes.dex */
    public static final class Event extends ExtendableMessageNano<Event> {
        private static volatile Event[] _emptyArray;
        public boolean blocked;
        public CBMessage cellBroadcastMessage;
        public TelephonyProto$RilDataCall[] dataCalls;
        public int delay;
        public int error;
        public int errorCode;
        public int format;
        public TelephonyProto$ImsCapabilities imsCapabilities;
        public TelephonyProto$ImsConnectionState imsConnectionState;
        public int imsError;
        public IncompleteSms incompleteSms;
        public long messageId;
        public int rilRequestId;
        public TelephonyProto$TelephonyServiceState serviceState;
        public TelephonyProto$TelephonySettings settings;
        public int smsType;
        public int tech;
        public int type;

        /* loaded from: classes.dex */
        public interface CBMessageType {
            public static final int CMAS = 2;
            public static final int ETWS = 1;
            public static final int OTHER = 3;
            public static final int TYPE_UNKNOWN = 0;
        }

        /* loaded from: classes.dex */
        public interface CBPriority {
            public static final int EMERGENCY = 4;
            public static final int INTERACTIVE = 2;
            public static final int NORMAL = 1;
            public static final int PRIORITY_UNKNOWN = 0;
            public static final int URGENT = 3;
        }

        /* loaded from: classes.dex */
        public interface Format {
            public static final int SMS_FORMAT_3GPP = 1;
            public static final int SMS_FORMAT_3GPP2 = 2;
            public static final int SMS_FORMAT_UNKNOWN = 0;
        }

        /* loaded from: classes.dex */
        public interface SmsType {
            public static final int SMS_TYPE_NORMAL = 0;
            public static final int SMS_TYPE_SMS_PP = 1;
            public static final int SMS_TYPE_VOICEMAIL_INDICATION = 2;
            public static final int SMS_TYPE_WAP_PUSH = 4;
            public static final int SMS_TYPE_ZERO = 3;
        }

        /* loaded from: classes.dex */
        public interface Tech {
            public static final int SMS_CDMA = 2;
            public static final int SMS_GSM = 1;
            public static final int SMS_IMS = 3;
            public static final int SMS_UNKNOWN = 0;
        }

        /* loaded from: classes.dex */
        public interface Type {
            public static final int CB_SMS_RECEIVED = 9;
            public static final int DATA_CALL_LIST_CHANGED = 5;
            public static final int EVENT_UNKNOWN = 0;
            public static final int IMS_CAPABILITIES_CHANGED = 4;
            public static final int IMS_CONNECTION_STATE_CHANGED = 3;
            public static final int INCOMPLETE_SMS_RECEIVED = 10;
            public static final int RIL_SERVICE_STATE_CHANGED = 2;
            public static final int SETTINGS_CHANGED = 1;
            public static final int SMS_RECEIVED = 8;
            public static final int SMS_SEND = 6;
            public static final int SMS_SEND_RESULT = 7;
        }

        /* loaded from: classes.dex */
        public static final class CBMessage extends ExtendableMessageNano<CBMessage> {
            private static volatile CBMessage[] _emptyArray;
            public long deliveredTimestampMillis;
            public int msgFormat;
            public int msgPriority;
            public int msgType;
            public int serialNumber;
            public int serviceCategory;

            public static CBMessage[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new CBMessage[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public CBMessage() {
                clear();
            }

            public CBMessage clear() {
                this.msgFormat = 0;
                this.msgPriority = 0;
                this.msgType = 0;
                this.serviceCategory = 0;
                this.serialNumber = 0;
                this.deliveredTimestampMillis = 0L;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
            public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                int i = this.msgFormat;
                if (i != 0) {
                    codedOutputByteBufferNano.writeInt32(1, i);
                }
                int i2 = this.msgPriority;
                if (i2 != 0) {
                    codedOutputByteBufferNano.writeInt32(2, i2);
                }
                int i3 = this.msgType;
                if (i3 != 0) {
                    codedOutputByteBufferNano.writeInt32(3, i3);
                }
                int i4 = this.serviceCategory;
                if (i4 != 0) {
                    codedOutputByteBufferNano.writeInt32(4, i4);
                }
                int i5 = this.serialNumber;
                if (i5 != 0) {
                    codedOutputByteBufferNano.writeInt32(5, i5);
                }
                long j = this.deliveredTimestampMillis;
                if (j != 0) {
                    codedOutputByteBufferNano.writeInt64(6, j);
                }
                super.writeTo(codedOutputByteBufferNano);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
            public int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                int i = this.msgFormat;
                if (i != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
                }
                int i2 = this.msgPriority;
                if (i2 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
                }
                int i3 = this.msgType;
                if (i3 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
                }
                int i4 = this.serviceCategory;
                if (i4 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i4);
                }
                int i5 = this.serialNumber;
                if (i5 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, i5);
                }
                long j = this.deliveredTimestampMillis;
                return j != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(6, j) : computeSerializedSize;
            }

            @Override // com.android.internal.telephony.protobuf.nano.MessageNano
            public CBMessage mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int readTag = codedInputByteBufferNano.readTag();
                    if (readTag == 0) {
                        return this;
                    }
                    if (readTag == 8) {
                        int readInt32 = codedInputByteBufferNano.readInt32();
                        if (readInt32 == 0 || readInt32 == 1 || readInt32 == 2) {
                            this.msgFormat = readInt32;
                        }
                    } else if (readTag == 16) {
                        int readInt322 = codedInputByteBufferNano.readInt32();
                        if (readInt322 == 0 || readInt322 == 1 || readInt322 == 2 || readInt322 == 3 || readInt322 == 4) {
                            this.msgPriority = readInt322;
                        }
                    } else if (readTag == 24) {
                        int readInt323 = codedInputByteBufferNano.readInt32();
                        if (readInt323 == 0 || readInt323 == 1 || readInt323 == 2 || readInt323 == 3) {
                            this.msgType = readInt323;
                        }
                    } else if (readTag == 32) {
                        this.serviceCategory = codedInputByteBufferNano.readInt32();
                    } else if (readTag == 40) {
                        this.serialNumber = codedInputByteBufferNano.readInt32();
                    } else if (readTag != 48) {
                        if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                            return this;
                        }
                    } else {
                        this.deliveredTimestampMillis = codedInputByteBufferNano.readInt64();
                    }
                }
            }

            public static CBMessage parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (CBMessage) MessageNano.mergeFrom(new CBMessage(), bArr);
            }

            public static CBMessage parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new CBMessage().mergeFrom(codedInputByteBufferNano);
            }
        }

        /* loaded from: classes.dex */
        public static final class IncompleteSms extends ExtendableMessageNano<IncompleteSms> {
            private static volatile IncompleteSms[] _emptyArray;
            public int receivedParts;
            public int totalParts;

            public static IncompleteSms[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new IncompleteSms[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public IncompleteSms() {
                clear();
            }

            public IncompleteSms clear() {
                this.receivedParts = 0;
                this.totalParts = 0;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
            public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                int i = this.receivedParts;
                if (i != 0) {
                    codedOutputByteBufferNano.writeInt32(1, i);
                }
                int i2 = this.totalParts;
                if (i2 != 0) {
                    codedOutputByteBufferNano.writeInt32(2, i2);
                }
                super.writeTo(codedOutputByteBufferNano);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
            public int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                int i = this.receivedParts;
                if (i != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
                }
                int i2 = this.totalParts;
                return i2 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(2, i2) : computeSerializedSize;
            }

            @Override // com.android.internal.telephony.protobuf.nano.MessageNano
            public IncompleteSms mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int readTag = codedInputByteBufferNano.readTag();
                    if (readTag == 0) {
                        return this;
                    }
                    if (readTag == 8) {
                        this.receivedParts = codedInputByteBufferNano.readInt32();
                    } else if (readTag != 16) {
                        if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                            return this;
                        }
                    } else {
                        this.totalParts = codedInputByteBufferNano.readInt32();
                    }
                }
            }

            public static IncompleteSms parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (IncompleteSms) MessageNano.mergeFrom(new IncompleteSms(), bArr);
            }

            public static IncompleteSms parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new IncompleteSms().mergeFrom(codedInputByteBufferNano);
            }
        }

        public static Event[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Event[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Event() {
            clear();
        }

        public Event clear() {
            this.type = 0;
            this.delay = 0;
            this.settings = null;
            this.serviceState = null;
            this.imsConnectionState = null;
            this.imsCapabilities = null;
            this.dataCalls = TelephonyProto$RilDataCall.emptyArray();
            this.format = 0;
            this.tech = 0;
            this.errorCode = 0;
            this.error = 0;
            this.rilRequestId = 0;
            this.cellBroadcastMessage = null;
            this.imsError = 0;
            this.incompleteSms = null;
            this.smsType = 0;
            this.blocked = false;
            this.messageId = 0L;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.type;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            int i2 = this.delay;
            if (i2 != 0) {
                codedOutputByteBufferNano.writeInt32(2, i2);
            }
            TelephonyProto$TelephonySettings telephonyProto$TelephonySettings = this.settings;
            if (telephonyProto$TelephonySettings != null) {
                codedOutputByteBufferNano.writeMessage(3, telephonyProto$TelephonySettings);
            }
            TelephonyProto$TelephonyServiceState telephonyProto$TelephonyServiceState = this.serviceState;
            if (telephonyProto$TelephonyServiceState != null) {
                codedOutputByteBufferNano.writeMessage(4, telephonyProto$TelephonyServiceState);
            }
            TelephonyProto$ImsConnectionState telephonyProto$ImsConnectionState = this.imsConnectionState;
            if (telephonyProto$ImsConnectionState != null) {
                codedOutputByteBufferNano.writeMessage(5, telephonyProto$ImsConnectionState);
            }
            TelephonyProto$ImsCapabilities telephonyProto$ImsCapabilities = this.imsCapabilities;
            if (telephonyProto$ImsCapabilities != null) {
                codedOutputByteBufferNano.writeMessage(6, telephonyProto$ImsCapabilities);
            }
            TelephonyProto$RilDataCall[] telephonyProto$RilDataCallArr = this.dataCalls;
            if (telephonyProto$RilDataCallArr != null && telephonyProto$RilDataCallArr.length > 0) {
                int i3 = 0;
                while (true) {
                    TelephonyProto$RilDataCall[] telephonyProto$RilDataCallArr2 = this.dataCalls;
                    if (i3 >= telephonyProto$RilDataCallArr2.length) {
                        break;
                    }
                    TelephonyProto$RilDataCall telephonyProto$RilDataCall = telephonyProto$RilDataCallArr2[i3];
                    if (telephonyProto$RilDataCall != null) {
                        codedOutputByteBufferNano.writeMessage(7, telephonyProto$RilDataCall);
                    }
                    i3++;
                }
            }
            int i4 = this.format;
            if (i4 != 0) {
                codedOutputByteBufferNano.writeInt32(8, i4);
            }
            int i5 = this.tech;
            if (i5 != 0) {
                codedOutputByteBufferNano.writeInt32(9, i5);
            }
            int i6 = this.errorCode;
            if (i6 != 0) {
                codedOutputByteBufferNano.writeInt32(10, i6);
            }
            int i7 = this.error;
            if (i7 != 0) {
                codedOutputByteBufferNano.writeInt32(11, i7);
            }
            int i8 = this.rilRequestId;
            if (i8 != 0) {
                codedOutputByteBufferNano.writeInt32(12, i8);
            }
            CBMessage cBMessage = this.cellBroadcastMessage;
            if (cBMessage != null) {
                codedOutputByteBufferNano.writeMessage(13, cBMessage);
            }
            int i9 = this.imsError;
            if (i9 != 0) {
                codedOutputByteBufferNano.writeInt32(14, i9);
            }
            IncompleteSms incompleteSms = this.incompleteSms;
            if (incompleteSms != null) {
                codedOutputByteBufferNano.writeMessage(15, incompleteSms);
            }
            int i10 = this.smsType;
            if (i10 != 0) {
                codedOutputByteBufferNano.writeInt32(16, i10);
            }
            boolean z = this.blocked;
            if (z) {
                codedOutputByteBufferNano.writeBool(17, z);
            }
            long j = this.messageId;
            if (j != 0) {
                codedOutputByteBufferNano.writeInt64(18, j);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            int i = this.type;
            if (i != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.delay;
            if (i2 != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            TelephonyProto$TelephonySettings telephonyProto$TelephonySettings = this.settings;
            if (telephonyProto$TelephonySettings != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, telephonyProto$TelephonySettings);
            }
            TelephonyProto$TelephonyServiceState telephonyProto$TelephonyServiceState = this.serviceState;
            if (telephonyProto$TelephonyServiceState != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(4, telephonyProto$TelephonyServiceState);
            }
            TelephonyProto$ImsConnectionState telephonyProto$ImsConnectionState = this.imsConnectionState;
            if (telephonyProto$ImsConnectionState != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(5, telephonyProto$ImsConnectionState);
            }
            TelephonyProto$ImsCapabilities telephonyProto$ImsCapabilities = this.imsCapabilities;
            if (telephonyProto$ImsCapabilities != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(6, telephonyProto$ImsCapabilities);
            }
            TelephonyProto$RilDataCall[] telephonyProto$RilDataCallArr = this.dataCalls;
            if (telephonyProto$RilDataCallArr != null && telephonyProto$RilDataCallArr.length > 0) {
                int i3 = 0;
                while (true) {
                    TelephonyProto$RilDataCall[] telephonyProto$RilDataCallArr2 = this.dataCalls;
                    if (i3 >= telephonyProto$RilDataCallArr2.length) {
                        break;
                    }
                    TelephonyProto$RilDataCall telephonyProto$RilDataCall = telephonyProto$RilDataCallArr2[i3];
                    if (telephonyProto$RilDataCall != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(7, telephonyProto$RilDataCall);
                    }
                    i3++;
                }
            }
            int i4 = this.format;
            if (i4 != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(8, i4);
            }
            int i5 = this.tech;
            if (i5 != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(9, i5);
            }
            int i6 = this.errorCode;
            if (i6 != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(10, i6);
            }
            int i7 = this.error;
            if (i7 != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(11, i7);
            }
            int i8 = this.rilRequestId;
            if (i8 != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(12, i8);
            }
            CBMessage cBMessage = this.cellBroadcastMessage;
            if (cBMessage != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(13, cBMessage);
            }
            int i9 = this.imsError;
            if (i9 != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(14, i9);
            }
            IncompleteSms incompleteSms = this.incompleteSms;
            if (incompleteSms != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(15, incompleteSms);
            }
            int i10 = this.smsType;
            if (i10 != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(16, i10);
            }
            boolean z = this.blocked;
            if (z) {
                computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(17, z);
            }
            long j = this.messageId;
            return j != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(18, j) : computeSerializedSize;
        }

        @Override // com.android.internal.telephony.protobuf.nano.MessageNano
        public Event mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                switch (readTag) {
                    case 0:
                        return this;
                    case 8:
                        int readInt32 = codedInputByteBufferNano.readInt32();
                        switch (readInt32) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                                this.type = readInt32;
                                continue;
                        }
                    case 16:
                        int readInt322 = codedInputByteBufferNano.readInt32();
                        switch (readInt322) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                            case 11:
                            case 12:
                            case 13:
                            case 14:
                            case 15:
                            case 16:
                            case 17:
                            case 18:
                            case 19:
                                this.delay = readInt322;
                                continue;
                        }
                    case 26:
                        if (this.settings == null) {
                            this.settings = new TelephonyProto$TelephonySettings();
                        }
                        codedInputByteBufferNano.readMessage(this.settings);
                        break;
                    case 34:
                        if (this.serviceState == null) {
                            this.serviceState = new TelephonyProto$TelephonyServiceState();
                        }
                        codedInputByteBufferNano.readMessage(this.serviceState);
                        break;
                    case 42:
                        if (this.imsConnectionState == null) {
                            this.imsConnectionState = new TelephonyProto$ImsConnectionState();
                        }
                        codedInputByteBufferNano.readMessage(this.imsConnectionState);
                        break;
                    case 50:
                        if (this.imsCapabilities == null) {
                            this.imsCapabilities = new TelephonyProto$ImsCapabilities();
                        }
                        codedInputByteBufferNano.readMessage(this.imsCapabilities);
                        break;
                    case 58:
                        int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 58);
                        TelephonyProto$RilDataCall[] telephonyProto$RilDataCallArr = this.dataCalls;
                        int length = telephonyProto$RilDataCallArr == null ? 0 : telephonyProto$RilDataCallArr.length;
                        int i = repeatedFieldArrayLength + length;
                        TelephonyProto$RilDataCall[] telephonyProto$RilDataCallArr2 = new TelephonyProto$RilDataCall[i];
                        if (length != 0) {
                            System.arraycopy(telephonyProto$RilDataCallArr, 0, telephonyProto$RilDataCallArr2, 0, length);
                        }
                        while (length < i - 1) {
                            TelephonyProto$RilDataCall telephonyProto$RilDataCall = new TelephonyProto$RilDataCall();
                            telephonyProto$RilDataCallArr2[length] = telephonyProto$RilDataCall;
                            codedInputByteBufferNano.readMessage(telephonyProto$RilDataCall);
                            codedInputByteBufferNano.readTag();
                            length++;
                        }
                        TelephonyProto$RilDataCall telephonyProto$RilDataCall2 = new TelephonyProto$RilDataCall();
                        telephonyProto$RilDataCallArr2[length] = telephonyProto$RilDataCall2;
                        codedInputByteBufferNano.readMessage(telephonyProto$RilDataCall2);
                        this.dataCalls = telephonyProto$RilDataCallArr2;
                        break;
                    case 64:
                        int readInt323 = codedInputByteBufferNano.readInt32();
                        if (readInt323 != 0 && readInt323 != 1 && readInt323 != 2) {
                            break;
                        } else {
                            this.format = readInt323;
                            break;
                        }
                    case RadioNVItems.RIL_NV_LTE_BAND_ENABLE_26 /* 72 */:
                        int readInt324 = codedInputByteBufferNano.readInt32();
                        if (readInt324 != 0 && readInt324 != 1 && readInt324 != 2 && readInt324 != 3) {
                            break;
                        } else {
                            this.tech = readInt324;
                            break;
                        }
                    case RadioNVItems.RIL_NV_LTE_NEXT_SCAN /* 80 */:
                        this.errorCode = codedInputByteBufferNano.readInt32();
                        break;
                    case CallFailCause.INCOMPATIBLE_DESTINATION /* 88 */:
                        int readInt325 = codedInputByteBufferNano.readInt32();
                        switch (readInt325) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                            case 11:
                            case 12:
                            case 13:
                            case 14:
                            case 15:
                            case 16:
                            case 17:
                            case 18:
                            case 19:
                            case 20:
                            case 21:
                            case 22:
                            case 23:
                            case 24:
                            case 25:
                            case 26:
                            case 27:
                            case 28:
                                this.error = readInt325;
                                break;
                            default:
                                switch (readInt325) {
                                    case 36:
                                    case 37:
                                    case 38:
                                    case 39:
                                    case 40:
                                    case 41:
                                    case 42:
                                    case 43:
                                    case 44:
                                    case 45:
                                    case 46:
                                    case 47:
                                    case 48:
                                    case 49:
                                    case 50:
                                    case 51:
                                    case 52:
                                    case 53:
                                    case 54:
                                    case 55:
                                    case 56:
                                    case 57:
                                    case 58:
                                    case 59:
                                    case 60:
                                    case TelephonyProto$RilErrno.RIL_E_NETWORK_NOT_READY /* 61 */:
                                    case TelephonyProto$RilErrno.RIL_E_NOT_PROVISIONED /* 62 */:
                                    case 63:
                                    case 64:
                                    case 65:
                                    case 66:
                                    case TelephonyProto$RilErrno.RIL_E_INVALID_RESPONSE /* 67 */:
                                        this.error = readInt325;
                                        break;
                                }
                        }
                    case 96:
                        this.rilRequestId = codedInputByteBufferNano.readInt32();
                        break;
                    case 106:
                        if (this.cellBroadcastMessage == null) {
                            this.cellBroadcastMessage = new CBMessage();
                        }
                        codedInputByteBufferNano.readMessage(this.cellBroadcastMessage);
                        break;
                    case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_APN_TYPE_CONFLICT /* 112 */:
                        int readInt326 = codedInputByteBufferNano.readInt32();
                        if (readInt326 != 0 && readInt326 != 1 && readInt326 != 2 && readInt326 != 3 && readInt326 != 4) {
                            break;
                        } else {
                            this.imsError = readInt326;
                            break;
                        }
                    case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_AUTH_FAILURE_ON_EMERGENCY_CALL /* 122 */:
                        if (this.incompleteSms == null) {
                            this.incompleteSms = new IncompleteSms();
                        }
                        codedInputByteBufferNano.readMessage(this.incompleteSms);
                        break;
                    case 128:
                        int readInt327 = codedInputByteBufferNano.readInt32();
                        if (readInt327 != 0 && readInt327 != 1 && readInt327 != 2 && readInt327 != 3 && readInt327 != 4) {
                            break;
                        } else {
                            this.smsType = readInt327;
                            break;
                        }
                    case NetworkStackConstants.ICMPV6_NEIGHBOR_ADVERTISEMENT /* 136 */:
                        this.blocked = codedInputByteBufferNano.readBool();
                        break;
                    case 144:
                        this.messageId = codedInputByteBufferNano.readInt64();
                        break;
                    default:
                        if (storeUnknownField(codedInputByteBufferNano, readTag)) {
                            break;
                        } else {
                            return this;
                        }
                }
            }
        }

        public static Event parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (Event) MessageNano.mergeFrom(new Event(), bArr);
        }

        public static Event parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new Event().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static TelephonyProto$SmsSession[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new TelephonyProto$SmsSession[0];
                }
            }
        }
        return _emptyArray;
    }

    public TelephonyProto$SmsSession() {
        clear();
    }

    public TelephonyProto$SmsSession clear() {
        this.startTimeMinutes = 0;
        this.phoneId = 0;
        this.events = Event.emptyArray();
        this.eventsDropped = false;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        int i = this.startTimeMinutes;
        if (i != 0) {
            codedOutputByteBufferNano.writeInt32(1, i);
        }
        int i2 = this.phoneId;
        if (i2 != 0) {
            codedOutputByteBufferNano.writeInt32(2, i2);
        }
        Event[] eventArr = this.events;
        if (eventArr != null && eventArr.length > 0) {
            int i3 = 0;
            while (true) {
                Event[] eventArr2 = this.events;
                if (i3 >= eventArr2.length) {
                    break;
                }
                Event event = eventArr2[i3];
                if (event != null) {
                    codedOutputByteBufferNano.writeMessage(3, event);
                }
                i3++;
            }
        }
        boolean z = this.eventsDropped;
        if (z) {
            codedOutputByteBufferNano.writeBool(4, z);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        int i = this.startTimeMinutes;
        if (i != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
        }
        int i2 = this.phoneId;
        if (i2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
        }
        Event[] eventArr = this.events;
        if (eventArr != null && eventArr.length > 0) {
            int i3 = 0;
            while (true) {
                Event[] eventArr2 = this.events;
                if (i3 >= eventArr2.length) {
                    break;
                }
                Event event = eventArr2[i3];
                if (event != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, event);
                }
                i3++;
            }
        }
        boolean z = this.eventsDropped;
        return z ? computeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(4, z) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public TelephonyProto$SmsSession mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 8) {
                this.startTimeMinutes = codedInputByteBufferNano.readInt32();
            } else if (readTag == 16) {
                this.phoneId = codedInputByteBufferNano.readInt32();
            } else if (readTag == 26) {
                int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 26);
                Event[] eventArr = this.events;
                int length = eventArr == null ? 0 : eventArr.length;
                int i = repeatedFieldArrayLength + length;
                Event[] eventArr2 = new Event[i];
                if (length != 0) {
                    System.arraycopy(eventArr, 0, eventArr2, 0, length);
                }
                while (length < i - 1) {
                    Event event = new Event();
                    eventArr2[length] = event;
                    codedInputByteBufferNano.readMessage(event);
                    codedInputByteBufferNano.readTag();
                    length++;
                }
                Event event2 = new Event();
                eventArr2[length] = event2;
                codedInputByteBufferNano.readMessage(event2);
                this.events = eventArr2;
            } else if (readTag != 32) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.eventsDropped = codedInputByteBufferNano.readBool();
            }
        }
    }

    public static TelephonyProto$SmsSession parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (TelephonyProto$SmsSession) MessageNano.mergeFrom(new TelephonyProto$SmsSession(), bArr);
    }

    public static TelephonyProto$SmsSession parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new TelephonyProto$SmsSession().mergeFrom(codedInputByteBufferNano);
    }
}
