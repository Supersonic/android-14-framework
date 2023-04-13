package android.net.sip;

import android.annotation.SystemApi;
import android.content.Context;
import android.media.AudioManager;
import android.net.rtp.AudioCodec;
import android.net.rtp.AudioGroup;
import android.net.rtp.AudioStream;
import android.net.sip.SimpleSessionDescription;
import android.net.sip.SipSession;
import android.net.wifi.WifiManager;
import android.os.Message;
import android.telephony.Rlog;
import android.text.TextUtils;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
/* loaded from: classes.dex */
public class SipAudioCall {
    private static final boolean DBG = false;
    private static final boolean DONT_RELEASE_SOCKET = false;
    private static final String LOG_TAG = SipAudioCall.class.getSimpleName();
    private static final boolean RELEASE_SOCKET = true;
    private static final int SESSION_TIMEOUT = 5;
    private static final int TRANSFER_TIMEOUT = 15;
    private AudioGroup mAudioGroup;
    private AudioStream mAudioStream;
    private Context mContext;
    private String mErrorMessage;
    private Listener mListener;
    private SipProfile mLocalProfile;
    private String mPeerSd;
    private SipSession mSipSession;
    private SipSession mTransferringSession;
    private WifiManager.WifiLock mWifiHighPerfLock;
    private WifiManager mWm;
    private long mSessionId = System.currentTimeMillis();
    private boolean mInCall = false;
    private boolean mMuted = false;
    private boolean mHold = false;
    private int mErrorCode = 0;
    private final Object mLock = new Object();

    /* renamed from: -$$Nest$mloge  reason: not valid java name */
    static /* bridge */ /* synthetic */ void m21$$Nest$mloge(SipAudioCall sipAudioCall, String str, Throwable th) {
        sipAudioCall.loge(str, th);
    }

    /* loaded from: classes.dex */
    public static class Listener {
        public void onReadyToCall(SipAudioCall call) {
            onChanged(call);
        }

        public void onCalling(SipAudioCall call) {
            onChanged(call);
        }

        public void onRinging(SipAudioCall call, SipProfile caller) {
            onChanged(call);
        }

        public void onRingingBack(SipAudioCall call) {
            onChanged(call);
        }

        public void onCallEstablished(SipAudioCall call) {
            onChanged(call);
        }

        public void onCallEnded(SipAudioCall call) {
            onChanged(call);
        }

        public void onCallBusy(SipAudioCall call) {
            onChanged(call);
        }

        public void onCallHeld(SipAudioCall call) {
            onChanged(call);
        }

        public void onError(SipAudioCall call, int errorCode, String errorMessage) {
        }

        public void onChanged(SipAudioCall call) {
        }
    }

    public SipAudioCall(Context context, SipProfile localProfile) {
        this.mContext = context;
        this.mLocalProfile = localProfile;
        this.mWm = (WifiManager) context.getSystemService("wifi");
    }

    public void setListener(Listener listener) {
        setListener(listener, false);
    }

    public void setListener(Listener listener, boolean callbackImmediately) {
        this.mListener = listener;
        if (listener != null && callbackImmediately) {
            try {
                int i = this.mErrorCode;
                if (i != 0) {
                    listener.onError(this, i, this.mErrorMessage);
                } else if (this.mInCall) {
                    if (this.mHold) {
                        listener.onCallHeld(this);
                    } else {
                        listener.onCallEstablished(this);
                    }
                } else {
                    int state = getState();
                    switch (state) {
                        case 0:
                            listener.onReadyToCall(this);
                            break;
                        case 3:
                            listener.onRinging(this, getPeerProfile());
                            break;
                        case 5:
                            listener.onCalling(this);
                            break;
                        case SipSession.State.OUTGOING_CALL_RING_BACK /* 6 */:
                            listener.onRingingBack(this);
                            break;
                    }
                }
            } catch (Throwable t) {
                loge("setListener()", t);
            }
        }
    }

