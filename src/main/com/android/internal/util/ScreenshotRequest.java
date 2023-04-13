package com.android.internal.util;

import android.content.ComponentName;
import android.graphics.Bitmap;
import android.graphics.ColorSpace;
import android.graphics.Insets;
import android.graphics.ParcelableColorSpace;
import android.graphics.Rect;
import android.hardware.HardwareBuffer;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Log;
import java.util.Objects;
/* loaded from: classes3.dex */
public class ScreenshotRequest implements Parcelable {
    public static final Parcelable.Creator<ScreenshotRequest> CREATOR = new Parcelable.Creator<ScreenshotRequest>() { // from class: com.android.internal.util.ScreenshotRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ScreenshotRequest createFromParcel(Parcel source) {
            return new ScreenshotRequest(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ScreenshotRequest[] newArray(int size) {
            return new ScreenshotRequest[size];
        }
    };
    private static final String TAG = "ScreenshotRequest";
    private final Bitmap mBitmap;
    private final Rect mBoundsInScreen;
    private final Insets mInsets;
    private final int mSource;
    private final int mTaskId;
    private final ComponentName mTopComponent;
    private final int mType;
    private final int mUserId;

    private ScreenshotRequest(int type, int source, ComponentName topComponent, int taskId, int userId, Bitmap bitmap, Rect boundsInScreen, Insets insets) {
        this.mType = type;
        this.mSource = source;
        this.mTopComponent = topComponent;
        this.mTaskId = taskId;
        this.mUserId = userId;
        this.mBitmap = bitmap;
        this.mBoundsInScreen = boundsInScreen;
        this.mInsets = insets;
    }

    ScreenshotRequest(Parcel in) {
        this.mType = in.readInt();
        this.mSource = in.readInt();
        this.mTopComponent = (ComponentName) in.readTypedObject(ComponentName.CREATOR);
        this.mTaskId = in.readInt();
        this.mUserId = in.readInt();
        this.mBitmap = HardwareBitmapBundler.bundleToHardwareBitmap((Bundle) in.readTypedObject(Bundle.CREATOR));
        this.mBoundsInScreen = (Rect) in.readTypedObject(Rect.CREATOR);
        this.mInsets = (Insets) in.readTypedObject(Insets.CREATOR);
    }

    public int getType() {
        return this.mType;
    }

    public int getSource() {
        return this.mSource;
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    public Rect getBoundsInScreen() {
        return this.mBoundsInScreen;
    }

    public Insets getInsets() {
        return this.mInsets;
    }

    public int getTaskId() {
        return this.mTaskId;
    }

    public int getUserId() {
        return this.mUserId;
    }

    public ComponentName getTopComponent() {
        return this.mTopComponent;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mType);
        dest.writeInt(this.mSource);
        dest.writeTypedObject(this.mTopComponent, 0);
        dest.writeInt(this.mTaskId);
        dest.writeInt(this.mUserId);
        dest.writeTypedObject(HardwareBitmapBundler.hardwareBitmapToBundle(this.mBitmap), 0);
        dest.writeTypedObject(this.mBoundsInScreen, 0);
        dest.writeTypedObject(this.mInsets, 0);
    }

    /* loaded from: classes3.dex */
    public static class Builder {
        private Bitmap mBitmap;
        private Rect mBoundsInScreen;
        private final int mSource;
        private ComponentName mTopComponent;
        private final int mType;
        private Insets mInsets = Insets.NONE;
        private int mTaskId = -1;
        private int mUserId = -10000;

        public Builder(int type, int source) {
            if (type != 1 && type != 3) {
                throw new IllegalArgumentException("Invalid screenshot type requested!");
            }
            this.mType = type;
            this.mSource = source;
        }

        public ScreenshotRequest build() {
            if (this.mType == 1 && this.mBitmap != null) {
                Log.m104w(ScreenshotRequest.TAG, "Bitmap provided, but request is fullscreen. Bitmap will be ignored.");
            }
            if (this.mType == 3 && this.mBitmap == null) {
                throw new IllegalStateException("Request is PROVIDED_IMAGE, but no bitmap is provided!");
            }
            return new ScreenshotRequest(this.mType, this.mSource, this.mTopComponent, this.mTaskId, this.mUserId, this.mBitmap, this.mBoundsInScreen, this.mInsets);
        }

        public Builder setTopComponent(ComponentName topComponent) {
            this.mTopComponent = topComponent;
            return this;
        }

        public Builder setTaskId(int taskId) {
            this.mTaskId = taskId;
            return this;
        }

        public Builder setUserId(int userId) {
            this.mUserId = userId;
            return this;
        }

        public Builder setBitmap(Bitmap bitmap) {
            this.mBitmap = bitmap;
            return this;
        }

        public Builder setBoundsOnScreen(Rect bounds) {
            this.mBoundsInScreen = bounds;
            return this;
        }

        public Builder setInsets(Insets insets) {
            this.mInsets = insets;
            return this;
        }
    }

    /* loaded from: classes3.dex */
    private static final class HardwareBitmapBundler {
        private static final String KEY_BUFFER = "bitmap_util_buffer";
        private static final String KEY_COLOR_SPACE = "bitmap_util_color_space";

        private HardwareBitmapBundler() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static Bundle hardwareBitmapToBundle(Bitmap bitmap) {
            ParcelableColorSpace colorSpace;
            if (bitmap == null) {
                return null;
            }
            if (bitmap.getConfig() != Bitmap.Config.HARDWARE) {
                throw new IllegalArgumentException("Passed bitmap must have hardware config, found: " + bitmap.getConfig());
            }
            if (bitmap.getColorSpace() == null) {
                colorSpace = new ParcelableColorSpace(ColorSpace.get(ColorSpace.Named.SRGB));
            } else {
                colorSpace = new ParcelableColorSpace(bitmap.getColorSpace());
            }
            Bundle bundle = new Bundle();
            bundle.putParcelable(KEY_BUFFER, bitmap.getHardwareBuffer());
            bundle.putParcelable(KEY_COLOR_SPACE, colorSpace);
            return bundle;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static Bitmap bundleToHardwareBitmap(Bundle bundle) {
            if (bundle == null) {
                return null;
            }
            if (bundle.containsKey(KEY_BUFFER) && bundle.containsKey(KEY_COLOR_SPACE)) {
                HardwareBuffer buffer = (HardwareBuffer) bundle.getParcelable(KEY_BUFFER, HardwareBuffer.class);
                ParcelableColorSpace colorSpace = (ParcelableColorSpace) bundle.getParcelable(KEY_COLOR_SPACE, ParcelableColorSpace.class);
                return Bitmap.wrapHardwareBuffer((HardwareBuffer) Objects.requireNonNull(buffer), colorSpace.getColorSpace());
            }
            throw new IllegalArgumentException("Bundle does not contain a hardware bitmap");
        }
    }
}
