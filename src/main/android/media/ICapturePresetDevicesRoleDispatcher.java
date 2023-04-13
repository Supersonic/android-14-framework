package android.media;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes2.dex */
public interface ICapturePresetDevicesRoleDispatcher extends IInterface {
    public static final String DESCRIPTOR = "android.media.ICapturePresetDevicesRoleDispatcher";

    void dispatchDevicesRoleChanged(int i, int i2, List<AudioDeviceAttributes> list) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ICapturePresetDevicesRoleDispatcher {
        @Override // android.media.ICapturePresetDevicesRoleDispatcher
        public void dispatchDevicesRoleChanged(int capturePreset, int role, List<AudioDeviceAttributes> devices) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ICapturePresetDevicesRoleDispatcher {
        static final int TRANSACTION_dispatchDevicesRoleChanged = 1;

        public Stub() {
            attachInterface(this, ICapturePresetDevicesRoleDispatcher.DESCRIPTOR);
        }

        public static ICapturePresetDevicesRoleDispatcher asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ICapturePresetDevicesRoleDispatcher.DESCRIPTOR);
            if (iin != null && (iin instanceof ICapturePresetDevicesRoleDispatcher)) {
                return (ICapturePresetDevicesRoleDispatcher) iin;
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
                    return "dispatchDevicesRoleChanged";
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
                data.enforceInterface(ICapturePresetDevicesRoleDispatcher.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ICapturePresetDevicesRoleDispatcher.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            int _arg1 = data.readInt();
                            List<AudioDeviceAttributes> _arg2 = data.createTypedArrayList(AudioDeviceAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            dispatchDevicesRoleChanged(_arg0, _arg1, _arg2);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements ICapturePresetDevicesRoleDispatcher {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ICapturePresetDevicesRoleDispatcher.DESCRIPTOR;
            }

            @Override // android.media.ICapturePresetDevicesRoleDispatcher
            public void dispatchDevicesRoleChanged(int capturePreset, int role, List<AudioDeviceAttributes> devices) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICapturePresetDevicesRoleDispatcher.DESCRIPTOR);
                    _data.writeInt(capturePreset);
                    _data.writeInt(role);
                    _data.writeTypedList(devices, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 0;
        }
    }
}
