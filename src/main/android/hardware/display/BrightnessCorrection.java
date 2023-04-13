package android.hardware.display;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.MathUtils;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
@SystemApi
/* loaded from: classes.dex */
public final class BrightnessCorrection implements Parcelable {
    public static final Parcelable.Creator<BrightnessCorrection> CREATOR = new Parcelable.Creator<BrightnessCorrection>() { // from class: android.hardware.display.BrightnessCorrection.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BrightnessCorrection createFromParcel(Parcel in) {
            int type = in.readInt();
            switch (type) {
                case 1:
                    return ScaleAndTranslateLog.readFromParcel(in);
                default:
                    return null;
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BrightnessCorrection[] newArray(int size) {
            return new BrightnessCorrection[size];
        }
    };
    private static final int SCALE_AND_TRANSLATE_LOG = 1;
    private static final String TAG_SCALE_AND_TRANSLATE_LOG = "scale-and-translate-log";
    private BrightnessCorrectionImplementation mImplementation;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface BrightnessCorrectionImplementation {
        float apply(float f);

        void saveToXml(TypedXmlSerializer typedXmlSerializer) throws IOException;

        String toString();

        void writeToParcel(Parcel parcel);
    }

    private BrightnessCorrection(BrightnessCorrectionImplementation implementation) {
        this.mImplementation = implementation;
    }

    public static BrightnessCorrection createScaleAndTranslateLog(float scale, float translate) {
        BrightnessCorrectionImplementation implementation = new ScaleAndTranslateLog(scale, translate);
        return new BrightnessCorrection(implementation);
    }

    public float apply(float brightness) {
        return this.mImplementation.apply(brightness);
    }

    public String toString() {
        return this.mImplementation.toString();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BrightnessCorrection)) {
            return false;
        }
        BrightnessCorrection other = (BrightnessCorrection) o;
        return other.mImplementation.equals(this.mImplementation);
    }

    public int hashCode() {
        return this.mImplementation.hashCode();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        this.mImplementation.writeToParcel(dest);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void saveToXml(TypedXmlSerializer serializer) throws IOException {
        this.mImplementation.saveToXml(serializer);
    }

    public static BrightnessCorrection loadFromXml(TypedXmlPullParser parser) throws IOException, XmlPullParserException {
        int depth = parser.getDepth();
        while (XmlUtils.nextElementWithin(parser, depth)) {
            if (TAG_SCALE_AND_TRANSLATE_LOG.equals(parser.getName())) {
                return ScaleAndTranslateLog.loadFromXml(parser);
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float loadFloatFromXml(TypedXmlPullParser parser, String attribute) {
        return parser.getAttributeFloat(null, attribute, Float.NaN);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ScaleAndTranslateLog implements BrightnessCorrectionImplementation {
        private static final String ATTR_SCALE = "scale";
        private static final String ATTR_TRANSLATE = "translate";
        private static final float MAX_SCALE = 2.0f;
        private static final float MAX_TRANSLATE = 0.7f;
        private static final float MIN_SCALE = 0.5f;
        private static final float MIN_TRANSLATE = -0.6f;
        private final float mScale;
        private final float mTranslate;

        ScaleAndTranslateLog(float scale, float translate) {
            if (Float.isNaN(scale) || Float.isNaN(translate)) {
                throw new IllegalArgumentException("scale and translate must be numbers");
            }
            this.mScale = MathUtils.constrain(scale, 0.5f, (float) MAX_SCALE);
            this.mTranslate = MathUtils.constrain(translate, (float) MIN_TRANSLATE, (float) MAX_TRANSLATE);
        }

        @Override // android.hardware.display.BrightnessCorrection.BrightnessCorrectionImplementation
        public float apply(float brightness) {
            return MathUtils.exp((this.mScale * MathUtils.log(brightness)) + this.mTranslate);
        }

        @Override // android.hardware.display.BrightnessCorrection.BrightnessCorrectionImplementation
        public String toString() {
            return "ScaleAndTranslateLog(" + this.mScale + ", " + this.mTranslate + NavigationBarInflaterView.KEY_CODE_END;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof ScaleAndTranslateLog) {
                ScaleAndTranslateLog other = (ScaleAndTranslateLog) o;
                return other.mScale == this.mScale && other.mTranslate == this.mTranslate;
            }
            return false;
        }

        public int hashCode() {
            int result = (1 * 31) + Float.hashCode(this.mScale);
            return (result * 31) + Float.hashCode(this.mTranslate);
        }

        @Override // android.hardware.display.BrightnessCorrection.BrightnessCorrectionImplementation
        public void writeToParcel(Parcel dest) {
            dest.writeInt(1);
            dest.writeFloat(this.mScale);
            dest.writeFloat(this.mTranslate);
        }

        @Override // android.hardware.display.BrightnessCorrection.BrightnessCorrectionImplementation
        public void saveToXml(TypedXmlSerializer serializer) throws IOException {
            serializer.startTag(null, BrightnessCorrection.TAG_SCALE_AND_TRANSLATE_LOG);
            serializer.attributeFloat(null, "scale", this.mScale);
            serializer.attributeFloat(null, ATTR_TRANSLATE, this.mTranslate);
            serializer.endTag(null, BrightnessCorrection.TAG_SCALE_AND_TRANSLATE_LOG);
        }

        static BrightnessCorrection readFromParcel(Parcel in) {
            float scale = in.readFloat();
            float translate = in.readFloat();
            return BrightnessCorrection.createScaleAndTranslateLog(scale, translate);
        }

        static BrightnessCorrection loadFromXml(TypedXmlPullParser parser) throws IOException, XmlPullParserException {
            float scale = BrightnessCorrection.loadFloatFromXml(parser, "scale");
            float translate = BrightnessCorrection.loadFloatFromXml(parser, ATTR_TRANSLATE);
            return BrightnessCorrection.createScaleAndTranslateLog(scale, translate);
        }
    }
}
