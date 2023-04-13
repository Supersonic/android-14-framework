package com.android.internal.textservice;

import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.internal.textservice.ISpellCheckerServiceCallback;
import com.android.internal.textservice.ISpellCheckerSessionListener;
/* loaded from: classes3.dex */
public interface ISpellCheckerService extends IInterface {
    void getISpellCheckerSession(String str, ISpellCheckerSessionListener iSpellCheckerSessionListener, Bundle bundle, int i, ISpellCheckerServiceCallback iSpellCheckerServiceCallback) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ISpellCheckerService {
        @Override // com.android.internal.textservice.ISpellCheckerService
        public void getISpellCheckerSession(String locale, ISpellCheckerSessionListener listener, Bundle bundle, int supportedAttributes, ISpellCheckerServiceCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISpellCheckerService {
        public static final String DESCRIPTOR = "com.android.internal.textservice.ISpellCheckerService";
        static final int TRANSACTION_getISpellCheckerSession = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISpellCheckerService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISpellCheckerService)) {
                return (ISpellCheckerService) iin;
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
                    return "getISpellCheckerSession";
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
                            String _arg0 = data.readString();
                            ISpellCheckerSessionListener _arg1 = ISpellCheckerSessionListener.Stub.asInterface(data.readStrongBinder());
                            Bundle _arg2 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg3 = data.readInt();
                            ISpellCheckerServiceCallback _arg4 = ISpellCheckerServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getISpellCheckerSession(_arg0, _arg1, _arg2, _arg3, _arg4);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ISpellCheckerService {
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

            @Override // com.android.internal.textservice.ISpellCheckerService
            public void getISpellCheckerSession(String locale, ISpellCheckerSessionListener listener, Bundle bundle, int supportedAttributes, ISpellCheckerServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(locale);
                    _data.writeStrongInterface(listener);
                    _data.writeTypedObject(bundle, 0);
                    _data.writeInt(supportedAttributes);
                    _data.writeStrongInterface(callback);
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
