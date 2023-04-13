package android.window;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.SurfaceControl;
import android.window.IRemoteTransitionFinishedCallback;
/* loaded from: classes4.dex */
public interface IRemoteTransition extends IInterface {
    public static final String DESCRIPTOR = "android.window.IRemoteTransition";

    void mergeAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, IBinder iBinder2, IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback) throws RemoteException;

    void startAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IRemoteTransition {
        @Override // android.window.IRemoteTransition
        public void startAnimation(IBinder token, TransitionInfo info, SurfaceControl.Transaction t, IRemoteTransitionFinishedCallback finishCallback) throws RemoteException {
        }

        @Override // android.window.IRemoteTransition
        public void mergeAnimation(IBinder transition, TransitionInfo info, SurfaceControl.Transaction t, IBinder mergeTarget, IRemoteTransitionFinishedCallback finishCallback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IRemoteTransition {
        static final int TRANSACTION_mergeAnimation = 2;
        static final int TRANSACTION_startAnimation = 1;

        public Stub() {
            attachInterface(this, IRemoteTransition.DESCRIPTOR);
        }

        public static IRemoteTransition asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IRemoteTransition.DESCRIPTOR);
            if (iin != null && (iin instanceof IRemoteTransition)) {
                return (IRemoteTransition) iin;
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
                    return "startAnimation";
                case 2:
                    return "mergeAnimation";
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
                data.enforceInterface(IRemoteTransition.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IRemoteTransition.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IBinder _arg0 = data.readStrongBinder();
                            TransitionInfo _arg1 = (TransitionInfo) data.readTypedObject(TransitionInfo.CREATOR);
                            SurfaceControl.Transaction _arg2 = (SurfaceControl.Transaction) data.readTypedObject(SurfaceControl.Transaction.CREATOR);
                            IRemoteTransitionFinishedCallback _arg3 = IRemoteTransitionFinishedCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            startAnimation(_arg0, _arg1, _arg2, _arg3);
                            break;
                        case 2:
                            IBinder _arg02 = data.readStrongBinder();
                            TransitionInfo _arg12 = (TransitionInfo) data.readTypedObject(TransitionInfo.CREATOR);
                            SurfaceControl.Transaction _arg22 = (SurfaceControl.Transaction) data.readTypedObject(SurfaceControl.Transaction.CREATOR);
                            IBinder _arg32 = data.readStrongBinder();
                            IRemoteTransitionFinishedCallback _arg4 = IRemoteTransitionFinishedCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            mergeAnimation(_arg02, _arg12, _arg22, _arg32, _arg4);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IRemoteTransition {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IRemoteTransition.DESCRIPTOR;
            }

            @Override // android.window.IRemoteTransition
            public void startAnimation(IBinder token, TransitionInfo info, SurfaceControl.Transaction t, IRemoteTransitionFinishedCallback finishCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteTransition.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(t, 0);
                    _data.writeStrongInterface(finishCallback);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.window.IRemoteTransition
            public void mergeAnimation(IBinder transition, TransitionInfo info, SurfaceControl.Transaction t, IBinder mergeTarget, IRemoteTransitionFinishedCallback finishCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteTransition.DESCRIPTOR);
                    _data.writeStrongBinder(transition);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(t, 0);
                    _data.writeStrongBinder(mergeTarget);
                    _data.writeStrongInterface(finishCallback);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 1;
        }
    }
}
