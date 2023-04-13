package com.android.server.vcn;

import android.content.Context;
import android.net.ConnectivityDiagnosticsManager;
import android.net.ConnectivityManager;
import android.net.InetAddresses;
import android.net.IpPrefix;
import android.net.IpSecManager;
import android.net.IpSecTransform;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkAgent;
import android.net.NetworkAgentConfig;
import android.net.NetworkCapabilities;
import android.net.NetworkProvider;
import android.net.NetworkRequest;
import android.net.NetworkScore;
import android.net.RouteInfo;
import android.net.TelephonyNetworkSpecifier;
import android.net.Uri;
import android.net.ipsec.ike.ChildSaProposal;
import android.net.ipsec.ike.ChildSessionCallback;
import android.net.ipsec.ike.ChildSessionConfiguration;
import android.net.ipsec.ike.ChildSessionParams;
import android.net.ipsec.ike.IkeSession;
import android.net.ipsec.ike.IkeSessionCallback;
import android.net.ipsec.ike.IkeSessionConfiguration;
import android.net.ipsec.ike.IkeSessionConnectionInfo;
import android.net.ipsec.ike.IkeSessionParams;
import android.net.ipsec.ike.IkeTrafficSelector;
import android.net.ipsec.ike.IkeTunnelConnectionParams;
import android.net.ipsec.ike.TunnelModeChildSessionParams;
import android.net.ipsec.ike.exceptions.IkeException;
import android.net.ipsec.ike.exceptions.IkeInternalException;
import android.net.ipsec.ike.exceptions.IkeProtocolException;
import android.net.vcn.VcnGatewayConnectionConfig;
import android.net.vcn.VcnTransportInfo;
import android.net.wifi.WifiInfo;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.PowerManager;
import android.os.Process;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.ArraySet;
import android.util.LocalLog;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import com.android.internal.util.WakeupMessage;
import com.android.server.VcnManagementService;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.vcn.TelephonySubscriptionTracker;
import com.android.server.vcn.Vcn;
import com.android.server.vcn.VcnGatewayConnection;
import com.android.server.vcn.routeselection.UnderlyingNetworkController;
import com.android.server.vcn.routeselection.UnderlyingNetworkRecord;
import com.android.server.vcn.util.LogUtils;
import com.android.server.vcn.util.MtuUtils;
import com.android.server.vcn.util.OneWayBoolean;
import com.android.server.vcn.util.PersistableBundleUtils;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public class VcnGatewayConnection extends StateMachine {
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    static final String DISCONNECT_REQUEST_ALARM;
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    static final InetAddress DUMMY_ADDR;
    public static final int[] MERGED_CAPABILITIES;
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    static final String NETWORK_INFO_EXTRA_INFO = "VCN";
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    static final String NETWORK_INFO_NETWORK_TYPE_STRING = "MOBILE";
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    static final int NETWORK_LOSS_DISCONNECT_TIMEOUT_SECONDS = 30;
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    static final String RETRY_TIMEOUT_ALARM;
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    static final String SAFEMODE_TIMEOUT_ALARM;
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    static final int SAFEMODE_TIMEOUT_SECONDS = 30;
    public static final String TAG;
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    static final String TEARDOWN_TIMEOUT_ALARM;
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    static final int TEARDOWN_TIMEOUT_SECONDS = 5;
    public VcnChildSessionConfiguration mChildConfig;
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    final ConnectedState mConnectedState;
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    final ConnectingState mConnectingState;
    public final VcnGatewayConnectionConfig mConnectionConfig;
    public final VcnConnectivityDiagnosticsCallback mConnectivityDiagnosticsCallback;
    public final ConnectivityDiagnosticsManager mConnectivityDiagnosticsManager;
    public final ConnectivityManager mConnectivityManager;
    public int mCurrentToken;
    public final Dependencies mDeps;
    public WakeupMessage mDisconnectRequestAlarm;
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    final DisconnectedState mDisconnectedState;
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    final DisconnectingState mDisconnectingState;
    public int mFailedAttempts;
    public final Vcn.VcnGatewayStatusCallback mGatewayStatusCallback;
    public IkeSessionConnectionInfo mIkeConnectionInfo;
    public VcnIkeSession mIkeSession;
    public final IpSecManager mIpSecManager;
    public boolean mIsInSafeMode;
    public final boolean mIsMobileDataEnabled;
    public OneWayBoolean mIsQuitting;
    public TelephonySubscriptionTracker.TelephonySubscriptionSnapshot mLastSnapshot;
    public VcnNetworkAgent mNetworkAgent;
    public WakeupMessage mRetryTimeoutAlarm;
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    final RetryTimeoutState mRetryTimeoutState;
    public WakeupMessage mSafeModeTimeoutAlarm;
    public final ParcelUuid mSubscriptionGroup;
    public WakeupMessage mTeardownTimeoutAlarm;
    public IpSecManager.IpSecTunnelInterface mTunnelIface;
    public UnderlyingNetworkRecord mUnderlying;
    public final UnderlyingNetworkController mUnderlyingNetworkController;
    public final VcnUnderlyingNetworkControllerCallback mUnderlyingNetworkControllerCallback;
    public final VcnContext mVcnContext;
    public final VcnWakeLock mWakeLock;

    /* loaded from: classes2.dex */
    public interface EventInfo {
    }

    public final void logVdbg(String str) {
    }

    static {
        String simpleName = VcnGatewayConnection.class.getSimpleName();
        TAG = simpleName;
        DUMMY_ADDR = InetAddresses.parseNumericAddress("192.0.2.0");
        TEARDOWN_TIMEOUT_ALARM = simpleName + "_TEARDOWN_TIMEOUT_ALARM";
        DISCONNECT_REQUEST_ALARM = simpleName + "_DISCONNECT_REQUEST_ALARM";
        RETRY_TIMEOUT_ALARM = simpleName + "_RETRY_TIMEOUT_ALARM";
        SAFEMODE_TIMEOUT_ALARM = simpleName + "_SAFEMODE_TIMEOUT_ALARM";
        MERGED_CAPABILITIES = new int[]{11, 18};
    }

    /* loaded from: classes2.dex */
    public static class EventUnderlyingNetworkChangedInfo implements EventInfo {
        public final UnderlyingNetworkRecord newUnderlying;

        public EventUnderlyingNetworkChangedInfo(UnderlyingNetworkRecord underlyingNetworkRecord) {
            this.newUnderlying = underlyingNetworkRecord;
        }

        public int hashCode() {
            return Objects.hash(this.newUnderlying);
        }

        public boolean equals(Object obj) {
            if (obj instanceof EventUnderlyingNetworkChangedInfo) {
                return Objects.equals(this.newUnderlying, ((EventUnderlyingNetworkChangedInfo) obj).newUnderlying);
            }
            return false;
        }
    }

    /* loaded from: classes2.dex */
    public static class EventSessionLostInfo implements EventInfo {
        public final Exception exception;

        public EventSessionLostInfo(Exception exc) {
            this.exception = exc;
        }

        public int hashCode() {
            return Objects.hash(this.exception);
        }

        public boolean equals(Object obj) {
            if (obj instanceof EventSessionLostInfo) {
                return Objects.equals(this.exception, ((EventSessionLostInfo) obj).exception);
            }
            return false;
        }
    }

    /* loaded from: classes2.dex */
    public static class EventTransformCreatedInfo implements EventInfo {
        public final int direction;
        public final IpSecTransform transform;

        public EventTransformCreatedInfo(int i, IpSecTransform ipSecTransform) {
            this.direction = i;
            Objects.requireNonNull(ipSecTransform);
            this.transform = ipSecTransform;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.direction), this.transform);
        }

        public boolean equals(Object obj) {
            if (obj instanceof EventTransformCreatedInfo) {
                EventTransformCreatedInfo eventTransformCreatedInfo = (EventTransformCreatedInfo) obj;
                return this.direction == eventTransformCreatedInfo.direction && Objects.equals(this.transform, eventTransformCreatedInfo.transform);
            }
            return false;
        }
    }

    /* loaded from: classes2.dex */
    public static class EventSetupCompletedInfo implements EventInfo {
        public final VcnChildSessionConfiguration childSessionConfig;

        public EventSetupCompletedInfo(VcnChildSessionConfiguration vcnChildSessionConfiguration) {
            Objects.requireNonNull(vcnChildSessionConfiguration);
            this.childSessionConfig = vcnChildSessionConfiguration;
        }

        public int hashCode() {
            return Objects.hash(this.childSessionConfig);
        }

        public boolean equals(Object obj) {
            if (obj instanceof EventSetupCompletedInfo) {
                return Objects.equals(this.childSessionConfig, ((EventSetupCompletedInfo) obj).childSessionConfig);
            }
            return false;
        }
    }

    /* loaded from: classes2.dex */
    public static class EventDisconnectRequestedInfo implements EventInfo {
        public final String reason;
        public final boolean shouldQuit;

        public EventDisconnectRequestedInfo(String str, boolean z) {
            Objects.requireNonNull(str);
            this.reason = str;
            this.shouldQuit = z;
        }

        public int hashCode() {
            return Objects.hash(this.reason, Boolean.valueOf(this.shouldQuit));
        }

        public boolean equals(Object obj) {
            if (obj instanceof EventDisconnectRequestedInfo) {
                EventDisconnectRequestedInfo eventDisconnectRequestedInfo = (EventDisconnectRequestedInfo) obj;
                return this.reason.equals(eventDisconnectRequestedInfo.reason) && this.shouldQuit == eventDisconnectRequestedInfo.shouldQuit;
            }
            return false;
        }
    }

    /* loaded from: classes2.dex */
    public static class EventMigrationCompletedInfo implements EventInfo {
        public final IpSecTransform inTransform;
        public final IpSecTransform outTransform;

        public EventMigrationCompletedInfo(IpSecTransform ipSecTransform, IpSecTransform ipSecTransform2) {
            Objects.requireNonNull(ipSecTransform);
            this.inTransform = ipSecTransform;
            Objects.requireNonNull(ipSecTransform2);
            this.outTransform = ipSecTransform2;
        }

        public int hashCode() {
            return Objects.hash(this.inTransform, this.outTransform);
        }

        public boolean equals(Object obj) {
            if (obj instanceof EventMigrationCompletedInfo) {
                EventMigrationCompletedInfo eventMigrationCompletedInfo = (EventMigrationCompletedInfo) obj;
                return Objects.equals(this.inTransform, eventMigrationCompletedInfo.inTransform) && Objects.equals(this.outTransform, eventMigrationCompletedInfo.outTransform);
            }
            return false;
        }
    }

    /* loaded from: classes2.dex */
    public static class EventIkeConnectionInfoChangedInfo implements EventInfo {
        public final IkeSessionConnectionInfo ikeConnectionInfo;

        public EventIkeConnectionInfoChangedInfo(IkeSessionConnectionInfo ikeSessionConnectionInfo) {
            this.ikeConnectionInfo = ikeSessionConnectionInfo;
        }

        public int hashCode() {
            return Objects.hash(this.ikeConnectionInfo);
        }

        public boolean equals(Object obj) {
            if (obj instanceof EventIkeConnectionInfoChangedInfo) {
                return Objects.equals(this.ikeConnectionInfo, ((EventIkeConnectionInfoChangedInfo) obj).ikeConnectionInfo);
            }
            return false;
        }
    }

    /* loaded from: classes2.dex */
    public static class EventDataStallSuspectedInfo implements EventInfo {
        public final Network network;

        public EventDataStallSuspectedInfo(Network network) {
            this.network = network;
        }

        public int hashCode() {
            return Objects.hash(this.network);
        }

        public boolean equals(Object obj) {
            if (obj instanceof EventDataStallSuspectedInfo) {
                return Objects.equals(this.network, ((EventDataStallSuspectedInfo) obj).network);
            }
            return false;
        }
    }

    public VcnGatewayConnection(VcnContext vcnContext, ParcelUuid parcelUuid, TelephonySubscriptionTracker.TelephonySubscriptionSnapshot telephonySubscriptionSnapshot, VcnGatewayConnectionConfig vcnGatewayConnectionConfig, Vcn.VcnGatewayStatusCallback vcnGatewayStatusCallback, boolean z) {
        this(vcnContext, parcelUuid, telephonySubscriptionSnapshot, vcnGatewayConnectionConfig, vcnGatewayStatusCallback, z, new Dependencies());
    }

    /* JADX WARN: Illegal instructions before constructor call */
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public VcnGatewayConnection(VcnContext vcnContext, ParcelUuid parcelUuid, TelephonySubscriptionTracker.TelephonySubscriptionSnapshot telephonySubscriptionSnapshot, VcnGatewayConnectionConfig vcnGatewayConnectionConfig, Vcn.VcnGatewayStatusCallback vcnGatewayStatusCallback, boolean z, Dependencies dependencies) {
        super(r5, vcnContext.getLooper());
        String str = TAG;
        Objects.requireNonNull(vcnContext, "Missing vcnContext");
        DisconnectedState disconnectedState = new DisconnectedState();
        this.mDisconnectedState = disconnectedState;
        DisconnectingState disconnectingState = new DisconnectingState();
        this.mDisconnectingState = disconnectingState;
        ConnectingState connectingState = new ConnectingState();
        this.mConnectingState = connectingState;
        ConnectedState connectedState = new ConnectedState();
        this.mConnectedState = connectedState;
        RetryTimeoutState retryTimeoutState = new RetryTimeoutState();
        this.mRetryTimeoutState = retryTimeoutState;
        this.mTunnelIface = null;
        this.mIsQuitting = new OneWayBoolean();
        this.mIsInSafeMode = false;
        this.mCurrentToken = -1;
        this.mFailedAttempts = 0;
        this.mVcnContext = vcnContext;
        Objects.requireNonNull(parcelUuid, "Missing subscriptionGroup");
        this.mSubscriptionGroup = parcelUuid;
        Objects.requireNonNull(vcnGatewayConnectionConfig, "Missing connectionConfig");
        this.mConnectionConfig = vcnGatewayConnectionConfig;
        Objects.requireNonNull(vcnGatewayStatusCallback, "Missing gatewayStatusCallback");
        this.mGatewayStatusCallback = vcnGatewayStatusCallback;
        this.mIsMobileDataEnabled = z;
        Objects.requireNonNull(dependencies, "Missing deps");
        this.mDeps = dependencies;
        Objects.requireNonNull(telephonySubscriptionSnapshot, "Missing snapshot");
        this.mLastSnapshot = telephonySubscriptionSnapshot;
        VcnUnderlyingNetworkControllerCallback vcnUnderlyingNetworkControllerCallback = new VcnUnderlyingNetworkControllerCallback();
        this.mUnderlyingNetworkControllerCallback = vcnUnderlyingNetworkControllerCallback;
        this.mWakeLock = dependencies.newWakeLock(vcnContext.getContext(), 1, str);
        this.mUnderlyingNetworkController = dependencies.newUnderlyingNetworkController(vcnContext, vcnGatewayConnectionConfig, parcelUuid, this.mLastSnapshot, vcnUnderlyingNetworkControllerCallback);
        this.mIpSecManager = (IpSecManager) vcnContext.getContext().getSystemService(IpSecManager.class);
        this.mConnectivityManager = (ConnectivityManager) vcnContext.getContext().getSystemService(ConnectivityManager.class);
        ConnectivityDiagnosticsManager connectivityDiagnosticsManager = (ConnectivityDiagnosticsManager) vcnContext.getContext().getSystemService(ConnectivityDiagnosticsManager.class);
        this.mConnectivityDiagnosticsManager = connectivityDiagnosticsManager;
        VcnConnectivityDiagnosticsCallback vcnConnectivityDiagnosticsCallback = new VcnConnectivityDiagnosticsCallback();
        this.mConnectivityDiagnosticsCallback = vcnConnectivityDiagnosticsCallback;
        if (vcnGatewayConnectionConfig.hasGatewayOption(0)) {
            connectivityDiagnosticsManager.registerConnectivityDiagnosticsCallback(new NetworkRequest.Builder().addTransportType(0).build(), new HandlerExecutor(new Handler(vcnContext.getLooper())), vcnConnectivityDiagnosticsCallback);
        }
        addState(disconnectedState);
        addState(disconnectingState);
        addState(connectingState);
        addState(connectedState);
        addState(retryTimeoutState);
        setInitialState(disconnectedState);
        setDbg(false);
        start();
    }

    public boolean isInSafeMode() {
        this.mVcnContext.ensureRunningOnLooperThread();
        return this.mIsInSafeMode;
    }

    public void teardownAsynchronously() {
        logDbg("Triggering async teardown");
        sendDisconnectRequestedAndAcquireWakelock("teardown() called on VcnTunnel", true);
    }

    public void onQuitting() {
        logInfo("Quitting VcnGatewayConnection");
        if (this.mNetworkAgent != null) {
            logWtf("NetworkAgent was non-null in onQuitting");
            this.mNetworkAgent.unregister();
            this.mNetworkAgent = null;
        }
        if (this.mIkeSession != null) {
            logWtf("IkeSession was non-null in onQuitting");
            this.mIkeSession.kill();
            this.mIkeSession = null;
        }
        IpSecManager.IpSecTunnelInterface ipSecTunnelInterface = this.mTunnelIface;
        if (ipSecTunnelInterface != null) {
            ipSecTunnelInterface.close();
        }
        releaseWakeLock();
        cancelTeardownTimeoutAlarm();
        cancelDisconnectRequestAlarm();
        cancelRetryTimeoutAlarm();
        cancelSafeModeAlarm();
        this.mUnderlyingNetworkController.teardown();
        this.mGatewayStatusCallback.onQuit();
        this.mConnectivityDiagnosticsManager.unregisterConnectivityDiagnosticsCallback(this.mConnectivityDiagnosticsCallback);
    }

    public void updateSubscriptionSnapshot(TelephonySubscriptionTracker.TelephonySubscriptionSnapshot telephonySubscriptionSnapshot) {
        Objects.requireNonNull(telephonySubscriptionSnapshot, "Missing snapshot");
        this.mVcnContext.ensureRunningOnLooperThread();
        this.mLastSnapshot = telephonySubscriptionSnapshot;
        this.mUnderlyingNetworkController.updateSubscriptionSnapshot(telephonySubscriptionSnapshot);
        sendMessageAndAcquireWakeLock(9, Integer.MIN_VALUE);
    }

    /* loaded from: classes2.dex */
    public class VcnConnectivityDiagnosticsCallback extends ConnectivityDiagnosticsManager.ConnectivityDiagnosticsCallback {
        public VcnConnectivityDiagnosticsCallback() {
        }

        @Override // android.net.ConnectivityDiagnosticsManager.ConnectivityDiagnosticsCallback
        public void onDataStallSuspected(ConnectivityDiagnosticsManager.DataStallReport dataStallReport) {
            VcnGatewayConnection.this.mVcnContext.ensureRunningOnLooperThread();
            Network network = dataStallReport.getNetwork();
            VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
            vcnGatewayConnection.logInfo("Data stall suspected on " + network);
            VcnGatewayConnection.this.sendMessageAndAcquireWakeLock(13, Integer.MIN_VALUE, new EventDataStallSuspectedInfo(network));
        }
    }

    /* loaded from: classes2.dex */
    public class VcnUnderlyingNetworkControllerCallback implements UnderlyingNetworkController.UnderlyingNetworkControllerCallback {
        public VcnUnderlyingNetworkControllerCallback() {
        }

        @Override // com.android.server.vcn.routeselection.UnderlyingNetworkController.UnderlyingNetworkControllerCallback
        public void onSelectedUnderlyingNetworkChanged(UnderlyingNetworkRecord underlyingNetworkRecord) {
            VcnGatewayConnection.this.mVcnContext.ensureRunningOnLooperThread();
            VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
            StringBuilder sb = new StringBuilder();
            sb.append("Selected underlying network changed: ");
            sb.append(underlyingNetworkRecord == null ? null : underlyingNetworkRecord.network);
            vcnGatewayConnection.logInfo(sb.toString());
            if (underlyingNetworkRecord == null) {
                if (VcnGatewayConnection.this.mDeps.isAirplaneModeOn(VcnGatewayConnection.this.mVcnContext)) {
                    VcnGatewayConnection.this.sendMessageAndAcquireWakeLock(1, Integer.MIN_VALUE, new EventUnderlyingNetworkChangedInfo(null));
                    VcnGatewayConnection.this.sendDisconnectRequestedAndAcquireWakelock("Underlying Network lost", false);
                    return;
                }
                VcnGatewayConnection.this.setDisconnectRequestAlarm();
            } else {
                VcnGatewayConnection.this.cancelDisconnectRequestAlarm();
            }
            VcnGatewayConnection.this.sendMessageAndAcquireWakeLock(1, Integer.MIN_VALUE, new EventUnderlyingNetworkChangedInfo(underlyingNetworkRecord));
        }
    }

    public final void acquireWakeLock() {
        this.mVcnContext.ensureRunningOnLooperThread();
        if (this.mIsQuitting.getValue()) {
            return;
        }
        this.mWakeLock.acquire();
        logVdbg("Wakelock acquired: " + this.mWakeLock);
    }

    public final void releaseWakeLock() {
        this.mVcnContext.ensureRunningOnLooperThread();
        this.mWakeLock.release();
        logVdbg("Wakelock released: " + this.mWakeLock);
    }

    public final void maybeReleaseWakeLock() {
        Handler handler = getHandler();
        if (handler == null || !handler.hasMessagesOrCallbacks()) {
            releaseWakeLock();
        }
    }

    public void sendMessage(int i) {
        logWtf("sendMessage should not be used in VcnGatewayConnection. See sendMessageAndAcquireWakeLock()");
        super.sendMessage(i);
    }

    public void sendMessage(int i, Object obj) {
        logWtf("sendMessage should not be used in VcnGatewayConnection. See sendMessageAndAcquireWakeLock()");
        super.sendMessage(i, obj);
    }

    public void sendMessage(int i, int i2) {
        logWtf("sendMessage should not be used in VcnGatewayConnection. See sendMessageAndAcquireWakeLock()");
        super.sendMessage(i, i2);
    }

    public void sendMessage(int i, int i2, int i3) {
        logWtf("sendMessage should not be used in VcnGatewayConnection. See sendMessageAndAcquireWakeLock()");
        super.sendMessage(i, i2, i3);
    }

    public void sendMessage(int i, int i2, int i3, Object obj) {
        logWtf("sendMessage should not be used in VcnGatewayConnection. See sendMessageAndAcquireWakeLock()");
        super.sendMessage(i, i2, i3, obj);
    }

    public void sendMessage(Message message) {
        logWtf("sendMessage should not be used in VcnGatewayConnection. See sendMessageAndAcquireWakeLock()");
        super.sendMessage(message);
    }

    public final void sendMessageAndAcquireWakeLock(int i, int i2) {
        acquireWakeLock();
        super.sendMessage(i, i2);
    }

    public final void sendMessageAndAcquireWakeLock(int i, int i2, EventInfo eventInfo) {
        acquireWakeLock();
        super.sendMessage(i, i2, Integer.MIN_VALUE, eventInfo);
    }

    /* renamed from: sendMessageAndAcquireWakeLock */
    public final void lambda$createScheduledAlarm$0(Message message) {
        acquireWakeLock();
        super.sendMessage(message);
    }

    public final void removeEqualMessages(int i) {
        removeEqualMessages(i, null);
    }

    public final void removeEqualMessages(int i, Object obj) {
        Handler handler = getHandler();
        if (handler != null) {
            handler.removeEqualMessages(i, obj);
        }
        maybeReleaseWakeLock();
    }

    public final WakeupMessage createScheduledAlarm(String str, final Message message, long j) {
        Handler handler = getHandler();
        if (handler == null) {
            logWarn("Attempted to schedule alarm after StateMachine has quit", new IllegalStateException());
            return null;
        }
        WakeupMessage newWakeupMessage = this.mDeps.newWakeupMessage(this.mVcnContext, handler, str, new Runnable() { // from class: com.android.server.vcn.VcnGatewayConnection$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                VcnGatewayConnection.this.lambda$createScheduledAlarm$0(message);
            }
        });
        newWakeupMessage.schedule(this.mDeps.getElapsedRealTime() + j);
        return newWakeupMessage;
    }

    public final void setTeardownTimeoutAlarm() {
        logVdbg("Setting teardown timeout alarm; mCurrentToken: " + this.mCurrentToken);
        if (this.mTeardownTimeoutAlarm != null) {
            logWtf("mTeardownTimeoutAlarm should be null before being set; mCurrentToken: " + this.mCurrentToken);
        }
        this.mTeardownTimeoutAlarm = createScheduledAlarm(TEARDOWN_TIMEOUT_ALARM, obtainMessage(8, this.mCurrentToken), TimeUnit.SECONDS.toMillis(5L));
    }

    public final void cancelTeardownTimeoutAlarm() {
        logVdbg("Cancelling teardown timeout alarm; mCurrentToken: " + this.mCurrentToken);
        WakeupMessage wakeupMessage = this.mTeardownTimeoutAlarm;
        if (wakeupMessage != null) {
            wakeupMessage.cancel();
            this.mTeardownTimeoutAlarm = null;
        }
        removeEqualMessages(8);
    }

    public final void setDisconnectRequestAlarm() {
        logVdbg("Setting alarm to disconnect due to underlying network loss; mCurrentToken: " + this.mCurrentToken);
        if (this.mDisconnectRequestAlarm != null) {
            return;
        }
        this.mDisconnectRequestAlarm = createScheduledAlarm(DISCONNECT_REQUEST_ALARM, obtainMessage(7, Integer.MIN_VALUE, 0, new EventDisconnectRequestedInfo("Underlying Network lost", false)), TimeUnit.SECONDS.toMillis(30L));
    }

    public final void cancelDisconnectRequestAlarm() {
        logVdbg("Cancelling alarm to disconnect due to underlying network loss; mCurrentToken: " + this.mCurrentToken);
        WakeupMessage wakeupMessage = this.mDisconnectRequestAlarm;
        if (wakeupMessage != null) {
            wakeupMessage.cancel();
            this.mDisconnectRequestAlarm = null;
        }
        removeEqualMessages(7, new EventDisconnectRequestedInfo("Underlying Network lost", false));
    }

    public final void setRetryTimeoutAlarm(long j) {
        logVdbg("Setting retry alarm; mCurrentToken: " + this.mCurrentToken);
        if (this.mRetryTimeoutAlarm != null) {
            logWtf("mRetryTimeoutAlarm should be null before being set; mCurrentToken: " + this.mCurrentToken);
        }
        this.mRetryTimeoutAlarm = createScheduledAlarm(RETRY_TIMEOUT_ALARM, obtainMessage(2, this.mCurrentToken), j);
    }

    public final void cancelRetryTimeoutAlarm() {
        logVdbg("Cancel retry alarm; mCurrentToken: " + this.mCurrentToken);
        WakeupMessage wakeupMessage = this.mRetryTimeoutAlarm;
        if (wakeupMessage != null) {
            wakeupMessage.cancel();
            this.mRetryTimeoutAlarm = null;
        }
        removeEqualMessages(2);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public void setSafeModeAlarm() {
        long millis;
        logVdbg("Setting safe mode alarm; mCurrentToken: " + this.mCurrentToken);
        if (this.mSafeModeTimeoutAlarm != null) {
            return;
        }
        Message obtainMessage = obtainMessage(10, Integer.MIN_VALUE);
        String str = SAFEMODE_TIMEOUT_ALARM;
        if (this.mVcnContext.isInTestMode()) {
            millis = TimeUnit.SECONDS.toMillis(10L);
        } else {
            millis = TimeUnit.SECONDS.toMillis(30L);
        }
        this.mSafeModeTimeoutAlarm = createScheduledAlarm(str, obtainMessage, millis);
    }

    public final void cancelSafeModeAlarm() {
        logVdbg("Cancel safe mode alarm; mCurrentToken: " + this.mCurrentToken);
        WakeupMessage wakeupMessage = this.mSafeModeTimeoutAlarm;
        if (wakeupMessage != null) {
            wakeupMessage.cancel();
            this.mSafeModeTimeoutAlarm = null;
        }
        removeEqualMessages(10);
    }

    public final void sessionLostWithoutCallback(int i, Exception exc) {
        sendMessageAndAcquireWakeLock(3, i, new EventSessionLostInfo(exc));
    }

    public final void sessionLost(int i, Exception exc) {
        if (exc != null) {
            Vcn.VcnGatewayStatusCallback vcnGatewayStatusCallback = this.mGatewayStatusCallback;
            String gatewayConnectionName = this.mConnectionConfig.getGatewayConnectionName();
            String name = RuntimeException.class.getName();
            vcnGatewayStatusCallback.onGatewayConnectionError(gatewayConnectionName, 0, name, "Received " + exc.getClass().getSimpleName() + " with message: " + exc.getMessage());
        }
        sessionLostWithoutCallback(i, exc);
    }

    public static boolean isIkeAuthFailure(Exception exc) {
        return (exc instanceof IkeProtocolException) && ((IkeProtocolException) exc).getErrorType() == 24;
    }

    public final void notifyStatusCallbackForSessionClosed(Exception exc) {
        String name;
        String str;
        int i;
        if (isIkeAuthFailure(exc)) {
            name = exc.getClass().getName();
            str = exc.getMessage();
            i = 1;
        } else if ((exc instanceof IkeInternalException) && (exc.getCause() instanceof IOException)) {
            name = IOException.class.getName();
            str = exc.getCause().getMessage();
            i = 2;
        } else {
            name = RuntimeException.class.getName();
            str = "Received " + exc.getClass().getSimpleName() + " with message: " + exc.getMessage();
            i = 0;
        }
        logDbg("Encountered error; code=" + i + ", exceptionClass=" + name + ", exceptionMessage=" + str);
        this.mGatewayStatusCallback.onGatewayConnectionError(this.mConnectionConfig.getGatewayConnectionName(), i, name, str);
    }

    public final void ikeConnectionInfoChanged(int i, IkeSessionConnectionInfo ikeSessionConnectionInfo) {
        sendMessageAndAcquireWakeLock(12, i, new EventIkeConnectionInfoChangedInfo(ikeSessionConnectionInfo));
    }

    public final void sessionClosed(int i, Exception exc) {
        if (exc != null) {
            notifyStatusCallbackForSessionClosed(exc);
        }
        sessionLostWithoutCallback(i, exc);
        sendMessageAndAcquireWakeLock(4, i);
    }

    public final void migrationCompleted(int i, IpSecTransform ipSecTransform, IpSecTransform ipSecTransform2) {
        sendMessageAndAcquireWakeLock(11, i, new EventMigrationCompletedInfo(ipSecTransform, ipSecTransform2));
    }

    public final void childTransformCreated(int i, IpSecTransform ipSecTransform, int i2) {
        sendMessageAndAcquireWakeLock(5, i, new EventTransformCreatedInfo(i2, ipSecTransform));
    }

    public final void childOpened(int i, VcnChildSessionConfiguration vcnChildSessionConfiguration) {
        sendMessageAndAcquireWakeLock(6, i, new EventSetupCompletedInfo(vcnChildSessionConfiguration));
    }

    /* loaded from: classes2.dex */
    public abstract class BaseState extends State {
        public abstract void enterState() throws Exception;

        public void exitState() throws Exception {
        }

        public boolean isValidToken(int i) {
            return true;
        }

        public abstract void processStateMsg(Message message) throws Exception;

        public BaseState() {
        }

        public void enter() {
            try {
                enterState();
            } catch (Exception e) {
                VcnGatewayConnection.this.logWtf("Uncaught exception", e);
                VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
                vcnGatewayConnection.sendDisconnectRequestedAndAcquireWakelock("Uncaught exception: " + e.toString(), true);
            }
        }

        public final boolean processMessage(Message message) {
            int i = message.arg1;
            if (!isValidToken(i)) {
                VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
                vcnGatewayConnection.logDbg("Message called with obsolete token: " + i + "; what: " + message.what);
                return true;
            }
            try {
                processStateMsg(message);
            } catch (Exception e) {
                VcnGatewayConnection.this.logWtf("Uncaught exception", e);
                VcnGatewayConnection vcnGatewayConnection2 = VcnGatewayConnection.this;
                vcnGatewayConnection2.sendDisconnectRequestedAndAcquireWakelock("Uncaught exception: " + e.toString(), true);
            }
            VcnGatewayConnection.this.maybeReleaseWakeLock();
            return true;
        }

        public void exit() {
            try {
                exitState();
            } catch (Exception e) {
                VcnGatewayConnection.this.logWtf("Uncaught exception", e);
                VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
                vcnGatewayConnection.sendDisconnectRequestedAndAcquireWakelock("Uncaught exception: " + e.toString(), true);
            }
        }

        public void logUnhandledMessage(Message message) {
            int i = message.what;
            switch (i) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                    logUnexpectedEvent(i);
                    return;
                default:
                    logWtfUnknownEvent(i);
                    return;
            }
        }

        public void teardownNetwork() {
            if (VcnGatewayConnection.this.mNetworkAgent != null) {
                VcnGatewayConnection.this.mNetworkAgent.unregister();
                VcnGatewayConnection.this.mNetworkAgent = null;
            }
        }

        public void handleDisconnectRequested(EventDisconnectRequestedInfo eventDisconnectRequestedInfo) {
            VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
            vcnGatewayConnection.logInfo("Tearing down. Cause: " + eventDisconnectRequestedInfo.reason + "; quitting = " + eventDisconnectRequestedInfo.shouldQuit);
            if (eventDisconnectRequestedInfo.shouldQuit) {
                VcnGatewayConnection.this.mIsQuitting.setTrue();
            }
            teardownNetwork();
            if (VcnGatewayConnection.this.mIkeSession == null) {
                VcnGatewayConnection vcnGatewayConnection2 = VcnGatewayConnection.this;
                vcnGatewayConnection2.transitionTo(vcnGatewayConnection2.mDisconnectedState);
                return;
            }
            VcnGatewayConnection vcnGatewayConnection3 = VcnGatewayConnection.this;
            vcnGatewayConnection3.transitionTo(vcnGatewayConnection3.mDisconnectingState);
        }

        public void handleSafeModeTimeoutExceeded() {
            VcnGatewayConnection.this.mSafeModeTimeoutAlarm = null;
            VcnGatewayConnection.this.logInfo("Entering safe mode after timeout exceeded");
            teardownNetwork();
            VcnGatewayConnection.this.mIsInSafeMode = true;
            VcnGatewayConnection.this.mGatewayStatusCallback.onSafeModeStatusChanged();
        }

        public void logUnexpectedEvent(int i) {
            VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
            vcnGatewayConnection.logVdbg("Unexpected event code " + i + " in state " + getClass().getSimpleName());
        }

        public void logWtfUnknownEvent(int i) {
            VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
            vcnGatewayConnection.logWtf("Unknown event code " + i + " in state " + getClass().getSimpleName());
        }
    }

    /* loaded from: classes2.dex */
    public class DisconnectedState extends BaseState {
        public DisconnectedState() {
            super();
        }

        @Override // com.android.server.vcn.VcnGatewayConnection.BaseState
        public void enterState() {
            if (VcnGatewayConnection.this.mIsQuitting.getValue()) {
                VcnGatewayConnection.this.quitNow();
            }
            if (VcnGatewayConnection.this.mIkeSession != null || VcnGatewayConnection.this.mNetworkAgent != null) {
                VcnGatewayConnection.this.logWtf("Active IKE Session or NetworkAgent in DisconnectedState");
            }
            VcnGatewayConnection.this.cancelSafeModeAlarm();
        }

        @Override // com.android.server.vcn.VcnGatewayConnection.BaseState
        public void processStateMsg(Message message) {
            int i = message.what;
            if (i == 1) {
                VcnGatewayConnection.this.mUnderlying = ((EventUnderlyingNetworkChangedInfo) message.obj).newUnderlying;
                if (VcnGatewayConnection.this.mUnderlying != null) {
                    VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
                    vcnGatewayConnection.transitionTo(vcnGatewayConnection.mConnectingState);
                }
            } else if (i == 7) {
                if (((EventDisconnectRequestedInfo) message.obj).shouldQuit) {
                    VcnGatewayConnection.this.mIsQuitting.setTrue();
                    VcnGatewayConnection.this.quitNow();
                }
            } else {
                logUnhandledMessage(message);
            }
        }

        @Override // com.android.server.vcn.VcnGatewayConnection.BaseState
        public void exitState() {
            VcnGatewayConnection.this.setSafeModeAlarm();
        }
    }

    /* loaded from: classes2.dex */
    public abstract class ActiveBaseState extends BaseState {
        public ActiveBaseState() {
            super();
        }

        @Override // com.android.server.vcn.VcnGatewayConnection.BaseState
        public boolean isValidToken(int i) {
            return i == Integer.MIN_VALUE || i == VcnGatewayConnection.this.mCurrentToken;
        }
    }

    /* loaded from: classes2.dex */
    public class DisconnectingState extends ActiveBaseState {
        public boolean mSkipRetryTimeout;

        public DisconnectingState() {
            super();
            this.mSkipRetryTimeout = false;
        }

        public void setSkipRetryTimeout(boolean z) {
            this.mSkipRetryTimeout = z;
        }

        @Override // com.android.server.vcn.VcnGatewayConnection.BaseState
        public void enterState() throws Exception {
            if (VcnGatewayConnection.this.mIkeSession == null) {
                VcnGatewayConnection.this.logWtf("IKE session was already closed when entering Disconnecting state.");
                VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
                vcnGatewayConnection.sendMessageAndAcquireWakeLock(4, vcnGatewayConnection.mCurrentToken);
            } else if (VcnGatewayConnection.this.mUnderlying == null) {
                VcnGatewayConnection.this.mIkeSession.kill();
            } else {
                VcnGatewayConnection.this.mIkeSession.close();
                VcnGatewayConnection.this.setTeardownTimeoutAlarm();
            }
        }

        @Override // com.android.server.vcn.VcnGatewayConnection.BaseState
        public void processStateMsg(Message message) {
            int i = message.what;
            if (i == 1) {
                VcnGatewayConnection.this.mUnderlying = ((EventUnderlyingNetworkChangedInfo) message.obj).newUnderlying;
                if (VcnGatewayConnection.this.mUnderlying != null) {
                    return;
                }
            } else if (i == 4) {
                VcnGatewayConnection.this.mIkeSession = null;
                if (!VcnGatewayConnection.this.mIsQuitting.getValue() && VcnGatewayConnection.this.mUnderlying != null) {
                    VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
                    vcnGatewayConnection.transitionTo(this.mSkipRetryTimeout ? vcnGatewayConnection.mConnectingState : vcnGatewayConnection.mRetryTimeoutState);
                    return;
                }
                teardownNetwork();
                VcnGatewayConnection vcnGatewayConnection2 = VcnGatewayConnection.this;
                vcnGatewayConnection2.transitionTo(vcnGatewayConnection2.mDisconnectedState);
                return;
            } else if (i == 10) {
                handleSafeModeTimeoutExceeded();
                return;
            } else if (i == 7) {
                EventDisconnectRequestedInfo eventDisconnectRequestedInfo = (EventDisconnectRequestedInfo) message.obj;
                if (eventDisconnectRequestedInfo.shouldQuit) {
                    VcnGatewayConnection.this.mIsQuitting.setTrue();
                }
                teardownNetwork();
                if (eventDisconnectRequestedInfo.reason.equals("Underlying Network lost")) {
                    VcnGatewayConnection.this.mIkeSession.kill();
                    return;
                }
                return;
            } else if (i != 8) {
                logUnhandledMessage(message);
                return;
            }
            VcnGatewayConnection.this.mIkeSession.kill();
        }

        @Override // com.android.server.vcn.VcnGatewayConnection.BaseState
        public void exitState() throws Exception {
            this.mSkipRetryTimeout = false;
            VcnGatewayConnection.this.cancelTeardownTimeoutAlarm();
        }
    }

    /* loaded from: classes2.dex */
    public class ConnectingState extends ActiveBaseState {
        public ConnectingState() {
            super();
        }

        @Override // com.android.server.vcn.VcnGatewayConnection.BaseState
        public void enterState() {
            if (VcnGatewayConnection.this.mIkeSession != null) {
                VcnGatewayConnection.this.logWtf("ConnectingState entered with active session");
                VcnGatewayConnection.this.mIkeSession.kill();
                VcnGatewayConnection.this.mIkeSession = null;
            }
            VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
            vcnGatewayConnection.mIkeSession = vcnGatewayConnection.buildIkeSession(vcnGatewayConnection.mUnderlying.network);
        }

        @Override // com.android.server.vcn.VcnGatewayConnection.BaseState
        public void processStateMsg(Message message) {
            int i = message.what;
            if (i != 1) {
                if (i != 10) {
                    if (i != 12) {
                        if (i != 3) {
                            if (i == 4) {
                                VcnGatewayConnection.this.deferMessage(message);
                                VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
                                vcnGatewayConnection.transitionTo(vcnGatewayConnection.mDisconnectingState);
                                return;
                            } else if (i != 5 && i != 6) {
                                if (i == 7) {
                                    handleDisconnectRequested((EventDisconnectRequestedInfo) message.obj);
                                    return;
                                } else {
                                    logUnhandledMessage(message);
                                    return;
                                }
                            }
                        }
                    }
                    VcnGatewayConnection.this.deferMessage(message);
                    VcnGatewayConnection vcnGatewayConnection2 = VcnGatewayConnection.this;
                    vcnGatewayConnection2.transitionTo(vcnGatewayConnection2.mConnectedState);
                    return;
                }
                handleSafeModeTimeoutExceeded();
                return;
            }
            UnderlyingNetworkRecord underlyingNetworkRecord = VcnGatewayConnection.this.mUnderlying;
            VcnGatewayConnection.this.mUnderlying = ((EventUnderlyingNetworkChangedInfo) message.obj).newUnderlying;
            if (underlyingNetworkRecord == null) {
                VcnGatewayConnection.this.logWtf("Old underlying network was null in connected state. Bug?");
            }
            if (VcnGatewayConnection.this.mUnderlying == null) {
                VcnGatewayConnection vcnGatewayConnection3 = VcnGatewayConnection.this;
                vcnGatewayConnection3.transitionTo(vcnGatewayConnection3.mDisconnectingState);
                return;
            } else if (underlyingNetworkRecord != null && VcnGatewayConnection.this.mUnderlying.network.equals(underlyingNetworkRecord.network)) {
                return;
            } else {
                VcnGatewayConnection.this.mDisconnectingState.setSkipRetryTimeout(true);
            }
            VcnGatewayConnection vcnGatewayConnection4 = VcnGatewayConnection.this;
            vcnGatewayConnection4.transitionTo(vcnGatewayConnection4.mDisconnectingState);
        }
    }

    /* loaded from: classes2.dex */
    public abstract class ConnectedStateBase extends ActiveBaseState {
        public ConnectedStateBase() {
            super();
        }

        public void updateNetworkAgent(IpSecManager.IpSecTunnelInterface ipSecTunnelInterface, VcnNetworkAgent vcnNetworkAgent, VcnChildSessionConfiguration vcnChildSessionConfiguration, IkeSessionConnectionInfo ikeSessionConnectionInfo) {
            NetworkCapabilities buildNetworkCapabilities = VcnGatewayConnection.buildNetworkCapabilities(VcnGatewayConnection.this.mConnectionConfig, VcnGatewayConnection.this.mUnderlying, VcnGatewayConnection.this.mIsMobileDataEnabled);
            VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
            LinkProperties buildConnectedLinkProperties = vcnGatewayConnection.buildConnectedLinkProperties(vcnGatewayConnection.mConnectionConfig, ipSecTunnelInterface, vcnChildSessionConfiguration, VcnGatewayConnection.this.mUnderlying, ikeSessionConnectionInfo);
            vcnNetworkAgent.sendNetworkCapabilities(buildNetworkCapabilities);
            vcnNetworkAgent.sendLinkProperties(buildConnectedLinkProperties);
            vcnNetworkAgent.setUnderlyingNetworks(VcnGatewayConnection.this.mUnderlying == null ? null : Collections.singletonList(VcnGatewayConnection.this.mUnderlying.network));
        }

        public VcnNetworkAgent buildNetworkAgent(IpSecManager.IpSecTunnelInterface ipSecTunnelInterface, VcnChildSessionConfiguration vcnChildSessionConfiguration, IkeSessionConnectionInfo ikeSessionConnectionInfo) {
            NetworkCapabilities buildNetworkCapabilities = VcnGatewayConnection.buildNetworkCapabilities(VcnGatewayConnection.this.mConnectionConfig, VcnGatewayConnection.this.mUnderlying, VcnGatewayConnection.this.mIsMobileDataEnabled);
            VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
            VcnNetworkAgent newNetworkAgent = VcnGatewayConnection.this.mDeps.newNetworkAgent(VcnGatewayConnection.this.mVcnContext, VcnGatewayConnection.TAG, buildNetworkCapabilities, vcnGatewayConnection.buildConnectedLinkProperties(vcnGatewayConnection.mConnectionConfig, ipSecTunnelInterface, vcnChildSessionConfiguration, VcnGatewayConnection.this.mUnderlying, ikeSessionConnectionInfo), Vcn.getNetworkScore(), new NetworkAgentConfig.Builder().setLegacyType(0).setLegacyTypeName(VcnGatewayConnection.NETWORK_INFO_NETWORK_TYPE_STRING).setLegacySubType(0).setLegacySubTypeName(TelephonyManager.getNetworkTypeName(0)).setLegacyExtraInfo(VcnGatewayConnection.NETWORK_INFO_EXTRA_INFO).build(), VcnGatewayConnection.this.mVcnContext.getVcnNetworkProvider(), new Consumer() { // from class: com.android.server.vcn.VcnGatewayConnection$ConnectedStateBase$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    VcnGatewayConnection.ConnectedStateBase.this.lambda$buildNetworkAgent$0((VcnGatewayConnection.VcnNetworkAgent) obj);
                }
            }, new Consumer() { // from class: com.android.server.vcn.VcnGatewayConnection$ConnectedStateBase$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    VcnGatewayConnection.ConnectedStateBase.this.lambda$buildNetworkAgent$1((Integer) obj);
                }
            });
            newNetworkAgent.register();
            newNetworkAgent.markConnected();
            return newNetworkAgent;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$buildNetworkAgent$0(VcnNetworkAgent vcnNetworkAgent) {
            if (VcnGatewayConnection.this.mNetworkAgent != vcnNetworkAgent) {
                VcnGatewayConnection.this.logDbg("unwanted() called on stale NetworkAgent");
                return;
            }
            VcnGatewayConnection.this.logInfo("NetworkAgent was unwanted");
            VcnGatewayConnection.this.teardownAsynchronously();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$buildNetworkAgent$1(Integer num) {
            if (VcnGatewayConnection.this.mIsQuitting.getValue()) {
                return;
            }
            int intValue = num.intValue();
            if (intValue == 1) {
                clearFailedAttemptCounterAndSafeModeAlarm();
            } else if (intValue == 2) {
                if (VcnGatewayConnection.this.mUnderlying != null) {
                    VcnGatewayConnection.this.mConnectivityManager.reportNetworkConnectivity(VcnGatewayConnection.this.mUnderlying.network, false);
                }
                VcnGatewayConnection.this.setSafeModeAlarm();
            } else {
                VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
                vcnGatewayConnection.logWtf("Unknown validation status " + num + "; ignoring");
            }
        }

        public void clearFailedAttemptCounterAndSafeModeAlarm() {
            VcnGatewayConnection.this.mVcnContext.ensureRunningOnLooperThread();
            VcnGatewayConnection.this.mFailedAttempts = 0;
            VcnGatewayConnection.this.cancelSafeModeAlarm();
            VcnGatewayConnection.this.mIsInSafeMode = false;
            VcnGatewayConnection.this.mGatewayStatusCallback.onSafeModeStatusChanged();
        }

        public void applyTransform(int i, IpSecManager.IpSecTunnelInterface ipSecTunnelInterface, Network network, IpSecTransform ipSecTransform, int i2) {
            if (i2 != 0 && i2 != 1) {
                VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
                vcnGatewayConnection.logWtf("Applying transform for unexpected direction: " + i2);
            }
            try {
                ipSecTunnelInterface.setUnderlyingNetwork(network);
                VcnGatewayConnection.this.mIpSecManager.applyTunnelModeTransform(ipSecTunnelInterface, i2, ipSecTransform);
                Set allExposedCapabilities = VcnGatewayConnection.this.mConnectionConfig.getAllExposedCapabilities();
                if (i2 == 0 && allExposedCapabilities.contains(2)) {
                    VcnGatewayConnection.this.mIpSecManager.applyTunnelModeTransform(ipSecTunnelInterface, 2, ipSecTransform);
                }
            } catch (IOException e) {
                VcnGatewayConnection vcnGatewayConnection2 = VcnGatewayConnection.this;
                vcnGatewayConnection2.logInfo("Transform application failed for network " + i, e);
                VcnGatewayConnection.this.sessionLost(i, e);
            }
        }

        public void setupInterface(int i, IpSecManager.IpSecTunnelInterface ipSecTunnelInterface, VcnChildSessionConfiguration vcnChildSessionConfiguration, VcnChildSessionConfiguration vcnChildSessionConfiguration2) {
            try {
                ArraySet arraySet = new ArraySet(vcnChildSessionConfiguration.getInternalAddresses());
                ArraySet arraySet2 = new ArraySet();
                if (vcnChildSessionConfiguration2 != null) {
                    arraySet2.addAll(vcnChildSessionConfiguration2.getInternalAddresses());
                }
                ArraySet<LinkAddress> arraySet3 = new ArraySet();
                arraySet3.addAll((Collection) arraySet);
                arraySet3.removeAll((Collection<?>) arraySet2);
                ArraySet<LinkAddress> arraySet4 = new ArraySet();
                arraySet4.addAll((Collection) arraySet2);
                arraySet4.removeAll((Collection<?>) arraySet);
                for (LinkAddress linkAddress : arraySet3) {
                    ipSecTunnelInterface.addAddress(linkAddress.getAddress(), linkAddress.getPrefixLength());
                }
                for (LinkAddress linkAddress2 : arraySet4) {
                    ipSecTunnelInterface.removeAddress(linkAddress2.getAddress(), linkAddress2.getPrefixLength());
                }
            } catch (IOException e) {
                VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
                vcnGatewayConnection.logInfo("Adding address to tunnel failed for token " + i, e);
                VcnGatewayConnection.this.sessionLost(i, e);
            }
        }
    }

    /* loaded from: classes2.dex */
    public class ConnectedState extends ConnectedStateBase {
        public ConnectedState() {
            super();
        }

        @Override // com.android.server.vcn.VcnGatewayConnection.BaseState
        public void enterState() throws Exception {
            if (VcnGatewayConnection.this.mTunnelIface == null) {
                try {
                    VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
                    IpSecManager ipSecManager = vcnGatewayConnection.mIpSecManager;
                    InetAddress inetAddress = VcnGatewayConnection.DUMMY_ADDR;
                    vcnGatewayConnection.mTunnelIface = ipSecManager.createIpSecTunnelInterface(inetAddress, inetAddress, VcnGatewayConnection.this.mUnderlying.network);
                } catch (IpSecManager.ResourceUnavailableException | IOException unused) {
                    VcnGatewayConnection.this.teardownAsynchronously();
                }
            }
        }

        @Override // com.android.server.vcn.VcnGatewayConnection.BaseState
        public void processStateMsg(Message message) {
            switch (message.what) {
                case 1:
                    handleUnderlyingNetworkChanged(message);
                    return;
                case 2:
                case 8:
                case 9:
                default:
                    logUnhandledMessage(message);
                    return;
                case 3:
                    VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
                    vcnGatewayConnection.transitionTo(vcnGatewayConnection.mDisconnectingState);
                    return;
                case 4:
                    VcnGatewayConnection.this.deferMessage(message);
                    VcnGatewayConnection vcnGatewayConnection2 = VcnGatewayConnection.this;
                    vcnGatewayConnection2.transitionTo(vcnGatewayConnection2.mDisconnectingState);
                    return;
                case 5:
                    EventTransformCreatedInfo eventTransformCreatedInfo = (EventTransformCreatedInfo) message.obj;
                    applyTransform(VcnGatewayConnection.this.mCurrentToken, VcnGatewayConnection.this.mTunnelIface, VcnGatewayConnection.this.mUnderlying.network, eventTransformCreatedInfo.transform, eventTransformCreatedInfo.direction);
                    return;
                case 6:
                    VcnChildSessionConfiguration vcnChildSessionConfiguration = VcnGatewayConnection.this.mChildConfig;
                    VcnGatewayConnection.this.mChildConfig = ((EventSetupCompletedInfo) message.obj).childSessionConfig;
                    setupInterfaceAndNetworkAgent(VcnGatewayConnection.this.mCurrentToken, VcnGatewayConnection.this.mTunnelIface, VcnGatewayConnection.this.mChildConfig, vcnChildSessionConfiguration, VcnGatewayConnection.this.mIkeConnectionInfo);
                    int parallelTunnelCount = VcnGatewayConnection.this.mDeps.getParallelTunnelCount(VcnGatewayConnection.this.mLastSnapshot, VcnGatewayConnection.this.mSubscriptionGroup);
                    VcnGatewayConnection vcnGatewayConnection3 = VcnGatewayConnection.this;
                    vcnGatewayConnection3.logInfo("Parallel tunnel count: " + parallelTunnelCount);
                    for (int i = 0; i < parallelTunnelCount - 1; i++) {
                        VcnIkeSession vcnIkeSession = VcnGatewayConnection.this.mIkeSession;
                        ChildSessionParams buildOpportunisticChildParams = VcnGatewayConnection.this.buildOpportunisticChildParams();
                        VcnGatewayConnection vcnGatewayConnection4 = VcnGatewayConnection.this;
                        vcnIkeSession.openChildSession(buildOpportunisticChildParams, new VcnChildSessionCallback(vcnGatewayConnection4.mCurrentToken, true));
                    }
                    return;
                case 7:
                    handleDisconnectRequested((EventDisconnectRequestedInfo) message.obj);
                    return;
                case 10:
                    handleSafeModeTimeoutExceeded();
                    return;
                case 11:
                    handleMigrationCompleted((EventMigrationCompletedInfo) message.obj);
                    return;
                case 12:
                    VcnGatewayConnection.this.mIkeConnectionInfo = ((EventIkeConnectionInfoChangedInfo) message.obj).ikeConnectionInfo;
                    return;
                case 13:
                    handleDataStallSuspected(((EventDataStallSuspectedInfo) message.obj).network);
                    return;
            }
        }

        public final void handleMigrationCompleted(EventMigrationCompletedInfo eventMigrationCompletedInfo) {
            VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
            vcnGatewayConnection.logInfo("Migration completed: " + VcnGatewayConnection.this.mUnderlying.network);
            applyTransform(VcnGatewayConnection.this.mCurrentToken, VcnGatewayConnection.this.mTunnelIface, VcnGatewayConnection.this.mUnderlying.network, eventMigrationCompletedInfo.inTransform, 0);
            applyTransform(VcnGatewayConnection.this.mCurrentToken, VcnGatewayConnection.this.mTunnelIface, VcnGatewayConnection.this.mUnderlying.network, eventMigrationCompletedInfo.outTransform, 1);
            updateNetworkAgent(VcnGatewayConnection.this.mTunnelIface, VcnGatewayConnection.this.mNetworkAgent, VcnGatewayConnection.this.mChildConfig, VcnGatewayConnection.this.mIkeConnectionInfo);
            VcnGatewayConnection.this.mConnectivityManager.reportNetworkConnectivity(VcnGatewayConnection.this.mNetworkAgent.getNetwork(), false);
        }

        public final void handleUnderlyingNetworkChanged(Message message) {
            UnderlyingNetworkRecord underlyingNetworkRecord = VcnGatewayConnection.this.mUnderlying;
            VcnGatewayConnection.this.mUnderlying = ((EventUnderlyingNetworkChangedInfo) message.obj).newUnderlying;
            if (VcnGatewayConnection.this.mUnderlying == null) {
                VcnGatewayConnection.this.logInfo("Underlying network lost");
            } else if (underlyingNetworkRecord == null || !underlyingNetworkRecord.network.equals(VcnGatewayConnection.this.mUnderlying.network)) {
                VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
                vcnGatewayConnection.logInfo("Migrating to new network: " + VcnGatewayConnection.this.mUnderlying.network);
                VcnGatewayConnection.this.mIkeSession.setNetwork(VcnGatewayConnection.this.mUnderlying.network);
            } else if (VcnGatewayConnection.this.mNetworkAgent == null || VcnGatewayConnection.this.mChildConfig == null) {
            } else {
                updateNetworkAgent(VcnGatewayConnection.this.mTunnelIface, VcnGatewayConnection.this.mNetworkAgent, VcnGatewayConnection.this.mChildConfig, VcnGatewayConnection.this.mIkeConnectionInfo);
            }
        }

        public final void handleDataStallSuspected(Network network) {
            if (VcnGatewayConnection.this.mUnderlying == null || VcnGatewayConnection.this.mNetworkAgent == null || !VcnGatewayConnection.this.mNetworkAgent.getNetwork().equals(network)) {
                return;
            }
            VcnGatewayConnection.this.logInfo("Perform Mobility update to recover from suspected data stall");
            VcnGatewayConnection.this.mIkeSession.setNetwork(VcnGatewayConnection.this.mUnderlying.network);
        }

        public void setupInterfaceAndNetworkAgent(int i, IpSecManager.IpSecTunnelInterface ipSecTunnelInterface, VcnChildSessionConfiguration vcnChildSessionConfiguration, VcnChildSessionConfiguration vcnChildSessionConfiguration2, IkeSessionConnectionInfo ikeSessionConnectionInfo) {
            setupInterface(i, ipSecTunnelInterface, vcnChildSessionConfiguration, vcnChildSessionConfiguration2);
            if (VcnGatewayConnection.this.mNetworkAgent == null) {
                VcnGatewayConnection.this.mNetworkAgent = buildNetworkAgent(ipSecTunnelInterface, vcnChildSessionConfiguration, ikeSessionConnectionInfo);
                return;
            }
            updateNetworkAgent(ipSecTunnelInterface, VcnGatewayConnection.this.mNetworkAgent, vcnChildSessionConfiguration, ikeSessionConnectionInfo);
            clearFailedAttemptCounterAndSafeModeAlarm();
        }

        @Override // com.android.server.vcn.VcnGatewayConnection.BaseState
        public void exitState() {
            VcnGatewayConnection.this.setSafeModeAlarm();
        }
    }

    /* loaded from: classes2.dex */
    public class RetryTimeoutState extends ActiveBaseState {
        public RetryTimeoutState() {
            super();
        }

        @Override // com.android.server.vcn.VcnGatewayConnection.BaseState
        public void enterState() throws Exception {
            VcnGatewayConnection.this.mFailedAttempts++;
            if (VcnGatewayConnection.this.mUnderlying == null) {
                VcnGatewayConnection.this.logWtf("Underlying network was null in retry state");
                teardownNetwork();
                VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
                vcnGatewayConnection.transitionTo(vcnGatewayConnection.mDisconnectedState);
                return;
            }
            VcnGatewayConnection.this.setRetryTimeoutAlarm(getNextRetryIntervalsMs());
        }

        @Override // com.android.server.vcn.VcnGatewayConnection.BaseState
        public void processStateMsg(Message message) {
            int i = message.what;
            if (i == 1) {
                UnderlyingNetworkRecord underlyingNetworkRecord = VcnGatewayConnection.this.mUnderlying;
                VcnGatewayConnection.this.mUnderlying = ((EventUnderlyingNetworkChangedInfo) message.obj).newUnderlying;
                if (VcnGatewayConnection.this.mUnderlying == null) {
                    teardownNetwork();
                    VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
                    vcnGatewayConnection.transitionTo(vcnGatewayConnection.mDisconnectedState);
                    return;
                } else if (underlyingNetworkRecord != null && VcnGatewayConnection.this.mUnderlying.network.equals(underlyingNetworkRecord.network)) {
                    return;
                }
            } else if (i != 2) {
                if (i == 7) {
                    handleDisconnectRequested((EventDisconnectRequestedInfo) message.obj);
                    return;
                } else if (i == 10) {
                    handleSafeModeTimeoutExceeded();
                    return;
                } else {
                    logUnhandledMessage(message);
                    return;
                }
            }
            VcnGatewayConnection vcnGatewayConnection2 = VcnGatewayConnection.this;
            vcnGatewayConnection2.transitionTo(vcnGatewayConnection2.mConnectingState);
        }

        @Override // com.android.server.vcn.VcnGatewayConnection.BaseState
        public void exitState() {
            VcnGatewayConnection.this.cancelRetryTimeoutAlarm();
        }

        public final long getNextRetryIntervalsMs() {
            int i = VcnGatewayConnection.this.mFailedAttempts - 1;
            long[] retryIntervalsMillis = VcnGatewayConnection.this.mConnectionConfig.getRetryIntervalsMillis();
            if (i >= retryIntervalsMillis.length) {
                return retryIntervalsMillis[retryIntervalsMillis.length - 1];
            }
            return retryIntervalsMillis[i];
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public static NetworkCapabilities buildNetworkCapabilities(VcnGatewayConnectionConfig vcnGatewayConnectionConfig, UnderlyingNetworkRecord underlyingNetworkRecord, boolean z) {
        int[] iArr;
        NetworkCapabilities.Builder builder = new NetworkCapabilities.Builder();
        builder.addTransportType(0);
        builder.addCapability(28);
        builder.addCapability(20);
        builder.addCapability(21);
        for (Integer num : vcnGatewayConnectionConfig.getAllExposedCapabilities()) {
            int intValue = num.intValue();
            if (z || (intValue != 12 && intValue != 2)) {
                builder.addCapability(intValue);
            }
        }
        if (underlyingNetworkRecord != null) {
            NetworkCapabilities networkCapabilities = underlyingNetworkRecord.networkCapabilities;
            for (int i : MERGED_CAPABILITIES) {
                if (networkCapabilities.hasCapability(i)) {
                    builder.addCapability(i);
                }
            }
            int[] administratorUids = networkCapabilities.getAdministratorUids();
            Arrays.sort(administratorUids);
            if (networkCapabilities.getOwnerUid() > 0 && Arrays.binarySearch(administratorUids, networkCapabilities.getOwnerUid()) < 0) {
                administratorUids = Arrays.copyOf(administratorUids, administratorUids.length + 1);
                administratorUids[administratorUids.length - 1] = networkCapabilities.getOwnerUid();
                Arrays.sort(administratorUids);
            }
            builder.setOwnerUid(Process.myUid());
            int[] copyOf = Arrays.copyOf(administratorUids, administratorUids.length + 1);
            copyOf[copyOf.length - 1] = Process.myUid();
            builder.setAdministratorUids(copyOf);
            builder.setLinkUpstreamBandwidthKbps(networkCapabilities.getLinkUpstreamBandwidthKbps());
            builder.setLinkDownstreamBandwidthKbps(networkCapabilities.getLinkDownstreamBandwidthKbps());
            if (networkCapabilities.hasTransport(1) && (networkCapabilities.getTransportInfo() instanceof WifiInfo)) {
                builder.setTransportInfo(new VcnTransportInfo((WifiInfo) networkCapabilities.getTransportInfo(), vcnGatewayConnectionConfig.getMinUdpPort4500NatTimeoutSeconds()));
            } else if (networkCapabilities.hasTransport(0) && (networkCapabilities.getNetworkSpecifier() instanceof TelephonyNetworkSpecifier)) {
                builder.setTransportInfo(new VcnTransportInfo(((TelephonyNetworkSpecifier) networkCapabilities.getNetworkSpecifier()).getSubscriptionId(), vcnGatewayConnectionConfig.getMinUdpPort4500NatTimeoutSeconds()));
            } else {
                Slog.wtf(TAG, "Unknown transport type or missing TransportInfo/NetworkSpecifier for non-null underlying network");
            }
            builder.setUnderlyingNetworks(List.of(underlyingNetworkRecord.network));
        } else {
            Slog.wtf(TAG, "No underlying network while building network capabilities", new IllegalStateException());
        }
        return builder.build();
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public LinkProperties buildConnectedLinkProperties(VcnGatewayConnectionConfig vcnGatewayConnectionConfig, IpSecManager.IpSecTunnelInterface ipSecTunnelInterface, VcnChildSessionConfiguration vcnChildSessionConfiguration, UnderlyingNetworkRecord underlyingNetworkRecord, IkeSessionConnectionInfo ikeSessionConnectionInfo) {
        IkeTunnelConnectionParams tunnelConnectionParams = vcnGatewayConnectionConfig.getTunnelConnectionParams();
        LinkProperties linkProperties = new LinkProperties();
        linkProperties.setInterfaceName(ipSecTunnelInterface.getInterfaceName());
        for (LinkAddress linkAddress : vcnChildSessionConfiguration.getInternalAddresses()) {
            linkProperties.addLinkAddress(linkAddress);
        }
        for (InetAddress inetAddress : vcnChildSessionConfiguration.getInternalDnsServers()) {
            linkProperties.addDnsServer(inetAddress);
        }
        int i = 0;
        linkProperties.addRoute(new RouteInfo(new IpPrefix(Inet4Address.ANY, 0), null, null, 1));
        linkProperties.addRoute(new RouteInfo(new IpPrefix(Inet6Address.ANY, 0), null, null, 1));
        if (underlyingNetworkRecord != null) {
            LinkProperties linkProperties2 = underlyingNetworkRecord.linkProperties;
            linkProperties.setTcpBufferSizes(linkProperties2.getTcpBufferSizes());
            int mtu = linkProperties2.getMtu();
            i = (mtu != 0 || linkProperties2.getInterfaceName() == null) ? mtu : this.mDeps.getUnderlyingIfaceMtu(linkProperties2.getInterfaceName());
        } else {
            Slog.wtf(TAG, "No underlying network while building link properties", new IllegalStateException());
        }
        linkProperties.setMtu(MtuUtils.getMtu(tunnelConnectionParams.getTunnelModeChildSessionParams().getSaProposals(), vcnGatewayConnectionConfig.getMaxMtu(), i, ikeSessionConnectionInfo.getLocalAddress() instanceof Inet4Address));
        return linkProperties;
    }

    /* loaded from: classes2.dex */
    public class IkeSessionCallbackImpl implements IkeSessionCallback {
        public final int mToken;

        public IkeSessionCallbackImpl(int i) {
            this.mToken = i;
        }

        @Override // android.net.ipsec.ike.IkeSessionCallback
        public void onOpened(IkeSessionConfiguration ikeSessionConfiguration) {
            VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
            vcnGatewayConnection.logDbg("IkeOpened for token " + this.mToken);
            VcnGatewayConnection.this.ikeConnectionInfoChanged(this.mToken, ikeSessionConfiguration.getIkeSessionConnectionInfo());
        }

        @Override // android.net.ipsec.ike.IkeSessionCallback
        public void onClosed() {
            VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
            vcnGatewayConnection.logDbg("IkeClosed for token " + this.mToken);
            VcnGatewayConnection.this.sessionClosed(this.mToken, null);
        }

        public void onClosedExceptionally(IkeException ikeException) {
            VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
            vcnGatewayConnection.logInfo("IkeClosedExceptionally for token " + this.mToken, ikeException);
            VcnGatewayConnection.this.sessionClosed(this.mToken, ikeException);
        }

        public void onError(IkeProtocolException ikeProtocolException) {
            VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
            vcnGatewayConnection.logInfo("IkeError for token " + this.mToken, ikeProtocolException);
        }

        public void onIkeSessionConnectionInfoChanged(IkeSessionConnectionInfo ikeSessionConnectionInfo) {
            VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
            vcnGatewayConnection.logDbg("onIkeSessionConnectionInfoChanged for token " + this.mToken);
            VcnGatewayConnection.this.ikeConnectionInfoChanged(this.mToken, ikeSessionConnectionInfo);
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    /* loaded from: classes2.dex */
    public class VcnChildSessionCallback implements ChildSessionCallback {
        public boolean mIsChildOpened;
        public final boolean mIsOpportunistic;
        public final int mToken;

        public VcnChildSessionCallback(VcnGatewayConnection vcnGatewayConnection, int i) {
            this(i, false);
        }

        public VcnChildSessionCallback(int i, boolean z) {
            this.mIsChildOpened = false;
            this.mToken = i;
            this.mIsOpportunistic = z;
        }

        @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
        public void onOpened(VcnChildSessionConfiguration vcnChildSessionConfiguration) {
            VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
            vcnGatewayConnection.logDbg("ChildOpened for token " + this.mToken);
            if (this.mIsOpportunistic) {
                VcnGatewayConnection.this.logDbg("ChildOpened for opportunistic child; suppressing event message");
                this.mIsChildOpened = true;
                return;
            }
            VcnGatewayConnection.this.childOpened(this.mToken, vcnChildSessionConfiguration);
        }

        @Override // android.net.ipsec.ike.ChildSessionCallback
        public void onOpened(ChildSessionConfiguration childSessionConfiguration) {
            onOpened(new VcnChildSessionConfiguration(childSessionConfiguration));
        }

        @Override // android.net.ipsec.ike.ChildSessionCallback
        public void onClosed() {
            VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
            vcnGatewayConnection.logDbg("ChildClosed for token " + this.mToken);
            if (this.mIsOpportunistic && !this.mIsChildOpened) {
                VcnGatewayConnection.this.logDbg("ChildClosed for unopened opportunistic child; ignoring");
            } else {
                VcnGatewayConnection.this.sessionLost(this.mToken, null);
            }
        }

        public void onClosedExceptionally(IkeException ikeException) {
            VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
            vcnGatewayConnection.logInfo("ChildClosedExceptionally for token " + this.mToken, ikeException);
            if (this.mIsOpportunistic && !this.mIsChildOpened) {
                VcnGatewayConnection.this.logInfo("ChildClosedExceptionally for unopened opportunistic child; ignoring");
            } else {
                VcnGatewayConnection.this.sessionLost(this.mToken, ikeException);
            }
        }

        @Override // android.net.ipsec.ike.ChildSessionCallback
        public void onIpSecTransformCreated(IpSecTransform ipSecTransform, int i) {
            VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
            vcnGatewayConnection.logDbg("ChildTransformCreated; Direction: " + i + "; token " + this.mToken);
            VcnGatewayConnection.this.childTransformCreated(this.mToken, ipSecTransform, i);
        }

        public void onIpSecTransformsMigrated(IpSecTransform ipSecTransform, IpSecTransform ipSecTransform2) {
            VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
            vcnGatewayConnection.logDbg("ChildTransformsMigrated; token " + this.mToken);
            VcnGatewayConnection.this.migrationCompleted(this.mToken, ipSecTransform, ipSecTransform2);
        }

        @Override // android.net.ipsec.ike.ChildSessionCallback
        public void onIpSecTransformDeleted(IpSecTransform ipSecTransform, int i) {
            VcnGatewayConnection vcnGatewayConnection = VcnGatewayConnection.this;
            vcnGatewayConnection.logDbg("ChildTransformDeleted; Direction: " + i + "; for token " + this.mToken);
        }
    }

    public String getLogPrefix() {
        return "(" + LogUtils.getHashedSubscriptionGroup(this.mSubscriptionGroup) + PackageManagerShellCommandDataLoader.STDIN_PATH + this.mConnectionConfig.getGatewayConnectionName() + PackageManagerShellCommandDataLoader.STDIN_PATH + System.identityHashCode(this) + ") ";
    }

    public final String getTagLogPrefix() {
        return "[ " + TAG + " " + getLogPrefix() + "]";
    }

    public final void logDbg(String str) {
        String str2 = TAG;
        Slog.d(str2, getLogPrefix() + str);
    }

    public final void logInfo(String str) {
        String str2 = TAG;
        Slog.i(str2, getLogPrefix() + str);
        LocalLog localLog = VcnManagementService.LOCAL_LOG;
        localLog.log("[INFO] " + getTagLogPrefix() + str);
    }

    public final void logInfo(String str, Throwable th) {
        String str2 = TAG;
        Slog.i(str2, getLogPrefix() + str, th);
        LocalLog localLog = VcnManagementService.LOCAL_LOG;
        localLog.log("[INFO] " + getTagLogPrefix() + str + th);
    }

    public final void logWarn(String str, Throwable th) {
        String str2 = TAG;
        Slog.w(str2, getLogPrefix() + str, th);
        LocalLog localLog = VcnManagementService.LOCAL_LOG;
        localLog.log("[WARN] " + getTagLogPrefix() + str + th);
    }

    public final void logWtf(String str) {
        String str2 = TAG;
        Slog.wtf(str2, getLogPrefix() + str);
        LocalLog localLog = VcnManagementService.LOCAL_LOG;
        localLog.log("[WTF ] " + str);
    }

    public final void logWtf(String str, Throwable th) {
        String str2 = TAG;
        Slog.wtf(str2, getLogPrefix() + str, th);
        LocalLog localLog = VcnManagementService.LOCAL_LOG;
        localLog.log("[WTF ] " + str + th);
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.println("VcnGatewayConnection (" + this.mConnectionConfig.getGatewayConnectionName() + "):");
        indentingPrintWriter.increaseIndent();
        StringBuilder sb = new StringBuilder();
        sb.append("Current state: ");
        sb.append(getCurrentState() == null ? null : getCurrentState().getClass().getSimpleName());
        indentingPrintWriter.println(sb.toString());
        indentingPrintWriter.println("mIsQuitting: " + this.mIsQuitting.getValue());
        indentingPrintWriter.println("mIsInSafeMode: " + this.mIsInSafeMode);
        indentingPrintWriter.println("mCurrentToken: " + this.mCurrentToken);
        indentingPrintWriter.println("mFailedAttempts: " + this.mFailedAttempts);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("mNetworkAgent.getNetwork(): ");
        VcnNetworkAgent vcnNetworkAgent = this.mNetworkAgent;
        sb2.append(vcnNetworkAgent != null ? vcnNetworkAgent.getNetwork() : null);
        indentingPrintWriter.println(sb2.toString());
        indentingPrintWriter.println();
        this.mUnderlyingNetworkController.dump(indentingPrintWriter);
        indentingPrintWriter.println();
        indentingPrintWriter.decreaseIndent();
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public void setTunnelInterface(IpSecManager.IpSecTunnelInterface ipSecTunnelInterface) {
        this.mTunnelIface = ipSecTunnelInterface;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public UnderlyingNetworkController.UnderlyingNetworkControllerCallback getUnderlyingNetworkControllerCallback() {
        return this.mUnderlyingNetworkControllerCallback;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public ConnectivityDiagnosticsManager.ConnectivityDiagnosticsCallback getConnectivityDiagnosticsCallback() {
        return this.mConnectivityDiagnosticsCallback;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public UnderlyingNetworkRecord getUnderlyingNetwork() {
        return this.mUnderlying;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public void setUnderlyingNetwork(UnderlyingNetworkRecord underlyingNetworkRecord) {
        this.mUnderlying = underlyingNetworkRecord;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public IkeSessionConnectionInfo getIkeConnectionInfo() {
        return this.mIkeConnectionInfo;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public boolean isQuitting() {
        return this.mIsQuitting.getValue();
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public void setQuitting() {
        this.mIsQuitting.setTrue();
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public VcnIkeSession getIkeSession() {
        return this.mIkeSession;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public void setIkeSession(VcnIkeSession vcnIkeSession) {
        this.mIkeSession = vcnIkeSession;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public VcnNetworkAgent getNetworkAgent() {
        return this.mNetworkAgent;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public void setNetworkAgent(VcnNetworkAgent vcnNetworkAgent) {
        this.mNetworkAgent = vcnNetworkAgent;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public void sendDisconnectRequestedAndAcquireWakelock(String str, boolean z) {
        sendMessageAndAcquireWakeLock(7, Integer.MIN_VALUE, new EventDisconnectRequestedInfo(str, z));
    }

    public final IkeSessionParams buildIkeParams(Network network) {
        IkeSessionParams.Builder builder = new IkeSessionParams.Builder(this.mConnectionConfig.getTunnelConnectionParams().getIkeSessionParams());
        builder.setNetwork(network);
        return builder.build();
    }

    public final ChildSessionParams buildChildParams() {
        return this.mConnectionConfig.getTunnelConnectionParams().getTunnelModeChildSessionParams();
    }

    public final ChildSessionParams buildOpportunisticChildParams() {
        TunnelModeChildSessionParams tunnelModeChildSessionParams = this.mConnectionConfig.getTunnelConnectionParams().getTunnelModeChildSessionParams();
        TunnelModeChildSessionParams.Builder builder = new TunnelModeChildSessionParams.Builder();
        for (ChildSaProposal childSaProposal : tunnelModeChildSessionParams.getChildSaProposals()) {
            builder.addChildSaProposal(childSaProposal);
        }
        for (IkeTrafficSelector ikeTrafficSelector : tunnelModeChildSessionParams.getInboundTrafficSelectors()) {
            builder.addInboundTrafficSelectors(ikeTrafficSelector);
        }
        for (IkeTrafficSelector ikeTrafficSelector2 : tunnelModeChildSessionParams.getOutboundTrafficSelectors()) {
            builder.addOutboundTrafficSelectors(ikeTrafficSelector2);
        }
        builder.setLifetimeSeconds(tunnelModeChildSessionParams.getHardLifetimeSeconds(), tunnelModeChildSessionParams.getSoftLifetimeSeconds());
        return builder.build();
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public VcnIkeSession buildIkeSession(Network network) {
        int i = this.mCurrentToken + 1;
        this.mCurrentToken = i;
        return this.mDeps.newIkeSession(this.mVcnContext, buildIkeParams(network), buildChildParams(), new IkeSessionCallbackImpl(i), new VcnChildSessionCallback(this, i));
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    /* loaded from: classes2.dex */
    public static class Dependencies {
        public UnderlyingNetworkController newUnderlyingNetworkController(VcnContext vcnContext, VcnGatewayConnectionConfig vcnGatewayConnectionConfig, ParcelUuid parcelUuid, TelephonySubscriptionTracker.TelephonySubscriptionSnapshot telephonySubscriptionSnapshot, UnderlyingNetworkController.UnderlyingNetworkControllerCallback underlyingNetworkControllerCallback) {
            return new UnderlyingNetworkController(vcnContext, vcnGatewayConnectionConfig, parcelUuid, telephonySubscriptionSnapshot, underlyingNetworkControllerCallback);
        }

        public VcnIkeSession newIkeSession(VcnContext vcnContext, IkeSessionParams ikeSessionParams, ChildSessionParams childSessionParams, IkeSessionCallback ikeSessionCallback, ChildSessionCallback childSessionCallback) {
            return new VcnIkeSession(vcnContext, ikeSessionParams, childSessionParams, ikeSessionCallback, childSessionCallback);
        }

        public VcnWakeLock newWakeLock(Context context, int i, String str) {
            return new VcnWakeLock(context, i, str);
        }

        public WakeupMessage newWakeupMessage(VcnContext vcnContext, Handler handler, String str, Runnable runnable) {
            return new WakeupMessage(vcnContext.getContext(), handler, str, runnable);
        }

        public VcnNetworkAgent newNetworkAgent(VcnContext vcnContext, String str, NetworkCapabilities networkCapabilities, LinkProperties linkProperties, NetworkScore networkScore, NetworkAgentConfig networkAgentConfig, NetworkProvider networkProvider, Consumer<VcnNetworkAgent> consumer, Consumer<Integer> consumer2) {
            return new VcnNetworkAgent(vcnContext, str, networkCapabilities, linkProperties, networkScore, networkAgentConfig, networkProvider, consumer, consumer2);
        }

        public boolean isAirplaneModeOn(VcnContext vcnContext) {
            return Settings.Global.getInt(vcnContext.getContext().getContentResolver(), "airplane_mode_on", 0) != 0;
        }

        public long getElapsedRealTime() {
            return SystemClock.elapsedRealtime();
        }

        public int getUnderlyingIfaceMtu(String str) {
            try {
                NetworkInterface byName = NetworkInterface.getByName(str);
                if (byName == null) {
                    return 0;
                }
                return byName.getMTU();
            } catch (IOException e) {
                Slog.d(VcnGatewayConnection.TAG, "Could not get MTU of underlying network", e);
                return 0;
            }
        }

        public int getParallelTunnelCount(TelephonySubscriptionTracker.TelephonySubscriptionSnapshot telephonySubscriptionSnapshot, ParcelUuid parcelUuid) {
            PersistableBundleUtils.PersistableBundleWrapper carrierConfigForSubGrp = telephonySubscriptionSnapshot.getCarrierConfigForSubGrp(parcelUuid);
            return Math.max(1, carrierConfigForSubGrp != null ? carrierConfigForSubGrp.getInt("vcn_tunnel_aggregation_sa_count_max", 1) : 1);
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    /* loaded from: classes2.dex */
    public static class VcnChildSessionConfiguration {
        public final ChildSessionConfiguration mChildConfig;

        public VcnChildSessionConfiguration(ChildSessionConfiguration childSessionConfiguration) {
            this.mChildConfig = childSessionConfiguration;
        }

        public List<LinkAddress> getInternalAddresses() {
            return this.mChildConfig.getInternalAddresses();
        }

        public List<InetAddress> getInternalDnsServers() {
            return this.mChildConfig.getInternalDnsServers();
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    /* loaded from: classes2.dex */
    public static class VcnIkeSession {
        public final IkeSession mImpl;

        public VcnIkeSession(VcnContext vcnContext, IkeSessionParams ikeSessionParams, ChildSessionParams childSessionParams, IkeSessionCallback ikeSessionCallback, ChildSessionCallback childSessionCallback) {
            this.mImpl = new IkeSession(vcnContext.getContext(), ikeSessionParams, childSessionParams, new HandlerExecutor(new Handler(vcnContext.getLooper())), ikeSessionCallback, childSessionCallback);
        }

        public void openChildSession(ChildSessionParams childSessionParams, ChildSessionCallback childSessionCallback) {
            this.mImpl.openChildSession(childSessionParams, childSessionCallback);
        }

        public void close() {
            this.mImpl.close();
        }

        public void kill() {
            this.mImpl.kill();
        }

        public void setNetwork(Network network) {
            this.mImpl.setNetwork(network);
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    /* loaded from: classes2.dex */
    public static class VcnWakeLock {
        public final PowerManager.WakeLock mImpl;

        public VcnWakeLock(Context context, int i, String str) {
            PowerManager.WakeLock newWakeLock = ((PowerManager) context.getSystemService(PowerManager.class)).newWakeLock(i, str);
            this.mImpl = newWakeLock;
            newWakeLock.setReferenceCounted(false);
        }

        public synchronized void acquire() {
            this.mImpl.acquire();
        }

        public synchronized void release() {
            this.mImpl.release();
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    /* loaded from: classes2.dex */
    public static class VcnNetworkAgent {
        public final NetworkAgent mImpl;

        public VcnNetworkAgent(VcnContext vcnContext, String str, NetworkCapabilities networkCapabilities, LinkProperties linkProperties, NetworkScore networkScore, NetworkAgentConfig networkAgentConfig, NetworkProvider networkProvider, final Consumer<VcnNetworkAgent> consumer, final Consumer<Integer> consumer2) {
            this.mImpl = new NetworkAgent(vcnContext.getContext(), vcnContext.getLooper(), str, networkCapabilities, linkProperties, networkScore, networkAgentConfig, networkProvider) { // from class: com.android.server.vcn.VcnGatewayConnection.VcnNetworkAgent.1
                public void onNetworkUnwanted() {
                    consumer.accept(VcnNetworkAgent.this);
                }

                public void onValidationStatus(int i, Uri uri) {
                    consumer2.accept(Integer.valueOf(i));
                }
            };
        }

        public void register() {
            this.mImpl.register();
        }

        public void markConnected() {
            this.mImpl.markConnected();
        }

        public void unregister() {
            this.mImpl.unregister();
        }

        public void sendNetworkCapabilities(NetworkCapabilities networkCapabilities) {
            this.mImpl.sendNetworkCapabilities(networkCapabilities);
        }

        public void sendLinkProperties(LinkProperties linkProperties) {
            this.mImpl.sendLinkProperties(linkProperties);
        }

        public void setUnderlyingNetworks(List<Network> list) {
            this.mImpl.setUnderlyingNetworks(list);
        }

        public Network getNetwork() {
            return this.mImpl.getNetwork();
        }
    }
}
