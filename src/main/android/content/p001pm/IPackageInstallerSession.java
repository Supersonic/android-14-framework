package android.content.p001pm;

import android.content.IntentSender;
import android.content.p001pm.IOnChecksumsReadyListener;
import android.content.p001pm.PackageInstaller;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import java.util.List;
/* renamed from: android.content.pm.IPackageInstallerSession */
/* loaded from: classes.dex */
public interface IPackageInstallerSession extends IInterface {
    void abandon() throws RemoteException;

    void addChildSessionId(int i) throws RemoteException;

    void addClientProgress(float f) throws RemoteException;

    void addFile(int i, String str, long j, byte[] bArr, byte[] bArr2) throws RemoteException;

    void close() throws RemoteException;

    void commit(IntentSender intentSender, boolean z) throws RemoteException;

    List<String> fetchPackageNames() throws RemoteException;

    ParcelFileDescriptor getAppMetadataFd() throws RemoteException;

    int[] getChildSessionIds() throws RemoteException;

    DataLoaderParamsParcel getDataLoaderParams() throws RemoteException;

    int getInstallFlags() throws RemoteException;

    String[] getNames() throws RemoteException;

    int getParentSessionId() throws RemoteException;

    boolean isApplicationEnabledSettingPersistent() throws RemoteException;

    boolean isMultiPackage() throws RemoteException;

    boolean isRequestUpdateOwnership() throws RemoteException;

    boolean isStaged() throws RemoteException;

    ParcelFileDescriptor openRead(String str) throws RemoteException;

    ParcelFileDescriptor openWrite(String str, long j, long j2) throws RemoteException;

    ParcelFileDescriptor openWriteAppMetadata() throws RemoteException;

    void removeAppMetadata() throws RemoteException;

    void removeChildSessionId(int i) throws RemoteException;

    void removeFile(int i, String str) throws RemoteException;

    void removeSplit(String str) throws RemoteException;

    void requestChecksums(String str, int i, int i2, List list, IOnChecksumsReadyListener iOnChecksumsReadyListener) throws RemoteException;

    void requestUserPreapproval(PackageInstaller.PreapprovalDetails preapprovalDetails, IntentSender intentSender) throws RemoteException;

    void seal() throws RemoteException;

    void setChecksums(String str, Checksum[] checksumArr, byte[] bArr) throws RemoteException;

    void setClientProgress(float f) throws RemoteException;

    void stageViaHardLink(String str) throws RemoteException;

    void transfer(String str) throws RemoteException;

    void write(String str, long j, long j2, ParcelFileDescriptor parcelFileDescriptor) throws RemoteException;

