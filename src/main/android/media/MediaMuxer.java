package android.media;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.MediaCodec;
import dalvik.system.CloseGuard;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.Map;
/* loaded from: classes2.dex */
public final class MediaMuxer {
    private static final int MUXER_STATE_INITIALIZED = 0;
    private static final int MUXER_STATE_STARTED = 1;
    private static final int MUXER_STATE_STOPPED = 2;
    private static final int MUXER_STATE_UNINITIALIZED = -1;
    private long mNativeObject;
    private int mState = -1;
    private final CloseGuard mCloseGuard = CloseGuard.get();
    private int mLastTrackIndex = -1;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface Format {
    }

    private static native int nativeAddTrack(long j, String[] strArr, Object[] objArr);

    private static native void nativeRelease(long j);

    private static native void nativeSetLocation(long j, int i, int i2);

    private static native void nativeSetOrientationHint(long j, int i);

    private static native long nativeSetup(FileDescriptor fileDescriptor, int i) throws IllegalArgumentException, IOException;

    private static native void nativeStart(long j);

    private static native void nativeStop(long j);

    private static native void nativeWriteSampleData(long j, int i, ByteBuffer byteBuffer, int i2, int i3, long j2, int i4);

    static {
        System.loadLibrary("media_jni");
    }

    /* loaded from: classes2.dex */
    public static final class OutputFormat {
        public static final int MUXER_OUTPUT_3GPP = 2;
        public static final int MUXER_OUTPUT_FIRST = 0;
        public static final int MUXER_OUTPUT_HEIF = 3;
        public static final int MUXER_OUTPUT_LAST = 4;
        public static final int MUXER_OUTPUT_MPEG_4 = 0;
        public static final int MUXER_OUTPUT_OGG = 4;
        public static final int MUXER_OUTPUT_WEBM = 1;

        private OutputFormat() {
        }
    }

    private String convertMuxerStateCodeToString(int aState) {
        switch (aState) {
            case -1:
                return "UNINITIALIZED";
            case 0:
                return "INITIALIZED";
            case 1:
                return "STARTED";
            case 2:
                return "STOPPED";
            default:
                return "UNKNOWN";
        }
    }

