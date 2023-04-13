package com.android.internal.telephony.util;

import android.text.TextUtils;
import com.android.net.module.annotation.VisibleForTesting;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public abstract class DnsPacket {
    public static final int ANSECTION = 1;
    public static final int ARSECTION = 3;
    public static final int NSSECTION = 2;
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    static final int NUM_SECTIONS = 4;
    public static final int QDSECTION = 0;
    protected final DnsHeader mHeader;
    protected final List<DnsRecord>[] mRecords;

    /* loaded from: classes.dex */
    public static class ParseException extends RuntimeException {
        public String reason;

        public ParseException(String str) {
            super(str);
            this.reason = str;
        }

        public ParseException(String str, Throwable th) {
            super(str, th);
            this.reason = str;
        }
    }

    /* loaded from: classes.dex */
    public static class DnsHeader {
        private final int mFlags;
        private final int mId;
        private final int[] mRecordCount;

        @VisibleForTesting
        public DnsHeader(ByteBuffer byteBuffer) throws BufferUnderflowException {
            Objects.requireNonNull(byteBuffer);
            this.mId = Short.toUnsignedInt(byteBuffer.getShort());
            this.mFlags = Short.toUnsignedInt(byteBuffer.getShort());
            this.mRecordCount = new int[4];
            for (int i = 0; i < 4; i++) {
                this.mRecordCount[i] = Short.toUnsignedInt(byteBuffer.getShort());
            }
        }

        public boolean isResponse() {
            return (this.mFlags & 32768) != 0;
        }

        @VisibleForTesting
        public DnsHeader(int i, int i2, int i3, int i4) {
            this.mId = i;
            this.mFlags = i2;
            this.mRecordCount = r1;
            int[] iArr = {i3, i4};
        }

        public int getRecordCount(int i) {
            return this.mRecordCount[i];
        }

        public int getFlags() {
            return this.mFlags;
        }

        public int getId() {
            return this.mId;
        }

        public String toString() {
            return "DnsHeader{id=" + this.mId + ", flags=" + this.mFlags + ", recordCounts=" + Arrays.toString(this.mRecordCount) + '}';
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            DnsHeader dnsHeader = (DnsHeader) obj;
            return this.mId == dnsHeader.mId && this.mFlags == dnsHeader.mFlags && Arrays.equals(this.mRecordCount, dnsHeader.mRecordCount);
        }

        public int hashCode() {
            return (this.mId * 31) + (this.mFlags * 37) + Arrays.hashCode(this.mRecordCount);
        }

        public byte[] getBytes() {
            ByteBuffer allocate = ByteBuffer.allocate(12);
            allocate.putShort((short) this.mId);
            allocate.putShort((short) this.mFlags);
            for (int i = 0; i < 4; i++) {
                allocate.putShort((short) this.mRecordCount[i]);
            }
            return allocate.array();
        }
    }

    /* loaded from: classes.dex */
    public static class DnsRecord {
        public static final int NAME_COMPRESSION = 192;
        public static final int NAME_NORMAL = 0;
        public final String dName;
        private final byte[] mRdata;
        public final int nsClass;
        public final int nsType;
        public final int rType;
        public final long ttl;

        @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
        public DnsRecord(int i, ByteBuffer byteBuffer) throws BufferUnderflowException, ParseException {
            Objects.requireNonNull(byteBuffer);
            this.rType = i;
            String parseName = DnsPacketUtils$DnsRecordParser.parseName(byteBuffer, 0, true);
            this.dName = parseName;
            if (parseName.length() > 255) {
                throw new ParseException("Parse name fail, name size is too long: " + parseName.length());
            }
            this.nsType = Short.toUnsignedInt(byteBuffer.getShort());
            this.nsClass = Short.toUnsignedInt(byteBuffer.getShort());
            if (i != 0) {
                this.ttl = Integer.toUnsignedLong(byteBuffer.getInt());
                byte[] bArr = new byte[Short.toUnsignedInt(byteBuffer.getShort())];
                this.mRdata = bArr;
                byteBuffer.get(bArr);
                return;
            }
            this.ttl = 0L;
            this.mRdata = null;
        }

        public static DnsRecord makeAOrAAAARecord(int i, String str, int i2, long j, InetAddress inetAddress) throws IOException {
            return new DnsRecord(i, str, inetAddress.getAddress().length == 4 ? 1 : 28, i2, j, inetAddress, null);
        }

        public static DnsRecord makeCNameRecord(int i, String str, int i2, long j, String str2) throws IOException {
            return new DnsRecord(i, str, 5, i2, j, null, str2);
        }

        public static DnsRecord makeQuestion(String str, int i, int i2) {
            return new DnsRecord(str, i, i2);
        }

        private static String requireHostName(String str) {
            if (DnsPacketUtils$DnsRecordParser.isHostName(str)) {
                return str;
            }
            throw new IllegalArgumentException("Expected domain name but got " + str);
        }

        private DnsRecord(String str, int i, int i2) {
            this.rType = 0;
            this.dName = requireHostName(str);
            this.nsType = i;
            this.nsClass = i2;
            this.mRdata = null;
            this.ttl = 0L;
        }

        private DnsRecord(int i, String str, int i2, int i3, long j, InetAddress inetAddress, String str2) throws IOException {
            this.rType = i;
            this.dName = requireHostName(str);
            this.nsType = i2;
            this.nsClass = i3;
            if (i < 0 || i >= 4 || i == 0) {
                throw new IllegalArgumentException("Unexpected record type: " + i);
            }
            this.mRdata = i2 == 5 ? DnsPacketUtils$DnsRecordParser.domainNameToLabels(str2) : inetAddress.getAddress();
            this.ttl = j;
        }

        public byte[] getRR() {
            byte[] bArr = this.mRdata;
            if (bArr == null) {
                return null;
            }
            return (byte[]) bArr.clone();
        }

        public byte[] getBytes() throws IOException {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.write(DnsPacketUtils$DnsRecordParser.domainNameToLabels(this.dName));
            dataOutputStream.writeShort(this.nsType);
            dataOutputStream.writeShort(this.nsClass);
            if (this.rType != 0) {
                dataOutputStream.writeInt((int) this.ttl);
                byte[] bArr = this.mRdata;
                if (bArr == null) {
                    dataOutputStream.writeShort(0);
                } else {
                    dataOutputStream.writeShort(bArr.length);
                    dataOutputStream.write(this.mRdata);
                }
            }
            return byteArrayOutputStream.toByteArray();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            DnsRecord dnsRecord = (DnsRecord) obj;
            return this.rType == dnsRecord.rType && this.nsType == dnsRecord.nsType && this.nsClass == dnsRecord.nsClass && this.ttl == dnsRecord.ttl && TextUtils.equals(this.dName, dnsRecord.dName) && Arrays.equals(this.mRdata, dnsRecord.mRdata);
        }

        public int hashCode() {
            long j = this.ttl;
            return (Objects.hash(this.dName) * 31) + (((int) ((-1) & j)) * 37) + (((int) (j >> 32)) * 41) + (this.nsType * 43) + (this.nsClass * 47) + (this.rType * 53) + Arrays.hashCode(this.mRdata);
        }

        public String toString() {
            return "DnsRecord{rType=" + this.rType + ", dName='" + this.dName + "', nsType=" + this.nsType + ", nsClass=" + this.nsClass + ", ttl=" + this.ttl + ", mRdata=" + Arrays.toString(this.mRdata) + '}';
        }
    }

    protected DnsPacket(byte[] bArr) throws ParseException {
        if (bArr == null) {
            throw new ParseException("Parse header failed, null input data");
        }
        try {
            ByteBuffer wrap = ByteBuffer.wrap(bArr);
            this.mHeader = new DnsHeader(wrap);
            this.mRecords = new ArrayList[4];
            for (int i = 0; i < 4; i++) {
                int recordCount = this.mHeader.getRecordCount(i);
                this.mRecords[i] = new ArrayList(recordCount);
                for (int i2 = 0; i2 < recordCount; i2++) {
                    try {
                        this.mRecords[i].add(new DnsRecord(i, wrap));
                    } catch (BufferUnderflowException e) {
                        throw new ParseException("Parse record fail", e);
                    }
                }
            }
        } catch (BufferUnderflowException e2) {
            throw new ParseException("Parse Header fail, bad input data", e2);
        }
    }

    protected DnsPacket(DnsHeader dnsHeader, List<DnsRecord> list, List<DnsRecord> list2) {
        Objects.requireNonNull(dnsHeader);
        this.mHeader = dnsHeader;
        this.mRecords = r0;
        List<DnsRecord>[] listArr = {Collections.unmodifiableList(new ArrayList(list)), Collections.unmodifiableList(new ArrayList(list2)), new ArrayList(), new ArrayList()};
        for (int i = 0; i < 4; i++) {
            if (this.mHeader.mRecordCount[i] != this.mRecords[i].size()) {
                throw new IllegalArgumentException("Record count mismatch: expected " + this.mHeader.mRecordCount[i] + " but was " + this.mRecords[i]);
            }
        }
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(this.mHeader.getBytes());
        for (int i = 0; i < 4; i++) {
            for (DnsRecord dnsRecord : this.mRecords[i]) {
                byteArrayOutputStream.write(dnsRecord.getBytes());
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    public String toString() {
        return "DnsPacket{header=" + this.mHeader + ", records='" + Arrays.toString(this.mRecords) + '}';
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        DnsPacket dnsPacket = (DnsPacket) obj;
        return Objects.equals(this.mHeader, dnsPacket.mHeader) && Arrays.deepEquals(this.mRecords, dnsPacket.mRecords);
    }

    public int hashCode() {
        return (Objects.hash(this.mHeader) * 31) + Arrays.hashCode(this.mRecords);
    }
}
