package android.content.p001pm;

import android.annotation.IdRes;
import android.annotation.NonNull;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.lang.annotation.Annotation;
/* renamed from: android.content.pm.Attribution */
/* loaded from: classes.dex */
public final class Attribution implements Parcelable {
    public static final Parcelable.Creator<Attribution> CREATOR = new Parcelable.Creator<Attribution>() { // from class: android.content.pm.Attribution.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Attribution[] newArray(int size) {
            return new Attribution[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Attribution createFromParcel(Parcel in) {
            return new Attribution(in);
        }
    };
    private final int mLabel;
    private String mTag;

    public Attribution(String tag, int label) {
        this.mTag = tag;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) tag);
        this.mLabel = label;
        AnnotationValidations.validate((Class<? extends Annotation>) IdRes.class, (Annotation) null, label);
    }

    public String getTag() {
        return this.mTag;
    }

    public int getLabel() {
        return this.mLabel;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mTag);
        dest.writeInt(this.mLabel);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    Attribution(Parcel in) {
        String tag = in.readString();
        int label = in.readInt();
        this.mTag = tag;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) tag);
        this.mLabel = label;
        AnnotationValidations.validate((Class<? extends Annotation>) IdRes.class, (Annotation) null, label);
    }

    @Deprecated
    private void __metadata() {
    }
}
