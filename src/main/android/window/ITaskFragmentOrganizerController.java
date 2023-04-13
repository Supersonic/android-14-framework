package android.window;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.RemoteAnimationDefinition;
import android.window.ITaskFragmentOrganizer;
/* loaded from: classes4.dex */
public interface ITaskFragmentOrganizerController extends IInterface {
    public static final String DESCRIPTOR = "android.window.ITaskFragmentOrganizerController";

    void applyTransaction(WindowContainerTransaction windowContainerTransaction, int i, boolean z) throws RemoteException;

    boolean isActivityEmbedded(IBinder iBinder) throws RemoteException;

    void onTransactionHandled(IBinder iBinder, WindowContainerTransaction windowContainerTransaction, int i, boolean z) throws RemoteException;

    void registerOrganizer(ITaskFragmentOrganizer iTaskFragmentOrganizer) throws RemoteException;

    void registerRemoteAnimations(ITaskFragmentOrganizer iTaskFragmentOrganizer, RemoteAnimationDefinition remoteAnimationDefinition) throws RemoteException;

    void unregisterOrganizer(ITaskFragmentOrganizer iTaskFragmentOrganizer) throws RemoteException;

    void unregisterRemoteAnimations(ITaskFragmentOrganizer iTaskFragmentOrganizer) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements ITaskFragmentOrganizerController {
        @Override // android.window.ITaskFragmentOrganizerController
        public void registerOrganizer(ITaskFragmentOrganizer organizer) throws RemoteException {
        }

        @Override // android.window.ITaskFragmentOrganizerController
        public void unregisterOrganizer(ITaskFragmentOrganizer organizer) throws RemoteException {
        }

        @Override // android.window.ITaskFragmentOrganizerController
        public void registerRemoteAnimations(ITaskFragmentOrganizer organizer, RemoteAnimationDefinition definition) throws RemoteException {
        }

        @Override // android.window.ITaskFragmentOrganizerController
        public void unregisterRemoteAnimations(ITaskFragmentOrganizer organizer) throws RemoteException {
        }

        @Override // android.window.ITaskFragmentOrganizerController
        public boolean isActivityEmbedded(IBinder activityToken) throws RemoteException {
            return false;
        }

        @Override // android.window.ITaskFragmentOrganizerController
        public void onTransactionHandled(IBinder transactionToken, WindowContainerTransaction wct, int transitionType, boolean shouldApplyIndependently) throws RemoteException {
        }

        @Override // android.window.ITaskFragmentOrganizerController
        public void applyTransaction(WindowContainerTransaction wct, int transitionType, boolean shouldApplyIndependently) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements ITaskFragmentOrganizerController {
        static final int TRANSACTION_applyTransaction = 7;
        static final int TRANSACTION_isActivityEmbedded = 5;
        static final int TRANSACTION_onTransactionHandled = 6;
        static final int TRANSACTION_registerOrganizer = 1;
        static final int TRANSACTION_registerRemoteAnimations = 3;
        static final int TRANSACTION_unregisterOrganizer = 2;
        static final int TRANSACTION_unregisterRemoteAnimations = 4;

        public Stub() {
            attachInterface(this, ITaskFragmentOrganizerController.DESCRIPTOR);
        }

        public static ITaskFragmentOrganizerController asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ITaskFragmentOrganizerController.DESCRIPTOR);
            if (iin != null && (iin instanceof ITaskFragmentOrganizerController)) {
                return (ITaskFragmentOrganizerController) iin;
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
                    return "registerRemoteAnimations";
                case 4:
                    return "unregisterRemoteAnimations";
                case 5:
                    return "isActivityEmbedded";
                case 6:
                    return "onTransactionHandled";
                case 7:
                    return "applyTransaction";
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
                data.enforceInterface(ITaskFragmentOrganizerController.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ITaskFragmentOrganizerController.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ITaskFragmentOrganizer _arg0 = ITaskFragmentOrganizer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerOrganizer(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            ITaskFragmentOrganizer _arg02 = ITaskFragmentOrganizer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterOrganizer(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            ITaskFragmentOrganizer _arg03 = ITaskFragmentOrganizer.Stub.asInterface(data.readStrongBinder());
                            RemoteAnimationDefinition _arg1 = (RemoteAnimationDefinition) data.readTypedObject(RemoteAnimationDefinition.CREATOR);
                            data.enforceNoDataAvail();
                            registerRemoteAnimations(_arg03, _arg1);
                            reply.writeNoException();
                            break;
                        case 4:
                            IBinder _arg04 = data.readStrongBinder();
                            ITaskFragmentOrganizer _arg05 = ITaskFragmentOrganizer.Stub.asInterface(_arg04);
                            data.enforceNoDataAvail();
                            unregisterRemoteAnimations(_arg05);
                            reply.writeNoException();
                            break;
                        case 5:
                            IBinder _arg06 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            boolean _result = isActivityEmbedded(_arg06);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 6:
                            IBinder _arg07 = data.readStrongBinder();
                            WindowContainerTransaction _arg12 = (WindowContainerTransaction) data.readTypedObject(WindowContainerTransaction.CREATOR);
                            int _arg2 = data.readInt();
                            boolean _arg3 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onTransactionHandled(_arg07, _arg12, _arg2, _arg3);
                            reply.writeNoException();
                            break;
                        case 7:
                            WindowContainerTransaction _arg08 = (WindowContainerTransaction) data.readTypedObject(WindowContainerTransaction.CREATOR);
                            int _arg13 = data.readInt();
                            boolean _arg22 = data.readBoolean();
                            data.enforceNoDataAvail();
                            applyTransaction(_arg08, _arg13, _arg22);
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
        public static class Proxy implements ITaskFragmentOrganizerController {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ITaskFragmentOrganizerController.DESCRIPTOR;
            }

            @Override // android.window.ITaskFragmentOrganizerController
            public void registerOrganizer(ITaskFragmentOrganizer organizer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITaskFragmentOrganizerController.DESCRIPTOR);
                    _data.writeStrongInterface(organizer);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskFragmentOrganizerController
            public void unregisterOrganizer(ITaskFragmentOrganizer organizer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITaskFragmentOrganizerController.DESCRIPTOR);
                    _data.writeStrongInterface(organizer);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskFragmentOrganizerController
            public void registerRemoteAnimations(ITaskFragmentOrganizer organizer, RemoteAnimationDefinition definition) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITaskFragmentOrganizerController.DESCRIPTOR);
                    _data.writeStrongInterface(organizer);
                    _data.writeTypedObject(definition, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskFragmentOrganizerController
            public void unregisterRemoteAnimations(ITaskFragmentOrganizer organizer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITaskFragmentOrganizerController.DESCRIPTOR);
                    _data.writeStrongInterface(organizer);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskFragmentOrganizerController
            public boolean isActivityEmbedded(IBinder activityToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITaskFragmentOrganizerController.DESCRIPTOR);
                    _data.writeStrongBinder(activityToken);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskFragmentOrganizerController
            public void onTransactionHandled(IBinder transactionToken, WindowContainerTransaction wct, int transitionType, boolean shouldApplyIndependently) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITaskFragmentOrganizerController.DESCRIPTOR);
                    _data.writeStrongBinder(transactionToken);
                    _data.writeTypedObject(wct, 0);
                    _data.writeInt(transitionType);
                    _data.writeBoolean(shouldApplyIndependently);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskFragmentOrganizerController
            public void applyTransaction(WindowContainerTransaction wct, int transitionType, boolean shouldApplyIndependently) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITaskFragmentOrganizerController.DESCRIPTOR);
                    _data.writeTypedObject(wct, 0);
                    _data.writeInt(transitionType);
                    _data.writeBoolean(shouldApplyIndependently);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 6;
        }
    }
}
