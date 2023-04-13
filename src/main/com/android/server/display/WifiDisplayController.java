package com.android.server.display;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.display.WifiDisplay;
import android.hardware.display.WifiDisplaySessionInfo;
import android.media.RemoteDisplay;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pWfdInfo;
import android.os.Handler;
import android.provider.Settings;
import android.util.Slog;
import android.view.Surface;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.jobs.XmlUtils;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;
/* loaded from: classes.dex */
public final class WifiDisplayController implements DumpUtils.Dump {
    public WifiDisplay mAdvertisedDisplay;
    public int mAdvertisedDisplayFlags;
    public int mAdvertisedDisplayHeight;
    public Surface mAdvertisedDisplaySurface;
    public int mAdvertisedDisplayWidth;
    public WifiP2pDevice mCancelingDevice;
    public WifiP2pDevice mConnectedDevice;
    public WifiP2pGroup mConnectedDeviceGroupInfo;
    public WifiP2pDevice mConnectingDevice;
    public int mConnectionRetriesLeft;
    public final Context mContext;
    public WifiP2pDevice mDesiredDevice;
    public WifiP2pDevice mDisconnectingDevice;
    public boolean mDiscoverPeersInProgress;
    public final Handler mHandler;
    public final Listener mListener;
    public NetworkInfo mNetworkInfo;
    public RemoteDisplay mRemoteDisplay;
    public boolean mRemoteDisplayConnected;
    public String mRemoteDisplayInterface;
    public boolean mScanRequested;
    public WifiP2pDevice mThisDevice;
    public boolean mWfdEnabled;
    public boolean mWfdEnabling;
    public boolean mWifiDisplayCertMode;
    public boolean mWifiDisplayOnSetting;
    public WifiP2pManager.Channel mWifiP2pChannel;
    public boolean mWifiP2pEnabled;
    public WifiP2pManager mWifiP2pManager;
    public final BroadcastReceiver mWifiP2pReceiver;
    public final ArrayList<WifiP2pDevice> mAvailableWifiDisplayPeers = new ArrayList<>();
    public int mWifiDisplayWpsConfig = 4;
    public final Runnable mDiscoverPeers = new Runnable() { // from class: com.android.server.display.WifiDisplayController.16
        @Override // java.lang.Runnable
        public void run() {
            WifiDisplayController.this.tryDiscoverPeers();
        }
    };
    public final Runnable mConnectionTimeout = new Runnable() { // from class: com.android.server.display.WifiDisplayController.17
        @Override // java.lang.Runnable
        public void run() {
            if (WifiDisplayController.this.mConnectingDevice == null || WifiDisplayController.this.mConnectingDevice != WifiDisplayController.this.mDesiredDevice) {
                return;
            }
            Slog.i("WifiDisplayController", "Timed out waiting for Wifi display connection after 30 seconds: " + WifiDisplayController.this.mConnectingDevice.deviceName);
            WifiDisplayController.this.handleConnectionFailure(true);
        }
    };
    public final Runnable mRtspTimeout = new Runnable() { // from class: com.android.server.display.WifiDisplayController.18
        @Override // java.lang.Runnable
        public void run() {
            if (WifiDisplayController.this.mConnectedDevice == null || WifiDisplayController.this.mRemoteDisplay == null || WifiDisplayController.this.mRemoteDisplayConnected) {
                return;
            }
            Slog.i("WifiDisplayController", "Timed out waiting for Wifi display RTSP connection after 30 seconds: " + WifiDisplayController.this.mConnectedDevice.deviceName);
            WifiDisplayController.this.handleConnectionFailure(true);
        }
    };

    /* loaded from: classes.dex */
    public interface Listener {
        void onDisplayChanged(WifiDisplay wifiDisplay);

        void onDisplayConnected(WifiDisplay wifiDisplay, Surface surface, int i, int i2, int i3);

        void onDisplayConnecting(WifiDisplay wifiDisplay);

        void onDisplayConnectionFailed();

        void onDisplayDisconnected();

        void onDisplaySessionInfo(WifiDisplaySessionInfo wifiDisplaySessionInfo);

        void onFeatureStateChanged(int i);

        void onScanFinished();

        void onScanResults(WifiDisplay[] wifiDisplayArr);

        void onScanStarted();
    }

    public static boolean isPrimarySinkDeviceType(int i) {
        return i == 1 || i == 3;
    }

