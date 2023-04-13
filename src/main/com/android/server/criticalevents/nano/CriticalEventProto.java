package com.android.server.criticalevents.nano;

import com.android.framework.protobuf.nano.CodedInputByteBufferNano;
import com.android.framework.protobuf.nano.CodedOutputByteBufferNano;
import com.android.framework.protobuf.nano.InternalNano;
import com.android.framework.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.framework.protobuf.nano.MessageNano;
import com.android.framework.protobuf.nano.WireFormatNano;
import java.io.IOException;
/* loaded from: classes5.dex */
public final class CriticalEventProto extends MessageNano {
    public static final int ANR_FIELD_NUMBER = 4;
    public static final int DATA_APP = 1;
    public static final int HALF_WATCHDOG_FIELD_NUMBER = 3;
    public static final int JAVA_CRASH_FIELD_NUMBER = 5;
    public static final int NATIVE_CRASH_FIELD_NUMBER = 6;
    public static final int PROCESS_CLASS_UNKNOWN = 0;
    public static final int SYSTEM_APP = 2;
    public static final int SYSTEM_SERVER = 3;
    public static final int WATCHDOG_FIELD_NUMBER = 2;
    private static volatile CriticalEventProto[] _emptyArray;
    private int eventCase_ = 0;
    private Object event_;
    public long timestampMs;

    /* loaded from: classes5.dex */
    public static final class Watchdog extends MessageNano {
        private static volatile Watchdog[] _emptyArray;
        public String subject;
        public String uuid;

        public static Watchdog[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Watchdog[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Watchdog() {
            clear();
        }

        public Watchdog clear() {
            this.subject = "";
            this.uuid = "";
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (!this.subject.equals("")) {
                output.writeString(1, this.subject);
            }
            if (!this.uuid.equals("")) {
                output.writeString(2, this.uuid);
            }
            super.writeTo(output);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.framework.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            if (!this.subject.equals("")) {
                size += CodedOutputByteBufferNano.computeStringSize(1, this.subject);
            }
            if (!this.uuid.equals("")) {
                return size + CodedOutputByteBufferNano.computeStringSize(2, this.uuid);
            }
            return size;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public Watchdog mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        return this;
                    case 10:
                        this.subject = input.readString();
                        break;
                    case 18:
                        this.uuid = input.readString();
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

        public static Watchdog parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (Watchdog) MessageNano.mergeFrom(new Watchdog(), data);
        }

        public static Watchdog parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new Watchdog().mergeFrom(input);
        }
    }

    /* loaded from: classes5.dex */
    public static final class HalfWatchdog extends MessageNano {
        private static volatile HalfWatchdog[] _emptyArray;
        public String subject;

