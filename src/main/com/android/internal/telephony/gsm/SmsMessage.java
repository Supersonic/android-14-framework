package com.android.internal.telephony.gsm;

import android.app.settings.SettingsEnums;
import android.content.res.Resources;
import android.drm.DrmManagerClient;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import com.android.internal.C4057R;
import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.telephony.EncodeException;
import com.android.internal.telephony.GsmAlphabet;
import com.android.internal.telephony.Sms7BitEncodingTranslator;
import com.android.internal.telephony.SmsAddress;
import com.android.internal.telephony.SmsConstants;
import com.android.internal.telephony.SmsHeader;
import com.android.internal.telephony.SmsMessageBase;
import com.android.internal.telephony.uicc.IccUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.net.module.util.NetworkStackConstants;
import com.android.telephony.Rlog;
import com.google.android.mms.pdu.CharacterSets;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Iterator;
/* loaded from: classes3.dex */
public class SmsMessage extends SmsMessageBase {
    private static final int INVALID_VALIDITY_PERIOD = -1;
    static final String LOG_TAG = "SmsMessage";
    private static final int VALIDITY_PERIOD_FORMAT_ABSOLUTE = 3;
    private static final int VALIDITY_PERIOD_FORMAT_ENHANCED = 1;
    private static final int VALIDITY_PERIOD_FORMAT_NONE = 0;
    private static final int VALIDITY_PERIOD_FORMAT_RELATIVE = 2;
    private static final int VALIDITY_PERIOD_MAX = 635040;
    private static final int VALIDITY_PERIOD_MIN = 5;
    private static final boolean VDBG = false;
    private int mDataCodingScheme;
    private int mMti;
    private int mProtocolIdentifier;
    private int mStatus;
    private SmsConstants.MessageClass messageClass;
    private boolean mReplyPathPresent = false;
    private boolean mIsStatusReportMessage = false;
    private int mVoiceMailCount = 0;

    /* loaded from: classes3.dex */
    public static class SubmitPdu extends SmsMessageBase.SubmitPduBase {
    }

    public static SmsMessage createFromPdu(byte[] pdu) {
        try {
            SmsMessage msg = new SmsMessage();
            msg.parsePdu(pdu);
            return msg;
        } catch (OutOfMemoryError e) {
            Rlog.m7e(LOG_TAG, "SMS PDU parsing failed with out of memory: ", e);
            return null;
        } catch (RuntimeException ex) {
            Rlog.m7e(LOG_TAG, "SMS PDU parsing failed: ", ex);
            return null;
        }
    }

    public boolean isTypeZero() {
        return this.mProtocolIdentifier == 64;
    }

    public static SmsMessage createFromEfRecord(int index, byte[] data) {
        try {
            SmsMessage msg = new SmsMessage();
            msg.mIndexOnIcc = index;
            if ((data[0] & 1) == 0) {
                Rlog.m2w(LOG_TAG, "SMS parsing failed: Trying to parse a free record");
                return null;
            }
            msg.mStatusOnIcc = data[0] & 7;
            int size = data.length - 1;
            byte[] pdu = new byte[size];
            System.arraycopy(data, 1, pdu, 0, size);
            msg.parsePdu(pdu);
            return msg;
        } catch (RuntimeException ex) {
            Rlog.m7e(LOG_TAG, "SMS PDU parsing failed: ", ex);
            return null;
        }
    }

    public static int getTPLayerLengthForPDU(String pdu) {
        int len = pdu.length() / 2;
        int smscLen = Integer.parseInt(pdu.substring(0, 2), 16);
        return (len - smscLen) - 1;
    }

    public static int getRelativeValidityPeriod(int validityPeriod) {
        if (validityPeriod < 5) {
            return -1;
        }
        if (validityPeriod <= 720) {
            int relValidityPeriod = (validityPeriod / 5) - 1;
            return relValidityPeriod;
        } else if (validityPeriod <= 1440) {
            int relValidityPeriod2 = ((validityPeriod - 720) / 30) + 143;
            return relValidityPeriod2;
        } else if (validityPeriod <= 43200) {
            int relValidityPeriod3 = (validityPeriod / MetricsProto.MetricsEvent.ACTION_HUSH_GESTURE) + 166;
            return relValidityPeriod3;
        } else if (validityPeriod > VALIDITY_PERIOD_MAX) {
            return -1;
        } else {
            int relValidityPeriod4 = (validityPeriod / FrameworkStatsLog.SETTING_SNAPSHOT) + 192;
            return relValidityPeriod4;
        }
    }

    public static SubmitPdu getSubmitPdu(String scAddress, String destinationAddress, String message, boolean statusReportRequested, byte[] header) {
        return getSubmitPdu(scAddress, destinationAddress, message, statusReportRequested, header, 0, 0, 0);
    }

    public static SubmitPdu getSubmitPdu(String scAddress, String destinationAddress, String message, boolean statusReportRequested, byte[] header, int encoding, int languageTable, int languageShiftTable) {
        return getSubmitPdu(scAddress, destinationAddress, message, statusReportRequested, header, encoding, languageTable, languageShiftTable, -1, 0);
    }

