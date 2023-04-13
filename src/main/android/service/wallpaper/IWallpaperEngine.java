package android.service.wallpaper;

import android.graphics.Rect;
import android.graphics.RectF;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.MotionEvent;
import android.view.SurfaceControl;
import java.util.List;
/* loaded from: classes3.dex */
public interface IWallpaperEngine extends IInterface {
    void addLocalColorsAreas(List<RectF> list) throws RemoteException;

    void applyDimming(float f) throws RemoteException;

    void destroy() throws RemoteException;

    void dispatchPointer(MotionEvent motionEvent) throws RemoteException;

    void dispatchWallpaperCommand(String str, int i, int i2, int i3, Bundle bundle) throws RemoteException;

    SurfaceControl mirrorSurfaceControl() throws RemoteException;

    void removeLocalColorsAreas(List<RectF> list) throws RemoteException;

    void requestWallpaperColors() throws RemoteException;

    void resizePreview(Rect rect) throws RemoteException;

    void setDesiredSize(int i, int i2) throws RemoteException;

    void setDisplayPadding(Rect rect) throws RemoteException;

    void setInAmbientMode(boolean z, long j) throws RemoteException;

    void setVisibility(boolean z) throws RemoteException;

    void setWallpaperFlags(int i) throws RemoteException;

