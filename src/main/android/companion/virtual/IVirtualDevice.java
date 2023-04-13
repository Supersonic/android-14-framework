package android.companion.virtual;

import android.Manifest;
import android.app.ActivityThread;
import android.app.PendingIntent;
import android.companion.virtual.IVirtualDeviceIntentInterceptor;
import android.companion.virtual.audio.IAudioConfigChangedCallback;
import android.companion.virtual.audio.IAudioRoutingCallback;
import android.companion.virtual.sensor.VirtualSensor;
import android.companion.virtual.sensor.VirtualSensorEvent;
import android.content.AttributionSource;
import android.content.IntentFilter;
import android.graphics.PointF;
import android.hardware.input.VirtualDpadConfig;
import android.hardware.input.VirtualKeyEvent;
import android.hardware.input.VirtualKeyboardConfig;
import android.hardware.input.VirtualMouseButtonEvent;
import android.hardware.input.VirtualMouseConfig;
import android.hardware.input.VirtualMouseRelativeEvent;
import android.hardware.input.VirtualMouseScrollEvent;
import android.hardware.input.VirtualNavigationTouchpadConfig;
import android.hardware.input.VirtualTouchEvent;
import android.hardware.input.VirtualTouchscreenConfig;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.PermissionEnforcer;
import android.p008os.RemoteException;
import android.p008os.ResultReceiver;
import java.util.List;
/* loaded from: classes.dex */
public interface IVirtualDevice extends IInterface {
    public static final String DESCRIPTOR = "android.companion.virtual.IVirtualDevice";

    void close() throws RemoteException;

    void createVirtualDpad(VirtualDpadConfig virtualDpadConfig, IBinder iBinder) throws RemoteException;

    void createVirtualKeyboard(VirtualKeyboardConfig virtualKeyboardConfig, IBinder iBinder) throws RemoteException;

    void createVirtualMouse(VirtualMouseConfig virtualMouseConfig, IBinder iBinder) throws RemoteException;

    void createVirtualNavigationTouchpad(VirtualNavigationTouchpadConfig virtualNavigationTouchpadConfig, IBinder iBinder) throws RemoteException;

    void createVirtualTouchscreen(VirtualTouchscreenConfig virtualTouchscreenConfig, IBinder iBinder) throws RemoteException;

    int getAssociationId() throws RemoteException;

    PointF getCursorPosition(IBinder iBinder) throws RemoteException;

    int getDeviceId() throws RemoteException;

    int getInputDeviceId(IBinder iBinder) throws RemoteException;

    List<VirtualSensor> getVirtualSensorList() throws RemoteException;

    void launchPendingIntent(int i, PendingIntent pendingIntent, ResultReceiver resultReceiver) throws RemoteException;

    void onAudioSessionEnded() throws RemoteException;

    void onAudioSessionStarting(int i, IAudioRoutingCallback iAudioRoutingCallback, IAudioConfigChangedCallback iAudioConfigChangedCallback) throws RemoteException;

    void registerIntentInterceptor(IVirtualDeviceIntentInterceptor iVirtualDeviceIntentInterceptor, IntentFilter intentFilter) throws RemoteException;

    boolean sendButtonEvent(IBinder iBinder, VirtualMouseButtonEvent virtualMouseButtonEvent) throws RemoteException;

    boolean sendDpadKeyEvent(IBinder iBinder, VirtualKeyEvent virtualKeyEvent) throws RemoteException;

    boolean sendKeyEvent(IBinder iBinder, VirtualKeyEvent virtualKeyEvent) throws RemoteException;

    boolean sendRelativeEvent(IBinder iBinder, VirtualMouseRelativeEvent virtualMouseRelativeEvent) throws RemoteException;

    boolean sendScrollEvent(IBinder iBinder, VirtualMouseScrollEvent virtualMouseScrollEvent) throws RemoteException;

