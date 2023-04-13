package android.p008os;

import android.p008os.IIncidentAuthListener;
import android.p008os.IncidentManager;
import java.util.List;
/* renamed from: android.os.IIncidentCompanion */
/* loaded from: classes3.dex */
public interface IIncidentCompanion extends IInterface {
    public static final String DESCRIPTOR = "android.os.IIncidentCompanion";

    void approveReport(String str) throws RemoteException;

    void authorizeReport(int i, String str, String str2, String str3, int i2, IIncidentAuthListener iIncidentAuthListener) throws RemoteException;

    void cancelAuthorization(IIncidentAuthListener iIncidentAuthListener) throws RemoteException;

    void deleteAllIncidentReports(String str) throws RemoteException;

    void deleteIncidentReports(String str, String str2, String str3) throws RemoteException;

    void denyReport(String str) throws RemoteException;

    IncidentManager.IncidentReport getIncidentReport(String str, String str2, String str3) throws RemoteException;

    List<String> getIncidentReportList(String str, String str2) throws RemoteException;

    List<String> getPendingReports() throws RemoteException;

    void sendReportReadyBroadcast(String str, String str2) throws RemoteException;

    /* renamed from: android.os.IIncidentCompanion$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IIncidentCompanion {
        @Override // android.p008os.IIncidentCompanion
        public void authorizeReport(int callingUid, String callingPackage, String receiverClass, String reportId, int flags, IIncidentAuthListener callback) throws RemoteException {
        }

        @Override // android.p008os.IIncidentCompanion
        public void cancelAuthorization(IIncidentAuthListener callback) throws RemoteException {
        }

        @Override // android.p008os.IIncidentCompanion
        public void sendReportReadyBroadcast(String pkg, String cls) throws RemoteException {
        }

        @Override // android.p008os.IIncidentCompanion
        public List<String> getPendingReports() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IIncidentCompanion
        public void approveReport(String uri) throws RemoteException {
        }

        @Override // android.p008os.IIncidentCompanion
        public void denyReport(String uri) throws RemoteException {
        }

        @Override // android.p008os.IIncidentCompanion
        public List<String> getIncidentReportList(String pkg, String cls) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IIncidentCompanion
        public IncidentManager.IncidentReport getIncidentReport(String pkg, String cls, String id) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IIncidentCompanion
        public void deleteIncidentReports(String pkg, String cls, String id) throws RemoteException {
        }

        @Override // android.p008os.IIncidentCompanion
        public void deleteAllIncidentReports(String pkg) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IIncidentCompanion$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IIncidentCompanion {
        static final int TRANSACTION_approveReport = 5;
        static final int TRANSACTION_authorizeReport = 1;
        static final int TRANSACTION_cancelAuthorization = 2;
        static final int TRANSACTION_deleteAllIncidentReports = 10;
        static final int TRANSACTION_deleteIncidentReports = 9;
        static final int TRANSACTION_denyReport = 6;
        static final int TRANSACTION_getIncidentReport = 8;
        static final int TRANSACTION_getIncidentReportList = 7;
        static final int TRANSACTION_getPendingReports = 4;
        static final int TRANSACTION_sendReportReadyBroadcast = 3;

        public Stub() {
            attachInterface(this, IIncidentCompanion.DESCRIPTOR);
        }

        public static IIncidentCompanion asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IIncidentCompanion.DESCRIPTOR);
            if (iin != null && (iin instanceof IIncidentCompanion)) {
                return (IIncidentCompanion) iin;
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
                    return "authorizeReport";
                case 2:
                    return "cancelAuthorization";
                case 3:
                    return "sendReportReadyBroadcast";
                case 4:
                    return "getPendingReports";
                case 5:
                    return "approveReport";
                case 6:
                    return "denyReport";
                case 7:
                    return "getIncidentReportList";
                case 8:
                    return "getIncidentReport";
                case 9:
                    return "deleteIncidentReports";
                case 10:
                    return "deleteAllIncidentReports";
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
                data.enforceInterface(IIncidentCompanion.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IIncidentCompanion.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            String _arg1 = data.readString();
                            String _arg2 = data.readString();
                            String _arg3 = data.readString();
                            int _arg4 = data.readInt();
                            IIncidentAuthListener _arg5 = IIncidentAuthListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            authorizeReport(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
                            break;
                        case 2:
                            IIncidentAuthListener _arg02 = IIncidentAuthListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            cancelAuthorization(_arg02);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            String _arg12 = data.readString();
                            data.enforceNoDataAvail();
                            sendReportReadyBroadcast(_arg03, _arg12);
                            break;
                        case 4:
                            List<String> _result = getPendingReports();
                            reply.writeNoException();
                            reply.writeStringList(_result);
                            break;
                        case 5:
                            String _arg04 = data.readString();
                            data.enforceNoDataAvail();
                            approveReport(_arg04);
                            reply.writeNoException();
                            break;
                        case 6:
                            String _arg05 = data.readString();
                            data.enforceNoDataAvail();
                            denyReport(_arg05);
                            reply.writeNoException();
                            break;
                        case 7:
                            String _arg06 = data.readString();
                            String _arg13 = data.readString();
                            data.enforceNoDataAvail();
                            List<String> _result2 = getIncidentReportList(_arg06, _arg13);
                            reply.writeNoException();
                            reply.writeStringList(_result2);
                            break;
                        case 8:
                            String _arg07 = data.readString();
                            String _arg14 = data.readString();
                            String _arg22 = data.readString();
                            data.enforceNoDataAvail();
                            IncidentManager.IncidentReport _result3 = getIncidentReport(_arg07, _arg14, _arg22);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 9:
                            String _arg08 = data.readString();
                            String _arg15 = data.readString();
                            String _arg23 = data.readString();
                            data.enforceNoDataAvail();
                            deleteIncidentReports(_arg08, _arg15, _arg23);
                            reply.writeNoException();
                            break;
                        case 10:
                            String _arg09 = data.readString();
                            data.enforceNoDataAvail();
                            deleteAllIncidentReports(_arg09);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.os.IIncidentCompanion$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements IIncidentCompanion {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IIncidentCompanion.DESCRIPTOR;
            }

            @Override // android.p008os.IIncidentCompanion
            public void authorizeReport(int callingUid, String callingPackage, String receiverClass, String reportId, int flags, IIncidentAuthListener callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IIncidentCompanion.DESCRIPTOR);
                    _data.writeInt(callingUid);
                    _data.writeString(callingPackage);
                    _data.writeString(receiverClass);
                    _data.writeString(reportId);
                    _data.writeInt(flags);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IIncidentCompanion
            public void cancelAuthorization(IIncidentAuthListener callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IIncidentCompanion.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IIncidentCompanion
            public void sendReportReadyBroadcast(String pkg, String cls) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IIncidentCompanion.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeString(cls);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IIncidentCompanion
            public List<String> getPendingReports() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncidentCompanion.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IIncidentCompanion
            public void approveReport(String uri) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncidentCompanion.DESCRIPTOR);
                    _data.writeString(uri);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IIncidentCompanion
            public void denyReport(String uri) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncidentCompanion.DESCRIPTOR);
                    _data.writeString(uri);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IIncidentCompanion
            public List<String> getIncidentReportList(String pkg, String cls) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncidentCompanion.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeString(cls);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IIncidentCompanion
            public IncidentManager.IncidentReport getIncidentReport(String pkg, String cls, String id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncidentCompanion.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeString(cls);
                    _data.writeString(id);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    IncidentManager.IncidentReport _result = (IncidentManager.IncidentReport) _reply.readTypedObject(IncidentManager.IncidentReport.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IIncidentCompanion
            public void deleteIncidentReports(String pkg, String cls, String id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncidentCompanion.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeString(cls);
                    _data.writeString(id);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IIncidentCompanion
            public void deleteAllIncidentReports(String pkg) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncidentCompanion.DESCRIPTOR);
                    _data.writeString(pkg);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 9;
        }
    }
}
