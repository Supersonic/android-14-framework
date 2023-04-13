package android.service.contentcapture;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.service.contentcapture.IDataShareCallback;
import android.view.contentcapture.ContentCaptureContext;
import android.view.contentcapture.DataRemovalRequest;
import android.view.contentcapture.DataShareRequest;
import com.android.internal.p028os.IResultReceiver;
/* loaded from: classes3.dex */
public interface IContentCaptureService extends IInterface {
    public static final String DESCRIPTOR = "android.service.contentcapture.IContentCaptureService";

    void onActivityEvent(ActivityEvent activityEvent) throws RemoteException;

    void onActivitySnapshot(int i, SnapshotData snapshotData) throws RemoteException;

    void onConnected(IBinder iBinder, boolean z, boolean z2) throws RemoteException;

    void onDataRemovalRequest(DataRemovalRequest dataRemovalRequest) throws RemoteException;

    void onDataShared(DataShareRequest dataShareRequest, IDataShareCallback iDataShareCallback) throws RemoteException;

    void onDisconnected() throws RemoteException;

    void onSessionFinished(int i) throws RemoteException;

    void onSessionStarted(ContentCaptureContext contentCaptureContext, int i, int i2, IResultReceiver iResultReceiver, int i3) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IContentCaptureService {
        @Override // android.service.contentcapture.IContentCaptureService
        public void onConnected(IBinder callback, boolean verbose, boolean debug) throws RemoteException {
        }

        @Override // android.service.contentcapture.IContentCaptureService
        public void onDisconnected() throws RemoteException {
        }

        @Override // android.service.contentcapture.IContentCaptureService
        public void onSessionStarted(ContentCaptureContext context, int sessionId, int uid, IResultReceiver clientReceiver, int initialState) throws RemoteException {
        }

        @Override // android.service.contentcapture.IContentCaptureService
        public void onSessionFinished(int sessionId) throws RemoteException {
        }

        @Override // android.service.contentcapture.IContentCaptureService
        public void onActivitySnapshot(int sessionId, SnapshotData snapshotData) throws RemoteException {
        }

        @Override // android.service.contentcapture.IContentCaptureService
        public void onDataRemovalRequest(DataRemovalRequest request) throws RemoteException {
        }

        @Override // android.service.contentcapture.IContentCaptureService
        public void onDataShared(DataShareRequest request, IDataShareCallback callback) throws RemoteException {
        }

        @Override // android.service.contentcapture.IContentCaptureService
        public void onActivityEvent(ActivityEvent event) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IContentCaptureService {
        static final int TRANSACTION_onActivityEvent = 8;
        static final int TRANSACTION_onActivitySnapshot = 5;
        static final int TRANSACTION_onConnected = 1;
        static final int TRANSACTION_onDataRemovalRequest = 6;
        static final int TRANSACTION_onDataShared = 7;
        static final int TRANSACTION_onDisconnected = 2;
        static final int TRANSACTION_onSessionFinished = 4;
        static final int TRANSACTION_onSessionStarted = 3;

        public Stub() {
            attachInterface(this, IContentCaptureService.DESCRIPTOR);
        }

        public static IContentCaptureService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IContentCaptureService.DESCRIPTOR);
            if (iin != null && (iin instanceof IContentCaptureService)) {
                return (IContentCaptureService) iin;
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
                    return "onConnected";
                case 2:
                    return "onDisconnected";
                case 3:
                    return "onSessionStarted";
                case 4:
                    return "onSessionFinished";
                case 5:
                    return "onActivitySnapshot";
                case 6:
                    return "onDataRemovalRequest";
                case 7:
                    return "onDataShared";
                case 8:
                    return "onActivityEvent";
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
                data.enforceInterface(IContentCaptureService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IContentCaptureService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IBinder _arg0 = data.readStrongBinder();
                            boolean _arg1 = data.readBoolean();
                            boolean _arg2 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onConnected(_arg0, _arg1, _arg2);
                            break;
                        case 2:
                            onDisconnected();
                            break;
                        case 3:
                            ContentCaptureContext _arg02 = (ContentCaptureContext) data.readTypedObject(ContentCaptureContext.CREATOR);
                            int _arg12 = data.readInt();
                            int _arg22 = data.readInt();
                            IResultReceiver _arg3 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            int _arg4 = data.readInt();
                            data.enforceNoDataAvail();
                            onSessionStarted(_arg02, _arg12, _arg22, _arg3, _arg4);
                            break;
                        case 4:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            onSessionFinished(_arg03);
                            break;
                        case 5:
                            int _arg04 = data.readInt();
                            SnapshotData _arg13 = (SnapshotData) data.readTypedObject(SnapshotData.CREATOR);
                            data.enforceNoDataAvail();
                            onActivitySnapshot(_arg04, _arg13);
                            break;
                        case 6:
                            DataRemovalRequest _arg05 = (DataRemovalRequest) data.readTypedObject(DataRemovalRequest.CREATOR);
                            data.enforceNoDataAvail();
                            onDataRemovalRequest(_arg05);
                            break;
                        case 7:
                            DataShareRequest _arg06 = (DataShareRequest) data.readTypedObject(DataShareRequest.CREATOR);
                            IDataShareCallback _arg14 = IDataShareCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onDataShared(_arg06, _arg14);
                            break;
                        case 8:
                            ActivityEvent _arg07 = (ActivityEvent) data.readTypedObject(ActivityEvent.CREATOR);
                            data.enforceNoDataAvail();
                            onActivityEvent(_arg07);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IContentCaptureService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IContentCaptureService.DESCRIPTOR;
            }

            @Override // android.service.contentcapture.IContentCaptureService
            public void onConnected(IBinder callback, boolean verbose, boolean debug) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureService.DESCRIPTOR);
                    _data.writeStrongBinder(callback);
                    _data.writeBoolean(verbose);
                    _data.writeBoolean(debug);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.contentcapture.IContentCaptureService
            public void onDisconnected() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureService.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.contentcapture.IContentCaptureService
            public void onSessionStarted(ContentCaptureContext context, int sessionId, int uid, IResultReceiver clientReceiver, int initialState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureService.DESCRIPTOR);
                    _data.writeTypedObject(context, 0);
                    _data.writeInt(sessionId);
                    _data.writeInt(uid);
                    _data.writeStrongInterface(clientReceiver);
                    _data.writeInt(initialState);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.contentcapture.IContentCaptureService
            public void onSessionFinished(int sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureService.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.contentcapture.IContentCaptureService
            public void onActivitySnapshot(int sessionId, SnapshotData snapshotData) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureService.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeTypedObject(snapshotData, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.contentcapture.IContentCaptureService
            public void onDataRemovalRequest(DataRemovalRequest request) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureService.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.contentcapture.IContentCaptureService
            public void onDataShared(DataShareRequest request, IDataShareCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureService.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.contentcapture.IContentCaptureService
            public void onActivityEvent(ActivityEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureService.DESCRIPTOR);
                    _data.writeTypedObject(event, 0);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 7;
        }
    }
}