    public static SubmitPdu getSubmitPdu(String scAddress, String destinationAddress, String message, boolean statusReportRequested, byte[] header, int encoding, int languageTable, int languageShiftTable, int validityPeriod) {
        return getSubmitPdu(scAddress, destinationAddress, message, statusReportRequested, header, encoding, languageTable, languageShiftTable, validityPeriod, 0);
    }

    public static SubmitPdu getSubmitPdu(String scAddress, String destinationAddress, String message, boolean statusReportRequested, byte[] header, int encoding, int languageTable, int languageShiftTable, int validityPeriod, int messageRef) {
        byte[] header2;
        int encoding2;
        int languageTable2;
        int languageShiftTable2;
        byte mtiByte;
        byte[] userData;
        byte[] header3;
        if (message == null || destinationAddress == null) {
            return null;
        }
        if (encoding != 0) {
            header2 = header;
            encoding2 = encoding;
            languageTable2 = languageTable;
            languageShiftTable2 = languageShiftTable;
        } else {
            GsmAlphabet.TextEncodingDetails ted = calculateLength(message, false);
            int encoding3 = ted.codeUnitSize;
            int languageTable3 = ted.languageTable;
            int languageShiftTable3 = ted.languageShiftTable;
            if (encoding3 == 1 && (languageTable3 != 0 || languageShiftTable3 != 0)) {
                if (header != null) {
                    SmsHeader smsHeader = SmsHeader.fromByteArray(header);
                    if (smsHeader.languageTable == languageTable3 && smsHeader.languageShiftTable == languageShiftTable3) {
                        header3 = header;
                    } else {
                        Rlog.m2w(LOG_TAG, "Updating language table in SMS header: " + smsHeader.languageTable + " -> " + languageTable3 + ", " + smsHeader.languageShiftTable + " -> " + languageShiftTable3);
                        smsHeader.languageTable = languageTable3;
                        smsHeader.languageShiftTable = languageShiftTable3;
                        header3 = SmsHeader.toByteArray(smsHeader);
                    }
                    encoding2 = encoding3;
                    languageTable2 = languageTable3;
                    languageShiftTable2 = languageShiftTable3;
                    header2 = header3;
                } else {
                    SmsHeader smsHeader2 = new SmsHeader();
                    smsHeader2.languageTable = languageTable3;
                    smsHeader2.languageShiftTable = languageShiftTable3;
                    encoding2 = encoding3;
                    languageTable2 = languageTable3;
                    languageShiftTable2 = languageShiftTable3;
                    header2 = SmsHeader.toByteArray(smsHeader2);
                }
            } else {
                header2 = header;
                encoding2 = encoding3;
                languageTable2 = languageTable3;
                languageShiftTable2 = languageShiftTable3;
            }
        }
        SubmitPdu ret = new SubmitPdu();
        int relativeValidityPeriod = getRelativeValidityPeriod(validityPeriod);
        byte mtiByte2 = 1;
        if (header2 != null) {
            mtiByte2 = (byte) (1 | 64);
        }
        if (relativeValidityPeriod == -1) {
            mtiByte = mtiByte2;
        } else {
            mtiByte = (byte) (mtiByte2 | 16);
        }
        ByteArrayOutputStream bo = getSubmitPduHead(scAddress, destinationAddress, mtiByte, statusReportRequested, ret, messageRef);
        if (bo == null) {
            return ret;
        }
        try {
            if (encoding2 == 1) {
                userData = GsmAlphabet.stringToGsm7BitPackedWithHeader(message, header2, languageTable2, languageShiftTable2);
            } else {
                try {
                    userData = encodeUCS2(message, header2);
                } catch (UnsupportedEncodingException uex) {
                    Rlog.m7e(LOG_TAG, "Implausible UnsupportedEncodingException ", uex);
                    return null;
                }
            }
        } catch (EncodeException e) {
            if (e.getError() == 1) {
                Rlog.m7e(LOG_TAG, "Exceed size limitation EncodeException", e);
                return null;
            }
            try {
                userData = encodeUCS2(message, header2);
                encoding2 = 3;
            } catch (EncodeException ex1) {
                Rlog.m7e(LOG_TAG, "Exceed size limitation EncodeException", ex1);
                return null;
            } catch (UnsupportedEncodingException uex2) {
                Rlog.m7e(LOG_TAG, "Implausible UnsupportedEncodingException ", uex2);
                return null;
            }
        }
        if (encoding2 == 1) {
            if ((userData[0] & 255) > 160) {
                Rlog.m8e(LOG_TAG, "Message too long (" + (userData[0] & 255) + " septets)");
                return null;
            }
            bo.write(0);
        } else if ((userData[0] & 255) > 140) {
            Rlog.m8e(LOG_TAG, "Message too long (" + (userData[0] & 255) + " bytes)");
            return null;
        } else {
            bo.write(8);
        }
        if (relativeValidityPeriod != -1) {
            bo.write(relativeValidityPeriod);
        }
        bo.write(userData, 0, userData.length);
        ret.encodedMessage = bo.toByteArray();
        return ret;
    }

