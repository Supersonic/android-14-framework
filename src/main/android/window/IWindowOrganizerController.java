package android.window;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.RemoteAnimationAdapter;
import android.window.IDisplayAreaOrganizerController;
import android.window.ITaskFragmentOrganizerController;
import android.window.ITaskOrganizerController;
import android.window.ITransitionMetricsReporter;
import android.window.ITransitionPlayer;
import android.window.IWindowContainerTransactionCallback;
/* loaded from: classes4.dex */
public interface IWindowOrganizerController extends IInterface {
    public static final String DESCRIPTOR = "android.window.IWindowOrganizerController";

    int applySyncTransaction(WindowContainerTransaction windowContainerTransaction, IWindowContainerTransactionCallback iWindowContainerTransactionCallback) throws RemoteException;

    void applyTransaction(WindowContainerTransaction windowContainerTransaction) throws RemoteException;

    int finishTransition(IBinder iBinder, WindowContainerTransaction windowContainerTransaction, IWindowContainerTransactionCallback iWindowContainerTransactionCallback) throws RemoteException;

    IBinder getApplyToken() throws RemoteException;

    IDisplayAreaOrganizerController getDisplayAreaOrganizerController() throws RemoteException;

    ITaskFragmentOrganizerController getTaskFragmentOrganizerController() throws RemoteException;

    ITaskOrganizerController getTaskOrganizerController() throws RemoteException;

    ITransitionMetricsReporter getTransitionMetricsReporter() throws RemoteException;

    void registerTransitionPlayer(ITransitionPlayer iTransitionPlayer) throws RemoteException;

    int startLegacyTransition(int i, RemoteAnimationAdapter remoteAnimationAdapter, IWindowContainerTransactionCallback iWindowContainerTransactionCallback, WindowContainerTransaction windowContainerTransaction) throws RemoteException;

    IBinder startNewTransition(int i, WindowContainerTransaction windowContainerTransaction) throws RemoteException;

