package android.media;

import android.app.settings.SettingsEnums;
import android.bluetooth.hci.BluetoothHciProtoEnums;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.MediaCodecInfo;
import android.net.NetworkPolicyManager;
import android.p008os.Process;
import android.p008os.SystemProperties;
import android.sysprop.MediaProperties;
import android.util.Log;
import android.util.Pair;
import android.util.Range;
import android.util.Rational;
import android.util.Size;
import android.view.SurfaceControl;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.Protocol;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
/* loaded from: classes2.dex */
public final class MediaCodecInfo {
    private static final int DEFAULT_MAX_SUPPORTED_INSTANCES = 32;
    private static final int ERROR_NONE_SUPPORTED = 4;
    private static final int ERROR_UNRECOGNIZED = 1;
    private static final int ERROR_UNSUPPORTED = 2;
    private static final int FLAG_IS_ENCODER = 1;
    private static final int FLAG_IS_HARDWARE_ACCELERATED = 8;
    private static final int FLAG_IS_SOFTWARE_ONLY = 4;
    private static final int FLAG_IS_VENDOR = 2;
    private static final int MAX_SUPPORTED_INSTANCES_LIMIT = 256;
    private static final String TAG = "MediaCodecInfo";
    private String mCanonicalName;
    private Map<String, CodecCapabilities> mCaps = new HashMap();
    private int mFlags;
    private String mName;
    private static final Range<Integer> POSITIVE_INTEGERS = Range.create(1, Integer.MAX_VALUE);
    private static final Range<Long> POSITIVE_LONGS = Range.create(1L, Long.MAX_VALUE);
    private static final Range<Rational> POSITIVE_RATIONALS = Range.create(new Rational(1, Integer.MAX_VALUE), new Rational(Integer.MAX_VALUE, 1));
    private static final Range<Integer> FRAME_RATE_RANGE = Range.create(0, 960);
    private static final Range<Integer> BITRATE_RANGE = Range.create(0, 500000000);

    /* JADX INFO: Access modifiers changed from: package-private */
    public MediaCodecInfo(String name, String canonicalName, int flags, CodecCapabilities[] caps) {
        this.mName = name;
        this.mCanonicalName = canonicalName;
        this.mFlags = flags;
        for (CodecCapabilities c : caps) {
            this.mCaps.put(c.getMimeType(), c);
        }
    }

    public final String getName() {
        return this.mName;
    }

    public final String getCanonicalName() {
        return this.mCanonicalName;
    }

    public final boolean isAlias() {
        return !this.mName.equals(this.mCanonicalName);
    }

    public final boolean isEncoder() {
        return (this.mFlags & 1) != 0;
    }

    public final boolean isVendor() {
        return (this.mFlags & 2) != 0;
    }

    public final boolean isSoftwareOnly() {
        return (this.mFlags & 4) != 0;
    }

    public final boolean isHardwareAccelerated() {
        return (this.mFlags & 8) != 0;
    }

