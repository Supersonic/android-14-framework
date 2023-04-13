package com.android.internal.telephony;

import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Patterns;
import com.android.internal.telephony.GsmAlphabet;
import com.android.internal.telephony.SmsConstants;
import java.text.BreakIterator;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes3.dex */
public abstract class SmsMessageBase {
    public static final Pattern NAME_ADDR_EMAIL_PATTERN = Pattern.compile("\\s*(\"[^\"]*\"|[^<>\"]+)\\s*<([^<>]+)>\\s*");
    protected String mEmailBody;
    protected String mEmailFrom;
    protected boolean mIsEmail;
    protected boolean mIsMwi;
    protected String mMessageBody;
    public int mMessageRef;
    protected boolean mMwiDontStore;
    protected boolean mMwiSense;
    protected SmsAddress mOriginatingAddress;
    protected byte[] mPdu;
    protected String mPseudoSubject;
    protected SmsAddress mRecipientAddress;
    protected String mScAddress;
    protected long mScTimeMillis;
    protected byte[] mUserData;
    protected SmsHeader mUserDataHeader;
    protected int mReceivedEncodingType = 0;
    protected int mStatusOnIcc = -1;
    protected int mIndexOnIcc = -1;

    public abstract SmsConstants.MessageClass getMessageClass();

    public abstract int getProtocolIdentifier();

    public abstract int getStatus();

    public abstract boolean isCphsMwiMessage();

    public abstract boolean isMWIClearMessage();

    public abstract boolean isMWISetMessage();

    public abstract boolean isMwiDontStore();

    public abstract boolean isReplace();

    public abstract boolean isReplyPathPresent();

    public abstract boolean isStatusReportMessage();

    /* loaded from: classes3.dex */
    public static abstract class SubmitPduBase {
        public byte[] encodedMessage;
        public byte[] encodedScAddress;

        public String toString() {
            return "SubmitPdu: encodedScAddress = " + Arrays.toString(this.encodedScAddress) + ", encodedMessage = " + Arrays.toString(this.encodedMessage);
        }
    }

    public String getServiceCenterAddress() {
        return this.mScAddress;
    }

    public String getOriginatingAddress() {
        SmsAddress smsAddress = this.mOriginatingAddress;
        if (smsAddress == null) {
            return null;
        }
        return smsAddress.getAddressString();
    }

    public String getDisplayOriginatingAddress() {
        if (this.mIsEmail) {
            return this.mEmailFrom;
        }
        return getOriginatingAddress();
    }

    public String getMessageBody() {
        return this.mMessageBody;
    }

    public String getDisplayMessageBody() {
        if (this.mIsEmail) {
            return this.mEmailBody;
        }
        return getMessageBody();
    }

    public String getPseudoSubject() {
        String str = this.mPseudoSubject;
        return str == null ? "" : str;
    }

    public long getTimestampMillis() {
        return this.mScTimeMillis;
    }

    public boolean isEmail() {
        return this.mIsEmail;
    }

    public String getEmailBody() {
        return this.mEmailBody;
    }

    public String getEmailFrom() {
        return this.mEmailFrom;
    }

    public byte[] getUserData() {
        return this.mUserData;
    }

    public SmsHeader getUserDataHeader() {
        return this.mUserDataHeader;
    }

    public byte[] getPdu() {
        return this.mPdu;
    }

    public int getStatusOnIcc() {
        return this.mStatusOnIcc;
    }

    public int getIndexOnIcc() {
        return this.mIndexOnIcc;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void parseMessageBody() {
        SmsAddress smsAddress = this.mOriginatingAddress;
        if (smsAddress != null && smsAddress.couldBeEmailGateway()) {
            extractEmailAddressFromMessageBody();
        }
    }

    private static String extractAddrSpec(String messageHeader) {
        Matcher match = NAME_ADDR_EMAIL_PATTERN.matcher(messageHeader);
        if (match.matches()) {
            return match.group(2);
        }
        return messageHeader;
    }

    public static boolean isEmailAddress(String messageHeader) {
        if (TextUtils.isEmpty(messageHeader)) {
            return false;
        }
        String s = extractAddrSpec(messageHeader);
        Matcher match = Patterns.EMAIL_ADDRESS.matcher(s);
        return match.matches();
    }

    protected void extractEmailAddressFromMessageBody() {
        String[] parts = this.mMessageBody.split("( /)|( )", 2);
        if (parts.length < 2) {
            return;
        }
        String str = parts[0];
        this.mEmailFrom = str;
        this.mEmailBody = parts[1];
        this.mIsEmail = isEmailAddress(str);
    }

    /* JADX WARN: Code restructure failed: missing block: B:25:?, code lost:
        return r2;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static int findNextUnicodePosition(int currentPosition, int byteLimit, CharSequence msgBody) {
        int nextPos = Math.min((byteLimit / 2) + currentPosition, msgBody.length());
        if (nextPos < msgBody.length()) {
            BreakIterator breakIterator = BreakIterator.getCharacterInstance();
            breakIterator.setText(msgBody.toString());
            if (!breakIterator.isBoundary(nextPos)) {
                int breakPos = breakIterator.preceding(nextPos);
                while (breakPos + 4 <= nextPos && isRegionalIndicatorSymbol(Character.codePointAt(msgBody, breakPos)) && isRegionalIndicatorSymbol(Character.codePointAt(msgBody, breakPos + 2))) {
                    breakPos += 4;
                }
                if (Character.isHighSurrogate(msgBody.charAt(nextPos - 1))) {
                    return nextPos - 1;
                }
                return nextPos;
            }
            return nextPos;
        }
        return nextPos;
    }

    private static boolean isRegionalIndicatorSymbol(int codePoint) {
        return 127462 <= codePoint && codePoint <= 127487;
    }

    public static GsmAlphabet.TextEncodingDetails calcUnicodeEncodingDetails(CharSequence msgBody) {
        GsmAlphabet.TextEncodingDetails ted = new GsmAlphabet.TextEncodingDetails();
        int octets = msgBody.length() * 2;
        ted.codeUnitSize = 3;
        ted.codeUnitCount = msgBody.length();
        if (octets > 140) {
            int maxUserDataBytesWithHeader = 134;
            if (!SmsMessage.hasEmsSupport() && octets <= (134 - 2) * 9) {
                maxUserDataBytesWithHeader = 134 - 2;
            }
            int pos = 0;
            int msgCount = 0;
            while (pos < msgBody.length()) {
                int nextPos = findNextUnicodePosition(pos, maxUserDataBytesWithHeader, msgBody);
                if (nextPos == msgBody.length()) {
                    ted.codeUnitsRemaining = ((maxUserDataBytesWithHeader / 2) + pos) - msgBody.length();
                }
                pos = nextPos;
                msgCount++;
            }
            ted.msgCount = msgCount;
        } else {
            ted.msgCount = 1;
            ted.codeUnitsRemaining = (140 - octets) / 2;
        }
        return ted;
    }

    public String getRecipientAddress() {
        SmsAddress smsAddress = this.mRecipientAddress;
        if (smsAddress == null) {
            return null;
        }
        return smsAddress.getAddressString();
    }

    public int getReceivedEncodingType() {
        return this.mReceivedEncodingType;
    }
}
