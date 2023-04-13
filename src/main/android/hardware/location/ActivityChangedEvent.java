package android.hardware.location;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes2.dex */
public class ActivityChangedEvent implements Parcelable {
    public static final Parcelable.Creator<ActivityChangedEvent> CREATOR = new Parcelable.Creator<ActivityChangedEvent>() { // from class: android.hardware.location.ActivityChangedEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ActivityChangedEvent createFromParcel(Parcel source) {
            int activityRecognitionEventsLength = source.readInt();
            ActivityRecognitionEvent[] activityRecognitionEvents = new ActivityRecognitionEvent[activityRecognitionEventsLength];
            source.readTypedArray(activityRecognitionEvents, ActivityRecognitionEvent.CREATOR);
            return new ActivityChangedEvent(activityRecognitionEvents);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ActivityChangedEvent[] newArray(int size) {
            return new ActivityChangedEvent[size];
        }
    };
    private final List<ActivityRecognitionEvent> mActivityRecognitionEvents;

    public ActivityChangedEvent(ActivityRecognitionEvent[] activityRecognitionEvents) {
        if (activityRecognitionEvents == null) {
            throw new InvalidParameterException("Parameter 'activityRecognitionEvents' must not be null.");
        }
        this.mActivityRecognitionEvents = Arrays.asList(activityRecognitionEvents);
    }

    public Iterable<ActivityRecognitionEvent> getActivityRecognitionEvents() {
        return this.mActivityRecognitionEvents;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        ActivityRecognitionEvent[] activityRecognitionEventArray = (ActivityRecognitionEvent[]) this.mActivityRecognitionEvents.toArray(new ActivityRecognitionEvent[0]);
        parcel.writeInt(activityRecognitionEventArray.length);
        parcel.writeTypedArray(activityRecognitionEventArray, flags);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("[ ActivityChangedEvent:");
        for (ActivityRecognitionEvent event : this.mActivityRecognitionEvents) {
            builder.append("\n    ");
            builder.append(event.toString());
        }
        builder.append("\n]");
        return builder.toString();
    }
}
