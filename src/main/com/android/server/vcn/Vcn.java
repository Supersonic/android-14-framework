package com.android.server.vcn;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkScore;
import android.net.Uri;
import android.net.vcn.VcnConfig;
import android.net.vcn.VcnGatewayConnectionConfig;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Message;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.LocalLog;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.VcnManagementService;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.vcn.TelephonySubscriptionTracker;
import com.android.server.vcn.VcnNetworkProvider;
import com.android.server.vcn.util.LogUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes2.dex */
public class Vcn extends Handler {
    public VcnConfig mConfig;
    public final VcnContentResolver mContentResolver;
    public volatile int mCurrentStatus;
    public final Dependencies mDeps;
    public boolean mIsMobileDataEnabled;
    public TelephonySubscriptionTracker.TelephonySubscriptionSnapshot mLastSnapshot;
    public final ContentObserver mMobileDataSettingsObserver;
    public final Map<Integer, VcnUserMobileDataStateListener> mMobileDataStateListeners;
    public final VcnNetworkRequestListener mRequestListener;
    public final ParcelUuid mSubscriptionGroup;
    public final VcnManagementService.VcnCallback mVcnCallback;
    public final VcnContext mVcnContext;
    public final Map<VcnGatewayConnectionConfig, VcnGatewayConnection> mVcnGatewayConnections;
    public static final String TAG = Vcn.class.getSimpleName();
    public static final List<Integer> CAPS_REQUIRING_MOBILE_DATA = Arrays.asList(12, 2);

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    /* loaded from: classes2.dex */
    public interface VcnGatewayStatusCallback {
        void onGatewayConnectionError(String str, int i, String str2, String str3);

        void onQuit();

        void onSafeModeStatusChanged();
    }

    public final void logVdbg(String str) {
    }

