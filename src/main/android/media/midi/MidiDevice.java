package android.media.midi;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.Process;
import android.p008os.RemoteException;
import android.util.Log;
import dalvik.system.CloseGuard;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import libcore.io.IoUtils;
/* loaded from: classes2.dex */
public final class MidiDevice implements Closeable {
    private static final String TAG = "MidiDevice";
    private final IBinder mClientToken;
    private final MidiDeviceInfo mDeviceInfo;
    private final IMidiDeviceServer mDeviceServer;
    private final IBinder mDeviceServerBinder;
    private final IBinder mDeviceToken;
    private final CloseGuard mGuard;
    private boolean mIsDeviceClosed;
    private final IMidiManager mMidiManager;
    private long mNativeHandle;

    /* loaded from: classes2.dex */
    public class MidiConnection implements Closeable {
        private final CloseGuard mGuard;
        private final IMidiDeviceServer mInputPortDeviceServer;
        private final IBinder mInputPortToken;
        private boolean mIsClosed;
        private final IBinder mOutputPortToken;

        MidiConnection(IBinder outputPortToken, MidiInputPort inputPort) {
            CloseGuard closeGuard = CloseGuard.get();
            this.mGuard = closeGuard;
            this.mInputPortDeviceServer = inputPort.getDeviceServer();
            this.mInputPortToken = inputPort.getToken();
            this.mOutputPortToken = outputPortToken;
            closeGuard.open("close");
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            synchronized (this.mGuard) {
                if (this.mIsClosed) {
                    return;
                }
                this.mGuard.close();
                try {
                    this.mInputPortDeviceServer.closePort(this.mInputPortToken);
                    MidiDevice.this.mDeviceServer.closePort(this.mOutputPortToken);
                } catch (RemoteException e) {
                    Log.m110e(MidiDevice.TAG, "RemoteException in MidiConnection.close");
                }
                this.mIsClosed = true;
            }
        }

        protected void finalize() throws Throwable {
            try {
                CloseGuard closeGuard = this.mGuard;
                if (closeGuard != null) {
                    closeGuard.warnIfOpen();
                }
                close();
            } finally {
                super.finalize();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MidiDevice(MidiDeviceInfo deviceInfo, IMidiDeviceServer server, IMidiManager midiManager, IBinder clientToken, IBinder deviceToken) {
        CloseGuard closeGuard = CloseGuard.get();
        this.mGuard = closeGuard;
        this.mDeviceInfo = deviceInfo;
        this.mDeviceServer = server;
        this.mDeviceServerBinder = server.asBinder();
        this.mMidiManager = midiManager;
        this.mClientToken = clientToken;
        this.mDeviceToken = deviceToken;
        closeGuard.open("close");
    }

    public MidiDeviceInfo getInfo() {
        return this.mDeviceInfo;
    }

    public MidiInputPort openInputPort(int portNumber) {
        if (this.mIsDeviceClosed) {
            return null;
        }
        try {
            IBinder token = new Binder();
            FileDescriptor fd = this.mDeviceServer.openInputPort(token, portNumber);
            if (fd == null) {
                return null;
            }
            return new MidiInputPort(this.mDeviceServer, token, fd, portNumber);
        } catch (RemoteException e) {
            Log.m110e(TAG, "RemoteException in openInputPort");
            return null;
        }
    }

    public MidiOutputPort openOutputPort(int portNumber) {
        if (this.mIsDeviceClosed) {
            return null;
        }
        try {
            IBinder token = new Binder();
            FileDescriptor fd = this.mDeviceServer.openOutputPort(token, portNumber);
            if (fd == null) {
                return null;
            }
            return new MidiOutputPort(this.mDeviceServer, token, fd, portNumber);
        } catch (RemoteException e) {
            Log.m110e(TAG, "RemoteException in openOutputPort");
            return null;
        }
    }

    public MidiConnection connectPorts(MidiInputPort inputPort, int outputPortNumber) {
        FileDescriptor fd;
        if (outputPortNumber < 0 || outputPortNumber >= this.mDeviceInfo.getOutputPortCount()) {
            throw new IllegalArgumentException("outputPortNumber out of range");
        }
        if (this.mIsDeviceClosed || (fd = inputPort.claimFileDescriptor()) == null) {
            return null;
        }
        try {
            IBinder token = new Binder();
            int calleePid = this.mDeviceServer.connectPorts(token, fd, outputPortNumber);
            if (calleePid != Process.myPid()) {
                IoUtils.closeQuietly(fd);
            }
            return new MidiConnection(token, inputPort);
        } catch (RemoteException e) {
            Log.m110e(TAG, "RemoteException in connectPorts");
            return null;
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        synchronized (this.mGuard) {
            if (this.mNativeHandle != 0) {
                Log.m104w(TAG, "MidiDevice#close() called while there is an outstanding native client 0x" + Long.toHexString(this.mNativeHandle));
            }
            if (!this.mIsDeviceClosed && this.mNativeHandle == 0) {
                this.mGuard.close();
                this.mIsDeviceClosed = true;
                try {
                    this.mMidiManager.closeDevice(this.mClientToken, this.mDeviceToken);
                } catch (RemoteException e) {
                    Log.m110e(TAG, "RemoteException in closeDevice");
                }
            }
        }
    }

    protected void finalize() throws Throwable {
        try {
            CloseGuard closeGuard = this.mGuard;
            if (closeGuard != null) {
                closeGuard.warnIfOpen();
            }
            close();
        } finally {
            super.finalize();
        }
    }

    public String toString() {
        return "MidiDevice: " + this.mDeviceInfo.toString();
    }
}
