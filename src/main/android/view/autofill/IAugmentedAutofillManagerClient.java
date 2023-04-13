package android.view.autofill;

import android.app.assist.AssistStructure;
import android.content.Context;
import android.graphics.Rect;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.autofill.IAutofillWindowPresenter;
import java.util.List;
/* loaded from: classes4.dex */
public interface IAugmentedAutofillManagerClient extends IInterface {
    public static final String DESCRIPTOR = "android.view.autofill.IAugmentedAutofillManagerClient";

    void autofill(int i, List<AutofillId> list, List<AutofillValue> list2, boolean z) throws RemoteException;

    Rect getViewCoordinates(AutofillId autofillId) throws RemoteException;

    AssistStructure.ViewNodeParcelable getViewNodeParcelable(AutofillId autofillId) throws RemoteException;

    boolean requestAutofill(int i, AutofillId autofillId) throws RemoteException;

    void requestHideFillUi(int i, AutofillId autofillId) throws RemoteException;

    void requestShowFillUi(int i, AutofillId autofillId, int i2, int i3, Rect rect, IAutofillWindowPresenter iAutofillWindowPresenter) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IAugmentedAutofillManagerClient {
        @Override // android.view.autofill.IAugmentedAutofillManagerClient
        public Rect getViewCoordinates(AutofillId id) throws RemoteException {
            return null;
        }

        @Override // android.view.autofill.IAugmentedAutofillManagerClient
        public AssistStructure.ViewNodeParcelable getViewNodeParcelable(AutofillId id) throws RemoteException {
            return null;
        }

        @Override // android.view.autofill.IAugmentedAutofillManagerClient
        public void autofill(int sessionId, List<AutofillId> ids, List<AutofillValue> values, boolean hideHighlight) throws RemoteException {
        }

        @Override // android.view.autofill.IAugmentedAutofillManagerClient
        public void requestShowFillUi(int sessionId, AutofillId id, int width, int height, Rect anchorBounds, IAutofillWindowPresenter presenter) throws RemoteException {
        }

        @Override // android.view.autofill.IAugmentedAutofillManagerClient
        public void requestHideFillUi(int sessionId, AutofillId id) throws RemoteException {
        }

        @Override // android.view.autofill.IAugmentedAutofillManagerClient
        public boolean requestAutofill(int sessionId, AutofillId id) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IAugmentedAutofillManagerClient {
        static final int TRANSACTION_autofill = 3;
        static final int TRANSACTION_getViewCoordinates = 1;
        static final int TRANSACTION_getViewNodeParcelable = 2;
        static final int TRANSACTION_requestAutofill = 6;
        static final int TRANSACTION_requestHideFillUi = 5;
        static final int TRANSACTION_requestShowFillUi = 4;

        public Stub() {
            attachInterface(this, IAugmentedAutofillManagerClient.DESCRIPTOR);
        }

        public static IAugmentedAutofillManagerClient asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IAugmentedAutofillManagerClient.DESCRIPTOR);
            if (iin != null && (iin instanceof IAugmentedAutofillManagerClient)) {
                return (IAugmentedAutofillManagerClient) iin;
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
                    return "getViewCoordinates";
                case 2:
                    return "getViewNodeParcelable";
                case 3:
                    return Context.AUTOFILL_MANAGER_SERVICE;
                case 4:
                    return "requestShowFillUi";
                case 5:
                    return "requestHideFillUi";
                case 6:
                    return "requestAutofill";
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
                data.enforceInterface(IAugmentedAutofillManagerClient.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IAugmentedAutofillManagerClient.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            AutofillId _arg0 = (AutofillId) data.readTypedObject(AutofillId.CREATOR);
                            data.enforceNoDataAvail();
                            Rect _result = getViewCoordinates(_arg0);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 2:
                            AutofillId _arg02 = (AutofillId) data.readTypedObject(AutofillId.CREATOR);
                            data.enforceNoDataAvail();
                            AssistStructure.ViewNodeParcelable _result2 = getViewNodeParcelable(_arg02);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            List<AutofillId> _arg1 = data.createTypedArrayList(AutofillId.CREATOR);
                            List<AutofillValue> _arg2 = data.createTypedArrayList(AutofillValue.CREATOR);
                            boolean _arg3 = data.readBoolean();
                            data.enforceNoDataAvail();
                            autofill(_arg03, _arg1, _arg2, _arg3);
                            reply.writeNoException();
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            AutofillId _arg12 = (AutofillId) data.readTypedObject(AutofillId.CREATOR);
                            int _arg22 = data.readInt();
                            int _arg32 = data.readInt();
                            Rect _arg4 = (Rect) data.readTypedObject(Rect.CREATOR);
                            IAutofillWindowPresenter _arg5 = IAutofillWindowPresenter.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            requestShowFillUi(_arg04, _arg12, _arg22, _arg32, _arg4, _arg5);
                            reply.writeNoException();
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            AutofillId _arg13 = (AutofillId) data.readTypedObject(AutofillId.CREATOR);
                            data.enforceNoDataAvail();
                            requestHideFillUi(_arg05, _arg13);
                            reply.writeNoException();
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            AutofillId _arg14 = (AutofillId) data.readTypedObject(AutofillId.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result3 = requestAutofill(_arg06, _arg14);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IAugmentedAutofillManagerClient {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAugmentedAutofillManagerClient.DESCRIPTOR;
            }

            @Override // android.view.autofill.IAugmentedAutofillManagerClient
            public Rect getViewCoordinates(AutofillId id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAugmentedAutofillManagerClient.DESCRIPTOR);
                    _data.writeTypedObject(id, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    Rect _result = (Rect) _reply.readTypedObject(Rect.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAugmentedAutofillManagerClient
            public AssistStructure.ViewNodeParcelable getViewNodeParcelable(AutofillId id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAugmentedAutofillManagerClient.DESCRIPTOR);
                    _data.writeTypedObject(id, 0);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    AssistStructure.ViewNodeParcelable _result = (AssistStructure.ViewNodeParcelable) _reply.readTypedObject(AssistStructure.ViewNodeParcelable.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAugmentedAutofillManagerClient
            public void autofill(int sessionId, List<AutofillId> ids, List<AutofillValue> values, boolean hideHighlight) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAugmentedAutofillManagerClient.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeTypedList(ids, 0);
                    _data.writeTypedList(values, 0);
                    _data.writeBoolean(hideHighlight);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAugmentedAutofillManagerClient
            public void requestShowFillUi(int sessionId, AutofillId id, int width, int height, Rect anchorBounds, IAutofillWindowPresenter presenter) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAugmentedAutofillManagerClient.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeTypedObject(id, 0);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    _data.writeTypedObject(anchorBounds, 0);
                    _data.writeStrongInterface(presenter);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAugmentedAutofillManagerClient
            public void requestHideFillUi(int sessionId, AutofillId id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAugmentedAutofillManagerClient.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeTypedObject(id, 0);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAugmentedAutofillManagerClient
            public boolean requestAutofill(int sessionId, AutofillId id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAugmentedAutofillManagerClient.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeTypedObject(id, 0);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
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
