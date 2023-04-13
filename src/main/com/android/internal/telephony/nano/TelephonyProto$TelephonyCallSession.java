package com.android.internal.telephony.nano;

import android.telephony.gsm.SmsMessage;
import com.android.internal.telephony.CallFailCause;
import com.android.internal.telephony.RadioNVItems;
import com.android.internal.telephony.cat.BerTlv;
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
public final class TelephonyProto$TelephonyCallSession extends ExtendableMessageNano<TelephonyProto$TelephonyCallSession> {
    private static volatile TelephonyProto$TelephonyCallSession[] _emptyArray;
    public Event[] events;
    public boolean eventsDropped;
    public int phoneId;
    public int startTimeMinutes;

    /* loaded from: classes.dex */
    public static final class Event extends ExtendableMessageNano<Event> {
        private static volatile Event[] _emptyArray;
        public int audioCodec;
        public int callIndex;
        public CallQuality callQuality;
        public CallQualitySummary callQualitySummaryDl;
        public CallQualitySummary callQualitySummaryUl;
        public int callState;
        public RilCall[] calls;
        public TelephonyProto$RilDataCall[] dataCalls;
        public int delay;
        public int emergencyNumberDatabaseVersion;
        public int error;
        public TelephonyProto$ImsCapabilities imsCapabilities;
        public int imsCommand;
        public TelephonyProto$ImsConnectionState imsConnectionState;
        public TelephonyProto$EmergencyNumberInfo imsEmergencyNumberInfo;
        public boolean isImsEmergencyCall;
        public int mergedCallIndex;
        public long nitzTimestampMillis;
        public int phoneState;
        public TelephonyProto$ImsReasonInfo reasonInfo;
        public int rilRequest;
        public int rilRequestId;
        public TelephonyProto$TelephonyServiceState serviceState;
        public TelephonyProto$TelephonySettings settings;
        public int srcAccessTech;
        public int srvccState;
        public int targetAccessTech;
        public int type;

        /* loaded from: classes.dex */
        public interface AudioCodec {
            public static final int AUDIO_CODEC_AMR = 1;
            public static final int AUDIO_CODEC_AMR_WB = 2;
            public static final int AUDIO_CODEC_EVRC = 4;
            public static final int AUDIO_CODEC_EVRC_B = 5;
            public static final int AUDIO_CODEC_EVRC_NW = 7;
            public static final int AUDIO_CODEC_EVRC_WB = 6;
            public static final int AUDIO_CODEC_EVS_FB = 20;
            public static final int AUDIO_CODEC_EVS_NB = 17;
            public static final int AUDIO_CODEC_EVS_SWB = 19;
            public static final int AUDIO_CODEC_EVS_WB = 18;
            public static final int AUDIO_CODEC_G711A = 13;
            public static final int AUDIO_CODEC_G711AB = 15;
            public static final int AUDIO_CODEC_G711U = 11;
            public static final int AUDIO_CODEC_G722 = 14;
            public static final int AUDIO_CODEC_G723 = 12;
            public static final int AUDIO_CODEC_G729 = 16;
            public static final int AUDIO_CODEC_GSM_EFR = 8;
            public static final int AUDIO_CODEC_GSM_FR = 9;
            public static final int AUDIO_CODEC_GSM_HR = 10;
            public static final int AUDIO_CODEC_QCELP13K = 3;
            public static final int AUDIO_CODEC_UNKNOWN = 0;
        }

        /* loaded from: classes.dex */
        public interface CallState {
            public static final int CALL_ACTIVE = 2;
            public static final int CALL_ALERTING = 5;
            public static final int CALL_DIALING = 4;
            public static final int CALL_DISCONNECTED = 8;
            public static final int CALL_DISCONNECTING = 9;
            public static final int CALL_HOLDING = 3;
            public static final int CALL_IDLE = 1;
            public static final int CALL_INCOMING = 6;
            public static final int CALL_UNKNOWN = 0;
            public static final int CALL_WAITING = 7;
        }

        /* loaded from: classes.dex */
        public interface ImsCommand {
            public static final int IMS_CMD_ACCEPT = 2;
            public static final int IMS_CMD_CONFERENCE_EXTEND = 9;
            public static final int IMS_CMD_HOLD = 5;
            public static final int IMS_CMD_INVITE_PARTICIPANT = 10;
            public static final int IMS_CMD_MERGE = 7;
            public static final int IMS_CMD_REJECT = 3;
            public static final int IMS_CMD_REMOVE_PARTICIPANT = 11;
            public static final int IMS_CMD_RESUME = 6;
            public static final int IMS_CMD_START = 1;
            public static final int IMS_CMD_TERMINATE = 4;
            public static final int IMS_CMD_UNKNOWN = 0;
            public static final int IMS_CMD_UPDATE = 8;
        }

        /* loaded from: classes.dex */
        public interface PhoneState {
            public static final int STATE_IDLE = 1;
            public static final int STATE_OFFHOOK = 3;
            public static final int STATE_RINGING = 2;
            public static final int STATE_UNKNOWN = 0;
        }

        /* loaded from: classes.dex */
        public interface RilRequest {
            public static final int RIL_REQUEST_ANSWER = 2;
            public static final int RIL_REQUEST_CDMA_FLASH = 6;
            public static final int RIL_REQUEST_CONFERENCE = 7;
            public static final int RIL_REQUEST_DIAL = 1;
            public static final int RIL_REQUEST_HANGUP = 3;
            public static final int RIL_REQUEST_SET_CALL_WAITING = 4;
            public static final int RIL_REQUEST_SWITCH_HOLDING_AND_ACTIVE = 5;
            public static final int RIL_REQUEST_UNKNOWN = 0;
        }

        /* loaded from: classes.dex */
        public interface RilSrvccState {
            public static final int HANDOVER_CANCELED = 4;
            public static final int HANDOVER_COMPLETED = 2;
            public static final int HANDOVER_FAILED = 3;
            public static final int HANDOVER_STARTED = 1;
            public static final int HANDOVER_UNKNOWN = 0;
        }

