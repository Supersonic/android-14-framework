package com.android.net.module.util;

import android.text.TextUtils;
import android.text.format.DateFormat;
import com.android.net.module.util.DnsPacketUtils;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.InetAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes5.dex */
public abstract class DnsPacket {
    public static final int ANSECTION = 1;
    public static final int ARSECTION = 3;
    public static final int NSSECTION = 2;
    static final int NUM_SECTIONS = 4;
    public static final int QDSECTION = 0;
    private static final String TAG = DnsPacket.class.getSimpleName();
    private static final int TYPE_CNAME = 5;
    protected final DnsHeader mHeader;
    protected final List<DnsRecord>[] mRecords;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes5.dex */
    public @interface RecordType {
    }

    /* loaded from: classes5.dex */
    public static class ParseException extends RuntimeException {
        public String reason;

        public ParseException(String reason) {
            super(reason);
            this.reason = reason;
        }

        public ParseException(String reason, Throwable cause) {
            super(reason, cause);
            this.reason = reason;
        }
    }

    /* loaded from: classes5.dex */
    public static class DnsHeader {
        private static final int FLAGS_SECTION_QR_BIT = 15;
        private static final int SIZE_IN_BYTES = 12;
        private static final String TAG = "DnsHeader";
        private final int mFlags;
        private final int mId;
        private final int[] mRecordCount;

        public DnsHeader(ByteBuffer buf) throws BufferUnderflowException {
            Objects.requireNonNull(buf);
            this.mId = Short.toUnsignedInt(buf.getShort());
            this.mFlags = Short.toUnsignedInt(buf.getShort());
            this.mRecordCount = new int[4];
            for (int i = 0; i < 4; i++) {
                this.mRecordCount[i] = Short.toUnsignedInt(buf.getShort());
            }
        }

        public boolean isResponse() {
            return (this.mFlags & 32768) != 0;
        }

        public DnsHeader(int id, int flags, int qdcount, int ancount) {
            this.mId = id;
            this.mFlags = flags;
            this.mRecordCount = r0;
            int[] iArr = {qdcount, ancount};
        }

        public int getRecordCount(int type) {
            return this.mRecordCount[type];
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

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o.getClass() != getClass()) {
                return false;
            }
            DnsHeader other = (DnsHeader) o;
            return this.mId == other.mId && this.mFlags == other.mFlags && Arrays.equals(this.mRecordCount, other.mRecordCount);
        }

        public int hashCode() {
            return (this.mId * 31) + (this.mFlags * 37) + Arrays.hashCode(this.mRecordCount);
        }

