package com.android.internal.statusbar;

import android.annotation.NonNull;
import android.graphics.Rect;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.view.InsetsFlags;
import android.view.ViewDebug;
import com.android.internal.util.AnnotationValidations;
import java.util.Objects;
/* loaded from: classes4.dex */
public class LetterboxDetails implements Parcelable {
    public static final Parcelable.Creator<LetterboxDetails> CREATOR = new Parcelable.Creator<LetterboxDetails>() { // from class: com.android.internal.statusbar.LetterboxDetails.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LetterboxDetails[] newArray(int size) {
            return new LetterboxDetails[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LetterboxDetails createFromParcel(Parcel in) {
            return new LetterboxDetails(in);
        }
    };
    private final int mAppAppearance;
    private final Rect mLetterboxFullBounds;
    private final Rect mLetterboxInnerBounds;

    public Rect getLetterboxInnerBounds() {
        return this.mLetterboxInnerBounds;
    }

    public Rect getLetterboxFullBounds() {
        return this.mLetterboxFullBounds;
    }

    public int getAppAppearance() {
        return this.mAppAppearance;
    }

    public String appAppearanceToString() {
        return ViewDebug.flagsToString(InsetsFlags.class, "appearance", this.mAppAppearance);
    }

    public LetterboxDetails(Rect letterboxInnerBounds, Rect letterboxFullBounds, int appAppearance) {
        this.mLetterboxInnerBounds = letterboxInnerBounds;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) letterboxInnerBounds);
        this.mLetterboxFullBounds = letterboxFullBounds;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) letterboxFullBounds);
        this.mAppAppearance = appAppearance;
    }

    public String toString() {
        return "LetterboxDetails { letterboxInnerBounds = " + this.mLetterboxInnerBounds + ", letterboxFullBounds = " + this.mLetterboxFullBounds + ", appAppearance = " + appAppearanceToString() + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LetterboxDetails that = (LetterboxDetails) o;
        if (Objects.equals(this.mLetterboxInnerBounds, that.mLetterboxInnerBounds) && Objects.equals(this.mLetterboxFullBounds, that.mLetterboxFullBounds) && this.mAppAppearance == that.mAppAppearance) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + Objects.hashCode(this.mLetterboxInnerBounds);
        return (((_hash * 31) + Objects.hashCode(this.mLetterboxFullBounds)) * 31) + this.mAppAppearance;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedObject(this.mLetterboxInnerBounds, flags);
        dest.writeTypedObject(this.mLetterboxFullBounds, flags);
        dest.writeInt(this.mAppAppearance);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    protected LetterboxDetails(Parcel in) {
        Rect letterboxInnerBounds = (Rect) in.readTypedObject(Rect.CREATOR);
        Rect letterboxFullBounds = (Rect) in.readTypedObject(Rect.CREATOR);
        int appAppearance = in.readInt();
        this.mLetterboxInnerBounds = letterboxInnerBounds;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) letterboxInnerBounds);
        this.mLetterboxFullBounds = letterboxFullBounds;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) letterboxFullBounds);
        this.mAppAppearance = appAppearance;
    }

    @Deprecated
    private void __metadata() {
    }
}
