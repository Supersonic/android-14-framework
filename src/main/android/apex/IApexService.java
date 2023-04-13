package android.apex;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes.dex */
public interface IApexService extends IInterface {
    public static final String DESCRIPTOR = "android$apex$IApexService".replace('$', '.');

    void abortStagedSession(int i) throws RemoteException;

    long calculateSizeForCompressedApex(CompressedApexInfoList compressedApexInfoList) throws RemoteException;

    void destroyCeSnapshots(int i, int i2) throws RemoteException;

    void destroyCeSnapshotsNotSpecified(int i, int[] iArr) throws RemoteException;

    void destroyDeSnapshots(int i) throws RemoteException;

    ApexInfo getActivePackage(String str) throws RemoteException;

    ApexInfo[] getActivePackages() throws RemoteException;

    ApexInfo[] getAllPackages() throws RemoteException;

    ApexSessionInfo[] getSessions() throws RemoteException;

    ApexInfo[] getStagedApexInfos(ApexSessionParams apexSessionParams) throws RemoteException;

    ApexSessionInfo getStagedSessionInfo(int i) throws RemoteException;

    ApexInfo installAndActivatePackage(String str) throws RemoteException;

    void markBootCompleted() throws RemoteException;

    void markStagedSessionReady(int i) throws RemoteException;

    void markStagedSessionSuccessful(int i) throws RemoteException;

    void recollectDataApex(String str, String str2) throws RemoteException;

    void recollectPreinstalledData(List<String> list) throws RemoteException;

    void remountPackages() throws RemoteException;

    void reserveSpaceForCompressedApex(CompressedApexInfoList compressedApexInfoList) throws RemoteException;

    void restoreCeData(int i, int i2, String str) throws RemoteException;

    void resumeRevertIfNeeded() throws RemoteException;

    void revertActiveSessions() throws RemoteException;

    void snapshotCeData(int i, int i2, String str) throws RemoteException;

    void stagePackages(List<String> list) throws RemoteException;

    void submitStagedSession(ApexSessionParams apexSessionParams, ApexInfoList apexInfoList) throws RemoteException;

