package android.view.selectiontoolbar;

import android.annotation.NonNull;
import android.graphics.Rect;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.view.SurfaceControlViewHost;
import com.android.internal.util.AnnotationValidations;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class WidgetInfo implements Parcelable {
    public static final Parcelable.Creator<WidgetInfo> CREATOR = new Parcelable.Creator<WidgetInfo>() { // from class: android.view.selectiontoolbar.WidgetInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WidgetInfo[] newArray(int size) {
            return new WidgetInfo[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WidgetInfo createFromParcel(Parcel in) {
            return new WidgetInfo(in);
        }
    };
    private final Rect mContentRect;
    private final SurfaceControlViewHost.SurfacePackage mSurfacePackage;
    private final long mWidgetToken;

    public WidgetInfo(long widgetToken, Rect contentRect, SurfaceControlViewHost.SurfacePackage surfacePackage) {
        this.mWidgetToken = widgetToken;
        this.mContentRect = contentRect;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) contentRect);
        this.mSurfacePackage = surfacePackage;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) surfacePackage);
    }

    public long getWidgetToken() {
        return this.mWidgetToken;
    }

    public Rect getContentRect() {
        return this.mContentRect;
    }

    public SurfaceControlViewHost.SurfacePackage getSurfacePackage() {
        return this.mSurfacePackage;
    }

    public String toString() {
        return "WidgetInfo { widgetToken = " + this.mWidgetToken + ", contentRect = " + this.mContentRect + ", surfacePackage = " + this.mSurfacePackage + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WidgetInfo that = (WidgetInfo) o;
        if (this.mWidgetToken == that.mWidgetToken && Objects.equals(this.mContentRect, that.mContentRect) && Objects.equals(this.mSurfacePackage, that.mSurfacePackage)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + Long.hashCode(this.mWidgetToken);
        return (((_hash * 31) + Objects.hashCode(this.mContentRect)) * 31) + Objects.hashCode(this.mSurfacePackage);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mWidgetToken);
        dest.writeTypedObject(this.mContentRect, flags);
        dest.writeTypedObject(this.mSurfacePackage, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    WidgetInfo(Parcel in) {
        long widgetToken = in.readLong();
        Rect contentRect = (Rect) in.readTypedObject(Rect.CREATOR);
        SurfaceControlViewHost.SurfacePackage surfacePackage = (SurfaceControlViewHost.SurfacePackage) in.readTypedObject(SurfaceControlViewHost.SurfacePackage.CREATOR);
        this.mWidgetToken = widgetToken;
        this.mContentRect = contentRect;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) contentRect);
        this.mSurfacePackage = surfacePackage;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) surfacePackage);
    }

    @Deprecated
    private void __metadata() {
    }
}
