package com.android.server.sip;

import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.sip.ISipService;
import android.net.sip.ISipSession;
import android.net.sip.ISipSessionListener;
import android.net.sip.SipErrorCode;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipSessionAdapter;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.telephony.Rlog;
import com.android.server.sip.SipSessionGroup;
import gov.nist.core.Separators;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.sip.SipException;
/* loaded from: classes.dex */
public final class SipService extends ISipService.Stub {
    static final boolean DBG = true;
    private static final int DEFAULT_KEEPALIVE_INTERVAL = 10;
    private static final int DEFAULT_MAX_KEEPALIVE_INTERVAL = 120;
    private static final int EXPIRY_TIME = 3600;
    private static final int MIN_EXPIRY_TIME = 60;
    private static final int SHORT_EXPIRY_TIME = 10;
    static final String TAG = "SipService";
    private final AppOpsManager mAppOps;
    private ConnectivityReceiver mConnectivityReceiver;
    private Context mContext;
    private int mKeepAliveInterval;
    private String mLocalIp;
    private SipWakeLock mMyWakeLock;
    private SipKeepAliveProcessCallback mSipKeepAliveProcessCallback;
    private boolean mSipOnWifiOnly;
    private SipWakeupTimer mTimer;
    private WifiManager.WifiLock mWifiLock;
    private int mNetworkType = -1;
    private MyExecutor mExecutor = new MyExecutor();
    private Map<String, SipSessionGroupExt> mSipGroups = new HashMap();
    private Map<String, ISipSession> mPendingSessions = new HashMap();
    private int mLastGoodKeepAliveInterval = 10;

    public static void start(Context context) {
        if (SipManager.isApiSupported(context) && ServiceManager.getService("sip") == null) {
            ServiceManager.addService("sip", new SipService(context));
            context.sendBroadcast(new Intent(SipManager.ACTION_SIP_SERVICE_UP));
            slog("start:");
        }
    }

    private SipService(Context context) {
        log("SipService: started!");
        this.mContext = context;
        this.mConnectivityReceiver = new ConnectivityReceiver();
        WifiManager.WifiLock createWifiLock = ((WifiManager) context.getSystemService("wifi")).createWifiLock(1, TAG);
        this.mWifiLock = createWifiLock;
        createWifiLock.setReferenceCounted(false);
        this.mSipOnWifiOnly = SipManager.isSipWifiOnly(context);
        this.mMyWakeLock = new SipWakeLock((PowerManager) context.getSystemService("power"));
        this.mTimer = new SipWakeupTimer(context, this.mExecutor);
        this.mAppOps = (AppOpsManager) this.mContext.getSystemService(AppOpsManager.class);
    }

    @Override // android.net.sip.ISipService
    public synchronized List<SipProfile> getProfiles(String opPackageName) throws RemoteException {
        ArrayList<SipProfile> profiles;
        if (!canUseSip(opPackageName, "getProfiles")) {
            throw new RemoteException(String.format("Package %s cannot use Sip service", opPackageName));
        }
        boolean isCallerRadio = isCallerRadio();
        profiles = new ArrayList<>();
        for (SipSessionGroupExt group : this.mSipGroups.values()) {
            if (isCallerRadio || isCallerCreator(group)) {
                profiles.add(group.getLocalProfile());
            }
        }
        return profiles;
    }

    @Override // android.net.sip.ISipService
    public synchronized void open(SipProfile localProfile, String opPackageName) {
        if (canUseSip(opPackageName, "open")) {
            localProfile.setCallingUid(Binder.getCallingUid());
            try {
                createGroup(localProfile);
            } catch (SipException e) {
                loge("openToMakeCalls()", e);
            }
        }
    }

    @Override // android.net.sip.ISipService
    public synchronized void open3(SipProfile localProfile, PendingIntent incomingCallPendingIntent, ISipSessionListener listener, String opPackageName) {
        if (canUseSip(opPackageName, "open3")) {
            localProfile.setCallingUid(Binder.getCallingUid());
            if (incomingCallPendingIntent == null) {
                log("open3: incomingCallPendingIntent cannot be null; the profile is not opened");
                return;
            }
            log("open3: " + obfuscateSipUri(localProfile.getUriString()) + ": " + incomingCallPendingIntent + ": " + listener);
            try {
                SipSessionGroupExt group = createGroup(localProfile, incomingCallPendingIntent, listener);
                if (localProfile.getAutoRegistration()) {
                    group.openToReceiveCalls();
                    updateWakeLocks();
                }
            } catch (SipException e) {
                loge("open3:", e);
            }
        }
    }

    private boolean isCallerCreator(SipSessionGroupExt group) {
        SipProfile profile = group.getLocalProfile();
        if (profile.getCallingUid() == Binder.getCallingUid()) {
            return DBG;
        }
        return false;
    }

    private boolean isCallerCreatorOrRadio(SipSessionGroupExt group) {
        if (isCallerRadio() || isCallerCreator(group)) {
            return DBG;
        }
        return false;
    }

    private boolean isCallerRadio() {
        if (Binder.getCallingUid() == 1001) {
            return DBG;
        }
        return false;
    }