    public MediaMuxer(String path, int format) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("path must not be null");
        }
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(path, "rws");
            file.setLength(0L);
            FileDescriptor fd = file.getFD();
            setUpMediaMuxer(fd, format);
            file.close();
        } catch (Throwable th) {
            if (file != null) {
                file.close();
            }
            throw th;
        }
    }

    public MediaMuxer(FileDescriptor fd, int format) throws IOException {
        setUpMediaMuxer(fd, format);
    }

    private void setUpMediaMuxer(FileDescriptor fd, int format) throws IOException {
        if (format < 0 || format > 4) {
            throw new IllegalArgumentException("format: " + format + " is invalid");
        }
        this.mNativeObject = nativeSetup(fd, format);
        this.mState = 0;
        this.mCloseGuard.open("release");
    }

    public void setOrientationHint(int degrees) {
        if (degrees != 0 && degrees != 90 && degrees != 180 && degrees != 270) {
            throw new IllegalArgumentException("Unsupported angle: " + degrees);
        }
        if (this.mState == 0) {
            nativeSetOrientationHint(this.mNativeObject, degrees);
            return;
        }
        throw new IllegalStateException("Can't set rotation degrees due to wrong state(" + convertMuxerStateCodeToString(this.mState) + NavigationBarInflaterView.KEY_CODE_END);
    }

    public void setLocation(float latitude, float longitude) {
        int latitudex10000 = Math.round(latitude * 10000.0f);
        int longitudex10000 = Math.round(10000.0f * longitude);
        if (latitudex10000 > 900000 || latitudex10000 < -900000) {
            String msg = "Latitude: " + latitude + " out of range.";
            throw new IllegalArgumentException(msg);
        } else if (longitudex10000 > 1800000 || longitudex10000 < -1800000) {
            String msg2 = "Longitude: " + longitude + " out of range";
            throw new IllegalArgumentException(msg2);
        } else {
            if (this.mState == 0) {
                long j = this.mNativeObject;
                if (j != 0) {
                    nativeSetLocation(j, latitudex10000, longitudex10000);
                    return;
                }
            }
            throw new IllegalStateException("Can't set location due to wrong state(" + convertMuxerStateCodeToString(this.mState) + NavigationBarInflaterView.KEY_CODE_END);
        }
    }

    public void start() {
        long j = this.mNativeObject;
        if (j == 0) {
            throw new IllegalStateException("Muxer has been released!");
        }
        if (this.mState == 0) {
            nativeStart(j);
            this.mState = 1;
            return;
        }
        throw new IllegalStateException("Can't start due to wrong state(" + convertMuxerStateCodeToString(this.mState) + NavigationBarInflaterView.KEY_CODE_END);
    }

    public void stop() {
        if (this.mState == 1) {
            try {
                try {
                    nativeStop(this.mNativeObject);
                    return;
                } catch (Exception e) {
                    throw e;
                }
            } finally {
                this.mState = 2;
            }
        }
        throw new IllegalStateException("Can't stop due to wrong state(" + convertMuxerStateCodeToString(this.mState) + NavigationBarInflaterView.KEY_CODE_END);
    }

    protected void finalize() throws Throwable {
        try {
            CloseGuard closeGuard = this.mCloseGuard;
            if (closeGuard != null) {
                closeGuard.warnIfOpen();
            }
            long j = this.mNativeObject;
            if (j != 0) {
                nativeRelease(j);
                this.mNativeObject = 0L;
            }
        } finally {
            super.finalize();
        }
    }

    public int addTrack(MediaFormat format) {
        if (format == null) {
            throw new IllegalArgumentException("format must not be null.");
        }
        if (this.mState != 0) {
            throw new IllegalStateException("Muxer is not initialized.");
        }
        if (this.mNativeObject == 0) {
            throw new IllegalStateException("Muxer has been released!");
        }
        Map<String, Object> formatMap = format.getMap();
        int mapSize = formatMap.size();
        if (mapSize > 0) {
            String[] keys = new String[mapSize];
            Object[] values = new Object[mapSize];
            int i = 0;
            for (Map.Entry<String, Object> entry : formatMap.entrySet()) {
                keys[i] = entry.getKey();
                values[i] = entry.getValue();
                i++;
            }
            int trackIndex = nativeAddTrack(this.mNativeObject, keys, values);
            if (this.mLastTrackIndex >= trackIndex) {
                throw new IllegalArgumentException("Invalid format.");
            }
            this.mLastTrackIndex = trackIndex;
            return trackIndex;
        }
        throw new IllegalArgumentException("format must not be empty.");
    }

    public void writeSampleData(int trackIndex, ByteBuffer byteBuf, MediaCodec.BufferInfo bufferInfo) {
        if (trackIndex < 0 || trackIndex > this.mLastTrackIndex) {
            throw new IllegalArgumentException("trackIndex is invalid");
        }
        if (byteBuf == null) {
            throw new IllegalArgumentException("byteBuffer must not be null");
        }
        if (bufferInfo == null) {
            throw new IllegalArgumentException("bufferInfo must not be null");
        }
        if (bufferInfo.size < 0 || bufferInfo.offset < 0 || bufferInfo.offset + bufferInfo.size > byteBuf.capacity()) {
            throw new IllegalArgumentException("bufferInfo must specify a valid buffer offset and size");
        }
        long j = this.mNativeObject;
        if (j == 0) {
            throw new IllegalStateException("Muxer has been released!");
        }
        if (this.mState != 1) {
            throw new IllegalStateException("Can't write, muxer is not started");
        }
        nativeWriteSampleData(j, trackIndex, byteBuf, bufferInfo.offset, bufferInfo.size, bufferInfo.presentationTimeUs, bufferInfo.flags);
    }

    public void release() {
        if (this.mState == 1) {
            stop();
        }
        long j = this.mNativeObject;
        if (j != 0) {
            nativeRelease(j);
            this.mNativeObject = 0L;
            this.mCloseGuard.close();
        }
        this.mState = -1;
    }
}
