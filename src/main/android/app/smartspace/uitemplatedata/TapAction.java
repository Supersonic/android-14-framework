package android.app.smartspace.uitemplatedata;

import android.annotation.SystemApi;
import android.app.PendingIntent;
import android.app.smartspace.SmartspaceUtils;
import android.content.Intent;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.UserHandle;
import android.text.TextUtils;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class TapAction implements Parcelable {
    public static final Parcelable.Creator<TapAction> CREATOR = new Parcelable.Creator<TapAction>() { // from class: android.app.smartspace.uitemplatedata.TapAction.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TapAction createFromParcel(Parcel in) {
            return new TapAction(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TapAction[] newArray(int size) {
            return new TapAction[size];
        }
    };
    private final Bundle mExtras;
    private final CharSequence mId;
    private final Intent mIntent;
    private final PendingIntent mPendingIntent;
    private final boolean mShouldShowOnLockscreen;
    private final UserHandle mUserHandle;

    TapAction(Parcel in) {
        this.mId = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        this.mIntent = (Intent) in.readTypedObject(Intent.CREATOR);
        this.mPendingIntent = (PendingIntent) in.readTypedObject(PendingIntent.CREATOR);
        this.mUserHandle = (UserHandle) in.readTypedObject(UserHandle.CREATOR);
        this.mExtras = in.readBundle();
        this.mShouldShowOnLockscreen = in.readBoolean();
    }

    private TapAction(CharSequence id, Intent intent, PendingIntent pendingIntent, UserHandle userHandle, Bundle extras, boolean shouldShowOnLockscreen) {
        this.mId = id;
        this.mIntent = intent;
        this.mPendingIntent = pendingIntent;
        this.mUserHandle = userHandle;
        this.mExtras = extras;
        this.mShouldShowOnLockscreen = shouldShowOnLockscreen;
    }

    public CharSequence getId() {
        return this.mId;
    }

    public Intent getIntent() {
        return this.mIntent;
    }

    public PendingIntent getPendingIntent() {
        return this.mPendingIntent;
    }

    public UserHandle getUserHandle() {
        return this.mUserHandle;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public boolean shouldShowOnLockscreen() {
        return this.mShouldShowOnLockscreen;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        TextUtils.writeToParcel(this.mId, out, flags);
        out.writeTypedObject(this.mIntent, flags);
        out.writeTypedObject(this.mPendingIntent, flags);
        out.writeTypedObject(this.mUserHandle, flags);
        out.writeBundle(this.mExtras);
        out.writeBoolean(this.mShouldShowOnLockscreen);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof TapAction) {
            TapAction that = (TapAction) o;
            return SmartspaceUtils.isEqual(this.mId, that.mId);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mId);
    }

    public String toString() {
        return "SmartspaceTapAction{mId=" + ((Object) this.mId) + "mIntent=" + this.mIntent + ", mPendingIntent=" + this.mPendingIntent + ", mUserHandle=" + this.mUserHandle + ", mExtras=" + this.mExtras + ", mShouldShowOnLockscreen=" + this.mShouldShowOnLockscreen + '}';
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static final class Builder {
        private Bundle mExtras;
        private CharSequence mId;
        private Intent mIntent;
        private PendingIntent mPendingIntent;
        private boolean mShouldShowOnLockScreen = false;
        private UserHandle mUserHandle;

        public Builder(CharSequence id) {
            this.mId = (CharSequence) Objects.requireNonNull(id);
        }

        public Builder setIntent(Intent intent) {
            this.mIntent = intent;
            return this;
        }

        public Builder setPendingIntent(PendingIntent pendingIntent) {
            this.mPendingIntent = pendingIntent;
            return this;
        }

        public Builder setUserHandle(UserHandle userHandle) {
            this.mUserHandle = userHandle;
            return this;
        }

        public Builder setExtras(Bundle extras) {
            this.mExtras = extras;
            return this;
        }

        public Builder setShouldShowOnLockscreen(boolean shouldShowOnLockScreen) {
            this.mShouldShowOnLockScreen = shouldShowOnLockScreen;
            return this;
        }

        public TapAction build() {
            if (this.mIntent == null && this.mPendingIntent == null && this.mExtras == null) {
                throw new IllegalStateException("Please assign at least 1 valid tap field");
            }
            return new TapAction(this.mId, this.mIntent, this.mPendingIntent, this.mUserHandle, this.mExtras, this.mShouldShowOnLockScreen);
        }
    }
}
