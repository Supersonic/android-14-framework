package com.android.server.companion.presence;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.companion.AssociationInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Slog;
import com.android.server.companion.AssociationStore;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
@SuppressLint({"LongLogTag"})
/* loaded from: classes.dex */
public class BleCompanionDeviceScanner implements AssociationStore.OnChangeListener {
    public static final ScanSettings SCAN_SETTINGS = new ScanSettings.Builder().setCallbackType(6).setScanMode(0).build();
    public final AssociationStore mAssociationStore;
    public BluetoothLeScanner mBleScanner;
    public BluetoothAdapter mBtAdapter;
    public final Callback mCallback;
    public boolean mScanning = false;
    public final ScanCallback mScanCallback = new ScanCallback() { // from class: com.android.server.companion.presence.BleCompanionDeviceScanner.2
        @Override // android.bluetooth.le.ScanCallback
        public void onScanResult(int i, ScanResult scanResult) {
            BluetoothDevice device = scanResult.getDevice();
            if (i == 2) {
                if (BleCompanionDeviceScanner.this.mMainThreadHandler.hasNotifyDeviceLostMessages(device)) {
                    BleCompanionDeviceScanner.this.mMainThreadHandler.removeNotifyDeviceLostMessages(device);
                } else {
                    BleCompanionDeviceScanner.this.notifyDeviceFound(device);
                }
            } else if (i == 4) {
                BleCompanionDeviceScanner.this.mMainThreadHandler.sendNotifyDeviceLostDelayedMessage(device);
            } else {
                Slog.wtf("CDM_BleCompanionDeviceScanner", "Unexpected callback " + BleCompanionDeviceScanner.nameForBleScanCallbackType(i));
            }
        }

        @Override // android.bluetooth.le.ScanCallback
        public void onScanFailed(int i) {
            BleCompanionDeviceScanner.this.mScanning = false;
        }
    };
    public final MainThreadHandler mMainThreadHandler = new MainThreadHandler();

    /* loaded from: classes.dex */
    public interface Callback {
        void onBleCompanionDeviceFound(int i);

        void onBleCompanionDeviceLost(int i);
    }

    public BleCompanionDeviceScanner(AssociationStore associationStore, Callback callback) {
        this.mAssociationStore = associationStore;
        this.mCallback = callback;
    }

    public void init(Context context, BluetoothAdapter bluetoothAdapter) {
        if (this.mBtAdapter != null) {
            throw new IllegalStateException(getClass().getSimpleName() + " is already initialized");
        }
        Objects.requireNonNull(bluetoothAdapter);
        this.mBtAdapter = bluetoothAdapter;
        checkBleState();
        registerBluetoothStateBroadcastReceiver(context);
        this.mAssociationStore.registerListener(this);
    }

    public final void restartScan() {
        enforceInitialized();
        if (this.mBleScanner == null) {
            return;
        }
        stopScanIfNeeded();
        startScan();
    }

