package com.google.android.mms.pdu;

import android.content.ContentResolver;
import android.content.Context;
import android.text.TextUtils;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
/* loaded from: classes5.dex */
public class PduComposer {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int END_STRING_FLAG = 0;
    private static final int LENGTH_QUOTE = 31;
    private static final int LONG_INTEGER_LENGTH_MAX = 8;
    private static final int PDU_COMPOSER_BLOCK_SIZE = 1024;
    private static final int PDU_COMPOSE_CONTENT_ERROR = 1;
    private static final int PDU_COMPOSE_FIELD_NOT_SET = 2;
    private static final int PDU_COMPOSE_FIELD_NOT_SUPPORTED = 3;
    private static final int PDU_COMPOSE_SUCCESS = 0;
    private static final int PDU_EMAIL_ADDRESS_TYPE = 2;
    private static final int PDU_IPV4_ADDRESS_TYPE = 3;
    private static final int PDU_IPV6_ADDRESS_TYPE = 4;
    private static final int PDU_PHONE_NUMBER_ADDRESS_TYPE = 1;
    private static final int PDU_UNKNOWN_ADDRESS_TYPE = 5;
    private static final int QUOTED_STRING_FLAG = 34;
    static final String REGEXP_EMAIL_ADDRESS_TYPE = "[a-zA-Z| ]*\\<{0,1}[a-zA-Z| ]+@{1}[a-zA-Z| ]+\\.{1}[a-zA-Z| ]+\\>{0,1}";
    static final String REGEXP_IPV4_ADDRESS_TYPE = "[0-9]{1,3}\\.{1}[0-9]{1,3}\\.{1}[0-9]{1,3}\\.{1}[0-9]{1,3}";
    static final String REGEXP_IPV6_ADDRESS_TYPE = "[a-fA-F]{4}\\:{1}[a-fA-F0-9]{4}\\:{1}[a-fA-F0-9]{4}\\:{1}[a-fA-F0-9]{4}\\:{1}[a-fA-F0-9]{4}\\:{1}[a-fA-F0-9]{4}\\:{1}[a-fA-F0-9]{4}\\:{1}[a-fA-F0-9]{4}";
    static final String REGEXP_PHONE_NUMBER_ADDRESS_TYPE = "\\+?[0-9|\\.|\\-]+";
    private static final int SHORT_INTEGER_MAX = 127;
    static final String STRING_IPV4_ADDRESS_TYPE = "/TYPE=IPV4";
    static final String STRING_IPV6_ADDRESS_TYPE = "/TYPE=IPV6";
    static final String STRING_PHONE_NUMBER_ADDRESS_TYPE = "/TYPE=PLMN";
    private static final int TEXT_MAX = 127;
    private static HashMap<String, Integer> mContentTypeMap;
    protected ByteArrayOutputStream mMessage;
    private GenericPdu mPdu;
    private PduHeaders mPduHeader;
    protected int mPosition;
    private final ContentResolver mResolver;
    private BufferStack mStack;

    static {
        mContentTypeMap = null;
        mContentTypeMap = new HashMap<>();
        for (int i = 0; i < PduContentTypes.contentTypes.length; i++) {
            mContentTypeMap.put(PduContentTypes.contentTypes[i], Integer.valueOf(i));
        }
    }

    public PduComposer(Context context, GenericPdu pdu) {
        this.mMessage = null;
        this.mPdu = null;
        this.mPosition = 0;
        this.mStack = null;
        this.mPduHeader = null;
        this.mPdu = pdu;
        this.mResolver = context.getContentResolver();
        this.mPduHeader = pdu.getPduHeaders();
        this.mStack = new BufferStack();
        this.mMessage = new ByteArrayOutputStream();
        this.mPosition = 0;
    }