    void setZoomOut(float f) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IWallpaperEngine {
        @Override // android.service.wallpaper.IWallpaperEngine
        public void setDesiredSize(int width, int height) throws RemoteException {
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void setDisplayPadding(Rect padding) throws RemoteException {
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void setVisibility(boolean visible) throws RemoteException {
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void setInAmbientMode(boolean inAmbientDisplay, long animationDuration) throws RemoteException {
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void dispatchPointer(MotionEvent event) throws RemoteException {
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void dispatchWallpaperCommand(String action, int x, int y, int z, Bundle extras) throws RemoteException {
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void requestWallpaperColors() throws RemoteException {
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void destroy() throws RemoteException {
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void setZoomOut(float scale) throws RemoteException {
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void resizePreview(Rect positionInWindow) throws RemoteException {
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void removeLocalColorsAreas(List<RectF> regions) throws RemoteException {
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void addLocalColorsAreas(List<RectF> regions) throws RemoteException {
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public SurfaceControl mirrorSurfaceControl() throws RemoteException {
            return null;
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void applyDimming(float dimAmount) throws RemoteException {
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void setWallpaperFlags(int which) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IWallpaperEngine {
        public static final String DESCRIPTOR = "android.service.wallpaper.IWallpaperEngine";
        static final int TRANSACTION_addLocalColorsAreas = 12;
        static final int TRANSACTION_applyDimming = 14;
        static final int TRANSACTION_destroy = 8;
        static final int TRANSACTION_dispatchPointer = 5;
        static final int TRANSACTION_dispatchWallpaperCommand = 6;
        static final int TRANSACTION_mirrorSurfaceControl = 13;
        static final int TRANSACTION_removeLocalColorsAreas = 11;
        static final int TRANSACTION_requestWallpaperColors = 7;
        static final int TRANSACTION_resizePreview = 10;
        static final int TRANSACTION_setDesiredSize = 1;
        static final int TRANSACTION_setDisplayPadding = 2;
        static final int TRANSACTION_setInAmbientMode = 4;
        static final int TRANSACTION_setVisibility = 3;
        static final int TRANSACTION_setWallpaperFlags = 15;
        static final int TRANSACTION_setZoomOut = 9;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IWallpaperEngine asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IWallpaperEngine)) {
                return (IWallpaperEngine) iin;
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
                    return "setDesiredSize";
                case 2:
                    return "setDisplayPadding";
                case 3:
                    return "setVisibility";
                case 4:
                    return "setInAmbientMode";
                case 5:
                    return "dispatchPointer";
                case 6:
                    return "dispatchWallpaperCommand";
                case 7:
                    return "requestWallpaperColors";
                case 8:
                    return "destroy";
                case 9:
                    return "setZoomOut";
                case 10:
                    return "resizePreview";
                case 11:
                    return "removeLocalColorsAreas";
                case 12:
                    return "addLocalColorsAreas";
                case 13:
                    return "mirrorSurfaceControl";
                case 14:
                    return "applyDimming";
                case 15:
                    return "setWallpaperFlags";
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
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            setDesiredSize(_arg0, _arg1);
                            break;
                        case 2:
                            Rect _arg02 = (Rect) data.readTypedObject(Rect.CREATOR);
                            data.enforceNoDataAvail();
                            setDisplayPadding(_arg02);
                            break;
                        case 3:
                            boolean _arg03 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setVisibility(_arg03);
                            break;
                        case 4:
                            boolean _arg04 = data.readBoolean();
                            long _arg12 = data.readLong();
                            data.enforceNoDataAvail();
                            setInAmbientMode(_arg04, _arg12);
                            break;
                        case 5:
                            MotionEvent _arg05 = (MotionEvent) data.readTypedObject(MotionEvent.CREATOR);
                            data.enforceNoDataAvail();
                            dispatchPointer(_arg05);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            int _arg13 = data.readInt();
                            int _arg2 = data.readInt();
                            int _arg3 = data.readInt();
                            Bundle _arg4 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            dispatchWallpaperCommand(_arg06, _arg13, _arg2, _arg3, _arg4);
                            break;
                        case 7:
                            requestWallpaperColors();
                            break;
                        case 8:
                            destroy();
                            break;
                        case 9:
                            float _arg07 = data.readFloat();
                            data.enforceNoDataAvail();
                            setZoomOut(_arg07);
                            break;
                        case 10:
                            Rect _arg08 = (Rect) data.readTypedObject(Rect.CREATOR);
                            data.enforceNoDataAvail();
                            resizePreview(_arg08);
                            break;
                        case 11:
                            List<RectF> _arg09 = data.createTypedArrayList(RectF.CREATOR);
                            data.enforceNoDataAvail();
                            removeLocalColorsAreas(_arg09);
                            break;
                        case 12:
                            List<RectF> _arg010 = data.createTypedArrayList(RectF.CREATOR);
                            data.enforceNoDataAvail();
                            addLocalColorsAreas(_arg010);
                            break;
                        case 13:
                            SurfaceControl _result = mirrorSurfaceControl();
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 14:
                            float _arg011 = data.readFloat();
                            data.enforceNoDataAvail();
                            applyDimming(_arg011);
                            break;
                        case 15:
                            int _arg012 = data.readInt();
                            data.enforceNoDataAvail();
                            setWallpaperFlags(_arg012);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IWallpaperEngine {
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

            @Override // android.service.wallpaper.IWallpaperEngine
            public void setDesiredSize(int width, int height) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.wallpaper.IWallpaperEngine
            public void setDisplayPadding(Rect padding) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(padding, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.wallpaper.IWallpaperEngine
            public void setVisibility(boolean visible) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(visible);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.wallpaper.IWallpaperEngine
            public void setInAmbientMode(boolean inAmbientDisplay, long animationDuration) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(inAmbientDisplay);
                    _data.writeLong(animationDuration);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.wallpaper.IWallpaperEngine
            public void dispatchPointer(MotionEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(event, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.wallpaper.IWallpaperEngine
            public void dispatchWallpaperCommand(String action, int x, int y, int z, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(action);
                    _data.writeInt(x);
                    _data.writeInt(y);
                    _data.writeInt(z);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.wallpaper.IWallpaperEngine
            public void requestWallpaperColors() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.wallpaper.IWallpaperEngine
            public void destroy() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.wallpaper.IWallpaperEngine
            public void setZoomOut(float scale) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloat(scale);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.wallpaper.IWallpaperEngine
            public void resizePreview(Rect positionInWindow) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(positionInWindow, 0);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.wallpaper.IWallpaperEngine
            public void removeLocalColorsAreas(List<RectF> regions) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(regions, 0);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.wallpaper.IWallpaperEngine
            public void addLocalColorsAreas(List<RectF> regions) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(regions, 0);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.wallpaper.IWallpaperEngine
            public SurfaceControl mirrorSurfaceControl() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    SurfaceControl _result = (SurfaceControl) _reply.readTypedObject(SurfaceControl.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.wallpaper.IWallpaperEngine
            public void applyDimming(float dimAmount) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloat(dimAmount);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.wallpaper.IWallpaperEngine
            public void setWallpaperFlags(int which) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(which);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 14;
        }
    }
}
