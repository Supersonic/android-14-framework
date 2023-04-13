package android.app;

import android.accessibilityservice.IAccessibilityServiceClient;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.MediaMetrics;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.view.InputEvent;
import android.view.SurfaceControl;
import android.view.WindowAnimationFrameStats;
import android.view.WindowContentFrameStats;
import java.util.List;
/* loaded from: classes.dex */
public interface IUiAutomationConnection extends IInterface {
    void adoptShellPermissionIdentity(int i, String[] strArr) throws RemoteException;

    void clearWindowAnimationFrameStats() throws RemoteException;

    boolean clearWindowContentFrameStats(int i) throws RemoteException;

    void connect(IAccessibilityServiceClient iAccessibilityServiceClient, int i) throws RemoteException;

    void disconnect() throws RemoteException;

    void dropShellPermissionIdentity() throws RemoteException;

    void executeShellCommand(String str, ParcelFileDescriptor parcelFileDescriptor, ParcelFileDescriptor parcelFileDescriptor2) throws RemoteException;

    void executeShellCommandWithStderr(String str, ParcelFileDescriptor parcelFileDescriptor, ParcelFileDescriptor parcelFileDescriptor2, ParcelFileDescriptor parcelFileDescriptor3) throws RemoteException;

    List<String> getAdoptedShellPermissions() throws RemoteException;

    WindowAnimationFrameStats getWindowAnimationFrameStats() throws RemoteException;

    WindowContentFrameStats getWindowContentFrameStats(int i) throws RemoteException;

    void grantRuntimePermission(String str, String str2, int i) throws RemoteException;

    boolean injectInputEvent(InputEvent inputEvent, boolean z, boolean z2) throws RemoteException;

    void injectInputEventToInputFilter(InputEvent inputEvent) throws RemoteException;

    void revokeRuntimePermission(String str, String str2, int i) throws RemoteException;

    boolean setRotation(int i) throws RemoteException;

    void shutdown() throws RemoteException;

    void syncInputTransactions(boolean z) throws RemoteException;

    Bitmap takeScreenshot(Rect rect) throws RemoteException;

