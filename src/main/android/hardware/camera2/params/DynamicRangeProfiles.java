package android.hardware.camera2.params;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes.dex */
public final class DynamicRangeProfiles {
    public static final long DOLBY_VISION_10B_HDR_OEM = 64;
    public static final long DOLBY_VISION_10B_HDR_OEM_PO = 128;
    public static final long DOLBY_VISION_10B_HDR_REF = 16;
    public static final long DOLBY_VISION_10B_HDR_REF_PO = 32;
    public static final long DOLBY_VISION_8B_HDR_OEM = 1024;
    public static final long DOLBY_VISION_8B_HDR_OEM_PO = 2048;
    public static final long DOLBY_VISION_8B_HDR_REF = 256;
    public static final long DOLBY_VISION_8B_HDR_REF_PO = 512;
    public static final long HDR10 = 4;
    public static final long HDR10_PLUS = 8;
    public static final long HLG10 = 2;
    public static final long PUBLIC_MAX = 4096;
    public static final long STANDARD = 1;
    private final HashMap<Long, Set<Long>> mProfileMap = new HashMap<>();
    private final HashMap<Long, Boolean> mLookahedLatencyMap = new HashMap<>();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Profile {
    }

    public DynamicRangeProfiles(long[] elements) {
        if (elements.length % 3 != 0) {
            throw new IllegalArgumentException("Dynamic range profile map length " + elements.length + " is not even!");
        }
        int i = 0;
        while (true) {
            boolean z = false;
            if (i < elements.length) {
                checkProfileValue(elements[i]);
                if (elements[i] == 1) {
                    throw new IllegalArgumentException("Dynamic range profile map must not include a STANDARD profile entry!");
                }
                HashSet<Long> profiles = new HashSet<>();
                if (elements[i + 1] != 0) {
                    for (long profile = 1; profile < 4096; profile <<= 1) {
                        if ((elements[i + 1] & profile) != 0) {
                            profiles.add(Long.valueOf(profile));
                        }
                    }
                }
                this.mProfileMap.put(Long.valueOf(elements[i]), profiles);
                HashMap<Long, Boolean> hashMap = this.mLookahedLatencyMap;
                Long valueOf = Long.valueOf(elements[i]);
                if (elements[i + 2] != 0) {
                    z = true;
                }
                hashMap.put(valueOf, Boolean.valueOf(z));
                i += 3;
            } else {
                HashSet<Long> standardConstraints = new HashSet<>();
                standardConstraints.add(1L);
                for (Long profile2 : this.mProfileMap.keySet()) {
                    if (this.mProfileMap.get(profile2).isEmpty() || this.mProfileMap.get(profile2).contains(1L)) {
                        standardConstraints.add(profile2);
                    }
                }
                this.mProfileMap.put(1L, standardConstraints);
                this.mLookahedLatencyMap.put(1L, false);
                return;
            }
        }
    }

    public static void checkProfileValue(long profile) {
        if (profile != 1 && profile != 2 && profile != 4 && profile != 8 && profile != 16 && profile != 32 && profile != 64 && profile != 128 && profile != 256 && profile != 512 && profile != 1024 && profile != 2048) {
            throw new IllegalArgumentException("Unknown profile " + profile);
        }
    }

    public Set<Long> getSupportedProfiles() {
        return Collections.unmodifiableSet(this.mProfileMap.keySet());
    }

    public Set<Long> getProfileCaptureRequestConstraints(long profile) {
        Set<Long> ret = this.mProfileMap.get(Long.valueOf(profile));
        if (ret == null) {
            throw new IllegalArgumentException("Unsupported profile!");
        }
        return Collections.unmodifiableSet(ret);
    }

    public boolean isExtraLatencyPresent(long profile) {
        Boolean ret = this.mLookahedLatencyMap.get(Long.valueOf(profile));
        if (ret == null) {
            throw new IllegalArgumentException("Unsupported profile!");
        }
        return ret.booleanValue();
    }
}