        /* loaded from: classes.dex */
        public interface Type {
            public static final int AUDIO_CODEC = 22;
            public static final int CALL_QUALITY_CHANGED = 23;
            public static final int DATA_CALL_LIST_CHANGED = 5;
            public static final int EVENT_UNKNOWN = 0;
            public static final int IMS_CALL_HANDOVER = 18;
            public static final int IMS_CALL_HANDOVER_FAILED = 19;
            public static final int IMS_CALL_RECEIVE = 15;
            public static final int IMS_CALL_STATE_CHANGED = 16;
            public static final int IMS_CALL_TERMINATED = 17;
            public static final int IMS_CAPABILITIES_CHANGED = 4;
            public static final int IMS_COMMAND = 11;
            public static final int IMS_COMMAND_COMPLETE = 14;
            public static final int IMS_COMMAND_FAILED = 13;
            public static final int IMS_COMMAND_RECEIVED = 12;
            public static final int IMS_CONNECTION_STATE_CHANGED = 3;
            public static final int NITZ_TIME = 21;
            public static final int PHONE_STATE_CHANGED = 20;
            public static final int RIL_CALL_LIST_CHANGED = 10;
            public static final int RIL_CALL_RING = 8;
            public static final int RIL_CALL_SRVCC = 9;
            public static final int RIL_REQUEST = 6;
            public static final int RIL_RESPONSE = 7;
            public static final int RIL_SERVICE_STATE_CHANGED = 2;
            public static final int SETTINGS_CHANGED = 1;
        }

        /* loaded from: classes.dex */
        public static final class RilCall extends ExtendableMessageNano<RilCall> {
            private static volatile RilCall[] _emptyArray;
            public int callEndReason;
            public int emergencyNumberDatabaseVersion;
            public TelephonyProto$EmergencyNumberInfo emergencyNumberInfo;
            public int index;
            public boolean isEmergencyCall;
            public boolean isMultiparty;
            public int preciseDisconnectCause;
            public int state;
            public int type;

            /* loaded from: classes.dex */
            public interface Type {

                /* renamed from: MO */
                public static final int f14MO = 1;

                /* renamed from: MT */
                public static final int f15MT = 2;
                public static final int UNKNOWN = 0;
            }

            public static RilCall[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new RilCall[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public RilCall() {
                clear();
            }

            public RilCall clear() {
                this.index = 0;
                this.state = 0;
                this.type = 0;
                this.callEndReason = 0;
                this.isMultiparty = false;
                this.preciseDisconnectCause = 0;
                this.isEmergencyCall = false;
                this.emergencyNumberInfo = null;
                this.emergencyNumberDatabaseVersion = 0;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
            public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                int i = this.index;
                if (i != 0) {
                    codedOutputByteBufferNano.writeInt32(1, i);
                }
                int i2 = this.state;
                if (i2 != 0) {
                    codedOutputByteBufferNano.writeInt32(2, i2);
                }
                int i3 = this.type;
                if (i3 != 0) {
                    codedOutputByteBufferNano.writeInt32(3, i3);
                }
                int i4 = this.callEndReason;
                if (i4 != 0) {
                    codedOutputByteBufferNano.writeInt32(4, i4);
                }
                boolean z = this.isMultiparty;
                if (z) {
                    codedOutputByteBufferNano.writeBool(5, z);
                }
                int i5 = this.preciseDisconnectCause;
                if (i5 != 0) {
                    codedOutputByteBufferNano.writeInt32(6, i5);
                }
                boolean z2 = this.isEmergencyCall;
                if (z2) {
                    codedOutputByteBufferNano.writeBool(7, z2);
                }
                TelephonyProto$EmergencyNumberInfo telephonyProto$EmergencyNumberInfo = this.emergencyNumberInfo;
                if (telephonyProto$EmergencyNumberInfo != null) {
                    codedOutputByteBufferNano.writeMessage(8, telephonyProto$EmergencyNumberInfo);
                }
                int i6 = this.emergencyNumberDatabaseVersion;
                if (i6 != 0) {
                    codedOutputByteBufferNano.writeInt32(9, i6);
                }
                super.writeTo(codedOutputByteBufferNano);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
            public int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                int i = this.index;
                if (i != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
                }
                int i2 = this.state;
                if (i2 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
                }
                int i3 = this.type;
                if (i3 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
                }
                int i4 = this.callEndReason;
                if (i4 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i4);
                }
                boolean z = this.isMultiparty;
                if (z) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(5, z);
                }
                int i5 = this.preciseDisconnectCause;
                if (i5 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i5);
                }
                boolean z2 = this.isEmergencyCall;
                if (z2) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(7, z2);
                }
                TelephonyProto$EmergencyNumberInfo telephonyProto$EmergencyNumberInfo = this.emergencyNumberInfo;
                if (telephonyProto$EmergencyNumberInfo != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(8, telephonyProto$EmergencyNumberInfo);
                }
                int i6 = this.emergencyNumberDatabaseVersion;
                return i6 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(9, i6) : computeSerializedSize;
            }

