package android.accessibilityservice;

import android.accessibilityservice.IAccessibilityServiceConnection;
import android.graphics.Region;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.EditorInfo;
import com.android.internal.inputmethod.IAccessibilityInputMethodSession;
import com.android.internal.inputmethod.IAccessibilityInputMethodSessionCallback;
import com.android.internal.inputmethod.IRemoteAccessibilityInputConnection;
/* loaded from: classes.dex */
public interface IAccessibilityServiceClient extends IInterface {
    void bindInput() throws RemoteException;

    void clearAccessibilityCache() throws RemoteException;

    void createImeSession(IAccessibilityInputMethodSessionCallback iAccessibilityInputMethodSessionCallback) throws RemoteException;

    void init(IAccessibilityServiceConnection iAccessibilityServiceConnection, int i, IBinder iBinder) throws RemoteException;

    void onAccessibilityButtonAvailabilityChanged(boolean z) throws RemoteException;

    void onAccessibilityButtonClicked(int i) throws RemoteException;

    void onAccessibilityEvent(AccessibilityEvent accessibilityEvent, boolean z) throws RemoteException;

    void onFingerprintCapturingGesturesChanged(boolean z) throws RemoteException;

    void onFingerprintGesture(int i) throws RemoteException;

    void onGesture(AccessibilityGestureEvent accessibilityGestureEvent) throws RemoteException;

    void onInterrupt() throws RemoteException;

    void onKeyEvent(KeyEvent keyEvent, int i) throws RemoteException;

    void onMagnificationChanged(int i, Region region, MagnificationConfig magnificationConfig) throws RemoteException;

    void onMotionEvent(MotionEvent motionEvent) throws RemoteException;

    void onPerformGestureResult(int i, boolean z) throws RemoteException;

    void onSoftKeyboardShowModeChanged(int i) throws RemoteException;

    void onSystemActionsChanged() throws RemoteException;

    void onTouchStateChanged(int i, int i2) throws RemoteException;

    void setImeSessionEnabled(IAccessibilityInputMethodSession iAccessibilityInputMethodSession, boolean z) throws RemoteException;

    void startInput(IRemoteAccessibilityInputConnection iRemoteAccessibilityInputConnection, EditorInfo editorInfo, boolean z) throws RemoteException;

