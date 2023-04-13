package android.telephony.mbms.vendor;

import android.content.ContentResolver;
import android.content.Context;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.mbms.DownloadRequest;
import android.telephony.mbms.FileInfo;
import android.telephony.mbms.IDownloadProgressListener;
import android.telephony.mbms.IDownloadStatusListener;
import android.telephony.mbms.IMbmsDownloadSessionCallback;
import java.util.List;
/* loaded from: classes3.dex */
public interface IMbmsDownloadService extends IInterface {
    int addProgressListener(DownloadRequest downloadRequest, IDownloadProgressListener iDownloadProgressListener) throws RemoteException;

    int addServiceAnnouncement(int i, byte[] bArr) throws RemoteException;

    int addStatusListener(DownloadRequest downloadRequest, IDownloadStatusListener iDownloadStatusListener) throws RemoteException;

    int cancelDownload(DownloadRequest downloadRequest) throws RemoteException;

    void dispose(int i) throws RemoteException;

    int download(DownloadRequest downloadRequest) throws RemoteException;

    int initialize(int i, IMbmsDownloadSessionCallback iMbmsDownloadSessionCallback) throws RemoteException;

    List<DownloadRequest> listPendingDownloads(int i) throws RemoteException;

    int removeProgressListener(DownloadRequest downloadRequest, IDownloadProgressListener iDownloadProgressListener) throws RemoteException;

    int removeStatusListener(DownloadRequest downloadRequest, IDownloadStatusListener iDownloadStatusListener) throws RemoteException;

    int requestDownloadState(DownloadRequest downloadRequest, FileInfo fileInfo) throws RemoteException;

    int requestUpdateFileServices(int i, List<String> list) throws RemoteException;

    int resetDownloadKnowledge(DownloadRequest downloadRequest) throws RemoteException;

