package com.android.internal.location.nano;

import com.android.framework.protobuf.nano.CodedInputByteBufferNano;
import com.android.framework.protobuf.nano.CodedOutputByteBufferNano;
import com.android.framework.protobuf.nano.InternalNano;
import com.android.framework.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.framework.protobuf.nano.MessageNano;
import com.android.framework.protobuf.nano.WireFormatNano;
import java.io.IOException;
/* loaded from: classes4.dex */
public interface GnssLogsProto {

    /* loaded from: classes4.dex */
    public static final class GnssLog extends MessageNano {
        private static volatile GnssLog[] _emptyArray;
        public String hardwareRevision;
        public double meanL5TopFourAverageCn0DbHz;
        public int meanPositionAccuracyMeters;
        public int meanTimeToFirstFixSecs;
        public double meanTopFourAverageCn0DbHz;
        public int numL5SvStatusProcessed;
        public int numL5SvStatusUsedInFix;
        public int numL5TopFourAverageCn0Processed;
        public int numLocationReportProcessed;
        public int numPositionAccuracyProcessed;
        public int numSvStatusProcessed;
        public int numSvStatusUsedInFix;
        public int numTimeToFirstFixProcessed;
        public int numTopFourAverageCn0Processed;
        public int percentageLocationFailure;
        public PowerMetrics powerMetrics;
        public double standardDeviationL5TopFourAverageCn0DbHz;
        public int standardDeviationPositionAccuracyMeters;
        public int standardDeviationTimeToFirstFixSecs;
        public double standardDeviationTopFourAverageCn0DbHz;

