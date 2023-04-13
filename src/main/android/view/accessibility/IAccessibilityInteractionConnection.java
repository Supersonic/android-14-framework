package android.view.accessibility;

import android.graphics.Region;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.MagnificationSpec;
import android.view.SurfaceControl;
import android.view.accessibility.IAccessibilityInteractionConnectionCallback;
import android.window.ScreenCapture;
/* loaded from: classes4.dex */
public interface IAccessibilityInteractionConnection extends IInterface {
    void attachAccessibilityOverlayToWindow(SurfaceControl surfaceControl) throws RemoteException;

    void clearAccessibilityFocus() throws RemoteException;

    void findAccessibilityNodeInfoByAccessibilityId(long j, Region region, int i, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i2, int i3, long j2, MagnificationSpec magnificationSpec, float[] fArr, Bundle bundle) throws RemoteException;

    void findAccessibilityNodeInfosByText(long j, String str, Region region, int i, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i2, int i3, long j2, MagnificationSpec magnificationSpec, float[] fArr) throws RemoteException;

    void findAccessibilityNodeInfosByViewId(long j, String str, Region region, int i, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i2, int i3, long j2, MagnificationSpec magnificationSpec, float[] fArr) throws RemoteException;

    void findFocus(long j, int i, Region region, int i2, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i3, int i4, long j2, MagnificationSpec magnificationSpec, float[] fArr) throws RemoteException;

    void focusSearch(long j, int i, Region region, int i2, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i3, int i4, long j2, MagnificationSpec magnificationSpec, float[] fArr) throws RemoteException;

    void notifyOutsideTouch() throws RemoteException;

    void performAccessibilityAction(long j, int i, Bundle bundle, int i2, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i3, int i4, long j2) throws RemoteException;

