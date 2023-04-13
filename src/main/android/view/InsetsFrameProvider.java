package android.view;

import android.app.admin.DevicePolicyResources;
import android.graphics.Insets;
import android.graphics.Rect;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.view.WindowInsets;
import android.view.WindowManager;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes4.dex */
public class InsetsFrameProvider implements Parcelable {
    private static final int HAS_INSETS_SIZE = 1;
    private static final int HAS_INSETS_SIZE_OVERRIDE = 2;
    public static final int SOURCE_CONTAINER_BOUNDS = 1;
    public static final int SOURCE_DISPLAY = 0;
    public static final int SOURCE_FRAME = 2;
    private final int mIndex;
    private Insets mInsetsSize;
    private InsetsSizeOverride[] mInsetsSizeOverrides;
    private Insets mMinimalInsetsSizeInDisplayCutoutSafe;
    private final IBinder mOwner;
    private int mSource;
    private final int mType;
    private static final Rect sTmpRect = new Rect();
    private static final Rect sTmpRect2 = new Rect();
    public static final Parcelable.Creator<InsetsFrameProvider> CREATOR = new Parcelable.Creator<InsetsFrameProvider>() { // from class: android.view.InsetsFrameProvider.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InsetsFrameProvider createFromParcel(Parcel in) {
            return new InsetsFrameProvider(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InsetsFrameProvider[] newArray(int size) {
            return new InsetsFrameProvider[size];
        }
    };

    public InsetsFrameProvider(IBinder owner, int index, int type) {
        this.mSource = 2;
        this.mInsetsSize = null;
        this.mInsetsSizeOverrides = null;
        this.mMinimalInsetsSizeInDisplayCutoutSafe = null;
        if (index < 0 || index >= 2048) {
            throw new IllegalArgumentException();
        }
        WindowInsets.Type.indexOf(type);
        this.mOwner = owner;
        this.mIndex = index;
        this.mType = type;
    }

    public IBinder getOwner() {
        return this.mOwner;
    }

    public int getIndex() {
        return this.mIndex;
    }

    public int getType() {
        return this.mType;
    }

    public InsetsFrameProvider setSource(int source) {
        this.mSource = source;
        return this;
    }

    public int getSource() {
        return this.mSource;
    }

    public InsetsFrameProvider setInsetsSize(Insets insetsSize) {
        this.mInsetsSize = insetsSize;
        return this;
    }

    public Insets getInsetsSize() {
        return this.mInsetsSize;
    }

    public InsetsFrameProvider setInsetsSizeOverrides(InsetsSizeOverride[] insetsSizeOverrides) {
        this.mInsetsSizeOverrides = insetsSizeOverrides;
        return this;
    }

    public InsetsSizeOverride[] getInsetsSizeOverrides() {
        return this.mInsetsSizeOverrides;
    }

    public InsetsFrameProvider setMinimalInsetsSizeInDisplayCutoutSafe(Insets minimalInsetsSizeInDisplayCutoutSafe) {
        this.mMinimalInsetsSizeInDisplayCutoutSafe = minimalInsetsSizeInDisplayCutoutSafe;
        return this;
    }

    public Insets getMinimalInsetsSizeInDisplayCutoutSafe() {
        return this.mMinimalInsetsSizeInDisplayCutoutSafe;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("InsetsFrameProvider: {");
        sb.append("owner=").append(this.mOwner);
        sb.append(", index=").append(this.mIndex);
        sb.append(", type=").append(WindowInsets.Type.toString(this.mType));
        sb.append(", source=").append(sourceToString(this.mSource));
        if (this.mInsetsSize != null) {
            sb.append(", insetsSize=").append(this.mInsetsSize);
        }
        if (this.mInsetsSizeOverrides != null) {
            sb.append(", insetsSizeOverrides=").append(Arrays.toString(this.mInsetsSizeOverrides));
        }
        sb.append("}");
        return sb.toString();
    }

    private static String sourceToString(int source) {
        switch (source) {
            case 0:
                return "DISPLAY";
            case 1:
                return "CONTAINER_BOUNDS";
            case 2:
                return "FRAME";
            default:
                return DevicePolicyResources.UNDEFINED;
        }
    }

    public InsetsFrameProvider(Parcel in) {
        this.mSource = 2;
        this.mInsetsSize = null;
        this.mInsetsSizeOverrides = null;
        this.mMinimalInsetsSizeInDisplayCutoutSafe = null;
        this.mOwner = in.readStrongBinder();
        this.mIndex = in.readInt();
        this.mType = in.readInt();
        int insetsSizeModified = in.readInt();
        this.mSource = in.readInt();
        if ((insetsSizeModified & 1) != 0) {
            this.mInsetsSize = Insets.CREATOR.createFromParcel(in);
        }
        if ((insetsSizeModified & 2) != 0) {
            this.mInsetsSizeOverrides = (InsetsSizeOverride[]) in.createTypedArray(InsetsSizeOverride.CREATOR);
        }
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeStrongBinder(this.mOwner);
        out.writeInt(this.mIndex);
        out.writeInt(this.mType);
        int insetsSizeModified = 0;
        if (this.mInsetsSize != null) {
            insetsSizeModified = 0 | 1;
        }
        if (this.mInsetsSizeOverrides != null) {
            insetsSizeModified |= 2;
        }
        out.writeInt(insetsSizeModified);
        out.writeInt(this.mSource);
        Insets insets = this.mInsetsSize;
        if (insets != null) {
            insets.writeToParcel(out, flags);
        }
        InsetsSizeOverride[] insetsSizeOverrideArr = this.mInsetsSizeOverrides;
        if (insetsSizeOverrideArr != null) {
            out.writeTypedArray(insetsSizeOverrideArr, flags);
        }
    }

    public boolean idEquals(InsetsFrameProvider o) {
        return Objects.equals(this.mOwner, o.mOwner) && this.mIndex == o.mIndex && this.mType == o.mType;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InsetsFrameProvider other = (InsetsFrameProvider) o;
        if (Objects.equals(this.mOwner, other.mOwner) && this.mIndex == other.mIndex && this.mType == other.mType && this.mSource == other.mSource && Objects.equals(this.mInsetsSize, other.mInsetsSize) && Arrays.equals(this.mInsetsSizeOverrides, other.mInsetsSizeOverrides)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mOwner, Integer.valueOf(this.mIndex), Integer.valueOf(this.mType), Integer.valueOf(this.mSource), this.mInsetsSize, Integer.valueOf(Arrays.hashCode(this.mInsetsSizeOverrides)));
    }

    public static void calculateInsetsFrame(Rect displayFrame, Rect containerBounds, Rect displayCutoutSafe, Rect inOutFrame, int source, Insets insetsSize, int privateFlags, Insets displayCutoutSafeInsetsSize) {
        boolean extendByCutout = false;
        if (source == 0) {
            inOutFrame.set(displayFrame);
        } else {
            if (source == 1) {
                inOutFrame.set(containerBounds);
            } else {
                extendByCutout = (privateFlags & 8192) != 0;
            }
        }
        if (insetsSize == null) {
            return;
        }
        if (displayCutoutSafeInsetsSize != null) {
            sTmpRect2.set(inOutFrame);
        }
        calculateInsetsFrame(inOutFrame, insetsSize);
        if (extendByCutout) {
            WindowLayout.extendFrameByCutout(displayCutoutSafe, displayFrame, inOutFrame, sTmpRect);
        }
        if (displayCutoutSafeInsetsSize != null) {
            Rect rect = sTmpRect2;
            calculateInsetsFrame(rect, displayCutoutSafeInsetsSize);
            WindowLayout.extendFrameByCutout(displayCutoutSafe, displayFrame, rect, sTmpRect);
            if (rect.contains(inOutFrame)) {
                inOutFrame.set(rect);
            }
        }
    }

    private static void calculateInsetsFrame(Rect inOutFrame, Insets insetsSize) {
        if (insetsSize.left != 0) {
            inOutFrame.right = inOutFrame.left + insetsSize.left;
        } else if (insetsSize.top != 0) {
            inOutFrame.bottom = inOutFrame.top + insetsSize.top;
        } else if (insetsSize.right != 0) {
            inOutFrame.left = inOutFrame.right - insetsSize.right;
        } else if (insetsSize.bottom != 0) {
            inOutFrame.top = inOutFrame.bottom - insetsSize.bottom;
        } else {
            inOutFrame.setEmpty();
        }
    }

    /* loaded from: classes4.dex */
    public static class InsetsSizeOverride implements Parcelable {
        public static final Parcelable.Creator<InsetsSizeOverride> CREATOR = new Parcelable.Creator<InsetsSizeOverride>() { // from class: android.view.InsetsFrameProvider.InsetsSizeOverride.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public InsetsSizeOverride createFromParcel(Parcel in) {
                return new InsetsSizeOverride(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public InsetsSizeOverride[] newArray(int size) {
                return new InsetsSizeOverride[size];
            }
        };
        private final Insets mInsetsSize;
        private final int mWindowType;

        protected InsetsSizeOverride(Parcel in) {
            this.mWindowType = in.readInt();
            this.mInsetsSize = (Insets) in.readParcelable(null, Insets.class);
        }

        public InsetsSizeOverride(int windowType, Insets insetsSize) {
            this.mWindowType = windowType;
            this.mInsetsSize = insetsSize;
        }

        public int getWindowType() {
            return this.mWindowType;
        }

        public Insets getInsetsSize() {
            return this.mInsetsSize;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(this.mWindowType);
            out.writeParcelable(this.mInsetsSize, flags);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(32);
            sb.append("TypedInsetsSize: {");
            sb.append("windowType=").append(ViewDebug.intToString(WindowManager.LayoutParams.class, "type", this.mWindowType));
            sb.append(", insetsSize=").append(this.mInsetsSize);
            sb.append("}");
            return sb.toString();
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mWindowType), this.mInsetsSize);
        }
    }
}
