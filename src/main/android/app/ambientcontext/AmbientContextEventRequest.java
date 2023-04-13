package android.app.ambientcontext;

import android.annotation.NonNull;
import android.annotation.SystemApi;
import android.app.ambientcontext.AmbientContextEvent;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.PersistableBundle;
import android.util.ArraySet;
import com.android.internal.util.AnnotationValidations;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
@SystemApi
/* loaded from: classes.dex */
public final class AmbientContextEventRequest implements Parcelable {
    public static final Parcelable.Creator<AmbientContextEventRequest> CREATOR = new Parcelable.Creator<AmbientContextEventRequest>() { // from class: android.app.ambientcontext.AmbientContextEventRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AmbientContextEventRequest[] newArray(int size) {
            return new AmbientContextEventRequest[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AmbientContextEventRequest createFromParcel(Parcel in) {
            return new AmbientContextEventRequest(in);
        }
    };
    private final Set<Integer> mEventTypes;
    private final PersistableBundle mOptions;

    private AmbientContextEventRequest(Set<Integer> eventTypes, PersistableBundle options) {
        this.mEventTypes = eventTypes;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) eventTypes);
        Preconditions.checkArgument(!eventTypes.isEmpty(), "eventTypes cannot be empty");
        for (Integer num : eventTypes) {
            int eventType = num.intValue();
            AnnotationValidations.validate((Class<? extends Annotation>) AmbientContextEvent.EventCode.class, (Annotation) null, eventType);
        }
        this.mOptions = options;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) options);
    }

    public Set<Integer> getEventTypes() {
        return this.mEventTypes;
    }

    public PersistableBundle getOptions() {
        return this.mOptions;
    }

    public String toString() {
        return "AmbientContextEventRequest { eventTypes = " + this.mEventTypes + ", options = " + this.mOptions + " }";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeArraySet(new ArraySet<>(this.mEventTypes));
        dest.writeTypedObject(this.mOptions, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    private AmbientContextEventRequest(Parcel in) {
        Set<Integer> eventTypes = in.readArraySet(Integer.class.getClassLoader());
        PersistableBundle options = (PersistableBundle) in.readTypedObject(PersistableBundle.CREATOR);
        this.mEventTypes = eventTypes;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) eventTypes);
        Preconditions.checkArgument(!eventTypes.isEmpty(), "eventTypes cannot be empty");
        for (Integer num : eventTypes) {
            int eventType = num.intValue();
            AnnotationValidations.validate((Class<? extends Annotation>) AmbientContextEvent.EventCode.class, (Annotation) null, eventType);
        }
        this.mOptions = options;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) options);
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private long mBuilderFieldsSet = 0;
        private Set<Integer> mEventTypes;
        private PersistableBundle mOptions;

        public Builder addEventType(int value) {
            checkNotUsed();
            if (this.mEventTypes == null) {
                this.mBuilderFieldsSet |= 1;
                this.mEventTypes = new HashSet();
            }
            this.mEventTypes.add(Integer.valueOf(value));
            return this;
        }

        public Builder setOptions(PersistableBundle value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 2;
            this.mOptions = value;
            return this;
        }

        public AmbientContextEventRequest build() {
            checkNotUsed();
            long j = this.mBuilderFieldsSet | 4;
            this.mBuilderFieldsSet = j;
            if ((j & 1) == 0) {
                this.mEventTypes = new HashSet();
            }
            if ((this.mBuilderFieldsSet & 2) == 0) {
                this.mOptions = new PersistableBundle();
            }
            AmbientContextEventRequest o = new AmbientContextEventRequest(this.mEventTypes, this.mOptions);
            return o;
        }

        private void checkNotUsed() {
            if ((this.mBuilderFieldsSet & 4) != 0) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }
}
