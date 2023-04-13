package android.content.res;

import android.content.res.Resources;
import android.graphics.Color;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.MathUtils;
import android.util.SparseArray;
import android.util.StateSet;
import android.util.Xml;
import com.android.ims.ImsConfig;
import com.android.internal.C4057R;
import com.android.internal.graphics.ColorUtils;
import com.android.internal.graphics.cam.Cam;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class ColorStateList extends ComplexColor implements Parcelable {
    private static final int DEFAULT_COLOR = -65536;
    private static final String TAG = "ColorStateList";
    private int mChangingConfigurations;
    private int[] mColors;
    private int mDefaultColor;
    private ColorStateListFactory mFactory;
    private boolean mIsOpaque;
    private int[][] mStateSpecs;
    private int[][] mThemeAttrs;
    private static final int[][] EMPTY = {new int[0]};
    private static final SparseArray<WeakReference<ColorStateList>> sCache = new SparseArray<>();
    public static final Parcelable.Creator<ColorStateList> CREATOR = new Parcelable.Creator<ColorStateList>() { // from class: android.content.res.ColorStateList.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ColorStateList[] newArray(int size) {
            return new ColorStateList[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ColorStateList createFromParcel(Parcel source) {
            int N = source.readInt();
            int[][] stateSpecs = new int[N];
            for (int i = 0; i < N; i++) {
                stateSpecs[i] = source.createIntArray();
            }
            int[] colors = source.createIntArray();
            return new ColorStateList(stateSpecs, colors);
        }
    };

    private ColorStateList() {
    }

    public ColorStateList(int[][] states, int[] colors) {
        this.mStateSpecs = states;
        this.mColors = colors;
        onColorsChanged();
    }

    public static ColorStateList valueOf(int color) {
        SparseArray<WeakReference<ColorStateList>> sparseArray = sCache;
        synchronized (sparseArray) {
            int index = sparseArray.indexOfKey(color);
            if (index >= 0) {
                ColorStateList cached = sparseArray.valueAt(index).get();
                if (cached != null) {
                    return cached;
                }
                sparseArray.removeAt(index);
            }
            int N = sparseArray.size();
            for (int i = N - 1; i >= 0; i--) {
                SparseArray<WeakReference<ColorStateList>> sparseArray2 = sCache;
                if (sparseArray2.valueAt(i).refersTo(null)) {
                    sparseArray2.removeAt(i);
                }
            }
            ColorStateList csl = new ColorStateList(EMPTY, new int[]{color});
            sCache.put(color, new WeakReference<>(csl));
            return csl;
        }
    }

    private ColorStateList(ColorStateList orig) {
        if (orig != null) {
            this.mChangingConfigurations = orig.mChangingConfigurations;
            this.mStateSpecs = orig.mStateSpecs;
            this.mDefaultColor = orig.mDefaultColor;
            this.mIsOpaque = orig.mIsOpaque;
            this.mThemeAttrs = (int[][]) orig.mThemeAttrs.clone();
            this.mColors = (int[]) orig.mColors.clone();
        }
    }

    @Deprecated
    public static ColorStateList createFromXml(Resources r, XmlPullParser parser) throws XmlPullParserException, IOException {
        return createFromXml(r, parser, null);
    }

    public static ColorStateList createFromXml(Resources r, XmlPullParser parser, Resources.Theme theme) throws XmlPullParserException, IOException {
        int type;
        AttributeSet attrs = Xml.asAttributeSet(parser);
        do {
            type = parser.next();
            if (type == 2) {
                break;
            }
        } while (type != 1);
        if (type != 2) {
            throw new XmlPullParserException("No start tag found");
        }
        return createFromXmlInner(r, parser, attrs, theme);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ColorStateList createFromXmlInner(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        String name = parser.getName();
        if (!name.equals("selector")) {
            throw new XmlPullParserException(parser.getPositionDescription() + ": invalid color state list tag " + name);
        }
        ColorStateList colorStateList = new ColorStateList();
        colorStateList.inflate(r, parser, attrs, theme);
        return colorStateList;
    }

    public ColorStateList withAlpha(int alpha) {
        int[] colors = new int[this.mColors.length];
        int len = colors.length;
        for (int i = 0; i < len; i++) {
            colors[i] = (this.mColors[i] & 16777215) | (alpha << 24);
        }
        return new ColorStateList(this.mStateSpecs, colors);
    }

    public ColorStateList withLStar(float lStar) {
        int[] colors = new int[this.mColors.length];
        int len = colors.length;
        for (int i = 0; i < len; i++) {
            colors[i] = modulateColor(this.mColors[i], 1.0f, lStar);
        }
        return new ColorStateList(this.mStateSpecs, colors);
    }

    private void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        boolean hasUnresolvedAttrs;
        int innerDepth;
        boolean hasUnresolvedAttrs2;
        boolean hasUnresolvedAttrs3;
        int i = 1;
        int innerDepth2 = parser.getDepth() + 1;
        int changingConfigurations = 0;
        int defaultColor = -65536;
        boolean hasUnresolvedAttrs4 = false;
        int[][] stateSpecList = (int[][]) ArrayUtils.newUnpaddedArray(int[].class, 20);
        int[][] themeAttrsList = new int[stateSpecList.length];
        int[] colorList = new int[stateSpecList.length];
        int listSize = 0;
        while (true) {
            int type = parser.next();
            if (type != i) {
                int depth = parser.getDepth();
                if (depth >= innerDepth2 || type != 3) {
                    if (type != 2 || depth > innerDepth2) {
                        innerDepth = innerDepth2;
                        hasUnresolvedAttrs2 = hasUnresolvedAttrs4;
                    } else if (!parser.getName().equals(ImsConfig.EXTRA_CHANGED_ITEM)) {
                        innerDepth = innerDepth2;
                        hasUnresolvedAttrs2 = hasUnresolvedAttrs4;
                    } else {
                        TypedArray a = Resources.obtainAttributes(r, theme, attrs, C4057R.styleable.ColorStateListItem);
                        int[] themeAttrs = a.extractThemeAttrs();
                        int innerDepth3 = innerDepth2;
                        int baseColor = a.getColor(0, Color.MAGENTA);
                        float alphaMod = a.getFloat(1, 1.0f);
                        float lStar = a.getFloat(2, -1.0f);
                        int changingConfigurations2 = changingConfigurations | a.getChangingConfigurations();
                        a.recycle();
                        int j = 0;
                        int numAttrs = attrs.getAttributeCount();
                        int[] stateSpec = new int[numAttrs];
                        int i2 = 0;
                        while (i2 < numAttrs) {
                            int numAttrs2 = numAttrs;
                            int stateResId = attrs.getAttributeNameResource(i2);
                            boolean hasUnresolvedAttrs5 = hasUnresolvedAttrs4;
                            if (stateResId != 16844359) {
                                switch (stateResId) {
                                    case 16843173:
                                    case 16843551:
                                        break;
                                    default:
                                        int j2 = j + 1;
                                        stateSpec[j] = attrs.getAttributeBooleanValue(i2, false) ? stateResId : -stateResId;
                                        j = j2;
                                        break;
                                }
                            }
                            i2++;
                            numAttrs = numAttrs2;
                            hasUnresolvedAttrs4 = hasUnresolvedAttrs5;
                        }
                        boolean hasUnresolvedAttrs6 = hasUnresolvedAttrs4;
                        int[] stateSpec2 = StateSet.trimStateSet(stateSpec, j);
                        int color = modulateColor(baseColor, alphaMod, lStar);
                        if (listSize == 0 || stateSpec2.length == 0) {
                            defaultColor = color;
                        }
                        if (themeAttrs == null) {
                            hasUnresolvedAttrs3 = hasUnresolvedAttrs6;
                        } else {
                            hasUnresolvedAttrs3 = true;
                        }
                        colorList = GrowingArrayUtils.append(colorList, listSize, color);
                        themeAttrsList = (int[][]) GrowingArrayUtils.append(themeAttrsList, listSize, themeAttrs);
                        stateSpecList = (int[][]) GrowingArrayUtils.append(stateSpecList, listSize, stateSpec2);
                        listSize++;
                        hasUnresolvedAttrs4 = hasUnresolvedAttrs3;
                        changingConfigurations = changingConfigurations2;
                        innerDepth2 = innerDepth3;
                        i = 1;
                    }
                    innerDepth2 = innerDepth;
                    hasUnresolvedAttrs4 = hasUnresolvedAttrs2;
                    i = 1;
                } else {
                    hasUnresolvedAttrs = hasUnresolvedAttrs4;
                }
            } else {
                hasUnresolvedAttrs = hasUnresolvedAttrs4;
            }
        }
        this.mChangingConfigurations = changingConfigurations;
        this.mDefaultColor = defaultColor;
        if (!hasUnresolvedAttrs) {
            this.mThemeAttrs = null;
        } else {
            int[][] iArr = new int[listSize];
            this.mThemeAttrs = iArr;
            System.arraycopy(themeAttrsList, 0, iArr, 0, listSize);
        }
        int[] iArr2 = new int[listSize];
        this.mColors = iArr2;
        this.mStateSpecs = new int[listSize];
        System.arraycopy(colorList, 0, iArr2, 0, listSize);
        System.arraycopy(stateSpecList, 0, this.mStateSpecs, 0, listSize);
        onColorsChanged();
    }

    @Override // android.content.res.ComplexColor
    public boolean canApplyTheme() {
        return this.mThemeAttrs != null;
    }

    private void applyTheme(Resources.Theme t) {
        float defaultAlphaMod;
        if (this.mThemeAttrs == null) {
            return;
        }
        boolean hasUnresolvedAttrs = false;
        int[][] themeAttrsList = this.mThemeAttrs;
        int N = themeAttrsList.length;
        for (int i = 0; i < N; i++) {
            if (themeAttrsList[i] != null) {
                TypedArray a = t.resolveAttributes(themeAttrsList[i], C4057R.styleable.ColorStateListItem);
                if (themeAttrsList[i][0] != 0) {
                    defaultAlphaMod = Color.alpha(this.mColors[i]) / 255.0f;
                } else {
                    defaultAlphaMod = 1.0f;
                }
                themeAttrsList[i] = a.extractThemeAttrs(themeAttrsList[i]);
                if (themeAttrsList[i] != null) {
                    hasUnresolvedAttrs = true;
                }
                int baseColor = a.getColor(0, this.mColors[i]);
                float alphaMod = a.getFloat(1, defaultAlphaMod);
                float lStar = a.getFloat(2, -1.0f);
                this.mColors[i] = modulateColor(baseColor, alphaMod, lStar);
                this.mChangingConfigurations |= a.getChangingConfigurations();
                a.recycle();
            }
        }
        if (!hasUnresolvedAttrs) {
            this.mThemeAttrs = null;
        }
        onColorsChanged();
    }

    @Override // android.content.res.ComplexColor
    public ColorStateList obtainForTheme(Resources.Theme t) {
        if (t == null || !canApplyTheme()) {
            return this;
        }
        ColorStateList clone = new ColorStateList(this);
        clone.applyTheme(t);
        return clone;
    }

    @Override // android.content.res.ComplexColor
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mChangingConfigurations;
    }

    private int modulateColor(int baseColor, float alphaMod, float lStar) {
        boolean validLStar = lStar >= 0.0f && lStar <= 100.0f;
        if (alphaMod == 1.0f && !validLStar) {
            return baseColor;
        }
        int baseAlpha = Color.alpha(baseColor);
        int alpha = MathUtils.constrain((int) ((baseAlpha * alphaMod) + 0.5f), 0, 255);
        if (validLStar) {
            Cam baseCam = ColorUtils.colorToCAM(baseColor);
            baseColor = ColorUtils.CAMToColor(baseCam.getHue(), baseCam.getChroma(), lStar);
        }
        return (16777215 & baseColor) | (alpha << 24);
    }

    @Override // android.content.res.ComplexColor
    public boolean isStateful() {
        int[][] iArr = this.mStateSpecs;
        return iArr.length >= 1 && iArr[0].length > 0;
    }

    public boolean hasFocusStateSpecified() {
        return StateSet.containsAttribute(this.mStateSpecs, 16842908);
    }

    public boolean isOpaque() {
        return this.mIsOpaque;
    }

    public int getColorForState(int[] stateSet, int defaultColor) {
        int setLength = this.mStateSpecs.length;
        for (int i = 0; i < setLength; i++) {
            int[] stateSpec = this.mStateSpecs[i];
            if (StateSet.stateSetMatches(stateSpec, stateSet)) {
                return this.mColors[i];
            }
        }
        return defaultColor;
    }

    @Override // android.content.res.ComplexColor
    public int getDefaultColor() {
        return this.mDefaultColor;
    }

    public int[][] getStates() {
        return this.mStateSpecs;
    }

    public int[] getColors() {
        return this.mColors;
    }

    public boolean hasState(int state) {
        int[][] stateSpecs = this.mStateSpecs;
        for (int[] states : stateSpecs) {
            int stateCount = states.length;
            for (int stateIndex = 0; stateIndex < stateCount; stateIndex++) {
                if (states[stateIndex] == state || states[stateIndex] == (~state)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String toString() {
        return "ColorStateList{mThemeAttrs=" + Arrays.deepToString(this.mThemeAttrs) + "mChangingConfigurations=" + this.mChangingConfigurations + "mStateSpecs=" + Arrays.deepToString(this.mStateSpecs) + "mColors=" + Arrays.toString(this.mColors) + "mDefaultColor=" + this.mDefaultColor + '}';
    }

    private void onColorsChanged() {
        int defaultColor = -65536;
        boolean isOpaque = true;
        int[][] states = this.mStateSpecs;
        int[] colors = this.mColors;
        int N = states.length;
        if (N > 0) {
            defaultColor = colors[0];
            int i = N - 1;
            while (true) {
                if (i <= 0) {
                    break;
                } else if (states[i].length != 0) {
                    i--;
                } else {
                    defaultColor = colors[i];
                    break;
                }
            }
            int i2 = 0;
            while (true) {
                if (i2 >= N) {
                    break;
                } else if (Color.alpha(colors[i2]) == 255) {
                    i2++;
                } else {
                    isOpaque = false;
                    break;
                }
            }
        }
        this.mDefaultColor = defaultColor;
        this.mIsOpaque = isOpaque;
    }

    @Override // android.content.res.ComplexColor
    public ConstantState<ComplexColor> getConstantState() {
        if (this.mFactory == null) {
            this.mFactory = new ColorStateListFactory(this);
        }
        return this.mFactory;
    }

    /* loaded from: classes.dex */
    private static class ColorStateListFactory extends ConstantState<ComplexColor> {
        private final ColorStateList mSrc;

        public ColorStateListFactory(ColorStateList src) {
            this.mSrc = src;
        }

        @Override // android.content.res.ConstantState
        public int getChangingConfigurations() {
            return this.mSrc.mChangingConfigurations;
        }

        @Override // android.content.res.ConstantState
        /* renamed from: newInstance */
        public ComplexColor newInstance2() {
            return this.mSrc;
        }

        @Override // android.content.res.ConstantState
        /* renamed from: newInstance */
        public ComplexColor newInstance2(Resources res, Resources.Theme theme) {
            return this.mSrc.obtainForTheme(theme);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (canApplyTheme()) {
            Log.m104w(TAG, "Wrote partially-resolved ColorStateList to parcel!");
        }
        int N = this.mStateSpecs.length;
        dest.writeInt(N);
        for (int i = 0; i < N; i++) {
            dest.writeIntArray(this.mStateSpecs[i]);
        }
        dest.writeIntArray(this.mColors);
    }
}