    private static byte[] encodeUCS2(String message, byte[] header) throws UnsupportedEncodingException, EncodeException {
        byte[] userData;
        byte[] textPart = message.getBytes("utf-16be");
        if (header != null) {
            userData = new byte[header.length + textPart.length + 1];
            userData[0] = (byte) header.length;
            System.arraycopy(header, 0, userData, 1, header.length);
            System.arraycopy(textPart, 0, userData, header.length + 1, textPart.length);
        } else {
            userData = textPart;
        }
        if (userData.length > 255) {
            throw new EncodeException("Payload cannot exceed 255 bytes", 1);
        }
        byte[] ret = new byte[userData.length + 1];
        ret[0] = (byte) (255 & userData.length);
        System.arraycopy(userData, 0, ret, 1, userData.length);
        return ret;
    }

    public static SubmitPdu getSubmitPdu(String scAddress, String destinationAddress, String message, boolean statusReportRequested) {
        return getSubmitPdu(scAddress, destinationAddress, message, statusReportRequested, (byte[]) null);
    }

    public static SubmitPdu getSubmitPdu(String scAddress, String destinationAddress, String message, boolean statusReportRequested, int validityPeriod) {
        return getSubmitPdu(scAddress, destinationAddress, message, statusReportRequested, null, 0, 0, 0, validityPeriod, 0);
    }

    public static SubmitPdu getSubmitPdu(String scAddress, String destinationAddress, int destinationPort, byte[] data, boolean statusReportRequested, int messageRef) {
        SmsHeader.PortAddrs portAddrs = new SmsHeader.PortAddrs();
        portAddrs.destPort = destinationPort;
        portAddrs.origPort = 0;
        portAddrs.areEightBits = false;
        SmsHeader smsHeader = new SmsHeader();
        smsHeader.portAddrs = portAddrs;
        byte[] smsHeaderData = SmsHeader.toByteArray(smsHeader);
        if (data.length + smsHeaderData.length + 1 > 140) {
            Rlog.m8e(LOG_TAG, "SMS data message may only contain " + ((140 - smsHeaderData.length) - 1) + " bytes");
            return null;
        }
        SubmitPdu ret = new SubmitPdu();
        ByteArrayOutputStream bo = getSubmitPduHead(scAddress, destinationAddress, (byte) 65, statusReportRequested, ret, messageRef);
        if (bo == null) {
            return ret;
        }
        bo.write(4);
        bo.write(data.length + smsHeaderData.length + 1);
        bo.write(smsHeaderData.length);
        bo.write(smsHeaderData, 0, smsHeaderData.length);
        bo.write(data, 0, data.length);
        ret.encodedMessage = bo.toByteArray();
        return ret;
    }

    public static SubmitPdu getSubmitPdu(String scAddress, String destinationAddress, int destinationPort, byte[] data, boolean statusReportRequested) {
        return getSubmitPdu(scAddress, destinationAddress, destinationPort, data, statusReportRequested, 0);
    }

    private static ByteArrayOutputStream getSubmitPduHead(String scAddress, String destinationAddress, byte mtiByte, boolean statusReportRequested, SubmitPdu ret) {
        return getSubmitPduHead(scAddress, destinationAddress, mtiByte, statusReportRequested, ret, 0);
    }

    private static ByteArrayOutputStream getSubmitPduHead(String scAddress, String destinationAddress, byte mtiByte, boolean statusReportRequested, SubmitPdu ret, int messageRef) {
        ByteArrayOutputStream bo = new ByteArrayOutputStream(180);
        if (scAddress == null) {
            ret.encodedScAddress = null;
        } else {
            ret.encodedScAddress = PhoneNumberUtils.networkPortionToCalledPartyBCDWithLength(scAddress);
        }
        if (statusReportRequested) {
            mtiByte = (byte) (mtiByte | NetworkStackConstants.TCPHDR_URG);
        }
        bo.write(mtiByte);
        bo.write(messageRef);
        byte[] daBytes = PhoneNumberUtils.networkPortionToCalledPartyBCD(destinationAddress);
        if (daBytes == null) {
            return null;
        }
        bo.write(((daBytes.length - 1) * 2) - ((daBytes[daBytes.length - 1] & 240) != 240 ? 0 : 1));
        bo.write(daBytes, 0, daBytes.length);
        bo.write(0);
        return bo;
    }

