package android.hardware.radio;

import android.hardware.radio.IAnnouncementListener;
import android.hardware.radio.ICloseHandle;
import android.hardware.radio.ITuner;
import android.hardware.radio.ITunerCallback;
import android.hardware.radio.RadioManager;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes2.dex */
public interface IRadioService extends IInterface {
    ICloseHandle addAnnouncementListener(int[] iArr, IAnnouncementListener iAnnouncementListener) throws RemoteException;

    List<RadioManager.ModuleProperties> listModules() throws RemoteException;

    ITuner openTuner(int i, RadioManager.BandConfig bandConfig, boolean z, ITunerCallback iTunerCallback, int i2) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IRadioService {
        @Override // android.hardware.radio.IRadioService
        public List<RadioManager.ModuleProperties> listModules() throws RemoteException {
            return null;
        }

        @Override // android.hardware.radio.IRadioService
        public ITuner openTuner(int moduleId, RadioManager.BandConfig bandConfig, boolean withAudio, ITunerCallback callback, int targetSdkVersion) throws RemoteException {
            return null;
        }

        @Override // android.hardware.radio.IRadioService
        public ICloseHandle addAnnouncementListener(int[] enabledTypes, IAnnouncementListener listener) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRadioService {
        public static final String DESCRIPTOR = "android.hardware.radio.IRadioService";
        static final int TRANSACTION_addAnnouncementListener = 3;
        static final int TRANSACTION_listModules = 1;
        static final int TRANSACTION_openTuner = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IRadioService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRadioService)) {
                return (IRadioService) iin;
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
                    return "listModules";
                case 2:
                    return "openTuner";
                case 3:
                    return "addAnnouncementListener";
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
                            List<RadioManager.ModuleProperties> _result = listModules();
                            reply.writeNoException();
                            reply.writeTypedList(_result, 1);
                            break;
                        case 2:
                            int _arg0 = data.readInt();
                            RadioManager.BandConfig _arg1 = (RadioManager.BandConfig) data.readTypedObject(RadioManager.BandConfig.CREATOR);
                            boolean _arg2 = data.readBoolean();
                            ITunerCallback _arg3 = ITunerCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg4 = data.readInt();
                            data.enforceNoDataAvail();
                            ITuner _result2 = openTuner(_arg0, _arg1, _arg2, _arg3, _arg4);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result2);
                            break;
                        case 3:
                            int[] _arg02 = data.createIntArray();
                            IAnnouncementListener _arg12 = IAnnouncementListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            ICloseHandle _result3 = addAnnouncementListener(_arg02, _arg12);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result3);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRadioService {
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

            @Override // android.hardware.radio.IRadioService
            public List<RadioManager.ModuleProperties> listModules() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    List<RadioManager.ModuleProperties> _result = _reply.createTypedArrayList(RadioManager.ModuleProperties.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.IRadioService
            public ITuner openTuner(int moduleId, RadioManager.BandConfig bandConfig, boolean withAudio, ITunerCallback callback, int targetSdkVersion) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(moduleId);
                    _data.writeTypedObject(bandConfig, 0);
                    _data.writeBoolean(withAudio);
                    _data.writeStrongInterface(callback);
                    _data.writeInt(targetSdkVersion);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    ITuner _result = ITuner.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.IRadioService
            public ICloseHandle addAnnouncementListener(int[] enabledTypes, IAnnouncementListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(enabledTypes);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    ICloseHandle _result = ICloseHandle.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 2;
        }
    }
}
