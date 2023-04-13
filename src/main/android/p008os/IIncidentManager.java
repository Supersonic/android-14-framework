package android.p008os;

import android.p008os.IIncidentDumpCallback;
import android.p008os.IIncidentReportStatusListener;
import android.p008os.IncidentManager;
import java.io.FileDescriptor;
import java.util.List;
/* renamed from: android.os.IIncidentManager */
/* loaded from: classes3.dex */
public interface IIncidentManager extends IInterface {
    void deleteAllIncidentReports(String str) throws RemoteException;

    void deleteIncidentReports(String str, String str2, String str3) throws RemoteException;

    IncidentManager.IncidentReport getIncidentReport(String str, String str2, String str3) throws RemoteException;

    List<String> getIncidentReportList(String str, String str2) throws RemoteException;

    void registerSection(int i, String str, IIncidentDumpCallback iIncidentDumpCallback) throws RemoteException;

    void reportIncident(IncidentReportArgs incidentReportArgs) throws RemoteException;

    void reportIncidentToDumpstate(FileDescriptor fileDescriptor, IIncidentReportStatusListener iIncidentReportStatusListener) throws RemoteException;

    void reportIncidentToStream(IncidentReportArgs incidentReportArgs, IIncidentReportStatusListener iIncidentReportStatusListener, FileDescriptor fileDescriptor) throws RemoteException;

    void systemRunning() throws RemoteException;

    void unregisterSection(int i) throws RemoteException;

    /* renamed from: android.os.IIncidentManager$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IIncidentManager {
        @Override // android.p008os.IIncidentManager
        public void reportIncident(IncidentReportArgs args) throws RemoteException {
        }

        @Override // android.p008os.IIncidentManager
        public void reportIncidentToStream(IncidentReportArgs args, IIncidentReportStatusListener listener, FileDescriptor stream) throws RemoteException {
        }

        @Override // android.p008os.IIncidentManager
        public void reportIncidentToDumpstate(FileDescriptor stream, IIncidentReportStatusListener listener) throws RemoteException {
        }

        @Override // android.p008os.IIncidentManager
        public void registerSection(int id, String name, IIncidentDumpCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IIncidentManager
        public void unregisterSection(int id) throws RemoteException {
        }

        @Override // android.p008os.IIncidentManager
        public void systemRunning() throws RemoteException {
        }

        @Override // android.p008os.IIncidentManager
        public List<String> getIncidentReportList(String pkg, String cls) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IIncidentManager
        public IncidentManager.IncidentReport getIncidentReport(String pkg, String cls, String id) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IIncidentManager
        public void deleteIncidentReports(String pkg, String cls, String id) throws RemoteException {
        }

        @Override // android.p008os.IIncidentManager
        public void deleteAllIncidentReports(String pkg) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IIncidentManager$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IIncidentManager {
        public static final String DESCRIPTOR = "android.os.IIncidentManager";
        static final int TRANSACTION_deleteAllIncidentReports = 10;
        static final int TRANSACTION_deleteIncidentReports = 9;
        static final int TRANSACTION_getIncidentReport = 8;
        static final int TRANSACTION_getIncidentReportList = 7;
        static final int TRANSACTION_registerSection = 4;
        static final int TRANSACTION_reportIncident = 1;
        static final int TRANSACTION_reportIncidentToDumpstate = 3;
        static final int TRANSACTION_reportIncidentToStream = 2;
        static final int TRANSACTION_systemRunning = 6;
        static final int TRANSACTION_unregisterSection = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IIncidentManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IIncidentManager)) {
                return (IIncidentManager) iin;
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
                    return "reportIncident";
                case 2:
                    return "reportIncidentToStream";
                case 3:
                    return "reportIncidentToDumpstate";
                case 4:
                    return "registerSection";
                case 5:
                    return "unregisterSection";
                case 6:
                    return "systemRunning";
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
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IncidentReportArgs _arg0 = (IncidentReportArgs) data.readTypedObject(IncidentReportArgs.CREATOR);
                            data.enforceNoDataAvail();
                            reportIncident(_arg0);
                            break;
                        case 2:
                            IncidentReportArgs _arg02 = (IncidentReportArgs) data.readTypedObject(IncidentReportArgs.CREATOR);
                            IIncidentReportStatusListener _arg1 = IIncidentReportStatusListener.Stub.asInterface(data.readStrongBinder());
                            FileDescriptor _arg2 = data.readRawFileDescriptor();
                            data.enforceNoDataAvail();
                            reportIncidentToStream(_arg02, _arg1, _arg2);
                            break;
                        case 3:
                            FileDescriptor _arg03 = data.readRawFileDescriptor();
                            IIncidentReportStatusListener _arg12 = IIncidentReportStatusListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            reportIncidentToDumpstate(_arg03, _arg12);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            String _arg13 = data.readString();
                            IIncidentDumpCallback _arg22 = IIncidentDumpCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerSection(_arg04, _arg13, _arg22);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            unregisterSection(_arg05);
                            break;
                        case 6:
                            systemRunning();
                            break;
                        case 7:
                            String _arg06 = data.readString();
                            String _arg14 = data.readString();
                            data.enforceNoDataAvail();
                            List<String> _result = getIncidentReportList(_arg06, _arg14);
                            reply.writeNoException();
                            reply.writeStringList(_result);
                            break;
                        case 8:
                            String _arg07 = data.readString();
                            String _arg15 = data.readString();
                            String _arg23 = data.readString();
                            data.enforceNoDataAvail();
                            IncidentManager.IncidentReport _result2 = getIncidentReport(_arg07, _arg15, _arg23);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 9:
                            String _arg08 = data.readString();
                            String _arg16 = data.readString();
                            String _arg24 = data.readString();
                            data.enforceNoDataAvail();
                            deleteIncidentReports(_arg08, _arg16, _arg24);
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

        /* renamed from: android.os.IIncidentManager$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements IIncidentManager {
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

            @Override // android.p008os.IIncidentManager
            public void reportIncident(IncidentReportArgs args) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(args, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IIncidentManager
            public void reportIncidentToStream(IncidentReportArgs args, IIncidentReportStatusListener listener, FileDescriptor stream) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(args, 0);
                    _data.writeStrongInterface(listener);
                    _data.writeRawFileDescriptor(stream);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IIncidentManager
            public void reportIncidentToDumpstate(FileDescriptor stream, IIncidentReportStatusListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeRawFileDescriptor(stream);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IIncidentManager
            public void registerSection(int id, String name, IIncidentDumpCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    _data.writeString(name);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IIncidentManager
            public void unregisterSection(int id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IIncidentManager
            public void systemRunning() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IIncidentManager
            public List<String> getIncidentReportList(String pkg, String cls) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // android.p008os.IIncidentManager
            public IncidentManager.IncidentReport getIncidentReport(String pkg, String cls, String id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // android.p008os.IIncidentManager
            public void deleteIncidentReports(String pkg, String cls, String id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // android.p008os.IIncidentManager
            public void deleteAllIncidentReports(String pkg) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
