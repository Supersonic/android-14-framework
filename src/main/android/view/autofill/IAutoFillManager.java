package android.view.autofill;

import android.content.ComponentName;
import android.graphics.Rect;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.service.autofill.UserData;
import android.view.autofill.IAutoFillManagerClient;
import com.android.internal.p028os.IResultReceiver;
import java.util.List;
/* loaded from: classes4.dex */
public interface IAutoFillManager extends IInterface {
    void addClient(IAutoFillManagerClient iAutoFillManagerClient, ComponentName componentName, int i, IResultReceiver iResultReceiver) throws RemoteException;

    void cancelSession(int i, int i2) throws RemoteException;

    void disableOwnedAutofillServices(int i) throws RemoteException;

    void finishSession(int i, int i2, int i3) throws RemoteException;

    void getAutofillServiceComponentName(IResultReceiver iResultReceiver) throws RemoteException;

    void getAvailableFieldClassificationAlgorithms(IResultReceiver iResultReceiver) throws RemoteException;

    void getDefaultFieldClassificationAlgorithm(IResultReceiver iResultReceiver) throws RemoteException;

    void getFillEventHistory(IResultReceiver iResultReceiver) throws RemoteException;

    void getUserData(IResultReceiver iResultReceiver) throws RemoteException;

    void getUserDataId(IResultReceiver iResultReceiver) throws RemoteException;

    void isFieldClassificationEnabled(IResultReceiver iResultReceiver) throws RemoteException;

    void isServiceEnabled(int i, String str, IResultReceiver iResultReceiver) throws RemoteException;

    void isServiceSupported(int i, IResultReceiver iResultReceiver) throws RemoteException;

    void onPendingSaveUi(int i, IBinder iBinder) throws RemoteException;

    void removeClient(IAutoFillManagerClient iAutoFillManagerClient, int i) throws RemoteException;

    void restoreSession(int i, IBinder iBinder, IBinder iBinder2, IResultReceiver iResultReceiver) throws RemoteException;

    void setAugmentedAutofillWhitelist(List<String> list, List<ComponentName> list2, IResultReceiver iResultReceiver) throws RemoteException;

    void setAuthenticationResult(Bundle bundle, int i, int i2, int i3) throws RemoteException;

    void setAutofillFailure(int i, List<AutofillId> list, int i2) throws RemoteException;

    void setHasCallback(int i, int i2, boolean z) throws RemoteException;

    void setUserData(UserData userData) throws RemoteException;

    void startSession(IBinder iBinder, IBinder iBinder2, AutofillId autofillId, Rect rect, AutofillValue autofillValue, int i, boolean z, int i2, ComponentName componentName, boolean z2, IResultReceiver iResultReceiver) throws RemoteException;

