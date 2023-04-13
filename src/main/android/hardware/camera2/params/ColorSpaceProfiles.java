package android.hardware.camera2.params;

import android.graphics.ColorSpace;
import android.util.ArrayMap;
import android.util.ArraySet;
import java.util.Map;
import java.util.Set;
/* loaded from: classes.dex */
public final class ColorSpaceProfiles {
    public static final int UNSPECIFIED = -1;
    private final Map<ColorSpace.Named, Map<Integer, Set<Long>>> mProfileMap = new ArrayMap();

    public ColorSpaceProfiles(long[] elements) {
        if (elements.length % 3 != 0) {
            throw new IllegalArgumentException("Color space profile map length " + elements.length + " is not divisible by 3!");
        }
        for (int i = 0; i < elements.length; i += 3) {
            int colorSpace = (int) elements[i];
            checkProfileValue(colorSpace);
            ColorSpace.Named namedColorSpace = ColorSpace.Named.values()[colorSpace];
            int imageFormat = (int) elements[i + 1];
            long dynamicRangeProfileBitmap = elements[i + 2];
            if (!this.mProfileMap.containsKey(namedColorSpace)) {
                ArrayMap<Integer, Set<Long>> imageFormatMap = new ArrayMap<>();
                this.mProfileMap.put(namedColorSpace, imageFormatMap);
            }
            if (!this.mProfileMap.get(namedColorSpace).containsKey(Integer.valueOf(imageFormat))) {
                ArraySet<Long> dynamicRangeProfiles = new ArraySet<>();
                this.mProfileMap.get(namedColorSpace).put(Integer.valueOf(imageFormat), dynamicRangeProfiles);
            }
            if (dynamicRangeProfileBitmap != 0) {
                for (long dynamicRangeProfile = 1; dynamicRangeProfile < 4096; dynamicRangeProfile <<= 1) {
                    if ((dynamicRangeProfileBitmap & dynamicRangeProfile) != 0) {
                        this.mProfileMap.get(namedColorSpace).get(Integer.valueOf(imageFormat)).add(Long.valueOf(dynamicRangeProfile));
                    }
                }
            }
        }
    }

    public static void checkProfileValue(int colorSpace) {
        boolean found = false;
        ColorSpace.Named[] values = ColorSpace.Named.values();
        int length = values.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            ColorSpace.Named value = values[i];
            if (colorSpace != value.ordinal()) {
                i++;
            } else {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("Unknown ColorSpace " + colorSpace);
        }
    }

    public Map<ColorSpace.Named, Map<Integer, Set<Long>>> getProfileMap() {
        return this.mProfileMap;
    }

    public Set<ColorSpace.Named> getSupportedColorSpaces(int imageFormat) {
        ArraySet<ColorSpace.Named> supportedColorSpaceProfiles = new ArraySet<>();
        for (ColorSpace.Named colorSpace : this.mProfileMap.keySet()) {
            if (imageFormat == 0) {
                supportedColorSpaceProfiles.add(colorSpace);
            } else {
                Map<Integer, Set<Long>> imageFormatMap = this.mProfileMap.get(colorSpace);
                if (imageFormatMap.containsKey(Integer.valueOf(imageFormat))) {
                    supportedColorSpaceProfiles.add(colorSpace);
                }
            }
        }
        return supportedColorSpaceProfiles;
    }

    public Set<Integer> getSupportedImageFormatsForColorSpace(ColorSpace.Named colorSpace) {
        Map<Integer, Set<Long>> imageFormatMap = this.mProfileMap.get(colorSpace);
        if (imageFormatMap == null) {
            return new ArraySet();
        }
        return imageFormatMap.keySet();
    }

    public Set<Long> getSupportedDynamicRangeProfiles(ColorSpace.Named colorSpace, int imageFormat) {
        Set<Long> dynamicRangeProfiles;
        Map<Integer, Set<Long>> imageFormatMap = this.mProfileMap.get(colorSpace);
        if (imageFormatMap == null) {
            return new ArraySet();
        }
        if (imageFormat == 0) {
            dynamicRangeProfiles = new ArraySet();
            for (Integer num : imageFormatMap.keySet()) {
                int supportedImageFormat = num.intValue();
                Set<Long> supportedDynamicRangeProfiles = imageFormatMap.get(Integer.valueOf(supportedImageFormat));
                for (Long supportedDynamicRangeProfile : supportedDynamicRangeProfiles) {
                    dynamicRangeProfiles.add(supportedDynamicRangeProfile);
                }
            }
        } else {
            dynamicRangeProfiles = imageFormatMap.get(Integer.valueOf(imageFormat));
            if (dynamicRangeProfiles == null) {
                return new ArraySet();
            }
        }
        return dynamicRangeProfiles;
    }

    public Set<ColorSpace.Named> getSupportedColorSpacesForDynamicRange(int imageFormat, long dynamicRangeProfile) {
        ArraySet<ColorSpace.Named> supportedColorSpaceProfiles = new ArraySet<>();
        for (ColorSpace.Named colorSpace : this.mProfileMap.keySet()) {
            Map<Integer, Set<Long>> imageFormatMap = this.mProfileMap.get(colorSpace);
            if (imageFormat == 0) {
                for (Integer num : imageFormatMap.keySet()) {
                    int supportedImageFormat = num.intValue();
                    Set<Long> dynamicRangeProfiles = imageFormatMap.get(Integer.valueOf(supportedImageFormat));
                    if (dynamicRangeProfiles.contains(Long.valueOf(dynamicRangeProfile))) {
                        supportedColorSpaceProfiles.add(colorSpace);
                    }
                }
            } else if (imageFormatMap.containsKey(Integer.valueOf(imageFormat))) {
                Set<Long> dynamicRangeProfiles2 = imageFormatMap.get(Integer.valueOf(imageFormat));
                if (dynamicRangeProfiles2.contains(Long.valueOf(dynamicRangeProfile))) {
                    supportedColorSpaceProfiles.add(colorSpace);
                }
            }
        }
        return supportedColorSpaceProfiles;
    }
}
