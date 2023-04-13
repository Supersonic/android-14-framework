package com.android.server.midi;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothUuid;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.XmlResourceParser;
import android.media.MediaMetrics;
import android.media.midi.IBluetoothMidiService;
import android.media.midi.IMidiDeviceListener;
import android.media.midi.IMidiDeviceOpenCallback;
import android.media.midi.IMidiDeviceServer;
import android.media.midi.IMidiManager;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiDeviceStatus;
import android.media.midi.MidiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.util.EventLog;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.PackageMonitor;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.SystemService;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
/* loaded from: classes2.dex */
public class MidiService extends IMidiManager.Stub {
    public final BroadcastReceiver mBleMidiReceiver;
    public int mBluetoothServiceUid;
    public final Context mContext;
    public final HashSet<ParcelUuid> mNonMidiUUIDs;
    public final PackageManager mPackageManager;
    public final PackageMonitor mPackageMonitor;
    public static final UUID MIDI_SERVICE = UUID.fromString("03B80E5A-EDE8-4B33-A751-6CE34EC4C700");
    public static final MidiDeviceInfo[] EMPTY_DEVICE_INFO_ARRAY = new MidiDeviceInfo[0];
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    public final HashMap<IBinder, Client> mClients = new HashMap<>();
    public final HashMap<MidiDeviceInfo, Device> mDevicesByInfo = new HashMap<>();
    public final HashMap<BluetoothDevice, Device> mBluetoothDevices = new HashMap<>();
    public final HashMap<BluetoothDevice, MidiDevice> mBleMidiDeviceMap = new HashMap<>();
    public final HashMap<IBinder, Device> mDevicesByServer = new HashMap<>();
    public int mNextDeviceId = 1;
    public final Object mUsbMidiLock = new Object();
    @GuardedBy({"mUsbMidiLock"})
    public final HashMap<String, Integer> mUsbMidiLegacyDeviceOpenCount = new HashMap<>();
    @GuardedBy({"mUsbMidiLock"})
    public final HashSet<String> mUsbMidiUniversalDeviceInUse = new HashSet<>();

    /* loaded from: classes2.dex */
    public static class Lifecycle extends SystemService {
        public MidiService mMidiService;

        public Lifecycle(Context context) {
            super(context);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v0, types: [com.android.server.midi.MidiService, android.os.IBinder] */
        @Override // com.android.server.SystemService
        public void onStart() {
            ?? midiService = new MidiService(getContext());
            this.mMidiService = midiService;
            publishBinderService("midi", midiService);
        }

        @Override // com.android.server.SystemService
        public void onUserUnlocking(SystemService.TargetUser targetUser) {
            if (targetUser.getUserIdentifier() == 0) {
                this.mMidiService.onUnlockUser();
            }
        }
    }

    /* loaded from: classes2.dex */
    public final class Client implements IBinder.DeathRecipient {
        public final IBinder mToken;
        public final HashMap<IBinder, IMidiDeviceListener> mListeners = new HashMap<>();
        public final HashMap<IBinder, DeviceConnection> mDeviceConnections = new HashMap<>();
        public final int mUid = Binder.getCallingUid();
        public final int mPid = Binder.getCallingPid();

        public Client(IBinder iBinder) {
            this.mToken = iBinder;
        }

        public void addListener(IMidiDeviceListener iMidiDeviceListener) {
            if (this.mListeners.size() >= 16) {
                throw new SecurityException("too many MIDI listeners for UID = " + this.mUid);
            }
            this.mListeners.put(iMidiDeviceListener.asBinder(), iMidiDeviceListener);
        }

        public void removeListener(IMidiDeviceListener iMidiDeviceListener) {
            this.mListeners.remove(iMidiDeviceListener.asBinder());
            if (this.mListeners.size() == 0 && this.mDeviceConnections.size() == 0) {
                close();
            }
        }

        public void addDeviceConnection(Device device, IMidiDeviceOpenCallback iMidiDeviceOpenCallback) {
            Log.d("MidiService.Client", "addDeviceConnection() device:" + device);
            if (this.mDeviceConnections.size() >= 64) {
                Log.i("MidiService.Client", "too many MIDI connections for UID = " + this.mUid);
                throw new SecurityException("too many MIDI connections for UID = " + this.mUid);
            }
            DeviceConnection deviceConnection = new DeviceConnection(device, this, iMidiDeviceOpenCallback);
            this.mDeviceConnections.put(deviceConnection.getToken(), deviceConnection);
            device.addDeviceConnection(deviceConnection);
        }

        public void removeDeviceConnection(IBinder iBinder) {
            DeviceConnection remove = this.mDeviceConnections.remove(iBinder);
            if (remove != null) {
                remove.getDevice().removeDeviceConnection(remove);
            }
            if (this.mListeners.size() == 0 && this.mDeviceConnections.size() == 0) {
                close();
            }
        }

        public void removeDeviceConnection(DeviceConnection deviceConnection) {
            this.mDeviceConnections.remove(deviceConnection.getToken());
            if (this.mListeners.size() == 0 && this.mDeviceConnections.size() == 0) {
                close();
            }
        }

        public void deviceAdded(Device device) {
            if (device.isUidAllowed(this.mUid)) {
                MidiDeviceInfo deviceInfo = device.getDeviceInfo();
                try {
                    for (IMidiDeviceListener iMidiDeviceListener : this.mListeners.values()) {
                        iMidiDeviceListener.onDeviceAdded(deviceInfo);
                    }
                } catch (RemoteException e) {
                    Log.e("MidiService.Client", "remote exception", e);
                }
            }
        }

        public void deviceRemoved(Device device) {
            if (device.isUidAllowed(this.mUid)) {
                MidiDeviceInfo deviceInfo = device.getDeviceInfo();
                try {
                    for (IMidiDeviceListener iMidiDeviceListener : this.mListeners.values()) {
                        iMidiDeviceListener.onDeviceRemoved(deviceInfo);
                    }
                } catch (RemoteException e) {
                    Log.e("MidiService.Client", "remote exception", e);
                }
            }
        }

        public void deviceStatusChanged(Device device, MidiDeviceStatus midiDeviceStatus) {
            if (device.isUidAllowed(this.mUid)) {
                try {
                    for (IMidiDeviceListener iMidiDeviceListener : this.mListeners.values()) {
                        iMidiDeviceListener.onDeviceStatusChanged(midiDeviceStatus);
                    }
                } catch (RemoteException e) {
                    Log.e("MidiService.Client", "remote exception", e);
                }
            }
        }

