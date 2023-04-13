package android.view;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.DisplayUtils;
import android.util.Pair;
import com.android.internal.C4057R;
import java.util.Arrays;
/* loaded from: classes4.dex */
public class RoundedCorners implements Parcelable {
    public static final int ROUNDED_CORNER_POSITION_LENGTH = 4;
    private static int sCachedDisplayHeight;
    private static int sCachedDisplayWidth;
    private static float sCachedPhysicalPixelDisplaySizeRatio;
    private static Pair<Integer, Integer> sCachedRadii;
    private static RoundedCorners sCachedRoundedCorners;
    public final RoundedCorner[] mRoundedCorners;
    public static final RoundedCorners NO_ROUNDED_CORNERS = new RoundedCorners(new RoundedCorner(0), new RoundedCorner(1), new RoundedCorner(2), new RoundedCorner(3));
    private static final Object CACHE_LOCK = new Object();
    public static final Parcelable.Creator<RoundedCorners> CREATOR = new Parcelable.Creator<RoundedCorners>() { // from class: android.view.RoundedCorners.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RoundedCorners createFromParcel(Parcel in) {
            int variant = in.readInt();
            if (variant == 0) {
                return RoundedCorners.NO_ROUNDED_CORNERS;
            }
            RoundedCorner[] roundedCorners = new RoundedCorner[4];
            in.readTypedArray(roundedCorners, RoundedCorner.CREATOR);
            return new RoundedCorners(roundedCorners);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RoundedCorners[] newArray(int size) {
            return new RoundedCorners[size];
        }
    };

    public RoundedCorners(RoundedCorner[] roundedCorners) {
        this.mRoundedCorners = roundedCorners;
    }

    public RoundedCorners(RoundedCorner topLeft, RoundedCorner topRight, RoundedCorner bottomRight, RoundedCorner bottomLeft) {
        this.mRoundedCorners = r0;
        RoundedCorner[] roundedCornerArr = {topLeft, topRight, bottomRight, bottomLeft};
    }

    public RoundedCorners(RoundedCorners roundedCorners) {
        this.mRoundedCorners = new RoundedCorner[4];
        for (int i = 0; i < 4; i++) {
            this.mRoundedCorners[i] = new RoundedCorner(roundedCorners.mRoundedCorners[i]);
        }
    }

    public static RoundedCorners fromResources(Resources res, String displayUniqueId, int physicalDisplayWidth, int physicalDisplayHeight, int displayWidth, int displayHeight) {
        return fromRadii(loadRoundedCornerRadii(res, displayUniqueId), physicalDisplayWidth, physicalDisplayHeight, displayWidth, displayHeight);
    }

    public static RoundedCorners fromRadii(Pair<Integer, Integer> radii, int displayWidth, int displayHeight) {
        return fromRadii(radii, displayWidth, displayHeight, displayWidth, displayHeight);
    }

    private static RoundedCorners fromRadii(Pair<Integer, Integer> radii, int physicalDisplayWidth, int physicalDisplayHeight, int displayWidth, int displayHeight) {
        if (radii == null) {
            return null;
        }
        float physicalPixelDisplaySizeRatio = DisplayUtils.getPhysicalPixelDisplaySizeRatio(physicalDisplayWidth, physicalDisplayHeight, displayWidth, displayHeight);
        synchronized (CACHE_LOCK) {
            if (radii.equals(sCachedRadii) && sCachedDisplayWidth == displayWidth && sCachedDisplayHeight == displayHeight && sCachedPhysicalPixelDisplaySizeRatio == physicalPixelDisplaySizeRatio) {
                return sCachedRoundedCorners;
            }
            RoundedCorner[] roundedCorners = new RoundedCorner[4];
            int topRadius = radii.first.intValue() > 0 ? radii.first.intValue() : 0;
            int bottomRadius = radii.second.intValue() > 0 ? radii.second.intValue() : 0;
            if (physicalPixelDisplaySizeRatio != 1.0f) {
                topRadius = (int) ((topRadius * physicalPixelDisplaySizeRatio) + 0.5d);
                bottomRadius = (int) ((bottomRadius * physicalPixelDisplaySizeRatio) + 0.5d);
            }
            int i = 0;
            while (i < 4) {
                roundedCorners[i] = createRoundedCorner(i, i <= 1 ? topRadius : bottomRadius, displayWidth, displayHeight);
                i++;
            }
            RoundedCorners result = new RoundedCorners(roundedCorners);
            synchronized (CACHE_LOCK) {
                sCachedDisplayWidth = displayWidth;
                sCachedDisplayHeight = displayHeight;
                sCachedRadii = radii;
                sCachedRoundedCorners = result;
                sCachedPhysicalPixelDisplaySizeRatio = physicalPixelDisplaySizeRatio;
            }
            return result;
        }
    }

