package com.android.server.companion.presence;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.companion.AssociationInfo;
import android.net.MacAddress;
import android.os.Handler;
import android.os.HandlerExecutor;
import com.android.server.companion.AssociationStore;
import java.util.HashMap;
import java.util.Map;
@SuppressLint({"LongLogTag"})
/* loaded from: classes.dex */
public class BluetoothCompanionDeviceConnectionListener extends BluetoothAdapter.BluetoothConnectionCallback implements AssociationStore.OnChangeListener {
    public final Map<MacAddress, BluetoothDevice> mAllConnectedDevices = new HashMap();
    public final AssociationStore mAssociationStore;
    public final Callback mCallback;

    /* loaded from: classes.dex */
    public interface Callback {
        void onBluetoothCompanionDeviceConnected(int i);

        void onBluetoothCompanionDeviceDisconnected(int i);
    }

    @Override // com.android.server.companion.AssociationStore.OnChangeListener
    public void onAssociationRemoved(AssociationInfo associationInfo) {
    }

    public BluetoothCompanionDeviceConnectionListener(AssociationStore associationStore, Callback callback) {
        this.mAssociationStore = associationStore;
        this.mCallback = callback;
    }

    public void init(BluetoothAdapter bluetoothAdapter) {
        bluetoothAdapter.registerBluetoothConnectionCallback(new HandlerExecutor(Handler.getMain()), this);
        this.mAssociationStore.registerListener(this);
    }

    public void onDeviceConnected(BluetoothDevice bluetoothDevice) {
        if (this.mAllConnectedDevices.put(MacAddress.fromString(bluetoothDevice.getAddress()), bluetoothDevice) != null) {
            return;
        }
        onDeviceConnectivityChanged(bluetoothDevice, true);
    }

    public void onDeviceDisconnected(BluetoothDevice bluetoothDevice, int i) {
        if (this.mAllConnectedDevices.remove(MacAddress.fromString(bluetoothDevice.getAddress())) == null) {
            return;
        }
        onDeviceConnectivityChanged(bluetoothDevice, false);
    }

    public final void onDeviceConnectivityChanged(BluetoothDevice bluetoothDevice, boolean z) {
        for (AssociationInfo associationInfo : this.mAssociationStore.getAssociationsByAddress(bluetoothDevice.getAddress())) {
            int id = associationInfo.getId();
            if (z) {
                this.mCallback.onBluetoothCompanionDeviceConnected(id);
            } else {
                this.mCallback.onBluetoothCompanionDeviceDisconnected(id);
            }
        }
    }

    @Override // com.android.server.companion.AssociationStore.OnChangeListener
    public void onAssociationAdded(AssociationInfo associationInfo) {
        if (this.mAllConnectedDevices.containsKey(associationInfo.getDeviceMacAddress())) {
            this.mCallback.onBluetoothCompanionDeviceConnected(associationInfo.getId());
        }
    }

    @Override // com.android.server.companion.AssociationStore.OnChangeListener
    public void onAssociationUpdated(AssociationInfo associationInfo, boolean z) {
        if (z) {
            throw new IllegalArgumentException("Address changes are not supported.");
        }
    }
}