        public final void close() {
            synchronized (MidiService.this.mClients) {
                MidiService.this.mClients.remove(this.mToken);
                this.mToken.unlinkToDeath(this, 0);
            }
            for (DeviceConnection deviceConnection : this.mDeviceConnections.values()) {
                deviceConnection.getDevice().removeDeviceConnection(deviceConnection);
            }
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            Log.d("MidiService.Client", "Client died: " + this);
            close();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("Client: UID: ");
            sb.append(this.mUid);
            sb.append(" PID: ");
            sb.append(this.mPid);
            sb.append(" listener count: ");
            sb.append(this.mListeners.size());
            sb.append(" Device Connections:");
            for (DeviceConnection deviceConnection : this.mDeviceConnections.values()) {
                sb.append(" <device ");
                sb.append(deviceConnection.getDevice().getDeviceInfo().getId());
                sb.append(">");
            }
            return sb.toString();
        }
    }

    public final Client getClient(IBinder iBinder) {
        Client client;
        synchronized (this.mClients) {
            client = this.mClients.get(iBinder);
            if (client == null) {
                client = new Client(iBinder);
                try {
                    iBinder.linkToDeath(client, 0);
                    this.mClients.put(iBinder, client);
                } catch (RemoteException unused) {
                    return null;
                }
            }
        }
        return client;
    }

    /* loaded from: classes2.dex */
    public final class Device implements IBinder.DeathRecipient {
        public final BluetoothDevice mBluetoothDevice;
        public final ArrayList<DeviceConnection> mDeviceConnections;
        public AtomicInteger mDeviceConnectionsAdded;
        public AtomicInteger mDeviceConnectionsRemoved;
        public MidiDeviceInfo mDeviceInfo;
        public MidiDeviceStatus mDeviceStatus;
        public Instant mPreviousCounterInstant;
        public IMidiDeviceServer mServer;
        public ServiceConnection mServiceConnection;
        public final ServiceInfo mServiceInfo;
        public AtomicInteger mTotalInputBytes;
        public AtomicInteger mTotalOutputBytes;
        public AtomicLong mTotalTimeConnectedNs;
        public final int mUid;

        public Device(IMidiDeviceServer iMidiDeviceServer, MidiDeviceInfo midiDeviceInfo, ServiceInfo serviceInfo, int i) {
            this.mDeviceConnections = new ArrayList<>();
            this.mDeviceConnectionsAdded = new AtomicInteger();
            this.mDeviceConnectionsRemoved = new AtomicInteger();
            this.mTotalTimeConnectedNs = new AtomicLong();
            this.mPreviousCounterInstant = null;
            this.mTotalInputBytes = new AtomicInteger();
            this.mTotalOutputBytes = new AtomicInteger();
            this.mDeviceInfo = midiDeviceInfo;
            this.mServiceInfo = serviceInfo;
            this.mUid = i;
            this.mBluetoothDevice = (BluetoothDevice) midiDeviceInfo.getProperties().getParcelable("bluetooth_device", BluetoothDevice.class);
            setDeviceServer(iMidiDeviceServer);
        }

        public Device(BluetoothDevice bluetoothDevice) {
            this.mDeviceConnections = new ArrayList<>();
            this.mDeviceConnectionsAdded = new AtomicInteger();
            this.mDeviceConnectionsRemoved = new AtomicInteger();
            this.mTotalTimeConnectedNs = new AtomicLong();
            this.mPreviousCounterInstant = null;
            this.mTotalInputBytes = new AtomicInteger();
            this.mTotalOutputBytes = new AtomicInteger();
            this.mBluetoothDevice = bluetoothDevice;
            this.mServiceInfo = null;
            this.mUid = MidiService.this.mBluetoothServiceUid;
        }

        public final void setDeviceServer(IMidiDeviceServer iMidiDeviceServer) {
            Log.i("MidiService.Device", "setDeviceServer()");
            if (iMidiDeviceServer != null) {
                if (this.mServer != null) {
                    Log.e("MidiService.Device", "mServer already set in setDeviceServer");
                    return;
                }
                IBinder asBinder = iMidiDeviceServer.asBinder();
                try {
                    asBinder.linkToDeath(this, 0);
                    this.mServer = iMidiDeviceServer;
                    MidiService.this.mDevicesByServer.put(asBinder, this);
                } catch (RemoteException unused) {
                    this.mServer = null;
                    return;
                }
            } else {
                IMidiDeviceServer iMidiDeviceServer2 = this.mServer;
                if (iMidiDeviceServer2 != null) {
                    this.mServer = null;
                    IBinder asBinder2 = iMidiDeviceServer2.asBinder();
                    MidiService.this.mDevicesByServer.remove(asBinder2);
                    this.mDeviceStatus = null;
                    try {
                        iMidiDeviceServer2.closeDevice();
                        asBinder2.unlinkToDeath(this, 0);
                    } catch (RemoteException unused2) {
                    }
                    iMidiDeviceServer = iMidiDeviceServer2;
                }
            }
            ArrayList<DeviceConnection> arrayList = this.mDeviceConnections;
            if (arrayList != null) {
                synchronized (arrayList) {
                    Iterator<DeviceConnection> it = this.mDeviceConnections.iterator();
                    while (it.hasNext()) {
                        it.next().notifyClient(iMidiDeviceServer);
                    }
                }
            }
        }

        public MidiDeviceInfo getDeviceInfo() {
            return this.mDeviceInfo;
        }

        public void setDeviceInfo(MidiDeviceInfo midiDeviceInfo) {
            this.mDeviceInfo = midiDeviceInfo;
        }

        public MidiDeviceStatus getDeviceStatus() {
            return this.mDeviceStatus;
        }

        public void setDeviceStatus(MidiDeviceStatus midiDeviceStatus) {
            this.mDeviceStatus = midiDeviceStatus;
        }

        public IMidiDeviceServer getDeviceServer() {
            return this.mServer;
        }

        public ServiceInfo getServiceInfo() {
            return this.mServiceInfo;
        }

        public String getPackageName() {
            ServiceInfo serviceInfo = this.mServiceInfo;
            if (serviceInfo == null) {
                return null;
            }
            return serviceInfo.packageName;
        }

        public int getUid() {
            return this.mUid;
        }

        public boolean isUidAllowed(int i) {
            return !this.mDeviceInfo.isPrivate() || this.mUid == i;
        }