    public static SubmitPdu getDeliverPdu(String scAddress, String originatingAddress, String message, long date) {
        byte[] header;
        int i;
        byte[] userData;
        int i2;
        if (originatingAddress == null || message == null) {
            return null;
        }
        GsmAlphabet.TextEncodingDetails ted = calculateLength(message, false);
        int encoding = ted.codeUnitSize;
        int languageTable = ted.languageTable;
        int languageShiftTable = ted.languageShiftTable;
        if (encoding == 1 && (languageTable != 0 || languageShiftTable != 0)) {
            SmsHeader smsHeader = new SmsHeader();
            smsHeader.languageTable = languageTable;
            smsHeader.languageShiftTable = languageShiftTable;
            byte[] header2 = SmsHeader.toByteArray(smsHeader);
            header = header2;
        } else {
            header = null;
        }
        SubmitPdu ret = new SubmitPdu();
        ByteArrayOutputStream bo = new ByteArrayOutputStream(180);
        if (scAddress == null) {
            ret.encodedScAddress = null;
        } else {
            ret.encodedScAddress = PhoneNumberUtils.networkPortionToCalledPartyBCDWithLength(scAddress);
        }
        bo.write(0);
        byte[] oaBytes = PhoneNumberUtils.networkPortionToCalledPartyBCD(originatingAddress);
        if (oaBytes == null) {
            return null;
        }
        int length = (oaBytes.length - 1) * 2;
        if ((oaBytes[oaBytes.length - 1] & 240) != 240) {
            i = 0;
        } else {
            i = 1;
        }
        bo.write(length - i);
        bo.write(oaBytes, 0, oaBytes.length);
        bo.write(0);
        try {
            if (encoding == 1) {
                userData = GsmAlphabet.stringToGsm7BitPackedWithHeader(message, header, languageTable, languageShiftTable);
            } else {
                try {
                    userData = encodeUCS2(message, header);
                } catch (UnsupportedEncodingException uex) {
                    Rlog.m7e(LOG_TAG, "Implausible UnsupportedEncodingException ", uex);
                    return null;
                }
            }
        } catch (EncodeException e) {
            if (e.getError() == 1) {
                Rlog.m7e(LOG_TAG, "Exceed size limitation EncodeException", e);
                return null;
            }
            try {
                userData = encodeUCS2(message, header);
                encoding = 3;
            } catch (EncodeException ex1) {
                Rlog.m7e(LOG_TAG, "Exceed size limitation EncodeException", ex1);
                return null;
            } catch (UnsupportedEncodingException uex2) {
                Rlog.m7e(LOG_TAG, "Implausible UnsupportedEncodingException ", uex2);
                return null;
            }
        }
        if (encoding == 1) {
            if ((userData[0] & 255) > 160) {
                Rlog.m8e(LOG_TAG, "Message too long (" + (userData[0] & 255) + " septets)");
                return null;
            }
            bo.write(0);
        } else if ((userData[0] & 255) > 140) {
            Rlog.m8e(LOG_TAG, "Message too long (" + (userData[0] & 255) + " bytes)");
            return null;
        } else {
            bo.write(8);
        }
        byte[] scts = new byte[7];
        ZonedDateTime zoneDateTime = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault());
        LocalDateTime localDateTime = zoneDateTime.toLocalDateTime();
        int timezoneOffset = (zoneDateTime.getOffset().getTotalSeconds() / 60) / 15;
        boolean negativeOffset = timezoneOffset < 0;
        if (negativeOffset) {
            timezoneOffset = -timezoneOffset;
        }
        int year = localDateTime.getYear();
        int month = localDateTime.getMonthValue();
        int day = localDateTime.getDayOfMonth();
        int hour = localDateTime.getHour();
        int minute = localDateTime.getMinute();
        int second = localDateTime.getSecond();
        int year2 = year > 2000 ? year + DrmManagerClient.ERROR_UNKNOWN : year - 1900;
        scts[0] = (byte) ((((year2 % 10) & 15) << 4) | ((year2 / 10) & 15));
        scts[1] = (byte) ((((month % 10) & 15) << 4) | ((month / 10) & 15));
        scts[2] = (byte) ((((day % 10) & 15) << 4) | ((day / 10) & 15));
        scts[3] = (byte) ((((hour % 10) & 15) << 4) | ((hour / 10) & 15));
        scts[4] = (byte) ((((minute % 10) & 15) << 4) | ((minute / 10) & 15));
        scts[5] = (byte) ((((second % 10) & 15) << 4) | ((second / 10) & 15));
        scts[6] = (byte) ((((timezoneOffset % 10) & 15) << 4) | ((timezoneOffset / 10) & 15));
        if (negativeOffset) {
            i2 = 0;
            scts[0] = (byte) (scts[0] | 8);
        } else {
            i2 = 0;
        }
        bo.write(scts, i2, scts.length);
        bo.write(userData, i2, userData.length);
        ret.encodedMessage = bo.toByteArray();
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class PduParser {
        byte[] mPdu;
        byte[] mUserData;
        SmsHeader mUserDataHeader;
        int mCur = 0;
        int mUserDataSeptetPadding = 0;

        PduParser(byte[] pdu) {
            this.mPdu = pdu;
        }

        String getSCAddress() {
            String ret;
            int len = getByte();
            if (len == 0) {
                ret = null;
            } else {
                try {
                    ret = PhoneNumberUtils.calledPartyBCDToString(this.mPdu, this.mCur, len, 2);
                } catch (RuntimeException tr) {
                    Rlog.m9d(SmsMessage.LOG_TAG, "invalid SC address: ", tr);
                    ret = null;
                }
            }
            this.mCur += len;
            return ret;
        }

        int getByte() {
            byte[] bArr = this.mPdu;
            int i = this.mCur;
            this.mCur = i + 1;
            return bArr[i] & 255;
        }

        GsmSmsAddress getAddress() {
            byte[] bArr = this.mPdu;
            int i = this.mCur;
            int addressLength = bArr[i] & 255;
            int lengthBytes = ((addressLength + 1) / 2) + 2;
            try {
                GsmSmsAddress ret = new GsmSmsAddress(bArr, i, lengthBytes);
                this.mCur += lengthBytes;
                return ret;
            } catch (ParseException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        long getSCTimestampMillis() {
            byte[] bArr = this.mPdu;
            int i = this.mCur;
            this.mCur = i + 1;
            int year = IccUtils.gsmBcdByteToInt(bArr[i]);
            byte[] bArr2 = this.mPdu;
            int i2 = this.mCur;
            this.mCur = i2 + 1;
            int month = IccUtils.gsmBcdByteToInt(bArr2[i2]);
            byte[] bArr3 = this.mPdu;
            int i3 = this.mCur;
            this.mCur = i3 + 1;
            int day = IccUtils.gsmBcdByteToInt(bArr3[i3]);
            byte[] bArr4 = this.mPdu;
            int i4 = this.mCur;
            this.mCur = i4 + 1;
            int hour = IccUtils.gsmBcdByteToInt(bArr4[i4]);
            byte[] bArr5 = this.mPdu;
            int i5 = this.mCur;
            this.mCur = i5 + 1;
            int minute = IccUtils.gsmBcdByteToInt(bArr5[i5]);
            byte[] bArr6 = this.mPdu;
            int i6 = this.mCur;
            this.mCur = i6 + 1;
            int second = IccUtils.gsmBcdByteToInt(bArr6[i6]);
            byte[] bArr7 = this.mPdu;
            int i7 = this.mCur;
            this.mCur = i7 + 1;
            byte tzByte = bArr7[i7];
            int timezoneOffset = IccUtils.gsmBcdByteToInt((byte) (tzByte & (-9)));
            int timeZoneOffsetSeconds = ((tzByte & 8) == 0 ? timezoneOffset : -timezoneOffset) * 15 * 60;
            int fullYear = year >= 90 ? year + SettingsEnums.ACCESSIBILITY_MENU : year + 2000;
            try {
                LocalDateTime localDateTime = LocalDateTime.of(fullYear, month, day, hour, minute, second);
                long epochSeconds = localDateTime.toEpochSecond(ZoneOffset.UTC) - timeZoneOffsetSeconds;
                return 1000 * epochSeconds;
            } catch (DateTimeException ex) {
                Rlog.m7e(SmsMessage.LOG_TAG, "Invalid timestamp", ex);
                return 0L;
            }
        }

        int constructUserData(boolean hasUserDataHeader, boolean dataInSeptets) {
            int bufferLen;
            int offset = this.mCur;
            byte[] bArr = this.mPdu;
            int offset2 = offset + 1;
            int userDataLength = bArr[offset] & 255;
            int headerSeptets = 0;
            int userDataHeaderLength = 0;
            if (hasUserDataHeader) {
                int offset3 = offset2 + 1;
                userDataHeaderLength = bArr[offset2] & 255;
                byte[] udh = new byte[userDataHeaderLength];
                System.arraycopy(bArr, offset3, udh, 0, userDataHeaderLength);
                this.mUserDataHeader = SmsHeader.fromByteArray(udh);
                int offset4 = offset3 + userDataHeaderLength;
                int headerBits = (userDataHeaderLength + 1) * 8;
                int headerSeptets2 = headerBits / 7;
                headerSeptets = headerSeptets2 + (headerBits % 7 > 0 ? 1 : 0);
                this.mUserDataSeptetPadding = (headerSeptets * 7) - headerBits;
                offset2 = offset4;
            }
            if (dataInSeptets) {
                bufferLen = this.mPdu.length - offset2;
            } else {
                bufferLen = userDataLength - (hasUserDataHeader ? userDataHeaderLength + 1 : 0);
                if (bufferLen < 0) {
                    bufferLen = 0;
                }
            }
            byte[] bArr2 = new byte[bufferLen];
            this.mUserData = bArr2;
            System.arraycopy(this.mPdu, offset2, bArr2, 0, bArr2.length);
            this.mCur = offset2;
            if (dataInSeptets) {
                int count = userDataLength - headerSeptets;
                if (count < 0) {
                    return 0;
                }
                return count;
            }
            return this.mUserData.length;
        }

        byte[] getUserData() {
            return this.mUserData;
        }

        SmsHeader getUserDataHeader() {
            return this.mUserDataHeader;
        }

        String getUserDataGSM7Bit(int septetCount, int languageTable, int languageShiftTable) {
            String ret = GsmAlphabet.gsm7BitPackedToString(this.mPdu, this.mCur, septetCount, this.mUserDataSeptetPadding, languageTable, languageShiftTable);
            this.mCur += (septetCount * 7) / 8;
            return ret;
        }

        String getUserDataGSM8bit(int byteCount) {
            String ret = GsmAlphabet.gsm8BitUnpackedToString(this.mPdu, this.mCur, byteCount);
            this.mCur += byteCount;
            return ret;
        }

        String getUserDataUCS2(int byteCount) {
            String ret;
            try {
                ret = new String(this.mPdu, this.mCur, byteCount, CharacterSets.MIMENAME_UTF_16);
            } catch (UnsupportedEncodingException ex) {
                Rlog.m7e(SmsMessage.LOG_TAG, "implausible UnsupportedEncodingException", ex);
                ret = "";
            }
            this.mCur += byteCount;
            return ret;
        }

        String getUserDataKSC5601(int byteCount) {
            String ret;
            try {
                ret = new String(this.mPdu, this.mCur, byteCount, "KSC5601");
            } catch (UnsupportedEncodingException ex) {
                Rlog.m7e(SmsMessage.LOG_TAG, "implausible UnsupportedEncodingException", ex);
                ret = "";
            }
            this.mCur += byteCount;
            return ret;
        }

        boolean moreDataPresent() {
            return this.mPdu.length > this.mCur;
        }
    }

    public static GsmAlphabet.TextEncodingDetails calculateLength(CharSequence msgBody, boolean use7bitOnly) {
        CharSequence newMsgBody = null;
        Resources r = Resources.getSystem();
        if (r.getBoolean(C4057R.bool.config_sms_force_7bit_encoding)) {
            newMsgBody = Sms7BitEncodingTranslator.translate(msgBody, false);
        }
        if (TextUtils.isEmpty(newMsgBody)) {
            newMsgBody = msgBody;
        }
        GsmAlphabet.TextEncodingDetails ted = GsmAlphabet.countGsmSeptets(newMsgBody, use7bitOnly);
        if (ted == null) {
            return SmsMessageBase.calcUnicodeEncodingDetails(newMsgBody);
        }
        return ted;
    }

    @Override // com.android.internal.telephony.SmsMessageBase
    public int getProtocolIdentifier() {
        return this.mProtocolIdentifier;
    }

    int getDataCodingScheme() {
        return this.mDataCodingScheme;
    }

    @Override // com.android.internal.telephony.SmsMessageBase
    public boolean isReplace() {
        int i = this.mProtocolIdentifier;
        return (i & 192) == 64 && (i & 63) > 0 && (i & 63) < 8;
    }

    @Override // com.android.internal.telephony.SmsMessageBase
    public boolean isCphsMwiMessage() {
        return ((GsmSmsAddress) this.mOriginatingAddress).isCphsVoiceMessageClear() || ((GsmSmsAddress) this.mOriginatingAddress).isCphsVoiceMessageSet();
    }

    @Override // com.android.internal.telephony.SmsMessageBase
    public boolean isMWIClearMessage() {
        if (!this.mIsMwi || this.mMwiSense) {
            return this.mOriginatingAddress != null && ((GsmSmsAddress) this.mOriginatingAddress).isCphsVoiceMessageClear();
        }
        return true;
    }

    @Override // com.android.internal.telephony.SmsMessageBase
    public boolean isMWISetMessage() {
        if (this.mIsMwi && this.mMwiSense) {
            return true;
        }
        return this.mOriginatingAddress != null && ((GsmSmsAddress) this.mOriginatingAddress).isCphsVoiceMessageSet();
    }

    @Override // com.android.internal.telephony.SmsMessageBase
    public boolean isMwiDontStore() {
        if (this.mIsMwi && this.mMwiDontStore) {
            return true;
        }
        return isCphsMwiMessage() && " ".equals(getMessageBody());
    }

    @Override // com.android.internal.telephony.SmsMessageBase
    public int getStatus() {
        return this.mStatus;
    }

    @Override // com.android.internal.telephony.SmsMessageBase
    public boolean isStatusReportMessage() {
        return this.mIsStatusReportMessage;
    }

    @Override // com.android.internal.telephony.SmsMessageBase
    public boolean isReplyPathPresent() {
        return this.mReplyPathPresent;
    }

    private void parsePdu(byte[] pdu) {
        this.mPdu = pdu;
        PduParser p = new PduParser(pdu);
        this.mScAddress = p.getSCAddress();
        String str = this.mScAddress;
        int firstByte = p.getByte();
        int i = firstByte & 3;
        this.mMti = i;
        switch (i) {
            case 0:
            case 3:
                parseSmsDeliver(p, firstByte);
                return;
            case 1:
                parseSmsSubmit(p, firstByte);
                return;
            case 2:
                parseSmsStatusReport(p, firstByte);
                return;
            default:
                throw new RuntimeException("Unsupported message type");
        }
    }

    private void parseSmsStatusReport(PduParser p, int firstByte) {
        this.mIsStatusReportMessage = true;
        this.mMessageRef = p.getByte();
        this.mRecipientAddress = p.getAddress();
        this.mScTimeMillis = p.getSCTimestampMillis();
        p.getSCTimestampMillis();
        this.mStatus = p.getByte();
        if (p.moreDataPresent()) {
            int extraParams = p.getByte();
            int moreExtraParams = extraParams;
            while ((moreExtraParams & 128) != 0) {
                moreExtraParams = p.getByte();
            }
            if ((extraParams & 120) == 0) {
                if ((extraParams & 1) != 0) {
                    this.mProtocolIdentifier = p.getByte();
                }
                if ((extraParams & 2) != 0) {
                    this.mDataCodingScheme = p.getByte();
                }
                if ((extraParams & 4) != 0) {
                    boolean hasUserDataHeader = (firstByte & 64) == 64;
                    parseUserData(p, hasUserDataHeader);
                }
            }
        }
    }

    private void parseSmsDeliver(PduParser p, int firstByte) {
        this.mReplyPathPresent = (firstByte & 128) == 128;
        this.mOriginatingAddress = p.getAddress();
        SmsAddress smsAddress = this.mOriginatingAddress;
        this.mProtocolIdentifier = p.getByte();
        this.mDataCodingScheme = p.getByte();
        this.mScTimeMillis = p.getSCTimestampMillis();
        boolean hasUserDataHeader = (firstByte & 64) == 64;
        parseUserData(p, hasUserDataHeader);
    }

    private void parseSmsSubmit(PduParser p, int firstByte) {
        int validityPeriodLength;
        this.mReplyPathPresent = (firstByte & 128) == 128;
        this.mMessageRef = p.getByte();
        this.mRecipientAddress = p.getAddress();
        SmsAddress smsAddress = this.mRecipientAddress;
        this.mProtocolIdentifier = p.getByte();
        this.mDataCodingScheme = p.getByte();
        int validityPeriodFormat = (firstByte >> 3) & 3;
        if (validityPeriodFormat == 0) {
            validityPeriodLength = 0;
        } else if (validityPeriodFormat == 2) {
            validityPeriodLength = 1;
        } else {
            validityPeriodLength = 7;
        }
        while (true) {
            int validityPeriodLength2 = validityPeriodLength - 1;
            if (validityPeriodLength <= 0) {
                break;
            }
            p.getByte();
            validityPeriodLength = validityPeriodLength2;
        }
        boolean hasUserDataHeader = (firstByte & 64) == 64;
        parseUserData(p, hasUserDataHeader);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x01dc, code lost:
        if ((r15 & 240) != 224) goto L42;
     */
    /* JADX WARN: Code restructure failed: missing block: B:93:0x01e3, code lost:
        if ((r15 & 3) != 0) goto L42;
     */
    /* JADX WARN: Code restructure failed: missing block: B:94:0x01e5, code lost:
        r16.mMwiDontStore = true;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void parseUserData(PduParser p, boolean hasUserDataHeader) {
        int i;
        boolean hasMessageClass = false;
        int encodingType = 0;
        Resources r = Resources.getSystem();
        int i2 = this.mDataCodingScheme;
        boolean z = true;
        int i3 = 128;
        if ((i2 & 128) == 0) {
            boolean userDataCompressed = (i2 & 32) != 0;
            hasMessageClass = (i2 & 16) != 0;
            if (userDataCompressed) {
                Rlog.m2w(LOG_TAG, "4 - Unsupported SMS data coding scheme (compression) " + (this.mDataCodingScheme & 255));
            } else {
                switch ((i2 >> 2) & 3) {
                    case 0:
                        encodingType = 1;
                        break;
                    case 1:
                        if (r.getBoolean(C4057R.bool.config_sms_decode_gsm_8bit_data)) {
                            encodingType = 2;
                            break;
                        }
                        Rlog.m2w(LOG_TAG, "1 - Unsupported SMS data coding scheme " + (this.mDataCodingScheme & 255));
                        encodingType = r.getInteger(C4057R.integer.default_reserved_data_coding_scheme);
                        break;
                    case 2:
                        encodingType = 3;
                        break;
                    case 3:
                        Rlog.m2w(LOG_TAG, "1 - Unsupported SMS data coding scheme " + (this.mDataCodingScheme & 255));
                        encodingType = r.getInteger(C4057R.integer.default_reserved_data_coding_scheme);
                        break;
                }
            }
        } else if ((i2 & 240) == 240) {
            hasMessageClass = true;
            encodingType = (i2 & 4) == 0 ? 1 : 2;
        } else if ((i2 & 240) == 192 || (i2 & 240) == 208 || (i2 & 240) == 224) {
            if ((i2 & 240) == 224) {
                encodingType = 3;
            } else {
                encodingType = 1;
            }
            boolean active = (i2 & 8) == 8;
            if ((i2 & 3) != 0) {
                this.mIsMwi = false;
                Rlog.m2w(LOG_TAG, "MWI in DCS for fax/email/other: " + (this.mDataCodingScheme & 255));
            } else {
                this.mIsMwi = true;
                this.mMwiSense = active;
                this.mMwiDontStore = (this.mDataCodingScheme & 240) == 192;
                if (active) {
                    this.mVoiceMailCount = -1;
                } else {
                    this.mVoiceMailCount = 0;
                }
                Rlog.m2w(LOG_TAG, "MWI in DCS for Vmail. DCS = " + (this.mDataCodingScheme & 255) + " Dont store = " + this.mMwiDontStore + " vmail count = " + this.mVoiceMailCount);
            }
        } else if ((i2 & 192) != 128) {
            Rlog.m2w(LOG_TAG, "3 - Unsupported SMS data coding scheme " + (this.mDataCodingScheme & 255));
        } else if (i2 != 132) {
            Rlog.m2w(LOG_TAG, "5 - Unsupported SMS data coding scheme " + (this.mDataCodingScheme & 255));
        } else {
            encodingType = 4;
        }
        int count = p.constructUserData(hasUserDataHeader, encodingType == 1);
        this.mUserData = p.getUserData();
        this.mUserDataHeader = p.getUserDataHeader();
        this.mReceivedEncodingType = encodingType;
        if (!hasUserDataHeader || this.mUserDataHeader.specialSmsMsgList.size() == 0) {
            i = 0;
        } else {
            Iterator<SmsHeader.SpecialSmsMsg> it = this.mUserDataHeader.specialSmsMsgList.iterator();
            while (it.hasNext()) {
                SmsHeader.SpecialSmsMsg msg = it.next();
                int msgInd = msg.msgIndType & 255;
                if (msgInd == 0 || msgInd == i3) {
                    this.mIsMwi = z;
                    if (msgInd == 128) {
                        this.mMwiDontStore = false;
                    } else if (!this.mMwiDontStore) {
                        int i4 = this.mDataCodingScheme;
                        if ((i4 & 240) == 208) {
                        }
                    }
                    int i5 = msg.msgCount & 255;
                    this.mVoiceMailCount = i5;
                    if (i5 > 0) {
                        this.mMwiSense = true;
                    } else {
                        this.mMwiSense = false;
                    }
                    Rlog.m2w(LOG_TAG, "MWI in TP-UDH for Vmail. Msg Ind = " + msgInd + " Dont store = " + this.mMwiDontStore + " Vmail count = " + this.mVoiceMailCount);
                } else {
                    Rlog.m2w(LOG_TAG, "TP_UDH fax/email/extended msg/multisubscriber profile. Msg Ind = " + msgInd);
                }
                z = true;
                i3 = 128;
            }
            i = 0;
        }
        switch (encodingType) {
            case 0:
                this.mMessageBody = null;
                break;
            case 1:
                int i6 = hasUserDataHeader ? this.mUserDataHeader.languageTable : i;
                if (hasUserDataHeader) {
                    i = this.mUserDataHeader.languageShiftTable;
                }
                this.mMessageBody = p.getUserDataGSM7Bit(count, i6, i);
                break;
            case 2:
                if (r.getBoolean(C4057R.bool.config_sms_decode_gsm_8bit_data)) {
                    this.mMessageBody = p.getUserDataGSM8bit(count);
                    break;
                } else {
                    this.mMessageBody = null;
                    break;
                }
            case 3:
                this.mMessageBody = p.getUserDataUCS2(count);
                break;
            case 4:
                this.mMessageBody = p.getUserDataKSC5601(count);
                break;
        }
        if (this.mMessageBody != null) {
            parseMessageBody();
        }
        if (!hasMessageClass) {
            this.messageClass = SmsConstants.MessageClass.UNKNOWN;
            return;
        }
        switch (this.mDataCodingScheme & 3) {
            case 0:
                this.messageClass = SmsConstants.MessageClass.CLASS_0;
                return;
            case 1:
                this.messageClass = SmsConstants.MessageClass.CLASS_1;
                return;
            case 2:
                this.messageClass = SmsConstants.MessageClass.CLASS_2;
                return;
            case 3:
                this.messageClass = SmsConstants.MessageClass.CLASS_3;
                return;
            default:
                return;
        }
    }

    @Override // com.android.internal.telephony.SmsMessageBase
    public SmsConstants.MessageClass getMessageClass() {
        return this.messageClass;
    }

    boolean isUsimDataDownload() {
        int i;
        return this.messageClass == SmsConstants.MessageClass.CLASS_2 && ((i = this.mProtocolIdentifier) == 127 || i == 124);
    }

    public int getNumOfVoicemails() {
        if (!this.mIsMwi && isCphsMwiMessage()) {
            if (this.mOriginatingAddress != null && ((GsmSmsAddress) this.mOriginatingAddress).isCphsVoiceMessageSet()) {
                this.mVoiceMailCount = 255;
            } else {
                this.mVoiceMailCount = 0;
            }
            Rlog.m4v(LOG_TAG, "CPHS voice mail message");
        }
        return this.mVoiceMailCount;
    }
}
