package android.window;

import android.graphics.Rect;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes4.dex */
public final class TaskFragmentCreationParams implements Parcelable {
    public static final Parcelable.Creator<TaskFragmentCreationParams> CREATOR = new Parcelable.Creator<TaskFragmentCreationParams>() { // from class: android.window.TaskFragmentCreationParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TaskFragmentCreationParams createFromParcel(Parcel in) {
            return new TaskFragmentCreationParams(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TaskFragmentCreationParams[] newArray(int size) {
            return new TaskFragmentCreationParams[size];
        }
    };
    private final IBinder mFragmentToken;
    private final Rect mInitialRelativeBounds;
    private final TaskFragmentOrganizerToken mOrganizer;
    private final IBinder mOwnerToken;
    private final IBinder mPairedActivityToken;
    private final IBinder mPairedPrimaryFragmentToken;
    private final int mWindowingMode;

    private TaskFragmentCreationParams(TaskFragmentOrganizerToken organizer, IBinder fragmentToken, IBinder ownerToken, Rect initialRelativeBounds, int windowingMode, IBinder pairedPrimaryFragmentToken, IBinder pairedActivityToken) {
        Rect rect = new Rect();
        this.mInitialRelativeBounds = rect;
        if (pairedPrimaryFragmentToken != null && pairedActivityToken != null) {
            throw new IllegalArgumentException("pairedPrimaryFragmentToken and pairedActivityToken should not be set at the same time.");
        }
        this.mOrganizer = organizer;
        this.mFragmentToken = fragmentToken;
        this.mOwnerToken = ownerToken;
        rect.set(initialRelativeBounds);
        this.mWindowingMode = windowingMode;
        this.mPairedPrimaryFragmentToken = pairedPrimaryFragmentToken;
        this.mPairedActivityToken = pairedActivityToken;
    }

    public TaskFragmentOrganizerToken getOrganizer() {
        return this.mOrganizer;
    }

    public IBinder getFragmentToken() {
        return this.mFragmentToken;
    }

    public IBinder getOwnerToken() {
        return this.mOwnerToken;
    }

    public Rect getInitialRelativeBounds() {
        return this.mInitialRelativeBounds;
    }

    public int getWindowingMode() {
        return this.mWindowingMode;
    }

    public IBinder getPairedPrimaryFragmentToken() {
        return this.mPairedPrimaryFragmentToken;
    }

    public IBinder getPairedActivityToken() {
        return this.mPairedActivityToken;
    }

    private TaskFragmentCreationParams(Parcel in) {
        Rect rect = new Rect();
        this.mInitialRelativeBounds = rect;
        this.mOrganizer = TaskFragmentOrganizerToken.CREATOR.createFromParcel(in);
        this.mFragmentToken = in.readStrongBinder();
        this.mOwnerToken = in.readStrongBinder();
        rect.readFromParcel(in);
        this.mWindowingMode = in.readInt();
        this.mPairedPrimaryFragmentToken = in.readStrongBinder();
        this.mPairedActivityToken = in.readStrongBinder();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        this.mOrganizer.writeToParcel(dest, flags);
        dest.writeStrongBinder(this.mFragmentToken);
        dest.writeStrongBinder(this.mOwnerToken);
        this.mInitialRelativeBounds.writeToParcel(dest, flags);
        dest.writeInt(this.mWindowingMode);
        dest.writeStrongBinder(this.mPairedPrimaryFragmentToken);
        dest.writeStrongBinder(this.mPairedActivityToken);
    }

    public String toString() {
        return "TaskFragmentCreationParams{ organizer=" + this.mOrganizer + " fragmentToken=" + this.mFragmentToken + " ownerToken=" + this.mOwnerToken + " initialRelativeBounds=" + this.mInitialRelativeBounds + " windowingMode=" + this.mWindowingMode + " pairedFragmentToken=" + this.mPairedPrimaryFragmentToken + " pairedActivityToken=" + this.mPairedActivityToken + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* loaded from: classes4.dex */
    public static final class Builder {
        private final IBinder mFragmentToken;
        private final TaskFragmentOrganizerToken mOrganizer;
        private final IBinder mOwnerToken;
        private IBinder mPairedActivityToken;
        private IBinder mPairedPrimaryFragmentToken;
        private final Rect mInitialRelativeBounds = new Rect();
        private int mWindowingMode = 0;

        public Builder(TaskFragmentOrganizerToken organizer, IBinder fragmentToken, IBinder ownerToken) {
            this.mOrganizer = organizer;
            this.mFragmentToken = fragmentToken;
            this.mOwnerToken = ownerToken;
        }

        public Builder setInitialRelativeBounds(Rect bounds) {
            this.mInitialRelativeBounds.set(bounds);
            return this;
        }

        public Builder setWindowingMode(int windowingMode) {
            this.mWindowingMode = windowingMode;
            return this;
        }

        public Builder setPairedPrimaryFragmentToken(IBinder fragmentToken) {
            this.mPairedPrimaryFragmentToken = fragmentToken;
            return this;
        }

        public Builder setPairedActivityToken(IBinder activityToken) {
            this.mPairedActivityToken = activityToken;
            return this;
        }

        public TaskFragmentCreationParams build() {
            return new TaskFragmentCreationParams(this.mOrganizer, this.mFragmentToken, this.mOwnerToken, this.mInitialRelativeBounds, this.mWindowingMode, this.mPairedPrimaryFragmentToken, this.mPairedActivityToken);
        }
    }
}
