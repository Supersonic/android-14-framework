package android.hardware.camera2.utils;

import android.hardware.camera2.params.StreamConfigurationMap;
import android.system.OsConstants;
import android.util.Range;
import android.util.Size;
import android.view.Surface;
import com.android.internal.util.Preconditions;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class SurfaceUtils {
    private static final int BAD_VALUE = -OsConstants.EINVAL;
    private static final int BGRA_8888 = 5;
    private static final int USAGE_HW_COMPOSER = 2048;
    private static final int USAGE_RENDERSCRIPT = 1048576;

    private static native int nativeDetectSurfaceDataspace(Surface surface);

    private static native int nativeDetectSurfaceDimens(Surface surface, int[] iArr);

    private static native int nativeDetectSurfaceType(Surface surface);

    private static native long nativeDetectSurfaceUsageFlags(Surface surface);

    private static native long nativeGetSurfaceId(Surface surface);

    public static boolean isSurfaceForPreview(Surface surface) {
        Preconditions.checkNotNull(surface);
        long usageFlags = nativeDetectSurfaceUsageFlags(surface);
        boolean previewConsumer = (usageFlags & 1114115) == 0 && (usageFlags & 2816) != 0;
        getSurfaceFormat(surface);
        return previewConsumer;
    }

    public static boolean isSurfaceForHwVideoEncoder(Surface surface) {
        Preconditions.checkNotNull(surface);
        long usageFlags = nativeDetectSurfaceUsageFlags(surface);
        boolean videoEncoderConsumer = (usageFlags & 1050627) == 0 && (usageFlags & 65536) != 0;
        getSurfaceFormat(surface);
        return videoEncoderConsumer;
    }

    public static long getSurfaceId(Surface surface) {
        Preconditions.checkNotNull(surface);
        try {
            return nativeGetSurfaceId(surface);
        } catch (IllegalArgumentException e) {
            return 0L;
        }
    }

    public static long getSurfaceUsage(Surface surface) {
        Preconditions.checkNotNull(surface);
        try {
            return nativeDetectSurfaceUsageFlags(surface);
        } catch (IllegalArgumentException e) {
            return 0L;
        }
    }

    public static Size getSurfaceSize(Surface surface) {
        Preconditions.checkNotNull(surface);
        int[] dimens = new int[2];
        int errorFlag = nativeDetectSurfaceDimens(surface, dimens);
        if (errorFlag == BAD_VALUE) {
            throw new IllegalArgumentException("Surface was abandoned");
        }
        return new Size(dimens[0], dimens[1]);
    }

    public static int getSurfaceFormat(Surface surface) {
        Preconditions.checkNotNull(surface);
        int surfaceType = nativeDetectSurfaceType(surface);
        if (surfaceType == BAD_VALUE) {
            throw new IllegalArgumentException("Surface was abandoned");
        }
        if (surfaceType >= 1 && surfaceType <= 5) {
            return 34;
        }
        return surfaceType;
    }

    public static int detectSurfaceFormat(Surface surface) {
        Preconditions.checkNotNull(surface);
        int surfaceType = nativeDetectSurfaceType(surface);
        if (surfaceType == BAD_VALUE) {
            throw new IllegalArgumentException("Surface was abandoned");
        }
        return surfaceType;
    }

    public static int getSurfaceDataspace(Surface surface) {
        Preconditions.checkNotNull(surface);
        int dataSpace = nativeDetectSurfaceDataspace(surface);
        if (dataSpace == BAD_VALUE) {
            throw new IllegalArgumentException("Surface was abandoned");
        }
        return dataSpace;
    }

    public static boolean isFlexibleConsumer(Surface output) {
        Preconditions.checkNotNull(output);
        long usageFlags = nativeDetectSurfaceUsageFlags(output);
        return (usageFlags & 1114112) == 0 && (usageFlags & 2307) != 0;
    }

    private static void checkHighSpeedSurfaceFormat(Surface surface) {
        int surfaceFormat = getSurfaceFormat(surface);
        if (surfaceFormat != 34) {
            throw new IllegalArgumentException("Surface format(" + surfaceFormat + ") is not for preview or hardware video encoding!");
        }
    }

    public static void checkConstrainedHighSpeedSurfaces(Collection<Surface> surfaces, Range<Integer> fpsRange, StreamConfigurationMap config) {
        List<Size> highSpeedSizes;
        if (surfaces == null || surfaces.size() == 0 || surfaces.size() > 2) {
            throw new IllegalArgumentException("Output target surface list must not be null and the size must be 1 or 2");
        }
        if (fpsRange == null) {
            highSpeedSizes = Arrays.asList(config.getHighSpeedVideoSizes());
        } else {
            Range<Integer>[] highSpeedFpsRanges = config.getHighSpeedVideoFpsRanges();
            if (!Arrays.asList(highSpeedFpsRanges).contains(fpsRange)) {
                throw new IllegalArgumentException("Fps range " + fpsRange.toString() + " in the request is not a supported high speed fps range " + Arrays.toString(highSpeedFpsRanges));
            }
            highSpeedSizes = Arrays.asList(config.getHighSpeedVideoSizesFor(fpsRange));
        }
        for (Surface surface : surfaces) {
            checkHighSpeedSurfaceFormat(surface);
            Size surfaceSize = getSurfaceSize(surface);
            if (!highSpeedSizes.contains(surfaceSize)) {
                throw new IllegalArgumentException("Surface size " + surfaceSize.toString() + " is not part of the high speed supported size list " + Arrays.toString(highSpeedSizes.toArray()));
            }
            if (!isSurfaceForPreview(surface) && !isSurfaceForHwVideoEncoder(surface)) {
                throw new IllegalArgumentException("This output surface is neither preview nor hardware video encoding surface");
            }
            if (isSurfaceForPreview(surface) && isSurfaceForHwVideoEncoder(surface)) {
                throw new IllegalArgumentException("This output surface can not be both preview and hardware video encoding surface");
            }
        }
        if (surfaces.size() == 2) {
            Iterator<Surface> iterator = surfaces.iterator();
            boolean isFirstSurfacePreview = isSurfaceForPreview(iterator.next());
            boolean isSecondSurfacePreview = isSurfaceForPreview(iterator.next());
            if (isFirstSurfacePreview == isSecondSurfacePreview) {
                throw new IllegalArgumentException("The 2 output surfaces must have different type");
            }
        }
    }
}
