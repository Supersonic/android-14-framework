package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Pair;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.InboundSmsHandler;
import com.android.telephony.Rlog;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
/* loaded from: classes.dex */
public class InboundSmsTracker {
    @VisibleForTesting
    public static final int DEST_PORT_FLAG_3GPP2 = 262144;
    @VisibleForTesting
    public static final int DEST_PORT_FLAG_3GPP2_WAP_PDU = 524288;
    @VisibleForTesting
    public static final int DEST_PORT_FLAG_NO_PORT = 65536;
    @VisibleForTesting
    public static final String SELECT_BY_REFERENCE = "address=? AND reference_number=? AND count=? AND (destination_port & 524288=0) AND deleted=0";
    @VisibleForTesting
    public static final String SELECT_BY_REFERENCE_3GPP2WAP = "address=? AND reference_number=? AND count=? AND (destination_port & 524288=524288) AND deleted=0";
    private final String mAddress;
    private String mDeleteWhere;
    private String[] mDeleteWhereArgs;
    private final int mDestPort;
    private final String mDisplayAddress;
    private final boolean mIs3gpp2;
    private final boolean mIs3gpp2WapPdu;
    private final boolean mIsClass0;
    private final String mMessageBody;
    private final int mMessageCount;
    private final long mMessageId;
    private final byte[] mPdu;
    private final int mReferenceNumber;
    private final int mSequenceNumber;
    private InboundSmsHandler.SmsBroadcastReceiver mSmsBroadcastReceiver;
    private final int mSmsSource;
    private final int mSubId;
    private final long mTimestamp;

    public static int getRealDestPort(int i) {
        if ((65536 & i) != 0) {
            return -1;
        }
        return i & 65535;
    }

    public InboundSmsTracker(Context context, byte[] bArr, long j, int i, boolean z, boolean z2, String str, String str2, String str3, boolean z3, int i2, int i3) {
        this.mPdu = bArr;
        this.mTimestamp = j;
        this.mDestPort = i;
        this.mIs3gpp2 = z;
        this.mIs3gpp2WapPdu = z2;
        this.mMessageBody = str3;
        this.mAddress = str;
        this.mDisplayAddress = str2;
        this.mIsClass0 = z3;
        this.mReferenceNumber = -1;
        this.mSequenceNumber = getIndexOffset();
        this.mMessageCount = 1;
        this.mSubId = i2;
        this.mMessageId = createMessageId(context, j, i2);
        this.mSmsSource = i3;
    }

    public InboundSmsTracker(Context context, byte[] bArr, long j, int i, boolean z, String str, String str2, int i2, int i3, int i4, boolean z2, String str3, boolean z3, int i5, int i6) {
        this.mPdu = bArr;
        this.mTimestamp = j;
        this.mDestPort = i;
        this.mIs3gpp2 = z;
        this.mIs3gpp2WapPdu = z2;
        this.mMessageBody = str3;
        this.mIsClass0 = z3;
        this.mDisplayAddress = str2;
        this.mAddress = str;
        this.mReferenceNumber = i2;
        this.mSequenceNumber = i3;
        this.mMessageCount = i4;
        this.mSubId = i5;
        this.mMessageId = createMessageId(context, j, i5);
        this.mSmsSource = i6;
    }