    void unbindInput() throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IAccessibilityServiceClient {
        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void init(IAccessibilityServiceConnection connection, int connectionId, IBinder windowToken) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onAccessibilityEvent(AccessibilityEvent event, boolean serviceWantsEvent) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onInterrupt() throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onGesture(AccessibilityGestureEvent gestureEvent) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void clearAccessibilityCache() throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onKeyEvent(KeyEvent event, int sequence) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onMagnificationChanged(int displayId, Region region, MagnificationConfig config) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onMotionEvent(MotionEvent event) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onTouchStateChanged(int displayId, int state) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onSoftKeyboardShowModeChanged(int showMode) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onPerformGestureResult(int sequence, boolean completedSuccessfully) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onFingerprintCapturingGesturesChanged(boolean capturing) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onFingerprintGesture(int gesture) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onAccessibilityButtonClicked(int displayId) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onAccessibilityButtonAvailabilityChanged(boolean available) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onSystemActionsChanged() throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void createImeSession(IAccessibilityInputMethodSessionCallback callback) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void setImeSessionEnabled(IAccessibilityInputMethodSession session, boolean enabled) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void bindInput() throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void unbindInput() throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void startInput(IRemoteAccessibilityInputConnection connection, EditorInfo editorInfo, boolean restarting) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IAccessibilityServiceClient {
        public static final String DESCRIPTOR = "android.accessibilityservice.IAccessibilityServiceClient";
        static final int TRANSACTION_bindInput = 19;
        static final int TRANSACTION_clearAccessibilityCache = 5;
        static final int TRANSACTION_createImeSession = 17;
        static final int TRANSACTION_init = 1;
        static final int TRANSACTION_onAccessibilityButtonAvailabilityChanged = 15;
        static final int TRANSACTION_onAccessibilityButtonClicked = 14;
        static final int TRANSACTION_onAccessibilityEvent = 2;
        static final int TRANSACTION_onFingerprintCapturingGesturesChanged = 12;
        static final int TRANSACTION_onFingerprintGesture = 13;
        static final int TRANSACTION_onGesture = 4;
        static final int TRANSACTION_onInterrupt = 3;
        static final int TRANSACTION_onKeyEvent = 6;
        static final int TRANSACTION_onMagnificationChanged = 7;
        static final int TRANSACTION_onMotionEvent = 8;
        static final int TRANSACTION_onPerformGestureResult = 11;
        static final int TRANSACTION_onSoftKeyboardShowModeChanged = 10;
        static final int TRANSACTION_onSystemActionsChanged = 16;
        static final int TRANSACTION_onTouchStateChanged = 9;
        static final int TRANSACTION_setImeSessionEnabled = 18;
        static final int TRANSACTION_startInput = 21;
        static final int TRANSACTION_unbindInput = 20;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAccessibilityServiceClient asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAccessibilityServiceClient)) {
                return (IAccessibilityServiceClient) iin;
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
                    return "init";
                case 2:
                    return "onAccessibilityEvent";
                case 3:
                    return "onInterrupt";
                case 4:
                    return "onGesture";
                case 5:
                    return "clearAccessibilityCache";
                case 6:
                    return "onKeyEvent";
                case 7:
                    return "onMagnificationChanged";
                case 8:
                    return "onMotionEvent";
                case 9:
                    return "onTouchStateChanged";
                case 10:
                    return "onSoftKeyboardShowModeChanged";
                case 11:
                    return "onPerformGestureResult";
                case 12:
                    return "onFingerprintCapturingGesturesChanged";
                case 13:
                    return "onFingerprintGesture";
                case 14:
                    return "onAccessibilityButtonClicked";
                case 15:
                    return "onAccessibilityButtonAvailabilityChanged";
                case 16:
                    return "onSystemActionsChanged";
                case 17:
                    return "createImeSession";
                case 18:
                    return "setImeSessionEnabled";
                case 19:
                    return "bindInput";
                case 20:
                    return "unbindInput";
                case 21:
                    return "startInput";
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
                            IAccessibilityServiceConnection _arg0 = IAccessibilityServiceConnection.Stub.asInterface(data.readStrongBinder());
                            int _arg1 = data.readInt();
                            IBinder _arg2 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            init(_arg0, _arg1, _arg2);
                            break;
                        case 2:
                            AccessibilityEvent _arg02 = (AccessibilityEvent) data.readTypedObject(AccessibilityEvent.CREATOR);
                            boolean _arg12 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onAccessibilityEvent(_arg02, _arg12);
                            break;
                        case 3:
                            onInterrupt();
                            break;
                        case 4:
                            AccessibilityGestureEvent _arg03 = (AccessibilityGestureEvent) data.readTypedObject(AccessibilityGestureEvent.CREATOR);
                            data.enforceNoDataAvail();
                            onGesture(_arg03);
                            break;
                        case 5:
                            clearAccessibilityCache();
                            break;
                        case 6:
                            KeyEvent _arg04 = (KeyEvent) data.readTypedObject(KeyEvent.CREATOR);
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            onKeyEvent(_arg04, _arg13);
                            break;
                        case 7:
                            int _arg05 = data.readInt();
                            Region _arg14 = (Region) data.readTypedObject(Region.CREATOR);
                            MagnificationConfig _arg22 = (MagnificationConfig) data.readTypedObject(MagnificationConfig.CREATOR);
                            data.enforceNoDataAvail();
                            onMagnificationChanged(_arg05, _arg14, _arg22);
                            break;
                        case 8:
                            MotionEvent _arg06 = (MotionEvent) data.readTypedObject(MotionEvent.CREATOR);
                            data.enforceNoDataAvail();
                            onMotionEvent(_arg06);
                            break;
                        case 9:
                            int _arg07 = data.readInt();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            onTouchStateChanged(_arg07, _arg15);
                            break;
                        case 10:
                            int _arg08 = data.readInt();
                            data.enforceNoDataAvail();
                            onSoftKeyboardShowModeChanged(_arg08);
                            break;
                        case 11:
                            int _arg09 = data.readInt();
                            boolean _arg16 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onPerformGestureResult(_arg09, _arg16);
                            break;
                        case 12:
                            boolean _arg010 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onFingerprintCapturingGesturesChanged(_arg010);
                            break;
                        case 13:
                            int _arg011 = data.readInt();
                            data.enforceNoDataAvail();
                            onFingerprintGesture(_arg011);
                            break;
                        case 14:
                            int _arg012 = data.readInt();
                            data.enforceNoDataAvail();
                            onAccessibilityButtonClicked(_arg012);
                            break;
                        case 15:
                            boolean _arg013 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onAccessibilityButtonAvailabilityChanged(_arg013);
                            break;
                        case 16:
                            onSystemActionsChanged();
                            break;
                        case 17:
                            IAccessibilityInputMethodSessionCallback _arg014 = IAccessibilityInputMethodSessionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            createImeSession(_arg014);
                            break;
                        case 18:
                            IAccessibilityInputMethodSession _arg015 = IAccessibilityInputMethodSession.Stub.asInterface(data.readStrongBinder());
                            boolean _arg17 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setImeSessionEnabled(_arg015, _arg17);
                            break;
                        case 19:
                            bindInput();
                            break;
                        case 20:
                            unbindInput();
                            break;
                        case 21:
                            IRemoteAccessibilityInputConnection _arg016 = IRemoteAccessibilityInputConnection.Stub.asInterface(data.readStrongBinder());
                            EditorInfo _arg18 = (EditorInfo) data.readTypedObject(EditorInfo.CREATOR);
                            boolean _arg23 = data.readBoolean();
                            data.enforceNoDataAvail();
                            startInput(_arg016, _arg18, _arg23);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements IAccessibilityServiceClient {
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

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void init(IAccessibilityServiceConnection connection, int connectionId, IBinder windowToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(connection);
                    _data.writeInt(connectionId);
                    _data.writeStrongBinder(windowToken);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void onAccessibilityEvent(AccessibilityEvent event, boolean serviceWantsEvent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(event, 0);
                    _data.writeBoolean(serviceWantsEvent);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void onInterrupt() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void onGesture(AccessibilityGestureEvent gestureEvent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(gestureEvent, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void clearAccessibilityCache() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void onKeyEvent(KeyEvent event, int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(event, 0);
                    _data.writeInt(sequence);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void onMagnificationChanged(int displayId, Region region, MagnificationConfig config) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeTypedObject(region, 0);
                    _data.writeTypedObject(config, 0);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void onMotionEvent(MotionEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(event, 0);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void onTouchStateChanged(int displayId, int state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeInt(state);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void onSoftKeyboardShowModeChanged(int showMode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(showMode);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void onPerformGestureResult(int sequence, boolean completedSuccessfully) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sequence);
                    _data.writeBoolean(completedSuccessfully);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void onFingerprintCapturingGesturesChanged(boolean capturing) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(capturing);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void onFingerprintGesture(int gesture) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(gesture);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void onAccessibilityButtonClicked(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void onAccessibilityButtonAvailabilityChanged(boolean available) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(available);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void onSystemActionsChanged() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void createImeSession(IAccessibilityInputMethodSessionCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void setImeSessionEnabled(IAccessibilityInputMethodSession session, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(session);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void bindInput() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(19, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void unbindInput() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(20, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void startInput(IRemoteAccessibilityInputConnection connection, EditorInfo editorInfo, boolean restarting) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(connection);
                    _data.writeTypedObject(editorInfo, 0);
                    _data.writeBoolean(restarting);
                    this.mRemote.transact(21, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 20;
        }
    }
}