    public byte[] make() {
        int type = this.mPdu.getMessageType();
        switch (type) {
            case 128:
            case 132:
                if (makeSendRetrievePdu(type) != 0) {
                    return null;
                }
                break;
            case 129:
            case 130:
            case 134:
            default:
                return null;
            case 131:
                if (makeNotifyResp() != 0) {
                    return null;
                }
                break;
            case 133:
                if (makeAckInd() != 0) {
                    return null;
                }
                break;
            case 135:
                if (makeReadRecInd() != 0) {
                    return null;
                }
                break;
        }
        return this.mMessage.toByteArray();
    }

    protected void arraycopy(byte[] buf, int pos, int length) {
        this.mMessage.write(buf, pos, length);
        this.mPosition += length;
    }

    protected void append(int value) {
        this.mMessage.write(value);
        this.mPosition++;
    }

    protected void appendShortInteger(int value) {
        append((value | 128) & 255);
    }

    protected void appendOctet(int number) {
        append(number);
    }

    protected void appendShortLength(int value) {
        append(value);
    }

    protected void appendLongInteger(long longInt) {
        long temp = longInt;
        int size = 0;
        while (temp != 0 && size < 8) {
            temp >>>= 8;
            size++;
        }
        appendShortLength(size);
        int shift = (size - 1) * 8;
        for (int i = 0; i < size; i++) {
            append((int) ((longInt >>> shift) & 255));
            shift -= 8;
        }
    }

    protected void appendTextString(byte[] text) {
        if ((text[0] & 255) > 127) {
            append(127);
        }
        arraycopy(text, 0, text.length);
        append(0);
    }

    protected void appendTextString(String str) {
        appendTextString(str.getBytes());
    }

    protected void appendEncodedString(EncodedStringValue enStr) {
        int charset = enStr.getCharacterSet();
        byte[] textString = enStr.getTextString();
        if (textString == null) {
            return;
        }
        this.mStack.newbuf();
        PositionMarker start = this.mStack.mark();
        appendShortInteger(charset);
        appendTextString(textString);
        int len = start.getLength();
        this.mStack.pop();
        appendValueLength(len);
        this.mStack.copy();
    }

    protected void appendUintvarInteger(long value) {
        long max = 127;
        int i = 0;
        while (i < 5 && value >= max) {
            max = (max << 7) | 127;
            i++;
        }
        while (i > 0) {
            long temp = value >>> (i * 7);
            append((int) ((128 | (temp & 127)) & 255));
            i--;
        }
        append((int) (value & 127));
    }

    protected void appendDateValue(long date) {
        appendLongInteger(date);
    }

    protected void appendValueLength(long value) {
        if (value < 31) {
            appendShortLength((int) value);
            return;
        }
        append(31);
        appendUintvarInteger(value);
    }

    protected void appendQuotedString(byte[] text) {
        append(34);
        arraycopy(text, 0, text.length);
        append(0);
    }

    protected void appendQuotedString(String str) {
        appendQuotedString(str.getBytes());
    }

    private EncodedStringValue appendAddressType(EncodedStringValue address) {
        try {
            int addressType = checkAddressType(address.getString());
            EncodedStringValue temp = EncodedStringValue.copy(address);
            if (1 == addressType) {
                temp.appendTextString(STRING_PHONE_NUMBER_ADDRESS_TYPE.getBytes());
            } else if (3 == addressType) {
                temp.appendTextString(STRING_IPV4_ADDRESS_TYPE.getBytes());
            } else if (4 == addressType) {
                temp.appendTextString(STRING_IPV6_ADDRESS_TYPE.getBytes());
            }
            return temp;
        } catch (NullPointerException e) {
            return null;
        }
    }

