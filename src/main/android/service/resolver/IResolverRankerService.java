package android.service.resolver;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.service.resolver.IResolverRankerResult;
import java.util.List;
/* loaded from: classes3.dex */
public interface IResolverRankerService extends IInterface {
    void predict(List<ResolverTarget> list, IResolverRankerResult iResolverRankerResult) throws RemoteException;

    void train(List<ResolverTarget> list, int i) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IResolverRankerService {
        @Override // android.service.resolver.IResolverRankerService
        public void predict(List<ResolverTarget> targets, IResolverRankerResult result) throws RemoteException {
        }

        @Override // android.service.resolver.IResolverRankerService
        public void train(List<ResolverTarget> targets, int selectedPosition) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IResolverRankerService {
        public static final String DESCRIPTOR = "android.service.resolver.IResolverRankerService";
        static final int TRANSACTION_predict = 1;
        static final int TRANSACTION_train = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IResolverRankerService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IResolverRankerService)) {
                return (IResolverRankerService) iin;
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
                    return "predict";
                case 2:
                    return "train";
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
                            List<ResolverTarget> _arg0 = data.createTypedArrayList(ResolverTarget.CREATOR);
                            IResolverRankerResult _arg1 = IResolverRankerResult.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            predict(_arg0, _arg1);
                            break;
                        case 2:
                            List<ResolverTarget> _arg02 = data.createTypedArrayList(ResolverTarget.CREATOR);
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            train(_arg02, _arg12);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IResolverRankerService {
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

            @Override // android.service.resolver.IResolverRankerService
            public void predict(List<ResolverTarget> targets, IResolverRankerResult result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(targets, 0);
                    _data.writeStrongInterface(result);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.resolver.IResolverRankerService
            public void train(List<ResolverTarget> targets, int selectedPosition) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(targets, 0);
                    _data.writeInt(selectedPosition);
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
