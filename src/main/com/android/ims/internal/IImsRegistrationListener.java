package com.android.ims.internal;

import android.net.Uri;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.ims.ImsReasonInfo;
/* loaded from: classes4.dex */
public interface IImsRegistrationListener extends IInterface {
    void registrationAssociatedUriChanged(Uri[] uriArr) throws RemoteException;

    void registrationChangeFailed(int i, ImsReasonInfo imsReasonInfo) throws RemoteException;

    @Deprecated
    void registrationConnected() throws RemoteException;

    void registrationConnectedWithRadioTech(int i) throws RemoteException;

    void registrationDisconnected(ImsReasonInfo imsReasonInfo) throws RemoteException;

    void registrationFeatureCapabilityChanged(int i, int[] iArr, int[] iArr2) throws RemoteException;

    @Deprecated
    void registrationProgressing() throws RemoteException;

    void registrationProgressingWithRadioTech(int i) throws RemoteException;

    void registrationResumed() throws RemoteException;

    void registrationServiceCapabilityChanged(int i, int i2) throws RemoteException;

    void registrationSuspended() throws RemoteException;

    void voiceMessageCountUpdate(int i) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IImsRegistrationListener {
        @Override // com.android.ims.internal.IImsRegistrationListener
        public void registrationConnected() throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsRegistrationListener
        public void registrationProgressing() throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsRegistrationListener
        public void registrationConnectedWithRadioTech(int imsRadioTech) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsRegistrationListener
        public void registrationProgressingWithRadioTech(int imsRadioTech) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsRegistrationListener
        public void registrationDisconnected(ImsReasonInfo imsReasonInfo) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsRegistrationListener
        public void registrationResumed() throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsRegistrationListener
        public void registrationSuspended() throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsRegistrationListener
        public void registrationServiceCapabilityChanged(int serviceClass, int event) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsRegistrationListener
        public void registrationFeatureCapabilityChanged(int serviceClass, int[] enabledFeatures, int[] disabledFeatures) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsRegistrationListener
        public void voiceMessageCountUpdate(int count) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsRegistrationListener
        public void registrationAssociatedUriChanged(Uri[] uris) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsRegistrationListener
        public void registrationChangeFailed(int targetAccessTech, ImsReasonInfo imsReasonInfo) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IImsRegistrationListener {
        public static final String DESCRIPTOR = "com.android.ims.internal.IImsRegistrationListener";
        static final int TRANSACTION_registrationAssociatedUriChanged = 11;
        static final int TRANSACTION_registrationChangeFailed = 12;
        static final int TRANSACTION_registrationConnected = 1;
        static final int TRANSACTION_registrationConnectedWithRadioTech = 3;
        static final int TRANSACTION_registrationDisconnected = 5;
        static final int TRANSACTION_registrationFeatureCapabilityChanged = 9;
        static final int TRANSACTION_registrationProgressing = 2;
        static final int TRANSACTION_registrationProgressingWithRadioTech = 4;
        static final int TRANSACTION_registrationResumed = 6;
        static final int TRANSACTION_registrationServiceCapabilityChanged = 8;
        static final int TRANSACTION_registrationSuspended = 7;
        static final int TRANSACTION_voiceMessageCountUpdate = 10;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IImsRegistrationListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IImsRegistrationListener)) {
                return (IImsRegistrationListener) iin;
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
                    return "registrationConnected";
                case 2:
                    return "registrationProgressing";
                case 3:
                    return "registrationConnectedWithRadioTech";
                case 4:
                    return "registrationProgressingWithRadioTech";
                case 5:
                    return "registrationDisconnected";
                case 6:
                    return "registrationResumed";
                case 7:
                    return "registrationSuspended";
                case 8:
                    return "registrationServiceCapabilityChanged";
                case 9:
                    return "registrationFeatureCapabilityChanged";
                case 10:
                    return "voiceMessageCountUpdate";
                case 11:
                    return "registrationAssociatedUriChanged";
                case 12:
                    return "registrationChangeFailed";
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
                            registrationConnected();
                            break;
                        case 2:
                            registrationProgressing();
                            break;
                        case 3:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            registrationConnectedWithRadioTech(_arg0);
                            break;
                        case 4:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            registrationProgressingWithRadioTech(_arg02);
                            break;
                        case 5:
                            ImsReasonInfo _arg03 = (ImsReasonInfo) data.readTypedObject(ImsReasonInfo.CREATOR);
                            data.enforceNoDataAvail();
                            registrationDisconnected(_arg03);
                            break;
                        case 6:
                            registrationResumed();
                            break;
                        case 7:
                            registrationSuspended();
                            break;
                        case 8:
                            int _arg04 = data.readInt();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            registrationServiceCapabilityChanged(_arg04, _arg1);
                            break;
                        case 9:
                            int _arg05 = data.readInt();
                            int[] _arg12 = data.createIntArray();
                            int[] _arg2 = data.createIntArray();
                            data.enforceNoDataAvail();
                            registrationFeatureCapabilityChanged(_arg05, _arg12, _arg2);
                            break;
                        case 10:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            voiceMessageCountUpdate(_arg06);
                            break;
                        case 11:
                            Uri[] _arg07 = (Uri[]) data.createTypedArray(Uri.CREATOR);
                            data.enforceNoDataAvail();
                            registrationAssociatedUriChanged(_arg07);
                            break;
                        case 12:
                            int _arg08 = data.readInt();
                            ImsReasonInfo _arg13 = (ImsReasonInfo) data.readTypedObject(ImsReasonInfo.CREATOR);
                            data.enforceNoDataAvail();
                            registrationChangeFailed(_arg08, _arg13);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IImsRegistrationListener {
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

            @Override // com.android.ims.internal.IImsRegistrationListener
            public void registrationConnected() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsRegistrationListener
            public void registrationProgressing() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsRegistrationListener
            public void registrationConnectedWithRadioTech(int imsRadioTech) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(imsRadioTech);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsRegistrationListener
            public void registrationProgressingWithRadioTech(int imsRadioTech) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(imsRadioTech);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsRegistrationListener
            public void registrationDisconnected(ImsReasonInfo imsReasonInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(imsReasonInfo, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsRegistrationListener
            public void registrationResumed() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsRegistrationListener
            public void registrationSuspended() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsRegistrationListener
            public void registrationServiceCapabilityChanged(int serviceClass, int event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serviceClass);
                    _data.writeInt(event);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsRegistrationListener
            public void registrationFeatureCapabilityChanged(int serviceClass, int[] enabledFeatures, int[] disabledFeatures) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serviceClass);
                    _data.writeIntArray(enabledFeatures);
                    _data.writeIntArray(disabledFeatures);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsRegistrationListener
            public void voiceMessageCountUpdate(int count) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(count);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsRegistrationListener
            public void registrationAssociatedUriChanged(Uri[] uris) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedArray(uris, 0);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsRegistrationListener
            public void registrationChangeFailed(int targetAccessTech, ImsReasonInfo imsReasonInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(targetAccessTech);
                    _data.writeTypedObject(imsReasonInfo, 0);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 11;
        }
    }
}
