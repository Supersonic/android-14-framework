package android.telephony.ims.aidl;

import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.ims.ImsCallProfile;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.MediaQualityStatus;
import android.telephony.ims.aidl.IImsCallSessionListener;
import android.telephony.ims.aidl.IImsTrafficSessionCallback;
import com.android.ims.internal.IImsCallSession;
/* loaded from: classes3.dex */
public interface IImsMmTelListener extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.ims.aidl.IImsMmTelListener";

    void onAudioModeIsVoipChanged(int i) throws RemoteException;

    IImsCallSessionListener onIncomingCall(IImsCallSession iImsCallSession, String str, Bundle bundle) throws RemoteException;

    void onMediaQualityStatusChanged(MediaQualityStatus mediaQualityStatus) throws RemoteException;

    void onModifyImsTrafficSession(int i, int i2) throws RemoteException;

    void onRejectedCall(ImsCallProfile imsCallProfile, ImsReasonInfo imsReasonInfo) throws RemoteException;

    void onStartImsTrafficSession(int i, int i2, int i3, int i4, IImsTrafficSessionCallback iImsTrafficSessionCallback) throws RemoteException;

    void onStopImsTrafficSession(int i) throws RemoteException;

    void onTriggerEpsFallback(int i) throws RemoteException;

    void onVoiceMessageCountUpdate(int i) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IImsMmTelListener {
        @Override // android.telephony.ims.aidl.IImsMmTelListener
        public IImsCallSessionListener onIncomingCall(IImsCallSession c, String callId, Bundle extras) throws RemoteException {
            return null;
        }

        @Override // android.telephony.ims.aidl.IImsMmTelListener
        public void onRejectedCall(ImsCallProfile callProfile, ImsReasonInfo reason) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelListener
        public void onVoiceMessageCountUpdate(int count) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelListener
        public void onAudioModeIsVoipChanged(int imsAudioHandler) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelListener
        public void onTriggerEpsFallback(int reason) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelListener
        public void onStartImsTrafficSession(int token, int trafficType, int accessNetworkType, int trafficDirection, IImsTrafficSessionCallback callback) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelListener
        public void onModifyImsTrafficSession(int token, int accessNetworkType) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelListener
        public void onStopImsTrafficSession(int token) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelListener
        public void onMediaQualityStatusChanged(MediaQualityStatus status) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IImsMmTelListener {
        static final int TRANSACTION_onAudioModeIsVoipChanged = 4;
        static final int TRANSACTION_onIncomingCall = 1;
        static final int TRANSACTION_onMediaQualityStatusChanged = 9;
        static final int TRANSACTION_onModifyImsTrafficSession = 7;
        static final int TRANSACTION_onRejectedCall = 2;
        static final int TRANSACTION_onStartImsTrafficSession = 6;
        static final int TRANSACTION_onStopImsTrafficSession = 8;
        static final int TRANSACTION_onTriggerEpsFallback = 5;
        static final int TRANSACTION_onVoiceMessageCountUpdate = 3;

        public Stub() {
            attachInterface(this, IImsMmTelListener.DESCRIPTOR);
        }

        public static IImsMmTelListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IImsMmTelListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IImsMmTelListener)) {
                return (IImsMmTelListener) iin;
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
                    return "onIncomingCall";
                case 2:
                    return "onRejectedCall";
                case 3:
                    return "onVoiceMessageCountUpdate";
                case 4:
                    return "onAudioModeIsVoipChanged";
                case 5:
                    return "onTriggerEpsFallback";
                case 6:
                    return "onStartImsTrafficSession";
                case 7:
                    return "onModifyImsTrafficSession";
                case 8:
                    return "onStopImsTrafficSession";
                case 9:
                    return "onMediaQualityStatusChanged";
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
                data.enforceInterface(IImsMmTelListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IImsMmTelListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IImsCallSession _arg0 = IImsCallSession.Stub.asInterface(data.readStrongBinder());
                            String _arg1 = data.readString();
                            Bundle _arg2 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            IImsCallSessionListener _result = onIncomingCall(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result);
                            break;
                        case 2:
                            ImsCallProfile _arg02 = (ImsCallProfile) data.readTypedObject(ImsCallProfile.CREATOR);
                            ImsReasonInfo _arg12 = (ImsReasonInfo) data.readTypedObject(ImsReasonInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onRejectedCall(_arg02, _arg12);
                            reply.writeNoException();
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            onVoiceMessageCountUpdate(_arg03);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            onAudioModeIsVoipChanged(_arg04);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            onTriggerEpsFallback(_arg05);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            int _arg13 = data.readInt();
                            int _arg22 = data.readInt();
                            int _arg3 = data.readInt();
                            IImsTrafficSessionCallback _arg4 = IImsTrafficSessionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onStartImsTrafficSession(_arg06, _arg13, _arg22, _arg3, _arg4);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            onModifyImsTrafficSession(_arg07, _arg14);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            data.enforceNoDataAvail();
                            onStopImsTrafficSession(_arg08);
                            break;
                        case 9:
                            MediaQualityStatus _arg09 = (MediaQualityStatus) data.readTypedObject(MediaQualityStatus.CREATOR);
                            data.enforceNoDataAvail();
                            onMediaQualityStatusChanged(_arg09);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IImsMmTelListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IImsMmTelListener.DESCRIPTOR;
            }

            @Override // android.telephony.ims.aidl.IImsMmTelListener
            public IImsCallSessionListener onIncomingCall(IImsCallSession c, String callId, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsMmTelListener.DESCRIPTOR);
                    _data.writeStrongInterface(c);
                    _data.writeString(callId);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    IImsCallSessionListener _result = IImsCallSessionListener.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelListener
            public void onRejectedCall(ImsCallProfile callProfile, ImsReasonInfo reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsMmTelListener.DESCRIPTOR);
                    _data.writeTypedObject(callProfile, 0);
                    _data.writeTypedObject(reason, 0);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelListener
            public void onVoiceMessageCountUpdate(int count) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsMmTelListener.DESCRIPTOR);
                    _data.writeInt(count);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelListener
            public void onAudioModeIsVoipChanged(int imsAudioHandler) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsMmTelListener.DESCRIPTOR);
                    _data.writeInt(imsAudioHandler);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelListener
            public void onTriggerEpsFallback(int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsMmTelListener.DESCRIPTOR);
                    _data.writeInt(reason);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelListener
            public void onStartImsTrafficSession(int token, int trafficType, int accessNetworkType, int trafficDirection, IImsTrafficSessionCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsMmTelListener.DESCRIPTOR);
                    _data.writeInt(token);
                    _data.writeInt(trafficType);
                    _data.writeInt(accessNetworkType);
                    _data.writeInt(trafficDirection);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelListener
            public void onModifyImsTrafficSession(int token, int accessNetworkType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsMmTelListener.DESCRIPTOR);
                    _data.writeInt(token);
                    _data.writeInt(accessNetworkType);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelListener
            public void onStopImsTrafficSession(int token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsMmTelListener.DESCRIPTOR);
                    _data.writeInt(token);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelListener
            public void onMediaQualityStatusChanged(MediaQualityStatus status) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsMmTelListener.DESCRIPTOR);
                    _data.writeTypedObject(status, 0);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 8;
        }
    }
}
