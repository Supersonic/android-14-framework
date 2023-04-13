package android.hardware.display;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Pair;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParserException;
@SystemApi
/* loaded from: classes.dex */
public final class BrightnessConfiguration implements Parcelable {
    private static final String ATTR_CATEGORY = "category";
    private static final String ATTR_COLLECT_COLOR = "collect-color";
    private static final String ATTR_DESCRIPTION = "description";
    private static final String ATTR_LUX = "lux";
    private static final String ATTR_MODEL_LOWER_BOUND = "model-lower-bound";
    private static final String ATTR_MODEL_TIMEOUT = "model-timeout";
    private static final String ATTR_MODEL_UPPER_BOUND = "model-upper-bound";
    private static final String ATTR_NITS = "nits";
    private static final String ATTR_PACKAGE_NAME = "package-name";
    public static final Parcelable.Creator<BrightnessConfiguration> CREATOR = new Parcelable.Creator<BrightnessConfiguration>() { // from class: android.hardware.display.BrightnessConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BrightnessConfiguration createFromParcel(Parcel in) {
            float[] lux = in.createFloatArray();
            float[] nits = in.createFloatArray();
            Builder builder = new Builder(lux, nits);
            int n = in.readInt();
            for (int i = 0; i < n; i++) {
                String packageName = in.readString();
                BrightnessCorrection correction = BrightnessCorrection.CREATOR.createFromParcel(in);
                builder.addCorrectionByPackageName(packageName, correction);
            }
            int n2 = in.readInt();
            for (int i2 = 0; i2 < n2; i2++) {
                int category = in.readInt();
                BrightnessCorrection correction2 = BrightnessCorrection.CREATOR.createFromParcel(in);
                builder.addCorrectionByCategory(category, correction2);
            }
            String description = in.readString();
            builder.setDescription(description);
            boolean shouldCollectColorSamples = in.readBoolean();
            builder.setShouldCollectColorSamples(shouldCollectColorSamples);
            builder.setShortTermModelTimeoutMillis(in.readLong());
            builder.setShortTermModelLowerLuxMultiplier(in.readFloat());
            builder.setShortTermModelUpperLuxMultiplier(in.readFloat());
            return builder.build();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BrightnessConfiguration[] newArray(int size) {
            return new BrightnessConfiguration[size];
        }
    };
    public static final long SHORT_TERM_TIMEOUT_UNSET = -1;
    private static final String TAG_BRIGHTNESS_CORRECTION = "brightness-correction";
    private static final String TAG_BRIGHTNESS_CORRECTIONS = "brightness-corrections";
    private static final String TAG_BRIGHTNESS_CURVE = "brightness-curve";
    private static final String TAG_BRIGHTNESS_PARAMS = "brightness-params";
    private static final String TAG_BRIGHTNESS_POINT = "brightness-point";
    private final Map<Integer, BrightnessCorrection> mCorrectionsByCategory;
    private final Map<String, BrightnessCorrection> mCorrectionsByPackageName;
    private final String mDescription;
    private final float[] mLux;
    private final float[] mNits;
    private final float mShortTermModelLowerLuxMultiplier;
    private final long mShortTermModelTimeout;
    private final float mShortTermModelUpperLuxMultiplier;
    private final boolean mShouldCollectColorSamples;

    private BrightnessConfiguration(float[] lux, float[] nits, Map<String, BrightnessCorrection> correctionsByPackageName, Map<Integer, BrightnessCorrection> correctionsByCategory, String description, boolean shouldCollectColorSamples, long shortTermModelTimeout, float shortTermModelLowerLuxMultiplier, float shortTermModelUpperLuxMultiplier) {
        this.mLux = lux;
        this.mNits = nits;
        this.mCorrectionsByPackageName = correctionsByPackageName;
        this.mCorrectionsByCategory = correctionsByCategory;
        this.mDescription = description;
        this.mShouldCollectColorSamples = shouldCollectColorSamples;
        this.mShortTermModelTimeout = shortTermModelTimeout;
        this.mShortTermModelLowerLuxMultiplier = shortTermModelLowerLuxMultiplier;
        this.mShortTermModelUpperLuxMultiplier = shortTermModelUpperLuxMultiplier;
    }

    public Pair<float[], float[]> getCurve() {
        float[] fArr = this.mLux;
        float[] copyOf = Arrays.copyOf(fArr, fArr.length);
        float[] fArr2 = this.mNits;
        return Pair.create(copyOf, Arrays.copyOf(fArr2, fArr2.length));
    }

