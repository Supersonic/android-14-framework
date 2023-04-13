package android.service.autofill;

import android.content.IntentSender;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.service.autofill.IInlineSuggestionUi;
import android.view.SurfaceControlViewHost;
/* loaded from: classes3.dex */
public interface IInlineSuggestionUiCallback extends IInterface {
    public static final String DESCRIPTOR = "android.service.autofill.IInlineSuggestionUiCallback";

    void onClick() throws RemoteException;

    void onContent(IInlineSuggestionUi iInlineSuggestionUi, SurfaceControlViewHost.SurfacePackage surfacePackage, int i, int i2) throws RemoteException;

    void onError() throws RemoteException;

    void onLongClick() throws RemoteException;

    void onStartIntentSender(IntentSender intentSender) throws RemoteException;

    void onTransferTouchFocusToImeWindow(IBinder iBinder, int i) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IInlineSuggestionUiCallback {
        @Override // android.service.autofill.IInlineSuggestionUiCallback
        public void onClick() throws RemoteException {
        }

        @Override // android.service.autofill.IInlineSuggestionUiCallback
        public void onLongClick() throws RemoteException {
        }

        @Override // android.service.autofill.IInlineSuggestionUiCallback
        public void onContent(IInlineSuggestionUi content, SurfaceControlViewHost.SurfacePackage surface, int width, int height) throws RemoteException {
        }

        @Override // android.service.autofill.IInlineSuggestionUiCallback
        public void onError() throws RemoteException {
        }

        @Override // android.service.autofill.IInlineSuggestionUiCallback
        public void onTransferTouchFocusToImeWindow(IBinder sourceInputToken, int displayId) throws RemoteException {
        }

        @Override // android.service.autofill.IInlineSuggestionUiCallback
        public void onStartIntentSender(IntentSender intentSender) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IInlineSuggestionUiCallback {
        static final int TRANSACTION_onClick = 1;
        static final int TRANSACTION_onContent = 3;
        static final int TRANSACTION_onError = 4;
        static final int TRANSACTION_onLongClick = 2;
        static final int TRANSACTION_onStartIntentSender = 6;
        static final int TRANSACTION_onTransferTouchFocusToImeWindow = 5;

        public Stub() {
            attachInterface(this, IInlineSuggestionUiCallback.DESCRIPTOR);
        }

        public static IInlineSuggestionUiCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IInlineSuggestionUiCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IInlineSuggestionUiCallback)) {
                return (IInlineSuggestionUiCallback) iin;
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
                    return "onClick";
                case 2:
                    return "onLongClick";
                case 3:
                    return "onContent";
                case 4:
                    return "onError";
                case 5:
                    return "onTransferTouchFocusToImeWindow";
                case 6:
                    return "onStartIntentSender";
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
                data.enforceInterface(IInlineSuggestionUiCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IInlineSuggestionUiCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            onClick();
                            break;
                        case 2:
                            onLongClick();
                            break;
                        case 3:
                            IInlineSuggestionUi _arg0 = IInlineSuggestionUi.Stub.asInterface(data.readStrongBinder());
                            SurfaceControlViewHost.SurfacePackage _arg1 = (SurfaceControlViewHost.SurfacePackage) data.readTypedObject(SurfaceControlViewHost.SurfacePackage.CREATOR);
                            int _arg2 = data.readInt();
                            int _arg3 = data.readInt();
                            data.enforceNoDataAvail();
                            onContent(_arg0, _arg1, _arg2, _arg3);
                            break;
                        case 4:
                            onError();
                            break;
                        case 5:
                            IBinder _arg02 = data.readStrongBinder();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            onTransferTouchFocusToImeWindow(_arg02, _arg12);
                            break;
                        case 6:
                            IntentSender _arg03 = (IntentSender) data.readTypedObject(IntentSender.CREATOR);
                            data.enforceNoDataAvail();
                            onStartIntentSender(_arg03);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IInlineSuggestionUiCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IInlineSuggestionUiCallback.DESCRIPTOR;
            }

            @Override // android.service.autofill.IInlineSuggestionUiCallback
            public void onClick() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInlineSuggestionUiCallback.DESCRIPTOR);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.autofill.IInlineSuggestionUiCallback
            public void onLongClick() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInlineSuggestionUiCallback.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.autofill.IInlineSuggestionUiCallback
            public void onContent(IInlineSuggestionUi content, SurfaceControlViewHost.SurfacePackage surface, int width, int height) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInlineSuggestionUiCallback.DESCRIPTOR);
                    _data.writeStrongInterface(content);
                    _data.writeTypedObject(surface, 0);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.autofill.IInlineSuggestionUiCallback
            public void onError() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInlineSuggestionUiCallback.DESCRIPTOR);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.autofill.IInlineSuggestionUiCallback
            public void onTransferTouchFocusToImeWindow(IBinder sourceInputToken, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInlineSuggestionUiCallback.DESCRIPTOR);
                    _data.writeStrongBinder(sourceInputToken);
                    _data.writeInt(displayId);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.autofill.IInlineSuggestionUiCallback
            public void onStartIntentSender(IntentSender intentSender) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInlineSuggestionUiCallback.DESCRIPTOR);
                    _data.writeTypedObject(intentSender, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 5;
        }
    }
}
