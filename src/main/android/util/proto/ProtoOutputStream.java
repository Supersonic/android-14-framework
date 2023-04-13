package android.util.proto;

import android.util.Log;
import com.android.internal.logging.nano.MetricsProto;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
/* loaded from: classes3.dex */
public final class ProtoOutputStream extends ProtoStream {
    public static final String TAG = "ProtoOutputStream";
    private EncodedBuffer mBuffer;
    private boolean mCompacted;
    private int mCopyBegin;
    private int mDepth;
    private long mExpectedObjectToken;
    private int mNextObjectId;
    private OutputStream mStream;

    public ProtoOutputStream() {
        this(0);
    }

    public ProtoOutputStream(int chunkSize) {
        this.mNextObjectId = -1;
        this.mBuffer = new EncodedBuffer(chunkSize);
    }

    public ProtoOutputStream(OutputStream stream) {
        this();
        this.mStream = stream;
    }

    public ProtoOutputStream(FileDescriptor fd) {
        this(new FileOutputStream(fd));
    }

    public int getRawSize() {
        if (this.mCompacted) {
            return getBytes().length;
        }
        return this.mBuffer.getSize();
    }

    public void write(long fieldId, double val) {
        assertNotCompacted();
        int id = (int) fieldId;
        switch ((int) ((17587891077120L & fieldId) >> 32)) {
            case 257:
                writeDoubleImpl(id, val);
                return;
            case 258:
                writeFloatImpl(id, (float) val);
                return;
            case 259:
                writeInt64Impl(id, (long) val);
                return;
            case 260:
                writeUInt64Impl(id, (long) val);
                return;
            case 261:
                writeInt32Impl(id, (int) val);
                return;
            case 262:
                writeFixed64Impl(id, (long) val);
                return;
            case 263:
                writeFixed32Impl(id, (int) val);
                return;
            case 264:
                writeBoolImpl(id, val != 0.0d);
                return;
            case 269:
                writeUInt32Impl(id, (int) val);
                return;
            case 270:
                writeEnumImpl(id, (int) val);
                return;
            case 271:
                writeSFixed32Impl(id, (int) val);
                return;
            case 272:
                writeSFixed64Impl(id, (long) val);
                return;
            case 273:
                writeSInt32Impl(id, (int) val);
                return;
            case 274:
                writeSInt64Impl(id, (long) val);
                return;
            case 513:
            case 1281:
                writeRepeatedDoubleImpl(id, val);
                return;
            case 514:
            case 1282:
                writeRepeatedFloatImpl(id, (float) val);
                return;
            case 515:
            case 1283:
                writeRepeatedInt64Impl(id, (long) val);
                return;
            case 516:
            case 1284:
                writeRepeatedUInt64Impl(id, (long) val);
                return;
            case 517:
            case 1285:
                writeRepeatedInt32Impl(id, (int) val);
                return;
            case 518:
            case 1286:
                writeRepeatedFixed64Impl(id, (long) val);
                return;
            case 519:
            case 1287:
                writeRepeatedFixed32Impl(id, (int) val);
                return;
            case 520:
            case MetricsProto.MetricsEvent.ROTATION_SUGGESTION_SHOWN /* 1288 */:
                writeRepeatedBoolImpl(id, val != 0.0d);
                return;
            case 525:
            case 1293:
                writeRepeatedUInt32Impl(id, (int) val);
                return;
            case MetricsProto.MetricsEvent.DIALOG_SUPPORT_DISCLAIMER /* 526 */:
            case 1294:
                writeRepeatedEnumImpl(id, (int) val);
                return;
            case MetricsProto.MetricsEvent.DIALOG_SUPPORT_PHONE /* 527 */:
            case MetricsProto.MetricsEvent.OUTPUT_CHOOSER /* 1295 */:
                writeRepeatedSFixed32Impl(id, (int) val);
                return;
            case 528:
            case MetricsProto.MetricsEvent.ACTION_OUTPUT_CHOOSER_CONNECT /* 1296 */:
                writeRepeatedSFixed64Impl(id, (long) val);
                return;
            case 529:
            case MetricsProto.MetricsEvent.ACTION_OUTPUT_CHOOSER_DISCONNECT /* 1297 */:
                writeRepeatedSInt32Impl(id, (int) val);
                return;
            case 530:
            case MetricsProto.MetricsEvent.SETTINGS_TV_HOME_THEATER_CONTROL_CATEGORY /* 1298 */:
                writeRepeatedSInt64Impl(id, (long) val);
                return;
            default:
                throw new IllegalArgumentException("Attempt to call write(long, double) with " + getFieldIdString(fieldId));
        }
    }

