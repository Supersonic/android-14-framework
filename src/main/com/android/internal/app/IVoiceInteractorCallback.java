package com.android.internal.app;

import android.app.VoiceInteractor;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.internal.app.IVoiceInteractorRequest;
/* loaded from: classes4.dex */
public interface IVoiceInteractorCallback extends IInterface {
    void deliverAbortVoiceResult(IVoiceInteractorRequest iVoiceInteractorRequest, Bundle bundle) throws RemoteException;

    void deliverCancel(IVoiceInteractorRequest iVoiceInteractorRequest) throws RemoteException;

    void deliverCommandResult(IVoiceInteractorRequest iVoiceInteractorRequest, boolean z, Bundle bundle) throws RemoteException;

    void deliverCompleteVoiceResult(IVoiceInteractorRequest iVoiceInteractorRequest, Bundle bundle) throws RemoteException;

    void deliverConfirmationResult(IVoiceInteractorRequest iVoiceInteractorRequest, boolean z, Bundle bundle) throws RemoteException;

    void deliverPickOptionResult(IVoiceInteractorRequest iVoiceInteractorRequest, boolean z, VoiceInteractor.PickOptionRequest.Option[] optionArr, Bundle bundle) throws RemoteException;

    void destroy() throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IVoiceInteractorCallback {
        @Override // com.android.internal.app.IVoiceInteractorCallback
        public void deliverConfirmationResult(IVoiceInteractorRequest request, boolean confirmed, Bundle result) throws RemoteException {
        }

        @Override // com.android.internal.app.IVoiceInteractorCallback
        public void deliverPickOptionResult(IVoiceInteractorRequest request, boolean finished, VoiceInteractor.PickOptionRequest.Option[] selections, Bundle result) throws RemoteException {
        }

        @Override // com.android.internal.app.IVoiceInteractorCallback
        public void deliverCompleteVoiceResult(IVoiceInteractorRequest request, Bundle result) throws RemoteException {
        }

        @Override // com.android.internal.app.IVoiceInteractorCallback
        public void deliverAbortVoiceResult(IVoiceInteractorRequest request, Bundle result) throws RemoteException {
        }

        @Override // com.android.internal.app.IVoiceInteractorCallback
        public void deliverCommandResult(IVoiceInteractorRequest request, boolean finished, Bundle result) throws RemoteException {
        }

        @Override // com.android.internal.app.IVoiceInteractorCallback
        public void deliverCancel(IVoiceInteractorRequest request) throws RemoteException {
        }

        @Override // com.android.internal.app.IVoiceInteractorCallback
        public void destroy() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IVoiceInteractorCallback {
        public static final String DESCRIPTOR = "com.android.internal.app.IVoiceInteractorCallback";
        static final int TRANSACTION_deliverAbortVoiceResult = 4;
        static final int TRANSACTION_deliverCancel = 6;
        static final int TRANSACTION_deliverCommandResult = 5;
        static final int TRANSACTION_deliverCompleteVoiceResult = 3;
        static final int TRANSACTION_deliverConfirmationResult = 1;
        static final int TRANSACTION_deliverPickOptionResult = 2;
        static final int TRANSACTION_destroy = 7;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IVoiceInteractorCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IVoiceInteractorCallback)) {
                return (IVoiceInteractorCallback) iin;
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
                    return "deliverConfirmationResult";
                case 2:
                    return "deliverPickOptionResult";
                case 3:
                    return "deliverCompleteVoiceResult";
                case 4:
                    return "deliverAbortVoiceResult";
                case 5:
                    return "deliverCommandResult";
                case 6:
                    return "deliverCancel";
                case 7:
                    return "destroy";
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
                            IVoiceInteractorRequest _arg0 = IVoiceInteractorRequest.Stub.asInterface(data.readStrongBinder());
                            boolean _arg1 = data.readBoolean();
                            Bundle _arg2 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            deliverConfirmationResult(_arg0, _arg1, _arg2);
                            break;
                        case 2:
                            IVoiceInteractorRequest _arg02 = IVoiceInteractorRequest.Stub.asInterface(data.readStrongBinder());
                            boolean _arg12 = data.readBoolean();
                            VoiceInteractor.PickOptionRequest.Option[] _arg22 = (VoiceInteractor.PickOptionRequest.Option[]) data.createTypedArray(VoiceInteractor.PickOptionRequest.Option.CREATOR);
                            Bundle _arg3 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            deliverPickOptionResult(_arg02, _arg12, _arg22, _arg3);
                            break;
                        case 3:
                            IVoiceInteractorRequest _arg03 = IVoiceInteractorRequest.Stub.asInterface(data.readStrongBinder());
                            Bundle _arg13 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            deliverCompleteVoiceResult(_arg03, _arg13);
                            break;
                        case 4:
                            IVoiceInteractorRequest _arg04 = IVoiceInteractorRequest.Stub.asInterface(data.readStrongBinder());
                            Bundle _arg14 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            deliverAbortVoiceResult(_arg04, _arg14);
                            break;
                        case 5:
                            IVoiceInteractorRequest _arg05 = IVoiceInteractorRequest.Stub.asInterface(data.readStrongBinder());
                            boolean _arg15 = data.readBoolean();
                            Bundle _arg23 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            deliverCommandResult(_arg05, _arg15, _arg23);
                            break;
                        case 6:
                            IVoiceInteractorRequest _arg06 = IVoiceInteractorRequest.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            deliverCancel(_arg06);
                            break;
                        case 7:
                            destroy();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IVoiceInteractorCallback {
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

            @Override // com.android.internal.app.IVoiceInteractorCallback
            public void deliverConfirmationResult(IVoiceInteractorRequest request, boolean confirmed, Bundle result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(request);
                    _data.writeBoolean(confirmed);
                    _data.writeTypedObject(result, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IVoiceInteractorCallback
            public void deliverPickOptionResult(IVoiceInteractorRequest request, boolean finished, VoiceInteractor.PickOptionRequest.Option[] selections, Bundle result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(request);
                    _data.writeBoolean(finished);
                    _data.writeTypedArray(selections, 0);
                    _data.writeTypedObject(result, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IVoiceInteractorCallback
            public void deliverCompleteVoiceResult(IVoiceInteractorRequest request, Bundle result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(request);
                    _data.writeTypedObject(result, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IVoiceInteractorCallback
            public void deliverAbortVoiceResult(IVoiceInteractorRequest request, Bundle result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(request);
                    _data.writeTypedObject(result, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IVoiceInteractorCallback
            public void deliverCommandResult(IVoiceInteractorRequest request, boolean finished, Bundle result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(request);
                    _data.writeBoolean(finished);
                    _data.writeTypedObject(result, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IVoiceInteractorCallback
            public void deliverCancel(IVoiceInteractorRequest request) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(request);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IVoiceInteractorCallback
            public void destroy() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 6;
        }
    }
}