    public final String[] getSupportedTypes() {
        Set<String> typeSet = this.mCaps.keySet();
        String[] types = (String[]) typeSet.toArray(new String[typeSet.size()]);
        Arrays.sort(types);
        return types;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int checkPowerOfTwo(int value, String message) {
        if (((value - 1) & value) != 0) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class Feature {
        public boolean mDefault;
        public boolean mInternal;
        public String mName;
        public int mValue;

        public Feature(String name, int value, boolean def) {
            this(name, value, def, false);
        }

        public Feature(String name, int value, boolean def, boolean internal) {
            this.mName = name;
            this.mValue = value;
            this.mDefault = def;
            this.mInternal = internal;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class LazyHolder {
        private static final Range<Integer> SIZE_RANGE;

        private LazyHolder() {
        }

        static {
            SIZE_RANGE = Process.is64Bit() ? Range.create(1, 32768) : Range.create(1, MediaProperties.resolution_limit_32bit().orElse(4096));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Range<Integer> getSizeRange() {
        return LazyHolder.SIZE_RANGE;
    }

    /* loaded from: classes2.dex */
    public static final class CodecCapabilities {
        public static final int COLOR_Format12bitRGB444 = 3;
        public static final int COLOR_Format16bitARGB1555 = 5;
        public static final int COLOR_Format16bitARGB4444 = 4;
        public static final int COLOR_Format16bitBGR565 = 7;
        public static final int COLOR_Format16bitRGB565 = 6;
        public static final int COLOR_Format18BitBGR666 = 41;
        public static final int COLOR_Format18bitARGB1665 = 9;
        public static final int COLOR_Format18bitRGB666 = 8;
        public static final int COLOR_Format19bitARGB1666 = 10;
        public static final int COLOR_Format24BitABGR6666 = 43;
        public static final int COLOR_Format24BitARGB6666 = 42;
        public static final int COLOR_Format24bitARGB1887 = 13;
        public static final int COLOR_Format24bitBGR888 = 12;
        public static final int COLOR_Format24bitRGB888 = 11;
        public static final int COLOR_Format25bitARGB1888 = 14;
        public static final int COLOR_Format32bitABGR2101010 = 2130750114;
        public static final int COLOR_Format32bitABGR8888 = 2130747392;
        public static final int COLOR_Format32bitARGB8888 = 16;
        public static final int COLOR_Format32bitBGRA8888 = 15;
        public static final int COLOR_Format64bitABGRFloat = 2130710294;
        public static final int COLOR_Format8bitRGB332 = 2;
        public static final int COLOR_FormatCbYCrY = 27;
        public static final int COLOR_FormatCrYCbY = 28;
        public static final int COLOR_FormatL16 = 36;
        public static final int COLOR_FormatL2 = 33;
        public static final int COLOR_FormatL24 = 37;
        public static final int COLOR_FormatL32 = 38;
        public static final int COLOR_FormatL4 = 34;
        public static final int COLOR_FormatL8 = 35;
        public static final int COLOR_FormatMonochrome = 1;
        public static final int COLOR_FormatRGBAFlexible = 2134288520;
        public static final int COLOR_FormatRGBFlexible = 2134292616;
        public static final int COLOR_FormatRawBayer10bit = 31;
        public static final int COLOR_FormatRawBayer8bit = 30;
        public static final int COLOR_FormatRawBayer8bitcompressed = 32;
        public static final int COLOR_FormatSurface = 2130708361;
        public static final int COLOR_FormatYCbYCr = 25;
        public static final int COLOR_FormatYCrYCb = 26;
        public static final int COLOR_FormatYUV411PackedPlanar = 18;
        public static final int COLOR_FormatYUV411Planar = 17;
        public static final int COLOR_FormatYUV420Flexible = 2135033992;
        public static final int COLOR_FormatYUV420PackedPlanar = 20;
        public static final int COLOR_FormatYUV420PackedSemiPlanar = 39;
        public static final int COLOR_FormatYUV420Planar = 19;
        public static final int COLOR_FormatYUV420SemiPlanar = 21;
        public static final int COLOR_FormatYUV422Flexible = 2135042184;
        public static final int COLOR_FormatYUV422PackedPlanar = 23;
        public static final int COLOR_FormatYUV422PackedSemiPlanar = 40;
        public static final int COLOR_FormatYUV422Planar = 22;
        public static final int COLOR_FormatYUV422SemiPlanar = 24;
        public static final int COLOR_FormatYUV444Flexible = 2135181448;
        public static final int COLOR_FormatYUV444Interleaved = 29;
        public static final int COLOR_FormatYUVP010 = 54;
        public static final int COLOR_QCOM_FormatYUV420SemiPlanar = 2141391872;
        public static final int COLOR_TI_FormatYUV420PackedSemiPlanar = 2130706688;
        public static final String FEATURE_LowLatency = "low-latency";
        private static final String TAG = "CodecCapabilities";
        public int[] colorFormats;
        private AudioCapabilities mAudioCaps;
        private MediaFormat mCapabilitiesInfo;
        private MediaFormat mDefaultFormat;
        private EncoderCapabilities mEncoderCaps;
        int mError;
        private int mFlagsRequired;
        private int mFlagsSupported;
        private int mFlagsVerified;
        private int mMaxSupportedInstances;
        private String mMime;
        private VideoCapabilities mVideoCaps;
        public CodecProfileLevel[] profileLevels;
        public static final String FEATURE_AdaptivePlayback = "adaptive-playback";
        public static final String FEATURE_SecurePlayback = "secure-playback";
        public static final String FEATURE_TunneledPlayback = "tunneled-playback";
        public static final String FEATURE_PartialFrame = "partial-frame";
        public static final String FEATURE_FrameParsing = "frame-parsing";
        public static final String FEATURE_MultipleFrames = "multiple-frames";
        public static final String FEATURE_DynamicTimestamp = "dynamic-timestamp";
        private static final String FEATURE_SpecialCodec = "special-codec";
        private static final Feature[] decoderFeatures = {new Feature(FEATURE_AdaptivePlayback, 1, true), new Feature(FEATURE_SecurePlayback, 2, false), new Feature(FEATURE_TunneledPlayback, 4, false), new Feature(FEATURE_PartialFrame, 8, false), new Feature(FEATURE_FrameParsing, 16, false), new Feature(FEATURE_MultipleFrames, 32, false), new Feature(FEATURE_DynamicTimestamp, 64, false), new Feature("low-latency", 128, true), new Feature(FEATURE_SpecialCodec, 1073741824, false, true)};
        public static final String FEATURE_IntraRefresh = "intra-refresh";
        public static final String FEATURE_QpBounds = "qp-bounds";
        public static final String FEATURE_EncodingStatistics = "encoding-statistics";
        public static final String FEATURE_HdrEditing = "hdr-editing";
        private static final Feature[] encoderFeatures = {new Feature(FEATURE_IntraRefresh, 1, false), new Feature(FEATURE_MultipleFrames, 2, false), new Feature(FEATURE_DynamicTimestamp, 4, false), new Feature(FEATURE_QpBounds, 8, false), new Feature(FEATURE_EncodingStatistics, 16, false), new Feature(FEATURE_HdrEditing, 32, false), new Feature(FEATURE_SpecialCodec, 1073741824, false, true)};

        public CodecCapabilities() {
        }

        public final boolean isFeatureSupported(String name) {
            return checkFeature(name, this.mFlagsSupported);
        }

        public final boolean isFeatureRequired(String name) {
            return checkFeature(name, this.mFlagsRequired);
        }

        public String[] validFeatures() {
            Feature[] features = getValidFeatures();
            String[] res = new String[features.length];
            for (int i = 0; i < res.length; i++) {
                if (!features[i].mInternal) {
                    res[i] = features[i].mName;
                }
            }
            return res;
        }

        private Feature[] getValidFeatures() {
            if (!isEncoder()) {
                return decoderFeatures;
            }
            return encoderFeatures;
        }

        private boolean checkFeature(String name, int flags) {
            Feature[] validFeatures;
            for (Feature feat : getValidFeatures()) {
                if (feat.mName.equals(name)) {
                    return (feat.mValue & flags) != 0;
                }
            }
            return false;
        }

        public boolean isRegular() {
            Feature[] validFeatures;
            for (Feature feat : getValidFeatures()) {
                if (!feat.mDefault && isFeatureRequired(feat.mName)) {
                    return false;
                }
            }
            return true;
        }

        public final boolean isFormatSupported(MediaFormat format) {
            Feature[] validFeatures;
            CodecProfileLevel[] codecProfileLevelArr;
            Integer yesNo;
            Map<String, Object> map = format.getMap();
            String mime = (String) map.get(MediaFormat.KEY_MIME);
            if (mime != null && !this.mMime.equalsIgnoreCase(mime)) {
                return false;
            }
            for (Feature feat : getValidFeatures()) {
                if (!feat.mInternal && (yesNo = (Integer) map.get(MediaFormat.KEY_FEATURE_ + feat.mName)) != null && ((yesNo.intValue() == 1 && !isFeatureSupported(feat.mName)) || (yesNo.intValue() == 0 && isFeatureRequired(feat.mName)))) {
                    return false;
                }
            }
            Integer profile = (Integer) map.get(MediaFormat.KEY_PROFILE);
            Integer level = (Integer) map.get("level");
            if (profile != null) {
                if (!supportsProfileLevel(profile.intValue(), level)) {
                    return false;
                }
                int maxLevel = 0;
                for (CodecProfileLevel pl : this.profileLevels) {
                    if (pl.profile == profile.intValue() && pl.level > maxLevel && (!this.mMime.equalsIgnoreCase("video/3gpp") || pl.level != 16 || maxLevel == 1)) {
                        maxLevel = pl.level;
                    }
                }
                CodecCapabilities levelCaps = createFromProfileLevel(this.mMime, profile.intValue(), maxLevel);
                Map<String, Object> mapWithoutProfile = new HashMap<>(map);
                mapWithoutProfile.remove(MediaFormat.KEY_PROFILE);
                MediaFormat formatWithoutProfile = new MediaFormat(mapWithoutProfile);
                if (levelCaps != null && !levelCaps.isFormatSupported(formatWithoutProfile)) {
                    return false;
                }
            }
            AudioCapabilities audioCapabilities = this.mAudioCaps;
            if (audioCapabilities != null && !audioCapabilities.supportsFormat(format)) {
                return false;
            }
            VideoCapabilities videoCapabilities = this.mVideoCaps;
            if (videoCapabilities != null && !videoCapabilities.supportsFormat(format)) {
                return false;
            }
            EncoderCapabilities encoderCapabilities = this.mEncoderCaps;
            return encoderCapabilities == null || encoderCapabilities.supportsFormat(format);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static boolean supportsBitrate(Range<Integer> bitrateRange, MediaFormat format) {
            Map<String, Object> map = format.getMap();
            Integer maxBitrate = (Integer) map.get(MediaFormat.KEY_MAX_BIT_RATE);
            Integer bitrate = (Integer) map.get(MediaFormat.KEY_BIT_RATE);
            if (bitrate == null) {
                bitrate = maxBitrate;
            } else if (maxBitrate != null) {
                bitrate = Integer.valueOf(Math.max(bitrate.intValue(), maxBitrate.intValue()));
            }
            if (bitrate != null && bitrate.intValue() > 0) {
                return bitrateRange.contains((Range<Integer>) bitrate);
            }
            return true;
        }

        private boolean supportsProfileLevel(int profile, Integer level) {
            CodecProfileLevel[] codecProfileLevelArr;
            for (CodecProfileLevel pl : this.profileLevels) {
                if (pl.profile == profile) {
                    if (level == null || this.mMime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_AAC) || this.mMime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_DTS) || this.mMime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_DTS_HD) || this.mMime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_DTS_UHD)) {
                        return true;
                    }
                    if ((!this.mMime.equalsIgnoreCase("video/3gpp") || pl.level == level.intValue() || pl.level != 16 || level.intValue() <= 1) && (!this.mMime.equalsIgnoreCase(MediaFormat.MIMETYPE_VIDEO_MPEG4) || pl.level == level.intValue() || pl.level != 4 || level.intValue() <= 1)) {
                        if (this.mMime.equalsIgnoreCase(MediaFormat.MIMETYPE_VIDEO_HEVC)) {
                            boolean supportsHighTier = (pl.level & 44739242) != 0;
                            boolean checkingHighTier = (44739242 & level.intValue()) != 0;
                            if (checkingHighTier && !supportsHighTier) {
                            }
                        }
                        if (pl.level >= level.intValue()) {
                            return createFromProfileLevel(this.mMime, profile, pl.level) == null || createFromProfileLevel(this.mMime, profile, level.intValue()) != null;
                        }
                    }
                }
            }
            return false;
        }

        public MediaFormat getDefaultFormat() {
            return this.mDefaultFormat;
        }

        public String getMimeType() {
            return this.mMime;
        }

        public int getMaxSupportedInstances() {
            return this.mMaxSupportedInstances;
        }

        private boolean isAudio() {
            return this.mAudioCaps != null;
        }

        public AudioCapabilities getAudioCapabilities() {
            return this.mAudioCaps;
        }

        private boolean isEncoder() {
            return this.mEncoderCaps != null;
        }

        public EncoderCapabilities getEncoderCapabilities() {
            return this.mEncoderCaps;
        }

        private boolean isVideo() {
            return this.mVideoCaps != null;
        }

        public VideoCapabilities getVideoCapabilities() {
            return this.mVideoCaps;
        }

        public CodecCapabilities dup() {
            CodecCapabilities caps = new CodecCapabilities();
            CodecProfileLevel[] codecProfileLevelArr = this.profileLevels;
            caps.profileLevels = (CodecProfileLevel[]) Arrays.copyOf(codecProfileLevelArr, codecProfileLevelArr.length);
            int[] iArr = this.colorFormats;
            caps.colorFormats = Arrays.copyOf(iArr, iArr.length);
            caps.mMime = this.mMime;
            caps.mMaxSupportedInstances = this.mMaxSupportedInstances;
            caps.mFlagsRequired = this.mFlagsRequired;
            caps.mFlagsSupported = this.mFlagsSupported;
            caps.mFlagsVerified = this.mFlagsVerified;
            caps.mAudioCaps = this.mAudioCaps;
            caps.mVideoCaps = this.mVideoCaps;
            caps.mEncoderCaps = this.mEncoderCaps;
            caps.mDefaultFormat = this.mDefaultFormat;
            caps.mCapabilitiesInfo = this.mCapabilitiesInfo;
            return caps;
        }

        public static CodecCapabilities createFromProfileLevel(String mime, int profile, int level) {
            CodecProfileLevel pl = new CodecProfileLevel();
            pl.profile = profile;
            pl.level = level;
            MediaFormat defaultFormat = new MediaFormat();
            defaultFormat.setString(MediaFormat.KEY_MIME, mime);
            CodecCapabilities ret = new CodecCapabilities(new CodecProfileLevel[]{pl}, new int[0], true, defaultFormat, new MediaFormat());
            if (ret.mError != 0) {
                return null;
            }
            return ret;
        }

        CodecCapabilities(CodecProfileLevel[] profLevs, int[] colFmts, boolean encoder, Map<String, Object> defaultFormatMap, Map<String, Object> capabilitiesMap) {
            this(profLevs, colFmts, encoder, new MediaFormat(defaultFormatMap), new MediaFormat(capabilitiesMap));
        }

        CodecCapabilities(CodecProfileLevel[] profLevs, int[] colFmts, boolean encoder, MediaFormat defaultFormat, MediaFormat info) {
            boolean z;
            Map<String, Object> map = info.getMap();
            this.colorFormats = colFmts;
            int i = 0;
            this.mFlagsVerified = 0;
            this.mDefaultFormat = defaultFormat;
            this.mCapabilitiesInfo = info;
            String string = defaultFormat.getString(MediaFormat.KEY_MIME);
            this.mMime = string;
            CodecProfileLevel[] profLevs2 = profLevs;
            boolean z2 = true;
            if (profLevs2.length == 0 && string.equalsIgnoreCase(MediaFormat.MIMETYPE_VIDEO_VP9)) {
                CodecProfileLevel profLev = new CodecProfileLevel();
                profLev.profile = 1;
                profLev.level = VideoCapabilities.equivalentVP9Level(info);
                profLevs2 = new CodecProfileLevel[]{profLev};
            }
            this.profileLevels = profLevs2;
            if (this.mMime.toLowerCase().startsWith("audio/")) {
                AudioCapabilities create = AudioCapabilities.create(info, this);
                this.mAudioCaps = create;
                create.getDefaultFormat(this.mDefaultFormat);
            } else if (this.mMime.toLowerCase().startsWith("video/") || this.mMime.equalsIgnoreCase(MediaFormat.MIMETYPE_IMAGE_ANDROID_HEIC)) {
                this.mVideoCaps = VideoCapabilities.create(info, this);
            }
            if (encoder) {
                EncoderCapabilities create2 = EncoderCapabilities.create(info, this);
                this.mEncoderCaps = create2;
                create2.getDefaultFormat(this.mDefaultFormat);
            }
            Map<String, Object> global = MediaCodecList.getGlobalSettings();
            this.mMaxSupportedInstances = Utils.parseIntSafely(global.get("max-concurrent-instances"), 32);
            int maxInstances = Utils.parseIntSafely(map.get("max-concurrent-instances"), this.mMaxSupportedInstances);
            this.mMaxSupportedInstances = ((Integer) Range.create(1, 256).clamp(Integer.valueOf(maxInstances))).intValue();
            Feature[] validFeatures = getValidFeatures();
            int length = validFeatures.length;
            while (i < length) {
                Feature feat = validFeatures[i];
                String key = MediaFormat.KEY_FEATURE_ + feat.mName;
                Integer yesNo = (Integer) map.get(key);
                if (yesNo == null) {
                    z = z2;
                } else {
                    if (yesNo.intValue() > 0) {
                        this.mFlagsRequired = feat.mValue | this.mFlagsRequired;
                    }
                    this.mFlagsSupported |= feat.mValue;
                    if (feat.mInternal) {
                        z = true;
                    } else {
                        z = true;
                        this.mDefaultFormat.setInteger(key, 1);
                    }
                }
                i++;
                z2 = z;
            }
        }
    }

    /* loaded from: classes2.dex */
    public static final class AudioCapabilities {
        private static final int MAX_INPUT_CHANNEL_COUNT = 30;
        private static final String TAG = "AudioCapabilities";
        private Range<Integer> mBitrateRange;
        private Range<Integer>[] mInputChannelRanges;
        private CodecCapabilities mParent;
        private Range<Integer>[] mSampleRateRanges;
        private int[] mSampleRates;

        public Range<Integer> getBitrateRange() {
            return this.mBitrateRange;
        }

        public int[] getSupportedSampleRates() {
            int[] iArr = this.mSampleRates;
            if (iArr != null) {
                return Arrays.copyOf(iArr, iArr.length);
            }
            return null;
        }

        public Range<Integer>[] getSupportedSampleRateRanges() {
            Range<Integer>[] rangeArr = this.mSampleRateRanges;
            return (Range[]) Arrays.copyOf(rangeArr, rangeArr.length);
        }

        public int getMaxInputChannelCount() {
            int overall_max = 0;
            for (int i = this.mInputChannelRanges.length - 1; i >= 0; i--) {
                int lmax = this.mInputChannelRanges[i].getUpper().intValue();
                if (lmax > overall_max) {
                    overall_max = lmax;
                }
            }
            return overall_max;
        }

        public int getMinInputChannelCount() {
            int overall_min = 30;
            for (int i = this.mInputChannelRanges.length - 1; i >= 0; i--) {
                int lmin = this.mInputChannelRanges[i].getLower().intValue();
                if (lmin < overall_min) {
                    overall_min = lmin;
                }
            }
            return overall_min;
        }

        public Range<Integer>[] getInputChannelCountRanges() {
            Range<Integer>[] rangeArr = this.mInputChannelRanges;
            return (Range[]) Arrays.copyOf(rangeArr, rangeArr.length);
        }

        private AudioCapabilities() {
        }

        public static AudioCapabilities create(MediaFormat info, CodecCapabilities parent) {
            AudioCapabilities caps = new AudioCapabilities();
            caps.init(info, parent);
            return caps;
        }

        private void init(MediaFormat info, CodecCapabilities parent) {
            this.mParent = parent;
            initWithPlatformLimits();
            applyLevelLimits();
            parseFromInfo(info);
        }

        private void initWithPlatformLimits() {
            this.mBitrateRange = Range.create(0, Integer.MAX_VALUE);
            this.mInputChannelRanges = new Range[]{Range.create(1, 30)};
            int minSampleRate = SystemProperties.getInt("ro.mediacodec.min_sample_rate", 7350);
            int maxSampleRate = SystemProperties.getInt("ro.mediacodec.max_sample_rate", 192000);
            this.mSampleRateRanges = new Range[]{Range.create(Integer.valueOf(minSampleRate), Integer.valueOf(maxSampleRate))};
            this.mSampleRates = null;
        }

        private boolean supports(Integer sampleRate, Integer inputChannels) {
            if (inputChannels != null) {
                int ix = Utils.binarySearchDistinctRanges(this.mInputChannelRanges, inputChannels);
                if (ix < 0) {
                    return false;
                }
            }
            if (sampleRate != null) {
                int ix2 = Utils.binarySearchDistinctRanges(this.mSampleRateRanges, sampleRate);
                if (ix2 < 0) {
                    return false;
                }
                return true;
            }
            return true;
        }

        public boolean isSampleRateSupported(int sampleRate) {
            return supports(Integer.valueOf(sampleRate), null);
        }

        private void limitSampleRates(int[] rates) {
            Arrays.sort(rates);
            ArrayList<Range<Integer>> ranges = new ArrayList<>();
            for (int rate : rates) {
                if (supports(Integer.valueOf(rate), null)) {
                    ranges.add(Range.create(Integer.valueOf(rate), Integer.valueOf(rate)));
                }
            }
            this.mSampleRateRanges = (Range[]) ranges.toArray(new Range[ranges.size()]);
            createDiscreteSampleRates();
        }

        private void createDiscreteSampleRates() {
            this.mSampleRates = new int[this.mSampleRateRanges.length];
            int i = 0;
            while (true) {
                Range<Integer>[] rangeArr = this.mSampleRateRanges;
                if (i < rangeArr.length) {
                    this.mSampleRates[i] = rangeArr[i].getLower().intValue();
                    i++;
                } else {
                    return;
                }
            }
        }

        private void limitSampleRates(Range<Integer>[] rateRanges) {
            Utils.sortDistinctRanges(rateRanges);
            Range<Integer>[] intersectSortedDistinctRanges = Utils.intersectSortedDistinctRanges(this.mSampleRateRanges, rateRanges);
            this.mSampleRateRanges = intersectSortedDistinctRanges;
            for (Range<Integer> range : intersectSortedDistinctRanges) {
                if (!range.getLower().equals(range.getUpper())) {
                    this.mSampleRates = null;
                    return;
                }
            }
            createDiscreteSampleRates();
        }

        private void applyLevelLimits() {
            Range<Integer> sampleRateRange;
            Range<Integer> sampleRateRange2;
            int[] sampleRates = null;
            Range<Integer> sampleRateRange3 = null;
            Range<Integer> bitRates = null;
            int maxChannels = 30;
            CodecProfileLevel[] profileLevels = this.mParent.profileLevels;
            String mime = this.mParent.getMimeType();
            if (mime.equalsIgnoreCase("audio/mpeg")) {
                sampleRates = new int[]{8000, 11025, 12000, 16000, 22050, 24000, 32000, 44100, 48000};
                bitRates = Range.create(8000, 320000);
                maxChannels = 2;
            } else if (mime.equalsIgnoreCase("audio/3gpp")) {
                sampleRates = new int[]{8000};
                bitRates = Range.create(4750, 12200);
                maxChannels = 1;
            } else if (mime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_AMR_WB)) {
                sampleRates = new int[]{16000};
                bitRates = Range.create(6600, 23850);
                maxChannels = 1;
            } else if (mime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_AAC)) {
                sampleRates = new int[]{7350, 8000, 11025, 12000, 16000, 22050, 24000, 32000, 44100, 48000, 64000, 88200, 96000};
                bitRates = Range.create(8000, 510000);
                maxChannels = 48;
            } else if (mime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_VORBIS)) {
                bitRates = Range.create(32000, 500000);
                sampleRateRange3 = Range.create(8000, 192000);
                maxChannels = 255;
            } else {
                int i = 48000;
                if (mime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_OPUS)) {
                    bitRates = Range.create(6000, 510000);
                    sampleRates = new int[]{8000, 12000, 16000, 24000, 48000};
                    maxChannels = 255;
                } else if (!mime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_RAW)) {
                    if (!mime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_FLAC)) {
                        if (mime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_G711_ALAW)) {
                            sampleRateRange = null;
                        } else if (mime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_G711_MLAW)) {
                            sampleRateRange = null;
                        } else if (mime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_MSGSM)) {
                            sampleRates = new int[]{8000};
                            bitRates = Range.create(13000, 13000);
                            maxChannels = 1;
                        } else if (mime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_AC3)) {
                            maxChannels = 6;
                        } else if (mime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_EAC3)) {
                            maxChannels = 16;
                        } else if (mime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_EAC3_JOC)) {
                            sampleRates = new int[]{48000};
                            bitRates = Range.create(32000, 6144000);
                            maxChannels = 16;
                        } else if (mime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_AC4)) {
                            sampleRates = new int[]{44100, 48000, 96000, 192000};
                            bitRates = Range.create(16000, 2688000);
                            maxChannels = 24;
                        } else if (mime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_DTS)) {
                            sampleRates = new int[]{44100, 48000};
                            bitRates = Range.create(96000, 1524000);
                            maxChannels = 6;
                        } else if (mime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_DTS_HD)) {
                            int length = profileLevels.length;
                            int i2 = 0;
                            while (i2 < length) {
                                CodecProfileLevel profileLevel = profileLevels[i2];
                                switch (profileLevel.profile) {
                                    case 1:
                                    case 4:
                                        sampleRateRange2 = sampleRateRange3;
                                        sampleRates = new int[]{44100, 48000, 88200, 96000, 176400, 192000};
                                        bitRates = Range.create(96000, 24500000);
                                        break;
                                    case 2:
                                        sampleRateRange2 = sampleRateRange3;
                                        bitRates = Range.create(32000, 768000);
                                        sampleRates = new int[]{22050, 24000, 44100, 48000};
                                        break;
                                    case 3:
                                    default:
                                        sampleRateRange2 = sampleRateRange3;
                                        Log.m104w(TAG, "Unrecognized profile " + profileLevel.profile + " for " + mime);
                                        this.mParent.mError |= 1;
                                        sampleRates = new int[]{44100, 48000, 88200, 96000, 176400, 192000};
                                        bitRates = Range.create(96000, 24500000);
                                        break;
                                }
                                i2++;
                                sampleRateRange3 = sampleRateRange2;
                            }
                            maxChannels = 8;
                        } else if (mime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_DTS_UHD)) {
                            int length2 = profileLevels.length;
                            int i3 = 0;
                            while (i3 < length2) {
                                CodecProfileLevel profileLevel2 = profileLevels[i3];
                                switch (profileLevel2.profile) {
                                    case 1:
                                        sampleRates = new int[]{44100, 48000, 88200, 96000, 176400, 192000};
                                        bitRates = Range.create(96000, 24500000);
                                        maxChannels = 32;
                                        break;
                                    case 2:
                                        sampleRates = new int[]{i};
                                        bitRates = Range.create(96000, 768000);
                                        maxChannels = 10;
                                        break;
                                    default:
                                        Log.m104w(TAG, "Unrecognized profile " + profileLevel2.profile + " for " + mime);
                                        this.mParent.mError |= 1;
                                        sampleRates = new int[]{44100, 48000, 88200, 96000, 176400, 192000};
                                        bitRates = Range.create(96000, 24500000);
                                        maxChannels = 32;
                                        break;
                                }
                                i3++;
                                i = 48000;
                            }
                            sampleRateRange3 = null;
                        } else {
                            Log.m104w(TAG, "Unsupported mime " + mime);
                            this.mParent.mError |= 2;
                            sampleRateRange3 = null;
                        }
                        sampleRates = new int[]{8000};
                        bitRates = Range.create(64000, 64000);
                        sampleRateRange3 = sampleRateRange;
                    } else {
                        sampleRateRange3 = Range.create(1, 655350);
                        maxChannels = 255;
                    }
                } else {
                    sampleRateRange3 = Range.create(1, 192000);
                    bitRates = Range.create(1, 10000000);
                    maxChannels = AudioSystem.OUT_CHANNEL_COUNT_MAX;
                }
            }
            if (sampleRates != null) {
                limitSampleRates(sampleRates);
            } else if (sampleRateRange3 != null) {
                limitSampleRates(new Range[]{sampleRateRange3});
            }
            Range<Integer> channelRange = Range.create(1, Integer.valueOf(maxChannels));
            applyLimits(new Range[]{channelRange}, bitRates);
        }

        private void applyLimits(Range<Integer>[] inputChannels, Range<Integer> bitRates) {
            Range<Integer>[] myInputChannels = new Range[inputChannels.length];
            for (int i = 0; i < inputChannels.length; i++) {
                int lower = inputChannels[i].clamp(1).intValue();
                int upper = inputChannels[i].clamp(30).intValue();
                myInputChannels[i] = Range.create(Integer.valueOf(lower), Integer.valueOf(upper));
            }
            Utils.sortDistinctRanges(myInputChannels);
            Range<Integer>[] joinedChannelList = Utils.intersectSortedDistinctRanges(myInputChannels, this.mInputChannelRanges);
            this.mInputChannelRanges = joinedChannelList;
            if (bitRates != null) {
                this.mBitrateRange = this.mBitrateRange.intersect(bitRates);
            }
        }

        private void parseFromInfo(MediaFormat info) {
            Range<Integer>[] channels = {Range.create(1, 30)};
            Range<Integer> bitRates = MediaCodecInfo.POSITIVE_INTEGERS;
            if (info.containsKey("sample-rate-ranges")) {
                String[] rateStrings = info.getString("sample-rate-ranges").split(",");
                Range<Integer>[] rateRanges = new Range[rateStrings.length];
                for (int i = 0; i < rateStrings.length; i++) {
                    rateRanges[i] = Utils.parseIntRange(rateStrings[i], null);
                }
                limitSampleRates(rateRanges);
            }
            if (info.containsKey("channel-ranges")) {
                String[] channelStrings = info.getString("channel-ranges").split(",");
                Range<Integer>[] channelRanges = new Range[channelStrings.length];
                for (int i2 = 0; i2 < channelStrings.length; i2++) {
                    channelRanges[i2] = Utils.parseIntRange(channelStrings[i2], null);
                }
                channels = channelRanges;
            } else if (info.containsKey("channel-range")) {
                Range<Integer> oneRange = Utils.parseIntRange(info.getString("channel-range"), null);
                channels = new Range[]{oneRange};
            } else if (info.containsKey("max-channel-count")) {
                int maxInputChannels = Utils.parseIntSafely(info.getString("max-channel-count"), 30);
                channels = maxInputChannels == 0 ? new Range[]{Range.create(0, 0)} : new Range[]{Range.create(1, Integer.valueOf(maxInputChannels))};
            } else if ((this.mParent.mError & 2) != 0) {
                channels = new Range[]{Range.create(0, 0)};
            }
            if (info.containsKey("bitrate-range")) {
                bitRates = bitRates.intersect(Utils.parseIntRange(info.getString("bitrate-range"), bitRates));
            }
            applyLimits(channels, bitRates);
        }

        public void getDefaultFormat(MediaFormat format) {
            if (this.mBitrateRange.getLower().equals(this.mBitrateRange.getUpper())) {
                format.setInteger(MediaFormat.KEY_BIT_RATE, this.mBitrateRange.getLower().intValue());
            }
            if (getMaxInputChannelCount() == 1) {
                format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
            }
            int[] iArr = this.mSampleRates;
            if (iArr != null && iArr.length == 1) {
                format.setInteger(MediaFormat.KEY_SAMPLE_RATE, iArr[0]);
            }
        }

        public boolean supportsFormat(MediaFormat format) {
            Map<String, Object> map = format.getMap();
            Integer sampleRate = (Integer) map.get(MediaFormat.KEY_SAMPLE_RATE);
            Integer channels = (Integer) map.get(MediaFormat.KEY_CHANNEL_COUNT);
            return supports(sampleRate, channels) && CodecCapabilities.supportsBitrate(this.mBitrateRange, format);
        }
    }

    /* loaded from: classes2.dex */
    public static final class VideoCapabilities {
        private static final String TAG = "VideoCapabilities";
        private boolean mAllowMbOverride;
        private Range<Rational> mAspectRatioRange;
        private Range<Integer> mBitrateRange;
        private Range<Rational> mBlockAspectRatioRange;
        private Range<Integer> mBlockCountRange;
        private int mBlockHeight;
        private int mBlockWidth;
        private Range<Long> mBlocksPerSecondRange;
        private Range<Integer> mFrameRateRange;
        private int mHeightAlignment;
        private Range<Integer> mHeightRange;
        private Range<Integer> mHorizontalBlockRange;
        private Map<Size, Range<Long>> mMeasuredFrameRates;
        private CodecCapabilities mParent;
        private List<PerformancePoint> mPerformancePoints;
        private int mSmallerDimensionUpperLimit;
        private Range<Integer> mVerticalBlockRange;
        private int mWidthAlignment;
        private Range<Integer> mWidthRange;

        public Range<Integer> getBitrateRange() {
            return this.mBitrateRange;
        }

        public Range<Integer> getSupportedWidths() {
            return this.mWidthRange;
        }

        public Range<Integer> getSupportedHeights() {
            return this.mHeightRange;
        }

        public int getWidthAlignment() {
            return this.mWidthAlignment;
        }

        public int getHeightAlignment() {
            return this.mHeightAlignment;
        }

        public int getSmallerDimensionUpperLimit() {
            return this.mSmallerDimensionUpperLimit;
        }

        public Range<Integer> getSupportedFrameRates() {
            return this.mFrameRateRange;
        }

        public Range<Integer> getSupportedWidthsFor(int height) {
            try {
                Range<Integer> range = this.mWidthRange;
                if (!this.mHeightRange.contains((Range<Integer>) Integer.valueOf(height)) || height % this.mHeightAlignment != 0) {
                    throw new IllegalArgumentException("unsupported height");
                }
                int heightInBlocks = Utils.divUp(height, this.mBlockHeight);
                int minWidthInBlocks = Math.max(Utils.divUp(this.mBlockCountRange.getLower().intValue(), heightInBlocks), (int) Math.ceil(this.mBlockAspectRatioRange.getLower().doubleValue() * heightInBlocks));
                int maxWidthInBlocks = Math.min(this.mBlockCountRange.getUpper().intValue() / heightInBlocks, (int) (this.mBlockAspectRatioRange.getUpper().doubleValue() * heightInBlocks));
                Range<Integer> range2 = range.intersect(Integer.valueOf(((minWidthInBlocks - 1) * this.mBlockWidth) + this.mWidthAlignment), Integer.valueOf(this.mBlockWidth * maxWidthInBlocks));
                if (height > this.mSmallerDimensionUpperLimit) {
                    range2 = range2.intersect(1, Integer.valueOf(this.mSmallerDimensionUpperLimit));
                }
                return range2.intersect(Integer.valueOf((int) Math.ceil(this.mAspectRatioRange.getLower().doubleValue() * height)), Integer.valueOf((int) (this.mAspectRatioRange.getUpper().doubleValue() * height)));
            } catch (IllegalArgumentException e) {
                Log.m106v(TAG, "could not get supported widths for " + height);
                throw new IllegalArgumentException("unsupported height");
            }
        }

        public Range<Integer> getSupportedHeightsFor(int width) {
            try {
                Range<Integer> range = this.mHeightRange;
                if (!this.mWidthRange.contains((Range<Integer>) Integer.valueOf(width)) || width % this.mWidthAlignment != 0) {
                    throw new IllegalArgumentException("unsupported width");
                }
                int widthInBlocks = Utils.divUp(width, this.mBlockWidth);
                int minHeightInBlocks = Math.max(Utils.divUp(this.mBlockCountRange.getLower().intValue(), widthInBlocks), (int) Math.ceil(widthInBlocks / this.mBlockAspectRatioRange.getUpper().doubleValue()));
                int maxHeightInBlocks = Math.min(this.mBlockCountRange.getUpper().intValue() / widthInBlocks, (int) (widthInBlocks / this.mBlockAspectRatioRange.getLower().doubleValue()));
                Range<Integer> range2 = range.intersect(Integer.valueOf(((minHeightInBlocks - 1) * this.mBlockHeight) + this.mHeightAlignment), Integer.valueOf(this.mBlockHeight * maxHeightInBlocks));
                if (width > this.mSmallerDimensionUpperLimit) {
                    range2 = range2.intersect(1, Integer.valueOf(this.mSmallerDimensionUpperLimit));
                }
                return range2.intersect(Integer.valueOf((int) Math.ceil(width / this.mAspectRatioRange.getUpper().doubleValue())), Integer.valueOf((int) (width / this.mAspectRatioRange.getLower().doubleValue())));
            } catch (IllegalArgumentException e) {
                Log.m106v(TAG, "could not get supported heights for " + width);
                throw new IllegalArgumentException("unsupported width");
            }
        }

        public Range<Double> getSupportedFrameRatesFor(int width, int height) {
            Range<Integer> range = this.mHeightRange;
            if (!supports(Integer.valueOf(width), Integer.valueOf(height), null)) {
                throw new IllegalArgumentException("unsupported size");
            }
            int blockCount = Utils.divUp(width, this.mBlockWidth) * Utils.divUp(height, this.mBlockHeight);
            return Range.create(Double.valueOf(Math.max(this.mBlocksPerSecondRange.getLower().longValue() / blockCount, this.mFrameRateRange.getLower().intValue())), Double.valueOf(Math.min(this.mBlocksPerSecondRange.getUpper().longValue() / blockCount, this.mFrameRateRange.getUpper().intValue())));
        }

        private int getBlockCount(int width, int height) {
            return Utils.divUp(width, this.mBlockWidth) * Utils.divUp(height, this.mBlockHeight);
        }

        private Size findClosestSize(int width, int height) {
            int targetBlockCount = getBlockCount(width, height);
            Size closestSize = null;
            int minDiff = Integer.MAX_VALUE;
            for (Size size : this.mMeasuredFrameRates.keySet()) {
                int diff = Math.abs(targetBlockCount - getBlockCount(size.getWidth(), size.getHeight()));
                if (diff < minDiff) {
                    minDiff = diff;
                    closestSize = size;
                }
            }
            return closestSize;
        }

        private Range<Double> estimateFrameRatesFor(int width, int height) {
            Size size = findClosestSize(width, height);
            Range<Long> range = this.mMeasuredFrameRates.get(size);
            Double ratio = Double.valueOf(getBlockCount(size.getWidth(), size.getHeight()) / Math.max(getBlockCount(width, height), 1));
            return Range.create(Double.valueOf(range.getLower().longValue() * ratio.doubleValue()), Double.valueOf(range.getUpper().longValue() * ratio.doubleValue()));
        }

        public Range<Double> getAchievableFrameRatesFor(int width, int height) {
            if (!supports(Integer.valueOf(width), Integer.valueOf(height), null)) {
                throw new IllegalArgumentException("unsupported size");
            }
            Map<Size, Range<Long>> map = this.mMeasuredFrameRates;
            if (map == null || map.size() <= 0) {
                Log.m104w(TAG, "Codec did not publish any measurement data.");
                return null;
            }
            return estimateFrameRatesFor(width, height);
        }

        /* loaded from: classes2.dex */
        public static final class PerformancePoint {
            private Size mBlockSize;
            private int mHeight;
            private int mMaxFrameRate;
            private long mMaxMacroBlockRate;
            private int mWidth;
            public static final PerformancePoint SD_24 = new PerformancePoint(MetricsProto.MetricsEvent.ACTION_PERMISSION_DENIED_RECEIVE_WAP_PUSH, 480, 24);
            public static final PerformancePoint SD_25 = new PerformancePoint(MetricsProto.MetricsEvent.ACTION_PERMISSION_DENIED_RECEIVE_WAP_PUSH, 576, 25);
            public static final PerformancePoint SD_30 = new PerformancePoint(MetricsProto.MetricsEvent.ACTION_PERMISSION_DENIED_RECEIVE_WAP_PUSH, 480, 30);
            public static final PerformancePoint SD_48 = new PerformancePoint(MetricsProto.MetricsEvent.ACTION_PERMISSION_DENIED_RECEIVE_WAP_PUSH, 480, 48);
            public static final PerformancePoint SD_50 = new PerformancePoint(MetricsProto.MetricsEvent.ACTION_PERMISSION_DENIED_RECEIVE_WAP_PUSH, 576, 50);
            public static final PerformancePoint SD_60 = new PerformancePoint(MetricsProto.MetricsEvent.ACTION_PERMISSION_DENIED_RECEIVE_WAP_PUSH, 480, 60);
            public static final PerformancePoint HD_24 = new PerformancePoint(1280, MetricsProto.MetricsEvent.ACTION_PERMISSION_DENIED_RECEIVE_WAP_PUSH, 24);
            public static final PerformancePoint HD_25 = new PerformancePoint(1280, MetricsProto.MetricsEvent.ACTION_PERMISSION_DENIED_RECEIVE_WAP_PUSH, 25);
            public static final PerformancePoint HD_30 = new PerformancePoint(1280, MetricsProto.MetricsEvent.ACTION_PERMISSION_DENIED_RECEIVE_WAP_PUSH, 30);
            public static final PerformancePoint HD_50 = new PerformancePoint(1280, MetricsProto.MetricsEvent.ACTION_PERMISSION_DENIED_RECEIVE_WAP_PUSH, 50);
            public static final PerformancePoint HD_60 = new PerformancePoint(1280, MetricsProto.MetricsEvent.ACTION_PERMISSION_DENIED_RECEIVE_WAP_PUSH, 60);
            public static final PerformancePoint HD_100 = new PerformancePoint(1280, MetricsProto.MetricsEvent.ACTION_PERMISSION_DENIED_RECEIVE_WAP_PUSH, 100);
            public static final PerformancePoint HD_120 = new PerformancePoint(1280, MetricsProto.MetricsEvent.ACTION_PERMISSION_DENIED_RECEIVE_WAP_PUSH, 120);
            public static final PerformancePoint HD_200 = new PerformancePoint(1280, MetricsProto.MetricsEvent.ACTION_PERMISSION_DENIED_RECEIVE_WAP_PUSH, 200);
            public static final PerformancePoint HD_240 = new PerformancePoint(1280, MetricsProto.MetricsEvent.ACTION_PERMISSION_DENIED_RECEIVE_WAP_PUSH, 240);
            public static final PerformancePoint FHD_24 = new PerformancePoint(SettingsEnums.SCREEN_RESOLUTION, BluetoothHciProtoEnums.CMD_CREATE_LOGICAL_LINK, 24);
            public static final PerformancePoint FHD_25 = new PerformancePoint(SettingsEnums.SCREEN_RESOLUTION, BluetoothHciProtoEnums.CMD_CREATE_LOGICAL_LINK, 25);
            public static final PerformancePoint FHD_30 = new PerformancePoint(SettingsEnums.SCREEN_RESOLUTION, BluetoothHciProtoEnums.CMD_CREATE_LOGICAL_LINK, 30);
            public static final PerformancePoint FHD_50 = new PerformancePoint(SettingsEnums.SCREEN_RESOLUTION, BluetoothHciProtoEnums.CMD_CREATE_LOGICAL_LINK, 50);
            public static final PerformancePoint FHD_60 = new PerformancePoint(SettingsEnums.SCREEN_RESOLUTION, BluetoothHciProtoEnums.CMD_CREATE_LOGICAL_LINK, 60);
            public static final PerformancePoint FHD_100 = new PerformancePoint(SettingsEnums.SCREEN_RESOLUTION, BluetoothHciProtoEnums.CMD_CREATE_LOGICAL_LINK, 100);
            public static final PerformancePoint FHD_120 = new PerformancePoint(SettingsEnums.SCREEN_RESOLUTION, BluetoothHciProtoEnums.CMD_CREATE_LOGICAL_LINK, 120);
            public static final PerformancePoint FHD_200 = new PerformancePoint(SettingsEnums.SCREEN_RESOLUTION, BluetoothHciProtoEnums.CMD_CREATE_LOGICAL_LINK, 200);
            public static final PerformancePoint FHD_240 = new PerformancePoint(SettingsEnums.SCREEN_RESOLUTION, BluetoothHciProtoEnums.CMD_CREATE_LOGICAL_LINK, 240);
            public static final PerformancePoint UHD_24 = new PerformancePoint(NetworkPolicyManager.MASK_RESTRICTED_MODE_NETWORKS, 2160, 24);
            public static final PerformancePoint UHD_25 = new PerformancePoint(NetworkPolicyManager.MASK_RESTRICTED_MODE_NETWORKS, 2160, 25);
            public static final PerformancePoint UHD_30 = new PerformancePoint(NetworkPolicyManager.MASK_RESTRICTED_MODE_NETWORKS, 2160, 30);
            public static final PerformancePoint UHD_50 = new PerformancePoint(NetworkPolicyManager.MASK_RESTRICTED_MODE_NETWORKS, 2160, 50);
            public static final PerformancePoint UHD_60 = new PerformancePoint(NetworkPolicyManager.MASK_RESTRICTED_MODE_NETWORKS, 2160, 60);
            public static final PerformancePoint UHD_100 = new PerformancePoint(NetworkPolicyManager.MASK_RESTRICTED_MODE_NETWORKS, 2160, 100);
            public static final PerformancePoint UHD_120 = new PerformancePoint(NetworkPolicyManager.MASK_RESTRICTED_MODE_NETWORKS, 2160, 120);
            public static final PerformancePoint UHD_200 = new PerformancePoint(NetworkPolicyManager.MASK_RESTRICTED_MODE_NETWORKS, 2160, 200);
            public static final PerformancePoint UHD_240 = new PerformancePoint(NetworkPolicyManager.MASK_RESTRICTED_MODE_NETWORKS, 2160, 240);

            public int getMaxMacroBlocks() {
                return saturateLongToInt(this.mWidth * this.mHeight);
            }

            public int getMaxFrameRate() {
                return this.mMaxFrameRate;
            }

            public long getMaxMacroBlockRate() {
                return this.mMaxMacroBlockRate;
            }

            public String toString() {
                int blockWidth = this.mBlockSize.getWidth() * 16;
                int blockHeight = this.mBlockSize.getHeight() * 16;
                int origRate = (int) Utils.divUp(this.mMaxMacroBlockRate, getMaxMacroBlocks());
                String info = (this.mWidth * 16) + "x" + (this.mHeight * 16) + "@" + origRate;
                if (origRate < this.mMaxFrameRate) {
                    info = info + ", max " + this.mMaxFrameRate + "fps";
                }
                if (blockWidth > 16 || blockHeight > 16) {
                    info = info + ", " + blockWidth + "x" + blockHeight + " blocks";
                }
                return "PerformancePoint(" + info + NavigationBarInflaterView.KEY_CODE_END;
            }

            public int hashCode() {
                return this.mMaxFrameRate;
            }

            public PerformancePoint(int width, int height, int frameRate, int maxFrameRate, Size blockSize) {
                MediaCodecInfo.checkPowerOfTwo(blockSize.getWidth(), "block width");
                MediaCodecInfo.checkPowerOfTwo(blockSize.getHeight(), "block height");
                this.mBlockSize = new Size(Utils.divUp(blockSize.getWidth(), 16), Utils.divUp(blockSize.getHeight(), 16));
                this.mWidth = (int) (Utils.divUp(Math.max(1L, width), Math.max(blockSize.getWidth(), 16)) * this.mBlockSize.getWidth());
                this.mHeight = (int) (Utils.divUp(Math.max(1L, height), Math.max(blockSize.getHeight(), 16)) * this.mBlockSize.getHeight());
                this.mMaxFrameRate = Math.max(1, Math.max(frameRate, maxFrameRate));
                this.mMaxMacroBlockRate = Math.max(1, frameRate) * getMaxMacroBlocks();
            }

            public PerformancePoint(PerformancePoint pp, Size newBlockSize) {
                this(pp.mWidth * 16, pp.mHeight * 16, (int) Utils.divUp(pp.mMaxMacroBlockRate, pp.getMaxMacroBlocks()), pp.mMaxFrameRate, new Size(Math.max(newBlockSize.getWidth(), pp.mBlockSize.getWidth() * 16), Math.max(newBlockSize.getHeight(), pp.mBlockSize.getHeight() * 16)));
            }

            public PerformancePoint(int width, int height, int frameRate) {
                this(width, height, frameRate, frameRate, new Size(16, 16));
            }

            private int saturateLongToInt(long value) {
                if (value < -2147483648L) {
                    return Integer.MIN_VALUE;
                }
                if (value > 2147483647L) {
                    return Integer.MAX_VALUE;
                }
                return (int) value;
            }

            private int align(int value, int alignment) {
                return Utils.divUp(value, alignment) * alignment;
            }

            private void checkPowerOfTwo2(int value, String description) {
                if (value == 0 || ((value - 1) & value) != 0) {
                    throw new IllegalArgumentException(description + " (" + value + ") must be a power of 2");
                }
            }

            public boolean covers(MediaFormat format) {
                PerformancePoint other = new PerformancePoint(format.getInteger("width", 0), format.getInteger("height", 0), Math.round((float) Math.ceil(format.getNumber(MediaFormat.KEY_FRAME_RATE, 0).doubleValue())));
                return covers(other);
            }

            public boolean covers(PerformancePoint other) {
                Size commonSize = getCommonBlockSize(other);
                PerformancePoint aligned = new PerformancePoint(this, commonSize);
                PerformancePoint otherAligned = new PerformancePoint(other, commonSize);
                return aligned.getMaxMacroBlocks() >= otherAligned.getMaxMacroBlocks() && aligned.mMaxFrameRate >= otherAligned.mMaxFrameRate && aligned.mMaxMacroBlockRate >= otherAligned.mMaxMacroBlockRate;
            }

            private Size getCommonBlockSize(PerformancePoint other) {
                return new Size(Math.max(this.mBlockSize.getWidth(), other.mBlockSize.getWidth()) * 16, Math.max(this.mBlockSize.getHeight(), other.mBlockSize.getHeight()) * 16);
            }

            public boolean equals(Object o) {
                if (o instanceof PerformancePoint) {
                    PerformancePoint other = (PerformancePoint) o;
                    Size commonSize = getCommonBlockSize(other);
                    PerformancePoint aligned = new PerformancePoint(this, commonSize);
                    PerformancePoint otherAligned = new PerformancePoint(other, commonSize);
                    return aligned.getMaxMacroBlocks() == otherAligned.getMaxMacroBlocks() && aligned.mMaxFrameRate == otherAligned.mMaxFrameRate && aligned.mMaxMacroBlockRate == otherAligned.mMaxMacroBlockRate;
                }
                return false;
            }
        }

        public List<PerformancePoint> getSupportedPerformancePoints() {
            return this.mPerformancePoints;
        }

        public boolean areSizeAndRateSupported(int width, int height, double frameRate) {
            return supports(Integer.valueOf(width), Integer.valueOf(height), Double.valueOf(frameRate));
        }

        public boolean isSizeSupported(int width, int height) {
            return supports(Integer.valueOf(width), Integer.valueOf(height), null);
        }

        private boolean supports(Integer width, Integer height, Number rate) {
            boolean ok = true;
            boolean z = true;
            if (1 != 0 && width != null) {
                ok = this.mWidthRange.contains((Range<Integer>) width) && width.intValue() % this.mWidthAlignment == 0;
            }
            if (ok && height != null) {
                ok = this.mHeightRange.contains((Range<Integer>) height) && height.intValue() % this.mHeightAlignment == 0;
            }
            if (ok && rate != null) {
                ok = this.mFrameRateRange.contains(Utils.intRangeFor(rate.doubleValue()));
            }
            if (ok && height != null && width != null) {
                boolean ok2 = Math.min(height.intValue(), width.intValue()) <= this.mSmallerDimensionUpperLimit;
                int widthInBlocks = Utils.divUp(width.intValue(), this.mBlockWidth);
                int heightInBlocks = Utils.divUp(height.intValue(), this.mBlockHeight);
                int blockCount = widthInBlocks * heightInBlocks;
                if (!ok2 || !this.mBlockCountRange.contains((Range<Integer>) Integer.valueOf(blockCount)) || !this.mBlockAspectRatioRange.contains((Range<Rational>) new Rational(widthInBlocks, heightInBlocks)) || !this.mAspectRatioRange.contains((Range<Rational>) new Rational(width.intValue(), height.intValue()))) {
                    z = false;
                }
                boolean ok3 = z;
                if (ok3 && rate != null) {
                    double blocksPerSec = blockCount * rate.doubleValue();
                    return this.mBlocksPerSecondRange.contains(Utils.longRangeFor(blocksPerSec));
                }
                return ok3;
            }
            return ok;
        }

        public boolean supportsFormat(MediaFormat format) {
            Map<String, Object> map = format.getMap();
            Integer width = (Integer) map.get("width");
            Integer height = (Integer) map.get("height");
            Number rate = (Number) map.get(MediaFormat.KEY_FRAME_RATE);
            return supports(width, height, rate) && CodecCapabilities.supportsBitrate(this.mBitrateRange, format);
        }

        private VideoCapabilities() {
        }

        public static VideoCapabilities create(MediaFormat info, CodecCapabilities parent) {
            VideoCapabilities caps = new VideoCapabilities();
            caps.init(info, parent);
            return caps;
        }

        private void init(MediaFormat info, CodecCapabilities parent) {
            this.mParent = parent;
            initWithPlatformLimits();
            applyLevelLimits();
            parseFromInfo(info);
            updateLimits();
        }

        public Size getBlockSize() {
            return new Size(this.mBlockWidth, this.mBlockHeight);
        }

        public Range<Integer> getBlockCountRange() {
            return this.mBlockCountRange;
        }

        public Range<Long> getBlocksPerSecondRange() {
            return this.mBlocksPerSecondRange;
        }

        public Range<Rational> getAspectRatioRange(boolean blocks) {
            return blocks ? this.mBlockAspectRatioRange : this.mAspectRatioRange;
        }

        private void initWithPlatformLimits() {
            this.mBitrateRange = MediaCodecInfo.BITRATE_RANGE;
            this.mWidthRange = MediaCodecInfo.getSizeRange();
            this.mHeightRange = MediaCodecInfo.getSizeRange();
            this.mFrameRateRange = MediaCodecInfo.FRAME_RATE_RANGE;
            this.mHorizontalBlockRange = MediaCodecInfo.getSizeRange();
            this.mVerticalBlockRange = MediaCodecInfo.getSizeRange();
            this.mBlockCountRange = MediaCodecInfo.POSITIVE_INTEGERS;
            this.mBlocksPerSecondRange = MediaCodecInfo.POSITIVE_LONGS;
            this.mBlockAspectRatioRange = MediaCodecInfo.POSITIVE_RATIONALS;
            this.mAspectRatioRange = MediaCodecInfo.POSITIVE_RATIONALS;
            this.mWidthAlignment = 2;
            this.mHeightAlignment = 2;
            this.mBlockWidth = 2;
            this.mBlockHeight = 2;
            this.mSmallerDimensionUpperLimit = ((Integer) MediaCodecInfo.getSizeRange().getUpper()).intValue();
        }

        private List<PerformancePoint> getPerformancePoints(Map<String, Object> map) {
            Vector<PerformancePoint> ret = new Vector<>();
            String prefix = "performance-point-";
            Set<String> keys = map.keySet();
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                String key = it.next();
                if (key.startsWith("performance-point-")) {
                    String subKey = key.substring("performance-point-".length());
                    if (subKey.equals("none") && ret.size() == 0) {
                        return Collections.unmodifiableList(ret);
                    }
                    String[] temp = key.split(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                    if (temp.length == 4) {
                        String sizeStr = temp[2];
                        Size size = Utils.parseSize(sizeStr, null);
                        if (size != null && size.getWidth() * size.getHeight() > 0) {
                            Range<Long> range = Utils.parseLongRange(map.get(key), null);
                            if (range != null && range.getLower().longValue() >= 0) {
                                if (range.getUpper().longValue() >= 0) {
                                    String prefix2 = prefix;
                                    Set<String> keys2 = keys;
                                    PerformancePoint given = new PerformancePoint(size.getWidth(), size.getHeight(), range.getLower().intValue(), range.getUpper().intValue(), new Size(this.mBlockWidth, this.mBlockHeight));
                                    Iterator<String> it2 = it;
                                    PerformancePoint rotated = new PerformancePoint(size.getHeight(), size.getWidth(), range.getLower().intValue(), range.getUpper().intValue(), new Size(this.mBlockWidth, this.mBlockHeight));
                                    ret.add(given);
                                    if (!given.covers(rotated)) {
                                        ret.add(rotated);
                                    }
                                    it = it2;
                                    prefix = prefix2;
                                    keys = keys2;
                                }
                            }
                        }
                    }
                }
            }
            if (ret.size() == 0) {
                return null;
            }
            ret.sort(new Comparator() { // from class: android.media.MediaCodecInfo$VideoCapabilities$$ExternalSyntheticLambda0
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return MediaCodecInfo.VideoCapabilities.lambda$getPerformancePoints$0((MediaCodecInfo.VideoCapabilities.PerformancePoint) obj, (MediaCodecInfo.VideoCapabilities.PerformancePoint) obj2);
                }
            });
            return Collections.unmodifiableList(ret);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ int lambda$getPerformancePoints$0(PerformancePoint a, PerformancePoint b) {
            int i = -1;
            if (a.getMaxMacroBlocks() != b.getMaxMacroBlocks()) {
                if (a.getMaxMacroBlocks() >= b.getMaxMacroBlocks()) {
                    i = 1;
                }
            } else if (a.getMaxMacroBlockRate() != b.getMaxMacroBlockRate()) {
                if (a.getMaxMacroBlockRate() >= b.getMaxMacroBlockRate()) {
                    i = 1;
                }
            } else if (a.getMaxFrameRate() == b.getMaxFrameRate()) {
                i = 0;
            } else if (a.getMaxFrameRate() >= b.getMaxFrameRate()) {
                i = 1;
            }
            return -i;
        }

        private Map<Size, Range<Long>> getMeasuredFrameRates(Map<String, Object> map) {
            Range<Long> range;
            Map<Size, Range<Long>> ret = new HashMap<>();
            Set<String> keys = map.keySet();
            for (String key : keys) {
                if (key.startsWith("measured-frame-rate-")) {
                    key.substring("measured-frame-rate-".length());
                    String[] temp = key.split(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                    if (temp.length == 5) {
                        String sizeStr = temp[3];
                        Size size = Utils.parseSize(sizeStr, null);
                        if (size != null && size.getWidth() * size.getHeight() > 0 && (range = Utils.parseLongRange(map.get(key), null)) != null && range.getLower().longValue() >= 0 && range.getUpper().longValue() >= 0) {
                            ret.put(size, range);
                        }
                    }
                }
            }
            return ret;
        }

        private static Pair<Range<Integer>, Range<Integer>> parseWidthHeightRanges(Object o) {
            Pair<Size, Size> range = Utils.parseSizeRange(o);
            if (range != null) {
                try {
                    return Pair.create(Range.create(Integer.valueOf(range.first.getWidth()), Integer.valueOf(range.second.getWidth())), Range.create(Integer.valueOf(range.first.getHeight()), Integer.valueOf(range.second.getHeight())));
                } catch (IllegalArgumentException e) {
                    Log.m104w(TAG, "could not parse size range '" + o + "'");
                    return null;
                }
            }
            return null;
        }

        public static int equivalentVP9Level(MediaFormat info) {
            int D;
            Map<String, Object> map = info.getMap();
            Size blockSize = Utils.parseSize(map.get("block-size"), new Size(8, 8));
            int BS = blockSize.getWidth() * blockSize.getHeight();
            Range<Integer> counts = Utils.parseIntRange(map.get("block-count-range"), null);
            int FS = counts == null ? 0 : counts.getUpper().intValue() * BS;
            Range<Long> blockRates = Utils.parseLongRange(map.get("blocks-per-second-range"), null);
            long SR = blockRates == null ? 0L : BS * blockRates.getUpper().longValue();
            Pair<Range<Integer>, Range<Integer>> dimensionRanges = parseWidthHeightRanges(map.get("size-range"));
            if (dimensionRanges == null) {
                D = 0;
            } else {
                D = Math.max(dimensionRanges.first.getUpper().intValue(), dimensionRanges.second.getUpper().intValue());
            }
            Range<Integer> bitRates = Utils.parseIntRange(map.get("bitrate-range"), null);
            int BR = bitRates != null ? Utils.divUp(bitRates.getUpper().intValue(), 1000) : 0;
            if (SR <= 829440 && FS <= 36864 && BR <= 200 && D <= 512) {
                return 1;
            }
            if (SR <= 2764800 && FS <= 73728 && BR <= 800 && D <= 768) {
                return 2;
            }
            if (SR <= 4608000 && FS <= 122880 && BR <= 1800 && D <= 960) {
                return 4;
            }
            if (SR > 9216000 || FS > 245760 || BR > 3600 || D > 1344) {
                if (SR <= 20736000 && FS <= 552960 && BR <= 7200 && D <= 2048) {
                    return 16;
                }
                if (SR <= 36864000 && FS <= 983040 && BR <= 12000 && D <= 2752) {
                    return 32;
                }
                if (SR <= 83558400 && FS <= 2228224 && BR <= 18000 && D <= 4160) {
                    return 64;
                }
                if (SR <= 160432128 && FS <= 2228224 && BR <= 30000 && D <= 4160) {
                    return 128;
                }
                if (SR <= 311951360 && FS <= 8912896 && BR <= 60000 && D <= 8384) {
                    return 256;
                }
                if (SR > 588251136 || FS > 8912896 || BR > 120000 || D > 8384) {
                    if (SR <= 1176502272 && FS <= 8912896 && BR <= 180000 && D <= 8384) {
                        return 1024;
                    }
                    if (SR <= 1176502272 && FS <= 35651584 && BR <= 180000 && D <= 16832) {
                        return 2048;
                    }
                    if (SR <= 2353004544L && FS <= 35651584 && BR <= 240000 && D <= 16832) {
                        return 4096;
                    }
                    return 8192;
                }
                return 512;
            }
            return 8;
        }

        /* JADX WARN: Removed duplicated region for block: B:17:0x012e  */
        /* JADX WARN: Removed duplicated region for block: B:24:0x016f  */
        /* JADX WARN: Removed duplicated region for block: B:27:0x01dd  */
        /* JADX WARN: Removed duplicated region for block: B:51:0x0282  */
        /* JADX WARN: Removed duplicated region for block: B:53:0x028e  */
        /* JADX WARN: Removed duplicated region for block: B:55:0x029a  */
        /* JADX WARN: Removed duplicated region for block: B:57:0x02a6  */
        /* JADX WARN: Removed duplicated region for block: B:59:0x02c5  */
        /* JADX WARN: Removed duplicated region for block: B:61:0x02e5  */
        /* JADX WARN: Removed duplicated region for block: B:63:0x0303  */
        /* JADX WARN: Removed duplicated region for block: B:65:0x030f  */
        /* JADX WARN: Removed duplicated region for block: B:67:0x031b  */
        /* JADX WARN: Removed duplicated region for block: B:73:0x00f9 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:75:0x013d A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        private void parseFromInfo(MediaFormat info) {
            Range<Integer> heights;
            Range<Integer> widths;
            Range<Integer> frameRates;
            Range<Integer> heights2;
            Range<Integer> bitRates;
            Range<Integer> bitRates2;
            Range<Integer> heights3;
            Range<Integer> widths2;
            Range<Integer> frameRates2;
            Range<Rational> ratios;
            Range<Rational> blockRatios;
            Map<String, Object> map = info.getMap();
            Size blockSize = new Size(this.mBlockWidth, this.mBlockHeight);
            Size alignment = new Size(this.mWidthAlignment, this.mHeightAlignment);
            Range<Integer> widths3 = null;
            Range<Integer> heights4 = null;
            Size blockSize2 = Utils.parseSize(map.get("block-size"), blockSize);
            Size alignment2 = Utils.parseSize(map.get("alignment"), alignment);
            Range<Integer> counts = Utils.parseIntRange(map.get("block-count-range"), null);
            Range<Long> blockRates = Utils.parseLongRange(map.get("blocks-per-second-range"), null);
            this.mMeasuredFrameRates = getMeasuredFrameRates(map);
            this.mPerformancePoints = getPerformancePoints(map);
            Pair<Range<Integer>, Range<Integer>> sizeRanges = parseWidthHeightRanges(map.get("size-range"));
            if (sizeRanges != null) {
                Range<Integer> widths4 = sizeRanges.first;
                widths3 = widths4;
                Range<Integer> heights5 = sizeRanges.second;
                heights4 = heights5;
            }
            if (map.containsKey("feature-can-swap-width-height")) {
                if (widths3 != null) {
                    this.mSmallerDimensionUpperLimit = Math.min(widths3.getUpper().intValue(), heights4.getUpper().intValue());
                    Range<Integer> widths5 = widths3.extend(heights4);
                    heights = widths5;
                    widths = widths5;
                    Range<Rational> ratios2 = Utils.parseRationalRange(map.get("block-aspect-ratio-range"), null);
                    Range<Rational> blockRatios2 = Utils.parseRationalRange(map.get("pixel-aspect-ratio-range"), null);
                    frameRates = Utils.parseIntRange(map.get("frame-rate-range"), null);
                    if (frameRates != null) {
                        heights2 = widths;
                    } else {
                        try {
                            frameRates = frameRates.intersect(MediaCodecInfo.FRAME_RATE_RANGE);
                            heights2 = widths;
                        } catch (IllegalArgumentException e) {
                            heights2 = widths;
                            Log.m104w(TAG, "frame rate range (" + frameRates + ") is out of limits: " + MediaCodecInfo.FRAME_RATE_RANGE);
                            frameRates = null;
                        }
                    }
                    bitRates = Utils.parseIntRange(map.get("bitrate-range"), null);
                    if (bitRates != null) {
                        bitRates2 = bitRates;
                    } else {
                        try {
                            bitRates2 = bitRates.intersect(MediaCodecInfo.BITRATE_RANGE);
                        } catch (IllegalArgumentException e2) {
                            Log.m104w(TAG, "bitrate range (" + bitRates + ") is out of limits: " + MediaCodecInfo.BITRATE_RANGE);
                            bitRates2 = null;
                        }
                    }
                    MediaCodecInfo.checkPowerOfTwo(blockSize2.getWidth(), "block-size width must be power of two");
                    MediaCodecInfo.checkPowerOfTwo(blockSize2.getHeight(), "block-size height must be power of two");
                    MediaCodecInfo.checkPowerOfTwo(alignment2.getWidth(), "alignment width must be power of two");
                    MediaCodecInfo.checkPowerOfTwo(alignment2.getHeight(), "alignment height must be power of two");
                    Range<Integer> heights6 = heights2;
                    Range<Integer> widths6 = heights;
                    Range<Integer> frameRates3 = frameRates;
                    applyMacroBlockLimits(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE, blockSize2.getWidth(), blockSize2.getHeight(), alignment2.getWidth(), alignment2.getHeight());
                    if ((this.mParent.mError & 2) != 0) {
                        if (!this.mAllowMbOverride) {
                            if (widths6 != null) {
                                this.mWidthRange = this.mWidthRange.intersect(widths6);
                            }
                            if (heights6 != null) {
                                this.mHeightRange = this.mHeightRange.intersect(heights6);
                            }
                            if (counts != null) {
                                this.mBlockCountRange = this.mBlockCountRange.intersect(Utils.factorRange(counts, ((this.mBlockWidth * this.mBlockHeight) / blockSize2.getWidth()) / blockSize2.getHeight()));
                            }
                            if (blockRates != null) {
                                this.mBlocksPerSecondRange = this.mBlocksPerSecondRange.intersect(Utils.factorRange(blockRates, ((this.mBlockWidth * this.mBlockHeight) / blockSize2.getWidth()) / blockSize2.getHeight()));
                            }
                            if (blockRatios2 != null) {
                                this.mBlockAspectRatioRange = this.mBlockAspectRatioRange.intersect(Utils.scaleRange(blockRatios2, this.mBlockHeight / blockSize2.getHeight(), this.mBlockWidth / blockSize2.getWidth()));
                            }
                            if (ratios2 != null) {
                                this.mAspectRatioRange = this.mAspectRatioRange.intersect(ratios2);
                            }
                            if (frameRates3 != null) {
                                this.mFrameRateRange = this.mFrameRateRange.intersect(frameRates3);
                            }
                            if (bitRates2 != null) {
                                this.mBitrateRange = this.mBitrateRange.intersect(bitRates2);
                            }
                            updateLimits();
                        }
                        heights3 = heights6;
                        widths2 = widths6;
                        frameRates2 = frameRates3;
                        ratios = ratios2;
                        blockRatios = blockRatios2;
                    } else {
                        heights3 = heights6;
                        widths2 = widths6;
                        frameRates2 = frameRates3;
                        ratios = ratios2;
                        blockRatios = blockRatios2;
                    }
                    if (widths2 != null) {
                        this.mWidthRange = MediaCodecInfo.getSizeRange().intersect(widths2);
                    }
                    if (heights3 != null) {
                        this.mHeightRange = MediaCodecInfo.getSizeRange().intersect(heights3);
                    }
                    if (counts != null) {
                        this.mBlockCountRange = MediaCodecInfo.POSITIVE_INTEGERS.intersect(Utils.factorRange(counts, ((this.mBlockWidth * this.mBlockHeight) / blockSize2.getWidth()) / blockSize2.getHeight()));
                    }
                    if (blockRates != null) {
                        this.mBlocksPerSecondRange = MediaCodecInfo.POSITIVE_LONGS.intersect(Utils.factorRange(blockRates, ((this.mBlockWidth * this.mBlockHeight) / blockSize2.getWidth()) / blockSize2.getHeight()));
                    }
                    if (blockRatios != null) {
                        this.mBlockAspectRatioRange = MediaCodecInfo.POSITIVE_RATIONALS.intersect(Utils.scaleRange(blockRatios, this.mBlockHeight / blockSize2.getHeight(), this.mBlockWidth / blockSize2.getWidth()));
                    }
                    if (ratios != null) {
                        this.mAspectRatioRange = MediaCodecInfo.POSITIVE_RATIONALS.intersect(ratios);
                    }
                    if (frameRates2 != null) {
                        this.mFrameRateRange = MediaCodecInfo.FRAME_RATE_RANGE.intersect(frameRates2);
                    }
                    if (bitRates2 != null) {
                        if ((this.mParent.mError & 2) != 0) {
                            this.mBitrateRange = MediaCodecInfo.BITRATE_RANGE.intersect(bitRates2);
                        } else {
                            this.mBitrateRange = this.mBitrateRange.intersect(bitRates2);
                        }
                    }
                    updateLimits();
                }
                Log.m104w(TAG, "feature can-swap-width-height is best used with size-range");
                this.mSmallerDimensionUpperLimit = Math.min(this.mWidthRange.getUpper().intValue(), this.mHeightRange.getUpper().intValue());
                Range<Integer> extend = this.mWidthRange.extend(this.mHeightRange);
                this.mHeightRange = extend;
                this.mWidthRange = extend;
            }
            Range<Integer> range = heights4;
            heights = widths3;
            widths = range;
            Range<Rational> ratios22 = Utils.parseRationalRange(map.get("block-aspect-ratio-range"), null);
            Range<Rational> blockRatios22 = Utils.parseRationalRange(map.get("pixel-aspect-ratio-range"), null);
            frameRates = Utils.parseIntRange(map.get("frame-rate-range"), null);
            if (frameRates != null) {
            }
            bitRates = Utils.parseIntRange(map.get("bitrate-range"), null);
            if (bitRates != null) {
            }
            MediaCodecInfo.checkPowerOfTwo(blockSize2.getWidth(), "block-size width must be power of two");
            MediaCodecInfo.checkPowerOfTwo(blockSize2.getHeight(), "block-size height must be power of two");
            MediaCodecInfo.checkPowerOfTwo(alignment2.getWidth(), "alignment width must be power of two");
            MediaCodecInfo.checkPowerOfTwo(alignment2.getHeight(), "alignment height must be power of two");
            Range<Integer> heights62 = heights2;
            Range<Integer> widths62 = heights;
            Range<Integer> frameRates32 = frameRates;
            applyMacroBlockLimits(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE, blockSize2.getWidth(), blockSize2.getHeight(), alignment2.getWidth(), alignment2.getHeight());
            if ((this.mParent.mError & 2) != 0) {
            }
            if (widths2 != null) {
            }
            if (heights3 != null) {
            }
            if (counts != null) {
            }
            if (blockRates != null) {
            }
            if (blockRatios != null) {
            }
            if (ratios != null) {
            }
            if (frameRates2 != null) {
            }
            if (bitRates2 != null) {
            }
            updateLimits();
        }

        private void applyBlockLimits(int blockWidth, int blockHeight, Range<Integer> counts, Range<Long> rates, Range<Rational> ratios) {
            MediaCodecInfo.checkPowerOfTwo(blockWidth, "blockWidth must be a power of two");
            MediaCodecInfo.checkPowerOfTwo(blockHeight, "blockHeight must be a power of two");
            int newBlockWidth = Math.max(blockWidth, this.mBlockWidth);
            int newBlockHeight = Math.max(blockHeight, this.mBlockHeight);
            int factor = ((newBlockWidth * newBlockHeight) / this.mBlockWidth) / this.mBlockHeight;
            if (factor != 1) {
                this.mBlockCountRange = Utils.factorRange(this.mBlockCountRange, factor);
                this.mBlocksPerSecondRange = Utils.factorRange(this.mBlocksPerSecondRange, factor);
                this.mBlockAspectRatioRange = Utils.scaleRange(this.mBlockAspectRatioRange, newBlockHeight / this.mBlockHeight, newBlockWidth / this.mBlockWidth);
                this.mHorizontalBlockRange = Utils.factorRange(this.mHorizontalBlockRange, newBlockWidth / this.mBlockWidth);
                this.mVerticalBlockRange = Utils.factorRange(this.mVerticalBlockRange, newBlockHeight / this.mBlockHeight);
            }
            int factor2 = ((newBlockWidth * newBlockHeight) / blockWidth) / blockHeight;
            if (factor2 != 1) {
                counts = Utils.factorRange(counts, factor2);
                rates = Utils.factorRange(rates, factor2);
                ratios = Utils.scaleRange(ratios, newBlockHeight / blockHeight, newBlockWidth / blockWidth);
            }
            this.mBlockCountRange = this.mBlockCountRange.intersect(counts);
            this.mBlocksPerSecondRange = this.mBlocksPerSecondRange.intersect(rates);
            this.mBlockAspectRatioRange = this.mBlockAspectRatioRange.intersect(ratios);
            this.mBlockWidth = newBlockWidth;
            this.mBlockHeight = newBlockHeight;
        }

        private void applyAlignment(int widthAlignment, int heightAlignment) {
            MediaCodecInfo.checkPowerOfTwo(widthAlignment, "widthAlignment must be a power of two");
            MediaCodecInfo.checkPowerOfTwo(heightAlignment, "heightAlignment must be a power of two");
            int i = this.mBlockWidth;
            if (widthAlignment > i || heightAlignment > this.mBlockHeight) {
                applyBlockLimits(Math.max(widthAlignment, i), Math.max(heightAlignment, this.mBlockHeight), MediaCodecInfo.POSITIVE_INTEGERS, MediaCodecInfo.POSITIVE_LONGS, MediaCodecInfo.POSITIVE_RATIONALS);
            }
            this.mWidthAlignment = Math.max(widthAlignment, this.mWidthAlignment);
            this.mHeightAlignment = Math.max(heightAlignment, this.mHeightAlignment);
            this.mWidthRange = Utils.alignRange(this.mWidthRange, this.mWidthAlignment);
            this.mHeightRange = Utils.alignRange(this.mHeightRange, this.mHeightAlignment);
        }

        private void updateLimits() {
            Range<Integer> intersect = this.mHorizontalBlockRange.intersect(Utils.factorRange(this.mWidthRange, this.mBlockWidth));
            this.mHorizontalBlockRange = intersect;
            this.mHorizontalBlockRange = intersect.intersect(Range.create(Integer.valueOf(this.mBlockCountRange.getLower().intValue() / this.mVerticalBlockRange.getUpper().intValue()), Integer.valueOf(this.mBlockCountRange.getUpper().intValue() / this.mVerticalBlockRange.getLower().intValue())));
            Range<Integer> intersect2 = this.mVerticalBlockRange.intersect(Utils.factorRange(this.mHeightRange, this.mBlockHeight));
            this.mVerticalBlockRange = intersect2;
            this.mVerticalBlockRange = intersect2.intersect(Range.create(Integer.valueOf(this.mBlockCountRange.getLower().intValue() / this.mHorizontalBlockRange.getUpper().intValue()), Integer.valueOf(this.mBlockCountRange.getUpper().intValue() / this.mHorizontalBlockRange.getLower().intValue())));
            this.mBlockCountRange = this.mBlockCountRange.intersect(Range.create(Integer.valueOf(this.mHorizontalBlockRange.getLower().intValue() * this.mVerticalBlockRange.getLower().intValue()), Integer.valueOf(this.mHorizontalBlockRange.getUpper().intValue() * this.mVerticalBlockRange.getUpper().intValue())));
            this.mBlockAspectRatioRange = this.mBlockAspectRatioRange.intersect(new Rational(this.mHorizontalBlockRange.getLower().intValue(), this.mVerticalBlockRange.getUpper().intValue()), new Rational(this.mHorizontalBlockRange.getUpper().intValue(), this.mVerticalBlockRange.getLower().intValue()));
            this.mWidthRange = this.mWidthRange.intersect(Integer.valueOf(((this.mHorizontalBlockRange.getLower().intValue() - 1) * this.mBlockWidth) + this.mWidthAlignment), Integer.valueOf(this.mHorizontalBlockRange.getUpper().intValue() * this.mBlockWidth));
            this.mHeightRange = this.mHeightRange.intersect(Integer.valueOf(((this.mVerticalBlockRange.getLower().intValue() - 1) * this.mBlockHeight) + this.mHeightAlignment), Integer.valueOf(this.mVerticalBlockRange.getUpper().intValue() * this.mBlockHeight));
            this.mAspectRatioRange = this.mAspectRatioRange.intersect(new Rational(this.mWidthRange.getLower().intValue(), this.mHeightRange.getUpper().intValue()), new Rational(this.mWidthRange.getUpper().intValue(), this.mHeightRange.getLower().intValue()));
            this.mSmallerDimensionUpperLimit = Math.min(this.mSmallerDimensionUpperLimit, Math.min(this.mWidthRange.getUpper().intValue(), this.mHeightRange.getUpper().intValue()));
            Range<Long> intersect3 = this.mBlocksPerSecondRange.intersect(Long.valueOf(this.mBlockCountRange.getLower().intValue() * this.mFrameRateRange.getLower().intValue()), Long.valueOf(this.mBlockCountRange.getUpper().intValue() * this.mFrameRateRange.getUpper().intValue()));
            this.mBlocksPerSecondRange = intersect3;
            this.mFrameRateRange = this.mFrameRateRange.intersect(Integer.valueOf((int) (intersect3.getLower().longValue() / this.mBlockCountRange.getUpper().intValue())), Integer.valueOf((int) (this.mBlocksPerSecondRange.getUpper().longValue() / this.mBlockCountRange.getLower().intValue())));
        }

        private void applyMacroBlockLimits(int maxHorizontalBlocks, int maxVerticalBlocks, int maxBlocks, long maxBlocksPerSecond, int blockWidth, int blockHeight, int widthAlignment, int heightAlignment) {
            applyMacroBlockLimits(1, 1, maxHorizontalBlocks, maxVerticalBlocks, maxBlocks, maxBlocksPerSecond, blockWidth, blockHeight, widthAlignment, heightAlignment);
        }

        private void applyMacroBlockLimits(int minHorizontalBlocks, int minVerticalBlocks, int maxHorizontalBlocks, int maxVerticalBlocks, int maxBlocks, long maxBlocksPerSecond, int blockWidth, int blockHeight, int widthAlignment, int heightAlignment) {
            applyAlignment(widthAlignment, heightAlignment);
            applyBlockLimits(blockWidth, blockHeight, Range.create(1, Integer.valueOf(maxBlocks)), Range.create(1L, Long.valueOf(maxBlocksPerSecond)), Range.create(new Rational(1, maxVerticalBlocks), new Rational(maxHorizontalBlocks, 1)));
            this.mHorizontalBlockRange = this.mHorizontalBlockRange.intersect(Integer.valueOf(Utils.divUp(minHorizontalBlocks, this.mBlockWidth / blockWidth)), Integer.valueOf(maxHorizontalBlocks / (this.mBlockWidth / blockWidth)));
            this.mVerticalBlockRange = this.mVerticalBlockRange.intersect(Integer.valueOf(Utils.divUp(minVerticalBlocks, this.mBlockHeight / blockHeight)), Integer.valueOf(maxVerticalBlocks / (this.mBlockHeight / blockHeight)));
        }

        private void applyLevelLimits() {
            Integer num;
            int errors;
            int maxBlocks;
            CodecProfileLevel[] profileLevels;
            int D;
            String str;
            int FS;
            int BR;
            int BR2;
            int errors2;
            long SR;
            String str2;
            int D2;
            double FR;
            String str3;
            int i;
            String str4;
            int FS2;
            CodecProfileLevel[] profileLevels2;
            long SR2;
            int D3;
            Integer num2;
            int FS3;
            String str5;
            int BR3;
            String str6;
            String str7;
            String str8;
            String str9;
            String str10;
            int i2;
            int MBPS;
            String str11;
            int MBPS2;
            CodecProfileLevel[] profileLevels3;
            String str12;
            int FR2;
            int minHeight;
            int FR3;
            int minHeight2;
            int minWidth;
            int maxRate;
            String str13;
            int i3;
            CodecProfileLevel[] profileLevels4;
            int MBPS3;
            int FS4;
            int BR4;
            int FR4;
            String str14;
            int H;
            int H2;
            String str15;
            int W;
            int i4;
            int MBPS4;
            int FS5;
            String str16;
            int BR5;
            int FR5;
            String str17;
            int W2;
            String str18;
            int H3;
            String str19;
            int MBPS5;
            int FS6;
            int BR6;
            CodecProfileLevel[] profileLevels5;
            int DPB;
            String str20;
            int BR7;
            CodecProfileLevel[] profileLevels6 = this.mParent.profileLevels;
            String mime = this.mParent.getMimeType();
            boolean equalsIgnoreCase = mime.equalsIgnoreCase(MediaFormat.MIMETYPE_VIDEO_AVC);
            String str21 = "Unsupported profile ";
            String str22 = "Unrecognized level ";
            String str23 = "Unrecognized profile ";
            String str24 = " for ";
            String str25 = TAG;
            Integer num3 = 1;
            if (equalsIgnoreCase) {
                int length = profileLevels6.length;
                errors = 4;
                int maxBlocks2 = 99;
                int maxBps = 64000;
                int maxDPBBlocks = 396;
                long maxBlocksPerSecond = 1485;
                int i5 = 0;
                while (i5 < length) {
                    int i6 = length;
                    CodecProfileLevel profileLevel = profileLevels6[i5];
                    boolean supported = true;
                    Integer num4 = num3;
                    switch (profileLevel.level) {
                        case 1:
                            str19 = str22;
                            MBPS5 = 1485;
                            FS6 = 99;
                            BR6 = 64;
                            profileLevels5 = profileLevels6;
                            DPB = 396;
                            break;
                        case 2:
                            str19 = str22;
                            MBPS5 = 1485;
                            FS6 = 99;
                            BR6 = 128;
                            profileLevels5 = profileLevels6;
                            DPB = 396;
                            break;
                        case 4:
                            str19 = str22;
                            MBPS5 = 3000;
                            FS6 = 396;
                            BR6 = 192;
                            profileLevels5 = profileLevels6;
                            DPB = 900;
                            break;
                        case 8:
                            str19 = str22;
                            MBPS5 = 6000;
                            FS6 = 396;
                            BR6 = 384;
                            profileLevels5 = profileLevels6;
                            DPB = 2376;
                            break;
                        case 16:
                            str19 = str22;
                            MBPS5 = 11880;
                            FS6 = 396;
                            BR6 = 768;
                            profileLevels5 = profileLevels6;
                            DPB = 2376;
                            break;
                        case 32:
                            str19 = str22;
                            MBPS5 = 11880;
                            FS6 = 396;
                            BR6 = 2000;
                            profileLevels5 = profileLevels6;
                            DPB = 2376;
                            break;
                        case 64:
                            str19 = str22;
                            MBPS5 = 19800;
                            FS6 = 792;
                            BR6 = 4000;
                            profileLevels5 = profileLevels6;
                            DPB = 4752;
                            break;
                        case 128:
                            str19 = str22;
                            MBPS5 = 20250;
                            FS6 = 1620;
                            BR6 = 4000;
                            profileLevels5 = profileLevels6;
                            DPB = 8100;
                            break;
                        case 256:
                            str19 = str22;
                            MBPS5 = 40500;
                            FS6 = 1620;
                            BR6 = 10000;
                            profileLevels5 = profileLevels6;
                            DPB = 8100;
                            break;
                        case 512:
                            str19 = str22;
                            MBPS5 = 108000;
                            FS6 = 3600;
                            BR6 = 14000;
                            profileLevels5 = profileLevels6;
                            DPB = 18000;
                            break;
                        case 1024:
                            str19 = str22;
                            MBPS5 = 216000;
                            FS6 = 5120;
                            BR6 = 20000;
                            profileLevels5 = profileLevels6;
                            DPB = 20480;
                            break;
                        case 2048:
                            str19 = str22;
                            MBPS5 = 245760;
                            FS6 = 8192;
                            BR6 = 20000;
                            profileLevels5 = profileLevels6;
                            DPB = 32768;
                            break;
                        case 4096:
                            str19 = str22;
                            MBPS5 = 245760;
                            FS6 = 8192;
                            BR6 = 50000;
                            profileLevels5 = profileLevels6;
                            DPB = 32768;
                            break;
                        case 8192:
                            str19 = str22;
                            MBPS5 = 522240;
                            FS6 = 8704;
                            BR6 = 50000;
                            profileLevels5 = profileLevels6;
                            DPB = 34816;
                            break;
                        case 16384:
                            str19 = str22;
                            MBPS5 = 589824;
                            FS6 = 22080;
                            BR6 = 135000;
                            profileLevels5 = profileLevels6;
                            DPB = 110400;
                            break;
                        case 32768:
                            str19 = str22;
                            MBPS5 = 983040;
                            FS6 = 36864;
                            BR6 = 240000;
                            profileLevels5 = profileLevels6;
                            DPB = 184320;
                            break;
                        case 65536:
                            str19 = str22;
                            MBPS5 = 2073600;
                            FS6 = 36864;
                            BR6 = 240000;
                            profileLevels5 = profileLevels6;
                            DPB = 184320;
                            break;
                        case 131072:
                            str19 = str22;
                            MBPS5 = 4177920;
                            FS6 = 139264;
                            BR6 = 240000;
                            profileLevels5 = profileLevels6;
                            DPB = 696320;
                            break;
                        case 262144:
                            str19 = str22;
                            MBPS5 = 8355840;
                            FS6 = 139264;
                            BR6 = 480000;
                            profileLevels5 = profileLevels6;
                            DPB = 696320;
                            break;
                        case 524288:
                            str19 = str22;
                            MBPS5 = 16711680;
                            FS6 = 139264;
                            BR6 = 800000;
                            profileLevels5 = profileLevels6;
                            DPB = 696320;
                            break;
                        default:
                            str19 = str22;
                            Log.m104w(TAG, str22 + profileLevel.level + " for " + mime);
                            errors |= 1;
                            MBPS5 = 0;
                            FS6 = 0;
                            BR6 = 0;
                            profileLevels5 = profileLevels6;
                            DPB = 0;
                            break;
                    }
                    int FS7 = i5;
                    switch (profileLevel.profile) {
                        case 1:
                        case 2:
                        case 65536:
                            str20 = str21;
                            BR7 = BR6 * 1000;
                            break;
                        case 4:
                        case 32:
                        case 64:
                            str20 = str21;
                            Log.m104w(TAG, str21 + profileLevel.profile + " for " + mime);
                            errors |= 2;
                            supported = false;
                            BR7 = BR6 * 1000;
                            break;
                        case 8:
                        case 524288:
                            BR7 = BR6 * MetricsProto.MetricsEvent.FIELD_SELECTION_RANGE_START;
                            str20 = str21;
                            break;
                        case 16:
                            BR7 = BR6 * 3000;
                            str20 = str21;
                            break;
                        default:
                            str20 = str21;
                            Log.m104w(TAG, str23 + profileLevel.profile + " for " + mime);
                            errors |= 1;
                            BR7 = BR6 * 1000;
                            break;
                    }
                    if (supported) {
                        errors &= -5;
                    }
                    maxBlocksPerSecond = Math.max(MBPS5, maxBlocksPerSecond);
                    maxBlocks2 = Math.max(FS6, maxBlocks2);
                    maxBps = Math.max(BR7, maxBps);
                    maxDPBBlocks = Math.max(maxDPBBlocks, DPB);
                    i5 = FS7 + 1;
                    str23 = str23;
                    length = i6;
                    profileLevels6 = profileLevels5;
                    str21 = str20;
                    num3 = num4;
                    str22 = str19;
                }
                num = num3;
                int maxLengthInBlocks = (int) Math.sqrt(maxBlocks2 * 8);
                applyMacroBlockLimits(maxLengthInBlocks, maxLengthInBlocks, maxBlocks2, maxBlocksPerSecond, 16, 16, 1, 1);
                maxBlocks = maxBps;
            } else {
                String str26 = "Unrecognized level ";
                String str27 = "Unrecognized profile ";
                num = num3;
                String str28 = "/";
                String str29 = "Unrecognized profile/level ";
                if (mime.equalsIgnoreCase(MediaFormat.MIMETYPE_VIDEO_MPEG2)) {
                    CodecProfileLevel[] profileLevels7 = profileLevels6;
                    int length2 = profileLevels7.length;
                    errors = 4;
                    int maxBlocks3 = 99;
                    int maxHeight = 9;
                    int maxBps2 = 64000;
                    int maxWidth = 11;
                    long maxBlocksPerSecond2 = 1485;
                    int maxRate2 = 15;
                    int MBPS6 = 0;
                    while (MBPS6 < length2) {
                        int i7 = length2;
                        CodecProfileLevel profileLevel2 = profileLevels7[MBPS6];
                        boolean supported2 = true;
                        CodecProfileLevel[] profileLevels8 = profileLevels7;
                        switch (profileLevel2.profile) {
                            case 0:
                                i4 = MBPS6;
                                switch (profileLevel2.level) {
                                    case 1:
                                        MBPS4 = 40500;
                                        FS5 = 1620;
                                        str16 = str27;
                                        BR5 = 15000;
                                        FR5 = 30;
                                        str17 = str25;
                                        W2 = 45;
                                        str18 = str24;
                                        H3 = 36;
                                        break;
                                    default:
                                        Log.m104w(str25, str29 + profileLevel2.profile + str28 + profileLevel2.level + str24 + mime);
                                        errors |= 1;
                                        MBPS4 = 0;
                                        FS5 = 0;
                                        str16 = str27;
                                        BR5 = 0;
                                        FR5 = 0;
                                        str17 = str25;
                                        W2 = 0;
                                        str18 = str24;
                                        H3 = 0;
                                        break;
                                }
                            case 1:
                                i4 = MBPS6;
                                switch (profileLevel2.level) {
                                    case 0:
                                        MBPS4 = 11880;
                                        FS5 = 396;
                                        str16 = str27;
                                        BR5 = 4000;
                                        FR5 = 30;
                                        str17 = str25;
                                        W2 = 22;
                                        str18 = str24;
                                        H3 = 18;
                                        break;
                                    case 1:
                                        MBPS4 = 40500;
                                        FS5 = 1620;
                                        str16 = str27;
                                        BR5 = 15000;
                                        FR5 = 30;
                                        str17 = str25;
                                        W2 = 45;
                                        str18 = str24;
                                        H3 = 36;
                                        break;
                                    case 2:
                                        MBPS4 = 183600;
                                        FS5 = 6120;
                                        str16 = str27;
                                        BR5 = 60000;
                                        FR5 = 60;
                                        str17 = str25;
                                        W2 = 90;
                                        str18 = str24;
                                        H3 = 68;
                                        break;
                                    case 3:
                                        MBPS4 = 244800;
                                        FS5 = 8160;
                                        str16 = str27;
                                        BR5 = 80000;
                                        FR5 = 60;
                                        str17 = str25;
                                        W2 = 120;
                                        str18 = str24;
                                        H3 = 68;
                                        break;
                                    case 4:
                                        MBPS4 = 489600;
                                        FS5 = 8160;
                                        str16 = str27;
                                        BR5 = 80000;
                                        FR5 = 60;
                                        str17 = str25;
                                        W2 = 120;
                                        str18 = str24;
                                        H3 = 68;
                                        break;
                                    default:
                                        Log.m104w(str25, str29 + profileLevel2.profile + str28 + profileLevel2.level + str24 + mime);
                                        errors |= 1;
                                        MBPS4 = 0;
                                        FS5 = 0;
                                        str16 = str27;
                                        BR5 = 0;
                                        FR5 = 0;
                                        str17 = str25;
                                        W2 = 0;
                                        str18 = str24;
                                        H3 = 0;
                                        break;
                                }
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                                i4 = MBPS6;
                                Log.m108i(str25, "Unsupported profile " + profileLevel2.profile + str24 + mime);
                                errors |= 2;
                                supported2 = false;
                                MBPS4 = 0;
                                FS5 = 0;
                                str16 = str27;
                                BR5 = 0;
                                FR5 = 0;
                                str17 = str25;
                                W2 = 0;
                                str18 = str24;
                                H3 = 0;
                                break;
                            default:
                                i4 = MBPS6;
                                Log.m104w(str25, str27 + profileLevel2.profile + str24 + mime);
                                errors |= 1;
                                MBPS4 = 0;
                                FS5 = 0;
                                str16 = str27;
                                BR5 = 0;
                                FR5 = 0;
                                str17 = str25;
                                W2 = 0;
                                str18 = str24;
                                H3 = 0;
                                break;
                        }
                        if (supported2) {
                            errors &= -5;
                        }
                        maxBlocksPerSecond2 = Math.max(MBPS4, maxBlocksPerSecond2);
                        maxBlocks3 = Math.max(FS5, maxBlocks3);
                        maxBps2 = Math.max(BR5 * 1000, maxBps2);
                        maxWidth = Math.max(W2, maxWidth);
                        maxHeight = Math.max(H3, maxHeight);
                        maxRate2 = Math.max(FR5, maxRate2);
                        MBPS6 = i4 + 1;
                        length2 = i7;
                        str27 = str16;
                        str25 = str17;
                        str24 = str18;
                        str28 = str28;
                        profileLevels7 = profileLevels8;
                        str29 = str29;
                    }
                    int maxRate3 = maxRate2;
                    int maxRate4 = maxWidth;
                    int maxWidth2 = maxBlocks3;
                    applyMacroBlockLimits(maxRate4, maxHeight, maxWidth2, maxBlocksPerSecond2, 16, 16, 1, 1);
                    this.mFrameRateRange = this.mFrameRateRange.intersect(12, Integer.valueOf(maxRate3));
                    maxBlocks = maxBps2;
                } else {
                    String str30 = str27;
                    String str31 = "/";
                    String str32 = "Unrecognized profile/level ";
                    String str33 = " for ";
                    String str34 = TAG;
                    if (mime.equalsIgnoreCase(MediaFormat.MIMETYPE_VIDEO_MPEG4)) {
                        int maxRate5 = 9;
                        CodecProfileLevel[] profileLevels9 = profileLevels6;
                        int length3 = profileLevels9.length;
                        long maxBlocksPerSecond3 = 1485;
                        int maxBlocks4 = 99;
                        int maxBps3 = 64000;
                        errors = 4;
                        int maxHeight2 = 15;
                        int i8 = 0;
                        int maxWidth3 = 11;
                        while (i8 < length3) {
                            CodecProfileLevel profileLevel3 = profileLevels9[i8];
                            boolean supported3 = true;
                            int MBPS7 = profileLevel3.profile;
                            switch (MBPS7) {
                                case 1:
                                    str13 = str32;
                                    String str35 = str31;
                                    i3 = length3;
                                    String str36 = str33;
                                    profileLevels4 = profileLevels9;
                                    String str37 = str34;
                                    switch (profileLevel3.level) {
                                        case 1:
                                            MBPS3 = 1485;
                                            FS4 = 99;
                                            BR4 = 64;
                                            str34 = str37;
                                            FR4 = 15;
                                            str14 = str35;
                                            H = 9;
                                            H2 = 1;
                                            str15 = str36;
                                            W = 11;
                                            break;
                                        case 2:
                                            MBPS3 = 1485;
                                            FS4 = 99;
                                            BR4 = 128;
                                            str34 = str37;
                                            FR4 = 15;
                                            str14 = str35;
                                            H = 9;
                                            H2 = 1;
                                            str15 = str36;
                                            W = 11;
                                            break;
                                        case 4:
                                            MBPS3 = 1485;
                                            FS4 = 99;
                                            BR4 = 64;
                                            str34 = str37;
                                            FR4 = 30;
                                            str14 = str35;
                                            H = 9;
                                            H2 = 0;
                                            str15 = str36;
                                            W = 11;
                                            break;
                                        case 8:
                                            MBPS3 = 5940;
                                            FS4 = 396;
                                            BR4 = 128;
                                            str34 = str37;
                                            FR4 = 30;
                                            str14 = str35;
                                            H = 18;
                                            H2 = 0;
                                            str15 = str36;
                                            W = 22;
                                            break;
                                        case 16:
                                            MBPS3 = 11880;
                                            FS4 = 396;
                                            BR4 = 384;
                                            str34 = str37;
                                            FR4 = 30;
                                            str14 = str35;
                                            H = 18;
                                            H2 = 0;
                                            str15 = str36;
                                            W = 22;
                                            break;
                                        case 64:
                                            MBPS3 = 36000;
                                            FS4 = 1200;
                                            BR4 = 4000;
                                            str34 = str37;
                                            FR4 = 30;
                                            str14 = str35;
                                            H = 30;
                                            H2 = 0;
                                            str15 = str36;
                                            W = 40;
                                            break;
                                        case 128:
                                            MBPS3 = 40500;
                                            FS4 = 1620;
                                            BR4 = 8000;
                                            str34 = str37;
                                            FR4 = 30;
                                            str14 = str35;
                                            H = 36;
                                            H2 = 0;
                                            str15 = str36;
                                            W = 45;
                                            break;
                                        case 256:
                                            MBPS3 = 108000;
                                            FS4 = 3600;
                                            BR4 = 12000;
                                            str34 = str37;
                                            FR4 = 30;
                                            str14 = str35;
                                            H = 45;
                                            H2 = 0;
                                            str15 = str36;
                                            W = 80;
                                            break;
                                        default:
                                            Log.m104w(str37, str13 + profileLevel3.profile + str35 + profileLevel3.level + str36 + mime);
                                            errors |= 1;
                                            str34 = str37;
                                            FR4 = 0;
                                            MBPS3 = 0;
                                            FS4 = 0;
                                            BR4 = 0;
                                            str14 = str35;
                                            H = 0;
                                            H2 = 0;
                                            str15 = str36;
                                            W = 0;
                                            break;
                                    }
                                case 2:
                                case 4:
                                case 8:
                                case 16:
                                case 32:
                                case 64:
                                case 128:
                                case 256:
                                case 512:
                                case 1024:
                                case 2048:
                                case 4096:
                                case 8192:
                                case 16384:
                                    str13 = str32;
                                    String str38 = str31;
                                    i3 = length3;
                                    String str39 = str33;
                                    profileLevels4 = profileLevels9;
                                    Log.m108i(str34, "Unsupported profile " + profileLevel3.profile + str39 + mime);
                                    errors |= 2;
                                    supported3 = false;
                                    FR4 = 0;
                                    MBPS3 = 0;
                                    FS4 = 0;
                                    BR4 = 0;
                                    str14 = str38;
                                    H = 0;
                                    H2 = 0;
                                    str15 = str39;
                                    W = 0;
                                    break;
                                case 32768:
                                    switch (profileLevel3.level) {
                                        case 1:
                                        case 4:
                                            MBPS3 = 2970;
                                            FS4 = 99;
                                            str13 = str32;
                                            BR4 = 128;
                                            H = 9;
                                            H2 = 0;
                                            str14 = str31;
                                            i3 = length3;
                                            W = 11;
                                            str15 = str33;
                                            profileLevels4 = profileLevels9;
                                            FR4 = 30;
                                            break;
                                        case 8:
                                            MBPS3 = 5940;
                                            FS4 = 396;
                                            str13 = str32;
                                            BR4 = 384;
                                            H = 18;
                                            H2 = 0;
                                            str14 = str31;
                                            i3 = length3;
                                            W = 22;
                                            str15 = str33;
                                            profileLevels4 = profileLevels9;
                                            FR4 = 30;
                                            break;
                                        case 16:
                                            MBPS3 = 11880;
                                            FS4 = 396;
                                            str13 = str32;
                                            BR4 = 768;
                                            H = 18;
                                            H2 = 0;
                                            str14 = str31;
                                            i3 = length3;
                                            W = 22;
                                            str15 = str33;
                                            profileLevels4 = profileLevels9;
                                            FR4 = 30;
                                            break;
                                        case 24:
                                            MBPS3 = 11880;
                                            FS4 = 396;
                                            str13 = str32;
                                            BR4 = 1500;
                                            H = 18;
                                            H2 = 0;
                                            str14 = str31;
                                            i3 = length3;
                                            W = 22;
                                            str15 = str33;
                                            profileLevels4 = profileLevels9;
                                            FR4 = 30;
                                            break;
                                        case 32:
                                            MBPS3 = 23760;
                                            FS4 = 792;
                                            str13 = str32;
                                            BR4 = 3000;
                                            H = 36;
                                            H2 = 0;
                                            str14 = str31;
                                            i3 = length3;
                                            W = 44;
                                            str15 = str33;
                                            profileLevels4 = profileLevels9;
                                            FR4 = 30;
                                            break;
                                        case 128:
                                            MBPS3 = 48600;
                                            FS4 = 1620;
                                            str13 = str32;
                                            BR4 = 8000;
                                            H = 36;
                                            H2 = 0;
                                            str14 = str31;
                                            i3 = length3;
                                            W = 45;
                                            str15 = str33;
                                            profileLevels4 = profileLevels9;
                                            FR4 = 30;
                                            break;
                                        default:
                                            str13 = str32;
                                            StringBuilder append = new StringBuilder().append(str13);
                                            int BR8 = profileLevel3.profile;
                                            StringBuilder append2 = append.append(BR8);
                                            String str40 = str31;
                                            i3 = length3;
                                            String str41 = str33;
                                            profileLevels4 = profileLevels9;
                                            Log.m104w(str34, append2.append(str40).append(profileLevel3.level).append(str41).append(mime).toString());
                                            errors |= 1;
                                            FR4 = 0;
                                            MBPS3 = 0;
                                            FS4 = 0;
                                            BR4 = 0;
                                            str14 = str40;
                                            H = 0;
                                            H2 = 0;
                                            str15 = str41;
                                            W = 0;
                                            break;
                                    }
                                default:
                                    str13 = str32;
                                    String str42 = str31;
                                    i3 = length3;
                                    String str43 = str33;
                                    profileLevels4 = profileLevels9;
                                    Log.m104w(str34, str30 + profileLevel3.profile + str43 + mime);
                                    errors |= 1;
                                    FR4 = 0;
                                    MBPS3 = 0;
                                    FS4 = 0;
                                    BR4 = 0;
                                    str14 = str42;
                                    H = 0;
                                    H2 = 0;
                                    str15 = str43;
                                    W = 0;
                                    break;
                            }
                            if (supported3) {
                                errors &= -5;
                            }
                            str32 = str13;
                            int maxRate6 = maxHeight2;
                            maxBlocksPerSecond3 = Math.max(MBPS3, maxBlocksPerSecond3);
                            maxBlocks4 = Math.max(FS4, maxBlocks4);
                            maxBps3 = Math.max(BR4 * 1000, maxBps3);
                            if (H2 != 0) {
                                int maxWidth4 = Math.max(W, maxWidth3);
                                int maxHeight3 = Math.max(H, maxRate5);
                                maxWidth3 = maxWidth4;
                                maxRate5 = maxHeight3;
                                maxHeight2 = Math.max(FR4, maxRate6);
                            } else {
                                int maxDim = (int) Math.sqrt(FS4 * 2);
                                int maxWidth5 = Math.max(maxDim, maxWidth3);
                                int maxHeight4 = Math.max(maxDim, maxRate5);
                                maxHeight2 = Math.max(Math.max(FR4, 60), maxRate6);
                                maxWidth3 = maxWidth5;
                                maxRate5 = maxHeight4;
                            }
                            i8++;
                            profileLevels9 = profileLevels4;
                            length3 = i3;
                            str31 = str14;
                            str33 = str15;
                        }
                        int maxRate7 = maxHeight2;
                        applyMacroBlockLimits(maxWidth3, maxRate5, maxBlocks4, maxBlocksPerSecond3, 16, 16, 1, 1);
                        this.mFrameRateRange = this.mFrameRateRange.intersect(12, Integer.valueOf(maxRate7));
                        maxBlocks = maxBps3;
                    } else {
                        String str44 = str33;
                        if (mime.equalsIgnoreCase("video/3gpp")) {
                            CodecProfileLevel[] profileLevels10 = profileLevels6;
                            int length4 = profileLevels10.length;
                            int errors3 = 4;
                            int errors4 = 15;
                            int minAlignment = 16;
                            int maxWidth6 = 11;
                            int maxHeight5 = 9;
                            int maxBps4 = 64000;
                            int minWidth2 = 11;
                            int maxBlocks5 = 99;
                            int minHeight3 = 9;
                            long maxBlocksPerSecond4 = 1485;
                            int minH = 0;
                            while (minH < length4) {
                                CodecProfileLevel profileLevel4 = profileLevels10[minH];
                                int BR9 = 0;
                                int minW = minWidth2;
                                int minH2 = minHeight3;
                                boolean strict = false;
                                switch (profileLevel4.level) {
                                    case 1:
                                        i2 = length4;
                                        strict = true;
                                        BR9 = 1;
                                        int MBPS8 = 11 * 9 * 15;
                                        MBPS = MBPS8;
                                        str11 = str44;
                                        MBPS2 = minH;
                                        profileLevels3 = profileLevels10;
                                        str12 = str34;
                                        FR2 = 15;
                                        minHeight = minHeight3;
                                        FR3 = minWidth2;
                                        minHeight2 = 11;
                                        minWidth = 9;
                                        break;
                                    case 2:
                                        i2 = length4;
                                        strict = true;
                                        BR9 = 2;
                                        int MBPS9 = 22 * 18 * 15;
                                        MBPS = MBPS9;
                                        str11 = str44;
                                        MBPS2 = minH;
                                        profileLevels3 = profileLevels10;
                                        str12 = str34;
                                        FR2 = 30;
                                        minHeight = minHeight3;
                                        FR3 = minWidth2;
                                        minHeight2 = 22;
                                        minWidth = 18;
                                        break;
                                    case 4:
                                        i2 = length4;
                                        strict = true;
                                        BR9 = 6;
                                        int MBPS10 = 22 * 18 * 30;
                                        MBPS = MBPS10;
                                        str11 = str44;
                                        MBPS2 = minH;
                                        profileLevels3 = profileLevels10;
                                        str12 = str34;
                                        FR2 = 30;
                                        minHeight = minHeight3;
                                        FR3 = minWidth2;
                                        minHeight2 = 22;
                                        minWidth = 18;
                                        break;
                                    case 8:
                                        i2 = length4;
                                        strict = true;
                                        BR9 = 32;
                                        int MBPS11 = 22 * 18 * 30;
                                        MBPS = MBPS11;
                                        str11 = str44;
                                        MBPS2 = minH;
                                        profileLevels3 = profileLevels10;
                                        str12 = str34;
                                        FR2 = 30;
                                        minHeight = minHeight3;
                                        FR3 = minWidth2;
                                        minHeight2 = 22;
                                        minWidth = 18;
                                        break;
                                    case 16:
                                        int minAlignment2 = profileLevel4.profile;
                                        i2 = length4;
                                        strict = minAlignment2 == 1 || profileLevel4.profile == 4;
                                        if (!strict) {
                                            minW = 1;
                                            minH2 = 1;
                                            minAlignment = 4;
                                        }
                                        BR9 = 2;
                                        int MBPS12 = 11 * 9 * 15;
                                        MBPS = MBPS12;
                                        str11 = str44;
                                        MBPS2 = minH;
                                        profileLevels3 = profileLevels10;
                                        str12 = str34;
                                        FR2 = 15;
                                        minHeight = minHeight3;
                                        FR3 = minWidth2;
                                        minHeight2 = 11;
                                        minWidth = 9;
                                        break;
                                    case 32:
                                        minW = 1;
                                        minH2 = 1;
                                        BR9 = 64;
                                        int MBPS13 = 22 * 18 * 50;
                                        minAlignment = 4;
                                        i2 = length4;
                                        MBPS = MBPS13;
                                        str11 = str44;
                                        MBPS2 = minH;
                                        profileLevels3 = profileLevels10;
                                        str12 = str34;
                                        FR2 = 60;
                                        minHeight = minHeight3;
                                        FR3 = minWidth2;
                                        minHeight2 = 22;
                                        minWidth = 18;
                                        break;
                                    case 64:
                                        minW = 1;
                                        minH2 = 1;
                                        BR9 = 128;
                                        int MBPS14 = 45 * 18 * 50;
                                        minAlignment = 4;
                                        i2 = length4;
                                        MBPS = MBPS14;
                                        str11 = str44;
                                        MBPS2 = minH;
                                        profileLevels3 = profileLevels10;
                                        str12 = str34;
                                        FR2 = 60;
                                        minHeight = minHeight3;
                                        FR3 = minWidth2;
                                        minHeight2 = 45;
                                        minWidth = 18;
                                        break;
                                    case 128:
                                        minW = 1;
                                        minH2 = 1;
                                        BR9 = 256;
                                        int MBPS15 = 45 * 36 * 50;
                                        minAlignment = 4;
                                        i2 = length4;
                                        MBPS = MBPS15;
                                        str11 = str44;
                                        MBPS2 = minH;
                                        profileLevels3 = profileLevels10;
                                        str12 = str34;
                                        FR2 = 60;
                                        minHeight = minHeight3;
                                        FR3 = minWidth2;
                                        minHeight2 = 45;
                                        minWidth = 36;
                                        break;
                                    default:
                                        i2 = length4;
                                        str11 = str44;
                                        profileLevels3 = profileLevels10;
                                        str12 = str34;
                                        Log.m104w(str12, str32 + profileLevel4.profile + str31 + profileLevel4.level + str11 + mime);
                                        errors3 |= 1;
                                        minHeight = minHeight3;
                                        MBPS = 0;
                                        minHeight2 = 0;
                                        MBPS2 = minH;
                                        FR2 = 0;
                                        FR3 = minWidth2;
                                        minWidth = 0;
                                        break;
                                }
                                int W3 = FR2;
                                int FR6 = profileLevel4.profile;
                                switch (FR6) {
                                    case 1:
                                    case 2:
                                    case 4:
                                    case 8:
                                    case 16:
                                    case 32:
                                    case 64:
                                    case 128:
                                    case 256:
                                        maxRate = errors4;
                                        break;
                                    default:
                                        maxRate = errors4;
                                        Log.m104w(str12, str30 + profileLevel4.profile + str11 + mime);
                                        errors3 |= 1;
                                        break;
                                }
                                if (strict) {
                                    minW = 11;
                                    minH2 = 9;
                                } else {
                                    this.mAllowMbOverride = true;
                                }
                                int minH3 = minH2;
                                errors3 &= -5;
                                String str45 = str12;
                                maxBlocksPerSecond4 = Math.max(MBPS, maxBlocksPerSecond4);
                                maxBlocks5 = Math.max(minHeight2 * minWidth, maxBlocks5);
                                maxBps4 = Math.max(64000 * BR9, maxBps4);
                                maxWidth6 = Math.max(minHeight2, maxWidth6);
                                maxHeight5 = Math.max(minWidth, maxHeight5);
                                int maxRate8 = Math.max(W3, maxRate);
                                int H4 = FR3;
                                minWidth2 = Math.min(minW, H4);
                                int W4 = minHeight;
                                minHeight3 = Math.min(minH3, W4);
                                minH = MBPS2 + 1;
                                errors4 = maxRate8;
                                profileLevels10 = profileLevels3;
                                str44 = str11;
                                str34 = str45;
                                length4 = i2;
                            }
                            int maxRate9 = errors4;
                            if (!this.mAllowMbOverride) {
                                this.mBlockAspectRatioRange = Range.create(new Rational(11, 9), new Rational(11, 9));
                            }
                            int i9 = minWidth2;
                            int minWidth3 = maxWidth6;
                            int i10 = maxHeight5;
                            int maxHeight6 = maxBlocks5;
                            applyMacroBlockLimits(i9, minHeight3, minWidth3, i10, maxHeight6, maxBlocksPerSecond4, 16, 16, minAlignment, minAlignment);
                            this.mFrameRateRange = Range.create(num, Integer.valueOf(maxRate9));
                            errors = errors3;
                            maxBlocks = maxBps4;
                        } else {
                            String str46 = str44;
                            Integer num5 = num;
                            String str47 = str34;
                            if (mime.equalsIgnoreCase(MediaFormat.MIMETYPE_VIDEO_VP8)) {
                                int length5 = profileLevels6.length;
                                errors = 4;
                                int i11 = 0;
                                while (i11 < length5) {
                                    CodecProfileLevel profileLevel5 = profileLevels6[i11];
                                    switch (profileLevel5.level) {
                                        case 1:
                                        case 2:
                                        case 4:
                                        case 8:
                                            str7 = str26;
                                            str8 = str46;
                                            str9 = str47;
                                            break;
                                        default:
                                            str7 = str26;
                                            str8 = str46;
                                            str9 = str47;
                                            Log.m104w(str9, str7 + profileLevel5.level + str8 + mime);
                                            errors |= 1;
                                            break;
                                    }
                                    switch (profileLevel5.profile) {
                                        case 1:
                                            str10 = str30;
                                            break;
                                        default:
                                            str10 = str30;
                                            Log.m104w(str9, str10 + profileLevel5.profile + str8 + mime);
                                            errors |= 1;
                                            break;
                                    }
                                    errors &= -5;
                                    i11++;
                                    str26 = str7;
                                    str46 = str8;
                                    str47 = str9;
                                    str30 = str10;
                                }
                                applyMacroBlockLimits(32767, 32767, Integer.MAX_VALUE, 2147483647L, 16, 16, 1, 1);
                                num = num5;
                                maxBlocks = 100000000;
                            } else {
                                CodecProfileLevel[] profileLevels11 = profileLevels6;
                                String str48 = str30;
                                String str49 = str26;
                                String str50 = str47;
                                if (mime.equalsIgnoreCase(MediaFormat.MIMETYPE_VIDEO_VP9)) {
                                    long maxBlocksPerSecond5 = 829440;
                                    int maxBlocks6 = 36864;
                                    int length6 = profileLevels11.length;
                                    errors = 4;
                                    int maxDim2 = 512;
                                    int maxBps5 = 200000;
                                    int maxBps6 = 0;
                                    while (maxBps6 < length6) {
                                        CodecProfileLevel profileLevel6 = profileLevels11[maxBps6];
                                        int i12 = length6;
                                        switch (profileLevel6.level) {
                                            case 1:
                                                profileLevels2 = profileLevels11;
                                                SR2 = 829440;
                                                D3 = 512;
                                                num2 = num5;
                                                FS3 = 36864;
                                                str5 = str49;
                                                BR3 = 200;
                                                break;
                                            case 2:
                                                profileLevels2 = profileLevels11;
                                                SR2 = 2764800;
                                                D3 = 768;
                                                num2 = num5;
                                                FS3 = 73728;
                                                str5 = str49;
                                                BR3 = 800;
                                                break;
                                            case 4:
                                                profileLevels2 = profileLevels11;
                                                SR2 = 4608000;
                                                D3 = 960;
                                                num2 = num5;
                                                FS3 = 122880;
                                                str5 = str49;
                                                BR3 = 1800;
                                                break;
                                            case 8:
                                                profileLevels2 = profileLevels11;
                                                SR2 = 9216000;
                                                D3 = 1344;
                                                num2 = num5;
                                                FS3 = 245760;
                                                str5 = str49;
                                                BR3 = 3600;
                                                break;
                                            case 16:
                                                profileLevels2 = profileLevels11;
                                                SR2 = 20736000;
                                                D3 = 2048;
                                                num2 = num5;
                                                FS3 = 552960;
                                                str5 = str49;
                                                BR3 = 7200;
                                                break;
                                            case 32:
                                                profileLevels2 = profileLevels11;
                                                SR2 = 36864000;
                                                D3 = 2752;
                                                num2 = num5;
                                                FS3 = 983040;
                                                str5 = str49;
                                                BR3 = 12000;
                                                break;
                                            case 64:
                                                profileLevels2 = profileLevels11;
                                                SR2 = 83558400;
                                                D3 = 4160;
                                                num2 = num5;
                                                FS3 = 2228224;
                                                str5 = str49;
                                                BR3 = 18000;
                                                break;
                                            case 128:
                                                profileLevels2 = profileLevels11;
                                                SR2 = 160432128;
                                                D3 = 4160;
                                                num2 = num5;
                                                FS3 = 2228224;
                                                str5 = str49;
                                                BR3 = 30000;
                                                break;
                                            case 256:
                                                profileLevels2 = profileLevels11;
                                                SR2 = 311951360;
                                                D3 = 8384;
                                                num2 = num5;
                                                FS3 = 8912896;
                                                str5 = str49;
                                                BR3 = 60000;
                                                break;
                                            case 512:
                                                profileLevels2 = profileLevels11;
                                                SR2 = 588251136;
                                                D3 = 8384;
                                                num2 = num5;
                                                FS3 = 8912896;
                                                str5 = str49;
                                                BR3 = 120000;
                                                break;
                                            case 1024:
                                                profileLevels2 = profileLevels11;
                                                SR2 = 1176502272;
                                                D3 = 8384;
                                                num2 = num5;
                                                FS3 = 8912896;
                                                str5 = str49;
                                                BR3 = 180000;
                                                break;
                                            case 2048:
                                                profileLevels2 = profileLevels11;
                                                SR2 = 1176502272;
                                                D3 = 16832;
                                                num2 = num5;
                                                FS3 = 35651584;
                                                str5 = str49;
                                                BR3 = 180000;
                                                break;
                                            case 4096:
                                                profileLevels2 = profileLevels11;
                                                SR2 = 2353004544L;
                                                D3 = 16832;
                                                num2 = num5;
                                                FS3 = 35651584;
                                                str5 = str49;
                                                BR3 = 240000;
                                                break;
                                            case 8192:
                                                profileLevels2 = profileLevels11;
                                                SR2 = 4706009088L;
                                                D3 = 16832;
                                                num2 = num5;
                                                FS3 = 35651584;
                                                str5 = str49;
                                                BR3 = 480000;
                                                break;
                                            default:
                                                Log.m104w(str50, str49 + profileLevel6.level + str46 + mime);
                                                errors |= 1;
                                                profileLevels2 = profileLevels11;
                                                SR2 = 0;
                                                D3 = 0;
                                                num2 = num5;
                                                FS3 = 0;
                                                str5 = str49;
                                                BR3 = 0;
                                                break;
                                        }
                                        int i13 = maxBps6;
                                        switch (profileLevel6.profile) {
                                            case 1:
                                            case 2:
                                            case 4:
                                            case 8:
                                            case 4096:
                                            case 8192:
                                            case 16384:
                                            case 32768:
                                                str6 = str48;
                                                break;
                                            default:
                                                str6 = str48;
                                                Log.m104w(str50, str48 + profileLevel6.profile + str46 + mime);
                                                errors |= 1;
                                                break;
                                        }
                                        errors &= -5;
                                        maxBlocksPerSecond5 = Math.max(SR2, maxBlocksPerSecond5);
                                        maxBlocks6 = Math.max(FS3, maxBlocks6);
                                        maxBps5 = Math.max(BR3 * 1000, maxBps5);
                                        maxDim2 = Math.max(D3, maxDim2);
                                        maxBps6 = i13 + 1;
                                        num5 = num2;
                                        str49 = str5;
                                        str48 = str6;
                                        length6 = i12;
                                        profileLevels11 = profileLevels2;
                                    }
                                    num = num5;
                                    int maxLengthInBlocks2 = Utils.divUp(maxDim2, 8);
                                    applyMacroBlockLimits(maxLengthInBlocks2, maxLengthInBlocks2, Utils.divUp(maxBlocks6, 64), Utils.divUp(maxBlocksPerSecond5, 64L), 8, 8, 1, 1);
                                    maxBlocks = maxBps5;
                                } else {
                                    String str51 = str49;
                                    String str52 = str48;
                                    num = num5;
                                    if (mime.equalsIgnoreCase(MediaFormat.MIMETYPE_VIDEO_HEVC)) {
                                        long maxBlocksPerSecond6 = 576 * 15;
                                        int length7 = profileLevels11.length;
                                        long maxBlocksPerSecond7 = maxBlocksPerSecond6;
                                        int maxBlocks7 = 576;
                                        int maxBps7 = 128000;
                                        errors = 4;
                                        int i14 = 0;
                                        while (i14 < length7) {
                                            CodecProfileLevel profileLevel7 = profileLevels11[i14];
                                            int FS8 = 0;
                                            int BR10 = 0;
                                            switch (profileLevel7.level) {
                                                case 1:
                                                case 2:
                                                    FS8 = 36864;
                                                    BR10 = 128;
                                                    FR = 15.0d;
                                                    str3 = str51;
                                                    break;
                                                case 4:
                                                case 8:
                                                    FS8 = 122880;
                                                    BR10 = 1500;
                                                    FR = 30.0d;
                                                    str3 = str51;
                                                    break;
                                                case 16:
                                                case 32:
                                                    FS8 = 245760;
                                                    BR10 = 3000;
                                                    FR = 30.0d;
                                                    str3 = str51;
                                                    break;
                                                case 64:
                                                case 128:
                                                    FS8 = 552960;
                                                    BR10 = 6000;
                                                    FR = 30.0d;
                                                    str3 = str51;
                                                    break;
                                                case 256:
                                                case 512:
                                                    FS8 = SurfaceControl.FX_SURFACE_MASK;
                                                    BR10 = 10000;
                                                    FR = 33.75d;
                                                    str3 = str51;
                                                    break;
                                                case 1024:
                                                    FS8 = 2228224;
                                                    BR10 = 12000;
                                                    FR = 30.0d;
                                                    str3 = str51;
                                                    break;
                                                case 2048:
                                                    FS8 = 2228224;
                                                    BR10 = 30000;
                                                    FR = 30.0d;
                                                    str3 = str51;
                                                    break;
                                                case 4096:
                                                    FS8 = 2228224;
                                                    BR10 = 20000;
                                                    FR = 60.0d;
                                                    str3 = str51;
                                                    break;
                                                case 8192:
                                                    FS8 = 2228224;
                                                    BR10 = 50000;
                                                    FR = 60.0d;
                                                    str3 = str51;
                                                    break;
                                                case 16384:
                                                    FS8 = 8912896;
                                                    BR10 = 25000;
                                                    FR = 30.0d;
                                                    str3 = str51;
                                                    break;
                                                case 32768:
                                                    FS8 = 8912896;
                                                    BR10 = 100000;
                                                    FR = 30.0d;
                                                    str3 = str51;
                                                    break;
                                                case 65536:
                                                    FS8 = 8912896;
                                                    BR10 = 40000;
                                                    FR = 60.0d;
                                                    str3 = str51;
                                                    break;
                                                case 131072:
                                                    FS8 = 8912896;
                                                    BR10 = Protocol.BASE_WIFI_SCANNER_SERVICE;
                                                    FR = 60.0d;
                                                    str3 = str51;
                                                    break;
                                                case 262144:
                                                    FS8 = 8912896;
                                                    BR10 = 60000;
                                                    FR = 120.0d;
                                                    str3 = str51;
                                                    break;
                                                case 524288:
                                                    FS8 = 8912896;
                                                    BR10 = 240000;
                                                    FR = 120.0d;
                                                    str3 = str51;
                                                    break;
                                                case 1048576:
                                                    FS8 = 35651584;
                                                    BR10 = 60000;
                                                    FR = 30.0d;
                                                    str3 = str51;
                                                    break;
                                                case 2097152:
                                                    FS8 = 35651584;
                                                    BR10 = 240000;
                                                    FR = 30.0d;
                                                    str3 = str51;
                                                    break;
                                                case 4194304:
                                                    FS8 = 35651584;
                                                    BR10 = 120000;
                                                    FR = 60.0d;
                                                    str3 = str51;
                                                    break;
                                                case 8388608:
                                                    FS8 = 35651584;
                                                    BR10 = 480000;
                                                    FR = 60.0d;
                                                    str3 = str51;
                                                    break;
                                                case 16777216:
                                                    FS8 = 35651584;
                                                    BR10 = 240000;
                                                    FR = 120.0d;
                                                    str3 = str51;
                                                    break;
                                                case 33554432:
                                                    FS8 = 35651584;
                                                    BR10 = 800000;
                                                    FR = 120.0d;
                                                    str3 = str51;
                                                    break;
                                                default:
                                                    FR = 0.0d;
                                                    str3 = str51;
                                                    Log.m104w(str50, str3 + profileLevel7.level + str46 + mime);
                                                    errors |= 1;
                                                    break;
                                            }
                                            switch (profileLevel7.profile) {
                                                case 1:
                                                case 2:
                                                case 4:
                                                case 4096:
                                                case 8192:
                                                    i = length7;
                                                    str4 = str52;
                                                    break;
                                                default:
                                                    str4 = str52;
                                                    i = length7;
                                                    Log.m104w(str50, str4 + profileLevel7.profile + str46 + mime);
                                                    errors |= 1;
                                                    break;
                                            }
                                            errors &= -5;
                                            maxBlocksPerSecond7 = Math.max((int) (FS2 * FR), maxBlocksPerSecond7);
                                            maxBlocks7 = Math.max(FS8 >> 6, maxBlocks7);
                                            maxBps7 = Math.max(BR10 * 1000, maxBps7);
                                            i14++;
                                            str51 = str3;
                                            length7 = i;
                                            str50 = str50;
                                            str52 = str4;
                                        }
                                        int maxLengthInBlocks3 = (int) Math.sqrt(maxBlocks7 * 8);
                                        applyMacroBlockLimits(maxLengthInBlocks3, maxLengthInBlocks3, maxBlocks7, maxBlocksPerSecond7, 8, 8, 1, 1);
                                        maxBlocks = maxBps7;
                                    } else {
                                        CodecProfileLevel[] profileLevels12 = profileLevels11;
                                        String str53 = str50;
                                        String str54 = str51;
                                        if (mime.equalsIgnoreCase(MediaFormat.MIMETYPE_VIDEO_AV1)) {
                                            long maxBlocksPerSecond8 = 829440;
                                            int maxBlocks8 = 36864;
                                            int length8 = profileLevels12.length;
                                            int maxBps8 = 200000;
                                            int maxDim3 = 512;
                                            int errors5 = 4;
                                            int D4 = 0;
                                            while (D4 < length8) {
                                                CodecProfileLevel profileLevel8 = profileLevels12[D4];
                                                long SR3 = profileLevel8.level;
                                                switch (SR3) {
                                                    case 1:
                                                        profileLevels = profileLevels12;
                                                        D = 2048;
                                                        str = str54;
                                                        FS = 147456;
                                                        BR = length8;
                                                        BR2 = 1500;
                                                        errors2 = errors5;
                                                        SR = 5529600;
                                                        str2 = str53;
                                                        break;
                                                    case 2:
                                                    case 4:
                                                    case 8:
                                                        profileLevels = profileLevels12;
                                                        D = 2816;
                                                        str = str54;
                                                        FS = 278784;
                                                        BR = length8;
                                                        BR2 = 3000;
                                                        errors2 = errors5;
                                                        SR = 10454400;
                                                        str2 = str53;
                                                        break;
                                                    case 16:
                                                        profileLevels = profileLevels12;
                                                        D = 4352;
                                                        str = str54;
                                                        FS = 665856;
                                                        BR = length8;
                                                        BR2 = 6000;
                                                        errors2 = errors5;
                                                        SR = 24969600;
                                                        str2 = str53;
                                                        break;
                                                    case 32:
                                                    case 64:
                                                    case 128:
                                                        profileLevels = profileLevels12;
                                                        D = 5504;
                                                        str = str54;
                                                        FS = 1065024;
                                                        BR = length8;
                                                        BR2 = 10000;
                                                        errors2 = errors5;
                                                        SR = 39938400;
                                                        str2 = str53;
                                                        break;
                                                    case 256:
                                                        profileLevels = profileLevels12;
                                                        D = 6144;
                                                        str = str54;
                                                        FS = 2359296;
                                                        BR = length8;
                                                        BR2 = 12000;
                                                        errors2 = errors5;
                                                        SR = 77856768;
                                                        str2 = str53;
                                                        break;
                                                    case 512:
                                                    case 1024:
                                                    case 2048:
                                                        profileLevels = profileLevels12;
                                                        D = 6144;
                                                        str = str54;
                                                        FS = 2359296;
                                                        BR = length8;
                                                        BR2 = 20000;
                                                        errors2 = errors5;
                                                        SR = 155713536;
                                                        str2 = str53;
                                                        break;
                                                    case 4096:
                                                        profileLevels = profileLevels12;
                                                        D = 8192;
                                                        str = str54;
                                                        FS = 8912896;
                                                        BR = length8;
                                                        BR2 = 30000;
                                                        errors2 = errors5;
                                                        SR = 273715200;
                                                        str2 = str53;
                                                        break;
                                                    case 8192:
                                                        profileLevels = profileLevels12;
                                                        D = 8192;
                                                        str = str54;
                                                        FS = 8912896;
                                                        BR = length8;
                                                        BR2 = 40000;
                                                        errors2 = errors5;
                                                        SR = 547430400;
                                                        str2 = str53;
                                                        break;
                                                    case 16384:
                                                        profileLevels = profileLevels12;
                                                        D = 8192;
                                                        str = str54;
                                                        FS = 8912896;
                                                        BR = length8;
                                                        BR2 = 60000;
                                                        errors2 = errors5;
                                                        SR = 1094860800;
                                                        str2 = str53;
                                                        break;
                                                    case 32768:
                                                        profileLevels = profileLevels12;
                                                        D = 8192;
                                                        str = str54;
                                                        FS = 8912896;
                                                        BR = length8;
                                                        BR2 = 60000;
                                                        errors2 = errors5;
                                                        SR = 1176502272;
                                                        str2 = str53;
                                                        break;
                                                    case 65536:
                                                        profileLevels = profileLevels12;
                                                        D = 16384;
                                                        str = str54;
                                                        FS = 35651584;
                                                        BR = length8;
                                                        BR2 = 60000;
                                                        errors2 = errors5;
                                                        SR = 1176502272;
                                                        str2 = str53;
                                                        break;
                                                    case 131072:
                                                        profileLevels = profileLevels12;
                                                        D = 16384;
                                                        str = str54;
                                                        FS = 35651584;
                                                        BR = length8;
                                                        BR2 = 100000;
                                                        errors2 = errors5;
                                                        SR = 2189721600L;
                                                        str2 = str53;
                                                        break;
                                                    case 262144:
                                                        profileLevels = profileLevels12;
                                                        D = 16384;
                                                        str = str54;
                                                        FS = 35651584;
                                                        BR = length8;
                                                        BR2 = 160000;
                                                        errors2 = errors5;
                                                        SR = 4379443200L;
                                                        str2 = str53;
                                                        break;
                                                    case 524288:
                                                        profileLevels = profileLevels12;
                                                        D = 16384;
                                                        str = str54;
                                                        FS = 35651584;
                                                        BR = length8;
                                                        BR2 = 160000;
                                                        errors2 = errors5;
                                                        SR = 4706009088L;
                                                        str2 = str53;
                                                        break;
                                                    default:
                                                        str2 = str53;
                                                        Log.m104w(str2, str54 + profileLevel8.level + str46 + mime);
                                                        profileLevels = profileLevels12;
                                                        errors2 = errors5 | 1;
                                                        D = 0;
                                                        str = str54;
                                                        FS = 0;
                                                        SR = 0;
                                                        BR = length8;
                                                        BR2 = 0;
                                                        break;
                                                }
                                                int i15 = D4;
                                                switch (profileLevel8.profile) {
                                                    case 1:
                                                    case 2:
                                                    case 4096:
                                                    case 8192:
                                                        D2 = D;
                                                        break;
                                                    default:
                                                        D2 = D;
                                                        Log.m104w(str2, str52 + profileLevel8.profile + str46 + mime);
                                                        errors2 |= 1;
                                                        break;
                                                }
                                                maxBlocksPerSecond8 = Math.max(SR, maxBlocksPerSecond8);
                                                maxBlocks8 = Math.max(FS, maxBlocks8);
                                                maxBps8 = Math.max(BR2 * 1000, maxBps8);
                                                maxDim3 = Math.max(D2, maxDim3);
                                                D4 = i15 + 1;
                                                errors5 = errors2 & (-5);
                                                str53 = str2;
                                                length8 = BR;
                                                str54 = str;
                                                profileLevels12 = profileLevels;
                                            }
                                            int maxLengthInBlocks4 = Utils.divUp(maxDim3, 8);
                                            applyMacroBlockLimits(maxLengthInBlocks4, maxLengthInBlocks4, Utils.divUp(maxBlocks8, 64), Utils.divUp(maxBlocksPerSecond8, 64L), 8, 8, 1, 1);
                                            maxBlocks = maxBps8;
                                            errors = errors5;
                                        } else {
                                            Log.m104w(str53, "Unsupported mime " + mime);
                                            errors = 4 | 2;
                                            maxBlocks = 64000;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            this.mBitrateRange = Range.create(num, Integer.valueOf(maxBlocks));
            this.mParent.mError |= errors;
        }
    }

    /* loaded from: classes2.dex */
    public static final class EncoderCapabilities {
        public static final int BITRATE_MODE_CBR = 2;
        public static final int BITRATE_MODE_CBR_FD = 3;
        public static final int BITRATE_MODE_CQ = 0;
        public static final int BITRATE_MODE_VBR = 1;
        private static final Feature[] bitrates = {new Feature("VBR", 1, true), new Feature("CBR", 2, false), new Feature("CQ", 0, false), new Feature("CBR-FD", 3, false)};
        private int mBitControl;
        private Range<Integer> mComplexityRange;
        private Integer mDefaultComplexity;
        private Integer mDefaultQuality;
        private CodecCapabilities mParent;
        private Range<Integer> mQualityRange;
        private String mQualityScale;

        public Range<Integer> getQualityRange() {
            return this.mQualityRange;
        }

        public Range<Integer> getComplexityRange() {
            return this.mComplexityRange;
        }

        private static int parseBitrateMode(String mode) {
            Feature[] featureArr;
            for (Feature feat : bitrates) {
                if (feat.mName.equalsIgnoreCase(mode)) {
                    return feat.mValue;
                }
            }
            return 0;
        }

        public boolean isBitrateModeSupported(int mode) {
            Feature[] featureArr;
            for (Feature feat : bitrates) {
                if (mode == feat.mValue) {
                    return (this.mBitControl & (1 << mode)) != 0;
                }
            }
            return false;
        }

        private EncoderCapabilities() {
        }

        public static EncoderCapabilities create(MediaFormat info, CodecCapabilities parent) {
            EncoderCapabilities caps = new EncoderCapabilities();
            caps.init(info, parent);
            return caps;
        }

        private void init(MediaFormat info, CodecCapabilities parent) {
            this.mParent = parent;
            this.mComplexityRange = Range.create(0, 0);
            this.mQualityRange = Range.create(0, 0);
            this.mBitControl = 2;
            applyLevelLimits();
            parseFromInfo(info);
        }

        private void applyLevelLimits() {
            String mime = this.mParent.getMimeType();
            if (mime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_FLAC)) {
                this.mComplexityRange = Range.create(0, 8);
                this.mBitControl = 1;
            } else if (mime.equalsIgnoreCase("audio/3gpp") || mime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_AMR_WB) || mime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_G711_ALAW) || mime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_G711_MLAW) || mime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_MSGSM)) {
                this.mBitControl = 4;
            }
        }

        private void parseFromInfo(MediaFormat info) {
            String[] split;
            Map<String, Object> map = info.getMap();
            if (info.containsKey("complexity-range")) {
                this.mComplexityRange = Utils.parseIntRange(info.getString("complexity-range"), this.mComplexityRange);
            }
            if (info.containsKey("quality-range")) {
                this.mQualityRange = Utils.parseIntRange(info.getString("quality-range"), this.mQualityRange);
            }
            if (info.containsKey("feature-bitrate-modes")) {
                this.mBitControl = 0;
                for (String mode : info.getString("feature-bitrate-modes").split(",")) {
                    this.mBitControl |= 1 << parseBitrateMode(mode);
                }
            }
            try {
                this.mDefaultComplexity = Integer.valueOf(Integer.parseInt((String) map.get("complexity-default")));
            } catch (NumberFormatException e) {
            }
            try {
                this.mDefaultQuality = Integer.valueOf(Integer.parseInt((String) map.get("quality-default")));
            } catch (NumberFormatException e2) {
            }
            this.mQualityScale = (String) map.get("quality-scale");
        }

        private boolean supports(Integer complexity, Integer quality, Integer profile) {
            boolean ok = true;
            if (1 != 0 && complexity != null) {
                ok = this.mComplexityRange.contains((Range<Integer>) complexity);
            }
            if (ok && quality != null) {
                ok = this.mQualityRange.contains((Range<Integer>) quality);
            }
            if (ok && profile != null) {
                CodecProfileLevel[] codecProfileLevelArr = this.mParent.profileLevels;
                int length = codecProfileLevelArr.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    CodecProfileLevel pl = codecProfileLevelArr[i];
                    if (pl.profile != profile.intValue()) {
                        i++;
                    } else {
                        profile = null;
                        break;
                    }
                }
                return profile == null;
            }
            return ok;
        }

        public void getDefaultFormat(MediaFormat format) {
            Feature[] featureArr;
            Integer num;
            Integer num2;
            if (!this.mQualityRange.getUpper().equals(this.mQualityRange.getLower()) && (num2 = this.mDefaultQuality) != null) {
                format.setInteger("quality", num2.intValue());
            }
            if (!this.mComplexityRange.getUpper().equals(this.mComplexityRange.getLower()) && (num = this.mDefaultComplexity) != null) {
                format.setInteger(MediaFormat.KEY_COMPLEXITY, num.intValue());
            }
            for (Feature feat : bitrates) {
                if ((this.mBitControl & (1 << feat.mValue)) != 0) {
                    format.setInteger(MediaFormat.KEY_BITRATE_MODE, feat.mValue);
                    return;
                }
            }
        }

        public boolean supportsFormat(MediaFormat format) {
            Map<String, Object> map = format.getMap();
            String mime = this.mParent.getMimeType();
            Integer mode = (Integer) map.get(MediaFormat.KEY_BITRATE_MODE);
            if (mode != null && !isBitrateModeSupported(mode.intValue())) {
                return false;
            }
            Integer complexity = (Integer) map.get(MediaFormat.KEY_COMPLEXITY);
            if (MediaFormat.MIMETYPE_AUDIO_FLAC.equalsIgnoreCase(mime)) {
                Integer flacComplexity = (Integer) map.get(MediaFormat.KEY_FLAC_COMPRESSION_LEVEL);
                if (complexity == null) {
                    complexity = flacComplexity;
                } else if (flacComplexity != null && !complexity.equals(flacComplexity)) {
                    throw new IllegalArgumentException("conflicting values for complexity and flac-compression-level");
                }
            }
            Integer profile = (Integer) map.get(MediaFormat.KEY_PROFILE);
            if (MediaFormat.MIMETYPE_AUDIO_AAC.equalsIgnoreCase(mime)) {
                Integer aacProfile = (Integer) map.get(MediaFormat.KEY_AAC_PROFILE);
                if (profile == null) {
                    profile = aacProfile;
                } else if (aacProfile != null && !aacProfile.equals(profile)) {
                    throw new IllegalArgumentException("conflicting values for profile and aac-profile");
                }
            }
            Integer quality = (Integer) map.get("quality");
            return supports(complexity, quality, profile);
        }
    }

    /* loaded from: classes2.dex */
    public static final class CodecProfileLevel {
        public static final int AACObjectELD = 39;
        public static final int AACObjectERLC = 17;
        public static final int AACObjectERScalable = 20;
        public static final int AACObjectHE = 5;
        public static final int AACObjectHE_PS = 29;
        public static final int AACObjectLC = 2;
        public static final int AACObjectLD = 23;
        public static final int AACObjectLTP = 4;
        public static final int AACObjectMain = 1;
        public static final int AACObjectSSR = 3;
        public static final int AACObjectScalable = 6;
        public static final int AACObjectXHE = 42;
        private static final int AC4BitstreamVersion0 = 1;
        private static final int AC4BitstreamVersion1 = 2;
        private static final int AC4BitstreamVersion2 = 4;
        public static final int AC4Level0 = 1;
        public static final int AC4Level1 = 2;
        public static final int AC4Level2 = 4;
        public static final int AC4Level3 = 8;
        public static final int AC4Level4 = 16;
        private static final int AC4PresentationVersion0 = 1;
        private static final int AC4PresentationVersion1 = 2;
        private static final int AC4PresentationVersion2 = 4;
        public static final int AC4Profile00 = 257;
        public static final int AC4Profile10 = 513;
        public static final int AC4Profile11 = 514;
        public static final int AC4Profile21 = 1026;
        public static final int AC4Profile22 = 1028;
        public static final int AV1Level2 = 1;
        public static final int AV1Level21 = 2;
        public static final int AV1Level22 = 4;
        public static final int AV1Level23 = 8;
        public static final int AV1Level3 = 16;
        public static final int AV1Level31 = 32;
        public static final int AV1Level32 = 64;
        public static final int AV1Level33 = 128;
        public static final int AV1Level4 = 256;
        public static final int AV1Level41 = 512;
        public static final int AV1Level42 = 1024;
        public static final int AV1Level43 = 2048;
        public static final int AV1Level5 = 4096;
        public static final int AV1Level51 = 8192;
        public static final int AV1Level52 = 16384;
        public static final int AV1Level53 = 32768;
        public static final int AV1Level6 = 65536;
        public static final int AV1Level61 = 131072;
        public static final int AV1Level62 = 262144;
        public static final int AV1Level63 = 524288;
        public static final int AV1Level7 = 1048576;
        public static final int AV1Level71 = 2097152;
        public static final int AV1Level72 = 4194304;
        public static final int AV1Level73 = 8388608;
        public static final int AV1ProfileMain10 = 2;
        public static final int AV1ProfileMain10HDR10 = 4096;
        public static final int AV1ProfileMain10HDR10Plus = 8192;
        public static final int AV1ProfileMain8 = 1;
        public static final int AVCLevel1 = 1;
        public static final int AVCLevel11 = 4;
        public static final int AVCLevel12 = 8;
        public static final int AVCLevel13 = 16;
        public static final int AVCLevel1b = 2;
        public static final int AVCLevel2 = 32;
        public static final int AVCLevel21 = 64;
        public static final int AVCLevel22 = 128;
        public static final int AVCLevel3 = 256;
        public static final int AVCLevel31 = 512;
        public static final int AVCLevel32 = 1024;
        public static final int AVCLevel4 = 2048;
        public static final int AVCLevel41 = 4096;
        public static final int AVCLevel42 = 8192;
        public static final int AVCLevel5 = 16384;
        public static final int AVCLevel51 = 32768;
        public static final int AVCLevel52 = 65536;
        public static final int AVCLevel6 = 131072;
        public static final int AVCLevel61 = 262144;
        public static final int AVCLevel62 = 524288;
        public static final int AVCProfileBaseline = 1;
        public static final int AVCProfileConstrainedBaseline = 65536;
        public static final int AVCProfileConstrainedHigh = 524288;
        public static final int AVCProfileExtended = 4;
        public static final int AVCProfileHigh = 8;
        public static final int AVCProfileHigh10 = 16;
        public static final int AVCProfileHigh422 = 32;
        public static final int AVCProfileHigh444 = 64;
        public static final int AVCProfileMain = 2;
        public static final int DTS_HDProfileHRA = 1;
        public static final int DTS_HDProfileLBR = 2;
        public static final int DTS_HDProfileMA = 4;
        public static final int DTS_UHDProfileP1 = 1;
        public static final int DTS_UHDProfileP2 = 2;
        public static final int DolbyVisionLevel8k30 = 1024;
        public static final int DolbyVisionLevel8k60 = 2048;
        public static final int DolbyVisionLevelFhd24 = 4;
        public static final int DolbyVisionLevelFhd30 = 8;
        public static final int DolbyVisionLevelFhd60 = 16;
        public static final int DolbyVisionLevelHd24 = 1;
        public static final int DolbyVisionLevelHd30 = 2;
        public static final int DolbyVisionLevelUhd120 = 512;
        public static final int DolbyVisionLevelUhd24 = 32;
        public static final int DolbyVisionLevelUhd30 = 64;
        public static final int DolbyVisionLevelUhd48 = 128;
        public static final int DolbyVisionLevelUhd60 = 256;
        public static final int DolbyVisionProfileDvav110 = 1024;
        public static final int DolbyVisionProfileDvavPen = 2;
        public static final int DolbyVisionProfileDvavPer = 1;
        public static final int DolbyVisionProfileDvavSe = 512;
        public static final int DolbyVisionProfileDvheDen = 8;
        public static final int DolbyVisionProfileDvheDer = 4;
        public static final int DolbyVisionProfileDvheDtb = 128;
        public static final int DolbyVisionProfileDvheDth = 64;
        public static final int DolbyVisionProfileDvheDtr = 16;
        public static final int DolbyVisionProfileDvheSt = 256;
        public static final int DolbyVisionProfileDvheStn = 32;
        public static final int H263Level10 = 1;
        public static final int H263Level20 = 2;
        public static final int H263Level30 = 4;
        public static final int H263Level40 = 8;
        public static final int H263Level45 = 16;
        public static final int H263Level50 = 32;
        public static final int H263Level60 = 64;
        public static final int H263Level70 = 128;
        public static final int H263ProfileBackwardCompatible = 4;
        public static final int H263ProfileBaseline = 1;
        public static final int H263ProfileH320Coding = 2;
        public static final int H263ProfileHighCompression = 32;
        public static final int H263ProfileHighLatency = 256;
        public static final int H263ProfileISWV2 = 8;
        public static final int H263ProfileISWV3 = 16;
        public static final int H263ProfileInterlace = 128;
        public static final int H263ProfileInternet = 64;
        public static final int HEVCHighTierLevel1 = 2;
        public static final int HEVCHighTierLevel2 = 8;
        public static final int HEVCHighTierLevel21 = 32;
        public static final int HEVCHighTierLevel3 = 128;
        public static final int HEVCHighTierLevel31 = 512;
        public static final int HEVCHighTierLevel4 = 2048;
        public static final int HEVCHighTierLevel41 = 8192;
        public static final int HEVCHighTierLevel5 = 32768;
        public static final int HEVCHighTierLevel51 = 131072;
        public static final int HEVCHighTierLevel52 = 524288;
        public static final int HEVCHighTierLevel6 = 2097152;
        public static final int HEVCHighTierLevel61 = 8388608;
        public static final int HEVCHighTierLevel62 = 33554432;
        private static final int HEVCHighTierLevels = 44739242;
        public static final int HEVCMainTierLevel1 = 1;
        public static final int HEVCMainTierLevel2 = 4;
        public static final int HEVCMainTierLevel21 = 16;
        public static final int HEVCMainTierLevel3 = 64;
        public static final int HEVCMainTierLevel31 = 256;
        public static final int HEVCMainTierLevel4 = 1024;
        public static final int HEVCMainTierLevel41 = 4096;
        public static final int HEVCMainTierLevel5 = 16384;
        public static final int HEVCMainTierLevel51 = 65536;
        public static final int HEVCMainTierLevel52 = 262144;
        public static final int HEVCMainTierLevel6 = 1048576;
        public static final int HEVCMainTierLevel61 = 4194304;
        public static final int HEVCMainTierLevel62 = 16777216;
        public static final int HEVCProfileMain = 1;
        public static final int HEVCProfileMain10 = 2;
        public static final int HEVCProfileMain10HDR10 = 4096;
        public static final int HEVCProfileMain10HDR10Plus = 8192;
        public static final int HEVCProfileMainStill = 4;
        public static final int MPEG2LevelH14 = 2;
        public static final int MPEG2LevelHL = 3;
        public static final int MPEG2LevelHP = 4;
        public static final int MPEG2LevelLL = 0;
        public static final int MPEG2LevelML = 1;
        public static final int MPEG2Profile422 = 2;
        public static final int MPEG2ProfileHigh = 5;
        public static final int MPEG2ProfileMain = 1;
        public static final int MPEG2ProfileSNR = 3;
        public static final int MPEG2ProfileSimple = 0;
        public static final int MPEG2ProfileSpatial = 4;
        public static final int MPEG4Level0 = 1;
        public static final int MPEG4Level0b = 2;
        public static final int MPEG4Level1 = 4;
        public static final int MPEG4Level2 = 8;
        public static final int MPEG4Level3 = 16;
        public static final int MPEG4Level3b = 24;
        public static final int MPEG4Level4 = 32;
        public static final int MPEG4Level4a = 64;
        public static final int MPEG4Level5 = 128;
        public static final int MPEG4Level6 = 256;
        public static final int MPEG4ProfileAdvancedCoding = 4096;
        public static final int MPEG4ProfileAdvancedCore = 8192;
        public static final int MPEG4ProfileAdvancedRealTime = 1024;
        public static final int MPEG4ProfileAdvancedScalable = 16384;
        public static final int MPEG4ProfileAdvancedSimple = 32768;
        public static final int MPEG4ProfileBasicAnimated = 256;
        public static final int MPEG4ProfileCore = 4;
        public static final int MPEG4ProfileCoreScalable = 2048;
        public static final int MPEG4ProfileHybrid = 512;
        public static final int MPEG4ProfileMain = 8;
        public static final int MPEG4ProfileNbit = 16;
        public static final int MPEG4ProfileScalableTexture = 32;
        public static final int MPEG4ProfileSimple = 1;
        public static final int MPEG4ProfileSimpleFBA = 128;
        public static final int MPEG4ProfileSimpleFace = 64;
        public static final int MPEG4ProfileSimpleScalable = 2;
        public static final int VP8Level_Version0 = 1;
        public static final int VP8Level_Version1 = 2;
        public static final int VP8Level_Version2 = 4;
        public static final int VP8Level_Version3 = 8;
        public static final int VP8ProfileMain = 1;
        public static final int VP9Level1 = 1;
        public static final int VP9Level11 = 2;
        public static final int VP9Level2 = 4;
        public static final int VP9Level21 = 8;
        public static final int VP9Level3 = 16;
        public static final int VP9Level31 = 32;
        public static final int VP9Level4 = 64;
        public static final int VP9Level41 = 128;
        public static final int VP9Level5 = 256;
        public static final int VP9Level51 = 512;
        public static final int VP9Level52 = 1024;
        public static final int VP9Level6 = 2048;
        public static final int VP9Level61 = 4096;
        public static final int VP9Level62 = 8192;
        public static final int VP9Profile0 = 1;
        public static final int VP9Profile1 = 2;
        public static final int VP9Profile2 = 4;
        public static final int VP9Profile2HDR = 4096;
        public static final int VP9Profile2HDR10Plus = 16384;
        public static final int VP9Profile3 = 8;
        public static final int VP9Profile3HDR = 8192;
        public static final int VP9Profile3HDR10Plus = 32768;
        public int level;
        public int profile;

        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof CodecProfileLevel)) {
                return false;
            }
            CodecProfileLevel other = (CodecProfileLevel) obj;
            return other.profile == this.profile && other.level == this.level;
        }

        public int hashCode() {
            return Long.hashCode((this.profile << 32) | this.level);
        }
    }

    public final CodecCapabilities getCapabilitiesForType(String type) {
        CodecCapabilities caps = this.mCaps.get(type);
        if (caps == null) {
            throw new IllegalArgumentException("codec does not support type");
        }
        return caps.dup();
    }

    public MediaCodecInfo makeRegular() {
        ArrayList<CodecCapabilities> caps = new ArrayList<>();
        for (CodecCapabilities c : this.mCaps.values()) {
            if (c.isRegular()) {
                caps.add(c);
            }
        }
        if (caps.size() == 0) {
            return null;
        }
        if (caps.size() == this.mCaps.size()) {
            return this;
        }
        return new MediaCodecInfo(this.mName, this.mCanonicalName, this.mFlags, (CodecCapabilities[]) caps.toArray(new CodecCapabilities[caps.size()]));
    }
}