        public void addDeviceConnection(DeviceConnection deviceConnection) {
            Intent intent;
            Log.d("MidiService.Device", "addDeviceConnection() [A] connection:" + deviceConnection);
            synchronized (this.mDeviceConnections) {
                this.mDeviceConnectionsAdded.incrementAndGet();
                if (this.mPreviousCounterInstant == null) {
                    this.mPreviousCounterInstant = Instant.now();
                }
                Log.d("MidiService.Device", "  mServer:" + this.mServer);
                if (this.mServer != null) {
                    Log.i("MidiService.Device", "++++ A");
                    this.mDeviceConnections.add(deviceConnection);
                    deviceConnection.notifyClient(this.mServer);
                } else if (this.mServiceConnection == null && (this.mServiceInfo != null || this.mBluetoothDevice != null)) {
                    Log.i("MidiService.Device", "++++ B");
                    this.mDeviceConnections.add(deviceConnection);
                    this.mServiceConnection = new ServiceConnection() { // from class: com.android.server.midi.MidiService.Device.1
                        @Override // android.content.ServiceConnection
                        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                            IMidiDeviceServer asInterface;
                            Log.i("MidiService.Device", "++++ onServiceConnected() mBluetoothDevice:" + Device.this.mBluetoothDevice);
                            if (Device.this.mBluetoothDevice != null) {
                                IBluetoothMidiService asInterface2 = IBluetoothMidiService.Stub.asInterface(iBinder);
                                Log.i("MidiService.Device", "++++ mBluetoothMidiService:" + asInterface2);
                                if (asInterface2 != null) {
                                    try {
                                        asInterface = IMidiDeviceServer.Stub.asInterface(asInterface2.addBluetoothDevice(Device.this.mBluetoothDevice));
                                    } catch (RemoteException e) {
                                        Log.e("MidiService.Device", "Could not call addBluetoothDevice()", e);
                                    }
                                }
                                asInterface = null;
                            } else {
                                asInterface = IMidiDeviceServer.Stub.asInterface(iBinder);
                            }
                            Device.this.setDeviceServer(asInterface);
                        }

                        @Override // android.content.ServiceConnection
                        public void onServiceDisconnected(ComponentName componentName) {
                            Device.this.setDeviceServer(null);
                            Device.this.mServiceConnection = null;
                        }
                    };
                    if (this.mBluetoothDevice != null) {
                        intent = new Intent("android.media.midi.BluetoothMidiService");
                        intent.setComponent(new ComponentName("com.android.bluetoothmidiservice", "com.android.bluetoothmidiservice.BluetoothMidiService"));
                    } else {
                        intent = new Intent("android.media.midi.MidiDeviceService");
                        ServiceInfo serviceInfo = this.mServiceInfo;
                        intent.setComponent(new ComponentName(serviceInfo.packageName, serviceInfo.name));
                    }
                    if (!MidiService.this.mContext.bindService(intent, this.mServiceConnection, 1)) {
                        Log.e("MidiService.Device", "Unable to bind service: " + intent);
                        setDeviceServer(null);
                        this.mServiceConnection = null;
                    }
                } else {
                    Log.e("MidiService.Device", "No way to connect to device in addDeviceConnection");
                    deviceConnection.notifyClient(null);
                }
            }
        }

        public void removeDeviceConnection(DeviceConnection deviceConnection) {
            synchronized (MidiService.this.mDevicesByInfo) {
                synchronized (this.mDeviceConnections) {
                    int incrementAndGet = this.mDeviceConnectionsRemoved.incrementAndGet();
                    Instant instant = this.mPreviousCounterInstant;
                    if (instant != null) {
                        this.mTotalTimeConnectedNs.addAndGet(Duration.between(instant, Instant.now()).toNanos());
                    }
                    if (incrementAndGet >= this.mDeviceConnectionsAdded.get()) {
                        this.mPreviousCounterInstant = null;
                    } else {
                        this.mPreviousCounterInstant = Instant.now();
                    }
                    logMetrics(false);
                    this.mDeviceConnections.remove(deviceConnection);
                    if (deviceConnection.getDevice().getDeviceInfo().getType() == 1) {
                        synchronized (MidiService.this.mUsbMidiLock) {
                            MidiService.this.removeUsbMidiDeviceLocked(deviceConnection.getDevice().getDeviceInfo());
                        }
                    }
                    if (this.mDeviceConnections.size() == 0 && this.mServiceConnection != null) {
                        MidiService.this.mContext.unbindService(this.mServiceConnection);
                        this.mServiceConnection = null;
                        if (this.mBluetoothDevice != null) {
                            closeLocked();
                        } else {
                            setDeviceServer(null);
                        }
                    }
                }
            }
        }

        public void closeLocked() {
            synchronized (this.mDeviceConnections) {
                Iterator<DeviceConnection> it = this.mDeviceConnections.iterator();
                while (it.hasNext()) {
                    DeviceConnection next = it.next();
                    if (next.getDevice().getDeviceInfo().getType() == 1) {
                        synchronized (MidiService.this.mUsbMidiLock) {
                            MidiService.this.removeUsbMidiDeviceLocked(next.getDevice().getDeviceInfo());
                        }
                    }
                    next.getClient().removeDeviceConnection(next);
                }
                this.mDeviceConnections.clear();
                if (this.mPreviousCounterInstant != null) {
                    Instant now = Instant.now();
                    this.mTotalTimeConnectedNs.addAndGet(Duration.between(this.mPreviousCounterInstant, now).toNanos());
                    this.mPreviousCounterInstant = now;
                }
                logMetrics(true);
            }
            setDeviceServer(null);
            if (this.mServiceInfo == null) {
                MidiService.this.removeDeviceLocked(this);
            } else {
                this.mDeviceStatus = new MidiDeviceStatus(this.mDeviceInfo);
            }
            if (this.mBluetoothDevice != null) {
                MidiService.this.mBluetoothDevices.remove(this.mBluetoothDevice);
            }
        }

