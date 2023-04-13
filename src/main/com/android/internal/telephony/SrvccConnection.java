package com.android.internal.telephony;

import android.net.Uri;
import android.telephony.ims.ImsCallProfile;
import android.telephony.ims.ImsStreamMediaProfile;
import android.text.TextUtils;
import com.android.ims.internal.ConferenceParticipant;
import com.android.internal.telephony.Call;
import com.android.internal.telephony.imsphone.ImsPhoneConnection;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public class SrvccConnection {
    public static final int CALL_TYPE_EMERGENCY = 1;
    public static final int CALL_TYPE_NORMAL = 0;
    public static final int SUBSTATE_NONE = 0;
    public static final int SUBSTATE_PREALERTING = 1;
    public static final int TONE_LOCAL = 1;
    public static final int TONE_NETWORK = 2;
    public static final int TONE_NONE = 0;
    private boolean mIsMT;
    private boolean mIsMpty;
    private String mName;
    private int mNamePresentation;
    private int mNumPresentation;
    private String mNumber;
    private int mRingbackToneType;
    private Call.State mState;
    private int mSubstate;
    private int mType;

    public SrvccConnection(ImsCallProfile imsCallProfile, ImsPhoneConnection imsPhoneConnection, int i) {
        this.mType = 0;
        this.mSubstate = 0;
        this.mRingbackToneType = 0;
        this.mIsMpty = false;
        Call.State callState = toCallState(i);
        this.mState = callState;
        if (callState == Call.State.ALERTING) {
            this.mRingbackToneType = isLocalTone(imsCallProfile) ? 1 : 2;
        }
        if (i == 9) {
            this.mSubstate = 1;
        }
        if (imsPhoneConnection == null) {
            initialize(imsCallProfile);
        } else {
            initialize(imsPhoneConnection);
        }
    }

    public SrvccConnection(ConferenceParticipant conferenceParticipant, int i) {
        this.mType = 0;
        this.mSubstate = 0;
        this.mRingbackToneType = 0;
        this.mIsMpty = false;
        Rlog.d("SrvccConnection", "initialize with ConferenceParticipant");
        this.mState = toCallState(i);
        this.mIsMT = conferenceParticipant.getCallDirection() == 0;
        this.mNumber = getParticipantAddress(conferenceParticipant.getHandle());
        int participantPresentation = conferenceParticipant.getParticipantPresentation();
        this.mNumPresentation = participantPresentation;
        if (participantPresentation == 2) {
            this.mNumber = PhoneConfigurationManager.SSSS;
        }
        String displayName = conferenceParticipant.getDisplayName();
        this.mName = displayName;
        if (!TextUtils.isEmpty(displayName)) {
            this.mNamePresentation = 1;
        } else {
            this.mNamePresentation = 3;
        }
        this.mIsMpty = true;
    }

    private static String getParticipantAddress(Uri uri) {
        if (uri == null) {
            return null;
        }
        String schemeSpecificPart = uri.getSchemeSpecificPart();
        if (TextUtils.isEmpty(schemeSpecificPart)) {
            return null;
        }
        String[] split = schemeSpecificPart.split("[@;:]");
        if (split.length == 0) {
            return null;
        }
        return split[0];
    }

    private void initialize(ImsCallProfile imsCallProfile) {
        Rlog.d("SrvccConnection", "initialize with ImsCallProfile");
        this.mIsMT = true;
        this.mNumber = imsCallProfile.getCallExtra("oi");
        this.mName = imsCallProfile.getCallExtra("cna");
        this.mNumPresentation = ImsCallProfile.OIRToPresentation(imsCallProfile.getCallExtraInt("oir"));
        this.mNamePresentation = ImsCallProfile.OIRToPresentation(imsCallProfile.getCallExtraInt("cnap"));
    }

    private void initialize(ImsPhoneConnection imsPhoneConnection) {
        Rlog.d("SrvccConnection", "initialize with ImsPhoneConnection");
        if (imsPhoneConnection.isEmergencyCall()) {
            this.mType = 1;
        }
        this.mIsMT = imsPhoneConnection.isIncoming();
        this.mNumber = imsPhoneConnection.getAddress();
        this.mNumPresentation = imsPhoneConnection.getNumberPresentation();
        this.mName = imsPhoneConnection.getCnapName();
        this.mNamePresentation = imsPhoneConnection.getCnapNamePresentation();
    }

    private boolean isLocalTone(ImsCallProfile imsCallProfile) {
        ImsStreamMediaProfile mediaProfile;
        return (imsCallProfile == null || (mediaProfile = imsCallProfile.getMediaProfile()) == null || mediaProfile.getAudioDirection() != 0) ? false : true;
    }

    private static Call.State toCallState(int i) {
        if (i != 9) {
            switch (i) {
                case 1:
                    return Call.State.ACTIVE;
                case 2:
                    return Call.State.HOLDING;
                case 3:
                    return Call.State.DIALING;
                case 4:
                    return Call.State.ALERTING;
                case 5:
                    return Call.State.INCOMING;
                case 6:
                    return Call.State.WAITING;
                default:
                    return Call.State.DISCONNECTED;
            }
        }
        return Call.State.INCOMING;
    }

    public int getType() {
        return this.mType;
    }

    public Call.State getState() {
        return this.mState;
    }

    public void setState(Call.State state) {
        this.mState = state;
    }

    public int getSubState() {
        return this.mSubstate;
    }

    public int getRingbackToneType() {
        return this.mRingbackToneType;
    }

    public boolean isMultiParty() {
        return this.mIsMpty;
    }

    public boolean isIncoming() {
        return this.mIsMT;
    }

    public String getNumber() {
        return this.mNumber;
    }

    public int getNumberPresentation() {
        return this.mNumPresentation;
    }

    public String getName() {
        return this.mName;
    }

    public int getNamePresentation() {
        return this.mNamePresentation;
    }

    public String toString() {
        return " type:" + getType() + ", state:" + getState() + ", subState:" + getSubState() + ", toneType:" + getRingbackToneType() + ", mpty:" + isMultiParty() + ", incoming:" + isIncoming() + ", numberPresentation:" + getNumberPresentation() + ", number:" + Rlog.pii("SrvccConnection", getNumber()) + ", namePresentation:" + getNamePresentation() + ", name:" + Rlog.pii("SrvccConnection", getName());
    }
}
