package android.hardware.graphics.common;
/* loaded from: classes2.dex */
public @interface BufferUsage {
    public static final long CAMERA_INPUT = 262144;
    public static final long CAMERA_OUTPUT = 131072;
    public static final long COMPOSER_CLIENT_TARGET = 4096;
    public static final long COMPOSER_CURSOR = 32768;
    public static final long COMPOSER_OVERLAY = 2048;
    public static final long CPU_READ_MASK = 15;
    public static final long CPU_READ_NEVER = 0;
    public static final long CPU_READ_OFTEN = 3;
    public static final long CPU_READ_RARELY = 2;
    public static final long CPU_WRITE_MASK = 240;
    public static final long CPU_WRITE_NEVER = 0;
    public static final long CPU_WRITE_OFTEN = 48;
    public static final long CPU_WRITE_RARELY = 32;
    public static final long FRONT_BUFFER = 4294967296L;
    public static final long GPU_CUBE_MAP = 33554432;
    public static final long GPU_DATA_BUFFER = 16777216;
    public static final long GPU_MIPMAP_COMPLETE = 67108864;
    public static final long GPU_RENDER_TARGET = 512;
    public static final long GPU_TEXTURE = 256;
    public static final long HW_IMAGE_ENCODER = 134217728;
    public static final long PROTECTED = 16384;
    public static final long RENDERSCRIPT = 1048576;
    public static final long SENSOR_DIRECT_DATA = 8388608;
    public static final long VENDOR_MASK = -268435456;
    public static final long VENDOR_MASK_HI = -281474976710656L;
    public static final long VIDEO_DECODER = 4194304;
    public static final long VIDEO_ENCODER = 65536;
}
