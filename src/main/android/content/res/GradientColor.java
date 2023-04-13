package android.content.res;

import android.content.res.Resources;
import android.graphics.LinearGradient;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.android.ims.ImsConfig;
import com.android.internal.C4057R;
import com.android.internal.util.GrowingArrayUtils;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class GradientColor extends ComplexColor {
    private static final boolean DBG_GRADIENT = false;
    private static final String TAG = "GradientColor";
    private static final int TILE_MODE_CLAMP = 0;
    private static final int TILE_MODE_MIRROR = 2;
    private static final int TILE_MODE_REPEAT = 1;
    private int mCenterColor;
    private float mCenterX;
    private float mCenterY;
    private int mChangingConfigurations;
    private int mDefaultColor;
    private int mEndColor;
    private float mEndX;
    private float mEndY;
    private GradientColorFactory mFactory;
    private float mGradientRadius;
    private int mGradientType;
    private boolean mHasCenterColor;
    private int[] mItemColors;
    private float[] mItemOffsets;
    private int[][] mItemsThemeAttrs;
    private Shader mShader;
    private int mStartColor;
    private float mStartX;
    private float mStartY;
    private int[] mThemeAttrs;
    private int mTileMode;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    private @interface GradientTileMode {
    }

    private GradientColor() {
        this.mShader = null;
        this.mGradientType = 0;
        this.mCenterX = 0.0f;
        this.mCenterY = 0.0f;
        this.mStartX = 0.0f;
        this.mStartY = 0.0f;
        this.mEndX = 0.0f;
        this.mEndY = 0.0f;
        this.mStartColor = 0;
        this.mCenterColor = 0;
        this.mEndColor = 0;
        this.mHasCenterColor = false;
        this.mTileMode = 0;
        this.mGradientRadius = 0.0f;
    }

    private GradientColor(GradientColor copy) {
        this.mShader = null;
        this.mGradientType = 0;
        this.mCenterX = 0.0f;
        this.mCenterY = 0.0f;
        this.mStartX = 0.0f;
        this.mStartY = 0.0f;
        this.mEndX = 0.0f;
        this.mEndY = 0.0f;
        this.mStartColor = 0;
        this.mCenterColor = 0;
        this.mEndColor = 0;
        this.mHasCenterColor = false;
        this.mTileMode = 0;
        this.mGradientRadius = 0.0f;
        if (copy != null) {
            this.mChangingConfigurations = copy.mChangingConfigurations;
            this.mDefaultColor = copy.mDefaultColor;
            this.mShader = copy.mShader;
            this.mGradientType = copy.mGradientType;
            this.mCenterX = copy.mCenterX;
            this.mCenterY = copy.mCenterY;
            this.mStartX = copy.mStartX;
            this.mStartY = copy.mStartY;
            this.mEndX = copy.mEndX;
            this.mEndY = copy.mEndY;
            this.mStartColor = copy.mStartColor;
            this.mCenterColor = copy.mCenterColor;
            this.mEndColor = copy.mEndColor;
            this.mHasCenterColor = copy.mHasCenterColor;
            this.mGradientRadius = copy.mGradientRadius;
            this.mTileMode = copy.mTileMode;
            int[] iArr = copy.mItemColors;
            if (iArr != null) {
                this.mItemColors = (int[]) iArr.clone();
            }
            float[] fArr = copy.mItemOffsets;
            if (fArr != null) {
                this.mItemOffsets = (float[]) fArr.clone();
            }
            int[] iArr2 = copy.mThemeAttrs;
            if (iArr2 != null) {
                this.mThemeAttrs = (int[]) iArr2.clone();
            }
            int[][] iArr3 = copy.mItemsThemeAttrs;
            if (iArr3 != null) {
                this.mItemsThemeAttrs = (int[][]) iArr3.clone();
            }
        }
    }

    private static Shader.TileMode parseTileMode(int tileMode) {
        switch (tileMode) {
            case 0:
                return Shader.TileMode.CLAMP;
            case 1:
                return Shader.TileMode.REPEAT;
            case 2:
                return Shader.TileMode.MIRROR;
            default:
                return Shader.TileMode.CLAMP;
        }
    }

    private void updateRootElementState(TypedArray a) {
        this.mThemeAttrs = a.extractThemeAttrs();
        this.mStartX = a.getFloat(8, this.mStartX);
        this.mStartY = a.getFloat(9, this.mStartY);
        this.mEndX = a.getFloat(10, this.mEndX);
        this.mEndY = a.getFloat(11, this.mEndY);
        this.mCenterX = a.getFloat(3, this.mCenterX);
        this.mCenterY = a.getFloat(4, this.mCenterY);
        this.mGradientType = a.getInt(2, this.mGradientType);
        this.mStartColor = a.getColor(0, this.mStartColor);
        this.mHasCenterColor |= a.hasValue(7);
        this.mCenterColor = a.getColor(7, this.mCenterColor);
        this.mEndColor = a.getColor(1, this.mEndColor);
        this.mTileMode = a.getInt(6, this.mTileMode);
        this.mGradientRadius = a.getFloat(5, this.mGradientRadius);
    }

    private void validateXmlContent() throws XmlPullParserException {
        if (this.mGradientRadius <= 0.0f && this.mGradientType == 1) {
            throw new XmlPullParserException("<gradient> tag requires 'gradientRadius' attribute with radial type");
        }
    }

    public Shader getShader() {
        return this.mShader;
    }

    public static GradientColor createFromXml(Resources r, XmlResourceParser parser, Resources.Theme theme) throws XmlPullParserException, IOException {
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
    public static GradientColor createFromXmlInner(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        String name = parser.getName();
        if (!name.equals("gradient")) {
            throw new XmlPullParserException(parser.getPositionDescription() + ": invalid gradient color tag " + name);
        }
        GradientColor gradientColor = new GradientColor();
        gradientColor.inflate(r, parser, attrs, theme);
        return gradientColor;
    }

    private void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        TypedArray a = Resources.obtainAttributes(r, theme, attrs, C4057R.styleable.GradientColor);
        updateRootElementState(a);
        this.mChangingConfigurations |= a.getChangingConfigurations();
        a.recycle();
        validateXmlContent();
        inflateChildElements(r, parser, attrs, theme);
        onColorsChange();
    }

    /* JADX WARN: Code restructure failed: missing block: B:30:0x00e3, code lost:
        if (r6 <= 0) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x00e5, code lost:
        if (r7 == false) goto L16;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x00e7, code lost:
        r1 = new int[r6];
        r20.mItemsThemeAttrs = r1;
        java.lang.System.arraycopy(r5, 0, r1, 0, r6);
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00ef, code lost:
        r20.mItemsThemeAttrs = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x00f2, code lost:
        r1 = new int[r6];
        r20.mItemColors = r1;
        r20.mItemOffsets = new float[r6];
        java.lang.System.arraycopy(r4, 0, r1, 0, r6);
        java.lang.System.arraycopy(r3, 0, r20.mItemOffsets, 0, r6);
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x0102, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:?, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void inflateChildElements(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        int depth;
        int innerDepth;
        int i = 1;
        int innerDepth2 = parser.getDepth() + 1;
        float[] offsetList = new float[20];
        int[] colorList = new int[offsetList.length];
        int[][] themeAttrsList = new int[offsetList.length];
        int listSize = 0;
        boolean hasUnresolvedAttrs = false;
        while (true) {
            int type = parser.next();
            if (type != i && ((depth = parser.getDepth()) >= innerDepth2 || type != 3)) {
                if (type != 2) {
                    innerDepth = innerDepth2;
                } else if (depth > innerDepth2) {
                    innerDepth = innerDepth2;
                } else if (parser.getName().equals(ImsConfig.EXTRA_CHANGED_ITEM)) {
                    TypedArray a = Resources.obtainAttributes(r, theme, attrs, C4057R.styleable.GradientColorItem);
                    boolean hasColor = a.hasValue(0);
                    boolean hasOffset = a.hasValue(i);
                    if (!hasColor || !hasOffset) {
                        break;
                    }
                    int[] themeAttrs = a.extractThemeAttrs();
                    int color = a.getColor(0, 0);
                    int innerDepth3 = innerDepth2;
                    float offset = a.getFloat(1, 0.0f);
                    this.mChangingConfigurations |= a.getChangingConfigurations();
                    a.recycle();
                    if (themeAttrs != null) {
                        hasUnresolvedAttrs = true;
                    }
                    colorList = GrowingArrayUtils.append(colorList, listSize, color);
                    offsetList = GrowingArrayUtils.append(offsetList, listSize, offset);
                    themeAttrsList = (int[][]) GrowingArrayUtils.append(themeAttrsList, listSize, themeAttrs);
                    listSize++;
                    innerDepth2 = innerDepth3;
                    i = 1;
                } else {
                    innerDepth = innerDepth2;
                }
                innerDepth2 = innerDepth;
                i = 1;
            }
        }
        throw new XmlPullParserException(parser.getPositionDescription() + ": <item> tag requires a 'color' attribute and a 'offset' attribute!");
    }

    private void applyItemsAttrsTheme(Resources.Theme t) {
        if (this.mItemsThemeAttrs == null) {
            return;
        }
        boolean hasUnresolvedAttrs = false;
        int[][] themeAttrsList = this.mItemsThemeAttrs;
        int N = themeAttrsList.length;
        for (int i = 0; i < N; i++) {
            if (themeAttrsList[i] != null) {
                TypedArray a = t.resolveAttributes(themeAttrsList[i], C4057R.styleable.GradientColorItem);
                themeAttrsList[i] = a.extractThemeAttrs(themeAttrsList[i]);
                if (themeAttrsList[i] != null) {
                    hasUnresolvedAttrs = true;
                }
                int[] iArr = this.mItemColors;
                iArr[i] = a.getColor(0, iArr[i]);
                float[] fArr = this.mItemOffsets;
                fArr[i] = a.getFloat(1, fArr[i]);
                this.mChangingConfigurations |= a.getChangingConfigurations();
                a.recycle();
            }
        }
        if (!hasUnresolvedAttrs) {
            this.mItemsThemeAttrs = null;
        }
    }

    private void onColorsChange() {
        int[] tempColors;
        float[] tempOffsets = null;
        int[] iArr = this.mItemColors;
        if (iArr != null) {
            int length = iArr.length;
            tempColors = new int[length];
            tempOffsets = new float[length];
            for (int i = 0; i < length; i++) {
                tempColors[i] = this.mItemColors[i];
                tempOffsets[i] = this.mItemOffsets[i];
            }
        } else if (this.mHasCenterColor) {
            tempColors = new int[]{this.mStartColor, this.mCenterColor, this.mEndColor};
            tempOffsets = new float[]{0.0f, 0.5f, 1.0f};
        } else {
            tempColors = new int[]{this.mStartColor, this.mEndColor};
        }
        if (tempColors.length < 2) {
            Log.m104w(TAG, "<gradient> tag requires 2 color values specified!" + tempColors.length + " " + Arrays.toString(tempColors));
        }
        int i2 = this.mGradientType;
        if (i2 == 0) {
            this.mShader = new LinearGradient(this.mStartX, this.mStartY, this.mEndX, this.mEndY, tempColors, tempOffsets, parseTileMode(this.mTileMode));
        } else if (i2 == 1) {
            this.mShader = new RadialGradient(this.mCenterX, this.mCenterY, this.mGradientRadius, tempColors, tempOffsets, parseTileMode(this.mTileMode));
        } else {
            this.mShader = new SweepGradient(this.mCenterX, this.mCenterY, tempColors, tempOffsets);
        }
        this.mDefaultColor = tempColors[0];
    }

    @Override // android.content.res.ComplexColor
    public int getDefaultColor() {
        return this.mDefaultColor;
    }

    @Override // android.content.res.ComplexColor
    public ConstantState<ComplexColor> getConstantState() {
        if (this.mFactory == null) {
            this.mFactory = new GradientColorFactory(this);
        }
        return this.mFactory;
    }

    /* loaded from: classes.dex */
    private static class GradientColorFactory extends ConstantState<ComplexColor> {
        private final GradientColor mSrc;

        public GradientColorFactory(GradientColor src) {
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

    @Override // android.content.res.ComplexColor
    public GradientColor obtainForTheme(Resources.Theme t) {
        if (t == null || !canApplyTheme()) {
            return this;
        }
        GradientColor clone = new GradientColor(this);
        clone.applyTheme(t);
        return clone;
    }

    @Override // android.content.res.ComplexColor
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mChangingConfigurations;
    }

    private void applyTheme(Resources.Theme t) {
        if (this.mThemeAttrs != null) {
            applyRootAttrsTheme(t);
        }
        if (this.mItemsThemeAttrs != null) {
            applyItemsAttrsTheme(t);
        }
        onColorsChange();
    }

    private void applyRootAttrsTheme(Resources.Theme t) {
        TypedArray a = t.resolveAttributes(this.mThemeAttrs, C4057R.styleable.GradientColor);
        this.mThemeAttrs = a.extractThemeAttrs(this.mThemeAttrs);
        updateRootElementState(a);
        this.mChangingConfigurations |= a.getChangingConfigurations();
        a.recycle();
    }

    @Override // android.content.res.ComplexColor
    public boolean canApplyTheme() {
        return (this.mThemeAttrs == null && this.mItemsThemeAttrs == null) ? false : true;
    }
}
