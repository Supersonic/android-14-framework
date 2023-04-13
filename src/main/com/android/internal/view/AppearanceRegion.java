package com.android.internal.view;

import android.annotation.NonNull;
import android.graphics.Rect;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.view.InsetsFlags;
import android.view.ViewDebug;
import com.android.internal.util.AnnotationValidations;
/* loaded from: classes2.dex */
public class AppearanceRegion implements Parcelable {
    public static final Parcelable.Creator<AppearanceRegion> CREATOR = new Parcelable.Creator<AppearanceRegion>() { // from class: com.android.internal.view.AppearanceRegion.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AppearanceRegion[] newArray(int size) {
            return new AppearanceRegion[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AppearanceRegion createFromParcel(Parcel in) {
            return new AppearanceRegion(in);
        }
    };
    private int mAppearance;
    private Rect mBounds;

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AppearanceRegion sa = (AppearanceRegion) o;
        if (this.mAppearance == sa.mAppearance && this.mBounds.equals(sa.mBounds)) {
            return true;
        }
        return false;
    }

    public String toString() {
        String appearanceString = ViewDebug.flagsToString(InsetsFlags.class, "appearance", this.mAppearance);
        return "AppearanceRegion{" + appearanceString + " bounds=" + this.mBounds.toShortString() + "}";
    }

    public AppearanceRegion(int appearance, Rect bounds) {
        this.mAppearance = appearance;
        this.mBounds = bounds;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) bounds);
    }

    public int getAppearance() {
        return this.mAppearance;
    }

    public Rect getBounds() {
        return this.mBounds;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mAppearance);
        dest.writeTypedObject(this.mBounds, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    protected AppearanceRegion(Parcel in) {
        int appearance = in.readInt();
        Rect bounds = (Rect) in.readTypedObject(Rect.CREATOR);
        this.mAppearance = appearance;
        this.mBounds = bounds;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) bounds);
    }

    @Deprecated
    private void __metadata() {
    }
}
