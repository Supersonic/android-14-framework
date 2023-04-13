package android.widget.inline;

import android.annotation.NonNull;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Size;
import com.android.internal.util.AnnotationValidations;
import com.android.internal.widget.InlinePresentationStyleUtils;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class InlinePresentationSpec implements Parcelable {
    public static final Parcelable.Creator<InlinePresentationSpec> CREATOR = new Parcelable.Creator<InlinePresentationSpec>() { // from class: android.widget.inline.InlinePresentationSpec.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InlinePresentationSpec[] newArray(int size) {
            return new InlinePresentationSpec[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InlinePresentationSpec createFromParcel(Parcel in) {
            return new InlinePresentationSpec(in);
        }
    };
    private final Size mMaxSize;
    private final Size mMinSize;
    private final Bundle mStyle;

    /* JADX INFO: Access modifiers changed from: private */
    public static Bundle defaultStyle() {
        return Bundle.EMPTY;
    }

    private boolean styleEquals(Bundle style) {
        return InlinePresentationStyleUtils.bundleEquals(this.mStyle, style);
    }

    public void filterContentTypes() {
        InlinePresentationStyleUtils.filterContentTypes(this.mStyle);
    }

    /* loaded from: classes4.dex */
    static abstract class BaseBuilder {
        BaseBuilder() {
        }
    }

    InlinePresentationSpec(Size minSize, Size maxSize, Bundle style) {
        this.mMinSize = minSize;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) minSize);
        this.mMaxSize = maxSize;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) maxSize);
        this.mStyle = style;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) style);
    }

    public Size getMinSize() {
        return this.mMinSize;
    }

    public Size getMaxSize() {
        return this.mMaxSize;
    }

    public Bundle getStyle() {
        return this.mStyle;
    }

    public String toString() {
        return "InlinePresentationSpec { minSize = " + this.mMinSize + ", maxSize = " + this.mMaxSize + ", style = " + this.mStyle + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InlinePresentationSpec that = (InlinePresentationSpec) o;
        if (Objects.equals(this.mMinSize, that.mMinSize) && Objects.equals(this.mMaxSize, that.mMaxSize) && styleEquals(that.mStyle)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + Objects.hashCode(this.mMinSize);
        return (((_hash * 31) + Objects.hashCode(this.mMaxSize)) * 31) + Objects.hashCode(this.mStyle);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSize(this.mMinSize);
        dest.writeSize(this.mMaxSize);
        dest.writeBundle(this.mStyle);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    InlinePresentationSpec(Parcel in) {
        Size minSize = in.readSize();
        Size maxSize = in.readSize();
        Bundle style = in.readBundle();
        this.mMinSize = minSize;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) minSize);
        this.mMaxSize = maxSize;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) maxSize);
        this.mStyle = style;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) style);
    }

    /* loaded from: classes4.dex */
    public static final class Builder extends BaseBuilder {
        private long mBuilderFieldsSet = 0;
        private Size mMaxSize;
        private Size mMinSize;
        private Bundle mStyle;

        public Builder(Size minSize, Size maxSize) {
            this.mMinSize = minSize;
            AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) minSize);
            this.mMaxSize = maxSize;
            AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) maxSize);
        }

        public Builder setStyle(Bundle value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 4;
            this.mStyle = value;
            return this;
        }

        public InlinePresentationSpec build() {
            checkNotUsed();
            long j = this.mBuilderFieldsSet | 8;
            this.mBuilderFieldsSet = j;
            if ((j & 4) == 0) {
                this.mStyle = InlinePresentationSpec.defaultStyle();
            }
            InlinePresentationSpec o = new InlinePresentationSpec(this.mMinSize, this.mMaxSize, this.mStyle);
            return o;
        }

        private void checkNotUsed() {
            if ((this.mBuilderFieldsSet & 8) != 0) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }

    @Deprecated
    private void __metadata() {
    }
}
