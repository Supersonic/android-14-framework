package com.android.server.p033wm.nano;

import com.android.framework.protobuf.nano.CodedInputByteBufferNano;
import com.android.framework.protobuf.nano.CodedOutputByteBufferNano;
import com.android.framework.protobuf.nano.InternalNano;
import com.android.framework.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.framework.protobuf.nano.MessageNano;
import com.android.framework.protobuf.nano.WireFormatNano;
import java.io.IOException;
/* renamed from: com.android.server.wm.nano.WindowManagerProtos */
/* loaded from: classes5.dex */
public interface WindowManagerProtos {

    /* renamed from: com.android.server.wm.nano.WindowManagerProtos$TaskSnapshotProto */
    /* loaded from: classes5.dex */
    public static final class TaskSnapshotProto extends MessageNano {
        private static volatile TaskSnapshotProto[] _emptyArray;
        public int appearance;

        /* renamed from: id */
        public long f2222id;
        public int insetBottom;
        public int insetLeft;
        public int insetRight;
        public int insetTop;
        public boolean isRealSnapshot;
        public boolean isTranslucent;
        public float legacyScale;
        public int letterboxInsetBottom;
        public int letterboxInsetLeft;
        public int letterboxInsetRight;
        public int letterboxInsetTop;
        public int orientation;
        public int rotation;
        public int systemUiVisibility;
        public int taskHeight;
        public int taskWidth;
        public String topActivityComponent;
        public int windowingMode;

