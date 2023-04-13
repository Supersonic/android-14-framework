package android.view.translation;

import android.content.ComponentName;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.IRemoteCallback;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.p008os.ResultReceiver;
import android.view.autofill.AutofillId;
import com.android.internal.p028os.IResultReceiver;
import java.util.List;
/* loaded from: classes4.dex */
public interface ITranslationManager extends IInterface {
    public static final String DESCRIPTOR = "android.view.translation.ITranslationManager";

    void getServiceSettingsActivity(IResultReceiver iResultReceiver, int i) throws RemoteException;

    void onSessionCreated(TranslationContext translationContext, int i, IResultReceiver iResultReceiver, int i2) throws RemoteException;

    void onTranslationCapabilitiesRequest(int i, int i2, ResultReceiver resultReceiver, int i3) throws RemoteException;

    void onTranslationFinished(boolean z, IBinder iBinder, ComponentName componentName, int i) throws RemoteException;

    void registerTranslationCapabilityCallback(IRemoteCallback iRemoteCallback, int i) throws RemoteException;

    void registerUiTranslationStateCallback(IRemoteCallback iRemoteCallback, int i) throws RemoteException;

    void unregisterTranslationCapabilityCallback(IRemoteCallback iRemoteCallback, int i) throws RemoteException;

    void unregisterUiTranslationStateCallback(IRemoteCallback iRemoteCallback, int i) throws RemoteException;

