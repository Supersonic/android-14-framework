package com.android.internal.telephony.uicc.asn1;

import com.android.internal.telephony.uicc.IccUtils;
import com.android.net.module.util.NetworkStackConstants;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public final class Asn1Node {
    private static final int INT_BYTES = 4;
    private final List<Asn1Node> mChildren;
    private final boolean mConstructed;
    private byte[] mDataBytes;
    private int mDataLength;
    private int mDataOffset;
    private int mEncodedLength;
    private final int mTag;
    private static final List<Asn1Node> EMPTY_NODE_LIST = Collections.emptyList();
    private static final byte[] TRUE_BYTES = {-1};
    private static final byte[] FALSE_BYTES = {0};

    /* loaded from: classes3.dex */
    public static final class Builder {
        private final List<Asn1Node> mChildren;
        private final int mTag;

        private Builder(int tag) {
            if (!Asn1Node.isConstructedTag(tag)) {
                throw new IllegalArgumentException("Builder should be created for a constructed tag: " + tag);
            }
            this.mTag = tag;
            this.mChildren = new ArrayList();
        }

        public Builder addChild(Asn1Node child) {
            this.mChildren.add(child);
            return this;
        }

        public Builder addChild(Builder child) {
            this.mChildren.add(child.build());
            return this;
        }

        public Builder addChildren(byte[] encodedBytes) throws InvalidAsn1DataException {
            Asn1Decoder subDecoder = new Asn1Decoder(encodedBytes, 0, encodedBytes.length);
            while (subDecoder.hasNextNode()) {
                this.mChildren.add(subDecoder.nextNode());
            }
            return this;
        }

        public Builder addChildAsInteger(int tag, int value) {
            if (Asn1Node.isConstructedTag(tag)) {
                throw new IllegalStateException("Cannot set value of a constructed tag: " + tag);
            }
            byte[] dataBytes = IccUtils.signedIntToBytes(value);
            addChild(new Asn1Node(tag, dataBytes, 0, dataBytes.length));
            return this;
        }

        public Builder addChildAsString(int tag, String value) {
            if (Asn1Node.isConstructedTag(tag)) {
                throw new IllegalStateException("Cannot set value of a constructed tag: " + tag);
            }
            byte[] dataBytes = value.getBytes(StandardCharsets.UTF_8);
            addChild(new Asn1Node(tag, dataBytes, 0, dataBytes.length));
            return this;
        }

        public Builder addChildAsBytes(int tag, byte[] value) {
            if (Asn1Node.isConstructedTag(tag)) {
                throw new IllegalStateException("Cannot set value of a constructed tag: " + tag);
            }
            addChild(new Asn1Node(tag, value, 0, value.length));
            return this;
        }

        public Builder addChildAsBytesFromHex(int tag, String hex) {
            return addChildAsBytes(tag, IccUtils.hexStringToBytes(hex));
        }

        public Builder addChildAsBits(int tag, int value) {
            if (Asn1Node.isConstructedTag(tag)) {
                throw new IllegalStateException("Cannot set value of a constructed tag: " + tag);
            }
            byte[] dataBytes = new byte[5];
            int value2 = Integer.reverse(value);
            int dataLength = 0;
            for (int i = 1; i < dataBytes.length; i++) {
                dataBytes[i] = (byte) (value2 >> ((4 - i) * 8));
                if (dataBytes[i] != 0) {
                    dataLength = i;
                }
            }
            int dataLength2 = dataLength + 1;
            dataBytes[0] = IccUtils.countTrailingZeros(dataBytes[dataLength2 - 1]);
            addChild(new Asn1Node(tag, dataBytes, 0, dataLength2));
            return this;
        }

        public Builder addChildAsBoolean(int tag, boolean value) {
            if (Asn1Node.isConstructedTag(tag)) {
                throw new IllegalStateException("Cannot set value of a constructed tag: " + tag);
            }
            addChild(new Asn1Node(tag, value ? Asn1Node.TRUE_BYTES : Asn1Node.FALSE_BYTES, 0, 1));
            return this;
        }

        public Asn1Node build() {
            return new Asn1Node(this.mTag, this.mChildren);
        }
    }

    public static Builder newBuilder(int tag) {
        return new Builder(tag);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isConstructedTag(int tag) {
        byte[] tagBytes = IccUtils.unsignedIntToBytes(tag);
        return (tagBytes[0] & NetworkStackConstants.TCPHDR_URG) != 0;
    }

    private static int calculateEncodedBytesNumForLength(int length) {
        if (length > 127) {
            int len = 1 + IccUtils.byteNumForUnsignedInt(length);
            return len;
        }
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Asn1Node(int tag, byte[] src, int offset, int length) {
        this.mTag = tag;
        boolean isConstructedTag = isConstructedTag(tag);
        this.mConstructed = isConstructedTag;
        this.mDataBytes = src;
        this.mDataOffset = offset;
        this.mDataLength = length;
        this.mChildren = isConstructedTag ? new ArrayList<>() : EMPTY_NODE_LIST;
        this.mEncodedLength = IccUtils.byteNumForUnsignedInt(tag) + calculateEncodedBytesNumForLength(this.mDataLength) + this.mDataLength;
    }

    private Asn1Node(int tag, List<Asn1Node> children) {
        this.mTag = tag;
        this.mConstructed = true;
        this.mChildren = children;
        this.mDataLength = 0;
        int size = children.size();
        for (int i = 0; i < size; i++) {
            this.mDataLength += children.get(i).mEncodedLength;
        }
        int i2 = this.mTag;
        this.mEncodedLength = IccUtils.byteNumForUnsignedInt(i2) + calculateEncodedBytesNumForLength(this.mDataLength) + this.mDataLength;
    }

    public int getTag() {
        return this.mTag;
    }

    public boolean isConstructed() {
        return this.mConstructed;
    }

    public boolean hasChild(int tag, int... tags) throws InvalidAsn1DataException {
        try {
            getChild(tag, tags);
            return true;
        } catch (TagNotFoundException e) {
            return false;
        }
    }

    public Asn1Node getChild(int tag, int... tags) throws TagNotFoundException, InvalidAsn1DataException {
        if (!this.mConstructed) {
            throw new TagNotFoundException(tag);
        }
        int index = 0;
        Asn1Node node = this;
        while (node != null) {
            List<Asn1Node> children = node.getChildren();
            int size = children.size();
            Asn1Node foundChild = null;
            int i = 0;
            while (true) {
                if (i >= size) {
                    break;
                }
                Asn1Node child = children.get(i);
                if (child.getTag() != tag) {
                    i++;
                } else {
                    foundChild = child;
                    break;
                }
            }
            node = foundChild;
            if (index >= tags.length) {
                break;
            }
            tag = tags[index];
            index++;
        }
        if (node == null) {
            throw new TagNotFoundException(tag);
        }
        return node;
    }

    public List<Asn1Node> getChildren(int tag) throws TagNotFoundException, InvalidAsn1DataException {
        if (!this.mConstructed) {
            return EMPTY_NODE_LIST;
        }
        List<Asn1Node> children = getChildren();
        if (children.isEmpty()) {
            return EMPTY_NODE_LIST;
        }
        List<Asn1Node> output = new ArrayList<>();
        int size = children.size();
        for (int i = 0; i < size; i++) {
            Asn1Node child = children.get(i);
            if (child.getTag() == tag) {
                output.add(child);
            }
        }
        return output.isEmpty() ? EMPTY_NODE_LIST : output;
    }

    public List<Asn1Node> getChildren() throws InvalidAsn1DataException {
        if (!this.mConstructed) {
            return EMPTY_NODE_LIST;
        }
        byte[] bArr = this.mDataBytes;
        if (bArr != null) {
            Asn1Decoder subDecoder = new Asn1Decoder(bArr, this.mDataOffset, this.mDataLength);
            while (subDecoder.hasNextNode()) {
                this.mChildren.add(subDecoder.nextNode());
            }
            this.mDataBytes = null;
            this.mDataOffset = 0;
        }
        return this.mChildren;
    }

    public boolean hasValue() {
        return (this.mConstructed || this.mDataBytes == null) ? false : true;
    }

    public int asInteger() throws InvalidAsn1DataException {
        if (this.mConstructed) {
            throw new IllegalStateException("Cannot get value of a constructed node.");
        }
        byte[] bArr = this.mDataBytes;
        if (bArr == null) {
            throw new InvalidAsn1DataException(this.mTag, "Data bytes cannot be null.");
        }
        try {
            return IccUtils.bytesToInt(bArr, this.mDataOffset, this.mDataLength);
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new InvalidAsn1DataException(this.mTag, "Cannot parse data bytes.", e);
        }
    }

    public long asRawLong() throws InvalidAsn1DataException {
        if (this.mConstructed) {
            throw new IllegalStateException("Cannot get value of a constructed node.");
        }
        byte[] bArr = this.mDataBytes;
        if (bArr == null) {
            throw new InvalidAsn1DataException(this.mTag, "Data bytes cannot be null.");
        }
        try {
            return IccUtils.bytesToRawLong(bArr, this.mDataOffset, this.mDataLength);
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new InvalidAsn1DataException(this.mTag, "Cannot parse data bytes.", e);
        }
    }

    public String asString() throws InvalidAsn1DataException {
        if (this.mConstructed) {
            throw new IllegalStateException("Cannot get value of a constructed node.");
        }
        byte[] bArr = this.mDataBytes;
        if (bArr == null) {
            throw new InvalidAsn1DataException(this.mTag, "Data bytes cannot be null.");
        }
        try {
            return new String(bArr, this.mDataOffset, this.mDataLength, StandardCharsets.UTF_8);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidAsn1DataException(this.mTag, "Cannot parse data bytes.", e);
        }
    }

    public byte[] asBytes() throws InvalidAsn1DataException {
        if (this.mConstructed) {
            throw new IllegalStateException("Cannot get value of a constructed node.");
        }
        byte[] bArr = this.mDataBytes;
        if (bArr == null) {
            throw new InvalidAsn1DataException(this.mTag, "Data bytes cannot be null.");
        }
        int i = this.mDataLength;
        byte[] output = new byte[i];
        try {
            System.arraycopy(bArr, this.mDataOffset, output, 0, i);
            return output;
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidAsn1DataException(this.mTag, "Cannot parse data bytes.", e);
        }
    }

    public int asBits() throws InvalidAsn1DataException {
        if (this.mConstructed) {
            throw new IllegalStateException("Cannot get value of a constructed node.");
        }
        byte[] bArr = this.mDataBytes;
        if (bArr == null) {
            throw new InvalidAsn1DataException(this.mTag, "Data bytes cannot be null.");
        }
        try {
            int bits = IccUtils.bytesToInt(bArr, this.mDataOffset + 1, this.mDataLength - 1);
            for (int i = this.mDataLength - 1; i < 4; i++) {
                bits <<= 8;
            }
            int i2 = Integer.reverse(bits);
            return i2;
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new InvalidAsn1DataException(this.mTag, "Cannot parse data bytes.", e);
        }
    }

    public boolean asBoolean() throws InvalidAsn1DataException {
        if (this.mConstructed) {
            throw new IllegalStateException("Cannot get value of a constructed node.");
        }
        byte[] bArr = this.mDataBytes;
        if (bArr == null) {
            throw new InvalidAsn1DataException(this.mTag, "Data bytes cannot be null.");
        }
        if (this.mDataLength != 1) {
            throw new InvalidAsn1DataException(this.mTag, "Cannot parse data bytes as boolean: length=" + this.mDataLength);
        }
        int i = this.mDataOffset;
        if (i < 0 || i >= bArr.length) {
            throw new InvalidAsn1DataException(this.mTag, "Cannot parse data bytes.", new ArrayIndexOutOfBoundsException(this.mDataOffset));
        }
        byte b = bArr[i];
        if (b == -1) {
            return Boolean.TRUE.booleanValue();
        }
        if (b == 0) {
            return Boolean.FALSE.booleanValue();
        }
        throw new InvalidAsn1DataException(this.mTag, "Cannot parse data bytes as boolean: " + ((int) this.mDataBytes[this.mDataOffset]));
    }

    public int getEncodedLength() {
        return this.mEncodedLength;
    }

    public int getDataLength() {
        return this.mDataLength;
    }

    public void writeToBytes(byte[] dest, int offset) {
        if (offset < 0 || this.mEncodedLength + offset > dest.length) {
            throw new IndexOutOfBoundsException("Not enough space to write. Required bytes: " + this.mEncodedLength);
        }
        write(dest, offset);
    }

    public byte[] toBytes() {
        byte[] dest = new byte[this.mEncodedLength];
        write(dest, 0);
        return dest;
    }

    public String toHex() {
        return IccUtils.bytesToHexString(toBytes());
    }

    public String getHeadAsHex() {
        String headHex = IccUtils.bytesToHexString(IccUtils.unsignedIntToBytes(this.mTag));
        int i = this.mDataLength;
        if (i <= 127) {
            return headHex + IccUtils.byteToHex((byte) this.mDataLength);
        }
        byte[] lenBytes = IccUtils.unsignedIntToBytes(i);
        return (headHex + IccUtils.byteToHex((byte) (lenBytes.length | 128))) + IccUtils.bytesToHexString(lenBytes);
    }

    private int write(byte[] dest, int offset) {
        int offset2;
        int offset3 = offset + IccUtils.unsignedIntToBytes(this.mTag, dest, offset);
        int i = this.mDataLength;
        if (i <= 127) {
            offset2 = offset3 + 1;
            dest[offset3] = (byte) i;
        } else {
            int offset4 = offset3 + 1;
            int lenLen = IccUtils.unsignedIntToBytes(i, dest, offset4);
            dest[offset4 - 1] = (byte) (lenLen | 128);
            offset2 = offset4 + lenLen;
        }
        if (this.mConstructed && this.mDataBytes == null) {
            int size = this.mChildren.size();
            for (int i2 = 0; i2 < size; i2++) {
                Asn1Node child = this.mChildren.get(i2);
                offset2 = child.write(dest, offset2);
            }
            return offset2;
        }
        byte[] bArr = this.mDataBytes;
        if (bArr != null) {
            System.arraycopy(bArr, this.mDataOffset, dest, offset2, this.mDataLength);
            return offset2 + this.mDataLength;
        }
        return offset2;
    }
}