    void startTransition(IBinder iBinder, WindowContainerTransaction windowContainerTransaction) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IWindowOrganizerController {
        @Override // android.window.IWindowOrganizerController
        public void applyTransaction(WindowContainerTransaction t) throws RemoteException {
        }

        @Override // android.window.IWindowOrganizerController
        public int applySyncTransaction(WindowContainerTransaction t, IWindowContainerTransactionCallback callback) throws RemoteException {
            return 0;
        }

        @Override // android.window.IWindowOrganizerController
        public IBinder startNewTransition(int type, WindowContainerTransaction t) throws RemoteException {
            return null;
        }

        @Override // android.window.IWindowOrganizerController
        public void startTransition(IBinder transitionToken, WindowContainerTransaction t) throws RemoteException {
        }

        @Override // android.window.IWindowOrganizerController
        public int startLegacyTransition(int type, RemoteAnimationAdapter adapter, IWindowContainerTransactionCallback syncCallback, WindowContainerTransaction t) throws RemoteException {
            return 0;
        }

        @Override // android.window.IWindowOrganizerController
        public int finishTransition(IBinder transitionToken, WindowContainerTransaction t, IWindowContainerTransactionCallback callback) throws RemoteException {
            return 0;
        }

        @Override // android.window.IWindowOrganizerController
        public ITaskOrganizerController getTaskOrganizerController() throws RemoteException {
            return null;
        }

        @Override // android.window.IWindowOrganizerController
        public IDisplayAreaOrganizerController getDisplayAreaOrganizerController() throws RemoteException {
            return null;
        }

        @Override // android.window.IWindowOrganizerController
        public ITaskFragmentOrganizerController getTaskFragmentOrganizerController() throws RemoteException {
            return null;
        }

        @Override // android.window.IWindowOrganizerController
        public void registerTransitionPlayer(ITransitionPlayer player) throws RemoteException {
        }

        @Override // android.window.IWindowOrganizerController
        public ITransitionMetricsReporter getTransitionMetricsReporter() throws RemoteException {
            return null;
        }

        @Override // android.window.IWindowOrganizerController
        public IBinder getApplyToken() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IWindowOrganizerController {
        static final int TRANSACTION_applySyncTransaction = 2;
        static final int TRANSACTION_applyTransaction = 1;
        static final int TRANSACTION_finishTransition = 6;
        static final int TRANSACTION_getApplyToken = 12;
        static final int TRANSACTION_getDisplayAreaOrganizerController = 8;
        static final int TRANSACTION_getTaskFragmentOrganizerController = 9;
        static final int TRANSACTION_getTaskOrganizerController = 7;
        static final int TRANSACTION_getTransitionMetricsReporter = 11;
        static final int TRANSACTION_registerTransitionPlayer = 10;
        static final int TRANSACTION_startLegacyTransition = 5;
        static final int TRANSACTION_startNewTransition = 3;
        static final int TRANSACTION_startTransition = 4;

        public Stub() {
            attachInterface(this, IWindowOrganizerController.DESCRIPTOR);
        }

        public static IWindowOrganizerController asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IWindowOrganizerController.DESCRIPTOR);
            if (iin != null && (iin instanceof IWindowOrganizerController)) {
                return (IWindowOrganizerController) iin;
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
                    return "applyTransaction";
                case 2:
                    return "applySyncTransaction";
                case 3:
                    return "startNewTransition";
                case 4:
                    return "startTransition";
                case 5:
                    return "startLegacyTransition";
                case 6:
                    return "finishTransition";
                case 7:
                    return "getTaskOrganizerController";
                case 8:
                    return "getDisplayAreaOrganizerController";
                case 9:
                    return "getTaskFragmentOrganizerController";
                case 10:
                    return "registerTransitionPlayer";
                case 11:
                    return "getTransitionMetricsReporter";
                case 12:
                    return "getApplyToken";
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
                data.enforceInterface(IWindowOrganizerController.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IWindowOrganizerController.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            WindowContainerTransaction _arg0 = (WindowContainerTransaction) data.readTypedObject(WindowContainerTransaction.CREATOR);
                            data.enforceNoDataAvail();
                            applyTransaction(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            WindowContainerTransaction _arg02 = (WindowContainerTransaction) data.readTypedObject(WindowContainerTransaction.CREATOR);
                            IWindowContainerTransactionCallback _arg1 = IWindowContainerTransactionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result = applySyncTransaction(_arg02, _arg1);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            WindowContainerTransaction _arg12 = (WindowContainerTransaction) data.readTypedObject(WindowContainerTransaction.CREATOR);
                            data.enforceNoDataAvail();
                            IBinder _result2 = startNewTransition(_arg03, _arg12);
                            reply.writeNoException();
                            reply.writeStrongBinder(_result2);
                            break;
                        case 4:
                            IBinder _arg04 = data.readStrongBinder();
                            WindowContainerTransaction _arg13 = (WindowContainerTransaction) data.readTypedObject(WindowContainerTransaction.CREATOR);
                            data.enforceNoDataAvail();
                            startTransition(_arg04, _arg13);
                            reply.writeNoException();
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            RemoteAnimationAdapter _arg14 = (RemoteAnimationAdapter) data.readTypedObject(RemoteAnimationAdapter.CREATOR);
                            IWindowContainerTransactionCallback _arg2 = IWindowContainerTransactionCallback.Stub.asInterface(data.readStrongBinder());
                            WindowContainerTransaction _arg3 = (WindowContainerTransaction) data.readTypedObject(WindowContainerTransaction.CREATOR);
                            data.enforceNoDataAvail();
                            int _result3 = startLegacyTransition(_arg05, _arg14, _arg2, _arg3);
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            break;
                        case 6:
                            IBinder _arg06 = data.readStrongBinder();
                            WindowContainerTransaction _arg15 = (WindowContainerTransaction) data.readTypedObject(WindowContainerTransaction.CREATOR);
                            IWindowContainerTransactionCallback _arg22 = IWindowContainerTransactionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result4 = finishTransition(_arg06, _arg15, _arg22);
                            reply.writeNoException();
                            reply.writeInt(_result4);
                            break;
                        case 7:
                            ITaskOrganizerController _result5 = getTaskOrganizerController();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result5);
                            break;
                        case 8:
                            IDisplayAreaOrganizerController _result6 = getDisplayAreaOrganizerController();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result6);
                            break;
                        case 9:
                            ITaskFragmentOrganizerController _result7 = getTaskFragmentOrganizerController();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result7);
                            break;
                        case 10:
                            ITransitionPlayer _arg07 = ITransitionPlayer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerTransitionPlayer(_arg07);
                            reply.writeNoException();
                            break;
                        case 11:
                            ITransitionMetricsReporter _result8 = getTransitionMetricsReporter();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result8);
                            break;
                        case 12:
                            IBinder _result9 = getApplyToken();
                            reply.writeNoException();
                            reply.writeStrongBinder(_result9);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IWindowOrganizerController {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IWindowOrganizerController.DESCRIPTOR;
            }

