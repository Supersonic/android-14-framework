package android.net.p003ip;

import android.net.DhcpResultsParcelable;
import android.net.Layer2PacketParcelable;
import android.net.LinkProperties;
import android.net.networkstack.aidl.p004ip.ReachabilityLossInfoParcelable;
import java.util.List;
/* renamed from: android.net.ip.IpClientCallbacks */
/* loaded from: classes.dex */
public class IpClientCallbacks {
    public void installPacketFilter(byte[] bArr) {
    }

    public void onIpClientCreated(IIpClient iIpClient) {
    }

    public void onLinkPropertiesChange(LinkProperties linkProperties) {
    }

    public void onNewDhcpResults(DhcpResultsParcelable dhcpResultsParcelable) {
    }

    public void onPostDhcpAction() {
    }

    public void onPreDhcpAction() {
    }

    public void onPreconnectionStart(List<Layer2PacketParcelable> list) {
    }

    public void onProvisioningFailure(LinkProperties linkProperties) {
    }

    public void onProvisioningSuccess(LinkProperties linkProperties) {
    }

    public void onQuit() {
    }

    public void onReachabilityLost(String str) {
    }

    public void setFallbackMulticastFilter(boolean z) {
    }

    public void setNeighborDiscoveryOffload(boolean z) {
    }

    public void startReadPacketFilter() {
    }

    public void onReachabilityFailure(ReachabilityLossInfoParcelable reachabilityLossInfoParcelable) {
        onReachabilityLost(reachabilityLossInfoParcelable.message);
    }
}