    private static Pair<Integer, Integer> loadRoundedCornerRadii(Resources res, String displayUniqueId) {
        int radiusDefault = getRoundedCornerRadius(res, displayUniqueId);
        int radiusTop = getRoundedCornerTopRadius(res, displayUniqueId);
        int radiusBottom = getRoundedCornerBottomRadius(res, displayUniqueId);
        if (radiusDefault == 0 && radiusTop == 0 && radiusBottom == 0) {
            return null;
        }
        Pair<Integer, Integer> radii = new Pair<>(Integer.valueOf(radiusTop > 0 ? radiusTop : radiusDefault), Integer.valueOf(radiusBottom > 0 ? radiusBottom : radiusDefault));
        return radii;
    }

    public static int getRoundedCornerRadius(Resources res, String displayUniqueId) {
        int radius;
        int index = DisplayUtils.getDisplayUniqueIdConfigIndex(res, displayUniqueId);
        TypedArray array = res.obtainTypedArray(C4057R.array.config_roundedCornerRadiusArray);
        if (index >= 0 && index < array.length()) {
            radius = array.getDimensionPixelSize(index, 0);
        } else {
            radius = res.getDimensionPixelSize(C4057R.dimen.rounded_corner_radius);
        }
        array.recycle();
        return radius;
    }

    public static int getRoundedCornerTopRadius(Resources res, String displayUniqueId) {
        int radius;
        int index = DisplayUtils.getDisplayUniqueIdConfigIndex(res, displayUniqueId);
        TypedArray array = res.obtainTypedArray(C4057R.array.config_roundedCornerTopRadiusArray);
        if (index >= 0 && index < array.length()) {
            radius = array.getDimensionPixelSize(index, 0);
        } else {
            radius = res.getDimensionPixelSize(C4057R.dimen.rounded_corner_radius_top);
        }
        array.recycle();
        return radius;
    }

    public static int getRoundedCornerBottomRadius(Resources res, String displayUniqueId) {
        int radius;
        int index = DisplayUtils.getDisplayUniqueIdConfigIndex(res, displayUniqueId);
        TypedArray array = res.obtainTypedArray(C4057R.array.config_roundedCornerBottomRadiusArray);
        if (index >= 0 && index < array.length()) {
            radius = array.getDimensionPixelSize(index, 0);
        } else {
            radius = res.getDimensionPixelSize(C4057R.dimen.rounded_corner_radius_bottom);
        }
        array.recycle();
        return radius;
    }

    public static int getRoundedCornerRadiusAdjustment(Resources res, String displayUniqueId) {
        int radius;
        int index = DisplayUtils.getDisplayUniqueIdConfigIndex(res, displayUniqueId);
        TypedArray array = res.obtainTypedArray(C4057R.array.config_roundedCornerRadiusAdjustmentArray);
        if (index >= 0 && index < array.length()) {
            radius = array.getDimensionPixelSize(index, 0);
        } else {
            radius = res.getDimensionPixelSize(C4057R.dimen.rounded_corner_radius_adjustment);
        }
        array.recycle();
        return radius;
    }

    public static int getRoundedCornerRadiusTopAdjustment(Resources res, String displayUniqueId) {
        int radius;
        int index = DisplayUtils.getDisplayUniqueIdConfigIndex(res, displayUniqueId);
        TypedArray array = res.obtainTypedArray(C4057R.array.config_roundedCornerTopRadiusAdjustmentArray);
        if (index >= 0 && index < array.length()) {
            radius = array.getDimensionPixelSize(index, 0);
        } else {
            radius = res.getDimensionPixelSize(C4057R.dimen.rounded_corner_radius_top_adjustment);
        }
        array.recycle();
        return radius;
    }