    public void write(long fieldId, float val) {
        assertNotCompacted();
        int id = (int) fieldId;
        switch ((int) ((17587891077120L & fieldId) >> 32)) {
            case 257:
                writeDoubleImpl(id, val);
                return;
            case 258:
                writeFloatImpl(id, val);
                return;
            case 259:
                writeInt64Impl(id, val);
                return;
            case 260:
                writeUInt64Impl(id, val);
                return;
            case 261:
                writeInt32Impl(id, (int) val);
                return;
            case 262:
                writeFixed64Impl(id, val);
                return;
            case 263:
                writeFixed32Impl(id, (int) val);
                return;
            case 264:
                writeBoolImpl(id, val != 0.0f);
                return;
            case 269:
                writeUInt32Impl(id, (int) val);
                return;
            case 270:
                writeEnumImpl(id, (int) val);
                return;
            case 271:
                writeSFixed32Impl(id, (int) val);
                return;
            case 272:
                writeSFixed64Impl(id, val);
                return;
            case 273:
                writeSInt32Impl(id, (int) val);
                return;
            case 274:
                writeSInt64Impl(id, val);
                return;
            case 513:
            case 1281:
                writeRepeatedDoubleImpl(id, val);
                return;
            case 514:
            case 1282:
                writeRepeatedFloatImpl(id, val);
                return;
            case 515:
            case 1283:
                writeRepeatedInt64Impl(id, val);
                return;
            case 516:
            case 1284:
                writeRepeatedUInt64Impl(id, val);
                return;
            case 517:
            case 1285:
                writeRepeatedInt32Impl(id, (int) val);
                return;
            case 518:
            case 1286:
                writeRepeatedFixed64Impl(id, val);
                return;
            case 519:
            case 1287:
                writeRepeatedFixed32Impl(id, (int) val);
                return;
            case 520:
            case MetricsProto.MetricsEvent.ROTATION_SUGGESTION_SHOWN /* 1288 */:
                writeRepeatedBoolImpl(id, val != 0.0f);
                return;
            case 525:
            case 1293:
                writeRepeatedUInt32Impl(id, (int) val);
                return;
            case MetricsProto.MetricsEvent.DIALOG_SUPPORT_DISCLAIMER /* 526 */:
            case 1294:
                writeRepeatedEnumImpl(id, (int) val);
                return;
            case MetricsProto.MetricsEvent.DIALOG_SUPPORT_PHONE /* 527 */:
            case MetricsProto.MetricsEvent.OUTPUT_CHOOSER /* 1295 */:
                writeRepeatedSFixed32Impl(id, (int) val);
                return;
            case 528:
            case MetricsProto.MetricsEvent.ACTION_OUTPUT_CHOOSER_CONNECT /* 1296 */:
                writeRepeatedSFixed64Impl(id, val);
                return;
            case 529:
            case MetricsProto.MetricsEvent.ACTION_OUTPUT_CHOOSER_DISCONNECT /* 1297 */:
                writeRepeatedSInt32Impl(id, (int) val);
                return;
            case 530:
            case MetricsProto.MetricsEvent.SETTINGS_TV_HOME_THEATER_CONTROL_CATEGORY /* 1298 */:
                writeRepeatedSInt64Impl(id, val);
                return;
            default:
                throw new IllegalArgumentException("Attempt to call write(long, float) with " + getFieldIdString(fieldId));
        }
    }

    public void write(long fieldId, int val) {
        assertNotCompacted();
        int id = (int) fieldId;
        switch ((int) ((17587891077120L & fieldId) >> 32)) {
            case 257:
                writeDoubleImpl(id, val);
                return;
            case 258:
                writeFloatImpl(id, val);
                return;
            case 259:
                writeInt64Impl(id, val);
                return;
            case 260:
                writeUInt64Impl(id, val);
                return;
            case 261:
                writeInt32Impl(id, val);
                return;
            case 262:
                writeFixed64Impl(id, val);
                return;
            case 263:
                writeFixed32Impl(id, val);
                return;
            case 264:
                writeBoolImpl(id, val != 0);
                return;
            case 269:
                writeUInt32Impl(id, val);
                return;
            case 270:
                writeEnumImpl(id, val);
                return;
            case 271:
                writeSFixed32Impl(id, val);
                return;
            case 272:
                writeSFixed64Impl(id, val);
                return;
            case 273:
                writeSInt32Impl(id, val);
                return;
            case 274:
                writeSInt64Impl(id, val);
                return;
            case 513:
            case 1281:
                writeRepeatedDoubleImpl(id, val);
                return;
            case 514:
            case 1282:
                writeRepeatedFloatImpl(id, val);
                return;
            case 515:
            case 1283:
                writeRepeatedInt64Impl(id, val);
                return;
            case 516:
            case 1284:
                writeRepeatedUInt64Impl(id, val);
                return;
            case 517:
            case 1285:
                writeRepeatedInt32Impl(id, val);
                return;
            case 518:
            case 1286:
                writeRepeatedFixed64Impl(id, val);
                return;
            case 519:
            case 1287:
                writeRepeatedFixed32Impl(id, val);
                return;
            case 520:
            case MetricsProto.MetricsEvent.ROTATION_SUGGESTION_SHOWN /* 1288 */:
                writeRepeatedBoolImpl(id, val != 0);
                return;
            case 525:
            case 1293:
                writeRepeatedUInt32Impl(id, val);
                return;
            case MetricsProto.MetricsEvent.DIALOG_SUPPORT_DISCLAIMER /* 526 */:
            case 1294:
                writeRepeatedEnumImpl(id, val);
                return;
            case MetricsProto.MetricsEvent.DIALOG_SUPPORT_PHONE /* 527 */:
            case MetricsProto.MetricsEvent.OUTPUT_CHOOSER /* 1295 */:
                writeRepeatedSFixed32Impl(id, val);
                return;
            case 528:
            case MetricsProto.MetricsEvent.ACTION_OUTPUT_CHOOSER_CONNECT /* 1296 */:
                writeRepeatedSFixed64Impl(id, val);
                return;
            case 529:
            case MetricsProto.MetricsEvent.ACTION_OUTPUT_CHOOSER_DISCONNECT /* 1297 */:
                writeRepeatedSInt32Impl(id, val);
                return;
            case 530:
            case MetricsProto.MetricsEvent.SETTINGS_TV_HOME_THEATER_CONTROL_CATEGORY /* 1298 */:
                writeRepeatedSInt64Impl(id, val);
                return;
            default:
                throw new IllegalArgumentException("Attempt to call write(long, int) with " + getFieldIdString(fieldId));
        }
    }

