package android.view.inputmethod;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.SpannedString;
import android.text.TextUtils;
import android.view.inputmethod.SparseRectFArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class CursorAnchorInfo implements Parcelable {
    public static final Parcelable.Creator<CursorAnchorInfo> CREATOR = new Parcelable.Creator<CursorAnchorInfo>() { // from class: android.view.inputmethod.CursorAnchorInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CursorAnchorInfo createFromParcel(Parcel source) {
            return new CursorAnchorInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CursorAnchorInfo[] newArray(int size) {
            return new CursorAnchorInfo[size];
        }
    };
    public static final int FLAG_HAS_INVISIBLE_REGION = 2;
    public static final int FLAG_HAS_VISIBLE_REGION = 1;
    public static final int FLAG_IS_RTL = 4;
    private final SparseRectFArray mCharacterBoundsArray;
    private final CharSequence mComposingText;
    private final int mComposingTextStart;
    private final EditorBoundsInfo mEditorBoundsInfo;
    private final int mHashCode;
    private final float mInsertionMarkerBaseline;
    private final float mInsertionMarkerBottom;
    private final int mInsertionMarkerFlags;
    private final float mInsertionMarkerHorizontal;
    private final float mInsertionMarkerTop;
    private final float[] mMatrixValues;
    private final int mSelectionEnd;
    private final int mSelectionStart;
    private final TextAppearanceInfo mTextAppearanceInfo;
    private final float[] mVisibleLineBounds;

    public CursorAnchorInfo(Parcel source) {
        this.mHashCode = source.readInt();
        this.mSelectionStart = source.readInt();
        this.mSelectionEnd = source.readInt();
        this.mComposingTextStart = source.readInt();
        this.mComposingText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
        this.mInsertionMarkerFlags = source.readInt();
        this.mInsertionMarkerHorizontal = source.readFloat();
        this.mInsertionMarkerTop = source.readFloat();
        this.mInsertionMarkerBaseline = source.readFloat();
        this.mInsertionMarkerBottom = source.readFloat();
        this.mCharacterBoundsArray = (SparseRectFArray) source.readTypedObject(SparseRectFArray.CREATOR);
        this.mEditorBoundsInfo = (EditorBoundsInfo) source.readTypedObject(EditorBoundsInfo.CREATOR);
        this.mMatrixValues = source.createFloatArray();
        this.mVisibleLineBounds = source.createFloatArray();
        this.mTextAppearanceInfo = (TextAppearanceInfo) source.readTypedObject(TextAppearanceInfo.CREATOR);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mHashCode);
        dest.writeInt(this.mSelectionStart);
        dest.writeInt(this.mSelectionEnd);
        dest.writeInt(this.mComposingTextStart);
        TextUtils.writeToParcel(this.mComposingText, dest, flags);
        dest.writeInt(this.mInsertionMarkerFlags);
        dest.writeFloat(this.mInsertionMarkerHorizontal);
        dest.writeFloat(this.mInsertionMarkerTop);
        dest.writeFloat(this.mInsertionMarkerBaseline);
        dest.writeFloat(this.mInsertionMarkerBottom);
        dest.writeTypedObject(this.mCharacterBoundsArray, flags);
        dest.writeTypedObject(this.mEditorBoundsInfo, flags);
        dest.writeFloatArray(this.mMatrixValues);
        dest.writeFloatArray(this.mVisibleLineBounds);
        dest.writeTypedObject(this.mTextAppearanceInfo, flags);
    }

    public int hashCode() {
        return this.mHashCode;
    }

    private static boolean areSameFloatImpl(float a, float b) {
        return (Float.isNaN(a) && Float.isNaN(b)) || a == b;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CursorAnchorInfo)) {
            return false;
        }
        CursorAnchorInfo that = (CursorAnchorInfo) obj;
        if (hashCode() != that.hashCode() || this.mSelectionStart != that.mSelectionStart || this.mSelectionEnd != that.mSelectionEnd || this.mInsertionMarkerFlags != that.mInsertionMarkerFlags || !areSameFloatImpl(this.mInsertionMarkerHorizontal, that.mInsertionMarkerHorizontal) || !areSameFloatImpl(this.mInsertionMarkerTop, that.mInsertionMarkerTop) || !areSameFloatImpl(this.mInsertionMarkerBaseline, that.mInsertionMarkerBaseline) || !areSameFloatImpl(this.mInsertionMarkerBottom, that.mInsertionMarkerBottom) || !Objects.equals(this.mCharacterBoundsArray, that.mCharacterBoundsArray) || !Objects.equals(this.mEditorBoundsInfo, that.mEditorBoundsInfo) || !Arrays.equals(this.mVisibleLineBounds, that.mVisibleLineBounds) || this.mComposingTextStart != that.mComposingTextStart || !Objects.equals(this.mComposingText, that.mComposingText) || this.mMatrixValues.length != that.mMatrixValues.length) {
            return false;
        }
        int i = 0;
        while (true) {
            float[] fArr = this.mMatrixValues;
            if (i < fArr.length) {
                if (fArr[i] != that.mMatrixValues[i]) {
                    return false;
                }
                i++;
            } else if (!Objects.equals(this.mTextAppearanceInfo, that.mTextAppearanceInfo)) {
                return false;
            } else {
                return true;
            }
        }
    }

    public String toString() {
        return "CursorAnchorInfo{mHashCode=" + this.mHashCode + " mSelection=" + this.mSelectionStart + "," + this.mSelectionEnd + " mComposingTextStart=" + this.mComposingTextStart + " mComposingText=" + ((Object) this.mComposingText) + " mInsertionMarkerFlags=" + this.mInsertionMarkerFlags + " mInsertionMarkerHorizontal=" + this.mInsertionMarkerHorizontal + " mInsertionMarkerTop=" + this.mInsertionMarkerTop + " mInsertionMarkerBaseline=" + this.mInsertionMarkerBaseline + " mInsertionMarkerBottom=" + this.mInsertionMarkerBottom + " mCharacterBoundsArray=" + this.mCharacterBoundsArray + " mEditorBoundsInfo=" + this.mEditorBoundsInfo + " mVisibleLineBounds=" + getVisibleLineBounds() + " mMatrix=" + Arrays.toString(this.mMatrixValues) + " mTextAppearanceInfo=" + this.mTextAppearanceInfo + "}";
    }

    /* loaded from: classes4.dex */
    public static final class Builder {
        private static final int LINE_BOUNDS_INITIAL_SIZE = 4;
        private int mSelectionStart = -1;
        private int mSelectionEnd = -1;
        private int mComposingTextStart = -1;
        private CharSequence mComposingText = null;
        private float mInsertionMarkerHorizontal = Float.NaN;
        private float mInsertionMarkerTop = Float.NaN;
        private float mInsertionMarkerBaseline = Float.NaN;
        private float mInsertionMarkerBottom = Float.NaN;
        private int mInsertionMarkerFlags = 0;
        private SparseRectFArray.SparseRectFArrayBuilder mCharacterBoundsArrayBuilder = null;
        private EditorBoundsInfo mEditorBoundsInfo = null;
        private float[] mMatrixValues = null;
        private boolean mMatrixInitialized = false;
        private float[] mVisibleLineBounds = new float[16];
        private int mVisibleLineBoundsCount = 0;
        private TextAppearanceInfo mTextAppearanceInfo = null;

        public Builder setSelectionRange(int newStart, int newEnd) {
            this.mSelectionStart = newStart;
            this.mSelectionEnd = newEnd;
            return this;
        }

        public Builder setComposingText(int composingTextStart, CharSequence composingText) {
            this.mComposingTextStart = composingTextStart;
            if (composingText == null) {
                this.mComposingText = null;
            } else {
                this.mComposingText = new SpannedString(composingText);
            }
            return this;
        }

        public Builder setInsertionMarkerLocation(float horizontalPosition, float lineTop, float lineBaseline, float lineBottom, int flags) {
            this.mInsertionMarkerHorizontal = horizontalPosition;
            this.mInsertionMarkerTop = lineTop;
            this.mInsertionMarkerBaseline = lineBaseline;
            this.mInsertionMarkerBottom = lineBottom;
            this.mInsertionMarkerFlags = flags;
            return this;
        }

        public Builder addCharacterBounds(int index, float left, float top, float right, float bottom, int flags) {
            if (index < 0) {
                throw new IllegalArgumentException("index must not be a negative integer.");
            }
            if (this.mCharacterBoundsArrayBuilder == null) {
                this.mCharacterBoundsArrayBuilder = new SparseRectFArray.SparseRectFArrayBuilder();
            }
            this.mCharacterBoundsArrayBuilder.append(index, left, top, right, bottom, flags);
            return this;
        }

        public Builder setEditorBoundsInfo(EditorBoundsInfo bounds) {
            this.mEditorBoundsInfo = bounds;
            return this;
        }

        public Builder setMatrix(Matrix matrix) {
            if (this.mMatrixValues == null) {
                this.mMatrixValues = new float[9];
            }
            (matrix != null ? matrix : Matrix.IDENTITY_MATRIX).getValues(this.mMatrixValues);
            this.mMatrixInitialized = true;
            return this;
        }

        public Builder setTextAppearanceInfo(TextAppearanceInfo textAppearanceInfo) {
            this.mTextAppearanceInfo = textAppearanceInfo;
            return this;
        }

        public Builder addVisibleLineBounds(float left, float top, float right, float bottom) {
            float[] fArr = this.mVisibleLineBounds;
            int length = fArr.length;
            int i = this.mVisibleLineBoundsCount;
            if (length <= i + 4) {
                this.mVisibleLineBounds = Arrays.copyOf(fArr, (i + 4) * 2);
            }
            float[] fArr2 = this.mVisibleLineBounds;
            int i2 = this.mVisibleLineBoundsCount;
            int i3 = i2 + 1;
            this.mVisibleLineBoundsCount = i3;
            fArr2[i2] = left;
            int i4 = i3 + 1;
            this.mVisibleLineBoundsCount = i4;
            fArr2[i3] = top;
            int i5 = i4 + 1;
            this.mVisibleLineBoundsCount = i5;
            fArr2[i4] = right;
            this.mVisibleLineBoundsCount = i5 + 1;
            fArr2[i5] = bottom;
            return this;
        }

        public Builder clearVisibleLineBounds() {
            this.mVisibleLineBoundsCount = 0;
            return this;
        }

        public CursorAnchorInfo build() {
            if (!this.mMatrixInitialized) {
                SparseRectFArray.SparseRectFArrayBuilder sparseRectFArrayBuilder = this.mCharacterBoundsArrayBuilder;
                boolean hasVisibleLineBounds = true;
                boolean hasCharacterBounds = (sparseRectFArrayBuilder == null || sparseRectFArrayBuilder.isEmpty()) ? false : true;
                if (this.mVisibleLineBounds == null || this.mVisibleLineBoundsCount <= 0) {
                    hasVisibleLineBounds = false;
                }
                if (hasCharacterBounds || hasVisibleLineBounds || !Float.isNaN(this.mInsertionMarkerHorizontal) || !Float.isNaN(this.mInsertionMarkerTop) || !Float.isNaN(this.mInsertionMarkerBaseline) || !Float.isNaN(this.mInsertionMarkerBottom)) {
                    throw new IllegalArgumentException("Coordinate transformation matrix is required when positional parameters are specified.");
                }
            }
            return CursorAnchorInfo.create(this);
        }

        public void reset() {
            this.mSelectionStart = -1;
            this.mSelectionEnd = -1;
            this.mComposingTextStart = -1;
            this.mComposingText = null;
            this.mInsertionMarkerFlags = 0;
            this.mInsertionMarkerHorizontal = Float.NaN;
            this.mInsertionMarkerTop = Float.NaN;
            this.mInsertionMarkerBaseline = Float.NaN;
            this.mInsertionMarkerBottom = Float.NaN;
            this.mMatrixInitialized = false;
            SparseRectFArray.SparseRectFArrayBuilder sparseRectFArrayBuilder = this.mCharacterBoundsArrayBuilder;
            if (sparseRectFArrayBuilder != null) {
                sparseRectFArrayBuilder.reset();
            }
            this.mEditorBoundsInfo = null;
            clearVisibleLineBounds();
            this.mTextAppearanceInfo = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static CursorAnchorInfo create(Builder builder) {
        SparseRectFArray characterBoundsArray;
        if (builder.mCharacterBoundsArrayBuilder != null) {
            characterBoundsArray = builder.mCharacterBoundsArrayBuilder.build();
        } else {
            characterBoundsArray = null;
        }
        float[] matrixValues = new float[9];
        if (builder.mMatrixInitialized) {
            System.arraycopy(builder.mMatrixValues, 0, matrixValues, 0, 9);
        } else {
            Matrix.IDENTITY_MATRIX.getValues(matrixValues);
        }
        return new CursorAnchorInfo(builder.mSelectionStart, builder.mSelectionEnd, builder.mComposingTextStart, builder.mComposingText, builder.mInsertionMarkerFlags, builder.mInsertionMarkerHorizontal, builder.mInsertionMarkerTop, builder.mInsertionMarkerBaseline, builder.mInsertionMarkerBottom, characterBoundsArray, builder.mEditorBoundsInfo, matrixValues, Arrays.copyOf(builder.mVisibleLineBounds, builder.mVisibleLineBoundsCount), builder.mTextAppearanceInfo);
    }

    private CursorAnchorInfo(int selectionStart, int selectionEnd, int composingTextStart, CharSequence composingText, int insertionMarkerFlags, float insertionMarkerHorizontal, float insertionMarkerTop, float insertionMarkerBaseline, float insertionMarkerBottom, SparseRectFArray characterBoundsArray, EditorBoundsInfo editorBoundsInfo, float[] matrixValues, float[] visibleLineBounds, TextAppearanceInfo textAppearanceInfo) {
        this.mSelectionStart = selectionStart;
        this.mSelectionEnd = selectionEnd;
        this.mComposingTextStart = composingTextStart;
        this.mComposingText = composingText;
        this.mInsertionMarkerFlags = insertionMarkerFlags;
        this.mInsertionMarkerHorizontal = insertionMarkerHorizontal;
        this.mInsertionMarkerTop = insertionMarkerTop;
        this.mInsertionMarkerBaseline = insertionMarkerBaseline;
        this.mInsertionMarkerBottom = insertionMarkerBottom;
        this.mCharacterBoundsArray = characterBoundsArray;
        this.mEditorBoundsInfo = editorBoundsInfo;
        this.mMatrixValues = matrixValues;
        this.mVisibleLineBounds = visibleLineBounds;
        this.mTextAppearanceInfo = textAppearanceInfo;
        int hashCode = Objects.hashCode(composingText);
        this.mHashCode = (hashCode * 31) + Arrays.hashCode(matrixValues);
    }

    public static CursorAnchorInfo createForAdditionalParentMatrix(CursorAnchorInfo original, Matrix parentMatrix) {
        return new CursorAnchorInfo(original.mSelectionStart, original.mSelectionEnd, original.mComposingTextStart, original.mComposingText, original.mInsertionMarkerFlags, original.mInsertionMarkerHorizontal, original.mInsertionMarkerTop, original.mInsertionMarkerBaseline, original.mInsertionMarkerBottom, original.mCharacterBoundsArray, original.mEditorBoundsInfo, computeMatrixValues(parentMatrix, original), original.mVisibleLineBounds, original.mTextAppearanceInfo);
    }

    private static float[] computeMatrixValues(Matrix parentMatrix, CursorAnchorInfo info) {
        if (parentMatrix.isIdentity()) {
            return info.mMatrixValues;
        }
        Matrix newMatrix = new Matrix();
        newMatrix.setValues(info.mMatrixValues);
        newMatrix.postConcat(parentMatrix);
        float[] matrixValues = new float[9];
        newMatrix.getValues(matrixValues);
        return matrixValues;
    }

    public int getSelectionStart() {
        return this.mSelectionStart;
    }

    public int getSelectionEnd() {
        return this.mSelectionEnd;
    }

    public int getComposingTextStart() {
        return this.mComposingTextStart;
    }

    public CharSequence getComposingText() {
        return this.mComposingText;
    }

    public int getInsertionMarkerFlags() {
        return this.mInsertionMarkerFlags;
    }

    public float getInsertionMarkerHorizontal() {
        return this.mInsertionMarkerHorizontal;
    }

    public float getInsertionMarkerTop() {
        return this.mInsertionMarkerTop;
    }

    public float getInsertionMarkerBaseline() {
        return this.mInsertionMarkerBaseline;
    }

    public float getInsertionMarkerBottom() {
        return this.mInsertionMarkerBottom;
    }

    public RectF getCharacterBounds(int index) {
        SparseRectFArray sparseRectFArray = this.mCharacterBoundsArray;
        if (sparseRectFArray == null) {
            return null;
        }
        return sparseRectFArray.get(index);
    }

    public int getCharacterBoundsFlags(int index) {
        SparseRectFArray sparseRectFArray = this.mCharacterBoundsArray;
        if (sparseRectFArray == null) {
            return 0;
        }
        return sparseRectFArray.getFlags(index, 0);
    }

    public List<RectF> getVisibleLineBounds() {
        if (this.mVisibleLineBounds == null) {
            return Collections.emptyList();
        }
        List<RectF> result = new ArrayList<>(this.mVisibleLineBounds.length / 4);
        int index = 0;
        while (index < this.mVisibleLineBounds.length) {
            float[] fArr = this.mVisibleLineBounds;
            int index2 = index + 1;
            int index3 = index2 + 1;
            int index4 = index3 + 1;
            RectF rectF = new RectF(fArr[index], fArr[index2], fArr[index3], fArr[index4]);
            result.add(rectF);
            index = index4 + 1;
        }
        return result;
    }

    public EditorBoundsInfo getEditorBoundsInfo() {
        return this.mEditorBoundsInfo;
    }

    public TextAppearanceInfo getTextAppearanceInfo() {
        return this.mTextAppearanceInfo;
    }

    public Matrix getMatrix() {
        Matrix matrix = new Matrix();
        matrix.setValues(this.mMatrixValues);
        return matrix;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