    public BrightnessCorrection getCorrectionByPackageName(String packageName) {
        return this.mCorrectionsByPackageName.get(packageName);
    }

    public BrightnessCorrection getCorrectionByCategory(int category) {
        return this.mCorrectionsByCategory.get(Integer.valueOf(category));
    }

    public String getDescription() {
        return this.mDescription;
    }

    public boolean shouldCollectColorSamples() {
        return this.mShouldCollectColorSamples;
    }

    public long getShortTermModelTimeoutMillis() {
        return this.mShortTermModelTimeout;
    }

    public float getShortTermModelUpperLuxMultiplier() {
        return this.mShortTermModelUpperLuxMultiplier;
    }

    public float getShortTermModelLowerLuxMultiplier() {
        return this.mShortTermModelLowerLuxMultiplier;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloatArray(this.mLux);
        dest.writeFloatArray(this.mNits);
        dest.writeInt(this.mCorrectionsByPackageName.size());
        for (Map.Entry<String, BrightnessCorrection> entry : this.mCorrectionsByPackageName.entrySet()) {
            String packageName = entry.getKey();
            BrightnessCorrection correction = entry.getValue();
            dest.writeString(packageName);
            correction.writeToParcel(dest, flags);
        }
        dest.writeInt(this.mCorrectionsByCategory.size());
        for (Map.Entry<Integer, BrightnessCorrection> entry2 : this.mCorrectionsByCategory.entrySet()) {
            int category = entry2.getKey().intValue();
            BrightnessCorrection correction2 = entry2.getValue();
            dest.writeInt(category);
            correction2.writeToParcel(dest, flags);
        }
        dest.writeString(this.mDescription);
        dest.writeBoolean(this.mShouldCollectColorSamples);
        dest.writeLong(this.mShortTermModelTimeout);
        dest.writeFloat(this.mShortTermModelLowerLuxMultiplier);
        dest.writeFloat(this.mShortTermModelUpperLuxMultiplier);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("BrightnessConfiguration{[");
        int size = this.mLux.length;
        for (int i = 0; i < size; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(NavigationBarInflaterView.KEY_CODE_START).append(this.mLux[i]).append(", ").append(this.mNits[i]).append(NavigationBarInflaterView.KEY_CODE_END);
        }
        sb.append("], {");
        for (Map.Entry<String, BrightnessCorrection> entry : this.mCorrectionsByPackageName.entrySet()) {
            sb.append("'" + entry.getKey() + "': " + entry.getValue() + ", ");
        }
        for (Map.Entry<Integer, BrightnessCorrection> entry2 : this.mCorrectionsByCategory.entrySet()) {
            sb.append(entry2.getKey() + ": " + entry2.getValue() + ", ");
        }
        sb.append("}, '");
        String str = this.mDescription;
        if (str != null) {
            sb.append(str);
        }
        sb.append(", shouldCollectColorSamples = " + this.mShouldCollectColorSamples);
        if (this.mShortTermModelTimeout >= 0) {
            sb.append(", shortTermModelTimeout = " + this.mShortTermModelTimeout);
        }
        if (!Float.isNaN(this.mShortTermModelLowerLuxMultiplier)) {
            sb.append(", shortTermModelLowerLuxMultiplier = " + this.mShortTermModelLowerLuxMultiplier);
        }
        if (!Float.isNaN(this.mShortTermModelLowerLuxMultiplier)) {
            sb.append(", shortTermModelUpperLuxMultiplier = " + this.mShortTermModelUpperLuxMultiplier);
        }
        sb.append("'}");
        return sb.toString();
    }

