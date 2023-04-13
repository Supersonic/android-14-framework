package android.hardware.usb;

import android.annotation.SystemApi;
import android.content.Context;
import android.p008os.ParcelFileDescriptor;
import com.android.internal.location.GpsNetInitiatedHandler;
import com.android.internal.util.Preconditions;
import dalvik.system.CloseGuard;
import java.io.FileDescriptor;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeoutException;
/* loaded from: classes2.dex */
public class UsbDeviceConnection {
    private static final String TAG = "UsbDeviceConnection";
    private Context mContext;
    private final UsbDevice mDevice;
    private long mNativeContext;
    private final CloseGuard mCloseGuard = CloseGuard.get();
    private final Object mLock = new Object();

    private native int native_bulk_request(int i, byte[] bArr, int i2, int i3, int i4);

    private native boolean native_claim_interface(int i, boolean z);

    private native void native_close();

    private native int native_control_request(int i, int i2, int i3, int i4, byte[] bArr, int i5, int i6, int i7);

    private native byte[] native_get_desc();

    private native int native_get_fd();

    private native String native_get_serial();

    private native boolean native_open(String str, FileDescriptor fileDescriptor);

    private native boolean native_release_interface(int i);

    private native UsbRequest native_request_wait(long j) throws TimeoutException;

    private native boolean native_reset_device();

    private native boolean native_set_configuration(int i);

    private native boolean native_set_interface(int i, int i2);

    public UsbDeviceConnection(UsbDevice device) {
        this.mDevice = device;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean open(String name, ParcelFileDescriptor pfd, Context context) {
        boolean wasOpened;
        this.mContext = context.getApplicationContext();
        synchronized (this.mLock) {
            wasOpened = native_open(name, pfd.getFileDescriptor());
            if (wasOpened) {
                this.mCloseGuard.open("UsbDeviceConnection.close");
            }
        }
        return wasOpened;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isOpen() {
        return this.mNativeContext != 0;
    }

    public Context getContext() {
        return this.mContext;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean cancelRequest(UsbRequest request) {
        synchronized (this.mLock) {
            if (isOpen()) {
                return request.cancelIfOpen();
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean queueRequest(UsbRequest request, ByteBuffer buffer, int length) {
        synchronized (this.mLock) {
            if (isOpen()) {
                return request.queueIfConnectionOpen(buffer, length);
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean queueRequest(UsbRequest request, ByteBuffer buffer) {
        synchronized (this.mLock) {
            if (isOpen()) {
                return request.queueIfConnectionOpen(buffer);
            }
            return false;
        }
    }

    public void close() {
        synchronized (this.mLock) {
            if (isOpen()) {
                native_close();
                this.mCloseGuard.close();
            }
        }
    }

    public int getFileDescriptor() {
        return native_get_fd();
    }

    public byte[] getRawDescriptors() {
        return native_get_desc();
    }

    public boolean claimInterface(UsbInterface intf, boolean force) {
        return native_claim_interface(intf.getId(), force);
    }

    public boolean releaseInterface(UsbInterface intf) {
        return native_release_interface(intf.getId());
    }

    public boolean setInterface(UsbInterface intf) {
        return native_set_interface(intf.getId(), intf.getAlternateSetting());
    }

    public boolean setConfiguration(UsbConfiguration configuration) {
        return native_set_configuration(configuration.getId());
    }

    public int controlTransfer(int requestType, int request, int value, int index, byte[] buffer, int length, int timeout) {
        return controlTransfer(requestType, request, value, index, buffer, 0, length, timeout);
    }

    public int controlTransfer(int requestType, int request, int value, int index, byte[] buffer, int offset, int length, int timeout) {
        checkBounds(buffer, offset, length);
        return native_control_request(requestType, request, value, index, buffer, offset, length, timeout);
    }

    public int bulkTransfer(UsbEndpoint endpoint, byte[] buffer, int length, int timeout) {
        return bulkTransfer(endpoint, buffer, 0, length, timeout);
    }

    public int bulkTransfer(UsbEndpoint endpoint, byte[] buffer, int offset, int length, int timeout) {
        checkBounds(buffer, offset, length);
        if (this.mContext.getApplicationInfo().targetSdkVersion < 28 && length > 16384) {
            length = 16384;
        }
        return native_bulk_request(endpoint.getAddress(), buffer, offset, length, timeout);
    }

    @SystemApi
    public boolean resetDevice() {
        return native_reset_device();
    }

    public UsbRequest requestWait() {
        UsbRequest request = null;
        try {
            request = native_request_wait(-1L);
        } catch (TimeoutException e) {
        }
        if (request != null) {
            request.dequeue(this.mContext.getApplicationInfo().targetSdkVersion >= 26);
        }
        return request;
    }

    public UsbRequest requestWait(long timeout) throws TimeoutException {
        UsbRequest request = native_request_wait(Preconditions.checkArgumentNonnegative(timeout, GpsNetInitiatedHandler.NI_INTENT_KEY_TIMEOUT));
        if (request != null) {
            request.dequeue(true);
        }
        return request;
    }

    public String getSerial() {
        return native_get_serial();
    }

    private static void checkBounds(byte[] buffer, int start, int length) {
        int bufferLength = buffer != null ? buffer.length : 0;
        if (length < 0 || start < 0 || start + length > bufferLength) {
            throw new IllegalArgumentException("Buffer start or length out of bounds.");
        }
    }

    protected void finalize() throws Throwable {
        try {
            CloseGuard closeGuard = this.mCloseGuard;
            if (closeGuard != null) {
                closeGuard.warnIfOpen();
            }
        } finally {
            super.finalize();
        }
    }
}
