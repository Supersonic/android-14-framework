package android.speech;

import android.content.ComponentName;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.speech.IRecognitionServiceManagerCallback;
/* loaded from: classes3.dex */
public interface IRecognitionServiceManager extends IInterface {
    public static final String DESCRIPTOR = "android.speech.IRecognitionServiceManager";

    void createSession(ComponentName componentName, IBinder iBinder, boolean z, IRecognitionServiceManagerCallback iRecognitionServiceManagerCallback) throws RemoteException;

    void setTemporaryComponent(ComponentName componentName) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IRecognitionServiceManager {
        @Override // android.speech.IRecognitionServiceManager
        public void createSession(ComponentName componentName, IBinder clientToken, boolean onDevice, IRecognitionServiceManagerCallback callback) throws RemoteException {
        }

        @Override // android.speech.IRecognitionServiceManager
        public void setTemporaryComponent(ComponentName componentName) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IRecognitionServiceManager {
        static final int TRANSACTION_createSession = 1;
        static final int TRANSACTION_setTemporaryComponent = 2;

        public Stub() {
            attachInterface(this, IRecognitionServiceManager.DESCRIPTOR);
        }

        public static IRecognitionServiceManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IRecognitionServiceManager.DESCRIPTOR);
            if (iin != null && (iin instanceof IRecognitionServiceManager)) {
                return (IRecognitionServiceManager) iin;
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
                    return "createSession";
                case 2:
                    return "setTemporaryComponent";
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
                data.enforceInterface(IRecognitionServiceManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IRecognitionServiceManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            IBinder _arg1 = data.readStrongBinder();
                            boolean _arg2 = data.readBoolean();
                            IRecognitionServiceManagerCallback _arg3 = IRecognitionServiceManagerCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            createSession(_arg0, _arg1, _arg2, _arg3);
                            break;
                        case 2:
                            ComponentName _arg02 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            setTemporaryComponent(_arg02);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IRecognitionServiceManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IRecognitionServiceManager.DESCRIPTOR;
            }

            @Override // android.speech.IRecognitionServiceManager
            public void createSession(ComponentName componentName, IBinder clientToken, boolean onDevice, IRecognitionServiceManagerCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRecognitionServiceManager.DESCRIPTOR);
                    _data.writeTypedObject(componentName, 0);
                    _data.writeStrongBinder(clientToken);
                    _data.writeBoolean(onDevice);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.IRecognitionServiceManager
            public void setTemporaryComponent(ComponentName componentName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRecognitionServiceManager.DESCRIPTOR);
                    _data.writeTypedObject(componentName, 0);
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
