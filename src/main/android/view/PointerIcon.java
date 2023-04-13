package android.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.C4057R;
import com.android.internal.util.XmlUtils;
/* loaded from: classes4.dex */
public final class PointerIcon implements Parcelable {
    private static final String TAG = "PointerIcon";
    public static final int TYPE_ALIAS = 1010;
    public static final int TYPE_ALL_SCROLL = 1013;
    public static final int TYPE_ARROW = 1000;
    public static final int TYPE_CELL = 1006;
    public static final int TYPE_CONTEXT_MENU = 1001;
    public static final int TYPE_COPY = 1011;
    public static final int TYPE_CROSSHAIR = 1007;
    public static final int TYPE_CUSTOM = -1;
    public static final int TYPE_DEFAULT = 1000;
    public static final int TYPE_GRAB = 1020;
    public static final int TYPE_GRABBING = 1021;
    public static final int TYPE_HAND = 1002;
    public static final int TYPE_HANDWRITING = 1022;
    public static final int TYPE_HELP = 1003;
    public static final int TYPE_HORIZONTAL_DOUBLE_ARROW = 1014;
    public static final int TYPE_NOT_SPECIFIED = 1;
    public static final int TYPE_NO_DROP = 1012;
    public static final int TYPE_NULL = 0;
    private static final int TYPE_OEM_FIRST = 10000;
    public static final int TYPE_SPOT_ANCHOR = 2002;
    public static final int TYPE_SPOT_HOVER = 2000;
    public static final int TYPE_SPOT_TOUCH = 2001;
    public static final int TYPE_TEXT = 1008;
    public static final int TYPE_TOP_LEFT_DIAGONAL_DOUBLE_ARROW = 1017;
    public static final int TYPE_TOP_RIGHT_DIAGONAL_DOUBLE_ARROW = 1016;
    public static final int TYPE_VERTICAL_DOUBLE_ARROW = 1015;
    public static final int TYPE_VERTICAL_TEXT = 1009;
    public static final int TYPE_WAIT = 1004;
    public static final int TYPE_ZOOM_IN = 1018;
    public static final int TYPE_ZOOM_OUT = 1019;
    private static DisplayManager.DisplayListener sDisplayListener;
    private Bitmap mBitmap;
    private Bitmap[] mBitmapFrames;
    private int mDurationPerFrame;
    private float mHotSpotX;
    private float mHotSpotY;
    private int mSystemIconResourceId;
    private final int mType;
    private static final PointerIcon gNullIcon = new PointerIcon(0);
    private static final SparseArray<SparseArray<PointerIcon>> gSystemIconsByDisplay = new SparseArray<>();
    private static boolean sUseLargeIcons = false;
    public static final Parcelable.Creator<PointerIcon> CREATOR = new Parcelable.Creator<PointerIcon>() { // from class: android.view.PointerIcon.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PointerIcon createFromParcel(Parcel in) {
            int type = in.readInt();
            if (type == 0) {
                return PointerIcon.getNullIcon();
            }
            int systemIconResourceId = in.readInt();
            if (systemIconResourceId != 0) {
                PointerIcon icon = new PointerIcon(type);
                icon.mSystemIconResourceId = systemIconResourceId;
                return icon;
            }
            Bitmap bitmap = Bitmap.CREATOR.createFromParcel(in);
            float hotSpotX = in.readFloat();
            float hotSpotY = in.readFloat();
            return PointerIcon.create(bitmap, hotSpotX, hotSpotY);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PointerIcon[] newArray(int size) {
            return new PointerIcon[size];
        }
    };

    private PointerIcon(int type) {
        this.mType = type;
    }

    public static PointerIcon getNullIcon() {
        return gNullIcon;
    }

    public static PointerIcon getDefaultIcon(Context context) {
        return getSystemIcon(context, 1000);
    }

    public static PointerIcon getSystemIcon(Context context, int type) {
        if (context == null) {
            throw new IllegalArgumentException("context must not be null");
        }
        if (type == 0) {
            return gNullIcon;
        }
        if (sDisplayListener == null) {
            registerDisplayListener(context);
        }
        int displayId = context.getDisplayId();
        SparseArray<SparseArray<PointerIcon>> sparseArray = gSystemIconsByDisplay;
        SparseArray<PointerIcon> systemIcons = sparseArray.get(displayId);
        if (systemIcons == null) {
            systemIcons = new SparseArray<>();
            sparseArray.put(displayId, systemIcons);
        }
        PointerIcon icon = systemIcons.get(type);
        if (icon != null) {
            return icon;
        }
        int typeIndex = getSystemIconTypeIndex(type);
        if (typeIndex == 0) {
            typeIndex = getSystemIconTypeIndex(1000);
        }
        int defStyle = sUseLargeIcons ? C4057R.C4062style.LargePointer : C4057R.C4062style.Pointer;
        TypedArray a = context.obtainStyledAttributes(null, C4057R.styleable.Pointer, 0, defStyle);
        int resourceId = a.getResourceId(typeIndex, -1);
        a.recycle();
        if (resourceId == -1) {
            Log.m104w(TAG, "Missing theme resources for pointer icon type " + type);
            return type == 1000 ? gNullIcon : getSystemIcon(context, 1000);
        }
        PointerIcon icon2 = new PointerIcon(type);
        if (((-16777216) & resourceId) == 16777216) {
            icon2.mSystemIconResourceId = resourceId;
        } else {
            icon2.loadResource(context, context.getResources(), resourceId);
        }
        systemIcons.append(type, icon2);
        return icon2;
    }

