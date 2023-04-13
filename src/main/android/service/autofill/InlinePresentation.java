package android.service.autofill;

import android.annotation.NonNull;
import android.app.slice.Slice;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.widget.inline.InlinePresentationSpec;
import com.android.internal.util.AnnotationValidations;
import java.util.List;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class InlinePresentation implements Parcelable {
    public static final Parcelable.Creator<InlinePresentation> CREATOR = new Parcelable.Creator<InlinePresentation>() { // from class: android.service.autofill.InlinePresentation.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InlinePresentation[] newArray(int size) {
            return new InlinePresentation[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InlinePresentation createFromParcel(Parcel in) {
            return new InlinePresentation(in);
        }
    };
    private final InlinePresentationSpec mInlinePresentationSpec;
    private final boolean mPinned;
    private final Slice mSlice;

    public String[] getAutofillHints() {
        List<String> hints = this.mSlice.getHints();
        return (String[]) hints.toArray(new String[hints.size()]);
    }

    public static InlinePresentation createTooltipPresentation(Slice slice, InlinePresentationSpec spec) {
        return new InlinePresentation(slice, spec, false);
    }

    public InlinePresentation(Slice slice, InlinePresentationSpec inlinePresentationSpec, boolean pinned) {
        this.mSlice = slice;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) slice);
        this.mInlinePresentationSpec = inlinePresentationSpec;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) inlinePresentationSpec);
        this.mPinned = pinned;
    }

    public Slice getSlice() {
        return this.mSlice;
    }

    public InlinePresentationSpec getInlinePresentationSpec() {
        return this.mInlinePresentationSpec;
    }

    public boolean isPinned() {
        return this.mPinned;
    }

    public String toString() {
        return "InlinePresentation { slice = " + this.mSlice + ", inlinePresentationSpec = " + this.mInlinePresentationSpec + ", pinned = " + this.mPinned + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InlinePresentation that = (InlinePresentation) o;
        if (Objects.equals(this.mSlice, that.mSlice) && Objects.equals(this.mInlinePresentationSpec, that.mInlinePresentationSpec) && this.mPinned == that.mPinned) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + Objects.hashCode(this.mSlice);
        return (((_hash * 31) + Objects.hashCode(this.mInlinePresentationSpec)) * 31) + Boolean.hashCode(this.mPinned);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mPinned ? (byte) (0 | 4) : (byte) 0;
        dest.writeByte(flg);
        dest.writeTypedObject(this.mSlice, flags);
        dest.writeTypedObject(this.mInlinePresentationSpec, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    InlinePresentation(Parcel in) {
        byte flg = in.readByte();
        boolean pinned = (flg & 4) != 0;
        Slice slice = (Slice) in.readTypedObject(Slice.CREATOR);
        InlinePresentationSpec inlinePresentationSpec = (InlinePresentationSpec) in.readTypedObject(InlinePresentationSpec.CREATOR);
        this.mSlice = slice;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) slice);
        this.mInlinePresentationSpec = inlinePresentationSpec;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) inlinePresentationSpec);
        this.mPinned = pinned;
    }

    @Deprecated
    private void __metadata() {
    }
}
