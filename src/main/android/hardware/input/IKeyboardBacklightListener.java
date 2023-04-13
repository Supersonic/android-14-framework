package android.hardware.input;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IKeyboardBacklightListener extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.input.IKeyboardBacklightListener";

    void onBrightnessChanged(int i, IKeyboardBacklightState iKeyboardBacklightState, boolean z) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IKeyboardBacklightListener {
        @Override // android.hardware.input.IKeyboardBacklightListener
        public void onBrightnessChanged(int deviceId, IKeyboardBacklightState state, boolean isTriggeredByKeyPress) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IKeyboardBacklightListener {
        static final int TRANSACTION_onBrightnessChanged = 1;

        public Stub() {
            attachInterface(this, IKeyboardBacklightListener.DESCRIPTOR);
        }

        public static IKeyboardBacklightListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IKeyboardBacklightListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IKeyboardBacklightListener)) {
                return (IKeyboardBacklightListener) iin;
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
                    return "onBrightnessChanged";
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
                data.enforceInterface(IKeyboardBacklightListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IKeyboardBacklightListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            IKeyboardBacklightState _arg1 = (IKeyboardBacklightState) data.readTypedObject(IKeyboardBacklightState.CREATOR);
                            boolean _arg2 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onBrightnessChanged(_arg0, _arg1, _arg2);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IKeyboardBacklightListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IKeyboardBacklightListener.DESCRIPTOR;
            }

            @Override // android.hardware.input.IKeyboardBacklightListener
            public void onBrightnessChanged(int deviceId, IKeyboardBacklightState state, boolean isTriggeredByKeyPress) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IKeyboardBacklightListener.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeTypedObject(state, 0);
                    _data.writeBoolean(isTriggeredByKeyPress);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 0;
        }
    }
}
