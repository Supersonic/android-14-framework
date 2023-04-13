package com.android.internal.telecom;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telecom.StreamingCall;
import com.android.internal.telecom.IStreamingCallAdapter;
/* loaded from: classes2.dex */
public interface ICallStreamingService extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.telecom.ICallStreamingService";

    void onCallStreamingStarted(StreamingCall streamingCall) throws RemoteException;

    void onCallStreamingStateChanged(int i) throws RemoteException;

    void onCallStreamingStopped() throws RemoteException;

    void setStreamingCallAdapter(IStreamingCallAdapter iStreamingCallAdapter) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ICallStreamingService {
        @Override // com.android.internal.telecom.ICallStreamingService
        public void setStreamingCallAdapter(IStreamingCallAdapter streamingCallAdapter) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallStreamingService
        public void onCallStreamingStarted(StreamingCall call) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallStreamingService
        public void onCallStreamingStopped() throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallStreamingService
        public void onCallStreamingStateChanged(int state) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ICallStreamingService {
        static final int TRANSACTION_onCallStreamingStarted = 2;
        static final int TRANSACTION_onCallStreamingStateChanged = 4;
        static final int TRANSACTION_onCallStreamingStopped = 3;
        static final int TRANSACTION_setStreamingCallAdapter = 1;

        public Stub() {
            attachInterface(this, ICallStreamingService.DESCRIPTOR);
        }

        public static ICallStreamingService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ICallStreamingService.DESCRIPTOR);
            if (iin != null && (iin instanceof ICallStreamingService)) {
                return (ICallStreamingService) iin;
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
                    return "setStreamingCallAdapter";
                case 2:
                    return "onCallStreamingStarted";
                case 3:
                    return "onCallStreamingStopped";
                case 4:
                    return "onCallStreamingStateChanged";
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
                data.enforceInterface(ICallStreamingService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ICallStreamingService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IStreamingCallAdapter _arg0 = IStreamingCallAdapter.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setStreamingCallAdapter(_arg0);
                            break;
                        case 2:
                            StreamingCall _arg02 = (StreamingCall) data.readTypedObject(StreamingCall.CREATOR);
                            data.enforceNoDataAvail();
                            onCallStreamingStarted(_arg02);
                            break;
                        case 3:
                            onCallStreamingStopped();
                            break;
                        case 4:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            onCallStreamingStateChanged(_arg03);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements ICallStreamingService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ICallStreamingService.DESCRIPTOR;
            }

            @Override // com.android.internal.telecom.ICallStreamingService
            public void setStreamingCallAdapter(IStreamingCallAdapter streamingCallAdapter) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallStreamingService.DESCRIPTOR);
                    _data.writeStrongInterface(streamingCallAdapter);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallStreamingService
            public void onCallStreamingStarted(StreamingCall call) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallStreamingService.DESCRIPTOR);
                    _data.writeTypedObject(call, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallStreamingService
            public void onCallStreamingStopped() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallStreamingService.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallStreamingService
            public void onCallStreamingStateChanged(int state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallStreamingService.DESCRIPTOR);
                    _data.writeInt(state);
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