    @Override // android.net.sip.ISipService
    public synchronized void close(String localProfileUri, String opPackageName) {
        if (canUseSip(opPackageName, "close")) {
            SipSessionGroupExt group = this.mSipGroups.get(localProfileUri);
            if (group == null) {
                return;
            }
            if (!isCallerCreatorOrRadio(group)) {
                log("only creator or radio can close this profile");
                return;
            }
            SipSessionGroupExt group2 = this.mSipGroups.remove(localProfileUri);
            notifyProfileRemoved(group2.getLocalProfile());
            group2.close();
            updateWakeLocks();
        }
    }

    @Override // android.net.sip.ISipService
    public synchronized boolean isOpened(String localProfileUri, String opPackageName) {
        if (canUseSip(opPackageName, "isOpened")) {
            SipSessionGroupExt group = this.mSipGroups.get(localProfileUri);
            if (group == null) {
                return false;
            }
            if (isCallerCreatorOrRadio(group)) {
                return DBG;
            }
            log("only creator or radio can query on the profile");
            return false;
        }
        return false;
    }

    @Override // android.net.sip.ISipService
    public synchronized boolean isRegistered(String localProfileUri, String opPackageName) {
        if (canUseSip(opPackageName, "isRegistered")) {
            SipSessionGroupExt group = this.mSipGroups.get(localProfileUri);
            if (group == null) {
                return false;
            }
            if (isCallerCreatorOrRadio(group)) {
                return group.isRegistered();
            }
            log("only creator or radio can query on the profile");
            return false;
        }
        return false;
    }

    @Override // android.net.sip.ISipService
    public synchronized void setRegistrationListener(String localProfileUri, ISipSessionListener listener, String opPackageName) {
        if (canUseSip(opPackageName, "setRegistrationListener")) {
            SipSessionGroupExt group = this.mSipGroups.get(localProfileUri);
            if (group == null) {
                return;
            }
            if (isCallerCreator(group)) {
                group.setListener(listener);
            } else {
                log("only creator can set listener on the profile");
            }
        }
    }

    @Override // android.net.sip.ISipService
    public synchronized ISipSession createSession(SipProfile localProfile, ISipSessionListener listener, String opPackageName) {
        log("createSession: profile" + localProfile);
        if (canUseSip(opPackageName, "createSession")) {
            localProfile.setCallingUid(Binder.getCallingUid());
            if (this.mNetworkType == -1) {
                log("createSession: mNetworkType==-1 ret=null");
                return null;
            }
            try {
                SipSessionGroupExt group = createGroup(localProfile);
                return group.createSession(listener);
            } catch (SipException e) {
                loge("createSession;", e);
                return null;
            }
        }
        return null;
    }

    @Override // android.net.sip.ISipService
    public synchronized ISipSession getPendingSession(String callId, String opPackageName) {
        if (canUseSip(opPackageName, "getPendingSession")) {
            if (callId == null) {
                return null;
            }
            return this.mPendingSessions.get(callId);
        }
        return null;
    }

    private String determineLocalIp() {
        try {
            DatagramSocket s = new DatagramSocket();
            s.connect(InetAddress.getByName("192.168.1.1"), 80);
            return s.getLocalAddress().getHostAddress();
        } catch (IOException e) {
            loge("determineLocalIp()", e);
            return null;
        }
    }

    private SipSessionGroupExt createGroup(SipProfile localProfile) throws SipException {
        String key = localProfile.getUriString();
        SipSessionGroupExt group = this.mSipGroups.get(key);
        if (group == null) {
            SipSessionGroupExt group2 = new SipSessionGroupExt(localProfile, null, null);
            this.mSipGroups.put(key, group2);
            notifyProfileAdded(localProfile);
            return group2;
        } else if (!isCallerCreator(group)) {
            throw new SipException("only creator can access the profile");
        } else {
            return group;
        }
    }

    private SipSessionGroupExt createGroup(SipProfile localProfile, PendingIntent incomingCallPendingIntent, ISipSessionListener listener) throws SipException {
        String key = localProfile.getUriString();
        SipSessionGroupExt group = this.mSipGroups.get(key);
        if (group != null) {
            if (!isCallerCreator(group)) {
                throw new SipException("only creator can access the profile");
            }
            group.setIncomingCallPendingIntent(incomingCallPendingIntent);
            group.setListener(listener);
            return group;
        }
        SipSessionGroupExt group2 = new SipSessionGroupExt(localProfile, incomingCallPendingIntent, listener);
        this.mSipGroups.put(key, group2);
        notifyProfileAdded(localProfile);
        return group2;
    }

    private void notifyProfileAdded(SipProfile localProfile) {
        log("notify: profile added: " + localProfile);
        Intent intent = new Intent(SipManager.ACTION_SIP_ADD_PHONE);
        intent.putExtra(SipManager.EXTRA_LOCAL_URI, localProfile.getUriString());
        this.mContext.sendBroadcast(intent, "android.permission.USE_SIP");
        if (this.mSipGroups.size() == 1) {
            registerReceivers();
        }
    }