        public final void logMetrics(boolean z) {
            int i = this.mDeviceConnectionsAdded.get();
            if (this.mDeviceInfo == null || i <= 0) {
                return;
            }
            new MediaMetrics.Item("audio.midi").setUid(this.mUid).set(MediaMetrics.Property.DEVICE_ID, Integer.valueOf(this.mDeviceInfo.getId())).set(MediaMetrics.Property.INPUT_PORT_COUNT, Integer.valueOf(this.mDeviceInfo.getInputPortCount())).set(MediaMetrics.Property.OUTPUT_PORT_COUNT, Integer.valueOf(this.mDeviceInfo.getOutputPortCount())).set(MediaMetrics.Property.HARDWARE_TYPE, Integer.valueOf(this.mDeviceInfo.getType())).set(MediaMetrics.Property.DURATION_NS, Long.valueOf(this.mTotalTimeConnectedNs.get())).set(MediaMetrics.Property.OPENED_COUNT, Integer.valueOf(i)).set(MediaMetrics.Property.CLOSED_COUNT, Integer.valueOf(this.mDeviceConnectionsRemoved.get())).set(MediaMetrics.Property.DEVICE_DISCONNECTED, z ? "true" : "false").set(MediaMetrics.Property.IS_SHARED, !this.mDeviceInfo.isPrivate() ? "true" : "false").set(MediaMetrics.Property.SUPPORTS_MIDI_UMP, this.mDeviceInfo.getDefaultProtocol() != -1 ? "true" : "false").set(MediaMetrics.Property.USING_ALSA, this.mDeviceInfo.getProperties().get("alsa_card") == null ? "false" : "true").set(MediaMetrics.Property.EVENT, "deviceClosed").set(MediaMetrics.Property.TOTAL_INPUT_BYTES, Integer.valueOf(this.mTotalInputBytes.get())).set(MediaMetrics.Property.TOTAL_OUTPUT_BYTES, Integer.valueOf(this.mTotalOutputBytes.get())).record();
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            Log.d("MidiService.Device", "Device died: " + this);
            synchronized (MidiService.this.mDevicesByInfo) {
                closeLocked();
            }
        }

        public void updateTotalBytes(int i, int i2) {
            this.mTotalInputBytes.set(i);
            this.mTotalOutputBytes.set(i2);
        }

        public String toString() {
            return "Device Info: " + this.mDeviceInfo + " Status: " + this.mDeviceStatus + " UID: " + this.mUid + " DeviceConnection count: " + this.mDeviceConnections.size() + " mServiceConnection: " + this.mServiceConnection;
        }
    }

    /* loaded from: classes2.dex */
    public final class DeviceConnection {
        public IMidiDeviceOpenCallback mCallback;
        public final Client mClient;
        public final Device mDevice;
        public final IBinder mToken = new Binder();

        public DeviceConnection(Device device, Client client, IMidiDeviceOpenCallback iMidiDeviceOpenCallback) {
            this.mDevice = device;
            this.mClient = client;
            this.mCallback = iMidiDeviceOpenCallback;
        }

        public Device getDevice() {
            return this.mDevice;
        }

        public Client getClient() {
            return this.mClient;
        }

        public IBinder getToken() {
            return this.mToken;
        }

        public void notifyClient(IMidiDeviceServer iMidiDeviceServer) {
            IBinder iBinder;
            Log.d("MidiService.DeviceConnection", "notifyClient");
            IMidiDeviceOpenCallback iMidiDeviceOpenCallback = this.mCallback;
            if (iMidiDeviceOpenCallback != null) {
                if (iMidiDeviceServer == null) {
                    iBinder = null;
                } else {
                    try {
                        iBinder = this.mToken;
                    } catch (RemoteException unused) {
                    }
                }
                iMidiDeviceOpenCallback.onDeviceOpened(iMidiDeviceServer, iBinder);
                this.mCallback = null;
            }
        }

        public String toString() {
            Device device = this.mDevice;
            if (device == null || device.getDeviceInfo() == null) {
                return "null";
            }
            return "" + this.mDevice.getDeviceInfo().getId();
        }
    }

