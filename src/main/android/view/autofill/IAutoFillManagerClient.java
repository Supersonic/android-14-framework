package android.view.autofill;

import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Rect;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.service.autofill.IFillCallback;
import android.view.KeyEvent;
import android.view.autofill.IAutofillWindowPresenter;
import android.view.inputmethod.InlineSuggestionsRequest;
import com.android.internal.p028os.IResultReceiver;
import java.util.List;
/* loaded from: classes4.dex */
public interface IAutoFillManagerClient extends IInterface {
    void authenticate(int i, int i2, IntentSender intentSender, Intent intent, boolean z) throws RemoteException;

    void autofill(int i, List<AutofillId> list, List<AutofillValue> list2, boolean z) throws RemoteException;

    void autofillContent(int i, AutofillId autofillId, ClipData clipData) throws RemoteException;

    void dispatchUnhandledKey(int i, AutofillId autofillId, KeyEvent keyEvent) throws RemoteException;

    void getAugmentedAutofillClient(IResultReceiver iResultReceiver) throws RemoteException;

    void notifyDisableAutofill(long j, ComponentName componentName) throws RemoteException;

    void notifyFillDialogTriggerIds(List<AutofillId> list) throws RemoteException;

    void notifyFillUiHidden(int i, AutofillId autofillId) throws RemoteException;

    void notifyFillUiShown(int i, AutofillId autofillId) throws RemoteException;

    void notifyNoFillUi(int i, AutofillId autofillId, int i2) throws RemoteException;

    void requestFillFromClient(int i, InlineSuggestionsRequest inlineSuggestionsRequest, IFillCallback iFillCallback) throws RemoteException;

    void requestHideFillUi(int i, AutofillId autofillId) throws RemoteException;

    void requestShowFillUi(int i, AutofillId autofillId, int i2, int i3, Rect rect, IAutofillWindowPresenter iAutofillWindowPresenter) throws RemoteException;

    void requestShowSoftInput(AutofillId autofillId) throws RemoteException;

    void setSaveUiState(int i, boolean z) throws RemoteException;

    void setSessionFinished(int i, List<AutofillId> list) throws RemoteException;

    void setState(int i) throws RemoteException;

    void setTrackedViews(int i, AutofillId[] autofillIdArr, boolean z, boolean z2, AutofillId[] autofillIdArr2, AutofillId autofillId) throws RemoteException;