    private int appendHeader(int field) {
        switch (field) {
            case 129:
            case 130:
            case 151:
                EncodedStringValue[] addr = this.mPduHeader.getEncodedStringValues(field);
                if (addr == null) {
                    return 2;
                }
                for (EncodedStringValue encodedStringValue : addr) {
                    EncodedStringValue temp = appendAddressType(encodedStringValue);
                    if (temp == null) {
                        return 1;
                    }
                    appendOctet(field);
                    appendEncodedString(temp);
                }
                return 0;
            case 131:
            case 132:
            case 135:
            case 140:
            case 142:
            case 146:
            case 147:
            case 148:
            default:
                return 3;
            case 133:
                long date = this.mPduHeader.getLongInteger(field);
                if (-1 == date) {
                    return 2;
                }
                appendOctet(field);
                appendDateValue(date);
                return 0;
            case 134:
            case 143:
            case 144:
            case 145:
            case 149:
            case 153:
            case 155:
                int octet = this.mPduHeader.getOctet(field);
                if (octet == 0) {
                    return 2;
                }
                appendOctet(field);
                appendOctet(octet);
                return 0;
            case 136:
                long expiry = this.mPduHeader.getLongInteger(field);
                if (-1 == expiry) {
                    return 2;
                }
                appendOctet(field);
                this.mStack.newbuf();
                PositionMarker expiryStart = this.mStack.mark();
                append(129);
                appendLongInteger(expiry);
                int expiryLength = expiryStart.getLength();
                this.mStack.pop();
                appendValueLength(expiryLength);
                this.mStack.copy();
                return 0;
            case 137:
                appendOctet(field);
                EncodedStringValue from = this.mPduHeader.getEncodedStringValue(field);
                if (from == null || TextUtils.isEmpty(from.getString()) || new String(from.getTextString()).equals(PduHeaders.FROM_INSERT_ADDRESS_TOKEN_STR)) {
                    append(1);
                    append(129);
                    return 0;
                }
                this.mStack.newbuf();
                PositionMarker fstart = this.mStack.mark();
                append(128);
                EncodedStringValue temp2 = appendAddressType(from);
                if (temp2 == null) {
                    return 1;
                }
                appendEncodedString(temp2);
                int flen = fstart.getLength();
                this.mStack.pop();
                appendValueLength(flen);
                this.mStack.copy();
                return 0;
            case 138:
                byte[] messageClass = this.mPduHeader.getTextString(field);
                if (messageClass == null) {
                    return 2;
                }
                appendOctet(field);
                if (Arrays.equals(messageClass, PduHeaders.MESSAGE_CLASS_ADVERTISEMENT_STR.getBytes())) {
                    appendOctet(129);
                    return 0;
                } else if (Arrays.equals(messageClass, "auto".getBytes())) {
                    appendOctet(131);
                    return 0;
                } else if (Arrays.equals(messageClass, PduHeaders.MESSAGE_CLASS_PERSONAL_STR.getBytes())) {
                    appendOctet(128);
                    return 0;
                } else if (Arrays.equals(messageClass, PduHeaders.MESSAGE_CLASS_INFORMATIONAL_STR.getBytes())) {
                    appendOctet(130);
                    return 0;
                } else {
                    appendTextString(messageClass);
                    return 0;
                }
            case 139:
            case 152:
                byte[] textString = this.mPduHeader.getTextString(field);
                if (textString == null) {
                    return 2;
                }
                appendOctet(field);
                appendTextString(textString);
                return 0;
            case 141:
                appendOctet(field);
                int version = this.mPduHeader.getOctet(field);
                if (version == 0) {
                    appendShortInteger(18);
                    return 0;
                }
                appendShortInteger(version);
                return 0;
            case 150:
            case 154:
                EncodedStringValue enString = this.mPduHeader.getEncodedStringValue(field);
                if (enString == null) {
                    return 2;
                }
                appendOctet(field);
                appendEncodedString(enString);
                return 0;
        }
    }

    private int makeReadRecInd() {
        if (this.mMessage == null) {
            this.mMessage = new ByteArrayOutputStream();
            this.mPosition = 0;
        }
        appendOctet(140);
        appendOctet(135);
        if (appendHeader(141) == 0 && appendHeader(139) == 0 && appendHeader(151) == 0 && appendHeader(137) == 0) {
            appendHeader(133);
            return appendHeader(155) != 0 ? 1 : 0;
        }
        return 1;
    }

