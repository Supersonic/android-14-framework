package android.telephony.ims.aidl;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.CallQuality;
import android.telephony.ims.ImsCallProfile;
import android.telephony.ims.ImsConferenceState;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.ImsStreamMediaProfile;
import android.telephony.ims.ImsSuppServiceNotification;
import android.telephony.ims.RtpHeaderExtension;
import com.android.ims.internal.IImsCallSession;
import java.util.List;
/* loaded from: classes3.dex */
public interface IImsCallSessionListener extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.ims.aidl.IImsCallSessionListener";

    void callQualityChanged(CallQuality callQuality) throws RemoteException;

    void callSessionConferenceExtendFailed(ImsReasonInfo imsReasonInfo) throws RemoteException;

    void callSessionConferenceExtendReceived(IImsCallSession iImsCallSession, ImsCallProfile imsCallProfile) throws RemoteException;

    void callSessionConferenceExtended(IImsCallSession iImsCallSession, ImsCallProfile imsCallProfile) throws RemoteException;

    void callSessionConferenceStateUpdated(ImsConferenceState imsConferenceState) throws RemoteException;

    void callSessionDtmfReceived(char c) throws RemoteException;

    void callSessionHandover(int i, int i2, ImsReasonInfo imsReasonInfo) throws RemoteException;

    void callSessionHandoverFailed(int i, int i2, ImsReasonInfo imsReasonInfo) throws RemoteException;

    void callSessionHeld(ImsCallProfile imsCallProfile) throws RemoteException;

    void callSessionHoldFailed(ImsReasonInfo imsReasonInfo) throws RemoteException;

    void callSessionHoldReceived(ImsCallProfile imsCallProfile) throws RemoteException;

    void callSessionInitiated(ImsCallProfile imsCallProfile) throws RemoteException;

    void callSessionInitiatedFailed(ImsReasonInfo imsReasonInfo) throws RemoteException;

    void callSessionInitiating(ImsCallProfile imsCallProfile) throws RemoteException;

    void callSessionInitiatingFailed(ImsReasonInfo imsReasonInfo) throws RemoteException;

    void callSessionInviteParticipantsRequestDelivered() throws RemoteException;

    void callSessionInviteParticipantsRequestFailed(ImsReasonInfo imsReasonInfo) throws RemoteException;

    void callSessionMayHandover(int i, int i2) throws RemoteException;

    void callSessionMergeComplete(IImsCallSession iImsCallSession) throws RemoteException;

    void callSessionMergeFailed(ImsReasonInfo imsReasonInfo) throws RemoteException;

    void callSessionMergeStarted(IImsCallSession iImsCallSession, ImsCallProfile imsCallProfile) throws RemoteException;

    void callSessionMultipartyStateChanged(boolean z) throws RemoteException;

    void callSessionProgressing(ImsStreamMediaProfile imsStreamMediaProfile) throws RemoteException;

    void callSessionRemoveParticipantsRequestDelivered() throws RemoteException;

    void callSessionRemoveParticipantsRequestFailed(ImsReasonInfo imsReasonInfo) throws RemoteException;

    void callSessionResumeFailed(ImsReasonInfo imsReasonInfo) throws RemoteException;

    void callSessionResumeReceived(ImsCallProfile imsCallProfile) throws RemoteException;

    void callSessionResumed(ImsCallProfile imsCallProfile) throws RemoteException;

    void callSessionRtpHeaderExtensionsReceived(List<RtpHeaderExtension> list) throws RemoteException;

    void callSessionRttAudioIndicatorChanged(ImsStreamMediaProfile imsStreamMediaProfile) throws RemoteException;

    void callSessionRttMessageReceived(String str) throws RemoteException;

    void callSessionRttModifyRequestReceived(ImsCallProfile imsCallProfile) throws RemoteException;

    void callSessionRttModifyResponseReceived(int i) throws RemoteException;

    void callSessionSendAnbrQuery(int i, int i2, int i3) throws RemoteException;

    void callSessionSuppServiceReceived(ImsSuppServiceNotification imsSuppServiceNotification) throws RemoteException;

    void callSessionTerminated(ImsReasonInfo imsReasonInfo) throws RemoteException;

    void callSessionTransferFailed(ImsReasonInfo imsReasonInfo) throws RemoteException;

    void callSessionTransferred() throws RemoteException;

    void callSessionTtyModeReceived(int i) throws RemoteException;

    void callSessionUpdateFailed(ImsReasonInfo imsReasonInfo) throws RemoteException;

    void callSessionUpdateReceived(ImsCallProfile imsCallProfile) throws RemoteException;

    void callSessionUpdated(ImsCallProfile imsCallProfile) throws RemoteException;

    void callSessionUssdMessageReceived(int i, String str) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IImsCallSessionListener {
        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionInitiating(ImsCallProfile profile) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionInitiatingFailed(ImsReasonInfo reasonInfo) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionProgressing(ImsStreamMediaProfile profile) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionInitiated(ImsCallProfile profile) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionInitiatedFailed(ImsReasonInfo reasonInfo) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionTerminated(ImsReasonInfo reasonInfo) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionHeld(ImsCallProfile profile) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionHoldFailed(ImsReasonInfo reasonInfo) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionHoldReceived(ImsCallProfile profile) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionResumed(ImsCallProfile profile) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionResumeFailed(ImsReasonInfo reasonInfo) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionResumeReceived(ImsCallProfile profile) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionMergeStarted(IImsCallSession newSession, ImsCallProfile profile) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionMergeComplete(IImsCallSession session) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionMergeFailed(ImsReasonInfo reasonInfo) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionUpdated(ImsCallProfile profile) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionUpdateFailed(ImsReasonInfo reasonInfo) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionUpdateReceived(ImsCallProfile profile) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionConferenceExtended(IImsCallSession newSession, ImsCallProfile profile) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionConferenceExtendFailed(ImsReasonInfo reasonInfo) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionConferenceExtendReceived(IImsCallSession newSession, ImsCallProfile profile) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionInviteParticipantsRequestDelivered() throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionInviteParticipantsRequestFailed(ImsReasonInfo reasonInfo) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionRemoveParticipantsRequestDelivered() throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionRemoveParticipantsRequestFailed(ImsReasonInfo reasonInfo) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionConferenceStateUpdated(ImsConferenceState state) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionUssdMessageReceived(int mode, String ussdMessage) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionHandover(int srcNetworkType, int targetNetworkType, ImsReasonInfo reasonInfo) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionHandoverFailed(int srcNetworkType, int targetNetworkType, ImsReasonInfo reasonInfo) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionMayHandover(int srcNetworkType, int targetNetworkType) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionTtyModeReceived(int mode) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionMultipartyStateChanged(boolean isMultiParty) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionSuppServiceReceived(ImsSuppServiceNotification suppSrvNotification) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionRttModifyRequestReceived(ImsCallProfile callProfile) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionRttModifyResponseReceived(int status) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionRttMessageReceived(String rttMessage) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionRttAudioIndicatorChanged(ImsStreamMediaProfile profile) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionTransferred() throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionTransferFailed(ImsReasonInfo reasonInfo) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionDtmfReceived(char dtmf) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callQualityChanged(CallQuality callQuality) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionRtpHeaderExtensionsReceived(List<RtpHeaderExtension> extensions) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionSendAnbrQuery(int mediaType, int direction, int bitsPerSecond) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IImsCallSessionListener {
        static final int TRANSACTION_callQualityChanged = 41;
        static final int TRANSACTION_callSessionConferenceExtendFailed = 20;
        static final int TRANSACTION_callSessionConferenceExtendReceived = 21;
        static final int TRANSACTION_callSessionConferenceExtended = 19;
        static final int TRANSACTION_callSessionConferenceStateUpdated = 26;
        static final int TRANSACTION_callSessionDtmfReceived = 40;
        static final int TRANSACTION_callSessionHandover = 28;
        static final int TRANSACTION_callSessionHandoverFailed = 29;
        static final int TRANSACTION_callSessionHeld = 7;
        static final int TRANSACTION_callSessionHoldFailed = 8;
        static final int TRANSACTION_callSessionHoldReceived = 9;
        static final int TRANSACTION_callSessionInitiated = 4;
        static final int TRANSACTION_callSessionInitiatedFailed = 5;
        static final int TRANSACTION_callSessionInitiating = 1;
        static final int TRANSACTION_callSessionInitiatingFailed = 2;
        static final int TRANSACTION_callSessionInviteParticipantsRequestDelivered = 22;
        static final int TRANSACTION_callSessionInviteParticipantsRequestFailed = 23;
        static final int TRANSACTION_callSessionMayHandover = 30;
        static final int TRANSACTION_callSessionMergeComplete = 14;
        static final int TRANSACTION_callSessionMergeFailed = 15;
        static final int TRANSACTION_callSessionMergeStarted = 13;
        static final int TRANSACTION_callSessionMultipartyStateChanged = 32;
        static final int TRANSACTION_callSessionProgressing = 3;
        static final int TRANSACTION_callSessionRemoveParticipantsRequestDelivered = 24;
        static final int TRANSACTION_callSessionRemoveParticipantsRequestFailed = 25;
        static final int TRANSACTION_callSessionResumeFailed = 11;
        static final int TRANSACTION_callSessionResumeReceived = 12;
        static final int TRANSACTION_callSessionResumed = 10;
        static final int TRANSACTION_callSessionRtpHeaderExtensionsReceived = 42;
        static final int TRANSACTION_callSessionRttAudioIndicatorChanged = 37;
        static final int TRANSACTION_callSessionRttMessageReceived = 36;
        static final int TRANSACTION_callSessionRttModifyRequestReceived = 34;
        static final int TRANSACTION_callSessionRttModifyResponseReceived = 35;
        static final int TRANSACTION_callSessionSendAnbrQuery = 43;
        static final int TRANSACTION_callSessionSuppServiceReceived = 33;
        static final int TRANSACTION_callSessionTerminated = 6;
        static final int TRANSACTION_callSessionTransferFailed = 39;
        static final int TRANSACTION_callSessionTransferred = 38;
        static final int TRANSACTION_callSessionTtyModeReceived = 31;
        static final int TRANSACTION_callSessionUpdateFailed = 17;
        static final int TRANSACTION_callSessionUpdateReceived = 18;
        static final int TRANSACTION_callSessionUpdated = 16;
        static final int TRANSACTION_callSessionUssdMessageReceived = 27;

        public Stub() {
            attachInterface(this, IImsCallSessionListener.DESCRIPTOR);
        }

        public static IImsCallSessionListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IImsCallSessionListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IImsCallSessionListener)) {
                return (IImsCallSessionListener) iin;
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
                    return "callSessionInitiating";
                case 2:
                    return "callSessionInitiatingFailed";
                case 3:
                    return "callSessionProgressing";
                case 4:
                    return "callSessionInitiated";
                case 5:
                    return "callSessionInitiatedFailed";
                case 6:
                    return "callSessionTerminated";
                case 7:
                    return "callSessionHeld";
                case 8:
                    return "callSessionHoldFailed";
                case 9:
                    return "callSessionHoldReceived";
                case 10:
                    return "callSessionResumed";
                case 11:
                    return "callSessionResumeFailed";
                case 12:
                    return "callSessionResumeReceived";
                case 13:
                    return "callSessionMergeStarted";
                case 14:
                    return "callSessionMergeComplete";
                case 15:
                    return "callSessionMergeFailed";
                case 16:
                    return "callSessionUpdated";
                case 17:
                    return "callSessionUpdateFailed";
                case 18:
                    return "callSessionUpdateReceived";
                case 19:
                    return "callSessionConferenceExtended";
                case 20:
                    return "callSessionConferenceExtendFailed";
                case 21:
                    return "callSessionConferenceExtendReceived";
                case 22:
                    return "callSessionInviteParticipantsRequestDelivered";
                case 23:
                    return "callSessionInviteParticipantsRequestFailed";
                case 24:
                    return "callSessionRemoveParticipantsRequestDelivered";
                case 25:
                    return "callSessionRemoveParticipantsRequestFailed";
                case 26:
                    return "callSessionConferenceStateUpdated";
                case 27:
                    return "callSessionUssdMessageReceived";
                case 28:
                    return "callSessionHandover";
                case 29:
                    return "callSessionHandoverFailed";
                case 30:
                    return "callSessionMayHandover";
                case 31:
                    return "callSessionTtyModeReceived";
                case 32:
                    return "callSessionMultipartyStateChanged";
                case 33:
                    return "callSessionSuppServiceReceived";
                case 34:
                    return "callSessionRttModifyRequestReceived";
                case 35:
                    return "callSessionRttModifyResponseReceived";
                case 36:
                    return "callSessionRttMessageReceived";
                case 37:
                    return "callSessionRttAudioIndicatorChanged";
                case 38:
                    return "callSessionTransferred";
                case 39:
                    return "callSessionTransferFailed";
                case 40:
                    return "callSessionDtmfReceived";
                case 41:
                    return "callQualityChanged";
                case 42:
                    return "callSessionRtpHeaderExtensionsReceived";
                case 43:
                    return "callSessionSendAnbrQuery";
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
                data.enforceInterface(IImsCallSessionListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IImsCallSessionListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ImsCallProfile _arg0 = (ImsCallProfile) data.readTypedObject(ImsCallProfile.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionInitiating(_arg0);
                            break;
                        case 2:
                            ImsReasonInfo _arg02 = (ImsReasonInfo) data.readTypedObject(ImsReasonInfo.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionInitiatingFailed(_arg02);
                            break;
                        case 3:
                            ImsStreamMediaProfile _arg03 = (ImsStreamMediaProfile) data.readTypedObject(ImsStreamMediaProfile.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionProgressing(_arg03);
                            break;
                        case 4:
                            ImsCallProfile _arg04 = (ImsCallProfile) data.readTypedObject(ImsCallProfile.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionInitiated(_arg04);
                            break;
                        case 5:
                            ImsReasonInfo _arg05 = (ImsReasonInfo) data.readTypedObject(ImsReasonInfo.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionInitiatedFailed(_arg05);
                            break;
                        case 6:
                            ImsReasonInfo _arg06 = (ImsReasonInfo) data.readTypedObject(ImsReasonInfo.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionTerminated(_arg06);
                            break;
                        case 7:
                            ImsCallProfile _arg07 = (ImsCallProfile) data.readTypedObject(ImsCallProfile.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionHeld(_arg07);
                            break;
                        case 8:
                            ImsReasonInfo _arg08 = (ImsReasonInfo) data.readTypedObject(ImsReasonInfo.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionHoldFailed(_arg08);
                            break;
                        case 9:
                            ImsCallProfile _arg09 = (ImsCallProfile) data.readTypedObject(ImsCallProfile.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionHoldReceived(_arg09);
                            break;
                        case 10:
                            ImsCallProfile _arg010 = (ImsCallProfile) data.readTypedObject(ImsCallProfile.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionResumed(_arg010);
                            break;
                        case 11:
                            ImsReasonInfo _arg011 = (ImsReasonInfo) data.readTypedObject(ImsReasonInfo.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionResumeFailed(_arg011);
                            break;
                        case 12:
                            ImsCallProfile _arg012 = (ImsCallProfile) data.readTypedObject(ImsCallProfile.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionResumeReceived(_arg012);
                            break;
                        case 13:
                            IImsCallSession _arg013 = IImsCallSession.Stub.asInterface(data.readStrongBinder());
                            ImsCallProfile _arg1 = (ImsCallProfile) data.readTypedObject(ImsCallProfile.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionMergeStarted(_arg013, _arg1);
                            break;
                        case 14:
                            IImsCallSession _arg014 = IImsCallSession.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            callSessionMergeComplete(_arg014);
                            break;
                        case 15:
                            ImsReasonInfo _arg015 = (ImsReasonInfo) data.readTypedObject(ImsReasonInfo.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionMergeFailed(_arg015);
                            break;
                        case 16:
                            ImsCallProfile _arg016 = (ImsCallProfile) data.readTypedObject(ImsCallProfile.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionUpdated(_arg016);
                            break;
                        case 17:
                            ImsReasonInfo _arg017 = (ImsReasonInfo) data.readTypedObject(ImsReasonInfo.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionUpdateFailed(_arg017);
                            break;
                        case 18:
                            ImsCallProfile _arg018 = (ImsCallProfile) data.readTypedObject(ImsCallProfile.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionUpdateReceived(_arg018);
                            break;
                        case 19:
                            IImsCallSession _arg019 = IImsCallSession.Stub.asInterface(data.readStrongBinder());
                            ImsCallProfile _arg12 = (ImsCallProfile) data.readTypedObject(ImsCallProfile.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionConferenceExtended(_arg019, _arg12);
                            break;
                        case 20:
                            ImsReasonInfo _arg020 = (ImsReasonInfo) data.readTypedObject(ImsReasonInfo.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionConferenceExtendFailed(_arg020);
                            break;
                        case 21:
                            IImsCallSession _arg021 = IImsCallSession.Stub.asInterface(data.readStrongBinder());
                            ImsCallProfile _arg13 = (ImsCallProfile) data.readTypedObject(ImsCallProfile.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionConferenceExtendReceived(_arg021, _arg13);
                            break;
                        case 22:
                            callSessionInviteParticipantsRequestDelivered();
                            break;
                        case 23:
                            ImsReasonInfo _arg022 = (ImsReasonInfo) data.readTypedObject(ImsReasonInfo.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionInviteParticipantsRequestFailed(_arg022);
                            break;
                        case 24:
                            callSessionRemoveParticipantsRequestDelivered();
                            break;
                        case 25:
                            ImsReasonInfo _arg023 = (ImsReasonInfo) data.readTypedObject(ImsReasonInfo.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionRemoveParticipantsRequestFailed(_arg023);
                            break;
                        case 26:
                            ImsConferenceState _arg024 = (ImsConferenceState) data.readTypedObject(ImsConferenceState.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionConferenceStateUpdated(_arg024);
                            break;
                        case 27:
                            int _arg025 = data.readInt();
                            String _arg14 = data.readString();
                            data.enforceNoDataAvail();
                            callSessionUssdMessageReceived(_arg025, _arg14);
                            break;
                        case 28:
                            int _arg026 = data.readInt();
                            int _arg15 = data.readInt();
                            ImsReasonInfo _arg2 = (ImsReasonInfo) data.readTypedObject(ImsReasonInfo.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionHandover(_arg026, _arg15, _arg2);
                            break;
                        case 29:
                            int _arg027 = data.readInt();
                            int _arg16 = data.readInt();
                            ImsReasonInfo _arg22 = (ImsReasonInfo) data.readTypedObject(ImsReasonInfo.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionHandoverFailed(_arg027, _arg16, _arg22);
                            break;
                        case 30:
                            int _arg028 = data.readInt();
                            int _arg17 = data.readInt();
                            data.enforceNoDataAvail();
                            callSessionMayHandover(_arg028, _arg17);
                            break;
                        case 31:
                            int _arg029 = data.readInt();
                            data.enforceNoDataAvail();
                            callSessionTtyModeReceived(_arg029);
                            break;
                        case 32:
                            boolean _arg030 = data.readBoolean();
                            data.enforceNoDataAvail();
                            callSessionMultipartyStateChanged(_arg030);
                            break;
                        case 33:
                            ImsSuppServiceNotification _arg031 = (ImsSuppServiceNotification) data.readTypedObject(ImsSuppServiceNotification.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionSuppServiceReceived(_arg031);
                            break;
                        case 34:
                            ImsCallProfile _arg032 = (ImsCallProfile) data.readTypedObject(ImsCallProfile.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionRttModifyRequestReceived(_arg032);
                            break;
                        case 35:
                            int _arg033 = data.readInt();
                            data.enforceNoDataAvail();
                            callSessionRttModifyResponseReceived(_arg033);
                            break;
                        case 36:
                            String _arg034 = data.readString();
                            data.enforceNoDataAvail();
                            callSessionRttMessageReceived(_arg034);
                            break;
                        case 37:
                            ImsStreamMediaProfile _arg035 = (ImsStreamMediaProfile) data.readTypedObject(ImsStreamMediaProfile.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionRttAudioIndicatorChanged(_arg035);
                            break;
                        case 38:
                            callSessionTransferred();
                            break;
                        case 39:
                            ImsReasonInfo _arg036 = (ImsReasonInfo) data.readTypedObject(ImsReasonInfo.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionTransferFailed(_arg036);
                            break;
                        case 40:
                            char _arg037 = (char) data.readInt();
                            data.enforceNoDataAvail();
                            callSessionDtmfReceived(_arg037);
                            break;
                        case 41:
                            CallQuality _arg038 = (CallQuality) data.readTypedObject(CallQuality.CREATOR);
                            data.enforceNoDataAvail();
                            callQualityChanged(_arg038);
                            break;
                        case 42:
                            List<RtpHeaderExtension> _arg039 = data.createTypedArrayList(RtpHeaderExtension.CREATOR);
                            data.enforceNoDataAvail();
                            callSessionRtpHeaderExtensionsReceived(_arg039);
                            break;
                        case 43:
                            int _arg040 = data.readInt();
                            int _arg18 = data.readInt();
                            int _arg23 = data.readInt();
                            data.enforceNoDataAvail();
                            callSessionSendAnbrQuery(_arg040, _arg18, _arg23);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements IImsCallSessionListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IImsCallSessionListener.DESCRIPTOR;
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionInitiating(ImsCallProfile profile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(profile, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionInitiatingFailed(ImsReasonInfo reasonInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(reasonInfo, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionProgressing(ImsStreamMediaProfile profile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(profile, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionInitiated(ImsCallProfile profile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(profile, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionInitiatedFailed(ImsReasonInfo reasonInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(reasonInfo, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionTerminated(ImsReasonInfo reasonInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(reasonInfo, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionHeld(ImsCallProfile profile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(profile, 0);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionHoldFailed(ImsReasonInfo reasonInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(reasonInfo, 0);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionHoldReceived(ImsCallProfile profile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(profile, 0);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionResumed(ImsCallProfile profile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(profile, 0);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionResumeFailed(ImsReasonInfo reasonInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(reasonInfo, 0);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionResumeReceived(ImsCallProfile profile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(profile, 0);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionMergeStarted(IImsCallSession newSession, ImsCallProfile profile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeStrongInterface(newSession);
                    _data.writeTypedObject(profile, 0);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionMergeComplete(IImsCallSession session) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeStrongInterface(session);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionMergeFailed(ImsReasonInfo reasonInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(reasonInfo, 0);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionUpdated(ImsCallProfile profile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(profile, 0);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionUpdateFailed(ImsReasonInfo reasonInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(reasonInfo, 0);
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionUpdateReceived(ImsCallProfile profile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(profile, 0);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionConferenceExtended(IImsCallSession newSession, ImsCallProfile profile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeStrongInterface(newSession);
                    _data.writeTypedObject(profile, 0);
                    this.mRemote.transact(19, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionConferenceExtendFailed(ImsReasonInfo reasonInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(reasonInfo, 0);
                    this.mRemote.transact(20, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionConferenceExtendReceived(IImsCallSession newSession, ImsCallProfile profile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeStrongInterface(newSession);
                    _data.writeTypedObject(profile, 0);
                    this.mRemote.transact(21, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionInviteParticipantsRequestDelivered() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    this.mRemote.transact(22, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionInviteParticipantsRequestFailed(ImsReasonInfo reasonInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(reasonInfo, 0);
                    this.mRemote.transact(23, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionRemoveParticipantsRequestDelivered() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    this.mRemote.transact(24, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionRemoveParticipantsRequestFailed(ImsReasonInfo reasonInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(reasonInfo, 0);
                    this.mRemote.transact(25, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionConferenceStateUpdated(ImsConferenceState state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(state, 0);
                    this.mRemote.transact(26, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionUssdMessageReceived(int mode, String ussdMessage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeInt(mode);
                    _data.writeString(ussdMessage);
                    this.mRemote.transact(27, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionHandover(int srcNetworkType, int targetNetworkType, ImsReasonInfo reasonInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeInt(srcNetworkType);
                    _data.writeInt(targetNetworkType);
                    _data.writeTypedObject(reasonInfo, 0);
                    this.mRemote.transact(28, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionHandoverFailed(int srcNetworkType, int targetNetworkType, ImsReasonInfo reasonInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeInt(srcNetworkType);
                    _data.writeInt(targetNetworkType);
                    _data.writeTypedObject(reasonInfo, 0);
                    this.mRemote.transact(29, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionMayHandover(int srcNetworkType, int targetNetworkType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeInt(srcNetworkType);
                    _data.writeInt(targetNetworkType);
                    this.mRemote.transact(30, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionTtyModeReceived(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeInt(mode);
                    this.mRemote.transact(31, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionMultipartyStateChanged(boolean isMultiParty) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeBoolean(isMultiParty);
                    this.mRemote.transact(32, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionSuppServiceReceived(ImsSuppServiceNotification suppSrvNotification) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(suppSrvNotification, 0);
                    this.mRemote.transact(33, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionRttModifyRequestReceived(ImsCallProfile callProfile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(callProfile, 0);
                    this.mRemote.transact(34, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionRttModifyResponseReceived(int status) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeInt(status);
                    this.mRemote.transact(35, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionRttMessageReceived(String rttMessage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeString(rttMessage);
                    this.mRemote.transact(36, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionRttAudioIndicatorChanged(ImsStreamMediaProfile profile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(profile, 0);
                    this.mRemote.transact(37, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionTransferred() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    this.mRemote.transact(38, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionTransferFailed(ImsReasonInfo reasonInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(reasonInfo, 0);
                    this.mRemote.transact(39, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionDtmfReceived(char dtmf) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeInt(dtmf);
                    this.mRemote.transact(40, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callQualityChanged(CallQuality callQuality) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedObject(callQuality, 0);
                    this.mRemote.transact(41, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionRtpHeaderExtensionsReceived(List<RtpHeaderExtension> extensions) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeTypedList(extensions, 0);
                    this.mRemote.transact(42, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsCallSessionListener
            public void callSessionSendAnbrQuery(int mediaType, int direction, int bitsPerSecond) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsCallSessionListener.DESCRIPTOR);
                    _data.writeInt(mediaType);
                    _data.writeInt(direction);
                    _data.writeInt(bitsPerSecond);
                    this.mRemote.transact(43, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 42;
        }
    }
}
