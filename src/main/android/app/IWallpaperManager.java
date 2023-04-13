package android.app;

import android.app.ILocalWallpaperColorConsumer;
import android.app.IWallpaperManagerCallback;
import android.content.ComponentName;
import android.graphics.Rect;
import android.graphics.RectF;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes.dex */
public interface IWallpaperManager extends IInterface {
    void addOnLocalColorsChangedListener(ILocalWallpaperColorConsumer iLocalWallpaperColorConsumer, List<RectF> list, int i, int i2, int i3) throws RemoteException;

    void clearWallpaper(String str, int i, int i2) throws RemoteException;

    int getHeightHint(int i) throws RemoteException;

    String getName() throws RemoteException;

    @Deprecated
    ParcelFileDescriptor getWallpaper(String str, IWallpaperManagerCallback iWallpaperManagerCallback, int i, Bundle bundle, int i2) throws RemoteException;

    WallpaperColors getWallpaperColors(int i, int i2, int i3) throws RemoteException;

    float getWallpaperDimAmount() throws RemoteException;

    int getWallpaperIdForUser(int i, int i2) throws RemoteException;

    WallpaperInfo getWallpaperInfo(int i) throws RemoteException;

    ParcelFileDescriptor getWallpaperInfoFile(int i) throws RemoteException;

    WallpaperInfo getWallpaperInfoWithFlags(int i, int i2) throws RemoteException;

    ParcelFileDescriptor getWallpaperWithFeature(String str, String str2, IWallpaperManagerCallback iWallpaperManagerCallback, int i, Bundle bundle, int i2, boolean z) throws RemoteException;

    int getWidthHint(int i) throws RemoteException;

    boolean hasNamedWallpaper(String str) throws RemoteException;

    boolean isSetWallpaperAllowed(String str) throws RemoteException;

    boolean isWallpaperBackupEligible(int i, int i2) throws RemoteException;

    boolean isWallpaperSupported(String str) throws RemoteException;

    boolean lockScreenWallpaperExists() throws RemoteException;

    void notifyGoingToSleep(int i, int i2, Bundle bundle) throws RemoteException;

    void notifyWakingUp(int i, int i2, Bundle bundle) throws RemoteException;

    void registerWallpaperColorsCallback(IWallpaperManagerCallback iWallpaperManagerCallback, int i, int i2) throws RemoteException;

    void removeOnLocalColorsChangedListener(ILocalWallpaperColorConsumer iLocalWallpaperColorConsumer, List<RectF> list, int i, int i2, int i3) throws RemoteException;

    void setDimensionHints(int i, int i2, String str, int i3) throws RemoteException;

    void setDisplayPadding(Rect rect, String str, int i) throws RemoteException;

    void setInAmbientMode(boolean z, long j) throws RemoteException;

    boolean setLockWallpaperCallback(IWallpaperManagerCallback iWallpaperManagerCallback) throws RemoteException;

    ParcelFileDescriptor setWallpaper(String str, String str2, Rect rect, boolean z, Bundle bundle, int i, IWallpaperManagerCallback iWallpaperManagerCallback, int i2) throws RemoteException;

    void setWallpaperComponent(ComponentName componentName) throws RemoteException;

    void setWallpaperComponentChecked(ComponentName componentName, String str, int i, int i2) throws RemoteException;

    void setWallpaperDimAmount(float f) throws RemoteException;

    void settingsRestored() throws RemoteException;

