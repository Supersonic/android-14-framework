package android.hardware.display;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.ArraySet;
import android.view.ContentRecordingSession;
import android.view.Surface;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes.dex */
public final class VirtualDisplayConfig implements Parcelable {
    public static final Parcelable.Creator<VirtualDisplayConfig> CREATOR = new Parcelable.Creator<VirtualDisplayConfig>() { // from class: android.hardware.display.VirtualDisplayConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualDisplayConfig[] newArray(int size) {
            return new VirtualDisplayConfig[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualDisplayConfig createFromParcel(Parcel in) {
            return new VirtualDisplayConfig(in);
        }
    };
    private ContentRecordingSession mContentRecordingSession;
    private final int mDensityDpi;
    private ArraySet<String> mDisplayCategories;
    private final int mDisplayIdToMirror;
    private final int mFlags;
    private final int mHeight;
    private final String mName;
    private final float mRequestedRefreshRate;
    private final Surface mSurface;
    private final String mUniqueId;
    private final int mWidth;
    private final boolean mWindowManagerMirroringEnabled;

    private VirtualDisplayConfig(String name, int width, int height, int densityDpi, int flags, Surface surface, String uniqueId, int displayIdToMirror, boolean windowManagerMirroringEnabled, ContentRecordingSession session, ArraySet<String> displayCategories, float requestedRefreshRate) {
        this.mDisplayCategories = null;
        this.mName = name;
        this.mWidth = width;
        this.mHeight = height;
        this.mDensityDpi = densityDpi;
        this.mFlags = flags;
        this.mSurface = surface;
        this.mUniqueId = uniqueId;
        this.mDisplayIdToMirror = displayIdToMirror;
        this.mWindowManagerMirroringEnabled = windowManagerMirroringEnabled;
        this.mContentRecordingSession = session;
        this.mDisplayCategories = displayCategories;
        this.mRequestedRefreshRate = requestedRefreshRate;
    }

    public String getName() {
        return this.mName;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getDensityDpi() {
        return this.mDensityDpi;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public Surface getSurface() {
        return this.mSurface;
    }

    public String getUniqueId() {
        return this.mUniqueId;
    }

    public int getDisplayIdToMirror() {
        return this.mDisplayIdToMirror;
    }

    public boolean isWindowManagerMirroringEnabled() {
        return this.mWindowManagerMirroringEnabled;
    }

    public ContentRecordingSession getContentRecordingSession() {
        return this.mContentRecordingSession;
    }

    public Set<String> getDisplayCategories() {
        return Collections.unmodifiableSet(this.mDisplayCategories);
    }

    public float getRequestedRefreshRate() {
        return this.mRequestedRefreshRate;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString8(this.mName);
        dest.writeInt(this.mWidth);
        dest.writeInt(this.mHeight);
        dest.writeInt(this.mDensityDpi);
        dest.writeInt(this.mFlags);
        dest.writeTypedObject(this.mSurface, flags);
        dest.writeString8(this.mUniqueId);
        dest.writeInt(this.mDisplayIdToMirror);
        dest.writeBoolean(this.mWindowManagerMirroringEnabled);
        dest.writeTypedObject(this.mContentRecordingSession, flags);
        dest.writeArraySet(this.mDisplayCategories);
        dest.writeFloat(this.mRequestedRefreshRate);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof VirtualDisplayConfig) {
            VirtualDisplayConfig that = (VirtualDisplayConfig) o;
            return Objects.equals(this.mName, that.mName) && this.mWidth == that.mWidth && this.mHeight == that.mHeight && this.mDensityDpi == that.mDensityDpi && this.mFlags == that.mFlags && Objects.equals(this.mSurface, that.mSurface) && Objects.equals(this.mUniqueId, that.mUniqueId) && this.mDisplayIdToMirror == that.mDisplayIdToMirror && this.mWindowManagerMirroringEnabled == that.mWindowManagerMirroringEnabled && Objects.equals(this.mContentRecordingSession, that.mContentRecordingSession) && Objects.equals(this.mDisplayCategories, that.mDisplayCategories) && this.mRequestedRefreshRate == that.mRequestedRefreshRate;
        }
        return false;
    }

    public int hashCode() {
        int hashCode = Objects.hash(this.mName, Integer.valueOf(this.mWidth), Integer.valueOf(this.mHeight), Integer.valueOf(this.mDensityDpi), Integer.valueOf(this.mFlags), this.mSurface, this.mUniqueId, Integer.valueOf(this.mDisplayIdToMirror), Boolean.valueOf(this.mWindowManagerMirroringEnabled), this.mContentRecordingSession, this.mDisplayCategories, Float.valueOf(this.mRequestedRefreshRate));
        return hashCode;
    }

    public String toString() {
        return "VirtualDisplayConfig( mName=" + this.mName + " mHeight=" + this.mHeight + " mWidth=" + this.mWidth + " mDensityDpi=" + this.mDensityDpi + " mFlags=" + this.mFlags + " mSurface=" + this.mSurface + " mUniqueId=" + this.mUniqueId + " mDisplayIdToMirror=" + this.mDisplayIdToMirror + " mWindowManagerMirroringEnabled=" + this.mWindowManagerMirroringEnabled + " mContentRecordingSession=" + this.mContentRecordingSession + " mDisplayCategories=" + this.mDisplayCategories + " mRequestedRefreshRate=" + this.mRequestedRefreshRate + NavigationBarInflaterView.KEY_CODE_END;
    }

    private VirtualDisplayConfig(Parcel in) {
        this.mDisplayCategories = null;
        this.mName = in.readString8();
        this.mWidth = in.readInt();
        this.mHeight = in.readInt();
        this.mDensityDpi = in.readInt();
        this.mFlags = in.readInt();
        this.mSurface = (Surface) in.readTypedObject(Surface.CREATOR);
        this.mUniqueId = in.readString8();
        this.mDisplayIdToMirror = in.readInt();
        this.mWindowManagerMirroringEnabled = in.readBoolean();
        this.mContentRecordingSession = (ContentRecordingSession) in.readTypedObject(ContentRecordingSession.CREATOR);
        this.mDisplayCategories = in.readArraySet(null);
        this.mRequestedRefreshRate = in.readFloat();
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private ContentRecordingSession mContentRecordingSession;
        private final int mDensityDpi;
        private final int mHeight;
        private final String mName;
        private final int mWidth;
        private int mFlags = 0;
        private Surface mSurface = null;
        private String mUniqueId = null;
        private int mDisplayIdToMirror = 0;
        private boolean mWindowManagerMirroringEnabled = false;
        private ArraySet<String> mDisplayCategories = new ArraySet<>();
        private float mRequestedRefreshRate = 0.0f;

        public Builder(String name, int width, int height, int densityDpi) {
            if (name == null) {
                throw new IllegalArgumentException("Virtual display name is required");
            }
            if (width <= 0) {
                throw new IllegalArgumentException("Virtual display width must be positive");
            }
            if (height <= 0) {
                throw new IllegalArgumentException("Virtual display height must be positive");
            }
            if (densityDpi <= 0) {
                throw new IllegalArgumentException("Virtual display density must be positive");
            }
            this.mName = name;
            this.mWidth = width;
            this.mHeight = height;
            this.mDensityDpi = densityDpi;
        }

        public Builder setFlags(int flags) {
            this.mFlags = flags;
            return this;
        }

        public Builder setSurface(Surface surface) {
            this.mSurface = surface;
            return this;
        }

        public Builder setUniqueId(String uniqueId) {
            this.mUniqueId = uniqueId;
            return this;
        }

        public Builder setDisplayIdToMirror(int displayIdToMirror) {
            this.mDisplayIdToMirror = displayIdToMirror;
            return this;
        }

        public Builder setWindowManagerMirroringEnabled(boolean windowManagerMirroringEnabled) {
            this.mWindowManagerMirroringEnabled = windowManagerMirroringEnabled;
            return this;
        }

        public Builder setContentRecordingSession(ContentRecordingSession session) {
            this.mContentRecordingSession = session;
            return this;
        }

        public Builder setDisplayCategories(Set<String> displayCategories) {
            this.mDisplayCategories.clear();
            this.mDisplayCategories.addAll((Collection) Objects.requireNonNull(displayCategories));
            return this;
        }

        public Builder addDisplayCategory(String displayCategory) {
            this.mDisplayCategories.add((String) Objects.requireNonNull(displayCategory));
            return this;
        }

        public Builder setRequestedRefreshRate(float requestedRefreshRate) {
            if (requestedRefreshRate < 0.0f) {
                throw new IllegalArgumentException("Virtual display requested refresh rate must be non-negative");
            }
            this.mRequestedRefreshRate = requestedRefreshRate;
            return this;
        }

        public VirtualDisplayConfig build() {
            return new VirtualDisplayConfig(this.mName, this.mWidth, this.mHeight, this.mDensityDpi, this.mFlags, this.mSurface, this.mUniqueId, this.mDisplayIdToMirror, this.mWindowManagerMirroringEnabled, this.mContentRecordingSession, this.mDisplayCategories, this.mRequestedRefreshRate);
        }
    }
}