    private int makeNotifyResp() {
        if (this.mMessage == null) {
            this.mMessage = new ByteArrayOutputStream();
            this.mPosition = 0;
        }
        appendOctet(140);
        appendOctet(131);
        if (appendHeader(152) == 0 && appendHeader(141) == 0 && appendHeader(149) == 0) {
            appendHeader(145);
            return 0;
        }
        return 1;
    }

    private int makeAckInd() {
        if (this.mMessage == null) {
            this.mMessage = new ByteArrayOutputStream();
            this.mPosition = 0;
        }
        appendOctet(140);
        appendOctet(133);
        if (appendHeader(152) == 0 && appendHeader(141) == 0) {
            appendHeader(145);
            return 0;
        }
        return 1;
    }

    private int makeSendRetrievePdu(int type) {
        if (this.mMessage == null) {
            this.mMessage = new ByteArrayOutputStream();
            this.mPosition = 0;
        }
        appendOctet(140);
        appendOctet(type);
        appendOctet(152);
        byte[] trid = this.mPduHeader.getTextString(152);
        if (trid == null) {
            throw new IllegalArgumentException("Transaction-ID is null.");
        }
        appendTextString(trid);
        if (appendHeader(141) != 0) {
            return 1;
        }
        appendHeader(133);
        if (appendHeader(137) != 0) {
            return 1;
        }
        boolean recipient = false;
        if (appendHeader(151) != 1) {
            recipient = true;
        }
        if (appendHeader(130) != 1) {
            recipient = true;
        }
        if (appendHeader(129) != 1) {
            recipient = true;
        }
        if (recipient) {
            appendHeader(150);
            appendHeader(138);
            appendHeader(136);
            appendHeader(143);
            appendHeader(134);
            appendHeader(144);
            if (type == 132) {
                appendHeader(153);
                appendHeader(154);
            }
            appendOctet(132);
            return makeMessageBody(type);
        }
        return 1;
    }