    int setTempFileRootDirectory(int i, String str) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IMbmsDownloadService {
        @Override // android.telephony.mbms.vendor.IMbmsDownloadService
        public int initialize(int subId, IMbmsDownloadSessionCallback listener) throws RemoteException {
            return 0;
        }

        @Override // android.telephony.mbms.vendor.IMbmsDownloadService
        public int requestUpdateFileServices(int subId, List<String> serviceClasses) throws RemoteException {
            return 0;
        }

        @Override // android.telephony.mbms.vendor.IMbmsDownloadService
        public int setTempFileRootDirectory(int subId, String rootDirectoryPath) throws RemoteException {
            return 0;
        }

        @Override // android.telephony.mbms.vendor.IMbmsDownloadService
        public int addServiceAnnouncement(int subId, byte[] contents) throws RemoteException {
            return 0;
        }

        @Override // android.telephony.mbms.vendor.IMbmsDownloadService
        public int download(DownloadRequest downloadRequest) throws RemoteException {
            return 0;
        }

        @Override // android.telephony.mbms.vendor.IMbmsDownloadService
        public int addStatusListener(DownloadRequest downloadRequest, IDownloadStatusListener listener) throws RemoteException {
            return 0;
        }

        @Override // android.telephony.mbms.vendor.IMbmsDownloadService
        public int removeStatusListener(DownloadRequest downloadRequest, IDownloadStatusListener listener) throws RemoteException {
            return 0;
        }

        @Override // android.telephony.mbms.vendor.IMbmsDownloadService
        public int addProgressListener(DownloadRequest downloadRequest, IDownloadProgressListener listener) throws RemoteException {
            return 0;
        }

        @Override // android.telephony.mbms.vendor.IMbmsDownloadService
        public int removeProgressListener(DownloadRequest downloadRequest, IDownloadProgressListener listener) throws RemoteException {
            return 0;
        }

        @Override // android.telephony.mbms.vendor.IMbmsDownloadService
        public List<DownloadRequest> listPendingDownloads(int subscriptionId) throws RemoteException {
            return null;
        }

        @Override // android.telephony.mbms.vendor.IMbmsDownloadService
        public int cancelDownload(DownloadRequest downloadRequest) throws RemoteException {
            return 0;
        }

        @Override // android.telephony.mbms.vendor.IMbmsDownloadService
        public int requestDownloadState(DownloadRequest downloadRequest, FileInfo fileInfo) throws RemoteException {
            return 0;
        }

        @Override // android.telephony.mbms.vendor.IMbmsDownloadService
        public int resetDownloadKnowledge(DownloadRequest downloadRequest) throws RemoteException {
            return 0;
        }

        @Override // android.telephony.mbms.vendor.IMbmsDownloadService
        public void dispose(int subId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IMbmsDownloadService {
        public static final String DESCRIPTOR = "android.telephony.mbms.vendor.IMbmsDownloadService";
        static final int TRANSACTION_addProgressListener = 8;
        static final int TRANSACTION_addServiceAnnouncement = 4;
        static final int TRANSACTION_addStatusListener = 6;
        static final int TRANSACTION_cancelDownload = 11;
        static final int TRANSACTION_dispose = 14;
        static final int TRANSACTION_download = 5;
        static final int TRANSACTION_initialize = 1;
        static final int TRANSACTION_listPendingDownloads = 10;
        static final int TRANSACTION_removeProgressListener = 9;
        static final int TRANSACTION_removeStatusListener = 7;
        static final int TRANSACTION_requestDownloadState = 12;
        static final int TRANSACTION_requestUpdateFileServices = 2;
        static final int TRANSACTION_resetDownloadKnowledge = 13;
        static final int TRANSACTION_setTempFileRootDirectory = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMbmsDownloadService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IMbmsDownloadService)) {
                return (IMbmsDownloadService) iin;
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
                    return ContentResolver.SYNC_EXTRAS_INITIALIZE;
                case 2:
                    return "requestUpdateFileServices";
                case 3:
                    return "setTempFileRootDirectory";
                case 4:
                    return "addServiceAnnouncement";
                case 5:
                    return Context.DOWNLOAD_SERVICE;
                case 6:
                    return "addStatusListener";
                case 7:
                    return "removeStatusListener";
                case 8:
                    return "addProgressListener";
                case 9:
                    return "removeProgressListener";
                case 10:
                    return "listPendingDownloads";
                case 11:
                    return "cancelDownload";
                case 12:
                    return "requestDownloadState";
                case 13:
                    return "resetDownloadKnowledge";
                case 14:
                    return "dispose";
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
                            int _arg0 = data.readInt();
                            IMbmsDownloadSessionCallback _arg1 = IMbmsDownloadSessionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result = initialize(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            List<String> _arg12 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            int _result2 = requestUpdateFileServices(_arg02, _arg12);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            String _arg13 = data.readString();
                            data.enforceNoDataAvail();
                            int _result3 = setTempFileRootDirectory(_arg03, _arg13);
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            byte[] _arg14 = data.createByteArray();
                            data.enforceNoDataAvail();
                            int _result4 = addServiceAnnouncement(_arg04, _arg14);
                            reply.writeNoException();
                            reply.writeInt(_result4);
                            break;
                        case 5:
                            DownloadRequest _arg05 = (DownloadRequest) data.readTypedObject(DownloadRequest.CREATOR);
                            data.enforceNoDataAvail();
                            int _result5 = download(_arg05);
                            reply.writeNoException();
                            reply.writeInt(_result5);
                            break;
                        case 6:
                            DownloadRequest _arg06 = (DownloadRequest) data.readTypedObject(DownloadRequest.CREATOR);
                            IDownloadStatusListener _arg15 = IDownloadStatusListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result6 = addStatusListener(_arg06, _arg15);
                            reply.writeNoException();
                            reply.writeInt(_result6);
                            break;
                        case 7:
                            DownloadRequest _arg07 = (DownloadRequest) data.readTypedObject(DownloadRequest.CREATOR);
                            IDownloadStatusListener _arg16 = IDownloadStatusListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result7 = removeStatusListener(_arg07, _arg16);
                            reply.writeNoException();
                            reply.writeInt(_result7);
                            break;
                        case 8:
                            DownloadRequest _arg08 = (DownloadRequest) data.readTypedObject(DownloadRequest.CREATOR);
                            IDownloadProgressListener _arg17 = IDownloadProgressListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result8 = addProgressListener(_arg08, _arg17);
                            reply.writeNoException();
                            reply.writeInt(_result8);
                            break;
                        case 9:
                            DownloadRequest _arg09 = (DownloadRequest) data.readTypedObject(DownloadRequest.CREATOR);
                            IDownloadProgressListener _arg18 = IDownloadProgressListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result9 = removeProgressListener(_arg09, _arg18);
                            reply.writeNoException();
                            reply.writeInt(_result9);
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            data.enforceNoDataAvail();
                            List<DownloadRequest> _result10 = listPendingDownloads(_arg010);
                            reply.writeNoException();
                            reply.writeTypedList(_result10, 1);
                            break;
                        case 11:
                            DownloadRequest _arg011 = (DownloadRequest) data.readTypedObject(DownloadRequest.CREATOR);
                            data.enforceNoDataAvail();
                            int _result11 = cancelDownload(_arg011);
                            reply.writeNoException();
                            reply.writeInt(_result11);
                            break;
                        case 12:
                            DownloadRequest _arg012 = (DownloadRequest) data.readTypedObject(DownloadRequest.CREATOR);
                            FileInfo _arg19 = (FileInfo) data.readTypedObject(FileInfo.CREATOR);
                            data.enforceNoDataAvail();
                            int _result12 = requestDownloadState(_arg012, _arg19);
                            reply.writeNoException();
                            reply.writeInt(_result12);
                            break;
                        case 13:
                            DownloadRequest _arg013 = (DownloadRequest) data.readTypedObject(DownloadRequest.CREATOR);
                            data.enforceNoDataAvail();
                            int _result13 = resetDownloadKnowledge(_arg013);
                            reply.writeNoException();
                            reply.writeInt(_result13);
                            break;
                        case 14:
                            int _arg014 = data.readInt();
                            data.enforceNoDataAvail();
                            dispose(_arg014);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IMbmsDownloadService {
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

            @Override // android.telephony.mbms.vendor.IMbmsDownloadService
            public int initialize(int subId, IMbmsDownloadSessionCallback listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.vendor.IMbmsDownloadService
            public int requestUpdateFileServices(int subId, List<String> serviceClasses) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeStringList(serviceClasses);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.vendor.IMbmsDownloadService
            public int setTempFileRootDirectory(int subId, String rootDirectoryPath) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(rootDirectoryPath);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.vendor.IMbmsDownloadService
            public int addServiceAnnouncement(int subId, byte[] contents) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeByteArray(contents);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.vendor.IMbmsDownloadService
            public int download(DownloadRequest downloadRequest) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(downloadRequest, 0);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.vendor.IMbmsDownloadService
            public int addStatusListener(DownloadRequest downloadRequest, IDownloadStatusListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(downloadRequest, 0);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.vendor.IMbmsDownloadService
            public int removeStatusListener(DownloadRequest downloadRequest, IDownloadStatusListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(downloadRequest, 0);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.vendor.IMbmsDownloadService
            public int addProgressListener(DownloadRequest downloadRequest, IDownloadProgressListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(downloadRequest, 0);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.vendor.IMbmsDownloadService
            public int removeProgressListener(DownloadRequest downloadRequest, IDownloadProgressListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(downloadRequest, 0);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.vendor.IMbmsDownloadService
            public List<DownloadRequest> listPendingDownloads(int subscriptionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subscriptionId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    List<DownloadRequest> _result = _reply.createTypedArrayList(DownloadRequest.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.vendor.IMbmsDownloadService
            public int cancelDownload(DownloadRequest downloadRequest) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(downloadRequest, 0);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.vendor.IMbmsDownloadService
            public int requestDownloadState(DownloadRequest downloadRequest, FileInfo fileInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(downloadRequest, 0);
                    _data.writeTypedObject(fileInfo, 0);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.vendor.IMbmsDownloadService
            public int resetDownloadKnowledge(DownloadRequest downloadRequest) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(downloadRequest, 0);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.vendor.IMbmsDownloadService
            public void dispose(int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 13;
        }
    }
}
