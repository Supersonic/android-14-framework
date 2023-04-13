package android.app.smartspace.uitemplatedata;

import android.annotation.SystemApi;
import android.app.smartspace.SmartspaceUtils;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class Icon implements Parcelable {
    public static final Parcelable.Creator<Icon> CREATOR = new Parcelable.Creator<Icon>() { // from class: android.app.smartspace.uitemplatedata.Icon.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Icon createFromParcel(Parcel in) {
            return new Icon(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Icon[] newArray(int size) {
            return new Icon[size];
        }
    };
    private final CharSequence mContentDescription;
    private final android.graphics.drawable.Icon mIcon;
    private final boolean mShouldTint;

    Icon(Parcel in) {
        this.mIcon = (android.graphics.drawable.Icon) in.readTypedObject(android.graphics.drawable.Icon.CREATOR);
        this.mContentDescription = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        this.mShouldTint = in.readBoolean();
    }

    private Icon(android.graphics.drawable.Icon icon, CharSequence contentDescription, boolean shouldTint) {
        this.mIcon = icon;
        this.mContentDescription = contentDescription;
        this.mShouldTint = shouldTint;
    }

    public android.graphics.drawable.Icon getIcon() {
        return this.mIcon;
    }

    public CharSequence getContentDescription() {
        return this.mContentDescription;
    }

    public boolean shouldTint() {
        return this.mShouldTint;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Icon) {
            Icon that = (Icon) o;
            return this.mIcon.toString().equals(that.mIcon.toString()) && SmartspaceUtils.isEqual(this.mContentDescription, that.mContentDescription) && this.mShouldTint == that.mShouldTint;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mIcon.toString(), this.mContentDescription, Boolean.valueOf(this.mShouldTint));
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeTypedObject(this.mIcon, flags);
        TextUtils.writeToParcel(this.mContentDescription, out, flags);
        out.writeBoolean(this.mShouldTint);
    }

    public String toString() {
        return "SmartspaceIcon{mIcon=" + this.mIcon + ", mContentDescription=" + ((Object) this.mContentDescription) + ", mShouldTint=" + this.mShouldTint + '}';
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static final class Builder {
        private CharSequence mContentDescription;
        private android.graphics.drawable.Icon mIcon;
        private boolean mShouldTint = true;

        public Builder(android.graphics.drawable.Icon icon) {
            this.mIcon = (android.graphics.drawable.Icon) Objects.requireNonNull(icon);
        }

        public Builder setContentDescription(CharSequence contentDescription) {
            this.mContentDescription = contentDescription;
            return this;
        }

        public Builder setShouldTint(boolean shouldTint) {
            this.mShouldTint = shouldTint;
            return this;
        }

        public Icon build() {
            return new Icon(this.mIcon, this.mContentDescription, this.mShouldTint);
        }
    }
}
