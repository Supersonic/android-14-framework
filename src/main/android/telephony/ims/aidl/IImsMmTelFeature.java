package android.telephony.ims.aidl;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Message;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.ims.ImsCallProfile;
import android.telephony.ims.MediaQualityStatus;
import android.telephony.ims.MediaThreshold;
import android.telephony.ims.RtpHeaderExtensionType;
import android.telephony.ims.aidl.IImsCapabilityCallback;
import android.telephony.ims.aidl.IImsMmTelListener;
import android.telephony.ims.aidl.IImsSmsListener;
import android.telephony.ims.aidl.ISrvccStartedCallback;
import android.telephony.ims.feature.CapabilityChangeRequest;
import com.android.ims.internal.IImsCallSession;
import com.android.ims.internal.IImsEcbm;
import com.android.ims.internal.IImsMultiEndpoint;
import com.android.ims.internal.IImsUt;
import java.util.List;
/* loaded from: classes3.dex */
public interface IImsMmTelFeature extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.ims.aidl.IImsMmTelFeature";

    void acknowledgeSms(int i, int i2, int i3) throws RemoteException;

    void acknowledgeSmsReport(int i, int i2, int i3) throws RemoteException;

    void acknowledgeSmsWithPdu(int i, int i2, int i3, byte[] bArr) throws RemoteException;

    void addCapabilityCallback(IImsCapabilityCallback iImsCapabilityCallback) throws RemoteException;

    void changeCapabilitiesConfiguration(CapabilityChangeRequest capabilityChangeRequest, IImsCapabilityCallback iImsCapabilityCallback) throws RemoteException;

    void changeOfferedRtpHeaderExtensionTypes(List<RtpHeaderExtensionType> list) throws RemoteException;

    ImsCallProfile createCallProfile(int i, int i2) throws RemoteException;

    IImsCallSession createCallSession(ImsCallProfile imsCallProfile) throws RemoteException;

    IImsEcbm getEcbmInterface() throws RemoteException;

    int getFeatureState() throws RemoteException;

    IImsMultiEndpoint getMultiEndpointInterface() throws RemoteException;

    String getSmsFormat() throws RemoteException;

    IImsUt getUtInterface() throws RemoteException;

    void notifySrvccCanceled() throws RemoteException;

    void notifySrvccCompleted() throws RemoteException;

    void notifySrvccFailed() throws RemoteException;

    void notifySrvccStarted(ISrvccStartedCallback iSrvccStartedCallback) throws RemoteException;

    void onMemoryAvailable(int i) throws RemoteException;

    void onSmsReady() throws RemoteException;

    void queryCapabilityConfiguration(int i, int i2, IImsCapabilityCallback iImsCapabilityCallback) throws RemoteException;

    int queryCapabilityStatus() throws RemoteException;

    MediaQualityStatus queryMediaQualityStatus(int i) throws RemoteException;

    void removeCapabilityCallback(IImsCapabilityCallback iImsCapabilityCallback) throws RemoteException;

    void sendSms(int i, int i2, String str, String str2, boolean z, byte[] bArr) throws RemoteException;

    void setListener(IImsMmTelListener iImsMmTelListener) throws RemoteException;

    void setMediaQualityThreshold(int i, MediaThreshold mediaThreshold) throws RemoteException;

    void setSmsListener(IImsSmsListener iImsSmsListener) throws RemoteException;

    void setTerminalBasedCallWaitingStatus(boolean z) throws RemoteException;

    void setUiTtyMode(int i, Message message) throws RemoteException;

    int shouldProcessCall(String[] strArr) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IImsMmTelFeature {
        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void setListener(IImsMmTelListener l) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public int getFeatureState() throws RemoteException {
            return 0;
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public ImsCallProfile createCallProfile(int callSessionType, int callType) throws RemoteException {
            return null;
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void changeOfferedRtpHeaderExtensionTypes(List<RtpHeaderExtensionType> types) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public IImsCallSession createCallSession(ImsCallProfile profile) throws RemoteException {
            return null;
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public int shouldProcessCall(String[] uris) throws RemoteException {
            return 0;
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public IImsUt getUtInterface() throws RemoteException {
            return null;
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public IImsEcbm getEcbmInterface() throws RemoteException {
            return null;
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void setUiTtyMode(int uiTtyMode, Message onCompleteMessage) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public IImsMultiEndpoint getMultiEndpointInterface() throws RemoteException {
            return null;
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public int queryCapabilityStatus() throws RemoteException {
            return 0;
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void setTerminalBasedCallWaitingStatus(boolean enabled) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void addCapabilityCallback(IImsCapabilityCallback c) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void removeCapabilityCallback(IImsCapabilityCallback c) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void changeCapabilitiesConfiguration(CapabilityChangeRequest request, IImsCapabilityCallback c) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void queryCapabilityConfiguration(int capability, int radioTech, IImsCapabilityCallback c) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void notifySrvccStarted(ISrvccStartedCallback cb) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void notifySrvccCompleted() throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void notifySrvccFailed() throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void notifySrvccCanceled() throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void setMediaQualityThreshold(int mediaSessionType, MediaThreshold threshold) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public MediaQualityStatus queryMediaQualityStatus(int mediaSessionType) throws RemoteException {
            return null;
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void setSmsListener(IImsSmsListener l) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void sendSms(int token, int messageRef, String format, String smsc, boolean retry, byte[] pdu) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void onMemoryAvailable(int token) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void acknowledgeSms(int token, int messageRef, int result) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void acknowledgeSmsWithPdu(int token, int messageRef, int result, byte[] pdu) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void acknowledgeSmsReport(int token, int messageRef, int result) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public String getSmsFormat() throws RemoteException {
            return null;
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void onSmsReady() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IImsMmTelFeature {
        static final int TRANSACTION_acknowledgeSms = 26;
        static final int TRANSACTION_acknowledgeSmsReport = 28;
        static final int TRANSACTION_acknowledgeSmsWithPdu = 27;
        static final int TRANSACTION_addCapabilityCallback = 13;
        static final int TRANSACTION_changeCapabilitiesConfiguration = 15;
        static final int TRANSACTION_changeOfferedRtpHeaderExtensionTypes = 4;
        static final int TRANSACTION_createCallProfile = 3;
        static final int TRANSACTION_createCallSession = 5;
        static final int TRANSACTION_getEcbmInterface = 8;
        static final int TRANSACTION_getFeatureState = 2;
        static final int TRANSACTION_getMultiEndpointInterface = 10;
        static final int TRANSACTION_getSmsFormat = 29;
        static final int TRANSACTION_getUtInterface = 7;
        static final int TRANSACTION_notifySrvccCanceled = 20;
        static final int TRANSACTION_notifySrvccCompleted = 18;
        static final int TRANSACTION_notifySrvccFailed = 19;
        static final int TRANSACTION_notifySrvccStarted = 17;
        static final int TRANSACTION_onMemoryAvailable = 25;
        static final int TRANSACTION_onSmsReady = 30;
        static final int TRANSACTION_queryCapabilityConfiguration = 16;
        static final int TRANSACTION_queryCapabilityStatus = 11;
        static final int TRANSACTION_queryMediaQualityStatus = 22;
        static final int TRANSACTION_removeCapabilityCallback = 14;
        static final int TRANSACTION_sendSms = 24;
        static final int TRANSACTION_setListener = 1;
        static final int TRANSACTION_setMediaQualityThreshold = 21;
        static final int TRANSACTION_setSmsListener = 23;
        static final int TRANSACTION_setTerminalBasedCallWaitingStatus = 12;
        static final int TRANSACTION_setUiTtyMode = 9;
        static final int TRANSACTION_shouldProcessCall = 6;

        public Stub() {
            attachInterface(this, IImsMmTelFeature.DESCRIPTOR);
        }

        public static IImsMmTelFeature asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IImsMmTelFeature.DESCRIPTOR);
            if (iin != null && (iin instanceof IImsMmTelFeature)) {
                return (IImsMmTelFeature) iin;
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
                    return "setListener";
                case 2:
                    return "getFeatureState";
                case 3:
                    return "createCallProfile";
                case 4:
                    return "changeOfferedRtpHeaderExtensionTypes";
                case 5:
                    return "createCallSession";
                case 6:
                    return "shouldProcessCall";
                case 7:
                    return "getUtInterface";
                case 8:
                    return "getEcbmInterface";
                case 9:
                    return "setUiTtyMode";
                case 10:
                    return "getMultiEndpointInterface";
                case 11:
                    return "queryCapabilityStatus";
                case 12:
                    return "setTerminalBasedCallWaitingStatus";
                case 13:
                    return "addCapabilityCallback";
                case 14:
                    return "removeCapabilityCallback";
                case 15:
                    return "changeCapabilitiesConfiguration";
                case 16:
                    return "queryCapabilityConfiguration";
                case 17:
                    return "notifySrvccStarted";
                case 18:
                    return "notifySrvccCompleted";
                case 19:
                    return "notifySrvccFailed";
                case 20:
                    return "notifySrvccCanceled";
                case 21:
                    return "setMediaQualityThreshold";
                case 22:
                    return "queryMediaQualityStatus";
                case 23:
                    return "setSmsListener";
                case 24:
                    return "sendSms";
                case 25:
                    return "onMemoryAvailable";
                case 26:
                    return "acknowledgeSms";
                case 27:
                    return "acknowledgeSmsWithPdu";
                case 28:
                    return "acknowledgeSmsReport";
                case 29:
                    return "getSmsFormat";
                case 30:
                    return "onSmsReady";
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
                data.enforceInterface(IImsMmTelFeature.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IImsMmTelFeature.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IImsMmTelListener _arg0 = IImsMmTelListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setListener(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            int _result = getFeatureState();
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 3:
                            int _arg02 = data.readInt();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            ImsCallProfile _result2 = createCallProfile(_arg02, _arg1);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 4:
                            List<RtpHeaderExtensionType> _arg03 = data.createTypedArrayList(RtpHeaderExtensionType.CREATOR);
                            data.enforceNoDataAvail();
                            changeOfferedRtpHeaderExtensionTypes(_arg03);
                            reply.writeNoException();
                            break;
                        case 5:
                            ImsCallProfile _arg04 = (ImsCallProfile) data.readTypedObject(ImsCallProfile.CREATOR);
                            data.enforceNoDataAvail();
                            IImsCallSession _result3 = createCallSession(_arg04);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result3);
                            break;
                        case 6:
                            String[] _arg05 = data.createStringArray();
                            data.enforceNoDataAvail();
                            int _result4 = shouldProcessCall(_arg05);
                            reply.writeNoException();
                            reply.writeInt(_result4);
                            break;
                        case 7:
                            IImsUt _result5 = getUtInterface();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result5);
                            break;
                        case 8:
                            IImsEcbm _result6 = getEcbmInterface();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result6);
                            break;
                        case 9:
                            int _arg06 = data.readInt();
                            Message _arg12 = (Message) data.readTypedObject(Message.CREATOR);
                            data.enforceNoDataAvail();
                            setUiTtyMode(_arg06, _arg12);
                            reply.writeNoException();
                            break;
                        case 10:
                            IImsMultiEndpoint _result7 = getMultiEndpointInterface();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result7);
                            break;
                        case 11:
                            int _result8 = queryCapabilityStatus();
                            reply.writeNoException();
                            reply.writeInt(_result8);
                            break;
                        case 12:
                            boolean _arg07 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setTerminalBasedCallWaitingStatus(_arg07);
                            reply.writeNoException();
                            break;
                        case 13:
                            IImsCapabilityCallback _arg08 = IImsCapabilityCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addCapabilityCallback(_arg08);
                            break;
                        case 14:
                            IImsCapabilityCallback _arg09 = IImsCapabilityCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeCapabilityCallback(_arg09);
                            break;
                        case 15:
                            CapabilityChangeRequest _arg010 = (CapabilityChangeRequest) data.readTypedObject(CapabilityChangeRequest.CREATOR);
                            IImsCapabilityCallback _arg13 = IImsCapabilityCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            changeCapabilitiesConfiguration(_arg010, _arg13);
                            break;
                        case 16:
                            int _arg011 = data.readInt();
                            int _arg14 = data.readInt();
                            IImsCapabilityCallback _arg2 = IImsCapabilityCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            queryCapabilityConfiguration(_arg011, _arg14, _arg2);
                            break;
                        case 17:
                            ISrvccStartedCallback _arg012 = ISrvccStartedCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            notifySrvccStarted(_arg012);
                            break;
                        case 18:
                            notifySrvccCompleted();
                            break;
                        case 19:
                            notifySrvccFailed();
                            break;
                        case 20:
                            notifySrvccCanceled();
                            break;
                        case 21:
                            int _arg013 = data.readInt();
                            MediaThreshold _arg15 = (MediaThreshold) data.readTypedObject(MediaThreshold.CREATOR);
                            data.enforceNoDataAvail();
                            setMediaQualityThreshold(_arg013, _arg15);
                            break;
                        case 22:
                            int _arg014 = data.readInt();
                            data.enforceNoDataAvail();
                            MediaQualityStatus _result9 = queryMediaQualityStatus(_arg014);
                            reply.writeNoException();
                            reply.writeTypedObject(_result9, 1);
                            break;
                        case 23:
                            IImsSmsListener _arg015 = IImsSmsListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setSmsListener(_arg015);
                            reply.writeNoException();
                            break;
                        case 24:
                            int _arg016 = data.readInt();
                            int _arg16 = data.readInt();
                            String _arg22 = data.readString();
                            String _arg3 = data.readString();
                            boolean _arg4 = data.readBoolean();
                            byte[] _arg5 = data.createByteArray();
                            data.enforceNoDataAvail();
                            sendSms(_arg016, _arg16, _arg22, _arg3, _arg4, _arg5);
                            break;
                        case 25:
                            int _arg017 = data.readInt();
                            data.enforceNoDataAvail();
                            onMemoryAvailable(_arg017);
                            break;
                        case 26:
                            int _arg018 = data.readInt();
                            int _arg17 = data.readInt();
                            int _arg23 = data.readInt();
                            data.enforceNoDataAvail();
                            acknowledgeSms(_arg018, _arg17, _arg23);
                            break;
                        case 27:
                            int _arg019 = data.readInt();
                            int _arg18 = data.readInt();
                            int _arg24 = data.readInt();
                            byte[] _arg32 = data.createByteArray();
                            data.enforceNoDataAvail();
                            acknowledgeSmsWithPdu(_arg019, _arg18, _arg24, _arg32);
                            break;
                        case 28:
                            int _arg020 = data.readInt();
                            int _arg19 = data.readInt();
                            int _arg25 = data.readInt();
                            data.enforceNoDataAvail();
                            acknowledgeSmsReport(_arg020, _arg19, _arg25);
                            break;
                        case 29:
                            String _result10 = getSmsFormat();
                            reply.writeNoException();
                            reply.writeString(_result10);
                            break;
                        case 30:
                            onSmsReady();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IImsMmTelFeature {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IImsMmTelFeature.DESCRIPTOR;
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public void setListener(IImsMmTelListener l) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    _data.writeStrongInterface(l);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public int getFeatureState() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public ImsCallProfile createCallProfile(int callSessionType, int callType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    _data.writeInt(callSessionType);
                    _data.writeInt(callType);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    ImsCallProfile _result = (ImsCallProfile) _reply.readTypedObject(ImsCallProfile.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public void changeOfferedRtpHeaderExtensionTypes(List<RtpHeaderExtensionType> types) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    _data.writeTypedList(types, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public IImsCallSession createCallSession(ImsCallProfile profile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    _data.writeTypedObject(profile, 0);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    IImsCallSession _result = IImsCallSession.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public int shouldProcessCall(String[] uris) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    _data.writeStringArray(uris);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public IImsUt getUtInterface() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    IImsUt _result = IImsUt.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public IImsEcbm getEcbmInterface() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    IImsEcbm _result = IImsEcbm.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public void setUiTtyMode(int uiTtyMode, Message onCompleteMessage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    _data.writeInt(uiTtyMode);
                    _data.writeTypedObject(onCompleteMessage, 0);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public IImsMultiEndpoint getMultiEndpointInterface() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    IImsMultiEndpoint _result = IImsMultiEndpoint.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public int queryCapabilityStatus() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public void setTerminalBasedCallWaitingStatus(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public void addCapabilityCallback(IImsCapabilityCallback c) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    _data.writeStrongInterface(c);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public void removeCapabilityCallback(IImsCapabilityCallback c) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    _data.writeStrongInterface(c);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public void changeCapabilitiesConfiguration(CapabilityChangeRequest request, IImsCapabilityCallback c) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(c);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public void queryCapabilityConfiguration(int capability, int radioTech, IImsCapabilityCallback c) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    _data.writeInt(capability);
                    _data.writeInt(radioTech);
                    _data.writeStrongInterface(c);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public void notifySrvccStarted(ISrvccStartedCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    _data.writeStrongInterface(cb);
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public void notifySrvccCompleted() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public void notifySrvccFailed() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    this.mRemote.transact(19, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public void notifySrvccCanceled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    this.mRemote.transact(20, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public void setMediaQualityThreshold(int mediaSessionType, MediaThreshold threshold) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    _data.writeInt(mediaSessionType);
                    _data.writeTypedObject(threshold, 0);
                    this.mRemote.transact(21, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public MediaQualityStatus queryMediaQualityStatus(int mediaSessionType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    _data.writeInt(mediaSessionType);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    MediaQualityStatus _result = (MediaQualityStatus) _reply.readTypedObject(MediaQualityStatus.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public void setSmsListener(IImsSmsListener l) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    _data.writeStrongInterface(l);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public void sendSms(int token, int messageRef, String format, String smsc, boolean retry, byte[] pdu) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    _data.writeInt(token);
                    _data.writeInt(messageRef);
                    _data.writeString(format);
                    _data.writeString(smsc);
                    _data.writeBoolean(retry);
                    _data.writeByteArray(pdu);
                    this.mRemote.transact(24, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public void onMemoryAvailable(int token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    _data.writeInt(token);
                    this.mRemote.transact(25, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public void acknowledgeSms(int token, int messageRef, int result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    _data.writeInt(token);
                    _data.writeInt(messageRef);
                    _data.writeInt(result);
                    this.mRemote.transact(26, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public void acknowledgeSmsWithPdu(int token, int messageRef, int result, byte[] pdu) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    _data.writeInt(token);
                    _data.writeInt(messageRef);
                    _data.writeInt(result);
                    _data.writeByteArray(pdu);
                    this.mRemote.transact(27, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public void acknowledgeSmsReport(int token, int messageRef, int result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    _data.writeInt(token);
                    _data.writeInt(messageRef);
                    _data.writeInt(result);
                    this.mRemote.transact(28, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public String getSmsFormat() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsMmTelFeature
            public void onSmsReady() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsMmTelFeature.DESCRIPTOR);
                    this.mRemote.transact(30, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 29;
        }
    }
}
