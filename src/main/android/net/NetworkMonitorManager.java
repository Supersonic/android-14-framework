package android.net;

import android.annotation.Hide;
import android.net.networkstack.aidl.NetworkMonitorParameters;
import android.os.Binder;
import android.os.RemoteException;
import android.util.Log;
@Hide
/* loaded from: classes.dex */
public class NetworkMonitorManager {
    private final INetworkMonitor mNetworkMonitor;
    private final String mTag;

    public NetworkMonitorManager(INetworkMonitor iNetworkMonitor, String str) {
        this.mNetworkMonitor = iNetworkMonitor;
        this.mTag = str;
    }

    public NetworkMonitorManager(INetworkMonitor iNetworkMonitor) {
        this(iNetworkMonitor, NetworkMonitorManager.class.getSimpleName());
    }

    private void log(String str, Throwable th) {
        Log.e(this.mTag, str, th);
    }

    public boolean start() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mNetworkMonitor.start();
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error in start", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean launchCaptivePortalApp() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mNetworkMonitor.launchCaptivePortalApp();
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error in launchCaptivePortalApp", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean notifyCaptivePortalAppFinished(int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mNetworkMonitor.notifyCaptivePortalAppFinished(i);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error in notifyCaptivePortalAppFinished", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean setAcceptPartialConnectivity() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mNetworkMonitor.setAcceptPartialConnectivity();
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error in setAcceptPartialConnectivity", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean forceReevaluation(int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mNetworkMonitor.forceReevaluation(i);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error in forceReevaluation", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean notifyPrivateDnsChanged(PrivateDnsConfigParcel privateDnsConfigParcel) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mNetworkMonitor.notifyPrivateDnsChanged(privateDnsConfigParcel);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error in notifyPrivateDnsChanged", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean notifyDnsResponse(int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mNetworkMonitor.notifyDnsResponse(i);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error in notifyDnsResponse", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    @Deprecated
    public boolean notifyNetworkConnected(LinkProperties linkProperties, NetworkCapabilities networkCapabilities) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mNetworkMonitor.notifyNetworkConnected(linkProperties, networkCapabilities);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error in notifyNetworkConnected", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean notifyNetworkConnected(NetworkMonitorParameters networkMonitorParameters) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mNetworkMonitor.notifyNetworkConnectedParcel(networkMonitorParameters);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error in notifyNetworkConnected", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean notifyNetworkDisconnected() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mNetworkMonitor.notifyNetworkDisconnected();
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error in notifyNetworkDisconnected", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean notifyLinkPropertiesChanged(LinkProperties linkProperties) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mNetworkMonitor.notifyLinkPropertiesChanged(linkProperties);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error in notifyLinkPropertiesChanged", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean notifyNetworkCapabilitiesChanged(NetworkCapabilities networkCapabilities) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mNetworkMonitor.notifyNetworkCapabilitiesChanged(networkCapabilities);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (RemoteException e) {
                log("Error in notifyNetworkCapabilitiesChanged", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }
}