    public void write(long fieldId, long val) {
        assertNotCompacted();
        int id = (int) fieldId;
        switch ((int) ((17587891077120L & fieldId) >> 32)) {
            case 257:
                writeDoubleImpl(id, val);
                return;
            case 258:
                writeFloatImpl(id, (float) val);
                return;
            case 259:
                writeInt64Impl(id, val);
                return;
            case 260:
                writeUInt64Impl(id, val);
                return;
            case 261:
                writeInt32Impl(id, (int) val);
                return;
            case 262:
                writeFixed64Impl(id, val);
                return;
            case 263:
                writeFixed32Impl(id, (int) val);
                return;
            case 264:
                writeBoolImpl(id, val != 0);
                return;
            case 269:
                writeUInt32Impl(id, (int) val);
                return;
            case 270:
                writeEnumImpl(id, (int) val);
                return;
            case 271:
                writeSFixed32Impl(id, (int) val);
                return;
            case 272:
                writeSFixed64Impl(id, val);
                return;
            case 273:
                writeSInt32Impl(id, (int) val);
                return;
            case 274:
                writeSInt64Impl(id, val);
                return;
            case 513:
            case 1281:
                writeRepeatedDoubleImpl(id, val);
                return;
            case 514:
            case 1282:
                writeRepeatedFloatImpl(id, (float) val);
                return;
            case 515:
            case 1283:
                writeRepeatedInt64Impl(id, val);
                return;
            case 516:
            case 1284:
                writeRepeatedUInt64Impl(id, val);
                return;
            case 517:
            case 1285:
                writeRepeatedInt32Impl(id, (int) val);
                return;
            case 518:
            case 1286:
                writeRepeatedFixed64Impl(id, val);
                return;
            case 519:
            case 1287:
                writeRepeatedFixed32Impl(id, (int) val);
                return;
            case 520:
            case MetricsProto.MetricsEvent.ROTATION_SUGGESTION_SHOWN /* 1288 */:
                writeRepeatedBoolImpl(id, val != 0);
                return;
            case 525:
            case 1293:
                writeRepeatedUInt32Impl(id, (int) val);
                return;
            case MetricsProto.MetricsEvent.DIALOG_SUPPORT_DISCLAIMER /* 526 */:
            case 1294:
                writeRepeatedEnumImpl(id, (int) val);
                return;
            case MetricsProto.MetricsEvent.DIALOG_SUPPORT_PHONE /* 527 */:
            case MetricsProto.MetricsEvent.OUTPUT_CHOOSER /* 1295 */:
                writeRepeatedSFixed32Impl(id, (int) val);
                return;
            case 528:
            case MetricsProto.MetricsEvent.ACTION_OUTPUT_CHOOSER_CONNECT /* 1296 */:
                writeRepeatedSFixed64Impl(id, val);
                return;
            case 529:
            case MetricsProto.MetricsEvent.ACTION_OUTPUT_CHOOSER_DISCONNECT /* 1297 */:
                writeRepeatedSInt32Impl(id, (int) val);
                return;
            case 530:
            case MetricsProto.MetricsEvent.SETTINGS_TV_HOME_THEATER_CONTROL_CATEGORY /* 1298 */:
                writeRepeatedSInt64Impl(id, val);
                return;
            default:
                throw new IllegalArgumentException("Attempt to call write(long, long) with " + getFieldIdString(fieldId));
        }
    }

    public void write(long fieldId, boolean val) {
        assertNotCompacted();
        int id = (int) fieldId;
        switch ((int) ((17587891077120L & fieldId) >> 32)) {
            case 264:
                writeBoolImpl(id, val);
                return;
            case 520:
            case MetricsProto.MetricsEvent.ROTATION_SUGGESTION_SHOWN /* 1288 */:
                writeRepeatedBoolImpl(id, val);
                return;
            default:
                throw new IllegalArgumentException("Attempt to call write(long, boolean) with " + getFieldIdString(fieldId));
        }
    }

    public void write(long fieldId, String val) {
        assertNotCompacted();
        int id = (int) fieldId;
        switch ((int) ((17587891077120L & fieldId) >> 32)) {
            case 265:
                writeStringImpl(id, val);
                return;
            case 521:
            case MetricsProto.MetricsEvent.AUTOFILL_INVALID_PERMISSION /* 1289 */:
                writeRepeatedStringImpl(id, val);
                return;
            default:
                throw new IllegalArgumentException("Attempt to call write(long, String) with " + getFieldIdString(fieldId));
        }
    }

    public void write(long fieldId, byte[] val) {
        assertNotCompacted();
        int id = (int) fieldId;
        switch ((int) ((17587891077120L & fieldId) >> 32)) {
            case 267:
                writeObjectImpl(id, val);
                return;
            case 268:
                writeBytesImpl(id, val);
                return;
            case 523:
            case 1291:
                writeRepeatedObjectImpl(id, val);
                return;
            case 524:
            case 1292:
                writeRepeatedBytesImpl(id, val);
                return;
            default:
                throw new IllegalArgumentException("Attempt to call write(long, byte[]) with " + getFieldIdString(fieldId));
        }
    }

    public long start(long fieldId) {
        assertNotCompacted();
        int id = (int) fieldId;
        if ((ProtoStream.FIELD_TYPE_MASK & fieldId) == ProtoStream.FIELD_TYPE_MESSAGE) {
            long count = ProtoStream.FIELD_COUNT_MASK & fieldId;
            if (count == 1099511627776L) {
                return startObjectImpl(id, false);
            }
            if (count == 2199023255552L || count == ProtoStream.FIELD_COUNT_PACKED) {
                return startObjectImpl(id, true);
            }
        }
        throw new IllegalArgumentException("Attempt to call start(long) with " + getFieldIdString(fieldId));
    }

    public void end(long token) {
        endObjectImpl(token, getRepeatedFromToken(token));
    }