    void updateUiTranslationState(int i, TranslationSpec translationSpec, TranslationSpec translationSpec2, List<AutofillId> list, IBinder iBinder, int i2, UiTranslationSpec uiTranslationSpec, int i3) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements ITranslationManager {
        @Override // android.view.translation.ITranslationManager
        public void onTranslationCapabilitiesRequest(int sourceFormat, int destFormat, ResultReceiver receiver, int userId) throws RemoteException {
        }

        @Override // android.view.translation.ITranslationManager
        public void registerTranslationCapabilityCallback(IRemoteCallback callback, int userId) throws RemoteException {
        }

        @Override // android.view.translation.ITranslationManager
        public void unregisterTranslationCapabilityCallback(IRemoteCallback callback, int userId) throws RemoteException {
        }

        @Override // android.view.translation.ITranslationManager
        public void onSessionCreated(TranslationContext translationContext, int sessionId, IResultReceiver receiver, int userId) throws RemoteException {
        }

        @Override // android.view.translation.ITranslationManager
        public void updateUiTranslationState(int state, TranslationSpec sourceSpec, TranslationSpec targetSpec, List<AutofillId> viewIds, IBinder token, int taskId, UiTranslationSpec uiTranslationSpec, int userId) throws RemoteException {
        }

        @Override // android.view.translation.ITranslationManager
        public void registerUiTranslationStateCallback(IRemoteCallback callback, int userId) throws RemoteException {
        }

        @Override // android.view.translation.ITranslationManager
        public void unregisterUiTranslationStateCallback(IRemoteCallback callback, int userId) throws RemoteException {
        }

        @Override // android.view.translation.ITranslationManager
        public void getServiceSettingsActivity(IResultReceiver result, int userId) throws RemoteException {
        }

        @Override // android.view.translation.ITranslationManager
        public void onTranslationFinished(boolean activityDestroyed, IBinder token, ComponentName componentName, int userId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements ITranslationManager {
        static final int TRANSACTION_getServiceSettingsActivity = 8;
        static final int TRANSACTION_onSessionCreated = 4;
        static final int TRANSACTION_onTranslationCapabilitiesRequest = 1;
        static final int TRANSACTION_onTranslationFinished = 9;
        static final int TRANSACTION_registerTranslationCapabilityCallback = 2;
        static final int TRANSACTION_registerUiTranslationStateCallback = 6;
        static final int TRANSACTION_unregisterTranslationCapabilityCallback = 3;
        static final int TRANSACTION_unregisterUiTranslationStateCallback = 7;
        static final int TRANSACTION_updateUiTranslationState = 5;

        public Stub() {
            attachInterface(this, ITranslationManager.DESCRIPTOR);
        }

        public static ITranslationManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ITranslationManager.DESCRIPTOR);
            if (iin != null && (iin instanceof ITranslationManager)) {
                return (ITranslationManager) iin;
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
                    return "onTranslationCapabilitiesRequest";
                case 2:
                    return "registerTranslationCapabilityCallback";
                case 3:
                    return "unregisterTranslationCapabilityCallback";
                case 4:
                    return "onSessionCreated";
                case 5:
                    return "updateUiTranslationState";
                case 6:
                    return "registerUiTranslationStateCallback";
                case 7:
                    return "unregisterUiTranslationStateCallback";
                case 8:
                    return "getServiceSettingsActivity";
                case 9:
                    return "onTranslationFinished";
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
                data.enforceInterface(ITranslationManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ITranslationManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            int _arg1 = data.readInt();
                            ResultReceiver _arg2 = (ResultReceiver) data.readTypedObject(ResultReceiver.CREATOR);
                            int _arg3 = data.readInt();
                            data.enforceNoDataAvail();
                            onTranslationCapabilitiesRequest(_arg0, _arg1, _arg2, _arg3);
                            break;
                        case 2:
                            IRemoteCallback _arg02 = IRemoteCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            registerTranslationCapabilityCallback(_arg02, _arg12);
                            break;
                        case 3:
                            IRemoteCallback _arg03 = IRemoteCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            unregisterTranslationCapabilityCallback(_arg03, _arg13);
                            break;
                        case 4:
                            TranslationContext _arg04 = (TranslationContext) data.readTypedObject(TranslationContext.CREATOR);
                            int _arg14 = data.readInt();
                            IResultReceiver _arg22 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            int _arg32 = data.readInt();
                            data.enforceNoDataAvail();
                            onSessionCreated(_arg04, _arg14, _arg22, _arg32);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            TranslationSpec _arg15 = (TranslationSpec) data.readTypedObject(TranslationSpec.CREATOR);
                            TranslationSpec _arg23 = (TranslationSpec) data.readTypedObject(TranslationSpec.CREATOR);
                            List<AutofillId> _arg33 = data.createTypedArrayList(AutofillId.CREATOR);
                            IBinder _arg4 = data.readStrongBinder();
                            int _arg5 = data.readInt();
                            UiTranslationSpec _arg6 = (UiTranslationSpec) data.readTypedObject(UiTranslationSpec.CREATOR);
                            int _arg7 = data.readInt();
                            data.enforceNoDataAvail();
                            updateUiTranslationState(_arg05, _arg15, _arg23, _arg33, _arg4, _arg5, _arg6, _arg7);
                            break;
                        case 6:
                            IRemoteCallback _arg06 = IRemoteCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            registerUiTranslationStateCallback(_arg06, _arg16);
                            break;
                        case 7:
                            IRemoteCallback _arg07 = IRemoteCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg17 = data.readInt();
                            data.enforceNoDataAvail();
                            unregisterUiTranslationStateCallback(_arg07, _arg17);
                            break;
                        case 8:
                            IResultReceiver _arg08 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            int _arg18 = data.readInt();
                            data.enforceNoDataAvail();
                            getServiceSettingsActivity(_arg08, _arg18);
                            break;
                        case 9:
                            boolean _arg09 = data.readBoolean();
                            IBinder _arg19 = data.readStrongBinder();
                            ComponentName _arg24 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg34 = data.readInt();
                            data.enforceNoDataAvail();
                            onTranslationFinished(_arg09, _arg19, _arg24, _arg34);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements ITranslationManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ITranslationManager.DESCRIPTOR;
            }

            @Override // android.view.translation.ITranslationManager
            public void onTranslationCapabilitiesRequest(int sourceFormat, int destFormat, ResultReceiver receiver, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITranslationManager.DESCRIPTOR);
                    _data.writeInt(sourceFormat);
                    _data.writeInt(destFormat);
                    _data.writeTypedObject(receiver, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.translation.ITranslationManager
            public void registerTranslationCapabilityCallback(IRemoteCallback callback, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITranslationManager.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeInt(userId);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.translation.ITranslationManager
            public void unregisterTranslationCapabilityCallback(IRemoteCallback callback, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITranslationManager.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeInt(userId);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.translation.ITranslationManager
            public void onSessionCreated(TranslationContext translationContext, int sessionId, IResultReceiver receiver, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITranslationManager.DESCRIPTOR);
                    _data.writeTypedObject(translationContext, 0);
                    _data.writeInt(sessionId);
                    _data.writeStrongInterface(receiver);
                    _data.writeInt(userId);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.translation.ITranslationManager
            public void updateUiTranslationState(int state, TranslationSpec sourceSpec, TranslationSpec targetSpec, List<AutofillId> viewIds, IBinder token, int taskId, UiTranslationSpec uiTranslationSpec, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITranslationManager.DESCRIPTOR);
                    _data.writeInt(state);
                    _data.writeTypedObject(sourceSpec, 0);
                    _data.writeTypedObject(targetSpec, 0);
                    _data.writeTypedList(viewIds, 0);
                    _data.writeStrongBinder(token);
                    _data.writeInt(taskId);
                    _data.writeTypedObject(uiTranslationSpec, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.translation.ITranslationManager
            public void registerUiTranslationStateCallback(IRemoteCallback callback, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITranslationManager.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeInt(userId);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.translation.ITranslationManager
            public void unregisterUiTranslationStateCallback(IRemoteCallback callback, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITranslationManager.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeInt(userId);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.translation.ITranslationManager
            public void getServiceSettingsActivity(IResultReceiver result, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITranslationManager.DESCRIPTOR);
                    _data.writeStrongInterface(result);
                    _data.writeInt(userId);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.translation.ITranslationManager
            public void onTranslationFinished(boolean activityDestroyed, IBinder token, ComponentName componentName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITranslationManager.DESCRIPTOR);
                    _data.writeBoolean(activityDestroyed);
                    _data.writeStrongBinder(token);
                    _data.writeTypedObject(componentName, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 8;
        }
    }
}