    private int makeMessageBody(int type) {
        byte[] name;
        Throwable th;
        int dataLength;
        String str = ">";
        String str2 = "<";
        this.mStack.newbuf();
        PositionMarker ctStart = this.mStack.mark();
        String contentType = new String(this.mPduHeader.getTextString(132));
        Integer contentTypeIdentifier = mContentTypeMap.get(contentType);
        int i = 1;
        if (contentTypeIdentifier == null) {
            return 1;
        }
        appendShortInteger(contentTypeIdentifier.intValue());
        PduBody body = type == 132 ? ((RetrieveConf) this.mPdu).getBody() : ((SendReq) this.mPdu).getBody();
        if (body != null && body.getPartsNum() != 0) {
            try {
                PduPart part = body.getPart(0);
                byte[] start = part.getContentId();
                if (start != null) {
                    appendOctet(138);
                    if (60 == start[0] && 62 == start[start.length - 1]) {
                        appendTextString(start);
                    } else {
                        appendTextString("<" + new String(start) + ">");
                    }
                }
                appendOctet(137);
                appendTextString(part.getContentType());
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            int ctLength = ctStart.getLength();
            this.mStack.pop();
            appendValueLength(ctLength);
            this.mStack.copy();
            int partNum = body.getPartsNum();
            appendUintvarInteger(partNum);
            int i2 = 0;
            while (i2 < partNum) {
                PduPart part2 = body.getPart(i2);
                this.mStack.newbuf();
                PositionMarker attachment = this.mStack.mark();
                this.mStack.newbuf();
                PositionMarker contentTypeBegin = this.mStack.mark();
                byte[] partContentType = part2.getContentType();
                if (partContentType == null) {
                    return i;
                }
                Integer partContentTypeIdentifier = mContentTypeMap.get(new String(partContentType));
                if (partContentTypeIdentifier == null) {
                    appendTextString(partContentType);
                } else {
                    appendShortInteger(partContentTypeIdentifier.intValue());
                }
                byte[] name2 = part2.getName();
                if (name2 == null) {
                    byte[] name3 = part2.getFilename();
                    if (name3 == null) {
                        byte[] name4 = part2.getContentLocation();
                        if (name4 == null) {
                            return i;
                        }
                        name = name4;
                    } else {
                        name = name3;
                    }
                } else {
                    name = name2;
                }
                appendOctet(133);
                appendTextString(name);
                PositionMarker ctStart2 = ctStart;
                int charset = part2.getCharset();
                if (charset != 0) {
                    appendOctet(129);
                    appendShortInteger(charset);
                }
                int contentTypeLength = contentTypeBegin.getLength();
                this.mStack.pop();
                String contentType2 = contentType;
                PduBody body2 = body;
                appendValueLength(contentTypeLength);
                this.mStack.copy();
                byte[] contentId = part2.getContentId();
                if (contentId != null) {
                    appendOctet(192);
                    if (60 == contentId[0] && 62 == contentId[contentId.length - 1]) {
                        appendQuotedString(contentId);
                    }
                    appendQuotedString(str2 + new String(contentId) + str);
                }
                byte[] contentLocation = part2.getContentLocation();
                if (contentLocation != null) {
                    appendOctet(142);
                    appendTextString(contentLocation);
                }
                String str3 = str;
                int headerLength = attachment.getLength();
                int dataLength2 = 0;
                String str4 = str2;
                byte[] partData = part2.getData();
                if (partData != null) {
                    arraycopy(partData, 0, partData.length);
                    dataLength = partData.length;
                } else {
                    InputStream cr = null;
                    try {
                        byte[] buffer = new byte[1024];
                        try {
                            try {
                                cr = this.mResolver.openInputStream(part2.getDataUri());
                                int len = 0;
                                while (true) {
                                    try {
                                        int len2 = cr.read(buffer);
                                        byte[] contentId2 = contentId;
                                        if (len2 == -1) {
                                            break;
                                        }
                                        try {
                                            byte[] contentLocation2 = contentLocation;
                                            try {
                                                this.mMessage.write(buffer, 0, len2);
                                                this.mPosition += len2;
                                                dataLength2 += len2;
                                                len = len2;
                                                contentLocation = contentLocation2;
                                                contentId = contentId2;
                                            } catch (FileNotFoundException e2) {
                                                e = e2;
                                                if (cr != null) {
                                                    try {
                                                        cr.close();
                                                        return 1;
                                                    } catch (IOException e3) {
                                                        return 1;
                                                    }
                                                }
                                                return 1;
                                            } catch (IOException e4) {
                                                e = e4;
                                                if (cr != null) {
                                                    try {
                                                        cr.close();
                                                        return 1;
                                                    } catch (IOException e5) {
                                                        return 1;
                                                    }
                                                }
                                                return 1;
                                            } catch (RuntimeException e6) {
                                                e = e6;
                                                if (cr != null) {
                                                    try {
                                                        cr.close();
                                                        return 1;
                                                    } catch (IOException e7) {
                                                        return 1;
                                                    }
                                                }
                                                return 1;
                                            } catch (Throwable th2) {
                                                th = th2;
                                                if (cr != null) {
                                                    try {
                                                        cr.close();
                                                    } catch (IOException e8) {
                                                    }
                                                }
                                                throw th;
                                            }
                                        } catch (FileNotFoundException e9) {
                                            e = e9;
                                        } catch (IOException e10) {
                                            e = e10;
                                        } catch (RuntimeException e11) {
                                            e = e11;
                                        } catch (Throwable th3) {
                                            th = th3;
                                        }
                                    } catch (FileNotFoundException e12) {
                                        e = e12;
                                    } catch (IOException e13) {
                                        e = e13;
                                    } catch (RuntimeException e14) {
                                        e = e14;
                                    } catch (Throwable th4) {
                                        th = th4;
                                    }
                                }
                                if (cr != null) {
                                    try {
                                        cr.close();
                                    } catch (IOException e15) {
                                    }
                                }
                                dataLength = dataLength2;
                            } catch (FileNotFoundException e16) {
                                e = e16;
                                cr = null;
                            } catch (IOException e17) {
                                e = e17;
                                cr = null;
                            } catch (RuntimeException e18) {
                                e = e18;
                                cr = null;
                            } catch (Throwable th5) {
                                th = th5;
                                cr = null;
                            }
                        } catch (FileNotFoundException e19) {
                            e = e19;
                        } catch (IOException e20) {
                            e = e20;
                        } catch (RuntimeException e21) {
                            e = e21;
                        } catch (Throwable th6) {
                            th = th6;
                        }
                    } catch (FileNotFoundException e22) {
                        e = e22;
                    } catch (IOException e23) {
                        e = e23;
                    } catch (RuntimeException e24) {
                        e = e24;
                    } catch (Throwable th7) {
                        th = th7;
                    }
                }
                if (dataLength != attachment.getLength() - headerLength) {
                    throw new RuntimeException("BUG: Length correctness check failed");
                }
                this.mStack.pop();
                appendUintvarInteger(headerLength);
                appendUintvarInteger(dataLength);
                this.mStack.copy();
                i2++;
                ctStart = ctStart2;
                contentType = contentType2;
                body = body2;
                str = str3;
                str2 = str4;
                i = 1;
            }
            return 0;
        }
        appendUintvarInteger(0L);
        this.mStack.pop();
        this.mStack.copy();
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class LengthRecordNode {
        ByteArrayOutputStream currentMessage;
        public int currentPosition;
        public LengthRecordNode next;

        private LengthRecordNode() {
            this.currentMessage = null;
            this.currentPosition = 0;
            this.next = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class PositionMarker {
        private int c_pos;
        private int currentStackSize;

        private PositionMarker() {
        }

        int getLength() {
            if (this.currentStackSize != PduComposer.this.mStack.stackSize) {
                throw new RuntimeException("BUG: Invalid call to getLength()");
            }
            return PduComposer.this.mPosition - this.c_pos;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class BufferStack {
        private LengthRecordNode stack;
        int stackSize;
        private LengthRecordNode toCopy;

        private BufferStack() {
            this.stack = null;
            this.toCopy = null;
            this.stackSize = 0;
        }

        void newbuf() {
            if (this.toCopy != null) {
                throw new RuntimeException("BUG: Invalid newbuf() before copy()");
            }
            LengthRecordNode temp = new LengthRecordNode();
            temp.currentMessage = PduComposer.this.mMessage;
            temp.currentPosition = PduComposer.this.mPosition;
            temp.next = this.stack;
            this.stack = temp;
            this.stackSize++;
            PduComposer.this.mMessage = new ByteArrayOutputStream();
            PduComposer.this.mPosition = 0;
        }

        void pop() {
            ByteArrayOutputStream currentMessage = PduComposer.this.mMessage;
            int currentPosition = PduComposer.this.mPosition;
            PduComposer.this.mMessage = this.stack.currentMessage;
            PduComposer.this.mPosition = this.stack.currentPosition;
            LengthRecordNode lengthRecordNode = this.stack;
            this.toCopy = lengthRecordNode;
            this.stack = lengthRecordNode.next;
            this.stackSize--;
            this.toCopy.currentMessage = currentMessage;
            this.toCopy.currentPosition = currentPosition;
        }

        void copy() {
            PduComposer.this.arraycopy(this.toCopy.currentMessage.toByteArray(), 0, this.toCopy.currentPosition);
            this.toCopy = null;
        }

        PositionMarker mark() {
            PositionMarker m = new PositionMarker();
            m.c_pos = PduComposer.this.mPosition;
            m.currentStackSize = this.stackSize;
            return m;
        }
    }

    protected static int checkAddressType(String address) {
        if (address == null) {
            return 5;
        }
        if (address.matches(REGEXP_IPV4_ADDRESS_TYPE)) {
            return 3;
        }
        if (address.matches(REGEXP_PHONE_NUMBER_ADDRESS_TYPE)) {
            return 1;
        }
        if (address.matches(REGEXP_EMAIL_ADDRESS_TYPE)) {
            return 2;
        }
        if (!address.matches(REGEXP_IPV6_ADDRESS_TYPE)) {
            return 5;
        }
        return 4;
    }
}
