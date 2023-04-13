package com.android.server.media;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHearingAid;
import android.bluetooth.BluetoothLeAudio;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import java.util.Objects;
/* loaded from: classes2.dex */
public class BluetoothProfileMonitor {
    public BluetoothA2dp mA2dpProfile;
    public final BluetoothAdapter mBluetoothAdapter;
    public final Context mContext;
    public BluetoothHearingAid mHearingAidProfile;
    public BluetoothLeAudio mLeAudioProfile;
    public final ProfileListener mProfileListener = new ProfileListener();

    /* loaded from: classes2.dex */
    public interface OnProfileChangedListener {
    }

    public BluetoothProfileMonitor(Context context, BluetoothAdapter bluetoothAdapter) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(bluetoothAdapter);
        this.mContext = context;
        this.mBluetoothAdapter = bluetoothAdapter;
    }

    public void start() {
        this.mBluetoothAdapter.getProfileProxy(this.mContext, this.mProfileListener, 2);
        this.mBluetoothAdapter.getProfileProxy(this.mContext, this.mProfileListener, 21);
        this.mBluetoothAdapter.getProfileProxy(this.mContext, this.mProfileListener, 22);
    }

    public boolean isProfileSupported(int i, BluetoothDevice bluetoothDevice) {
        BluetoothProfile bluetoothProfile;
        synchronized (this) {
            if (i == 2) {
                bluetoothProfile = this.mA2dpProfile;
            } else if (i == 21) {
                bluetoothProfile = this.mHearingAidProfile;
            } else if (i == 22) {
                bluetoothProfile = this.mLeAudioProfile;
            } else {
                throw new IllegalArgumentException(i + " is not supported as Bluetooth profile");
            }
        }
        if (bluetoothProfile == null) {
            return false;
        }
        return bluetoothProfile.getConnectedDevices().contains(bluetoothDevice);
    }

    public long getGroupId(int i, BluetoothDevice bluetoothDevice) {
        synchronized (this) {
            long j = -1;
            if (i != 2) {
                if (i == 21) {
                    BluetoothHearingAid bluetoothHearingAid = this.mHearingAidProfile;
                    if (bluetoothHearingAid != null) {
                        j = bluetoothHearingAid.getHiSyncId(bluetoothDevice);
                    }
                    return j;
                } else if (i == 22) {
                    BluetoothLeAudio bluetoothLeAudio = this.mLeAudioProfile;
                    if (bluetoothLeAudio != null) {
                        j = bluetoothLeAudio.getGroupId(bluetoothDevice);
                    }
                    return j;
                } else {
                    throw new IllegalArgumentException(i + " is not supported as Bluetooth profile");
                }
            }
            return -1L;
        }
    }

    /* loaded from: classes2.dex */
    public final class ProfileListener implements BluetoothProfile.ServiceListener {
        public ProfileListener() {
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            synchronized (BluetoothProfileMonitor.this) {
                if (i == 2) {
                    BluetoothProfileMonitor.this.mA2dpProfile = (BluetoothA2dp) bluetoothProfile;
                } else if (i == 21) {
                    BluetoothProfileMonitor.this.mHearingAidProfile = (BluetoothHearingAid) bluetoothProfile;
                } else if (i != 22) {
                    return;
                } else {
                    BluetoothProfileMonitor.this.mLeAudioProfile = (BluetoothLeAudio) bluetoothProfile;
                }
                BluetoothProfileMonitor.this.getClass();
            }
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int i) {
            synchronized (BluetoothProfileMonitor.this) {
                if (i == 2) {
                    BluetoothProfileMonitor.this.mA2dpProfile = null;
                } else if (i == 21) {
                    BluetoothProfileMonitor.this.mHearingAidProfile = null;
                } else if (i != 22) {
                    return;
                } else {
                    BluetoothProfileMonitor.this.mLeAudioProfile = null;
                }
                BluetoothProfileMonitor.this.getClass();
            }
        }
    }
}
