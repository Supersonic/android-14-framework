package android.service.ambientcontext;

import android.annotation.NonNull;
import android.annotation.SystemApi;
import android.app.ambientcontext.AmbientContextEvent;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class AmbientContextDetectionResult implements Parcelable {
    public static final Parcelable.Creator<AmbientContextDetectionResult> CREATOR = new Parcelable.Creator<AmbientContextDetectionResult>() { // from class: android.service.ambientcontext.AmbientContextDetectionResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AmbientContextDetectionResult[] newArray(int size) {
            return new AmbientContextDetectionResult[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AmbientContextDetectionResult createFromParcel(Parcel in) {
            return new AmbientContextDetectionResult(in);
        }
    };
    public static final String RESULT_RESPONSE_BUNDLE_KEY = "android.app.ambientcontext.AmbientContextDetectionResultBundleKey";
    private final List<AmbientContextEvent> mEvents;
    private final String mPackageName;

    AmbientContextDetectionResult(List<AmbientContextEvent> events, String packageName) {
        this.mEvents = events;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) events);
        this.mPackageName = packageName;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) packageName);
    }

    public List<AmbientContextEvent> getEvents() {
        return this.mEvents;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String toString() {
        return "AmbientContextEventResponse { events = " + this.mEvents + ", packageName = " + this.mPackageName + " }";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) 0);
        dest.writeParcelableList(this.mEvents, flags);
        dest.writeString(this.mPackageName);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    AmbientContextDetectionResult(Parcel in) {
        in.readByte();
        ArrayList<AmbientContextEvent> events = new ArrayList<>();
        in.readParcelableList(events, AmbientContextEvent.class.getClassLoader(), AmbientContextEvent.class);
        String packageName = in.readString();
        this.mEvents = events;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) events);
        this.mPackageName = packageName;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) packageName);
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private long mBuilderFieldsSet = 0;
        private ArrayList<AmbientContextEvent> mEvents;
        private String mPackageName;

        public Builder(String packageName) {
            Objects.requireNonNull(packageName);
            this.mPackageName = packageName;
        }

        public Builder addEvent(AmbientContextEvent value) {
            checkNotUsed();
            if (this.mEvents == null) {
                this.mBuilderFieldsSet |= 1;
                this.mEvents = new ArrayList<>();
            }
            this.mEvents.add(value);
            return this;
        }

        public Builder addEvents(List<AmbientContextEvent> values) {
            checkNotUsed();
            if (this.mEvents == null) {
                this.mBuilderFieldsSet |= 1;
                this.mEvents = new ArrayList<>();
            }
            this.mEvents.addAll(values);
            return this;
        }

        public Builder clearEvents() {
            checkNotUsed();
            ArrayList<AmbientContextEvent> arrayList = this.mEvents;
            if (arrayList != null) {
                arrayList.clear();
            }
            return this;
        }

        public AmbientContextDetectionResult build() {
            checkNotUsed();
            long j = this.mBuilderFieldsSet | 2;
            this.mBuilderFieldsSet = j;
            if ((j & 1) == 0) {
                this.mEvents = new ArrayList<>();
            }
            AmbientContextDetectionResult o = new AmbientContextDetectionResult(this.mEvents, this.mPackageName);
            return o;
        }

        private void checkNotUsed() {
            if ((this.mBuilderFieldsSet & 2) != 0) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }
}