    void unregisterWallpaperColorsCallback(IWallpaperManagerCallback iWallpaperManagerCallback, int i, int i2) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IWallpaperManager {
        @Override // android.app.IWallpaperManager
        public ParcelFileDescriptor setWallpaper(String name, String callingPackage, Rect cropHint, boolean allowBackup, Bundle extras, int which, IWallpaperManagerCallback completion, int userId) throws RemoteException {
            return null;
        }

        @Override // android.app.IWallpaperManager
        public void setWallpaperComponentChecked(ComponentName name, String callingPackage, int which, int userId) throws RemoteException {
        }

        @Override // android.app.IWallpaperManager
        public void setWallpaperComponent(ComponentName name) throws RemoteException {
        }

        @Override // android.app.IWallpaperManager
        public ParcelFileDescriptor getWallpaper(String callingPkg, IWallpaperManagerCallback cb, int which, Bundle outParams, int userId) throws RemoteException {
            return null;
        }

        @Override // android.app.IWallpaperManager
        public ParcelFileDescriptor getWallpaperWithFeature(String callingPkg, String callingFeatureId, IWallpaperManagerCallback cb, int which, Bundle outParams, int userId, boolean getCropped) throws RemoteException {
            return null;
        }

        @Override // android.app.IWallpaperManager
        public int getWallpaperIdForUser(int which, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.app.IWallpaperManager
        public WallpaperInfo getWallpaperInfo(int userId) throws RemoteException {
            return null;
        }

        @Override // android.app.IWallpaperManager
        public WallpaperInfo getWallpaperInfoWithFlags(int which, int userId) throws RemoteException {
            return null;
        }

        @Override // android.app.IWallpaperManager
        public ParcelFileDescriptor getWallpaperInfoFile(int userId) throws RemoteException {
            return null;
        }

        @Override // android.app.IWallpaperManager
        public void clearWallpaper(String callingPackage, int which, int userId) throws RemoteException {
        }

        @Override // android.app.IWallpaperManager
        public boolean hasNamedWallpaper(String name) throws RemoteException {
            return false;
        }

        @Override // android.app.IWallpaperManager
        public void setDimensionHints(int width, int height, String callingPackage, int displayId) throws RemoteException {
        }

        @Override // android.app.IWallpaperManager
        public int getWidthHint(int displayId) throws RemoteException {
            return 0;
        }

        @Override // android.app.IWallpaperManager
        public int getHeightHint(int displayId) throws RemoteException {
            return 0;
        }

        @Override // android.app.IWallpaperManager
        public void setDisplayPadding(Rect padding, String callingPackage, int displayId) throws RemoteException {
        }

        @Override // android.app.IWallpaperManager
        public String getName() throws RemoteException {
            return null;
        }

        @Override // android.app.IWallpaperManager
        public void settingsRestored() throws RemoteException {
        }

        @Override // android.app.IWallpaperManager
        public boolean isWallpaperSupported(String callingPackage) throws RemoteException {
            return false;
        }

        @Override // android.app.IWallpaperManager
        public boolean isSetWallpaperAllowed(String callingPackage) throws RemoteException {
            return false;
        }

        @Override // android.app.IWallpaperManager
        public boolean isWallpaperBackupEligible(int which, int userId) throws RemoteException {
            return false;
        }

        @Override // android.app.IWallpaperManager
        public boolean setLockWallpaperCallback(IWallpaperManagerCallback cb) throws RemoteException {
            return false;
        }

        @Override // android.app.IWallpaperManager
        public WallpaperColors getWallpaperColors(int which, int userId, int displayId) throws RemoteException {
            return null;
        }

        @Override // android.app.IWallpaperManager
        public void removeOnLocalColorsChangedListener(ILocalWallpaperColorConsumer callback, List<RectF> area, int which, int userId, int displayId) throws RemoteException {
        }

        @Override // android.app.IWallpaperManager
        public void addOnLocalColorsChangedListener(ILocalWallpaperColorConsumer callback, List<RectF> regions, int which, int userId, int displayId) throws RemoteException {
        }

        @Override // android.app.IWallpaperManager
        public void registerWallpaperColorsCallback(IWallpaperManagerCallback cb, int userId, int displayId) throws RemoteException {
        }

        @Override // android.app.IWallpaperManager
        public void unregisterWallpaperColorsCallback(IWallpaperManagerCallback cb, int userId, int displayId) throws RemoteException {
        }

        @Override // android.app.IWallpaperManager
        public void setInAmbientMode(boolean inAmbientMode, long animationDuration) throws RemoteException {
        }

        @Override // android.app.IWallpaperManager
        public void notifyWakingUp(int x, int y, Bundle extras) throws RemoteException {
        }

        @Override // android.app.IWallpaperManager
        public void notifyGoingToSleep(int x, int y, Bundle extras) throws RemoteException {
        }

        @Override // android.app.IWallpaperManager
        public void setWallpaperDimAmount(float dimAmount) throws RemoteException {
        }

        @Override // android.app.IWallpaperManager
        public float getWallpaperDimAmount() throws RemoteException {
            return 0.0f;
        }

        @Override // android.app.IWallpaperManager
        public boolean lockScreenWallpaperExists() throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IWallpaperManager {
        public static final String DESCRIPTOR = "android.app.IWallpaperManager";
        static final int TRANSACTION_addOnLocalColorsChangedListener = 24;
        static final int TRANSACTION_clearWallpaper = 10;
        static final int TRANSACTION_getHeightHint = 14;
        static final int TRANSACTION_getName = 16;
        static final int TRANSACTION_getWallpaper = 4;
        static final int TRANSACTION_getWallpaperColors = 22;
        static final int TRANSACTION_getWallpaperDimAmount = 31;
        static final int TRANSACTION_getWallpaperIdForUser = 6;
        static final int TRANSACTION_getWallpaperInfo = 7;
        static final int TRANSACTION_getWallpaperInfoFile = 9;
        static final int TRANSACTION_getWallpaperInfoWithFlags = 8;
        static final int TRANSACTION_getWallpaperWithFeature = 5;
        static final int TRANSACTION_getWidthHint = 13;
        static final int TRANSACTION_hasNamedWallpaper = 11;
        static final int TRANSACTION_isSetWallpaperAllowed = 19;
        static final int TRANSACTION_isWallpaperBackupEligible = 20;
        static final int TRANSACTION_isWallpaperSupported = 18;
        static final int TRANSACTION_lockScreenWallpaperExists = 32;
        static final int TRANSACTION_notifyGoingToSleep = 29;
        static final int TRANSACTION_notifyWakingUp = 28;
        static final int TRANSACTION_registerWallpaperColorsCallback = 25;
        static final int TRANSACTION_removeOnLocalColorsChangedListener = 23;
        static final int TRANSACTION_setDimensionHints = 12;
        static final int TRANSACTION_setDisplayPadding = 15;
        static final int TRANSACTION_setInAmbientMode = 27;
        static final int TRANSACTION_setLockWallpaperCallback = 21;
        static final int TRANSACTION_setWallpaper = 1;
        static final int TRANSACTION_setWallpaperComponent = 3;
        static final int TRANSACTION_setWallpaperComponentChecked = 2;
        static final int TRANSACTION_setWallpaperDimAmount = 30;
        static final int TRANSACTION_settingsRestored = 17;
        static final int TRANSACTION_unregisterWallpaperColorsCallback = 26;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IWallpaperManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IWallpaperManager)) {
                return (IWallpaperManager) iin;
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
                    return "setWallpaper";
                case 2:
                    return "setWallpaperComponentChecked";
                case 3:
                    return "setWallpaperComponent";
                case 4:
                    return "getWallpaper";
                case 5:
                    return "getWallpaperWithFeature";
                case 6:
                    return "getWallpaperIdForUser";
                case 7:
                    return "getWallpaperInfo";
                case 8:
                    return "getWallpaperInfoWithFlags";
                case 9:
                    return "getWallpaperInfoFile";
                case 10:
                    return "clearWallpaper";
                case 11:
                    return "hasNamedWallpaper";
                case 12:
                    return "setDimensionHints";
                case 13:
                    return "getWidthHint";
                case 14:
                    return "getHeightHint";
                case 15:
                    return "setDisplayPadding";
                case 16:
                    return "getName";
                case 17:
                    return "settingsRestored";
                case 18:
                    return "isWallpaperSupported";
                case 19:
                    return "isSetWallpaperAllowed";
                case 20:
                    return "isWallpaperBackupEligible";
                case 21:
                    return "setLockWallpaperCallback";
                case 22:
                    return "getWallpaperColors";
                case 23:
                    return "removeOnLocalColorsChangedListener";
                case 24:
                    return "addOnLocalColorsChangedListener";
                case 25:
                    return "registerWallpaperColorsCallback";
                case 26:
                    return "unregisterWallpaperColorsCallback";
                case 27:
                    return "setInAmbientMode";
                case 28:
                    return "notifyWakingUp";
                case 29:
                    return "notifyGoingToSleep";
                case 30:
                    return "setWallpaperDimAmount";
                case 31:
                    return "getWallpaperDimAmount";
                case 32:
                    return "lockScreenWallpaperExists";
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
                            String _arg0 = data.readString();
                            String _arg1 = data.readString();
                            Rect _arg2 = (Rect) data.readTypedObject(Rect.CREATOR);
                            boolean _arg3 = data.readBoolean();
                            Bundle _arg4 = new Bundle();
                            int _arg5 = data.readInt();
                            IWallpaperManagerCallback _arg6 = IWallpaperManagerCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg7 = data.readInt();
                            data.enforceNoDataAvail();
                            ParcelFileDescriptor _result = setWallpaper(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            reply.writeTypedObject(_arg4, 1);
                            break;
                        case 2:
                            ComponentName _arg02 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg12 = data.readString();
                            int _arg22 = data.readInt();
                            int _arg32 = data.readInt();
                            data.enforceNoDataAvail();
                            setWallpaperComponentChecked(_arg02, _arg12, _arg22, _arg32);
                            reply.writeNoException();
                            break;
                        case 3:
                            ComponentName _arg03 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            setWallpaperComponent(_arg03);
                            reply.writeNoException();
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            IWallpaperManagerCallback _arg13 = IWallpaperManagerCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg23 = data.readInt();
                            Bundle _arg33 = new Bundle();
                            int _arg42 = data.readInt();
                            data.enforceNoDataAvail();
                            ParcelFileDescriptor _result2 = getWallpaper(_arg04, _arg13, _arg23, _arg33, _arg42);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            reply.writeTypedObject(_arg33, 1);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            String _arg14 = data.readString();
                            IWallpaperManagerCallback _arg24 = IWallpaperManagerCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg34 = data.readInt();
                            Bundle _arg43 = new Bundle();
                            int _arg52 = data.readInt();
                            boolean _arg62 = data.readBoolean();
                            data.enforceNoDataAvail();
                            ParcelFileDescriptor _result3 = getWallpaperWithFeature(_arg05, _arg14, _arg24, _arg34, _arg43, _arg52, _arg62);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            reply.writeTypedObject(_arg43, 1);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result4 = getWallpaperIdForUser(_arg06, _arg15);
                            reply.writeNoException();
                            reply.writeInt(_result4);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            WallpaperInfo _result5 = getWallpaperInfo(_arg07);
                            reply.writeNoException();
                            reply.writeTypedObject(_result5, 1);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            WallpaperInfo _result6 = getWallpaperInfoWithFlags(_arg08, _arg16);
                            reply.writeNoException();
                            reply.writeTypedObject(_result6, 1);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            data.enforceNoDataAvail();
                            ParcelFileDescriptor _result7 = getWallpaperInfoFile(_arg09);
                            reply.writeNoException();
                            reply.writeTypedObject(_result7, 1);
                            break;
                        case 10:
                            String _arg010 = data.readString();
                            int _arg17 = data.readInt();
                            int _arg25 = data.readInt();
                            data.enforceNoDataAvail();
                            clearWallpaper(_arg010, _arg17, _arg25);
                            reply.writeNoException();
                            break;
                        case 11:
                            String _arg011 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result8 = hasNamedWallpaper(_arg011);
                            reply.writeNoException();
                            reply.writeBoolean(_result8);
                            break;
                        case 12:
                            int _arg012 = data.readInt();
                            int _arg18 = data.readInt();
                            String _arg26 = data.readString();
                            int _arg35 = data.readInt();
                            data.enforceNoDataAvail();
                            setDimensionHints(_arg012, _arg18, _arg26, _arg35);
                            reply.writeNoException();
                            break;
                        case 13:
                            int _arg013 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result9 = getWidthHint(_arg013);
                            reply.writeNoException();
                            reply.writeInt(_result9);
                            break;
                        case 14:
                            int _arg014 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result10 = getHeightHint(_arg014);
                            reply.writeNoException();
                            reply.writeInt(_result10);
                            break;
                        case 15:
                            Rect _arg015 = (Rect) data.readTypedObject(Rect.CREATOR);
                            String _arg19 = data.readString();
                            int _arg27 = data.readInt();
                            data.enforceNoDataAvail();
                            setDisplayPadding(_arg015, _arg19, _arg27);
                            reply.writeNoException();
                            break;
                        case 16:
                            String _result11 = getName();
                            reply.writeNoException();
                            reply.writeString(_result11);
                            break;
                        case 17:
                            settingsRestored();
                            reply.writeNoException();
                            break;
                        case 18:
                            String _arg016 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result12 = isWallpaperSupported(_arg016);
                            reply.writeNoException();
                            reply.writeBoolean(_result12);
                            break;
                        case 19:
                            String _arg017 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result13 = isSetWallpaperAllowed(_arg017);
                            reply.writeNoException();
                            reply.writeBoolean(_result13);
                            break;
                        case 20:
                            int _arg018 = data.readInt();
                            int _arg110 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result14 = isWallpaperBackupEligible(_arg018, _arg110);
                            reply.writeNoException();
                            reply.writeBoolean(_result14);
                            break;
                        case 21:
                            IWallpaperManagerCallback _arg019 = IWallpaperManagerCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result15 = setLockWallpaperCallback(_arg019);
                            reply.writeNoException();
                            reply.writeBoolean(_result15);
                            break;
                        case 22:
                            int _arg020 = data.readInt();
                            int _arg111 = data.readInt();
                            int _arg28 = data.readInt();
                            data.enforceNoDataAvail();
                            WallpaperColors _result16 = getWallpaperColors(_arg020, _arg111, _arg28);
                            reply.writeNoException();
                            reply.writeTypedObject(_result16, 1);
                            break;
                        case 23:
                            ILocalWallpaperColorConsumer _arg021 = ILocalWallpaperColorConsumer.Stub.asInterface(data.readStrongBinder());
                            List<RectF> _arg112 = data.createTypedArrayList(RectF.CREATOR);
                            int _arg29 = data.readInt();
                            int _arg36 = data.readInt();
                            int _arg44 = data.readInt();
                            data.enforceNoDataAvail();
                            removeOnLocalColorsChangedListener(_arg021, _arg112, _arg29, _arg36, _arg44);
                            reply.writeNoException();
                            break;
                        case 24:
                            ILocalWallpaperColorConsumer _arg022 = ILocalWallpaperColorConsumer.Stub.asInterface(data.readStrongBinder());
                            List<RectF> _arg113 = data.createTypedArrayList(RectF.CREATOR);
                            int _arg210 = data.readInt();
                            int _arg37 = data.readInt();
                            int _arg45 = data.readInt();
                            data.enforceNoDataAvail();
                            addOnLocalColorsChangedListener(_arg022, _arg113, _arg210, _arg37, _arg45);
                            reply.writeNoException();
                            break;
                        case 25:
                            IWallpaperManagerCallback _arg023 = IWallpaperManagerCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg114 = data.readInt();
                            int _arg211 = data.readInt();
                            data.enforceNoDataAvail();
                            registerWallpaperColorsCallback(_arg023, _arg114, _arg211);
                            reply.writeNoException();
                            break;
                        case 26:
                            IWallpaperManagerCallback _arg024 = IWallpaperManagerCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg115 = data.readInt();
                            int _arg212 = data.readInt();
                            data.enforceNoDataAvail();
                            unregisterWallpaperColorsCallback(_arg024, _arg115, _arg212);
                            reply.writeNoException();
                            break;
                        case 27:
                            boolean _arg025 = data.readBoolean();
                            long _arg116 = data.readLong();
                            data.enforceNoDataAvail();
                            setInAmbientMode(_arg025, _arg116);
                            break;
                        case 28:
                            int _arg026 = data.readInt();
                            int _arg117 = data.readInt();
                            Bundle _arg213 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            notifyWakingUp(_arg026, _arg117, _arg213);
                            break;
                        case 29:
                            int _arg027 = data.readInt();
                            int _arg118 = data.readInt();
                            Bundle _arg214 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            notifyGoingToSleep(_arg027, _arg118, _arg214);
                            reply.writeNoException();
                            break;
                        case 30:
                            float _arg028 = data.readFloat();
                            data.enforceNoDataAvail();
                            setWallpaperDimAmount(_arg028);
                            break;
                        case 31:
                            float _result17 = getWallpaperDimAmount();
                            reply.writeNoException();
                            reply.writeFloat(_result17);
                            break;
                        case 32:
                            boolean _result18 = lockScreenWallpaperExists();
                            reply.writeNoException();
                            reply.writeBoolean(_result18);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements IWallpaperManager {
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

            @Override // android.app.IWallpaperManager
            public ParcelFileDescriptor setWallpaper(String name, String callingPackage, Rect cropHint, boolean allowBackup, Bundle extras, int which, IWallpaperManagerCallback completion, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeString(callingPackage);
                    _data.writeTypedObject(cropHint, 0);
                    _data.writeBoolean(allowBackup);
                    _data.writeInt(which);
                    _data.writeStrongInterface(completion);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    ParcelFileDescriptor _result = (ParcelFileDescriptor) _reply.readTypedObject(ParcelFileDescriptor.CREATOR);
                    if (_reply.readInt() != 0) {
                        extras.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public void setWallpaperComponentChecked(ComponentName name, String callingPackage, int which, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(name, 0);
                    _data.writeString(callingPackage);
                    _data.writeInt(which);
                    _data.writeInt(userId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public void setWallpaperComponent(ComponentName name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(name, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public ParcelFileDescriptor getWallpaper(String callingPkg, IWallpaperManagerCallback cb, int which, Bundle outParams, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    _data.writeStrongInterface(cb);
                    _data.writeInt(which);
                    _data.writeInt(userId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    ParcelFileDescriptor _result = (ParcelFileDescriptor) _reply.readTypedObject(ParcelFileDescriptor.CREATOR);
                    if (_reply.readInt() != 0) {
                        outParams.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public ParcelFileDescriptor getWallpaperWithFeature(String callingPkg, String callingFeatureId, IWallpaperManagerCallback cb, int which, Bundle outParams, int userId, boolean getCropped) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    _data.writeString(callingFeatureId);
                    _data.writeStrongInterface(cb);
                    _data.writeInt(which);
                    _data.writeInt(userId);
                    _data.writeBoolean(getCropped);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    ParcelFileDescriptor _result = (ParcelFileDescriptor) _reply.readTypedObject(ParcelFileDescriptor.CREATOR);
                    if (_reply.readInt() != 0) {
                        outParams.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public int getWallpaperIdForUser(int which, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(which);
                    _data.writeInt(userId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public WallpaperInfo getWallpaperInfo(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    WallpaperInfo _result = (WallpaperInfo) _reply.readTypedObject(WallpaperInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public WallpaperInfo getWallpaperInfoWithFlags(int which, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(which);
                    _data.writeInt(userId);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    WallpaperInfo _result = (WallpaperInfo) _reply.readTypedObject(WallpaperInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public ParcelFileDescriptor getWallpaperInfoFile(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    ParcelFileDescriptor _result = (ParcelFileDescriptor) _reply.readTypedObject(ParcelFileDescriptor.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public void clearWallpaper(String callingPackage, int which, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeInt(which);
                    _data.writeInt(userId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public boolean hasNamedWallpaper(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public void setDimensionHints(int width, int height, String callingPackage, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    _data.writeString(callingPackage);
                    _data.writeInt(displayId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public int getWidthHint(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public int getHeightHint(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public void setDisplayPadding(Rect padding, String callingPackage, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(padding, 0);
                    _data.writeString(callingPackage);
                    _data.writeInt(displayId);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public String getName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public void settingsRestored() throws RemoteException {
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

            @Override // android.app.IWallpaperManager
            public boolean isWallpaperSupported(String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public boolean isSetWallpaperAllowed(String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public boolean isWallpaperBackupEligible(int which, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(which);
                    _data.writeInt(userId);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public boolean setLockWallpaperCallback(IWallpaperManagerCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(cb);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public WallpaperColors getWallpaperColors(int which, int userId, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(which);
                    _data.writeInt(userId);
                    _data.writeInt(displayId);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    WallpaperColors _result = (WallpaperColors) _reply.readTypedObject(WallpaperColors.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public void removeOnLocalColorsChangedListener(ILocalWallpaperColorConsumer callback, List<RectF> area, int which, int userId, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeTypedList(area, 0);
                    _data.writeInt(which);
                    _data.writeInt(userId);
                    _data.writeInt(displayId);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public void addOnLocalColorsChangedListener(ILocalWallpaperColorConsumer callback, List<RectF> regions, int which, int userId, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeTypedList(regions, 0);
                    _data.writeInt(which);
                    _data.writeInt(userId);
                    _data.writeInt(displayId);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public void registerWallpaperColorsCallback(IWallpaperManagerCallback cb, int userId, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(cb);
                    _data.writeInt(userId);
                    _data.writeInt(displayId);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public void unregisterWallpaperColorsCallback(IWallpaperManagerCallback cb, int userId, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(cb);
                    _data.writeInt(userId);
                    _data.writeInt(displayId);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public void setInAmbientMode(boolean inAmbientMode, long animationDuration) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(inAmbientMode);
                    _data.writeLong(animationDuration);
                    this.mRemote.transact(27, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public void notifyWakingUp(int x, int y, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(x);
                    _data.writeInt(y);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(28, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public void notifyGoingToSleep(int x, int y, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(x);
                    _data.writeInt(y);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public void setWallpaperDimAmount(float dimAmount) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloat(dimAmount);
                    this.mRemote.transact(30, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public float getWallpaperDimAmount() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    float _result = _reply.readFloat();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IWallpaperManager
            public boolean lockScreenWallpaperExists() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 31;
        }
    }
}
