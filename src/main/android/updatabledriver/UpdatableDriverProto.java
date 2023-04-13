package android.updatabledriver;

import com.android.framework.protobuf.AbstractMessageLite;
import com.android.framework.protobuf.ByteString;
import com.android.framework.protobuf.CodedInputStream;
import com.android.framework.protobuf.ExtensionRegistryLite;
import com.android.framework.protobuf.GeneratedMessageLite;
import com.android.framework.protobuf.Internal;
import com.android.framework.protobuf.InvalidProtocolBufferException;
import com.android.framework.protobuf.MessageLiteOrBuilder;
import com.android.framework.protobuf.Parser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public final class UpdatableDriverProto {

    /* loaded from: classes3.dex */
    public interface DenylistOrBuilder extends MessageLiteOrBuilder {
        String getPackageNames(int i);

        ByteString getPackageNamesBytes(int i);

        int getPackageNamesCount();

        List<String> getPackageNamesList();

        long getVersionCode();

        boolean hasVersionCode();
    }

    /* loaded from: classes3.dex */
    public interface DenylistsOrBuilder extends MessageLiteOrBuilder {
        Denylist getDenylists(int i);

        int getDenylistsCount();

        List<Denylist> getDenylistsList();
    }

    private UpdatableDriverProto() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    /* loaded from: classes3.dex */
    public static final class Denylist extends GeneratedMessageLite<Denylist, Builder> implements DenylistOrBuilder {
        private static final Denylist DEFAULT_INSTANCE;
        public static final int PACKAGE_NAMES_FIELD_NUMBER = 2;
        private static volatile Parser<Denylist> PARSER = null;
        public static final int VERSION_CODE_FIELD_NUMBER = 1;
        private int bitField0_;
        private Internal.ProtobufList<String> packageNames_ = GeneratedMessageLite.emptyProtobufList();
        private long versionCode_;

        private Denylist() {
        }

        @Override // android.updatabledriver.UpdatableDriverProto.DenylistOrBuilder
        public boolean hasVersionCode() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // android.updatabledriver.UpdatableDriverProto.DenylistOrBuilder
        public long getVersionCode() {
            return this.versionCode_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setVersionCode(long value) {
            this.bitField0_ |= 1;
            this.versionCode_ = value;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearVersionCode() {
            this.bitField0_ &= -2;
            this.versionCode_ = 0L;
        }

        @Override // android.updatabledriver.UpdatableDriverProto.DenylistOrBuilder
        public List<String> getPackageNamesList() {
            return this.packageNames_;
        }

        @Override // android.updatabledriver.UpdatableDriverProto.DenylistOrBuilder
        public int getPackageNamesCount() {
            return this.packageNames_.size();
        }

        @Override // android.updatabledriver.UpdatableDriverProto.DenylistOrBuilder
        public String getPackageNames(int index) {
            return this.packageNames_.get(index);
        }

        @Override // android.updatabledriver.UpdatableDriverProto.DenylistOrBuilder
        public ByteString getPackageNamesBytes(int index) {
            return ByteString.copyFromUtf8(this.packageNames_.get(index));
        }

        private void ensurePackageNamesIsMutable() {
            Internal.ProtobufList<String> tmp = this.packageNames_;
            if (!tmp.isModifiable()) {
                this.packageNames_ = GeneratedMessageLite.mutableCopy(tmp);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setPackageNames(int index, String value) {
            value.getClass();
            ensurePackageNamesIsMutable();
            this.packageNames_.set(index, value);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addPackageNames(String value) {
            value.getClass();
            ensurePackageNamesIsMutable();
            this.packageNames_.add(value);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addAllPackageNames(Iterable<String> values) {
            ensurePackageNamesIsMutable();
            AbstractMessageLite.addAll((Iterable) values, (List) this.packageNames_);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearPackageNames() {
            this.packageNames_ = GeneratedMessageLite.emptyProtobufList();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addPackageNamesBytes(ByteString value) {
            ensurePackageNamesIsMutable();
            this.packageNames_.add(value.toStringUtf8());
        }

        public static Denylist parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return (Denylist) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static Denylist parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Denylist) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static Denylist parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (Denylist) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static Denylist parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Denylist) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static Denylist parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (Denylist) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static Denylist parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Denylist) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static Denylist parseFrom(InputStream input) throws IOException {
            return (Denylist) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static Denylist parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Denylist) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static Denylist parseDelimitedFrom(InputStream input) throws IOException {
            return (Denylist) parseDelimitedFrom(DEFAULT_INSTANCE, input);
        }

        public static Denylist parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Denylist) parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static Denylist parseFrom(CodedInputStream input) throws IOException {
            return (Denylist) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static Denylist parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Denylist) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(Denylist prototype) {
            return DEFAULT_INSTANCE.createBuilder(prototype);
        }

        /* loaded from: classes3.dex */
        public static final class Builder extends GeneratedMessageLite.Builder<Denylist, Builder> implements DenylistOrBuilder {
            /* synthetic */ Builder(C34311 x0) {
                this();
            }

            private Builder() {
                super(Denylist.DEFAULT_INSTANCE);
            }

            @Override // android.updatabledriver.UpdatableDriverProto.DenylistOrBuilder
            public boolean hasVersionCode() {
                return ((Denylist) this.instance).hasVersionCode();
            }

            @Override // android.updatabledriver.UpdatableDriverProto.DenylistOrBuilder
            public long getVersionCode() {
                return ((Denylist) this.instance).getVersionCode();
            }

            public Builder setVersionCode(long value) {
                copyOnWrite();
                ((Denylist) this.instance).setVersionCode(value);
                return this;
            }

            public Builder clearVersionCode() {
                copyOnWrite();
                ((Denylist) this.instance).clearVersionCode();
                return this;
            }

            @Override // android.updatabledriver.UpdatableDriverProto.DenylistOrBuilder
            public List<String> getPackageNamesList() {
                return Collections.unmodifiableList(((Denylist) this.instance).getPackageNamesList());
            }

            @Override // android.updatabledriver.UpdatableDriverProto.DenylistOrBuilder
            public int getPackageNamesCount() {
                return ((Denylist) this.instance).getPackageNamesCount();
            }

            @Override // android.updatabledriver.UpdatableDriverProto.DenylistOrBuilder
            public String getPackageNames(int index) {
                return ((Denylist) this.instance).getPackageNames(index);
            }

            @Override // android.updatabledriver.UpdatableDriverProto.DenylistOrBuilder
            public ByteString getPackageNamesBytes(int index) {
                return ((Denylist) this.instance).getPackageNamesBytes(index);
            }

            public Builder setPackageNames(int index, String value) {
                copyOnWrite();
                ((Denylist) this.instance).setPackageNames(index, value);
                return this;
            }

            public Builder addPackageNames(String value) {
                copyOnWrite();
                ((Denylist) this.instance).addPackageNames(value);
                return this;
            }

            public Builder addAllPackageNames(Iterable<String> values) {
                copyOnWrite();
                ((Denylist) this.instance).addAllPackageNames(values);
                return this;
            }

            public Builder clearPackageNames() {
                copyOnWrite();
                ((Denylist) this.instance).clearPackageNames();
                return this;
            }

            public Builder addPackageNamesBytes(ByteString value) {
                copyOnWrite();
                ((Denylist) this.instance).addPackageNamesBytes(value);
                return this;
            }
        }

        @Override // com.android.framework.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (C34311.f466xa1df5c61[method.ordinal()]) {
                case 1:
                    return new Denylist();
                case 2:
                    return new Builder(null);
                case 3:
                    Object[] objects = {"bitField0_", "versionCode_", "packageNames_"};
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0001\u0000\u0001ဂ\u0000\u0002\u001a", objects);
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<Denylist> parser = PARSER;
                    if (parser == null) {
                        synchronized (Denylist.class) {
                            parser = PARSER;
                            if (parser == null) {
                                parser = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                                PARSER = parser;
                            }
                        }
                    }
                    return parser;
                case 6:
                    return (byte) 1;
                case 7:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        static {
            Denylist defaultInstance = new Denylist();
            DEFAULT_INSTANCE = defaultInstance;
            GeneratedMessageLite.registerDefaultInstance(Denylist.class, defaultInstance);
        }

        public static Denylist getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Denylist> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    /* renamed from: android.updatabledriver.UpdatableDriverProto$1 */
    /* loaded from: classes3.dex */
    static /* synthetic */ class C34311 {

        /* renamed from: $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke */
        static final /* synthetic */ int[] f466xa1df5c61;

        static {
            int[] iArr = new int[GeneratedMessageLite.MethodToInvoke.values().length];
            f466xa1df5c61 = iArr;
            try {
                iArr[GeneratedMessageLite.MethodToInvoke.NEW_MUTABLE_INSTANCE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f466xa1df5c61[GeneratedMessageLite.MethodToInvoke.NEW_BUILDER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f466xa1df5c61[GeneratedMessageLite.MethodToInvoke.BUILD_MESSAGE_INFO.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f466xa1df5c61[GeneratedMessageLite.MethodToInvoke.GET_DEFAULT_INSTANCE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f466xa1df5c61[GeneratedMessageLite.MethodToInvoke.GET_PARSER.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f466xa1df5c61[GeneratedMessageLite.MethodToInvoke.GET_MEMOIZED_IS_INITIALIZED.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f466xa1df5c61[GeneratedMessageLite.MethodToInvoke.SET_MEMOIZED_IS_INITIALIZED.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class Denylists extends GeneratedMessageLite<Denylists, Builder> implements DenylistsOrBuilder {
        private static final Denylists DEFAULT_INSTANCE;
        public static final int DENYLISTS_FIELD_NUMBER = 1;
        private static volatile Parser<Denylists> PARSER;
        private Internal.ProtobufList<Denylist> denylists_ = emptyProtobufList();

        private Denylists() {
        }

        @Override // android.updatabledriver.UpdatableDriverProto.DenylistsOrBuilder
        public List<Denylist> getDenylistsList() {
            return this.denylists_;
        }

        public List<? extends DenylistOrBuilder> getDenylistsOrBuilderList() {
            return this.denylists_;
        }

        @Override // android.updatabledriver.UpdatableDriverProto.DenylistsOrBuilder
        public int getDenylistsCount() {
            return this.denylists_.size();
        }

        @Override // android.updatabledriver.UpdatableDriverProto.DenylistsOrBuilder
        public Denylist getDenylists(int index) {
            return this.denylists_.get(index);
        }

        public DenylistOrBuilder getDenylistsOrBuilder(int index) {
            return this.denylists_.get(index);
        }

        private void ensureDenylistsIsMutable() {
            Internal.ProtobufList<Denylist> tmp = this.denylists_;
            if (!tmp.isModifiable()) {
                this.denylists_ = GeneratedMessageLite.mutableCopy(tmp);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setDenylists(int index, Denylist value) {
            value.getClass();
            ensureDenylistsIsMutable();
            this.denylists_.set(index, value);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addDenylists(Denylist value) {
            value.getClass();
            ensureDenylistsIsMutable();
            this.denylists_.add(value);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addDenylists(int index, Denylist value) {
            value.getClass();
            ensureDenylistsIsMutable();
            this.denylists_.add(index, value);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addAllDenylists(Iterable<? extends Denylist> values) {
            ensureDenylistsIsMutable();
            AbstractMessageLite.addAll((Iterable) values, (List) this.denylists_);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearDenylists() {
            this.denylists_ = emptyProtobufList();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void removeDenylists(int index) {
            ensureDenylistsIsMutable();
            this.denylists_.remove(index);
        }

        public static Denylists parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return (Denylists) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static Denylists parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Denylists) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static Denylists parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (Denylists) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static Denylists parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Denylists) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static Denylists parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (Denylists) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static Denylists parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Denylists) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static Denylists parseFrom(InputStream input) throws IOException {
            return (Denylists) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static Denylists parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Denylists) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static Denylists parseDelimitedFrom(InputStream input) throws IOException {
            return (Denylists) parseDelimitedFrom(DEFAULT_INSTANCE, input);
        }

        public static Denylists parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Denylists) parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static Denylists parseFrom(CodedInputStream input) throws IOException {
            return (Denylists) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static Denylists parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Denylists) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(Denylists prototype) {
            return DEFAULT_INSTANCE.createBuilder(prototype);
        }

        /* loaded from: classes3.dex */
        public static final class Builder extends GeneratedMessageLite.Builder<Denylists, Builder> implements DenylistsOrBuilder {
            /* synthetic */ Builder(C34311 x0) {
                this();
            }

            private Builder() {
                super(Denylists.DEFAULT_INSTANCE);
            }

            @Override // android.updatabledriver.UpdatableDriverProto.DenylistsOrBuilder
            public List<Denylist> getDenylistsList() {
                return Collections.unmodifiableList(((Denylists) this.instance).getDenylistsList());
            }

            @Override // android.updatabledriver.UpdatableDriverProto.DenylistsOrBuilder
            public int getDenylistsCount() {
                return ((Denylists) this.instance).getDenylistsCount();
            }

            @Override // android.updatabledriver.UpdatableDriverProto.DenylistsOrBuilder
            public Denylist getDenylists(int index) {
                return ((Denylists) this.instance).getDenylists(index);
            }

            public Builder setDenylists(int index, Denylist value) {
                copyOnWrite();
                ((Denylists) this.instance).setDenylists(index, value);
                return this;
            }

            public Builder setDenylists(int index, Denylist.Builder builderForValue) {
                copyOnWrite();
                ((Denylists) this.instance).setDenylists(index, builderForValue.build());
                return this;
            }

            public Builder addDenylists(Denylist value) {
                copyOnWrite();
                ((Denylists) this.instance).addDenylists(value);
                return this;
            }

            public Builder addDenylists(int index, Denylist value) {
                copyOnWrite();
                ((Denylists) this.instance).addDenylists(index, value);
                return this;
            }

            public Builder addDenylists(Denylist.Builder builderForValue) {
                copyOnWrite();
                ((Denylists) this.instance).addDenylists(builderForValue.build());
                return this;
            }

            public Builder addDenylists(int index, Denylist.Builder builderForValue) {
                copyOnWrite();
                ((Denylists) this.instance).addDenylists(index, builderForValue.build());
                return this;
            }

            public Builder addAllDenylists(Iterable<? extends Denylist> values) {
                copyOnWrite();
                ((Denylists) this.instance).addAllDenylists(values);
                return this;
            }

            public Builder clearDenylists() {
                copyOnWrite();
                ((Denylists) this.instance).clearDenylists();
                return this;
            }

            public Builder removeDenylists(int index) {
                copyOnWrite();
                ((Denylists) this.instance).removeDenylists(index);
                return this;
            }
        }

        @Override // com.android.framework.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (C34311.f466xa1df5c61[method.ordinal()]) {
                case 1:
                    return new Denylists();
                case 2:
                    return new Builder(null);
                case 3:
                    Object[] objects = {"denylists_", Denylist.class};
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0000\u0001\u001b", objects);
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<Denylists> parser = PARSER;
                    if (parser == null) {
                        synchronized (Denylists.class) {
                            parser = PARSER;
                            if (parser == null) {
                                parser = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                                PARSER = parser;
                            }
                        }
                    }
                    return parser;
                case 6:
                    return (byte) 1;
                case 7:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        static {
            Denylists defaultInstance = new Denylists();
            DEFAULT_INSTANCE = defaultInstance;
            GeneratedMessageLite.registerDefaultInstance(Denylists.class, defaultInstance);
        }

        public static Denylists getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Denylists> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }
}