    void unstagePackages(List<String> list) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IApexService {
        @Override // android.apex.IApexService
        public void submitStagedSession(ApexSessionParams params, ApexInfoList packages) throws RemoteException {
        }

        @Override // android.apex.IApexService
        public void markStagedSessionReady(int session_id) throws RemoteException {
        }

        @Override // android.apex.IApexService
        public void markStagedSessionSuccessful(int session_id) throws RemoteException {
        }

        @Override // android.apex.IApexService
        public ApexSessionInfo[] getSessions() throws RemoteException {
            return null;
        }

        @Override // android.apex.IApexService
        public ApexSessionInfo getStagedSessionInfo(int session_id) throws RemoteException {
            return null;
        }

        @Override // android.apex.IApexService
        public ApexInfo[] getStagedApexInfos(ApexSessionParams params) throws RemoteException {
            return null;
        }

        @Override // android.apex.IApexService
        public ApexInfo[] getActivePackages() throws RemoteException {
            return null;
        }

        @Override // android.apex.IApexService
        public ApexInfo[] getAllPackages() throws RemoteException {
            return null;
        }

        @Override // android.apex.IApexService
        public void abortStagedSession(int session_id) throws RemoteException {
        }

        @Override // android.apex.IApexService
        public void revertActiveSessions() throws RemoteException {
        }

        @Override // android.apex.IApexService
        public void snapshotCeData(int user_id, int rollback_id, String apex_name) throws RemoteException {
        }

        @Override // android.apex.IApexService
        public void restoreCeData(int user_id, int rollback_id, String apex_name) throws RemoteException {
        }

        @Override // android.apex.IApexService
        public void destroyDeSnapshots(int rollback_id) throws RemoteException {
        }

        @Override // android.apex.IApexService
        public void destroyCeSnapshots(int user_id, int rollback_id) throws RemoteException {
        }

        @Override // android.apex.IApexService
        public void destroyCeSnapshotsNotSpecified(int user_id, int[] retain_rollback_ids) throws RemoteException {
        }

        @Override // android.apex.IApexService
        public void unstagePackages(List<String> active_package_paths) throws RemoteException {
        }

        @Override // android.apex.IApexService
        public ApexInfo getActivePackage(String package_name) throws RemoteException {
            return null;
        }

        @Override // android.apex.IApexService
        public void stagePackages(List<String> package_tmp_paths) throws RemoteException {
        }

        @Override // android.apex.IApexService
        public void resumeRevertIfNeeded() throws RemoteException {
        }

        @Override // android.apex.IApexService
        public void remountPackages() throws RemoteException {
        }

        @Override // android.apex.IApexService
        public void recollectPreinstalledData(List<String> paths) throws RemoteException {
        }

        @Override // android.apex.IApexService
        public void recollectDataApex(String path, String decompression_dir) throws RemoteException {
        }

        @Override // android.apex.IApexService
        public void markBootCompleted() throws RemoteException {
        }

        @Override // android.apex.IApexService
        public long calculateSizeForCompressedApex(CompressedApexInfoList compressed_apex_info_list) throws RemoteException {
            return 0L;
        }

        @Override // android.apex.IApexService
        public void reserveSpaceForCompressedApex(CompressedApexInfoList compressed_apex_info_list) throws RemoteException {
        }

        @Override // android.apex.IApexService
        public ApexInfo installAndActivatePackage(String packagePath) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IApexService {
        static final int TRANSACTION_abortStagedSession = 9;
        static final int TRANSACTION_calculateSizeForCompressedApex = 24;
        static final int TRANSACTION_destroyCeSnapshots = 14;
        static final int TRANSACTION_destroyCeSnapshotsNotSpecified = 15;
        static final int TRANSACTION_destroyDeSnapshots = 13;
        static final int TRANSACTION_getActivePackage = 17;
        static final int TRANSACTION_getActivePackages = 7;
        static final int TRANSACTION_getAllPackages = 8;
        static final int TRANSACTION_getSessions = 4;
        static final int TRANSACTION_getStagedApexInfos = 6;
        static final int TRANSACTION_getStagedSessionInfo = 5;
        static final int TRANSACTION_installAndActivatePackage = 26;
        static final int TRANSACTION_markBootCompleted = 23;
        static final int TRANSACTION_markStagedSessionReady = 2;
        static final int TRANSACTION_markStagedSessionSuccessful = 3;
        static final int TRANSACTION_recollectDataApex = 22;
        static final int TRANSACTION_recollectPreinstalledData = 21;
        static final int TRANSACTION_remountPackages = 20;
        static final int TRANSACTION_reserveSpaceForCompressedApex = 25;
        static final int TRANSACTION_restoreCeData = 12;
        static final int TRANSACTION_resumeRevertIfNeeded = 19;
        static final int TRANSACTION_revertActiveSessions = 10;
        static final int TRANSACTION_snapshotCeData = 11;
        static final int TRANSACTION_stagePackages = 18;
        static final int TRANSACTION_submitStagedSession = 1;
        static final int TRANSACTION_unstagePackages = 16;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IApexService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IApexService)) {
                return (IApexService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(descriptor);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(descriptor);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ApexSessionParams _arg0 = (ApexSessionParams) data.readTypedObject(ApexSessionParams.CREATOR);
                            ApexInfoList _arg1 = new ApexInfoList();
                            submitStagedSession(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeTypedObject(_arg1, 1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            markStagedSessionReady(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            markStagedSessionSuccessful(_arg03);
                            reply.writeNoException();
                            break;
                        case 4:
                            ApexSessionInfo[] _result = getSessions();
                            reply.writeNoException();
                            reply.writeTypedArray(_result, 1);
                            break;
                        case 5:
                            int _arg04 = data.readInt();
                            ApexSessionInfo _result2 = getStagedSessionInfo(_arg04);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 6:
                            ApexSessionParams _arg05 = (ApexSessionParams) data.readTypedObject(ApexSessionParams.CREATOR);
                            ApexInfo[] _result3 = getStagedApexInfos(_arg05);
                            reply.writeNoException();
                            reply.writeTypedArray(_result3, 1);
                            break;
                        case 7:
                            ApexInfo[] _result4 = getActivePackages();
                            reply.writeNoException();
                            reply.writeTypedArray(_result4, 1);
                            break;
                        case 8:
                            ApexInfo[] _result5 = getAllPackages();
                            reply.writeNoException();
                            reply.writeTypedArray(_result5, 1);
                            break;
                        case 9:
                            int _arg06 = data.readInt();
                            abortStagedSession(_arg06);
                            reply.writeNoException();
                            break;
                        case 10:
                            revertActiveSessions();
                            reply.writeNoException();
                            break;
                        case 11:
                            int _arg07 = data.readInt();
                            int _arg12 = data.readInt();
                            String _arg2 = data.readString();
                            snapshotCeData(_arg07, _arg12, _arg2);
                            reply.writeNoException();
                            break;
                        case 12:
                            int _arg08 = data.readInt();
                            int _arg13 = data.readInt();
                            String _arg22 = data.readString();
                            restoreCeData(_arg08, _arg13, _arg22);
                            reply.writeNoException();
                            break;
                        case 13:
                            int _arg09 = data.readInt();
                            destroyDeSnapshots(_arg09);
                            reply.writeNoException();
                            break;
                        case 14:
                            int _arg010 = data.readInt();
                            destroyCeSnapshots(_arg010, data.readInt());
                            reply.writeNoException();
                            break;
                        case 15:
                            int _arg011 = data.readInt();
                            destroyCeSnapshotsNotSpecified(_arg011, data.createIntArray());
                            reply.writeNoException();
                            break;
                        case 16:
                            List<String> _arg012 = data.createStringArrayList();
                            unstagePackages(_arg012);
                            reply.writeNoException();
                            break;
                        case 17:
                            String _arg013 = data.readString();
                            ApexInfo _result6 = getActivePackage(_arg013);
                            reply.writeNoException();
                            reply.writeTypedObject(_result6, 1);
                            break;
                        case 18:
                            List<String> _arg014 = data.createStringArrayList();
                            stagePackages(_arg014);
                            reply.writeNoException();
                            break;
                        case 19:
                            resumeRevertIfNeeded();
                            reply.writeNoException();
                            break;
                        case 20:
                            remountPackages();
                            reply.writeNoException();
                            break;
                        case 21:
                            List<String> _arg015 = data.createStringArrayList();
                            recollectPreinstalledData(_arg015);
                            reply.writeNoException();
                            break;
                        case 22:
                            String _arg016 = data.readString();
                            recollectDataApex(_arg016, data.readString());
                            reply.writeNoException();
                            break;
                        case 23:
                            markBootCompleted();
                            reply.writeNoException();
                            break;
                        case 24:
                            CompressedApexInfoList _arg017 = (CompressedApexInfoList) data.readTypedObject(CompressedApexInfoList.CREATOR);
                            long _result7 = calculateSizeForCompressedApex(_arg017);
                            reply.writeNoException();
                            reply.writeLong(_result7);
                            break;
                        case 25:
                            CompressedApexInfoList _arg018 = (CompressedApexInfoList) data.readTypedObject(CompressedApexInfoList.CREATOR);
                            reserveSpaceForCompressedApex(_arg018);
                            reply.writeNoException();
                            break;
                        case 26:
                            String _arg019 = data.readString();
                            ApexInfo _result8 = installAndActivatePackage(_arg019);
                            reply.writeNoException();
                            reply.writeTypedObject(_result8, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IApexService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override // android.apex.IApexService
            public void submitStagedSession(ApexSessionParams params, ApexInfoList packages) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(params, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        packages.readFromParcel(_reply);
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public void markStagedSessionReady(int session_id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(session_id);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public void markStagedSessionSuccessful(int session_id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(session_id);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public ApexSessionInfo[] getSessions() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    ApexSessionInfo[] _result = (ApexSessionInfo[]) _reply.createTypedArray(ApexSessionInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public ApexSessionInfo getStagedSessionInfo(int session_id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(session_id);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    ApexSessionInfo _result = (ApexSessionInfo) _reply.readTypedObject(ApexSessionInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public ApexInfo[] getStagedApexInfos(ApexSessionParams params) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(params, 0);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    ApexInfo[] _result = (ApexInfo[]) _reply.createTypedArray(ApexInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public ApexInfo[] getActivePackages() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    ApexInfo[] _result = (ApexInfo[]) _reply.createTypedArray(ApexInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public ApexInfo[] getAllPackages() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    ApexInfo[] _result = (ApexInfo[]) _reply.createTypedArray(ApexInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public void abortStagedSession(int session_id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(session_id);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public void revertActiveSessions() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public void snapshotCeData(int user_id, int rollback_id, String apex_name) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(user_id);
                    _data.writeInt(rollback_id);
                    _data.writeString(apex_name);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public void restoreCeData(int user_id, int rollback_id, String apex_name) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(user_id);
                    _data.writeInt(rollback_id);
                    _data.writeString(apex_name);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public void destroyDeSnapshots(int rollback_id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(rollback_id);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public void destroyCeSnapshots(int user_id, int rollback_id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(user_id);
                    _data.writeInt(rollback_id);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public void destroyCeSnapshotsNotSpecified(int user_id, int[] retain_rollback_ids) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(user_id);
                    _data.writeIntArray(retain_rollback_ids);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public void unstagePackages(List<String> active_package_paths) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStringList(active_package_paths);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public ApexInfo getActivePackage(String package_name) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(package_name);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    ApexInfo _result = (ApexInfo) _reply.readTypedObject(ApexInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public void stagePackages(List<String> package_tmp_paths) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStringList(package_tmp_paths);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public void resumeRevertIfNeeded() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public void remountPackages() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public void recollectPreinstalledData(List<String> paths) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStringList(paths);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public void recollectDataApex(String path, String decompression_dir) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(path);
                    _data.writeString(decompression_dir);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public void markBootCompleted() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public long calculateSizeForCompressedApex(CompressedApexInfoList compressed_apex_info_list) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(compressed_apex_info_list, 0);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public void reserveSpaceForCompressedApex(CompressedApexInfoList compressed_apex_info_list) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(compressed_apex_info_list, 0);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apex.IApexService
            public ApexInfo installAndActivatePackage(String packagePath) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(packagePath);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    ApexInfo _result = (ApexInfo) _reply.readTypedObject(ApexInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