    Bitmap takeSurfaceControlScreenshot(SurfaceControl surfaceControl) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IUiAutomationConnection {
        @Override // android.app.IUiAutomationConnection
        public void connect(IAccessibilityServiceClient client, int flags) throws RemoteException {
        }

        @Override // android.app.IUiAutomationConnection
        public void disconnect() throws RemoteException {
        }

        @Override // android.app.IUiAutomationConnection
        public boolean injectInputEvent(InputEvent event, boolean sync, boolean waitForAnimations) throws RemoteException {
            return false;
        }

        @Override // android.app.IUiAutomationConnection
        public void injectInputEventToInputFilter(InputEvent event) throws RemoteException {
        }

        @Override // android.app.IUiAutomationConnection
        public void syncInputTransactions(boolean waitForAnimations) throws RemoteException {
        }

        @Override // android.app.IUiAutomationConnection
        public boolean setRotation(int rotation) throws RemoteException {
            return false;
        }

        @Override // android.app.IUiAutomationConnection
        public Bitmap takeScreenshot(Rect crop) throws RemoteException {
            return null;
        }

        @Override // android.app.IUiAutomationConnection
        public Bitmap takeSurfaceControlScreenshot(SurfaceControl surfaceControl) throws RemoteException {
            return null;
        }

        @Override // android.app.IUiAutomationConnection
        public boolean clearWindowContentFrameStats(int windowId) throws RemoteException {
            return false;
        }

        @Override // android.app.IUiAutomationConnection
        public WindowContentFrameStats getWindowContentFrameStats(int windowId) throws RemoteException {
            return null;
        }

        @Override // android.app.IUiAutomationConnection
        public void clearWindowAnimationFrameStats() throws RemoteException {
        }

        @Override // android.app.IUiAutomationConnection
        public WindowAnimationFrameStats getWindowAnimationFrameStats() throws RemoteException {
            return null;
        }

        @Override // android.app.IUiAutomationConnection
        public void executeShellCommand(String command, ParcelFileDescriptor sink, ParcelFileDescriptor source) throws RemoteException {
        }

        @Override // android.app.IUiAutomationConnection
        public void grantRuntimePermission(String packageName, String permission, int userId) throws RemoteException {
        }

        @Override // android.app.IUiAutomationConnection
        public void revokeRuntimePermission(String packageName, String permission, int userId) throws RemoteException {
        }

        @Override // android.app.IUiAutomationConnection
        public void adoptShellPermissionIdentity(int uid, String[] permissions) throws RemoteException {
        }

        @Override // android.app.IUiAutomationConnection
        public void dropShellPermissionIdentity() throws RemoteException {
        }

        @Override // android.app.IUiAutomationConnection
        public void shutdown() throws RemoteException {
        }

        @Override // android.app.IUiAutomationConnection
        public void executeShellCommandWithStderr(String command, ParcelFileDescriptor sink, ParcelFileDescriptor source, ParcelFileDescriptor stderrSink) throws RemoteException {
        }

        @Override // android.app.IUiAutomationConnection
        public List<String> getAdoptedShellPermissions() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IUiAutomationConnection {
        public static final String DESCRIPTOR = "android.app.IUiAutomationConnection";
        static final int TRANSACTION_adoptShellPermissionIdentity = 16;
        static final int TRANSACTION_clearWindowAnimationFrameStats = 11;
        static final int TRANSACTION_clearWindowContentFrameStats = 9;
        static final int TRANSACTION_connect = 1;
        static final int TRANSACTION_disconnect = 2;
        static final int TRANSACTION_dropShellPermissionIdentity = 17;
        static final int TRANSACTION_executeShellCommand = 13;
        static final int TRANSACTION_executeShellCommandWithStderr = 19;
        static final int TRANSACTION_getAdoptedShellPermissions = 20;
        static final int TRANSACTION_getWindowAnimationFrameStats = 12;
        static final int TRANSACTION_getWindowContentFrameStats = 10;
        static final int TRANSACTION_grantRuntimePermission = 14;
        static final int TRANSACTION_injectInputEvent = 3;
        static final int TRANSACTION_injectInputEventToInputFilter = 4;
        static final int TRANSACTION_revokeRuntimePermission = 15;
        static final int TRANSACTION_setRotation = 6;
        static final int TRANSACTION_shutdown = 18;
        static final int TRANSACTION_syncInputTransactions = 5;
        static final int TRANSACTION_takeScreenshot = 7;
        static final int TRANSACTION_takeSurfaceControlScreenshot = 8;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IUiAutomationConnection asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IUiAutomationConnection)) {
                return (IUiAutomationConnection) iin;
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
                    return MediaMetrics.Value.CONNECT;
                case 2:
                    return MediaMetrics.Value.DISCONNECT;
                case 3:
                    return "injectInputEvent";
                case 4:
                    return "injectInputEventToInputFilter";
                case 5:
                    return "syncInputTransactions";
                case 6:
                    return "setRotation";
                case 7:
                    return "takeScreenshot";
                case 8:
                    return "takeSurfaceControlScreenshot";
                case 9:
                    return "clearWindowContentFrameStats";
                case 10:
                    return "getWindowContentFrameStats";
                case 11:
                    return "clearWindowAnimationFrameStats";
                case 12:
                    return "getWindowAnimationFrameStats";
                case 13:
                    return "executeShellCommand";
                case 14:
                    return "grantRuntimePermission";
                case 15:
                    return "revokeRuntimePermission";
                case 16:
                    return "adoptShellPermissionIdentity";
                case 17:
                    return "dropShellPermissionIdentity";
                case 18:
                    return "shutdown";
                case 19:
                    return "executeShellCommandWithStderr";
                case 20:
                    return "getAdoptedShellPermissions";
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
                            IAccessibilityServiceClient _arg0 = IAccessibilityServiceClient.Stub.asInterface(data.readStrongBinder());
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            connect(_arg0, _arg1);
                            reply.writeNoException();
                            break;
                        case 2:
                            disconnect();
                            reply.writeNoException();
                            break;
                        case 3:
                            InputEvent _arg02 = (InputEvent) data.readTypedObject(InputEvent.CREATOR);
                            boolean _arg12 = data.readBoolean();
                            boolean _arg2 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result = injectInputEvent(_arg02, _arg12, _arg2);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 4:
                            InputEvent _arg03 = (InputEvent) data.readTypedObject(InputEvent.CREATOR);
                            data.enforceNoDataAvail();
                            injectInputEventToInputFilter(_arg03);
                            reply.writeNoException();
                            break;
                        case 5:
                            boolean _arg04 = data.readBoolean();
                            data.enforceNoDataAvail();
                            syncInputTransactions(_arg04);
                            reply.writeNoException();
                            break;
                        case 6:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result2 = setRotation(_arg05);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 7:
                            Rect _arg06 = (Rect) data.readTypedObject(Rect.CREATOR);
                            data.enforceNoDataAvail();
                            Bitmap _result3 = takeScreenshot(_arg06);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 8:
                            SurfaceControl _arg07 = (SurfaceControl) data.readTypedObject(SurfaceControl.CREATOR);
                            data.enforceNoDataAvail();
                            Bitmap _result4 = takeSurfaceControlScreenshot(_arg07);
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        case 9:
                            int _arg08 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result5 = clearWindowContentFrameStats(_arg08);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 10:
                            int _arg09 = data.readInt();
                            data.enforceNoDataAvail();
                            WindowContentFrameStats _result6 = getWindowContentFrameStats(_arg09);
                            reply.writeNoException();
                            reply.writeTypedObject(_result6, 1);
                            break;
                        case 11:
                            clearWindowAnimationFrameStats();
                            reply.writeNoException();
                            break;
                        case 12:
                            WindowAnimationFrameStats _result7 = getWindowAnimationFrameStats();
                            reply.writeNoException();
                            reply.writeTypedObject(_result7, 1);
                            break;
                        case 13:
                            String _arg010 = data.readString();
                            ParcelFileDescriptor _arg13 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            ParcelFileDescriptor _arg22 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            data.enforceNoDataAvail();
                            executeShellCommand(_arg010, _arg13, _arg22);
                            reply.writeNoException();
                            break;
                        case 14:
                            String _arg011 = data.readString();
                            String _arg14 = data.readString();
                            int _arg23 = data.readInt();
                            data.enforceNoDataAvail();
                            grantRuntimePermission(_arg011, _arg14, _arg23);
                            reply.writeNoException();
                            break;
                        case 15:
                            String _arg012 = data.readString();
                            String _arg15 = data.readString();
                            int _arg24 = data.readInt();
                            data.enforceNoDataAvail();
                            revokeRuntimePermission(_arg012, _arg15, _arg24);
                            reply.writeNoException();
                            break;
                        case 16:
                            int _arg013 = data.readInt();
                            String[] _arg16 = data.createStringArray();
                            data.enforceNoDataAvail();
                            adoptShellPermissionIdentity(_arg013, _arg16);
                            reply.writeNoException();
                            break;
                        case 17:
                            dropShellPermissionIdentity();
                            reply.writeNoException();
                            break;
                        case 18:
                            shutdown();
                            break;
                        case 19:
                            String _arg014 = data.readString();
                            ParcelFileDescriptor _arg17 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            ParcelFileDescriptor _arg25 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            ParcelFileDescriptor _arg3 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            data.enforceNoDataAvail();
                            executeShellCommandWithStderr(_arg014, _arg17, _arg25, _arg3);
                            reply.writeNoException();
                            break;
                        case 20:
                            List<String> _result8 = getAdoptedShellPermissions();
                            reply.writeNoException();
                            reply.writeStringList(_result8);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements IUiAutomationConnection {
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

            @Override // android.app.IUiAutomationConnection
            public void connect(IAccessibilityServiceClient client, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeInt(flags);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiAutomationConnection
            public void disconnect() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiAutomationConnection
            public boolean injectInputEvent(InputEvent event, boolean sync, boolean waitForAnimations) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(event, 0);
                    _data.writeBoolean(sync);
                    _data.writeBoolean(waitForAnimations);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiAutomationConnection
            public void injectInputEventToInputFilter(InputEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(event, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiAutomationConnection
            public void syncInputTransactions(boolean waitForAnimations) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(waitForAnimations);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiAutomationConnection
            public boolean setRotation(int rotation) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(rotation);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiAutomationConnection
            public Bitmap takeScreenshot(Rect crop) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(crop, 0);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    Bitmap _result = (Bitmap) _reply.readTypedObject(Bitmap.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiAutomationConnection
            public Bitmap takeSurfaceControlScreenshot(SurfaceControl surfaceControl) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(surfaceControl, 0);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    Bitmap _result = (Bitmap) _reply.readTypedObject(Bitmap.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiAutomationConnection
            public boolean clearWindowContentFrameStats(int windowId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(windowId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiAutomationConnection
            public WindowContentFrameStats getWindowContentFrameStats(int windowId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(windowId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    WindowContentFrameStats _result = (WindowContentFrameStats) _reply.readTypedObject(WindowContentFrameStats.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiAutomationConnection
            public void clearWindowAnimationFrameStats() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiAutomationConnection
            public WindowAnimationFrameStats getWindowAnimationFrameStats() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    WindowAnimationFrameStats _result = (WindowAnimationFrameStats) _reply.readTypedObject(WindowAnimationFrameStats.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiAutomationConnection
            public void executeShellCommand(String command, ParcelFileDescriptor sink, ParcelFileDescriptor source) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(command);
                    _data.writeTypedObject(sink, 0);
                    _data.writeTypedObject(source, 0);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiAutomationConnection
            public void grantRuntimePermission(String packageName, String permission, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(permission);
                    _data.writeInt(userId);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiAutomationConnection
            public void revokeRuntimePermission(String packageName, String permission, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(permission);
                    _data.writeInt(userId);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiAutomationConnection
            public void adoptShellPermissionIdentity(int uid, String[] permissions) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeStringArray(permissions);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiAutomationConnection
            public void dropShellPermissionIdentity() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiAutomationConnection
            public void shutdown() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.IUiAutomationConnection
            public void executeShellCommandWithStderr(String command, ParcelFileDescriptor sink, ParcelFileDescriptor source, ParcelFileDescriptor stderrSink) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(command);
                    _data.writeTypedObject(sink, 0);
                    _data.writeTypedObject(source, 0);
                    _data.writeTypedObject(stderrSink, 0);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiAutomationConnection
            public List<String> getAdoptedShellPermissions() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 19;
        }
    }
}