    private void notifyProfileRemoved(SipProfile localProfile) {
        log("notify: profile removed: " + localProfile);
        Intent intent = new Intent(SipManager.ACTION_SIP_REMOVE_PROFILE);
        intent.putExtra(SipManager.EXTRA_LOCAL_URI, localProfile.getUriString());
        this.mContext.sendBroadcast(intent, "android.permission.USE_SIP");
        if (this.mSipGroups.size() == 0) {
            unregisterReceivers();
        }
    }

    private void stopPortMappingMeasurement() {
        SipKeepAliveProcessCallback sipKeepAliveProcessCallback = this.mSipKeepAliveProcessCallback;
        if (sipKeepAliveProcessCallback != null) {
            sipKeepAliveProcessCallback.stop();
            this.mSipKeepAliveProcessCallback = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startPortMappingLifetimeMeasurement(SipProfile localProfile) {
        startPortMappingLifetimeMeasurement(localProfile, DEFAULT_MAX_KEEPALIVE_INTERVAL);
    }

    private void startPortMappingLifetimeMeasurement(SipProfile localProfile, int maxInterval) {
        if (this.mSipKeepAliveProcessCallback == null && this.mKeepAliveInterval == -1 && isBehindNAT(this.mLocalIp)) {
            log("startPortMappingLifetimeMeasurement: profile=" + localProfile.getUriString());
            int minInterval = this.mLastGoodKeepAliveInterval;
            if (minInterval >= maxInterval) {
                this.mLastGoodKeepAliveInterval = 10;
                minInterval = 10;
                log("  reset min interval to 10");
            }
            SipKeepAliveProcessCallback sipKeepAliveProcessCallback = new SipKeepAliveProcessCallback(localProfile, minInterval, maxInterval);
            this.mSipKeepAliveProcessCallback = sipKeepAliveProcessCallback;
            sipKeepAliveProcessCallback.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restartPortMappingLifetimeMeasurement(SipProfile localProfile, int maxInterval) {
        stopPortMappingMeasurement();
        this.mKeepAliveInterval = -1;
        startPortMappingLifetimeMeasurement(localProfile, maxInterval);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void addPendingSession(ISipSession session) {
        try {
            cleanUpPendingSessions();
            this.mPendingSessions.put(session.getCallId(), session);
            log("#pending sess=" + this.mPendingSessions.size());
        } catch (RemoteException e) {
            loge("addPendingSession()", e);
        }
    }

    private void cleanUpPendingSessions() throws RemoteException {
        Map.Entry<String, ISipSession>[] entries = (Map.Entry[]) this.mPendingSessions.entrySet().toArray(new Map.Entry[this.mPendingSessions.size()]);
        for (Map.Entry<String, ISipSession> entry : entries) {
            if (entry.getValue().getState() != 3) {
                this.mPendingSessions.remove(entry.getKey());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized boolean callingSelf(SipSessionGroupExt ringingGroup, SipSessionGroup.SipSessionImpl ringingSession) {
        String callId = ringingSession.getCallId();
        for (SipSessionGroupExt group : this.mSipGroups.values()) {
            if (group != ringingGroup && group.containsSession(callId)) {
                log("call self: " + ringingSession.getLocalProfile().getUriString() + " -> " + group.getLocalProfile().getUriString());
                return DBG;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void onKeepAliveIntervalChanged() {
        for (SipSessionGroupExt group : this.mSipGroups.values()) {
            group.onKeepAliveIntervalChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getKeepAliveInterval() {
        int i = this.mKeepAliveInterval;
        if (i >= 0) {
            return i;
        }
        return this.mLastGoodKeepAliveInterval;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isBehindNAT(String address) {
        byte[] d;
        try {
            d = InetAddress.getByName(address).getAddress();
        } catch (UnknownHostException e) {
            loge("isBehindAT()" + address, e);
        }
        if (d[0] != 10 && ((d[0] & 255) != 172 || (d[1] & 240) != 16)) {
            if ((d[0] & 255) == 192) {
                if ((d[1] & 255) == 168) {
                }
            }
            return false;
        }
        return DBG;
    }

    private boolean canUseSip(String packageName, String message) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.USE_SIP", message);
        if (this.mAppOps.noteOp("android:use_sip", Binder.getCallingUid(), packageName, null, message) == 0) {
            return DBG;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SipSessionGroupExt extends SipSessionAdapter {
        private static final boolean SSGE_DBG = true;
        private static final String SSGE_TAG = "SipSessionGroupExt";
        private SipAutoReg mAutoRegistration;
        private PendingIntent mIncomingCallPendingIntent;
        private boolean mOpenedToReceiveCalls;
        private SipSessionGroup mSipGroup;

        public SipSessionGroupExt(SipProfile localProfile, PendingIntent incomingCallPendingIntent, ISipSessionListener listener) throws SipException {
            this.mAutoRegistration = new SipAutoReg();
            log("SipSessionGroupExt: profile=" + localProfile);
            this.mSipGroup = new SipSessionGroup(duplicate(localProfile), localProfile.getPassword(), SipService.this.mTimer, SipService.this.mMyWakeLock);
            this.mIncomingCallPendingIntent = incomingCallPendingIntent;
            this.mAutoRegistration.setListener(listener);
        }

        public SipProfile getLocalProfile() {
            return this.mSipGroup.getLocalProfile();
        }

        public boolean containsSession(String callId) {
            return this.mSipGroup.containsSession(callId);
        }

        public void onKeepAliveIntervalChanged() {
            this.mAutoRegistration.onKeepAliveIntervalChanged();
        }

        void setWakeupTimer(SipWakeupTimer timer) {
            this.mSipGroup.setWakeupTimer(timer);
        }

        private SipProfile duplicate(SipProfile p) {
            try {
                return new SipProfile.Builder(p).setPassword(Separators.STAR).build();
            } catch (Exception e) {
                loge("duplicate()", e);
                throw new RuntimeException("duplicate profile", e);
            }
        }

        public void setListener(ISipSessionListener listener) {
            this.mAutoRegistration.setListener(listener);
        }

        public void setIncomingCallPendingIntent(PendingIntent pIntent) {
            this.mIncomingCallPendingIntent = pIntent;
        }

        public void openToReceiveCalls() {
            this.mOpenedToReceiveCalls = SSGE_DBG;
            if (SipService.this.mNetworkType != -1) {
                this.mSipGroup.openToReceiveCalls(this);
                this.mAutoRegistration.start(this.mSipGroup);
            }
            log("openToReceiveCalls: " + SipService.obfuscateSipUri(getUri()) + ": " + this.mIncomingCallPendingIntent);
        }

        public void onConnectivityChanged(boolean connected) throws SipException {
            log("onConnectivityChanged: connected=" + connected + " uri=" + SipService.obfuscateSipUri(getUri()) + ": " + this.mIncomingCallPendingIntent);
            this.mSipGroup.onConnectivityChanged();
            if (connected) {
                this.mSipGroup.reset();
                if (this.mOpenedToReceiveCalls) {
                    openToReceiveCalls();
                    return;
                }
                return;
            }
            this.mSipGroup.close();
            this.mAutoRegistration.stop();
        }

        public void close() {
            this.mOpenedToReceiveCalls = false;
            this.mSipGroup.close();
            this.mAutoRegistration.stop();
            log("close: " + SipService.obfuscateSipUri(getUri()) + ": " + this.mIncomingCallPendingIntent);
        }

        public ISipSession createSession(ISipSessionListener listener) {
            log("createSession");
            return this.mSipGroup.createSession(listener);
        }

        @Override // android.net.sip.SipSessionAdapter, android.net.sip.ISipSessionListener
        public void onRinging(ISipSession s, SipProfile caller, String sessionDescription) {
            SipSessionGroup.SipSessionImpl session = (SipSessionGroup.SipSessionImpl) s;
            synchronized (SipService.this) {
                try {
                    try {
                    } catch (PendingIntent.CanceledException e) {
                        loge("onRinging: pendingIntent is canceled, drop incoming call", e);
                        session.endCall();
                    }
                    if (isRegistered() && !SipService.this.callingSelf(this, session)) {
                        SipService.this.addPendingSession(session);
                        Intent intent = SipManager.createIncomingCallBroadcast(session.getCallId(), sessionDescription);
                        log("onRinging: uri=" + getUri() + ": " + caller.getUri() + ": " + session.getCallId() + Separators.f12SP + this.mIncomingCallPendingIntent);
                        this.mIncomingCallPendingIntent.send(SipService.this.mContext, 101, intent);
                        return;
                    }
                    log("onRinging: end notReg or self");
                    session.endCall();
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // android.net.sip.SipSessionAdapter, android.net.sip.ISipSessionListener
        public void onError(ISipSession session, int errorCode, String message) {
            log("onError: errorCode=" + errorCode + " desc=" + SipErrorCode.toString(errorCode) + ": " + message);
        }

        public boolean isOpenedToReceiveCalls() {
            return this.mOpenedToReceiveCalls;
        }

        public boolean isRegistered() {
            return this.mAutoRegistration.isRegistered();
        }

        private String getUri() {
            return this.mSipGroup.getLocalProfileUri();
        }

        private void log(String s) {
            Rlog.d(SSGE_TAG, s);
        }

        private void loge(String s, Throwable t) {
            Rlog.e(SSGE_TAG, s, t);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SipKeepAliveProcessCallback implements Runnable, SipSessionGroup.KeepAliveProcessCallback {
        private static final int MIN_INTERVAL = 5;
        private static final int NAT_MEASUREMENT_RETRY_INTERVAL = 120;
        private static final int PASS_THRESHOLD = 10;
        private static final boolean SKAI_DBG = true;
        private static final String SKAI_TAG = "SipKeepAliveProcessCallback";
        private SipSessionGroupExt mGroup;
        private int mInterval;
        private SipProfile mLocalProfile;
        private int mMaxInterval;
        private int mMinInterval;
        private int mPassCount;
        private SipSessionGroup.SipSessionImpl mSession;

        public SipKeepAliveProcessCallback(SipProfile localProfile, int minInterval, int maxInterval) {
            this.mMaxInterval = maxInterval;
            this.mMinInterval = minInterval;
            this.mLocalProfile = localProfile;
        }

        public void start() {
            synchronized (SipService.this) {
                if (this.mSession != null) {
                    return;
                }
                int i = (this.mMaxInterval + this.mMinInterval) / 2;
                this.mInterval = i;
                this.mPassCount = 0;
                if (i < 10 || checkTermination()) {
                    log("start: measurement aborted; interval=[" + this.mMinInterval + Separators.COMMA + this.mMaxInterval + "]");
                    return;
                }
                log("start: interval=" + this.mInterval);
                SipSessionGroupExt sipSessionGroupExt = new SipSessionGroupExt(this.mLocalProfile, null, null);
                this.mGroup = sipSessionGroupExt;
                sipSessionGroupExt.setWakeupTimer(new SipWakeupTimer(SipService.this.mContext, SipService.this.mExecutor));
                SipSessionGroup.SipSessionImpl sipSessionImpl = (SipSessionGroup.SipSessionImpl) this.mGroup.createSession(null);
                this.mSession = sipSessionImpl;
                sipSessionImpl.startKeepAliveProcess(this.mInterval, this);
            }
        }

        public void stop() {
            synchronized (SipService.this) {
                SipSessionGroup.SipSessionImpl sipSessionImpl = this.mSession;
                if (sipSessionImpl != null) {
                    sipSessionImpl.stopKeepAliveProcess();
                    this.mSession = null;
                }
                SipSessionGroupExt sipSessionGroupExt = this.mGroup;
                if (sipSessionGroupExt != null) {
                    sipSessionGroupExt.close();
                    this.mGroup = null;
                }
                SipService.this.mTimer.cancel(this);
                log("stop");
            }
        }

        private void restart() {
            synchronized (SipService.this) {
                if (this.mSession == null) {
                    return;
                }
                log("restart: interval=" + this.mInterval);
                try {
                    this.mSession.stopKeepAliveProcess();
                    this.mPassCount = 0;
                    this.mSession.startKeepAliveProcess(this.mInterval, this);
                } catch (SipException e) {
                    loge("restart", e);
                }
            }
        }

        private boolean checkTermination() {
            if (this.mMaxInterval - this.mMinInterval < 5) {
                return SKAI_DBG;
            }
            return false;
        }

        @Override // com.android.server.sip.SipSessionGroup.KeepAliveProcessCallback
        public void onResponse(boolean portChanged) {
            synchronized (SipService.this) {
                if (!portChanged) {
                    int i = this.mPassCount + 1;
                    this.mPassCount = i;
                    if (i != 10) {
                        return;
                    }
                    if (SipService.this.mKeepAliveInterval > 0) {
                        SipService sipService = SipService.this;
                        sipService.mLastGoodKeepAliveInterval = sipService.mKeepAliveInterval;
                    }
                    SipService sipService2 = SipService.this;
                    int i2 = this.mInterval;
                    this.mMinInterval = i2;
                    sipService2.mKeepAliveInterval = i2;
                    log("onResponse: portChanged=" + portChanged + " mKeepAliveInterval=" + SipService.this.mKeepAliveInterval);
                    SipService.this.onKeepAliveIntervalChanged();
                } else {
                    this.mMaxInterval = this.mInterval;
                }
                if (checkTermination()) {
                    stop();
                    SipService.this.mKeepAliveInterval = this.mMinInterval;
                    log("onResponse: checkTermination mKeepAliveInterval=" + SipService.this.mKeepAliveInterval);
                } else {
                    this.mInterval = (this.mMaxInterval + this.mMinInterval) / 2;
                    log("onResponse: mKeepAliveInterval=" + SipService.this.mKeepAliveInterval + ", new mInterval=" + this.mInterval);
                    restart();
                }
            }
        }

        @Override // com.android.server.sip.SipSessionGroup.KeepAliveProcessCallback
        public void onError(int errorCode, String description) {
            loge("onError: errorCode=" + errorCode + " desc=" + description);
            restartLater();
        }

        @Override // java.lang.Runnable
        public void run() {
            SipService.this.mTimer.cancel(this);
            restart();
        }

        private void restartLater() {
            synchronized (SipService.this) {
                SipService.this.mTimer.cancel(this);
                SipService.this.mTimer.set(NAT_MEASUREMENT_RETRY_INTERVAL * 1000, this);
            }
        }

        private void log(String s) {
            Rlog.d(SKAI_TAG, s);
        }

        private void loge(String s) {
            Rlog.d(SKAI_TAG, s);
        }

        private void loge(String s, Throwable t) {
            Rlog.d(SKAI_TAG, s, t);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SipAutoReg extends SipSessionAdapter implements Runnable, SipSessionGroup.KeepAliveProcessCallback {
        private static final int MIN_KEEPALIVE_SUCCESS_COUNT = 10;
        private static final boolean SAR_DBG = true;
        private String SAR_TAG;
        private int mBackoff;
        private int mErrorCode;
        private String mErrorMessage;
        private long mExpiryTime;
        private SipSessionGroup.SipSessionImpl mKeepAliveSession;
        private int mKeepAliveSuccessCount;
        private SipSessionListenerProxy mProxy;
        private boolean mRegistered;
        private boolean mRunning;
        private SipSessionGroup.SipSessionImpl mSession;

        private SipAutoReg() {
            this.mProxy = new SipSessionListenerProxy();
            this.mBackoff = 1;
            this.mRunning = false;
            this.mKeepAliveSuccessCount = 0;
        }

        public void start(SipSessionGroup group) {
            if (!this.mRunning) {
                this.mRunning = SAR_DBG;
                this.mBackoff = 1;
                SipSessionGroup.SipSessionImpl sipSessionImpl = (SipSessionGroup.SipSessionImpl) group.createSession(this);
                this.mSession = sipSessionImpl;
                if (sipSessionImpl == null) {
                    return;
                }
                SipService.this.mMyWakeLock.acquire(this.mSession);
                this.mSession.unregister();
                this.SAR_TAG = "SipAutoReg:" + SipService.obfuscateSipUri(this.mSession.getLocalProfile().getUriString());
                log("start: group=" + group);
            }
        }

        private void startKeepAliveProcess(int interval) {
            log("startKeepAliveProcess: interval=" + interval);
            SipSessionGroup.SipSessionImpl sipSessionImpl = this.mKeepAliveSession;
            if (sipSessionImpl == null) {
                this.mKeepAliveSession = this.mSession.duplicate();
            } else {
                sipSessionImpl.stopKeepAliveProcess();
            }
            try {
                this.mKeepAliveSession.startKeepAliveProcess(interval, this);
            } catch (SipException e) {
                loge("startKeepAliveProcess: interval=" + interval, e);
            }
        }

        private void stopKeepAliveProcess() {
            SipSessionGroup.SipSessionImpl sipSessionImpl = this.mKeepAliveSession;
            if (sipSessionImpl != null) {
                sipSessionImpl.stopKeepAliveProcess();
                this.mKeepAliveSession = null;
            }
            this.mKeepAliveSuccessCount = 0;
        }

        @Override // com.android.server.sip.SipSessionGroup.KeepAliveProcessCallback
        public void onResponse(boolean portChanged) {
            synchronized (SipService.this) {
                if (portChanged) {
                    int interval = SipService.this.getKeepAliveInterval();
                    if (this.mKeepAliveSuccessCount < 10) {
                        log("onResponse: keepalive doesn't work with interval " + interval + ", past success count=" + this.mKeepAliveSuccessCount);
                        if (interval > 10) {
                            SipService.this.restartPortMappingLifetimeMeasurement(this.mSession.getLocalProfile(), interval);
                            this.mKeepAliveSuccessCount = 0;
                        }
                    } else {
                        log("keep keepalive going with interval " + interval + ", past success count=" + this.mKeepAliveSuccessCount);
                        this.mKeepAliveSuccessCount /= 2;
                    }
                } else {
                    SipService.this.startPortMappingLifetimeMeasurement(this.mSession.getLocalProfile());
                    this.mKeepAliveSuccessCount++;
                }
                if (this.mRunning && portChanged) {
                    this.mKeepAliveSession = null;
                    SipService.this.mMyWakeLock.acquire(this.mSession);
                    this.mSession.register(SipService.EXPIRY_TIME);
                }
            }
        }

        @Override // com.android.server.sip.SipSessionGroup.KeepAliveProcessCallback
        public void onError(int errorCode, String description) {
            loge("onError: errorCode=" + errorCode + " desc=" + description);
            onResponse(SAR_DBG);
        }

        public void stop() {
            if (this.mRunning) {
                this.mRunning = false;
                SipService.this.mMyWakeLock.release(this.mSession);
                SipSessionGroup.SipSessionImpl sipSessionImpl = this.mSession;
                if (sipSessionImpl != null) {
                    sipSessionImpl.setListener(null);
                    if (SipService.this.mNetworkType != -1 && this.mRegistered) {
                        this.mSession.unregister();
                    }
                }
                SipService.this.mTimer.cancel(this);
                stopKeepAliveProcess();
                this.mRegistered = false;
                setListener(this.mProxy.getListener());
            }
        }

        public void onKeepAliveIntervalChanged() {
            if (this.mKeepAliveSession != null) {
                int newInterval = SipService.this.getKeepAliveInterval();
                log("onKeepAliveIntervalChanged: interval=" + newInterval);
                this.mKeepAliveSuccessCount = 0;
                startKeepAliveProcess(newInterval);
            }
        }

        public void setListener(ISipSessionListener listener) {
            int state;
            synchronized (SipService.this) {
                this.mProxy.setListener(listener);
                SipSessionGroup.SipSessionImpl sipSessionImpl = this.mSession;
                if (sipSessionImpl == null) {
                    state = 0;
                } else {
                    state = sipSessionImpl.getState();
                }
                if (state != 1 && state != 2) {
                    if (this.mRegistered) {
                        int duration = (int) (this.mExpiryTime - SystemClock.elapsedRealtime());
                        this.mProxy.onRegistrationDone(this.mSession, duration);
                    } else {
                        int i = this.mErrorCode;
                        if (i != 0) {
                            if (i == -5) {
                                this.mProxy.onRegistrationTimeout(this.mSession);
                            } else {
                                this.mProxy.onRegistrationFailed(this.mSession, i, this.mErrorMessage);
                            }
                        } else if (SipService.this.mNetworkType == -1) {
                            this.mProxy.onRegistrationFailed(this.mSession, -10, "no data connection");
                        } else if (!this.mRunning) {
                            this.mProxy.onRegistrationFailed(this.mSession, -4, "registration not running");
                        } else {
                            this.mProxy.onRegistrationFailed(this.mSession, -9, String.valueOf(state));
                        }
                    }
                }
                this.mProxy.onRegistering(this.mSession);
            }
        }

        public boolean isRegistered() {
            return this.mRegistered;
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (SipService.this) {
                if (this.mRunning) {
                    this.mErrorCode = 0;
                    this.mErrorMessage = null;
                    log("run: registering");
                    if (SipService.this.mNetworkType != -1) {
                        SipService.this.mMyWakeLock.acquire(this.mSession);
                        this.mSession.register(SipService.EXPIRY_TIME);
                    }
                }
            }
        }

        private void restart(int duration) {
            log("restart: duration=" + duration + "s later.");
            SipService.this.mTimer.cancel(this);
            SipService.this.mTimer.set(duration * 1000, this);
        }

        private int backoffDuration() {
            int i = this.mBackoff;
            int duration = i * 10;
            if (duration > SipService.EXPIRY_TIME) {
                return SipService.EXPIRY_TIME;
            }
            this.mBackoff = i * 2;
            return duration;
        }

        @Override // android.net.sip.SipSessionAdapter, android.net.sip.ISipSessionListener
        public void onRegistering(ISipSession session) {
            log("onRegistering: " + session);
            synchronized (SipService.this) {
                if (notCurrentSession(session)) {
                    return;
                }
                this.mRegistered = false;
                this.mProxy.onRegistering(session);
            }
        }

        private boolean notCurrentSession(ISipSession session) {
            if (session != this.mSession) {
                ((SipSessionGroup.SipSessionImpl) session).setListener(null);
                SipService.this.mMyWakeLock.release(session);
                return SAR_DBG;
            }
            return this.mRunning ^ SAR_DBG;
        }

        @Override // android.net.sip.SipSessionAdapter, android.net.sip.ISipSessionListener
        public void onRegistrationDone(ISipSession session, int duration) {
            log("onRegistrationDone: " + session);
            synchronized (SipService.this) {
                if (notCurrentSession(session)) {
                    return;
                }
                this.mProxy.onRegistrationDone(session, duration);
                if (duration > 0) {
                    this.mExpiryTime = SystemClock.elapsedRealtime() + (duration * 1000);
                    if (!this.mRegistered) {
                        this.mRegistered = SAR_DBG;
                        int duration2 = duration - 60;
                        if (duration2 < 60) {
                            duration2 = 60;
                        }
                        restart(duration2);
                        SipProfile localProfile = this.mSession.getLocalProfile();
                        if (this.mKeepAliveSession == null) {
                            SipService sipService = SipService.this;
                            if (sipService.isBehindNAT(sipService.mLocalIp) || localProfile.getSendKeepAlive()) {
                                startKeepAliveProcess(SipService.this.getKeepAliveInterval());
                            }
                        }
                    }
                    SipService.this.mMyWakeLock.release(session);
                } else {
                    this.mRegistered = false;
                    this.mExpiryTime = -1L;
                    log("Refresh registration immediately");
                    run();
                }
            }
        }

        @Override // android.net.sip.SipSessionAdapter, android.net.sip.ISipSessionListener
        public void onRegistrationFailed(ISipSession session, int errorCode, String message) {
            log("onRegistrationFailed: " + session + ": " + SipErrorCode.toString(errorCode) + ": " + message);
            synchronized (SipService.this) {
                if (notCurrentSession(session)) {
                    return;
                }
                switch (errorCode) {
                    case SipErrorCode.SERVER_UNREACHABLE /* -12 */:
                    case SipErrorCode.INVALID_CREDENTIALS /* -8 */:
                        log("   pause auto-registration");
                        stop();
                        break;
                    default:
                        restartLater();
                        break;
                }
                this.mErrorCode = errorCode;
                this.mErrorMessage = message;
                this.mProxy.onRegistrationFailed(session, errorCode, message);
                SipService.this.mMyWakeLock.release(session);
            }
        }

        @Override // android.net.sip.SipSessionAdapter, android.net.sip.ISipSessionListener
        public void onRegistrationTimeout(ISipSession session) {
            log("onRegistrationTimeout: " + session);
            synchronized (SipService.this) {
                if (notCurrentSession(session)) {
                    return;
                }
                this.mErrorCode = -5;
                this.mProxy.onRegistrationTimeout(session);
                restartLater();
                SipService.this.mMyWakeLock.release(session);
            }
        }

        private void restartLater() {
            loge("restartLater");
            this.mRegistered = false;
            restart(backoffDuration());
        }

        private void log(String s) {
            Rlog.d(this.SAR_TAG, s);
        }

        private void loge(String s) {
            Rlog.e(this.SAR_TAG, s);
        }

        private void loge(String s, Throwable e) {
            Rlog.e(this.SAR_TAG, s, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ConnectivityReceiver extends BroadcastReceiver {
        private ConnectivityReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                final NetworkInfo info = (NetworkInfo) bundle.get("networkInfo");
                SipService.this.mExecutor.execute(new Runnable() { // from class: com.android.server.sip.SipService.ConnectivityReceiver.1
                    @Override // java.lang.Runnable
                    public void run() {
                        SipService.this.onConnectivityChanged(info);
                    }
                });
            }
        }
    }

    private void registerReceivers() {
        this.mContext.registerReceiver(this.mConnectivityReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        log("registerReceivers:");
    }

    private void unregisterReceivers() {
        this.mContext.unregisterReceiver(this.mConnectivityReceiver);
        log("unregisterReceivers:");
        this.mWifiLock.release();
        this.mNetworkType = -1;
    }

    private void updateWakeLocks() {
        for (SipSessionGroupExt group : this.mSipGroups.values()) {
            if (group.isOpenedToReceiveCalls()) {
                int i = this.mNetworkType;
                if (i == 1 || i == -1) {
                    this.mWifiLock.acquire();
                    return;
                } else {
                    this.mWifiLock.release();
                    return;
                }
            }
        }
        this.mWifiLock.release();
        this.mMyWakeLock.reset();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x000f, code lost:
        if (r7.getType() == r6.mNetworkType) goto L4;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public synchronized void onConnectivityChanged(NetworkInfo info) {
        if (info != null) {
            if (!info.isConnected()) {
            }
        }
        ConnectivityManager cm = (ConnectivityManager) this.mContext.getSystemService("connectivity");
        info = cm.getActiveNetworkInfo();
        int networkType = (info == null || !info.isConnected()) ? -1 : info.getType();
        if (this.mSipOnWifiOnly && networkType != 1) {
            networkType = -1;
        }
        if (this.mNetworkType == networkType) {
            return;
        }
        log("onConnectivityChanged: " + this.mNetworkType + " -> " + networkType);
        try {
            if (this.mNetworkType != -1) {
                this.mLocalIp = null;
                stopPortMappingMeasurement();
                for (SipSessionGroupExt group : this.mSipGroups.values()) {
                    group.onConnectivityChanged(false);
                }
            }
            this.mNetworkType = networkType;
            if (networkType != -1) {
                this.mLocalIp = determineLocalIp();
                this.mKeepAliveInterval = -1;
                this.mLastGoodKeepAliveInterval = 10;
                for (SipSessionGroupExt group2 : this.mSipGroups.values()) {
                    group2.onConnectivityChanged(DBG);
                }
            }
            updateWakeLocks();
        } catch (SipException e) {
            loge("onConnectivityChanged()", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Looper createLooper() {
        HandlerThread thread = new HandlerThread("SipService.Executor");
        thread.start();
        return thread.getLooper();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MyExecutor extends Handler implements Executor {
        MyExecutor() {
            super(SipService.createLooper());
        }

        @Override // java.util.concurrent.Executor
        public void execute(Runnable task) {
            SipService.this.mMyWakeLock.acquire(task);
            Message.obtain(this, 0, task).sendToTarget();
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.obj instanceof Runnable) {
                executeInternal((Runnable) msg.obj);
            } else {
                SipService.this.log("handleMessage: not Runnable ignore msg=" + msg);
            }
        }

        private void executeInternal(Runnable task) {
            try {
                task.run();
            } finally {
                try {
                } finally {
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String s) {
        Rlog.d(TAG, s);
    }

    private static void slog(String s) {
        Rlog.d(TAG, s);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loge(String s, Throwable e) {
        Rlog.e(TAG, s, e);
    }

    public static String obfuscateSipUri(String sipUri) {
        StringBuilder sb = new StringBuilder();
        int start = 0;
        String sipUri2 = sipUri.trim();
        if (sipUri2.startsWith("sip:")) {
            start = 4;
            sb.append("sip:");
        }
        char prevC = 0;
        int len = sipUri2.length();
        for (int i = start; i < len; i++) {
            char c = sipUri2.charAt(i);
            char nextC = i + 1 < len ? sipUri2.charAt(i + 1) : (char) 0;
            char charToAppend = '*';
            if (i - start < 1 || i + 1 == len || isAllowedCharacter(c) || prevC == '@' || nextC == '@') {
                charToAppend = c;
            }
            sb.append(charToAppend);
            prevC = c;
        }
        return sb.toString();
    }

    private static boolean isAllowedCharacter(char c) {
        if (c == '@' || c == '.') {
            return DBG;
        }
        return false;
    }
}