    public int hashCode() {
        int result = (((((((1 * 31) + Arrays.hashCode(this.mLux)) * 31) + Arrays.hashCode(this.mNits)) * 31) + this.mCorrectionsByPackageName.hashCode()) * 31) + this.mCorrectionsByCategory.hashCode();
        String str = this.mDescription;
        if (str != null) {
            result = (result * 31) + str.hashCode();
        }
        return (((((((result * 31) + Boolean.hashCode(this.mShouldCollectColorSamples)) * 31) + Long.hashCode(this.mShortTermModelTimeout)) * 31) + Float.hashCode(this.mShortTermModelLowerLuxMultiplier)) * 31) + Float.hashCode(this.mShortTermModelUpperLuxMultiplier);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof BrightnessConfiguration) {
            BrightnessConfiguration other = (BrightnessConfiguration) o;
            return Arrays.equals(this.mLux, other.mLux) && Arrays.equals(this.mNits, other.mNits) && this.mCorrectionsByPackageName.equals(other.mCorrectionsByPackageName) && this.mCorrectionsByCategory.equals(other.mCorrectionsByCategory) && Objects.equals(this.mDescription, other.mDescription) && this.mShouldCollectColorSamples == other.mShouldCollectColorSamples && this.mShortTermModelTimeout == other.mShortTermModelTimeout && checkFloatEquals(this.mShortTermModelLowerLuxMultiplier, other.mShortTermModelLowerLuxMultiplier) && checkFloatEquals(this.mShortTermModelUpperLuxMultiplier, other.mShortTermModelUpperLuxMultiplier);
        }
        return false;
    }

    private boolean checkFloatEquals(float one, float two) {
        return (Float.isNaN(one) && Float.isNaN(two)) || one == two;
    }

    public void saveToXml(TypedXmlSerializer serializer) throws IOException {
        serializer.startTag(null, TAG_BRIGHTNESS_CURVE);
        String str = this.mDescription;
        if (str != null) {
            serializer.attribute(null, "description", str);
        }
        for (int i = 0; i < this.mLux.length; i++) {
            serializer.startTag(null, TAG_BRIGHTNESS_POINT);
            serializer.attributeFloat(null, ATTR_LUX, this.mLux[i]);
            serializer.attributeFloat(null, ATTR_NITS, this.mNits[i]);
            serializer.endTag(null, TAG_BRIGHTNESS_POINT);
        }
        serializer.endTag(null, TAG_BRIGHTNESS_CURVE);
        serializer.startTag(null, TAG_BRIGHTNESS_CORRECTIONS);
        for (Map.Entry<String, BrightnessCorrection> entry : this.mCorrectionsByPackageName.entrySet()) {
            String packageName = entry.getKey();
            BrightnessCorrection correction = entry.getValue();
            serializer.startTag(null, TAG_BRIGHTNESS_CORRECTION);
            serializer.attribute(null, ATTR_PACKAGE_NAME, packageName);
            correction.saveToXml(serializer);
            serializer.endTag(null, TAG_BRIGHTNESS_CORRECTION);
        }
        for (Map.Entry<Integer, BrightnessCorrection> entry2 : this.mCorrectionsByCategory.entrySet()) {
            int category = entry2.getKey().intValue();
            BrightnessCorrection correction2 = entry2.getValue();
            serializer.startTag(null, TAG_BRIGHTNESS_CORRECTION);
            serializer.attributeInt(null, "category", category);
            correction2.saveToXml(serializer);
            serializer.endTag(null, TAG_BRIGHTNESS_CORRECTION);
        }
        serializer.endTag(null, TAG_BRIGHTNESS_CORRECTIONS);
        serializer.startTag(null, TAG_BRIGHTNESS_PARAMS);
        if (this.mShouldCollectColorSamples) {
            serializer.attributeBoolean(null, ATTR_COLLECT_COLOR, true);
        }
        long j = this.mShortTermModelTimeout;
        if (j >= 0) {
            serializer.attributeLong(null, ATTR_MODEL_TIMEOUT, j);
        }
        if (!Float.isNaN(this.mShortTermModelLowerLuxMultiplier)) {
            serializer.attributeFloat(null, ATTR_MODEL_LOWER_BOUND, this.mShortTermModelLowerLuxMultiplier);
        }
        if (!Float.isNaN(this.mShortTermModelUpperLuxMultiplier)) {
            serializer.attributeFloat(null, ATTR_MODEL_UPPER_BOUND, this.mShortTermModelUpperLuxMultiplier);
        }
        serializer.endTag(null, TAG_BRIGHTNESS_PARAMS);
    }

    public static BrightnessConfiguration loadFromXml(TypedXmlPullParser parser) throws IOException, XmlPullParserException {
        int configDepth;
        String description = null;
        List<Float> luxList = new ArrayList<>();
        List<Float> nitsList = new ArrayList<>();
        Map<String, BrightnessCorrection> correctionsByPackageName = new HashMap<>();
        Map<Integer, BrightnessCorrection> correctionsByCategory = new HashMap<>();
        boolean shouldCollectColorSamples = false;
        long shortTermModelTimeout = -1;
        float shortTermModelLowerLuxMultiplier = Float.NaN;
        float shortTermModelUpperLuxMultiplier = Float.NaN;
        int configDepth2 = parser.getDepth();
        while (XmlUtils.nextElementWithin(parser, configDepth2)) {
            String str = null;
            if (TAG_BRIGHTNESS_CURVE.equals(parser.getName())) {
                description = parser.getAttributeValue(null, "description");
                int curveDepth = parser.getDepth();
                while (XmlUtils.nextElementWithin(parser, curveDepth)) {
                    if (TAG_BRIGHTNESS_POINT.equals(parser.getName())) {
                        float lux = loadFloatFromXml(parser, ATTR_LUX);
                        float nits = loadFloatFromXml(parser, ATTR_NITS);
                        luxList.add(Float.valueOf(lux));
                        nitsList.add(Float.valueOf(nits));
                    }
                }
            } else {
                if (TAG_BRIGHTNESS_CORRECTIONS.equals(parser.getName())) {
                    int correctionsDepth = parser.getDepth();
                    while (XmlUtils.nextElementWithin(parser, correctionsDepth)) {
                        if (TAG_BRIGHTNESS_CORRECTION.equals(parser.getName())) {
                            String packageName = parser.getAttributeValue(str, ATTR_PACKAGE_NAME);
                            int configDepth3 = configDepth2;
                            int category = parser.getAttributeInt(str, "category", -1);
                            BrightnessCorrection correction = BrightnessCorrection.loadFromXml(parser);
                            if (packageName != null) {
                                correctionsByPackageName.put(packageName, correction);
                            } else if (category != -1) {
                                correctionsByCategory.put(Integer.valueOf(category), correction);
                            }
                            configDepth2 = configDepth3;
                            str = null;
                        }
                    }
                    configDepth = configDepth2;
                } else {
                    configDepth = configDepth2;
                    if (TAG_BRIGHTNESS_PARAMS.equals(parser.getName())) {
                        shouldCollectColorSamples = parser.getAttributeBoolean(null, ATTR_COLLECT_COLOR, false);
                        Long timeout = loadLongFromXml(parser, ATTR_MODEL_TIMEOUT);
                        if (timeout != null) {
                            shortTermModelTimeout = timeout.longValue();
                        }
                        shortTermModelLowerLuxMultiplier = loadFloatFromXml(parser, ATTR_MODEL_LOWER_BOUND);
                        shortTermModelUpperLuxMultiplier = loadFloatFromXml(parser, ATTR_MODEL_UPPER_BOUND);
                        configDepth2 = configDepth;
                    }
                }
                configDepth2 = configDepth;
            }
        }
        int n = luxList.size();
        float[] lux2 = new float[n];
        float[] nits2 = new float[n];
        for (int i = 0; i < n; i++) {
            lux2[i] = luxList.get(i).floatValue();
            nits2[i] = nitsList.get(i).floatValue();
        }
        Builder builder = new Builder(lux2, nits2);
        builder.setDescription(description);
        for (Map.Entry<String, BrightnessCorrection> entry : correctionsByPackageName.entrySet()) {
            String packageName2 = entry.getKey();
            String description2 = description;
            BrightnessCorrection correction2 = entry.getValue();
            builder.addCorrectionByPackageName(packageName2, correction2);
            description = description2;
        }
        Iterator<Map.Entry<Integer, BrightnessCorrection>> it = correctionsByCategory.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, BrightnessCorrection> entry2 = it.next();
            int category2 = entry2.getKey().intValue();
            Iterator<Map.Entry<Integer, BrightnessCorrection>> it2 = it;
            BrightnessCorrection correction3 = entry2.getValue();
            builder.addCorrectionByCategory(category2, correction3);
            it = it2;
        }
        builder.setShouldCollectColorSamples(shouldCollectColorSamples);
        builder.setShortTermModelTimeoutMillis(shortTermModelTimeout);
        builder.setShortTermModelLowerLuxMultiplier(shortTermModelLowerLuxMultiplier);
        builder.setShortTermModelUpperLuxMultiplier(shortTermModelUpperLuxMultiplier);
        return builder.build();
    }

    private static float loadFloatFromXml(TypedXmlPullParser parser, String attribute) {
        return parser.getAttributeFloat(null, attribute, Float.NaN);
    }

    private static Long loadLongFromXml(TypedXmlPullParser parser, String attribute) {
        try {
            return Long.valueOf(parser.getAttributeLong(null, attribute));
        } catch (Exception e) {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private static final int MAX_CORRECTIONS_BY_CATEGORY = 20;
        private static final int MAX_CORRECTIONS_BY_PACKAGE_NAME = 20;
        private Map<Integer, BrightnessCorrection> mCorrectionsByCategory;
        private Map<String, BrightnessCorrection> mCorrectionsByPackageName;
        private float[] mCurveLux;
        private float[] mCurveNits;
        private String mDescription;
        private boolean mShouldCollectColorSamples;
        private long mShortTermModelTimeout = -1;
        private float mShortTermModelLowerLuxMultiplier = Float.NaN;
        private float mShortTermModelUpperLuxMultiplier = Float.NaN;

        public Builder(float[] lux, float[] nits) {
            Objects.requireNonNull(lux);
            Objects.requireNonNull(nits);
            if (lux.length == 0 || nits.length == 0) {
                throw new IllegalArgumentException("Lux and nits arrays must not be empty");
            }
            if (lux.length != nits.length) {
                throw new IllegalArgumentException("Lux and nits arrays must be the same length");
            }
            if (lux[0] != 0.0f) {
                throw new IllegalArgumentException("Initial control point must be for 0 lux");
            }
            Preconditions.checkArrayElementsInRange(lux, 0.0f, Float.MAX_VALUE, BrightnessConfiguration.ATTR_LUX);
            Preconditions.checkArrayElementsInRange(nits, 0.0f, Float.MAX_VALUE, BrightnessConfiguration.ATTR_NITS);
            checkMonotonic(lux, true, BrightnessConfiguration.ATTR_LUX);
            checkMonotonic(nits, false, BrightnessConfiguration.ATTR_NITS);
            this.mCurveLux = lux;
            this.mCurveNits = nits;
            this.mCorrectionsByPackageName = new HashMap();
            this.mCorrectionsByCategory = new HashMap();
        }

        public int getMaxCorrectionsByPackageName() {
            return 20;
        }

        public int getMaxCorrectionsByCategory() {
            return 20;
        }

        public Builder addCorrectionByPackageName(String packageName, BrightnessCorrection correction) {
            Objects.requireNonNull(packageName, "packageName must not be null");
            Objects.requireNonNull(correction, "correction must not be null");
            if (this.mCorrectionsByPackageName.size() >= getMaxCorrectionsByPackageName()) {
                throw new IllegalArgumentException("Too many corrections by package name");
            }
            this.mCorrectionsByPackageName.put(packageName, correction);
            return this;
        }

        public Builder addCorrectionByCategory(int category, BrightnessCorrection correction) {
            Objects.requireNonNull(correction, "correction must not be null");
            if (this.mCorrectionsByCategory.size() >= getMaxCorrectionsByCategory()) {
                throw new IllegalArgumentException("Too many corrections by category");
            }
            this.mCorrectionsByCategory.put(Integer.valueOf(category), correction);
            return this;
        }

        public Builder setDescription(String description) {
            this.mDescription = description;
            return this;
        }

        public Builder setShouldCollectColorSamples(boolean shouldCollectColorSamples) {
            this.mShouldCollectColorSamples = shouldCollectColorSamples;
            return this;
        }

        public Builder setShortTermModelTimeoutMillis(long shortTermModelTimeoutMillis) {
            this.mShortTermModelTimeout = shortTermModelTimeoutMillis;
            return this;
        }

        public Builder setShortTermModelUpperLuxMultiplier(float shortTermModelUpperLuxMultiplier) {
            if (shortTermModelUpperLuxMultiplier < 0.0f) {
                throw new IllegalArgumentException("Negative lux multiplier");
            }
            this.mShortTermModelUpperLuxMultiplier = shortTermModelUpperLuxMultiplier;
            return this;
        }

        public Builder setShortTermModelLowerLuxMultiplier(float shortTermModelLowerLuxMultiplier) {
            if (shortTermModelLowerLuxMultiplier < 0.0f) {
                throw new IllegalArgumentException("Negative lux multiplier");
            }
            this.mShortTermModelLowerLuxMultiplier = shortTermModelLowerLuxMultiplier;
            return this;
        }

        public BrightnessConfiguration build() {
            if (this.mCurveLux == null || this.mCurveNits == null) {
                throw new IllegalStateException("A curve must be set!");
            }
            return new BrightnessConfiguration(this.mCurveLux, this.mCurveNits, this.mCorrectionsByPackageName, this.mCorrectionsByCategory, this.mDescription, this.mShouldCollectColorSamples, this.mShortTermModelTimeout, this.mShortTermModelLowerLuxMultiplier, this.mShortTermModelUpperLuxMultiplier);
        }

        private static void checkMonotonic(float[] vals, boolean strictlyIncreasing, String name) {
            if (vals.length <= 1) {
                return;
            }
            float prev = vals[0];
            for (int i = 1; i < vals.length; i++) {
                if (prev > vals[i] || (prev == vals[i] && strictlyIncreasing)) {
                    String condition = strictlyIncreasing ? "strictly increasing" : "monotonic";
                    throw new IllegalArgumentException(name + " values must be " + condition);
                }
                prev = vals[i];
            }
        }
    }
}