            @Override // com.android.internal.telephony.protobuf.nano.MessageNano
            public RilCall mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int readTag = codedInputByteBufferNano.readTag();
                    if (readTag == 0) {
                        return this;
                    }
                    if (readTag == 8) {
                        this.index = codedInputByteBufferNano.readInt32();
                    } else if (readTag == 16) {
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
                                this.state = readInt32;
                                continue;
                        }
                    } else if (readTag == 24) {
                        int readInt322 = codedInputByteBufferNano.readInt32();
                        if (readInt322 == 0 || readInt322 == 1 || readInt322 == 2) {
                            this.type = readInt322;
                        }
                    } else if (readTag == 32) {
                        this.callEndReason = codedInputByteBufferNano.readInt32();
                    } else if (readTag == 40) {
                        this.isMultiparty = codedInputByteBufferNano.readBool();
                    } else if (readTag == 48) {
                        this.preciseDisconnectCause = codedInputByteBufferNano.readInt32();
                    } else if (readTag == 56) {
                        this.isEmergencyCall = codedInputByteBufferNano.readBool();
                    } else if (readTag == 66) {
                        if (this.emergencyNumberInfo == null) {
                            this.emergencyNumberInfo = new TelephonyProto$EmergencyNumberInfo();
                        }
                        codedInputByteBufferNano.readMessage(this.emergencyNumberInfo);
                    } else if (readTag != 72) {
                        if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                            return this;
                        }
                    } else {
                        this.emergencyNumberDatabaseVersion = codedInputByteBufferNano.readInt32();
                    }
                }
            }

            public static RilCall parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (RilCall) MessageNano.mergeFrom(new RilCall(), bArr);
            }

            public static RilCall parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new RilCall().mergeFrom(codedInputByteBufferNano);
            }
        }

        /* loaded from: classes.dex */
        public static final class SignalStrength extends ExtendableMessageNano<SignalStrength> {
            private static volatile SignalStrength[] _emptyArray;
            public int lteSnr;

            public static SignalStrength[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new SignalStrength[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public SignalStrength() {
                clear();
            }

            public SignalStrength clear() {
                this.lteSnr = 0;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
            public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                int i = this.lteSnr;
                if (i != 0) {
                    codedOutputByteBufferNano.writeInt32(1, i);
                }
                super.writeTo(codedOutputByteBufferNano);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
            public int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                int i = this.lteSnr;
                return i != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(1, i) : computeSerializedSize;
            }

            @Override // com.android.internal.telephony.protobuf.nano.MessageNano
            public SignalStrength mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int readTag = codedInputByteBufferNano.readTag();
                    if (readTag == 0) {
                        return this;
                    }
                    if (readTag != 8) {
                        if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                            return this;
                        }
                    } else {
                        this.lteSnr = codedInputByteBufferNano.readInt32();
                    }
                }
            }

            public static SignalStrength parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (SignalStrength) MessageNano.mergeFrom(new SignalStrength(), bArr);
            }

            public static SignalStrength parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new SignalStrength().mergeFrom(codedInputByteBufferNano);
            }
        }

        /* loaded from: classes.dex */
        public static final class CallQuality extends ExtendableMessageNano<CallQuality> {
            private static volatile CallQuality[] _emptyArray;
            public int averageRelativeJitterMillis;
            public int averageRoundTripTime;
            public int codecType;
            public int downlinkLevel;
            public int durationInSeconds;
            public long maxPlayoutDelayMillis;
            public int maxRelativeJitterMillis;
            public long minPlayoutDelayMillis;
            public int noDataFrames;
            public int rtpDroppedPackets;
            public int rtpDuplicatePackets;
            public boolean rtpInactivityDetected;
            public int rtpPacketsNotReceived;
            public int rtpPacketsReceived;
            public int rtpPacketsTransmitted;
            public int rtpPacketsTransmittedLost;
            public int rxRtpSidPackets;
            public boolean rxSilenceDetected;
            public boolean txSilenceDetected;
            public int uplinkLevel;
            public int voiceFrames;

            /* loaded from: classes.dex */
            public interface CallQualityLevel {
                public static final int BAD = 5;
                public static final int EXCELLENT = 1;
                public static final int FAIR = 3;
                public static final int GOOD = 2;
                public static final int NOT_AVAILABLE = 6;
                public static final int POOR = 4;
                public static final int UNDEFINED = 0;
            }

            public static CallQuality[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new CallQuality[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public CallQuality() {
                clear();
            }

            public CallQuality clear() {
                this.downlinkLevel = 0;
                this.uplinkLevel = 0;
                this.durationInSeconds = 0;
                this.rtpPacketsTransmitted = 0;
                this.rtpPacketsReceived = 0;
                this.rtpPacketsTransmittedLost = 0;
                this.rtpPacketsNotReceived = 0;
                this.averageRelativeJitterMillis = 0;
                this.maxRelativeJitterMillis = 0;
                this.averageRoundTripTime = 0;
                this.codecType = 0;
                this.rtpInactivityDetected = false;
                this.rxSilenceDetected = false;
                this.txSilenceDetected = false;
                this.voiceFrames = 0;
                this.noDataFrames = 0;
                this.rtpDroppedPackets = 0;
                this.minPlayoutDelayMillis = 0L;
                this.maxPlayoutDelayMillis = 0L;
                this.rxRtpSidPackets = 0;
                this.rtpDuplicatePackets = 0;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
            public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                int i = this.downlinkLevel;
                if (i != 0) {
                    codedOutputByteBufferNano.writeInt32(1, i);
                }
                int i2 = this.uplinkLevel;
                if (i2 != 0) {
                    codedOutputByteBufferNano.writeInt32(2, i2);
                }
                int i3 = this.durationInSeconds;
                if (i3 != 0) {
                    codedOutputByteBufferNano.writeInt32(3, i3);
                }
                int i4 = this.rtpPacketsTransmitted;
                if (i4 != 0) {
                    codedOutputByteBufferNano.writeInt32(4, i4);
                }
                int i5 = this.rtpPacketsReceived;
                if (i5 != 0) {
                    codedOutputByteBufferNano.writeInt32(5, i5);
                }
                int i6 = this.rtpPacketsTransmittedLost;
                if (i6 != 0) {
                    codedOutputByteBufferNano.writeInt32(6, i6);
                }
                int i7 = this.rtpPacketsNotReceived;
                if (i7 != 0) {
                    codedOutputByteBufferNano.writeInt32(7, i7);
                }
                int i8 = this.averageRelativeJitterMillis;
                if (i8 != 0) {
                    codedOutputByteBufferNano.writeInt32(8, i8);
                }
                int i9 = this.maxRelativeJitterMillis;
                if (i9 != 0) {
                    codedOutputByteBufferNano.writeInt32(9, i9);
                }
                int i10 = this.averageRoundTripTime;
                if (i10 != 0) {
                    codedOutputByteBufferNano.writeInt32(10, i10);
                }
                int i11 = this.codecType;
                if (i11 != 0) {
                    codedOutputByteBufferNano.writeInt32(11, i11);
                }
                boolean z = this.rtpInactivityDetected;
                if (z) {
                    codedOutputByteBufferNano.writeBool(12, z);
                }
                boolean z2 = this.rxSilenceDetected;
                if (z2) {
                    codedOutputByteBufferNano.writeBool(13, z2);
                }
                boolean z3 = this.txSilenceDetected;
                if (z3) {
                    codedOutputByteBufferNano.writeBool(14, z3);
                }
                int i12 = this.voiceFrames;
                if (i12 != 0) {
                    codedOutputByteBufferNano.writeInt32(15, i12);
                }
                int i13 = this.noDataFrames;
                if (i13 != 0) {
                    codedOutputByteBufferNano.writeInt32(16, i13);
                }
                int i14 = this.rtpDroppedPackets;
                if (i14 != 0) {
                    codedOutputByteBufferNano.writeInt32(17, i14);
                }
                long j = this.minPlayoutDelayMillis;
                if (j != 0) {
                    codedOutputByteBufferNano.writeInt64(18, j);
                }
                long j2 = this.maxPlayoutDelayMillis;
                if (j2 != 0) {
                    codedOutputByteBufferNano.writeInt64(19, j2);
                }
                int i15 = this.rxRtpSidPackets;
                if (i15 != 0) {
                    codedOutputByteBufferNano.writeInt32(20, i15);
                }
                int i16 = this.rtpDuplicatePackets;
                if (i16 != 0) {
                    codedOutputByteBufferNano.writeInt32(21, i16);
                }
                super.writeTo(codedOutputByteBufferNano);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
            public int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                int i = this.downlinkLevel;
                if (i != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
                }
                int i2 = this.uplinkLevel;
                if (i2 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
                }
                int i3 = this.durationInSeconds;
                if (i3 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
                }
                int i4 = this.rtpPacketsTransmitted;
                if (i4 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i4);
                }
                int i5 = this.rtpPacketsReceived;
                if (i5 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, i5);
                }
                int i6 = this.rtpPacketsTransmittedLost;
                if (i6 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i6);
                }
                int i7 = this.rtpPacketsNotReceived;
                if (i7 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(7, i7);
                }
                int i8 = this.averageRelativeJitterMillis;
                if (i8 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(8, i8);
                }
                int i9 = this.maxRelativeJitterMillis;
                if (i9 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(9, i9);
                }
                int i10 = this.averageRoundTripTime;
                if (i10 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(10, i10);
                }
                int i11 = this.codecType;
                if (i11 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(11, i11);
                }
                boolean z = this.rtpInactivityDetected;
                if (z) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(12, z);
                }
                boolean z2 = this.rxSilenceDetected;
                if (z2) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(13, z2);
                }
                boolean z3 = this.txSilenceDetected;
                if (z3) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(14, z3);
                }
                int i12 = this.voiceFrames;
                if (i12 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(15, i12);
                }
                int i13 = this.noDataFrames;
                if (i13 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(16, i13);
                }
                int i14 = this.rtpDroppedPackets;
                if (i14 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(17, i14);
                }
                long j = this.minPlayoutDelayMillis;
                if (j != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(18, j);
                }
                long j2 = this.maxPlayoutDelayMillis;
                if (j2 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(19, j2);
                }
                int i15 = this.rxRtpSidPackets;
                if (i15 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(20, i15);
                }
                int i16 = this.rtpDuplicatePackets;
                return i16 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(21, i16) : computeSerializedSize;
            }

            @Override // com.android.internal.telephony.protobuf.nano.MessageNano
            public CallQuality mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                                    this.downlinkLevel = readInt32;
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
                                    this.uplinkLevel = readInt322;
                                    continue;
                            }
                        case 24:
                            this.durationInSeconds = codedInputByteBufferNano.readInt32();
                            break;
                        case 32:
                            this.rtpPacketsTransmitted = codedInputByteBufferNano.readInt32();
                            break;
                        case 40:
                            this.rtpPacketsReceived = codedInputByteBufferNano.readInt32();
                            break;
                        case 48:
                            this.rtpPacketsTransmittedLost = codedInputByteBufferNano.readInt32();
                            break;
                        case 56:
                            this.rtpPacketsNotReceived = codedInputByteBufferNano.readInt32();
                            break;
                        case 64:
                            this.averageRelativeJitterMillis = codedInputByteBufferNano.readInt32();
                            break;
                        case RadioNVItems.RIL_NV_LTE_BAND_ENABLE_26 /* 72 */:
                            this.maxRelativeJitterMillis = codedInputByteBufferNano.readInt32();
                            break;
                        case RadioNVItems.RIL_NV_LTE_NEXT_SCAN /* 80 */:
                            this.averageRoundTripTime = codedInputByteBufferNano.readInt32();
                            break;
                        case CallFailCause.INCOMPATIBLE_DESTINATION /* 88 */:
                            int readInt323 = codedInputByteBufferNano.readInt32();
                            switch (readInt323) {
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
                                    this.codecType = readInt323;
                                    continue;
                            }
                        case 96:
                            this.rtpInactivityDetected = codedInputByteBufferNano.readBool();
                            break;
                        case 104:
                            this.rxSilenceDetected = codedInputByteBufferNano.readBool();
                            break;
                        case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_APN_TYPE_CONFLICT /* 112 */:
                            this.txSilenceDetected = codedInputByteBufferNano.readBool();
                            break;
                        case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_AND_POL_FAMILY_MISMATCH /* 120 */:
                            this.voiceFrames = codedInputByteBufferNano.readInt32();
                            break;
                        case 128:
                            this.noDataFrames = codedInputByteBufferNano.readInt32();
                            break;
                        case NetworkStackConstants.ICMPV6_NEIGHBOR_ADVERTISEMENT /* 136 */:
                            this.rtpDroppedPackets = codedInputByteBufferNano.readInt32();
                            break;
                        case 144:
                            this.minPlayoutDelayMillis = codedInputByteBufferNano.readInt64();
                            break;
                        case 152:
                            this.maxPlayoutDelayMillis = codedInputByteBufferNano.readInt64();
                            break;
                        case SmsMessage.MAX_USER_DATA_SEPTETS /* 160 */:
                            this.rxRtpSidPackets = codedInputByteBufferNano.readInt32();
                            break;
                        case 168:
                            this.rtpDuplicatePackets = codedInputByteBufferNano.readInt32();
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

            public static CallQuality parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (CallQuality) MessageNano.mergeFrom(new CallQuality(), bArr);
            }

            public static CallQuality parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new CallQuality().mergeFrom(codedInputByteBufferNano);
            }
        }

        /* loaded from: classes.dex */
        public static final class CallQualitySummary extends ExtendableMessageNano<CallQualitySummary> {
            private static volatile CallQualitySummary[] _emptyArray;
            public SignalStrength bestSsWithBadQuality;
            public SignalStrength bestSsWithGoodQuality;
            public CallQuality snapshotOfBestSsWithBadQuality;
            public CallQuality snapshotOfBestSsWithGoodQuality;
            public CallQuality snapshotOfEnd;
            public CallQuality snapshotOfWorstSsWithBadQuality;
            public CallQuality snapshotOfWorstSsWithGoodQuality;
            public int totalBadQualityDurationInSeconds;
            public int totalDurationWithQualityInformationInSeconds;
            public int totalGoodQualityDurationInSeconds;
            public SignalStrength worstSsWithBadQuality;
            public SignalStrength worstSsWithGoodQuality;

            public static CallQualitySummary[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new CallQualitySummary[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public CallQualitySummary() {
                clear();
            }

            public CallQualitySummary clear() {
                this.totalGoodQualityDurationInSeconds = 0;
                this.totalBadQualityDurationInSeconds = 0;
                this.totalDurationWithQualityInformationInSeconds = 0;
                this.snapshotOfWorstSsWithGoodQuality = null;
                this.snapshotOfBestSsWithGoodQuality = null;
                this.snapshotOfWorstSsWithBadQuality = null;
                this.snapshotOfBestSsWithBadQuality = null;
                this.worstSsWithGoodQuality = null;
                this.bestSsWithGoodQuality = null;
                this.worstSsWithBadQuality = null;
                this.bestSsWithBadQuality = null;
                this.snapshotOfEnd = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
            public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                int i = this.totalGoodQualityDurationInSeconds;
                if (i != 0) {
                    codedOutputByteBufferNano.writeInt32(1, i);
                }
                int i2 = this.totalBadQualityDurationInSeconds;
                if (i2 != 0) {
                    codedOutputByteBufferNano.writeInt32(2, i2);
                }
                int i3 = this.totalDurationWithQualityInformationInSeconds;
                if (i3 != 0) {
                    codedOutputByteBufferNano.writeInt32(3, i3);
                }
                CallQuality callQuality = this.snapshotOfWorstSsWithGoodQuality;
                if (callQuality != null) {
                    codedOutputByteBufferNano.writeMessage(4, callQuality);
                }
                CallQuality callQuality2 = this.snapshotOfBestSsWithGoodQuality;
                if (callQuality2 != null) {
                    codedOutputByteBufferNano.writeMessage(5, callQuality2);
                }
                CallQuality callQuality3 = this.snapshotOfWorstSsWithBadQuality;
                if (callQuality3 != null) {
                    codedOutputByteBufferNano.writeMessage(6, callQuality3);
                }
                CallQuality callQuality4 = this.snapshotOfBestSsWithBadQuality;
                if (callQuality4 != null) {
                    codedOutputByteBufferNano.writeMessage(7, callQuality4);
                }
                SignalStrength signalStrength = this.worstSsWithGoodQuality;
                if (signalStrength != null) {
                    codedOutputByteBufferNano.writeMessage(8, signalStrength);
                }
                SignalStrength signalStrength2 = this.bestSsWithGoodQuality;
                if (signalStrength2 != null) {
                    codedOutputByteBufferNano.writeMessage(9, signalStrength2);
                }
                SignalStrength signalStrength3 = this.worstSsWithBadQuality;
                if (signalStrength3 != null) {
                    codedOutputByteBufferNano.writeMessage(10, signalStrength3);
                }
                SignalStrength signalStrength4 = this.bestSsWithBadQuality;
                if (signalStrength4 != null) {
                    codedOutputByteBufferNano.writeMessage(11, signalStrength4);
                }
                CallQuality callQuality5 = this.snapshotOfEnd;
                if (callQuality5 != null) {
                    codedOutputByteBufferNano.writeMessage(12, callQuality5);
                }
                super.writeTo(codedOutputByteBufferNano);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
            public int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                int i = this.totalGoodQualityDurationInSeconds;
                if (i != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
                }
                int i2 = this.totalBadQualityDurationInSeconds;
                if (i2 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
                }
                int i3 = this.totalDurationWithQualityInformationInSeconds;
                if (i3 != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
                }
                CallQuality callQuality = this.snapshotOfWorstSsWithGoodQuality;
                if (callQuality != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(4, callQuality);
                }
                CallQuality callQuality2 = this.snapshotOfBestSsWithGoodQuality;
                if (callQuality2 != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(5, callQuality2);
                }
                CallQuality callQuality3 = this.snapshotOfWorstSsWithBadQuality;
                if (callQuality3 != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(6, callQuality3);
                }
                CallQuality callQuality4 = this.snapshotOfBestSsWithBadQuality;
                if (callQuality4 != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(7, callQuality4);
                }
                SignalStrength signalStrength = this.worstSsWithGoodQuality;
                if (signalStrength != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(8, signalStrength);
                }
                SignalStrength signalStrength2 = this.bestSsWithGoodQuality;
                if (signalStrength2 != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(9, signalStrength2);
                }
                SignalStrength signalStrength3 = this.worstSsWithBadQuality;
                if (signalStrength3 != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(10, signalStrength3);
                }
                SignalStrength signalStrength4 = this.bestSsWithBadQuality;
                if (signalStrength4 != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(11, signalStrength4);
                }
                CallQuality callQuality5 = this.snapshotOfEnd;
                return callQuality5 != null ? computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(12, callQuality5) : computeSerializedSize;
            }

            @Override // com.android.internal.telephony.protobuf.nano.MessageNano
            public CallQualitySummary mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int readTag = codedInputByteBufferNano.readTag();
                    switch (readTag) {
                        case 0:
                            return this;
                        case 8:
                            this.totalGoodQualityDurationInSeconds = codedInputByteBufferNano.readInt32();
                            break;
                        case 16:
                            this.totalBadQualityDurationInSeconds = codedInputByteBufferNano.readInt32();
                            break;
                        case 24:
                            this.totalDurationWithQualityInformationInSeconds = codedInputByteBufferNano.readInt32();
                            break;
                        case 34:
                            if (this.snapshotOfWorstSsWithGoodQuality == null) {
                                this.snapshotOfWorstSsWithGoodQuality = new CallQuality();
                            }
                            codedInputByteBufferNano.readMessage(this.snapshotOfWorstSsWithGoodQuality);
                            break;
                        case 42:
                            if (this.snapshotOfBestSsWithGoodQuality == null) {
                                this.snapshotOfBestSsWithGoodQuality = new CallQuality();
                            }
                            codedInputByteBufferNano.readMessage(this.snapshotOfBestSsWithGoodQuality);
                            break;
                        case 50:
                            if (this.snapshotOfWorstSsWithBadQuality == null) {
                                this.snapshotOfWorstSsWithBadQuality = new CallQuality();
                            }
                            codedInputByteBufferNano.readMessage(this.snapshotOfWorstSsWithBadQuality);
                            break;
                        case 58:
                            if (this.snapshotOfBestSsWithBadQuality == null) {
                                this.snapshotOfBestSsWithBadQuality = new CallQuality();
                            }
                            codedInputByteBufferNano.readMessage(this.snapshotOfBestSsWithBadQuality);
                            break;
                        case 66:
                            if (this.worstSsWithGoodQuality == null) {
                                this.worstSsWithGoodQuality = new SignalStrength();
                            }
                            codedInputByteBufferNano.readMessage(this.worstSsWithGoodQuality);
                            break;
                        case RadioNVItems.RIL_NV_LTE_SCAN_PRIORITY_25 /* 74 */:
                            if (this.bestSsWithGoodQuality == null) {
                                this.bestSsWithGoodQuality = new SignalStrength();
                            }
                            codedInputByteBufferNano.readMessage(this.bestSsWithGoodQuality);
                            break;
                        case RadioNVItems.RIL_NV_LTE_BSR_MAX_TIME /* 82 */:
                            if (this.worstSsWithBadQuality == null) {
                                this.worstSsWithBadQuality = new SignalStrength();
                            }
                            codedInputByteBufferNano.readMessage(this.worstSsWithBadQuality);
                            break;
                        case 90:
                            if (this.bestSsWithBadQuality == null) {
                                this.bestSsWithBadQuality = new SignalStrength();
                            }
                            codedInputByteBufferNano.readMessage(this.bestSsWithBadQuality);
                            break;
                        case 98:
                            if (this.snapshotOfEnd == null) {
                                this.snapshotOfEnd = new CallQuality();
                            }
                            codedInputByteBufferNano.readMessage(this.snapshotOfEnd);
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

            public static CallQualitySummary parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (CallQualitySummary) MessageNano.mergeFrom(new CallQualitySummary(), bArr);
            }

            public static CallQualitySummary parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new CallQualitySummary().mergeFrom(codedInputByteBufferNano);
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
            this.phoneState = 0;
            this.callState = 0;
            this.callIndex = 0;
            this.mergedCallIndex = 0;
            this.calls = RilCall.emptyArray();
            this.error = 0;
            this.rilRequest = 0;
            this.rilRequestId = 0;
            this.srvccState = 0;
            this.imsCommand = 0;
            this.reasonInfo = null;
            this.srcAccessTech = -1;
            this.targetAccessTech = -1;
            this.nitzTimestampMillis = 0L;
            this.audioCodec = 0;
            this.callQuality = null;
            this.callQualitySummaryDl = null;
            this.callQualitySummaryUl = null;
            this.isImsEmergencyCall = false;
            this.imsEmergencyNumberInfo = null;
            this.emergencyNumberDatabaseVersion = 0;
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
            int i3 = 0;
            if (telephonyProto$RilDataCallArr != null && telephonyProto$RilDataCallArr.length > 0) {
                int i4 = 0;
                while (true) {
                    TelephonyProto$RilDataCall[] telephonyProto$RilDataCallArr2 = this.dataCalls;
                    if (i4 >= telephonyProto$RilDataCallArr2.length) {
                        break;
                    }
                    TelephonyProto$RilDataCall telephonyProto$RilDataCall = telephonyProto$RilDataCallArr2[i4];
                    if (telephonyProto$RilDataCall != null) {
                        codedOutputByteBufferNano.writeMessage(7, telephonyProto$RilDataCall);
                    }
                    i4++;
                }
            }
            int i5 = this.phoneState;
            if (i5 != 0) {
                codedOutputByteBufferNano.writeInt32(8, i5);
            }
            int i6 = this.callState;
            if (i6 != 0) {
                codedOutputByteBufferNano.writeInt32(9, i6);
            }
            int i7 = this.callIndex;
            if (i7 != 0) {
                codedOutputByteBufferNano.writeInt32(10, i7);
            }
            int i8 = this.mergedCallIndex;
            if (i8 != 0) {
                codedOutputByteBufferNano.writeInt32(11, i8);
            }
            RilCall[] rilCallArr = this.calls;
            if (rilCallArr != null && rilCallArr.length > 0) {
                while (true) {
                    RilCall[] rilCallArr2 = this.calls;
                    if (i3 >= rilCallArr2.length) {
                        break;
                    }
                    RilCall rilCall = rilCallArr2[i3];
                    if (rilCall != null) {
                        codedOutputByteBufferNano.writeMessage(12, rilCall);
                    }
                    i3++;
                }
            }
            int i9 = this.error;
            if (i9 != 0) {
                codedOutputByteBufferNano.writeInt32(13, i9);
            }
            int i10 = this.rilRequest;
            if (i10 != 0) {
                codedOutputByteBufferNano.writeInt32(14, i10);
            }
            int i11 = this.rilRequestId;
            if (i11 != 0) {
                codedOutputByteBufferNano.writeInt32(15, i11);
            }
            int i12 = this.srvccState;
            if (i12 != 0) {
                codedOutputByteBufferNano.writeInt32(16, i12);
            }
            int i13 = this.imsCommand;
            if (i13 != 0) {
                codedOutputByteBufferNano.writeInt32(17, i13);
            }
            TelephonyProto$ImsReasonInfo telephonyProto$ImsReasonInfo = this.reasonInfo;
            if (telephonyProto$ImsReasonInfo != null) {
                codedOutputByteBufferNano.writeMessage(18, telephonyProto$ImsReasonInfo);
            }
            int i14 = this.srcAccessTech;
            if (i14 != -1) {
                codedOutputByteBufferNano.writeInt32(19, i14);
            }
            int i15 = this.targetAccessTech;
            if (i15 != -1) {
                codedOutputByteBufferNano.writeInt32(20, i15);
            }
            long j = this.nitzTimestampMillis;
            if (j != 0) {
                codedOutputByteBufferNano.writeInt64(21, j);
            }
            int i16 = this.audioCodec;
            if (i16 != 0) {
                codedOutputByteBufferNano.writeInt32(22, i16);
            }
            CallQuality callQuality = this.callQuality;
            if (callQuality != null) {
                codedOutputByteBufferNano.writeMessage(23, callQuality);
            }
            CallQualitySummary callQualitySummary = this.callQualitySummaryDl;
            if (callQualitySummary != null) {
                codedOutputByteBufferNano.writeMessage(24, callQualitySummary);
            }
            CallQualitySummary callQualitySummary2 = this.callQualitySummaryUl;
            if (callQualitySummary2 != null) {
                codedOutputByteBufferNano.writeMessage(25, callQualitySummary2);
            }
            boolean z = this.isImsEmergencyCall;
            if (z) {
                codedOutputByteBufferNano.writeBool(26, z);
            }
            TelephonyProto$EmergencyNumberInfo telephonyProto$EmergencyNumberInfo = this.imsEmergencyNumberInfo;
            if (telephonyProto$EmergencyNumberInfo != null) {
                codedOutputByteBufferNano.writeMessage(27, telephonyProto$EmergencyNumberInfo);
            }
            int i17 = this.emergencyNumberDatabaseVersion;
            if (i17 != 0) {
                codedOutputByteBufferNano.writeInt32(28, i17);
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
            int i3 = 0;
            if (telephonyProto$RilDataCallArr != null && telephonyProto$RilDataCallArr.length > 0) {
                int i4 = 0;
                while (true) {
                    TelephonyProto$RilDataCall[] telephonyProto$RilDataCallArr2 = this.dataCalls;
                    if (i4 >= telephonyProto$RilDataCallArr2.length) {
                        break;
                    }
                    TelephonyProto$RilDataCall telephonyProto$RilDataCall = telephonyProto$RilDataCallArr2[i4];
                    if (telephonyProto$RilDataCall != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(7, telephonyProto$RilDataCall);
                    }
                    i4++;
                }
            }
            int i5 = this.phoneState;
            if (i5 != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(8, i5);
            }
            int i6 = this.callState;
            if (i6 != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(9, i6);
            }
            int i7 = this.callIndex;
            if (i7 != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(10, i7);
            }
            int i8 = this.mergedCallIndex;
            if (i8 != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(11, i8);
            }
            RilCall[] rilCallArr = this.calls;
            if (rilCallArr != null && rilCallArr.length > 0) {
                while (true) {
                    RilCall[] rilCallArr2 = this.calls;
                    if (i3 >= rilCallArr2.length) {
                        break;
                    }
                    RilCall rilCall = rilCallArr2[i3];
                    if (rilCall != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(12, rilCall);
                    }
                    i3++;
                }
            }
            int i9 = this.error;
            if (i9 != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(13, i9);
            }
            int i10 = this.rilRequest;
            if (i10 != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(14, i10);
            }
            int i11 = this.rilRequestId;
            if (i11 != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(15, i11);
            }
            int i12 = this.srvccState;
            if (i12 != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(16, i12);
            }
            int i13 = this.imsCommand;
            if (i13 != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(17, i13);
            }
            TelephonyProto$ImsReasonInfo telephonyProto$ImsReasonInfo = this.reasonInfo;
            if (telephonyProto$ImsReasonInfo != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(18, telephonyProto$ImsReasonInfo);
            }
            int i14 = this.srcAccessTech;
            if (i14 != -1) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(19, i14);
            }
            int i15 = this.targetAccessTech;
            if (i15 != -1) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(20, i15);
            }
            long j = this.nitzTimestampMillis;
            if (j != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(21, j);
            }
            int i16 = this.audioCodec;
            if (i16 != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(22, i16);
            }
            CallQuality callQuality = this.callQuality;
            if (callQuality != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(23, callQuality);
            }
            CallQualitySummary callQualitySummary = this.callQualitySummaryDl;
            if (callQualitySummary != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(24, callQualitySummary);
            }
            CallQualitySummary callQualitySummary2 = this.callQualitySummaryUl;
            if (callQualitySummary2 != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(25, callQualitySummary2);
            }
            boolean z = this.isImsEmergencyCall;
            if (z) {
                computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(26, z);
            }
            TelephonyProto$EmergencyNumberInfo telephonyProto$EmergencyNumberInfo = this.imsEmergencyNumberInfo;
            if (telephonyProto$EmergencyNumberInfo != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(27, telephonyProto$EmergencyNumberInfo);
            }
            int i17 = this.emergencyNumberDatabaseVersion;
            return i17 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(28, i17) : computeSerializedSize;
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
                        if (readInt323 != 0 && readInt323 != 1 && readInt323 != 2 && readInt323 != 3) {
                            break;
                        } else {
                            this.phoneState = readInt323;
                            break;
                        }
                        break;
                    case RadioNVItems.RIL_NV_LTE_BAND_ENABLE_26 /* 72 */:
                        int readInt324 = codedInputByteBufferNano.readInt32();
                        switch (readInt324) {
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
                                this.callState = readInt324;
                                continue;
                        }
                    case RadioNVItems.RIL_NV_LTE_NEXT_SCAN /* 80 */:
                        this.callIndex = codedInputByteBufferNano.readInt32();
                        break;
                    case CallFailCause.INCOMPATIBLE_DESTINATION /* 88 */:
                        this.mergedCallIndex = codedInputByteBufferNano.readInt32();
                        break;
                    case 98:
                        int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 98);
                        RilCall[] rilCallArr = this.calls;
                        int length2 = rilCallArr == null ? 0 : rilCallArr.length;
                        int i2 = repeatedFieldArrayLength2 + length2;
                        RilCall[] rilCallArr2 = new RilCall[i2];
                        if (length2 != 0) {
                            System.arraycopy(rilCallArr, 0, rilCallArr2, 0, length2);
                        }
                        while (length2 < i2 - 1) {
                            RilCall rilCall = new RilCall();
                            rilCallArr2[length2] = rilCall;
                            codedInputByteBufferNano.readMessage(rilCall);
                            codedInputByteBufferNano.readTag();
                            length2++;
                        }
                        RilCall rilCall2 = new RilCall();
                        rilCallArr2[length2] = rilCall2;
                        codedInputByteBufferNano.readMessage(rilCall2);
                        this.calls = rilCallArr2;
                        break;
                    case 104:
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
                    case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_APN_TYPE_CONFLICT /* 112 */:
                        int readInt326 = codedInputByteBufferNano.readInt32();
                        switch (readInt326) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                                this.rilRequest = readInt326;
                                continue;
                        }
                    case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_AND_POL_FAMILY_MISMATCH /* 120 */:
                        this.rilRequestId = codedInputByteBufferNano.readInt32();
                        break;
                    case 128:
                        int readInt327 = codedInputByteBufferNano.readInt32();
                        if (readInt327 != 0 && readInt327 != 1 && readInt327 != 2 && readInt327 != 3 && readInt327 != 4) {
                            break;
                        } else {
                            this.srvccState = readInt327;
                            break;
                        }
                    case NetworkStackConstants.ICMPV6_NEIGHBOR_ADVERTISEMENT /* 136 */:
                        int readInt328 = codedInputByteBufferNano.readInt32();
                        switch (readInt328) {
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
                                this.imsCommand = readInt328;
                                continue;
                        }
                    case 146:
                        if (this.reasonInfo == null) {
                            this.reasonInfo = new TelephonyProto$ImsReasonInfo();
                        }
                        codedInputByteBufferNano.readMessage(this.reasonInfo);
                        break;
                    case 152:
                        int readInt329 = codedInputByteBufferNano.readInt32();
                        switch (readInt329) {
                            case -1:
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
                                this.srcAccessTech = readInt329;
                                continue;
                        }
                    case SmsMessage.MAX_USER_DATA_SEPTETS /* 160 */:
                        int readInt3210 = codedInputByteBufferNano.readInt32();
                        switch (readInt3210) {
                            case -1:
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
                                this.targetAccessTech = readInt3210;
                                continue;
                        }
                    case 168:
                        this.nitzTimestampMillis = codedInputByteBufferNano.readInt64();
                        break;
                    case 176:
                        int readInt3211 = codedInputByteBufferNano.readInt32();
                        switch (readInt3211) {
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
                                this.audioCodec = readInt3211;
                                continue;
                        }
                    case 186:
                        if (this.callQuality == null) {
                            this.callQuality = new CallQuality();
                        }
                        codedInputByteBufferNano.readMessage(this.callQuality);
                        break;
                    case 194:
                        if (this.callQualitySummaryDl == null) {
                            this.callQualitySummaryDl = new CallQualitySummary();
                        }
                        codedInputByteBufferNano.readMessage(this.callQualitySummaryDl);
                        break;
                    case 202:
                        if (this.callQualitySummaryUl == null) {
                            this.callQualitySummaryUl = new CallQualitySummary();
                        }
                        codedInputByteBufferNano.readMessage(this.callQualitySummaryUl);
                        break;
                    case BerTlv.BER_PROACTIVE_COMMAND_TAG /* 208 */:
                        this.isImsEmergencyCall = codedInputByteBufferNano.readBool();
                        break;
                    case 218:
                        if (this.imsEmergencyNumberInfo == null) {
                            this.imsEmergencyNumberInfo = new TelephonyProto$EmergencyNumberInfo();
                        }
                        codedInputByteBufferNano.readMessage(this.imsEmergencyNumberInfo);
                        break;
                    case 224:
                        this.emergencyNumberDatabaseVersion = codedInputByteBufferNano.readInt32();
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

    public static TelephonyProto$TelephonyCallSession[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new TelephonyProto$TelephonyCallSession[0];
                }
            }
        }
        return _emptyArray;
    }

    public TelephonyProto$TelephonyCallSession() {
        clear();
    }

    public TelephonyProto$TelephonyCallSession clear() {
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
    public TelephonyProto$TelephonyCallSession mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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

    public static TelephonyProto$TelephonyCallSession parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (TelephonyProto$TelephonyCallSession) MessageNano.mergeFrom(new TelephonyProto$TelephonyCallSession(), bArr);
    }

    public static TelephonyProto$TelephonyCallSession parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new TelephonyProto$TelephonyCallSession().mergeFrom(codedInputByteBufferNano);
    }
}