    public static int getRoundedCornerRadiusBottomAdjustment(Resources res, String displayUniqueId) {
        int radius;
        int index = DisplayUtils.getDisplayUniqueIdConfigIndex(res, displayUniqueId);
        TypedArray array = res.obtainTypedArray(C4057R.array.config_roundedCornerBottomRadiusAdjustmentArray);
        if (index >= 0 && index < array.length()) {
            radius = array.getDimensionPixelSize(index, 0);
        } else {
            radius = res.getDimensionPixelSize(C4057R.dimen.rounded_corner_radius_bottom_adjustment);
        }
        array.recycle();
        return radius;
    }

    public static boolean getBuiltInDisplayIsRound(Resources res, String displayUniqueId) {
        boolean isRound;
        int index = DisplayUtils.getDisplayUniqueIdConfigIndex(res, displayUniqueId);
        TypedArray array = res.obtainTypedArray(C4057R.array.config_builtInDisplayIsRoundArray);
        if (index >= 0 && index < array.length()) {
            isRound = array.getBoolean(index, false);
        } else {
            isRound = res.getBoolean(C4057R.bool.config_mainBuiltInDisplayIsRound);
        }
        array.recycle();
        return isRound;
    }

    public RoundedCorners insetWithFrame(Rect frame, Rect roundedCornerFrame) {
        int centerX;
        int centerY;
        int i;
        RoundedCorner[] roundedCorners;
        int i2;
        RoundedCorners roundedCorners2 = this;
        int insetLeft = frame.left - roundedCornerFrame.left;
        int insetTop = frame.top - roundedCornerFrame.top;
        int insetRight = roundedCornerFrame.right - frame.right;
        int insetBottom = roundedCornerFrame.bottom - frame.bottom;
        int i3 = 4;
        RoundedCorner[] roundedCorners3 = new RoundedCorner[4];
        int i4 = 0;
        while (i4 < i3) {
            if (roundedCorners2.mRoundedCorners[i4].isEmpty()) {
                roundedCorners3[i4] = new RoundedCorner(i4);
                i = i4;
                roundedCorners = roundedCorners3;
                i2 = i3;
            } else {
                int radius = roundedCorners2.mRoundedCorners[i4].getRadius();
                switch (i4) {
                    case 0:
                        centerX = radius;
                        centerY = radius;
                        break;
                    case 1:
                        int centerX2 = roundedCornerFrame.width();
                        centerX = centerX2 - radius;
                        centerY = radius;
                        break;
                    case 2:
                        int centerX3 = roundedCornerFrame.width();
                        int centerY2 = roundedCornerFrame.height() - radius;
                        centerX = centerX3 - radius;
                        centerY = centerY2;
                        break;
                    case 3:
                        int centerY3 = roundedCornerFrame.height() - radius;
                        centerX = radius;
                        centerY = centerY3;
                        break;
                    default:
                        throw new IllegalArgumentException("The position is not one of the RoundedCornerPosition =" + i4);
                }
                i = i4;
                roundedCorners = roundedCorners3;
                i2 = i3;
                roundedCorners[i] = insetRoundedCorner(i4, radius, centerX, centerY, insetLeft, insetTop, insetRight, insetBottom);
            }
            i4 = i + 1;
            roundedCorners2 = this;
            roundedCorners3 = roundedCorners;
            i3 = i2;
        }
        return new RoundedCorners(roundedCorners3);
    }

    public RoundedCorners inset(int insetLeft, int insetTop, int insetRight, int insetBottom) {
        RoundedCorner[] roundedCorners = new RoundedCorner[4];
        for (int i = 0; i < 4; i++) {
            roundedCorners[i] = insetRoundedCorner(i, this.mRoundedCorners[i].getRadius(), this.mRoundedCorners[i].getCenter().f76x, this.mRoundedCorners[i].getCenter().f77y, insetLeft, insetTop, insetRight, insetBottom);
        }
        return new RoundedCorners(roundedCorners);
    }

    private RoundedCorner insetRoundedCorner(int position, int radius, int centerX, int centerY, int insetLeft, int insetTop, int insetRight, int insetBottom) {
        if (this.mRoundedCorners[position].isEmpty()) {
            return new RoundedCorner(position);
        }
        boolean hasRoundedCorner = true;
        switch (position) {
            case 0:
                if (radius <= insetTop || radius <= insetLeft) {
                    hasRoundedCorner = false;
                    break;
                }
            case 1:
                if (radius <= insetTop || radius <= insetRight) {
                    hasRoundedCorner = false;
                    break;
                }
            case 2:
                if (radius <= insetBottom || radius <= insetRight) {
                    hasRoundedCorner = false;
                    break;
                }
            case 3:
                if (radius <= insetBottom || radius <= insetLeft) {
                    hasRoundedCorner = false;
                    break;
                }
            default:
                throw new IllegalArgumentException("The position is not one of the RoundedCornerPosition =" + position);
        }
        return new RoundedCorner(position, radius, hasRoundedCorner ? centerX - insetLeft : 0, hasRoundedCorner ? centerY - insetTop : 0);
    }