    public WifiDisplayController(Context context, Handler handler, Listener listener) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.display.WifiDisplayController.21
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if (action.equals("android.net.wifi.p2p.STATE_CHANGED")) {
                    WifiDisplayController.this.handleStateChanged(intent.getIntExtra("wifi_p2p_state", 1) == 2);
                } else if (action.equals("android.net.wifi.p2p.PEERS_CHANGED")) {
                    WifiDisplayController.this.handlePeersChanged();
                } else if (action.equals("android.net.wifi.p2p.CONNECTION_STATE_CHANGE")) {
                    WifiDisplayController.this.handleConnectionChanged((NetworkInfo) intent.getParcelableExtra("networkInfo", NetworkInfo.class));
                } else if (action.equals("android.net.wifi.p2p.THIS_DEVICE_CHANGED")) {
                    WifiDisplayController.this.mThisDevice = (WifiP2pDevice) intent.getParcelableExtra("wifiP2pDevice", WifiP2pDevice.class);
                }
            }
        };
        this.mWifiP2pReceiver = broadcastReceiver;
        this.mContext = context;
        this.mHandler = handler;
        this.mListener = listener;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.p2p.STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.p2p.PEERS_CHANGED");
        intentFilter.addAction("android.net.wifi.p2p.CONNECTION_STATE_CHANGE");
        intentFilter.addAction("android.net.wifi.p2p.THIS_DEVICE_CHANGED");
        context.registerReceiver(broadcastReceiver, intentFilter, null, handler);
        ContentObserver contentObserver = new ContentObserver(handler) { // from class: com.android.server.display.WifiDisplayController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                WifiDisplayController.this.updateSettings();
            }
        };
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.registerContentObserver(Settings.Global.getUriFor("wifi_display_on"), false, contentObserver);
        contentResolver.registerContentObserver(Settings.Global.getUriFor("wifi_display_certification_on"), false, contentObserver);
        contentResolver.registerContentObserver(Settings.Global.getUriFor("wifi_display_wps_config"), false, contentObserver);
        updateSettings();
    }

    public final void retrieveWifiP2pManagerAndChannel() {
        WifiP2pManager wifiP2pManager;
        if (this.mWifiP2pManager == null) {
            this.mWifiP2pManager = (WifiP2pManager) this.mContext.getSystemService("wifip2p");
        }
        if (this.mWifiP2pChannel != null || (wifiP2pManager = this.mWifiP2pManager) == null) {
            return;
        }
        this.mWifiP2pChannel = wifiP2pManager.initialize(this.mContext, this.mHandler.getLooper(), null);
    }

    public final void updateSettings() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        this.mWifiDisplayOnSetting = Settings.Global.getInt(contentResolver, "wifi_display_on", 0) != 0;
        boolean z = Settings.Global.getInt(contentResolver, "wifi_display_certification_on", 0) != 0;
        this.mWifiDisplayCertMode = z;
        this.mWifiDisplayWpsConfig = 4;
        if (z) {
            this.mWifiDisplayWpsConfig = Settings.Global.getInt(contentResolver, "wifi_display_wps_config", 4);
        }
        updateWfdEnableState();
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.println("mWifiDisplayOnSetting=" + this.mWifiDisplayOnSetting);
        printWriter.println("mWifiP2pEnabled=" + this.mWifiP2pEnabled);
        printWriter.println("mWfdEnabled=" + this.mWfdEnabled);
        printWriter.println("mWfdEnabling=" + this.mWfdEnabling);
        printWriter.println("mNetworkInfo=" + this.mNetworkInfo);
        printWriter.println("mScanRequested=" + this.mScanRequested);
        printWriter.println("mDiscoverPeersInProgress=" + this.mDiscoverPeersInProgress);
        printWriter.println("mDesiredDevice=" + describeWifiP2pDevice(this.mDesiredDevice));
        printWriter.println("mConnectingDisplay=" + describeWifiP2pDevice(this.mConnectingDevice));
        printWriter.println("mDisconnectingDisplay=" + describeWifiP2pDevice(this.mDisconnectingDevice));
        printWriter.println("mCancelingDisplay=" + describeWifiP2pDevice(this.mCancelingDevice));
        printWriter.println("mConnectedDevice=" + describeWifiP2pDevice(this.mConnectedDevice));
        printWriter.println("mConnectionRetriesLeft=" + this.mConnectionRetriesLeft);
        printWriter.println("mRemoteDisplay=" + this.mRemoteDisplay);
        printWriter.println("mRemoteDisplayInterface=" + this.mRemoteDisplayInterface);
        printWriter.println("mRemoteDisplayConnected=" + this.mRemoteDisplayConnected);
        printWriter.println("mAdvertisedDisplay=" + this.mAdvertisedDisplay);
        printWriter.println("mAdvertisedDisplaySurface=" + this.mAdvertisedDisplaySurface);
        printWriter.println("mAdvertisedDisplayWidth=" + this.mAdvertisedDisplayWidth);
        printWriter.println("mAdvertisedDisplayHeight=" + this.mAdvertisedDisplayHeight);
        printWriter.println("mAdvertisedDisplayFlags=" + this.mAdvertisedDisplayFlags);
        printWriter.println("mAvailableWifiDisplayPeers: size=" + this.mAvailableWifiDisplayPeers.size());
        Iterator<WifiP2pDevice> it = this.mAvailableWifiDisplayPeers.iterator();
        while (it.hasNext()) {
            printWriter.println("  " + describeWifiP2pDevice(it.next()));
        }
    }

    public void requestStartScan() {
        if (this.mScanRequested) {
            return;
        }
        this.mScanRequested = true;
        updateScanState();
    }

    public void requestStopScan() {
        if (this.mScanRequested) {
            this.mScanRequested = false;
            updateScanState();
        }
    }

    public void requestConnect(String str) {
        Iterator<WifiP2pDevice> it = this.mAvailableWifiDisplayPeers.iterator();
        while (it.hasNext()) {
            WifiP2pDevice next = it.next();
            if (next.deviceAddress.equals(str)) {
                connect(next);
            }
        }
    }

    public void requestPause() {
        RemoteDisplay remoteDisplay = this.mRemoteDisplay;
        if (remoteDisplay != null) {
            remoteDisplay.pause();
        }
    }

    public void requestResume() {
        RemoteDisplay remoteDisplay = this.mRemoteDisplay;
        if (remoteDisplay != null) {
            remoteDisplay.resume();
        }
    }

    public void requestDisconnect() {
        disconnect();
    }

    public final void updateWfdEnableState() {
        if (this.mWifiDisplayOnSetting && this.mWifiP2pEnabled) {
            if (this.mWfdEnabled || this.mWfdEnabling) {
                return;
            }
            this.mWfdEnabling = true;
            WifiP2pWfdInfo wifiP2pWfdInfo = new WifiP2pWfdInfo();
            wifiP2pWfdInfo.setEnabled(true);
            wifiP2pWfdInfo.setDeviceType(0);
            wifiP2pWfdInfo.setSessionAvailable(true);
            wifiP2pWfdInfo.setControlPort(7236);
            wifiP2pWfdInfo.setMaxThroughput(50);
            this.mWifiP2pManager.setWfdInfo(this.mWifiP2pChannel, wifiP2pWfdInfo, new WifiP2pManager.ActionListener() { // from class: com.android.server.display.WifiDisplayController.2
                @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                public void onSuccess() {
                    if (WifiDisplayController.this.mWfdEnabling) {
                        WifiDisplayController.this.mWfdEnabling = false;
                        WifiDisplayController.this.mWfdEnabled = true;
                        WifiDisplayController.this.reportFeatureState();
                        WifiDisplayController.this.updateScanState();
                    }
                }

                @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                public void onFailure(int i) {
                    WifiDisplayController.this.mWfdEnabling = false;
                }
            });
            return;
        }
        if (this.mWfdEnabled || this.mWfdEnabling) {
            WifiP2pWfdInfo wifiP2pWfdInfo2 = new WifiP2pWfdInfo();
            wifiP2pWfdInfo2.setEnabled(false);
            this.mWifiP2pManager.setWfdInfo(this.mWifiP2pChannel, wifiP2pWfdInfo2, new WifiP2pManager.ActionListener() { // from class: com.android.server.display.WifiDisplayController.3
                @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                public void onFailure(int i) {
                }

                @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                public void onSuccess() {
                }
            });
        }
        this.mWfdEnabling = false;
        this.mWfdEnabled = false;
        reportFeatureState();
        updateScanState();
        disconnect();
    }

    public final void reportFeatureState() {
        final int computeFeatureState = computeFeatureState();
        this.mHandler.post(new Runnable() { // from class: com.android.server.display.WifiDisplayController.4
            @Override // java.lang.Runnable
            public void run() {
                WifiDisplayController.this.mListener.onFeatureStateChanged(computeFeatureState);
            }
        });
    }

    public final int computeFeatureState() {
        if (this.mWifiP2pEnabled) {
            return this.mWifiDisplayOnSetting ? 3 : 2;
        }
        return 1;
    }

    public final void updateScanState() {
        if (this.mScanRequested && this.mWfdEnabled && this.mDesiredDevice == null) {
            if (this.mDiscoverPeersInProgress) {
                return;
            }
            Slog.i("WifiDisplayController", "Starting Wifi display scan.");
            this.mDiscoverPeersInProgress = true;
            handleScanStarted();
            tryDiscoverPeers();
        } else if (this.mDiscoverPeersInProgress) {
            this.mHandler.removeCallbacks(this.mDiscoverPeers);
            WifiP2pDevice wifiP2pDevice = this.mDesiredDevice;
            if (wifiP2pDevice == null || wifiP2pDevice == this.mConnectedDevice) {
                Slog.i("WifiDisplayController", "Stopping Wifi display scan.");
                this.mDiscoverPeersInProgress = false;
                stopPeerDiscovery();
                handleScanFinished();
            }
        }
    }

    public final void tryDiscoverPeers() {
        this.mWifiP2pManager.discoverPeers(this.mWifiP2pChannel, new WifiP2pManager.ActionListener() { // from class: com.android.server.display.WifiDisplayController.5
            @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
            public void onFailure(int i) {
            }

            @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
            public void onSuccess() {
                if (WifiDisplayController.this.mDiscoverPeersInProgress) {
                    WifiDisplayController.this.requestPeers();
                }
            }
        });
        this.mHandler.postDelayed(this.mDiscoverPeers, 10000L);
    }

    public final void stopPeerDiscovery() {
        this.mWifiP2pManager.stopPeerDiscovery(this.mWifiP2pChannel, new WifiP2pManager.ActionListener() { // from class: com.android.server.display.WifiDisplayController.6
            @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
            public void onFailure(int i) {
            }

            @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
            public void onSuccess() {
            }
        });
    }

    public final void requestPeers() {
        this.mWifiP2pManager.requestPeers(this.mWifiP2pChannel, new WifiP2pManager.PeerListListener() { // from class: com.android.server.display.WifiDisplayController.7
            @Override // android.net.wifi.p2p.WifiP2pManager.PeerListListener
            public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                WifiDisplayController.this.mAvailableWifiDisplayPeers.clear();
                for (WifiP2pDevice wifiP2pDevice : wifiP2pDeviceList.getDeviceList()) {
                    if (WifiDisplayController.isWifiDisplay(wifiP2pDevice)) {
                        WifiDisplayController.this.mAvailableWifiDisplayPeers.add(wifiP2pDevice);
                    }
                }
                if (WifiDisplayController.this.mDiscoverPeersInProgress) {
                    WifiDisplayController.this.handleScanResults();
                }
            }
        });
    }

    public final void handleScanStarted() {
        this.mHandler.post(new Runnable() { // from class: com.android.server.display.WifiDisplayController.8
            @Override // java.lang.Runnable
            public void run() {
                WifiDisplayController.this.mListener.onScanStarted();
            }
        });
    }

    public final void handleScanResults() {
        int size = this.mAvailableWifiDisplayPeers.size();
        final WifiDisplay[] wifiDisplayArr = (WifiDisplay[]) WifiDisplay.CREATOR.newArray(size);
        for (int i = 0; i < size; i++) {
            WifiP2pDevice wifiP2pDevice = this.mAvailableWifiDisplayPeers.get(i);
            wifiDisplayArr[i] = createWifiDisplay(wifiP2pDevice);
            updateDesiredDevice(wifiP2pDevice);
        }
        this.mHandler.post(new Runnable() { // from class: com.android.server.display.WifiDisplayController.9
            @Override // java.lang.Runnable
            public void run() {
                WifiDisplayController.this.mListener.onScanResults(wifiDisplayArr);
            }
        });
    }

    public final void handleScanFinished() {
        this.mHandler.post(new Runnable() { // from class: com.android.server.display.WifiDisplayController.10
            @Override // java.lang.Runnable
            public void run() {
                WifiDisplayController.this.mListener.onScanFinished();
            }
        });
    }

    public final void updateDesiredDevice(WifiP2pDevice wifiP2pDevice) {
        String str = wifiP2pDevice.deviceAddress;
        WifiP2pDevice wifiP2pDevice2 = this.mDesiredDevice;
        if (wifiP2pDevice2 == null || !wifiP2pDevice2.deviceAddress.equals(str)) {
            return;
        }
        this.mDesiredDevice.update(wifiP2pDevice);
        WifiDisplay wifiDisplay = this.mAdvertisedDisplay;
        if (wifiDisplay == null || !wifiDisplay.getDeviceAddress().equals(str)) {
            return;
        }
        readvertiseDisplay(createWifiDisplay(this.mDesiredDevice));
    }

    public final void connect(WifiP2pDevice wifiP2pDevice) {
        WifiP2pDevice wifiP2pDevice2 = this.mDesiredDevice;
        if (wifiP2pDevice2 == null || wifiP2pDevice2.deviceAddress.equals(wifiP2pDevice.deviceAddress)) {
            WifiP2pDevice wifiP2pDevice3 = this.mConnectedDevice;
            if (wifiP2pDevice3 == null || wifiP2pDevice3.deviceAddress.equals(wifiP2pDevice.deviceAddress) || this.mDesiredDevice != null) {
                if (!this.mWfdEnabled) {
                    Slog.i("WifiDisplayController", "Ignoring request to connect to Wifi display because the  feature is currently disabled: " + wifiP2pDevice.deviceName);
                    return;
                }
                this.mDesiredDevice = wifiP2pDevice;
                this.mConnectionRetriesLeft = 3;
                updateConnection();
            }
        }
    }

    public final void disconnect() {
        this.mDesiredDevice = null;
        updateConnection();
    }

    public final void retryConnection() {
        this.mDesiredDevice = new WifiP2pDevice(this.mDesiredDevice);
        updateConnection();
    }

    public final void updateConnection() {
        Inet4Address interfaceAddress;
        updateScanState();
        if (this.mRemoteDisplay != null && this.mConnectedDevice != this.mDesiredDevice) {
            Slog.i("WifiDisplayController", "Stopped listening for RTSP connection on " + this.mRemoteDisplayInterface + " from Wifi display: " + this.mConnectedDevice.deviceName);
            this.mRemoteDisplay.dispose();
            this.mRemoteDisplay = null;
            this.mRemoteDisplayInterface = null;
            this.mRemoteDisplayConnected = false;
            this.mHandler.removeCallbacks(this.mRtspTimeout);
            this.mWifiP2pManager.setMiracastMode(0);
            unadvertiseDisplay();
        }
        if (this.mDisconnectingDevice != null) {
            return;
        }
        WifiP2pDevice wifiP2pDevice = this.mConnectedDevice;
        if (wifiP2pDevice != null && wifiP2pDevice != this.mDesiredDevice) {
            Slog.i("WifiDisplayController", "Disconnecting from Wifi display: " + this.mConnectedDevice.deviceName);
            this.mDisconnectingDevice = this.mConnectedDevice;
            this.mConnectedDevice = null;
            this.mConnectedDeviceGroupInfo = null;
            unadvertiseDisplay();
            final WifiP2pDevice wifiP2pDevice2 = this.mDisconnectingDevice;
            this.mWifiP2pManager.removeGroup(this.mWifiP2pChannel, new WifiP2pManager.ActionListener() { // from class: com.android.server.display.WifiDisplayController.11
                @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                public void onSuccess() {
                    Slog.i("WifiDisplayController", "Disconnected from Wifi display: " + wifiP2pDevice2.deviceName);
                    next();
                }

                @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                public void onFailure(int i) {
                    Slog.i("WifiDisplayController", "Failed to disconnect from Wifi display: " + wifiP2pDevice2.deviceName + ", reason=" + i);
                    next();
                }

                public final void next() {
                    if (WifiDisplayController.this.mDisconnectingDevice == wifiP2pDevice2) {
                        WifiDisplayController.this.mDisconnectingDevice = null;
                        WifiDisplayController.this.updateConnection();
                    }
                }
            });
        } else if (this.mCancelingDevice != null) {
        } else {
            WifiP2pDevice wifiP2pDevice3 = this.mConnectingDevice;
            if (wifiP2pDevice3 != null && wifiP2pDevice3 != this.mDesiredDevice) {
                Slog.i("WifiDisplayController", "Canceling connection to Wifi display: " + this.mConnectingDevice.deviceName);
                this.mCancelingDevice = this.mConnectingDevice;
                this.mConnectingDevice = null;
                unadvertiseDisplay();
                this.mHandler.removeCallbacks(this.mConnectionTimeout);
                final WifiP2pDevice wifiP2pDevice4 = this.mCancelingDevice;
                this.mWifiP2pManager.cancelConnect(this.mWifiP2pChannel, new WifiP2pManager.ActionListener() { // from class: com.android.server.display.WifiDisplayController.12
                    @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                    public void onSuccess() {
                        Slog.i("WifiDisplayController", "Canceled connection to Wifi display: " + wifiP2pDevice4.deviceName);
                        next();
                    }

                    @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                    public void onFailure(int i) {
                        Slog.i("WifiDisplayController", "Failed to cancel connection to Wifi display: " + wifiP2pDevice4.deviceName + ", reason=" + i);
                        next();
                    }

                    public final void next() {
                        if (WifiDisplayController.this.mCancelingDevice == wifiP2pDevice4) {
                            WifiDisplayController.this.mCancelingDevice = null;
                            WifiDisplayController.this.updateConnection();
                        }
                    }
                });
            } else if (this.mDesiredDevice == null) {
                if (this.mWifiDisplayCertMode) {
                    this.mListener.onDisplaySessionInfo(getSessionInfo(this.mConnectedDeviceGroupInfo, 0));
                }
                unadvertiseDisplay();
            } else if (wifiP2pDevice == null && wifiP2pDevice3 == null) {
                Slog.i("WifiDisplayController", "Connecting to Wifi display: " + this.mDesiredDevice.deviceName);
                this.mConnectingDevice = this.mDesiredDevice;
                WifiP2pConfig wifiP2pConfig = new WifiP2pConfig();
                WpsInfo wpsInfo = new WpsInfo();
                int i = this.mWifiDisplayWpsConfig;
                if (i != 4) {
                    wpsInfo.setup = i;
                } else if (this.mConnectingDevice.wpsPbcSupported()) {
                    wpsInfo.setup = 0;
                } else if (this.mConnectingDevice.wpsDisplaySupported()) {
                    wpsInfo.setup = 2;
                } else {
                    wpsInfo.setup = 1;
                }
                wifiP2pConfig.wps = wpsInfo;
                WifiP2pDevice wifiP2pDevice5 = this.mConnectingDevice;
                wifiP2pConfig.deviceAddress = wifiP2pDevice5.deviceAddress;
                wifiP2pConfig.groupOwnerIntent = 0;
                advertiseDisplay(createWifiDisplay(wifiP2pDevice5), null, 0, 0, 0);
                final WifiP2pDevice wifiP2pDevice6 = this.mDesiredDevice;
                this.mWifiP2pManager.connect(this.mWifiP2pChannel, wifiP2pConfig, new WifiP2pManager.ActionListener() { // from class: com.android.server.display.WifiDisplayController.13
                    @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                    public void onSuccess() {
                        Slog.i("WifiDisplayController", "Initiated connection to Wifi display: " + wifiP2pDevice6.deviceName);
                        WifiDisplayController.this.mHandler.postDelayed(WifiDisplayController.this.mConnectionTimeout, 30000L);
                    }

                    @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                    public void onFailure(int i2) {
                        if (WifiDisplayController.this.mConnectingDevice == wifiP2pDevice6) {
                            Slog.i("WifiDisplayController", "Failed to initiate connection to Wifi display: " + wifiP2pDevice6.deviceName + ", reason=" + i2);
                            WifiDisplayController.this.mConnectingDevice = null;
                            WifiDisplayController.this.handleConnectionFailure(false);
                        }
                    }
                });
            } else if (wifiP2pDevice == null || this.mRemoteDisplay != null) {
            } else {
                if (getInterfaceAddress(this.mConnectedDeviceGroupInfo) == null) {
                    Slog.i("WifiDisplayController", "Failed to get local interface address for communicating with Wifi display: " + this.mConnectedDevice.deviceName);
                    handleConnectionFailure(false);
                    return;
                }
                this.mWifiP2pManager.setMiracastMode(1);
                final WifiP2pDevice wifiP2pDevice7 = this.mConnectedDevice;
                String str = interfaceAddress.getHostAddress() + XmlUtils.STRING_ARRAY_SEPARATOR + getPortNumber(wifiP2pDevice7);
                this.mRemoteDisplayInterface = str;
                Slog.i("WifiDisplayController", "Listening for RTSP connection on " + str + " from Wifi display: " + this.mConnectedDevice.deviceName);
                this.mRemoteDisplay = RemoteDisplay.listen(str, new RemoteDisplay.Listener() { // from class: com.android.server.display.WifiDisplayController.14
                    public void onDisplayConnected(Surface surface, int i2, int i3, int i4, int i5) {
                        if (WifiDisplayController.this.mConnectedDevice != wifiP2pDevice7 || WifiDisplayController.this.mRemoteDisplayConnected) {
                            return;
                        }
                        Slog.i("WifiDisplayController", "Opened RTSP connection with Wifi display: " + WifiDisplayController.this.mConnectedDevice.deviceName);
                        WifiDisplayController.this.mRemoteDisplayConnected = true;
                        WifiDisplayController.this.mHandler.removeCallbacks(WifiDisplayController.this.mRtspTimeout);
                        if (WifiDisplayController.this.mWifiDisplayCertMode) {
                            Listener listener = WifiDisplayController.this.mListener;
                            WifiDisplayController wifiDisplayController = WifiDisplayController.this;
                            listener.onDisplaySessionInfo(wifiDisplayController.getSessionInfo(wifiDisplayController.mConnectedDeviceGroupInfo, i5));
                        }
                        WifiDisplayController.this.advertiseDisplay(WifiDisplayController.createWifiDisplay(WifiDisplayController.this.mConnectedDevice), surface, i2, i3, i4);
                    }

                    public void onDisplayDisconnected() {
                        if (WifiDisplayController.this.mConnectedDevice == wifiP2pDevice7) {
                            Slog.i("WifiDisplayController", "Closed RTSP connection with Wifi display: " + WifiDisplayController.this.mConnectedDevice.deviceName);
                            WifiDisplayController.this.mHandler.removeCallbacks(WifiDisplayController.this.mRtspTimeout);
                            WifiDisplayController.this.disconnect();
                        }
                    }

                    public void onDisplayError(int i2) {
                        if (WifiDisplayController.this.mConnectedDevice == wifiP2pDevice7) {
                            Slog.i("WifiDisplayController", "Lost RTSP connection with Wifi display due to error " + i2 + ": " + WifiDisplayController.this.mConnectedDevice.deviceName);
                            WifiDisplayController.this.mHandler.removeCallbacks(WifiDisplayController.this.mRtspTimeout);
                            WifiDisplayController.this.handleConnectionFailure(false);
                        }
                    }
                }, this.mHandler, this.mContext.getOpPackageName());
                this.mHandler.postDelayed(this.mRtspTimeout, (this.mWifiDisplayCertMode ? 120 : 30) * 1000);
            }
        }
    }

    public final WifiDisplaySessionInfo getSessionInfo(WifiP2pGroup wifiP2pGroup, int i) {
        if (wifiP2pGroup == null) {
            return null;
        }
        Inet4Address interfaceAddress = getInterfaceAddress(wifiP2pGroup);
        return new WifiDisplaySessionInfo(!wifiP2pGroup.getOwner().deviceAddress.equals(this.mThisDevice.deviceAddress), i, wifiP2pGroup.getOwner().deviceAddress + " " + wifiP2pGroup.getNetworkName(), wifiP2pGroup.getPassphrase(), interfaceAddress != null ? interfaceAddress.getHostAddress() : "");
    }

    public final void handleStateChanged(boolean z) {
        this.mWifiP2pEnabled = z;
        if (z) {
            retrieveWifiP2pManagerAndChannel();
        }
        updateWfdEnableState();
    }

    public final void handlePeersChanged() {
        requestPeers();
    }

    public static boolean contains(WifiP2pGroup wifiP2pGroup, WifiP2pDevice wifiP2pDevice) {
        return wifiP2pGroup.getOwner().equals(wifiP2pDevice) || wifiP2pGroup.getClientList().contains(wifiP2pDevice);
    }

    public final void handleConnectionChanged(NetworkInfo networkInfo) {
        this.mNetworkInfo = networkInfo;
        if (this.mWfdEnabled && networkInfo.isConnected()) {
            if (this.mDesiredDevice != null || this.mWifiDisplayCertMode) {
                this.mWifiP2pManager.requestGroupInfo(this.mWifiP2pChannel, new WifiP2pManager.GroupInfoListener() { // from class: com.android.server.display.WifiDisplayController.15
                    @Override // android.net.wifi.p2p.WifiP2pManager.GroupInfoListener
                    public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
                        if (WifiDisplayController.this.mConnectingDevice != null && !WifiDisplayController.contains(wifiP2pGroup, WifiDisplayController.this.mConnectingDevice)) {
                            Slog.i("WifiDisplayController", "Aborting connection to Wifi display because the current P2P group does not contain the device we expected to find: " + WifiDisplayController.this.mConnectingDevice.deviceName + ", group info was: " + WifiDisplayController.describeWifiP2pGroup(wifiP2pGroup));
                            WifiDisplayController.this.handleConnectionFailure(false);
                        } else if (WifiDisplayController.this.mDesiredDevice != null && !WifiDisplayController.contains(wifiP2pGroup, WifiDisplayController.this.mDesiredDevice)) {
                            WifiDisplayController.this.disconnect();
                        } else {
                            if (WifiDisplayController.this.mWifiDisplayCertMode) {
                                boolean equals = wifiP2pGroup.getOwner().deviceAddress.equals(WifiDisplayController.this.mThisDevice.deviceAddress);
                                if (equals && wifiP2pGroup.getClientList().isEmpty()) {
                                    WifiDisplayController wifiDisplayController = WifiDisplayController.this;
                                    wifiDisplayController.mDesiredDevice = null;
                                    wifiDisplayController.mConnectingDevice = null;
                                    WifiDisplayController.this.mConnectedDeviceGroupInfo = wifiP2pGroup;
                                    WifiDisplayController.this.updateConnection();
                                } else if (WifiDisplayController.this.mConnectingDevice == null && WifiDisplayController.this.mDesiredDevice == null) {
                                    WifiDisplayController wifiDisplayController2 = WifiDisplayController.this;
                                    WifiP2pDevice next = equals ? wifiP2pGroup.getClientList().iterator().next() : wifiP2pGroup.getOwner();
                                    wifiDisplayController2.mDesiredDevice = next;
                                    wifiDisplayController2.mConnectingDevice = next;
                                }
                            }
                            if (WifiDisplayController.this.mConnectingDevice == null || WifiDisplayController.this.mConnectingDevice != WifiDisplayController.this.mDesiredDevice) {
                                return;
                            }
                            Slog.i("WifiDisplayController", "Connected to Wifi display: " + WifiDisplayController.this.mConnectingDevice.deviceName);
                            WifiDisplayController.this.mHandler.removeCallbacks(WifiDisplayController.this.mConnectionTimeout);
                            WifiDisplayController.this.mConnectedDeviceGroupInfo = wifiP2pGroup;
                            WifiDisplayController wifiDisplayController3 = WifiDisplayController.this;
                            wifiDisplayController3.mConnectedDevice = wifiDisplayController3.mConnectingDevice;
                            WifiDisplayController.this.mConnectingDevice = null;
                            WifiDisplayController.this.updateConnection();
                        }
                    }
                });
            }
        } else if (networkInfo.isConnectedOrConnecting()) {
        } else {
            this.mConnectedDeviceGroupInfo = null;
            if (this.mConnectingDevice != null || this.mConnectedDevice != null) {
                disconnect();
            }
            if (this.mWfdEnabled) {
                requestPeers();
            }
        }
    }

    public final void handleConnectionFailure(boolean z) {
        Slog.i("WifiDisplayController", "Wifi display connection failed!");
        final WifiP2pDevice wifiP2pDevice = this.mDesiredDevice;
        if (wifiP2pDevice != null) {
            if (this.mConnectionRetriesLeft > 0) {
                this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.display.WifiDisplayController.19
                    @Override // java.lang.Runnable
                    public void run() {
                        if (WifiDisplayController.this.mDesiredDevice != wifiP2pDevice || WifiDisplayController.this.mConnectionRetriesLeft <= 0) {
                            return;
                        }
                        WifiDisplayController wifiDisplayController = WifiDisplayController.this;
                        wifiDisplayController.mConnectionRetriesLeft--;
                        Slog.i("WifiDisplayController", "Retrying Wifi display connection.  Retries left: " + WifiDisplayController.this.mConnectionRetriesLeft);
                        WifiDisplayController.this.retryConnection();
                    }
                }, z ? 0L : 500L);
            } else {
                disconnect();
            }
        }
    }

    public final void advertiseDisplay(final WifiDisplay wifiDisplay, final Surface surface, final int i, final int i2, final int i3) {
        if (Objects.equals(this.mAdvertisedDisplay, wifiDisplay) && this.mAdvertisedDisplaySurface == surface && this.mAdvertisedDisplayWidth == i && this.mAdvertisedDisplayHeight == i2 && this.mAdvertisedDisplayFlags == i3) {
            return;
        }
        final WifiDisplay wifiDisplay2 = this.mAdvertisedDisplay;
        final Surface surface2 = this.mAdvertisedDisplaySurface;
        this.mAdvertisedDisplay = wifiDisplay;
        this.mAdvertisedDisplaySurface = surface;
        this.mAdvertisedDisplayWidth = i;
        this.mAdvertisedDisplayHeight = i2;
        this.mAdvertisedDisplayFlags = i3;
        this.mHandler.post(new Runnable() { // from class: com.android.server.display.WifiDisplayController.20
            @Override // java.lang.Runnable
            public void run() {
                Surface surface3 = surface2;
                if (surface3 != null && surface != surface3) {
                    WifiDisplayController.this.mListener.onDisplayDisconnected();
                } else {
                    WifiDisplay wifiDisplay3 = wifiDisplay2;
                    if (wifiDisplay3 != null && !wifiDisplay3.hasSameAddress(wifiDisplay)) {
                        WifiDisplayController.this.mListener.onDisplayConnectionFailed();
                    }
                }
                WifiDisplay wifiDisplay4 = wifiDisplay;
                if (wifiDisplay4 != null) {
                    if (!wifiDisplay4.hasSameAddress(wifiDisplay2)) {
                        WifiDisplayController.this.mListener.onDisplayConnecting(wifiDisplay);
                    } else if (!wifiDisplay.equals(wifiDisplay2)) {
                        WifiDisplayController.this.mListener.onDisplayChanged(wifiDisplay);
                    }
                    Surface surface4 = surface;
                    if (surface4 == null || surface4 == surface2) {
                        return;
                    }
                    WifiDisplayController.this.mListener.onDisplayConnected(wifiDisplay, surface, i, i2, i3);
                }
            }
        });
    }

    public final void unadvertiseDisplay() {
        advertiseDisplay(null, null, 0, 0, 0);
    }

    public final void readvertiseDisplay(WifiDisplay wifiDisplay) {
        advertiseDisplay(wifiDisplay, this.mAdvertisedDisplaySurface, this.mAdvertisedDisplayWidth, this.mAdvertisedDisplayHeight, this.mAdvertisedDisplayFlags);
    }

    public static Inet4Address getInterfaceAddress(WifiP2pGroup wifiP2pGroup) {
        try {
            Enumeration<InetAddress> inetAddresses = NetworkInterface.getByName(wifiP2pGroup.getInterface()).getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress nextElement = inetAddresses.nextElement();
                if (nextElement instanceof Inet4Address) {
                    return (Inet4Address) nextElement;
                }
            }
            Slog.w("WifiDisplayController", "Could not obtain address of network interface " + wifiP2pGroup.getInterface() + " because it had no IPv4 addresses.");
            return null;
        } catch (SocketException e) {
            Slog.w("WifiDisplayController", "Could not obtain address of network interface " + wifiP2pGroup.getInterface(), e);
            return null;
        }
    }

    public static int getPortNumber(WifiP2pDevice wifiP2pDevice) {
        return (wifiP2pDevice.deviceName.startsWith("DIRECT-") && wifiP2pDevice.deviceName.endsWith("Broadcom")) ? 8554 : 7236;
    }

    public static boolean isWifiDisplay(WifiP2pDevice wifiP2pDevice) {
        WifiP2pWfdInfo wfdInfo = wifiP2pDevice.getWfdInfo();
        return wfdInfo != null && wfdInfo.isEnabled() && isPrimarySinkDeviceType(wfdInfo.getDeviceType());
    }

    public static String describeWifiP2pDevice(WifiP2pDevice wifiP2pDevice) {
        return wifiP2pDevice != null ? wifiP2pDevice.toString().replace('\n', ',') : "null";
    }

    public static String describeWifiP2pGroup(WifiP2pGroup wifiP2pGroup) {
        return wifiP2pGroup != null ? wifiP2pGroup.toString().replace('\n', ',') : "null";
    }

    public static WifiDisplay createWifiDisplay(WifiP2pDevice wifiP2pDevice) {
        return new WifiDisplay(wifiP2pDevice.deviceAddress, wifiP2pDevice.deviceName, (String) null, true, wifiP2pDevice.getWfdInfo().isSessionAvailable(), false);
    }
}
