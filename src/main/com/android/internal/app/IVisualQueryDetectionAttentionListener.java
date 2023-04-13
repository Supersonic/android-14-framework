package com.android.internal.app;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface IVisualQueryDetectionAttentionListener extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.app.IVisualQueryDetectionAttentionListener";

    void onAttentionGained() throws RemoteException;

    void onAttentionLost() throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IVisualQueryDetectionAttentionListener {
        @Override // com.android.internal.app.IVisualQueryDetectionAttentionListener
        public void onAttentionGained() throws RemoteException {
        }

        @Override // com.android.internal.app.IVisualQueryDetectionAttentionListener
        public void onAttentionLost() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IVisualQueryDetectionAttentionListener {
        static final int TRANSACTION_onAttentionGained = 1;
        static final int TRANSACTION_onAttentionLost = 2;

        public Stub() {
            attachInterface(this, IVisualQueryDetectionAttentionListener.DESCRIPTOR);
        }

        public static IVisualQueryDetectionAttentionListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IVisualQueryDetectionAttentionListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IVisualQueryDetectionAttentionListener)) {
                return (IVisualQueryDetectionAttentionListener) iin;
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
                    return "onAttentionGained";
                case 2:
                    return "onAttentionLost";
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
                data.enforceInterface(IVisualQueryDetectionAttentionListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IVisualQueryDetectionAttentionListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            onAttentionGained();
                            break;
                        case 2:
                            onAttentionLost();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IVisualQueryDetectionAttentionListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IVisualQueryDetectionAttentionListener.DESCRIPTOR;
            }

            @Override // com.android.internal.app.IVisualQueryDetectionAttentionListener
            public void onAttentionGained() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IVisualQueryDetectionAttentionListener.DESCRIPTOR);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IVisualQueryDetectionAttentionListener
            public void onAttentionLost() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IVisualQueryDetectionAttentionListener.DESCRIPTOR);
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