    void updateSession(int i, AutofillId autofillId, Rect rect, AutofillValue autofillValue, int i2, int i3, int i4) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IAutoFillManager {
        @Override // android.view.autofill.IAutoFillManager
        public void addClient(IAutoFillManagerClient client, ComponentName componentName, int userId, IResultReceiver result) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManager
        public void removeClient(IAutoFillManagerClient client, int userId) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManager
        public void startSession(IBinder activityToken, IBinder appCallback, AutofillId autoFillId, Rect bounds, AutofillValue value, int userId, boolean hasCallback, int flags, ComponentName componentName, boolean compatMode, IResultReceiver result) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManager
        public void getFillEventHistory(IResultReceiver result) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManager
        public void restoreSession(int sessionId, IBinder activityToken, IBinder appCallback, IResultReceiver result) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManager
        public void updateSession(int sessionId, AutofillId id, Rect bounds, AutofillValue value, int action, int flags, int userId) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManager
        public void setAutofillFailure(int sessionId, List<AutofillId> ids, int userId) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManager
        public void finishSession(int sessionId, int userId, int commitReason) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManager
        public void cancelSession(int sessionId, int userId) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManager
        public void setAuthenticationResult(Bundle data, int sessionId, int authenticationId, int userId) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManager
        public void setHasCallback(int sessionId, int userId, boolean hasIt) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManager
        public void disableOwnedAutofillServices(int userId) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManager
        public void isServiceSupported(int userId, IResultReceiver result) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManager
        public void isServiceEnabled(int userId, String packageName, IResultReceiver result) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManager
        public void onPendingSaveUi(int operation, IBinder token) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManager
        public void getUserData(IResultReceiver result) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManager
        public void getUserDataId(IResultReceiver result) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManager
        public void setUserData(UserData userData) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManager
        public void isFieldClassificationEnabled(IResultReceiver result) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManager
        public void getAutofillServiceComponentName(IResultReceiver result) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManager
        public void getAvailableFieldClassificationAlgorithms(IResultReceiver result) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManager
        public void getDefaultFieldClassificationAlgorithm(IResultReceiver result) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManager
        public void setAugmentedAutofillWhitelist(List<String> packages, List<ComponentName> activities, IResultReceiver result) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IAutoFillManager {
        public static final String DESCRIPTOR = "android.view.autofill.IAutoFillManager";
        static final int TRANSACTION_addClient = 1;
        static final int TRANSACTION_cancelSession = 9;
        static final int TRANSACTION_disableOwnedAutofillServices = 12;
        static final int TRANSACTION_finishSession = 8;
        static final int TRANSACTION_getAutofillServiceComponentName = 20;
        static final int TRANSACTION_getAvailableFieldClassificationAlgorithms = 21;
        static final int TRANSACTION_getDefaultFieldClassificationAlgorithm = 22;
        static final int TRANSACTION_getFillEventHistory = 4;
        static final int TRANSACTION_getUserData = 16;
        static final int TRANSACTION_getUserDataId = 17;
        static final int TRANSACTION_isFieldClassificationEnabled = 19;
        static final int TRANSACTION_isServiceEnabled = 14;
        static final int TRANSACTION_isServiceSupported = 13;
        static final int TRANSACTION_onPendingSaveUi = 15;
        static final int TRANSACTION_removeClient = 2;
        static final int TRANSACTION_restoreSession = 5;
        static final int TRANSACTION_setAugmentedAutofillWhitelist = 23;
        static final int TRANSACTION_setAuthenticationResult = 10;
        static final int TRANSACTION_setAutofillFailure = 7;
        static final int TRANSACTION_setHasCallback = 11;
        static final int TRANSACTION_setUserData = 18;
        static final int TRANSACTION_startSession = 3;
        static final int TRANSACTION_updateSession = 6;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAutoFillManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAutoFillManager)) {
                return (IAutoFillManager) iin;
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
                    return "addClient";
                case 2:
                    return "removeClient";
                case 3:
                    return "startSession";
                case 4:
                    return "getFillEventHistory";
                case 5:
                    return "restoreSession";
                case 6:
                    return "updateSession";
                case 7:
                    return "setAutofillFailure";
                case 8:
                    return "finishSession";
                case 9:
                    return "cancelSession";
                case 10:
                    return "setAuthenticationResult";
                case 11:
                    return "setHasCallback";
                case 12:
                    return "disableOwnedAutofillServices";
                case 13:
                    return "isServiceSupported";
                case 14:
                    return "isServiceEnabled";
                case 15:
                    return "onPendingSaveUi";
                case 16:
                    return "getUserData";
                case 17:
                    return "getUserDataId";
                case 18:
                    return "setUserData";
                case 19:
                    return "isFieldClassificationEnabled";
                case 20:
                    return "getAutofillServiceComponentName";
                case 21:
                    return "getAvailableFieldClassificationAlgorithms";
                case 22:
                    return "getDefaultFieldClassificationAlgorithm";
                case 23:
                    return "setAugmentedAutofillWhitelist";
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
                            IAutoFillManagerClient _arg0 = IAutoFillManagerClient.Stub.asInterface(data.readStrongBinder());
                            ComponentName _arg1 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg2 = data.readInt();
                            IResultReceiver _arg3 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addClient(_arg0, _arg1, _arg2, _arg3);
                            return true;
                        case 2:
                            IAutoFillManagerClient _arg02 = IAutoFillManagerClient.Stub.asInterface(data.readStrongBinder());
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            removeClient(_arg02, _arg12);
                            return true;
                        case 3:
                            IBinder _arg03 = data.readStrongBinder();
                            IBinder _arg13 = data.readStrongBinder();
                            AutofillId _arg22 = (AutofillId) data.readTypedObject(AutofillId.CREATOR);
                            Rect _arg32 = (Rect) data.readTypedObject(Rect.CREATOR);
                            AutofillValue _arg4 = (AutofillValue) data.readTypedObject(AutofillValue.CREATOR);
                            int _arg5 = data.readInt();
                            boolean _arg6 = data.readBoolean();
                            int _arg7 = data.readInt();
                            ComponentName _arg8 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            boolean _arg9 = data.readBoolean();
                            IResultReceiver _arg10 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            startSession(_arg03, _arg13, _arg22, _arg32, _arg4, _arg5, _arg6, _arg7, _arg8, _arg9, _arg10);
                            return true;
                        case 4:
                            IResultReceiver _arg04 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getFillEventHistory(_arg04);
                            return true;
                        case 5:
                            int _arg05 = data.readInt();
                            IBinder _arg14 = data.readStrongBinder();
                            IBinder _arg23 = data.readStrongBinder();
                            IResultReceiver _arg33 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            restoreSession(_arg05, _arg14, _arg23, _arg33);
                            return true;
                        case 6:
                            int _arg06 = data.readInt();
                            AutofillId _arg15 = (AutofillId) data.readTypedObject(AutofillId.CREATOR);
                            Rect _arg24 = (Rect) data.readTypedObject(Rect.CREATOR);
                            AutofillValue _arg34 = (AutofillValue) data.readTypedObject(AutofillValue.CREATOR);
                            int _arg42 = data.readInt();
                            int _arg52 = data.readInt();
                            int _arg62 = data.readInt();
                            data.enforceNoDataAvail();
                            updateSession(_arg06, _arg15, _arg24, _arg34, _arg42, _arg52, _arg62);
                            return true;
                        case 7:
                            int _arg07 = data.readInt();
                            List<AutofillId> _arg16 = data.createTypedArrayList(AutofillId.CREATOR);
                            int _arg25 = data.readInt();
                            data.enforceNoDataAvail();
                            setAutofillFailure(_arg07, _arg16, _arg25);
                            return true;
                        case 8:
                            int _arg08 = data.readInt();
                            int _arg17 = data.readInt();
                            int _arg26 = data.readInt();
                            data.enforceNoDataAvail();
                            finishSession(_arg08, _arg17, _arg26);
                            return true;
                        case 9:
                            int _arg09 = data.readInt();
                            int _arg18 = data.readInt();
                            data.enforceNoDataAvail();
                            cancelSession(_arg09, _arg18);
                            return true;
                        case 10:
                            Bundle _arg010 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg19 = data.readInt();
                            int _arg27 = data.readInt();
                            int _arg35 = data.readInt();
                            data.enforceNoDataAvail();
                            setAuthenticationResult(_arg010, _arg19, _arg27, _arg35);
                            return true;
                        case 11:
                            int _arg011 = data.readInt();
                            int _arg110 = data.readInt();
                            boolean _arg28 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setHasCallback(_arg011, _arg110, _arg28);
                            return true;
                        case 12:
                            int _arg012 = data.readInt();
                            data.enforceNoDataAvail();
                            disableOwnedAutofillServices(_arg012);
                            return true;
                        case 13:
                            int _arg013 = data.readInt();
                            IResultReceiver _arg111 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            isServiceSupported(_arg013, _arg111);
                            return true;
                        case 14:
                            int _arg014 = data.readInt();
                            String _arg112 = data.readString();
                            IResultReceiver _arg29 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            isServiceEnabled(_arg014, _arg112, _arg29);
                            return true;
                        case 15:
                            int _arg015 = data.readInt();
                            IBinder _arg113 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            onPendingSaveUi(_arg015, _arg113);
                            return true;
                        case 16:
                            IResultReceiver _arg016 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getUserData(_arg016);
                            return true;
                        case 17:
                            IResultReceiver _arg017 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getUserDataId(_arg017);
                            return true;
                        case 18:
                            UserData _arg018 = (UserData) data.readTypedObject(UserData.CREATOR);
                            data.enforceNoDataAvail();
                            setUserData(_arg018);
                            return true;
                        case 19:
                            IResultReceiver _arg019 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            isFieldClassificationEnabled(_arg019);
                            return true;
                        case 20:
                            IResultReceiver _arg020 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getAutofillServiceComponentName(_arg020);
                            return true;
                        case 21:
                            IResultReceiver _arg021 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getAvailableFieldClassificationAlgorithms(_arg021);
                            return true;
                        case 22:
                            IResultReceiver _arg022 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getDefaultFieldClassificationAlgorithm(_arg022);
                            return true;
                        case 23:
                            List<String> _arg023 = data.createStringArrayList();
                            List<ComponentName> _arg114 = data.createTypedArrayList(ComponentName.CREATOR);
                            IResultReceiver _arg210 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setAugmentedAutofillWhitelist(_arg023, _arg114, _arg210);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IAutoFillManager {
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

            @Override // android.view.autofill.IAutoFillManager
            public void addClient(IAutoFillManagerClient client, ComponentName componentName, int userId, IResultReceiver result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeTypedObject(componentName, 0);
                    _data.writeInt(userId);
                    _data.writeStrongInterface(result);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManager
            public void removeClient(IAutoFillManagerClient client, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeInt(userId);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManager
            public void startSession(IBinder activityToken, IBinder appCallback, AutofillId autoFillId, Rect bounds, AutofillValue value, int userId, boolean hasCallback, int flags, ComponentName componentName, boolean compatMode, IResultReceiver result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(activityToken);
                    try {
                        _data.writeStrongBinder(appCallback);
                        try {
                            _data.writeTypedObject(autoFillId, 0);
                            try {
                                _data.writeTypedObject(bounds, 0);
                            } catch (Throwable th) {
                                th = th;
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeTypedObject(value, 0);
                        try {
                            _data.writeInt(userId);
                            try {
                                _data.writeBoolean(hasCallback);
                                try {
                                    _data.writeInt(flags);
                                } catch (Throwable th4) {
                                    th = th4;
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th5) {
                                th = th5;
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th6) {
                            th = th6;
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeTypedObject(componentName, 0);
                            try {
                                _data.writeBoolean(compatMode);
                                try {
                                    _data.writeStrongInterface(result);
                                    try {
                                        this.mRemote.transact(3, _data, null, 1);
                                        _data.recycle();
                                    } catch (Throwable th7) {
                                        th = th7;
                                        _data.recycle();
                                        throw th;
                                    }
                                } catch (Throwable th8) {
                                    th = th8;
                                }
                            } catch (Throwable th9) {
                                th = th9;
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th10) {
                            th = th10;
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th11) {
                        th = th11;
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th12) {
                    th = th12;
                }
            }

            @Override // android.view.autofill.IAutoFillManager
            public void getFillEventHistory(IResultReceiver result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(result);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManager
            public void restoreSession(int sessionId, IBinder activityToken, IBinder appCallback, IResultReceiver result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeStrongBinder(activityToken);
                    _data.writeStrongBinder(appCallback);
                    _data.writeStrongInterface(result);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManager
            public void updateSession(int sessionId, AutofillId id, Rect bounds, AutofillValue value, int action, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeTypedObject(id, 0);
                    _data.writeTypedObject(bounds, 0);
                    _data.writeTypedObject(value, 0);
                    _data.writeInt(action);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManager
            public void setAutofillFailure(int sessionId, List<AutofillId> ids, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeTypedList(ids, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManager
            public void finishSession(int sessionId, int userId, int commitReason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeInt(userId);
                    _data.writeInt(commitReason);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManager
            public void cancelSession(int sessionId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeInt(userId);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManager
            public void setAuthenticationResult(Bundle data, int sessionId, int authenticationId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(data, 0);
                    _data.writeInt(sessionId);
                    _data.writeInt(authenticationId);
                    _data.writeInt(userId);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManager
            public void setHasCallback(int sessionId, int userId, boolean hasIt) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeInt(userId);
                    _data.writeBoolean(hasIt);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManager
            public void disableOwnedAutofillServices(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManager
            public void isServiceSupported(int userId, IResultReceiver result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeStrongInterface(result);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManager
            public void isServiceEnabled(int userId, String packageName, IResultReceiver result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(packageName);
                    _data.writeStrongInterface(result);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManager
            public void onPendingSaveUi(int operation, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(operation);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManager
            public void getUserData(IResultReceiver result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(result);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManager
            public void getUserDataId(IResultReceiver result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(result);
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManager
            public void setUserData(UserData userData) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(userData, 0);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManager
            public void isFieldClassificationEnabled(IResultReceiver result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(result);
                    this.mRemote.transact(19, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManager
            public void getAutofillServiceComponentName(IResultReceiver result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(result);
                    this.mRemote.transact(20, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManager
            public void getAvailableFieldClassificationAlgorithms(IResultReceiver result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(result);
                    this.mRemote.transact(21, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManager
            public void getDefaultFieldClassificationAlgorithm(IResultReceiver result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(result);
                    this.mRemote.transact(22, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManager
            public void setAugmentedAutofillWhitelist(List<String> packages, List<ComponentName> activities, IResultReceiver result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringList(packages);
                    _data.writeTypedList(activities, 0);
                    _data.writeStrongInterface(result);
                    this.mRemote.transact(23, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 22;
        }
    }
}
