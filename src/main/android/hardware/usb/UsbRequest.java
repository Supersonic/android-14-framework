package android.hardware.usb;

import android.util.Log;
import com.android.internal.util.Preconditions;
import dalvik.system.CloseGuard;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Objects;
/* loaded from: classes2.dex */
public class UsbRequest {
    static final int MAX_USBFS_BUFFER_SIZE = 16384;
    private static final String TAG = "UsbRequest";
    private ByteBuffer mBuffer;
    private Object mClientData;
    private UsbDeviceConnection mConnection;
    private UsbEndpoint mEndpoint;
    private boolean mIsUsingNewQueue;
    private int mLength;
    private long mNativeContext;
    private ByteBuffer mTempBuffer;
    private final CloseGuard mCloseGuard = CloseGuard.get();
    private final Object mLock = new Object();

    private native boolean native_cancel();

    private native void native_close();

    private native int native_dequeue_array(byte[] bArr, int i, boolean z);

    private native int native_dequeue_direct();

    private native boolean native_init(UsbDeviceConnection usbDeviceConnection, int i, int i2, int i3, int i4);

    private native boolean native_queue(ByteBuffer byteBuffer, int i, int i2);

    private native boolean native_queue_array(byte[] bArr, int i, boolean z);

    private native boolean native_queue_direct(ByteBuffer byteBuffer, int i, boolean z);

    public boolean initialize(UsbDeviceConnection connection, UsbEndpoint endpoint) {
        this.mEndpoint = endpoint;
        this.mConnection = (UsbDeviceConnection) Objects.requireNonNull(connection, "connection");
        boolean wasInitialized = native_init(connection, endpoint.getAddress(), endpoint.getAttributes(), endpoint.getMaxPacketSize(), endpoint.getInterval());
        if (wasInitialized) {
            this.mCloseGuard.open("UsbRequest.close");
        }
        return wasInitialized;
    }

    public void close() {
        synchronized (this.mLock) {
            if (this.mNativeContext != 0) {
                this.mEndpoint = null;
                this.mConnection = null;
                native_close();
                this.mCloseGuard.close();
            }
        }
    }

    protected void finalize() throws Throwable {
        try {
            CloseGuard closeGuard = this.mCloseGuard;
            if (closeGuard != null) {
                closeGuard.warnIfOpen();
            }
            close();
        } finally {
            super.finalize();
        }
    }

    public UsbEndpoint getEndpoint() {
        return this.mEndpoint;
    }

    public Object getClientData() {
        return this.mClientData;
    }

    public void setClientData(Object data) {
        this.mClientData = data;
    }

