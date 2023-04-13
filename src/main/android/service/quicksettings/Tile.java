package android.service.quicksettings;

import android.app.PendingIntent;
import android.graphics.drawable.Icon;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
/* loaded from: classes3.dex */
public final class Tile implements Parcelable {
    public static final Parcelable.Creator<Tile> CREATOR = new Parcelable.Creator<Tile>() { // from class: android.service.quicksettings.Tile.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Tile createFromParcel(Parcel source) {
            return new Tile(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Tile[] newArray(int size) {
            return new Tile[size];
        }
    };
    public static final int STATE_ACTIVE = 2;
    public static final int STATE_INACTIVE = 1;
    public static final int STATE_UNAVAILABLE = 0;
    private static final String TAG = "Tile";
    private CharSequence mContentDescription;
    private Icon mIcon;
    private CharSequence mLabel;
    private PendingIntent mPendingIntent;
    private IQSService mService;
    private int mState = 1;
    private CharSequence mStateDescription;
    private CharSequence mSubtitle;
    private IBinder mToken;

    public Tile(Parcel source) {
        readFromParcel(source);
    }

    public Tile() {
    }

    public void setService(IQSService service, IBinder stub) {
        this.mService = service;
        this.mToken = stub;
    }

    public int getState() {
        return this.mState;
    }

    public void setState(int state) {
        this.mState = state;
    }

    public Icon getIcon() {
        return this.mIcon;
    }

    public void setIcon(Icon icon) {
        this.mIcon = icon;
    }

    public CharSequence getLabel() {
        return this.mLabel;
    }

    public void setLabel(CharSequence label) {
        this.mLabel = label;
    }

    public CharSequence getSubtitle() {
        return this.mSubtitle;
    }

    public void setSubtitle(CharSequence subtitle) {
        this.mSubtitle = subtitle;
    }

    public CharSequence getContentDescription() {
        return this.mContentDescription;
    }

    public CharSequence getStateDescription() {
        return this.mStateDescription;
    }

    public void setContentDescription(CharSequence contentDescription) {
        this.mContentDescription = contentDescription;
    }

    public void setStateDescription(CharSequence stateDescription) {
        this.mStateDescription = stateDescription;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void updateTile() {
        try {
            this.mService.updateQsTile(this, this.mToken);
        } catch (RemoteException e) {
            Log.m110e(TAG, "Couldn't update tile");
        }
    }

    public PendingIntent getActivityLaunchForClick() {
        return this.mPendingIntent;
    }

    public void setActivityLaunchForClick(PendingIntent pendingIntent) {
        if (pendingIntent != null && !pendingIntent.isActivity()) {
            throw new IllegalArgumentException();
        }
        this.mPendingIntent = pendingIntent;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (this.mIcon != null) {
            dest.writeByte((byte) 1);
            this.mIcon.writeToParcel(dest, flags);
        } else {
            dest.writeByte((byte) 0);
        }
        if (this.mPendingIntent != null) {
            dest.writeByte((byte) 1);
            this.mPendingIntent.writeToParcel(dest, flags);
        } else {
            dest.writeByte((byte) 0);
        }
        dest.writeInt(this.mState);
        TextUtils.writeToParcel(this.mLabel, dest, flags);
        TextUtils.writeToParcel(this.mSubtitle, dest, flags);
        TextUtils.writeToParcel(this.mContentDescription, dest, flags);
        TextUtils.writeToParcel(this.mStateDescription, dest, flags);
    }

    private void readFromParcel(Parcel source) {
        if (source.readByte() != 0) {
            this.mIcon = Icon.CREATOR.createFromParcel(source);
        } else {
            this.mIcon = null;
        }
        if (source.readByte() != 0) {
            this.mPendingIntent = PendingIntent.CREATOR.createFromParcel(source);
        } else {
            this.mPendingIntent = null;
        }
        this.mState = source.readInt();
        this.mLabel = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
        this.mSubtitle = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
        this.mContentDescription = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
        this.mStateDescription = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
    }
}