    @Override // com.android.server.companion.AssociationStore.OnChangeListener
    public void onAssociationChanged(int i, AssociationInfo associationInfo) {
        if (Looper.getMainLooper().isCurrentThread()) {
            restartScan();
        } else {
            this.mMainThreadHandler.post(new Runnable() { // from class: com.android.server.companion.presence.BleCompanionDeviceScanner$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    BleCompanionDeviceScanner.this.restartScan();
                }
            });
        }
    }

    public final void checkBleState() {
        enforceInitialized();
        boolean isLeEnabled = this.mBtAdapter.isLeEnabled();
        if (!isLeEnabled || this.mBleScanner == null) {
            if (isLeEnabled || this.mBleScanner != null) {
                if (isLeEnabled) {
                    BluetoothLeScanner bluetoothLeScanner = this.mBtAdapter.getBluetoothLeScanner();
                    this.mBleScanner = bluetoothLeScanner;
                    if (bluetoothLeScanner == null) {
                        return;
                    }
                    startScan();
                    return;
                }
                stopScanIfNeeded();
                this.mBleScanner = null;
            }
        }
    }

    public final void startScan() {
        String deviceMacAddressAsString;
        enforceInitialized();
        if (this.mScanning) {
            Slog.w("CDM_BleCompanionDeviceScanner", "Scan is already in progress.");
        } else if (this.mBleScanner == null) {
            Slog.w("CDM_BleCompanionDeviceScanner", "BLE is not available.");
        } else {
            HashSet<String> hashSet = new HashSet();
            for (AssociationInfo associationInfo : this.mAssociationStore.getAssociations()) {
                if (associationInfo.isNotifyOnDeviceNearby() && (deviceMacAddressAsString = associationInfo.getDeviceMacAddressAsString()) != null) {
                    hashSet.add(deviceMacAddressAsString);
                }
            }
            if (hashSet.isEmpty()) {
                return;
            }
            ArrayList arrayList = new ArrayList(hashSet.size());
            for (String str : hashSet) {
                arrayList.add(new ScanFilter.Builder().setDeviceAddress(str).build());
            }
            if (this.mBtAdapter.isLeEnabled()) {
                try {
                    this.mBleScanner.startScan(arrayList, SCAN_SETTINGS, this.mScanCallback);
                    this.mScanning = true;
                    return;
                } catch (IllegalStateException e) {
                    Slog.w("CDM_BleCompanionDeviceScanner", "Exception while starting BLE scanning", e);
                    return;
                }
            }
            Slog.w("CDM_BleCompanionDeviceScanner", "BLE scanning is not turned on");
        }
    }

    public final void stopScanIfNeeded() {
        enforceInitialized();
        if (this.mScanning) {
            if (this.mBtAdapter.isLeEnabled()) {
                try {
                    this.mBleScanner.stopScan(this.mScanCallback);
                } catch (IllegalStateException e) {
                    Slog.w("CDM_BleCompanionDeviceScanner", "Exception while stopping BLE scanning", e);
                }
            } else {
                Slog.w("CDM_BleCompanionDeviceScanner", "BLE scanning is not turned on");
            }
            this.mScanning = false;
        }
    }

    public final void notifyDeviceFound(BluetoothDevice bluetoothDevice) {
        for (AssociationInfo associationInfo : this.mAssociationStore.getAssociationsByAddress(bluetoothDevice.getAddress())) {
            this.mCallback.onBleCompanionDeviceFound(associationInfo.getId());
        }
    }

    public final void notifyDeviceLost(BluetoothDevice bluetoothDevice) {
        for (AssociationInfo associationInfo : this.mAssociationStore.getAssociationsByAddress(bluetoothDevice.getAddress())) {
            this.mCallback.onBleCompanionDeviceLost(associationInfo.getId());
        }
    }

    public final void registerBluetoothStateBroadcastReceiver(Context context) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.companion.presence.BleCompanionDeviceScanner.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                intent.getIntExtra("android.bluetooth.adapter.extra.PREVIOUS_STATE", -1);
                intent.getIntExtra("android.bluetooth.adapter.extra.STATE", -1);
                BleCompanionDeviceScanner.this.checkBleState();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.adapter.action.BLE_STATE_CHANGED");
        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    public final void enforceInitialized() {
        if (this.mBtAdapter != null) {
            return;
        }
        throw new IllegalStateException(getClass().getSimpleName() + " is not initialized");
    }

    @SuppressLint({"HandlerLeak"})
    /* loaded from: classes.dex */
    public class MainThreadHandler extends Handler {
        public MainThreadHandler() {
            super(Looper.getMainLooper());
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 1) {
                return;
            }
            BleCompanionDeviceScanner.this.notifyDeviceLost((BluetoothDevice) message.obj);
        }

        public void sendNotifyDeviceLostDelayedMessage(BluetoothDevice bluetoothDevice) {
            sendMessageDelayed(obtainMessage(1, bluetoothDevice), 120000L);
        }

        public boolean hasNotifyDeviceLostMessages(BluetoothDevice bluetoothDevice) {
            return hasEqualMessages(1, bluetoothDevice);
        }

        public void removeNotifyDeviceLostMessages(BluetoothDevice bluetoothDevice) {
            removeEqualMessages(1, bluetoothDevice);
        }
    }

    public static String nameForBleScanCallbackType(int i) {
        String str = i != 1 ? i != 2 ? i != 4 ? "Unknown" : "MATCH_LOST" : "FIRST_MATCH" : "ALL_MATCHES";
        return str + "(" + i + ")";
    }
}
