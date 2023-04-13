package com.android.internal.inputmethod;

import android.p008os.BadParcelableException;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.p008os.ResultReceiver;
import android.view.InputChannel;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ImeTracker;
import android.view.inputmethod.InputBinding;
import android.view.inputmethod.InputMethodSubtype;
import android.window.ImeOnBackInvokedDispatcher;
import com.android.internal.inputmethod.IInlineSuggestionsRequestCallback;
import com.android.internal.inputmethod.IInputMethodPrivilegedOperations;
import com.android.internal.inputmethod.IInputMethodSession;
import com.android.internal.inputmethod.IInputMethodSessionCallback;
import com.android.internal.inputmethod.IRemoteInputConnection;
import java.util.List;
/* loaded from: classes4.dex */
public interface IInputMethod extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.inputmethod.IInputMethod";

    void bindInput(InputBinding inputBinding) throws RemoteException;

    void canStartStylusHandwriting(int i) throws RemoteException;

    void changeInputMethodSubtype(InputMethodSubtype inputMethodSubtype) throws RemoteException;

    void createSession(InputChannel inputChannel, IInputMethodSessionCallback iInputMethodSessionCallback) throws RemoteException;

    void finishStylusHandwriting() throws RemoteException;

    void hideSoftInput(IBinder iBinder, ImeTracker.Token token, int i, ResultReceiver resultReceiver) throws RemoteException;

    void initInkWindow() throws RemoteException;

    void initializeInternal(InitParams initParams) throws RemoteException;

    void onCreateInlineSuggestionsRequest(InlineSuggestionsRequestInfo inlineSuggestionsRequestInfo, IInlineSuggestionsRequestCallback iInlineSuggestionsRequestCallback) throws RemoteException;

    void onNavButtonFlagsChanged(int i) throws RemoteException;

    void removeStylusHandwritingWindow() throws RemoteException;

    void setSessionEnabled(IInputMethodSession iInputMethodSession, boolean z) throws RemoteException;

    void setStylusWindowIdleTimeoutForTest(long j) throws RemoteException;

    void showSoftInput(IBinder iBinder, ImeTracker.Token token, int i, ResultReceiver resultReceiver) throws RemoteException;

    void startInput(StartInputParams startInputParams) throws RemoteException;

    void startStylusHandwriting(int i, InputChannel inputChannel, List<MotionEvent> list) throws RemoteException;

    void unbindInput() throws RemoteException;

    void updateEditorToolType(int i) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IInputMethod {
        @Override // com.android.internal.inputmethod.IInputMethod
        public void initializeInternal(InitParams params) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethod
        public void onCreateInlineSuggestionsRequest(InlineSuggestionsRequestInfo requestInfo, IInlineSuggestionsRequestCallback cb) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethod
        public void bindInput(InputBinding binding) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethod
        public void unbindInput() throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethod
        public void startInput(StartInputParams params) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethod
        public void onNavButtonFlagsChanged(int navButtonFlags) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethod
        public void createSession(InputChannel channel, IInputMethodSessionCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethod
        public void setSessionEnabled(IInputMethodSession session, boolean enabled) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethod
        public void showSoftInput(IBinder showInputToken, ImeTracker.Token statsToken, int flags, ResultReceiver resultReceiver) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethod
        public void hideSoftInput(IBinder hideInputToken, ImeTracker.Token statsToken, int flags, ResultReceiver resultReceiver) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethod
        public void updateEditorToolType(int toolType) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethod
        public void changeInputMethodSubtype(InputMethodSubtype subtype) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethod
        public void canStartStylusHandwriting(int requestId) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethod
        public void startStylusHandwriting(int requestId, InputChannel channel, List<MotionEvent> events) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethod
        public void initInkWindow() throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethod
        public void finishStylusHandwriting() throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethod
        public void removeStylusHandwritingWindow() throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethod
        public void setStylusWindowIdleTimeoutForTest(long timeout) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IInputMethod {
        static final int TRANSACTION_bindInput = 3;
        static final int TRANSACTION_canStartStylusHandwriting = 13;
        static final int TRANSACTION_changeInputMethodSubtype = 12;
        static final int TRANSACTION_createSession = 7;
        static final int TRANSACTION_finishStylusHandwriting = 16;
        static final int TRANSACTION_hideSoftInput = 10;
        static final int TRANSACTION_initInkWindow = 15;
        static final int TRANSACTION_initializeInternal = 1;
        static final int TRANSACTION_onCreateInlineSuggestionsRequest = 2;
        static final int TRANSACTION_onNavButtonFlagsChanged = 6;
        static final int TRANSACTION_removeStylusHandwritingWindow = 17;
        static final int TRANSACTION_setSessionEnabled = 8;
        static final int TRANSACTION_setStylusWindowIdleTimeoutForTest = 18;
        static final int TRANSACTION_showSoftInput = 9;
        static final int TRANSACTION_startInput = 5;
        static final int TRANSACTION_startStylusHandwriting = 14;
        static final int TRANSACTION_unbindInput = 4;
        static final int TRANSACTION_updateEditorToolType = 11;

        public Stub() {
            attachInterface(this, IInputMethod.DESCRIPTOR);
        }

        public static IInputMethod asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IInputMethod.DESCRIPTOR);
            if (iin != null && (iin instanceof IInputMethod)) {
                return (IInputMethod) iin;
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
                    return "initializeInternal";
                case 2:
                    return "onCreateInlineSuggestionsRequest";
                case 3:
                    return "bindInput";
                case 4:
                    return "unbindInput";
                case 5:
                    return "startInput";
                case 6:
                    return "onNavButtonFlagsChanged";
                case 7:
                    return "createSession";
                case 8:
                    return "setSessionEnabled";
                case 9:
                    return "showSoftInput";
                case 10:
                    return "hideSoftInput";
                case 11:
                    return "updateEditorToolType";
                case 12:
                    return "changeInputMethodSubtype";
                case 13:
                    return "canStartStylusHandwriting";
                case 14:
                    return "startStylusHandwriting";
                case 15:
                    return "initInkWindow";
                case 16:
                    return "finishStylusHandwriting";
                case 17:
                    return "removeStylusHandwritingWindow";
                case 18:
                    return "setStylusWindowIdleTimeoutForTest";
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
                data.enforceInterface(IInputMethod.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IInputMethod.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            InitParams _arg0 = (InitParams) data.readTypedObject(InitParams.CREATOR);
                            data.enforceNoDataAvail();
                            initializeInternal(_arg0);
                            break;
                        case 2:
                            InlineSuggestionsRequestInfo _arg02 = (InlineSuggestionsRequestInfo) data.readTypedObject(InlineSuggestionsRequestInfo.CREATOR);
                            IInlineSuggestionsRequestCallback _arg1 = IInlineSuggestionsRequestCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onCreateInlineSuggestionsRequest(_arg02, _arg1);
                            break;
                        case 3:
                            InputBinding _arg03 = (InputBinding) data.readTypedObject(InputBinding.CREATOR);
                            data.enforceNoDataAvail();
                            bindInput(_arg03);
                            break;
                        case 4:
                            unbindInput();
                            break;
                        case 5:
                            StartInputParams _arg04 = (StartInputParams) data.readTypedObject(StartInputParams.CREATOR);
                            data.enforceNoDataAvail();
                            startInput(_arg04);
                            break;
                        case 6:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            onNavButtonFlagsChanged(_arg05);
                            break;
                        case 7:
                            InputChannel _arg06 = (InputChannel) data.readTypedObject(InputChannel.CREATOR);
                            IInputMethodSessionCallback _arg12 = IInputMethodSessionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            createSession(_arg06, _arg12);
                            break;
                        case 8:
                            IBinder _arg07 = data.readStrongBinder();
                            IInputMethodSession _arg08 = IInputMethodSession.Stub.asInterface(_arg07);
                            boolean _arg13 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setSessionEnabled(_arg08, _arg13);
                            break;
                        case 9:
                            IBinder _arg09 = data.readStrongBinder();
                            ImeTracker.Token _arg14 = (ImeTracker.Token) data.readTypedObject(ImeTracker.Token.CREATOR);
                            int _arg2 = data.readInt();
                            ResultReceiver _arg3 = (ResultReceiver) data.readTypedObject(ResultReceiver.CREATOR);
                            data.enforceNoDataAvail();
                            showSoftInput(_arg09, _arg14, _arg2, _arg3);
                            break;
                        case 10:
                            IBinder _arg010 = data.readStrongBinder();
                            ImeTracker.Token _arg15 = (ImeTracker.Token) data.readTypedObject(ImeTracker.Token.CREATOR);
                            int _arg22 = data.readInt();
                            ResultReceiver _arg32 = (ResultReceiver) data.readTypedObject(ResultReceiver.CREATOR);
                            data.enforceNoDataAvail();
                            hideSoftInput(_arg010, _arg15, _arg22, _arg32);
                            break;
                        case 11:
                            int _arg011 = data.readInt();
                            data.enforceNoDataAvail();
                            updateEditorToolType(_arg011);
                            break;
                        case 12:
                            InputMethodSubtype _arg012 = (InputMethodSubtype) data.readTypedObject(InputMethodSubtype.CREATOR);
                            data.enforceNoDataAvail();
                            changeInputMethodSubtype(_arg012);
                            break;
                        case 13:
                            int _arg013 = data.readInt();
                            data.enforceNoDataAvail();
                            canStartStylusHandwriting(_arg013);
                            break;
                        case 14:
                            int _arg014 = data.readInt();
                            InputChannel _arg16 = (InputChannel) data.readTypedObject(InputChannel.CREATOR);
                            List<MotionEvent> _arg23 = data.createTypedArrayList(MotionEvent.CREATOR);
                            data.enforceNoDataAvail();
                            startStylusHandwriting(_arg014, _arg16, _arg23);
                            break;
                        case 15:
                            initInkWindow();
                            break;
                        case 16:
                            finishStylusHandwriting();
                            break;
                        case 17:
                            removeStylusHandwritingWindow();
                            break;
                        case 18:
                            long _arg015 = data.readLong();
                            data.enforceNoDataAvail();
                            setStylusWindowIdleTimeoutForTest(_arg015);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IInputMethod {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IInputMethod.DESCRIPTOR;
            }

            @Override // com.android.internal.inputmethod.IInputMethod
            public void initializeInternal(InitParams params) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethod.DESCRIPTOR);
                    _data.writeTypedObject(params, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethod
            public void onCreateInlineSuggestionsRequest(InlineSuggestionsRequestInfo requestInfo, IInlineSuggestionsRequestCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethod.DESCRIPTOR);
                    _data.writeTypedObject(requestInfo, 0);
                    _data.writeStrongInterface(cb);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethod
            public void bindInput(InputBinding binding) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethod.DESCRIPTOR);
                    _data.writeTypedObject(binding, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethod
            public void unbindInput() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethod.DESCRIPTOR);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethod
            public void startInput(StartInputParams params) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethod.DESCRIPTOR);
                    _data.writeTypedObject(params, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethod
            public void onNavButtonFlagsChanged(int navButtonFlags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethod.DESCRIPTOR);
                    _data.writeInt(navButtonFlags);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethod
            public void createSession(InputChannel channel, IInputMethodSessionCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethod.DESCRIPTOR);
                    _data.writeTypedObject(channel, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethod
            public void setSessionEnabled(IInputMethodSession session, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethod.DESCRIPTOR);
                    _data.writeStrongInterface(session);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethod
            public void showSoftInput(IBinder showInputToken, ImeTracker.Token statsToken, int flags, ResultReceiver resultReceiver) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethod.DESCRIPTOR);
                    _data.writeStrongBinder(showInputToken);
                    _data.writeTypedObject(statsToken, 0);
                    _data.writeInt(flags);
                    _data.writeTypedObject(resultReceiver, 0);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethod
            public void hideSoftInput(IBinder hideInputToken, ImeTracker.Token statsToken, int flags, ResultReceiver resultReceiver) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethod.DESCRIPTOR);
                    _data.writeStrongBinder(hideInputToken);
                    _data.writeTypedObject(statsToken, 0);
                    _data.writeInt(flags);
                    _data.writeTypedObject(resultReceiver, 0);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethod
            public void updateEditorToolType(int toolType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethod.DESCRIPTOR);
                    _data.writeInt(toolType);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethod
            public void changeInputMethodSubtype(InputMethodSubtype subtype) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethod.DESCRIPTOR);
                    _data.writeTypedObject(subtype, 0);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethod
            public void canStartStylusHandwriting(int requestId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethod.DESCRIPTOR);
                    _data.writeInt(requestId);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethod
            public void startStylusHandwriting(int requestId, InputChannel channel, List<MotionEvent> events) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethod.DESCRIPTOR);
                    _data.writeInt(requestId);
                    _data.writeTypedObject(channel, 0);
                    _data.writeTypedList(events, 0);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethod
            public void initInkWindow() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethod.DESCRIPTOR);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethod
            public void finishStylusHandwriting() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethod.DESCRIPTOR);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethod
            public void removeStylusHandwritingWindow() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethod.DESCRIPTOR);
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethod
            public void setStylusWindowIdleTimeoutForTest(long timeout) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethod.DESCRIPTOR);
                    _data.writeLong(timeout);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 17;
        }
    }

    /* loaded from: classes4.dex */
    public static class InitParams implements Parcelable {
        public static final Parcelable.Creator<InitParams> CREATOR = new Parcelable.Creator<InitParams>() { // from class: com.android.internal.inputmethod.IInputMethod.InitParams.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public InitParams createFromParcel(Parcel _aidl_source) {
                InitParams _aidl_out = new InitParams();
                _aidl_out.readFromParcel(_aidl_source);
                return _aidl_out;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public InitParams[] newArray(int _aidl_size) {
                return new InitParams[_aidl_size];
            }
        };
        public int navigationBarFlags = 0;
        public IInputMethodPrivilegedOperations privilegedOperations;
        public IBinder token;

        @Override // android.p008os.Parcelable
        public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
            int _aidl_start_pos = _aidl_parcel.dataPosition();
            _aidl_parcel.writeInt(0);
            _aidl_parcel.writeStrongBinder(this.token);
            _aidl_parcel.writeStrongInterface(this.privilegedOperations);
            _aidl_parcel.writeInt(this.navigationBarFlags);
            int _aidl_end_pos = _aidl_parcel.dataPosition();
            _aidl_parcel.setDataPosition(_aidl_start_pos);
            _aidl_parcel.writeInt(_aidl_end_pos - _aidl_start_pos);
            _aidl_parcel.setDataPosition(_aidl_end_pos);
        }

        public final void readFromParcel(Parcel _aidl_parcel) {
            int _aidl_start_pos = _aidl_parcel.dataPosition();
            int _aidl_parcelable_size = _aidl_parcel.readInt();
            try {
                if (_aidl_parcelable_size < 4) {
                    throw new BadParcelableException("Parcelable too small");
                }
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.token = _aidl_parcel.readStrongBinder();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.privilegedOperations = IInputMethodPrivilegedOperations.Stub.asInterface(_aidl_parcel.readStrongBinder());
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.navigationBarFlags = _aidl_parcel.readInt();
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
            } catch (Throwable th) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                throw th;
            }
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }
    }

    /* loaded from: classes4.dex */
    public static class StartInputParams implements Parcelable {
        public static final Parcelable.Creator<StartInputParams> CREATOR = new Parcelable.Creator<StartInputParams>() { // from class: com.android.internal.inputmethod.IInputMethod.StartInputParams.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public StartInputParams createFromParcel(Parcel _aidl_source) {
                StartInputParams _aidl_out = new StartInputParams();
                _aidl_out.readFromParcel(_aidl_source);
                return _aidl_out;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public StartInputParams[] newArray(int _aidl_size) {
                return new StartInputParams[_aidl_size];
            }
        };
        public EditorInfo editorInfo;
        public ImeOnBackInvokedDispatcher imeDispatcher;
        public IRemoteInputConnection remoteInputConnection;
        public IBinder startInputToken;
        public boolean restarting = false;
        public int navigationBarFlags = 0;

        @Override // android.p008os.Parcelable
        public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
            int _aidl_start_pos = _aidl_parcel.dataPosition();
            _aidl_parcel.writeInt(0);
            _aidl_parcel.writeStrongBinder(this.startInputToken);
            _aidl_parcel.writeStrongInterface(this.remoteInputConnection);
            _aidl_parcel.writeTypedObject(this.editorInfo, _aidl_flag);
            _aidl_parcel.writeBoolean(this.restarting);
            _aidl_parcel.writeInt(this.navigationBarFlags);
            _aidl_parcel.writeTypedObject(this.imeDispatcher, _aidl_flag);
            int _aidl_end_pos = _aidl_parcel.dataPosition();
            _aidl_parcel.setDataPosition(_aidl_start_pos);
            _aidl_parcel.writeInt(_aidl_end_pos - _aidl_start_pos);
            _aidl_parcel.setDataPosition(_aidl_end_pos);
        }

        public final void readFromParcel(Parcel _aidl_parcel) {
            int _aidl_start_pos = _aidl_parcel.dataPosition();
            int _aidl_parcelable_size = _aidl_parcel.readInt();
            try {
                if (_aidl_parcelable_size < 4) {
                    throw new BadParcelableException("Parcelable too small");
                }
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.startInputToken = _aidl_parcel.readStrongBinder();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.remoteInputConnection = IRemoteInputConnection.Stub.asInterface(_aidl_parcel.readStrongBinder());
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.editorInfo = (EditorInfo) _aidl_parcel.readTypedObject(EditorInfo.CREATOR);
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.restarting = _aidl_parcel.readBoolean();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.navigationBarFlags = _aidl_parcel.readInt();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.imeDispatcher = (ImeOnBackInvokedDispatcher) _aidl_parcel.readTypedObject(ImeOnBackInvokedDispatcher.CREATOR);
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
            } catch (Throwable th) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                throw th;
            }
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            int _mask = 0 | describeContents(this.editorInfo);
            return _mask | describeContents(this.imeDispatcher);
        }

        private int describeContents(Object _v) {
            if (_v == null || !(_v instanceof Parcelable)) {
                return 0;
            }
            return ((Parcelable) _v).describeContents();
        }
    }
}
