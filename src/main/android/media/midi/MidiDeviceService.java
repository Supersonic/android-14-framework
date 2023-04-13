package android.media.midi;

import android.app.Service;
import android.content.Intent;
import android.media.midi.IMidiManager;
import android.media.midi.MidiDeviceServer;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.util.Log;
/* loaded from: classes2.dex */
public abstract class MidiDeviceService extends Service {
    public static final String SERVICE_INTERFACE = "android.media.midi.MidiDeviceService";
    private static final String TAG = "MidiDeviceService";
    private final MidiDeviceServer.Callback mCallback = new MidiDeviceServer.Callback() { // from class: android.media.midi.MidiDeviceService.1
        @Override // android.media.midi.MidiDeviceServer.Callback
        public void onDeviceStatusChanged(MidiDeviceServer server, MidiDeviceStatus status) {
            MidiDeviceService.this.onDeviceStatusChanged(status);
        }

        @Override // android.media.midi.MidiDeviceServer.Callback
        public void onClose() {
            MidiDeviceService.this.onClose();
        }
    };
    private MidiDeviceInfo mDeviceInfo;
    private IMidiManager mMidiManager;
    private MidiDeviceServer mServer;

    public abstract MidiReceiver[] onGetInputPortReceivers();

    @Override // android.app.Service
    public void onCreate() {
        MidiDeviceServer server;
        MidiDeviceInfo deviceInfo;
        IMidiManager asInterface = IMidiManager.Stub.asInterface(ServiceManager.getService("midi"));
        this.mMidiManager = asInterface;
        try {
            deviceInfo = asInterface.getServiceDeviceInfo(getPackageName(), getClass().getName());
        } catch (RemoteException e) {
            Log.m110e(TAG, "RemoteException in IMidiManager.getServiceDeviceInfo");
            server = null;
        }
        if (deviceInfo == null) {
            Log.m110e(TAG, "Could not find MidiDeviceInfo for MidiDeviceService " + this);
            return;
        }
        this.mDeviceInfo = deviceInfo;
        MidiReceiver[] inputPortReceivers = onGetInputPortReceivers();
        if (inputPortReceivers == null) {
            inputPortReceivers = new MidiReceiver[0];
        }
        server = new MidiDeviceServer(this.mMidiManager, inputPortReceivers, deviceInfo, this.mCallback);
        this.mServer = server;
    }

    public final MidiReceiver[] getOutputPortReceivers() {
        MidiDeviceServer midiDeviceServer = this.mServer;
        if (midiDeviceServer == null) {
            return null;
        }
        return midiDeviceServer.getOutputPortReceivers();
    }

    public final MidiDeviceInfo getDeviceInfo() {
        return this.mDeviceInfo;
    }

    public void onDeviceStatusChanged(MidiDeviceStatus status) {
    }

    public void onClose() {
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        MidiDeviceServer midiDeviceServer;
        if (SERVICE_INTERFACE.equals(intent.getAction()) && (midiDeviceServer = this.mServer) != null) {
            return midiDeviceServer.getBinderInterface().asBinder();
        }
        return null;
    }
}