    public static void setUseLargeIcons(boolean use) {
        sUseLargeIcons = use;
        gSystemIconsByDisplay.clear();
    }

    public static PointerIcon create(Bitmap bitmap, float hotSpotX, float hotSpotY) {
        if (bitmap == null) {
            throw new IllegalArgumentException("bitmap must not be null");
        }
        validateHotSpot(bitmap, hotSpotX, hotSpotY);
        PointerIcon icon = new PointerIcon(-1);
        icon.mBitmap = bitmap;
        icon.mHotSpotX = hotSpotX;
        icon.mHotSpotY = hotSpotY;
        return icon;
    }

    public static PointerIcon load(Resources resources, int resourceId) {
        if (resources == null) {
            throw new IllegalArgumentException("resources must not be null");
        }
        PointerIcon icon = new PointerIcon(-1);
        icon.loadResource(null, resources, resourceId);
        return icon;
    }

    public PointerIcon load(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context must not be null");
        }
        if (this.mSystemIconResourceId == 0 || this.mBitmap != null) {
            return this;
        }
        PointerIcon result = new PointerIcon(this.mType);
        result.mSystemIconResourceId = this.mSystemIconResourceId;
        result.loadResource(context, context.getResources(), this.mSystemIconResourceId);
        return result;
    }

    public int getType() {
        return this.mType;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mType);
        if (this.mType != 0) {
            out.writeInt(this.mSystemIconResourceId);
            if (this.mSystemIconResourceId == 0) {
                this.mBitmap.writeToParcel(out, flags);
                out.writeFloat(this.mHotSpotX);
                out.writeFloat(this.mHotSpotY);
            }
        }
    }

    public boolean equals(Object other) {
        int i;
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof PointerIcon)) {
            return false;
        }
        PointerIcon otherIcon = (PointerIcon) other;
        if (this.mType != otherIcon.mType || (i = this.mSystemIconResourceId) != otherIcon.mSystemIconResourceId) {
            return false;
        }
        if (i != 0 || (this.mBitmap == otherIcon.mBitmap && this.mHotSpotX == otherIcon.mHotSpotX && this.mHotSpotY == otherIcon.mHotSpotY)) {
            return true;
        }
        return false;
    }

    private Bitmap getBitmapFromDrawable(BitmapDrawable bitmapDrawable) {
        Bitmap bitmap = bitmapDrawable.getBitmap();
        int scaledWidth = bitmapDrawable.getIntrinsicWidth();
        int scaledHeight = bitmapDrawable.getIntrinsicHeight();
        if (scaledWidth == bitmap.getWidth() && scaledHeight == bitmap.getHeight()) {
            return bitmap;
        }
        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF dst = new RectF(0.0f, 0.0f, scaledWidth, scaledHeight);
        Bitmap scaled = Bitmap.createBitmap(scaledWidth, scaledHeight, bitmap.getConfig());
        Canvas canvas = new Canvas(scaled);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(bitmap, src, dst, paint);
        return scaled;
    }

    private void loadResource(Context context, Resources resources, int resourceId) {
        Drawable drawable;
        Drawable drawable2;
        XmlResourceParser parser = resources.getXml(resourceId);
        try {
            try {
                XmlUtils.beginDocument(parser, "pointer-icon");
                TypedArray a = resources.obtainAttributes(parser, C4057R.styleable.PointerIcon);
                int bitmapRes = a.getResourceId(0, 0);
                float hotSpotX = a.getDimension(1, 0.0f);
                float hotSpotY = a.getDimension(2, 0.0f);
                a.recycle();
                if (bitmapRes == 0) {
                    throw new IllegalArgumentException("<pointer-icon> is missing bitmap attribute.");
                }
                if (context == null) {
                    drawable = resources.getDrawable(bitmapRes);
                } else {
                    drawable = context.getDrawable(bitmapRes);
                }
                if (drawable instanceof AnimationDrawable) {
                    AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
                    int frames = animationDrawable.getNumberOfFrames();
                    Drawable drawable3 = animationDrawable.getFrame(0);
                    if (frames != 1) {
                        this.mDurationPerFrame = animationDrawable.getDuration(0);
                        this.mBitmapFrames = new Bitmap[frames - 1];
                        int width = drawable3.getIntrinsicWidth();
                        int height = drawable3.getIntrinsicHeight();
                        for (int i = 1; i < frames; i++) {
                            Drawable drawableFrame = animationDrawable.getFrame(i);
                            if (!(drawableFrame instanceof BitmapDrawable)) {
                                throw new IllegalArgumentException("Frame of an animated pointer icon must refer to a bitmap drawable.");
                            }
                            if (drawableFrame.getIntrinsicWidth() != width || drawableFrame.getIntrinsicHeight() != height) {
                                throw new IllegalArgumentException("The bitmap size of " + i + "-th frame is different. All frames should have the exact same size and share the same hotspot.");
                            }
                            BitmapDrawable bitmapDrawableFrame = (BitmapDrawable) drawableFrame;
                            this.mBitmapFrames[i - 1] = getBitmapFromDrawable(bitmapDrawableFrame);
                        }
                        drawable2 = drawable3;
                    } else {
                        Log.m104w(TAG, "Animation icon with single frame -- simply treating the first frame as a normal bitmap icon.");
                        drawable2 = drawable3;
                    }
                    drawable = drawable2;
                }
                if (!(drawable instanceof BitmapDrawable)) {
                    throw new IllegalArgumentException("<pointer-icon> bitmap attribute must refer to a bitmap drawable.");
                }
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = getBitmapFromDrawable(bitmapDrawable);
                validateHotSpot(bitmap, hotSpotX, hotSpotY);
                this.mBitmap = bitmap;
                this.mHotSpotX = hotSpotX;
                this.mHotSpotY = hotSpotY;
            } catch (Exception ex) {
                throw new IllegalArgumentException("Exception parsing pointer icon resource.", ex);
            }
        } finally {
            parser.close();
        }
    }

    public String toString() {
        return "PointerIcon{type=" + typeToString(this.mType) + ", hotspotX=" + this.mHotSpotX + ", hotspotY=" + this.mHotSpotY + ", systemIconResourceId=" + this.mSystemIconResourceId + "}";
    }

    private static void validateHotSpot(Bitmap bitmap, float hotSpotX, float hotSpotY) {
        if (hotSpotX < 0.0f || hotSpotX >= bitmap.getWidth()) {
            throw new IllegalArgumentException("x hotspot lies outside of the bitmap area");
        }
        if (hotSpotY < 0.0f || hotSpotY >= bitmap.getHeight()) {
            throw new IllegalArgumentException("y hotspot lies outside of the bitmap area");
        }
    }

    private static int getSystemIconTypeIndex(int type) {
        switch (type) {
            case 1000:
                return 2;
            case 1001:
                return 4;
            case 1002:
                return 9;
            case 1003:
                return 11;
            case 1004:
                return 22;
            case 1006:
                return 3;
            case 1007:
                return 6;
            case 1008:
                return 17;
            case 1009:
                return 21;
            case 1010:
                return 0;
            case 1011:
                return 5;
            case 1012:
                return 13;
            case 1013:
                return 1;
            case 1014:
                return 12;
            case 1015:
                return 20;
            case 1016:
                return 19;
            case 1017:
                return 18;
            case 1018:
                return 23;
            case 1019:
                return 24;
            case 1020:
                return 7;
            case 1021:
                return 8;
            case 1022:
                return 10;
            case 2000:
                return 15;
            case 2001:
                return 16;
            case 2002:
                return 14;
            default:
                return 0;
        }
    }

    private static void registerDisplayListener(Context context) {
        sDisplayListener = new DisplayManager.DisplayListener() { // from class: android.view.PointerIcon.2
            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayAdded(int displayId) {
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayRemoved(int displayId) {
                PointerIcon.gSystemIconsByDisplay.remove(displayId);
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayChanged(int displayId) {
                PointerIcon.gSystemIconsByDisplay.remove(displayId);
            }
        };
        DisplayManager displayManager = (DisplayManager) context.getSystemService(DisplayManager.class);
        displayManager.registerDisplayListener(sDisplayListener, null);
    }

    public static String typeToString(int type) {
        switch (type) {
            case -1:
                return "CUSTOM";
            case 0:
                return "NULL";
            case 1:
                return "NOT_SPECIFIED";
            case 1000:
                return "ARROW";
            case 1001:
                return "CONTEXT_MENU";
            case 1002:
                return "HAND";
            case 1003:
                return "HELP";
            case 1004:
                return "WAIT";
            case 1006:
                return "CELL";
            case 1007:
                return "CROSSHAIR";
            case 1008:
                return "TEXT";
            case 1009:
                return "VERTICAL_TEXT";
            case 1010:
                return "ALIAS";
            case 1011:
                return "COPY";
            case 1012:
                return "NO_DROP";
            case 1013:
                return "ALL_SCROLL";
            case 1014:
                return "HORIZONTAL_DOUBLE_ARROW";
            case 1015:
                return "VERTICAL_DOUBLE_ARROW";
            case 1016:
                return "TOP_RIGHT_DIAGONAL_DOUBLE_ARROW";
            case 1017:
                return "TOP_LEFT_DIAGONAL_DOUBLE_ARROW";
            case 1018:
                return "ZOOM_IN";
            case 1019:
                return "ZOOM_OUT";
            case 1020:
                return "GRAB";
            case 1021:
                return "GRABBING";
            case 2000:
                return "SPOT_HOVER";
            case 2001:
                return "SPOT_TOUCH";
            case 2002:
                return "SPOT_ANCHOR";
            default:
                return Integer.toString(type);
        }
    }
}
