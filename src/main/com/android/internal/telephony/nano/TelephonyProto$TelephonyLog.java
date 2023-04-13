package com.android.internal.telephony.nano;

import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.RadioNVItems;
import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import com.android.internal.telephony.protobuf.nano.WireFormatNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class TelephonyProto$TelephonyLog extends ExtendableMessageNano<TelephonyProto$TelephonyLog> {
    private static volatile TelephonyProto$TelephonyLog[] _emptyArray;
    public TelephonyProto$BandwidthEstimatorStats bandwidthEstimatorStats;
    public TelephonyProto$TelephonyCallSession[] callSessions;
    public TelephonyProto$Time endTime;
    public TelephonyProto$TelephonyEvent[] events;
    public boolean eventsDropped;
    public String hardwareRevision;
    public TelephonyProto$TelephonyHistogram[] histograms;
    public TelephonyProto$ActiveSubscriptionInfo[] lastActiveSubscriptionInfo;
    public TelephonyProto$ModemPowerStats modemPowerStats;
    public TelephonyProto$SmsSession[] smsSessions;
    public TelephonyProto$Time startTime;

    public static TelephonyProto$TelephonyLog[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new TelephonyProto$TelephonyLog[0];
                }
            }
        }
        return _emptyArray;
    }

    public TelephonyProto$TelephonyLog() {
        clear();
    }

    public TelephonyProto$TelephonyLog clear() {
        this.events = TelephonyProto$TelephonyEvent.emptyArray();
        this.callSessions = TelephonyProto$TelephonyCallSession.emptyArray();
        this.smsSessions = TelephonyProto$SmsSession.emptyArray();
        this.histograms = TelephonyProto$TelephonyHistogram.emptyArray();
        this.eventsDropped = false;
        this.startTime = null;
        this.endTime = null;
        this.modemPowerStats = null;
        this.hardwareRevision = PhoneConfigurationManager.SSSS;
        this.lastActiveSubscriptionInfo = TelephonyProto$ActiveSubscriptionInfo.emptyArray();
        this.bandwidthEstimatorStats = null;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        TelephonyProto$TelephonyEvent[] telephonyProto$TelephonyEventArr = this.events;
        int i = 0;
        if (telephonyProto$TelephonyEventArr != null && telephonyProto$TelephonyEventArr.length > 0) {
            int i2 = 0;
            while (true) {
                TelephonyProto$TelephonyEvent[] telephonyProto$TelephonyEventArr2 = this.events;
                if (i2 >= telephonyProto$TelephonyEventArr2.length) {
                    break;
                }
                TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = telephonyProto$TelephonyEventArr2[i2];
                if (telephonyProto$TelephonyEvent != null) {
                    codedOutputByteBufferNano.writeMessage(1, telephonyProto$TelephonyEvent);
                }
                i2++;
            }
        }
        TelephonyProto$TelephonyCallSession[] telephonyProto$TelephonyCallSessionArr = this.callSessions;
        if (telephonyProto$TelephonyCallSessionArr != null && telephonyProto$TelephonyCallSessionArr.length > 0) {
            int i3 = 0;
            while (true) {
                TelephonyProto$TelephonyCallSession[] telephonyProto$TelephonyCallSessionArr2 = this.callSessions;
                if (i3 >= telephonyProto$TelephonyCallSessionArr2.length) {
                    break;
                }
                TelephonyProto$TelephonyCallSession telephonyProto$TelephonyCallSession = telephonyProto$TelephonyCallSessionArr2[i3];
                if (telephonyProto$TelephonyCallSession != null) {
                    codedOutputByteBufferNano.writeMessage(2, telephonyProto$TelephonyCallSession);
                }
                i3++;
            }
        }
        TelephonyProto$SmsSession[] telephonyProto$SmsSessionArr = this.smsSessions;
        if (telephonyProto$SmsSessionArr != null && telephonyProto$SmsSessionArr.length > 0) {
            int i4 = 0;
            while (true) {
                TelephonyProto$SmsSession[] telephonyProto$SmsSessionArr2 = this.smsSessions;
                if (i4 >= telephonyProto$SmsSessionArr2.length) {
                    break;
                }
                TelephonyProto$SmsSession telephonyProto$SmsSession = telephonyProto$SmsSessionArr2[i4];
                if (telephonyProto$SmsSession != null) {
                    codedOutputByteBufferNano.writeMessage(3, telephonyProto$SmsSession);
                }
                i4++;
            }
        }
        TelephonyProto$TelephonyHistogram[] telephonyProto$TelephonyHistogramArr = this.histograms;
        if (telephonyProto$TelephonyHistogramArr != null && telephonyProto$TelephonyHistogramArr.length > 0) {
            int i5 = 0;
            while (true) {
                TelephonyProto$TelephonyHistogram[] telephonyProto$TelephonyHistogramArr2 = this.histograms;
                if (i5 >= telephonyProto$TelephonyHistogramArr2.length) {
                    break;
                }
                TelephonyProto$TelephonyHistogram telephonyProto$TelephonyHistogram = telephonyProto$TelephonyHistogramArr2[i5];
                if (telephonyProto$TelephonyHistogram != null) {
                    codedOutputByteBufferNano.writeMessage(4, telephonyProto$TelephonyHistogram);
                }
                i5++;
            }
        }
        boolean z = this.eventsDropped;
        if (z) {
            codedOutputByteBufferNano.writeBool(5, z);
        }
        TelephonyProto$Time telephonyProto$Time = this.startTime;
        if (telephonyProto$Time != null) {
            codedOutputByteBufferNano.writeMessage(6, telephonyProto$Time);
        }
        TelephonyProto$Time telephonyProto$Time2 = this.endTime;
        if (telephonyProto$Time2 != null) {
            codedOutputByteBufferNano.writeMessage(7, telephonyProto$Time2);
        }
        TelephonyProto$ModemPowerStats telephonyProto$ModemPowerStats = this.modemPowerStats;
        if (telephonyProto$ModemPowerStats != null) {
            codedOutputByteBufferNano.writeMessage(8, telephonyProto$ModemPowerStats);
        }
        if (!this.hardwareRevision.equals(PhoneConfigurationManager.SSSS)) {
            codedOutputByteBufferNano.writeString(9, this.hardwareRevision);
        }
        TelephonyProto$ActiveSubscriptionInfo[] telephonyProto$ActiveSubscriptionInfoArr = this.lastActiveSubscriptionInfo;
        if (telephonyProto$ActiveSubscriptionInfoArr != null && telephonyProto$ActiveSubscriptionInfoArr.length > 0) {
            while (true) {
                TelephonyProto$ActiveSubscriptionInfo[] telephonyProto$ActiveSubscriptionInfoArr2 = this.lastActiveSubscriptionInfo;
                if (i >= telephonyProto$ActiveSubscriptionInfoArr2.length) {
                    break;
                }
                TelephonyProto$ActiveSubscriptionInfo telephonyProto$ActiveSubscriptionInfo = telephonyProto$ActiveSubscriptionInfoArr2[i];
                if (telephonyProto$ActiveSubscriptionInfo != null) {
                    codedOutputByteBufferNano.writeMessage(10, telephonyProto$ActiveSubscriptionInfo);
                }
                i++;
            }
        }
        TelephonyProto$BandwidthEstimatorStats telephonyProto$BandwidthEstimatorStats = this.bandwidthEstimatorStats;
        if (telephonyProto$BandwidthEstimatorStats != null) {
            codedOutputByteBufferNano.writeMessage(11, telephonyProto$BandwidthEstimatorStats);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        TelephonyProto$TelephonyEvent[] telephonyProto$TelephonyEventArr = this.events;
        int i = 0;
        if (telephonyProto$TelephonyEventArr != null && telephonyProto$TelephonyEventArr.length > 0) {
            int i2 = 0;
            while (true) {
                TelephonyProto$TelephonyEvent[] telephonyProto$TelephonyEventArr2 = this.events;
                if (i2 >= telephonyProto$TelephonyEventArr2.length) {
                    break;
                }
                TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = telephonyProto$TelephonyEventArr2[i2];
                if (telephonyProto$TelephonyEvent != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, telephonyProto$TelephonyEvent);
                }
                i2++;
            }
        }
        TelephonyProto$TelephonyCallSession[] telephonyProto$TelephonyCallSessionArr = this.callSessions;
        if (telephonyProto$TelephonyCallSessionArr != null && telephonyProto$TelephonyCallSessionArr.length > 0) {
            int i3 = 0;
            while (true) {
                TelephonyProto$TelephonyCallSession[] telephonyProto$TelephonyCallSessionArr2 = this.callSessions;
                if (i3 >= telephonyProto$TelephonyCallSessionArr2.length) {
                    break;
                }
                TelephonyProto$TelephonyCallSession telephonyProto$TelephonyCallSession = telephonyProto$TelephonyCallSessionArr2[i3];
                if (telephonyProto$TelephonyCallSession != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, telephonyProto$TelephonyCallSession);
                }
                i3++;
            }
        }
        TelephonyProto$SmsSession[] telephonyProto$SmsSessionArr = this.smsSessions;
        if (telephonyProto$SmsSessionArr != null && telephonyProto$SmsSessionArr.length > 0) {
            int i4 = 0;
            while (true) {
                TelephonyProto$SmsSession[] telephonyProto$SmsSessionArr2 = this.smsSessions;
                if (i4 >= telephonyProto$SmsSessionArr2.length) {
                    break;
                }
                TelephonyProto$SmsSession telephonyProto$SmsSession = telephonyProto$SmsSessionArr2[i4];
                if (telephonyProto$SmsSession != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, telephonyProto$SmsSession);
                }
                i4++;
            }
        }
        TelephonyProto$TelephonyHistogram[] telephonyProto$TelephonyHistogramArr = this.histograms;
        if (telephonyProto$TelephonyHistogramArr != null && telephonyProto$TelephonyHistogramArr.length > 0) {
            int i5 = 0;
            while (true) {
                TelephonyProto$TelephonyHistogram[] telephonyProto$TelephonyHistogramArr2 = this.histograms;
                if (i5 >= telephonyProto$TelephonyHistogramArr2.length) {
                    break;
                }
                TelephonyProto$TelephonyHistogram telephonyProto$TelephonyHistogram = telephonyProto$TelephonyHistogramArr2[i5];
                if (telephonyProto$TelephonyHistogram != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(4, telephonyProto$TelephonyHistogram);
                }
                i5++;
            }
        }
        boolean z = this.eventsDropped;
        if (z) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(5, z);
        }
        TelephonyProto$Time telephonyProto$Time = this.startTime;
        if (telephonyProto$Time != null) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(6, telephonyProto$Time);
        }
        TelephonyProto$Time telephonyProto$Time2 = this.endTime;
        if (telephonyProto$Time2 != null) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(7, telephonyProto$Time2);
        }
        TelephonyProto$ModemPowerStats telephonyProto$ModemPowerStats = this.modemPowerStats;
        if (telephonyProto$ModemPowerStats != null) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(8, telephonyProto$ModemPowerStats);
        }
        if (!this.hardwareRevision.equals(PhoneConfigurationManager.SSSS)) {
            computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(9, this.hardwareRevision);
        }
        TelephonyProto$ActiveSubscriptionInfo[] telephonyProto$ActiveSubscriptionInfoArr = this.lastActiveSubscriptionInfo;
        if (telephonyProto$ActiveSubscriptionInfoArr != null && telephonyProto$ActiveSubscriptionInfoArr.length > 0) {
            while (true) {
                TelephonyProto$ActiveSubscriptionInfo[] telephonyProto$ActiveSubscriptionInfoArr2 = this.lastActiveSubscriptionInfo;
                if (i >= telephonyProto$ActiveSubscriptionInfoArr2.length) {
                    break;
                }
                TelephonyProto$ActiveSubscriptionInfo telephonyProto$ActiveSubscriptionInfo = telephonyProto$ActiveSubscriptionInfoArr2[i];
                if (telephonyProto$ActiveSubscriptionInfo != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(10, telephonyProto$ActiveSubscriptionInfo);
                }
                i++;
            }
        }
        TelephonyProto$BandwidthEstimatorStats telephonyProto$BandwidthEstimatorStats = this.bandwidthEstimatorStats;
        return telephonyProto$BandwidthEstimatorStats != null ? computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(11, telephonyProto$BandwidthEstimatorStats) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public TelephonyProto$TelephonyLog mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            switch (readTag) {
                case 0:
                    return this;
                case 10:
                    int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 10);
                    TelephonyProto$TelephonyEvent[] telephonyProto$TelephonyEventArr = this.events;
                    int length = telephonyProto$TelephonyEventArr == null ? 0 : telephonyProto$TelephonyEventArr.length;
                    int i = repeatedFieldArrayLength + length;
                    TelephonyProto$TelephonyEvent[] telephonyProto$TelephonyEventArr2 = new TelephonyProto$TelephonyEvent[i];
                    if (length != 0) {
                        System.arraycopy(telephonyProto$TelephonyEventArr, 0, telephonyProto$TelephonyEventArr2, 0, length);
                    }
                    while (length < i - 1) {
                        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = new TelephonyProto$TelephonyEvent();
                        telephonyProto$TelephonyEventArr2[length] = telephonyProto$TelephonyEvent;
                        codedInputByteBufferNano.readMessage(telephonyProto$TelephonyEvent);
                        codedInputByteBufferNano.readTag();
                        length++;
                    }
                    TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent2 = new TelephonyProto$TelephonyEvent();
                    telephonyProto$TelephonyEventArr2[length] = telephonyProto$TelephonyEvent2;
                    codedInputByteBufferNano.readMessage(telephonyProto$TelephonyEvent2);
                    this.events = telephonyProto$TelephonyEventArr2;
                    break;
                case 18:
                    int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 18);
                    TelephonyProto$TelephonyCallSession[] telephonyProto$TelephonyCallSessionArr = this.callSessions;
                    int length2 = telephonyProto$TelephonyCallSessionArr == null ? 0 : telephonyProto$TelephonyCallSessionArr.length;
                    int i2 = repeatedFieldArrayLength2 + length2;
                    TelephonyProto$TelephonyCallSession[] telephonyProto$TelephonyCallSessionArr2 = new TelephonyProto$TelephonyCallSession[i2];
                    if (length2 != 0) {
                        System.arraycopy(telephonyProto$TelephonyCallSessionArr, 0, telephonyProto$TelephonyCallSessionArr2, 0, length2);
                    }
                    while (length2 < i2 - 1) {
                        TelephonyProto$TelephonyCallSession telephonyProto$TelephonyCallSession = new TelephonyProto$TelephonyCallSession();
                        telephonyProto$TelephonyCallSessionArr2[length2] = telephonyProto$TelephonyCallSession;
                        codedInputByteBufferNano.readMessage(telephonyProto$TelephonyCallSession);
                        codedInputByteBufferNano.readTag();
                        length2++;
                    }
                    TelephonyProto$TelephonyCallSession telephonyProto$TelephonyCallSession2 = new TelephonyProto$TelephonyCallSession();
                    telephonyProto$TelephonyCallSessionArr2[length2] = telephonyProto$TelephonyCallSession2;
                    codedInputByteBufferNano.readMessage(telephonyProto$TelephonyCallSession2);
                    this.callSessions = telephonyProto$TelephonyCallSessionArr2;
                    break;
                case 26:
                    int repeatedFieldArrayLength3 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 26);
                    TelephonyProto$SmsSession[] telephonyProto$SmsSessionArr = this.smsSessions;
                    int length3 = telephonyProto$SmsSessionArr == null ? 0 : telephonyProto$SmsSessionArr.length;
                    int i3 = repeatedFieldArrayLength3 + length3;
                    TelephonyProto$SmsSession[] telephonyProto$SmsSessionArr2 = new TelephonyProto$SmsSession[i3];
                    if (length3 != 0) {
                        System.arraycopy(telephonyProto$SmsSessionArr, 0, telephonyProto$SmsSessionArr2, 0, length3);
                    }
                    while (length3 < i3 - 1) {
                        TelephonyProto$SmsSession telephonyProto$SmsSession = new TelephonyProto$SmsSession();
                        telephonyProto$SmsSessionArr2[length3] = telephonyProto$SmsSession;
                        codedInputByteBufferNano.readMessage(telephonyProto$SmsSession);
                        codedInputByteBufferNano.readTag();
                        length3++;
                    }
                    TelephonyProto$SmsSession telephonyProto$SmsSession2 = new TelephonyProto$SmsSession();
                    telephonyProto$SmsSessionArr2[length3] = telephonyProto$SmsSession2;
                    codedInputByteBufferNano.readMessage(telephonyProto$SmsSession2);
                    this.smsSessions = telephonyProto$SmsSessionArr2;
                    break;
                case 34:
                    int repeatedFieldArrayLength4 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 34);
                    TelephonyProto$TelephonyHistogram[] telephonyProto$TelephonyHistogramArr = this.histograms;
                    int length4 = telephonyProto$TelephonyHistogramArr == null ? 0 : telephonyProto$TelephonyHistogramArr.length;
                    int i4 = repeatedFieldArrayLength4 + length4;
                    TelephonyProto$TelephonyHistogram[] telephonyProto$TelephonyHistogramArr2 = new TelephonyProto$TelephonyHistogram[i4];
                    if (length4 != 0) {
                        System.arraycopy(telephonyProto$TelephonyHistogramArr, 0, telephonyProto$TelephonyHistogramArr2, 0, length4);
                    }
                    while (length4 < i4 - 1) {
                        TelephonyProto$TelephonyHistogram telephonyProto$TelephonyHistogram = new TelephonyProto$TelephonyHistogram();
                        telephonyProto$TelephonyHistogramArr2[length4] = telephonyProto$TelephonyHistogram;
                        codedInputByteBufferNano.readMessage(telephonyProto$TelephonyHistogram);
                        codedInputByteBufferNano.readTag();
                        length4++;
                    }
                    TelephonyProto$TelephonyHistogram telephonyProto$TelephonyHistogram2 = new TelephonyProto$TelephonyHistogram();
                    telephonyProto$TelephonyHistogramArr2[length4] = telephonyProto$TelephonyHistogram2;
                    codedInputByteBufferNano.readMessage(telephonyProto$TelephonyHistogram2);
                    this.histograms = telephonyProto$TelephonyHistogramArr2;
                    break;
                case 40:
                    this.eventsDropped = codedInputByteBufferNano.readBool();
                    break;
                case 50:
                    if (this.startTime == null) {
                        this.startTime = new TelephonyProto$Time();
                    }
                    codedInputByteBufferNano.readMessage(this.startTime);
                    break;
                case 58:
                    if (this.endTime == null) {
                        this.endTime = new TelephonyProto$Time();
                    }
                    codedInputByteBufferNano.readMessage(this.endTime);
                    break;
                case 66:
                    if (this.modemPowerStats == null) {
                        this.modemPowerStats = new TelephonyProto$ModemPowerStats();
                    }
                    codedInputByteBufferNano.readMessage(this.modemPowerStats);
                    break;
                case RadioNVItems.RIL_NV_LTE_SCAN_PRIORITY_25 /* 74 */:
                    this.hardwareRevision = codedInputByteBufferNano.readString();
                    break;
                case RadioNVItems.RIL_NV_LTE_BSR_MAX_TIME /* 82 */:
                    int repeatedFieldArrayLength5 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 82);
                    TelephonyProto$ActiveSubscriptionInfo[] telephonyProto$ActiveSubscriptionInfoArr = this.lastActiveSubscriptionInfo;
                    int length5 = telephonyProto$ActiveSubscriptionInfoArr == null ? 0 : telephonyProto$ActiveSubscriptionInfoArr.length;
                    int i5 = repeatedFieldArrayLength5 + length5;
                    TelephonyProto$ActiveSubscriptionInfo[] telephonyProto$ActiveSubscriptionInfoArr2 = new TelephonyProto$ActiveSubscriptionInfo[i5];
                    if (length5 != 0) {
                        System.arraycopy(telephonyProto$ActiveSubscriptionInfoArr, 0, telephonyProto$ActiveSubscriptionInfoArr2, 0, length5);
                    }
                    while (length5 < i5 - 1) {
                        TelephonyProto$ActiveSubscriptionInfo telephonyProto$ActiveSubscriptionInfo = new TelephonyProto$ActiveSubscriptionInfo();
                        telephonyProto$ActiveSubscriptionInfoArr2[length5] = telephonyProto$ActiveSubscriptionInfo;
                        codedInputByteBufferNano.readMessage(telephonyProto$ActiveSubscriptionInfo);
                        codedInputByteBufferNano.readTag();
                        length5++;
                    }
                    TelephonyProto$ActiveSubscriptionInfo telephonyProto$ActiveSubscriptionInfo2 = new TelephonyProto$ActiveSubscriptionInfo();
                    telephonyProto$ActiveSubscriptionInfoArr2[length5] = telephonyProto$ActiveSubscriptionInfo2;
                    codedInputByteBufferNano.readMessage(telephonyProto$ActiveSubscriptionInfo2);
                    this.lastActiveSubscriptionInfo = telephonyProto$ActiveSubscriptionInfoArr2;
                    break;
                case 90:
                    if (this.bandwidthEstimatorStats == null) {
                        this.bandwidthEstimatorStats = new TelephonyProto$BandwidthEstimatorStats();
                    }
                    codedInputByteBufferNano.readMessage(this.bandwidthEstimatorStats);
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

    public static TelephonyProto$TelephonyLog parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (TelephonyProto$TelephonyLog) MessageNano.mergeFrom(new TelephonyProto$TelephonyLog(), bArr);
    }

    public static TelephonyProto$TelephonyLog parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new TelephonyProto$TelephonyLog().mergeFrom(codedInputByteBufferNano);
    }
}
