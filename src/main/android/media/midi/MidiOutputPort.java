package android.media.midi;

import android.p008os.IBinder;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.util.Log;
import com.android.internal.midi.MidiDispatcher;
import dalvik.system.CloseGuard;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import libcore.io.IoUtils;
/* loaded from: classes2.dex */
public final class MidiOutputPort extends MidiSender implements Closeable {
    private static final String TAG = "MidiOutputPort";
    private IMidiDeviceServer mDeviceServer;
    private final MidiDispatcher mDispatcher;
    private final CloseGuard mGuard;
    private final FileInputStream mInputStream;
    private boolean mIsClosed;
    private final int mPortNumber;
    private final Thread mThread;
    private final IBinder mToken;
    private AtomicInteger mTotalBytes;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MidiOutputPort(IMidiDeviceServer server, IBinder token, FileDescriptor fd, int portNumber) {
        this.mDispatcher = new MidiDispatcher();
        CloseGuard closeGuard = CloseGuard.get();
        this.mGuard = closeGuard;
        this.mTotalBytes = new AtomicInteger();
        Thread thread = new Thread() { // from class: android.media.midi.MidiOutputPort.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                int count;
                byte[] buffer = new byte[1024];
                while (true) {
                    try {
                        count = MidiOutputPort.this.mInputStream.read(buffer);
                    } catch (IOException e) {
                    } catch (Throwable th) {
                        IoUtils.closeQuietly(MidiOutputPort.this.mInputStream);
                        throw th;
                    }
                    if (count >= 0) {
                        int packetType = MidiPortImpl.getPacketType(buffer, count);
                        switch (packetType) {
                            case 1:
                                int offset = MidiPortImpl.getDataOffset(buffer, count);
                                int size = MidiPortImpl.getDataSize(buffer, count);
                                long timestamp = MidiPortImpl.getPacketTimestamp(buffer, count);
                                MidiOutputPort.this.mDispatcher.send(buffer, offset, size, timestamp);
                                break;
                            case 2:
                                MidiOutputPort.this.mDispatcher.flush();
                                break;
                            default:
                                Log.m110e(MidiOutputPort.TAG, "Unknown packet type " + packetType);
                                break;
                        }
                        MidiOutputPort.this.mTotalBytes.addAndGet(count);
                    } else {
                        IoUtils.closeQuietly(MidiOutputPort.this.mInputStream);
                        return;
                    }
                }
            }
        };
        this.mThread = thread;
        this.mDeviceServer = server;
        this.mToken = token;
        this.mPortNumber = portNumber;
        this.mInputStream = new ParcelFileDescriptor.AutoCloseInputStream(new ParcelFileDescriptor(fd));
        thread.start();
        closeGuard.open("close");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MidiOutputPort(FileDescriptor fd, int portNumber) {
        this(null, null, fd, portNumber);
    }

    public final int getPortNumber() {
        return this.mPortNumber;
    }

    @Override // android.media.midi.MidiSender
    public void onConnect(MidiReceiver receiver) {
        this.mDispatcher.getSender().connect(receiver);
    }

    @Override // android.media.midi.MidiSender
    public void onDisconnect(MidiReceiver receiver) {
        this.mDispatcher.getSender().disconnect(receiver);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        synchronized (this.mGuard) {
            if (this.mIsClosed) {
                return;
            }
            this.mGuard.close();
            this.mInputStream.close();
            IMidiDeviceServer iMidiDeviceServer = this.mDeviceServer;
            if (iMidiDeviceServer != null) {
                try {
                    iMidiDeviceServer.closePort(this.mToken);
                } catch (RemoteException e) {
                    Log.m110e(TAG, "RemoteException in MidiOutputPort.close()");
                }
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
            this.mDeviceServer = null;
            close();
        } finally {
            super.finalize();
        }
    }

    public int pullTotalBytesCount() {
        return this.mTotalBytes.getAndSet(0);
    }
}
