package android.view;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.Path;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.DisplayUtils;
import android.util.PathParser;
import android.util.RotationUtils;
import com.android.internal.C4057R;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class DisplayShape implements Parcelable {
    private final int mDisplayHeight;
    public final String mDisplayShapeSpec;
    private final int mDisplayWidth;
    private final int mOffsetX;
    private final int mOffsetY;
    private final float mPhysicalPixelDisplaySizeRatio;
    private final int mRotation;
    private final float mScale;
    public static final DisplayShape NONE = new DisplayShape("", 0, 0, 0.0f, 0);
    public static final Parcelable.Creator<DisplayShape> CREATOR = new Parcelable.Creator<DisplayShape>() { // from class: android.view.DisplayShape.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DisplayShape createFromParcel(Parcel in) {
            String spec = in.readString8();
            int displayWidth = in.readInt();
            int displayHeight = in.readInt();
            float ratio = in.readFloat();
            int rotation = in.readInt();
            int offsetX = in.readInt();
            int offsetY = in.readInt();
            float scale = in.readFloat();
            return new DisplayShape(spec, displayWidth, displayHeight, ratio, rotation, offsetX, offsetY, scale);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DisplayShape[] newArray(int size) {
            return new DisplayShape[size];
        }
    };

    private DisplayShape(String displayShapeSpec, int displayWidth, int displayHeight, float physicalPixelDisplaySizeRatio, int rotation) {
        this(displayShapeSpec, displayWidth, displayHeight, physicalPixelDisplaySizeRatio, rotation, 0, 0, 1.0f);
    }

    private DisplayShape(String displayShapeSpec, int displayWidth, int displayHeight, float physicalPixelDisplaySizeRatio, int rotation, int offsetX, int offsetY, float scale) {
        this.mDisplayShapeSpec = displayShapeSpec;
        this.mDisplayWidth = displayWidth;
        this.mDisplayHeight = displayHeight;
        this.mPhysicalPixelDisplaySizeRatio = physicalPixelDisplaySizeRatio;
        this.mRotation = rotation;
        this.mOffsetX = offsetX;
        this.mOffsetY = offsetY;
        this.mScale = scale;
    }

    public static DisplayShape fromResources(Resources res, String displayUniqueId, int physicalDisplayWidth, int physicalDisplayHeight, int displayWidth, int displayHeight) {
        boolean isScreenRound = RoundedCorners.getBuiltInDisplayIsRound(res, displayUniqueId);
        String spec = getSpecString(res, displayUniqueId);
        if (spec == null || spec.isEmpty()) {
            return createDefaultDisplayShape(displayWidth, displayHeight, isScreenRound);
        }
        float physicalPixelDisplaySizeRatio = DisplayUtils.getPhysicalPixelDisplaySizeRatio(physicalDisplayWidth, physicalDisplayHeight, displayWidth, displayHeight);
        return fromSpecString(spec, physicalPixelDisplaySizeRatio, displayWidth, displayHeight);
    }

    public static DisplayShape createDefaultDisplayShape(int displayWidth, int displayHeight, boolean isScreenRound) {
        return fromSpecString(createDefaultSpecString(displayWidth, displayHeight, isScreenRound), 1.0f, displayWidth, displayHeight);
    }

    public static DisplayShape fromSpecString(String spec, float physicalPixelDisplaySizeRatio, int displayWidth, int displayHeight) {
        return Cache.getDisplayShape(spec, physicalPixelDisplaySizeRatio, displayWidth, displayHeight);
    }

    private static String createDefaultSpecString(int displayWidth, int displayHeight, boolean isCircular) {
        if (!isCircular) {
            String spec = "M0,0 L" + displayWidth + ",0 L" + displayWidth + "," + displayHeight + " L0," + displayHeight + " Z";
            return spec;
        }
        float xRadius = displayWidth / 2.0f;
        float yRadius = displayHeight / 2.0f;
        String spec2 = "M0," + yRadius + " A" + xRadius + "," + yRadius + " 0 1,1 " + displayWidth + "," + yRadius + " A" + xRadius + "," + yRadius + " 0 1,1 0," + yRadius + " Z";
        return spec2;
    }

    public static String getSpecString(Resources res, String displayUniqueId) {
        String spec;
        int index = DisplayUtils.getDisplayUniqueIdConfigIndex(res, displayUniqueId);
        TypedArray array = res.obtainTypedArray(C4057R.array.config_displayShapeArray);
        if (index >= 0 && index < array.length()) {
            spec = array.getString(index);
        } else {
            spec = res.getString(C4057R.string.config_mainDisplayShape);
        }
        array.recycle();
        return spec;
    }

    public DisplayShape setRotation(int rotation) {
        return new DisplayShape(this.mDisplayShapeSpec, this.mDisplayWidth, this.mDisplayHeight, this.mPhysicalPixelDisplaySizeRatio, rotation, this.mOffsetX, this.mOffsetY, this.mScale);
    }

    public DisplayShape setOffset(int offsetX, int offsetY) {
        return new DisplayShape(this.mDisplayShapeSpec, this.mDisplayWidth, this.mDisplayHeight, this.mPhysicalPixelDisplaySizeRatio, this.mRotation, offsetX, offsetY, this.mScale);
    }

    public DisplayShape setScale(float scale) {
        return new DisplayShape(this.mDisplayShapeSpec, this.mDisplayWidth, this.mDisplayHeight, this.mPhysicalPixelDisplaySizeRatio, this.mRotation, this.mOffsetX, this.mOffsetY, scale);
    }

    public int hashCode() {
        return Objects.hash(this.mDisplayShapeSpec, Integer.valueOf(this.mDisplayWidth), Integer.valueOf(this.mDisplayHeight), Float.valueOf(this.mPhysicalPixelDisplaySizeRatio), Integer.valueOf(this.mRotation), Integer.valueOf(this.mOffsetX), Integer.valueOf(this.mOffsetY), Float.valueOf(this.mScale));
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof DisplayShape) {
            DisplayShape ds = (DisplayShape) o;
            return Objects.equals(this.mDisplayShapeSpec, ds.mDisplayShapeSpec) && this.mDisplayWidth == ds.mDisplayWidth && this.mDisplayHeight == ds.mDisplayHeight && this.mPhysicalPixelDisplaySizeRatio == ds.mPhysicalPixelDisplaySizeRatio && this.mRotation == ds.mRotation && this.mOffsetX == ds.mOffsetX && this.mOffsetY == ds.mOffsetY && this.mScale == ds.mScale;
        }
        return false;
    }

    public String toString() {
        return "DisplayShape{ spec=" + this.mDisplayShapeSpec.hashCode() + " displayWidth=" + this.mDisplayWidth + " displayHeight=" + this.mDisplayHeight + " physicalPixelDisplaySizeRatio=" + this.mPhysicalPixelDisplaySizeRatio + " rotation=" + this.mRotation + " offsetX=" + this.mOffsetX + " offsetY=" + this.mOffsetY + " scale=" + this.mScale + "}";
    }

    public Path getPath() {
        return Cache.getPath(this);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString8(this.mDisplayShapeSpec);
        dest.writeInt(this.mDisplayWidth);
        dest.writeInt(this.mDisplayHeight);
        dest.writeFloat(this.mPhysicalPixelDisplaySizeRatio);
        dest.writeInt(this.mRotation);
        dest.writeInt(this.mOffsetX);
        dest.writeInt(this.mOffsetY);
        dest.writeFloat(this.mScale);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class Cache {
        private static final Object CACHE_LOCK = new Object();
        private static DisplayShape sCacheForPath;
        private static int sCachedDisplayHeight;
        private static DisplayShape sCachedDisplayShape;
        private static int sCachedDisplayWidth;
        private static Path sCachedPath;
        private static float sCachedPhysicalPixelDisplaySizeRatio;
        private static String sCachedSpec;

        private Cache() {
        }

        static DisplayShape getDisplayShape(String spec, float physicalPixelDisplaySizeRatio, int displayWidth, int displayHeight) {
            Object obj = CACHE_LOCK;
            synchronized (obj) {
                if (spec.equals(sCachedSpec) && sCachedDisplayWidth == displayWidth && sCachedDisplayHeight == displayHeight && sCachedPhysicalPixelDisplaySizeRatio == physicalPixelDisplaySizeRatio) {
                    return sCachedDisplayShape;
                }
                DisplayShape shape = new DisplayShape(spec, displayWidth, displayHeight, physicalPixelDisplaySizeRatio, 0);
                synchronized (obj) {
                    sCachedSpec = spec;
                    sCachedDisplayWidth = displayWidth;
                    sCachedDisplayHeight = displayHeight;
                    sCachedPhysicalPixelDisplaySizeRatio = physicalPixelDisplaySizeRatio;
                    sCachedDisplayShape = shape;
                }
                return shape;
            }
        }

        static Path getPath(DisplayShape shape) {
            Object obj = CACHE_LOCK;
            synchronized (obj) {
                if (shape.equals(sCacheForPath)) {
                    return sCachedPath;
                }
                Path path = PathParser.createPathFromPathData(shape.mDisplayShapeSpec);
                if (!path.isEmpty()) {
                    Matrix matrix = new Matrix();
                    if (shape.mRotation != 0) {
                        RotationUtils.transformPhysicalToLogicalCoordinates(shape.mRotation, shape.mDisplayWidth, shape.mDisplayHeight, matrix);
                    }
                    if (shape.mPhysicalPixelDisplaySizeRatio != 1.0f) {
                        matrix.preScale(shape.mPhysicalPixelDisplaySizeRatio, shape.mPhysicalPixelDisplaySizeRatio);
                    }
                    if (shape.mOffsetX != 0 || shape.mOffsetY != 0) {
                        matrix.postTranslate(shape.mOffsetX, shape.mOffsetY);
                    }
                    if (shape.mScale != 1.0f) {
                        matrix.postScale(shape.mScale, shape.mScale);
                    }
                    path.transform(matrix);
                }
                synchronized (obj) {
                    sCacheForPath = shape;
                    sCachedPath = path;
                }
                return path;
            }
        }
    }
}