    public boolean isInCall() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mInCall;
        }
        return z;
    }

    public boolean isOnHold() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mHold;
        }
        return z;
    }

    public void close() {
        close(RELEASE_SOCKET);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void close(boolean closeRtp) {
        if (closeRtp) {
            stopCall(RELEASE_SOCKET);
        }
        this.mInCall = false;
        this.mHold = false;
        this.mSessionId = System.currentTimeMillis();
        this.mErrorCode = 0;
        this.mErrorMessage = null;
        SipSession sipSession = this.mSipSession;
        if (sipSession != null) {
            sipSession.setListener(null);
            this.mSipSession = null;
        }
    }

    public SipProfile getLocalProfile() {
        SipProfile sipProfile;
        synchronized (this.mLock) {
            sipProfile = this.mLocalProfile;
        }
        return sipProfile;
    }

    public SipProfile getPeerProfile() {
        SipProfile peerProfile;
        synchronized (this.mLock) {
            SipSession sipSession = this.mSipSession;
            peerProfile = sipSession == null ? null : sipSession.getPeerProfile();
        }
        return peerProfile;
    }

    public int getState() {
        synchronized (this.mLock) {
            SipSession sipSession = this.mSipSession;
            if (sipSession == null) {
                return 0;
            }
            return sipSession.getState();
        }
    }

    public SipSession getSipSession() {
        SipSession sipSession;
        synchronized (this.mLock) {
            sipSession = this.mSipSession;
        }
        return sipSession;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void transferToNewSession() {
        SipSession sipSession = this.mTransferringSession;
        if (sipSession == null) {
            return;
        }
        SipSession origin = this.mSipSession;
        this.mSipSession = sipSession;
        this.mTransferringSession = null;
        AudioStream audioStream = this.mAudioStream;
        if (audioStream != null) {
            audioStream.join(null);
        } else {
            this.mAudioStream = new AudioStream(InetAddress.getByName(getLocalIp()));
        }
        if (origin != null) {
            origin.endCall();
        }
        startAudio();
    }

    private SipSession.Listener createListener() {
        return new SipSession.Listener() { // from class: android.net.sip.SipAudioCall.1
            @Override // android.net.sip.SipSession.Listener
            public void onCalling(SipSession session) {
                Listener listener = SipAudioCall.this.mListener;
                if (listener != null) {
                    try {
                        listener.onCalling(SipAudioCall.this);
                    } catch (Throwable t) {
                        SipAudioCall.this.loge("onCalling():", t);
                    }
                }
            }

            @Override // android.net.sip.SipSession.Listener
            public void onRingingBack(SipSession session) {
                Listener listener = SipAudioCall.this.mListener;
                if (listener != null) {
                    try {
                        listener.onRingingBack(SipAudioCall.this);
                    } catch (Throwable t) {
                        SipAudioCall.this.loge("onRingingBack():", t);
                    }
                }
            }

            @Override // android.net.sip.SipSession.Listener
            public void onRinging(SipSession session, SipProfile peerProfile, String sessionDescription) {
                synchronized (SipAudioCall.this.mLock) {
                    if (SipAudioCall.this.mSipSession == null || !SipAudioCall.this.mInCall || !session.getCallId().equals(SipAudioCall.this.mSipSession.getCallId())) {
                        session.endCall();
                        return;
                    }
                    String answer = SipAudioCall.this.createAnswer(sessionDescription).encode();
                    SipAudioCall.this.mSipSession.answerCall(answer, 5);
                }
            }

            @Override // android.net.sip.SipSession.Listener
            public void onCallEstablished(SipSession session, String sessionDescription) {
                SipAudioCall.this.mPeerSd = sessionDescription;
                if (SipAudioCall.this.mTransferringSession != null && session == SipAudioCall.this.mTransferringSession) {
                    SipAudioCall.this.transferToNewSession();
                    return;
                }
                Listener listener = SipAudioCall.this.mListener;
                if (listener != null) {
                    try {
                        if (SipAudioCall.this.mHold) {
                            listener.onCallHeld(SipAudioCall.this);
                        } else {
                            listener.onCallEstablished(SipAudioCall.this);
                        }
                    } catch (Throwable t) {
                        SipAudioCall.this.loge("onCallEstablished(): ", t);
                    }
                }
            }

            @Override // android.net.sip.SipSession.Listener
            public void onCallEnded(SipSession session) {
                if (session == SipAudioCall.this.mTransferringSession) {
                    SipAudioCall.this.mTransferringSession = null;
                } else if (SipAudioCall.this.mTransferringSession != null || session != SipAudioCall.this.mSipSession) {
                } else {
                    Listener listener = SipAudioCall.this.mListener;
                    if (listener != null) {
                        try {
                            listener.onCallEnded(SipAudioCall.this);
                        } catch (Throwable t) {
                            SipAudioCall.this.loge("onCallEnded(): ", t);
                        }
                    }
                    SipAudioCall.this.close();
                }
            }

            @Override // android.net.sip.SipSession.Listener
            public void onCallBusy(SipSession session) {
                Listener listener = SipAudioCall.this.mListener;
                if (listener != null) {
                    try {
                        listener.onCallBusy(SipAudioCall.this);
                    } catch (Throwable t) {
                        SipAudioCall.this.loge("onCallBusy(): ", t);
                    }
                }
                SipAudioCall.this.close(false);
            }

            @Override // android.net.sip.SipSession.Listener
            public void onCallChangeFailed(SipSession session, int errorCode, String message) {
                SipAudioCall.this.mErrorCode = errorCode;
                SipAudioCall.this.mErrorMessage = message;
                Listener listener = SipAudioCall.this.mListener;
                if (listener != null) {
                    try {
                        SipAudioCall sipAudioCall = SipAudioCall.this;
                        listener.onError(sipAudioCall, sipAudioCall.mErrorCode, message);
                    } catch (Throwable t) {
                        SipAudioCall.this.loge("onCallBusy():", t);
                    }
                }
            }

            @Override // android.net.sip.SipSession.Listener
            public void onError(SipSession session, int errorCode, String message) {
                SipAudioCall.this.onError(errorCode, message);
            }

            @Override // android.net.sip.SipSession.Listener
            public void onRegistering(SipSession session) {
            }

            @Override // android.net.sip.SipSession.Listener
            public void onRegistrationTimeout(SipSession session) {
            }

            @Override // android.net.sip.SipSession.Listener
            public void onRegistrationFailed(SipSession session, int errorCode, String message) {
            }

            @Override // android.net.sip.SipSession.Listener
            public void onRegistrationDone(SipSession session, int duration) {
            }

            @Override // android.net.sip.SipSession.Listener
            public void onCallTransferring(SipSession newSession, String sessionDescription) {
                SipAudioCall.this.mTransferringSession = newSession;
                try {
                    if (sessionDescription == null) {
                        newSession.makeCall(newSession.getPeerProfile(), SipAudioCall.this.createOffer().encode(), SipAudioCall.TRANSFER_TIMEOUT);
                    } else {
                        String answer = SipAudioCall.this.createAnswer(sessionDescription).encode();
                        newSession.answerCall(answer, 5);
                    }
                } catch (Throwable e) {
                    SipAudioCall.this.loge("onCallTransferring()", e);
                    newSession.endCall();
                }
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x001d, code lost:
        if (isInCall() == false) goto L7;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onError(int errorCode, String message) {
        this.mErrorCode = errorCode;
        this.mErrorMessage = message;
        Listener listener = this.mListener;
        if (listener != null) {
            try {
                listener.onError(this, errorCode, message);
            } catch (Throwable t) {
                loge("onError():", t);
            }
        }
        synchronized (this.mLock) {
            if (errorCode != -10) {
            }
            close(RELEASE_SOCKET);
        }
    }

    public void attachCall(SipSession session, String sessionDescription) throws SipException {
        if (!SipManager.isVoipSupported(this.mContext)) {
            throw new SipException("VOIP API is not supported");
        }
        synchronized (this.mLock) {
            this.mSipSession = session;
            this.mPeerSd = sessionDescription;
            session.setListener(createListener());
        }
    }

    public void makeCall(SipProfile peerProfile, SipSession sipSession, int timeout) throws SipException {
        if (!SipManager.isVoipSupported(this.mContext)) {
            throw new SipException("VOIP API is not supported");
        }
        synchronized (this.mLock) {
            this.mSipSession = sipSession;
            try {
                this.mAudioStream = new AudioStream(InetAddress.getByName(getLocalIp()));
                sipSession.setListener(createListener());
                sipSession.makeCall(peerProfile, createOffer().encode(), timeout);
            } catch (IOException e) {
                loge("makeCall:", e);
                throw new SipException("makeCall()", e);
            }
        }
    }

    public void endCall() throws SipException {
        synchronized (this.mLock) {
            stopCall(RELEASE_SOCKET);
            this.mInCall = false;
            SipSession sipSession = this.mSipSession;
            if (sipSession != null) {
                sipSession.endCall();
            }
        }
    }

    public void holdCall(int timeout) throws SipException {
        synchronized (this.mLock) {
            if (this.mHold) {
                return;
            }
            SipSession sipSession = this.mSipSession;
            if (sipSession == null) {
                loge("holdCall:");
                throw new SipException("Not in a call to hold call");
            }
            sipSession.changeCall(createHoldOffer().encode(), timeout);
            this.mHold = RELEASE_SOCKET;
            setAudioGroupMode();
        }
    }

    public void answerCall(int timeout) throws SipException {
        synchronized (this.mLock) {
            if (this.mSipSession == null) {
                throw new SipException("No call to answer");
            }
            try {
                this.mAudioStream = new AudioStream(InetAddress.getByName(getLocalIp()));
                this.mSipSession.answerCall(createAnswer(this.mPeerSd).encode(), timeout);
            } catch (IOException e) {
                loge("answerCall:", e);
                throw new SipException("answerCall()", e);
            }
        }
    }

    public void continueCall(int timeout) throws SipException {
        synchronized (this.mLock) {
            if (this.mHold) {
                this.mSipSession.changeCall(createContinueOffer().encode(), timeout);
                this.mHold = false;
                setAudioGroupMode();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SimpleSessionDescription createOffer() {
        AudioCodec[] codecs;
        SimpleSessionDescription offer = new SimpleSessionDescription(this.mSessionId, getLocalIp());
        AudioCodec.getCodecs();
        SimpleSessionDescription.Media media = offer.newMedia("audio", this.mAudioStream.getLocalPort(), 1, "RTP/AVP");
        for (AudioCodec codec : AudioCodec.getCodecs()) {
            media.setRtpPayload(codec.type, codec.rtpmap, codec.fmtp);
        }
        media.setRtpPayload(127, "telephone-event/8000", "0-15");
        return offer;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SimpleSessionDescription createAnswer(String offerSd) {
        SimpleSessionDescription.Media[] media;
        String[] formats;
        int[] rtpPayloadTypes;
        int[] rtpPayloadTypes2;
        if (TextUtils.isEmpty(offerSd)) {
            return createOffer();
        }
        SimpleSessionDescription offer = new SimpleSessionDescription(offerSd);
        SimpleSessionDescription answer = new SimpleSessionDescription(this.mSessionId, getLocalIp());
        AudioCodec codec = null;
        for (SimpleSessionDescription.Media media2 : offer.getMedia()) {
            if (codec == null && media2.getPort() > 0 && "audio".equals(media2.getType()) && "RTP/AVP".equals(media2.getProtocol())) {
                for (int type : media2.getRtpPayloadTypes()) {
                    codec = AudioCodec.getCodec(type, media2.getRtpmap(type), media2.getFmtp(type));
                    if (codec != null) {
                        break;
                    }
                }
                if (codec != null) {
                    SimpleSessionDescription.Media reply = answer.newMedia("audio", this.mAudioStream.getLocalPort(), 1, "RTP/AVP");
                    reply.setRtpPayload(codec.type, codec.rtpmap, codec.fmtp);
                    for (int type2 : media2.getRtpPayloadTypes()) {
                        String rtpmap = media2.getRtpmap(type2);
                        if (type2 != codec.type && rtpmap != null && rtpmap.startsWith("telephone-event")) {
                            reply.setRtpPayload(type2, rtpmap, media2.getFmtp(type2));
                        }
                    }
                    if (media2.getAttribute("recvonly") != null) {
                        answer.setAttribute("sendonly", "");
                    } else if (media2.getAttribute("sendonly") != null) {
                        answer.setAttribute("recvonly", "");
                    } else if (offer.getAttribute("recvonly") != null) {
                        answer.setAttribute("sendonly", "");
                    } else if (offer.getAttribute("sendonly") != null) {
                        answer.setAttribute("recvonly", "");
                    }
                }
            }
            SimpleSessionDescription.Media reply2 = answer.newMedia(media2.getType(), 0, 1, media2.getProtocol());
            for (String format : media2.getFormats()) {
                reply2.setFormat(format, null);
            }
        }
        if (codec == null) {
            loge("createAnswer: no suitable codes");
            throw new IllegalStateException("Reject SDP: no suitable codecs");
        }
        return answer;
    }

    private SimpleSessionDescription createHoldOffer() {
        SimpleSessionDescription offer = createContinueOffer();
        offer.setAttribute("sendonly", "");
        return offer;
    }

    private SimpleSessionDescription createContinueOffer() {
        SimpleSessionDescription offer = new SimpleSessionDescription(this.mSessionId, getLocalIp());
        SimpleSessionDescription.Media media = offer.newMedia("audio", this.mAudioStream.getLocalPort(), 1, "RTP/AVP");
        AudioCodec codec = this.mAudioStream.getCodec();
        media.setRtpPayload(codec.type, codec.rtpmap, codec.fmtp);
        int dtmfType = this.mAudioStream.getDtmfType();
        if (dtmfType != -1) {
            media.setRtpPayload(dtmfType, "telephone-event/8000", "0-15");
        }
        return offer;
    }

    private void grabWifiHighPerfLock() {
        if (this.mWifiHighPerfLock == null) {
            WifiManager.WifiLock createWifiLock = ((WifiManager) this.mContext.getSystemService("wifi")).createWifiLock(3, LOG_TAG);
            this.mWifiHighPerfLock = createWifiLock;
            createWifiLock.acquire();
        }
    }

    private void releaseWifiHighPerfLock() {
        WifiManager.WifiLock wifiLock = this.mWifiHighPerfLock;
        if (wifiLock != null) {
            wifiLock.release();
            this.mWifiHighPerfLock = null;
        }
    }

    private boolean isWifiOn() {
        if (this.mWm.getConnectionInfo().getBSSID() == null) {
            return false;
        }
        return RELEASE_SOCKET;
    }

    public void toggleMute() {
        synchronized (this.mLock) {
            this.mMuted = !this.mMuted ? RELEASE_SOCKET : false;
            setAudioGroupMode();
        }
    }

    public boolean isMuted() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mMuted;
        }
        return z;
    }

    public void setSpeakerMode(boolean speakerMode) {
        synchronized (this.mLock) {
            ((AudioManager) this.mContext.getSystemService("audio")).setSpeakerphoneOn(speakerMode);
            setAudioGroupMode();
        }
    }

    private boolean isSpeakerOn() {
        return ((AudioManager) this.mContext.getSystemService("audio")).isSpeakerphoneOn();
    }

    public void sendDtmf(int code) {
        sendDtmf(code, null);
    }

    public void sendDtmf(int code, Message result) {
        synchronized (this.mLock) {
            AudioGroup audioGroup = getAudioGroup();
            if (audioGroup != null && this.mSipSession != null && 8 == getState()) {
                audioGroup.sendDtmf(code);
            }
            if (result != null) {
                result.sendToTarget();
            }
        }
    }

    public AudioStream getAudioStream() {
        AudioStream audioStream;
        synchronized (this.mLock) {
            audioStream = this.mAudioStream;
        }
        return audioStream;
    }

    @SystemApi
    public AudioGroup getAudioGroup() {
        synchronized (this.mLock) {
            AudioGroup audioGroup = this.mAudioGroup;
            if (audioGroup != null) {
                return audioGroup;
            }
            AudioStream audioStream = this.mAudioStream;
            return audioStream == null ? null : audioStream.getGroup();
        }
    }

    @SystemApi
    public void setAudioGroup(AudioGroup group) {
        synchronized (this.mLock) {
            AudioStream audioStream = this.mAudioStream;
            if (audioStream != null && audioStream.getGroup() != null) {
                this.mAudioStream.join(group);
            }
            this.mAudioGroup = group;
        }
    }

    public void startAudio() {
        try {
            startAudioInternal();
        } catch (UnknownHostException e) {
            onError(-7, e.getMessage());
        } catch (Throwable e2) {
            onError(-4, e2.getMessage());
        }
    }

    private synchronized void startAudioInternal() throws UnknownHostException {
        int[] rtpPayloadTypes;
        int[] rtpPayloadTypes2;
        if (this.mPeerSd == null) {
            throw new IllegalStateException("mPeerSd = null");
        }
        stopCall(false);
        this.mInCall = RELEASE_SOCKET;
        SimpleSessionDescription offer = new SimpleSessionDescription(this.mPeerSd);
        AudioStream stream = this.mAudioStream;
        AudioCodec codec = null;
        SimpleSessionDescription.Media[] media = offer.getMedia();
        int length = media.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            SimpleSessionDescription.Media media2 = media[i];
            if (codec == null && media2.getPort() > 0 && "audio".equals(media2.getType()) && "RTP/AVP".equals(media2.getProtocol())) {
                for (int type : media2.getRtpPayloadTypes()) {
                    codec = AudioCodec.getCodec(type, media2.getRtpmap(type), media2.getFmtp(type));
                    if (codec != null) {
                        break;
                    }
                }
                if (codec != null) {
                    String address = media2.getAddress();
                    if (address == null) {
                        address = offer.getAddress();
                    }
                    stream.associate(InetAddress.getByName(address), media2.getPort());
                    stream.setDtmfType(-1);
                    stream.setCodec(codec);
                    for (int type2 : media2.getRtpPayloadTypes()) {
                        String rtpmap = media2.getRtpmap(type2);
                        if (type2 != codec.type && rtpmap != null && rtpmap.startsWith("telephone-event")) {
                            stream.setDtmfType(type2);
                        }
                    }
                    if (this.mHold) {
                        stream.setMode(0);
                    } else if (media2.getAttribute("recvonly") != null) {
                        stream.setMode(1);
                    } else if (media2.getAttribute("sendonly") != null) {
                        stream.setMode(2);
                    } else if (offer.getAttribute("recvonly") != null) {
                        stream.setMode(1);
                    } else if (offer.getAttribute("sendonly") != null) {
                        stream.setMode(2);
                    } else {
                        stream.setMode(0);
                    }
                }
            }
            i++;
        }
        if (codec == null) {
            throw new IllegalStateException("Reject SDP: no suitable codecs");
        }
        if (isWifiOn()) {
            grabWifiHighPerfLock();
        }
        AudioGroup audioGroup = getAudioGroup();
        if (!this.mHold) {
            if (audioGroup == null) {
                audioGroup = new AudioGroup(this.mContext);
            }
            stream.join(audioGroup);
        }
        setAudioGroupMode();
    }

    private void setAudioGroupMode() {
        AudioGroup audioGroup = getAudioGroup();
        if (audioGroup != null) {
            if (this.mHold) {
                audioGroup.setMode(0);
            } else if (this.mMuted) {
                audioGroup.setMode(1);
            } else if (isSpeakerOn()) {
                audioGroup.setMode(3);
            } else {
                audioGroup.setMode(2);
            }
        }
    }

    private void stopCall(boolean releaseSocket) {
        releaseWifiHighPerfLock();
        AudioStream audioStream = this.mAudioStream;
        if (audioStream != null) {
            audioStream.join(null);
            if (releaseSocket) {
                this.mAudioStream.release();
                this.mAudioStream = null;
            }
        }
    }

    private String getLocalIp() {
        return this.mSipSession.getLocalIp();
    }

    private void throwSipException(Throwable throwable) throws SipException {
        if (throwable instanceof SipException) {
            throw ((SipException) throwable);
        }
        throw new SipException("", throwable);
    }

    private void log(String s) {
        Rlog.d(LOG_TAG, s);
    }

    private void loge(String s) {
        Rlog.e(LOG_TAG, s);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loge(String s, Throwable t) {
        Rlog.e(LOG_TAG, s, t);
    }
}