    public final boolean isBLEMIDIDevice(BluetoothDevice bluetoothDevice) {
        ParcelUuid[] uuids = bluetoothDevice.getUuids();
        if (uuids != null) {
            for (ParcelUuid parcelUuid : uuids) {
                if (parcelUuid.getUuid().equals(MIDI_SERVICE)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void dumpIntentExtras(Intent intent) {
        String action = intent.getAction();
        Log.d("MidiService", "Intent: " + action);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            for (String str : extras.keySet()) {
                StringBuilder sb = new StringBuilder();
                sb.append("  ");
                sb.append(str);
                sb.append(" : ");
                sb.append(extras.get(str) != null ? extras.get(str) : "NULL");
                Log.d("MidiService", sb.toString());
            }
        }
    }

    public static boolean isBleTransport(Intent intent) {
        Bundle extras = intent.getExtras();
        return extras != null && extras.getInt("android.bluetooth.device.extra.TRANSPORT", 0) == 2;
    }

    public final void dumpUuids(BluetoothDevice bluetoothDevice) {
        ParcelUuid[] uuids = bluetoothDevice.getUuids();
        StringBuilder sb = new StringBuilder();
        sb.append("dumpUuids(");
        sb.append(bluetoothDevice);
        sb.append(") numParcels:");
        sb.append(uuids != null ? uuids.length : 0);
        Log.d("MidiService", sb.toString());
        if (uuids == null) {
            Log.d("MidiService", "No UUID Parcels");
            return;
        }
        for (ParcelUuid parcelUuid : uuids) {
            Log.d("MidiService", " uuid:" + parcelUuid.getUuid());
        }
    }

    public final boolean hasNonMidiUuids(BluetoothDevice bluetoothDevice) {
        ParcelUuid[] uuids = bluetoothDevice.getUuids();
        if (uuids != null) {
            for (ParcelUuid parcelUuid : uuids) {
                if (this.mNonMidiUUIDs.contains(parcelUuid)) {
                    return true;
                }
            }
        }
        return false;
    }

    public MidiService(Context context) {
        HashSet<ParcelUuid> hashSet = new HashSet<>();
        this.mNonMidiUUIDs = hashSet;
        this.mPackageMonitor = new PackageMonitor() { // from class: com.android.server.midi.MidiService.1
            public void onPackageAdded(String str, int i) {
                MidiService.this.addPackageDeviceServers(str);
            }

            public void onPackageModified(String str) {
                MidiService.this.removePackageDeviceServers(str);
                MidiService.this.addPackageDeviceServers(str);
            }

            public void onPackageRemoved(String str, int i) {
                MidiService.this.removePackageDeviceServers(str);
            }
        };
        this.mBleMidiReceiver = new BroadcastReceiver() { // from class: com.android.server.midi.MidiService.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if (action == null) {
                    Log.w("MidiService", "MidiService, action is null");
                    return;
                }
                char c = 65535;
                switch (action.hashCode()) {
                    case -377527494:
                        if (action.equals("android.bluetooth.device.action.UUID")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -301431627:
                        if (action.equals("android.bluetooth.device.action.ACL_CONNECTED")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 1821585647:
                        if (action.equals("android.bluetooth.device.action.ACL_DISCONNECTED")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 2116862345:
                        if (action.equals("android.bluetooth.device.action.BOND_STATE_CHANGED")) {
                            c = 3;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                    case 3:
                        Log.d("MidiService", "ACTION_UUID");
                        BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE", BluetoothDevice.class);
                        MidiService.this.dumpUuids(bluetoothDevice);
                        if (MidiService.this.isBLEMIDIDevice(bluetoothDevice)) {
                            Log.d("MidiService", "BT MIDI DEVICE");
                            MidiService.this.openBluetoothDevice(bluetoothDevice);
                            return;
                        }
                        return;
                    case 1:
                        Log.d("MidiService", "ACTION_ACL_CONNECTED");
                        MidiService.dumpIntentExtras(intent);
                        if (!MidiService.isBleTransport(intent)) {
                            Log.i("MidiService", "No BLE transport - NOT MIDI");
                            return;
                        }
                        Log.d("MidiService", "BLE Device");
                        BluetoothDevice bluetoothDevice2 = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE", BluetoothDevice.class);
                        MidiService.this.dumpUuids(bluetoothDevice2);
                        if (MidiService.this.hasNonMidiUuids(bluetoothDevice2)) {
                            Log.d("MidiService", "Non-MIDI service UUIDs found. NOT MIDI");
                            return;
                        }
                        Log.d("MidiService", "Potential MIDI Device.");
                        MidiService.this.openBluetoothDevice(bluetoothDevice2);
                        return;
                    case 2:
                        Log.d("MidiService", "ACTION_ACL_DISCONNECTED");
                        BluetoothDevice bluetoothDevice3 = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE", BluetoothDevice.class);
                        if (MidiService.this.isBLEMIDIDevice(bluetoothDevice3)) {
                            MidiService.this.closeBluetoothDevice(bluetoothDevice3);
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        };
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
        this.mBluetoothServiceUid = -1;
        hashSet.add(BluetoothUuid.A2DP_SINK);
        hashSet.add(BluetoothUuid.A2DP_SOURCE);
        hashSet.add(BluetoothUuid.ADV_AUDIO_DIST);
        hashSet.add(BluetoothUuid.AVRCP_CONTROLLER);
        hashSet.add(BluetoothUuid.HFP);
        hashSet.add(BluetoothUuid.HSP);
        hashSet.add(BluetoothUuid.HID);
        hashSet.add(BluetoothUuid.LE_AUDIO);
        hashSet.add(BluetoothUuid.HOGP);
        hashSet.add(BluetoothUuid.HEARING_AID);
    }

    public final void onUnlockUser() {
        ApplicationInfo applicationInfo;
        PackageInfo packageInfo = null;
        this.mPackageMonitor.register(this.mContext, (Looper) null, true);
        List<ResolveInfo> queryIntentServices = this.mPackageManager.queryIntentServices(new Intent("android.media.midi.MidiDeviceService"), 128);
        if (queryIntentServices != null) {
            int size = queryIntentServices.size();
            for (int i = 0; i < size; i++) {
                ServiceInfo serviceInfo = queryIntentServices.get(i).serviceInfo;
                if (serviceInfo != null) {
                    addPackageDeviceServer(serviceInfo);
                }
            }
        }
        try {
            packageInfo = this.mPackageManager.getPackageInfo("com.android.bluetoothmidiservice", 0);
        } catch (PackageManager.NameNotFoundException unused) {
        }
        if (packageInfo != null && (applicationInfo = packageInfo.applicationInfo) != null) {
            this.mBluetoothServiceUid = applicationInfo.uid;
        } else {
            this.mBluetoothServiceUid = -1;
        }
    }

    public void registerListener(IBinder iBinder, IMidiDeviceListener iMidiDeviceListener) {
        Client client = getClient(iBinder);
        if (client == null) {
            return;
        }
        client.addListener(iMidiDeviceListener);
        updateStickyDeviceStatus(client.mUid, iMidiDeviceListener);
    }

    public void unregisterListener(IBinder iBinder, IMidiDeviceListener iMidiDeviceListener) {
        Client client = getClient(iBinder);
        if (client == null) {
            return;
        }
        client.removeListener(iMidiDeviceListener);
    }

    public final void updateStickyDeviceStatus(int i, IMidiDeviceListener iMidiDeviceListener) {
        synchronized (this.mDevicesByInfo) {
            for (Device device : this.mDevicesByInfo.values()) {
                if (device.isUidAllowed(i)) {
                    try {
                        MidiDeviceStatus deviceStatus = device.getDeviceStatus();
                        if (deviceStatus != null) {
                            iMidiDeviceListener.onDeviceStatusChanged(deviceStatus);
                        }
                    } catch (RemoteException e) {
                        Log.e("MidiService", "remote exception", e);
                    }
                }
            }
        }
    }

    public MidiDeviceInfo[] getDevices() {
        return getDevicesForTransport(1);
    }

    public MidiDeviceInfo[] getDevicesForTransport(int i) {
        ArrayList arrayList = new ArrayList();
        int callingUid = Binder.getCallingUid();
        synchronized (this.mDevicesByInfo) {
            for (Device device : this.mDevicesByInfo.values()) {
                if (device.isUidAllowed(callingUid)) {
                    if (i == 2) {
                        if (device.getDeviceInfo().getDefaultProtocol() != -1) {
                            arrayList.add(device.getDeviceInfo());
                        }
                    } else if (i == 1 && device.getDeviceInfo().getDefaultProtocol() == -1) {
                        arrayList.add(device.getDeviceInfo());
                    }
                }
            }
        }
        return (MidiDeviceInfo[]) arrayList.toArray(EMPTY_DEVICE_INFO_ARRAY);
    }

    public void openDevice(IBinder iBinder, MidiDeviceInfo midiDeviceInfo, IMidiDeviceOpenCallback iMidiDeviceOpenCallback) {
        Device device;
        Client client = getClient(iBinder);
        Log.d("MidiService", "openDevice() client:" + client);
        if (client == null) {
            return;
        }
        synchronized (this.mDevicesByInfo) {
            device = this.mDevicesByInfo.get(midiDeviceInfo);
            Log.d("MidiService", "  device:" + device);
            if (device == null) {
                throw new IllegalArgumentException("device does not exist: " + midiDeviceInfo);
            } else if (!device.isUidAllowed(Binder.getCallingUid())) {
                throw new SecurityException("Attempt to open private device with wrong UID");
            }
        }
        if (midiDeviceInfo.getType() == 1) {
            synchronized (this.mUsbMidiLock) {
                if (isUsbMidiDeviceInUseLocked(midiDeviceInfo)) {
                    throw new IllegalArgumentException("device already in use: " + midiDeviceInfo);
                }
                addUsbMidiDeviceLocked(midiDeviceInfo);
            }
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Log.i("MidiService", "addDeviceConnection() [B] device:" + device);
            client.addDeviceConnection(device, iMidiDeviceOpenCallback);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void openBluetoothDevice(final BluetoothDevice bluetoothDevice) {
        Log.d("MidiService", "openBluetoothDevice() device: " + bluetoothDevice);
        ((MidiManager) this.mContext.getSystemService(MidiManager.class)).openBluetoothDevice(bluetoothDevice, new MidiManager.OnDeviceOpenedListener() { // from class: com.android.server.midi.MidiService.3
            @Override // android.media.midi.MidiManager.OnDeviceOpenedListener
            public void onDeviceOpened(MidiDevice midiDevice) {
                synchronized (MidiService.this.mBleMidiDeviceMap) {
                    Log.i("MidiService", "onDeviceOpened() device:" + midiDevice);
                    MidiService.this.mBleMidiDeviceMap.put(bluetoothDevice, midiDevice);
                }
            }
        }, null);
    }

    public final void closeBluetoothDevice(BluetoothDevice bluetoothDevice) {
        MidiDevice remove;
        Log.d("MidiService", "closeBluetoothDevice() device: " + bluetoothDevice);
        synchronized (this.mBleMidiDeviceMap) {
            remove = this.mBleMidiDeviceMap.remove(bluetoothDevice);
        }
        if (remove != null) {
            try {
                remove.close();
            } catch (IOException e) {
                Log.e("MidiService", "Exception closing BLE-MIDI device" + e);
            }
        }
    }

    public void openBluetoothDevice(IBinder iBinder, BluetoothDevice bluetoothDevice, IMidiDeviceOpenCallback iMidiDeviceOpenCallback) {
        Device device;
        Log.d("MidiService", "openBluetoothDevice()");
        Client client = getClient(iBinder);
        if (client == null) {
            return;
        }
        Log.i("MidiService", "alloc device...");
        synchronized (this.mDevicesByInfo) {
            device = this.mBluetoothDevices.get(bluetoothDevice);
            if (device == null) {
                device = new Device(bluetoothDevice);
                this.mBluetoothDevices.put(bluetoothDevice, device);
            }
        }
        Log.i("MidiService", "device: " + device);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Log.i("MidiService", "addDeviceConnection() [C] device:" + device);
            client.addDeviceConnection(device, iMidiDeviceOpenCallback);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void closeDevice(IBinder iBinder, IBinder iBinder2) {
        Client client = getClient(iBinder);
        if (client == null) {
            return;
        }
        client.removeDeviceConnection(iBinder2);
    }

    public MidiDeviceInfo registerDeviceServer(IMidiDeviceServer iMidiDeviceServer, int i, int i2, String[] strArr, String[] strArr2, Bundle bundle, int i3, int i4) {
        MidiDeviceInfo addDeviceLocked;
        int callingUid = Binder.getCallingUid();
        if (i3 == 1 && callingUid != 1000) {
            throw new SecurityException("only system can create USB devices");
        }
        if (i3 == 3 && callingUid != this.mBluetoothServiceUid) {
            throw new SecurityException("only MidiBluetoothService can create Bluetooth devices");
        }
        synchronized (this.mDevicesByInfo) {
            addDeviceLocked = addDeviceLocked(i3, i, i2, strArr, strArr2, bundle, iMidiDeviceServer, null, false, callingUid, i4);
        }
        return addDeviceLocked;
    }

    public void unregisterDeviceServer(IMidiDeviceServer iMidiDeviceServer) {
        synchronized (this.mDevicesByInfo) {
            Device device = this.mDevicesByServer.get(iMidiDeviceServer.asBinder());
            if (device != null) {
                device.closeLocked();
            }
        }
    }

    public MidiDeviceInfo getServiceDeviceInfo(String str, String str2) {
        int callingUid = Binder.getCallingUid();
        synchronized (this.mDevicesByInfo) {
            for (Device device : this.mDevicesByInfo.values()) {
                ServiceInfo serviceInfo = device.getServiceInfo();
                if (serviceInfo != null && str.equals(serviceInfo.packageName) && str2.equals(serviceInfo.name)) {
                    if (device.isUidAllowed(callingUid)) {
                        return device.getDeviceInfo();
                    }
                    EventLog.writeEvent(1397638484, "185796676", -1, "");
                    return null;
                }
            }
            return null;
        }
    }

    public MidiDeviceStatus getDeviceStatus(MidiDeviceInfo midiDeviceInfo) {
        Device device = this.mDevicesByInfo.get(midiDeviceInfo);
        if (device == null) {
            throw new IllegalArgumentException("no such device for " + midiDeviceInfo);
        }
        int callingUid = Binder.getCallingUid();
        if (device.isUidAllowed(callingUid)) {
            return device.getDeviceStatus();
        }
        Log.e("MidiService", "getDeviceStatus() invalid UID = " + callingUid);
        EventLog.writeEvent(1397638484, "203549963", Integer.valueOf(callingUid), "getDeviceStatus: invalid uid");
        return null;
    }

    public void setDeviceStatus(IMidiDeviceServer iMidiDeviceServer, MidiDeviceStatus midiDeviceStatus) {
        Device device = this.mDevicesByServer.get(iMidiDeviceServer.asBinder());
        if (device != null) {
            if (Binder.getCallingUid() != device.getUid()) {
                throw new SecurityException("setDeviceStatus() caller UID " + Binder.getCallingUid() + " does not match device's UID " + device.getUid());
            }
            device.setDeviceStatus(midiDeviceStatus);
            notifyDeviceStatusChanged(device, midiDeviceStatus);
        }
    }

    public final void notifyDeviceStatusChanged(Device device, MidiDeviceStatus midiDeviceStatus) {
        synchronized (this.mClients) {
            for (Client client : this.mClients.values()) {
                client.deviceStatusChanged(device, midiDeviceStatus);
            }
        }
    }

    public final MidiDeviceInfo addDeviceLocked(int i, int i2, int i3, String[] strArr, String[] strArr2, Bundle bundle, IMidiDeviceServer iMidiDeviceServer, ServiceInfo serviceInfo, boolean z, int i4, int i5) {
        BluetoothDevice bluetoothDevice;
        int i6 = 0;
        for (Device device : this.mDevicesByInfo.values()) {
            if (device.getUid() == i4) {
                i6++;
            }
        }
        if (i6 >= 16) {
            throw new SecurityException("too many MIDI devices already created for UID = " + i4);
        }
        int i7 = this.mNextDeviceId;
        this.mNextDeviceId = i7 + 1;
        MidiDeviceInfo midiDeviceInfo = new MidiDeviceInfo(i, i7, i2, i3, strArr, strArr2, bundle, z, i5);
        Device device2 = null;
        if (iMidiDeviceServer != null) {
            try {
                iMidiDeviceServer.setDeviceInfo(midiDeviceInfo);
            } catch (RemoteException unused) {
                Log.e("MidiService", "RemoteException in setDeviceInfo()");
                return null;
            }
        }
        if (i == 3) {
            BluetoothDevice bluetoothDevice2 = (BluetoothDevice) bundle.getParcelable("bluetooth_device", BluetoothDevice.class);
            Device device3 = this.mBluetoothDevices.get(bluetoothDevice2);
            if (device3 != null) {
                device3.setDeviceInfo(midiDeviceInfo);
            }
            bluetoothDevice = bluetoothDevice2;
            device2 = device3;
        } else {
            bluetoothDevice = null;
        }
        if (device2 == null) {
            device2 = new Device(iMidiDeviceServer, midiDeviceInfo, serviceInfo, i4);
        }
        this.mDevicesByInfo.put(midiDeviceInfo, device2);
        if (bluetoothDevice != null) {
            this.mBluetoothDevices.put(bluetoothDevice, device2);
        }
        synchronized (this.mClients) {
            for (Client client : this.mClients.values()) {
                client.deviceAdded(device2);
            }
        }
        return midiDeviceInfo;
    }

    public final void removeDeviceLocked(Device device) {
        IMidiDeviceServer deviceServer = device.getDeviceServer();
        if (deviceServer != null) {
            this.mDevicesByServer.remove(deviceServer.asBinder());
        }
        this.mDevicesByInfo.remove(device.getDeviceInfo());
        synchronized (this.mClients) {
            for (Client client : this.mClients.values()) {
                client.deviceRemoved(device);
            }
        }
    }

    public final void addPackageDeviceServers(String str) {
        try {
            ServiceInfo[] serviceInfoArr = this.mPackageManager.getPackageInfo(str, 132).services;
            if (serviceInfoArr == null) {
                return;
            }
            for (ServiceInfo serviceInfo : serviceInfoArr) {
                addPackageDeviceServer(serviceInfo);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("MidiService", "handlePackageUpdate could not find package " + str, e);
        }
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:105:? -> B:80:0x01d5). Please submit an issue!!! */
    public final void addPackageDeviceServer(ServiceInfo serviceInfo) {
        int i;
        ArrayList arrayList;
        ArrayList arrayList2;
        int i2;
        HashMap<MidiDeviceInfo, Device> hashMap;
        HashMap<MidiDeviceInfo, Device> hashMap2;
        String[] strArr;
        String str;
        String str2;
        XmlResourceParser xmlResourceParser = null;
        try {
            try {
                XmlResourceParser loadXmlMetaData = serviceInfo.loadXmlMetaData(this.mPackageManager, "android.media.midi.MidiDeviceService");
                if (loadXmlMetaData == null) {
                    if (loadXmlMetaData != null) {
                        loadXmlMetaData.close();
                        return;
                    }
                    return;
                }
                try {
                    if (!"android.permission.BIND_MIDI_DEVICE_SERVICE".equals(serviceInfo.permission)) {
                        Log.w("MidiService", "Skipping MIDI device service " + serviceInfo.packageName + ": it does not require the permission android.permission.BIND_MIDI_DEVICE_SERVICE");
                        loadXmlMetaData.close();
                        return;
                    }
                    ArrayList arrayList3 = new ArrayList();
                    ArrayList arrayList4 = new ArrayList();
                    int i3 = 0;
                    int i4 = 0;
                    int i5 = 0;
                    int i6 = false;
                    Bundle bundle = null;
                    while (true) {
                        int next = loadXmlMetaData.next();
                        if (next == 1) {
                            loadXmlMetaData.close();
                            return;
                        }
                        if (next == 2) {
                            String name = loadXmlMetaData.getName();
                            if ("device".equals(name)) {
                                if (bundle != null) {
                                    Log.w("MidiService", "nested <device> elements in metadata for " + serviceInfo.packageName);
                                } else {
                                    bundle = new Bundle();
                                    bundle.putParcelable("service_info", serviceInfo);
                                    int attributeCount = loadXmlMetaData.getAttributeCount();
                                    int i7 = i3;
                                    i6 = i7;
                                    while (i7 < attributeCount) {
                                        String attributeName = loadXmlMetaData.getAttributeName(i7);
                                        String attributeValue = loadXmlMetaData.getAttributeValue(i7);
                                        if ("private".equals(attributeName)) {
                                            i6 = "true".equals(attributeValue);
                                        } else {
                                            bundle.putString(attributeName, attributeValue);
                                        }
                                        i7++;
                                    }
                                    i4 = i3;
                                    i5 = i4;
                                }
                            } else if ("input-port".equals(name)) {
                                if (bundle == null) {
                                    Log.w("MidiService", "<input-port> outside of <device> in metadata for " + serviceInfo.packageName);
                                } else {
                                    i4++;
                                    int attributeCount2 = loadXmlMetaData.getAttributeCount();
                                    int i8 = i3;
                                    while (true) {
                                        if (i8 >= attributeCount2) {
                                            str2 = null;
                                            break;
                                        }
                                        String attributeName2 = loadXmlMetaData.getAttributeName(i8);
                                        str2 = loadXmlMetaData.getAttributeValue(i8);
                                        if ("name".equals(attributeName2)) {
                                            break;
                                        }
                                        i8++;
                                    }
                                    arrayList3.add(str2);
                                }
                            } else if ("output-port".equals(name)) {
                                if (bundle == null) {
                                    Log.w("MidiService", "<output-port> outside of <device> in metadata for " + serviceInfo.packageName);
                                } else {
                                    i5++;
                                    int attributeCount3 = loadXmlMetaData.getAttributeCount();
                                    int i9 = i3;
                                    while (true) {
                                        if (i9 >= attributeCount3) {
                                            str = null;
                                            break;
                                        }
                                        String attributeName3 = loadXmlMetaData.getAttributeName(i9);
                                        str = loadXmlMetaData.getAttributeValue(i9);
                                        if ("name".equals(attributeName3)) {
                                            break;
                                        }
                                        i9++;
                                    }
                                    arrayList4.add(str);
                                }
                            }
                        } else if (next == 3 && "device".equals(loadXmlMetaData.getName()) && bundle != null) {
                            if (i4 == 0 && i5 == 0) {
                                Log.w("MidiService", "<device> with no ports in metadata for " + serviceInfo.packageName);
                            } else {
                                try {
                                    i2 = this.mPackageManager.getApplicationInfo(serviceInfo.packageName, i3).uid;
                                    hashMap = this.mDevicesByInfo;
                                } catch (PackageManager.NameNotFoundException unused) {
                                    i = i3;
                                    arrayList = arrayList4;
                                    arrayList2 = arrayList3;
                                    Log.e("MidiService", "could not fetch ApplicationInfo for " + serviceInfo.packageName);
                                }
                                synchronized (hashMap) {
                                    try {
                                        strArr = EMPTY_STRING_ARRAY;
                                        hashMap2 = hashMap;
                                        i = i3;
                                        arrayList = arrayList4;
                                        arrayList2 = arrayList3;
                                    } catch (Throwable th) {
                                        th = th;
                                        hashMap2 = hashMap;
                                        throw th;
                                    }
                                    try {
                                        addDeviceLocked(2, i4, i5, (String[]) arrayList3.toArray(strArr), (String[]) arrayList4.toArray(strArr), bundle, null, serviceInfo, i6, i2, -1);
                                        arrayList2.clear();
                                        arrayList.clear();
                                        bundle = null;
                                        arrayList3 = arrayList2;
                                        i3 = i;
                                        arrayList4 = arrayList;
                                    } catch (Throwable th2) {
                                        th = th2;
                                        throw th;
                                    }
                                }
                            }
                        }
                        i = i3;
                        arrayList = arrayList4;
                        arrayList2 = arrayList3;
                        arrayList3 = arrayList2;
                        i3 = i;
                        arrayList4 = arrayList;
                    }
                } catch (Exception e) {
                    e = e;
                    xmlResourceParser = loadXmlMetaData;
                    Log.w("MidiService", "Unable to load component info " + serviceInfo.toString(), e);
                    if (xmlResourceParser != null) {
                        xmlResourceParser.close();
                    }
                } catch (Throwable th3) {
                    th = th3;
                    xmlResourceParser = loadXmlMetaData;
                    if (xmlResourceParser != null) {
                        xmlResourceParser.close();
                    }
                    throw th;
                }
            } catch (Throwable th4) {
                th = th4;
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    public final void removePackageDeviceServers(String str) {
        synchronized (this.mDevicesByInfo) {
            Iterator<Device> it = this.mDevicesByInfo.values().iterator();
            while (it.hasNext()) {
                Device next = it.next();
                if (str.equals(next.getPackageName())) {
                    it.remove();
                    removeDeviceLocked(next);
                }
            }
        }
    }

    public void updateTotalBytes(IMidiDeviceServer iMidiDeviceServer, int i, int i2) {
        synchronized (this.mDevicesByInfo) {
            Device device = this.mDevicesByServer.get(iMidiDeviceServer.asBinder());
            if (device != null) {
                device.updateTotalBytes(i, i2);
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, "MidiService", printWriter)) {
            IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
            indentingPrintWriter.println("MIDI Manager State:");
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.println("Devices:");
            indentingPrintWriter.increaseIndent();
            synchronized (this.mDevicesByInfo) {
                for (Device device : this.mDevicesByInfo.values()) {
                    indentingPrintWriter.println(device.toString());
                }
            }
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println("Clients:");
            indentingPrintWriter.increaseIndent();
            synchronized (this.mClients) {
                for (Client client : this.mClients.values()) {
                    indentingPrintWriter.println(client.toString());
                }
            }
            indentingPrintWriter.decreaseIndent();
        }
    }

    @GuardedBy({"mUsbMidiLock"})
    public final boolean isUsbMidiDeviceInUseLocked(MidiDeviceInfo midiDeviceInfo) {
        String string = midiDeviceInfo.getProperties().getString("name");
        if (string.length() < 8) {
            return false;
        }
        String extractUsbDeviceName = extractUsbDeviceName(string);
        String extractUsbDeviceTag = extractUsbDeviceTag(string);
        Log.i("MidiService", "Checking " + extractUsbDeviceName + " " + extractUsbDeviceTag);
        if (this.mUsbMidiUniversalDeviceInUse.contains(extractUsbDeviceName)) {
            return true;
        }
        return extractUsbDeviceTag.equals("MIDI 2.0") && this.mUsbMidiLegacyDeviceOpenCount.containsKey(extractUsbDeviceName);
    }

    @GuardedBy({"mUsbMidiLock"})
    public void addUsbMidiDeviceLocked(MidiDeviceInfo midiDeviceInfo) {
        String string = midiDeviceInfo.getProperties().getString("name");
        if (string.length() < 8) {
            return;
        }
        String extractUsbDeviceName = extractUsbDeviceName(string);
        String extractUsbDeviceTag = extractUsbDeviceTag(string);
        Log.i("MidiService", "Adding " + extractUsbDeviceName + " " + extractUsbDeviceTag);
        if (extractUsbDeviceTag.equals("MIDI 2.0")) {
            this.mUsbMidiUniversalDeviceInUse.add(extractUsbDeviceName);
        } else if (extractUsbDeviceTag.equals("MIDI 1.0")) {
            this.mUsbMidiLegacyDeviceOpenCount.put(extractUsbDeviceName, Integer.valueOf(this.mUsbMidiLegacyDeviceOpenCount.getOrDefault(extractUsbDeviceName, 0).intValue() + 1));
        }
    }

    @GuardedBy({"mUsbMidiLock"})
    public void removeUsbMidiDeviceLocked(MidiDeviceInfo midiDeviceInfo) {
        String string = midiDeviceInfo.getProperties().getString("name");
        if (string.length() < 8) {
            return;
        }
        String extractUsbDeviceName = extractUsbDeviceName(string);
        String extractUsbDeviceTag = extractUsbDeviceTag(string);
        Log.i("MidiService", "Removing " + extractUsbDeviceName + " " + extractUsbDeviceTag);
        if (extractUsbDeviceTag.equals("MIDI 2.0")) {
            this.mUsbMidiUniversalDeviceInUse.remove(extractUsbDeviceName);
        } else if (extractUsbDeviceTag.equals("MIDI 1.0") && this.mUsbMidiLegacyDeviceOpenCount.containsKey(extractUsbDeviceName)) {
            int intValue = this.mUsbMidiLegacyDeviceOpenCount.get(extractUsbDeviceName).intValue();
            if (intValue > 1) {
                this.mUsbMidiLegacyDeviceOpenCount.put(extractUsbDeviceName, Integer.valueOf(intValue - 1));
            } else {
                this.mUsbMidiLegacyDeviceOpenCount.remove(extractUsbDeviceName);
            }
        }
    }

    public String extractUsbDeviceName(String str) {
        return str.substring(0, str.length() - 8);
    }

    public String extractUsbDeviceTag(String str) {
        return str.substring(str.length() - 8);
    }
}