    public InboundSmsTracker(Context context, Cursor cursor, boolean z) {
        this.mPdu = HexDump.hexStringToByteArray(cursor.getString(0));
        this.mIsClass0 = false;
        if (cursor.isNull(2)) {
            this.mDestPort = -1;
            this.mIs3gpp2 = z;
            this.mIs3gpp2WapPdu = false;
        } else {
            int i = cursor.getInt(2);
            if ((131072 & i) != 0) {
                this.mIs3gpp2 = false;
            } else if ((262144 & i) != 0) {
                this.mIs3gpp2 = true;
            } else {
                this.mIs3gpp2 = z;
            }
            this.mIs3gpp2WapPdu = (524288 & i) != 0;
            this.mDestPort = getRealDestPort(i);
        }
        long j = cursor.getLong(3);
        this.mTimestamp = j;
        String string = cursor.getString(6);
        this.mAddress = string;
        this.mDisplayAddress = cursor.getString(9);
        int i2 = cursor.getInt(SmsBroadcastUndelivered.PDU_PENDING_MESSAGE_PROJECTION_INDEX_MAPPING.get(11).intValue());
        this.mSubId = i2;
        if (cursor.getInt(5) == 1) {
            long j2 = cursor.getLong(7);
            this.mReferenceNumber = -1;
            this.mSequenceNumber = getIndexOffset();
            this.mMessageCount = 1;
            this.mDeleteWhere = InboundSmsHandler.SELECT_BY_ID;
            this.mDeleteWhereArgs = new String[]{Long.toString(j2)};
        } else {
            int i3 = cursor.getInt(4);
            this.mReferenceNumber = i3;
            int i4 = cursor.getInt(5);
            this.mMessageCount = i4;
            int i5 = cursor.getInt(1);
            this.mSequenceNumber = i5;
            int indexOffset = i5 - getIndexOffset();
            if (indexOffset < 0 || indexOffset >= i4) {
                throw new IllegalArgumentException("invalid PDU sequence " + i5 + " of " + i4);
            }
            this.mDeleteWhere = getQueryForSegments();
            this.mDeleteWhereArgs = new String[]{string, Integer.toString(i3), Integer.toString(i4)};
        }
        this.mMessageBody = cursor.getString(8);
        this.mMessageId = createMessageId(context, j, i2);
        this.mSmsSource = 0;
    }

    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("pdu", HexDump.toHexString(this.mPdu));
        contentValues.put("date", Long.valueOf(this.mTimestamp));
        int i = this.mDestPort;
        int i2 = (i == -1 ? DEST_PORT_FLAG_NO_PORT : i & 65535) | (this.mIs3gpp2 ? DEST_PORT_FLAG_3GPP2 : 131072);
        if (this.mIs3gpp2WapPdu) {
            i2 |= DEST_PORT_FLAG_3GPP2_WAP_PDU;
        }
        contentValues.put("destination_port", Integer.valueOf(i2));
        String str = this.mAddress;
        if (str != null) {
            contentValues.put("address", str);
            contentValues.put("display_originating_addr", this.mDisplayAddress);
            contentValues.put("reference_number", Integer.valueOf(this.mReferenceNumber));
            contentValues.put("sequence", Integer.valueOf(this.mSequenceNumber));
        }
        contentValues.put("count", Integer.valueOf(this.mMessageCount));
        contentValues.put("message_body", this.mMessageBody);
        contentValues.put("sub_id", Integer.valueOf(this.mSubId));
        return contentValues;
    }

    public void setDeleteWhere(String str, String[] strArr) {
        this.mDeleteWhere = str;
        this.mDeleteWhereArgs = strArr;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("SmsTracker{timestamp=");
        sb.append(new Date(this.mTimestamp));
        sb.append(" destPort=");
        sb.append(this.mDestPort);
        sb.append(" is3gpp2=");
        sb.append(this.mIs3gpp2);
        sb.append(" display_originating_addr=");
        sb.append(this.mDisplayAddress);
        sb.append(" refNumber=");
        sb.append(this.mReferenceNumber);
        sb.append(" seqNumber=");
        sb.append(this.mSequenceNumber);
        sb.append(" msgCount=");
        sb.append(this.mMessageCount);
        if (this.mDeleteWhere != null) {
            sb.append(" deleteWhere(");
            sb.append(this.mDeleteWhere);
            sb.append(") deleteArgs=(");
            sb.append(Arrays.toString(this.mDeleteWhereArgs));
            sb.append(')');
        }
        sb.append(" ");
        sb.append(SmsController.formatCrossStackMessageId(this.mMessageId));
        sb.append("}");
        return sb.toString();
    }

    public byte[] getPdu() {
        return this.mPdu;
    }

    public long getTimestamp() {
        return this.mTimestamp;
    }

    public int getDestPort() {
        return this.mDestPort;
    }

    public boolean is3gpp2() {
        return this.mIs3gpp2;
    }

    public boolean isClass0() {
        return this.mIsClass0;
    }

    public int getSubId() {
        return this.mSubId;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public String getFormat() {
        return this.mIs3gpp2 ? "3gpp2" : "3gpp";
    }

    public String getQueryForSegments() {
        return this.mIs3gpp2WapPdu ? SELECT_BY_REFERENCE_3GPP2WAP : SELECT_BY_REFERENCE;
    }

    public Pair<String, String[]> getExactMatchDupDetectQuery() {
        return new Pair<>(addDestPortQuery("address=? AND reference_number=? AND count=? AND sequence=? AND date=? AND message_body=?"), new String[]{getAddress(), Integer.toString(getReferenceNumber()), Integer.toString(getMessageCount()), Integer.toString(getSequenceNumber()), Long.toString(getTimestamp()), getMessageBody()});
    }

    public Pair<String, String[]> getInexactMatchDupDetectQuery() {
        if (getMessageCount() == 1) {
            return null;
        }
        return new Pair<>(addDestPortQuery("address=? AND reference_number=? AND count=? AND sequence=? AND deleted=0"), new String[]{getAddress(), Integer.toString(getReferenceNumber()), Integer.toString(getMessageCount()), Integer.toString(getSequenceNumber())});
    }

    private String addDestPortQuery(String str) {
        String str2 = this.mIs3gpp2WapPdu ? "destination_port & 524288=524288" : "destination_port & 524288=0";
        return str + " AND (" + str2 + ")";
    }

    private static long createMessageId(Context context, long j, int i) {
        String imei = ((TelephonyManager) context.getSystemService("phone")).getImei(SubscriptionManager.getSlotIndex(i));
        if (TextUtils.isEmpty(imei)) {
            return 0L;
        }
        return getShaValue(imei + j);
    }

    private static long getShaValue(String str) {
        try {
            return ByteBuffer.wrap(getShaBytes(str, 8)).getLong();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            Rlog.e("InboundSmsTracker", "Exception while getting SHA value for message", e);
            return 0L;
        }
    }

    private static byte[] getShaBytes(String str, int i) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        messageDigest.reset();
        messageDigest.update(str.getBytes("UTF-8"));
        byte[] digest = messageDigest.digest();
        if (digest.length >= i) {
            byte[] bArr = new byte[i];
            System.arraycopy(digest, 0, bArr, 0, i);
            return bArr;
        }
        return digest;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int getIndexOffset() {
        return (this.mIs3gpp2 && this.mIs3gpp2WapPdu) ? 0 : 1;
    }

    public String getAddress() {
        return this.mAddress;
    }

    public String getDisplayAddress() {
        return this.mDisplayAddress;
    }

    public String getMessageBody() {
        return this.mMessageBody;
    }

    public int getReferenceNumber() {
        return this.mReferenceNumber;
    }

    public int getSequenceNumber() {
        return this.mSequenceNumber;
    }

    public int getMessageCount() {
        return this.mMessageCount;
    }

    public String getDeleteWhere() {
        return this.mDeleteWhere;
    }

    public String[] getDeleteWhereArgs() {
        return this.mDeleteWhereArgs;
    }

    public long getMessageId() {
        return this.mMessageId;
    }

    public int getSource() {
        return this.mSmsSource;
    }

    public InboundSmsHandler.SmsBroadcastReceiver getSmsBroadcastReceiver(InboundSmsHandler inboundSmsHandler) {
        if (this.mSmsBroadcastReceiver == null) {
            Objects.requireNonNull(inboundSmsHandler);
            this.mSmsBroadcastReceiver = new InboundSmsHandler.SmsBroadcastReceiver(this);
        }
        return this.mSmsBroadcastReceiver;
    }
}