    public Vcn(VcnContext vcnContext, ParcelUuid parcelUuid, VcnConfig vcnConfig, TelephonySubscriptionTracker.TelephonySubscriptionSnapshot telephonySubscriptionSnapshot, VcnManagementService.VcnCallback vcnCallback) {
        this(vcnContext, parcelUuid, vcnConfig, telephonySubscriptionSnapshot, vcnCallback, new Dependencies());
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public Vcn(VcnContext vcnContext, ParcelUuid parcelUuid, VcnConfig vcnConfig, TelephonySubscriptionTracker.TelephonySubscriptionSnapshot telephonySubscriptionSnapshot, VcnManagementService.VcnCallback vcnCallback, Dependencies dependencies) {
        super(vcnContext.getLooper());
        Objects.requireNonNull(vcnContext, "Missing vcnContext");
        this.mMobileDataStateListeners = new ArrayMap();
        this.mVcnGatewayConnections = new HashMap();
        this.mCurrentStatus = 2;
        this.mIsMobileDataEnabled = false;
        this.mVcnContext = vcnContext;
        Objects.requireNonNull(parcelUuid, "Missing subscriptionGroup");
        this.mSubscriptionGroup = parcelUuid;
        Objects.requireNonNull(vcnCallback, "Missing vcnCallback");
        this.mVcnCallback = vcnCallback;
        Objects.requireNonNull(dependencies, "Missing deps");
        this.mDeps = dependencies;
        VcnNetworkRequestListener vcnNetworkRequestListener = new VcnNetworkRequestListener();
        this.mRequestListener = vcnNetworkRequestListener;
        VcnContentResolver newVcnContentResolver = dependencies.newVcnContentResolver(vcnContext);
        this.mContentResolver = newVcnContentResolver;
        VcnMobileDataContentObserver vcnMobileDataContentObserver = new VcnMobileDataContentObserver(this);
        this.mMobileDataSettingsObserver = vcnMobileDataContentObserver;
        newVcnContentResolver.registerContentObserver(Settings.Global.getUriFor("mobile_data"), true, vcnMobileDataContentObserver);
        Objects.requireNonNull(vcnConfig, "Missing config");
        this.mConfig = vcnConfig;
        Objects.requireNonNull(telephonySubscriptionSnapshot, "Missing snapshot");
        this.mLastSnapshot = telephonySubscriptionSnapshot;
        this.mIsMobileDataEnabled = getMobileDataStatus();
        updateMobileDataStateListeners();
        vcnContext.getVcnNetworkProvider().registerListener(vcnNetworkRequestListener);
    }

    public void updateConfig(VcnConfig vcnConfig) {
        Objects.requireNonNull(vcnConfig, "Missing config");
        sendMessage(obtainMessage(0, vcnConfig));
    }

    public void updateSubscriptionSnapshot(TelephonySubscriptionTracker.TelephonySubscriptionSnapshot telephonySubscriptionSnapshot) {
        Objects.requireNonNull(telephonySubscriptionSnapshot, "Missing snapshot");
        sendMessage(obtainMessage(2, telephonySubscriptionSnapshot));
    }

    public void teardownAsynchronously() {
        sendMessageAtFrontOfQueue(obtainMessage(100));
    }

    public int getStatus() {
        return this.mCurrentStatus;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public void setStatus(int i) {
        this.mCurrentStatus = i;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public Set<VcnGatewayConnection> getVcnGatewayConnections() {
        return Collections.unmodifiableSet(new HashSet(this.mVcnGatewayConnections.values()));
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public Map<VcnGatewayConnectionConfig, VcnGatewayConnection> getVcnGatewayConnectionConfigMap() {
        return Collections.unmodifiableMap(new HashMap(this.mVcnGatewayConnections));
    }

    /* loaded from: classes2.dex */
    public class VcnNetworkRequestListener implements VcnNetworkProvider.NetworkRequestListener {
        public VcnNetworkRequestListener() {
        }

        @Override // com.android.server.vcn.VcnNetworkProvider.NetworkRequestListener
        public void onNetworkRequested(NetworkRequest networkRequest) {
            Objects.requireNonNull(networkRequest, "Missing request");
            Vcn vcn = Vcn.this;
            vcn.sendMessage(vcn.obtainMessage(1, networkRequest));
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        if (this.mCurrentStatus == 2 || this.mCurrentStatus == 3) {
            int i = message.what;
            if (i == 0) {
                handleConfigUpdated((VcnConfig) message.obj);
            } else if (i == 1) {
                handleNetworkRequested((NetworkRequest) message.obj);
            } else if (i == 2) {
                handleSubscriptionsChanged((TelephonySubscriptionTracker.TelephonySubscriptionSnapshot) message.obj);
            } else if (i == 3) {
                handleGatewayConnectionQuit((VcnGatewayConnectionConfig) message.obj);
            } else if (i == 4) {
                handleSafeModeStatusChanged();
            } else if (i == 5) {
                handleMobileDataToggled();
            } else if (i == 100) {
                handleTeardown();
            } else {
                logWtf("Unknown msg.what: " + message.what);
            }
        }
    }

    public final void handleConfigUpdated(VcnConfig vcnConfig) {
        logDbg("Config updated: old = " + this.mConfig.hashCode() + "; new = " + vcnConfig.hashCode());
        this.mConfig = vcnConfig;
        for (Map.Entry<VcnGatewayConnectionConfig, VcnGatewayConnection> entry : this.mVcnGatewayConnections.entrySet()) {
            VcnGatewayConnection value = entry.getValue();
            if (!this.mConfig.getGatewayConnectionConfigs().contains(entry.getKey())) {
                if (value == null) {
                    logWtf("Found gatewayConnectionConfig without GatewayConnection");
                } else {
                    logInfo("Config updated, restarting gateway " + value.getLogPrefix());
                    value.teardownAsynchronously();
                }
            }
        }
        this.mVcnContext.getVcnNetworkProvider().resendAllRequests(this.mRequestListener);
    }

    public final void handleTeardown() {
        logDbg("Tearing down");
        this.mVcnContext.getVcnNetworkProvider().unregisterListener(this.mRequestListener);
        for (VcnGatewayConnection vcnGatewayConnection : this.mVcnGatewayConnections.values()) {
            vcnGatewayConnection.teardownAsynchronously();
        }
        for (VcnUserMobileDataStateListener vcnUserMobileDataStateListener : this.mMobileDataStateListeners.values()) {
            getTelephonyManager().unregisterTelephonyCallback(vcnUserMobileDataStateListener);
        }
        this.mMobileDataStateListeners.clear();
        this.mCurrentStatus = 1;
    }

    public final void handleSafeModeStatusChanged() {
        boolean z;
        logVdbg("VcnGatewayConnection safe mode status changed");
        Iterator<VcnGatewayConnection> it = this.mVcnGatewayConnections.values().iterator();
        while (true) {
            if (!it.hasNext()) {
                z = false;
                break;
            } else if (it.next().isInSafeMode()) {
                z = true;
                break;
            }
        }
        int i = this.mCurrentStatus;
        this.mCurrentStatus = z ? 3 : 2;
        if (i != this.mCurrentStatus) {
            this.mVcnCallback.onSafeModeStatusChanged(z);
            StringBuilder sb = new StringBuilder();
            sb.append("Safe mode ");
            sb.append(this.mCurrentStatus == 3 ? "entered" : "exited");
            logInfo(sb.toString());
        }
    }

    public final void handleNetworkRequested(NetworkRequest networkRequest) {
        logVdbg("Received request " + networkRequest);
        for (VcnGatewayConnectionConfig vcnGatewayConnectionConfig : this.mVcnGatewayConnections.keySet()) {
            if (isRequestSatisfiedByGatewayConnectionConfig(networkRequest, vcnGatewayConnectionConfig)) {
                logVdbg("Request already satisfied by existing VcnGatewayConnection: " + networkRequest);
                return;
            }
        }
        for (VcnGatewayConnectionConfig vcnGatewayConnectionConfig2 : this.mConfig.getGatewayConnectionConfigs()) {
            if (isRequestSatisfiedByGatewayConnectionConfig(networkRequest, vcnGatewayConnectionConfig2) && !getExposedCapabilitiesForMobileDataState(vcnGatewayConnectionConfig2).isEmpty()) {
                if (this.mVcnGatewayConnections.containsKey(vcnGatewayConnectionConfig2)) {
                    logWtf("Attempted to bring up VcnGatewayConnection for config with existing VcnGatewayConnection");
                    return;
                }
                logInfo("Bringing up new VcnGatewayConnection for request " + networkRequest);
                this.mVcnGatewayConnections.put(vcnGatewayConnectionConfig2, this.mDeps.newVcnGatewayConnection(this.mVcnContext, this.mSubscriptionGroup, this.mLastSnapshot, vcnGatewayConnectionConfig2, new VcnGatewayStatusCallbackImpl(vcnGatewayConnectionConfig2), this.mIsMobileDataEnabled));
                return;
            }
        }
        logVdbg("Request could not be fulfilled by VCN: " + networkRequest);
    }

    public final Set<Integer> getExposedCapabilitiesForMobileDataState(VcnGatewayConnectionConfig vcnGatewayConnectionConfig) {
        if (this.mIsMobileDataEnabled) {
            return vcnGatewayConnectionConfig.getAllExposedCapabilities();
        }
        ArraySet arraySet = new ArraySet(vcnGatewayConnectionConfig.getAllExposedCapabilities());
        arraySet.removeAll(CAPS_REQUIRING_MOBILE_DATA);
        return arraySet;
    }

    public final void handleGatewayConnectionQuit(VcnGatewayConnectionConfig vcnGatewayConnectionConfig) {
        logInfo("VcnGatewayConnection quit: " + vcnGatewayConnectionConfig);
        this.mVcnGatewayConnections.remove(vcnGatewayConnectionConfig);
        this.mVcnContext.getVcnNetworkProvider().resendAllRequests(this.mRequestListener);
    }

    public final void handleSubscriptionsChanged(TelephonySubscriptionTracker.TelephonySubscriptionSnapshot telephonySubscriptionSnapshot) {
        this.mLastSnapshot = telephonySubscriptionSnapshot;
        for (VcnGatewayConnection vcnGatewayConnection : this.mVcnGatewayConnections.values()) {
            vcnGatewayConnection.updateSubscriptionSnapshot(this.mLastSnapshot);
        }
        updateMobileDataStateListeners();
        handleMobileDataToggled();
    }

    public final void updateMobileDataStateListeners() {
        Set<Integer> allSubIdsInGroup = this.mLastSnapshot.getAllSubIdsInGroup(this.mSubscriptionGroup);
        HandlerExecutor handlerExecutor = new HandlerExecutor(this);
        for (Integer num : allSubIdsInGroup) {
            int intValue = num.intValue();
            if (!this.mMobileDataStateListeners.containsKey(Integer.valueOf(intValue))) {
                VcnUserMobileDataStateListener vcnUserMobileDataStateListener = new VcnUserMobileDataStateListener();
                getTelephonyManagerForSubid(intValue).registerTelephonyCallback(handlerExecutor, vcnUserMobileDataStateListener);
                this.mMobileDataStateListeners.put(Integer.valueOf(intValue), vcnUserMobileDataStateListener);
            }
        }
        Iterator<Map.Entry<Integer, VcnUserMobileDataStateListener>> it = this.mMobileDataStateListeners.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, VcnUserMobileDataStateListener> next = it.next();
            if (!allSubIdsInGroup.contains(next.getKey())) {
                getTelephonyManager().unregisterTelephonyCallback(next.getValue());
                it.remove();
            }
        }
    }

    public final void handleMobileDataToggled() {
        boolean z = this.mIsMobileDataEnabled;
        boolean mobileDataStatus = getMobileDataStatus();
        this.mIsMobileDataEnabled = mobileDataStatus;
        if (z != mobileDataStatus) {
            for (Map.Entry<VcnGatewayConnectionConfig, VcnGatewayConnection> entry : this.mVcnGatewayConnections.entrySet()) {
                VcnGatewayConnection value = entry.getValue();
                Set allExposedCapabilities = entry.getKey().getAllExposedCapabilities();
                if (allExposedCapabilities.contains(12) || allExposedCapabilities.contains(2)) {
                    if (value == null) {
                        logWtf("Found gatewayConnectionConfig without GatewayConnection");
                    } else {
                        value.teardownAsynchronously();
                    }
                }
            }
            this.mVcnContext.getVcnNetworkProvider().resendAllRequests(this.mRequestListener);
            StringBuilder sb = new StringBuilder();
            sb.append("Mobile data ");
            sb.append(this.mIsMobileDataEnabled ? "enabled" : "disabled");
            logInfo(sb.toString());
        }
    }

    public final boolean getMobileDataStatus() {
        for (Integer num : this.mLastSnapshot.getAllSubIdsInGroup(this.mSubscriptionGroup)) {
            if (getTelephonyManagerForSubid(num.intValue()).isDataEnabled()) {
                return true;
            }
        }
        return false;
    }

    public final boolean isRequestSatisfiedByGatewayConnectionConfig(NetworkRequest networkRequest, VcnGatewayConnectionConfig vcnGatewayConnectionConfig) {
        NetworkCapabilities.Builder builder = new NetworkCapabilities.Builder();
        builder.addTransportType(0);
        builder.addCapability(28);
        for (Integer num : getExposedCapabilitiesForMobileDataState(vcnGatewayConnectionConfig)) {
            builder.addCapability(num.intValue());
        }
        return networkRequest.canBeSatisfiedBy(builder.build());
    }

    public final TelephonyManager getTelephonyManager() {
        return (TelephonyManager) this.mVcnContext.getContext().getSystemService(TelephonyManager.class);
    }

    public final TelephonyManager getTelephonyManagerForSubid(int i) {
        return getTelephonyManager().createForSubscriptionId(i);
    }

    public final String getLogPrefix() {
        return "(" + LogUtils.getHashedSubscriptionGroup(this.mSubscriptionGroup) + PackageManagerShellCommandDataLoader.STDIN_PATH + System.identityHashCode(this) + ") ";
    }

    public final void logDbg(String str) {
        String str2 = TAG;
        Slog.d(str2, getLogPrefix() + str);
    }

    public final void logInfo(String str) {
        String str2 = TAG;
        Slog.i(str2, getLogPrefix() + str);
        LocalLog localLog = VcnManagementService.LOCAL_LOG;
        localLog.log(getLogPrefix() + "INFO: " + str);
    }

    public final void logWtf(String str) {
        String str2 = TAG;
        Slog.wtf(str2, getLogPrefix() + str);
        LocalLog localLog = VcnManagementService.LOCAL_LOG;
        localLog.log(getLogPrefix() + "WTF: " + str);
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.println("Vcn (" + this.mSubscriptionGroup + "):");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("mCurrentStatus: " + this.mCurrentStatus);
        indentingPrintWriter.println("mIsMobileDataEnabled: " + this.mIsMobileDataEnabled);
        indentingPrintWriter.println();
        indentingPrintWriter.println("mVcnGatewayConnections:");
        indentingPrintWriter.increaseIndent();
        for (VcnGatewayConnection vcnGatewayConnection : this.mVcnGatewayConnections.values()) {
            vcnGatewayConnection.dump(indentingPrintWriter);
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println();
        indentingPrintWriter.decreaseIndent();
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public boolean isMobileDataEnabled() {
        return this.mIsMobileDataEnabled;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public void setMobileDataEnabled(boolean z) {
        this.mIsMobileDataEnabled = z;
    }

    public static NetworkScore getNetworkScore() {
        return new NetworkScore.Builder().setLegacyInt(52).setTransportPrimary(true).build();
    }

    /* loaded from: classes2.dex */
    public class VcnGatewayStatusCallbackImpl implements VcnGatewayStatusCallback {
        public final VcnGatewayConnectionConfig mGatewayConnectionConfig;

        public VcnGatewayStatusCallbackImpl(VcnGatewayConnectionConfig vcnGatewayConnectionConfig) {
            this.mGatewayConnectionConfig = vcnGatewayConnectionConfig;
        }

        @Override // com.android.server.vcn.Vcn.VcnGatewayStatusCallback
        public void onQuit() {
            Vcn vcn = Vcn.this;
            vcn.sendMessage(vcn.obtainMessage(3, this.mGatewayConnectionConfig));
        }

        @Override // com.android.server.vcn.Vcn.VcnGatewayStatusCallback
        public void onSafeModeStatusChanged() {
            Vcn vcn = Vcn.this;
            vcn.sendMessage(vcn.obtainMessage(4));
        }

        @Override // com.android.server.vcn.Vcn.VcnGatewayStatusCallback
        public void onGatewayConnectionError(String str, int i, String str2, String str3) {
            Vcn.this.mVcnCallback.onGatewayConnectionError(str, i, str2, str3);
        }
    }

    /* loaded from: classes2.dex */
    public class VcnMobileDataContentObserver extends ContentObserver {
        public VcnMobileDataContentObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            Vcn vcn = Vcn.this;
            vcn.sendMessage(vcn.obtainMessage(5));
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    /* loaded from: classes2.dex */
    public class VcnUserMobileDataStateListener extends TelephonyCallback implements TelephonyCallback.UserMobileDataStateListener {
        public VcnUserMobileDataStateListener() {
        }

        @Override // android.telephony.TelephonyCallback.UserMobileDataStateListener
        public void onUserMobileDataStateChanged(boolean z) {
            Vcn vcn = Vcn.this;
            vcn.sendMessage(vcn.obtainMessage(5));
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    /* loaded from: classes2.dex */
    public static class Dependencies {
        public VcnGatewayConnection newVcnGatewayConnection(VcnContext vcnContext, ParcelUuid parcelUuid, TelephonySubscriptionTracker.TelephonySubscriptionSnapshot telephonySubscriptionSnapshot, VcnGatewayConnectionConfig vcnGatewayConnectionConfig, VcnGatewayStatusCallback vcnGatewayStatusCallback, boolean z) {
            return new VcnGatewayConnection(vcnContext, parcelUuid, telephonySubscriptionSnapshot, vcnGatewayConnectionConfig, vcnGatewayStatusCallback, z);
        }

        public VcnContentResolver newVcnContentResolver(VcnContext vcnContext) {
            return new VcnContentResolver(vcnContext);
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    /* loaded from: classes2.dex */
    public static class VcnContentResolver {
        public final ContentResolver mImpl;

        public VcnContentResolver(VcnContext vcnContext) {
            this.mImpl = vcnContext.getContext().getContentResolver();
        }

        public void registerContentObserver(Uri uri, boolean z, ContentObserver contentObserver) {
            this.mImpl.registerContentObserver(uri, z, contentObserver);
        }
    }
}