        public static TaskSnapshotProto[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new TaskSnapshotProto[0];
                    }
                }
            }
            return _emptyArray;
        }

        public TaskSnapshotProto() {
            clear();
        }

        public TaskSnapshotProto clear() {
            this.orientation = 0;
            this.insetLeft = 0;
            this.insetTop = 0;
            this.insetRight = 0;
            this.insetBottom = 0;
            this.isRealSnapshot = false;
            this.windowingMode = 0;
            this.systemUiVisibility = 0;
            this.isTranslucent = false;
            this.topActivityComponent = "";
            this.legacyScale = 0.0f;
            this.f2222id = 0L;
            this.rotation = 0;
            this.taskWidth = 0;
            this.taskHeight = 0;
            this.appearance = 0;
            this.letterboxInsetLeft = 0;
            this.letterboxInsetTop = 0;
            this.letterboxInsetRight = 0;
            this.letterboxInsetBottom = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            int i = this.orientation;
            if (i != 0) {
                output.writeInt32(1, i);
            }
            int i2 = this.insetLeft;
            if (i2 != 0) {
                output.writeInt32(2, i2);
            }
            int i3 = this.insetTop;
            if (i3 != 0) {
                output.writeInt32(3, i3);
            }
            int i4 = this.insetRight;
            if (i4 != 0) {
                output.writeInt32(4, i4);
            }
            int i5 = this.insetBottom;
            if (i5 != 0) {
                output.writeInt32(5, i5);
            }
            boolean z = this.isRealSnapshot;
            if (z) {
                output.writeBool(6, z);
            }
            int i6 = this.windowingMode;
            if (i6 != 0) {
                output.writeInt32(7, i6);
            }
            int i7 = this.systemUiVisibility;
            if (i7 != 0) {
                output.writeInt32(8, i7);
            }
            boolean z2 = this.isTranslucent;
            if (z2) {
                output.writeBool(9, z2);
            }
            if (!this.topActivityComponent.equals("")) {
                output.writeString(10, this.topActivityComponent);
            }
            if (Float.floatToIntBits(this.legacyScale) != Float.floatToIntBits(0.0f)) {
                output.writeFloat(11, this.legacyScale);
            }
            long j = this.f2222id;
            if (j != 0) {
                output.writeInt64(12, j);
            }
            int i8 = this.rotation;
            if (i8 != 0) {
                output.writeInt32(13, i8);
            }
            int i9 = this.taskWidth;
            if (i9 != 0) {
                output.writeInt32(14, i9);
            }
            int i10 = this.taskHeight;
            if (i10 != 0) {
                output.writeInt32(15, i10);
            }
            int i11 = this.appearance;
            if (i11 != 0) {
                output.writeInt32(16, i11);
            }
            int i12 = this.letterboxInsetLeft;
            if (i12 != 0) {
                output.writeInt32(17, i12);
            }
            int i13 = this.letterboxInsetTop;
            if (i13 != 0) {
                output.writeInt32(18, i13);
            }
            int i14 = this.letterboxInsetRight;
            if (i14 != 0) {
                output.writeInt32(19, i14);
            }
            int i15 = this.letterboxInsetBottom;
            if (i15 != 0) {
                output.writeInt32(20, i15);
            }
            super.writeTo(output);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.framework.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            int i = this.orientation;
            if (i != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.insetLeft;
            if (i2 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            int i3 = this.insetTop;
            if (i3 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(3, i3);
            }
            int i4 = this.insetRight;
            if (i4 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(4, i4);
            }
            int i5 = this.insetBottom;
            if (i5 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(5, i5);
            }
            boolean z = this.isRealSnapshot;
            if (z) {
                size += CodedOutputByteBufferNano.computeBoolSize(6, z);
            }
            int i6 = this.windowingMode;
            if (i6 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(7, i6);
            }
            int i7 = this.systemUiVisibility;
            if (i7 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(8, i7);
            }
            boolean z2 = this.isTranslucent;
            if (z2) {
                size += CodedOutputByteBufferNano.computeBoolSize(9, z2);
            }
            if (!this.topActivityComponent.equals("")) {
                size += CodedOutputByteBufferNano.computeStringSize(10, this.topActivityComponent);
            }
            if (Float.floatToIntBits(this.legacyScale) != Float.floatToIntBits(0.0f)) {
                size += CodedOutputByteBufferNano.computeFloatSize(11, this.legacyScale);
            }
            long j = this.f2222id;
            if (j != 0) {
                size += CodedOutputByteBufferNano.computeInt64Size(12, j);
            }
            int i8 = this.rotation;
            if (i8 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(13, i8);
            }
            int i9 = this.taskWidth;
            if (i9 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(14, i9);
            }
            int i10 = this.taskHeight;
            if (i10 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(15, i10);
            }
            int i11 = this.appearance;
            if (i11 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(16, i11);
            }
            int i12 = this.letterboxInsetLeft;
            if (i12 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(17, i12);
            }
            int i13 = this.letterboxInsetTop;
            if (i13 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(18, i13);
            }
            int i14 = this.letterboxInsetRight;
            if (i14 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(19, i14);
            }
            int i15 = this.letterboxInsetBottom;
            if (i15 != 0) {
                return size + CodedOutputByteBufferNano.computeInt32Size(20, i15);
            }
            return size;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public TaskSnapshotProto mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        return this;
                    case 8:
                        this.orientation = input.readInt32();
                        break;
                    case 16:
                        this.insetLeft = input.readInt32();
                        break;
                    case 24:
                        this.insetTop = input.readInt32();
                        break;
                    case 32:
                        this.insetRight = input.readInt32();
                        break;
                    case 40:
                        this.insetBottom = input.readInt32();
                        break;
                    case 48:
                        this.isRealSnapshot = input.readBool();
                        break;
                    case 56:
                        this.windowingMode = input.readInt32();
                        break;
                    case 64:
                        this.systemUiVisibility = input.readInt32();
                        break;
                    case 72:
                        this.isTranslucent = input.readBool();
                        break;
                    case 82:
                        this.topActivityComponent = input.readString();
                        break;
                    case 93:
                        this.legacyScale = input.readFloat();
                        break;
                    case 96:
                        this.f2222id = input.readInt64();
                        break;
                    case 104:
                        this.rotation = input.readInt32();
                        break;
                    case 112:
                        this.taskWidth = input.readInt32();
                        break;
                    case 120:
                        this.taskHeight = input.readInt32();
                        break;
                    case 128:
                        this.appearance = input.readInt32();
                        break;
                    case 136:
                        this.letterboxInsetLeft = input.readInt32();
                        break;
                    case 144:
                        this.letterboxInsetTop = input.readInt32();
                        break;
                    case 152:
                        this.letterboxInsetRight = input.readInt32();
                        break;
                    case 160:
                        this.letterboxInsetBottom = input.readInt32();
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

        public static TaskSnapshotProto parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (TaskSnapshotProto) MessageNano.mergeFrom(new TaskSnapshotProto(), data);
        }

        public static TaskSnapshotProto parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new TaskSnapshotProto().mergeFrom(input);
        }
    }

    /* renamed from: com.android.server.wm.nano.WindowManagerProtos$LetterboxProto */
    /* loaded from: classes5.dex */
    public static final class LetterboxProto extends MessageNano {
        public static final int LETTERBOX_HORIZONTAL_REACHABILITY_POSITION_CENTER = 1;
        public static final int LETTERBOX_HORIZONTAL_REACHABILITY_POSITION_LEFT = 0;
        public static final int LETTERBOX_HORIZONTAL_REACHABILITY_POSITION_RIGHT = 2;
        public static final int LETTERBOX_VERTICAL_REACHABILITY_POSITION_BOTTOM = 2;
        public static final int LETTERBOX_VERTICAL_REACHABILITY_POSITION_CENTER = 1;
        public static final int LETTERBOX_VERTICAL_REACHABILITY_POSITION_TOP = 0;
        private static volatile LetterboxProto[] _emptyArray;
        public int letterboxPositionForBookModeReachability;
        public int letterboxPositionForHorizontalReachability;
        public int letterboxPositionForTabletopModeReachability;
        public int letterboxPositionForVerticalReachability;

        public static LetterboxProto[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new LetterboxProto[0];
                    }
                }
            }
            return _emptyArray;
        }

        public LetterboxProto() {
            clear();
        }

        public LetterboxProto clear() {
            this.letterboxPositionForHorizontalReachability = 0;
            this.letterboxPositionForVerticalReachability = 0;
            this.letterboxPositionForBookModeReachability = 0;
            this.letterboxPositionForTabletopModeReachability = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            int i = this.letterboxPositionForHorizontalReachability;
            if (i != 0) {
                output.writeInt32(1, i);
            }
            int i2 = this.letterboxPositionForVerticalReachability;
            if (i2 != 0) {
                output.writeInt32(2, i2);
            }
            int i3 = this.letterboxPositionForBookModeReachability;
            if (i3 != 0) {
                output.writeInt32(3, i3);
            }
            int i4 = this.letterboxPositionForTabletopModeReachability;
            if (i4 != 0) {
                output.writeInt32(4, i4);
            }
            super.writeTo(output);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.framework.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            int i = this.letterboxPositionForHorizontalReachability;
            if (i != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.letterboxPositionForVerticalReachability;
            if (i2 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            int i3 = this.letterboxPositionForBookModeReachability;
            if (i3 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(3, i3);
            }
            int i4 = this.letterboxPositionForTabletopModeReachability;
            if (i4 != 0) {
                return size + CodedOutputByteBufferNano.computeInt32Size(4, i4);
            }
            return size;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public LetterboxProto mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        return this;
                    case 8:
                        int value = input.readInt32();
                        switch (value) {
                            case 0:
                            case 1:
                            case 2:
                                this.letterboxPositionForHorizontalReachability = value;
                                continue;
                        }
                    case 16:
                        int value2 = input.readInt32();
                        switch (value2) {
                            case 0:
                            case 1:
                            case 2:
                                this.letterboxPositionForVerticalReachability = value2;
                                continue;
                        }
                    case 24:
                        int value3 = input.readInt32();
                        switch (value3) {
                            case 0:
                            case 1:
                            case 2:
                                this.letterboxPositionForBookModeReachability = value3;
                                continue;
                        }
                    case 32:
                        int value4 = input.readInt32();
                        switch (value4) {
                            case 0:
                            case 1:
                            case 2:
                                this.letterboxPositionForTabletopModeReachability = value4;
                                continue;
                        }
                    default:
                        if (WireFormatNano.parseUnknownField(input, tag)) {
                            break;
                        } else {
                            return this;
                        }
                }
            }
        }

        public static LetterboxProto parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (LetterboxProto) MessageNano.mergeFrom(new LetterboxProto(), data);
        }

        public static LetterboxProto parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new LetterboxProto().mergeFrom(input);
        }
    }
}