    void takeScreenshotOfWindow(int i, ScreenCapture.ScreenCaptureListener screenCaptureListener, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IAccessibilityInteractionConnection {
        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void findAccessibilityNodeInfoByAccessibilityId(long accessibilityNodeId, Region bounds, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec, float[] matrixValues, Bundle arguments) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void findAccessibilityNodeInfosByViewId(long accessibilityNodeId, String viewId, Region bounds, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec, float[] matrix) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void findAccessibilityNodeInfosByText(long accessibilityNodeId, String text, Region bounds, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec, float[] matrixValues) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void findFocus(long accessibilityNodeId, int focusType, Region bounds, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec, float[] matrixValues) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void focusSearch(long accessibilityNodeId, int direction, Region bounds, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec, float[] matrixValues) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void performAccessibilityAction(long accessibilityNodeId, int action, Bundle arguments, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void clearAccessibilityFocus() throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void notifyOutsideTouch() throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void takeScreenshotOfWindow(int interactionId, ScreenCapture.ScreenCaptureListener listener, IAccessibilityInteractionConnectionCallback callback) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void attachAccessibilityOverlayToWindow(SurfaceControl sc) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IAccessibilityInteractionConnection {
        public static final String DESCRIPTOR = "android.view.accessibility.IAccessibilityInteractionConnection";
        static final int TRANSACTION_attachAccessibilityOverlayToWindow = 10;
        static final int TRANSACTION_clearAccessibilityFocus = 7;
        static final int TRANSACTION_findAccessibilityNodeInfoByAccessibilityId = 1;
        static final int TRANSACTION_findAccessibilityNodeInfosByText = 3;
        static final int TRANSACTION_findAccessibilityNodeInfosByViewId = 2;
        static final int TRANSACTION_findFocus = 4;
        static final int TRANSACTION_focusSearch = 5;
        static final int TRANSACTION_notifyOutsideTouch = 8;
        static final int TRANSACTION_performAccessibilityAction = 6;
        static final int TRANSACTION_takeScreenshotOfWindow = 9;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAccessibilityInteractionConnection asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAccessibilityInteractionConnection)) {
                return (IAccessibilityInteractionConnection) iin;
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
                    return "findAccessibilityNodeInfoByAccessibilityId";
                case 2:
                    return "findAccessibilityNodeInfosByViewId";
                case 3:
                    return "findAccessibilityNodeInfosByText";
                case 4:
                    return "findFocus";
                case 5:
                    return "focusSearch";
                case 6:
                    return "performAccessibilityAction";
                case 7:
                    return "clearAccessibilityFocus";
                case 8:
                    return "notifyOutsideTouch";
                case 9:
                    return "takeScreenshotOfWindow";
                case 10:
                    return "attachAccessibilityOverlayToWindow";
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
                            long _arg0 = data.readLong();
                            Region _arg1 = (Region) data.readTypedObject(Region.CREATOR);
                            int _arg2 = data.readInt();
                            IAccessibilityInteractionConnectionCallback _arg3 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg4 = data.readInt();
                            int _arg5 = data.readInt();
                            long _arg6 = data.readLong();
                            MagnificationSpec _arg7 = (MagnificationSpec) data.readTypedObject(MagnificationSpec.CREATOR);
                            float[] _arg8 = data.createFloatArray();
                            Bundle _arg9 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            findAccessibilityNodeInfoByAccessibilityId(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7, _arg8, _arg9);
                            return true;
                        case 2:
                            long _arg02 = data.readLong();
                            String _arg12 = data.readString();
                            Region _arg22 = (Region) data.readTypedObject(Region.CREATOR);
                            int _arg32 = data.readInt();
                            IAccessibilityInteractionConnectionCallback _arg42 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg52 = data.readInt();
                            int _arg62 = data.readInt();
                            long _arg72 = data.readLong();
                            MagnificationSpec _arg82 = (MagnificationSpec) data.readTypedObject(MagnificationSpec.CREATOR);
                            float[] _arg92 = data.createFloatArray();
                            data.enforceNoDataAvail();
                            findAccessibilityNodeInfosByViewId(_arg02, _arg12, _arg22, _arg32, _arg42, _arg52, _arg62, _arg72, _arg82, _arg92);
                            return true;
                        case 3:
                            long _arg03 = data.readLong();
                            String _arg13 = data.readString();
                            Region _arg23 = (Region) data.readTypedObject(Region.CREATOR);
                            int _arg33 = data.readInt();
                            IAccessibilityInteractionConnectionCallback _arg43 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg53 = data.readInt();
                            int _arg63 = data.readInt();
                            long _arg73 = data.readLong();
                            MagnificationSpec _arg83 = (MagnificationSpec) data.readTypedObject(MagnificationSpec.CREATOR);
                            float[] _arg93 = data.createFloatArray();
                            data.enforceNoDataAvail();
                            findAccessibilityNodeInfosByText(_arg03, _arg13, _arg23, _arg33, _arg43, _arg53, _arg63, _arg73, _arg83, _arg93);
                            return true;
                        case 4:
                            long _arg04 = data.readLong();
                            int _arg14 = data.readInt();
                            Region _arg24 = (Region) data.readTypedObject(Region.CREATOR);
                            int _arg34 = data.readInt();
                            IAccessibilityInteractionConnectionCallback _arg44 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg54 = data.readInt();
                            int _arg64 = data.readInt();
                            long _arg74 = data.readLong();
                            MagnificationSpec _arg84 = (MagnificationSpec) data.readTypedObject(MagnificationSpec.CREATOR);
                            float[] _arg94 = data.createFloatArray();
                            data.enforceNoDataAvail();
                            findFocus(_arg04, _arg14, _arg24, _arg34, _arg44, _arg54, _arg64, _arg74, _arg84, _arg94);
                            return true;
                        case 5:
                            long _arg05 = data.readLong();
                            int _arg15 = data.readInt();
                            Region _arg25 = (Region) data.readTypedObject(Region.CREATOR);
                            int _arg35 = data.readInt();
                            IAccessibilityInteractionConnectionCallback _arg45 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg55 = data.readInt();
                            int _arg65 = data.readInt();
                            long _arg75 = data.readLong();
                            MagnificationSpec _arg85 = (MagnificationSpec) data.readTypedObject(MagnificationSpec.CREATOR);
                            float[] _arg95 = data.createFloatArray();
                            data.enforceNoDataAvail();
                            focusSearch(_arg05, _arg15, _arg25, _arg35, _arg45, _arg55, _arg65, _arg75, _arg85, _arg95);
                            return true;
                        case 6:
                            long _arg06 = data.readLong();
                            int _arg16 = data.readInt();
                            Bundle _arg26 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg36 = data.readInt();
                            IAccessibilityInteractionConnectionCallback _arg46 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg56 = data.readInt();
                            int _arg66 = data.readInt();
                            long _arg76 = data.readLong();
                            data.enforceNoDataAvail();
                            performAccessibilityAction(_arg06, _arg16, _arg26, _arg36, _arg46, _arg56, _arg66, _arg76);
                            return true;
                        case 7:
                            clearAccessibilityFocus();
                            return true;
                        case 8:
                            notifyOutsideTouch();
                            return true;
                        case 9:
                            int _arg07 = data.readInt();
                            ScreenCapture.ScreenCaptureListener _arg17 = (ScreenCapture.ScreenCaptureListener) data.readTypedObject(ScreenCapture.ScreenCaptureListener.CREATOR);
                            IAccessibilityInteractionConnectionCallback _arg27 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            takeScreenshotOfWindow(_arg07, _arg17, _arg27);
                            return true;
                        case 10:
                            SurfaceControl _arg08 = (SurfaceControl) data.readTypedObject(SurfaceControl.CREATOR);
                            data.enforceNoDataAvail();
                            attachAccessibilityOverlayToWindow(_arg08);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IAccessibilityInteractionConnection {
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

            @Override // android.view.accessibility.IAccessibilityInteractionConnection
            public void findAccessibilityNodeInfoByAccessibilityId(long accessibilityNodeId, Region bounds, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec, float[] matrixValues, Bundle arguments) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(accessibilityNodeId);
                    try {
                        _data.writeTypedObject(bounds, 0);
                    } catch (Throwable th) {
                        th = th;
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
                try {
                    _data.writeInt(interactionId);
                    try {
                        _data.writeStrongInterface(callback);
                        try {
                            _data.writeInt(flags);
                            try {
                                _data.writeInt(interrogatingPid);
                            } catch (Throwable th3) {
                                th = th3;
                                _data.recycle();
                                throw th;
                            }
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
                    try {
                        _data.writeLong(interrogatingTid);
                        try {
                            _data.writeTypedObject(spec, 0);
                            try {
                                _data.writeFloatArray(matrixValues);
                                try {
                                    _data.writeTypedObject(arguments, 0);
                                } catch (Throwable th6) {
                                    th = th6;
                                }
                            } catch (Throwable th7) {
                                th = th7;
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th8) {
                            th = th8;
                            _data.recycle();
                            throw th;
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
                try {
                    this.mRemote.transact(1, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th11) {
                    th = th11;
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.accessibility.IAccessibilityInteractionConnection
            public void findAccessibilityNodeInfosByViewId(long accessibilityNodeId, String viewId, Region bounds, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec, float[] matrix) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(accessibilityNodeId);
                    try {
                        _data.writeString(viewId);
                    } catch (Throwable th) {
                        th = th;
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
                try {
                    _data.writeTypedObject(bounds, 0);
                    try {
                        _data.writeInt(interactionId);
                        try {
                            _data.writeStrongInterface(callback);
                            try {
                                _data.writeInt(flags);
                            } catch (Throwable th3) {
                                th = th3;
                                _data.recycle();
                                throw th;
                            }
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
                    try {
                        _data.writeInt(interrogatingPid);
                        try {
                            _data.writeLong(interrogatingTid);
                            try {
                                _data.writeTypedObject(spec, 0);
                                try {
                                    _data.writeFloatArray(matrix);
                                } catch (Throwable th6) {
                                    th = th6;
                                }
                            } catch (Throwable th7) {
                                th = th7;
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th8) {
                            th = th8;
                            _data.recycle();
                            throw th;
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
                try {
                    this.mRemote.transact(2, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th11) {
                    th = th11;
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.accessibility.IAccessibilityInteractionConnection
            public void findAccessibilityNodeInfosByText(long accessibilityNodeId, String text, Region bounds, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec, float[] matrixValues) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(accessibilityNodeId);
                    try {
                        _data.writeString(text);
                    } catch (Throwable th) {
                        th = th;
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
                try {
                    _data.writeTypedObject(bounds, 0);
                    try {
                        _data.writeInt(interactionId);
                        try {
                            _data.writeStrongInterface(callback);
                            try {
                                _data.writeInt(flags);
                            } catch (Throwable th3) {
                                th = th3;
                                _data.recycle();
                                throw th;
                            }
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
                    try {
                        _data.writeInt(interrogatingPid);
                        try {
                            _data.writeLong(interrogatingTid);
                            try {
                                _data.writeTypedObject(spec, 0);
                                try {
                                    _data.writeFloatArray(matrixValues);
                                } catch (Throwable th6) {
                                    th = th6;
                                }
                            } catch (Throwable th7) {
                                th = th7;
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th8) {
                            th = th8;
                            _data.recycle();
                            throw th;
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
                try {
                    this.mRemote.transact(3, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th11) {
                    th = th11;
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.accessibility.IAccessibilityInteractionConnection
            public void findFocus(long accessibilityNodeId, int focusType, Region bounds, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec, float[] matrixValues) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(accessibilityNodeId);
                    try {
                        _data.writeInt(focusType);
                    } catch (Throwable th) {
                        th = th;
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
                try {
                    _data.writeTypedObject(bounds, 0);
                    try {
                        _data.writeInt(interactionId);
                        try {
                            _data.writeStrongInterface(callback);
                            try {
                                _data.writeInt(flags);
                            } catch (Throwable th3) {
                                th = th3;
                                _data.recycle();
                                throw th;
                            }
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
                    try {
                        _data.writeInt(interrogatingPid);
                        try {
                            _data.writeLong(interrogatingTid);
                            try {
                                _data.writeTypedObject(spec, 0);
                                try {
                                    _data.writeFloatArray(matrixValues);
                                } catch (Throwable th6) {
                                    th = th6;
                                }
                            } catch (Throwable th7) {
                                th = th7;
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th8) {
                            th = th8;
                            _data.recycle();
                            throw th;
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
                try {
                    this.mRemote.transact(4, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th11) {
                    th = th11;
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.accessibility.IAccessibilityInteractionConnection
            public void focusSearch(long accessibilityNodeId, int direction, Region bounds, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec, float[] matrixValues) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(accessibilityNodeId);
                    try {
                        _data.writeInt(direction);
                    } catch (Throwable th) {
                        th = th;
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
                try {
                    _data.writeTypedObject(bounds, 0);
                    try {
                        _data.writeInt(interactionId);
                        try {
                            _data.writeStrongInterface(callback);
                            try {
                                _data.writeInt(flags);
                            } catch (Throwable th3) {
                                th = th3;
                                _data.recycle();
                                throw th;
                            }
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
                    try {
                        _data.writeInt(interrogatingPid);
                        try {
                            _data.writeLong(interrogatingTid);
                            try {
                                _data.writeTypedObject(spec, 0);
                                try {
                                    _data.writeFloatArray(matrixValues);
                                } catch (Throwable th6) {
                                    th = th6;
                                }
                            } catch (Throwable th7) {
                                th = th7;
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th8) {
                            th = th8;
                            _data.recycle();
                            throw th;
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
                try {
                    this.mRemote.transact(5, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th11) {
                    th = th11;
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.accessibility.IAccessibilityInteractionConnection
            public void performAccessibilityAction(long accessibilityNodeId, int action, Bundle arguments, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(accessibilityNodeId);
                    _data.writeInt(action);
                    _data.writeTypedObject(arguments, 0);
                    _data.writeInt(interactionId);
                    _data.writeStrongInterface(callback);
                    _data.writeInt(flags);
                    _data.writeInt(interrogatingPid);
                    _data.writeLong(interrogatingTid);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityInteractionConnection
            public void clearAccessibilityFocus() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityInteractionConnection
            public void notifyOutsideTouch() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityInteractionConnection
            public void takeScreenshotOfWindow(int interactionId, ScreenCapture.ScreenCaptureListener listener, IAccessibilityInteractionConnectionCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(interactionId);
                    _data.writeTypedObject(listener, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityInteractionConnection
            public void attachAccessibilityOverlayToWindow(SurfaceControl sc) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(sc, 0);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 9;
        }
    }
}
