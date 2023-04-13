package android.app.smartspace;

import android.annotation.SystemApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.UserHandle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class SmartspaceAction implements Parcelable {
    public static final Parcelable.Creator<SmartspaceAction> CREATOR = new Parcelable.Creator<SmartspaceAction>() { // from class: android.app.smartspace.SmartspaceAction.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SmartspaceAction createFromParcel(Parcel in) {
            return new SmartspaceAction(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SmartspaceAction[] newArray(int size) {
            return new SmartspaceAction[size];
        }
    };
    private static final String TAG = "SmartspaceAction";
    private final CharSequence mContentDescription;
    private Bundle mExtras;
    private final Icon mIcon;
    private final String mId;
    private final Intent mIntent;
    private final PendingIntent mPendingIntent;
    private final CharSequence mSubtitle;
    private final CharSequence mTitle;
    private final UserHandle mUserHandle;

    SmartspaceAction(Parcel in) {
        this.mId = in.readString();
        this.mIcon = (Icon) in.readTypedObject(Icon.CREATOR);
        this.mTitle = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        this.mSubtitle = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        this.mContentDescription = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        this.mPendingIntent = (PendingIntent) in.readTypedObject(PendingIntent.CREATOR);
        this.mIntent = (Intent) in.readTypedObject(Intent.CREATOR);
        this.mUserHandle = (UserHandle) in.readTypedObject(UserHandle.CREATOR);
        this.mExtras = in.readBundle();
    }

    private SmartspaceAction(String id, Icon icon, CharSequence title, CharSequence subtitle, CharSequence contentDescription, PendingIntent pendingIntent, Intent intent, UserHandle userHandle, Bundle extras) {
        this.mId = (String) Objects.requireNonNull(id);
        this.mIcon = icon;
        this.mTitle = (CharSequence) Objects.requireNonNull(title);
        this.mSubtitle = subtitle;
        this.mContentDescription = contentDescription;
        this.mPendingIntent = pendingIntent;
        this.mIntent = intent;
        this.mUserHandle = userHandle;
        this.mExtras = extras;
    }

    public String getId() {
        return this.mId;
    }

    public Icon getIcon() {
        return this.mIcon;
    }

    public CharSequence getTitle() {
        return this.mTitle;
    }

    public CharSequence getSubtitle() {
        return this.mSubtitle;
    }

    public CharSequence getContentDescription() {
        return this.mContentDescription;
    }

    public PendingIntent getPendingIntent() {
        return this.mPendingIntent;
    }

    public Intent getIntent() {
        return this.mIntent;
    }

    public UserHandle getUserHandle() {
        return this.mUserHandle;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof SmartspaceAction) {
            SmartspaceAction that = (SmartspaceAction) o;
            return this.mId.equals(that.mId);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mId);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.mId);
        out.writeTypedObject(this.mIcon, flags);
        TextUtils.writeToParcel(this.mTitle, out, flags);
        TextUtils.writeToParcel(this.mSubtitle, out, flags);
        TextUtils.writeToParcel(this.mContentDescription, out, flags);
        out.writeTypedObject(this.mPendingIntent, flags);
        out.writeTypedObject(this.mIntent, flags);
        out.writeTypedObject(this.mUserHandle, flags);
        out.writeBundle(this.mExtras);
    }

    public String toString() {
        return "SmartspaceAction{mId='" + this.mId + DateFormat.QUOTE + ", mIcon=" + this.mIcon + ", mTitle=" + ((Object) this.mTitle) + ", mSubtitle=" + ((Object) this.mSubtitle) + ", mContentDescription=" + ((Object) this.mContentDescription) + ", mPendingIntent=" + this.mPendingIntent + ", mIntent=" + this.mIntent + ", mUserHandle=" + this.mUserHandle + ", mExtras=" + this.mExtras + '}';
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static final class Builder {
        private CharSequence mContentDescription;
        private Bundle mExtras;
        private Icon mIcon;
        private String mId;
        private Intent mIntent;
        private PendingIntent mPendingIntent;
        private CharSequence mSubtitle;
        private CharSequence mTitle;
        private UserHandle mUserHandle;

        public Builder(String id, String title) {
            this.mId = (String) Objects.requireNonNull(id);
            this.mTitle = (CharSequence) Objects.requireNonNull(title);
        }

        public Builder setIcon(Icon icon) {
            this.mIcon = icon;
            return this;
        }

        public Builder setSubtitle(CharSequence subtitle) {
            this.mSubtitle = subtitle;
            return this;
        }

        public Builder setContentDescription(CharSequence contentDescription) {
            this.mContentDescription = contentDescription;
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

        public Builder setIntent(Intent intent) {
            this.mIntent = intent;
            return this;
        }

        public Builder setExtras(Bundle extras) {
            this.mExtras = extras;
            return this;
        }

        public SmartspaceAction build() {
            return new SmartspaceAction(this.mId, this.mIcon, this.mTitle, this.mSubtitle, this.mContentDescription, this.mPendingIntent, this.mIntent, this.mUserHandle, this.mExtras);
        }
    }
}
