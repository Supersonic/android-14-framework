package android.service.controls;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.service.controls.IControlsActionCallback;
import android.service.controls.IControlsSubscriber;
import android.service.controls.actions.ControlActionWrapper;
import java.util.List;
/* loaded from: classes3.dex */
public interface IControlsProvider extends IInterface {
    public static final String DESCRIPTOR = "android.service.controls.IControlsProvider";

    void action(String str, ControlActionWrapper controlActionWrapper, IControlsActionCallback iControlsActionCallback) throws RemoteException;

    void load(IControlsSubscriber iControlsSubscriber) throws RemoteException;

    void loadSuggested(IControlsSubscriber iControlsSubscriber) throws RemoteException;

    void subscribe(List<String> list, IControlsSubscriber iControlsSubscriber) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IControlsProvider {
        @Override // android.service.controls.IControlsProvider
        public void load(IControlsSubscriber subscriber) throws RemoteException {
        }

        @Override // android.service.controls.IControlsProvider
        public void loadSuggested(IControlsSubscriber subscriber) throws RemoteException {
        }

        @Override // android.service.controls.IControlsProvider
        public void subscribe(List<String> controlIds, IControlsSubscriber subscriber) throws RemoteException {
        }

        @Override // android.service.controls.IControlsProvider
        public void action(String controlId, ControlActionWrapper action, IControlsActionCallback cb) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IControlsProvider {
        static final int TRANSACTION_action = 4;
        static final int TRANSACTION_load = 1;
        static final int TRANSACTION_loadSuggested = 2;
        static final int TRANSACTION_subscribe = 3;

        public Stub() {
            attachInterface(this, IControlsProvider.DESCRIPTOR);
        }

        public static IControlsProvider asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IControlsProvider.DESCRIPTOR);
            if (iin != null && (iin instanceof IControlsProvider)) {
                return (IControlsProvider) iin;
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
                    return "load";
                case 2:
                    return "loadSuggested";
                case 3:
                    return "subscribe";
                case 4:
                    return "action";
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
                data.enforceInterface(IControlsProvider.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IControlsProvider.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IControlsSubscriber _arg0 = IControlsSubscriber.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            load(_arg0);
                            break;
                        case 2:
                            IControlsSubscriber _arg02 = IControlsSubscriber.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            loadSuggested(_arg02);
                            break;
                        case 3:
                            List<String> _arg03 = data.createStringArrayList();
                            IControlsSubscriber _arg1 = IControlsSubscriber.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            subscribe(_arg03, _arg1);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            ControlActionWrapper _arg12 = (ControlActionWrapper) data.readTypedObject(ControlActionWrapper.CREATOR);
                            IControlsActionCallback _arg2 = IControlsActionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            action(_arg04, _arg12, _arg2);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IControlsProvider {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IControlsProvider.DESCRIPTOR;
            }

            @Override // android.service.controls.IControlsProvider
            public void load(IControlsSubscriber subscriber) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IControlsProvider.DESCRIPTOR);
                    _data.writeStrongInterface(subscriber);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.controls.IControlsProvider
            public void loadSuggested(IControlsSubscriber subscriber) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IControlsProvider.DESCRIPTOR);
                    _data.writeStrongInterface(subscriber);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.controls.IControlsProvider
            public void subscribe(List<String> controlIds, IControlsSubscriber subscriber) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IControlsProvider.DESCRIPTOR);
                    _data.writeStringList(controlIds);
                    _data.writeStrongInterface(subscriber);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.controls.IControlsProvider
            public void action(String controlId, ControlActionWrapper action, IControlsActionCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IControlsProvider.DESCRIPTOR);
                    _data.writeString(controlId);
                    _data.writeTypedObject(action, 0);
                    _data.writeStrongInterface(cb);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
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
