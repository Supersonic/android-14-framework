package android.service.autofill.augmented;

import android.content.ComponentName;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.service.autofill.augmented.IFillCallback;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import android.view.inputmethod.InlineSuggestionsRequest;
/* loaded from: classes3.dex */
public interface IAugmentedAutofillService extends IInterface {
    public static final String DESCRIPTOR = "android.service.autofill.augmented.IAugmentedAutofillService";

    void onConnected(boolean z, boolean z2) throws RemoteException;

    void onDestroyAllFillWindowsRequest() throws RemoteException;

    void onDisconnected() throws RemoteException;

    void onFillRequest(int i, IBinder iBinder, int i2, ComponentName componentName, AutofillId autofillId, AutofillValue autofillValue, long j, InlineSuggestionsRequest inlineSuggestionsRequest, IFillCallback iFillCallback) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IAugmentedAutofillService {
        @Override // android.service.autofill.augmented.IAugmentedAutofillService
        public void onConnected(boolean debug, boolean verbose) throws RemoteException {
        }

        @Override // android.service.autofill.augmented.IAugmentedAutofillService
        public void onDisconnected() throws RemoteException {
        }

        @Override // android.service.autofill.augmented.IAugmentedAutofillService
        public void onFillRequest(int sessionId, IBinder autofillManagerClient, int taskId, ComponentName activityComponent, AutofillId focusedId, AutofillValue focusedValue, long requestTime, InlineSuggestionsRequest inlineSuggestionsRequest, IFillCallback callback) throws RemoteException {
        }

        @Override // android.service.autofill.augmented.IAugmentedAutofillService
        public void onDestroyAllFillWindowsRequest() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IAugmentedAutofillService {
        static final int TRANSACTION_onConnected = 1;
        static final int TRANSACTION_onDestroyAllFillWindowsRequest = 4;
        static final int TRANSACTION_onDisconnected = 2;
        static final int TRANSACTION_onFillRequest = 3;

        public Stub() {
            attachInterface(this, IAugmentedAutofillService.DESCRIPTOR);
        }

        public static IAugmentedAutofillService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IAugmentedAutofillService.DESCRIPTOR);
            if (iin != null && (iin instanceof IAugmentedAutofillService)) {
                return (IAugmentedAutofillService) iin;
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
                    return "onConnected";
                case 2:
                    return "onDisconnected";
                case 3:
                    return "onFillRequest";
                case 4:
                    return "onDestroyAllFillWindowsRequest";
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
                data.enforceInterface(IAugmentedAutofillService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IAugmentedAutofillService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            boolean _arg0 = data.readBoolean();
                            boolean _arg1 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onConnected(_arg0, _arg1);
                            break;
                        case 2:
                            onDisconnected();
                            break;
                        case 3:
                            int _arg02 = data.readInt();
                            IBinder _arg12 = data.readStrongBinder();
                            int _arg2 = data.readInt();
                            ComponentName _arg3 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            AutofillId _arg4 = (AutofillId) data.readTypedObject(AutofillId.CREATOR);
                            AutofillValue _arg5 = (AutofillValue) data.readTypedObject(AutofillValue.CREATOR);
                            long _arg6 = data.readLong();
                            InlineSuggestionsRequest _arg7 = (InlineSuggestionsRequest) data.readTypedObject(InlineSuggestionsRequest.CREATOR);
                            IFillCallback _arg8 = IFillCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onFillRequest(_arg02, _arg12, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7, _arg8);
                            break;
                        case 4:
                            onDestroyAllFillWindowsRequest();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IAugmentedAutofillService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAugmentedAutofillService.DESCRIPTOR;
            }

            @Override // android.service.autofill.augmented.IAugmentedAutofillService
            public void onConnected(boolean debug, boolean verbose) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAugmentedAutofillService.DESCRIPTOR);
                    _data.writeBoolean(debug);
                    _data.writeBoolean(verbose);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.autofill.augmented.IAugmentedAutofillService
            public void onDisconnected() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAugmentedAutofillService.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.autofill.augmented.IAugmentedAutofillService
            public void onFillRequest(int sessionId, IBinder autofillManagerClient, int taskId, ComponentName activityComponent, AutofillId focusedId, AutofillValue focusedValue, long requestTime, InlineSuggestionsRequest inlineSuggestionsRequest, IFillCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAugmentedAutofillService.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeStrongBinder(autofillManagerClient);
                    _data.writeInt(taskId);
                    _data.writeTypedObject(activityComponent, 0);
                    _data.writeTypedObject(focusedId, 0);
                    _data.writeTypedObject(focusedValue, 0);
                    _data.writeLong(requestTime);
                    _data.writeTypedObject(inlineSuggestionsRequest, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.autofill.augmented.IAugmentedAutofillService
            public void onDestroyAllFillWindowsRequest() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAugmentedAutofillService.DESCRIPTOR);
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
