package android.view;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes4.dex */
public abstract class AbsSavedState implements Parcelable {
    private final Parcelable mSuperState;
    public static final AbsSavedState EMPTY_STATE = new AbsSavedState() { // from class: android.view.AbsSavedState.1
    };
    public static final Parcelable.Creator<AbsSavedState> CREATOR = new Parcelable.ClassLoaderCreator<AbsSavedState>() { // from class: android.view.AbsSavedState.2
        @Override // android.p008os.Parcelable.Creator
        public AbsSavedState createFromParcel(Parcel in) {
            return createFromParcel(in, (ClassLoader) null);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.ClassLoaderCreator
        public AbsSavedState createFromParcel(Parcel in, ClassLoader loader) {
            Parcelable superState = in.readParcelable(loader);
            if (superState != null) {
                throw new IllegalStateException("superState must be null");
            }
            return AbsSavedState.EMPTY_STATE;
        }

        @Override // android.p008os.Parcelable.Creator
        public AbsSavedState[] newArray(int size) {
            return new AbsSavedState[size];
        }
    };

    private AbsSavedState() {
        this.mSuperState = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbsSavedState(Parcelable superState) {
        if (superState == null) {
            throw new IllegalArgumentException("superState must not be null");
        }
        this.mSuperState = superState != EMPTY_STATE ? superState : null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbsSavedState(Parcel source) {
        this(source, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbsSavedState(Parcel source, ClassLoader loader) {
        Parcelable superState = source.readParcelable(loader);
        this.mSuperState = superState != null ? superState : EMPTY_STATE;
    }

    public final Parcelable getSuperState() {
        return this.mSuperState;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mSuperState, flags);
    }
}