        public static HalfWatchdog[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new HalfWatchdog[0];
                    }
                }
            }
            return _emptyArray;
        }

        public HalfWatchdog() {
            clear();
        }

        public HalfWatchdog clear() {
            this.subject = "";
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (!this.subject.equals("")) {
                output.writeString(1, this.subject);
            }
            super.writeTo(output);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.framework.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            if (!this.subject.equals("")) {
                return size + CodedOutputByteBufferNano.computeStringSize(1, this.subject);
            }
            return size;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public HalfWatchdog mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        return this;
                    case 10:
                        this.subject = input.readString();
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

        public static HalfWatchdog parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (HalfWatchdog) MessageNano.mergeFrom(new HalfWatchdog(), data);
        }

        public static HalfWatchdog parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new HalfWatchdog().mergeFrom(input);
        }
    }

    /* loaded from: classes5.dex */
    public static final class AppNotResponding extends MessageNano {
        private static volatile AppNotResponding[] _emptyArray;
        public int pid;
        public String process;
        public int processClass;
        public String subject;
        public int uid;

        public static AppNotResponding[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new AppNotResponding[0];
                    }
                }
            }
            return _emptyArray;
        }

        public AppNotResponding() {
            clear();
        }

        public AppNotResponding clear() {
            this.subject = "";
            this.process = "";
            this.pid = 0;
            this.uid = 0;
            this.processClass = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (!this.subject.equals("")) {
                output.writeString(1, this.subject);
            }
            if (!this.process.equals("")) {
                output.writeString(2, this.process);
            }
            int i = this.pid;
            if (i != 0) {
                output.writeInt32(3, i);
            }
            int i2 = this.uid;
            if (i2 != 0) {
                output.writeInt32(4, i2);
            }
            int i3 = this.processClass;
            if (i3 != 0) {
                output.writeInt32(5, i3);
            }
            super.writeTo(output);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.framework.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            if (!this.subject.equals("")) {
                size += CodedOutputByteBufferNano.computeStringSize(1, this.subject);
            }
            if (!this.process.equals("")) {
                size += CodedOutputByteBufferNano.computeStringSize(2, this.process);
            }
            int i = this.pid;
            if (i != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(3, i);
            }
            int i2 = this.uid;
            if (i2 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(4, i2);
            }
            int i3 = this.processClass;
            if (i3 != 0) {
                return size + CodedOutputByteBufferNano.computeInt32Size(5, i3);
            }
            return size;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public AppNotResponding mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        return this;
                    case 10:
                        this.subject = input.readString();
                        break;
                    case 18:
                        this.process = input.readString();
                        break;
                    case 24:
                        this.pid = input.readInt32();
                        break;
                    case 32:
                        this.uid = input.readInt32();
                        break;
                    case 40:
                        int value = input.readInt32();
                        switch (value) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                                this.processClass = value;
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

        public static AppNotResponding parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (AppNotResponding) MessageNano.mergeFrom(new AppNotResponding(), data);
        }

        public static AppNotResponding parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new AppNotResponding().mergeFrom(input);
        }
    }

    /* loaded from: classes5.dex */
    public static final class JavaCrash extends MessageNano {
        private static volatile JavaCrash[] _emptyArray;
        public String exceptionClass;
        public int pid;
        public String process;
        public int processClass;
        public int uid;

        public static JavaCrash[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new JavaCrash[0];
                    }
                }
            }
            return _emptyArray;
        }

        public JavaCrash() {
            clear();
        }

        public JavaCrash clear() {
            this.exceptionClass = "";
            this.process = "";
            this.pid = 0;
            this.uid = 0;
            this.processClass = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (!this.exceptionClass.equals("")) {
                output.writeString(1, this.exceptionClass);
            }
            if (!this.process.equals("")) {
                output.writeString(2, this.process);
            }
            int i = this.pid;
            if (i != 0) {
                output.writeInt32(3, i);
            }
            int i2 = this.uid;
            if (i2 != 0) {
                output.writeInt32(4, i2);
            }
            int i3 = this.processClass;
            if (i3 != 0) {
                output.writeInt32(5, i3);
            }
            super.writeTo(output);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.framework.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            if (!this.exceptionClass.equals("")) {
                size += CodedOutputByteBufferNano.computeStringSize(1, this.exceptionClass);
            }
            if (!this.process.equals("")) {
                size += CodedOutputByteBufferNano.computeStringSize(2, this.process);
            }
            int i = this.pid;
            if (i != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(3, i);
            }
            int i2 = this.uid;
            if (i2 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(4, i2);
            }
            int i3 = this.processClass;
            if (i3 != 0) {
                return size + CodedOutputByteBufferNano.computeInt32Size(5, i3);
            }
            return size;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public JavaCrash mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        return this;
                    case 10:
                        this.exceptionClass = input.readString();
                        break;
                    case 18:
                        this.process = input.readString();
                        break;
                    case 24:
                        this.pid = input.readInt32();
                        break;
                    case 32:
                        this.uid = input.readInt32();
                        break;
                    case 40:
                        int value = input.readInt32();
                        switch (value) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                                this.processClass = value;
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

        public static JavaCrash parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (JavaCrash) MessageNano.mergeFrom(new JavaCrash(), data);
        }

        public static JavaCrash parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new JavaCrash().mergeFrom(input);
        }
    }

    /* loaded from: classes5.dex */
    public static final class NativeCrash extends MessageNano {
        private static volatile NativeCrash[] _emptyArray;
        public int pid;
        public String process;
        public int processClass;
        public int uid;

        public static NativeCrash[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new NativeCrash[0];
                    }
                }
            }
            return _emptyArray;
        }

        public NativeCrash() {
            clear();
        }

        public NativeCrash clear() {
            this.process = "";
            this.pid = 0;
            this.uid = 0;
            this.processClass = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (!this.process.equals("")) {
                output.writeString(1, this.process);
            }
            int i = this.pid;
            if (i != 0) {
                output.writeInt32(2, i);
            }
            int i2 = this.uid;
            if (i2 != 0) {
                output.writeInt32(3, i2);
            }
            int i3 = this.processClass;
            if (i3 != 0) {
                output.writeInt32(4, i3);
            }
            super.writeTo(output);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.framework.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            if (!this.process.equals("")) {
                size += CodedOutputByteBufferNano.computeStringSize(1, this.process);
            }
            int i = this.pid;
            if (i != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(2, i);
            }
            int i2 = this.uid;
            if (i2 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(3, i2);
            }
            int i3 = this.processClass;
            if (i3 != 0) {
                return size + CodedOutputByteBufferNano.computeInt32Size(4, i3);
            }
            return size;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public NativeCrash mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        return this;
                    case 10:
                        this.process = input.readString();
                        break;
                    case 16:
                        this.pid = input.readInt32();
                        break;
                    case 24:
                        this.uid = input.readInt32();
                        break;
                    case 32:
                        int value = input.readInt32();
                        switch (value) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                                this.processClass = value;
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

        public static NativeCrash parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (NativeCrash) MessageNano.mergeFrom(new NativeCrash(), data);
        }

        public static NativeCrash parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new NativeCrash().mergeFrom(input);
        }
    }

    public int getEventCase() {
        return this.eventCase_;
    }

    public CriticalEventProto clearEvent() {
        this.eventCase_ = 0;
        this.event_ = null;
        return this;
    }

    public static CriticalEventProto[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new CriticalEventProto[0];
                }
            }
        }
        return _emptyArray;
    }

    public boolean hasWatchdog() {
        return this.eventCase_ == 2;
    }

    public Watchdog getWatchdog() {
        if (this.eventCase_ == 2) {
            return (Watchdog) this.event_;
        }
        return null;
    }

    public CriticalEventProto setWatchdog(Watchdog value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.eventCase_ = 2;
        this.event_ = value;
        return this;
    }

    public boolean hasHalfWatchdog() {
        return this.eventCase_ == 3;
    }

    public HalfWatchdog getHalfWatchdog() {
        if (this.eventCase_ == 3) {
            return (HalfWatchdog) this.event_;
        }
        return null;
    }

    public CriticalEventProto setHalfWatchdog(HalfWatchdog value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.eventCase_ = 3;
        this.event_ = value;
        return this;
    }

    public boolean hasAnr() {
        return this.eventCase_ == 4;
    }

    public AppNotResponding getAnr() {
        if (this.eventCase_ == 4) {
            return (AppNotResponding) this.event_;
        }
        return null;
    }

    public CriticalEventProto setAnr(AppNotResponding value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.eventCase_ = 4;
        this.event_ = value;
        return this;
    }

    public boolean hasJavaCrash() {
        return this.eventCase_ == 5;
    }

    public JavaCrash getJavaCrash() {
        if (this.eventCase_ == 5) {
            return (JavaCrash) this.event_;
        }
        return null;
    }

    public CriticalEventProto setJavaCrash(JavaCrash value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.eventCase_ = 5;
        this.event_ = value;
        return this;
    }

    public boolean hasNativeCrash() {
        return this.eventCase_ == 6;
    }

    public NativeCrash getNativeCrash() {
        if (this.eventCase_ == 6) {
            return (NativeCrash) this.event_;
        }
        return null;
    }

    public CriticalEventProto setNativeCrash(NativeCrash value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.eventCase_ = 6;
        this.event_ = value;
        return this;
    }

    public CriticalEventProto() {
        clear();
    }

    public CriticalEventProto clear() {
        this.timestampMs = 0L;
        clearEvent();
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.framework.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano output) throws IOException {
        long j = this.timestampMs;
        if (j != 0) {
            output.writeInt64(1, j);
        }
        if (this.eventCase_ == 2) {
            output.writeMessage(2, (MessageNano) this.event_);
        }
        if (this.eventCase_ == 3) {
            output.writeMessage(3, (MessageNano) this.event_);
        }
        if (this.eventCase_ == 4) {
            output.writeMessage(4, (MessageNano) this.event_);
        }
        if (this.eventCase_ == 5) {
            output.writeMessage(5, (MessageNano) this.event_);
        }
        if (this.eventCase_ == 6) {
            output.writeMessage(6, (MessageNano) this.event_);
        }
        super.writeTo(output);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.framework.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int size = super.computeSerializedSize();
        long j = this.timestampMs;
        if (j != 0) {
            size += CodedOutputByteBufferNano.computeInt64Size(1, j);
        }
        if (this.eventCase_ == 2) {
            size += CodedOutputByteBufferNano.computeMessageSize(2, (MessageNano) this.event_);
        }
        if (this.eventCase_ == 3) {
            size += CodedOutputByteBufferNano.computeMessageSize(3, (MessageNano) this.event_);
        }
        if (this.eventCase_ == 4) {
            size += CodedOutputByteBufferNano.computeMessageSize(4, (MessageNano) this.event_);
        }
        if (this.eventCase_ == 5) {
            size += CodedOutputByteBufferNano.computeMessageSize(5, (MessageNano) this.event_);
        }
        if (this.eventCase_ == 6) {
            return size + CodedOutputByteBufferNano.computeMessageSize(6, (MessageNano) this.event_);
        }
        return size;
    }

    @Override // com.android.framework.protobuf.nano.MessageNano
    public CriticalEventProto mergeFrom(CodedInputByteBufferNano input) throws IOException {
        while (true) {
            int tag = input.readTag();
            switch (tag) {
                case 0:
                    return this;
                case 8:
                    this.timestampMs = input.readInt64();
                    break;
                case 18:
                    if (this.eventCase_ != 2) {
                        this.event_ = new Watchdog();
                    }
                    input.readMessage((MessageNano) this.event_);
                    this.eventCase_ = 2;
                    break;
                case 26:
                    if (this.eventCase_ != 3) {
                        this.event_ = new HalfWatchdog();
                    }
                    input.readMessage((MessageNano) this.event_);
                    this.eventCase_ = 3;
                    break;
                case 34:
                    if (this.eventCase_ != 4) {
                        this.event_ = new AppNotResponding();
                    }
                    input.readMessage((MessageNano) this.event_);
                    this.eventCase_ = 4;
                    break;
                case 42:
                    if (this.eventCase_ != 5) {
                        this.event_ = new JavaCrash();
                    }
                    input.readMessage((MessageNano) this.event_);
                    this.eventCase_ = 5;
                    break;
                case 50:
                    if (this.eventCase_ != 6) {
                        this.event_ = new NativeCrash();
                    }
                    input.readMessage((MessageNano) this.event_);
                    this.eventCase_ = 6;
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

    public static CriticalEventProto parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
        return (CriticalEventProto) MessageNano.mergeFrom(new CriticalEventProto(), data);
    }

    public static CriticalEventProto parseFrom(CodedInputByteBufferNano input) throws IOException {
        return new CriticalEventProto().mergeFrom(input);
    }
}
