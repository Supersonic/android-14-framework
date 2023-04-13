package android.net.p003ip;

import android.content.Context;
import android.net.DhcpResultsParcelable;
import android.net.Layer2PacketParcelable;
import android.net.LinkProperties;
import android.net.networkstack.ModuleNetworkStackClient;
import android.net.networkstack.aidl.p004ip.ReachabilityLossInfoParcelable;
import android.net.p003ip.IIpClientCallbacks;
import android.os.ConditionVariable;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
/* renamed from: android.net.ip.IpClientUtil */
/* loaded from: classes.dex */
public class IpClientUtil {
    public static final String DUMP_ARG = "ipclient";

    /* renamed from: android.net.ip.IpClientUtil$WaitForProvisioningCallbacks */
    /* loaded from: classes.dex */
    public static class WaitForProvisioningCallbacks extends IpClientCallbacks {
        private final ConditionVariable mCV = new ConditionVariable();
        private LinkProperties mCallbackLinkProperties;

        public LinkProperties waitForProvisioning() {
            this.mCV.block();
            return this.mCallbackLinkProperties;
        }

        @Override // android.net.p003ip.IpClientCallbacks
        public void onProvisioningSuccess(LinkProperties linkProperties) {
            this.mCallbackLinkProperties = linkProperties;
            this.mCV.open();
        }

        @Override // android.net.p003ip.IpClientCallbacks
        public void onProvisioningFailure(LinkProperties linkProperties) {
            this.mCallbackLinkProperties = null;
            this.mCV.open();
        }
    }

    public static void makeIpClient(Context context, String str, IpClientCallbacks ipClientCallbacks) {
        ModuleNetworkStackClient.getInstance(context).makeIpClient(str, new IpClientCallbacksProxy(ipClientCallbacks));
    }

    /* renamed from: android.net.ip.IpClientUtil$IpClientCallbacksProxy */
    /* loaded from: classes.dex */
    public static class IpClientCallbacksProxy extends IIpClientCallbacks.Stub {
        public final IpClientCallbacks mCb;

        @Override // android.net.p003ip.IIpClientCallbacks
        public String getInterfaceHash() {
            return "a7ed197af8532361170ac44595771304b7f04034";
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public int getInterfaceVersion() {
            return 16;
        }

        public IpClientCallbacksProxy(IpClientCallbacks ipClientCallbacks) {
            this.mCb = ipClientCallbacks;
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void onIpClientCreated(IIpClient iIpClient) {
            this.mCb.onIpClientCreated(iIpClient);
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void onPreDhcpAction() {
            this.mCb.onPreDhcpAction();
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void onPostDhcpAction() {
            this.mCb.onPostDhcpAction();
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void onNewDhcpResults(DhcpResultsParcelable dhcpResultsParcelable) {
            this.mCb.onNewDhcpResults(dhcpResultsParcelable);
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void onProvisioningSuccess(LinkProperties linkProperties) {
            this.mCb.onProvisioningSuccess(new LinkProperties(linkProperties));
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void onProvisioningFailure(LinkProperties linkProperties) {
            this.mCb.onProvisioningFailure(new LinkProperties(linkProperties));
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void onLinkPropertiesChange(LinkProperties linkProperties) {
            this.mCb.onLinkPropertiesChange(new LinkProperties(linkProperties));
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void onReachabilityLost(String str) {
            this.mCb.onReachabilityLost(str);
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void onQuit() {
            this.mCb.onQuit();
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void installPacketFilter(byte[] bArr) {
            this.mCb.installPacketFilter(bArr);
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void startReadPacketFilter() {
            this.mCb.startReadPacketFilter();
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void setFallbackMulticastFilter(boolean z) {
            this.mCb.setFallbackMulticastFilter(z);
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void setNeighborDiscoveryOffload(boolean z) {
            this.mCb.setNeighborDiscoveryOffload(z);
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void onPreconnectionStart(List<Layer2PacketParcelable> list) {
            this.mCb.onPreconnectionStart(list);
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void onReachabilityFailure(ReachabilityLossInfoParcelable reachabilityLossInfoParcelable) {
            this.mCb.onReachabilityFailure(reachabilityLossInfoParcelable);
        }
    }

    public static void dumpIpClient(IIpClient iIpClient, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("IpClient logs have moved to dumpsys network_stack");
    }
}
