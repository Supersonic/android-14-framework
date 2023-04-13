package android.window;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.RemoteAnimationTarget;
import android.window.IBackAnimationFinishedCallback;
/* loaded from: classes4.dex */
public interface IBackAnimationRunner extends IInterface {
    public static final String DESCRIPTOR = "android.window.IBackAnimationRunner";

    void onAnimationCancelled() throws RemoteException;

    void onAnimationStart(RemoteAnimationTarget[] remoteAnimationTargetArr, RemoteAnimationTarget[] remoteAnimationTargetArr2, RemoteAnimationTarget[] remoteAnimationTargetArr3, IBackAnimationFinishedCallback iBackAnimationFinishedCallback) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IBackAnimationRunner {
        @Override // android.window.IBackAnimationRunner
        public void onAnimationCancelled() throws RemoteException {
        }

        @Override // android.window.IBackAnimationRunner
        public void onAnimationStart(RemoteAnimationTarget[] apps, RemoteAnimationTarget[] wallpapers, RemoteAnimationTarget[] nonApps, IBackAnimationFinishedCallback finishedCallback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IBackAnimationRunner {
        static final int TRANSACTION_onAnimationCancelled = 2;
        static final int TRANSACTION_onAnimationStart = 3;

        public Stub() {
            attachInterface(this, IBackAnimationRunner.DESCRIPTOR);
        }

        public static IBackAnimationRunner asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IBackAnimationRunner.DESCRIPTOR);
            if (iin != null && (iin instanceof IBackAnimationRunner)) {
                return (IBackAnimationRunner) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 2:
                    return "onAnimationCancelled";
                case 3:
                    return "onAnimationStart";
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
                data.enforceInterface(IBackAnimationRunner.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IBackAnimationRunner.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 2:
                            onAnimationCancelled();
                            break;
                        case 3:
                            RemoteAnimationTarget[] _arg0 = (RemoteAnimationTarget[]) data.createTypedArray(RemoteAnimationTarget.CREATOR);
                            RemoteAnimationTarget[] _arg1 = (RemoteAnimationTarget[]) data.createTypedArray(RemoteAnimationTarget.CREATOR);
                            RemoteAnimationTarget[] _arg2 = (RemoteAnimationTarget[]) data.createTypedArray(RemoteAnimationTarget.CREATOR);
                            IBackAnimationFinishedCallback _arg3 = IBackAnimationFinishedCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onAnimationStart(_arg0, _arg1, _arg2, _arg3);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IBackAnimationRunner {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IBackAnimationRunner.DESCRIPTOR;
            }

            @Override // android.window.IBackAnimationRunner
            public void onAnimationCancelled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IBackAnimationRunner.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.window.IBackAnimationRunner
            public void onAnimationStart(RemoteAnimationTarget[] apps, RemoteAnimationTarget[] wallpapers, RemoteAnimationTarget[] nonApps, IBackAnimationFinishedCallback finishedCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IBackAnimationRunner.DESCRIPTOR);
                    _data.writeTypedArray(apps, 0);
                    _data.writeTypedArray(wallpapers, 0);
                    _data.writeTypedArray(nonApps, 0);
                    _data.writeStrongInterface(finishedCallback);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 2;
        }
    }
}