            @Override // android.window.IWindowOrganizerController
            public void applyTransaction(WindowContainerTransaction t) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWindowOrganizerController.DESCRIPTOR);
                    _data.writeTypedObject(t, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.IWindowOrganizerController
            public int applySyncTransaction(WindowContainerTransaction t, IWindowContainerTransactionCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWindowOrganizerController.DESCRIPTOR);
                    _data.writeTypedObject(t, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.IWindowOrganizerController
            public IBinder startNewTransition(int type, WindowContainerTransaction t) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWindowOrganizerController.DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeTypedObject(t, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    IBinder _result = _reply.readStrongBinder();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.IWindowOrganizerController
            public void startTransition(IBinder transitionToken, WindowContainerTransaction t) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWindowOrganizerController.DESCRIPTOR);
                    _data.writeStrongBinder(transitionToken);
                    _data.writeTypedObject(t, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.IWindowOrganizerController
            public int startLegacyTransition(int type, RemoteAnimationAdapter adapter, IWindowContainerTransactionCallback syncCallback, WindowContainerTransaction t) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWindowOrganizerController.DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeTypedObject(adapter, 0);
                    _data.writeStrongInterface(syncCallback);
                    _data.writeTypedObject(t, 0);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.IWindowOrganizerController
            public int finishTransition(IBinder transitionToken, WindowContainerTransaction t, IWindowContainerTransactionCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWindowOrganizerController.DESCRIPTOR);
                    _data.writeStrongBinder(transitionToken);
                    _data.writeTypedObject(t, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.IWindowOrganizerController
            public ITaskOrganizerController getTaskOrganizerController() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWindowOrganizerController.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    ITaskOrganizerController _result = ITaskOrganizerController.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.IWindowOrganizerController
            public IDisplayAreaOrganizerController getDisplayAreaOrganizerController() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWindowOrganizerController.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    IDisplayAreaOrganizerController _result = IDisplayAreaOrganizerController.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.IWindowOrganizerController
            public ITaskFragmentOrganizerController getTaskFragmentOrganizerController() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWindowOrganizerController.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    ITaskFragmentOrganizerController _result = ITaskFragmentOrganizerController.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.IWindowOrganizerController
            public void registerTransitionPlayer(ITransitionPlayer player) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWindowOrganizerController.DESCRIPTOR);
                    _data.writeStrongInterface(player);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.IWindowOrganizerController
            public ITransitionMetricsReporter getTransitionMetricsReporter() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWindowOrganizerController.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    ITransitionMetricsReporter _result = ITransitionMetricsReporter.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.IWindowOrganizerController
            public IBinder getApplyToken() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWindowOrganizerController.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    IBinder _result = _reply.readStrongBinder();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 11;
        }
    }
}