    boolean sendSensorEvent(IBinder iBinder, VirtualSensorEvent virtualSensorEvent) throws RemoteException;

    boolean sendTouchEvent(IBinder iBinder, VirtualTouchEvent virtualTouchEvent) throws RemoteException;

    void setShowPointerIcon(boolean z) throws RemoteException;

    void unregisterInputDevice(IBinder iBinder) throws RemoteException;

    void unregisterIntentInterceptor(IVirtualDeviceIntentInterceptor iVirtualDeviceIntentInterceptor) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IVirtualDevice {
        @Override // android.companion.virtual.IVirtualDevice
        public int getAssociationId() throws RemoteException {
            return 0;
        }

        @Override // android.companion.virtual.IVirtualDevice
        public int getDeviceId() throws RemoteException {
            return 0;
        }

        @Override // android.companion.virtual.IVirtualDevice
        public void close() throws RemoteException {
        }

        @Override // android.companion.virtual.IVirtualDevice
        public void onAudioSessionStarting(int displayId, IAudioRoutingCallback routingCallback, IAudioConfigChangedCallback configChangedCallback) throws RemoteException {
        }

        @Override // android.companion.virtual.IVirtualDevice
        public void onAudioSessionEnded() throws RemoteException {
        }

        @Override // android.companion.virtual.IVirtualDevice
        public void createVirtualDpad(VirtualDpadConfig config, IBinder token) throws RemoteException {
        }

        @Override // android.companion.virtual.IVirtualDevice
        public void createVirtualKeyboard(VirtualKeyboardConfig config, IBinder token) throws RemoteException {
        }

        @Override // android.companion.virtual.IVirtualDevice
        public void createVirtualMouse(VirtualMouseConfig config, IBinder token) throws RemoteException {
        }

        @Override // android.companion.virtual.IVirtualDevice
        public void createVirtualTouchscreen(VirtualTouchscreenConfig config, IBinder token) throws RemoteException {
        }

        @Override // android.companion.virtual.IVirtualDevice
        public void createVirtualNavigationTouchpad(VirtualNavigationTouchpadConfig config, IBinder token) throws RemoteException {
        }

        @Override // android.companion.virtual.IVirtualDevice
        public void unregisterInputDevice(IBinder token) throws RemoteException {
        }

        @Override // android.companion.virtual.IVirtualDevice
        public int getInputDeviceId(IBinder token) throws RemoteException {
            return 0;
        }

        @Override // android.companion.virtual.IVirtualDevice
        public boolean sendDpadKeyEvent(IBinder token, VirtualKeyEvent event) throws RemoteException {
            return false;
        }

        @Override // android.companion.virtual.IVirtualDevice
        public boolean sendKeyEvent(IBinder token, VirtualKeyEvent event) throws RemoteException {
            return false;
        }

        @Override // android.companion.virtual.IVirtualDevice
        public boolean sendButtonEvent(IBinder token, VirtualMouseButtonEvent event) throws RemoteException {
            return false;
        }

        @Override // android.companion.virtual.IVirtualDevice
        public boolean sendRelativeEvent(IBinder token, VirtualMouseRelativeEvent event) throws RemoteException {
            return false;
        }

        @Override // android.companion.virtual.IVirtualDevice
        public boolean sendScrollEvent(IBinder token, VirtualMouseScrollEvent event) throws RemoteException {
            return false;
        }

        @Override // android.companion.virtual.IVirtualDevice
        public boolean sendTouchEvent(IBinder token, VirtualTouchEvent event) throws RemoteException {
            return false;
        }

        @Override // android.companion.virtual.IVirtualDevice
        public List<VirtualSensor> getVirtualSensorList() throws RemoteException {
            return null;
        }

        @Override // android.companion.virtual.IVirtualDevice
        public boolean sendSensorEvent(IBinder token, VirtualSensorEvent event) throws RemoteException {
            return false;
        }

        @Override // android.companion.virtual.IVirtualDevice
        public void launchPendingIntent(int displayId, PendingIntent pendingIntent, ResultReceiver resultReceiver) throws RemoteException {
        }

        @Override // android.companion.virtual.IVirtualDevice
        public PointF getCursorPosition(IBinder token) throws RemoteException {
            return null;
        }

        @Override // android.companion.virtual.IVirtualDevice
        public void setShowPointerIcon(boolean showPointerIcon) throws RemoteException {
        }

        @Override // android.companion.virtual.IVirtualDevice
        public void registerIntentInterceptor(IVirtualDeviceIntentInterceptor intentInterceptor, IntentFilter filter) throws RemoteException {
        }

        @Override // android.companion.virtual.IVirtualDevice
        public void unregisterIntentInterceptor(IVirtualDeviceIntentInterceptor intentInterceptor) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IVirtualDevice {
        static final int TRANSACTION_close = 3;
        static final int TRANSACTION_createVirtualDpad = 6;
        static final int TRANSACTION_createVirtualKeyboard = 7;
        static final int TRANSACTION_createVirtualMouse = 8;
        static final int TRANSACTION_createVirtualNavigationTouchpad = 10;
        static final int TRANSACTION_createVirtualTouchscreen = 9;
        static final int TRANSACTION_getAssociationId = 1;
        static final int TRANSACTION_getCursorPosition = 22;
        static final int TRANSACTION_getDeviceId = 2;
        static final int TRANSACTION_getInputDeviceId = 12;
        static final int TRANSACTION_getVirtualSensorList = 19;
        static final int TRANSACTION_launchPendingIntent = 21;
        static final int TRANSACTION_onAudioSessionEnded = 5;
        static final int TRANSACTION_onAudioSessionStarting = 4;
        static final int TRANSACTION_registerIntentInterceptor = 24;
        static final int TRANSACTION_sendButtonEvent = 15;
        static final int TRANSACTION_sendDpadKeyEvent = 13;
        static final int TRANSACTION_sendKeyEvent = 14;
        static final int TRANSACTION_sendRelativeEvent = 16;
        static final int TRANSACTION_sendScrollEvent = 17;
        static final int TRANSACTION_sendSensorEvent = 20;
        static final int TRANSACTION_sendTouchEvent = 18;
        static final int TRANSACTION_setShowPointerIcon = 23;
        static final int TRANSACTION_unregisterInputDevice = 11;
        static final int TRANSACTION_unregisterIntentInterceptor = 25;
        private final PermissionEnforcer mEnforcer;

        public Stub(PermissionEnforcer enforcer) {
            attachInterface(this, IVirtualDevice.DESCRIPTOR);
            if (enforcer == null) {
                throw new IllegalArgumentException("enforcer cannot be null");
            }
            this.mEnforcer = enforcer;
        }

        @Deprecated
        public Stub() {
            this(PermissionEnforcer.fromContext(ActivityThread.currentActivityThread().getSystemContext()));
        }

        public static IVirtualDevice asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IVirtualDevice.DESCRIPTOR);
            if (iin != null && (iin instanceof IVirtualDevice)) {
                return (IVirtualDevice) iin;
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
                    return "getAssociationId";
                case 2:
                    return "getDeviceId";
                case 3:
                    return "close";
                case 4:
                    return "onAudioSessionStarting";
                case 5:
                    return "onAudioSessionEnded";
                case 6:
                    return "createVirtualDpad";
                case 7:
                    return "createVirtualKeyboard";
                case 8:
                    return "createVirtualMouse";
                case 9:
                    return "createVirtualTouchscreen";
                case 10:
                    return "createVirtualNavigationTouchpad";
                case 11:
                    return "unregisterInputDevice";
                case 12:
                    return "getInputDeviceId";
                case 13:
                    return "sendDpadKeyEvent";
                case 14:
                    return "sendKeyEvent";
                case 15:
                    return "sendButtonEvent";
                case 16:
                    return "sendRelativeEvent";
                case 17:
                    return "sendScrollEvent";
                case 18:
                    return "sendTouchEvent";
                case 19:
                    return "getVirtualSensorList";
                case 20:
                    return "sendSensorEvent";
                case 21:
                    return "launchPendingIntent";
                case 22:
                    return "getCursorPosition";
                case 23:
                    return "setShowPointerIcon";
                case 24:
                    return "registerIntentInterceptor";
                case 25:
                    return "unregisterIntentInterceptor";
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
                data.enforceInterface(IVirtualDevice.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IVirtualDevice.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _result = getAssociationId();
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            int _result2 = getDeviceId();
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 3:
                            close();
                            reply.writeNoException();
                            break;
                        case 4:
                            int _arg0 = data.readInt();
                            IAudioRoutingCallback _arg1 = IAudioRoutingCallback.Stub.asInterface(data.readStrongBinder());
                            IAudioConfigChangedCallback _arg2 = IAudioConfigChangedCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onAudioSessionStarting(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 5:
                            onAudioSessionEnded();
                            reply.writeNoException();
                            break;
                        case 6:
                            VirtualDpadConfig _arg02 = (VirtualDpadConfig) data.readTypedObject(VirtualDpadConfig.CREATOR);
                            IBinder _arg12 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            createVirtualDpad(_arg02, _arg12);
                            reply.writeNoException();
                            break;
                        case 7:
                            VirtualKeyboardConfig _arg03 = (VirtualKeyboardConfig) data.readTypedObject(VirtualKeyboardConfig.CREATOR);
                            IBinder _arg13 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            createVirtualKeyboard(_arg03, _arg13);
                            reply.writeNoException();
                            break;
                        case 8:
                            VirtualMouseConfig _arg04 = (VirtualMouseConfig) data.readTypedObject(VirtualMouseConfig.CREATOR);
                            IBinder _arg14 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            createVirtualMouse(_arg04, _arg14);
                            reply.writeNoException();
                            break;
                        case 9:
                            VirtualTouchscreenConfig _arg05 = (VirtualTouchscreenConfig) data.readTypedObject(VirtualTouchscreenConfig.CREATOR);
                            IBinder _arg15 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            createVirtualTouchscreen(_arg05, _arg15);
                            reply.writeNoException();
                            break;
                        case 10:
                            VirtualNavigationTouchpadConfig _arg06 = (VirtualNavigationTouchpadConfig) data.readTypedObject(VirtualNavigationTouchpadConfig.CREATOR);
                            IBinder _arg16 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            createVirtualNavigationTouchpad(_arg06, _arg16);
                            reply.writeNoException();
                            break;
                        case 11:
                            IBinder _arg07 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            unregisterInputDevice(_arg07);
                            reply.writeNoException();
                            break;
                        case 12:
                            IBinder _arg08 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            int _result3 = getInputDeviceId(_arg08);
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            break;
                        case 13:
                            IBinder _arg09 = data.readStrongBinder();
                            VirtualKeyEvent _arg17 = (VirtualKeyEvent) data.readTypedObject(VirtualKeyEvent.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result4 = sendDpadKeyEvent(_arg09, _arg17);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 14:
                            IBinder _arg010 = data.readStrongBinder();
                            VirtualKeyEvent _arg18 = (VirtualKeyEvent) data.readTypedObject(VirtualKeyEvent.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result5 = sendKeyEvent(_arg010, _arg18);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 15:
                            IBinder _arg011 = data.readStrongBinder();
                            VirtualMouseButtonEvent _arg19 = (VirtualMouseButtonEvent) data.readTypedObject(VirtualMouseButtonEvent.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result6 = sendButtonEvent(_arg011, _arg19);
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            break;
                        case 16:
                            IBinder _arg012 = data.readStrongBinder();
                            VirtualMouseRelativeEvent _arg110 = (VirtualMouseRelativeEvent) data.readTypedObject(VirtualMouseRelativeEvent.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result7 = sendRelativeEvent(_arg012, _arg110);
                            reply.writeNoException();
                            reply.writeBoolean(_result7);
                            break;
                        case 17:
                            IBinder _arg013 = data.readStrongBinder();
                            VirtualMouseScrollEvent _arg111 = (VirtualMouseScrollEvent) data.readTypedObject(VirtualMouseScrollEvent.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result8 = sendScrollEvent(_arg013, _arg111);
                            reply.writeNoException();
                            reply.writeBoolean(_result8);
                            break;
                        case 18:
                            IBinder _arg014 = data.readStrongBinder();
                            VirtualTouchEvent _arg112 = (VirtualTouchEvent) data.readTypedObject(VirtualTouchEvent.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result9 = sendTouchEvent(_arg014, _arg112);
                            reply.writeNoException();
                            reply.writeBoolean(_result9);
                            break;
                        case 19:
                            List<VirtualSensor> _result10 = getVirtualSensorList();
                            reply.writeNoException();
                            reply.writeTypedList(_result10, 1);
                            break;
                        case 20:
                            IBinder _arg015 = data.readStrongBinder();
                            VirtualSensorEvent _arg113 = (VirtualSensorEvent) data.readTypedObject(VirtualSensorEvent.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result11 = sendSensorEvent(_arg015, _arg113);
                            reply.writeNoException();
                            reply.writeBoolean(_result11);
                            break;
                        case 21:
                            int _arg016 = data.readInt();
                            PendingIntent _arg114 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            ResultReceiver _arg22 = (ResultReceiver) data.readTypedObject(ResultReceiver.CREATOR);
                            data.enforceNoDataAvail();
                            launchPendingIntent(_arg016, _arg114, _arg22);
                            reply.writeNoException();
                            break;
                        case 22:
                            IBinder _arg017 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            PointF _result12 = getCursorPosition(_arg017);
                            reply.writeNoException();
                            reply.writeTypedObject(_result12, 1);
                            break;
                        case 23:
                            boolean _arg018 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setShowPointerIcon(_arg018);
                            reply.writeNoException();
                            break;
                        case 24:
                            IVirtualDeviceIntentInterceptor _arg019 = IVirtualDeviceIntentInterceptor.Stub.asInterface(data.readStrongBinder());
                            IntentFilter _arg115 = (IntentFilter) data.readTypedObject(IntentFilter.CREATOR);
                            data.enforceNoDataAvail();
                            registerIntentInterceptor(_arg019, _arg115);
                            reply.writeNoException();
                            break;
                        case 25:
                            IVirtualDeviceIntentInterceptor _arg020 = IVirtualDeviceIntentInterceptor.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterIntentInterceptor(_arg020);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IVirtualDevice {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IVirtualDevice.DESCRIPTOR;
            }

            @Override // android.companion.virtual.IVirtualDevice
            public int getAssociationId() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public int getDeviceId() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public void close() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public void onAudioSessionStarting(int displayId, IAudioRoutingCallback routingCallback, IAudioConfigChangedCallback configChangedCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeStrongInterface(routingCallback);
                    _data.writeStrongInterface(configChangedCallback);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public void onAudioSessionEnded() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public void createVirtualDpad(VirtualDpadConfig config, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    _data.writeTypedObject(config, 0);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public void createVirtualKeyboard(VirtualKeyboardConfig config, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    _data.writeTypedObject(config, 0);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public void createVirtualMouse(VirtualMouseConfig config, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    _data.writeTypedObject(config, 0);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public void createVirtualTouchscreen(VirtualTouchscreenConfig config, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    _data.writeTypedObject(config, 0);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public void createVirtualNavigationTouchpad(VirtualNavigationTouchpadConfig config, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    _data.writeTypedObject(config, 0);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public void unregisterInputDevice(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public int getInputDeviceId(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public boolean sendDpadKeyEvent(IBinder token, VirtualKeyEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeTypedObject(event, 0);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public boolean sendKeyEvent(IBinder token, VirtualKeyEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeTypedObject(event, 0);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public boolean sendButtonEvent(IBinder token, VirtualMouseButtonEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeTypedObject(event, 0);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public boolean sendRelativeEvent(IBinder token, VirtualMouseRelativeEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeTypedObject(event, 0);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public boolean sendScrollEvent(IBinder token, VirtualMouseScrollEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeTypedObject(event, 0);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public boolean sendTouchEvent(IBinder token, VirtualTouchEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeTypedObject(event, 0);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public List<VirtualSensor> getVirtualSensorList() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    List<VirtualSensor> _result = _reply.createTypedArrayList(VirtualSensor.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public boolean sendSensorEvent(IBinder token, VirtualSensorEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeTypedObject(event, 0);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public void launchPendingIntent(int displayId, PendingIntent pendingIntent, ResultReceiver resultReceiver) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeTypedObject(pendingIntent, 0);
                    _data.writeTypedObject(resultReceiver, 0);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public PointF getCursorPosition(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    PointF _result = (PointF) _reply.readTypedObject(PointF.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public void setShowPointerIcon(boolean showPointerIcon) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    _data.writeBoolean(showPointerIcon);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public void registerIntentInterceptor(IVirtualDeviceIntentInterceptor intentInterceptor, IntentFilter filter) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    _data.writeStrongInterface(intentInterceptor);
                    _data.writeTypedObject(filter, 0);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDevice
            public void unregisterIntentInterceptor(IVirtualDeviceIntentInterceptor intentInterceptor) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDevice.DESCRIPTOR);
                    _data.writeStrongInterface(intentInterceptor);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        protected void close_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CREATE_VIRTUAL_DEVICE, source);
        }

        protected void onAudioSessionStarting_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CREATE_VIRTUAL_DEVICE, source);
        }

        protected void onAudioSessionEnded_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CREATE_VIRTUAL_DEVICE, source);
        }

        protected void createVirtualDpad_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CREATE_VIRTUAL_DEVICE, source);
        }

        protected void createVirtualKeyboard_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CREATE_VIRTUAL_DEVICE, source);
        }

        protected void createVirtualMouse_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CREATE_VIRTUAL_DEVICE, source);
        }

        protected void createVirtualTouchscreen_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CREATE_VIRTUAL_DEVICE, source);
        }

        protected void createVirtualNavigationTouchpad_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CREATE_VIRTUAL_DEVICE, source);
        }

        protected void unregisterInputDevice_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CREATE_VIRTUAL_DEVICE, source);
        }

        protected void sendDpadKeyEvent_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CREATE_VIRTUAL_DEVICE, source);
        }

        protected void sendKeyEvent_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CREATE_VIRTUAL_DEVICE, source);
        }

        protected void sendButtonEvent_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CREATE_VIRTUAL_DEVICE, source);
        }

        protected void sendRelativeEvent_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CREATE_VIRTUAL_DEVICE, source);
        }

        protected void sendScrollEvent_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CREATE_VIRTUAL_DEVICE, source);
        }

        protected void sendTouchEvent_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CREATE_VIRTUAL_DEVICE, source);
        }

        protected void getVirtualSensorList_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CREATE_VIRTUAL_DEVICE, source);
        }

        protected void sendSensorEvent_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CREATE_VIRTUAL_DEVICE, source);
        }

        protected void setShowPointerIcon_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CREATE_VIRTUAL_DEVICE, source);
        }

        protected void registerIntentInterceptor_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CREATE_VIRTUAL_DEVICE, source);
        }

        protected void unregisterIntentInterceptor_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CREATE_VIRTUAL_DEVICE, source);
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 24;
        }
    }
}
