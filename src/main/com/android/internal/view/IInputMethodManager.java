package com.android.internal.view;

import android.Manifest;
import android.app.ActivityThread;
import android.content.AttributionSource;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.PermissionEnforcer;
import android.p008os.RemoteException;
import android.p008os.ResultReceiver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ImeTracker;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;
import android.window.ImeOnBackInvokedDispatcher;
import com.android.internal.inputmethod.IImeTracker;
import com.android.internal.inputmethod.IInputMethodClient;
import com.android.internal.inputmethod.IRemoteAccessibilityInputConnection;
import com.android.internal.inputmethod.IRemoteInputConnection;
import com.android.internal.inputmethod.InputBindResult;
import java.util.List;
/* loaded from: classes2.dex */
public interface IInputMethodManager extends IInterface {
    boolean acceptStylusHandwritingDelegation(IInputMethodClient iInputMethodClient, String str, String str2) throws RemoteException;

    void addClient(IInputMethodClient iInputMethodClient, IRemoteInputConnection iRemoteInputConnection, int i) throws RemoteException;

    void addVirtualStylusIdForTestSession(IInputMethodClient iInputMethodClient) throws RemoteException;

    InputMethodInfo getCurrentInputMethodInfoAsUser(int i) throws RemoteException;

    InputMethodSubtype getCurrentInputMethodSubtype(int i) throws RemoteException;

    List<InputMethodInfo> getEnabledInputMethodList(int i) throws RemoteException;

    List<InputMethodSubtype> getEnabledInputMethodSubtypeList(String str, boolean z, int i) throws RemoteException;

    IImeTracker getImeTrackerService() throws RemoteException;

    List<InputMethodInfo> getInputMethodList(int i, int i2) throws RemoteException;

    int getInputMethodWindowVisibleHeight(IInputMethodClient iInputMethodClient) throws RemoteException;

    InputMethodSubtype getLastInputMethodSubtype(int i) throws RemoteException;

    boolean hideSoftInput(IInputMethodClient iInputMethodClient, IBinder iBinder, ImeTracker.Token token, int i, ResultReceiver resultReceiver, int i2) throws RemoteException;

    boolean isImeTraceEnabled() throws RemoteException;

    boolean isInputMethodPickerShownForTest() throws RemoteException;

    boolean isStylusHandwritingAvailableAsUser(int i) throws RemoteException;

    void prepareStylusHandwritingDelegation(IInputMethodClient iInputMethodClient, String str, String str2) throws RemoteException;

    void removeImeSurface() throws RemoteException;

    void removeImeSurfaceFromWindowAsync(IBinder iBinder) throws RemoteException;

    void reportPerceptibleAsync(IBinder iBinder, boolean z) throws RemoteException;

    void reportVirtualDisplayGeometryAsync(IInputMethodClient iInputMethodClient, int i, float[] fArr) throws RemoteException;

    void setAdditionalInputMethodSubtypes(String str, InputMethodSubtype[] inputMethodSubtypeArr, int i) throws RemoteException;

    void setExplicitlyEnabledInputMethodSubtypes(String str, int[] iArr, int i) throws RemoteException;

    void setStylusWindowIdleTimeoutForTest(IInputMethodClient iInputMethodClient, long j) throws RemoteException;

    void showInputMethodPickerFromClient(IInputMethodClient iInputMethodClient, int i) throws RemoteException;

    void showInputMethodPickerFromSystem(int i, int i2) throws RemoteException;

    boolean showSoftInput(IInputMethodClient iInputMethodClient, IBinder iBinder, ImeTracker.Token token, int i, int i2, ResultReceiver resultReceiver, int i3) throws RemoteException;

    void startImeTrace() throws RemoteException;