        public byte[] getBytes() {
            ByteBuffer buf = ByteBuffer.allocate(12);
            buf.putShort((short) this.mId);
            buf.putShort((short) this.mFlags);
            for (int i = 0; i < 4; i++) {
                buf.putShort((short) this.mRecordCount[i]);
            }
            return buf.array();
        }
    }

    /* loaded from: classes5.dex */
    public static class DnsRecord {
        private static final int MAXNAMESIZE = 255;
        public static final int NAME_COMPRESSION = 192;
        public static final int NAME_NORMAL = 0;
        private static final String TAG = "DnsRecord";
        public final String dName;
        private final byte[] mRdata;
        public final int nsClass;
        public final int nsType;
        public final int rType;
        public final long ttl;

        public DnsRecord(int rType, ByteBuffer buf) throws BufferUnderflowException, ParseException {
            Objects.requireNonNull(buf);
            this.rType = rType;
            String parseName = DnsPacketUtils.DnsRecordParser.parseName(buf, 0, true);
            this.dName = parseName;
            if (parseName.length() > 255) {
                throw new ParseException("Parse name fail, name size is too long: " + parseName.length());
            }
            this.nsType = Short.toUnsignedInt(buf.getShort());
            this.nsClass = Short.toUnsignedInt(buf.getShort());
            if (rType != 0) {
                this.ttl = Integer.toUnsignedLong(buf.getInt());
                int length = Short.toUnsignedInt(buf.getShort());
                byte[] bArr = new byte[length];
                this.mRdata = bArr;
                buf.get(bArr);
                return;
            }
            this.ttl = 0L;
            this.mRdata = null;
        }

        public static DnsRecord makeAOrAAAARecord(int rType, String dName, int nsClass, long ttl, InetAddress address) throws IOException {
            int nsType = address.getAddress().length == 4 ? 1 : 28;
            return new DnsRecord(rType, dName, nsType, nsClass, ttl, address, null);
        }

        public static DnsRecord makeCNameRecord(int rType, String dName, int nsClass, long ttl, String domainName) throws IOException {
            return new DnsRecord(rType, dName, 5, nsClass, ttl, null, domainName);
        }

        public static DnsRecord makeQuestion(String dName, int nsType, int nsClass) {
            return new DnsRecord(dName, nsType, nsClass);
        }

        private static String requireHostName(String name) {
            if (!DnsPacketUtils.DnsRecordParser.isHostName(name)) {
                throw new IllegalArgumentException("Expected domain name but got " + name);
            }
            return name;
        }

        private DnsRecord(String dName, int nsType, int nsClass) {
            this.rType = 0;
            this.dName = requireHostName(dName);
            this.nsType = nsType;
            this.nsClass = nsClass;
            this.mRdata = null;
            this.ttl = 0L;
        }

        private DnsRecord(int rType, String dName, int nsType, int nsClass, long ttl, InetAddress address, String rDataStr) throws IOException {
            this.rType = rType;
            this.dName = requireHostName(dName);
            this.nsType = nsType;
            this.nsClass = nsClass;
            if (rType < 0 || rType >= 4 || rType == 0) {
                throw new IllegalArgumentException("Unexpected record type: " + rType);
            }
            this.mRdata = nsType == 5 ? DnsPacketUtils.DnsRecordParser.domainNameToLabels(rDataStr) : address.getAddress();
            this.ttl = ttl;
        }

        public byte[] getRR() {
            byte[] bArr = this.mRdata;
            if (bArr == null) {
                return null;
            }
            return (byte[]) bArr.clone();
        }

        public byte[] getBytes() throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.write(DnsPacketUtils.DnsRecordParser.domainNameToLabels(this.dName));
            dos.writeShort(this.nsType);
            dos.writeShort(this.nsClass);
            if (this.rType != 0) {
                dos.writeInt((int) this.ttl);
                byte[] bArr = this.mRdata;
                if (bArr == null) {
                    dos.writeShort(0);
                } else {
                    dos.writeShort(bArr.length);
                    dos.write(this.mRdata);
                }
            }
            return baos.toByteArray();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o.getClass() != getClass()) {
                return false;
            }
            DnsRecord other = (DnsRecord) o;
            return this.rType == other.rType && this.nsType == other.nsType && this.nsClass == other.nsClass && this.ttl == other.ttl && TextUtils.equals(this.dName, other.dName) && Arrays.equals(this.mRdata, other.mRdata);
        }

        public int hashCode() {
            long j = this.ttl;
            return (Objects.hash(this.dName) * 31) + (((int) ((-1) & j)) * 37) + (((int) (j >> 32)) * 41) + (this.nsType * 43) + (this.nsClass * 47) + (this.rType * 53) + Arrays.hashCode(this.mRdata);
        }

        public String toString() {
            return "DnsRecord{rType=" + this.rType + ", dName='" + this.dName + DateFormat.QUOTE + ", nsType=" + this.nsType + ", nsClass=" + this.nsClass + ", ttl=" + this.ttl + ", mRdata=" + Arrays.toString(this.mRdata) + '}';
        }
    }

    protected DnsPacket(byte[] data) throws ParseException {
        if (data == null) {
            throw new ParseException("Parse header failed, null input data");
        }
        try {
            ByteBuffer buffer = ByteBuffer.wrap(data);
            this.mHeader = new DnsHeader(buffer);
            this.mRecords = new ArrayList[4];
            for (int i = 0; i < 4; i++) {
                int count = this.mHeader.getRecordCount(i);
                this.mRecords[i] = new ArrayList(count);
                for (int j = 0; j < count; j++) {
                    try {
                        this.mRecords[i].add(new DnsRecord(i, buffer));
                    } catch (BufferUnderflowException e) {
                        throw new ParseException("Parse record fail", e);
                    }
                }
            }
        } catch (BufferUnderflowException e2) {
            throw new ParseException("Parse Header fail, bad input data", e2);
        }
    }

    protected DnsPacket(DnsHeader header, List<DnsRecord> qd, List<DnsRecord> an) {
        this.mHeader = (DnsHeader) Objects.requireNonNull(header);
        this.mRecords = r1;
        List<DnsRecord>[] listArr = {Collections.unmodifiableList(new ArrayList(qd)), Collections.unmodifiableList(new ArrayList(an)), new ArrayList(), new ArrayList()};
        for (int i = 0; i < 4; i++) {
            if (this.mHeader.mRecordCount[i] != this.mRecords[i].size()) {
                throw new IllegalArgumentException("Record count mismatch: expected " + this.mHeader.mRecordCount[i] + " but was " + this.mRecords[i]);
            }
        }
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.write(this.mHeader.getBytes());
        for (int i = 0; i < 4; i++) {
            for (DnsRecord record : this.mRecords[i]) {
                buf.write(record.getBytes());
            }
        }
        return buf.toByteArray();
    }

    public String toString() {
        return "DnsPacket{header=" + this.mHeader + ", records='" + Arrays.toString(this.mRecords) + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o.getClass() != getClass()) {
            return false;
        }
        DnsPacket other = (DnsPacket) o;
        return Objects.equals(this.mHeader, other.mHeader) && Arrays.deepEquals(this.mRecords, other.mRecords);
    }

    public int hashCode() {
        int result = Objects.hash(this.mHeader);
        return (result * 31) + Arrays.hashCode(this.mRecords);
    }
}