    /* renamed from: android.content.pm.IPackageInstallerSession$Default */
    /* loaded from: classes.dex */
    public static class Default implements IPackageInstallerSession {
        @Override // android.content.p001pm.IPackageInstallerSession
        public void setClientProgress(float progress) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public void addClientProgress(float progress) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public String[] getNames() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public ParcelFileDescriptor openWrite(String name, long offsetBytes, long lengthBytes) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public ParcelFileDescriptor openRead(String name) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public void write(String name, long offsetBytes, long lengthBytes, ParcelFileDescriptor fd) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public void stageViaHardLink(String target) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public void setChecksums(String name, Checksum[] checksums, byte[] signature) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public void requestChecksums(String name, int optional, int required, List trustedInstallers, IOnChecksumsReadyListener onChecksumsReadyListener) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public void removeSplit(String splitName) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public void close() throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public void commit(IntentSender statusReceiver, boolean forTransferred) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public void transfer(String packageName) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public void abandon() throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public void seal() throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public List<String> fetchPackageNames() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public DataLoaderParamsParcel getDataLoaderParams() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public void addFile(int location, String name, long lengthBytes, byte[] metadata, byte[] signature) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public void removeFile(int location, String name) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public boolean isMultiPackage() throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public int[] getChildSessionIds() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public void addChildSessionId(int sessionId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public void removeChildSessionId(int sessionId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public int getParentSessionId() throws RemoteException {
            return 0;
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public boolean isStaged() throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public int getInstallFlags() throws RemoteException {
            return 0;
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public void requestUserPreapproval(PackageInstaller.PreapprovalDetails details, IntentSender statusReceiver) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public boolean isApplicationEnabledSettingPersistent() throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public boolean isRequestUpdateOwnership() throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public ParcelFileDescriptor getAppMetadataFd() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public ParcelFileDescriptor openWriteAppMetadata() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageInstallerSession
        public void removeAppMetadata() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.content.pm.IPackageInstallerSession$Stub */
    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IPackageInstallerSession {
        public static final String DESCRIPTOR = "android.content.pm.IPackageInstallerSession";
        static final int TRANSACTION_abandon = 14;
        static final int TRANSACTION_addChildSessionId = 22;
        static final int TRANSACTION_addClientProgress = 2;
        static final int TRANSACTION_addFile = 18;
        static final int TRANSACTION_close = 11;
        static final int TRANSACTION_commit = 12;
        static final int TRANSACTION_fetchPackageNames = 16;
        static final int TRANSACTION_getAppMetadataFd = 30;
        static final int TRANSACTION_getChildSessionIds = 21;
        static final int TRANSACTION_getDataLoaderParams = 17;
        static final int TRANSACTION_getInstallFlags = 26;
        static final int TRANSACTION_getNames = 3;
        static final int TRANSACTION_getParentSessionId = 24;
        static final int TRANSACTION_isApplicationEnabledSettingPersistent = 28;
        static final int TRANSACTION_isMultiPackage = 20;
        static final int TRANSACTION_isRequestUpdateOwnership = 29;
        static final int TRANSACTION_isStaged = 25;
        static final int TRANSACTION_openRead = 5;
        static final int TRANSACTION_openWrite = 4;
        static final int TRANSACTION_openWriteAppMetadata = 31;
        static final int TRANSACTION_removeAppMetadata = 32;
        static final int TRANSACTION_removeChildSessionId = 23;
        static final int TRANSACTION_removeFile = 19;
        static final int TRANSACTION_removeSplit = 10;
        static final int TRANSACTION_requestChecksums = 9;
        static final int TRANSACTION_requestUserPreapproval = 27;
        static final int TRANSACTION_seal = 15;
        static final int TRANSACTION_setChecksums = 8;
        static final int TRANSACTION_setClientProgress = 1;
        static final int TRANSACTION_stageViaHardLink = 7;
        static final int TRANSACTION_transfer = 13;
        static final int TRANSACTION_write = 6;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPackageInstallerSession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IPackageInstallerSession)) {
                return (IPackageInstallerSession) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "setClientProgress";
                case 2:
                    return "addClientProgress";
                case 3:
                    return "getNames";
                case 4:
                    return "openWrite";
                case 5:
                    return "openRead";
                case 6:
                    return "write";
                case 7:
                    return "stageViaHardLink";
                case 8:
                    return "setChecksums";
                case 9:
                    return "requestChecksums";
                case 10:
                    return "removeSplit";
                case 11:
                    return "close";
                case 12:
                    return "commit";
                case 13:
                    return "transfer";
                case 14:
                    return "abandon";
                case 15:
                    return "seal";
                case 16:
                    return "fetchPackageNames";
                case 17:
                    return "getDataLoaderParams";
                case 18:
                    return "addFile";
                case 19:
                    return "removeFile";
                case 20:
                    return "isMultiPackage";
                case 21:
                    return "getChildSessionIds";
                case 22:
                    return "addChildSessionId";
                case 23:
                    return "removeChildSessionId";
                case 24:
                    return "getParentSessionId";
                case 25:
                    return "isStaged";
                case 26:
                    return "getInstallFlags";
                case 27:
                    return "requestUserPreapproval";
                case 28:
                    return "isApplicationEnabledSettingPersistent";
                case 29:
                    return "isRequestUpdateOwnership";
                case 30:
                    return "getAppMetadataFd";
                case 31:
                    return "openWriteAppMetadata";
                case 32:
                    return "removeAppMetadata";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            float _arg0 = data.readFloat();
                            data.enforceNoDataAvail();
                            setClientProgress(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            float _arg02 = data.readFloat();
                            data.enforceNoDataAvail();
                            addClientProgress(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            String[] _result = getNames();
                            reply.writeNoException();
                            reply.writeStringArray(_result);
                            break;
                        case 4:
                            String _arg03 = data.readString();
                            long _arg1 = data.readLong();
                            long _arg2 = data.readLong();
                            data.enforceNoDataAvail();
                            ParcelFileDescriptor _result2 = openWrite(_arg03, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 5:
                            String _arg04 = data.readString();
                            data.enforceNoDataAvail();
                            ParcelFileDescriptor _result3 = openRead(_arg04);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 6:
                            String _arg05 = data.readString();
                            long _arg12 = data.readLong();
                            long _arg22 = data.readLong();
                            ParcelFileDescriptor _arg3 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            data.enforceNoDataAvail();
                            write(_arg05, _arg12, _arg22, _arg3);
                            reply.writeNoException();
                            break;
                        case 7:
                            String _arg06 = data.readString();
                            data.enforceNoDataAvail();
                            stageViaHardLink(_arg06);
                            reply.writeNoException();
                            break;
                        case 8:
                            String _arg07 = data.readString();
                            Checksum[] _arg13 = (Checksum[]) data.createTypedArray(Checksum.CREATOR);
                            byte[] _arg23 = data.createByteArray();
                            data.enforceNoDataAvail();
                            setChecksums(_arg07, _arg13, _arg23);
                            reply.writeNoException();
                            break;
                        case 9:
                            String _arg08 = data.readString();
                            int _arg14 = data.readInt();
                            int _arg24 = data.readInt();
                            ClassLoader cl = getClass().getClassLoader();
                            List _arg32 = data.readArrayList(cl);
                            IOnChecksumsReadyListener _arg4 = IOnChecksumsReadyListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            requestChecksums(_arg08, _arg14, _arg24, _arg32, _arg4);
                            reply.writeNoException();
                            break;
                        case 10:
                            String _arg09 = data.readString();
                            data.enforceNoDataAvail();
                            removeSplit(_arg09);
                            reply.writeNoException();
                            break;
                        case 11:
                            close();
                            reply.writeNoException();
                            break;
                        case 12:
                            IntentSender _arg010 = (IntentSender) data.readTypedObject(IntentSender.CREATOR);
                            boolean _arg15 = data.readBoolean();
                            data.enforceNoDataAvail();
                            commit(_arg010, _arg15);
                            reply.writeNoException();
                            break;
                        case 13:
                            String _arg011 = data.readString();
                            data.enforceNoDataAvail();
                            transfer(_arg011);
                            reply.writeNoException();
                            break;
                        case 14:
                            abandon();
                            reply.writeNoException();
                            break;
                        case 15:
                            seal();
                            reply.writeNoException();
                            break;
                        case 16:
                            List<String> _result4 = fetchPackageNames();
                            reply.writeNoException();
                            reply.writeStringList(_result4);
                            break;
                        case 17:
                            DataLoaderParamsParcel _result5 = getDataLoaderParams();
                            reply.writeNoException();
                            reply.writeTypedObject(_result5, 1);
                            break;
                        case 18:
                            int _arg012 = data.readInt();
                            String _arg16 = data.readString();
                            long _arg25 = data.readLong();
                            byte[] _arg33 = data.createByteArray();
                            byte[] _arg42 = data.createByteArray();
                            data.enforceNoDataAvail();
                            addFile(_arg012, _arg16, _arg25, _arg33, _arg42);
                            reply.writeNoException();
                            break;
                        case 19:
                            int _arg013 = data.readInt();
                            String _arg17 = data.readString();
                            data.enforceNoDataAvail();
                            removeFile(_arg013, _arg17);
                            reply.writeNoException();
                            break;
                        case 20:
                            boolean _result6 = isMultiPackage();
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            break;
                        case 21:
                            int[] _result7 = getChildSessionIds();
                            reply.writeNoException();
                            reply.writeIntArray(_result7);
                            break;
                        case 22:
                            int _arg014 = data.readInt();
                            data.enforceNoDataAvail();
                            addChildSessionId(_arg014);
                            reply.writeNoException();
                            break;
                        case 23:
                            int _arg015 = data.readInt();
                            data.enforceNoDataAvail();
                            removeChildSessionId(_arg015);
                            reply.writeNoException();
                            break;
                        case 24:
                            int _result8 = getParentSessionId();
                            reply.writeNoException();
                            reply.writeInt(_result8);
                            break;
                        case 25:
                            boolean _result9 = isStaged();
                            reply.writeNoException();
                            reply.writeBoolean(_result9);
                            break;
                        case 26:
                            int _result10 = getInstallFlags();
                            reply.writeNoException();
                            reply.writeInt(_result10);
                            break;
                        case 27:
                            PackageInstaller.PreapprovalDetails _arg016 = (PackageInstaller.PreapprovalDetails) data.readTypedObject(PackageInstaller.PreapprovalDetails.CREATOR);
                            IntentSender _arg18 = (IntentSender) data.readTypedObject(IntentSender.CREATOR);
                            data.enforceNoDataAvail();
                            requestUserPreapproval(_arg016, _arg18);
                            reply.writeNoException();
                            break;
                        case 28:
                            boolean _result11 = isApplicationEnabledSettingPersistent();
                            reply.writeNoException();
                            reply.writeBoolean(_result11);
                            break;
                        case 29:
                            boolean _result12 = isRequestUpdateOwnership();
                            reply.writeNoException();
                            reply.writeBoolean(_result12);
                            break;
                        case 30:
                            ParcelFileDescriptor _result13 = getAppMetadataFd();
                            reply.writeNoException();
                            reply.writeTypedObject(_result13, 1);
                            break;
                        case 31:
                            ParcelFileDescriptor _result14 = openWriteAppMetadata();
                            reply.writeNoException();
                            reply.writeTypedObject(_result14, 1);
                            break;
                        case 32:
                            removeAppMetadata();
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: android.content.pm.IPackageInstallerSession$Stub$Proxy */
        /* loaded from: classes.dex */
        public static class Proxy implements IPackageInstallerSession {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public void setClientProgress(float progress) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloat(progress);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public void addClientProgress(float progress) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloat(progress);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public String[] getNames() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public ParcelFileDescriptor openWrite(String name, long offsetBytes, long lengthBytes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeLong(offsetBytes);
                    _data.writeLong(lengthBytes);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    ParcelFileDescriptor _result = (ParcelFileDescriptor) _reply.readTypedObject(ParcelFileDescriptor.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public ParcelFileDescriptor openRead(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    ParcelFileDescriptor _result = (ParcelFileDescriptor) _reply.readTypedObject(ParcelFileDescriptor.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public void write(String name, long offsetBytes, long lengthBytes, ParcelFileDescriptor fd) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeLong(offsetBytes);
                    _data.writeLong(lengthBytes);
                    _data.writeTypedObject(fd, 0);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public void stageViaHardLink(String target) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(target);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public void setChecksums(String name, Checksum[] checksums, byte[] signature) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeTypedArray(checksums, 0);
                    _data.writeByteArray(signature);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public void requestChecksums(String name, int optional, int required, List trustedInstallers, IOnChecksumsReadyListener onChecksumsReadyListener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeInt(optional);
                    _data.writeInt(required);
                    _data.writeList(trustedInstallers);
                    _data.writeStrongInterface(onChecksumsReadyListener);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public void removeSplit(String splitName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(splitName);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public void close() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public void commit(IntentSender statusReceiver, boolean forTransferred) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(statusReceiver, 0);
                    _data.writeBoolean(forTransferred);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public void transfer(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public void abandon() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public void seal() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public List<String> fetchPackageNames() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public DataLoaderParamsParcel getDataLoaderParams() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    DataLoaderParamsParcel _result = (DataLoaderParamsParcel) _reply.readTypedObject(DataLoaderParamsParcel.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public void addFile(int location, String name, long lengthBytes, byte[] metadata, byte[] signature) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(location);
                    _data.writeString(name);
                    _data.writeLong(lengthBytes);
                    _data.writeByteArray(metadata);
                    _data.writeByteArray(signature);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public void removeFile(int location, String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(location);
                    _data.writeString(name);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public boolean isMultiPackage() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public int[] getChildSessionIds() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public void addChildSessionId(int sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public void removeChildSessionId(int sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public int getParentSessionId() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public boolean isStaged() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public int getInstallFlags() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public void requestUserPreapproval(PackageInstaller.PreapprovalDetails details, IntentSender statusReceiver) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(details, 0);
                    _data.writeTypedObject(statusReceiver, 0);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public boolean isApplicationEnabledSettingPersistent() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public boolean isRequestUpdateOwnership() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public ParcelFileDescriptor getAppMetadataFd() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    ParcelFileDescriptor _result = (ParcelFileDescriptor) _reply.readTypedObject(ParcelFileDescriptor.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public ParcelFileDescriptor openWriteAppMetadata() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    ParcelFileDescriptor _result = (ParcelFileDescriptor) _reply.readTypedObject(ParcelFileDescriptor.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageInstallerSession
            public void removeAppMetadata() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 31;
        }
    }
}
