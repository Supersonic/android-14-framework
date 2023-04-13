package android.media;

import android.app.compat.CompatChanges;
import android.hardware.Camera;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes2.dex */
public class CamcorderProfile {
    public static final int QUALITY_1080P = 6;
    public static final int QUALITY_2160P = 8;
    public static final int QUALITY_2K = 12;
    public static final int QUALITY_480P = 4;
    public static final int QUALITY_4KDCI = 10;
    public static final int QUALITY_720P = 5;
    public static final int QUALITY_8KUHD = 13;
    public static final int QUALITY_CIF = 3;
    public static final int QUALITY_HIGH = 1;
    public static final int QUALITY_HIGH_SPEED_1080P = 2004;
    public static final int QUALITY_HIGH_SPEED_2160P = 2005;
    public static final int QUALITY_HIGH_SPEED_480P = 2002;
    public static final int QUALITY_HIGH_SPEED_4KDCI = 2008;
    public static final int QUALITY_HIGH_SPEED_720P = 2003;
    public static final int QUALITY_HIGH_SPEED_CIF = 2006;
    public static final int QUALITY_HIGH_SPEED_HIGH = 2001;
    private static final int QUALITY_HIGH_SPEED_LIST_END = 2008;
    private static final int QUALITY_HIGH_SPEED_LIST_START = 2000;
    public static final int QUALITY_HIGH_SPEED_LOW = 2000;
    public static final int QUALITY_HIGH_SPEED_VGA = 2007;
    private static final int QUALITY_LIST_END = 13;
    private static final int QUALITY_LIST_START = 0;
    public static final int QUALITY_LOW = 0;
    public static final int QUALITY_QCIF = 2;
    public static final int QUALITY_QHD = 11;
    public static final int QUALITY_QVGA = 7;
    public static final int QUALITY_TIME_LAPSE_1080P = 1006;
    public static final int QUALITY_TIME_LAPSE_2160P = 1008;
    public static final int QUALITY_TIME_LAPSE_2K = 1012;
    public static final int QUALITY_TIME_LAPSE_480P = 1004;
    public static final int QUALITY_TIME_LAPSE_4KDCI = 1010;
    public static final int QUALITY_TIME_LAPSE_720P = 1005;
    public static final int QUALITY_TIME_LAPSE_8KUHD = 1013;
    public static final int QUALITY_TIME_LAPSE_CIF = 1003;
    public static final int QUALITY_TIME_LAPSE_HIGH = 1001;
    private static final int QUALITY_TIME_LAPSE_LIST_END = 1013;
    private static final int QUALITY_TIME_LAPSE_LIST_START = 1000;
    public static final int QUALITY_TIME_LAPSE_LOW = 1000;
    public static final int QUALITY_TIME_LAPSE_QCIF = 1002;
    public static final int QUALITY_TIME_LAPSE_QHD = 1011;
    public static final int QUALITY_TIME_LAPSE_QVGA = 1007;
    public static final int QUALITY_TIME_LAPSE_VGA = 1009;
    public static final int QUALITY_VGA = 9;
    public static final long RETURN_ADVANCED_VIDEO_PROFILES = 206033068;
    public int audioBitRate;
    public int audioChannels;
    public int audioCodec;
    public int audioSampleRate;
    public int duration;
    public int fileFormat;
    public int quality;
    public int videoBitRate;
    public int videoCodec;
    public int videoFrameHeight;
    public int videoFrameRate;
    public int videoFrameWidth;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface Quality {
    }

    private static final native CamcorderProfile native_get_camcorder_profile(int i, int i2);

    private static final native EncoderProfiles native_get_camcorder_profiles(int i, int i2, boolean z);

    private static final native boolean native_has_camcorder_profile(int i, int i2);

    private static final native void native_init();

    public static CamcorderProfile get(int quality) {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == 0) {
                return get(i, quality);
            }
        }
        return null;
    }

    public static CamcorderProfile get(int cameraId, int quality) {
        if ((quality < 0 || quality > 13) && ((quality < 1000 || quality > 1013) && (quality < 2000 || quality > 2008))) {
            String errMessage = "Unsupported quality level: " + quality;
            throw new IllegalArgumentException(errMessage);
        }
        return native_get_camcorder_profile(cameraId, quality);
    }

    public static EncoderProfiles getAll(String cameraId, int quality) {
        if ((quality < 0 || quality > 13) && ((quality < 1000 || quality > 1013) && (quality < 2000 || quality > 2008))) {
            String errMessage = "Unsupported quality level: " + quality;
            throw new IllegalArgumentException(errMessage);
        }
        try {
            int id = Integer.valueOf(cameraId).intValue();
            return native_get_camcorder_profiles(id, quality, CompatChanges.isChangeEnabled(RETURN_ADVANCED_VIDEO_PROFILES));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean hasProfile(int quality) {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == 0) {
                return hasProfile(i, quality);
            }
        }
        return false;
    }

    public static boolean hasProfile(int cameraId, int quality) {
        return native_has_camcorder_profile(cameraId, quality);
    }

    static {
        System.loadLibrary("media_jni");
        native_init();
    }

    private CamcorderProfile(int duration, int quality, int fileFormat, int videoCodec, int videoBitRate, int videoFrameRate, int videoWidth, int videoHeight, int audioCodec, int audioBitRate, int audioSampleRate, int audioChannels) {
        this.duration = duration;
        this.quality = quality;
        this.fileFormat = fileFormat;
        this.videoCodec = videoCodec;
        this.videoBitRate = videoBitRate;
        this.videoFrameRate = videoFrameRate;
        this.videoFrameWidth = videoWidth;
        this.videoFrameHeight = videoHeight;
        this.audioCodec = audioCodec;
        this.audioBitRate = audioBitRate;
        this.audioSampleRate = audioSampleRate;
        this.audioChannels = audioChannels;
    }
}
