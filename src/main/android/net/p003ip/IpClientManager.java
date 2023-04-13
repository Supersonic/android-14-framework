package android.net.p003ip;

import android.annotation.Hide;
import android.net.NattKeepalivePacketData;
import android.net.ProxyInfo;
import android.net.TcpKeepalivePacketData;
import android.net.TcpKeepalivePacketDataParcelable;
import android.net.shared.Layer2Information;
import android.net.shared.ProvisioningConfiguration;
import android.net.util.KeepalivePacketDataUtil;
import android.os.Binder;
import android.os.RemoteException;
import android.util.Log;
@Hide
/* renamed from: android.net.ip.IpClientManager */
/* loaded from: classes.dex */
public class IpClientManager {
    private final IIpClient mIpClient;
    private final String mTag;

    public IpClientManager(IIpClient iIpClient, String str) {
        this.mIpClient = iIpClient;
        this.mTag = str;
    }

    public IpClientManager(IIpClient iIpClient) {
        this(iIpClient, IpClientManager.class.getSimpleName());
    }

    private void log(String str, Throwable th) {
        Log.e(this.mTag, str, th);
    }

    public boolean completedPreDhcpAction() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mIpClient.completedPreDhcpAction();
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error completing PreDhcpAction", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean confirmConfiguration() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mIpClient.confirmConfiguration();
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error confirming IpClient configuration", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean readPacketFilterComplete(byte[] bArr) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mIpClient.readPacketFilterComplete(bArr);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error notifying IpClient of packet filter read", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean shutdown() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mIpClient.shutdown();
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error shutting down IpClient", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean startProvisioning(ProvisioningConfiguration provisioningConfiguration) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mIpClient.startProvisioning(provisioningConfiguration.toStableParcelable());
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error starting IpClient provisioning", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean stop() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mIpClient.stop();
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error stopping IpClient", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean setTcpBufferSizes(String str) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mIpClient.setTcpBufferSizes(str);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error setting IpClient TCP buffer sizes", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean setHttpProxy(ProxyInfo proxyInfo) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mIpClient.setHttpProxy(proxyInfo);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error setting IpClient proxy", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean setMulticastFilter(boolean z) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mIpClient.setMulticastFilter(z);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error setting multicast filter", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean addKeepalivePacketFilter(int i, TcpKeepalivePacketData tcpKeepalivePacketData) {
        return addKeepalivePacketFilter(i, KeepalivePacketDataUtil.toStableParcelable(tcpKeepalivePacketData));
    }

    @Deprecated
    public boolean addKeepalivePacketFilter(int i, TcpKeepalivePacketDataParcelable tcpKeepalivePacketDataParcelable) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mIpClient.addKeepalivePacketFilter(i, tcpKeepalivePacketDataParcelable);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error adding Keepalive Packet Filter ", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean addKeepalivePacketFilter(int i, NattKeepalivePacketData nattKeepalivePacketData) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mIpClient.addNattKeepalivePacketFilter(i, KeepalivePacketDataUtil.toStableParcelable(nattKeepalivePacketData));
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error adding NAT-T Keepalive Packet Filter ", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean removeKeepalivePacketFilter(int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mIpClient.removeKeepalivePacketFilter(i);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error removing Keepalive Packet Filter ", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean setL2KeyAndGroupHint(String str, String str2) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mIpClient.setL2KeyAndGroupHint(str, str2);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Failed setL2KeyAndGroupHint", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean notifyPreconnectionComplete(boolean z) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mIpClient.notifyPreconnectionComplete(z);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error notifying IpClient Preconnection completed", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean updateLayer2Information(Layer2Information layer2Information) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mIpClient.updateLayer2Information(layer2Information.toStableParcelable());
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error updating layer2 information", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }
}
