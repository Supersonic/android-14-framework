package com.android.server.sip;

import android.net.sip.ISipSession;
import android.net.sip.ISipSessionListener;
import android.net.sip.SipProfile;
import android.net.sip.SipSession;
import android.net.sip.SipSessionAdapter;
import android.telephony.Rlog;
import android.text.TextUtils;
import gov.nist.core.Separators;
import gov.nist.javax.sip.clientauthutils.AccountManager;
import gov.nist.javax.sip.clientauthutils.UserCredentials;
import gov.nist.javax.sip.header.ProxyAuthenticate;
import gov.nist.javax.sip.header.StatusLine;
import gov.nist.javax.sip.header.WWWAuthenticate;
import gov.nist.javax.sip.header.extensions.ReferredByHeader;
import gov.nist.javax.sip.header.extensions.ReplacesHeader;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.ObjectInUseException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.Transaction;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.HeaderAddress;
import javax.sip.header.ReferToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Message;
import javax.sip.message.Request;
import javax.sip.message.Response;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class SipSessionGroup implements SipListener {
    private static final String ANONYMOUS = "anonymous";
    private static final int CANCEL_CALL_TIMER = 3;
    private static final boolean DBG = false;
    private static final boolean DBG_PING = false;
    private static final EventObject DEREGISTER = new EventObject("Deregister");
    private static final EventObject END_CALL = new EventObject("End call");
    private static final int END_CALL_TIMER = 3;
    private static final int EXPIRY_TIME = 3600;
    private static final int INCALL_KEEPALIVE_INTERVAL = 10;
    private static final int KEEPALIVE_TIMEOUT = 5;
    private static final String TAG = "SipSession";
    private static final String THREAD_POOL_SIZE = "1";
    private static final long WAKE_LOCK_HOLDING_TIME = 500;
    private SipSessionImpl mCallReceiverSession;
    private String mExternalIp;
    private int mExternalPort;
    private String mLocalIp;
    private final SipProfile mLocalProfile;
    private final String mPassword;
    private Map<String, SipSessionImpl> mSessionMap = new HashMap();
    private SipHelper mSipHelper;
    private SipStack mSipStack;
    private SipWakeLock mWakeLock;
    private SipWakeupTimer mWakeupTimer;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface KeepAliveProcessCallback {
        void onError(int i, String str);

        void onResponse(boolean z);
    }

    /* renamed from: -$$Nest$fgetmLocalProfile  reason: not valid java name */
    static /* bridge */ /* synthetic */ SipProfile m60$$Nest$fgetmLocalProfile(SipSessionGroup sipSessionGroup) {
        return sipSessionGroup.mLocalProfile;
    }

    /* renamed from: -$$Nest$mgetRootCause  reason: not valid java name */
    static /* bridge */ /* synthetic */ Throwable m69$$Nest$mgetRootCause(SipSessionGroup sipSessionGroup, Throwable th) {
        return sipSessionGroup.getRootCause(th);
    }

    /* renamed from: -$$Nest$mloge  reason: not valid java name */
    static /* bridge */ /* synthetic */ void m70$$Nest$mloge(SipSessionGroup sipSessionGroup, String str, Throwable th) {
        sipSessionGroup.loge(str, th);
    }

    public SipSessionGroup(SipProfile profile, String password, SipWakeupTimer timer, SipWakeLock wakeLock) throws SipException {
        this.mLocalProfile = profile;
        this.mPassword = password;
        this.mWakeupTimer = timer;
        this.mWakeLock = wakeLock;
        reset();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setWakeupTimer(SipWakeupTimer timer) {
        this.mWakeupTimer = timer;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0080, code lost:
        r4 = r9.getLocalAddress().getHostAddress();
        r2 = r9.getLocalPort();
        r9.close();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public synchronized void reset() throws SipException {
        Properties properties = new Properties();
        String protocol = this.mLocalProfile.getProtocol();
        int port = this.mLocalProfile.getPort();
        String server = this.mLocalProfile.getProxyAddress();
        if (!TextUtils.isEmpty(server)) {
            properties.setProperty("javax.sip.OUTBOUND_PROXY", server + ':' + port + '/' + protocol);
        } else {
            server = this.mLocalProfile.getSipDomain();
        }
        if (server.startsWith("[") && server.endsWith("]")) {
            server = server.substring(1, server.length() - 1);
        }
        String local = null;
        try {
            InetAddress[] allByName = InetAddress.getAllByName(server);
            int length = allByName.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                InetAddress remote = allByName[i];
                DatagramSocket socket = new DatagramSocket();
                socket.connect(remote, port);
                if (socket.isConnected()) {
                    break;
                }
                socket.close();
                i++;
            }
        } catch (Exception e) {
        }
        if (local == null) {
            return;
        }
        close();
        this.mLocalIp = local;
        properties.setProperty("javax.sip.STACK_NAME", getStackName());
        properties.setProperty("gov.nist.javax.sip.THREAD_POOL_SIZE", THREAD_POOL_SIZE);
        SipStack createSipStack = SipFactory.getInstance().createSipStack(properties);
        this.mSipStack = createSipStack;
        try {
            try {
                SipProvider provider = createSipStack.createSipProvider(createSipStack.createListeningPoint(local, port, protocol));
                provider.addSipListener(this);
                this.mSipHelper = new SipHelper(this.mSipStack, provider);
                this.mSipStack.start();
            } catch (Exception e2) {
                throw new SipException("failed to initialize SIP stack", e2);
            }
        } catch (SipException e3) {
            throw e3;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void onConnectivityChanged() {
        SipSessionImpl[] ss = (SipSessionImpl[]) this.mSessionMap.values().toArray(new SipSessionImpl[this.mSessionMap.size()]);
        for (SipSessionImpl s : ss) {
            s.onError(-10, "data connection lost");
        }
    }

    synchronized void resetExternalAddress() {
        this.mExternalIp = null;
        this.mExternalPort = 0;
    }

    public SipProfile getLocalProfile() {
        return this.mLocalProfile;
    }

    public String getLocalProfileUri() {
        return this.mLocalProfile.getUriString();
    }

    private String getStackName() {
        return "stack" + System.currentTimeMillis();
    }

    public synchronized void close() {
        onConnectivityChanged();
        this.mSessionMap.clear();
        closeToNotReceiveCalls();
        SipStack sipStack = this.mSipStack;
        if (sipStack != null) {
            sipStack.stop();
            this.mSipStack = null;
            this.mSipHelper = null;
        }
        resetExternalAddress();
    }

    public synchronized boolean isClosed() {
        return this.mSipStack == null;
    }

    public synchronized void openToReceiveCalls(ISipSessionListener listener) {
        SipSessionImpl sipSessionImpl = this.mCallReceiverSession;
        if (sipSessionImpl == null) {
            this.mCallReceiverSession = new SipSessionCallReceiverImpl(listener);
        } else {
            sipSessionImpl.setListener(listener);
        }
    }

    public synchronized void closeToNotReceiveCalls() {
        this.mCallReceiverSession = null;
    }

    public ISipSession createSession(ISipSessionListener listener) {
        if (isClosed()) {
            return null;
        }
        return new SipSessionImpl(listener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean containsSession(String callId) {
        return this.mSessionMap.containsKey(callId);
    }

    private synchronized SipSessionImpl getSipSession(EventObject event) {
        SipSessionImpl session;
        String key = SipHelper.getCallId(event);
        session = this.mSessionMap.get(key);
        if (session != null && isLoggable(session)) {
            for (String str : this.mSessionMap.keySet()) {
            }
        }
        return session != null ? session : this.mCallReceiverSession;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void addSipSession(SipSessionImpl newSession) {
        removeSipSession(newSession);
        String key = newSession.getCallId();
        this.mSessionMap.put(key, newSession);
        if (isLoggable(newSession)) {
            for (String str : this.mSessionMap.keySet()) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void removeSipSession(SipSessionImpl session) {
        if (session == this.mCallReceiverSession) {
            return;
        }
        String key = session.getCallId();
        SipSessionImpl s = this.mSessionMap.remove(key);
        if (s != null && s != session) {
            this.mSessionMap.put(key, s);
            for (Map.Entry<String, SipSessionImpl> entry : this.mSessionMap.entrySet()) {
                if (entry.getValue() == s) {
                    this.mSessionMap.remove(entry.getKey());
                }
            }
        }
        if (s != null && isLoggable(s)) {
            for (String str : this.mSessionMap.keySet()) {
            }
        }
    }

    @Override // javax.sip.SipListener
    public void processRequest(RequestEvent event) {
        if (isRequestEvent("INVITE", event)) {
            this.mWakeLock.acquire(WAKE_LOCK_HOLDING_TIME);
        }
        process(event);
    }

    @Override // javax.sip.SipListener
    public void processResponse(ResponseEvent event) {
        process(event);
    }

    @Override // javax.sip.SipListener
    public void processIOException(IOExceptionEvent event) {
        process(event);
    }

    @Override // javax.sip.SipListener
    public void processTimeout(TimeoutEvent event) {
        process(event);
    }

    @Override // javax.sip.SipListener
    public void processTransactionTerminated(TransactionTerminatedEvent event) {
        process(event);
    }

    @Override // javax.sip.SipListener
    public void processDialogTerminated(DialogTerminatedEvent event) {
        process(event);
    }

    private synchronized void process(EventObject event) {
        SipSessionImpl session = getSipSession(event);
        boolean isLoggable = isLoggable(session, event);
        boolean processed = session != null && session.process(event);
        if (isLoggable && processed) {
            log("process: event new state after: " + SipSession.State.toString(session.mState));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String extractContent(Message message) {
        byte[] bytes = message.getRawContent();
        if (bytes != null) {
            try {
                if (message instanceof SIPMessage) {
                    return ((SIPMessage) message).getMessageContent();
                }
                return new String(bytes, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void extractExternalAddress(ResponseEvent evt) {
        Response response = evt.getResponse();
        ViaHeader viaHeader = (ViaHeader) response.getHeader("Via");
        if (viaHeader == null) {
            return;
        }
        int rport = viaHeader.getRPort();
        String externalIp = viaHeader.getReceived();
        if (rport > 0 && externalIp != null) {
            this.mExternalIp = externalIp;
            this.mExternalPort = rport;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Throwable getRootCause(Throwable exception) {
        Throwable cause = exception.getCause();
        while (cause != null) {
            exception = cause;
            cause = exception.getCause();
        }
        return exception;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SipSessionImpl createNewSession(RequestEvent event, ISipSessionListener listener, ServerTransaction transaction, int newState) throws SipException {
        SipSessionImpl newSession = new SipSessionImpl(listener);
        newSession.mServerTransaction = transaction;
        newSession.mState = newState;
        newSession.mDialog = newSession.mServerTransaction.getDialog();
        newSession.mInviteReceived = event;
        newSession.mPeerProfile = createPeerProfile((HeaderAddress) event.getRequest().getHeader("From"));
        newSession.mPeerSessionDescription = extractContent(event.getRequest());
        return newSession;
    }

    /* loaded from: classes.dex */
    private class SipSessionCallReceiverImpl extends SipSessionImpl {
        private static final boolean SSCRI_DBG = true;
        private static final String SSCRI_TAG = "SipSessionCallReceiverImpl";

        public SipSessionCallReceiverImpl(ISipSessionListener listener) {
            super(listener);
        }

        private int processInviteWithReplaces(RequestEvent event, ReplacesHeader replaces) {
            ReferredByHeader referredBy;
            String callId = replaces.getCallId();
            SipSessionImpl session = (SipSessionImpl) SipSessionGroup.this.mSessionMap.get(callId);
            if (session == null) {
                return Response.CALL_OR_TRANSACTION_DOES_NOT_EXIST;
            }
            Dialog dialog = session.mDialog;
            if (dialog == null) {
                return Response.DECLINE;
            }
            if (!dialog.getLocalTag().equals(replaces.getToTag()) || !dialog.getRemoteTag().equals(replaces.getFromTag()) || (referredBy = (ReferredByHeader) event.getRequest().getHeader("Referred-By")) == null || !dialog.getRemoteParty().equals(referredBy.getAddress())) {
                return Response.CALL_OR_TRANSACTION_DOES_NOT_EXIST;
            }
            return Response.f51OK;
        }

        private void processNewInviteRequest(RequestEvent event) throws SipException {
            ReplacesHeader replaces = (ReplacesHeader) event.getRequest().getHeader("Replaces");
            SipSessionImpl newSession = null;
            if (replaces != null) {
                int response = processInviteWithReplaces(event, replaces);
                log("processNewInviteRequest: " + replaces + " response=" + response);
                if (response == 200) {
                    SipSessionImpl replacedSession = (SipSessionImpl) SipSessionGroup.this.mSessionMap.get(replaces.getCallId());
                    newSession = SipSessionGroup.this.createNewSession(event, replacedSession.mProxy.getListener(), SipSessionGroup.this.mSipHelper.getServerTransaction(event), 3);
                    newSession.mProxy.onCallTransferring(newSession, newSession.mPeerSessionDescription);
                } else {
                    SipSessionGroup.this.mSipHelper.sendResponse(event, response);
                }
            } else {
                newSession = SipSessionGroup.this.createNewSession(event, this.mProxy, SipSessionGroup.this.mSipHelper.sendRinging(event, generateTag()), 3);
                this.mProxy.onRinging(newSession, newSession.mPeerProfile, newSession.mPeerSessionDescription);
            }
            if (newSession != null) {
                SipSessionGroup.this.addSipSession(newSession);
            }
        }

        @Override // com.android.server.sip.SipSessionGroup.SipSessionImpl
        public boolean process(EventObject evt) throws SipException {
            if (SipSessionGroup.isLoggable(this, evt)) {
                log("process: " + this + ": " + SipSession.State.toString(this.mState) + ": processing " + SipSessionGroup.logEvt(evt));
            }
            if (SipSessionGroup.isRequestEvent("INVITE", evt)) {
                processNewInviteRequest((RequestEvent) evt);
                return SSCRI_DBG;
            } else if (SipSessionGroup.isRequestEvent("OPTIONS", evt)) {
                SipSessionGroup.this.mSipHelper.sendResponse((RequestEvent) evt, Response.f51OK);
                return SSCRI_DBG;
            } else {
                return false;
            }
        }

        private void log(String s) {
            Rlog.d(SSCRI_TAG, s);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class SipSessionImpl extends ISipSession.Stub {
        private static final boolean SSI_DBG = true;
        private static final String SSI_TAG = "SipSessionImpl";
        int mAuthenticationRetryCount;
        ClientTransaction mClientTransaction;
        Dialog mDialog;
        boolean mInCall;
        RequestEvent mInviteReceived;
        SipProfile mPeerProfile;
        String mPeerSessionDescription;
        SipSessionImpl mReferSession;
        ReferredByHeader mReferredBy;
        String mReplaces;
        ServerTransaction mServerTransaction;
        SessionTimer mSessionTimer;
        private SipKeepAlive mSipKeepAlive;
        private SipSessionImpl mSipSessionImpl;
        SipSessionListenerProxy mProxy = new SipSessionListenerProxy();
        int mState = 0;

        /* renamed from: -$$Nest$monError  reason: not valid java name */
        static /* bridge */ /* synthetic */ void m84$$Nest$monError(SipSessionImpl sipSessionImpl, Throwable th) {
            sipSessionImpl.onError(th);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public class SessionTimer {
            private boolean mRunning = SipSessionImpl.SSI_DBG;

            SessionTimer() {
            }

            void start(final int timeout) {
                new Thread(new Runnable() { // from class: com.android.server.sip.SipSessionGroup.SipSessionImpl.SessionTimer.1
                    @Override // java.lang.Runnable
                    public void run() {
                        SessionTimer.this.sleep(timeout);
                        if (SessionTimer.this.mRunning) {
                            SessionTimer.this.timeout();
                        }
                    }
                }, "SipSessionTimerThread").start();
            }

            synchronized void cancel() {
                this.mRunning = false;
                notify();
            }

            /* JADX INFO: Access modifiers changed from: private */
            public void timeout() {
                synchronized (SipSessionGroup.this) {
                    SipSessionImpl.this.onError(-5, "Session timed out!");
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public synchronized void sleep(int timeout) {
                try {
                    wait(timeout * 1000);
                } catch (InterruptedException e) {
                    SipSessionGroup.this.loge("session timer interrupted!", e);
                }
            }
        }

        public SipSessionImpl(ISipSessionListener listener) {
            setListener(listener);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public SipSessionImpl duplicate() {
            return new SipSessionImpl(this.mProxy.getListener());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void reset() {
            this.mInCall = false;
            SipSessionGroup.this.removeSipSession(this);
            this.mPeerProfile = null;
            this.mState = 0;
            this.mInviteReceived = null;
            this.mPeerSessionDescription = null;
            this.mAuthenticationRetryCount = 0;
            this.mReferSession = null;
            this.mReferredBy = null;
            this.mReplaces = null;
            Dialog dialog = this.mDialog;
            if (dialog != null) {
                dialog.delete();
            }
            this.mDialog = null;
            try {
                ServerTransaction serverTransaction = this.mServerTransaction;
                if (serverTransaction != null) {
                    serverTransaction.terminate();
                }
            } catch (ObjectInUseException e) {
            }
            this.mServerTransaction = null;
            try {
                ClientTransaction clientTransaction = this.mClientTransaction;
                if (clientTransaction != null) {
                    clientTransaction.terminate();
                }
            } catch (ObjectInUseException e2) {
            }
            this.mClientTransaction = null;
            cancelSessionTimer();
            SipSessionImpl sipSessionImpl = this.mSipSessionImpl;
            if (sipSessionImpl != null) {
                sipSessionImpl.stopKeepAliveProcess();
                this.mSipSessionImpl = null;
            }
        }

        @Override // android.net.sip.ISipSession
        public boolean isInCall() {
            return this.mInCall;
        }

        @Override // android.net.sip.ISipSession
        public String getLocalIp() {
            return SipSessionGroup.this.mLocalIp;
        }

        @Override // android.net.sip.ISipSession
        public SipProfile getLocalProfile() {
            return SipSessionGroup.this.mLocalProfile;
        }

        @Override // android.net.sip.ISipSession
        public SipProfile getPeerProfile() {
            return this.mPeerProfile;
        }

        @Override // android.net.sip.ISipSession
        public String getCallId() {
            return SipHelper.getCallId(getTransaction());
        }

        private Transaction getTransaction() {
            ClientTransaction clientTransaction = this.mClientTransaction;
            if (clientTransaction != null) {
                return clientTransaction;
            }
            ServerTransaction serverTransaction = this.mServerTransaction;
            if (serverTransaction != null) {
                return serverTransaction;
            }
            return null;
        }

        @Override // android.net.sip.ISipSession
        public int getState() {
            return this.mState;
        }

        @Override // android.net.sip.ISipSession
        public void setListener(ISipSessionListener listener) {
            ISipSessionListener iSipSessionListener;
            SipSessionListenerProxy sipSessionListenerProxy = this.mProxy;
            if (listener instanceof SipSessionListenerProxy) {
                iSipSessionListener = ((SipSessionListenerProxy) listener).getListener();
            } else {
                iSipSessionListener = listener;
            }
            sipSessionListenerProxy.setListener(iSipSessionListener);
        }

        private void doCommandAsync(final EventObject command) {
            new Thread(new Runnable() { // from class: com.android.server.sip.SipSessionGroup.SipSessionImpl.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        SipSessionImpl.this.processCommand(command);
                    } catch (Throwable e) {
                        SipSessionGroup.this.loge("command error: " + command + ": " + SipSessionGroup.this.mLocalProfile.getUriString(), SipSessionGroup.this.getRootCause(e));
                        SipSessionImpl.this.onError(e);
                    }
                }
            }, "SipSessionAsyncCmdThread").start();
        }

        @Override // android.net.sip.ISipSession
        public void makeCall(SipProfile peerProfile, String sessionDescription, int timeout) {
            doCommandAsync(new MakeCallCommand(peerProfile, sessionDescription, timeout));
        }

        @Override // android.net.sip.ISipSession
        public void answerCall(String sessionDescription, int timeout) {
            synchronized (SipSessionGroup.this) {
                if (this.mPeerProfile == null) {
                    return;
                }
                doCommandAsync(new MakeCallCommand(this.mPeerProfile, sessionDescription, timeout));
            }
        }

        @Override // android.net.sip.ISipSession
        public void endCall() {
            doCommandAsync(SipSessionGroup.END_CALL);
        }

        @Override // android.net.sip.ISipSession
        public void changeCall(String sessionDescription, int timeout) {
            synchronized (SipSessionGroup.this) {
                if (this.mPeerProfile == null) {
                    return;
                }
                doCommandAsync(new MakeCallCommand(this.mPeerProfile, sessionDescription, timeout));
            }
        }

        @Override // android.net.sip.ISipSession
        public void register(int duration) {
            doCommandAsync(new RegisterCommand(duration));
        }

        @Override // android.net.sip.ISipSession
        public void unregister() {
            doCommandAsync(SipSessionGroup.DEREGISTER);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void processCommand(EventObject command) throws SipException {
            if (SipSessionGroup.isLoggable(command)) {
                log("process cmd: " + command);
            }
            if (!process(command)) {
                onError(-9, "cannot initiate a new transaction to execute: " + command);
            }
        }

        protected String generateTag() {
            return String.valueOf((long) (Math.random() * 4.294967296E9d));
        }

        public String toString() {
            try {
                String s = super.toString();
                return s.substring(s.indexOf(Separators.f10AT)) + Separators.COLON + SipSession.State.toString(this.mState);
            } catch (Throwable th) {
                return super.toString();
            }
        }

        public boolean process(EventObject evt) throws SipException {
            boolean processed;
            if (SipSessionGroup.isLoggable(this, evt)) {
                log(" ~~~~~   " + this + ": " + SipSession.State.toString(this.mState) + ": processing " + SipSessionGroup.logEvt(evt));
            }
            synchronized (SipSessionGroup.this) {
                boolean z = false;
                if (SipSessionGroup.this.isClosed()) {
                    return false;
                }
                SipKeepAlive sipKeepAlive = this.mSipKeepAlive;
                if (sipKeepAlive == null || !sipKeepAlive.process(evt)) {
                    Dialog dialog = null;
                    if (evt instanceof RequestEvent) {
                        dialog = ((RequestEvent) evt).getDialog();
                    } else if (evt instanceof ResponseEvent) {
                        dialog = ((ResponseEvent) evt).getDialog();
                        SipSessionGroup.this.extractExternalAddress((ResponseEvent) evt);
                    }
                    if (dialog != null) {
                        this.mDialog = dialog;
                    }
                    switch (this.mState) {
                        case 0:
                            processed = readyForCall(evt);
                            break;
                        case 1:
                        case 2:
                            processed = registeringToReady(evt);
                            break;
                        case 3:
                            processed = incomingCall(evt);
                            break;
                        case 4:
                            processed = incomingCallToInCall(evt);
                            break;
                        case 5:
                        case SipSession.State.OUTGOING_CALL_RING_BACK /* 6 */:
                            processed = outgoingCall(evt);
                            break;
                        case SipSession.State.OUTGOING_CALL_CANCELING /* 7 */:
                            processed = outgoingCallToReady(evt);
                            break;
                        case 8:
                            processed = inCall(evt);
                            break;
                        case 9:
                        default:
                            processed = false;
                            break;
                        case 10:
                            processed = endingCall(evt);
                            break;
                    }
                    if (processed || processExceptions(evt)) {
                        z = true;
                    }
                    return z;
                }
                return SSI_DBG;
            }
        }

        private boolean processExceptions(EventObject evt) throws SipException {
            if (SipSessionGroup.isRequestEvent("BYE", evt)) {
                SipSessionGroup.this.mSipHelper.sendResponse((RequestEvent) evt, Response.f51OK);
                endCallNormally();
                return SSI_DBG;
            } else if (SipSessionGroup.isRequestEvent(Request.CANCEL, evt)) {
                SipSessionGroup.this.mSipHelper.sendResponse((RequestEvent) evt, Response.CALL_OR_TRANSACTION_DOES_NOT_EXIST);
                return SSI_DBG;
            } else if (evt instanceof TransactionTerminatedEvent) {
                if (isCurrentTransaction((TransactionTerminatedEvent) evt)) {
                    if (evt instanceof TimeoutEvent) {
                        processTimeout((TimeoutEvent) evt);
                    } else {
                        processTransactionTerminated((TransactionTerminatedEvent) evt);
                    }
                    return SSI_DBG;
                }
                return false;
            } else if (SipSessionGroup.isRequestEvent("OPTIONS", evt)) {
                SipSessionGroup.this.mSipHelper.sendResponse((RequestEvent) evt, Response.f51OK);
                return SSI_DBG;
            } else if (evt instanceof DialogTerminatedEvent) {
                processDialogTerminated((DialogTerminatedEvent) evt);
                return SSI_DBG;
            } else {
                return false;
            }
        }

        private void processDialogTerminated(DialogTerminatedEvent event) {
            if (this.mDialog == event.getDialog()) {
                onError(new SipException("dialog terminated"));
            } else {
                log("not the current dialog; current=" + this.mDialog + ", terminated=" + event.getDialog());
            }
        }

        private boolean isCurrentTransaction(TransactionTerminatedEvent event) {
            Transaction current;
            Transaction target;
            if (event.isServerTransaction()) {
                current = this.mServerTransaction;
            } else {
                current = this.mClientTransaction;
            }
            if (event.isServerTransaction()) {
                target = event.getServerTransaction();
            } else {
                target = event.getClientTransaction();
            }
            if (current != target && this.mState != 9) {
                log("not the current transaction; current=" + toString(current) + ", target=" + toString(target));
                return false;
            } else if (current == null) {
                return SSI_DBG;
            } else {
                log("transaction terminated: " + toString(current));
                return SSI_DBG;
            }
        }

        private String toString(Transaction transaction) {
            if (transaction == null) {
                return "null";
            }
            Request request = transaction.getRequest();
            Dialog dialog = transaction.getDialog();
            CSeqHeader cseq = (CSeqHeader) request.getHeader("CSeq");
            Object[] objArr = new Object[4];
            objArr[0] = request.getMethod();
            objArr[1] = Long.valueOf(cseq.getSeqNumber());
            objArr[2] = transaction.getState();
            objArr[3] = dialog == null ? "-" : dialog.getState();
            return String.format("req=%s,%s,s=%s,ds=%s,", objArr);
        }

        private void processTransactionTerminated(TransactionTerminatedEvent event) {
            switch (this.mState) {
                case 0:
                case 8:
                    log("Transaction terminated; do nothing");
                    return;
                default:
                    log("Transaction terminated early: " + this);
                    onError(-3, "transaction terminated");
                    return;
            }
        }

        private void processTimeout(TimeoutEvent event) {
            log("processing Timeout...");
            switch (this.mState) {
                case 1:
                case 2:
                    reset();
                    this.mProxy.onRegistrationTimeout(this);
                    return;
                case 3:
                case 4:
                case 5:
                case SipSession.State.OUTGOING_CALL_CANCELING /* 7 */:
                    onError(-5, event.toString());
                    return;
                case SipSession.State.OUTGOING_CALL_RING_BACK /* 6 */:
                default:
                    log("   do nothing");
                    return;
            }
        }

        private int getExpiryTime(Response response) {
            int time = -1;
            ContactHeader contact = (ContactHeader) response.getHeader("Contact");
            if (contact != null) {
                time = contact.getExpires();
            }
            ExpiresHeader expires = (ExpiresHeader) response.getHeader("Expires");
            if (expires != null && (time < 0 || time > expires.getExpires())) {
                time = expires.getExpires();
            }
            if (time <= 0) {
                time = SipSessionGroup.EXPIRY_TIME;
            }
            ExpiresHeader expires2 = (ExpiresHeader) response.getHeader("Min-Expires");
            if (expires2 != null && time < expires2.getExpires()) {
                time = expires2.getExpires();
            }
            log("Expiry time = " + time);
            return time;
        }

        private boolean registeringToReady(EventObject evt) throws SipException {
            int i;
            if (SipSessionGroup.expectResponse("REGISTER", evt)) {
                ResponseEvent event = (ResponseEvent) evt;
                Response response = event.getResponse();
                int statusCode = response.getStatusCode();
                switch (statusCode) {
                    case Response.f51OK /* 200 */:
                        int state = this.mState;
                        if (state == 1) {
                            i = getExpiryTime(((ResponseEvent) evt).getResponse());
                        } else {
                            i = -1;
                        }
                        onRegistrationDone(i);
                        return SSI_DBG;
                    case Response.UNAUTHORIZED /* 401 */:
                    case Response.PROXY_AUTHENTICATION_REQUIRED /* 407 */:
                        handleAuthentication(event);
                        return SSI_DBG;
                    default:
                        if (statusCode >= 500) {
                            onRegistrationFailed(response);
                            return SSI_DBG;
                        }
                        return false;
                }
            }
            return false;
        }

        private boolean handleAuthentication(ResponseEvent event) throws SipException {
            Response response = event.getResponse();
            String nonce = getNonceFromResponse(response);
            if (nonce == null) {
                onError(-2, "server does not provide challenge");
                return false;
            } else if (this.mAuthenticationRetryCount < 2) {
                ClientTransaction handleChallenge = SipSessionGroup.this.mSipHelper.handleChallenge(event, getAccountManager());
                this.mClientTransaction = handleChallenge;
                this.mDialog = handleChallenge.getDialog();
                this.mAuthenticationRetryCount++;
                if (SipSessionGroup.isLoggable(this, event)) {
                    log("   authentication retry count=" + this.mAuthenticationRetryCount);
                }
                return SSI_DBG;
            } else {
                if (crossDomainAuthenticationRequired(response)) {
                    onError(-11, getRealmFromResponse(response));
                } else {
                    onError(-8, "incorrect username or password");
                }
                return false;
            }
        }

        private boolean crossDomainAuthenticationRequired(Response response) {
            String realm = getRealmFromResponse(response);
            if (realm == null) {
                realm = "";
            }
            return SipSessionGroup.this.mLocalProfile.getSipDomain().trim().equals(realm.trim()) ^ SSI_DBG;
        }

        private AccountManager getAccountManager() {
            return new AccountManager() { // from class: com.android.server.sip.SipSessionGroup.SipSessionImpl.2
                @Override // gov.nist.javax.sip.clientauthutils.AccountManager
                public UserCredentials getCredentials(ClientTransaction challengedTransaction, String realm) {
                    return new UserCredentials() { // from class: com.android.server.sip.SipSessionGroup.SipSessionImpl.2.1
                        @Override // gov.nist.javax.sip.clientauthutils.UserCredentials
                        public String getUserName() {
                            String username = SipSessionGroup.this.mLocalProfile.getAuthUserName();
                            return !TextUtils.isEmpty(username) ? username : SipSessionGroup.this.mLocalProfile.getUserName();
                        }

                        @Override // gov.nist.javax.sip.clientauthutils.UserCredentials
                        public String getPassword() {
                            return SipSessionGroup.this.mPassword;
                        }

                        @Override // gov.nist.javax.sip.clientauthutils.UserCredentials
                        public String getSipDomain() {
                            return SipSessionGroup.this.mLocalProfile.getSipDomain();
                        }
                    };
                }
            };
        }

        private String getRealmFromResponse(Response response) {
            WWWAuthenticate wwwAuth = (WWWAuthenticate) response.getHeader("WWW-Authenticate");
            if (wwwAuth != null) {
                return wwwAuth.getRealm();
            }
            ProxyAuthenticate proxyAuth = (ProxyAuthenticate) response.getHeader("Proxy-Authenticate");
            if (proxyAuth == null) {
                return null;
            }
            return proxyAuth.getRealm();
        }

        private String getNonceFromResponse(Response response) {
            WWWAuthenticate wwwAuth = (WWWAuthenticate) response.getHeader("WWW-Authenticate");
            if (wwwAuth != null) {
                return wwwAuth.getNonce();
            }
            ProxyAuthenticate proxyAuth = (ProxyAuthenticate) response.getHeader("Proxy-Authenticate");
            if (proxyAuth == null) {
                return null;
            }
            return proxyAuth.getNonce();
        }

        private String getResponseString(int statusCode) {
            StatusLine statusLine = new StatusLine();
            statusLine.setStatusCode(statusCode);
            statusLine.setReasonPhrase(SIPResponse.getReasonPhrase(statusCode));
            return statusLine.encode();
        }

        private boolean readyForCall(EventObject evt) throws SipException {
            if (evt instanceof MakeCallCommand) {
                this.mState = 5;
                MakeCallCommand cmd = (MakeCallCommand) evt;
                this.mPeerProfile = cmd.getPeerProfile();
                if (this.mReferSession != null) {
                    SipSessionGroup.this.mSipHelper.sendReferNotify(this.mReferSession.mDialog, getResponseString(100));
                }
                ClientTransaction sendInvite = SipSessionGroup.this.mSipHelper.sendInvite(SipSessionGroup.this.mLocalProfile, this.mPeerProfile, cmd.getSessionDescription(), generateTag(), this.mReferredBy, this.mReplaces);
                this.mClientTransaction = sendInvite;
                this.mDialog = sendInvite.getDialog();
                SipSessionGroup.this.addSipSession(this);
                startSessionTimer(cmd.getTimeout());
                this.mProxy.onCalling(this);
                return SSI_DBG;
            } else if (evt instanceof RegisterCommand) {
                this.mState = 1;
                int duration = ((RegisterCommand) evt).getDuration();
                ClientTransaction sendRegister = SipSessionGroup.this.mSipHelper.sendRegister(SipSessionGroup.this.mLocalProfile, generateTag(), duration);
                this.mClientTransaction = sendRegister;
                this.mDialog = sendRegister.getDialog();
                SipSessionGroup.this.addSipSession(this);
                this.mProxy.onRegistering(this);
                return SSI_DBG;
            } else if (SipSessionGroup.DEREGISTER == evt) {
                this.mState = 2;
                ClientTransaction sendRegister2 = SipSessionGroup.this.mSipHelper.sendRegister(SipSessionGroup.this.mLocalProfile, generateTag(), 0);
                this.mClientTransaction = sendRegister2;
                this.mDialog = sendRegister2.getDialog();
                SipSessionGroup.this.addSipSession(this);
                this.mProxy.onRegistering(this);
                return SSI_DBG;
            } else {
                return false;
            }
        }

        private boolean incomingCall(EventObject evt) throws SipException {
            if (evt instanceof MakeCallCommand) {
                this.mState = 4;
                this.mServerTransaction = SipSessionGroup.this.mSipHelper.sendInviteOk(this.mInviteReceived, SipSessionGroup.this.mLocalProfile, ((MakeCallCommand) evt).getSessionDescription(), this.mServerTransaction, SipSessionGroup.this.mExternalIp, SipSessionGroup.this.mExternalPort);
                startSessionTimer(((MakeCallCommand) evt).getTimeout());
                return SSI_DBG;
            } else if (SipSessionGroup.END_CALL == evt) {
                SipSessionGroup.this.mSipHelper.sendInviteBusyHere(this.mInviteReceived, this.mServerTransaction);
                endCallNormally();
                return SSI_DBG;
            } else if (SipSessionGroup.isRequestEvent(Request.CANCEL, evt)) {
                RequestEvent event = (RequestEvent) evt;
                SipSessionGroup.this.mSipHelper.sendResponse(event, Response.f51OK);
                SipSessionGroup.this.mSipHelper.sendInviteRequestTerminated(this.mInviteReceived.getRequest(), this.mServerTransaction);
                endCallNormally();
                return SSI_DBG;
            } else {
                return false;
            }
        }

        private boolean incomingCallToInCall(EventObject evt) {
            if (SipSessionGroup.isRequestEvent("ACK", evt)) {
                String sdp = SipSessionGroup.this.extractContent(((RequestEvent) evt).getRequest());
                if (sdp != null) {
                    this.mPeerSessionDescription = sdp;
                }
                if (this.mPeerSessionDescription == null) {
                    onError(-4, "peer sdp is empty");
                } else {
                    establishCall(false);
                }
                return SSI_DBG;
            } else if (SipSessionGroup.isRequestEvent(Request.CANCEL, evt)) {
                return SSI_DBG;
            } else {
                return false;
            }
        }

        private boolean outgoingCall(EventObject evt) throws SipException {
            if (SipSessionGroup.expectResponse("INVITE", evt)) {
                ResponseEvent event = (ResponseEvent) evt;
                Response response = event.getResponse();
                int statusCode = response.getStatusCode();
                switch (statusCode) {
                    case Response.RINGING /* 180 */:
                    case Response.CALL_IS_BEING_FORWARDED /* 181 */:
                    case Response.QUEUED /* 182 */:
                    case Response.SESSION_PROGRESS /* 183 */:
                        if (this.mState == 5) {
                            this.mState = 6;
                            cancelSessionTimer();
                            this.mProxy.onRingingBack(this);
                        }
                        return SSI_DBG;
                    case Response.f51OK /* 200 */:
                        if (this.mReferSession != null) {
                            SipSessionGroup.this.mSipHelper.sendReferNotify(this.mReferSession.mDialog, getResponseString(Response.f51OK));
                            this.mReferSession = null;
                        }
                        SipSessionGroup.this.mSipHelper.sendInviteAck(event, this.mDialog);
                        this.mPeerSessionDescription = SipSessionGroup.this.extractContent(response);
                        establishCall(SSI_DBG);
                        return SSI_DBG;
                    case Response.UNAUTHORIZED /* 401 */:
                    case Response.PROXY_AUTHENTICATION_REQUIRED /* 407 */:
                        if (handleAuthentication(event)) {
                            SipSessionGroup.this.addSipSession(this);
                        }
                        return SSI_DBG;
                    case Response.REQUEST_PENDING /* 491 */:
                        return SSI_DBG;
                    default:
                        if (this.mReferSession != null) {
                            SipSessionGroup.this.mSipHelper.sendReferNotify(this.mReferSession.mDialog, getResponseString(Response.SERVICE_UNAVAILABLE));
                        }
                        if (statusCode >= 400) {
                            onError(response);
                            return SSI_DBG;
                        } else if (statusCode >= 300) {
                            return false;
                        } else {
                            return SSI_DBG;
                        }
                }
            } else if (SipSessionGroup.END_CALL == evt) {
                this.mState = 7;
                SipSessionGroup.this.mSipHelper.sendCancel(this.mClientTransaction);
                startSessionTimer(3);
                return SSI_DBG;
            } else if (SipSessionGroup.isRequestEvent("INVITE", evt)) {
                RequestEvent event2 = (RequestEvent) evt;
                SipSessionGroup.this.mSipHelper.sendInviteBusyHere(event2, event2.getServerTransaction());
                return SSI_DBG;
            } else {
                return false;
            }
        }

        private boolean outgoingCallToReady(EventObject evt) throws SipException {
            if (evt instanceof ResponseEvent) {
                ResponseEvent event = (ResponseEvent) evt;
                Response response = event.getResponse();
                int statusCode = response.getStatusCode();
                if (SipSessionGroup.expectResponse(Request.CANCEL, evt)) {
                    if (statusCode == 200) {
                        return SSI_DBG;
                    }
                } else if (SipSessionGroup.expectResponse("INVITE", evt)) {
                    switch (statusCode) {
                        case Response.f51OK /* 200 */:
                            outgoingCall(evt);
                            return SSI_DBG;
                        case Response.REQUEST_TERMINATED /* 487 */:
                            endCallNormally();
                            return SSI_DBG;
                    }
                } else {
                    return false;
                }
                if (statusCode >= 400) {
                    onError(response);
                    return SSI_DBG;
                }
            } else if (evt instanceof TransactionTerminatedEvent) {
                onError(new SipException("timed out"));
            }
            return false;
        }

        private boolean processReferRequest(RequestEvent event) throws SipException {
            try {
                ReferToHeader referto = (ReferToHeader) event.getRequest().getHeader(ReferToHeader.NAME);
                Address address = referto.getAddress();
                SipURI uri = (SipURI) address.getURI();
                String replacesHeader = uri.getHeader("Replaces");
                String username = uri.getUser();
                if (username == null) {
                    SipSessionGroup.this.mSipHelper.sendResponse(event, Response.BAD_REQUEST);
                    return false;
                }
                SipSessionGroup.this.mSipHelper.sendResponse(event, Response.ACCEPTED);
                SipSessionImpl newSession = SipSessionGroup.this.createNewSession(event, this.mProxy.getListener(), SipSessionGroup.this.mSipHelper.getServerTransaction(event), 0);
                newSession.mReferSession = this;
                newSession.mReferredBy = (ReferredByHeader) event.getRequest().getHeader("Referred-By");
                newSession.mReplaces = replacesHeader;
                newSession.mPeerProfile = SipSessionGroup.createPeerProfile(referto);
                newSession.mProxy.onCallTransferring(newSession, null);
                return SSI_DBG;
            } catch (IllegalArgumentException e) {
                throw new SipException("createPeerProfile()", e);
            }
        }

        private boolean inCall(EventObject evt) throws SipException {
            if (SipSessionGroup.END_CALL == evt) {
                this.mState = 10;
                SipSessionGroup.this.mSipHelper.sendBye(this.mDialog);
                this.mProxy.onCallEnded(this);
                startSessionTimer(3);
                return SSI_DBG;
            } else if (SipSessionGroup.isRequestEvent("INVITE", evt)) {
                this.mState = 3;
                RequestEvent event = (RequestEvent) evt;
                this.mInviteReceived = event;
                String extractContent = SipSessionGroup.this.extractContent(event.getRequest());
                this.mPeerSessionDescription = extractContent;
                this.mServerTransaction = null;
                this.mProxy.onRinging(this, this.mPeerProfile, extractContent);
                return SSI_DBG;
            } else if (SipSessionGroup.isRequestEvent("BYE", evt)) {
                SipSessionGroup.this.mSipHelper.sendResponse((RequestEvent) evt, Response.f51OK);
                endCallNormally();
                return SSI_DBG;
            } else if (SipSessionGroup.isRequestEvent(Request.REFER, evt)) {
                return processReferRequest((RequestEvent) evt);
            } else {
                if (evt instanceof MakeCallCommand) {
                    this.mState = 5;
                    this.mClientTransaction = SipSessionGroup.this.mSipHelper.sendReinvite(this.mDialog, ((MakeCallCommand) evt).getSessionDescription());
                    startSessionTimer(((MakeCallCommand) evt).getTimeout());
                    return SSI_DBG;
                } else if ((evt instanceof ResponseEvent) && SipSessionGroup.expectResponse("NOTIFY", evt)) {
                    return SSI_DBG;
                } else {
                    return false;
                }
            }
        }

        private boolean endingCall(EventObject evt) throws SipException {
            if (SipSessionGroup.expectResponse("BYE", evt)) {
                ResponseEvent event = (ResponseEvent) evt;
                Response response = event.getResponse();
                int statusCode = response.getStatusCode();
                switch (statusCode) {
                    case Response.UNAUTHORIZED /* 401 */:
                    case Response.PROXY_AUTHENTICATION_REQUIRED /* 407 */:
                        if (handleAuthentication(event)) {
                            return SSI_DBG;
                        }
                        break;
                }
                cancelSessionTimer();
                reset();
                return SSI_DBG;
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void startSessionTimer(int timeout) {
            if (timeout > 0) {
                SessionTimer sessionTimer = new SessionTimer();
                this.mSessionTimer = sessionTimer;
                sessionTimer.start(timeout);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void cancelSessionTimer() {
            SessionTimer sessionTimer = this.mSessionTimer;
            if (sessionTimer != null) {
                sessionTimer.cancel();
                this.mSessionTimer = null;
            }
        }

        private String createErrorMessage(Response response) {
            return String.format("%s (%d)", response.getReasonPhrase(), Integer.valueOf(response.getStatusCode()));
        }

        private void enableKeepAlive() {
            SipSessionImpl sipSessionImpl = this.mSipSessionImpl;
            if (sipSessionImpl != null) {
                sipSessionImpl.stopKeepAliveProcess();
            } else {
                this.mSipSessionImpl = duplicate();
            }
            try {
                this.mSipSessionImpl.startKeepAliveProcess(10, this.mPeerProfile, null);
            } catch (SipException e) {
                SipSessionGroup.this.loge("keepalive cannot be enabled; ignored", e);
                this.mSipSessionImpl.stopKeepAliveProcess();
            }
        }

        private void establishCall(boolean enableKeepAlive) {
            this.mState = 8;
            cancelSessionTimer();
            if (!this.mInCall && enableKeepAlive) {
                enableKeepAlive();
            }
            this.mInCall = SSI_DBG;
            this.mProxy.onCallEstablished(this, this.mPeerSessionDescription);
        }

        private void endCallNormally() {
            reset();
            this.mProxy.onCallEnded(this);
        }

        private void endCallOnError(int errorCode, String message) {
            reset();
            this.mProxy.onError(this, errorCode, message);
        }

        private void endCallOnBusy() {
            reset();
            this.mProxy.onCallBusy(this);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onError(int errorCode, String message) {
            cancelSessionTimer();
            switch (this.mState) {
                case 1:
                case 2:
                    onRegistrationFailed(errorCode, message);
                    return;
                default:
                    endCallOnError(errorCode, message);
                    return;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onError(Throwable exception) {
            Throwable exception2 = SipSessionGroup.this.getRootCause(exception);
            onError(getErrorCode(exception2), exception2.toString());
        }

        private void onError(Response response) {
            int statusCode = response.getStatusCode();
            if (!this.mInCall && statusCode == 486) {
                endCallOnBusy();
            } else {
                onError(getErrorCode(statusCode), createErrorMessage(response));
            }
        }

        private int getErrorCode(int responseStatusCode) {
            switch (responseStatusCode) {
                case Response.FORBIDDEN /* 403 */:
                case Response.NOT_FOUND /* 404 */:
                case Response.NOT_ACCEPTABLE /* 406 */:
                case Response.GONE /* 410 */:
                case Response.TEMPORARILY_UNAVAILABLE /* 480 */:
                case Response.NOT_ACCEPTABLE_HERE /* 488 */:
                    return -7;
                case Response.REQUEST_TIMEOUT /* 408 */:
                    return -5;
                case Response.REQUEST_URI_TOO_LONG /* 414 */:
                case Response.ADDRESS_INCOMPLETE /* 484 */:
                case Response.AMBIGUOUS /* 485 */:
                    return -6;
                default:
                    if (responseStatusCode < 500) {
                        return -4;
                    }
                    return -2;
            }
        }

        private int getErrorCode(Throwable exception) {
            exception.getMessage();
            if (exception instanceof UnknownHostException) {
                return -12;
            }
            if (exception instanceof IOException) {
                return -1;
            }
            return -4;
        }

        private void onRegistrationDone(int duration) {
            reset();
            this.mProxy.onRegistrationDone(this, duration);
        }

        private void onRegistrationFailed(int errorCode, String message) {
            reset();
            this.mProxy.onRegistrationFailed(this, errorCode, message);
        }

        private void onRegistrationFailed(Response response) {
            int statusCode = response.getStatusCode();
            onRegistrationFailed(getErrorCode(statusCode), createErrorMessage(response));
        }

        public void startKeepAliveProcess(int interval, KeepAliveProcessCallback callback) throws SipException {
            synchronized (SipSessionGroup.this) {
                startKeepAliveProcess(interval, SipSessionGroup.this.mLocalProfile, callback);
            }
        }

        public void startKeepAliveProcess(int interval, SipProfile peerProfile, KeepAliveProcessCallback callback) throws SipException {
            synchronized (SipSessionGroup.this) {
                if (this.mSipKeepAlive != null) {
                    throw new SipException("Cannot create more than one keepalive process in a SipSession");
                }
                this.mPeerProfile = peerProfile;
                SipKeepAlive sipKeepAlive = new SipKeepAlive();
                this.mSipKeepAlive = sipKeepAlive;
                this.mProxy.setListener(sipKeepAlive);
                this.mSipKeepAlive.start(interval, callback);
            }
        }

        public void stopKeepAliveProcess() {
            synchronized (SipSessionGroup.this) {
                SipKeepAlive sipKeepAlive = this.mSipKeepAlive;
                if (sipKeepAlive != null) {
                    sipKeepAlive.stop();
                    this.mSipKeepAlive = null;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public class SipKeepAlive extends SipSessionAdapter implements Runnable {
            private static final boolean SKA_DBG = true;
            private static final String SKA_TAG = "SipKeepAlive";
            private KeepAliveProcessCallback mCallback;
            private int mInterval;
            private boolean mRunning = false;
            private boolean mPortChanged = false;
            private int mRPort = 0;

            SipKeepAlive() {
            }

            void start(int interval, KeepAliveProcessCallback callback) {
                if (this.mRunning) {
                    return;
                }
                this.mRunning = SKA_DBG;
                this.mInterval = interval;
                this.mCallback = new KeepAliveProcessCallbackProxy(callback);
                SipSessionGroup.this.mWakeupTimer.set(interval * 1000, this);
                log("start keepalive:" + SipSessionGroup.this.mLocalProfile.getUriString());
                run();
            }

            boolean process(EventObject evt) {
                if (this.mRunning && SipSessionImpl.this.mState == 9 && (evt instanceof ResponseEvent) && parseOptionsResult(evt)) {
                    if (this.mPortChanged) {
                        SipSessionGroup.this.resetExternalAddress();
                        stop();
                    } else {
                        SipSessionImpl.this.cancelSessionTimer();
                        SipSessionGroup.this.removeSipSession(SipSessionImpl.this);
                    }
                    this.mCallback.onResponse(this.mPortChanged);
                    return SKA_DBG;
                }
                return false;
            }

            @Override // android.net.sip.SipSessionAdapter, android.net.sip.ISipSessionListener
            public void onError(ISipSession session, int errorCode, String message) {
                stop();
                this.mCallback.onError(errorCode, message);
            }

            @Override // java.lang.Runnable
            public void run() {
                synchronized (SipSessionGroup.this) {
                    if (this.mRunning) {
                        sendKeepAlive();
                    }
                }
            }

            void stop() {
                synchronized (SipSessionGroup.this) {
                    log("stop keepalive:" + SipSessionGroup.this.mLocalProfile.getUriString() + ",RPort=" + this.mRPort);
                    this.mRunning = false;
                    SipSessionGroup.this.mWakeupTimer.cancel(this);
                    SipSessionImpl.this.reset();
                }
            }

            private void sendKeepAlive() throws SipException {
                synchronized (SipSessionGroup.this) {
                    SipSessionImpl.this.mState = 9;
                    SipSessionImpl sipSessionImpl = SipSessionImpl.this;
                    sipSessionImpl.mClientTransaction = SipSessionGroup.this.mSipHelper.sendOptions(SipSessionGroup.this.mLocalProfile, SipSessionImpl.this.mPeerProfile, SipSessionImpl.this.generateTag());
                    SipSessionImpl sipSessionImpl2 = SipSessionImpl.this;
                    sipSessionImpl2.mDialog = sipSessionImpl2.mClientTransaction.getDialog();
                    SipSessionGroup.this.addSipSession(SipSessionImpl.this);
                    SipSessionImpl.this.startSessionTimer(5);
                }
            }

            private boolean parseOptionsResult(EventObject evt) {
                if (SipSessionGroup.expectResponse("OPTIONS", evt)) {
                    ResponseEvent event = (ResponseEvent) evt;
                    int rPort = getRPortFromResponse(event.getResponse());
                    if (rPort != -1) {
                        if (this.mRPort == 0) {
                            this.mRPort = rPort;
                        }
                        int i = this.mRPort;
                        if (i != rPort) {
                            this.mPortChanged = SKA_DBG;
                            log(String.format("rport is changed: %d <> %d", Integer.valueOf(i), Integer.valueOf(rPort)));
                            this.mRPort = rPort;
                        } else {
                            log("rport is the same: " + rPort);
                        }
                    } else {
                        log("peer did not respond rport");
                    }
                    return SKA_DBG;
                }
                return false;
            }

            private int getRPortFromResponse(Response response) {
                ViaHeader viaHeader = (ViaHeader) response.getHeader("Via");
                if (viaHeader == null) {
                    return -1;
                }
                return viaHeader.getRPort();
            }

            private void log(String s) {
                Rlog.d(SKA_TAG, s);
            }
        }

        private void log(String s) {
            Rlog.d(SSI_TAG, s);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isRequestEvent(String method, EventObject event) {
        try {
            if (event instanceof RequestEvent) {
                RequestEvent requestEvent = (RequestEvent) event;
                return method.equals(requestEvent.getRequest().getMethod());
            }
            return false;
        } catch (Throwable th) {
            return false;
        }
    }

    private static String getCseqMethod(Message message) {
        return ((CSeqHeader) message.getHeader("CSeq")).getMethod();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean expectResponse(String expectedMethod, EventObject evt) {
        if (evt instanceof ResponseEvent) {
            ResponseEvent event = (ResponseEvent) evt;
            Response response = event.getResponse();
            return expectedMethod.equalsIgnoreCase(getCseqMethod(response));
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static SipProfile createPeerProfile(HeaderAddress header) throws SipException {
        try {
            Address address = header.getAddress();
            SipURI uri = (SipURI) address.getURI();
            String username = uri.getUser();
            if (username == null) {
                username = ANONYMOUS;
            }
            int port = uri.getPort();
            SipProfile.Builder builder = new SipProfile.Builder(username, uri.getHost()).setDisplayName(address.getDisplayName());
            if (port > 0) {
                builder.setPort(port);
            }
            return builder.build();
        } catch (IllegalArgumentException e) {
            throw new SipException("createPeerProfile()", e);
        } catch (ParseException e2) {
            throw new SipException("createPeerProfile()", e2);
        }
    }

    private static boolean isLoggable(SipSessionImpl s) {
        if (s != null) {
            switch (s.mState) {
                case 9:
                    return false;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isLoggable(EventObject evt) {
        return isLoggable(null, evt);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isLoggable(SipSessionImpl s, EventObject evt) {
        if (isLoggable(s) && evt != null) {
            if (evt instanceof ResponseEvent) {
                Response response = ((ResponseEvent) evt).getResponse();
                "OPTIONS".equals(response.getHeader("CSeq"));
                return false;
            } else if (evt instanceof RequestEvent) {
                isRequestEvent("OPTIONS", evt);
                return false;
            } else {
                return false;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String logEvt(EventObject evt) {
        if (evt instanceof RequestEvent) {
            return ((RequestEvent) evt).getRequest().toString();
        }
        if (evt instanceof ResponseEvent) {
            return ((ResponseEvent) evt).getResponse().toString();
        }
        return evt.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RegisterCommand extends EventObject {
        private int mDuration;

        public RegisterCommand(int duration) {
            super(SipSessionGroup.this);
            this.mDuration = duration;
        }

        public int getDuration() {
            return this.mDuration;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MakeCallCommand extends EventObject {
        private String mSessionDescription;
        private int mTimeout;

        public MakeCallCommand(SipProfile peerProfile, String sessionDescription, int timeout) {
            super(peerProfile);
            this.mSessionDescription = sessionDescription;
            this.mTimeout = timeout;
        }

        public SipProfile getPeerProfile() {
            return (SipProfile) getSource();
        }

        public String getSessionDescription() {
            return this.mSessionDescription;
        }

        public int getTimeout() {
            return this.mTimeout;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class KeepAliveProcessCallbackProxy implements KeepAliveProcessCallback {
        private static final String KAPCP_TAG = "KeepAliveProcessCallbackProxy";
        private KeepAliveProcessCallback mCallback;

        KeepAliveProcessCallbackProxy(KeepAliveProcessCallback callback) {
            this.mCallback = callback;
        }

        private void proxy(Runnable runnable) {
            new Thread(runnable, "SIP-KeepAliveProcessCallbackThread").start();
        }

        @Override // com.android.server.sip.SipSessionGroup.KeepAliveProcessCallback
        public void onResponse(final boolean portChanged) {
            if (this.mCallback == null) {
                return;
            }
            proxy(new Runnable() { // from class: com.android.server.sip.SipSessionGroup.KeepAliveProcessCallbackProxy.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        KeepAliveProcessCallbackProxy.this.mCallback.onResponse(portChanged);
                    } catch (Throwable t) {
                        KeepAliveProcessCallbackProxy.this.loge("onResponse", t);
                    }
                }
            });
        }

        @Override // com.android.server.sip.SipSessionGroup.KeepAliveProcessCallback
        public void onError(final int errorCode, final String description) {
            if (this.mCallback == null) {
                return;
            }
            proxy(new Runnable() { // from class: com.android.server.sip.SipSessionGroup.KeepAliveProcessCallbackProxy.2
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        KeepAliveProcessCallbackProxy.this.mCallback.onError(errorCode, description);
                    } catch (Throwable t) {
                        KeepAliveProcessCallbackProxy.this.loge("onError", t);
                    }
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void loge(String s, Throwable t) {
            Rlog.e(KAPCP_TAG, s, t);
        }
    }

    private void log(String s) {
        Rlog.d(TAG, s);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loge(String s, Throwable t) {
        Rlog.e(TAG, s, t);
    }
}
