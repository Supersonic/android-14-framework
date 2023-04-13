package com.android.internal.app;

import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface IVoiceInteractionSessionListener extends IInterface {
    void onSetUiHints(Bundle bundle) throws RemoteException;

    void onVoiceSessionHidden() throws RemoteException;

    void onVoiceSessionShown() throws RemoteException;

    void onVoiceSessionWindowVisibilityChanged(boolean z) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IVoiceInteractionSessionListener {
        @Override // com.android.internal.app.IVoiceInteractionSessionListener
        public void onVoiceSessionShown() throws RemoteException {
        }

        @Override // com.android.internal.app.IVoiceInteractionSessionListener
        public void onVoiceSessionHidden() throws RemoteException {
        }

        @Override // com.android.internal.app.IVoiceInteractionSessionListener
        public void onVoiceSessionWindowVisibilityChanged(boolean visible) throws RemoteException {
        }

        @Override // com.android.internal.app.IVoiceInteractionSessionListener
        public void onSetUiHints(Bundle args) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IVoiceInteractionSessionListener {
        public static final String DESCRIPTOR = "com.android.internal.app.IVoiceInteractionSessionListener";
        static final int TRANSACTION_onSetUiHints = 4;
        static final int TRANSACTION_onVoiceSessionHidden = 2;
        static final int TRANSACTION_onVoiceSessionShown = 1;
        static final int TRANSACTION_onVoiceSessionWindowVisibilityChanged = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IVoiceInteractionSessionListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IVoiceInteractionSessionListener)) {
                return (IVoiceInteractionSessionListener) iin;
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
                    return "onVoiceSessionShown";
                case 2:
                    return "onVoiceSessionHidden";
                case 3:
                    return "onVoiceSessionWindowVisibilityChanged";
                case 4:
                    return "onSetUiHints";
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
                            onVoiceSessionShown();
                            break;
                        case 2:
                            onVoiceSessionHidden();
                            break;
                        case 3:
                            boolean _arg0 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onVoiceSessionWindowVisibilityChanged(_arg0);
                            break;
                        case 4:
                            Bundle _arg02 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            onSetUiHints(_arg02);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IVoiceInteractionSessionListener {
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

            @Override // com.android.internal.app.IVoiceInteractionSessionListener
            public void onVoiceSessionShown() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IVoiceInteractionSessionListener
            public void onVoiceSessionHidden() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IVoiceInteractionSessionListener
            public void onVoiceSessionWindowVisibilityChanged(boolean visible) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(visible);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IVoiceInteractionSessionListener
            public void onSetUiHints(Bundle args) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(args, 0);
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