    @Deprecated
    public void writeDouble(long fieldId, double val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 1103806595072L);
        writeDoubleImpl(id, val);
    }

    private void writeDoubleImpl(int id, double val) {
        if (val != 0.0d) {
            writeTag(id, 1);
            this.mBuffer.writeRawFixed64(Double.doubleToLongBits(val));
        }
    }

    @Deprecated
    public void writeRepeatedDouble(long fieldId, double val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 2203318222848L);
        writeRepeatedDoubleImpl(id, val);
    }

    private void writeRepeatedDoubleImpl(int id, double val) {
        writeTag(id, 1);
        this.mBuffer.writeRawFixed64(Double.doubleToLongBits(val));
    }

    @Deprecated
    public void writePackedDouble(long fieldId, double[] val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 5501853106176L);
        int N = val != null ? val.length : 0;
        if (N > 0) {
            writeKnownLengthHeader(id, N * 8);
            for (int i = 0; i < N; i++) {
                this.mBuffer.writeRawFixed64(Double.doubleToLongBits(val[i]));
            }
        }
    }

    @Deprecated
    public void writeFloat(long fieldId, float val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 1108101562368L);
        writeFloatImpl(id, val);
    }

    private void writeFloatImpl(int id, float val) {
        if (val != 0.0f) {
            writeTag(id, 5);
            this.mBuffer.writeRawFixed32(Float.floatToIntBits(val));
        }
    }

    @Deprecated
    public void writeRepeatedFloat(long fieldId, float val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 2207613190144L);
        writeRepeatedFloatImpl(id, val);
    }

    private void writeRepeatedFloatImpl(int id, float val) {
        writeTag(id, 5);
        this.mBuffer.writeRawFixed32(Float.floatToIntBits(val));
    }

    @Deprecated
    public void writePackedFloat(long fieldId, float[] val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 5506148073472L);
        int N = val != null ? val.length : 0;
        if (N > 0) {
            writeKnownLengthHeader(id, N * 4);
            for (int i = 0; i < N; i++) {
                this.mBuffer.writeRawFixed32(Float.floatToIntBits(val[i]));
            }
        }
    }

    private void writeUnsignedVarintFromSignedInt(int val) {
        if (val >= 0) {
            this.mBuffer.writeRawVarint32(val);
        } else {
            this.mBuffer.writeRawVarint64(val);
        }
    }

    @Deprecated
    public void writeInt32(long fieldId, int val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 1120986464256L);
        writeInt32Impl(id, val);
    }

    private void writeInt32Impl(int id, int val) {
        if (val != 0) {
            writeTag(id, 0);
            writeUnsignedVarintFromSignedInt(val);
        }
    }

    @Deprecated
    public void writeRepeatedInt32(long fieldId, int val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 2220498092032L);
        writeRepeatedInt32Impl(id, val);
    }

    private void writeRepeatedInt32Impl(int id, int val) {
        writeTag(id, 0);
        writeUnsignedVarintFromSignedInt(val);
    }

    @Deprecated
    public void writePackedInt32(long fieldId, int[] val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 5519032975360L);
        int N = val != null ? val.length : 0;
        if (N > 0) {
            int size = 0;
            for (int i = 0; i < N; i++) {
                int v = val[i];
                size += v >= 0 ? EncodedBuffer.getRawVarint32Size(v) : 10;
            }
            writeKnownLengthHeader(id, size);
            for (int i2 = 0; i2 < N; i2++) {
                writeUnsignedVarintFromSignedInt(val[i2]);
            }
        }
    }

    @Deprecated
    public void writeInt64(long fieldId, long val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 1112396529664L);
        writeInt64Impl(id, val);
    }

    private void writeInt64Impl(int id, long val) {
        if (val != 0) {
            writeTag(id, 0);
            this.mBuffer.writeRawVarint64(val);
        }
    }

    @Deprecated
    public void writeRepeatedInt64(long fieldId, long val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 2211908157440L);
        writeRepeatedInt64Impl(id, val);
    }

    private void writeRepeatedInt64Impl(int id, long val) {
        writeTag(id, 0);
        this.mBuffer.writeRawVarint64(val);
    }

    @Deprecated
    public void writePackedInt64(long fieldId, long[] val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 5510443040768L);
        int N = val != null ? val.length : 0;
        if (N > 0) {
            int size = 0;
            for (int i = 0; i < N; i++) {
                size += EncodedBuffer.getRawVarint64Size(val[i]);
            }
            writeKnownLengthHeader(id, size);
            for (int i2 = 0; i2 < N; i2++) {
                this.mBuffer.writeRawVarint64(val[i2]);
            }
        }
    }

    @Deprecated
    public void writeUInt32(long fieldId, int val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 1155346202624L);
        writeUInt32Impl(id, val);
    }

    private void writeUInt32Impl(int id, int val) {
        if (val != 0) {
            writeTag(id, 0);
            this.mBuffer.writeRawVarint32(val);
        }
    }

    @Deprecated
    public void writeRepeatedUInt32(long fieldId, int val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 2254857830400L);
        writeRepeatedUInt32Impl(id, val);
    }

    private void writeRepeatedUInt32Impl(int id, int val) {
        writeTag(id, 0);
        this.mBuffer.writeRawVarint32(val);
    }

    @Deprecated
    public void writePackedUInt32(long fieldId, int[] val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 5553392713728L);
        int N = val != null ? val.length : 0;
        if (N > 0) {
            int size = 0;
            for (int i = 0; i < N; i++) {
                size += EncodedBuffer.getRawVarint32Size(val[i]);
            }
            writeKnownLengthHeader(id, size);
            for (int i2 = 0; i2 < N; i2++) {
                this.mBuffer.writeRawVarint32(val[i2]);
            }
        }
    }

    @Deprecated
    public void writeUInt64(long fieldId, long val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 1116691496960L);
        writeUInt64Impl(id, val);
    }

    private void writeUInt64Impl(int id, long val) {
        if (val != 0) {
            writeTag(id, 0);
            this.mBuffer.writeRawVarint64(val);
        }
    }

    @Deprecated
    public void writeRepeatedUInt64(long fieldId, long val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 2216203124736L);
        writeRepeatedUInt64Impl(id, val);
    }

    private void writeRepeatedUInt64Impl(int id, long val) {
        writeTag(id, 0);
        this.mBuffer.writeRawVarint64(val);
    }

    @Deprecated
    public void writePackedUInt64(long fieldId, long[] val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 5514738008064L);
        int N = val != null ? val.length : 0;
        if (N > 0) {
            int size = 0;
            for (int i = 0; i < N; i++) {
                size += EncodedBuffer.getRawVarint64Size(val[i]);
            }
            writeKnownLengthHeader(id, size);
            for (int i2 = 0; i2 < N; i2++) {
                this.mBuffer.writeRawVarint64(val[i2]);
            }
        }
    }

    @Deprecated
    public void writeSInt32(long fieldId, int val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 1172526071808L);
        writeSInt32Impl(id, val);
    }

    private void writeSInt32Impl(int id, int val) {
        if (val != 0) {
            writeTag(id, 0);
            this.mBuffer.writeRawZigZag32(val);
        }
    }

    @Deprecated
    public void writeRepeatedSInt32(long fieldId, int val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 2272037699584L);
        writeRepeatedSInt32Impl(id, val);
    }

    private void writeRepeatedSInt32Impl(int id, int val) {
        writeTag(id, 0);
        this.mBuffer.writeRawZigZag32(val);
    }

    @Deprecated
    public void writePackedSInt32(long fieldId, int[] val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 5570572582912L);
        int N = val != null ? val.length : 0;
        if (N > 0) {
            int size = 0;
            for (int i = 0; i < N; i++) {
                size += EncodedBuffer.getRawZigZag32Size(val[i]);
            }
            writeKnownLengthHeader(id, size);
            for (int i2 = 0; i2 < N; i2++) {
                this.mBuffer.writeRawZigZag32(val[i2]);
            }
        }
    }

    @Deprecated
    public void writeSInt64(long fieldId, long val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 1176821039104L);
        writeSInt64Impl(id, val);
    }

    private void writeSInt64Impl(int id, long val) {
        if (val != 0) {
            writeTag(id, 0);
            this.mBuffer.writeRawZigZag64(val);
        }
    }

    @Deprecated
    public void writeRepeatedSInt64(long fieldId, long val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 2276332666880L);
        writeRepeatedSInt64Impl(id, val);
    }

    private void writeRepeatedSInt64Impl(int id, long val) {
        writeTag(id, 0);
        this.mBuffer.writeRawZigZag64(val);
    }

    @Deprecated
    public void writePackedSInt64(long fieldId, long[] val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 5574867550208L);
        int N = val != null ? val.length : 0;
        if (N > 0) {
            int size = 0;
            for (int i = 0; i < N; i++) {
                size += EncodedBuffer.getRawZigZag64Size(val[i]);
            }
            writeKnownLengthHeader(id, size);
            for (int i2 = 0; i2 < N; i2++) {
                this.mBuffer.writeRawZigZag64(val[i2]);
            }
        }
    }

    @Deprecated
    public void writeFixed32(long fieldId, int val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 1129576398848L);
        writeFixed32Impl(id, val);
    }

    private void writeFixed32Impl(int id, int val) {
        if (val != 0) {
            writeTag(id, 5);
            this.mBuffer.writeRawFixed32(val);
        }
    }

    @Deprecated
    public void writeRepeatedFixed32(long fieldId, int val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 2229088026624L);
        writeRepeatedFixed32Impl(id, val);
    }

    private void writeRepeatedFixed32Impl(int id, int val) {
        writeTag(id, 5);
        this.mBuffer.writeRawFixed32(val);
    }

    @Deprecated
    public void writePackedFixed32(long fieldId, int[] val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 5527622909952L);
        int N = val != null ? val.length : 0;
        if (N > 0) {
            writeKnownLengthHeader(id, N * 4);
            for (int i = 0; i < N; i++) {
                this.mBuffer.writeRawFixed32(val[i]);
            }
        }
    }

    @Deprecated
    public void writeFixed64(long fieldId, long val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 1125281431552L);
        writeFixed64Impl(id, val);
    }

    private void writeFixed64Impl(int id, long val) {
        if (val != 0) {
            writeTag(id, 1);
            this.mBuffer.writeRawFixed64(val);
        }
    }

    @Deprecated
    public void writeRepeatedFixed64(long fieldId, long val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 2224793059328L);
        writeRepeatedFixed64Impl(id, val);
    }

    private void writeRepeatedFixed64Impl(int id, long val) {
        writeTag(id, 1);
        this.mBuffer.writeRawFixed64(val);
    }

    @Deprecated
    public void writePackedFixed64(long fieldId, long[] val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 5523327942656L);
        int N = val != null ? val.length : 0;
        if (N > 0) {
            writeKnownLengthHeader(id, N * 8);
            for (int i = 0; i < N; i++) {
                this.mBuffer.writeRawFixed64(val[i]);
            }
        }
    }

    @Deprecated
    public void writeSFixed32(long fieldId, int val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 1163936137216L);
        writeSFixed32Impl(id, val);
    }

    private void writeSFixed32Impl(int id, int val) {
        if (val != 0) {
            writeTag(id, 5);
            this.mBuffer.writeRawFixed32(val);
        }
    }

    @Deprecated
    public void writeRepeatedSFixed32(long fieldId, int val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 2263447764992L);
        writeRepeatedSFixed32Impl(id, val);
    }

    private void writeRepeatedSFixed32Impl(int id, int val) {
        writeTag(id, 5);
        this.mBuffer.writeRawFixed32(val);
    }

    @Deprecated
    public void writePackedSFixed32(long fieldId, int[] val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 5561982648320L);
        int N = val != null ? val.length : 0;
        if (N > 0) {
            writeKnownLengthHeader(id, N * 4);
            for (int i = 0; i < N; i++) {
                this.mBuffer.writeRawFixed32(val[i]);
            }
        }
    }

    @Deprecated
    public void writeSFixed64(long fieldId, long val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 1168231104512L);
        writeSFixed64Impl(id, val);
    }

    private void writeSFixed64Impl(int id, long val) {
        if (val != 0) {
            writeTag(id, 1);
            this.mBuffer.writeRawFixed64(val);
        }
    }

    @Deprecated
    public void writeRepeatedSFixed64(long fieldId, long val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 2267742732288L);
        writeRepeatedSFixed64Impl(id, val);
    }

    private void writeRepeatedSFixed64Impl(int id, long val) {
        writeTag(id, 1);
        this.mBuffer.writeRawFixed64(val);
    }

    @Deprecated
    public void writePackedSFixed64(long fieldId, long[] val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 5566277615616L);
        int N = val != null ? val.length : 0;
        if (N > 0) {
            writeKnownLengthHeader(id, N * 8);
            for (int i = 0; i < N; i++) {
                this.mBuffer.writeRawFixed64(val[i]);
            }
        }
    }

    @Deprecated
    public void writeBool(long fieldId, boolean val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 1133871366144L);
        writeBoolImpl(id, val);
    }

    private void writeBoolImpl(int id, boolean val) {
        if (val) {
            writeTag(id, 0);
            this.mBuffer.writeRawByte((byte) 1);
        }
    }

    @Deprecated
    public void writeRepeatedBool(long fieldId, boolean val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 2233382993920L);
        writeRepeatedBoolImpl(id, val);
    }

    private void writeRepeatedBoolImpl(int id, boolean val) {
        writeTag(id, 0);
        this.mBuffer.writeRawByte(val ? (byte) 1 : (byte) 0);
    }

    @Deprecated
    public void writePackedBool(long fieldId, boolean[] val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 5531917877248L);
        int N = val != null ? val.length : 0;
        if (N > 0) {
            writeKnownLengthHeader(id, N);
            for (int i = 0; i < N; i++) {
                this.mBuffer.writeRawByte(val[i] ? (byte) 1 : (byte) 0);
            }
        }
    }

    @Deprecated
    public void writeString(long fieldId, String val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 1138166333440L);
        writeStringImpl(id, val);
    }

    private void writeStringImpl(int id, String val) {
        if (val != null && val.length() > 0) {
            writeUtf8String(id, val);
        }
    }

    @Deprecated
    public void writeRepeatedString(long fieldId, String val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 2237677961216L);
        writeRepeatedStringImpl(id, val);
    }

    private void writeRepeatedStringImpl(int id, String val) {
        if (val == null || val.length() == 0) {
            writeKnownLengthHeader(id, 0);
        } else {
            writeUtf8String(id, val);
        }
    }

    private void writeUtf8String(int id, String val) {
        try {
            byte[] buf = val.getBytes("UTF-8");
            writeKnownLengthHeader(id, buf.length);
            this.mBuffer.writeRawBuffer(buf);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("not possible");
        }
    }

    @Deprecated
    public void writeBytes(long fieldId, byte[] val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 1151051235328L);
        writeBytesImpl(id, val);
    }

    private void writeBytesImpl(int id, byte[] val) {
        if (val != null && val.length > 0) {
            writeKnownLengthHeader(id, val.length);
            this.mBuffer.writeRawBuffer(val);
        }
    }

    @Deprecated
    public void writeRepeatedBytes(long fieldId, byte[] val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 2250562863104L);
        writeRepeatedBytesImpl(id, val);
    }

    private void writeRepeatedBytesImpl(int id, byte[] val) {
        writeKnownLengthHeader(id, val == null ? 0 : val.length);
        this.mBuffer.writeRawBuffer(val);
    }

    @Deprecated
    public void writeEnum(long fieldId, int val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 1159641169920L);
        writeEnumImpl(id, val);
    }

    private void writeEnumImpl(int id, int val) {
        if (val != 0) {
            writeTag(id, 0);
            writeUnsignedVarintFromSignedInt(val);
        }
    }

    @Deprecated
    public void writeRepeatedEnum(long fieldId, int val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 2259152797696L);
        writeRepeatedEnumImpl(id, val);
    }

    private void writeRepeatedEnumImpl(int id, int val) {
        writeTag(id, 0);
        writeUnsignedVarintFromSignedInt(val);
    }

    @Deprecated
    public void writePackedEnum(long fieldId, int[] val) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 5557687681024L);
        int N = val != null ? val.length : 0;
        if (N > 0) {
            int size = 0;
            for (int i = 0; i < N; i++) {
                int v = val[i];
                size += v >= 0 ? EncodedBuffer.getRawVarint32Size(v) : 10;
            }
            writeKnownLengthHeader(id, size);
            for (int i2 = 0; i2 < N; i2++) {
                writeUnsignedVarintFromSignedInt(val[i2]);
            }
        }
    }

    @Deprecated
    public long startObject(long fieldId) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 1146756268032L);
        return startObjectImpl(id, false);
    }

    @Deprecated
    public void endObject(long token) {
        assertNotCompacted();
        endObjectImpl(token, false);
    }

    @Deprecated
    public long startRepeatedObject(long fieldId) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 2246267895808L);
        return startObjectImpl(id, true);
    }

    @Deprecated
    public void endRepeatedObject(long token) {
        assertNotCompacted();
        endObjectImpl(token, true);
    }

    private long startObjectImpl(int id, boolean repeated) {
        writeTag(id, 2);
        int sizePos = this.mBuffer.getWritePos();
        this.mDepth++;
        this.mNextObjectId--;
        this.mBuffer.writeRawFixed32((int) (this.mExpectedObjectToken >> 32));
        this.mBuffer.writeRawFixed32((int) this.mExpectedObjectToken);
        long j = this.mExpectedObjectToken;
        long makeToken = makeToken(getTagSize(id), repeated, this.mDepth, this.mNextObjectId, sizePos);
        this.mExpectedObjectToken = makeToken;
        return makeToken;
    }

    private void endObjectImpl(long token, boolean repeated) {
        int depth = getDepthFromToken(token);
        boolean expectedRepeated = getRepeatedFromToken(token);
        int sizePos = getOffsetFromToken(token);
        int childRawSize = (this.mBuffer.getWritePos() - sizePos) - 8;
        if (repeated != expectedRepeated) {
            if (repeated) {
                throw new IllegalArgumentException("endRepeatedObject called where endObject should have been");
            }
            throw new IllegalArgumentException("endObject called where endRepeatedObject should have been");
        } else if ((this.mDepth & 511) != depth || this.mExpectedObjectToken != token) {
            throw new IllegalArgumentException("Mismatched startObject/endObject calls. Current depth " + this.mDepth + " token=" + token2String(token) + " expectedToken=" + token2String(this.mExpectedObjectToken));
        } else {
            this.mExpectedObjectToken = (this.mBuffer.getRawFixed32At(sizePos) << 32) | (this.mBuffer.getRawFixed32At(sizePos + 4) & 4294967295L);
            this.mDepth--;
            if (childRawSize > 0) {
                this.mBuffer.editRawFixed32(sizePos, -childRawSize);
                this.mBuffer.editRawFixed32(sizePos + 4, -1);
            } else if (repeated) {
                this.mBuffer.editRawFixed32(sizePos, 0);
                this.mBuffer.editRawFixed32(sizePos + 4, 0);
            } else {
                this.mBuffer.rewindWriteTo(sizePos - getTagSizeFromToken(token));
            }
        }
    }

    @Deprecated
    public void writeObject(long fieldId, byte[] value) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 1146756268032L);
        writeObjectImpl(id, value);
    }

    void writeObjectImpl(int id, byte[] value) {
        if (value != null && value.length != 0) {
            writeKnownLengthHeader(id, value.length);
            this.mBuffer.writeRawBuffer(value);
        }
    }

    @Deprecated
    public void writeRepeatedObject(long fieldId, byte[] value) {
        assertNotCompacted();
        int id = checkFieldId(fieldId, 2246267895808L);
        writeRepeatedObjectImpl(id, value);
    }

    void writeRepeatedObjectImpl(int id, byte[] value) {
        writeKnownLengthHeader(id, value == null ? 0 : value.length);
        this.mBuffer.writeRawBuffer(value);
    }

    public static long makeFieldId(int id, long fieldFlags) {
        return (id & 4294967295L) | fieldFlags;
    }

    public static int checkFieldId(long fieldId, long expectedFlags) {
        StringBuilder sb;
        long fieldCount = fieldId & ProtoStream.FIELD_COUNT_MASK;
        long fieldType = fieldId & ProtoStream.FIELD_TYPE_MASK;
        long expectedCount = expectedFlags & ProtoStream.FIELD_COUNT_MASK;
        long expectedType = expectedFlags & ProtoStream.FIELD_TYPE_MASK;
        if (((int) fieldId) == 0) {
            throw new IllegalArgumentException("Invalid proto field " + ((int) fieldId) + " fieldId=" + Long.toHexString(fieldId));
        }
        if (fieldType != expectedType || (fieldCount != expectedCount && (fieldCount != ProtoStream.FIELD_COUNT_PACKED || expectedCount != 2199023255552L))) {
            String countString = getFieldCountString(fieldCount);
            String typeString = getFieldTypeString(fieldType);
            if (typeString != null && countString != null) {
                StringBuilder sb2 = new StringBuilder();
                if (expectedType == ProtoStream.FIELD_TYPE_MESSAGE) {
                    sb = sb2;
                    sb.append("start");
                } else {
                    sb = sb2;
                    sb.append("write");
                }
                sb.append(getFieldCountString(expectedCount));
                sb.append(getFieldTypeString(expectedType));
                sb.append(" called for field ");
                sb.append((int) fieldId);
                sb.append(" which should be used with ");
                if (fieldType == ProtoStream.FIELD_TYPE_MESSAGE) {
                    sb.append("start");
                } else {
                    sb.append("write");
                }
                sb.append(countString);
                sb.append(typeString);
                if (fieldCount == ProtoStream.FIELD_COUNT_PACKED) {
                    sb.append(" or writeRepeated");
                    sb.append(typeString);
                }
                sb.append('.');
                throw new IllegalArgumentException(sb.toString());
            }
            StringBuilder sb3 = new StringBuilder();
            if (expectedType == ProtoStream.FIELD_TYPE_MESSAGE) {
                sb3.append("start");
            } else {
                sb3.append("write");
            }
            sb3.append(getFieldCountString(expectedCount));
            sb3.append(getFieldTypeString(expectedType));
            sb3.append(" called with an invalid fieldId: 0x");
            sb3.append(Long.toHexString(fieldId));
            sb3.append(". The proto field ID might be ");
            sb3.append((int) fieldId);
            sb3.append('.');
            throw new IllegalArgumentException(sb3.toString());
        }
        return (int) fieldId;
    }

    private static int getTagSize(int id) {
        return EncodedBuffer.getRawVarint32Size(id << 3);
    }

    public void writeTag(int id, int wireType) {
        this.mBuffer.writeRawVarint32((id << 3) | wireType);
    }

    private void writeKnownLengthHeader(int id, int size) {
        writeTag(id, 2);
        this.mBuffer.writeRawFixed32(size);
        this.mBuffer.writeRawFixed32(size);
    }

    private void assertNotCompacted() {
        if (this.mCompacted) {
            throw new IllegalArgumentException("write called after compact");
        }
    }

    public byte[] getBytes() {
        compactIfNecessary();
        EncodedBuffer encodedBuffer = this.mBuffer;
        return encodedBuffer.getBytes(encodedBuffer.getReadableSize());
    }

    private void compactIfNecessary() {
        if (!this.mCompacted) {
            if (this.mDepth != 0) {
                throw new IllegalArgumentException("Trying to compact with " + this.mDepth + " missing calls to endObject");
            }
            this.mBuffer.startEditing();
            int readableSize = this.mBuffer.getReadableSize();
            editEncodedSize(readableSize);
            this.mBuffer.rewindRead();
            compactSizes(readableSize);
            int i = this.mCopyBegin;
            if (i < readableSize) {
                this.mBuffer.writeFromThisBuffer(i, readableSize - i);
            }
            this.mBuffer.startEditing();
            this.mCompacted = true;
        }
    }

    private int editEncodedSize(int rawSize) {
        int objectStart = this.mBuffer.getReadPos();
        int objectEnd = objectStart + rawSize;
        int encodedSize = 0;
        while (true) {
            int tagPos = this.mBuffer.getReadPos();
            if (tagPos < objectEnd) {
                int tag = readRawTag();
                encodedSize += EncodedBuffer.getRawVarint32Size(tag);
                int wireType = tag & 7;
                switch (wireType) {
                    case 0:
                        while (true) {
                            encodedSize++;
                            if ((this.mBuffer.readRawByte() & 128) != 0) {
                            }
                        }
                        break;
                    case 1:
                        encodedSize += 8;
                        this.mBuffer.skipRead(8);
                        break;
                    case 2:
                        int childRawSize = this.mBuffer.readRawFixed32();
                        int childEncodedSizePos = this.mBuffer.getReadPos();
                        int childEncodedSize = this.mBuffer.readRawFixed32();
                        if (childRawSize >= 0) {
                            if (childEncodedSize != childRawSize) {
                                throw new RuntimeException("Pre-computed size where the precomputed size and the raw size in the buffer don't match! childRawSize=" + childRawSize + " childEncodedSize=" + childEncodedSize + " childEncodedSizePos=" + childEncodedSizePos);
                            }
                            this.mBuffer.skipRead(childRawSize);
                        } else {
                            childEncodedSize = editEncodedSize(-childRawSize);
                            this.mBuffer.editRawFixed32(childEncodedSizePos, childEncodedSize);
                        }
                        encodedSize += EncodedBuffer.getRawVarint32Size(childEncodedSize) + childEncodedSize;
                        break;
                    case 3:
                    case 4:
                        throw new RuntimeException("groups not supported at index " + tagPos);
                    case 5:
                        encodedSize += 4;
                        this.mBuffer.skipRead(4);
                        break;
                    default:
                        throw new ProtoParseException("editEncodedSize Bad tag tag=0x" + Integer.toHexString(tag) + " wireType=" + wireType + " -- " + this.mBuffer.getDebugString());
                }
            } else {
                return encodedSize;
            }
        }
    }

    private void compactSizes(int rawSize) {
        int objectStart = this.mBuffer.getReadPos();
        int objectEnd = objectStart + rawSize;
        while (true) {
            int tagPos = this.mBuffer.getReadPos();
            if (tagPos < objectEnd) {
                int tag = readRawTag();
                int wireType = tag & 7;
                switch (wireType) {
                    case 0:
                        do {
                        } while ((this.mBuffer.readRawByte() & 128) != 0);
                        break;
                    case 1:
                        this.mBuffer.skipRead(8);
                        break;
                    case 2:
                        EncodedBuffer encodedBuffer = this.mBuffer;
                        encodedBuffer.writeFromThisBuffer(this.mCopyBegin, encodedBuffer.getReadPos() - this.mCopyBegin);
                        int childRawSize = this.mBuffer.readRawFixed32();
                        int childEncodedSize = this.mBuffer.readRawFixed32();
                        this.mBuffer.writeRawVarint32(childEncodedSize);
                        this.mCopyBegin = this.mBuffer.getReadPos();
                        if (childRawSize >= 0) {
                            this.mBuffer.skipRead(childEncodedSize);
                            break;
                        } else {
                            compactSizes(-childRawSize);
                            break;
                        }
                    case 3:
                    case 4:
                        throw new RuntimeException("groups not supported at index " + tagPos);
                    case 5:
                        this.mBuffer.skipRead(4);
                        break;
                    default:
                        throw new ProtoParseException("compactSizes Bad tag tag=0x" + Integer.toHexString(tag) + " wireType=" + wireType + " -- " + this.mBuffer.getDebugString());
                }
            } else {
                return;
            }
        }
    }

    public void flush() {
        if (this.mStream == null || this.mDepth != 0 || this.mCompacted) {
            return;
        }
        compactIfNecessary();
        EncodedBuffer encodedBuffer = this.mBuffer;
        byte[] data = encodedBuffer.getBytes(encodedBuffer.getReadableSize());
        try {
            this.mStream.write(data);
            this.mStream.flush();
        } catch (IOException ex) {
            throw new RuntimeException("Error flushing proto to stream", ex);
        }
    }

    private int readRawTag() {
        if (this.mBuffer.getReadPos() == this.mBuffer.getReadableSize()) {
            return 0;
        }
        return (int) this.mBuffer.readRawUnsigned();
    }

    public void dump(String tag) {
        Log.m112d(tag, this.mBuffer.getDebugString());
        this.mBuffer.dumpBuffers(tag);
    }
}
