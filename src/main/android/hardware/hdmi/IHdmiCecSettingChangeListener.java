package android.hardware.hdmi;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IHdmiCecSettingChangeListener extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.hdmi.IHdmiCecSettingChangeListener";

    void onChange(String str) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IHdmiCecSettingChangeListener {
        @Override // android.hardware.hdmi.IHdmiCecSettingChangeListener
        public void onChange(String setting) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IHdmiCecSettingChangeListener {
        static final int TRANSACTION_onChange = 1;

        public Stub() {
            attachInterface(this, IHdmiCecSettingChangeListener.DESCRIPTOR);
        }

        public static IHdmiCecSettingChangeListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IHdmiCecSettingChangeListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IHdmiCecSettingChangeListener)) {
                return (IHdmiCecSettingChangeListener) iin;
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
                    return "onChange";
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
                data.enforceInterface(IHdmiCecSettingChangeListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IHdmiCecSettingChangeListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            data.enforceNoDataAvail();
                            onChange(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IHdmiCecSettingChangeListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IHdmiCecSettingChangeListener.DESCRIPTOR;
            }

            @Override // android.hardware.hdmi.IHdmiCecSettingChangeListener
            public void onChange(String setting) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IHdmiCecSettingChangeListener.DESCRIPTOR);
                    _data.writeString(setting);
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