    public RoundedCorner getRoundedCorner(int position) {
        if (this.mRoundedCorners[position].isEmpty()) {
            return null;
        }
        return new RoundedCorner(this.mRoundedCorners[position]);
    }

    public void setRoundedCorner(int position, RoundedCorner roundedCorner) {
        this.mRoundedCorners[position] = roundedCorner == null ? new RoundedCorner(position) : roundedCorner;
    }

    public RoundedCorner[] getAllRoundedCorners() {
        RoundedCorner[] roundedCorners = new RoundedCorner[4];
        for (int i = 0; i < 4; i++) {
            roundedCorners[i] = new RoundedCorner(roundedCorners[i]);
        }
        return roundedCorners;
    }

    public RoundedCorners scale(float scale) {
        if (scale == 1.0f) {
            return this;
        }
        RoundedCorner[] roundedCorners = new RoundedCorner[4];
        for (int i = 0; i < 4; i++) {
            RoundedCorner roundedCorner = this.mRoundedCorners[i];
            roundedCorners[i] = new RoundedCorner(i, (int) (roundedCorner.getRadius() * scale), (int) (roundedCorner.getCenter().f76x * scale), (int) (roundedCorner.getCenter().f77y * scale));
        }
        return new RoundedCorners(roundedCorners);
    }

    public RoundedCorners rotate(int rotation, int initialDisplayWidth, int initialDisplayHeight) {
        if (rotation == 0) {
            return this;
        }
        boolean isSizeFlipped = true;
        if (rotation != 1 && rotation != 3) {
            isSizeFlipped = false;
        }
        RoundedCorner[] newCorners = new RoundedCorner[4];
        for (int i = 0; i < this.mRoundedCorners.length; i++) {
            int newPosistion = getRotatedIndex(i, rotation);
            newCorners[newPosistion] = createRoundedCorner(newPosistion, this.mRoundedCorners[i].getRadius(), isSizeFlipped ? initialDisplayHeight : initialDisplayWidth, isSizeFlipped ? initialDisplayWidth : initialDisplayHeight);
        }
        return new RoundedCorners(newCorners);
    }

    private static RoundedCorner createRoundedCorner(int position, int radius, int displayWidth, int displayHeight) {
        switch (position) {
            case 0:
                return new RoundedCorner(0, radius, radius > 0 ? radius : 0, radius > 0 ? radius : 0);
            case 1:
                return new RoundedCorner(1, radius, radius > 0 ? displayWidth - radius : 0, radius > 0 ? radius : 0);
            case 2:
                return new RoundedCorner(2, radius, radius > 0 ? displayWidth - radius : 0, radius > 0 ? displayHeight - radius : 0);
            case 3:
                return new RoundedCorner(3, radius, radius > 0 ? radius : 0, radius > 0 ? displayHeight - radius : 0);
            default:
                throw new IllegalArgumentException("The position is not one of the RoundedCornerPosition =" + position);
        }
    }

    private static int getRotatedIndex(int position, int rotation) {
        return ((position - rotation) + 4) % 4;
    }

    public int hashCode() {
        RoundedCorner[] roundedCornerArr;
        int result = 0;
        for (RoundedCorner roundedCorner : this.mRoundedCorners) {
            result = (result * 31) + roundedCorner.hashCode();
        }
        return result;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RoundedCorners) {
            RoundedCorners r = (RoundedCorners) o;
            return Arrays.deepEquals(this.mRoundedCorners, r.mRoundedCorners);
        }
        return false;
    }

    public String toString() {
        return "RoundedCorners{" + Arrays.toString(this.mRoundedCorners) + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (equals(NO_ROUNDED_CORNERS)) {
            dest.writeInt(0);
            return;
        }
        dest.writeInt(1);
        dest.writeTypedArray(this.mRoundedCorners, flags);
    }
}