    @Deprecated
    public boolean queue(ByteBuffer buffer, int length) {
        UsbDeviceConnection connection = this.mConnection;
        if (connection == null) {
            throw new NullPointerException("invalid connection");
        }
        return connection.queueRequest(this, buffer, length);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean queueIfConnectionOpen(ByteBuffer buffer, int length) {
        int length2;
        boolean result;
        UsbDeviceConnection connection = this.mConnection;
        if (connection == null || !connection.isOpen()) {
            throw new NullPointerException("invalid connection");
        }
        boolean out = this.mEndpoint.getDirection() == 0;
        if (connection.getContext().getApplicationInfo().targetSdkVersion < 28 && length > 16384) {
            length2 = 16384;
        } else {
            length2 = length;
        }
        synchronized (this.mLock) {
            this.mBuffer = buffer;
            this.mLength = length2;
            if (buffer.isDirect()) {
                result = native_queue_direct(buffer, length2, out);
            } else {
                boolean result2 = buffer.hasArray();
                if (result2) {
                    result = native_queue_array(buffer.array(), length2, out);
                } else {
                    throw new IllegalArgumentException("buffer is not direct and has no array");
                }
            }
            if (!result) {
                this.mBuffer = null;
                this.mLength = 0;
            }
        }
        return result;
    }

    public boolean queue(ByteBuffer buffer) {
        UsbDeviceConnection connection = this.mConnection;
        if (connection == null) {
            throw new IllegalStateException("invalid connection");
        }
        return connection.queueRequest(this, buffer);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:30:0x006e A[Catch: all -> 0x00ad, TryCatch #0 {, blocks: (B:16:0x0032, B:18:0x0037, B:35:0x00a3, B:19:0x003e, B:21:0x004c, B:22:0x0057, B:28:0x0063, B:30:0x006e, B:32:0x007c, B:33:0x0092, B:34:0x0095), top: B:44:0x0032 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean queueIfConnectionOpen(ByteBuffer buffer) {
        boolean z;
        boolean wasQueued;
        UsbDeviceConnection connection = this.mConnection;
        if (connection == null || !connection.isOpen()) {
            throw new IllegalStateException("invalid connection");
        }
        Preconditions.checkState(this.mNativeContext != 0, "request is not initialized");
        Preconditions.checkState(!this.mIsUsingNewQueue, "this request is currently queued");
        boolean isSend = this.mEndpoint.getDirection() == 0;
        synchronized (this.mLock) {
            this.mBuffer = buffer;
            if (buffer == null) {
                this.mIsUsingNewQueue = true;
                wasQueued = native_queue(null, 0, 0);
            } else {
                if (connection.getContext().getApplicationInfo().targetSdkVersion < 28) {
                    Preconditions.checkArgumentInRange(buffer.remaining(), 0, 16384, "number of remaining bytes");
                }
                if (buffer.isReadOnly() && !isSend) {
                    z = false;
                    Preconditions.checkArgument(z, "buffer can not be read-only when receiving data");
                    if (!buffer.isDirect()) {
                        this.mTempBuffer = ByteBuffer.allocateDirect(this.mBuffer.remaining());
                        if (isSend) {
                            this.mBuffer.mark();
                            this.mTempBuffer.put(this.mBuffer);
                            this.mTempBuffer.flip();
                            this.mBuffer.reset();
                        }
                        buffer = this.mTempBuffer;
                    }
                    this.mIsUsingNewQueue = true;
                    wasQueued = native_queue(buffer, buffer.position(), buffer.remaining());
                }
                z = true;
                Preconditions.checkArgument(z, "buffer can not be read-only when receiving data");
                if (!buffer.isDirect()) {
                }
                this.mIsUsingNewQueue = true;
                wasQueued = native_queue(buffer, buffer.position(), buffer.remaining());
            }
        }
        if (!wasQueued) {
            this.mIsUsingNewQueue = false;
            this.mTempBuffer = null;
            this.mBuffer = null;
        }
        return wasQueued;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dequeue(boolean useBufferOverflowInsteadOfIllegalArg) {
        int bytesTransferred;
        boolean isSend = this.mEndpoint.getDirection() == 0;
        synchronized (this.mLock) {
            if (this.mIsUsingNewQueue) {
                int bytesTransferred2 = native_dequeue_direct();
                this.mIsUsingNewQueue = false;
                ByteBuffer byteBuffer = this.mBuffer;
                if (byteBuffer != null) {
                    ByteBuffer byteBuffer2 = this.mTempBuffer;
                    if (byteBuffer2 == null) {
                        byteBuffer.position(byteBuffer.position() + bytesTransferred2);
                    } else {
                        byteBuffer2.limit(bytesTransferred2);
                        if (isSend) {
                            ByteBuffer byteBuffer3 = this.mBuffer;
                            byteBuffer3.position(byteBuffer3.position() + bytesTransferred2);
                        } else {
                            this.mBuffer.put(this.mTempBuffer);
                        }
                        this.mTempBuffer = null;
                    }
                }
            } else {
                if (this.mBuffer.isDirect()) {
                    bytesTransferred = native_dequeue_direct();
                } else {
                    bytesTransferred = native_dequeue_array(this.mBuffer.array(), this.mLength, isSend);
                }
                if (bytesTransferred >= 0) {
                    int bytesToStore = Math.min(bytesTransferred, this.mLength);
                    try {
                        this.mBuffer.position(bytesToStore);
                    } catch (IllegalArgumentException e) {
                        if (!useBufferOverflowInsteadOfIllegalArg) {
                            throw e;
                        }
                        Log.m109e(TAG, "Buffer " + this.mBuffer + " does not have enough space to read " + bytesToStore + " bytes", e);
                        throw new BufferOverflowException();
                    }
                }
            }
            this.mBuffer = null;
            this.mLength = 0;
        }
    }

    public boolean cancel() {
        UsbDeviceConnection connection = this.mConnection;
        if (connection == null) {
            return false;
        }
        return connection.cancelRequest(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean cancelIfOpen() {
        UsbDeviceConnection connection = this.mConnection;
        if (this.mNativeContext == 0 || (connection != null && !connection.isOpen())) {
            Log.m104w(TAG, "Detected attempt to cancel a request on a connection which isn't open");
            return false;
        }
        return native_cancel();
    }
}
