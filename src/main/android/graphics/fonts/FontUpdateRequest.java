package android.graphics.fonts;

import android.graphics.fonts.FontFamilyUpdateRequest;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.Parcelable;
import android.text.FontConfig;
import android.text.format.DateFormat;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class FontUpdateRequest implements Parcelable {
    public static final Parcelable.Creator<FontUpdateRequest> CREATOR = new Parcelable.Creator<FontUpdateRequest>() { // from class: android.graphics.fonts.FontUpdateRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FontUpdateRequest createFromParcel(Parcel in) {
            return new FontUpdateRequest(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FontUpdateRequest[] newArray(int size) {
            return new FontUpdateRequest[size];
        }
    };
    public static final int TYPE_UPDATE_FONT_FAMILY = 1;
    public static final int TYPE_UPDATE_FONT_FILE = 0;
    private final ParcelFileDescriptor mFd;
    private final Family mFontFamily;
    private final byte[] mSignature;
    private final int mType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Type {
    }

    /* loaded from: classes.dex */
    public static final class Font implements Parcelable {
        private static final String ATTR_AXIS = "axis";
        private static final String ATTR_INDEX = "index";
        private static final String ATTR_POSTSCRIPT_NAME = "name";
        private static final String ATTR_SLANT = "slant";
        private static final String ATTR_WEIGHT = "weight";
        public static final Parcelable.Creator<Font> CREATOR = new Parcelable.Creator<Font>() { // from class: android.graphics.fonts.FontUpdateRequest.Font.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Font createFromParcel(Parcel source) {
                String fontName = source.readString8();
                int weight = source.readInt();
                int slant = source.readInt();
                int index = source.readInt();
                String varSettings = source.readString8();
                return new Font(fontName, new FontStyle(weight, slant), index, varSettings);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Font[] newArray(int size) {
                return new Font[size];
            }
        };
        private final FontStyle mFontStyle;
        private final String mFontVariationSettings;
        private final int mIndex;
        private final String mPostScriptName;

        public Font(String postScriptName, FontStyle fontStyle, int index, String fontVariationSettings) {
            this.mPostScriptName = postScriptName;
            this.mFontStyle = fontStyle;
            this.mIndex = index;
            this.mFontVariationSettings = fontVariationSettings;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString8(this.mPostScriptName);
            dest.writeInt(this.mFontStyle.getWeight());
            dest.writeInt(this.mFontStyle.getSlant());
            dest.writeInt(this.mIndex);
            dest.writeString8(this.mFontVariationSettings);
        }

        public static void writeToXml(TypedXmlSerializer out, Font font) throws IOException {
            out.attribute(null, "name", font.getPostScriptName());
            out.attributeInt(null, "index", font.getIndex());
            out.attributeInt(null, "weight", font.getFontStyle().getWeight());
            out.attributeInt(null, ATTR_SLANT, font.getFontStyle().getSlant());
            out.attribute(null, "axis", font.getFontVariationSettings());
        }

        public static Font readFromXml(XmlPullParser parser) throws IOException {
            String psName = parser.getAttributeValue(null, "name");
            if (psName == null) {
                throw new IOException("name attribute is missing in font tag.");
            }
            int index = FontUpdateRequest.getAttributeValueInt(parser, "index", 0);
            int weight = FontUpdateRequest.getAttributeValueInt(parser, "weight", 400);
            int slant = FontUpdateRequest.getAttributeValueInt(parser, ATTR_SLANT, 0);
            String varSettings = parser.getAttributeValue(null, "axis");
            if (varSettings == null) {
                varSettings = "";
            }
            return new Font(psName, new FontStyle(weight, slant), index, varSettings);
        }

        public String getPostScriptName() {
            return this.mPostScriptName;
        }

        public FontStyle getFontStyle() {
            return this.mFontStyle;
        }

        public int getIndex() {
            return this.mIndex;
        }

        public String getFontVariationSettings() {
            return this.mFontVariationSettings;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Font font = (Font) o;
            if (this.mIndex == font.mIndex && this.mPostScriptName.equals(font.mPostScriptName) && this.mFontStyle.equals(font.mFontStyle) && this.mFontVariationSettings.equals(font.mFontVariationSettings)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.mPostScriptName, this.mFontStyle, Integer.valueOf(this.mIndex), this.mFontVariationSettings);
        }

        public String toString() {
            return "Font{mPostScriptName='" + this.mPostScriptName + DateFormat.QUOTE + ", mFontStyle=" + this.mFontStyle + ", mIndex=" + this.mIndex + ", mFontVariationSettings='" + this.mFontVariationSettings + DateFormat.QUOTE + '}';
        }
    }

    /* loaded from: classes.dex */
    public static final class Family implements Parcelable {
        private static final String ATTR_NAME = "name";
        public static final Parcelable.Creator<Family> CREATOR = new Parcelable.Creator<Family>() { // from class: android.graphics.fonts.FontUpdateRequest.Family.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Family createFromParcel(Parcel source) {
                String familyName = source.readString8();
                List<Font> fonts = source.readParcelableList(new ArrayList(), Font.class.getClassLoader(), Font.class);
                return new Family(familyName, fonts);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Family[] newArray(int size) {
                return new Family[size];
            }
        };
        private static final String TAG_FAMILY = "family";
        private static final String TAG_FONT = "font";
        private final List<Font> mFonts;
        private final String mName;

        public Family(String name, List<Font> fonts) {
            this.mName = name;
            this.mFonts = fonts;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString8(this.mName);
            dest.writeParcelableList(this.mFonts, flags);
        }

        public static void writeFamilyToXml(TypedXmlSerializer out, Family family) throws IOException {
            out.attribute(null, "name", family.getName());
            List<Font> fonts = family.getFonts();
            for (int i = 0; i < fonts.size(); i++) {
                Font font = fonts.get(i);
                out.startTag(null, "font");
                Font.writeToXml(out, font);
                out.endTag(null, "font");
            }
        }

        public static Family readFromXml(XmlPullParser parser) throws XmlPullParserException, IOException {
            List<Font> fonts = new ArrayList<>();
            if (parser.getEventType() != 2 || !parser.getName().equals(TAG_FAMILY)) {
                throw new IOException("Unexpected parser state: must be START_TAG with family");
            }
            String name = parser.getAttributeValue(null, "name");
            if (name == null) {
                throw new IOException("name attribute is missing in family tag.");
            }
            while (true) {
                int type = parser.next();
                if (type == 1) {
                    break;
                } else if (type == 2 && parser.getName().equals("font")) {
                    fonts.add(Font.readFromXml(parser));
                } else if (type == 3 && parser.getName().equals(TAG_FAMILY)) {
                    break;
                }
            }
            return new Family(name, fonts);
        }

        public String getName() {
            return this.mName;
        }

        public List<Font> getFonts() {
            return this.mFonts;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Family family = (Family) o;
            if (this.mName.equals(family.mName) && this.mFonts.equals(family.mFonts)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.mName, this.mFonts);
        }

        public String toString() {
            return "Family{mName='" + this.mName + DateFormat.QUOTE + ", mFonts=" + this.mFonts + '}';
        }
    }

    public FontUpdateRequest(ParcelFileDescriptor fd, byte[] signature) {
        this.mType = 0;
        this.mFd = fd;
        this.mSignature = signature;
        this.mFontFamily = null;
    }

    public FontUpdateRequest(Family fontFamily) {
        this.mType = 1;
        this.mFd = null;
        this.mSignature = null;
        this.mFontFamily = fontFamily;
    }

    public FontUpdateRequest(String familyName, List<FontFamilyUpdateRequest.Font> variations) {
        this(createFontFamily(familyName, variations));
    }

    private static Family createFontFamily(String familyName, List<FontFamilyUpdateRequest.Font> fonts) {
        List<Font> updateFonts = new ArrayList<>(fonts.size());
        for (FontFamilyUpdateRequest.Font font : fonts) {
            updateFonts.add(new Font(font.getPostScriptName(), font.getStyle(), font.getIndex(), FontVariationAxis.toFontVariationSettings(font.getAxes())));
        }
        return new Family(familyName, updateFonts);
    }

    protected FontUpdateRequest(Parcel in) {
        this.mType = in.readInt();
        this.mFd = (ParcelFileDescriptor) in.readParcelable(ParcelFileDescriptor.class.getClassLoader(), ParcelFileDescriptor.class);
        this.mSignature = in.readBlob();
        this.mFontFamily = (Family) in.readParcelable(FontConfig.FontFamily.class.getClassLoader(), Family.class);
    }

    public int getType() {
        return this.mType;
    }

    public ParcelFileDescriptor getFd() {
        return this.mFd;
    }

    public byte[] getSignature() {
        return this.mSignature;
    }

    public Family getFontFamily() {
        return this.mFontFamily;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        ParcelFileDescriptor parcelFileDescriptor = this.mFd;
        if (parcelFileDescriptor != null) {
            return parcelFileDescriptor.describeContents();
        }
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mType);
        dest.writeParcelable(this.mFd, flags);
        dest.writeBlob(this.mSignature);
        dest.writeParcelable(this.mFontFamily, flags);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int getAttributeValueInt(XmlPullParser parser, String name, int defaultValue) {
        try {
            String value = parser.getAttributeValue(null, name);
            if (value == null) {
                return defaultValue;
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
