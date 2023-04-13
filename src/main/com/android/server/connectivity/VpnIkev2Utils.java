package com.android.server.connectivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Ikev2VpnProfile;
import android.net.InetAddresses;
import android.net.IpPrefix;
import android.net.IpSecTransform;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.RouteInfo;
import android.net.eap.EapSessionConfig;
import android.net.ipsec.ike.ChildSaProposal;
import android.net.ipsec.ike.ChildSessionCallback;
import android.net.ipsec.ike.ChildSessionConfiguration;
import android.net.ipsec.ike.ChildSessionParams;
import android.net.ipsec.ike.IkeFqdnIdentification;
import android.net.ipsec.ike.IkeIdentification;
import android.net.ipsec.ike.IkeIpv4AddrIdentification;
import android.net.ipsec.ike.IkeIpv6AddrIdentification;
import android.net.ipsec.ike.IkeKeyIdIdentification;
import android.net.ipsec.ike.IkeRfc822AddrIdentification;
import android.net.ipsec.ike.IkeSaProposal;
import android.net.ipsec.ike.IkeSessionCallback;
import android.net.ipsec.ike.IkeSessionConfiguration;
import android.net.ipsec.ike.IkeSessionConnectionInfo;
import android.net.ipsec.ike.IkeSessionParams;
import android.net.ipsec.ike.IkeTrafficSelector;
import android.net.ipsec.ike.TunnelModeChildSessionParams;
import android.net.ipsec.ike.exceptions.IkeException;
import android.net.ipsec.ike.exceptions.IkeProtocolException;
import android.system.OsConstants;
import android.util.Log;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.HexDump;
import com.android.internal.util.jobs.XmlUtils;
import com.android.net.module.util.IpRange;
import com.android.server.connectivity.Vpn;
import com.android.server.connectivity.VpnIkev2Utils;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class VpnIkev2Utils {
    public static final String TAG = "VpnIkev2Utils";

    public static IkeSessionParams.Builder makeIkeSessionParamsBuilder(Context context, Ikev2VpnProfile ikev2VpnProfile, Network network) {
        IkeIdentification parseIkeIdentification = parseIkeIdentification(ikev2VpnProfile.getUserIdentity());
        IkeSessionParams.Builder remoteIdentification = new IkeSessionParams.Builder(context).setServerHostname(ikev2VpnProfile.getServerAddr()).setNetwork(network).addIkeOption(2).setLocalIdentification(parseIkeIdentification).setRemoteIdentification(parseIkeIdentification(ikev2VpnProfile.getServerAddr()));
        setIkeAuth(ikev2VpnProfile, remoteIdentification);
        for (IkeSaProposal ikeSaProposal : getIkeSaProposals()) {
            remoteIdentification.addSaProposal(ikeSaProposal);
        }
        return remoteIdentification;
    }

    public static ChildSessionParams buildChildSessionParams(List<String> list) {
        TunnelModeChildSessionParams.Builder builder = new TunnelModeChildSessionParams.Builder();
        for (ChildSaProposal childSaProposal : getChildSaProposals(list)) {
            builder.addSaProposal(childSaProposal);
        }
        builder.addInternalAddressRequest(OsConstants.AF_INET);
        builder.addInternalAddressRequest(OsConstants.AF_INET6);
        builder.addInternalDnsServerRequest(OsConstants.AF_INET);
        builder.addInternalDnsServerRequest(OsConstants.AF_INET6);
        return builder.build();
    }

    public static void setIkeAuth(Ikev2VpnProfile ikev2VpnProfile, IkeSessionParams.Builder builder) {
        int type = ikev2VpnProfile.getType();
        if (type == 6) {
            builder.setAuthEap(ikev2VpnProfile.getServerRootCaCert(), new EapSessionConfig.Builder().setEapMsChapV2Config(ikev2VpnProfile.getUsername(), ikev2VpnProfile.getPassword()).build());
        } else if (type == 7) {
            builder.setAuthPsk(ikev2VpnProfile.getPresharedKey());
        } else if (type == 8) {
            builder.setAuthDigitalSignature(ikev2VpnProfile.getServerRootCaCert(), ikev2VpnProfile.getUserCert(), ikev2VpnProfile.getRsaPrivateKey());
        } else {
            throw new IllegalArgumentException("Unknown auth method set");
        }
    }

    public static List<IkeSaProposal> getIkeSaProposals() {
        ArrayList arrayList = new ArrayList();
        IkeSaProposal.Builder builder = new IkeSaProposal.Builder();
        builder.addEncryptionAlgorithm(13, 256);
        builder.addEncryptionAlgorithm(12, 256);
        builder.addEncryptionAlgorithm(13, FrameworkStatsLog.f392xcd34d435);
        builder.addEncryptionAlgorithm(12, FrameworkStatsLog.f392xcd34d435);
        builder.addEncryptionAlgorithm(13, 128);
        builder.addEncryptionAlgorithm(12, 128);
        builder.addIntegrityAlgorithm(14);
        builder.addIntegrityAlgorithm(13);
        builder.addIntegrityAlgorithm(12);
        builder.addIntegrityAlgorithm(5);
        builder.addIntegrityAlgorithm(8);
        IkeSaProposal.Builder builder2 = new IkeSaProposal.Builder();
        builder2.addEncryptionAlgorithm(28, 0);
        builder2.addEncryptionAlgorithm(20, 256);
        builder2.addEncryptionAlgorithm(19, 256);
        builder2.addEncryptionAlgorithm(18, 256);
        builder2.addEncryptionAlgorithm(20, FrameworkStatsLog.f392xcd34d435);
        builder2.addEncryptionAlgorithm(19, FrameworkStatsLog.f392xcd34d435);
        builder2.addEncryptionAlgorithm(18, FrameworkStatsLog.f392xcd34d435);
        builder2.addEncryptionAlgorithm(20, 128);
        builder2.addEncryptionAlgorithm(19, 128);
        builder2.addEncryptionAlgorithm(18, 128);
        for (IkeSaProposal.Builder builder3 : Arrays.asList(builder, builder2)) {
            builder3.addDhGroup(16);
            builder3.addDhGroup(31);
            builder3.addDhGroup(15);
            builder3.addDhGroup(14);
            builder3.addPseudorandomFunction(7);
            builder3.addPseudorandomFunction(6);
            builder3.addPseudorandomFunction(5);
            builder3.addPseudorandomFunction(4);
            builder3.addPseudorandomFunction(8);
            builder3.addPseudorandomFunction(2);
        }
        arrayList.add(builder.build());
        arrayList.add(builder2.build());
        return arrayList;
    }

    public static List<ChildSaProposal> getChildSaProposals(List<String> list) {
        ArrayList arrayList = new ArrayList();
        List<Integer> asList = Arrays.asList(256, Integer.valueOf((int) FrameworkStatsLog.f392xcd34d435), 128);
        if (Ikev2VpnProfile.hasNormalModeAlgorithms(list)) {
            ChildSaProposal.Builder builder = new ChildSaProposal.Builder();
            for (Integer num : asList) {
                int intValue = num.intValue();
                if (list.contains("rfc3686(ctr(aes))")) {
                    builder.addEncryptionAlgorithm(13, intValue);
                }
                if (list.contains("cbc(aes)")) {
                    builder.addEncryptionAlgorithm(12, intValue);
                }
            }
            if (list.contains("hmac(sha512)")) {
                builder.addIntegrityAlgorithm(14);
            }
            if (list.contains("hmac(sha384)")) {
                builder.addIntegrityAlgorithm(13);
            }
            if (list.contains("hmac(sha256)")) {
                builder.addIntegrityAlgorithm(12);
            }
            if (list.contains("xcbc(aes)")) {
                builder.addIntegrityAlgorithm(5);
            }
            if (list.contains("cmac(aes)")) {
                builder.addIntegrityAlgorithm(8);
            }
            if (builder.build().getIntegrityAlgorithms().isEmpty()) {
                Log.wtf(TAG, "Missing integrity algorithm when buildling Child SA proposal");
            } else {
                arrayList.add(builder.build());
            }
        }
        if (Ikev2VpnProfile.hasAeadAlgorithms(list)) {
            ChildSaProposal.Builder builder2 = new ChildSaProposal.Builder();
            if (list.contains("rfc7539esp(chacha20,poly1305)")) {
                builder2.addEncryptionAlgorithm(28, 0);
            }
            if (list.contains("rfc4106(gcm(aes))")) {
                builder2.addEncryptionAlgorithm(20, 256);
                builder2.addEncryptionAlgorithm(19, 256);
                builder2.addEncryptionAlgorithm(18, 256);
                builder2.addEncryptionAlgorithm(20, FrameworkStatsLog.f392xcd34d435);
                builder2.addEncryptionAlgorithm(19, FrameworkStatsLog.f392xcd34d435);
                builder2.addEncryptionAlgorithm(18, FrameworkStatsLog.f392xcd34d435);
                builder2.addEncryptionAlgorithm(20, 128);
                builder2.addEncryptionAlgorithm(19, 128);
                builder2.addEncryptionAlgorithm(18, 128);
            }
            arrayList.add(builder2.build());
        }
        return arrayList;
    }

    /* loaded from: classes.dex */
    public static class IkeSessionCallbackImpl implements IkeSessionCallback {
        public final Vpn.IkeV2VpnRunnerCallback mCallback;
        public final String mTag;
        public final int mToken;

        public IkeSessionCallbackImpl(String str, Vpn.IkeV2VpnRunnerCallback ikeV2VpnRunnerCallback, int i) {
            this.mTag = str;
            this.mCallback = ikeV2VpnRunnerCallback;
            this.mToken = i;
        }

        @Override // android.net.ipsec.ike.IkeSessionCallback
        public void onOpened(IkeSessionConfiguration ikeSessionConfiguration) {
            String str = this.mTag;
            Log.d(str, "IkeOpened for token " + this.mToken);
            this.mCallback.onIkeOpened(this.mToken, ikeSessionConfiguration);
        }

        @Override // android.net.ipsec.ike.IkeSessionCallback
        public void onClosed() {
            String str = this.mTag;
            Log.d(str, "IkeClosed for token " + this.mToken);
            this.mCallback.onSessionLost(this.mToken, null);
        }

        public void onClosedExceptionally(IkeException ikeException) {
            String str = this.mTag;
            Log.d(str, "IkeClosedExceptionally for token " + this.mToken, ikeException);
            this.mCallback.onSessionLost(this.mToken, ikeException);
        }

        public void onError(IkeProtocolException ikeProtocolException) {
            String str = this.mTag;
            Log.d(str, "IkeError for token " + this.mToken, ikeProtocolException);
        }

        public void onIkeSessionConnectionInfoChanged(IkeSessionConnectionInfo ikeSessionConnectionInfo) {
            String str = this.mTag;
            Log.d(str, "onIkeSessionConnectionInfoChanged for token " + this.mToken);
            this.mCallback.onIkeConnectionInfoChanged(this.mToken, ikeSessionConnectionInfo);
        }
    }

    /* loaded from: classes.dex */
    public static class ChildSessionCallbackImpl implements ChildSessionCallback {
        public final Vpn.IkeV2VpnRunnerCallback mCallback;
        public final String mTag;
        public final int mToken;

        public ChildSessionCallbackImpl(String str, Vpn.IkeV2VpnRunnerCallback ikeV2VpnRunnerCallback, int i) {
            this.mTag = str;
            this.mCallback = ikeV2VpnRunnerCallback;
            this.mToken = i;
        }

        @Override // android.net.ipsec.ike.ChildSessionCallback
        public void onOpened(ChildSessionConfiguration childSessionConfiguration) {
            String str = this.mTag;
            Log.d(str, "ChildOpened for token " + this.mToken);
            this.mCallback.onChildOpened(this.mToken, childSessionConfiguration);
        }

        @Override // android.net.ipsec.ike.ChildSessionCallback
        public void onClosed() {
            String str = this.mTag;
            Log.d(str, "ChildClosed for token " + this.mToken);
            this.mCallback.onSessionLost(this.mToken, null);
        }

        public void onClosedExceptionally(IkeException ikeException) {
            String str = this.mTag;
            Log.d(str, "ChildClosedExceptionally for token " + this.mToken, ikeException);
            this.mCallback.onSessionLost(this.mToken, ikeException);
        }

        @Override // android.net.ipsec.ike.ChildSessionCallback
        public void onIpSecTransformCreated(IpSecTransform ipSecTransform, int i) {
            String str = this.mTag;
            Log.d(str, "ChildTransformCreated; Direction: " + i + "; token " + this.mToken);
            this.mCallback.onChildTransformCreated(this.mToken, ipSecTransform, i);
        }

        @Override // android.net.ipsec.ike.ChildSessionCallback
        public void onIpSecTransformDeleted(IpSecTransform ipSecTransform, int i) {
            String str = this.mTag;
            Log.d(str, "ChildTransformDeleted; Direction: " + i + "; for token " + this.mToken);
        }

        public void onIpSecTransformsMigrated(IpSecTransform ipSecTransform, IpSecTransform ipSecTransform2) {
            String str = this.mTag;
            Log.d(str, "ChildTransformsMigrated; token " + this.mToken);
            this.mCallback.onChildMigrated(this.mToken, ipSecTransform, ipSecTransform2);
        }
    }

    /* loaded from: classes.dex */
    public static class Ikev2VpnNetworkCallback extends ConnectivityManager.NetworkCallback {
        public final Vpn.IkeV2VpnRunnerCallback mCallback;
        public final Executor mExecutor;
        public final String mTag;

        public Ikev2VpnNetworkCallback(String str, Vpn.IkeV2VpnRunnerCallback ikeV2VpnRunnerCallback, Executor executor) {
            this.mTag = str;
            this.mCallback = ikeV2VpnRunnerCallback;
            this.mExecutor = executor;
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onAvailable(final Network network) {
            String str = this.mTag;
            Log.d(str, "onAvailable called for network: " + network);
            this.mExecutor.execute(new Runnable() { // from class: com.android.server.connectivity.VpnIkev2Utils$Ikev2VpnNetworkCallback$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    VpnIkev2Utils.Ikev2VpnNetworkCallback.this.lambda$onAvailable$0(network);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAvailable$0(Network network) {
            this.mCallback.onDefaultNetworkChanged(network);
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onCapabilitiesChanged(Network network, final NetworkCapabilities networkCapabilities) {
            String str = this.mTag;
            Log.d(str, "NC changed for net " + network + " : " + networkCapabilities);
            this.mExecutor.execute(new Runnable() { // from class: com.android.server.connectivity.VpnIkev2Utils$Ikev2VpnNetworkCallback$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    VpnIkev2Utils.Ikev2VpnNetworkCallback.this.lambda$onCapabilitiesChanged$1(networkCapabilities);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCapabilitiesChanged$1(NetworkCapabilities networkCapabilities) {
            this.mCallback.onDefaultNetworkCapabilitiesChanged(networkCapabilities);
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onLinkPropertiesChanged(Network network, final LinkProperties linkProperties) {
            String str = this.mTag;
            Log.d(str, "LP changed for net " + network + " : " + linkProperties);
            this.mExecutor.execute(new Runnable() { // from class: com.android.server.connectivity.VpnIkev2Utils$Ikev2VpnNetworkCallback$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    VpnIkev2Utils.Ikev2VpnNetworkCallback.this.lambda$onLinkPropertiesChanged$2(linkProperties);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onLinkPropertiesChanged$2(LinkProperties linkProperties) {
            this.mCallback.onDefaultNetworkLinkPropertiesChanged(linkProperties);
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onLost(final Network network) {
            String str = this.mTag;
            Log.d(str, "onLost called for network: " + network);
            this.mExecutor.execute(new Runnable() { // from class: com.android.server.connectivity.VpnIkev2Utils$Ikev2VpnNetworkCallback$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    VpnIkev2Utils.Ikev2VpnNetworkCallback.this.lambda$onLost$3(network);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onLost$3(Network network) {
            this.mCallback.onDefaultNetworkLost(network);
        }
    }

    public static IkeIdentification parseIkeIdentification(String str) {
        if (str.contains("@")) {
            if (str.startsWith("@#")) {
                return new IkeKeyIdIdentification(HexDump.hexStringToByteArray(str.substring(2)));
            }
            if (str.startsWith("@@")) {
                return new IkeRfc822AddrIdentification(str.substring(2));
            }
            if (str.startsWith("@")) {
                return new IkeFqdnIdentification(str.substring(1));
            }
            return new IkeRfc822AddrIdentification(str);
        } else if (InetAddresses.isNumericAddress(str)) {
            InetAddress parseNumericAddress = InetAddresses.parseNumericAddress(str);
            if (parseNumericAddress instanceof Inet4Address) {
                return new IkeIpv4AddrIdentification((Inet4Address) parseNumericAddress);
            }
            if (parseNumericAddress instanceof Inet6Address) {
                return new IkeIpv6AddrIdentification((Inet6Address) parseNumericAddress);
            }
            throw new IllegalArgumentException("IP version not supported");
        } else if (str.contains(XmlUtils.STRING_ARRAY_SEPARATOR)) {
            return new IkeKeyIdIdentification(str.getBytes());
        } else {
            return new IkeFqdnIdentification(str);
        }
    }

    public static Collection<RouteInfo> getRoutesFromTrafficSelectors(List<IkeTrafficSelector> list) {
        HashSet hashSet = new HashSet();
        for (IkeTrafficSelector ikeTrafficSelector : list) {
            for (IpPrefix ipPrefix : new IpRange(ikeTrafficSelector.startingAddress, ikeTrafficSelector.endingAddress).asIpPrefixes()) {
                hashSet.add(new RouteInfo(ipPrefix, null, null, 1));
            }
        }
        return hashSet;
    }
}
