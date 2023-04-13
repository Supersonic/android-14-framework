package android.window;

import android.content.p001pm.ParceledListSlice;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.window.IDisplayAreaOrganizer;
/* loaded from: classes4.dex */
public interface IDisplayAreaOrganizerController extends IInterface {
    public static final String DESCRIPTOR = "android.window.IDisplayAreaOrganizerController";

    DisplayAreaAppearedInfo createTaskDisplayArea(IDisplayAreaOrganizer iDisplayAreaOrganizer, int i, int i2, String str) throws RemoteException;

    void deleteTaskDisplayArea(WindowContainerToken windowContainerToken) throws RemoteException;

    ParceledListSlice<DisplayAreaAppearedInfo> registerOrganizer(IDisplayAreaOrganizer iDisplayAreaOrganizer, int i) throws RemoteException;

    void unregisterOrganizer(IDisplayAreaOrganizer iDisplayAreaOrganizer) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IDisplayAreaOrganizerController {
        @Override // android.window.IDisplayAreaOrganizerController
        public ParceledListSlice<DisplayAreaAppearedInfo> registerOrganizer(IDisplayAreaOrganizer organizer, int displayAreaFeature) throws RemoteException {
            return null;
        }

        @Override // android.window.IDisplayAreaOrganizerController
        public void unregisterOrganizer(IDisplayAreaOrganizer organizer) throws RemoteException {
        }

        @Override // android.window.IDisplayAreaOrganizerController
        public DisplayAreaAppearedInfo createTaskDisplayArea(IDisplayAreaOrganizer organizer, int displayId, int parentFeatureId, String name) throws RemoteException {
            return null;
        }

        @Override // android.window.IDisplayAreaOrganizerController
        public void deleteTaskDisplayArea(WindowContainerToken taskDisplayArea) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IDisplayAreaOrganizerController {
        static final int TRANSACTION_createTaskDisplayArea = 3;
        static final int TRANSACTION_deleteTaskDisplayArea = 4;
        static final int TRANSACTION_registerOrganizer = 1;
        static final int TRANSACTION_unregisterOrganizer = 2;

        public Stub() {
            attachInterface(this, IDisplayAreaOrganizerController.DESCRIPTOR);
        }

        public static IDisplayAreaOrganizerController asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IDisplayAreaOrganizerController.DESCRIPTOR);
            if (iin != null && (iin instanceof IDisplayAreaOrganizerController)) {
                return (IDisplayAreaOrganizerController) iin;
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
                    return "registerOrganizer";
                case 2:
                    return "unregisterOrganizer";
                case 3:
                    return "createTaskDisplayArea";
                case 4:
                    return "deleteTaskDisplayArea";
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
                data.enforceInterface(IDisplayAreaOrganizerController.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IDisplayAreaOrganizerController.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IDisplayAreaOrganizer _arg0 = IDisplayAreaOrganizer.Stub.asInterface(data.readStrongBinder());
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            ParceledListSlice<DisplayAreaAppearedInfo> _result = registerOrganizer(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 2:
                            IDisplayAreaOrganizer _arg02 = IDisplayAreaOrganizer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterOrganizer(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            IDisplayAreaOrganizer _arg03 = IDisplayAreaOrganizer.Stub.asInterface(data.readStrongBinder());
                            int _arg12 = data.readInt();
                            int _arg2 = data.readInt();
                            String _arg3 = data.readString();
                            data.enforceNoDataAvail();
                            DisplayAreaAppearedInfo _result2 = createTaskDisplayArea(_arg03, _arg12, _arg2, _arg3);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 4:
                            WindowContainerToken _arg04 = (WindowContainerToken) data.readTypedObject(WindowContainerToken.CREATOR);
                            data.enforceNoDataAvail();
                            deleteTaskDisplayArea(_arg04);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IDisplayAreaOrganizerController {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IDisplayAreaOrganizerController.DESCRIPTOR;
            }

            @Override // android.window.IDisplayAreaOrganizerController
            public ParceledListSlice<DisplayAreaAppearedInfo> registerOrganizer(IDisplayAreaOrganizer organizer, int displayAreaFeature) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDisplayAreaOrganizerController.DESCRIPTOR);
                    _data.writeStrongInterface(organizer);
                    _data.writeInt(displayAreaFeature);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice<DisplayAreaAppearedInfo> _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.IDisplayAreaOrganizerController
            public void unregisterOrganizer(IDisplayAreaOrganizer organizer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDisplayAreaOrganizerController.DESCRIPTOR);
                    _data.writeStrongInterface(organizer);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.IDisplayAreaOrganizerController
            public DisplayAreaAppearedInfo createTaskDisplayArea(IDisplayAreaOrganizer organizer, int displayId, int parentFeatureId, String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDisplayAreaOrganizerController.DESCRIPTOR);
                    _data.writeStrongInterface(organizer);
                    _data.writeInt(displayId);
                    _data.writeInt(parentFeatureId);
                    _data.writeString(name);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    DisplayAreaAppearedInfo _result = (DisplayAreaAppearedInfo) _reply.readTypedObject(DisplayAreaAppearedInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.IDisplayAreaOrganizerController
            public void deleteTaskDisplayArea(WindowContainerToken taskDisplayArea) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDisplayAreaOrganizerController.DESCRIPTOR);
                    _data.writeTypedObject(taskDisplayArea, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 3;
        }
    }
}