    InputBindResult startInputOrWindowGainedFocus(int i, IInputMethodClient iInputMethodClient, IBinder iBinder, int i2, int i3, int i4, EditorInfo editorInfo, IRemoteInputConnection iRemoteInputConnection, IRemoteAccessibilityInputConnection iRemoteAccessibilityInputConnection, int i5, int i6, ImeOnBackInvokedDispatcher imeOnBackInvokedDispatcher) throws RemoteException;

    void startProtoDump(byte[] bArr, int i, String str) throws RemoteException;

    void startStylusHandwriting(IInputMethodClient iInputMethodClient) throws RemoteException;

    void stopImeTrace() throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IInputMethodManager {
        @Override // com.android.internal.view.IInputMethodManager
        public void addClient(IInputMethodClient client, IRemoteInputConnection inputmethod, int untrustedDisplayId) throws RemoteException {
        }

        @Override // com.android.internal.view.IInputMethodManager
        public InputMethodInfo getCurrentInputMethodInfoAsUser(int userId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.view.IInputMethodManager
        public List<InputMethodInfo> getInputMethodList(int userId, int directBootAwareness) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.view.IInputMethodManager
        public List<InputMethodInfo> getEnabledInputMethodList(int userId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.view.IInputMethodManager
        public List<InputMethodSubtype> getEnabledInputMethodSubtypeList(String imiId, boolean allowsImplicitlyEnabledSubtypes, int userId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.view.IInputMethodManager
        public InputMethodSubtype getLastInputMethodSubtype(int userId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.view.IInputMethodManager
        public boolean showSoftInput(IInputMethodClient client, IBinder windowToken, ImeTracker.Token statsToken, int flags, int lastClickToolType, ResultReceiver resultReceiver, int reason) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.view.IInputMethodManager
        public boolean hideSoftInput(IInputMethodClient client, IBinder windowToken, ImeTracker.Token statsToken, int flags, ResultReceiver resultReceiver, int reason) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.view.IInputMethodManager
        public InputBindResult startInputOrWindowGainedFocus(int startInputReason, IInputMethodClient client, IBinder windowToken, int startInputFlags, int softInputMode, int windowFlags, EditorInfo editorInfo, IRemoteInputConnection inputConnection, IRemoteAccessibilityInputConnection remoteAccessibilityInputConnection, int unverifiedTargetSdkVersion, int userId, ImeOnBackInvokedDispatcher imeDispatcher) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.view.IInputMethodManager
        public void showInputMethodPickerFromClient(IInputMethodClient client, int auxiliarySubtypeMode) throws RemoteException {
        }

        @Override // com.android.internal.view.IInputMethodManager
        public void showInputMethodPickerFromSystem(int auxiliarySubtypeMode, int displayId) throws RemoteException {
        }

        @Override // com.android.internal.view.IInputMethodManager
        public boolean isInputMethodPickerShownForTest() throws RemoteException {
            return false;
        }

        @Override // com.android.internal.view.IInputMethodManager
        public InputMethodSubtype getCurrentInputMethodSubtype(int userId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.view.IInputMethodManager
        public void setAdditionalInputMethodSubtypes(String id, InputMethodSubtype[] subtypes, int userId) throws RemoteException {
        }

        @Override // com.android.internal.view.IInputMethodManager
        public void setExplicitlyEnabledInputMethodSubtypes(String imeId, int[] subtypeHashCodes, int userId) throws RemoteException {
        }

        @Override // com.android.internal.view.IInputMethodManager
        public int getInputMethodWindowVisibleHeight(IInputMethodClient client) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.view.IInputMethodManager
        public void reportVirtualDisplayGeometryAsync(IInputMethodClient parentClient, int childDisplayId, float[] matrixValues) throws RemoteException {
        }

        @Override // com.android.internal.view.IInputMethodManager
        public void reportPerceptibleAsync(IBinder windowToken, boolean perceptible) throws RemoteException {
        }

        @Override // com.android.internal.view.IInputMethodManager
        public void removeImeSurface() throws RemoteException {
        }

        @Override // com.android.internal.view.IInputMethodManager
        public void removeImeSurfaceFromWindowAsync(IBinder windowToken) throws RemoteException {
        }

        @Override // com.android.internal.view.IInputMethodManager
        public void startProtoDump(byte[] protoDump, int source, String where) throws RemoteException {
        }

        @Override // com.android.internal.view.IInputMethodManager
        public boolean isImeTraceEnabled() throws RemoteException {
            return false;
        }

        @Override // com.android.internal.view.IInputMethodManager
        public void startImeTrace() throws RemoteException {
        }

        @Override // com.android.internal.view.IInputMethodManager
        public void stopImeTrace() throws RemoteException {
        }

        @Override // com.android.internal.view.IInputMethodManager
        public void startStylusHandwriting(IInputMethodClient client) throws RemoteException {
        }

        @Override // com.android.internal.view.IInputMethodManager
        public void prepareStylusHandwritingDelegation(IInputMethodClient client, String delegatePackageName, String delegatorPackageName) throws RemoteException {
        }

        @Override // com.android.internal.view.IInputMethodManager
        public boolean acceptStylusHandwritingDelegation(IInputMethodClient client, String delegatePackageName, String delegatorPackageName) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.view.IInputMethodManager
        public boolean isStylusHandwritingAvailableAsUser(int userId) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.view.IInputMethodManager
        public void addVirtualStylusIdForTestSession(IInputMethodClient client) throws RemoteException {
        }

        @Override // com.android.internal.view.IInputMethodManager
        public void setStylusWindowIdleTimeoutForTest(IInputMethodClient client, long timeout) throws RemoteException {
        }

        @Override // com.android.internal.view.IInputMethodManager
        public IImeTracker getImeTrackerService() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IInputMethodManager {
        public static final String DESCRIPTOR = "com.android.internal.view.IInputMethodManager";
        static final int TRANSACTION_acceptStylusHandwritingDelegation = 27;
        static final int TRANSACTION_addClient = 1;
        static final int TRANSACTION_addVirtualStylusIdForTestSession = 29;
        static final int TRANSACTION_getCurrentInputMethodInfoAsUser = 2;
        static final int TRANSACTION_getCurrentInputMethodSubtype = 13;
        static final int TRANSACTION_getEnabledInputMethodList = 4;
        static final int TRANSACTION_getEnabledInputMethodSubtypeList = 5;
        static final int TRANSACTION_getImeTrackerService = 31;
        static final int TRANSACTION_getInputMethodList = 3;
        static final int TRANSACTION_getInputMethodWindowVisibleHeight = 16;
        static final int TRANSACTION_getLastInputMethodSubtype = 6;
        static final int TRANSACTION_hideSoftInput = 8;
        static final int TRANSACTION_isImeTraceEnabled = 22;
        static final int TRANSACTION_isInputMethodPickerShownForTest = 12;
        static final int TRANSACTION_isStylusHandwritingAvailableAsUser = 28;
        static final int TRANSACTION_prepareStylusHandwritingDelegation = 26;
        static final int TRANSACTION_removeImeSurface = 19;
        static final int TRANSACTION_removeImeSurfaceFromWindowAsync = 20;
        static final int TRANSACTION_reportPerceptibleAsync = 18;
        static final int TRANSACTION_reportVirtualDisplayGeometryAsync = 17;
        static final int TRANSACTION_setAdditionalInputMethodSubtypes = 14;
        static final int TRANSACTION_setExplicitlyEnabledInputMethodSubtypes = 15;
        static final int TRANSACTION_setStylusWindowIdleTimeoutForTest = 30;
        static final int TRANSACTION_showInputMethodPickerFromClient = 10;
        static final int TRANSACTION_showInputMethodPickerFromSystem = 11;
        static final int TRANSACTION_showSoftInput = 7;
        static final int TRANSACTION_startImeTrace = 23;
        static final int TRANSACTION_startInputOrWindowGainedFocus = 9;
        static final int TRANSACTION_startProtoDump = 21;
        static final int TRANSACTION_startStylusHandwriting = 25;
        static final int TRANSACTION_stopImeTrace = 24;
        private final PermissionEnforcer mEnforcer;

        public Stub(PermissionEnforcer enforcer) {
            attachInterface(this, DESCRIPTOR);
            if (enforcer == null) {
                throw new IllegalArgumentException("enforcer cannot be null");
            }
            this.mEnforcer = enforcer;
        }

        @Deprecated
        public Stub() {
            this(PermissionEnforcer.fromContext(ActivityThread.currentActivityThread().getSystemContext()));
        }

        public static IInputMethodManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IInputMethodManager)) {
                return (IInputMethodManager) iin;
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
                    return "getCurrentInputMethodInfoAsUser";
                case 3:
                    return "getInputMethodList";
                case 4:
                    return "getEnabledInputMethodList";
                case 5:
                    return "getEnabledInputMethodSubtypeList";
                case 6:
                    return "getLastInputMethodSubtype";
                case 7:
                    return "showSoftInput";
                case 8:
                    return "hideSoftInput";
                case 9:
                    return "startInputOrWindowGainedFocus";
                case 10:
                    return "showInputMethodPickerFromClient";
                case 11:
                    return "showInputMethodPickerFromSystem";
                case 12:
                    return "isInputMethodPickerShownForTest";
                case 13:
                    return "getCurrentInputMethodSubtype";
                case 14:
                    return "setAdditionalInputMethodSubtypes";
                case 15:
                    return "setExplicitlyEnabledInputMethodSubtypes";
                case 16:
                    return "getInputMethodWindowVisibleHeight";
                case 17:
                    return "reportVirtualDisplayGeometryAsync";
                case 18:
                    return "reportPerceptibleAsync";
                case 19:
                    return "removeImeSurface";
                case 20:
                    return "removeImeSurfaceFromWindowAsync";
                case 21:
                    return "startProtoDump";
                case 22:
                    return "isImeTraceEnabled";
                case 23:
                    return "startImeTrace";
                case 24:
                    return "stopImeTrace";
                case 25:
                    return "startStylusHandwriting";
                case 26:
                    return "prepareStylusHandwritingDelegation";
                case 27:
                    return "acceptStylusHandwritingDelegation";
                case 28:
                    return "isStylusHandwritingAvailableAsUser";
                case 29:
                    return "addVirtualStylusIdForTestSession";
                case 30:
                    return "setStylusWindowIdleTimeoutForTest";
                case 31:
                    return "getImeTrackerService";
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
                            IInputMethodClient _arg0 = IInputMethodClient.Stub.asInterface(data.readStrongBinder());
                            IRemoteInputConnection _arg1 = IRemoteInputConnection.Stub.asInterface(data.readStrongBinder());
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            addClient(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            return true;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            InputMethodInfo _result = getCurrentInputMethodInfoAsUser(_arg02);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            return true;
                        case 3:
                            int _arg03 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            List<InputMethodInfo> _result2 = getInputMethodList(_arg03, _arg12);
                            reply.writeNoException();
                            reply.writeTypedList(_result2, 1);
                            return true;
                        case 4:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            List<InputMethodInfo> _result3 = getEnabledInputMethodList(_arg04);
                            reply.writeNoException();
                            reply.writeTypedList(_result3, 1);
                            return true;
                        case 5:
                            String _arg05 = data.readString();
                            boolean _arg13 = data.readBoolean();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            List<InputMethodSubtype> _result4 = getEnabledInputMethodSubtypeList(_arg05, _arg13, _arg22);
                            reply.writeNoException();
                            reply.writeTypedList(_result4, 1);
                            return true;
                        case 6:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            InputMethodSubtype _result5 = getLastInputMethodSubtype(_arg06);
                            reply.writeNoException();
                            reply.writeTypedObject(_result5, 1);
                            return true;
                        case 7:
                            IInputMethodClient _arg07 = IInputMethodClient.Stub.asInterface(data.readStrongBinder());
                            IBinder _arg14 = data.readStrongBinder();
                            ImeTracker.Token _arg23 = (ImeTracker.Token) data.readTypedObject(ImeTracker.Token.CREATOR);
                            int _arg3 = data.readInt();
                            int _arg4 = data.readInt();
                            ResultReceiver _arg5 = (ResultReceiver) data.readTypedObject(ResultReceiver.CREATOR);
                            int _arg6 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result6 = showSoftInput(_arg07, _arg14, _arg23, _arg3, _arg4, _arg5, _arg6);
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            return true;
                        case 8:
                            IInputMethodClient _arg08 = IInputMethodClient.Stub.asInterface(data.readStrongBinder());
                            IBinder _arg15 = data.readStrongBinder();
                            ImeTracker.Token _arg24 = (ImeTracker.Token) data.readTypedObject(ImeTracker.Token.CREATOR);
                            int _arg32 = data.readInt();
                            ResultReceiver _arg42 = (ResultReceiver) data.readTypedObject(ResultReceiver.CREATOR);
                            int _arg52 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result7 = hideSoftInput(_arg08, _arg15, _arg24, _arg32, _arg42, _arg52);
                            reply.writeNoException();
                            reply.writeBoolean(_result7);
                            return true;
                        case 9:
                            int _arg09 = data.readInt();
                            IInputMethodClient _arg16 = IInputMethodClient.Stub.asInterface(data.readStrongBinder());
                            IBinder _arg25 = data.readStrongBinder();
                            int _arg33 = data.readInt();
                            int _arg43 = data.readInt();
                            int _arg53 = data.readInt();
                            EditorInfo _arg62 = (EditorInfo) data.readTypedObject(EditorInfo.CREATOR);
                            IRemoteInputConnection _arg7 = IRemoteInputConnection.Stub.asInterface(data.readStrongBinder());
                            IRemoteAccessibilityInputConnection _arg8 = IRemoteAccessibilityInputConnection.Stub.asInterface(data.readStrongBinder());
                            int _arg9 = data.readInt();
                            int _arg10 = data.readInt();
                            ImeOnBackInvokedDispatcher _arg11 = (ImeOnBackInvokedDispatcher) data.readTypedObject(ImeOnBackInvokedDispatcher.CREATOR);
                            data.enforceNoDataAvail();
                            InputBindResult _result8 = startInputOrWindowGainedFocus(_arg09, _arg16, _arg25, _arg33, _arg43, _arg53, _arg62, _arg7, _arg8, _arg9, _arg10, _arg11);
                            reply.writeNoException();
                            reply.writeTypedObject(_result8, 1);
                            return true;
                        case 10:
                            IInputMethodClient _arg010 = IInputMethodClient.Stub.asInterface(data.readStrongBinder());
                            int _arg17 = data.readInt();
                            data.enforceNoDataAvail();
                            showInputMethodPickerFromClient(_arg010, _arg17);
                            reply.writeNoException();
                            return true;
                        case 11:
                            int _arg011 = data.readInt();
                            int _arg18 = data.readInt();
                            data.enforceNoDataAvail();
                            showInputMethodPickerFromSystem(_arg011, _arg18);
                            reply.writeNoException();
                            return true;
                        case 12:
                            boolean _result9 = isInputMethodPickerShownForTest();
                            reply.writeNoException();
                            reply.writeBoolean(_result9);
                            return true;
                        case 13:
                            int _arg012 = data.readInt();
                            data.enforceNoDataAvail();
                            InputMethodSubtype _result10 = getCurrentInputMethodSubtype(_arg012);
                            reply.writeNoException();
                            reply.writeTypedObject(_result10, 1);
                            return true;
                        case 14:
                            String _arg013 = data.readString();
                            InputMethodSubtype[] _arg19 = (InputMethodSubtype[]) data.createTypedArray(InputMethodSubtype.CREATOR);
                            int _arg26 = data.readInt();
                            data.enforceNoDataAvail();
                            setAdditionalInputMethodSubtypes(_arg013, _arg19, _arg26);
                            reply.writeNoException();
                            return true;
                        case 15:
                            String _arg014 = data.readString();
                            int[] _arg110 = data.createIntArray();
                            int _arg27 = data.readInt();
                            data.enforceNoDataAvail();
                            setExplicitlyEnabledInputMethodSubtypes(_arg014, _arg110, _arg27);
                            reply.writeNoException();
                            return true;
                        case 16:
                            IInputMethodClient _arg015 = IInputMethodClient.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result11 = getInputMethodWindowVisibleHeight(_arg015);
                            reply.writeNoException();
                            reply.writeInt(_result11);
                            return true;
                        case 17:
                            IBinder _arg016 = data.readStrongBinder();
                            IInputMethodClient _arg017 = IInputMethodClient.Stub.asInterface(_arg016);
                            int _arg111 = data.readInt();
                            float[] _arg28 = data.createFloatArray();
                            data.enforceNoDataAvail();
                            reportVirtualDisplayGeometryAsync(_arg017, _arg111, _arg28);
                            return true;
                        case 18:
                            IBinder _arg018 = data.readStrongBinder();
                            boolean _arg112 = data.readBoolean();
                            data.enforceNoDataAvail();
                            reportPerceptibleAsync(_arg018, _arg112);
                            return true;
                        case 19:
                            removeImeSurface();
                            reply.writeNoException();
                            return true;
                        case 20:
                            IBinder _arg019 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            removeImeSurfaceFromWindowAsync(_arg019);
                            return true;
                        case 21:
                            byte[] _arg020 = data.createByteArray();
                            int _arg113 = data.readInt();
                            String _arg29 = data.readString();
                            data.enforceNoDataAvail();
                            startProtoDump(_arg020, _arg113, _arg29);
                            reply.writeNoException();
                            return true;
                        case 22:
                            boolean _result12 = isImeTraceEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result12);
                            return true;
                        case 23:
                            startImeTrace();
                            reply.writeNoException();
                            return true;
                        case 24:
                            stopImeTrace();
                            reply.writeNoException();
                            return true;
                        case 25:
                            IInputMethodClient _arg021 = IInputMethodClient.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            startStylusHandwriting(_arg021);
                            reply.writeNoException();
                            return true;
                        case 26:
                            IInputMethodClient _arg022 = IInputMethodClient.Stub.asInterface(data.readStrongBinder());
                            String _arg114 = data.readString();
                            String _arg210 = data.readString();
                            data.enforceNoDataAvail();
                            prepareStylusHandwritingDelegation(_arg022, _arg114, _arg210);
                            reply.writeNoException();
                            return true;
                        case 27:
                            IInputMethodClient _arg023 = IInputMethodClient.Stub.asInterface(data.readStrongBinder());
                            String _arg115 = data.readString();
                            String _arg211 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result13 = acceptStylusHandwritingDelegation(_arg023, _arg115, _arg211);
                            reply.writeNoException();
                            reply.writeBoolean(_result13);
                            return true;
                        case 28:
                            int _arg024 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result14 = isStylusHandwritingAvailableAsUser(_arg024);
                            reply.writeNoException();
                            reply.writeBoolean(_result14);
                            return true;
                        case 29:
                            IInputMethodClient _arg025 = IInputMethodClient.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addVirtualStylusIdForTestSession(_arg025);
                            reply.writeNoException();
                            return true;
                        case 30:
                            IInputMethodClient _arg026 = IInputMethodClient.Stub.asInterface(data.readStrongBinder());
                            long _arg116 = data.readLong();
                            data.enforceNoDataAvail();
                            setStylusWindowIdleTimeoutForTest(_arg026, _arg116);
                            reply.writeNoException();
                            return true;
                        case 31:
                            IImeTracker _result15 = getImeTrackerService();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result15);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IInputMethodManager {
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

            @Override // com.android.internal.view.IInputMethodManager
            public void addClient(IInputMethodClient client, IRemoteInputConnection inputmethod, int untrustedDisplayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeStrongInterface(inputmethod);
                    _data.writeInt(untrustedDisplayId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public InputMethodInfo getCurrentInputMethodInfoAsUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    InputMethodInfo _result = (InputMethodInfo) _reply.readTypedObject(InputMethodInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public List<InputMethodInfo> getInputMethodList(int userId, int directBootAwareness) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeInt(directBootAwareness);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    List<InputMethodInfo> _result = _reply.createTypedArrayList(InputMethodInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public List<InputMethodInfo> getEnabledInputMethodList(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    List<InputMethodInfo> _result = _reply.createTypedArrayList(InputMethodInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public List<InputMethodSubtype> getEnabledInputMethodSubtypeList(String imiId, boolean allowsImplicitlyEnabledSubtypes, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(imiId);
                    _data.writeBoolean(allowsImplicitlyEnabledSubtypes);
                    _data.writeInt(userId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    List<InputMethodSubtype> _result = _reply.createTypedArrayList(InputMethodSubtype.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public InputMethodSubtype getLastInputMethodSubtype(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    InputMethodSubtype _result = (InputMethodSubtype) _reply.readTypedObject(InputMethodSubtype.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public boolean showSoftInput(IInputMethodClient client, IBinder windowToken, ImeTracker.Token statsToken, int flags, int lastClickToolType, ResultReceiver resultReceiver, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeStrongBinder(windowToken);
                    _data.writeTypedObject(statsToken, 0);
                    _data.writeInt(flags);
                    _data.writeInt(lastClickToolType);
                    _data.writeTypedObject(resultReceiver, 0);
                    _data.writeInt(reason);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public boolean hideSoftInput(IInputMethodClient client, IBinder windowToken, ImeTracker.Token statsToken, int flags, ResultReceiver resultReceiver, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeStrongBinder(windowToken);
                    _data.writeTypedObject(statsToken, 0);
                    _data.writeInt(flags);
                    _data.writeTypedObject(resultReceiver, 0);
                    _data.writeInt(reason);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public InputBindResult startInputOrWindowGainedFocus(int startInputReason, IInputMethodClient client, IBinder windowToken, int startInputFlags, int softInputMode, int windowFlags, EditorInfo editorInfo, IRemoteInputConnection inputConnection, IRemoteAccessibilityInputConnection remoteAccessibilityInputConnection, int unverifiedTargetSdkVersion, int userId, ImeOnBackInvokedDispatcher imeDispatcher) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(startInputReason);
                    _data.writeStrongInterface(client);
                    try {
                        _data.writeStrongBinder(windowToken);
                    } catch (Throwable th) {
                        th = th;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
                try {
                    _data.writeInt(startInputFlags);
                    try {
                        _data.writeInt(softInputMode);
                        try {
                            _data.writeInt(windowFlags);
                            try {
                                _data.writeTypedObject(editorInfo, 0);
                            } catch (Throwable th3) {
                                th = th3;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th4) {
                            th = th4;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeStrongInterface(inputConnection);
                        try {
                            _data.writeStrongInterface(remoteAccessibilityInputConnection);
                            try {
                                _data.writeInt(unverifiedTargetSdkVersion);
                                try {
                                    _data.writeInt(userId);
                                    try {
                                        _data.writeTypedObject(imeDispatcher, 0);
                                        try {
                                            this.mRemote.transact(9, _data, _reply, 0);
                                            _reply.readException();
                                            InputBindResult _result = (InputBindResult) _reply.readTypedObject(InputBindResult.CREATOR);
                                            _reply.recycle();
                                            _data.recycle();
                                            return _result;
                                        } catch (Throwable th6) {
                                            th = th6;
                                            _reply.recycle();
                                            _data.recycle();
                                            throw th;
                                        }
                                    } catch (Throwable th7) {
                                        th = th7;
                                    }
                                } catch (Throwable th8) {
                                    th = th8;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th9) {
                                th = th9;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th10) {
                            th = th10;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th11) {
                        th = th11;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th12) {
                    th = th12;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public void showInputMethodPickerFromClient(IInputMethodClient client, int auxiliarySubtypeMode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeInt(auxiliarySubtypeMode);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public void showInputMethodPickerFromSystem(int auxiliarySubtypeMode, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(auxiliarySubtypeMode);
                    _data.writeInt(displayId);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public boolean isInputMethodPickerShownForTest() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public InputMethodSubtype getCurrentInputMethodSubtype(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    InputMethodSubtype _result = (InputMethodSubtype) _reply.readTypedObject(InputMethodSubtype.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public void setAdditionalInputMethodSubtypes(String id, InputMethodSubtype[] subtypes, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(id);
                    _data.writeTypedArray(subtypes, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public void setExplicitlyEnabledInputMethodSubtypes(String imeId, int[] subtypeHashCodes, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(imeId);
                    _data.writeIntArray(subtypeHashCodes);
                    _data.writeInt(userId);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public int getInputMethodWindowVisibleHeight(IInputMethodClient client) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public void reportVirtualDisplayGeometryAsync(IInputMethodClient parentClient, int childDisplayId, float[] matrixValues) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(parentClient);
                    _data.writeInt(childDisplayId);
                    _data.writeFloatArray(matrixValues);
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public void reportPerceptibleAsync(IBinder windowToken, boolean perceptible) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(windowToken);
                    _data.writeBoolean(perceptible);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public void removeImeSurface() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public void removeImeSurfaceFromWindowAsync(IBinder windowToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(windowToken);
                    this.mRemote.transact(20, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public void startProtoDump(byte[] protoDump, int source, String where) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(protoDump);
                    _data.writeInt(source);
                    _data.writeString(where);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public boolean isImeTraceEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public void startImeTrace() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public void stopImeTrace() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public void startStylusHandwriting(IInputMethodClient client) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public void prepareStylusHandwritingDelegation(IInputMethodClient client, String delegatePackageName, String delegatorPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeString(delegatePackageName);
                    _data.writeString(delegatorPackageName);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public boolean acceptStylusHandwritingDelegation(IInputMethodClient client, String delegatePackageName, String delegatorPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeString(delegatePackageName);
                    _data.writeString(delegatorPackageName);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public boolean isStylusHandwritingAvailableAsUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public void addVirtualStylusIdForTestSession(IInputMethodClient client) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public void setStylusWindowIdleTimeoutForTest(IInputMethodClient client, long timeout) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeLong(timeout);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.view.IInputMethodManager
            public IImeTracker getImeTrackerService() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    IImeTracker _result = IImeTracker.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        protected void showInputMethodPickerFromSystem_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.WRITE_SECURE_SETTINGS, source);
        }

        protected void isInputMethodPickerShownForTest_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.TEST_INPUT_METHOD, source);
        }

        protected void removeImeSurface_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.INTERNAL_SYSTEM_WINDOW, source);
        }

        protected void startImeTrace_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CONTROL_UI_TRACING, source);
        }

        protected void stopImeTrace_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CONTROL_UI_TRACING, source);
        }

        protected void addVirtualStylusIdForTestSession_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.TEST_INPUT_METHOD, source);
        }

        protected void setStylusWindowIdleTimeoutForTest_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.TEST_INPUT_METHOD, source);
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 30;
        }
    }
}
