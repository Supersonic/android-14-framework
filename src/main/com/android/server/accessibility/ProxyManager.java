package com.android.server.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.AccessibilityTrace;
import android.accessibilityservice.IAccessibilityServiceClient;
import android.content.ComponentName;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.SparseArray;
import android.view.Display;
import android.view.accessibility.AccessibilityEvent;
import com.android.server.accessibility.AbstractAccessibilityServiceConnection;
import com.android.server.p014wm.WindowManagerInternal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
/* loaded from: classes.dex */
public class ProxyManager {
    public AccessibilityInputFilter mA11yInputFilter;
    public AccessibilityWindowManager mA11yWindowManager;
    public final Context mContext;
    public final Object mLock;
    public int mLastState = -1;
    public SparseArray<ProxyAccessibilityServiceConnection> mProxyA11yServiceConnections = new SparseArray<>();

    public ProxyManager(Object obj, AccessibilityWindowManager accessibilityWindowManager, Context context) {
        this.mLock = obj;
        this.mA11yWindowManager = accessibilityWindowManager;
        this.mContext = context;
    }

    public void registerProxy(final IAccessibilityServiceClient iAccessibilityServiceClient, final int i, Context context, int i2, Handler handler, AccessibilitySecurityPolicy accessibilitySecurityPolicy, AbstractAccessibilityServiceConnection.SystemSupport systemSupport, AccessibilityTrace accessibilityTrace, WindowManagerInternal windowManagerInternal) throws RemoteException {
        AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
        accessibilityServiceInfo.setCapabilities(3);
        accessibilityServiceInfo.setComponentName(new ComponentName("ProxyPackage", "ProxyClass" + i));
        ProxyAccessibilityServiceConnection proxyAccessibilityServiceConnection = new ProxyAccessibilityServiceConnection(context, accessibilityServiceInfo.getComponentName(), accessibilityServiceInfo, i2, handler, this.mLock, accessibilitySecurityPolicy, systemSupport, accessibilityTrace, windowManagerInternal, this.mA11yWindowManager, i);
        synchronized (this.mLock) {
            this.mProxyA11yServiceConnections.put(i, proxyAccessibilityServiceConnection);
        }
        iAccessibilityServiceClient.asBinder().linkToDeath(new IBinder.DeathRecipient() { // from class: com.android.server.accessibility.ProxyManager.1
            @Override // android.os.IBinder.DeathRecipient
            public void binderDied() {
                iAccessibilityServiceClient.asBinder().unlinkToDeath(this, 0);
                ProxyManager.this.clearConnection(i);
            }
        }, 0);
        synchronized (this.mLock) {
            proxyAccessibilityServiceConnection.mSystemSupport.onClientChangeLocked(true);
        }
        AccessibilityInputFilter accessibilityInputFilter = this.mA11yInputFilter;
        if (accessibilityInputFilter != null) {
            accessibilityInputFilter.disableFeaturesForDisplayIfInstalled(i);
        }
        proxyAccessibilityServiceConnection.initializeServiceInterface(iAccessibilityServiceClient);
    }

    public boolean unregisterProxy(int i) {
        return clearConnection(i);
    }

    public final boolean clearConnection(int i) {
        boolean z;
        Display display;
        synchronized (this.mLock) {
            if (this.mProxyA11yServiceConnections.contains(i)) {
                this.mProxyA11yServiceConnections.remove(i);
                z = true;
            } else {
                z = false;
            }
        }
        if (z) {
            this.mA11yWindowManager.stopTrackingDisplayProxy(i);
            if (this.mA11yInputFilter != null && (display = ((DisplayManager) this.mContext.getSystemService("display")).getDisplay(i)) != null) {
                this.mA11yInputFilter.enableFeaturesForDisplayIfInstalled(display);
            }
        }
        return z;
    }

    public boolean isProxyed(int i) {
        boolean contains;
        synchronized (this.mLock) {
            contains = this.mProxyA11yServiceConnections.contains(i);
        }
        return contains;
    }

    public void sendAccessibilityEventLocked(AccessibilityEvent accessibilityEvent) {
        ProxyAccessibilityServiceConnection proxyAccessibilityServiceConnection = this.mProxyA11yServiceConnections.get(accessibilityEvent.getDisplayId());
        if (proxyAccessibilityServiceConnection != null) {
            proxyAccessibilityServiceConnection.notifyAccessibilityEvent(accessibilityEvent);
        }
    }

    public boolean canRetrieveInteractiveWindowsLocked() {
        for (int i = 0; i < this.mProxyA11yServiceConnections.size(); i++) {
            if (this.mProxyA11yServiceConnections.valueAt(i).mRetrieveInteractiveWindows) {
                return true;
            }
        }
        return false;
    }

    public int getStateLocked() {
        int i = this.mProxyA11yServiceConnections.size() > 0 ? 1 : 0;
        for (int i2 = 0; i2 < this.mProxyA11yServiceConnections.size(); i2++) {
            if (this.mProxyA11yServiceConnections.valueAt(i2).mRequestTouchExplorationMode) {
                i |= 2;
            }
        }
        return i;
    }

    public int getLastSentStateLocked() {
        return this.mLastState;
    }

    public void setLastStateLocked(int i) {
        this.mLastState = i;
    }

    public int getRelevantEventTypesLocked() {
        int i = 0;
        for (int i2 = 0; i2 < this.mProxyA11yServiceConnections.size(); i2++) {
            i |= this.mProxyA11yServiceConnections.valueAt(i2).getRelevantEventTypes();
        }
        return i;
    }

    public int getNumProxysLocked() {
        return this.mProxyA11yServiceConnections.size();
    }

    public void addServiceInterfacesLocked(List<IAccessibilityServiceClient> list) {
        for (int i = 0; i < this.mProxyA11yServiceConnections.size(); i++) {
            ProxyAccessibilityServiceConnection valueAt = this.mProxyA11yServiceConnections.valueAt(i);
            IBinder iBinder = valueAt.mService;
            IAccessibilityServiceClient iAccessibilityServiceClient = valueAt.mServiceInterface;
            if (iBinder != null && iAccessibilityServiceClient != null) {
                list.add(iAccessibilityServiceClient);
            }
        }
    }

    public void clearCacheLocked() {
        for (int i = 0; i < this.mProxyA11yServiceConnections.size(); i++) {
            this.mProxyA11yServiceConnections.valueAt(i).notifyClearAccessibilityNodeInfoCache();
        }
    }

    public void setAccessibilityInputFilter(AccessibilityInputFilter accessibilityInputFilter) {
        this.mA11yInputFilter = accessibilityInputFilter;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        synchronized (this.mLock) {
            printWriter.println();
            printWriter.println("Proxy manager state:");
            printWriter.println("    Number of proxy connections: " + this.mProxyA11yServiceConnections.size());
            printWriter.println("    Registered proxy connections:");
            for (int i = 0; i < this.mProxyA11yServiceConnections.size(); i++) {
                ProxyAccessibilityServiceConnection valueAt = this.mProxyA11yServiceConnections.valueAt(i);
                if (valueAt != null) {
                    valueAt.dump(fileDescriptor, printWriter, strArr);
                }
            }
        }
    }
}
