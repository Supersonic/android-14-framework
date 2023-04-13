package android.stats.camera.nano;

import com.android.framework.protobuf.nano.CodedInputByteBufferNano;
import com.android.framework.protobuf.nano.CodedOutputByteBufferNano;
import com.android.framework.protobuf.nano.InternalNano;
import com.android.framework.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.framework.protobuf.nano.MessageNano;
import com.android.framework.protobuf.nano.WireFormatNano;
import java.io.IOException;
/* loaded from: classes3.dex */
public interface CameraProtos {

    /* loaded from: classes3.dex */
    public static final class CameraStreamProto extends MessageNano {
        public static final int CAPTURE_LATENCY = 1;
        public static final int UNKNOWN = 0;
        private static volatile CameraStreamProto[] _emptyArray;
        public int colorSpace;
        public int dataSpace;
        public long dynamicRangeProfile;
        public long errorCount;
        public int firstCaptureLatencyMillis;
        public int format;
        public int height;
        public float[] histogramBins;
        public long[] histogramCounts;
        public int histogramType;
        public int maxAppBuffers;
        public int maxHalBuffers;
        public long requestCount;
        public long streamUseCase;
        public long usage;
        public int width;

        public static CameraStreamProto[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new CameraStreamProto[0];
                    }
                }
            }
            return _emptyArray;
        }

        public CameraStreamProto() {
            clear();
        }

        public CameraStreamProto clear() {
            this.width = 0;
            this.height = 0;
            this.format = 0;
            this.dataSpace = 0;
            this.usage = 0L;
            this.requestCount = 0L;
            this.errorCount = 0L;
            this.firstCaptureLatencyMillis = 0;
            this.maxHalBuffers = 0;
            this.maxAppBuffers = 0;
            this.histogramType = 0;
            this.histogramBins = WireFormatNano.EMPTY_FLOAT_ARRAY;
            this.histogramCounts = WireFormatNano.EMPTY_LONG_ARRAY;
            this.dynamicRangeProfile = 0L;
            this.streamUseCase = 0L;
            this.colorSpace = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            int i = this.width;
            if (i != 0) {
                output.writeInt32(1, i);
            }
            int i2 = this.height;
            if (i2 != 0) {
                output.writeInt32(2, i2);
            }
            int i3 = this.format;
            if (i3 != 0) {
                output.writeInt32(3, i3);
            }
            int i4 = this.dataSpace;
            if (i4 != 0) {
                output.writeInt32(4, i4);
            }
            long j = this.usage;
            if (j != 0) {
                output.writeInt64(5, j);
            }
            long j2 = this.requestCount;
            if (j2 != 0) {
                output.writeInt64(6, j2);
            }
            long j3 = this.errorCount;
            if (j3 != 0) {
                output.writeInt64(7, j3);
            }
            int i5 = this.firstCaptureLatencyMillis;
            if (i5 != 0) {
                output.writeInt32(8, i5);
            }
            int i6 = this.maxHalBuffers;
            if (i6 != 0) {
                output.writeInt32(9, i6);
            }
            int i7 = this.maxAppBuffers;
            if (i7 != 0) {
                output.writeInt32(10, i7);
            }
            int i8 = this.histogramType;
            if (i8 != 0) {
                output.writeInt32(11, i8);
            }
            float[] fArr = this.histogramBins;
            if (fArr != null && fArr.length > 0) {
                int i9 = 0;
                while (true) {
                    float[] fArr2 = this.histogramBins;
                    if (i9 >= fArr2.length) {
                        break;
                    }
                    output.writeFloat(12, fArr2[i9]);
                    i9++;
                }
            }
            long[] jArr = this.histogramCounts;
            if (jArr != null && jArr.length > 0) {
                int i10 = 0;
                while (true) {
                    long[] jArr2 = this.histogramCounts;
                    if (i10 >= jArr2.length) {
                        break;
                    }
                    output.writeInt64(13, jArr2[i10]);
                    i10++;
                }
            }
            long j4 = this.dynamicRangeProfile;
            if (j4 != 0) {
                output.writeInt64(14, j4);
            }
            long j5 = this.streamUseCase;
            if (j5 != 0) {
                output.writeInt64(15, j5);
            }
            int i11 = this.colorSpace;
            if (i11 != 0) {
                output.writeInt32(16, i11);
            }
            super.writeTo(output);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.framework.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            long[] jArr;
            int size = super.computeSerializedSize();
            int i = this.width;
            if (i != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.height;
            if (i2 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            int i3 = this.format;
            if (i3 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(3, i3);
            }
            int i4 = this.dataSpace;
            if (i4 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(4, i4);
            }
            long j = this.usage;
            if (j != 0) {
                size += CodedOutputByteBufferNano.computeInt64Size(5, j);
            }
            long j2 = this.requestCount;
            if (j2 != 0) {
                size += CodedOutputByteBufferNano.computeInt64Size(6, j2);
            }
            long j3 = this.errorCount;
            if (j3 != 0) {
                size += CodedOutputByteBufferNano.computeInt64Size(7, j3);
            }
            int i5 = this.firstCaptureLatencyMillis;
            if (i5 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(8, i5);
            }
            int i6 = this.maxHalBuffers;
            if (i6 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(9, i6);
            }
            int i7 = this.maxAppBuffers;
            if (i7 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(10, i7);
            }
            int i8 = this.histogramType;
            if (i8 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(11, i8);
            }
            float[] fArr = this.histogramBins;
            if (fArr != null && fArr.length > 0) {
                int dataSize = fArr.length * 4;
                size = size + dataSize + (fArr.length * 1);
            }
            long[] jArr2 = this.histogramCounts;
            if (jArr2 != null && jArr2.length > 0) {
                int dataSize2 = 0;
                int i9 = 0;
                while (true) {
                    jArr = this.histogramCounts;
                    if (i9 >= jArr.length) {
                        break;
                    }
                    long element = jArr[i9];
                    dataSize2 += CodedOutputByteBufferNano.computeInt64SizeNoTag(element);
                    i9++;
                }
                size = size + dataSize2 + (jArr.length * 1);
            }
            long j4 = this.dynamicRangeProfile;
            if (j4 != 0) {
                size += CodedOutputByteBufferNano.computeInt64Size(14, j4);
            }
            long j5 = this.streamUseCase;
            if (j5 != 0) {
                size += CodedOutputByteBufferNano.computeInt64Size(15, j5);
            }
            int i10 = this.colorSpace;
            if (i10 != 0) {
                return size + CodedOutputByteBufferNano.computeInt32Size(16, i10);
            }
            return size;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public CameraStreamProto mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        return this;
                    case 8:
                        this.width = input.readInt32();
                        break;
                    case 16:
                        this.height = input.readInt32();
                        break;
                    case 24:
                        this.format = input.readInt32();
                        break;
                    case 32:
                        this.dataSpace = input.readInt32();
                        break;
                    case 40:
                        this.usage = input.readInt64();
                        break;
                    case 48:
                        this.requestCount = input.readInt64();
                        break;
                    case 56:
                        this.errorCount = input.readInt64();
                        break;
                    case 64:
                        this.firstCaptureLatencyMillis = input.readInt32();
                        break;
                    case 72:
                        this.maxHalBuffers = input.readInt32();
                        break;
                    case 80:
                        this.maxAppBuffers = input.readInt32();
                        break;
                    case 88:
                        int value = input.readInt32();
                        switch (value) {
                            case 0:
                            case 1:
                                this.histogramType = value;
                                continue;
                        }
                    case 98:
                        int length = input.readRawVarint32();
                        int limit = input.pushLimit(length);
                        int arrayLength = length / 4;
                        float[] fArr = this.histogramBins;
                        int i = fArr == null ? 0 : fArr.length;
                        float[] newArray = new float[i + arrayLength];
                        if (i != 0) {
                            System.arraycopy(fArr, 0, newArray, 0, i);
                        }
                        while (i < newArray.length) {
                            newArray[i] = input.readFloat();
                            i++;
                        }
                        this.histogramBins = newArray;
                        input.popLimit(limit);
                        break;
                    case 101:
                        int arrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(input, 101);
                        float[] fArr2 = this.histogramBins;
                        int i2 = fArr2 == null ? 0 : fArr2.length;
                        float[] newArray2 = new float[i2 + arrayLength2];
                        if (i2 != 0) {
                            System.arraycopy(fArr2, 0, newArray2, 0, i2);
                        }
                        while (i2 < newArray2.length - 1) {
                            newArray2[i2] = input.readFloat();
                            input.readTag();
                            i2++;
                        }
                        newArray2[i2] = input.readFloat();
                        this.histogramBins = newArray2;
                        break;
                    case 104:
                        int arrayLength3 = WireFormatNano.getRepeatedFieldArrayLength(input, 104);
                        long[] jArr = this.histogramCounts;
                        int i3 = jArr == null ? 0 : jArr.length;
                        long[] newArray3 = new long[i3 + arrayLength3];
                        if (i3 != 0) {
                            System.arraycopy(jArr, 0, newArray3, 0, i3);
                        }
                        while (i3 < newArray3.length - 1) {
                            newArray3[i3] = input.readInt64();
                            input.readTag();
                            i3++;
                        }
                        newArray3[i3] = input.readInt64();
                        this.histogramCounts = newArray3;
                        break;
                    case 106:
                        int limit2 = input.pushLimit(input.readRawVarint32());
                        int arrayLength4 = 0;
                        int startPos = input.getPosition();
                        while (input.getBytesUntilLimit() > 0) {
                            input.readInt64();
                            arrayLength4++;
                        }
                        input.rewindToPosition(startPos);
                        long[] jArr2 = this.histogramCounts;
                        int i4 = jArr2 == null ? 0 : jArr2.length;
                        long[] newArray4 = new long[i4 + arrayLength4];
                        if (i4 != 0) {
                            System.arraycopy(jArr2, 0, newArray4, 0, i4);
                        }
                        while (i4 < newArray4.length) {
                            newArray4[i4] = input.readInt64();
                            i4++;
                        }
                        this.histogramCounts = newArray4;
                        input.popLimit(limit2);
                        break;
                    case 112:
                        this.dynamicRangeProfile = input.readInt64();
                        break;
                    case 120:
                        this.streamUseCase = input.readInt64();
                        break;
                    case 128:
                        this.colorSpace = input.readInt32();
                        break;
                    default:
                        if (WireFormatNano.parseUnknownField(input, tag)) {
                            break;
                        } else {
                            return this;
                        }
                }
            }
        }

        public static CameraStreamProto parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (CameraStreamProto) MessageNano.mergeFrom(new CameraStreamProto(), data);
        }

        public static CameraStreamProto parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new CameraStreamProto().mergeFrom(input);
        }
    }
}
