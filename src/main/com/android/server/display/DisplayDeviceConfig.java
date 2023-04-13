package com.android.server.display;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.hardware.display.DisplayManagerInternal;
import android.hardware.input.HostUsiVersion;
import android.os.Environment;
import android.text.TextUtils;
import android.util.MathUtils;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.Spline;
import android.view.DisplayAddress;
import android.view.SurfaceControl;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.display.BrightnessSynchronizer;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.display.DensityMapping;
import com.android.server.display.config.AutoBrightness;
import com.android.server.display.config.BlockingZoneConfig;
import com.android.server.display.config.BrightnessThresholds;
import com.android.server.display.config.BrightnessThrottlingMap;
import com.android.server.display.config.BrightnessThrottlingPoint;
import com.android.server.display.config.Density;
import com.android.server.display.config.DisplayBrightnessPoint;
import com.android.server.display.config.DisplayConfiguration;
import com.android.server.display.config.DisplayQuirks;
import com.android.server.display.config.HbmTiming;
import com.android.server.display.config.HighBrightnessMode;
import com.android.server.display.config.IntegerArray;
import com.android.server.display.config.NitsMap;
import com.android.server.display.config.Point;
import com.android.server.display.config.RefreshRateConfigs;
import com.android.server.display.config.RefreshRateRange;
import com.android.server.display.config.RefreshRateThrottlingMap;
import com.android.server.display.config.RefreshRateThrottlingPoint;
import com.android.server.display.config.RefreshRateZone;
import com.android.server.display.config.SdrHdrRatioMap;
import com.android.server.display.config.SdrHdrRatioPoint;
import com.android.server.display.config.SensorDetails;
import com.android.server.display.config.ThermalStatus;
import com.android.server.display.config.ThermalThrottling;
import com.android.server.display.config.ThresholdPoint;
import com.android.server.display.config.UsiVersion;
import com.android.server.display.config.XmlParser;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class DisplayDeviceConfig {
    @VisibleForTesting
    static final float HDR_PERCENT_OF_SCREEN_REQUIRED_DEFAULT = 0.5f;
    public float[] mAmbientBrighteningLevels;
    public float[] mAmbientBrighteningLevelsIdle;
    public float[] mAmbientBrighteningPercentages;
    public float[] mAmbientBrighteningPercentagesIdle;
    public float[] mAmbientDarkeningLevels;
    public float[] mAmbientDarkeningLevelsIdle;
    public float[] mAmbientDarkeningPercentages;
    public float[] mAmbientDarkeningPercentagesIdle;
    public boolean mAutoBrightnessAvailable;
    public long mAutoBrightnessBrighteningLightDebounce;
    public long mAutoBrightnessDarkeningLightDebounce;
    public float[] mBacklight;
    public Spline mBacklightToBrightnessSpline;
    public Spline mBacklightToNitsSpline;
    public float[] mBrightness;
    public float[] mBrightnessLevelsLux;
    public float[] mBrightnessLevelsNits;
    public final Map<String, BrightnessThrottlingData> mBrightnessThrottlingDataMap;
    public Spline mBrightnessToBacklightSpline;
    public final Context mContext;
    public boolean mDdcAutoBrightnessAvailable;
    public int mDefaultHighBlockingZoneRefreshRate;
    public int mDefaultLowBlockingZoneRefreshRate;
    public int mDefaultPeakRefreshRate;
    public int mDefaultRefreshRate;
    public int mDefaultRefreshRateInHbmHdr;
    public int mDefaultRefreshRateInHbmSunlight;
    public DensityMapping mDensityMapping;
    public HighBrightnessModeData mHbmData;
    public int[] mHighAmbientBrightnessThresholds;
    public int[] mHighDisplayBrightnessThresholds;
    public HostUsiVersion mHostUsiVersion;
    public int mInterpolationType;
    public boolean mIsHighBrightnessModeEnabled;
    public String mLoadedFrom;
    public int[] mLowAmbientBrightnessThresholds;
    public int[] mLowDisplayBrightnessThresholds;
    public String mName;
    public float[] mNits;
    public Spline mNitsToBacklightSpline;
    public List<String> mQuirks;
    public float[] mRawBacklight;
    public float[] mRawNits;
    public final Map<String, SparseArray<SurfaceControl.RefreshRateRange>> mRefreshRateThrottlingMap;
    public final Map<String, SurfaceControl.RefreshRateRange> mRefreshRateZoneProfiles;
    public float[] mScreenBrighteningLevels;
    public float[] mScreenBrighteningLevelsIdle;
    public float[] mScreenBrighteningPercentages;
    public float[] mScreenBrighteningPercentagesIdle;
    public float[] mScreenDarkeningLevels;
    public float[] mScreenDarkeningLevelsIdle;
    public float[] mScreenDarkeningPercentages;
    public float[] mScreenDarkeningPercentagesIdle;
    public int[] mScreenOffBrightnessSensorValueToLux;
    public Spline mSdrToHdrRatioSpline;
    public static final int[] DEFAULT_BRIGHTNESS_THRESHOLDS = new int[0];
    public static final float[] DEFAULT_AMBIENT_THRESHOLD_LEVELS = {0.0f};
    public static final float[] DEFAULT_AMBIENT_BRIGHTENING_THRESHOLDS = {100.0f};
    public static final float[] DEFAULT_AMBIENT_DARKENING_THRESHOLDS = {200.0f};
    public static final float[] DEFAULT_SCREEN_THRESHOLD_LEVELS = {0.0f};
    public static final float[] DEFAULT_SCREEN_BRIGHTENING_THRESHOLDS = {100.0f};
    public static final float[] DEFAULT_SCREEN_DARKENING_THRESHOLDS = {200.0f};
    public final SensorData mAmbientLightSensor = new SensorData();
    public final SensorData mScreenOffBrightnessSensor = new SensorData();
    public final SensorData mProximitySensor = new SensorData();
    public final List<DisplayManagerInternal.RefreshRateLimitation> mRefreshRateLimitations = new ArrayList(2);
    public float mBacklightMinimum = Float.NaN;
    public float mBacklightMaximum = Float.NaN;
    public float mBrightnessDefault = Float.NaN;
    public float mBrightnessRampFastDecrease = Float.NaN;
    public float mBrightnessRampFastIncrease = Float.NaN;
    public float mBrightnessRampSlowDecrease = Float.NaN;
    public float mBrightnessRampSlowIncrease = Float.NaN;
    public long mBrightnessRampDecreaseMaxMillis = 0;
    public long mBrightnessRampIncreaseMaxMillis = 0;
    public int mAmbientHorizonLong = FrameworkStatsLog.WIFI_BYTES_TRANSFER;
    public int mAmbientHorizonShort = 2000;
    public float mScreenBrighteningMinThreshold = 0.0f;
    public float mScreenBrighteningMinThresholdIdle = 0.0f;
    public float mScreenDarkeningMinThreshold = 0.0f;
    public float mScreenDarkeningMinThresholdIdle = 0.0f;
    public float mAmbientLuxBrighteningMinThreshold = 0.0f;
    public float mAmbientLuxBrighteningMinThresholdIdle = 0.0f;
    public float mAmbientLuxDarkeningMinThreshold = 0.0f;
    public float mAmbientLuxDarkeningMinThresholdIdle = 0.0f;

    @VisibleForTesting
    public DisplayDeviceConfig(Context context) {
        float[] fArr = DEFAULT_SCREEN_THRESHOLD_LEVELS;
        this.mScreenBrighteningLevels = fArr;
        float[] fArr2 = DEFAULT_SCREEN_BRIGHTENING_THRESHOLDS;
        this.mScreenBrighteningPercentages = fArr2;
        this.mScreenDarkeningLevels = fArr;
        float[] fArr3 = DEFAULT_SCREEN_DARKENING_THRESHOLDS;
        this.mScreenDarkeningPercentages = fArr3;
        this.mScreenBrighteningLevelsIdle = fArr;
        this.mScreenBrighteningPercentagesIdle = fArr2;
        this.mScreenDarkeningLevelsIdle = fArr;
        this.mScreenDarkeningPercentagesIdle = fArr3;
        float[] fArr4 = DEFAULT_AMBIENT_THRESHOLD_LEVELS;
        this.mAmbientBrighteningLevels = fArr4;
        float[] fArr5 = DEFAULT_AMBIENT_BRIGHTENING_THRESHOLDS;
        this.mAmbientBrighteningPercentages = fArr5;
        this.mAmbientDarkeningLevels = fArr4;
        float[] fArr6 = DEFAULT_AMBIENT_DARKENING_THRESHOLDS;
        this.mAmbientDarkeningPercentages = fArr6;
        this.mAmbientBrighteningLevelsIdle = fArr4;
        this.mAmbientBrighteningPercentagesIdle = fArr5;
        this.mAmbientDarkeningLevelsIdle = fArr4;
        this.mAmbientDarkeningPercentagesIdle = fArr6;
        this.mIsHighBrightnessModeEnabled = false;
        this.mLoadedFrom = null;
        this.mAutoBrightnessBrighteningLightDebounce = -1L;
        this.mAutoBrightnessDarkeningLightDebounce = -1L;
        this.mAutoBrightnessAvailable = false;
        this.mDdcAutoBrightnessAvailable = true;
        this.mDefaultPeakRefreshRate = 0;
        this.mDefaultRefreshRate = 60;
        this.mDefaultRefreshRateInHbmHdr = 0;
        this.mDefaultRefreshRateInHbmSunlight = 0;
        this.mDefaultHighBlockingZoneRefreshRate = 0;
        this.mDefaultLowBlockingZoneRefreshRate = 60;
        this.mRefreshRateZoneProfiles = new HashMap();
        int[] iArr = DEFAULT_BRIGHTNESS_THRESHOLDS;
        this.mLowDisplayBrightnessThresholds = iArr;
        this.mLowAmbientBrightnessThresholds = iArr;
        this.mHighDisplayBrightnessThresholds = iArr;
        this.mHighAmbientBrightnessThresholds = iArr;
        this.mBrightnessThrottlingDataMap = new HashMap();
        this.mRefreshRateThrottlingMap = new HashMap();
        this.mContext = context;
    }

    public static DisplayDeviceConfig create(Context context, long j, boolean z) {
        DisplayDeviceConfig createWithoutDefaultValues = createWithoutDefaultValues(context, j, z);
        createWithoutDefaultValues.copyUninitializedValuesFromSecondaryConfig(loadDefaultConfigurationXml(context));
        return createWithoutDefaultValues;
    }

    public static DisplayDeviceConfig create(Context context, boolean z) {
        if (z) {
            return getConfigFromGlobalXml(context);
        }
        return getConfigFromPmValues(context);
    }

    public static DisplayDeviceConfig createWithoutDefaultValues(Context context, long j, boolean z) {
        DisplayDeviceConfig loadConfigFromDirectory = loadConfigFromDirectory(context, Environment.getProductDirectory(), j);
        if (loadConfigFromDirectory != null) {
            return loadConfigFromDirectory;
        }
        DisplayDeviceConfig loadConfigFromDirectory2 = loadConfigFromDirectory(context, Environment.getVendorDirectory(), j);
        return loadConfigFromDirectory2 != null ? loadConfigFromDirectory2 : create(context, z);
    }

    public static DisplayConfiguration loadDefaultConfigurationXml(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(Environment.buildPath(Environment.getProductDirectory(), new String[]{"etc", "displayconfig", "default.xml"}));
        arrayList.add(Environment.buildPath(Environment.getVendorDirectory(), new String[]{"etc", "displayconfig", "default.xml"}));
        String uiModeTypeString = Configuration.getUiModeTypeString(context.getResources().getInteger(17694807));
        if (uiModeTypeString != null) {
            arrayList.add(Environment.buildPath(Environment.getRootDirectory(), new String[]{"etc", "displayconfig", String.format("default_%s.xml", uiModeTypeString)}));
        }
        arrayList.add(Environment.buildPath(Environment.getRootDirectory(), new String[]{"etc", "displayconfig", "default.xml"}));
        File firstExistingFile = getFirstExistingFile(arrayList);
        DisplayConfiguration displayConfiguration = null;
        if (firstExistingFile == null) {
            return null;
        }
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(firstExistingFile));
            displayConfiguration = XmlParser.read(bufferedInputStream);
            if (displayConfiguration == null) {
                Slog.i("DisplayDeviceConfig", "Default DisplayDeviceConfig file is null");
            }
            bufferedInputStream.close();
        } catch (IOException | DatatypeConfigurationException | XmlPullParserException e) {
            Slog.e("DisplayDeviceConfig", "Encountered an error while reading/parsing display config file: " + firstExistingFile, e);
        }
        return displayConfiguration;
    }

    public static File getFirstExistingFile(Collection<File> collection) {
        for (File file : collection) {
            if (file.exists() && file.isFile()) {
                return file;
            }
        }
        return null;
    }

    public static DisplayDeviceConfig loadConfigFromDirectory(Context context, File file, long j) {
        DisplayDeviceConfig configFromSuffix = getConfigFromSuffix(context, file, "id_%d", j);
        if (configFromSuffix != null) {
            return configFromSuffix;
        }
        DisplayDeviceConfig configFromSuffix2 = getConfigFromSuffix(context, file, "%d", (-4611686018427387905L) & j);
        return configFromSuffix2 != null ? configFromSuffix2 : getConfigFromSuffix(context, file, "port_%d", DisplayAddress.fromPhysicalDisplayId(j).getPort());
    }

    public String getName() {
        return this.mName;
    }

    public float[] getNits() {
        return this.mNits;
    }

    public float getBacklightFromBrightness(float f) {
        return this.mBrightnessToBacklightSpline.interpolate(f);
    }

    public float getNitsFromBacklight(float f) {
        if (this.mBacklightToNitsSpline == null) {
            return -1.0f;
        }
        return this.mBacklightToNitsSpline.interpolate(Math.max(f, this.mBacklightMinimum));
    }

    public float getHdrBrightnessFromSdr(float f) {
        if (this.mSdrToHdrRatioSpline == null) {
            return -1.0f;
        }
        float nitsFromBacklight = getNitsFromBacklight(getBacklightFromBrightness(f));
        if (nitsFromBacklight == -1.0f) {
            return -1.0f;
        }
        float interpolate = nitsFromBacklight * this.mSdrToHdrRatioSpline.interpolate(nitsFromBacklight);
        Spline spline = this.mNitsToBacklightSpline;
        if (spline == null) {
            return -1.0f;
        }
        return this.mBacklightToBrightnessSpline.interpolate(Math.max(this.mBacklightMinimum, Math.min(this.mBacklightMaximum, spline.interpolate(interpolate))));
    }

    public float[] getBrightness() {
        return this.mBrightness;
    }

    public float getBrightnessDefault() {
        return this.mBrightnessDefault;
    }

    public float getBrightnessRampFastDecrease() {
        return this.mBrightnessRampFastDecrease;
    }

    public float getBrightnessRampFastIncrease() {
        return this.mBrightnessRampFastIncrease;
    }

    public float getBrightnessRampSlowDecrease() {
        return this.mBrightnessRampSlowDecrease;
    }

    public float getBrightnessRampSlowIncrease() {
        return this.mBrightnessRampSlowIncrease;
    }

    public long getBrightnessRampDecreaseMaxMillis() {
        return this.mBrightnessRampDecreaseMaxMillis;
    }

    public long getBrightnessRampIncreaseMaxMillis() {
        return this.mBrightnessRampIncreaseMaxMillis;
    }

    public int getAmbientHorizonLong() {
        return this.mAmbientHorizonLong;
    }

    public int getAmbientHorizonShort() {
        return this.mAmbientHorizonShort;
    }

    public float getScreenBrighteningMinThreshold() {
        return this.mScreenBrighteningMinThreshold;
    }

    public float getScreenDarkeningMinThreshold() {
        return this.mScreenDarkeningMinThreshold;
    }

    public float getScreenBrighteningMinThresholdIdle() {
        return this.mScreenBrighteningMinThresholdIdle;
    }

    public float getScreenDarkeningMinThresholdIdle() {
        return this.mScreenDarkeningMinThresholdIdle;
    }

    public float getAmbientLuxBrighteningMinThreshold() {
        return this.mAmbientLuxBrighteningMinThreshold;
    }

    public float getAmbientLuxDarkeningMinThreshold() {
        return this.mAmbientLuxDarkeningMinThreshold;
    }

    public float getAmbientLuxBrighteningMinThresholdIdle() {
        return this.mAmbientLuxBrighteningMinThresholdIdle;
    }

    public float getAmbientLuxDarkeningMinThresholdIdle() {
        return this.mAmbientLuxDarkeningMinThresholdIdle;
    }

    public float[] getScreenBrighteningLevels() {
        return this.mScreenBrighteningLevels;
    }

    public float[] getScreenBrighteningPercentages() {
        return this.mScreenBrighteningPercentages;
    }

    public float[] getScreenDarkeningLevels() {
        return this.mScreenDarkeningLevels;
    }

    public float[] getScreenDarkeningPercentages() {
        return this.mScreenDarkeningPercentages;
    }

    public float[] getAmbientBrighteningLevels() {
        return this.mAmbientBrighteningLevels;
    }

    public float[] getAmbientBrighteningPercentages() {
        return this.mAmbientBrighteningPercentages;
    }

    public float[] getAmbientDarkeningLevels() {
        return this.mAmbientDarkeningLevels;
    }

    public float[] getAmbientDarkeningPercentages() {
        return this.mAmbientDarkeningPercentages;
    }

    public float[] getScreenBrighteningLevelsIdle() {
        return this.mScreenBrighteningLevelsIdle;
    }

    public float[] getScreenBrighteningPercentagesIdle() {
        return this.mScreenBrighteningPercentagesIdle;
    }

    public float[] getScreenDarkeningLevelsIdle() {
        return this.mScreenDarkeningLevelsIdle;
    }

    public float[] getScreenDarkeningPercentagesIdle() {
        return this.mScreenDarkeningPercentagesIdle;
    }

    public float[] getAmbientBrighteningLevelsIdle() {
        return this.mAmbientBrighteningLevelsIdle;
    }

    public float[] getAmbientBrighteningPercentagesIdle() {
        return this.mAmbientBrighteningPercentagesIdle;
    }

    public float[] getAmbientDarkeningLevelsIdle() {
        return this.mAmbientDarkeningLevelsIdle;
    }

    public float[] getAmbientDarkeningPercentagesIdle() {
        return this.mAmbientDarkeningPercentagesIdle;
    }

    public SensorData getAmbientLightSensor() {
        return this.mAmbientLightSensor;
    }

    public SensorData getScreenOffBrightnessSensor() {
        return this.mScreenOffBrightnessSensor;
    }

    public SensorData getProximitySensor() {
        return this.mProximitySensor;
    }

    public boolean isAutoBrightnessAvailable() {
        return this.mAutoBrightnessAvailable;
    }

    public boolean hasQuirk(String str) {
        List<String> list = this.mQuirks;
        return list != null && list.contains(str);
    }

    public HighBrightnessModeData getHighBrightnessModeData() {
        if (!this.mIsHighBrightnessModeEnabled || this.mHbmData == null) {
            return null;
        }
        HighBrightnessModeData highBrightnessModeData = new HighBrightnessModeData();
        this.mHbmData.copyTo(highBrightnessModeData);
        return highBrightnessModeData;
    }

    public List<DisplayManagerInternal.RefreshRateLimitation> getRefreshRateLimitations() {
        return this.mRefreshRateLimitations;
    }

    public DensityMapping getDensityMapping() {
        return this.mDensityMapping;
    }

    public BrightnessThrottlingData getBrightnessThrottlingData(String str) {
        return BrightnessThrottlingData.create(this.mBrightnessThrottlingDataMap.get(str));
    }

    public SparseArray<SurfaceControl.RefreshRateRange> getRefreshRateThrottlingData(String str) {
        if (str == null) {
            str = "default";
        }
        return this.mRefreshRateThrottlingMap.get(str);
    }

    public long getAutoBrightnessDarkeningLightDebounce() {
        return this.mAutoBrightnessDarkeningLightDebounce;
    }

    public long getAutoBrightnessBrighteningLightDebounce() {
        return this.mAutoBrightnessBrighteningLightDebounce;
    }

    public float[] getAutoBrightnessBrighteningLevelsLux() {
        return this.mBrightnessLevelsLux;
    }

    public float[] getAutoBrightnessBrighteningLevelsNits() {
        return this.mBrightnessLevelsNits;
    }

    public int getDefaultPeakRefreshRate() {
        return this.mDefaultPeakRefreshRate;
    }

    public int getDefaultRefreshRate() {
        return this.mDefaultRefreshRate;
    }

    public int getDefaultRefreshRateInHbmHdr() {
        return this.mDefaultRefreshRateInHbmHdr;
    }

    public int getDefaultRefreshRateInHbmSunlight() {
        return this.mDefaultRefreshRateInHbmSunlight;
    }

    public int getDefaultHighBlockingZoneRefreshRate() {
        return this.mDefaultHighBlockingZoneRefreshRate;
    }

    public int getDefaultLowBlockingZoneRefreshRate() {
        return this.mDefaultLowBlockingZoneRefreshRate;
    }

    public SurfaceControl.RefreshRateRange getRefreshRange(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return this.mRefreshRateZoneProfiles.get(str);
    }

    @VisibleForTesting
    public Map<String, SurfaceControl.RefreshRateRange> getRefreshRangeProfiles() {
        return this.mRefreshRateZoneProfiles;
    }

    public int[] getLowDisplayBrightnessThresholds() {
        return this.mLowDisplayBrightnessThresholds;
    }

    public int[] getLowAmbientBrightnessThresholds() {
        return this.mLowAmbientBrightnessThresholds;
    }

    public int[] getHighDisplayBrightnessThresholds() {
        return this.mHighDisplayBrightnessThresholds;
    }

    public int[] getHighAmbientBrightnessThresholds() {
        return this.mHighAmbientBrightnessThresholds;
    }

    public int[] getScreenOffBrightnessSensorValueToLux() {
        return this.mScreenOffBrightnessSensorValueToLux;
    }

    public HostUsiVersion getHostUsiVersion() {
        return this.mHostUsiVersion;
    }

    public String toString() {
        return "DisplayDeviceConfig{mLoadedFrom=" + this.mLoadedFrom + ", mBacklight=" + Arrays.toString(this.mBacklight) + ", mNits=" + Arrays.toString(this.mNits) + ", mRawBacklight=" + Arrays.toString(this.mRawBacklight) + ", mRawNits=" + Arrays.toString(this.mRawNits) + ", mInterpolationType=" + this.mInterpolationType + ", mBrightness=" + Arrays.toString(this.mBrightness) + ", mBrightnessToBacklightSpline=" + this.mBrightnessToBacklightSpline + ", mBacklightToBrightnessSpline=" + this.mBacklightToBrightnessSpline + ", mNitsToBacklightSpline=" + this.mNitsToBacklightSpline + ", mBacklightMinimum=" + this.mBacklightMinimum + ", mBacklightMaximum=" + this.mBacklightMaximum + ", mBrightnessDefault=" + this.mBrightnessDefault + ", mQuirks=" + this.mQuirks + ", isHbmEnabled=" + this.mIsHighBrightnessModeEnabled + ", mHbmData=" + this.mHbmData + ", mSdrToHdrRatioSpline=" + this.mSdrToHdrRatioSpline + ", mBrightnessThrottlingData=" + this.mBrightnessThrottlingDataMap + "\n, mBrightnessRampFastDecrease=" + this.mBrightnessRampFastDecrease + ", mBrightnessRampFastIncrease=" + this.mBrightnessRampFastIncrease + ", mBrightnessRampSlowDecrease=" + this.mBrightnessRampSlowDecrease + ", mBrightnessRampSlowIncrease=" + this.mBrightnessRampSlowIncrease + ", mBrightnessRampDecreaseMaxMillis=" + this.mBrightnessRampDecreaseMaxMillis + ", mBrightnessRampIncreaseMaxMillis=" + this.mBrightnessRampIncreaseMaxMillis + "\n, mAmbientHorizonLong=" + this.mAmbientHorizonLong + ", mAmbientHorizonShort=" + this.mAmbientHorizonShort + "\n, mScreenDarkeningMinThreshold=" + this.mScreenDarkeningMinThreshold + ", mScreenDarkeningMinThresholdIdle=" + this.mScreenDarkeningMinThresholdIdle + ", mScreenBrighteningMinThreshold=" + this.mScreenBrighteningMinThreshold + ", mScreenBrighteningMinThresholdIdle=" + this.mScreenBrighteningMinThresholdIdle + ", mAmbientLuxDarkeningMinThreshold=" + this.mAmbientLuxDarkeningMinThreshold + ", mAmbientLuxDarkeningMinThresholdIdle=" + this.mAmbientLuxDarkeningMinThresholdIdle + ", mAmbientLuxBrighteningMinThreshold=" + this.mAmbientLuxBrighteningMinThreshold + ", mAmbientLuxBrighteningMinThresholdIdle=" + this.mAmbientLuxBrighteningMinThresholdIdle + "\n, mScreenBrighteningLevels=" + Arrays.toString(this.mScreenBrighteningLevels) + ", mScreenBrighteningPercentages=" + Arrays.toString(this.mScreenBrighteningPercentages) + ", mScreenDarkeningLevels=" + Arrays.toString(this.mScreenDarkeningLevels) + ", mScreenDarkeningPercentages=" + Arrays.toString(this.mScreenDarkeningPercentages) + ", mAmbientBrighteningLevels=" + Arrays.toString(this.mAmbientBrighteningLevels) + ", mAmbientBrighteningPercentages=" + Arrays.toString(this.mAmbientBrighteningPercentages) + ", mAmbientDarkeningLevels=" + Arrays.toString(this.mAmbientDarkeningLevels) + ", mAmbientDarkeningPercentages=" + Arrays.toString(this.mAmbientDarkeningPercentages) + "\n, mAmbientBrighteningLevelsIdle=" + Arrays.toString(this.mAmbientBrighteningLevelsIdle) + ", mAmbientBrighteningPercentagesIdle=" + Arrays.toString(this.mAmbientBrighteningPercentagesIdle) + ", mAmbientDarkeningLevelsIdle=" + Arrays.toString(this.mAmbientDarkeningLevelsIdle) + ", mAmbientDarkeningPercentagesIdle=" + Arrays.toString(this.mAmbientDarkeningPercentagesIdle) + ", mScreenBrighteningLevelsIdle=" + Arrays.toString(this.mScreenBrighteningLevelsIdle) + ", mScreenBrighteningPercentagesIdle=" + Arrays.toString(this.mScreenBrighteningPercentagesIdle) + ", mScreenDarkeningLevelsIdle=" + Arrays.toString(this.mScreenDarkeningLevelsIdle) + ", mScreenDarkeningPercentagesIdle=" + Arrays.toString(this.mScreenDarkeningPercentagesIdle) + "\n, mAmbientLightSensor=" + this.mAmbientLightSensor + ", mScreenOffBrightnessSensor=" + this.mScreenOffBrightnessSensor + ", mProximitySensor=" + this.mProximitySensor + ", mRefreshRateLimitations= " + Arrays.toString(this.mRefreshRateLimitations.toArray()) + ", mDensityMapping= " + this.mDensityMapping + ", mAutoBrightnessBrighteningLightDebounce= " + this.mAutoBrightnessBrighteningLightDebounce + ", mAutoBrightnessDarkeningLightDebounce= " + this.mAutoBrightnessDarkeningLightDebounce + ", mBrightnessLevelsLux= " + Arrays.toString(this.mBrightnessLevelsLux) + ", mBrightnessLevelsNits= " + Arrays.toString(this.mBrightnessLevelsNits) + ", mDdcAutoBrightnessAvailable= " + this.mDdcAutoBrightnessAvailable + ", mAutoBrightnessAvailable= " + this.mAutoBrightnessAvailable + "\n, mDefaultLowBlockingZoneRefreshRate= " + this.mDefaultLowBlockingZoneRefreshRate + ", mDefaultHighBlockingZoneRefreshRate= " + this.mDefaultHighBlockingZoneRefreshRate + ", mDefaultPeakRefreshRate= " + this.mDefaultPeakRefreshRate + ", mDefaultRefreshRate= " + this.mDefaultRefreshRate + ", mRefreshRateZoneProfiles= " + this.mRefreshRateZoneProfiles + ", mDefaultRefreshRateInHbmHdr= " + this.mDefaultRefreshRateInHbmHdr + ", mDefaultRefreshRateInHbmSunlight= " + this.mDefaultRefreshRateInHbmSunlight + ", mRefreshRateThrottlingMap= " + this.mRefreshRateThrottlingMap + "\n, mLowDisplayBrightnessThresholds= " + Arrays.toString(this.mLowDisplayBrightnessThresholds) + ", mLowAmbientBrightnessThresholds= " + Arrays.toString(this.mLowAmbientBrightnessThresholds) + ", mHighDisplayBrightnessThresholds= " + Arrays.toString(this.mHighDisplayBrightnessThresholds) + ", mHighAmbientBrightnessThresholds= " + Arrays.toString(this.mHighAmbientBrightnessThresholds) + "\n, mScreenOffBrightnessSensorValueToLux=" + Arrays.toString(this.mScreenOffBrightnessSensorValueToLux) + "\n, mUsiVersion= " + this.mHostUsiVersion + "}";
    }

    public static DisplayDeviceConfig getConfigFromSuffix(Context context, File file, String str, long j) {
        Locale locale = Locale.ROOT;
        File buildPath = Environment.buildPath(file, new String[]{"etc", "displayconfig", String.format(locale, "display_%s.xml", String.format(locale, str, Long.valueOf(j)))});
        DisplayDeviceConfig displayDeviceConfig = new DisplayDeviceConfig(context);
        if (displayDeviceConfig.initFromFile(buildPath)) {
            return displayDeviceConfig;
        }
        return null;
    }

    public static DisplayDeviceConfig getConfigFromGlobalXml(Context context) {
        DisplayDeviceConfig displayDeviceConfig = new DisplayDeviceConfig(context);
        displayDeviceConfig.initFromGlobalXml();
        return displayDeviceConfig;
    }

    public static DisplayDeviceConfig getConfigFromPmValues(Context context) {
        DisplayDeviceConfig displayDeviceConfig = new DisplayDeviceConfig(context);
        displayDeviceConfig.initFromDefaultValues();
        return displayDeviceConfig;
    }

    @VisibleForTesting
    public boolean initFromFile(File file) {
        if (file.exists()) {
            if (!file.isFile()) {
                Slog.e("DisplayDeviceConfig", "Display configuration is not a file: " + file + ", skipping");
                return false;
            }
            try {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
                DisplayConfiguration read = XmlParser.read(bufferedInputStream);
                if (read != null) {
                    loadName(read);
                    loadDensityMapping(read);
                    loadBrightnessDefaultFromDdcXml(read);
                    loadBrightnessConstraintsFromConfigXml();
                    loadBrightnessMap(read);
                    loadThermalThrottlingConfig(read);
                    loadHighBrightnessModeData(read);
                    loadQuirks(read);
                    loadBrightnessRamps(read);
                    loadAmbientLightSensorFromDdc(read);
                    loadScreenOffBrightnessSensorFromDdc(read);
                    loadProxSensorFromDdc(read);
                    loadAmbientHorizonFromDdc(read);
                    loadBrightnessChangeThresholds(read);
                    loadAutoBrightnessConfigValues(read);
                    loadRefreshRateSetting(read);
                    loadScreenOffBrightnessSensorValueToLuxFromDdc(read);
                    loadUsiVersion(read);
                } else {
                    Slog.w("DisplayDeviceConfig", "DisplayDeviceConfig file is null");
                }
                bufferedInputStream.close();
            } catch (IOException | DatatypeConfigurationException | XmlPullParserException e) {
                Slog.e("DisplayDeviceConfig", "Encountered an error while reading/parsing display config file: " + file, e);
            }
            this.mLoadedFrom = file.toString();
            return true;
        }
        return false;
    }

    public final void initFromGlobalXml() {
        loadBrightnessDefaultFromConfigXml();
        loadBrightnessConstraintsFromConfigXml();
        loadBrightnessMapFromConfigXml();
        loadBrightnessRampsFromConfigXml();
        loadAmbientLightSensorFromConfigXml();
        loadBrightnessChangeThresholdsFromXml();
        setProxSensorUnspecified();
        loadAutoBrightnessConfigsFromConfigXml();
        loadAutoBrightnessAvailableFromConfigXml();
        loadRefreshRateSetting(null);
        this.mLoadedFrom = "<config.xml>";
    }

    public final void initFromDefaultValues() {
        this.mLoadedFrom = "Static values";
        this.mBacklightMinimum = 0.0f;
        this.mBacklightMaximum = 1.0f;
        this.mBrightnessDefault = HDR_PERCENT_OF_SCREEN_REQUIRED_DEFAULT;
        this.mBrightnessRampFastDecrease = 1.0f;
        this.mBrightnessRampFastIncrease = 1.0f;
        this.mBrightnessRampSlowDecrease = 1.0f;
        this.mBrightnessRampSlowIncrease = 1.0f;
        this.mBrightnessRampDecreaseMaxMillis = 0L;
        this.mBrightnessRampIncreaseMaxMillis = 0L;
        setSimpleMappingStrategyValues();
        loadAmbientLightSensorFromConfigXml();
        setProxSensorUnspecified();
        loadAutoBrightnessAvailableFromConfigXml();
    }

    public final void copyUninitializedValuesFromSecondaryConfig(DisplayConfiguration displayConfiguration) {
        if (displayConfiguration != null && this.mDensityMapping == null) {
            loadDensityMapping(displayConfiguration);
        }
    }

    public final void loadName(DisplayConfiguration displayConfiguration) {
        this.mName = displayConfiguration.getName();
    }

    public final void loadDensityMapping(DisplayConfiguration displayConfiguration) {
        if (displayConfiguration.getDensityMapping() == null) {
            return;
        }
        List<Density> density = displayConfiguration.getDensityMapping().getDensity();
        DensityMapping.Entry[] entryArr = new DensityMapping.Entry[density.size()];
        for (int i = 0; i < density.size(); i++) {
            Density density2 = density.get(i);
            entryArr[i] = new DensityMapping.Entry(density2.getWidth().intValue(), density2.getHeight().intValue(), density2.getDensity().intValue());
        }
        this.mDensityMapping = DensityMapping.createByOwning(entryArr);
    }

    public final void loadBrightnessDefaultFromDdcXml(DisplayConfiguration displayConfiguration) {
        if (displayConfiguration != null) {
            BigDecimal screenBrightnessDefault = displayConfiguration.getScreenBrightnessDefault();
            if (screenBrightnessDefault != null) {
                this.mBrightnessDefault = screenBrightnessDefault.floatValue();
            } else {
                loadBrightnessDefaultFromConfigXml();
            }
        }
    }

    public final void loadBrightnessDefaultFromConfigXml() {
        float f = this.mContext.getResources().getFloat(17105107);
        if (f == -2.0f) {
            this.mBrightnessDefault = BrightnessSynchronizer.brightnessIntToFloat(this.mContext.getResources().getInteger(17694943));
        } else {
            this.mBrightnessDefault = f;
        }
    }

    public final void loadBrightnessConstraintsFromConfigXml() {
        float f = this.mContext.getResources().getFloat(17105109);
        float f2 = this.mContext.getResources().getFloat(17105108);
        if (f == -2.0f || f2 == -2.0f) {
            this.mBacklightMinimum = BrightnessSynchronizer.brightnessIntToFloat(this.mContext.getResources().getInteger(17694945));
            this.mBacklightMaximum = BrightnessSynchronizer.brightnessIntToFloat(this.mContext.getResources().getInteger(17694944));
            return;
        }
        this.mBacklightMinimum = f;
        this.mBacklightMaximum = f2;
    }

    public final void loadBrightnessMap(DisplayConfiguration displayConfiguration) {
        NitsMap screenBrightnessMap = displayConfiguration.getScreenBrightnessMap();
        if (screenBrightnessMap == null) {
            loadBrightnessMapFromConfigXml();
            return;
        }
        List<Point> point = screenBrightnessMap.getPoint();
        int size = point.size();
        float[] fArr = new float[size];
        float[] fArr2 = new float[size];
        this.mInterpolationType = convertInterpolationType(screenBrightnessMap.getInterpolation());
        int i = 0;
        for (Point point2 : point) {
            fArr[i] = point2.getNits().floatValue();
            float floatValue = point2.getValue().floatValue();
            fArr2[i] = floatValue;
            if (i > 0) {
                int i2 = i - 1;
                if (fArr[i] < fArr[i2]) {
                    Slog.e("DisplayDeviceConfig", "screenBrightnessMap must be non-decreasing, ignoring rest  of configuration. Nits: " + fArr[i] + " < " + fArr[i2]);
                    return;
                } else if (floatValue < fArr2[i2]) {
                    Slog.e("DisplayDeviceConfig", "screenBrightnessMap must be non-decreasing, ignoring rest  of configuration. Value: " + fArr2[i] + " < " + fArr2[i2]);
                    return;
                }
            }
            i++;
        }
        this.mRawNits = fArr;
        this.mRawBacklight = fArr2;
        constrainNitsAndBacklightArrays();
    }

    public final Spline loadSdrHdrRatioMap(HighBrightnessMode highBrightnessMode) {
        List<SdrHdrRatioPoint> point;
        int size;
        int i;
        SdrHdrRatioMap sdrHdrRatioMap_all = highBrightnessMode.getSdrHdrRatioMap_all();
        if (sdrHdrRatioMap_all != null && (size = (point = sdrHdrRatioMap_all.getPoint()).size()) > 0) {
            float[] fArr = new float[size];
            float[] fArr2 = new float[size];
            int i2 = 0;
            for (SdrHdrRatioPoint sdrHdrRatioPoint : point) {
                float floatValue = sdrHdrRatioPoint.getSdrNits().floatValue();
                fArr[i2] = floatValue;
                if (i2 > 0) {
                    if (floatValue < fArr[i2 - 1]) {
                        Slog.e("DisplayDeviceConfig", "sdrHdrRatioMap must be non-decreasing, ignoring rest  of configuration. nits: " + fArr[i2] + " < " + fArr[i]);
                        return null;
                    }
                }
                fArr2[i2] = sdrHdrRatioPoint.getHdrRatio().floatValue();
                i2++;
            }
            return Spline.createSpline(fArr, fArr2);
        }
        return null;
    }

    public final void loadThermalThrottlingConfig(DisplayConfiguration displayConfiguration) {
        ThermalThrottling thermalThrottling = displayConfiguration.getThermalThrottling();
        if (thermalThrottling == null) {
            Slog.i("DisplayDeviceConfig", "No thermal throttling config found");
            return;
        }
        loadBrightnessThrottlingMaps(thermalThrottling);
        loadRefreshRateThermalThrottlingMap(thermalThrottling);
    }

    public final void loadBrightnessThrottlingMaps(ThermalThrottling thermalThrottling) {
        boolean z;
        List<BrightnessThrottlingMap> brightnessThrottlingMap = thermalThrottling.getBrightnessThrottlingMap();
        if (brightnessThrottlingMap == null || brightnessThrottlingMap.isEmpty()) {
            Slog.i("DisplayDeviceConfig", "No brightness throttling map found");
            return;
        }
        for (BrightnessThrottlingMap brightnessThrottlingMap2 : brightnessThrottlingMap) {
            List<BrightnessThrottlingPoint> brightnessThrottlingPoint = brightnessThrottlingMap2.getBrightnessThrottlingPoint();
            ArrayList arrayList = new ArrayList(brightnessThrottlingPoint.size());
            Iterator<BrightnessThrottlingPoint> it = brightnessThrottlingPoint.iterator();
            while (true) {
                if (!it.hasNext()) {
                    z = false;
                    break;
                }
                BrightnessThrottlingPoint next = it.next();
                ThermalStatus thermalStatus = next.getThermalStatus();
                if (!thermalStatusIsValid(thermalStatus)) {
                    z = true;
                    break;
                }
                arrayList.add(new BrightnessThrottlingData.ThrottlingLevel(convertThermalStatus(thermalStatus), next.getBrightness().floatValue()));
            }
            if (!z) {
                String id = brightnessThrottlingMap2.getId() == null ? "default" : brightnessThrottlingMap2.getId();
                if (this.mBrightnessThrottlingDataMap.containsKey(id)) {
                    throw new RuntimeException("Brightness throttling data with ID " + id + " already exists");
                }
                this.mBrightnessThrottlingDataMap.put(id, BrightnessThrottlingData.create(arrayList));
            }
        }
    }

    public final void loadRefreshRateThermalThrottlingMap(ThermalThrottling thermalThrottling) {
        List<RefreshRateThrottlingMap> refreshRateThrottlingMap = thermalThrottling.getRefreshRateThrottlingMap();
        if (refreshRateThrottlingMap == null || refreshRateThrottlingMap.isEmpty()) {
            Slog.w("DisplayDeviceConfig", "RefreshRateThrottling: map not found");
            return;
        }
        for (RefreshRateThrottlingMap refreshRateThrottlingMap2 : refreshRateThrottlingMap) {
            List<RefreshRateThrottlingPoint> refreshRateThrottlingPoint = refreshRateThrottlingMap2.getRefreshRateThrottlingPoint();
            String id = refreshRateThrottlingMap2.getId() == null ? "default" : refreshRateThrottlingMap2.getId();
            if (refreshRateThrottlingPoint == null || refreshRateThrottlingPoint.isEmpty()) {
                Slog.w("DisplayDeviceConfig", "RefreshRateThrottling: points not found for mapId=" + id);
            } else if (this.mRefreshRateThrottlingMap.containsKey(id)) {
                Slog.wtf("DisplayDeviceConfig", "RefreshRateThrottling: map already exists, mapId=" + id);
            } else {
                SparseArray<SurfaceControl.RefreshRateRange> sparseArray = new SparseArray<>();
                for (RefreshRateThrottlingPoint refreshRateThrottlingPoint2 : refreshRateThrottlingPoint) {
                    ThermalStatus thermalStatus = refreshRateThrottlingPoint2.getThermalStatus();
                    if (!thermalStatusIsValid(thermalStatus)) {
                        Slog.wtf("DisplayDeviceConfig", "RefreshRateThrottling: Invalid thermalStatus=" + thermalStatus.getRawName() + ",mapId=" + id);
                    } else {
                        int convertThermalStatus = convertThermalStatus(thermalStatus);
                        if (sparseArray.contains(convertThermalStatus)) {
                            Slog.wtf("DisplayDeviceConfig", "RefreshRateThrottling: thermalStatus=" + thermalStatus.getRawName() + " is already in the map, mapId=" + id);
                        } else {
                            sparseArray.put(convertThermalStatus, new SurfaceControl.RefreshRateRange(refreshRateThrottlingPoint2.getRefreshRateRange().getMinimum().floatValue(), refreshRateThrottlingPoint2.getRefreshRateRange().getMaximum().floatValue()));
                        }
                    }
                }
                if (sparseArray.size() == 0) {
                    Slog.w("DisplayDeviceConfig", "RefreshRateThrottling: no valid throttling points fond for map, mapId=" + id);
                } else {
                    this.mRefreshRateThrottlingMap.put(id, sparseArray);
                }
            }
        }
    }

    public final void loadRefreshRateSetting(DisplayConfiguration displayConfiguration) {
        RefreshRateConfigs refreshRate = displayConfiguration == null ? null : displayConfiguration.getRefreshRate();
        BlockingZoneConfig lowerBlockingZoneConfigs = refreshRate == null ? null : refreshRate.getLowerBlockingZoneConfigs();
        BlockingZoneConfig higherBlockingZoneConfigs = refreshRate != null ? refreshRate.getHigherBlockingZoneConfigs() : null;
        loadPeakDefaultRefreshRate(refreshRate);
        loadDefaultRefreshRate(refreshRate);
        loadDefaultRefreshRateInHbm(refreshRate);
        loadLowerRefreshRateBlockingZones(lowerBlockingZoneConfigs);
        loadHigherRefreshRateBlockingZones(higherBlockingZoneConfigs);
        loadRefreshRateZoneProfiles(refreshRate);
    }

    public final void loadPeakDefaultRefreshRate(RefreshRateConfigs refreshRateConfigs) {
        if (refreshRateConfigs == null || refreshRateConfigs.getDefaultPeakRefreshRate() == null) {
            this.mDefaultPeakRefreshRate = this.mContext.getResources().getInteger(17694800);
        } else {
            this.mDefaultPeakRefreshRate = refreshRateConfigs.getDefaultPeakRefreshRate().intValue();
        }
    }

    public final void loadDefaultRefreshRate(RefreshRateConfigs refreshRateConfigs) {
        if (refreshRateConfigs == null || refreshRateConfigs.getDefaultRefreshRate() == null) {
            this.mDefaultRefreshRate = this.mContext.getResources().getInteger(17694802);
        } else {
            this.mDefaultRefreshRate = refreshRateConfigs.getDefaultRefreshRate().intValue();
        }
    }

    public final void loadRefreshRateZoneProfiles(RefreshRateConfigs refreshRateConfigs) {
        if (refreshRateConfigs == null) {
            return;
        }
        for (RefreshRateZone refreshRateZone : refreshRateConfigs.getRefreshRateZoneProfiles().getRefreshRateZoneProfile()) {
            RefreshRateRange refreshRateRange = refreshRateZone.getRefreshRateRange();
            this.mRefreshRateZoneProfiles.put(refreshRateZone.getId(), new SurfaceControl.RefreshRateRange(refreshRateRange.getMinimum().floatValue(), refreshRateRange.getMaximum().floatValue()));
        }
    }

    public final void loadDefaultRefreshRateInHbm(RefreshRateConfigs refreshRateConfigs) {
        if (refreshRateConfigs != null && refreshRateConfigs.getDefaultRefreshRateInHbmHdr() != null) {
            this.mDefaultRefreshRateInHbmHdr = refreshRateConfigs.getDefaultRefreshRateInHbmHdr().intValue();
        } else {
            this.mDefaultRefreshRateInHbmHdr = this.mContext.getResources().getInteger(17694803);
        }
        if (refreshRateConfigs != null && refreshRateConfigs.getDefaultRefreshRateInHbmSunlight() != null) {
            this.mDefaultRefreshRateInHbmSunlight = refreshRateConfigs.getDefaultRefreshRateInHbmSunlight().intValue();
        } else {
            this.mDefaultRefreshRateInHbmSunlight = this.mContext.getResources().getInteger(17694804);
        }
    }

    public final void loadLowerRefreshRateBlockingZones(BlockingZoneConfig blockingZoneConfig) {
        loadLowerBlockingZoneDefaultRefreshRate(blockingZoneConfig);
        loadLowerBrightnessThresholds(blockingZoneConfig);
    }

    public final void loadHigherRefreshRateBlockingZones(BlockingZoneConfig blockingZoneConfig) {
        loadHigherBlockingZoneDefaultRefreshRate(blockingZoneConfig);
        loadHigherBrightnessThresholds(blockingZoneConfig);
    }

    public final void loadHigherBlockingZoneDefaultRefreshRate(BlockingZoneConfig blockingZoneConfig) {
        if (blockingZoneConfig == null) {
            this.mDefaultHighBlockingZoneRefreshRate = this.mContext.getResources().getInteger(17694846);
        } else {
            this.mDefaultHighBlockingZoneRefreshRate = blockingZoneConfig.getDefaultRefreshRate().intValue();
        }
    }

    public final void loadLowerBlockingZoneDefaultRefreshRate(BlockingZoneConfig blockingZoneConfig) {
        if (blockingZoneConfig == null) {
            this.mDefaultLowBlockingZoneRefreshRate = this.mContext.getResources().getInteger(17694805);
        } else {
            this.mDefaultLowBlockingZoneRefreshRate = blockingZoneConfig.getDefaultRefreshRate().intValue();
        }
    }

    public final void loadLowerBrightnessThresholds(BlockingZoneConfig blockingZoneConfig) {
        if (blockingZoneConfig == null) {
            this.mLowDisplayBrightnessThresholds = this.mContext.getResources().getIntArray(17236005);
            int[] intArray = this.mContext.getResources().getIntArray(17235985);
            this.mLowAmbientBrightnessThresholds = intArray;
            int[] iArr = this.mLowDisplayBrightnessThresholds;
            if (iArr == null || intArray == null || iArr.length != intArray.length) {
                throw new RuntimeException("display low brightness threshold array and ambient brightness threshold array have different length: mLowDisplayBrightnessThresholds=" + Arrays.toString(this.mLowDisplayBrightnessThresholds) + ", mLowAmbientBrightnessThresholds=" + Arrays.toString(this.mLowAmbientBrightnessThresholds));
            }
            return;
        }
        List<DisplayBrightnessPoint> displayBrightnessPoint = blockingZoneConfig.getBlockingZoneThreshold().getDisplayBrightnessPoint();
        int size = displayBrightnessPoint.size();
        this.mLowDisplayBrightnessThresholds = new int[size];
        this.mLowAmbientBrightnessThresholds = new int[size];
        for (int i = 0; i < size; i++) {
            this.mLowDisplayBrightnessThresholds[i] = (int) displayBrightnessPoint.get(i).getNits().floatValue();
            this.mLowAmbientBrightnessThresholds[i] = displayBrightnessPoint.get(i).getLux().intValue();
        }
    }

    public final void loadHigherBrightnessThresholds(BlockingZoneConfig blockingZoneConfig) {
        int[] iArr;
        if (blockingZoneConfig == null) {
            this.mHighDisplayBrightnessThresholds = this.mContext.getResources().getIntArray(17236082);
            int[] intArray = this.mContext.getResources().getIntArray(17236081);
            this.mHighAmbientBrightnessThresholds = intArray;
            if (intArray == null || (iArr = this.mHighDisplayBrightnessThresholds) == null || intArray.length != iArr.length) {
                throw new RuntimeException("display high brightness threshold array and ambient brightness threshold array have different length: mHighDisplayBrightnessThresholds=" + Arrays.toString(this.mHighDisplayBrightnessThresholds) + ", mHighAmbientBrightnessThresholds=" + Arrays.toString(this.mHighAmbientBrightnessThresholds));
            }
            return;
        }
        List<DisplayBrightnessPoint> displayBrightnessPoint = blockingZoneConfig.getBlockingZoneThreshold().getDisplayBrightnessPoint();
        int size = displayBrightnessPoint.size();
        this.mHighDisplayBrightnessThresholds = new int[size];
        this.mHighAmbientBrightnessThresholds = new int[size];
        for (int i = 0; i < size; i++) {
            this.mHighDisplayBrightnessThresholds[i] = (int) displayBrightnessPoint.get(i).getNits().floatValue();
            this.mHighAmbientBrightnessThresholds[i] = displayBrightnessPoint.get(i).getLux().intValue();
        }
    }

    public final void loadAutoBrightnessConfigValues(DisplayConfiguration displayConfiguration) {
        AutoBrightness autoBrightness = displayConfiguration.getAutoBrightness();
        loadAutoBrightnessBrighteningLightDebounce(autoBrightness);
        loadAutoBrightnessDarkeningLightDebounce(autoBrightness);
        loadAutoBrightnessDisplayBrightnessMapping(autoBrightness);
        loadEnableAutoBrightness(autoBrightness);
    }

    public final void loadAutoBrightnessBrighteningLightDebounce(AutoBrightness autoBrightness) {
        if (autoBrightness == null || autoBrightness.getBrighteningLightDebounceMillis() == null) {
            this.mAutoBrightnessBrighteningLightDebounce = this.mContext.getResources().getInteger(17694744);
        } else {
            this.mAutoBrightnessBrighteningLightDebounce = autoBrightness.getBrighteningLightDebounceMillis().intValue();
        }
    }

    public final void loadAutoBrightnessDarkeningLightDebounce(AutoBrightness autoBrightness) {
        if (autoBrightness == null || autoBrightness.getDarkeningLightDebounceMillis() == null) {
            this.mAutoBrightnessDarkeningLightDebounce = this.mContext.getResources().getInteger(17694745);
        } else {
            this.mAutoBrightnessDarkeningLightDebounce = autoBrightness.getDarkeningLightDebounceMillis().intValue();
        }
    }

    public final void loadAutoBrightnessDisplayBrightnessMapping(AutoBrightness autoBrightness) {
        if (autoBrightness == null || autoBrightness.getDisplayBrightnessMapping() == null) {
            this.mBrightnessLevelsNits = getFloatArray(this.mContext.getResources().obtainTypedArray(17235987), -1.0f);
            this.mBrightnessLevelsLux = getLuxLevels(this.mContext.getResources().getIntArray(17235992));
            return;
        }
        int size = autoBrightness.getDisplayBrightnessMapping().getDisplayBrightnessPoint().size();
        this.mBrightnessLevelsNits = new float[size];
        this.mBrightnessLevelsLux = new float[size + 1];
        int i = 0;
        while (i < size) {
            this.mBrightnessLevelsNits[i] = autoBrightness.getDisplayBrightnessMapping().getDisplayBrightnessPoint().get(i).getNits().floatValue();
            int i2 = i + 1;
            this.mBrightnessLevelsLux[i2] = autoBrightness.getDisplayBrightnessMapping().getDisplayBrightnessPoint().get(i).getLux().floatValue();
            i = i2;
        }
    }

    public final void loadAutoBrightnessAvailableFromConfigXml() {
        this.mAutoBrightnessAvailable = this.mContext.getResources().getBoolean(17891379);
    }

    public final void loadBrightnessMapFromConfigXml() {
        Resources resources = this.mContext.getResources();
        float[] floatArray = BrightnessMappingStrategy.getFloatArray(resources.obtainTypedArray(17236132));
        int[] intArray = resources.getIntArray(17236131);
        int length = intArray.length;
        float[] fArr = new float[length];
        for (int i = 0; i < intArray.length; i++) {
            fArr[i] = BrightnessSynchronizer.brightnessIntToFloat(intArray[i]);
        }
        if (length == 0 || floatArray.length == 0) {
            setSimpleMappingStrategyValues();
            return;
        }
        this.mRawNits = floatArray;
        this.mRawBacklight = fArr;
        constrainNitsAndBacklightArrays();
    }

    public final void setSimpleMappingStrategyValues() {
        this.mNits = null;
        this.mBacklight = null;
        float[] fArr = {0.0f, 1.0f};
        this.mBrightnessToBacklightSpline = Spline.createSpline(fArr, fArr);
        this.mBacklightToBrightnessSpline = Spline.createSpline(fArr, fArr);
    }

    public final void constrainNitsAndBacklightArrays() {
        float f;
        float[] fArr = this.mRawBacklight;
        float f2 = fArr[0];
        float f3 = this.mBacklightMinimum;
        if (f2 <= f3) {
            float f4 = fArr[fArr.length - 1];
            float f5 = this.mBacklightMaximum;
            if (f4 >= f5 && f3 <= f5) {
                float[] fArr2 = new float[fArr.length];
                float[] fArr3 = new float[fArr.length];
                int i = 0;
                while (true) {
                    float[] fArr4 = this.mRawBacklight;
                    if (i >= fArr4.length - 1) {
                        i = 0;
                        break;
                    }
                    int i2 = i + 1;
                    if (fArr4[i2] > this.mBacklightMinimum) {
                        break;
                    }
                    i = i2;
                }
                boolean z = false;
                int i3 = 0;
                int i4 = i;
                while (true) {
                    float[] fArr5 = this.mRawBacklight;
                    if (i4 >= fArr5.length || z) {
                        break;
                    }
                    i3 = i4 - i;
                    float f6 = fArr5[i4];
                    float f7 = this.mBacklightMaximum;
                    boolean z2 = f6 >= f7 || i4 >= fArr5.length - 1;
                    if (i3 == 0) {
                        f6 = MathUtils.max(f6, this.mBacklightMinimum);
                        f = rawBacklightToNits(i4, f6);
                    } else if (z2) {
                        f6 = MathUtils.min(f6, f7);
                        f = rawBacklightToNits(i4 - 1, f6);
                    } else {
                        f = this.mRawNits[i4];
                    }
                    fArr3[i3] = f6;
                    fArr2[i3] = f;
                    i4++;
                    z = z2;
                }
                int i5 = i3 + 1;
                this.mBacklight = Arrays.copyOf(fArr3, i5);
                this.mNits = Arrays.copyOf(fArr2, i5);
                createBacklightConversionSplines();
                return;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Min or max values are invalid; raw min=");
        sb.append(this.mRawBacklight[0]);
        sb.append("; raw max=");
        float[] fArr6 = this.mRawBacklight;
        sb.append(fArr6[fArr6.length - 1]);
        sb.append("; backlight min=");
        sb.append(this.mBacklightMinimum);
        sb.append("; backlight max=");
        sb.append(this.mBacklightMaximum);
        throw new IllegalStateException(sb.toString());
    }

    public final float rawBacklightToNits(int i, float f) {
        float[] fArr = this.mRawBacklight;
        float f2 = fArr[i];
        int i2 = i + 1;
        float f3 = fArr[i2];
        float[] fArr2 = this.mRawNits;
        return MathUtils.map(f2, f3, fArr2[i], fArr2[i2], f);
    }

    public final void createBacklightConversionSplines() {
        float[] fArr;
        Spline createSpline;
        Spline createSpline2;
        Spline createSpline3;
        Spline createSpline4;
        this.mBrightness = new float[this.mBacklight.length];
        int i = 0;
        while (true) {
            fArr = this.mBrightness;
            if (i >= fArr.length) {
                break;
            }
            float[] fArr2 = this.mBacklight;
            fArr[i] = MathUtils.map(fArr2[0], fArr2[fArr2.length - 1], 0.0f, 1.0f, fArr2[i]);
            i++;
        }
        if (this.mInterpolationType == 1) {
            createSpline = Spline.createLinearSpline(fArr, this.mBacklight);
        } else {
            createSpline = Spline.createSpline(fArr, this.mBacklight);
        }
        this.mBrightnessToBacklightSpline = createSpline;
        if (this.mInterpolationType == 1) {
            createSpline2 = Spline.createLinearSpline(this.mBacklight, this.mBrightness);
        } else {
            createSpline2 = Spline.createSpline(this.mBacklight, this.mBrightness);
        }
        this.mBacklightToBrightnessSpline = createSpline2;
        if (this.mInterpolationType == 1) {
            createSpline3 = Spline.createLinearSpline(this.mBacklight, this.mNits);
        } else {
            createSpline3 = Spline.createSpline(this.mBacklight, this.mNits);
        }
        this.mBacklightToNitsSpline = createSpline3;
        if (this.mInterpolationType == 1) {
            createSpline4 = Spline.createLinearSpline(this.mNits, this.mBacklight);
        } else {
            createSpline4 = Spline.createSpline(this.mNits, this.mBacklight);
        }
        this.mNitsToBacklightSpline = createSpline4;
    }

    public final void loadQuirks(DisplayConfiguration displayConfiguration) {
        DisplayQuirks quirks = displayConfiguration.getQuirks();
        if (quirks != null) {
            this.mQuirks = new ArrayList(quirks.getQuirk());
        }
    }

    public final void loadHighBrightnessModeData(DisplayConfiguration displayConfiguration) {
        HighBrightnessMode highBrightnessMode = displayConfiguration.getHighBrightnessMode();
        if (highBrightnessMode != null) {
            this.mIsHighBrightnessModeEnabled = highBrightnessMode.getEnabled();
            HighBrightnessModeData highBrightnessModeData = new HighBrightnessModeData();
            this.mHbmData = highBrightnessModeData;
            highBrightnessModeData.minimumLux = highBrightnessMode.getMinimumLux_all().floatValue();
            float floatValue = highBrightnessMode.getTransitionPoint_all().floatValue();
            if (floatValue >= this.mBacklightMaximum) {
                throw new IllegalArgumentException("HBM transition point invalid. " + this.mHbmData.transitionPoint + " is not less than " + this.mBacklightMaximum);
            }
            this.mHbmData.transitionPoint = this.mBacklightToBrightnessSpline.interpolate(floatValue);
            HbmTiming timing_all = highBrightnessMode.getTiming_all();
            this.mHbmData.timeWindowMillis = timing_all.getTimeWindowSecs_all().longValue() * 1000;
            this.mHbmData.timeMaxMillis = timing_all.getTimeMaxSecs_all().longValue() * 1000;
            this.mHbmData.timeMinMillis = timing_all.getTimeMinSecs_all().longValue() * 1000;
            this.mHbmData.thermalStatusLimit = convertThermalStatus(highBrightnessMode.getThermalStatusLimit_all());
            this.mHbmData.allowInLowPowerMode = highBrightnessMode.getAllowInLowPowerMode_all();
            RefreshRateRange refreshRate_all = highBrightnessMode.getRefreshRate_all();
            if (refreshRate_all != null) {
                this.mRefreshRateLimitations.add(new DisplayManagerInternal.RefreshRateLimitation(1, refreshRate_all.getMinimum().floatValue(), refreshRate_all.getMaximum().floatValue()));
            }
            BigDecimal minimumHdrPercentOfScreen_all = highBrightnessMode.getMinimumHdrPercentOfScreen_all();
            if (minimumHdrPercentOfScreen_all != null) {
                this.mHbmData.minimumHdrPercentOfScreen = minimumHdrPercentOfScreen_all.floatValue();
                float f = this.mHbmData.minimumHdrPercentOfScreen;
                if (f > 1.0f || f < 0.0f) {
                    Slog.w("DisplayDeviceConfig", "Invalid minimum HDR percent of screen: " + String.valueOf(this.mHbmData.minimumHdrPercentOfScreen));
                    this.mHbmData.minimumHdrPercentOfScreen = HDR_PERCENT_OF_SCREEN_REQUIRED_DEFAULT;
                }
            } else {
                this.mHbmData.minimumHdrPercentOfScreen = HDR_PERCENT_OF_SCREEN_REQUIRED_DEFAULT;
            }
            this.mSdrToHdrRatioSpline = loadSdrHdrRatioMap(highBrightnessMode);
        }
    }

    public final void loadBrightnessRamps(DisplayConfiguration displayConfiguration) {
        BigDecimal screenBrightnessRampFastDecrease = displayConfiguration.getScreenBrightnessRampFastDecrease();
        BigDecimal screenBrightnessRampFastIncrease = displayConfiguration.getScreenBrightnessRampFastIncrease();
        BigDecimal screenBrightnessRampSlowDecrease = displayConfiguration.getScreenBrightnessRampSlowDecrease();
        BigDecimal screenBrightnessRampSlowIncrease = displayConfiguration.getScreenBrightnessRampSlowIncrease();
        if (screenBrightnessRampFastDecrease != null && screenBrightnessRampFastIncrease != null && screenBrightnessRampSlowDecrease != null && screenBrightnessRampSlowIncrease != null) {
            this.mBrightnessRampFastDecrease = screenBrightnessRampFastDecrease.floatValue();
            this.mBrightnessRampFastIncrease = screenBrightnessRampFastIncrease.floatValue();
            this.mBrightnessRampSlowDecrease = screenBrightnessRampSlowDecrease.floatValue();
            this.mBrightnessRampSlowIncrease = screenBrightnessRampSlowIncrease.floatValue();
        } else {
            if (screenBrightnessRampFastDecrease != null || screenBrightnessRampFastIncrease != null || screenBrightnessRampSlowDecrease != null || screenBrightnessRampSlowIncrease != null) {
                Slog.w("DisplayDeviceConfig", "Per display brightness ramp values ignored because not all values are present in display device config");
            }
            loadBrightnessRampsFromConfigXml();
        }
        BigInteger screenBrightnessRampIncreaseMaxMillis = displayConfiguration.getScreenBrightnessRampIncreaseMaxMillis();
        if (screenBrightnessRampIncreaseMaxMillis != null) {
            this.mBrightnessRampIncreaseMaxMillis = screenBrightnessRampIncreaseMaxMillis.intValue();
        }
        BigInteger screenBrightnessRampDecreaseMaxMillis = displayConfiguration.getScreenBrightnessRampDecreaseMaxMillis();
        if (screenBrightnessRampDecreaseMaxMillis != null) {
            this.mBrightnessRampDecreaseMaxMillis = screenBrightnessRampDecreaseMaxMillis.intValue();
        }
    }

    public final void loadBrightnessRampsFromConfigXml() {
        this.mBrightnessRampFastIncrease = BrightnessSynchronizer.brightnessIntToFloat(this.mContext.getResources().getInteger(17694763));
        float brightnessIntToFloat = BrightnessSynchronizer.brightnessIntToFloat(this.mContext.getResources().getInteger(17694764));
        this.mBrightnessRampSlowIncrease = brightnessIntToFloat;
        this.mBrightnessRampFastDecrease = this.mBrightnessRampFastIncrease;
        this.mBrightnessRampSlowDecrease = brightnessIntToFloat;
    }

    public final void loadAmbientLightSensorFromConfigXml() {
        SensorData sensorData = this.mAmbientLightSensor;
        sensorData.name = "";
        sensorData.type = this.mContext.getResources().getString(17039920);
    }

    public final void loadAutoBrightnessConfigsFromConfigXml() {
        loadAutoBrightnessDisplayBrightnessMapping(null);
    }

    public final void loadAmbientLightSensorFromDdc(DisplayConfiguration displayConfiguration) {
        SensorDetails lightSensor = displayConfiguration.getLightSensor();
        if (lightSensor != null) {
            this.mAmbientLightSensor.type = lightSensor.getType();
            this.mAmbientLightSensor.name = lightSensor.getName();
            RefreshRateRange refreshRate = lightSensor.getRefreshRate();
            if (refreshRate != null) {
                this.mAmbientLightSensor.minRefreshRate = refreshRate.getMinimum().floatValue();
                this.mAmbientLightSensor.maxRefreshRate = refreshRate.getMaximum().floatValue();
                return;
            }
            return;
        }
        loadAmbientLightSensorFromConfigXml();
    }

    public final void setProxSensorUnspecified() {
        SensorData sensorData = this.mProximitySensor;
        sensorData.name = null;
        sensorData.type = null;
    }

    public final void loadScreenOffBrightnessSensorFromDdc(DisplayConfiguration displayConfiguration) {
        SensorDetails screenOffBrightnessSensor = displayConfiguration.getScreenOffBrightnessSensor();
        if (screenOffBrightnessSensor != null) {
            this.mScreenOffBrightnessSensor.type = screenOffBrightnessSensor.getType();
            this.mScreenOffBrightnessSensor.name = screenOffBrightnessSensor.getName();
        }
    }

    public final void loadProxSensorFromDdc(DisplayConfiguration displayConfiguration) {
        SensorDetails proxSensor = displayConfiguration.getProxSensor();
        if (proxSensor != null) {
            this.mProximitySensor.name = proxSensor.getName();
            this.mProximitySensor.type = proxSensor.getType();
            RefreshRateRange refreshRate = proxSensor.getRefreshRate();
            if (refreshRate != null) {
                this.mProximitySensor.minRefreshRate = refreshRate.getMinimum().floatValue();
                this.mProximitySensor.maxRefreshRate = refreshRate.getMaximum().floatValue();
                return;
            }
            return;
        }
        setProxSensorUnspecified();
    }

    public final void loadBrightnessChangeThresholdsFromXml() {
        loadBrightnessChangeThresholds(null);
    }

    public final void loadBrightnessChangeThresholds(DisplayConfiguration displayConfiguration) {
        loadDisplayBrightnessThresholds(displayConfiguration);
        loadAmbientBrightnessThresholds(displayConfiguration);
        loadDisplayBrightnessThresholdsIdle(displayConfiguration);
        loadAmbientBrightnessThresholdsIdle(displayConfiguration);
    }

    public final void loadDisplayBrightnessThresholds(DisplayConfiguration displayConfiguration) {
        BrightnessThresholds brightnessThresholds;
        BrightnessThresholds brightnessThresholds2;
        if (displayConfiguration == null || displayConfiguration.getDisplayBrightnessChangeThresholds() == null) {
            brightnessThresholds = null;
            brightnessThresholds2 = null;
        } else {
            BrightnessThresholds brighteningThresholds = displayConfiguration.getDisplayBrightnessChangeThresholds().getBrighteningThresholds();
            brightnessThresholds = displayConfiguration.getDisplayBrightnessChangeThresholds().getDarkeningThresholds();
            brightnessThresholds2 = brighteningThresholds;
        }
        float[] fArr = DEFAULT_SCREEN_THRESHOLD_LEVELS;
        Pair<float[], float[]> brightnessLevelAndPercentage = getBrightnessLevelAndPercentage(brightnessThresholds2, 17236134, 17236130, fArr, DEFAULT_SCREEN_BRIGHTENING_THRESHOLDS, true);
        this.mScreenBrighteningLevels = (float[]) brightnessLevelAndPercentage.first;
        this.mScreenBrighteningPercentages = (float[]) brightnessLevelAndPercentage.second;
        Pair<float[], float[]> brightnessLevelAndPercentage2 = getBrightnessLevelAndPercentage(brightnessThresholds, 17236134, 17236133, fArr, DEFAULT_SCREEN_DARKENING_THRESHOLDS, true);
        this.mScreenDarkeningLevels = (float[]) brightnessLevelAndPercentage2.first;
        this.mScreenDarkeningPercentages = (float[]) brightnessLevelAndPercentage2.second;
        if (brightnessThresholds2 != null && brightnessThresholds2.getMinimum() != null) {
            this.mScreenBrighteningMinThreshold = brightnessThresholds2.getMinimum().floatValue();
        }
        if (brightnessThresholds == null || brightnessThresholds.getMinimum() == null) {
            return;
        }
        this.mScreenDarkeningMinThreshold = brightnessThresholds.getMinimum().floatValue();
    }

    public final void loadAmbientBrightnessThresholds(DisplayConfiguration displayConfiguration) {
        BrightnessThresholds brightnessThresholds;
        BrightnessThresholds brightnessThresholds2;
        if (displayConfiguration == null || displayConfiguration.getAmbientBrightnessChangeThresholds() == null) {
            brightnessThresholds = null;
            brightnessThresholds2 = null;
        } else {
            BrightnessThresholds brighteningThresholds = displayConfiguration.getAmbientBrightnessChangeThresholds().getBrighteningThresholds();
            brightnessThresholds = displayConfiguration.getAmbientBrightnessChangeThresholds().getDarkeningThresholds();
            brightnessThresholds2 = brighteningThresholds;
        }
        float[] fArr = DEFAULT_AMBIENT_THRESHOLD_LEVELS;
        Pair<float[], float[]> brightnessLevelAndPercentage = getBrightnessLevelAndPercentage(brightnessThresholds2, 17235984, 17235982, fArr, DEFAULT_AMBIENT_BRIGHTENING_THRESHOLDS);
        this.mAmbientBrighteningLevels = (float[]) brightnessLevelAndPercentage.first;
        this.mAmbientBrighteningPercentages = (float[]) brightnessLevelAndPercentage.second;
        Pair<float[], float[]> brightnessLevelAndPercentage2 = getBrightnessLevelAndPercentage(brightnessThresholds, 17235984, 17235983, fArr, DEFAULT_AMBIENT_DARKENING_THRESHOLDS);
        this.mAmbientDarkeningLevels = (float[]) brightnessLevelAndPercentage2.first;
        this.mAmbientDarkeningPercentages = (float[]) brightnessLevelAndPercentage2.second;
        if (brightnessThresholds2 != null && brightnessThresholds2.getMinimum() != null) {
            this.mAmbientLuxBrighteningMinThreshold = brightnessThresholds2.getMinimum().floatValue();
        }
        if (brightnessThresholds == null || brightnessThresholds.getMinimum() == null) {
            return;
        }
        this.mAmbientLuxDarkeningMinThreshold = brightnessThresholds.getMinimum().floatValue();
    }

    public final void loadDisplayBrightnessThresholdsIdle(DisplayConfiguration displayConfiguration) {
        BrightnessThresholds brightnessThresholds;
        BrightnessThresholds brightnessThresholds2;
        if (displayConfiguration == null || displayConfiguration.getDisplayBrightnessChangeThresholdsIdle() == null) {
            brightnessThresholds = null;
            brightnessThresholds2 = null;
        } else {
            BrightnessThresholds brighteningThresholds = displayConfiguration.getDisplayBrightnessChangeThresholdsIdle().getBrighteningThresholds();
            brightnessThresholds = displayConfiguration.getDisplayBrightnessChangeThresholdsIdle().getDarkeningThresholds();
            brightnessThresholds2 = brighteningThresholds;
        }
        float[] fArr = DEFAULT_SCREEN_THRESHOLD_LEVELS;
        Pair<float[], float[]> brightnessLevelAndPercentage = getBrightnessLevelAndPercentage(brightnessThresholds2, 17236134, 17236130, fArr, DEFAULT_SCREEN_BRIGHTENING_THRESHOLDS, true);
        this.mScreenBrighteningLevelsIdle = (float[]) brightnessLevelAndPercentage.first;
        this.mScreenBrighteningPercentagesIdle = (float[]) brightnessLevelAndPercentage.second;
        Pair<float[], float[]> brightnessLevelAndPercentage2 = getBrightnessLevelAndPercentage(brightnessThresholds, 17236134, 17236133, fArr, DEFAULT_SCREEN_DARKENING_THRESHOLDS, true);
        this.mScreenDarkeningLevelsIdle = (float[]) brightnessLevelAndPercentage2.first;
        this.mScreenDarkeningPercentagesIdle = (float[]) brightnessLevelAndPercentage2.second;
        if (brightnessThresholds2 != null && brightnessThresholds2.getMinimum() != null) {
            this.mScreenBrighteningMinThresholdIdle = brightnessThresholds2.getMinimum().floatValue();
        }
        if (brightnessThresholds == null || brightnessThresholds.getMinimum() == null) {
            return;
        }
        this.mScreenDarkeningMinThresholdIdle = brightnessThresholds.getMinimum().floatValue();
    }

    public final void loadAmbientBrightnessThresholdsIdle(DisplayConfiguration displayConfiguration) {
        BrightnessThresholds brightnessThresholds;
        BrightnessThresholds brightnessThresholds2;
        if (displayConfiguration == null || displayConfiguration.getAmbientBrightnessChangeThresholdsIdle() == null) {
            brightnessThresholds = null;
            brightnessThresholds2 = null;
        } else {
            BrightnessThresholds brighteningThresholds = displayConfiguration.getAmbientBrightnessChangeThresholdsIdle().getBrighteningThresholds();
            brightnessThresholds = displayConfiguration.getAmbientBrightnessChangeThresholdsIdle().getDarkeningThresholds();
            brightnessThresholds2 = brighteningThresholds;
        }
        float[] fArr = DEFAULT_AMBIENT_THRESHOLD_LEVELS;
        Pair<float[], float[]> brightnessLevelAndPercentage = getBrightnessLevelAndPercentage(brightnessThresholds2, 17235984, 17235982, fArr, DEFAULT_AMBIENT_BRIGHTENING_THRESHOLDS);
        this.mAmbientBrighteningLevelsIdle = (float[]) brightnessLevelAndPercentage.first;
        this.mAmbientBrighteningPercentagesIdle = (float[]) brightnessLevelAndPercentage.second;
        Pair<float[], float[]> brightnessLevelAndPercentage2 = getBrightnessLevelAndPercentage(brightnessThresholds, 17235984, 17235983, fArr, DEFAULT_AMBIENT_DARKENING_THRESHOLDS);
        this.mAmbientDarkeningLevelsIdle = (float[]) brightnessLevelAndPercentage2.first;
        this.mAmbientDarkeningPercentagesIdle = (float[]) brightnessLevelAndPercentage2.second;
        if (brightnessThresholds2 != null && brightnessThresholds2.getMinimum() != null) {
            this.mAmbientLuxBrighteningMinThresholdIdle = brightnessThresholds2.getMinimum().floatValue();
        }
        if (brightnessThresholds == null || brightnessThresholds.getMinimum() == null) {
            return;
        }
        this.mAmbientLuxDarkeningMinThresholdIdle = brightnessThresholds.getMinimum().floatValue();
    }

    public final Pair<float[], float[]> getBrightnessLevelAndPercentage(BrightnessThresholds brightnessThresholds, int i, int i2, float[] fArr, float[] fArr2) {
        return getBrightnessLevelAndPercentage(brightnessThresholds, i, i2, fArr, fArr2, false);
    }

    public final Pair<float[], float[]> getBrightnessLevelAndPercentage(BrightnessThresholds brightnessThresholds, int i, int i2, float[] fArr, float[] fArr2, boolean z) {
        int i3 = 0;
        if (brightnessThresholds != null && brightnessThresholds.getBrightnessThresholdPoints() != null && brightnessThresholds.getBrightnessThresholdPoints().getBrightnessThresholdPoint().size() != 0) {
            List<ThresholdPoint> brightnessThresholdPoint = brightnessThresholds.getBrightnessThresholdPoints().getBrightnessThresholdPoint();
            int size = brightnessThresholdPoint.size();
            float[] fArr3 = new float[size];
            float[] fArr4 = new float[size];
            for (ThresholdPoint thresholdPoint : brightnessThresholdPoint) {
                fArr3[i3] = thresholdPoint.getThreshold().floatValue();
                fArr4[i3] = thresholdPoint.getPercentage().floatValue();
                i3++;
            }
            return new Pair<>(fArr3, fArr4);
        }
        int[] intArray = this.mContext.getResources().getIntArray(i);
        int length = (intArray == null || intArray.length == 0) ? 1 : intArray.length + 1;
        int[] intArray2 = this.mContext.getResources().getIntArray(i2);
        boolean z2 = intArray2 == null || intArray2.length == 0;
        if (z2 && length == 1) {
            return new Pair<>(fArr, fArr2);
        }
        if (z2 || intArray2.length != length) {
            throw new IllegalArgumentException("Brightness threshold arrays do not align in length");
        }
        float[] fArr5 = new float[length];
        for (int i4 = 1; i4 < length; i4++) {
            fArr5[i4] = intArray[i4 - 1];
        }
        if (z) {
            fArr5 = constraintInRangeIfNeeded(fArr5);
        }
        float[] fArr6 = new float[length];
        while (i3 < intArray2.length) {
            fArr6[i3] = intArray2[i3] / 10.0f;
            i3++;
        }
        return new Pair<>(fArr5, fArr6);
    }

    public final float[] constraintInRangeIfNeeded(float[] fArr) {
        if (isAllInRange(fArr, 0.0f, 1.0f)) {
            return fArr;
        }
        Slog.w("DisplayDeviceConfig", "Detected screen thresholdLevels on a deprecated brightness scale");
        float[] fArr2 = new float[fArr.length];
        for (int i = 0; fArr.length > i; i++) {
            fArr2[i] = fArr[i] / 255.0f;
        }
        return fArr2;
    }

    public final boolean isAllInRange(float[] fArr, float f, float f2) {
        for (float f3 : fArr) {
            if (f3 < f || f3 > f2) {
                return false;
            }
        }
        return true;
    }

    /* renamed from: com.android.server.display.DisplayDeviceConfig$1 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C07701 {
        public static final /* synthetic */ int[] $SwitchMap$com$android$server$display$config$ThermalStatus;

        static {
            int[] iArr = new int[ThermalStatus.values().length];
            $SwitchMap$com$android$server$display$config$ThermalStatus = iArr;
            try {
                iArr[ThermalStatus.none.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$server$display$config$ThermalStatus[ThermalStatus.light.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$server$display$config$ThermalStatus[ThermalStatus.moderate.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$server$display$config$ThermalStatus[ThermalStatus.severe.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$server$display$config$ThermalStatus[ThermalStatus.critical.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$server$display$config$ThermalStatus[ThermalStatus.emergency.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$android$server$display$config$ThermalStatus[ThermalStatus.shutdown.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
        }
    }

    public final boolean thermalStatusIsValid(ThermalStatus thermalStatus) {
        if (thermalStatus == null) {
            return false;
        }
        switch (C07701.$SwitchMap$com$android$server$display$config$ThermalStatus[thermalStatus.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                return true;
            default:
                return false;
        }
    }

    @VisibleForTesting
    public static int convertThermalStatus(ThermalStatus thermalStatus) {
        if (thermalStatus == null) {
            return 0;
        }
        switch (C07701.$SwitchMap$com$android$server$display$config$ThermalStatus[thermalStatus.ordinal()]) {
            case 1:
                return 0;
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
                return 3;
            case 5:
                return 4;
            case 6:
                return 5;
            case 7:
                return 6;
            default:
                Slog.wtf("DisplayDeviceConfig", "Unexpected Thermal Status: " + thermalStatus);
                return 0;
        }
    }

    public final int convertInterpolationType(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        if ("linear".equals(str)) {
            return 1;
        }
        Slog.wtf("DisplayDeviceConfig", "Unexpected Interpolation Type: " + str);
        return 0;
    }

    public final void loadAmbientHorizonFromDdc(DisplayConfiguration displayConfiguration) {
        BigInteger ambientLightHorizonLong = displayConfiguration.getAmbientLightHorizonLong();
        if (ambientLightHorizonLong != null) {
            this.mAmbientHorizonLong = ambientLightHorizonLong.intValue();
        }
        BigInteger ambientLightHorizonShort = displayConfiguration.getAmbientLightHorizonShort();
        if (ambientLightHorizonShort != null) {
            this.mAmbientHorizonShort = ambientLightHorizonShort.intValue();
        }
    }

    public static float[] getFloatArray(TypedArray typedArray, float f) {
        int length = typedArray.length();
        float[] fArr = new float[length];
        for (int i = 0; i < length; i++) {
            fArr[i] = typedArray.getFloat(i, f);
        }
        typedArray.recycle();
        return fArr;
    }

    public static float[] getLuxLevels(int[] iArr) {
        float[] fArr = new float[iArr.length + 1];
        int i = 0;
        while (i < iArr.length) {
            int i2 = i + 1;
            fArr[i2] = iArr[i];
            i = i2;
        }
        return fArr;
    }

    public final void loadEnableAutoBrightness(AutoBrightness autoBrightness) {
        boolean z = true;
        this.mDdcAutoBrightnessAvailable = true;
        if (autoBrightness != null) {
            this.mDdcAutoBrightnessAvailable = autoBrightness.getEnabled();
        }
        this.mAutoBrightnessAvailable = (this.mContext.getResources().getBoolean(17891379) && this.mDdcAutoBrightnessAvailable) ? false : false;
    }

    public final void loadScreenOffBrightnessSensorValueToLuxFromDdc(DisplayConfiguration displayConfiguration) {
        IntegerArray screenOffBrightnessSensorValueToLux = displayConfiguration.getScreenOffBrightnessSensorValueToLux();
        if (screenOffBrightnessSensorValueToLux == null) {
            return;
        }
        List<BigInteger> item = screenOffBrightnessSensorValueToLux.getItem();
        this.mScreenOffBrightnessSensorValueToLux = new int[item.size()];
        for (int i = 0; i < item.size(); i++) {
            this.mScreenOffBrightnessSensorValueToLux[i] = item.get(i).intValue();
        }
    }

    public final void loadUsiVersion(DisplayConfiguration displayConfiguration) {
        UsiVersion usiVersion = displayConfiguration.getUsiVersion();
        this.mHostUsiVersion = usiVersion != null ? new HostUsiVersion(usiVersion.getMajorVersion().intValue(), usiVersion.getMinorVersion().intValue()) : null;
    }

    /* loaded from: classes.dex */
    public static class SensorData {
        public String name;
        public String type;
        public float minRefreshRate = 0.0f;
        public float maxRefreshRate = Float.POSITIVE_INFINITY;

        public String toString() {
            return "Sensor{type: " + this.type + ", name: " + this.name + ", refreshRateRange: [" + this.minRefreshRate + ", " + this.maxRefreshRate + "]} ";
        }

        public boolean matches(String str, String str2) {
            boolean z = !TextUtils.isEmpty(str);
            boolean z2 = !TextUtils.isEmpty(str2);
            return (z || z2) && (!z || str.equals(this.name)) && (!z2 || str2.equals(this.type));
        }
    }

    /* loaded from: classes.dex */
    public static class HighBrightnessModeData {
        public boolean allowInLowPowerMode;
        public float minimumHdrPercentOfScreen;
        public float minimumLux;
        public int thermalStatusLimit;
        public long timeMaxMillis;
        public long timeMinMillis;
        public long timeWindowMillis;
        public float transitionPoint;

        public void copyTo(HighBrightnessModeData highBrightnessModeData) {
            highBrightnessModeData.minimumLux = this.minimumLux;
            highBrightnessModeData.timeWindowMillis = this.timeWindowMillis;
            highBrightnessModeData.timeMaxMillis = this.timeMaxMillis;
            highBrightnessModeData.timeMinMillis = this.timeMinMillis;
            highBrightnessModeData.transitionPoint = this.transitionPoint;
            highBrightnessModeData.thermalStatusLimit = this.thermalStatusLimit;
            highBrightnessModeData.allowInLowPowerMode = this.allowInLowPowerMode;
            highBrightnessModeData.minimumHdrPercentOfScreen = this.minimumHdrPercentOfScreen;
        }

        public String toString() {
            return "HBM{minLux: " + this.minimumLux + ", transition: " + this.transitionPoint + ", timeWindow: " + this.timeWindowMillis + "ms, timeMax: " + this.timeMaxMillis + "ms, timeMin: " + this.timeMinMillis + "ms, thermalStatusLimit: " + this.thermalStatusLimit + ", allowInLowPowerMode: " + this.allowInLowPowerMode + ", minimumHdrPercentOfScreen: " + this.minimumHdrPercentOfScreen + "} ";
        }
    }

    /* loaded from: classes.dex */
    public static class BrightnessThrottlingData {
        public List<ThrottlingLevel> throttlingLevels;

        /* loaded from: classes.dex */
        public static class ThrottlingLevel {
            public float brightness;
            public int thermalStatus;

            public ThrottlingLevel(int i, float f) {
                this.thermalStatus = i;
                this.brightness = f;
            }

            public String toString() {
                return "[" + this.thermalStatus + "," + this.brightness + "]";
            }

            public boolean equals(Object obj) {
                if (obj instanceof ThrottlingLevel) {
                    ThrottlingLevel throttlingLevel = (ThrottlingLevel) obj;
                    return throttlingLevel.thermalStatus == this.thermalStatus && throttlingLevel.brightness == this.brightness;
                }
                return false;
            }

            public int hashCode() {
                return ((this.thermalStatus + 31) * 31) + Float.hashCode(this.brightness);
            }
        }

        public static BrightnessThrottlingData create(List<ThrottlingLevel> list) {
            if (list == null || list.size() == 0) {
                Slog.e("DisplayDeviceConfig", "BrightnessThrottlingData received null or empty throttling levels");
                return null;
            }
            ThrottlingLevel throttlingLevel = list.get(0);
            int size = list.size();
            int i = 1;
            while (i < size) {
                ThrottlingLevel throttlingLevel2 = list.get(i);
                if (throttlingLevel2.thermalStatus <= throttlingLevel.thermalStatus) {
                    Slog.e("DisplayDeviceConfig", "brightnessThrottlingMap must be strictly increasing, ignoring configuration. ThermalStatus " + throttlingLevel2.thermalStatus + " <= " + throttlingLevel.thermalStatus);
                    return null;
                } else if (throttlingLevel2.brightness >= throttlingLevel.brightness) {
                    Slog.e("DisplayDeviceConfig", "brightnessThrottlingMap must be strictly decreasing, ignoring configuration. Brightness " + throttlingLevel2.brightness + " >= " + throttlingLevel2.brightness);
                    return null;
                } else {
                    i++;
                    throttlingLevel = throttlingLevel2;
                }
            }
            for (ThrottlingLevel throttlingLevel3 : list) {
                if (throttlingLevel3.brightness > 1.0f) {
                    Slog.e("DisplayDeviceConfig", "brightnessThrottlingMap contains a brightness value exceeding system max. Brightness " + throttlingLevel3.brightness + " > 1.0");
                    return null;
                }
            }
            return new BrightnessThrottlingData(list);
        }

        public static BrightnessThrottlingData create(BrightnessThrottlingData brightnessThrottlingData) {
            if (brightnessThrottlingData == null) {
                return null;
            }
            return create(brightnessThrottlingData.throttlingLevels);
        }

        public String toString() {
            return "BrightnessThrottlingData{throttlingLevels:" + this.throttlingLevels + "} ";
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof BrightnessThrottlingData) {
                return this.throttlingLevels.equals(((BrightnessThrottlingData) obj).throttlingLevels);
            }
            return false;
        }

        public int hashCode() {
            return this.throttlingLevels.hashCode();
        }

        @VisibleForTesting
        public BrightnessThrottlingData(List<ThrottlingLevel> list) {
            this.throttlingLevels = new ArrayList(list.size());
            for (ThrottlingLevel throttlingLevel : list) {
                this.throttlingLevels.add(new ThrottlingLevel(throttlingLevel.thermalStatus, throttlingLevel.brightness));
            }
        }
    }
}