    void startIntentSender(IntentSender intentSender, Intent intent) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IAutoFillManagerClient {
        @Override // android.view.autofill.IAutoFillManagerClient
        public void setState(int flags) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManagerClient
        public void autofill(int sessionId, List<AutofillId> ids, List<AutofillValue> values, boolean hideHighlight) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManagerClient
        public void autofillContent(int sessionId, AutofillId id, ClipData content) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManagerClient
        public void authenticate(int sessionId, int authenticationId, IntentSender intent, Intent fillInIntent, boolean authenticateInline) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManagerClient
        public void setTrackedViews(int sessionId, AutofillId[] savableIds, boolean saveOnAllViewsInvisible, boolean saveOnFinish, AutofillId[] fillableIds, AutofillId saveTriggerId) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManagerClient
        public void requestShowFillUi(int sessionId, AutofillId id, int width, int height, Rect anchorBounds, IAutofillWindowPresenter presenter) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManagerClient
        public void requestHideFillUi(int sessionId, AutofillId id) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManagerClient
        public void notifyNoFillUi(int sessionId, AutofillId id, int sessionFinishedState) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManagerClient
        public void notifyFillUiShown(int sessionId, AutofillId id) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManagerClient
        public void notifyFillUiHidden(int sessionId, AutofillId id) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManagerClient
        public void dispatchUnhandledKey(int sessionId, AutofillId id, KeyEvent keyEvent) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManagerClient
        public void startIntentSender(IntentSender intentSender, Intent intent) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManagerClient
        public void setSaveUiState(int sessionId, boolean shown) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManagerClient
        public void setSessionFinished(int newState, List<AutofillId> autofillableIds) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManagerClient
        public void getAugmentedAutofillClient(IResultReceiver result) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManagerClient
        public void notifyDisableAutofill(long disableDuration, ComponentName componentName) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManagerClient
        public void requestShowSoftInput(AutofillId id) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManagerClient
        public void requestFillFromClient(int id, InlineSuggestionsRequest request, IFillCallback callback) throws RemoteException {
        }

        @Override // android.view.autofill.IAutoFillManagerClient
        public void notifyFillDialogTriggerIds(List<AutofillId> ids) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IAutoFillManagerClient {
        public static final String DESCRIPTOR = "android.view.autofill.IAutoFillManagerClient";
        static final int TRANSACTION_authenticate = 4;
        static final int TRANSACTION_autofill = 2;
        static final int TRANSACTION_autofillContent = 3;
        static final int TRANSACTION_dispatchUnhandledKey = 11;
        static final int TRANSACTION_getAugmentedAutofillClient = 15;
        static final int TRANSACTION_notifyDisableAutofill = 16;
        static final int TRANSACTION_notifyFillDialogTriggerIds = 19;
        static final int TRANSACTION_notifyFillUiHidden = 10;
        static final int TRANSACTION_notifyFillUiShown = 9;
        static final int TRANSACTION_notifyNoFillUi = 8;
        static final int TRANSACTION_requestFillFromClient = 18;
        static final int TRANSACTION_requestHideFillUi = 7;
        static final int TRANSACTION_requestShowFillUi = 6;
        static final int TRANSACTION_requestShowSoftInput = 17;
        static final int TRANSACTION_setSaveUiState = 13;
        static final int TRANSACTION_setSessionFinished = 14;
        static final int TRANSACTION_setState = 1;
        static final int TRANSACTION_setTrackedViews = 5;
        static final int TRANSACTION_startIntentSender = 12;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAutoFillManagerClient asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAutoFillManagerClient)) {
                return (IAutoFillManagerClient) iin;
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
                    return "setState";
                case 2:
                    return Context.AUTOFILL_MANAGER_SERVICE;
                case 3:
                    return "autofillContent";
                case 4:
                    return "authenticate";
                case 5:
                    return "setTrackedViews";
                case 6:
                    return "requestShowFillUi";
                case 7:
                    return "requestHideFillUi";
                case 8:
                    return "notifyNoFillUi";
                case 9:
                    return "notifyFillUiShown";
                case 10:
                    return "notifyFillUiHidden";
                case 11:
                    return "dispatchUnhandledKey";
                case 12:
                    return "startIntentSender";
                case 13:
                    return "setSaveUiState";
                case 14:
                    return "setSessionFinished";
                case 15:
                    return "getAugmentedAutofillClient";
                case 16:
                    return "notifyDisableAutofill";
                case 17:
                    return "requestShowSoftInput";
                case 18:
                    return "requestFillFromClient";
                case 19:
                    return "notifyFillDialogTriggerIds";
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
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            setState(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            List<AutofillId> _arg1 = data.createTypedArrayList(AutofillId.CREATOR);
                            List<AutofillValue> _arg2 = data.createTypedArrayList(AutofillValue.CREATOR);
                            boolean _arg3 = data.readBoolean();
                            data.enforceNoDataAvail();
                            autofill(_arg02, _arg1, _arg2, _arg3);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            AutofillId _arg12 = (AutofillId) data.readTypedObject(AutofillId.CREATOR);
                            ClipData _arg22 = (ClipData) data.readTypedObject(ClipData.CREATOR);
                            data.enforceNoDataAvail();
                            autofillContent(_arg03, _arg12, _arg22);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            int _arg13 = data.readInt();
                            IntentSender _arg23 = (IntentSender) data.readTypedObject(IntentSender.CREATOR);
                            Intent _arg32 = (Intent) data.readTypedObject(Intent.CREATOR);
                            boolean _arg4 = data.readBoolean();
                            data.enforceNoDataAvail();
                            authenticate(_arg04, _arg13, _arg23, _arg32, _arg4);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            AutofillId[] _arg14 = (AutofillId[]) data.createTypedArray(AutofillId.CREATOR);
                            boolean _arg24 = data.readBoolean();
                            boolean _arg33 = data.readBoolean();
                            AutofillId[] _arg42 = (AutofillId[]) data.createTypedArray(AutofillId.CREATOR);
                            AutofillId _arg5 = (AutofillId) data.readTypedObject(AutofillId.CREATOR);
                            data.enforceNoDataAvail();
                            setTrackedViews(_arg05, _arg14, _arg24, _arg33, _arg42, _arg5);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            AutofillId _arg15 = (AutofillId) data.readTypedObject(AutofillId.CREATOR);
                            int _arg25 = data.readInt();
                            int _arg34 = data.readInt();
                            Rect _arg43 = (Rect) data.readTypedObject(Rect.CREATOR);
                            IAutofillWindowPresenter _arg52 = IAutofillWindowPresenter.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            requestShowFillUi(_arg06, _arg15, _arg25, _arg34, _arg43, _arg52);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            AutofillId _arg16 = (AutofillId) data.readTypedObject(AutofillId.CREATOR);
                            data.enforceNoDataAvail();
                            requestHideFillUi(_arg07, _arg16);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            AutofillId _arg17 = (AutofillId) data.readTypedObject(AutofillId.CREATOR);
                            int _arg26 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyNoFillUi(_arg08, _arg17, _arg26);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            AutofillId _arg18 = (AutofillId) data.readTypedObject(AutofillId.CREATOR);
                            data.enforceNoDataAvail();
                            notifyFillUiShown(_arg09, _arg18);
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            AutofillId _arg19 = (AutofillId) data.readTypedObject(AutofillId.CREATOR);
                            data.enforceNoDataAvail();
                            notifyFillUiHidden(_arg010, _arg19);
                            break;
                        case 11:
                            int _arg011 = data.readInt();
                            AutofillId _arg110 = (AutofillId) data.readTypedObject(AutofillId.CREATOR);
                            KeyEvent _arg27 = (KeyEvent) data.readTypedObject(KeyEvent.CREATOR);
                            data.enforceNoDataAvail();
                            dispatchUnhandledKey(_arg011, _arg110, _arg27);
                            break;
                        case 12:
                            IntentSender _arg012 = (IntentSender) data.readTypedObject(IntentSender.CREATOR);
                            Intent _arg111 = (Intent) data.readTypedObject(Intent.CREATOR);
                            data.enforceNoDataAvail();
                            startIntentSender(_arg012, _arg111);
                            break;
                        case 13:
                            int _arg013 = data.readInt();
                            boolean _arg112 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setSaveUiState(_arg013, _arg112);
                            break;
                        case 14:
                            int _arg014 = data.readInt();
                            List<AutofillId> _arg113 = data.createTypedArrayList(AutofillId.CREATOR);
                            data.enforceNoDataAvail();
                            setSessionFinished(_arg014, _arg113);
                            break;
                        case 15:
                            IResultReceiver _arg015 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getAugmentedAutofillClient(_arg015);
                            break;
                        case 16:
                            long _arg016 = data.readLong();
                            ComponentName _arg114 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            notifyDisableAutofill(_arg016, _arg114);
                            break;
                        case 17:
                            AutofillId _arg017 = (AutofillId) data.readTypedObject(AutofillId.CREATOR);
                            data.enforceNoDataAvail();
                            requestShowSoftInput(_arg017);
                            break;
                        case 18:
                            int _arg018 = data.readInt();
                            InlineSuggestionsRequest _arg115 = (InlineSuggestionsRequest) data.readTypedObject(InlineSuggestionsRequest.CREATOR);
                            IFillCallback _arg28 = IFillCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            requestFillFromClient(_arg018, _arg115, _arg28);
                            break;
                        case 19:
                            List<AutofillId> _arg019 = data.createTypedArrayList(AutofillId.CREATOR);
                            data.enforceNoDataAvail();
                            notifyFillDialogTriggerIds(_arg019);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IAutoFillManagerClient {
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

            @Override // android.view.autofill.IAutoFillManagerClient
            public void setState(int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManagerClient
            public void autofill(int sessionId, List<AutofillId> ids, List<AutofillValue> values, boolean hideHighlight) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeTypedList(ids, 0);
                    _data.writeTypedList(values, 0);
                    _data.writeBoolean(hideHighlight);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManagerClient
            public void autofillContent(int sessionId, AutofillId id, ClipData content) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeTypedObject(id, 0);
                    _data.writeTypedObject(content, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManagerClient
            public void authenticate(int sessionId, int authenticationId, IntentSender intent, Intent fillInIntent, boolean authenticateInline) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeInt(authenticationId);
                    _data.writeTypedObject(intent, 0);
                    _data.writeTypedObject(fillInIntent, 0);
                    _data.writeBoolean(authenticateInline);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManagerClient
            public void setTrackedViews(int sessionId, AutofillId[] savableIds, boolean saveOnAllViewsInvisible, boolean saveOnFinish, AutofillId[] fillableIds, AutofillId saveTriggerId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeTypedArray(savableIds, 0);
                    _data.writeBoolean(saveOnAllViewsInvisible);
                    _data.writeBoolean(saveOnFinish);
                    _data.writeTypedArray(fillableIds, 0);
                    _data.writeTypedObject(saveTriggerId, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManagerClient
            public void requestShowFillUi(int sessionId, AutofillId id, int width, int height, Rect anchorBounds, IAutofillWindowPresenter presenter) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeTypedObject(id, 0);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    _data.writeTypedObject(anchorBounds, 0);
                    _data.writeStrongInterface(presenter);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManagerClient
            public void requestHideFillUi(int sessionId, AutofillId id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeTypedObject(id, 0);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManagerClient
            public void notifyNoFillUi(int sessionId, AutofillId id, int sessionFinishedState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeTypedObject(id, 0);
                    _data.writeInt(sessionFinishedState);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManagerClient
            public void notifyFillUiShown(int sessionId, AutofillId id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeTypedObject(id, 0);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManagerClient
            public void notifyFillUiHidden(int sessionId, AutofillId id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeTypedObject(id, 0);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManagerClient
            public void dispatchUnhandledKey(int sessionId, AutofillId id, KeyEvent keyEvent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeTypedObject(id, 0);
                    _data.writeTypedObject(keyEvent, 0);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManagerClient
            public void startIntentSender(IntentSender intentSender, Intent intent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(intentSender, 0);
                    _data.writeTypedObject(intent, 0);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManagerClient
            public void setSaveUiState(int sessionId, boolean shown) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeBoolean(shown);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManagerClient
            public void setSessionFinished(int newState, List<AutofillId> autofillableIds) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(newState);
                    _data.writeTypedList(autofillableIds, 0);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManagerClient
            public void getAugmentedAutofillClient(IResultReceiver result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(result);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManagerClient
            public void notifyDisableAutofill(long disableDuration, ComponentName componentName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(disableDuration);
                    _data.writeTypedObject(componentName, 0);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManagerClient
            public void requestShowSoftInput(AutofillId id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(id, 0);
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManagerClient
            public void requestFillFromClient(int id, InlineSuggestionsRequest request, IFillCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.autofill.IAutoFillManagerClient
            public void notifyFillDialogTriggerIds(List<AutofillId> ids) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(ids, 0);
                    this.mRemote.transact(19, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 18;
        }
    }
}
