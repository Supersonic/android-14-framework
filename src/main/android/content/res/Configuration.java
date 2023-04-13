package android.content.res;

import android.app.WindowConfiguration;
import android.app.slice.Slice;
import android.content.ConfigurationProto;
import android.graphics.FontListParser;
import android.hardware.Camera;
import android.p008os.Build;
import android.p008os.LocaleList;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Slog;
import android.util.proto.ProtoInputStream;
import android.util.proto.ProtoOutputStream;
import android.util.proto.WireTypeMismatchException;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.IllformedLocaleException;
import java.util.List;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class Configuration implements Parcelable, Comparable<Configuration> {
    public static final int ASSETS_SEQ_UNDEFINED = 0;
    public static final int COLOR_MODE_HDR_MASK = 12;
    public static final int COLOR_MODE_HDR_NO = 4;
    public static final int COLOR_MODE_HDR_SHIFT = 2;
    public static final int COLOR_MODE_HDR_UNDEFINED = 0;
    public static final int COLOR_MODE_HDR_YES = 8;
    public static final int COLOR_MODE_UNDEFINED = 0;
    public static final int COLOR_MODE_WIDE_COLOR_GAMUT_MASK = 3;
    public static final int COLOR_MODE_WIDE_COLOR_GAMUT_NO = 1;
    public static final int COLOR_MODE_WIDE_COLOR_GAMUT_UNDEFINED = 0;
    public static final int COLOR_MODE_WIDE_COLOR_GAMUT_YES = 2;
    public static final int DENSITY_DPI_ANY = 65534;
    public static final int DENSITY_DPI_NONE = 65535;
    public static final int DENSITY_DPI_UNDEFINED = 0;
    public static final int FONT_WEIGHT_ADJUSTMENT_UNDEFINED = Integer.MAX_VALUE;
    public static final int GRAMMATICAL_GENDER_FEMININE = 2;
    public static final int GRAMMATICAL_GENDER_MASCULINE = 3;
    public static final int GRAMMATICAL_GENDER_NEUTRAL = 1;
    public static final int GRAMMATICAL_GENDER_NOT_SPECIFIED = 0;
    public static final int HARDKEYBOARDHIDDEN_NO = 1;
    public static final int HARDKEYBOARDHIDDEN_UNDEFINED = 0;
    public static final int HARDKEYBOARDHIDDEN_YES = 2;
    public static final int KEYBOARDHIDDEN_NO = 1;
    public static final int KEYBOARDHIDDEN_SOFT = 3;
    public static final int KEYBOARDHIDDEN_UNDEFINED = 0;
    public static final int KEYBOARDHIDDEN_YES = 2;
    public static final int KEYBOARD_12KEY = 3;
    public static final int KEYBOARD_NOKEYS = 1;
    public static final int KEYBOARD_QWERTY = 2;
    public static final int KEYBOARD_UNDEFINED = 0;
    public static final int MNC_ZERO = 65535;
    public static final int NATIVE_CONFIG_COLOR_MODE = 65536;
    public static final int NATIVE_CONFIG_DENSITY = 256;
    public static final int NATIVE_CONFIG_GRAMMATICAL_GENDER = 131072;
    public static final int NATIVE_CONFIG_KEYBOARD = 16;
    public static final int NATIVE_CONFIG_KEYBOARD_HIDDEN = 32;
    public static final int NATIVE_CONFIG_LAYOUTDIR = 16384;
    public static final int NATIVE_CONFIG_LOCALE = 4;
    public static final int NATIVE_CONFIG_MCC = 1;
    public static final int NATIVE_CONFIG_MNC = 2;
    public static final int NATIVE_CONFIG_NAVIGATION = 64;
    public static final int NATIVE_CONFIG_ORIENTATION = 128;
    public static final int NATIVE_CONFIG_SCREEN_LAYOUT = 2048;
    public static final int NATIVE_CONFIG_SCREEN_SIZE = 512;
    public static final int NATIVE_CONFIG_SMALLEST_SCREEN_SIZE = 8192;
    public static final int NATIVE_CONFIG_TOUCHSCREEN = 8;
    public static final int NATIVE_CONFIG_UI_MODE = 4096;
    public static final int NATIVE_CONFIG_VERSION = 1024;
    public static final int NAVIGATIONHIDDEN_NO = 1;
    public static final int NAVIGATIONHIDDEN_UNDEFINED = 0;
    public static final int NAVIGATIONHIDDEN_YES = 2;
    public static final int NAVIGATION_DPAD = 2;
    public static final int NAVIGATION_NONAV = 1;
    public static final int NAVIGATION_TRACKBALL = 3;
    public static final int NAVIGATION_UNDEFINED = 0;
    public static final int NAVIGATION_WHEEL = 4;
    public static final int ORIENTATION_LANDSCAPE = 2;
    public static final int ORIENTATION_PORTRAIT = 1;
    @Deprecated
    public static final int ORIENTATION_SQUARE = 3;
    public static final int ORIENTATION_UNDEFINED = 0;
    public static final int SCREENLAYOUT_COMPAT_NEEDED = 268435456;
    public static final int SCREENLAYOUT_LAYOUTDIR_LTR = 64;
    public static final int SCREENLAYOUT_LAYOUTDIR_MASK = 192;
    public static final int SCREENLAYOUT_LAYOUTDIR_RTL = 128;
    public static final int SCREENLAYOUT_LAYOUTDIR_SHIFT = 6;
    public static final int SCREENLAYOUT_LAYOUTDIR_UNDEFINED = 0;
    public static final int SCREENLAYOUT_LONG_MASK = 48;
    public static final int SCREENLAYOUT_LONG_NO = 16;
    public static final int SCREENLAYOUT_LONG_UNDEFINED = 0;
    public static final int SCREENLAYOUT_LONG_YES = 32;
    public static final int SCREENLAYOUT_ROUND_MASK = 768;
    public static final int SCREENLAYOUT_ROUND_NO = 256;
    public static final int SCREENLAYOUT_ROUND_SHIFT = 8;
    public static final int SCREENLAYOUT_ROUND_UNDEFINED = 0;
    public static final int SCREENLAYOUT_ROUND_YES = 512;
    public static final int SCREENLAYOUT_SIZE_LARGE = 3;
    public static final int SCREENLAYOUT_SIZE_MASK = 15;
    public static final int SCREENLAYOUT_SIZE_NORMAL = 2;
    public static final int SCREENLAYOUT_SIZE_SMALL = 1;
    public static final int SCREENLAYOUT_SIZE_UNDEFINED = 0;
    public static final int SCREENLAYOUT_SIZE_XLARGE = 4;
    public static final int SCREENLAYOUT_UNDEFINED = 0;
    public static final int SCREEN_HEIGHT_DP_UNDEFINED = 0;
    public static final int SCREEN_WIDTH_DP_UNDEFINED = 0;
    public static final int SMALLEST_SCREEN_WIDTH_DP_UNDEFINED = 0;
    private static final String TAG = "Configuration";
    public static final int TOUCHSCREEN_FINGER = 3;
    public static final int TOUCHSCREEN_NOTOUCH = 1;
    @Deprecated
    public static final int TOUCHSCREEN_STYLUS = 2;
    public static final int TOUCHSCREEN_UNDEFINED = 0;
    public static final int UI_MODE_NIGHT_MASK = 48;
    public static final int UI_MODE_NIGHT_NO = 16;
    public static final int UI_MODE_NIGHT_UNDEFINED = 0;
    public static final int UI_MODE_NIGHT_YES = 32;
    public static final int UI_MODE_TYPE_APPLIANCE = 5;
    public static final int UI_MODE_TYPE_CAR = 3;
    public static final int UI_MODE_TYPE_DESK = 2;
    public static final int UI_MODE_TYPE_MASK = 15;
    public static final int UI_MODE_TYPE_NORMAL = 1;
    public static final int UI_MODE_TYPE_TELEVISION = 4;
    public static final int UI_MODE_TYPE_UNDEFINED = 0;
    public static final int UI_MODE_TYPE_VR_HEADSET = 7;
    public static final int UI_MODE_TYPE_WATCH = 6;
    private static final String XML_ATTR_APP_BOUNDS = "app_bounds";
    private static final String XML_ATTR_COLOR_MODE = "clrMod";
    private static final String XML_ATTR_DENSITY = "density";
    private static final String XML_ATTR_FONT_SCALE = "fs";
    private static final String XML_ATTR_FONT_WEIGHT_ADJUSTMENT = "fontWeightAdjustment";
    private static final String XML_ATTR_GRAMMATICAL_GENDER = "grammaticalGender";
    private static final String XML_ATTR_HARD_KEYBOARD_HIDDEN = "hardKeyHid";
    private static final String XML_ATTR_KEYBOARD = "key";
    private static final String XML_ATTR_KEYBOARD_HIDDEN = "keyHid";
    private static final String XML_ATTR_LOCALES = "locales";
    private static final String XML_ATTR_MCC = "mcc";
    private static final String XML_ATTR_MNC = "mnc";
    private static final String XML_ATTR_NAVIGATION = "nav";
    private static final String XML_ATTR_NAVIGATION_HIDDEN = "navHid";
    private static final String XML_ATTR_ORIENTATION = "ori";
    private static final String XML_ATTR_ROTATION = "rot";
    private static final String XML_ATTR_SCREEN_HEIGHT = "height";
    private static final String XML_ATTR_SCREEN_LAYOUT = "scrLay";
    private static final String XML_ATTR_SCREEN_WIDTH = "width";
    private static final String XML_ATTR_SMALLEST_WIDTH = "sw";
    private static final String XML_ATTR_TOUCHSCREEN = "touch";
    private static final String XML_ATTR_UI_MODE = "ui";
    public int assetsSeq;
    public int colorMode;
    public int compatScreenHeightDp;
    public int compatScreenWidthDp;
    public int compatSmallestScreenWidthDp;
    public int densityDpi;
    public float fontScale;
    public int fontWeightAdjustment;
    public int hardKeyboardHidden;
    public int keyboard;
    public int keyboardHidden;
    @Deprecated
    public Locale locale;
    private int mGrammaticalGender;
    private LocaleList mLocaleList;
    public int mcc;
    public int mnc;
    public int navigation;
    public int navigationHidden;
    public int orientation;
    public int screenHeightDp;
    public int screenLayout;
    public int screenWidthDp;
    public int seq;
    public int smallestScreenWidthDp;
    public int touchscreen;
    public int uiMode;
    public boolean userSetLocale;
    public final WindowConfiguration windowConfiguration;
    public static final Configuration EMPTY = new Configuration();
    public static final Parcelable.Creator<Configuration> CREATOR = new Parcelable.Creator<Configuration>() { // from class: android.content.res.Configuration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Configuration createFromParcel(Parcel source) {
            return new Configuration(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Configuration[] newArray(int size) {
            return new Configuration[size];
        }
    };

    /* loaded from: classes.dex */
    public @interface GrammaticalGender {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface NativeConfig {
    }

    /* loaded from: classes.dex */
    public @interface Orientation {
    }

    public static int resetScreenLayout(int curLayout) {
        return ((-268435520) & curLayout) | 36;
    }

    public static int reduceScreenLayout(int curLayout, int longSizeDp, int shortSizeDp) {
        int screenLayoutSize;
        boolean screenLayoutCompatNeeded;
        boolean screenLayoutLong;
        if (longSizeDp < 470) {
            screenLayoutSize = 1;
            screenLayoutLong = false;
            screenLayoutCompatNeeded = false;
        } else {
            if (longSizeDp >= 960 && shortSizeDp >= 720) {
                screenLayoutSize = 4;
            } else if (longSizeDp >= 640 && shortSizeDp >= 480) {
                screenLayoutSize = 3;
            } else {
                screenLayoutSize = 2;
            }
            if (shortSizeDp > 321 || longSizeDp > 570) {
                screenLayoutCompatNeeded = true;
            } else {
                screenLayoutCompatNeeded = false;
            }
            if ((longSizeDp * 3) / 5 >= shortSizeDp - 1) {
                screenLayoutLong = true;
            } else {
                screenLayoutLong = false;
            }
        }
        if (!screenLayoutLong) {
            curLayout = (curLayout & (-49)) | 16;
        }
        if (screenLayoutCompatNeeded) {
            curLayout |= 268435456;
        }
        int curSize = curLayout & 15;
        if (screenLayoutSize < curSize) {
            return (curLayout & (-16)) | screenLayoutSize;
        }
        return curLayout;
    }

    public static String configurationDiffToString(int diff) {
        ArrayList<String> list = new ArrayList<>();
        if ((diff & 1) != 0) {
            list.add("CONFIG_MCC");
        }
        if ((diff & 2) != 0) {
            list.add("CONFIG_MNC");
        }
        if ((diff & 4) != 0) {
            list.add("CONFIG_LOCALE");
        }
        if ((diff & 8) != 0) {
            list.add("CONFIG_TOUCHSCREEN");
        }
        if ((diff & 16) != 0) {
            list.add("CONFIG_KEYBOARD");
        }
        if ((diff & 32) != 0) {
            list.add("CONFIG_KEYBOARD_HIDDEN");
        }
        if ((diff & 64) != 0) {
            list.add("CONFIG_NAVIGATION");
        }
        if ((diff & 128) != 0) {
            list.add("CONFIG_ORIENTATION");
        }
        if ((diff & 256) != 0) {
            list.add("CONFIG_SCREEN_LAYOUT");
        }
        if ((diff & 16384) != 0) {
            list.add("CONFIG_COLOR_MODE");
        }
        if ((diff & 512) != 0) {
            list.add("CONFIG_UI_MODE");
        }
        if ((diff & 1024) != 0) {
            list.add("CONFIG_SCREEN_SIZE");
        }
        if ((diff & 2048) != 0) {
            list.add("CONFIG_SMALLEST_SCREEN_SIZE");
        }
        if ((diff & 4096) != 0) {
            list.add("CONFIG_DENSITY");
        }
        if ((diff & 8192) != 0) {
            list.add("CONFIG_LAYOUT_DIRECTION");
        }
        if ((1073741824 & diff) != 0) {
            list.add("CONFIG_FONT_SCALE");
        }
        if ((Integer.MIN_VALUE & diff) != 0) {
            list.add("CONFIG_ASSETS_PATHS");
        }
        if ((536870912 & diff) != 0) {
            list.add("CONFIG_WINDOW_CONFIGURATION");
        }
        if ((268435456 & diff) != 0) {
            list.add("CONFIG_AUTO_BOLD_TEXT");
        }
        if ((32768 & diff) != 0) {
            list.add("CONFIG_GRAMMATICAL_GENDER");
        }
        return "{" + TextUtils.join(", ", list) + "}";
    }

    public boolean isLayoutSizeAtLeast(int size) {
        int cur = this.screenLayout & 15;
        return cur != 0 && cur >= size;
    }

    public Configuration() {
        this.windowConfiguration = new WindowConfiguration();
        unset();
    }

    public Configuration(Configuration o) {
        this.windowConfiguration = new WindowConfiguration();
        setTo(o);
    }

    private void fixUpLocaleList() {
        Locale locale;
        if ((this.locale == null && !this.mLocaleList.isEmpty()) || ((locale = this.locale) != null && !locale.equals(this.mLocaleList.get(0)))) {
            this.mLocaleList = this.locale == null ? LocaleList.getEmptyLocaleList() : new LocaleList(this.locale);
        }
    }

    public void setTo(Configuration o) {
        this.fontScale = o.fontScale;
        this.mcc = o.mcc;
        this.mnc = o.mnc;
        Locale locale = o.locale;
        if (locale == null) {
            this.locale = null;
        } else if (!locale.equals(this.locale)) {
            this.locale = (Locale) o.locale.clone();
        }
        o.fixUpLocaleList();
        this.mLocaleList = o.mLocaleList;
        this.mGrammaticalGender = o.mGrammaticalGender;
        this.userSetLocale = o.userSetLocale;
        this.touchscreen = o.touchscreen;
        this.keyboard = o.keyboard;
        this.keyboardHidden = o.keyboardHidden;
        this.hardKeyboardHidden = o.hardKeyboardHidden;
        this.navigation = o.navigation;
        this.navigationHidden = o.navigationHidden;
        this.orientation = o.orientation;
        this.screenLayout = o.screenLayout;
        this.colorMode = o.colorMode;
        this.uiMode = o.uiMode;
        this.screenWidthDp = o.screenWidthDp;
        this.screenHeightDp = o.screenHeightDp;
        this.smallestScreenWidthDp = o.smallestScreenWidthDp;
        this.densityDpi = o.densityDpi;
        this.compatScreenWidthDp = o.compatScreenWidthDp;
        this.compatScreenHeightDp = o.compatScreenHeightDp;
        this.compatSmallestScreenWidthDp = o.compatSmallestScreenWidthDp;
        this.assetsSeq = o.assetsSeq;
        this.seq = o.seq;
        this.windowConfiguration.setTo(o.windowConfiguration);
        this.fontWeightAdjustment = o.fontWeightAdjustment;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("{");
        sb.append(this.fontScale);
        sb.append(" ");
        int i = this.mcc;
        if (i != 0) {
            sb.append(i);
            sb.append("mcc");
        } else {
            sb.append("?mcc");
        }
        int i2 = this.mnc;
        if (i2 != 65535) {
            sb.append(i2);
            sb.append("mnc");
        } else {
            sb.append("?mnc");
        }
        fixUpLocaleList();
        if (!this.mLocaleList.isEmpty()) {
            sb.append(" ");
            sb.append(this.mLocaleList);
        } else {
            sb.append(" ?localeList");
        }
        int i3 = this.mGrammaticalGender;
        if (i3 != 0) {
            switch (i3) {
                case 0:
                    sb.append(" ?grgend");
                    break;
                case 1:
                    sb.append(" neuter");
                    break;
                case 2:
                    sb.append(" feminine");
                    break;
                case 3:
                    sb.append(" masculine");
                    break;
            }
        }
        int layoutDir = this.screenLayout & 192;
        switch (layoutDir) {
            case 0:
                sb.append(" ?layoutDir");
                break;
            case 64:
                sb.append(" ldltr");
                break;
            case 128:
                sb.append(" ldrtl");
                break;
            default:
                sb.append(" layoutDir=");
                sb.append(layoutDir >> 6);
                break;
        }
        if (this.smallestScreenWidthDp != 0) {
            sb.append(" sw");
            sb.append(this.smallestScreenWidthDp);
            sb.append("dp");
        } else {
            sb.append(" ?swdp");
        }
        if (this.screenWidthDp != 0) {
            sb.append(" w");
            sb.append(this.screenWidthDp);
            sb.append("dp");
        } else {
            sb.append(" ?wdp");
        }
        if (this.screenHeightDp != 0) {
            sb.append(" h");
            sb.append(this.screenHeightDp);
            sb.append("dp");
        } else {
            sb.append(" ?hdp");
        }
        if (this.densityDpi != 0) {
            sb.append(" ");
            sb.append(this.densityDpi);
            sb.append("dpi");
        } else {
            sb.append(" ?density");
        }
        switch (this.screenLayout & 15) {
            case 0:
                sb.append(" ?lsize");
                break;
            case 1:
                sb.append(" smll");
                break;
            case 2:
                sb.append(" nrml");
                break;
            case 3:
                sb.append(" lrg");
                break;
            case 4:
                sb.append(" xlrg");
                break;
            default:
                sb.append(" layoutSize=");
                sb.append(this.screenLayout & 15);
                break;
        }
        switch (this.screenLayout & 48) {
            case 0:
                sb.append(" ?long");
                break;
            case 16:
                break;
            case 32:
                sb.append(" long");
                break;
            default:
                sb.append(" layoutLong=");
                sb.append(this.screenLayout & 48);
                break;
        }
        switch (this.colorMode & 12) {
            case 0:
                sb.append(" ?ldr");
                break;
            case 4:
                break;
            case 8:
                sb.append(" hdr");
                break;
            default:
                sb.append(" dynamicRange=");
                sb.append(this.colorMode & 12);
                break;
        }
        switch (this.colorMode & 3) {
            case 0:
                sb.append(" ?wideColorGamut");
                break;
            case 1:
                break;
            case 2:
                sb.append(" widecg");
                break;
            default:
                sb.append(" wideColorGamut=");
                sb.append(this.colorMode & 3);
                break;
        }
        switch (this.orientation) {
            case 0:
                sb.append(" ?orien");
                break;
            case 1:
                sb.append(" port");
                break;
            case 2:
                sb.append(" land");
                break;
            default:
                sb.append(" orien=");
                sb.append(this.orientation);
                break;
        }
        switch (this.uiMode & 15) {
            case 0:
                sb.append(" ?uimode");
                break;
            case 1:
                break;
            case 2:
                sb.append(" desk");
                break;
            case 3:
                sb.append(" car");
                break;
            case 4:
                sb.append(" television");
                break;
            case 5:
                sb.append(" appliance");
                break;
            case 6:
                sb.append(" watch");
                break;
            case 7:
                sb.append(" vrheadset");
                break;
            default:
                sb.append(" uimode=");
                sb.append(this.uiMode & 15);
                break;
        }
        switch (this.uiMode & 48) {
            case 0:
                sb.append(" ?night");
                break;
            case 16:
                break;
            case 32:
                sb.append(" night");
                break;
            default:
                sb.append(" night=");
                sb.append(this.uiMode & 48);
                break;
        }
        switch (this.touchscreen) {
            case 0:
                sb.append(" ?touch");
                break;
            case 1:
                sb.append(" -touch");
                break;
            case 2:
                sb.append(" stylus");
                break;
            case 3:
                sb.append(" finger");
                break;
            default:
                sb.append(" touch=");
                sb.append(this.touchscreen);
                break;
        }
        switch (this.keyboard) {
            case 0:
                sb.append(" ?keyb");
                break;
            case 1:
                sb.append(" -keyb");
                break;
            case 2:
                sb.append(" qwerty");
                break;
            case 3:
                sb.append(" 12key");
                break;
            default:
                sb.append(" keys=");
                sb.append(this.keyboard);
                break;
        }
        switch (this.keyboardHidden) {
            case 0:
                sb.append("/?");
                break;
            case 1:
                sb.append("/v");
                break;
            case 2:
                sb.append("/h");
                break;
            case 3:
                sb.append("/s");
                break;
            default:
                sb.append("/");
                sb.append(this.keyboardHidden);
                break;
        }
        switch (this.hardKeyboardHidden) {
            case 0:
                sb.append("/?");
                break;
            case 1:
                sb.append("/v");
                break;
            case 2:
                sb.append("/h");
                break;
            default:
                sb.append("/");
                sb.append(this.hardKeyboardHidden);
                break;
        }
        switch (this.navigation) {
            case 0:
                sb.append(" ?nav");
                break;
            case 1:
                sb.append(" -nav");
                break;
            case 2:
                sb.append(" dpad");
                break;
            case 3:
                sb.append(" tball");
                break;
            case 4:
                sb.append(" wheel");
                break;
            default:
                sb.append(" nav=");
                sb.append(this.navigation);
                break;
        }
        switch (this.navigationHidden) {
            case 0:
                sb.append("/?");
                break;
            case 1:
                sb.append("/v");
                break;
            case 2:
                sb.append("/h");
                break;
            default:
                sb.append("/");
                sb.append(this.navigationHidden);
                break;
        }
        sb.append(" winConfig=");
        sb.append(this.windowConfiguration);
        if (this.assetsSeq != 0) {
            sb.append(" as.").append(this.assetsSeq);
        }
        if (this.seq != 0) {
            sb.append(" s.").append(this.seq);
        }
        if (this.fontWeightAdjustment != Integer.MAX_VALUE) {
            sb.append(" fontWeightAdjustment=");
            sb.append(this.fontWeightAdjustment);
        } else {
            sb.append(" ?fontWeightAdjustment");
        }
        sb.append('}');
        return sb.toString();
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long fieldId, boolean persisted, boolean critical) {
        WindowConfiguration windowConfiguration;
        long token = protoOutputStream.start(fieldId);
        if (!critical) {
            protoOutputStream.write(1108101562369L, this.fontScale);
            protoOutputStream.write(1155346202626L, this.mcc);
            protoOutputStream.write(1155346202627L, this.mnc);
            LocaleList localeList = this.mLocaleList;
            if (localeList != null) {
                protoOutputStream.write(1138166333460L, localeList.toLanguageTags());
            }
            protoOutputStream.write(1155346202629L, this.screenLayout);
            protoOutputStream.write(1155346202630L, this.colorMode);
            protoOutputStream.write(1155346202631L, this.touchscreen);
            protoOutputStream.write(1155346202632L, this.keyboard);
            protoOutputStream.write(ConfigurationProto.KEYBOARD_HIDDEN, this.keyboardHidden);
            protoOutputStream.write(ConfigurationProto.HARD_KEYBOARD_HIDDEN, this.hardKeyboardHidden);
            protoOutputStream.write(ConfigurationProto.NAVIGATION, this.navigation);
            protoOutputStream.write(ConfigurationProto.NAVIGATION_HIDDEN, this.navigationHidden);
            protoOutputStream.write(ConfigurationProto.UI_MODE, this.uiMode);
            protoOutputStream.write(ConfigurationProto.SMALLEST_SCREEN_WIDTH_DP, this.smallestScreenWidthDp);
            protoOutputStream.write(ConfigurationProto.DENSITY_DPI, this.densityDpi);
            if (!persisted && (windowConfiguration = this.windowConfiguration) != null) {
                windowConfiguration.dumpDebug(protoOutputStream, 1146756268051L);
            }
            protoOutputStream.write(ConfigurationProto.FONT_WEIGHT_ADJUSTMENT, this.fontWeightAdjustment);
        }
        protoOutputStream.write(ConfigurationProto.ORIENTATION, this.orientation);
        protoOutputStream.write(ConfigurationProto.SCREEN_WIDTH_DP, this.screenWidthDp);
        protoOutputStream.write(ConfigurationProto.SCREEN_HEIGHT_DP, this.screenHeightDp);
        protoOutputStream.write(ConfigurationProto.GRAMMATICAL_GENDER, this.mGrammaticalGender);
        protoOutputStream.end(token);
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long fieldId) {
        dumpDebug(protoOutputStream, fieldId, false, false);
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long fieldId, boolean critical) {
        dumpDebug(protoOutputStream, fieldId, false, critical);
    }

    /* JADX WARN: Removed duplicated region for block: B:111:0x036b A[Catch: IllformedLocaleException -> 0x0396, all -> 0x03c9, TRY_ENTER, TryCatch #9 {IllformedLocaleException -> 0x0396, blocks: (B:111:0x036b, B:112:0x0392), top: B:164:0x0369 }] */
    /* JADX WARN: Removed duplicated region for block: B:112:0x0392 A[Catch: IllformedLocaleException -> 0x0396, all -> 0x03c9, TRY_LEAVE, TryCatch #9 {IllformedLocaleException -> 0x0396, blocks: (B:111:0x036b, B:112:0x0392), top: B:164:0x0369 }] */
    /* JADX WARN: Removed duplicated region for block: B:148:0x0448  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void readFromProto(ProtoInputStream protoInputStream, long fieldId) throws IOException {
        long token;
        String str;
        long token2;
        String language;
        String variant;
        String str2;
        String str3;
        String country;
        String script;
        WireTypeMismatchException wireTypeMismatchException;
        int inListIndex;
        String language2;
        String country2;
        String variant2;
        String script2;
        String str4;
        String str5;
        String str6;
        Configuration configuration = this;
        ProtoInputStream protoInputStream2 = protoInputStream;
        String str7 = "Repeated locale (";
        String str8 = "";
        long token3 = protoInputStream.start(fieldId);
        List<Locale> list = new ArrayList<>();
        while (true) {
            try {
                if (protoInputStream.nextField() == -1) {
                    long token4 = token3;
                    if (list.size() > 0) {
                        configuration.setLocales(new LocaleList((Locale[]) list.toArray(new Locale[list.size()])));
                    }
                    protoInputStream2.end(token4);
                    return;
                }
                try {
                    int fieldNumber = protoInputStream.getFieldNumber();
                    String str9 = TAG;
                    switch (fieldNumber) {
                        case 1:
                            str = str8;
                            token2 = token3;
                            configuration.fontScale = protoInputStream2.readFloat(1108101562369L);
                            break;
                        case 2:
                            str = str8;
                            token2 = token3;
                            configuration.mcc = protoInputStream2.readInt(1155346202626L);
                            break;
                        case 3:
                            str = str8;
                            token2 = token3;
                            protoInputStream2 = protoInputStream;
                            try {
                                configuration = this;
                            } catch (Throwable th) {
                                th = th;
                                configuration = this;
                            }
                            try {
                                configuration.mnc = protoInputStream2.readInt(1155346202627L);
                                break;
                            } catch (Throwable th2) {
                                th = th2;
                                token = token2;
                                if (list.size() > 0) {
                                }
                                protoInputStream2.end(token);
                                throw th;
                            }
                            break;
                        case 4:
                            long token5 = token3;
                            try {
                                long localeToken = protoInputStream2.start(2246267895812L);
                                String language3 = str8;
                                String country3 = str8;
                                String variant3 = str8;
                                String language4 = language3;
                                String country4 = country3;
                                String variant4 = variant3;
                                String script3 = str8;
                                for (int i = -1; protoInputStream.nextField() != i; i = -1) {
                                    try {
                                        try {
                                            switch (protoInputStream.getFieldNumber()) {
                                                case 1:
                                                    str6 = str9;
                                                    String language5 = protoInputStream2.readString(1138166333441L);
                                                    language4 = language5;
                                                    break;
                                                case 2:
                                                    str6 = str9;
                                                    String country5 = protoInputStream2.readString(1138166333442L);
                                                    country4 = country5;
                                                    break;
                                                case 3:
                                                    str6 = str9;
                                                    String variant5 = protoInputStream2.readString(1138166333443L);
                                                    variant4 = variant5;
                                                    break;
                                                case 4:
                                                    str6 = str9;
                                                    try {
                                                        script3 = protoInputStream2.readString(1138166333444L);
                                                        break;
                                                    } catch (WireTypeMismatchException e) {
                                                        wtme = e;
                                                        language = language4;
                                                        country = country4;
                                                        variant = variant4;
                                                        token2 = token5;
                                                        script = script3;
                                                        str2 = str7;
                                                        str3 = str6;
                                                        try {
                                                            throw wtme;
                                                        } catch (Throwable wtme) {
                                                            wireTypeMismatchException = wtme;
                                                            protoInputStream.end(localeToken);
                                                            try {
                                                                Locale locale = new Locale.Builder().setLanguage(language).setRegion(country).setVariant(variant).setScript(script).build();
                                                                inListIndex = list.indexOf(locale);
                                                                try {
                                                                    if (inListIndex == -1) {
                                                                        Slog.wtf(str3, str2 + list.get(inListIndex) + ") found when trying to add: " + locale.toString());
                                                                    } else {
                                                                        list.add(locale);
                                                                    }
                                                                } catch (IllformedLocaleException e2) {
                                                                    Slog.m96e(str3, "readFromProto error building locale with: language-" + language + ";country-" + country + ";variant-" + variant + ";script-" + script);
                                                                    throw wireTypeMismatchException;
                                                                }
                                                            } catch (IllformedLocaleException e3) {
                                                            }
                                                            throw wireTypeMismatchException;
                                                        }
                                                    } catch (Throwable th3) {
                                                        wireTypeMismatchException = th3;
                                                        language = language4;
                                                        country = country4;
                                                        variant = variant4;
                                                        token2 = token5;
                                                        script = script3;
                                                        str2 = str7;
                                                        str3 = str6;
                                                        protoInputStream.end(localeToken);
                                                        Locale locale2 = new Locale.Builder().setLanguage(language).setRegion(country).setVariant(variant).setScript(script).build();
                                                        inListIndex = list.indexOf(locale2);
                                                        if (inListIndex == -1) {
                                                        }
                                                        throw wireTypeMismatchException;
                                                    }
                                                default:
                                                    str6 = str9;
                                                    break;
                                            }
                                            str9 = str6;
                                        } catch (WireTypeMismatchException e4) {
                                            wtme = e4;
                                            language = language4;
                                            variant = variant4;
                                            str2 = str7;
                                            str3 = str9;
                                            country = country4;
                                            script = script3;
                                            token2 = token5;
                                        } catch (Throwable th4) {
                                            wireTypeMismatchException = th4;
                                            language = language4;
                                            variant = variant4;
                                            str2 = str7;
                                            str3 = str9;
                                            country = country4;
                                            script = script3;
                                            token2 = token5;
                                        }
                                    } catch (WireTypeMismatchException e5) {
                                        wtme = e5;
                                        language = language4;
                                        variant = variant4;
                                        str2 = str7;
                                        str3 = str9;
                                        country = country4;
                                        script = script3;
                                        token2 = token5;
                                    } catch (Throwable th5) {
                                        language = language4;
                                        variant = variant4;
                                        str2 = str7;
                                        str3 = str9;
                                        country = country4;
                                        script = script3;
                                        token2 = token5;
                                        wireTypeMismatchException = th5;
                                    }
                                }
                                String str10 = str9;
                                protoInputStream2.end(localeToken);
                                try {
                                    language2 = language4;
                                    try {
                                        country2 = country4;
                                        try {
                                            str = str8;
                                            variant2 = variant4;
                                            try {
                                                token2 = token5;
                                                script2 = script3;
                                                try {
                                                    try {
                                                        Locale locale3 = new Locale.Builder().setLanguage(language2).setRegion(country2).setVariant(variant2).setScript(script2).build();
                                                        int inListIndex2 = list.indexOf(locale3);
                                                        if (inListIndex2 != -1) {
                                                            StringBuilder append = new StringBuilder().append(str7);
                                                            str4 = str7;
                                                            try {
                                                                str5 = str10;
                                                                try {
                                                                    Slog.wtf(str5, append.append(list.get(inListIndex2)).append(") found when trying to add: ").append(locale3.toString()).toString());
                                                                } catch (IllformedLocaleException e6) {
                                                                    Slog.m96e(str5, "readFromProto error building locale with: language-" + language2 + ";country-" + country2 + ";variant-" + variant2 + ";script-" + script2);
                                                                    configuration = this;
                                                                    protoInputStream2 = protoInputStream;
                                                                    str7 = str4;
                                                                    str8 = str;
                                                                    token3 = token2;
                                                                }
                                                            } catch (IllformedLocaleException e7) {
                                                                str5 = str10;
                                                                Slog.m96e(str5, "readFromProto error building locale with: language-" + language2 + ";country-" + country2 + ";variant-" + variant2 + ";script-" + script2);
                                                                configuration = this;
                                                                protoInputStream2 = protoInputStream;
                                                                str7 = str4;
                                                                str8 = str;
                                                                token3 = token2;
                                                            }
                                                        } else {
                                                            str4 = str7;
                                                            list.add(locale3);
                                                        }
                                                    } catch (Throwable th6) {
                                                        th = th6;
                                                        configuration = this;
                                                        protoInputStream2 = protoInputStream;
                                                        token = token2;
                                                        if (list.size() > 0) {
                                                            configuration.setLocales(new LocaleList((Locale[]) list.toArray(new Locale[list.size()])));
                                                        }
                                                        protoInputStream2.end(token);
                                                        throw th;
                                                    }
                                                } catch (IllformedLocaleException e8) {
                                                    str4 = str7;
                                                }
                                            } catch (IllformedLocaleException e9) {
                                                token2 = token5;
                                                script2 = script3;
                                                str4 = str7;
                                                str5 = str10;
                                            }
                                        } catch (IllformedLocaleException e10) {
                                            str = str8;
                                            token2 = token5;
                                            variant2 = variant4;
                                            script2 = script3;
                                            str4 = str7;
                                            str5 = str10;
                                        }
                                    } catch (IllformedLocaleException e11) {
                                        str = str8;
                                        country2 = country4;
                                        variant2 = variant4;
                                        token2 = token5;
                                        script2 = script3;
                                        str4 = str7;
                                        str5 = str10;
                                    }
                                } catch (IllformedLocaleException e12) {
                                    str = str8;
                                    language2 = language4;
                                    country2 = country4;
                                    variant2 = variant4;
                                    token2 = token5;
                                    script2 = script3;
                                    str4 = str7;
                                    str5 = str10;
                                }
                                configuration = this;
                                protoInputStream2 = protoInputStream;
                                str7 = str4;
                            } catch (Throwable th7) {
                                th = th7;
                                configuration = this;
                                protoInputStream2 = protoInputStream;
                                token = token5;
                                if (list.size() > 0) {
                                }
                                protoInputStream2.end(token);
                                throw th;
                            }
                        case 5:
                            long token6 = token3;
                            configuration.screenLayout = protoInputStream2.readInt(1155346202629L);
                            str = str8;
                            token2 = token6;
                            break;
                        case 6:
                            configuration.colorMode = protoInputStream2.readInt(1155346202630L);
                            str = str8;
                            token2 = token3;
                            break;
                        case 7:
                            configuration.touchscreen = protoInputStream2.readInt(1155346202631L);
                            str = str8;
                            token2 = token3;
                            break;
                        case 8:
                            configuration.keyboard = protoInputStream2.readInt(1155346202632L);
                            str = str8;
                            token2 = token3;
                            break;
                        case 9:
                            configuration.keyboardHidden = protoInputStream2.readInt(ConfigurationProto.KEYBOARD_HIDDEN);
                            str = str8;
                            token2 = token3;
                            break;
                        case 10:
                            configuration.hardKeyboardHidden = protoInputStream2.readInt(ConfigurationProto.HARD_KEYBOARD_HIDDEN);
                            str = str8;
                            token2 = token3;
                            break;
                        case 11:
                            configuration.navigation = protoInputStream2.readInt(ConfigurationProto.NAVIGATION);
                            str = str8;
                            token2 = token3;
                            break;
                        case 12:
                            configuration.navigationHidden = protoInputStream2.readInt(ConfigurationProto.NAVIGATION_HIDDEN);
                            str = str8;
                            token2 = token3;
                            break;
                        case 13:
                            configuration.orientation = protoInputStream2.readInt(ConfigurationProto.ORIENTATION);
                            str = str8;
                            token2 = token3;
                            break;
                        case 14:
                            configuration.uiMode = protoInputStream2.readInt(ConfigurationProto.UI_MODE);
                            str = str8;
                            token2 = token3;
                            break;
                        case 15:
                            configuration.screenWidthDp = protoInputStream2.readInt(ConfigurationProto.SCREEN_WIDTH_DP);
                            str = str8;
                            token2 = token3;
                            break;
                        case 16:
                            configuration.screenHeightDp = protoInputStream2.readInt(ConfigurationProto.SCREEN_HEIGHT_DP);
                            str = str8;
                            token2 = token3;
                            break;
                        case 17:
                            configuration.smallestScreenWidthDp = protoInputStream2.readInt(ConfigurationProto.SMALLEST_SCREEN_WIDTH_DP);
                            str = str8;
                            token2 = token3;
                            break;
                        case 18:
                            configuration.densityDpi = protoInputStream2.readInt(ConfigurationProto.DENSITY_DPI);
                            str = str8;
                            token2 = token3;
                            break;
                        case 19:
                            configuration.windowConfiguration.readFromProto(protoInputStream2, 1146756268051L);
                            str = str8;
                            token2 = token3;
                            break;
                        case 20:
                            long token7 = token3;
                            try {
                                try {
                                    configuration.setLocales(LocaleList.forLanguageTags(protoInputStream2.readString(1138166333460L)));
                                    str = str8;
                                    token2 = token7;
                                    break;
                                } catch (Exception e13) {
                                    Slog.m95e(TAG, "error parsing locale list in configuration.", e13);
                                    str = str8;
                                    token2 = token7;
                                    break;
                                }
                            } catch (Throwable th8) {
                                th = th8;
                                token = token7;
                                if (list.size() > 0) {
                                }
                                protoInputStream2.end(token);
                                throw th;
                            }
                            break;
                        case 21:
                            configuration.fontWeightAdjustment = protoInputStream2.readInt(ConfigurationProto.FONT_WEIGHT_ADJUSTMENT);
                            str = str8;
                            token2 = token3;
                            break;
                        case 22:
                            try {
                                configuration.mGrammaticalGender = protoInputStream2.readInt(ConfigurationProto.GRAMMATICAL_GENDER);
                                str = str8;
                                token2 = token3;
                                break;
                            } catch (Throwable th9) {
                                th = th9;
                                token = token3;
                                if (list.size() > 0) {
                                }
                                protoInputStream2.end(token);
                                throw th;
                            }
                        default:
                            str = str8;
                            token2 = token3;
                            break;
                    }
                    str8 = str;
                    token3 = token2;
                } catch (Throwable th10) {
                    th = th10;
                    token = token3;
                }
            } catch (Throwable th11) {
                th = th11;
                token = token3;
            }
        }
    }

    public void writeResConfigToProto(ProtoOutputStream protoOutputStream, long fieldId, DisplayMetrics metrics) {
        int width;
        int height;
        if (metrics.widthPixels >= metrics.heightPixels) {
            width = metrics.widthPixels;
            height = metrics.heightPixels;
        } else {
            width = metrics.heightPixels;
            height = metrics.widthPixels;
        }
        long token = protoOutputStream.start(fieldId);
        dumpDebug(protoOutputStream, 1146756268033L);
        protoOutputStream.write(1155346202626L, Build.VERSION.RESOURCES_SDK_INT);
        protoOutputStream.write(1155346202627L, width);
        protoOutputStream.write(1155346202628L, height);
        protoOutputStream.end(token);
    }

    public static String uiModeToString(int uiMode) {
        switch (uiMode) {
            case 0:
                return "UI_MODE_TYPE_UNDEFINED";
            case 1:
                return "UI_MODE_TYPE_NORMAL";
            case 2:
                return "UI_MODE_TYPE_DESK";
            case 3:
                return "UI_MODE_TYPE_CAR";
            case 4:
                return "UI_MODE_TYPE_TELEVISION";
            case 5:
                return "UI_MODE_TYPE_APPLIANCE";
            case 6:
                return "UI_MODE_TYPE_WATCH";
            case 7:
                return "UI_MODE_TYPE_VR_HEADSET";
            default:
                return Integer.toString(uiMode);
        }
    }

    public void setToDefaults() {
        this.fontScale = 1.0f;
        this.mnc = 0;
        this.mcc = 0;
        this.mLocaleList = LocaleList.getEmptyLocaleList();
        this.locale = null;
        this.userSetLocale = false;
        this.touchscreen = 0;
        this.keyboard = 0;
        this.keyboardHidden = 0;
        this.hardKeyboardHidden = 0;
        this.navigation = 0;
        this.navigationHidden = 0;
        this.orientation = 0;
        this.screenLayout = 0;
        this.colorMode = 0;
        this.uiMode = 0;
        this.compatScreenWidthDp = 0;
        this.screenWidthDp = 0;
        this.compatScreenHeightDp = 0;
        this.screenHeightDp = 0;
        this.compatSmallestScreenWidthDp = 0;
        this.smallestScreenWidthDp = 0;
        this.densityDpi = 0;
        this.assetsSeq = 0;
        this.seq = 0;
        this.windowConfiguration.setToDefaults();
        this.fontWeightAdjustment = Integer.MAX_VALUE;
        this.mGrammaticalGender = 0;
    }

    public void unset() {
        setToDefaults();
        this.fontScale = 0.0f;
    }

    @Deprecated
    public void makeDefault() {
        setToDefaults();
    }

    public int updateFrom(Configuration delta) {
        int i;
        int changed = 0;
        float f = delta.fontScale;
        if (f > 0.0f && this.fontScale != f) {
            changed = 0 | 1073741824;
            this.fontScale = f;
        }
        int i2 = delta.mcc;
        if (i2 != 0 && this.mcc != i2) {
            changed |= 1;
            this.mcc = i2;
        }
        int i3 = delta.mnc;
        if (i3 != 0 && this.mnc != i3) {
            changed |= 2;
            this.mnc = i3;
        }
        fixUpLocaleList();
        delta.fixUpLocaleList();
        if (!delta.mLocaleList.isEmpty() && !this.mLocaleList.equals(delta.mLocaleList)) {
            changed |= 4;
            this.mLocaleList = delta.mLocaleList;
            if (!delta.locale.equals(this.locale)) {
                Locale locale = (Locale) delta.locale.clone();
                this.locale = locale;
                changed |= 8192;
                setLayoutDirection(locale);
            }
        }
        int deltaScreenLayoutDir = delta.screenLayout & 192;
        if (deltaScreenLayoutDir != 0) {
            int i4 = this.screenLayout;
            if (deltaScreenLayoutDir != (i4 & 192)) {
                this.screenLayout = (i4 & (-193)) | deltaScreenLayoutDir;
                changed |= 8192;
            }
        }
        if (delta.userSetLocale && (!this.userSetLocale || (changed & 4) != 0)) {
            changed |= 4;
            this.userSetLocale = true;
        }
        int i5 = delta.touchscreen;
        if (i5 != 0 && this.touchscreen != i5) {
            changed |= 8;
            this.touchscreen = i5;
        }
        int i6 = delta.keyboard;
        if (i6 != 0 && this.keyboard != i6) {
            changed |= 16;
            this.keyboard = i6;
        }
        int i7 = delta.keyboardHidden;
        if (i7 != 0 && this.keyboardHidden != i7) {
            changed |= 32;
            this.keyboardHidden = i7;
        }
        int i8 = delta.hardKeyboardHidden;
        if (i8 != 0 && this.hardKeyboardHidden != i8) {
            changed |= 32;
            this.hardKeyboardHidden = i8;
        }
        int i9 = delta.navigation;
        if (i9 != 0 && this.navigation != i9) {
            changed |= 64;
            this.navigation = i9;
        }
        int i10 = delta.navigationHidden;
        if (i10 != 0 && this.navigationHidden != i10) {
            changed |= 32;
            this.navigationHidden = i10;
        }
        int i11 = delta.orientation;
        if (i11 != 0 && this.orientation != i11) {
            changed |= 128;
            this.orientation = i11;
        }
        int i12 = delta.screenLayout;
        if ((i12 & 15) != 0) {
            int i13 = i12 & 15;
            int i14 = this.screenLayout;
            if (i13 != (i14 & 15)) {
                changed |= 256;
                this.screenLayout = (i12 & 15) | (i14 & (-16));
            }
        }
        int i15 = delta.screenLayout;
        if ((i15 & 48) != 0) {
            int i16 = i15 & 48;
            int i17 = this.screenLayout;
            if (i16 != (i17 & 48)) {
                changed |= 256;
                this.screenLayout = (i15 & 48) | (i17 & (-49));
            }
        }
        int i18 = delta.screenLayout;
        if ((i18 & 768) != 0) {
            int i19 = i18 & 768;
            int i20 = this.screenLayout;
            if (i19 != (i20 & 768)) {
                changed |= 256;
                this.screenLayout = (i18 & 768) | (i20 & (-769));
            }
        }
        int i21 = delta.screenLayout;
        int i22 = i21 & 268435456;
        int i23 = this.screenLayout;
        if (i22 != (i23 & 268435456) && i21 != 0) {
            changed |= 256;
            this.screenLayout = (i21 & 268435456) | ((-268435457) & i23);
        }
        int i24 = delta.colorMode;
        if ((i24 & 3) != 0) {
            int i25 = i24 & 3;
            int i26 = this.colorMode;
            if (i25 != (i26 & 3)) {
                changed |= 16384;
                this.colorMode = (i24 & 3) | (i26 & (-4));
            }
        }
        int i27 = delta.colorMode;
        if ((i27 & 12) != 0) {
            int i28 = i27 & 12;
            int i29 = this.colorMode;
            if (i28 != (i29 & 12)) {
                changed |= 16384;
                this.colorMode = (i27 & 12) | (i29 & (-13));
            }
        }
        int i30 = delta.uiMode;
        if (i30 != 0 && (i = this.uiMode) != i30) {
            changed |= 512;
            if ((i30 & 15) != 0) {
                this.uiMode = (i30 & 15) | (i & (-16));
            }
            int i31 = delta.uiMode;
            if ((i31 & 48) != 0) {
                this.uiMode = (i31 & 48) | (this.uiMode & (-49));
            }
        }
        int i32 = delta.screenWidthDp;
        if (i32 != 0 && this.screenWidthDp != i32) {
            changed |= 1024;
            this.screenWidthDp = i32;
        }
        int i33 = delta.screenHeightDp;
        if (i33 != 0 && this.screenHeightDp != i33) {
            changed |= 1024;
            this.screenHeightDp = i33;
        }
        int i34 = delta.smallestScreenWidthDp;
        if (i34 != 0 && this.smallestScreenWidthDp != i34) {
            changed |= 2048;
            this.smallestScreenWidthDp = i34;
        }
        int i35 = delta.densityDpi;
        if (i35 != 0 && this.densityDpi != i35) {
            changed |= 4096;
            this.densityDpi = i35;
        }
        int i36 = delta.compatScreenWidthDp;
        if (i36 != 0) {
            this.compatScreenWidthDp = i36;
        }
        int i37 = delta.compatScreenHeightDp;
        if (i37 != 0) {
            this.compatScreenHeightDp = i37;
        }
        int i38 = delta.compatSmallestScreenWidthDp;
        if (i38 != 0) {
            this.compatSmallestScreenWidthDp = i38;
        }
        int i39 = delta.assetsSeq;
        if (i39 != 0 && i39 != this.assetsSeq) {
            changed |= Integer.MIN_VALUE;
            this.assetsSeq = i39;
        }
        int i40 = delta.seq;
        if (i40 != 0) {
            this.seq = i40;
        }
        if (this.windowConfiguration.updateFrom(delta.windowConfiguration) != 0) {
            changed |= 536870912;
        }
        int i41 = delta.fontWeightAdjustment;
        if (i41 != Integer.MAX_VALUE && i41 != this.fontWeightAdjustment) {
            changed |= 268435456;
            this.fontWeightAdjustment = i41;
        }
        int i42 = delta.mGrammaticalGender;
        if (i42 != this.mGrammaticalGender) {
            int changed2 = changed | 32768;
            this.mGrammaticalGender = i42;
            return changed2;
        }
        return changed;
    }

    public void setTo(Configuration delta, int mask, int windowMask) {
        if ((1073741824 & mask) != 0) {
            this.fontScale = delta.fontScale;
        }
        if ((mask & 1) != 0) {
            this.mcc = delta.mcc;
        }
        if ((mask & 2) != 0) {
            this.mnc = delta.mnc;
        }
        if ((mask & 4) != 0) {
            LocaleList localeList = delta.mLocaleList;
            this.mLocaleList = localeList;
            if (!localeList.isEmpty() && !delta.locale.equals(this.locale)) {
                this.locale = (Locale) delta.locale.clone();
            }
        }
        if ((mask & 8192) != 0) {
            int deltaScreenLayoutDir = delta.screenLayout & 192;
            this.screenLayout = (this.screenLayout & (-193)) | deltaScreenLayoutDir;
        }
        int deltaScreenLayoutDir2 = mask & 4;
        if (deltaScreenLayoutDir2 != 0) {
            this.userSetLocale = delta.userSetLocale;
        }
        if ((mask & 8) != 0) {
            this.touchscreen = delta.touchscreen;
        }
        if ((mask & 16) != 0) {
            this.keyboard = delta.keyboard;
        }
        if ((mask & 32) != 0) {
            this.keyboardHidden = delta.keyboardHidden;
            this.hardKeyboardHidden = delta.hardKeyboardHidden;
            this.navigationHidden = delta.navigationHidden;
        }
        if ((mask & 64) != 0) {
            this.navigation = delta.navigation;
        }
        if ((mask & 128) != 0) {
            this.orientation = delta.orientation;
        }
        if ((mask & 256) != 0) {
            this.screenLayout |= delta.screenLayout & (-193);
        }
        if ((mask & 16384) != 0) {
            this.colorMode = delta.colorMode;
        }
        if ((mask & 512) != 0) {
            this.uiMode = delta.uiMode;
        }
        if ((mask & 1024) != 0) {
            this.screenWidthDp = delta.screenWidthDp;
            this.screenHeightDp = delta.screenHeightDp;
        }
        if ((mask & 2048) != 0) {
            this.smallestScreenWidthDp = delta.smallestScreenWidthDp;
        }
        if ((mask & 4096) != 0) {
            this.densityDpi = delta.densityDpi;
        }
        if ((Integer.MIN_VALUE & mask) != 0) {
            this.assetsSeq = delta.assetsSeq;
        }
        if ((536870912 & mask) != 0) {
            this.windowConfiguration.setTo(delta.windowConfiguration, windowMask);
        }
        if ((268435456 & mask) != 0) {
            this.fontWeightAdjustment = delta.fontWeightAdjustment;
        }
        if ((32768 & mask) != 0) {
            this.mGrammaticalGender = delta.mGrammaticalGender;
        }
    }

    public int diff(Configuration delta) {
        return diff(delta, false, false);
    }

    public int diffPublicOnly(Configuration delta) {
        return diff(delta, false, true);
    }

    public int diff(Configuration delta, boolean compareUndefined, boolean publicOnly) {
        int changed = 0;
        if ((compareUndefined || delta.fontScale > 0.0f) && this.fontScale != delta.fontScale) {
            changed = 0 | 1073741824;
        }
        if ((compareUndefined || delta.mcc != 0) && this.mcc != delta.mcc) {
            changed |= 1;
        }
        if ((compareUndefined || delta.mnc != 0) && this.mnc != delta.mnc) {
            changed |= 2;
        }
        fixUpLocaleList();
        delta.fixUpLocaleList();
        if ((compareUndefined || !delta.mLocaleList.isEmpty()) && !this.mLocaleList.equals(delta.mLocaleList)) {
            changed = changed | 4 | 8192;
        }
        int i = delta.screenLayout;
        int deltaScreenLayoutDir = i & 192;
        if ((compareUndefined || deltaScreenLayoutDir != 0) && deltaScreenLayoutDir != (this.screenLayout & 192)) {
            changed |= 8192;
        }
        if ((compareUndefined || delta.touchscreen != 0) && this.touchscreen != delta.touchscreen) {
            changed |= 8;
        }
        if ((compareUndefined || delta.keyboard != 0) && this.keyboard != delta.keyboard) {
            changed |= 16;
        }
        if ((compareUndefined || delta.keyboardHidden != 0) && this.keyboardHidden != delta.keyboardHidden) {
            changed |= 32;
        }
        if ((compareUndefined || delta.hardKeyboardHidden != 0) && this.hardKeyboardHidden != delta.hardKeyboardHidden) {
            changed |= 32;
        }
        if ((compareUndefined || delta.navigation != 0) && this.navigation != delta.navigation) {
            changed |= 64;
        }
        if ((compareUndefined || delta.navigationHidden != 0) && this.navigationHidden != delta.navigationHidden) {
            changed |= 32;
        }
        if ((compareUndefined || delta.orientation != 0) && this.orientation != delta.orientation) {
            changed |= 128;
        }
        if ((compareUndefined || getScreenLayoutNoDirection(i) != 0) && getScreenLayoutNoDirection(this.screenLayout) != getScreenLayoutNoDirection(delta.screenLayout)) {
            changed |= 256;
        }
        if ((compareUndefined || (delta.colorMode & 12) != 0) && (this.colorMode & 12) != (delta.colorMode & 12)) {
            changed |= 16384;
        }
        if ((compareUndefined || (delta.colorMode & 3) != 0) && (this.colorMode & 3) != (delta.colorMode & 3)) {
            changed |= 16384;
        }
        if ((compareUndefined || delta.uiMode != 0) && this.uiMode != delta.uiMode) {
            changed |= 512;
        }
        if ((compareUndefined || delta.screenWidthDp != 0) && this.screenWidthDp != delta.screenWidthDp) {
            changed |= 1024;
        }
        if ((compareUndefined || delta.screenHeightDp != 0) && this.screenHeightDp != delta.screenHeightDp) {
            changed |= 1024;
        }
        if ((compareUndefined || delta.smallestScreenWidthDp != 0) && this.smallestScreenWidthDp != delta.smallestScreenWidthDp) {
            changed |= 2048;
        }
        if ((compareUndefined || delta.densityDpi != 0) && this.densityDpi != delta.densityDpi) {
            changed |= 4096;
        }
        if ((compareUndefined || delta.assetsSeq != 0) && this.assetsSeq != delta.assetsSeq) {
            changed |= Integer.MIN_VALUE;
        }
        if (!publicOnly && this.windowConfiguration.diff(delta.windowConfiguration, compareUndefined) != 0) {
            changed |= 536870912;
        }
        if ((compareUndefined || delta.fontWeightAdjustment != Integer.MAX_VALUE) && this.fontWeightAdjustment != delta.fontWeightAdjustment) {
            changed |= 268435456;
        }
        if (this.mGrammaticalGender != delta.mGrammaticalGender) {
            return changed | 32768;
        }
        return changed;
    }

    public static boolean needNewResources(int configChanges, int interestingChanges) {
        return (configChanges & ((Integer.MIN_VALUE | interestingChanges) | 1073741824)) != 0;
    }

    public boolean isOtherSeqNewer(Configuration other) {
        int i;
        if (other == null) {
            return false;
        }
        int i2 = other.seq;
        if (i2 == 0 || (i = this.seq) == 0) {
            return true;
        }
        int diff = i2 - i;
        return Math.abs(diff) > 268435456 ? diff < 0 : diff > 0;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.fontScale);
        dest.writeInt(this.mcc);
        dest.writeInt(this.mnc);
        fixUpLocaleList();
        dest.writeTypedObject(this.mLocaleList, flags);
        if (this.userSetLocale) {
            dest.writeInt(1);
        } else {
            dest.writeInt(0);
        }
        dest.writeInt(this.touchscreen);
        dest.writeInt(this.keyboard);
        dest.writeInt(this.keyboardHidden);
        dest.writeInt(this.hardKeyboardHidden);
        dest.writeInt(this.navigation);
        dest.writeInt(this.navigationHidden);
        dest.writeInt(this.orientation);
        dest.writeInt(this.screenLayout);
        dest.writeInt(this.colorMode);
        dest.writeInt(this.uiMode);
        dest.writeInt(this.screenWidthDp);
        dest.writeInt(this.screenHeightDp);
        dest.writeInt(this.smallestScreenWidthDp);
        dest.writeInt(this.densityDpi);
        dest.writeInt(this.compatScreenWidthDp);
        dest.writeInt(this.compatScreenHeightDp);
        dest.writeInt(this.compatSmallestScreenWidthDp);
        this.windowConfiguration.writeToParcel(dest, flags);
        dest.writeInt(this.assetsSeq);
        dest.writeInt(this.seq);
        dest.writeInt(this.fontWeightAdjustment);
        dest.writeInt(this.mGrammaticalGender);
    }

    public void readFromParcel(Parcel source) {
        this.fontScale = source.readFloat();
        this.mcc = source.readInt();
        this.mnc = source.readInt();
        LocaleList localeList = (LocaleList) source.readTypedObject(LocaleList.CREATOR);
        this.mLocaleList = localeList;
        this.locale = localeList.get(0);
        this.userSetLocale = source.readInt() == 1;
        this.touchscreen = source.readInt();
        this.keyboard = source.readInt();
        this.keyboardHidden = source.readInt();
        this.hardKeyboardHidden = source.readInt();
        this.navigation = source.readInt();
        this.navigationHidden = source.readInt();
        this.orientation = source.readInt();
        this.screenLayout = source.readInt();
        this.colorMode = source.readInt();
        this.uiMode = source.readInt();
        this.screenWidthDp = source.readInt();
        this.screenHeightDp = source.readInt();
        this.smallestScreenWidthDp = source.readInt();
        this.densityDpi = source.readInt();
        this.compatScreenWidthDp = source.readInt();
        this.compatScreenHeightDp = source.readInt();
        this.compatSmallestScreenWidthDp = source.readInt();
        this.windowConfiguration.readFromParcel(source);
        this.assetsSeq = source.readInt();
        this.seq = source.readInt();
        this.fontWeightAdjustment = source.readInt();
        this.mGrammaticalGender = source.readInt();
    }

    private Configuration(Parcel source) {
        this.windowConfiguration = new WindowConfiguration();
        readFromParcel(source);
    }

    public boolean isNightModeActive() {
        return (this.uiMode & 48) == 32;
    }

    @Override // java.lang.Comparable
    public int compareTo(Configuration that) {
        float a = this.fontScale;
        float b = that.fontScale;
        if (a < b) {
            return -1;
        }
        if (a > b) {
            return 1;
        }
        int n = this.mcc - that.mcc;
        if (n != 0) {
            return n;
        }
        int n2 = this.mnc - that.mnc;
        if (n2 != 0) {
            return n2;
        }
        fixUpLocaleList();
        that.fixUpLocaleList();
        if (this.mLocaleList.isEmpty()) {
            if (!that.mLocaleList.isEmpty()) {
                return 1;
            }
        } else if (that.mLocaleList.isEmpty()) {
            return -1;
        } else {
            int minSize = Math.min(this.mLocaleList.size(), that.mLocaleList.size());
            for (int i = 0; i < minSize; i++) {
                Locale thisLocale = this.mLocaleList.get(i);
                Locale thatLocale = that.mLocaleList.get(i);
                int n3 = thisLocale.getLanguage().compareTo(thatLocale.getLanguage());
                if (n3 != 0) {
                    return n3;
                }
                int n4 = thisLocale.getCountry().compareTo(thatLocale.getCountry());
                if (n4 != 0) {
                    return n4;
                }
                int n5 = thisLocale.getVariant().compareTo(thatLocale.getVariant());
                if (n5 != 0) {
                    return n5;
                }
                int n6 = thisLocale.toLanguageTag().compareTo(thatLocale.toLanguageTag());
                if (n6 != 0) {
                    return n6;
                }
            }
            int n7 = this.mLocaleList.size() - that.mLocaleList.size();
            if (n7 != 0) {
                return n7;
            }
        }
        int minSize2 = this.mGrammaticalGender;
        int n8 = minSize2 - that.mGrammaticalGender;
        if (n8 != 0) {
            return n8;
        }
        int n9 = this.touchscreen - that.touchscreen;
        if (n9 != 0) {
            return n9;
        }
        int n10 = this.keyboard - that.keyboard;
        if (n10 != 0) {
            return n10;
        }
        int n11 = this.keyboardHidden - that.keyboardHidden;
        if (n11 != 0) {
            return n11;
        }
        int n12 = this.hardKeyboardHidden - that.hardKeyboardHidden;
        if (n12 != 0) {
            return n12;
        }
        int n13 = this.navigation - that.navigation;
        if (n13 != 0) {
            return n13;
        }
        int n14 = this.navigationHidden - that.navigationHidden;
        if (n14 != 0) {
            return n14;
        }
        int n15 = this.orientation - that.orientation;
        if (n15 != 0) {
            return n15;
        }
        int n16 = this.colorMode - that.colorMode;
        if (n16 != 0) {
            return n16;
        }
        int n17 = this.screenLayout - that.screenLayout;
        if (n17 != 0) {
            return n17;
        }
        int n18 = this.uiMode - that.uiMode;
        if (n18 != 0) {
            return n18;
        }
        int n19 = this.screenWidthDp - that.screenWidthDp;
        if (n19 != 0) {
            return n19;
        }
        int n20 = this.screenHeightDp - that.screenHeightDp;
        if (n20 != 0) {
            return n20;
        }
        int n21 = this.smallestScreenWidthDp - that.smallestScreenWidthDp;
        if (n21 != 0) {
            return n21;
        }
        int n22 = this.densityDpi - that.densityDpi;
        if (n22 != 0) {
            return n22;
        }
        int n23 = this.assetsSeq - that.assetsSeq;
        if (n23 != 0) {
            return n23;
        }
        int n24 = this.windowConfiguration.compareTo(that.windowConfiguration);
        return n24 != 0 ? n24 : this.fontWeightAdjustment - that.fontWeightAdjustment;
    }

    public boolean equals(Configuration that) {
        if (that == null) {
            return false;
        }
        if (that != this && compareTo(that) != 0) {
            return false;
        }
        return true;
    }

    public boolean equals(Object that) {
        try {
            return equals((Configuration) that);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public int hashCode() {
        int result = (17 * 31) + Float.floatToIntBits(this.fontScale);
        return (((((((((((((((((((((((((((((((((((((((result * 31) + this.mcc) * 31) + this.mnc) * 31) + this.mLocaleList.hashCode()) * 31) + this.touchscreen) * 31) + this.keyboard) * 31) + this.keyboardHidden) * 31) + this.hardKeyboardHidden) * 31) + this.navigation) * 31) + this.navigationHidden) * 31) + this.orientation) * 31) + this.screenLayout) * 31) + this.colorMode) * 31) + this.uiMode) * 31) + this.screenWidthDp) * 31) + this.screenHeightDp) * 31) + this.smallestScreenWidthDp) * 31) + this.densityDpi) * 31) + this.assetsSeq) * 31) + this.fontWeightAdjustment) * 31) + this.mGrammaticalGender;
    }

    public int getGrammaticalGender() {
        return this.mGrammaticalGender;
    }

    public void setGrammaticalGender(int grammaticalGender) {
        this.mGrammaticalGender = grammaticalGender;
    }

    public LocaleList getLocales() {
        fixUpLocaleList();
        return this.mLocaleList;
    }

    public void setLocales(LocaleList locales) {
        LocaleList emptyLocaleList = locales == null ? LocaleList.getEmptyLocaleList() : locales;
        this.mLocaleList = emptyLocaleList;
        Locale locale = emptyLocaleList.get(0);
        this.locale = locale;
        setLayoutDirection(locale);
    }

    public void setLocale(Locale loc) {
        setLocales(loc == null ? LocaleList.getEmptyLocaleList() : new LocaleList(loc));
    }

    public void clearLocales() {
        this.mLocaleList = LocaleList.getEmptyLocaleList();
        this.locale = null;
    }

    public int getLayoutDirection() {
        return (this.screenLayout & 192) == 128 ? 1 : 0;
    }

    public void setLayoutDirection(Locale loc) {
        int layoutDirection = TextUtils.getLayoutDirectionFromLocale(loc) + 1;
        this.screenLayout = (this.screenLayout & (-193)) | (layoutDirection << 6);
    }

    private static int getScreenLayoutNoDirection(int screenLayout) {
        return screenLayout & (-193);
    }

    public boolean isScreenRound() {
        return (this.screenLayout & 768) == 512;
    }

    public boolean isScreenWideColorGamut() {
        return (this.colorMode & 3) == 2;
    }

    public boolean isScreenHdr() {
        return (this.colorMode & 12) == 8;
    }

    public static String localesToResourceQualifier(LocaleList locs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < locs.size(); i++) {
            Locale loc = locs.get(i);
            int l = loc.getLanguage().length();
            if (l != 0) {
                int s = loc.getScript().length();
                int c = loc.getCountry().length();
                int v = loc.getVariant().length();
                if (sb.length() != 0) {
                    sb.append(",");
                }
                if (l == 2 && s == 0 && ((c == 0 || c == 2) && v == 0)) {
                    sb.append(loc.getLanguage());
                    if (c == 2) {
                        sb.append("-r").append(loc.getCountry());
                    }
                } else {
                    sb.append("b+");
                    sb.append(loc.getLanguage());
                    if (s != 0) {
                        sb.append("+");
                        sb.append(loc.getScript());
                    }
                    if (c != 0) {
                        sb.append("+");
                        sb.append(loc.getCountry());
                    }
                    if (v != 0) {
                        sb.append("+");
                        sb.append(loc.getVariant());
                    }
                }
            }
        }
        return sb.toString();
    }

    public static String resourceQualifierString(Configuration config) {
        return resourceQualifierString(config, null);
    }

    public static String resourceQualifierString(Configuration config, DisplayMetrics metrics) {
        int width;
        int height;
        ArrayList<String> parts = new ArrayList<>();
        if (config.mcc != 0) {
            parts.add("mcc" + config.mcc);
            if (config.mnc != 0) {
                parts.add("mnc" + config.mnc);
            }
        }
        if (!config.mLocaleList.isEmpty()) {
            String resourceQualifier = localesToResourceQualifier(config.mLocaleList);
            if (!resourceQualifier.isEmpty()) {
                parts.add(resourceQualifier);
            }
        }
        switch (config.mGrammaticalGender) {
            case 1:
                parts.add("neuter");
                break;
            case 2:
                parts.add("feminine");
                break;
            case 3:
                parts.add("masculine");
                break;
        }
        switch (config.screenLayout & 192) {
            case 64:
                parts.add("ldltr");
                break;
            case 128:
                parts.add("ldrtl");
                break;
        }
        if (config.smallestScreenWidthDp != 0) {
            parts.add(XML_ATTR_SMALLEST_WIDTH + config.smallestScreenWidthDp + "dp");
        }
        if (config.screenWidthDp != 0) {
            parts.add("w" + config.screenWidthDp + "dp");
        }
        if (config.screenHeightDp != 0) {
            parts.add("h" + config.screenHeightDp + "dp");
        }
        switch (config.screenLayout & 15) {
            case 1:
                parts.add("small");
                break;
            case 2:
                parts.add(FontListParser.STYLE_NORMAL);
                break;
            case 3:
                parts.add(Slice.HINT_LARGE);
                break;
            case 4:
                parts.add("xlarge");
                break;
        }
        switch (config.screenLayout & 48) {
            case 16:
                parts.add("notlong");
                break;
            case 32:
                parts.add("long");
                break;
        }
        switch (config.screenLayout & 768) {
            case 256:
                parts.add("notround");
                break;
            case 512:
                parts.add("round");
                break;
        }
        switch (config.colorMode & 3) {
            case 1:
                parts.add("nowidecg");
                break;
            case 2:
                parts.add("widecg");
                break;
        }
        switch (config.colorMode & 12) {
            case 4:
                parts.add("lowdr");
                break;
            case 8:
                parts.add("highdr");
                break;
        }
        switch (config.orientation) {
            case 1:
                parts.add("port");
                break;
            case 2:
                parts.add("land");
                break;
        }
        String uiModeTypeString = getUiModeTypeString(config.uiMode & 15);
        if (uiModeTypeString != null) {
            parts.add(uiModeTypeString);
        }
        switch (config.uiMode & 48) {
            case 16:
                parts.add("notnight");
                break;
            case 32:
                parts.add(Camera.Parameters.SCENE_MODE_NIGHT);
                break;
        }
        switch (config.densityDpi) {
            case 0:
                break;
            case 120:
                parts.add("ldpi");
                break;
            case 160:
                parts.add("mdpi");
                break;
            case 213:
                parts.add("tvdpi");
                break;
            case 240:
                parts.add("hdpi");
                break;
            case 320:
                parts.add("xhdpi");
                break;
            case 480:
                parts.add("xxhdpi");
                break;
            case 640:
                parts.add("xxxhdpi");
                break;
            case DENSITY_DPI_ANY /* 65534 */:
                parts.add("anydpi");
                break;
            case 65535:
                parts.add("nodpi");
                break;
            default:
                parts.add(config.densityDpi + "dpi");
                break;
        }
        switch (config.touchscreen) {
            case 1:
                parts.add("notouch");
                break;
            case 3:
                parts.add("finger");
                break;
        }
        switch (config.keyboardHidden) {
            case 1:
                parts.add("keysexposed");
                break;
            case 2:
                parts.add("keyshidden");
                break;
            case 3:
                parts.add("keyssoft");
                break;
        }
        switch (config.keyboard) {
            case 1:
                parts.add("nokeys");
                break;
            case 2:
                parts.add("qwerty");
                break;
            case 3:
                parts.add("12key");
                break;
        }
        switch (config.navigationHidden) {
            case 1:
                parts.add("navexposed");
                break;
            case 2:
                parts.add("navhidden");
                break;
        }
        switch (config.navigation) {
            case 1:
                parts.add("nonav");
                break;
            case 2:
                parts.add("dpad");
                break;
            case 3:
                parts.add("trackball");
                break;
            case 4:
                parts.add("wheel");
                break;
        }
        if (metrics != null) {
            if (metrics.widthPixels >= metrics.heightPixels) {
                width = metrics.widthPixels;
                height = metrics.heightPixels;
            } else {
                width = metrics.heightPixels;
                height = metrics.widthPixels;
            }
            parts.add(width + "x" + height);
        }
        parts.add("v" + Build.VERSION.RESOURCES_SDK_INT);
        return TextUtils.join(NativeLibraryHelper.CLEAR_ABI_OVERRIDE, parts);
    }

    public static String getUiModeTypeString(int uiModeType) {
        switch (uiModeType) {
            case 2:
                return "desk";
            case 3:
                return "car";
            case 4:
                return "television";
            case 5:
                return "appliance";
            case 6:
                return "watch";
            case 7:
                return "vrheadset";
            default:
                return null;
        }
    }

    public static Configuration generateDelta(Configuration base, Configuration change) {
        Configuration delta = new Configuration();
        float f = base.fontScale;
        float f2 = change.fontScale;
        if (f != f2) {
            delta.fontScale = f2;
        }
        int i = base.mcc;
        int i2 = change.mcc;
        if (i != i2) {
            delta.mcc = i2;
        }
        int i3 = base.mnc;
        int i4 = change.mnc;
        if (i3 != i4) {
            delta.mnc = i4;
        }
        base.fixUpLocaleList();
        change.fixUpLocaleList();
        if (!base.mLocaleList.equals(change.mLocaleList)) {
            delta.mLocaleList = change.mLocaleList;
            delta.locale = change.locale;
        }
        int i5 = base.mGrammaticalGender;
        int i6 = change.mGrammaticalGender;
        if (i5 != i6) {
            delta.mGrammaticalGender = i6;
        }
        int i7 = base.touchscreen;
        int i8 = change.touchscreen;
        if (i7 != i8) {
            delta.touchscreen = i8;
        }
        int i9 = base.keyboard;
        int i10 = change.keyboard;
        if (i9 != i10) {
            delta.keyboard = i10;
        }
        int i11 = base.keyboardHidden;
        int i12 = change.keyboardHidden;
        if (i11 != i12) {
            delta.keyboardHidden = i12;
        }
        int i13 = base.navigation;
        int i14 = change.navigation;
        if (i13 != i14) {
            delta.navigation = i14;
        }
        int i15 = base.navigationHidden;
        int i16 = change.navigationHidden;
        if (i15 != i16) {
            delta.navigationHidden = i16;
        }
        int i17 = base.orientation;
        int i18 = change.orientation;
        if (i17 != i18) {
            delta.orientation = i18;
        }
        int i19 = base.screenLayout & 15;
        int i20 = change.screenLayout;
        if (i19 != (i20 & 15)) {
            delta.screenLayout |= i20 & 15;
        }
        int i21 = base.screenLayout & 192;
        int i22 = change.screenLayout;
        if (i21 != (i22 & 192)) {
            delta.screenLayout |= i22 & 192;
        }
        int i23 = base.screenLayout & 48;
        int i24 = change.screenLayout;
        if (i23 != (i24 & 48)) {
            delta.screenLayout |= i24 & 48;
        }
        int i25 = base.screenLayout & 768;
        int i26 = change.screenLayout;
        if (i25 != (i26 & 768)) {
            delta.screenLayout |= i26 & 768;
        }
        int i27 = base.colorMode & 3;
        int i28 = change.colorMode;
        if (i27 != (i28 & 3)) {
            delta.colorMode |= i28 & 3;
        }
        int i29 = base.colorMode & 12;
        int i30 = change.colorMode;
        if (i29 != (i30 & 12)) {
            delta.colorMode |= i30 & 12;
        }
        int i31 = base.uiMode & 15;
        int i32 = change.uiMode;
        if (i31 != (i32 & 15)) {
            delta.uiMode |= i32 & 15;
        }
        int i33 = base.uiMode & 48;
        int i34 = change.uiMode;
        if (i33 != (i34 & 48)) {
            delta.uiMode |= i34 & 48;
        }
        int i35 = base.screenWidthDp;
        int i36 = change.screenWidthDp;
        if (i35 != i36) {
            delta.screenWidthDp = i36;
        }
        int i37 = base.screenHeightDp;
        int i38 = change.screenHeightDp;
        if (i37 != i38) {
            delta.screenHeightDp = i38;
        }
        int i39 = base.smallestScreenWidthDp;
        int i40 = change.smallestScreenWidthDp;
        if (i39 != i40) {
            delta.smallestScreenWidthDp = i40;
        }
        int i41 = base.densityDpi;
        int i42 = change.densityDpi;
        if (i41 != i42) {
            delta.densityDpi = i42;
        }
        int i43 = base.assetsSeq;
        int i44 = change.assetsSeq;
        if (i43 != i44) {
            delta.assetsSeq = i44;
        }
        if (!base.windowConfiguration.equals(change.windowConfiguration)) {
            delta.windowConfiguration.setTo(change.windowConfiguration);
        }
        int i45 = base.fontWeightAdjustment;
        int i46 = change.fontWeightAdjustment;
        if (i45 != i46) {
            delta.fontWeightAdjustment = i46;
        }
        return delta;
    }

    public static void readXmlAttrs(XmlPullParser parser, Configuration configOut) throws XmlPullParserException, IOException {
        configOut.fontScale = Float.intBitsToFloat(XmlUtils.readIntAttribute(parser, XML_ATTR_FONT_SCALE, 0));
        configOut.mcc = XmlUtils.readIntAttribute(parser, "mcc", 0);
        configOut.mnc = XmlUtils.readIntAttribute(parser, "mnc", 0);
        String localesStr = XmlUtils.readStringAttribute(parser, XML_ATTR_LOCALES);
        LocaleList forLanguageTags = LocaleList.forLanguageTags(localesStr);
        configOut.mLocaleList = forLanguageTags;
        configOut.locale = forLanguageTags.get(0);
        configOut.touchscreen = XmlUtils.readIntAttribute(parser, XML_ATTR_TOUCHSCREEN, 0);
        configOut.keyboard = XmlUtils.readIntAttribute(parser, "key", 0);
        configOut.keyboardHidden = XmlUtils.readIntAttribute(parser, XML_ATTR_KEYBOARD_HIDDEN, 0);
        configOut.hardKeyboardHidden = XmlUtils.readIntAttribute(parser, XML_ATTR_HARD_KEYBOARD_HIDDEN, 0);
        configOut.navigation = XmlUtils.readIntAttribute(parser, XML_ATTR_NAVIGATION, 0);
        configOut.navigationHidden = XmlUtils.readIntAttribute(parser, XML_ATTR_NAVIGATION_HIDDEN, 0);
        configOut.orientation = XmlUtils.readIntAttribute(parser, XML_ATTR_ORIENTATION, 0);
        configOut.screenLayout = XmlUtils.readIntAttribute(parser, XML_ATTR_SCREEN_LAYOUT, 0);
        configOut.colorMode = XmlUtils.readIntAttribute(parser, XML_ATTR_COLOR_MODE, 0);
        configOut.uiMode = XmlUtils.readIntAttribute(parser, XML_ATTR_UI_MODE, 0);
        configOut.screenWidthDp = XmlUtils.readIntAttribute(parser, "width", 0);
        configOut.screenHeightDp = XmlUtils.readIntAttribute(parser, "height", 0);
        configOut.smallestScreenWidthDp = XmlUtils.readIntAttribute(parser, XML_ATTR_SMALLEST_WIDTH, 0);
        configOut.densityDpi = XmlUtils.readIntAttribute(parser, XML_ATTR_DENSITY, 0);
        configOut.fontWeightAdjustment = XmlUtils.readIntAttribute(parser, XML_ATTR_FONT_WEIGHT_ADJUSTMENT, Integer.MAX_VALUE);
        configOut.mGrammaticalGender = XmlUtils.readIntAttribute(parser, XML_ATTR_GRAMMATICAL_GENDER, 0);
    }
}