        public static GnssLog[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new GnssLog[0];
                    }
                }
            }
            return _emptyArray;
        }

        public GnssLog() {
            clear();
        }

        public GnssLog clear() {
            this.numLocationReportProcessed = 0;
            this.percentageLocationFailure = 0;
            this.numTimeToFirstFixProcessed = 0;
            this.meanTimeToFirstFixSecs = 0;
            this.standardDeviationTimeToFirstFixSecs = 0;
            this.numPositionAccuracyProcessed = 0;
            this.meanPositionAccuracyMeters = 0;
            this.standardDeviationPositionAccuracyMeters = 0;
            this.numTopFourAverageCn0Processed = 0;
            this.meanTopFourAverageCn0DbHz = 0.0d;
            this.standardDeviationTopFourAverageCn0DbHz = 0.0d;
            this.powerMetrics = null;
            this.hardwareRevision = "";
            this.numSvStatusProcessed = 0;
            this.numL5SvStatusProcessed = 0;
            this.numSvStatusUsedInFix = 0;
            this.numL5SvStatusUsedInFix = 0;
            this.numL5TopFourAverageCn0Processed = 0;
            this.meanL5TopFourAverageCn0DbHz = 0.0d;
            this.standardDeviationL5TopFourAverageCn0DbHz = 0.0d;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            int i = this.numLocationReportProcessed;
            if (i != 0) {
                output.writeInt32(1, i);
            }
            int i2 = this.percentageLocationFailure;
            if (i2 != 0) {
                output.writeInt32(2, i2);
            }
            int i3 = this.numTimeToFirstFixProcessed;
            if (i3 != 0) {
                output.writeInt32(3, i3);
            }
            int i4 = this.meanTimeToFirstFixSecs;
            if (i4 != 0) {
                output.writeInt32(4, i4);
            }
            int i5 = this.standardDeviationTimeToFirstFixSecs;
            if (i5 != 0) {
                output.writeInt32(5, i5);
            }
            int i6 = this.numPositionAccuracyProcessed;
            if (i6 != 0) {
                output.writeInt32(6, i6);
            }
            int i7 = this.meanPositionAccuracyMeters;
            if (i7 != 0) {
                output.writeInt32(7, i7);
            }
            int i8 = this.standardDeviationPositionAccuracyMeters;
            if (i8 != 0) {
                output.writeInt32(8, i8);
            }
            int i9 = this.numTopFourAverageCn0Processed;
            if (i9 != 0) {
                output.writeInt32(9, i9);
            }
            if (Double.doubleToLongBits(this.meanTopFourAverageCn0DbHz) != Double.doubleToLongBits(0.0d)) {
                output.writeDouble(10, this.meanTopFourAverageCn0DbHz);
            }
            if (Double.doubleToLongBits(this.standardDeviationTopFourAverageCn0DbHz) != Double.doubleToLongBits(0.0d)) {
                output.writeDouble(11, this.standardDeviationTopFourAverageCn0DbHz);
            }
            PowerMetrics powerMetrics = this.powerMetrics;
            if (powerMetrics != null) {
                output.writeMessage(12, powerMetrics);
            }
            if (!this.hardwareRevision.equals("")) {
                output.writeString(13, this.hardwareRevision);
            }
            int i10 = this.numSvStatusProcessed;
            if (i10 != 0) {
                output.writeInt32(14, i10);
            }
            int i11 = this.numL5SvStatusProcessed;
            if (i11 != 0) {
                output.writeInt32(15, i11);
            }
            int i12 = this.numSvStatusUsedInFix;
            if (i12 != 0) {
                output.writeInt32(16, i12);
            }
            int i13 = this.numL5SvStatusUsedInFix;
            if (i13 != 0) {
                output.writeInt32(17, i13);
            }
            int i14 = this.numL5TopFourAverageCn0Processed;
            if (i14 != 0) {
                output.writeInt32(18, i14);
            }
            if (Double.doubleToLongBits(this.meanL5TopFourAverageCn0DbHz) != Double.doubleToLongBits(0.0d)) {
                output.writeDouble(19, this.meanL5TopFourAverageCn0DbHz);
            }
            if (Double.doubleToLongBits(this.standardDeviationL5TopFourAverageCn0DbHz) != Double.doubleToLongBits(0.0d)) {
                output.writeDouble(20, this.standardDeviationL5TopFourAverageCn0DbHz);
            }
            super.writeTo(output);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.framework.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            int i = this.numLocationReportProcessed;
            if (i != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.percentageLocationFailure;
            if (i2 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            int i3 = this.numTimeToFirstFixProcessed;
            if (i3 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(3, i3);
            }
            int i4 = this.meanTimeToFirstFixSecs;
            if (i4 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(4, i4);
            }
            int i5 = this.standardDeviationTimeToFirstFixSecs;
            if (i5 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(5, i5);
            }
            int i6 = this.numPositionAccuracyProcessed;
            if (i6 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(6, i6);
            }
            int i7 = this.meanPositionAccuracyMeters;
            if (i7 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(7, i7);
            }
            int i8 = this.standardDeviationPositionAccuracyMeters;
            if (i8 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(8, i8);
            }
            int i9 = this.numTopFourAverageCn0Processed;
            if (i9 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(9, i9);
            }
            if (Double.doubleToLongBits(this.meanTopFourAverageCn0DbHz) != Double.doubleToLongBits(0.0d)) {
                size += CodedOutputByteBufferNano.computeDoubleSize(10, this.meanTopFourAverageCn0DbHz);
            }
            if (Double.doubleToLongBits(this.standardDeviationTopFourAverageCn0DbHz) != Double.doubleToLongBits(0.0d)) {
                size += CodedOutputByteBufferNano.computeDoubleSize(11, this.standardDeviationTopFourAverageCn0DbHz);
            }
            PowerMetrics powerMetrics = this.powerMetrics;
            if (powerMetrics != null) {
                size += CodedOutputByteBufferNano.computeMessageSize(12, powerMetrics);
            }
            if (!this.hardwareRevision.equals("")) {
                size += CodedOutputByteBufferNano.computeStringSize(13, this.hardwareRevision);
            }
            int i10 = this.numSvStatusProcessed;
            if (i10 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(14, i10);
            }
            int i11 = this.numL5SvStatusProcessed;
            if (i11 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(15, i11);
            }
            int i12 = this.numSvStatusUsedInFix;
            if (i12 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(16, i12);
            }
            int i13 = this.numL5SvStatusUsedInFix;
            if (i13 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(17, i13);
            }
            int i14 = this.numL5TopFourAverageCn0Processed;
            if (i14 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(18, i14);
            }
            if (Double.doubleToLongBits(this.meanL5TopFourAverageCn0DbHz) != Double.doubleToLongBits(0.0d)) {
                size += CodedOutputByteBufferNano.computeDoubleSize(19, this.meanL5TopFourAverageCn0DbHz);
            }
            if (Double.doubleToLongBits(this.standardDeviationL5TopFourAverageCn0DbHz) != Double.doubleToLongBits(0.0d)) {
                return size + CodedOutputByteBufferNano.computeDoubleSize(20, this.standardDeviationL5TopFourAverageCn0DbHz);
            }
            return size;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public GnssLog mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        return this;
                    case 8:
                        this.numLocationReportProcessed = input.readInt32();
                        break;
                    case 16:
                        this.percentageLocationFailure = input.readInt32();
                        break;
                    case 24:
                        this.numTimeToFirstFixProcessed = input.readInt32();
                        break;
                    case 32:
                        this.meanTimeToFirstFixSecs = input.readInt32();
                        break;
                    case 40:
                        this.standardDeviationTimeToFirstFixSecs = input.readInt32();
                        break;
                    case 48:
                        this.numPositionAccuracyProcessed = input.readInt32();
                        break;
                    case 56:
                        this.meanPositionAccuracyMeters = input.readInt32();
                        break;
                    case 64:
                        this.standardDeviationPositionAccuracyMeters = input.readInt32();
                        break;
                    case 72:
                        this.numTopFourAverageCn0Processed = input.readInt32();
                        break;
                    case 81:
                        this.meanTopFourAverageCn0DbHz = input.readDouble();
                        break;
                    case 89:
                        this.standardDeviationTopFourAverageCn0DbHz = input.readDouble();
                        break;
                    case 98:
                        if (this.powerMetrics == null) {
                            this.powerMetrics = new PowerMetrics();
                        }
                        input.readMessage(this.powerMetrics);
                        break;
                    case 106:
                        this.hardwareRevision = input.readString();
                        break;
                    case 112:
                        this.numSvStatusProcessed = input.readInt32();
                        break;
                    case 120:
                        this.numL5SvStatusProcessed = input.readInt32();
                        break;
                    case 128:
                        this.numSvStatusUsedInFix = input.readInt32();
                        break;
                    case 136:
                        this.numL5SvStatusUsedInFix = input.readInt32();
                        break;
                    case 144:
                        this.numL5TopFourAverageCn0Processed = input.readInt32();
                        break;
                    case 153:
                        this.meanL5TopFourAverageCn0DbHz = input.readDouble();
                        break;
                    case 161:
                        this.standardDeviationL5TopFourAverageCn0DbHz = input.readDouble();
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

        public static GnssLog parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (GnssLog) MessageNano.mergeFrom(new GnssLog(), data);
        }

        public static GnssLog parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new GnssLog().mergeFrom(input);
        }
    }

    /* loaded from: classes4.dex */
    public static final class PowerMetrics extends MessageNano {
        private static volatile PowerMetrics[] _emptyArray;
        public double energyConsumedMah;
        public long loggingDurationMs;
        public long[] timeInSignalQualityLevelMs;

        public static PowerMetrics[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new PowerMetrics[0];
                    }
                }
            }
            return _emptyArray;
        }

        public PowerMetrics() {
            clear();
        }

        public PowerMetrics clear() {
            this.loggingDurationMs = 0L;
            this.energyConsumedMah = 0.0d;
            this.timeInSignalQualityLevelMs = WireFormatNano.EMPTY_LONG_ARRAY;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            long j = this.loggingDurationMs;
            if (j != 0) {
                output.writeInt64(1, j);
            }
            if (Double.doubleToLongBits(this.energyConsumedMah) != Double.doubleToLongBits(0.0d)) {
                output.writeDouble(2, this.energyConsumedMah);
            }
            long[] jArr = this.timeInSignalQualityLevelMs;
            if (jArr != null && jArr.length > 0) {
                int i = 0;
                while (true) {
                    long[] jArr2 = this.timeInSignalQualityLevelMs;
                    if (i >= jArr2.length) {
                        break;
                    }
                    output.writeInt64(3, jArr2[i]);
                    i++;
                }
            }
            super.writeTo(output);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.framework.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            long j = this.loggingDurationMs;
            if (j != 0) {
                size += CodedOutputByteBufferNano.computeInt64Size(1, j);
            }
            if (Double.doubleToLongBits(this.energyConsumedMah) != Double.doubleToLongBits(0.0d)) {
                size += CodedOutputByteBufferNano.computeDoubleSize(2, this.energyConsumedMah);
            }
            long[] jArr = this.timeInSignalQualityLevelMs;
            if (jArr != null && jArr.length > 0) {
                int dataSize = 0;
                int i = 0;
                while (true) {
                    long[] jArr2 = this.timeInSignalQualityLevelMs;
                    if (i < jArr2.length) {
                        long element = jArr2[i];
                        dataSize += CodedOutputByteBufferNano.computeInt64SizeNoTag(element);
                        i++;
                    } else {
                        return size + dataSize + (jArr2.length * 1);
                    }
                }
            } else {
                return size;
            }
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public PowerMetrics mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        return this;
                    case 8:
                        this.loggingDurationMs = input.readInt64();
                        break;
                    case 17:
                        this.energyConsumedMah = input.readDouble();
                        break;
                    case 24:
                        int arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 24);
                        long[] jArr = this.timeInSignalQualityLevelMs;
                        int i = jArr == null ? 0 : jArr.length;
                        long[] newArray = new long[i + arrayLength];
                        if (i != 0) {
                            System.arraycopy(jArr, 0, newArray, 0, i);
                        }
                        while (i < newArray.length - 1) {
                            newArray[i] = input.readInt64();
                            input.readTag();
                            i++;
                        }
                        newArray[i] = input.readInt64();
                        this.timeInSignalQualityLevelMs = newArray;
                        break;
                    case 26:
                        int length = input.readRawVarint32();
                        int limit = input.pushLimit(length);
                        int arrayLength2 = 0;
                        int startPos = input.getPosition();
                        while (input.getBytesUntilLimit() > 0) {
                            input.readInt64();
                            arrayLength2++;
                        }
                        input.rewindToPosition(startPos);
                        long[] jArr2 = this.timeInSignalQualityLevelMs;
                        int i2 = jArr2 == null ? 0 : jArr2.length;
                        long[] newArray2 = new long[i2 + arrayLength2];
                        if (i2 != 0) {
                            System.arraycopy(jArr2, 0, newArray2, 0, i2);
                        }
                        while (i2 < newArray2.length) {
                            newArray2[i2] = input.readInt64();
                            i2++;
                        }
                        this.timeInSignalQualityLevelMs = newArray2;
                        input.popLimit(limit);
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

        public static PowerMetrics parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (PowerMetrics) MessageNano.mergeFrom(new PowerMetrics(), data);
        }

        public static PowerMetrics parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new PowerMetrics().mergeFrom(input);
        }
    }
}
