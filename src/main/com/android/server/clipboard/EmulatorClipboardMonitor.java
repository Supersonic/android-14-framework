package com.android.server.clipboard;

import android.content.ClipData;
import android.os.PersistableBundle;
import android.os.SystemProperties;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.VmSocketAddress;
import android.util.Slog;
import java.io.EOFException;
import java.io.FileDescriptor;
import java.io.InterruptedIOException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class EmulatorClipboardMonitor implements Consumer<ClipData> {
    public static final boolean LOG_CLIBOARD_ACCESS = SystemProperties.getBoolean("ro.boot.qemu.log_clipboard_access", false);
    public final Thread mHostMonitorThread;
    public FileDescriptor mPipe = null;

    public static byte[] createOpenHandshake() {
        byte[] copyOf = Arrays.copyOf("pipe:clipboard".getBytes(), 15);
        copyOf[14] = 0;
        return copyOf;
    }

    public final synchronized FileDescriptor getPipeFD() {
        return this.mPipe;
    }

    public final synchronized void setPipeFD(FileDescriptor fileDescriptor) {
        this.mPipe = fileDescriptor;
    }

    public static FileDescriptor openPipeImpl() {
        try {
            FileDescriptor socket = Os.socket(OsConstants.AF_VSOCK, OsConstants.SOCK_STREAM, 0);
            try {
                Os.connect(socket, new VmSocketAddress(5000, OsConstants.VMADDR_CID_HOST));
                byte[] createOpenHandshake = createOpenHandshake();
                writeFully(socket, createOpenHandshake, 0, createOpenHandshake.length);
                return socket;
            } catch (ErrnoException | InterruptedIOException | SocketException unused) {
                Os.close(socket);
                return null;
            }
        } catch (ErrnoException unused2) {
            return null;
        }
    }

    public static FileDescriptor openPipe() throws InterruptedException {
        FileDescriptor openPipeImpl = openPipeImpl();
        while (openPipeImpl == null) {
            Thread.sleep(100L);
            openPipeImpl = openPipeImpl();
        }
        return openPipeImpl;
    }

    public final byte[] receiveMessage(FileDescriptor fileDescriptor) throws ErrnoException, InterruptedIOException, EOFException, ProtocolException {
        byte[] bArr = new byte[4];
        readFully(fileDescriptor, bArr, 0, 4);
        ByteBuffer wrap = ByteBuffer.wrap(bArr);
        wrap.order(ByteOrder.LITTLE_ENDIAN);
        int i = wrap.getInt();
        if (i < 0 || i > 134217728) {
            throw new ProtocolException("Clipboard message length: " + i + " out of bounds.");
        }
        byte[] bArr2 = new byte[i];
        readFully(fileDescriptor, bArr2, 0, i);
        return bArr2;
    }

    public static void sendMessage(FileDescriptor fileDescriptor, byte[] bArr) throws ErrnoException, InterruptedIOException {
        byte[] bArr2 = new byte[4];
        ByteBuffer wrap = ByteBuffer.wrap(bArr2);
        wrap.order(ByteOrder.LITTLE_ENDIAN);
        wrap.putInt(bArr.length);
        writeFully(fileDescriptor, bArr2, 0, 4);
        writeFully(fileDescriptor, bArr, 0, bArr.length);
    }

    public EmulatorClipboardMonitor(final Consumer<ClipData> consumer) {
        Thread thread = new Thread(new Runnable() { // from class: com.android.server.clipboard.EmulatorClipboardMonitor$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                EmulatorClipboardMonitor.this.lambda$new$0(consumer);
            }
        });
        this.mHostMonitorThread = thread;
        thread.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(Consumer consumer) {
        while (true) {
            FileDescriptor fileDescriptor = null;
            while (!Thread.interrupted()) {
                if (fileDescriptor == null) {
                    try {
                        fileDescriptor = openPipe();
                        setPipeFD(fileDescriptor);
                    } catch (ErrnoException | EOFException | InterruptedIOException | InterruptedException | OutOfMemoryError | ProtocolException e) {
                        Slog.w("EmulatorClipboardMonitor", "Failure to read from host clipboard", e);
                        setPipeFD(null);
                        try {
                            Os.close(fileDescriptor);
                        } catch (ErrnoException unused) {
                        }
                    }
                }
                String str = new String(receiveMessage(fileDescriptor));
                ClipData clipData = new ClipData("host clipboard", new String[]{"text/plain"}, new ClipData.Item(str));
                PersistableBundle persistableBundle = new PersistableBundle();
                persistableBundle.putBoolean("com.android.systemui.SUPPRESS_CLIPBOARD_OVERLAY", true);
                clipData.getDescription().setExtras(persistableBundle);
                if (LOG_CLIBOARD_ACCESS) {
                    Slog.i("EmulatorClipboardMonitor", "Setting the guest clipboard to '" + str + "'");
                }
                consumer.accept(clipData);
            }
            return;
        }
    }

    @Override // java.util.function.Consumer
    public void accept(ClipData clipData) {
        FileDescriptor pipeFD = getPipeFD();
        if (pipeFD != null) {
            setHostClipboard(pipeFD, getClipString(clipData));
        }
    }

    public final String getClipString(ClipData clipData) {
        CharSequence text;
        return (clipData == null || clipData.getItemCount() == 0 || (text = clipData.getItemAt(0).getText()) == null) ? "" : text.toString();
    }

    public static void setHostClipboard(final FileDescriptor fileDescriptor, final String str) {
        new Thread(new Runnable() { // from class: com.android.server.clipboard.EmulatorClipboardMonitor$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                EmulatorClipboardMonitor.lambda$setHostClipboard$1(str, fileDescriptor);
            }
        }).start();
    }

    public static /* synthetic */ void lambda$setHostClipboard$1(String str, FileDescriptor fileDescriptor) {
        if (LOG_CLIBOARD_ACCESS) {
            Slog.i("EmulatorClipboardMonitor", "Setting the host clipboard to '" + str + "'");
        }
        try {
            sendMessage(fileDescriptor, str.getBytes());
        } catch (ErrnoException | InterruptedIOException e) {
            Slog.e("EmulatorClipboardMonitor", "Failed to set host clipboard " + e.getMessage());
        } catch (IllegalArgumentException unused) {
        }
    }

    public static void readFully(FileDescriptor fileDescriptor, byte[] bArr, int i, int i2) throws ErrnoException, InterruptedIOException, EOFException {
        while (i2 > 0) {
            int read = Os.read(fileDescriptor, bArr, i, i2);
            if (read <= 0) {
                throw new EOFException();
            }
            i += read;
            i2 -= read;
        }
    }

    public static void writeFully(FileDescriptor fileDescriptor, byte[] bArr, int i, int i2) throws ErrnoException, InterruptedIOException {
        while (i2 > 0) {
            int write = Os.write(fileDescriptor, bArr, i, i2);
            if (write <= 0) {
                throw new ErrnoException("write", OsConstants.EIO);
            }
            i += write;
            i2 -= write;
        }
    }
}
