package android.app;

import android.graphics.Rect;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Rational;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public final class PictureInPictureParams implements Parcelable {
    public static final Parcelable.Creator<PictureInPictureParams> CREATOR = new Parcelable.Creator<PictureInPictureParams>() { // from class: android.app.PictureInPictureParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PictureInPictureParams createFromParcel(Parcel in) {
            return new PictureInPictureParams(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PictureInPictureParams[] newArray(int size) {
            return new PictureInPictureParams[size];
        }
    };
    private Rational mAspectRatio;
    private Boolean mAutoEnterEnabled;
    private RemoteAction mCloseAction;
    private Rational mExpandedAspectRatio;
    private Boolean mIsLaunchIntoPip;
    private Boolean mSeamlessResizeEnabled;
    private Rect mSourceRectHint;
    private CharSequence mSubtitle;
    private CharSequence mTitle;
    private List<RemoteAction> mUserActions;

    /* loaded from: classes.dex */
    public static class Builder {
        private Rational mAspectRatio;
        private Boolean mAutoEnterEnabled;
        private RemoteAction mCloseAction;
        private Rational mExpandedAspectRatio;
        private Boolean mIsLaunchIntoPip;
        private Boolean mSeamlessResizeEnabled;
        private Rect mSourceRectHint;
        private CharSequence mSubtitle;
        private CharSequence mTitle;
        private List<RemoteAction> mUserActions;

        public Builder() {
        }

        public Builder(PictureInPictureParams original) {
            this.mAspectRatio = original.mAspectRatio;
            this.mUserActions = original.mUserActions;
            this.mCloseAction = original.mCloseAction;
            this.mSourceRectHint = original.mSourceRectHint;
            this.mAutoEnterEnabled = original.mAutoEnterEnabled;
            this.mSeamlessResizeEnabled = original.mSeamlessResizeEnabled;
            this.mTitle = original.mTitle;
            this.mSubtitle = original.mSubtitle;
            this.mIsLaunchIntoPip = original.mIsLaunchIntoPip;
        }

        public Builder setAspectRatio(Rational aspectRatio) {
            this.mAspectRatio = aspectRatio;
            return this;
        }

        public Builder setExpandedAspectRatio(Rational expandedAspectRatio) {
            this.mExpandedAspectRatio = expandedAspectRatio;
            return this;
        }

        public Builder setActions(List<RemoteAction> actions) {
            if (this.mUserActions != null) {
                this.mUserActions = null;
            }
            if (actions != null) {
                this.mUserActions = new ArrayList(actions);
            }
            return this;
        }

        public Builder setCloseAction(RemoteAction action) {
            this.mCloseAction = action;
            return this;
        }

        public Builder setSourceRectHint(Rect launchBounds) {
            if (launchBounds == null) {
                this.mSourceRectHint = null;
            } else {
                this.mSourceRectHint = new Rect(launchBounds);
            }
            return this;
        }

        public Builder setAutoEnterEnabled(boolean autoEnterEnabled) {
            this.mAutoEnterEnabled = Boolean.valueOf(autoEnterEnabled);
            return this;
        }

        public Builder setSeamlessResizeEnabled(boolean seamlessResizeEnabled) {
            this.mSeamlessResizeEnabled = Boolean.valueOf(seamlessResizeEnabled);
            return this;
        }

        public Builder setTitle(CharSequence title) {
            this.mTitle = title;
            return this;
        }

        public Builder setSubtitle(CharSequence subtitle) {
            this.mSubtitle = subtitle;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setIsLaunchIntoPip(boolean isLaunchIntoPip) {
            this.mIsLaunchIntoPip = Boolean.valueOf(isLaunchIntoPip);
            return this;
        }

        public PictureInPictureParams build() {
            PictureInPictureParams params = new PictureInPictureParams(this.mAspectRatio, this.mExpandedAspectRatio, this.mUserActions, this.mCloseAction, this.mSourceRectHint, this.mAutoEnterEnabled, this.mSeamlessResizeEnabled, this.mTitle, this.mSubtitle, this.mIsLaunchIntoPip);
            return params;
        }
    }

    PictureInPictureParams() {
    }

    PictureInPictureParams(Parcel in) {
        this.mAspectRatio = readRationalFromParcel(in);
        this.mExpandedAspectRatio = readRationalFromParcel(in);
        if (in.readInt() != 0) {
            ArrayList arrayList = new ArrayList();
            this.mUserActions = arrayList;
            in.readTypedList(arrayList, RemoteAction.CREATOR);
        }
        this.mCloseAction = (RemoteAction) in.readTypedObject(RemoteAction.CREATOR);
        if (in.readInt() != 0) {
            this.mSourceRectHint = Rect.CREATOR.createFromParcel(in);
        }
        if (in.readInt() != 0) {
            this.mAutoEnterEnabled = Boolean.valueOf(in.readBoolean());
        }
        if (in.readInt() != 0) {
            this.mSeamlessResizeEnabled = Boolean.valueOf(in.readBoolean());
        }
        if (in.readInt() != 0) {
            this.mTitle = in.readCharSequence();
        }
        if (in.readInt() != 0) {
            this.mSubtitle = in.readCharSequence();
        }
        if (in.readInt() != 0) {
            this.mIsLaunchIntoPip = Boolean.valueOf(in.readBoolean());
        }
    }

    PictureInPictureParams(Rational aspectRatio, Rational expandedAspectRatio, List<RemoteAction> actions, RemoteAction closeAction, Rect sourceRectHint, Boolean autoEnterEnabled, Boolean seamlessResizeEnabled, CharSequence title, CharSequence subtitle, Boolean isLaunchIntoPip) {
        this.mAspectRatio = aspectRatio;
        this.mExpandedAspectRatio = expandedAspectRatio;
        this.mUserActions = actions;
        this.mCloseAction = closeAction;
        this.mSourceRectHint = sourceRectHint;
        this.mAutoEnterEnabled = autoEnterEnabled;
        this.mSeamlessResizeEnabled = seamlessResizeEnabled;
        this.mTitle = title;
        this.mSubtitle = subtitle;
        this.mIsLaunchIntoPip = isLaunchIntoPip;
    }

    public PictureInPictureParams(PictureInPictureParams other) {
        this(other.mAspectRatio, other.mExpandedAspectRatio, other.mUserActions, other.mCloseAction, other.hasSourceBoundsHint() ? new Rect(other.getSourceRectHint()) : null, other.mAutoEnterEnabled, other.mSeamlessResizeEnabled, other.mTitle, other.mSubtitle, other.mIsLaunchIntoPip);
    }

    public void copyOnlySet(PictureInPictureParams otherArgs) {
        if (otherArgs.hasSetAspectRatio()) {
            this.mAspectRatio = otherArgs.mAspectRatio;
        }
        this.mExpandedAspectRatio = otherArgs.mExpandedAspectRatio;
        if (otherArgs.hasSetActions()) {
            this.mUserActions = otherArgs.mUserActions;
        }
        if (otherArgs.hasSetCloseAction()) {
            this.mCloseAction = otherArgs.mCloseAction;
        }
        if (otherArgs.hasSourceBoundsHint()) {
            this.mSourceRectHint = new Rect(otherArgs.getSourceRectHint());
        }
        Boolean bool = otherArgs.mAutoEnterEnabled;
        if (bool != null) {
            this.mAutoEnterEnabled = bool;
        }
        Boolean bool2 = otherArgs.mSeamlessResizeEnabled;
        if (bool2 != null) {
            this.mSeamlessResizeEnabled = bool2;
        }
        if (otherArgs.hasSetTitle()) {
            this.mTitle = otherArgs.mTitle;
        }
        if (otherArgs.hasSetSubtitle()) {
            this.mSubtitle = otherArgs.mSubtitle;
        }
        Boolean bool3 = otherArgs.mIsLaunchIntoPip;
        if (bool3 != null) {
            this.mIsLaunchIntoPip = bool3;
        }
    }

    public float getAspectRatioFloat() {
        Rational rational = this.mAspectRatio;
        if (rational != null) {
            return rational.floatValue();
        }
        return 0.0f;
    }

    public Rational getAspectRatio() {
        return this.mAspectRatio;
    }

    public boolean hasSetAspectRatio() {
        return this.mAspectRatio != null;
    }

    public float getExpandedAspectRatioFloat() {
        Rational rational = this.mExpandedAspectRatio;
        if (rational != null) {
            return rational.floatValue();
        }
        return 0.0f;
    }

    public Rational getExpandedAspectRatio() {
        return this.mExpandedAspectRatio;
    }

    public boolean hasSetExpandedAspectRatio() {
        return this.mExpandedAspectRatio != null;
    }

    public List<RemoteAction> getActions() {
        List<RemoteAction> list = this.mUserActions;
        if (list == null) {
            return new ArrayList();
        }
        return list;
    }

    public boolean hasSetActions() {
        return this.mUserActions != null;
    }

    public RemoteAction getCloseAction() {
        return this.mCloseAction;
    }

    public boolean hasSetCloseAction() {
        return this.mCloseAction != null;
    }

    public void truncateActions(int size) {
        if (hasSetActions()) {
            List<RemoteAction> list = this.mUserActions;
            this.mUserActions = list.subList(0, Math.min(list.size(), size));
        }
    }

    public Rect getSourceRectHint() {
        return this.mSourceRectHint;
    }

    public boolean hasSourceBoundsHint() {
        Rect rect = this.mSourceRectHint;
        return (rect == null || rect.isEmpty()) ? false : true;
    }

    public boolean isAutoEnterEnabled() {
        Boolean bool = this.mAutoEnterEnabled;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public boolean isSeamlessResizeEnabled() {
        Boolean bool = this.mSeamlessResizeEnabled;
        if (bool == null) {
            return true;
        }
        return bool.booleanValue();
    }

    public boolean hasSetTitle() {
        return this.mTitle != null;
    }

    public CharSequence getTitle() {
        return this.mTitle;
    }

    public boolean hasSetSubtitle() {
        return this.mSubtitle != null;
    }

    public CharSequence getSubtitle() {
        return this.mSubtitle;
    }

    public boolean isLaunchIntoPip() {
        Boolean bool = this.mIsLaunchIntoPip;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public boolean empty() {
        return (hasSourceBoundsHint() || hasSetActions() || hasSetCloseAction() || hasSetAspectRatio() || hasSetExpandedAspectRatio() || this.mAutoEnterEnabled != null || this.mSeamlessResizeEnabled != null || hasSetTitle() || hasSetSubtitle() || this.mIsLaunchIntoPip != null) ? false : true;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof PictureInPictureParams) {
            PictureInPictureParams that = (PictureInPictureParams) o;
            return Objects.equals(this.mAutoEnterEnabled, that.mAutoEnterEnabled) && Objects.equals(this.mSeamlessResizeEnabled, that.mSeamlessResizeEnabled) && Objects.equals(this.mAspectRatio, that.mAspectRatio) && Objects.equals(this.mExpandedAspectRatio, that.mExpandedAspectRatio) && Objects.equals(this.mUserActions, that.mUserActions) && Objects.equals(this.mCloseAction, that.mCloseAction) && Objects.equals(this.mSourceRectHint, that.mSourceRectHint) && Objects.equals(this.mTitle, that.mTitle) && Objects.equals(this.mSubtitle, that.mSubtitle) && Objects.equals(this.mIsLaunchIntoPip, that.mIsLaunchIntoPip);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mAspectRatio, this.mExpandedAspectRatio, this.mUserActions, this.mCloseAction, this.mSourceRectHint, this.mAutoEnterEnabled, this.mSeamlessResizeEnabled, this.mTitle, this.mSubtitle, this.mIsLaunchIntoPip);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        writeRationalToParcel(this.mAspectRatio, out);
        writeRationalToParcel(this.mExpandedAspectRatio, out);
        if (this.mUserActions != null) {
            out.writeInt(1);
            out.writeTypedList(this.mUserActions, 0);
        } else {
            out.writeInt(0);
        }
        out.writeTypedObject(this.mCloseAction, 0);
        if (this.mSourceRectHint != null) {
            out.writeInt(1);
            this.mSourceRectHint.writeToParcel(out, 0);
        } else {
            out.writeInt(0);
        }
        if (this.mAutoEnterEnabled != null) {
            out.writeInt(1);
            out.writeBoolean(this.mAutoEnterEnabled.booleanValue());
        } else {
            out.writeInt(0);
        }
        if (this.mSeamlessResizeEnabled != null) {
            out.writeInt(1);
            out.writeBoolean(this.mSeamlessResizeEnabled.booleanValue());
        } else {
            out.writeInt(0);
        }
        if (this.mTitle != null) {
            out.writeInt(1);
            out.writeCharSequence(this.mTitle);
        } else {
            out.writeInt(0);
        }
        if (this.mSubtitle != null) {
            out.writeInt(1);
            out.writeCharSequence(this.mSubtitle);
        } else {
            out.writeInt(0);
        }
        if (this.mIsLaunchIntoPip != null) {
            out.writeInt(1);
            out.writeBoolean(this.mIsLaunchIntoPip.booleanValue());
            return;
        }
        out.writeInt(0);
    }

    private void writeRationalToParcel(Rational rational, Parcel out) {
        if (rational != null) {
            out.writeInt(1);
            out.writeInt(rational.getNumerator());
            out.writeInt(rational.getDenominator());
            return;
        }
        out.writeInt(0);
    }

    private Rational readRationalFromParcel(Parcel in) {
        if (in.readInt() != 0) {
            return new Rational(in.readInt(), in.readInt());
        }
        return null;
    }

    public String toString() {
        return "PictureInPictureParams( aspectRatio=" + getAspectRatio() + " expandedAspectRatio=" + this.mExpandedAspectRatio + " sourceRectHint=" + getSourceRectHint() + " hasSetActions=" + hasSetActions() + " hasSetCloseAction=" + hasSetCloseAction() + " isAutoPipEnabled=" + isAutoEnterEnabled() + " isSeamlessResizeEnabled=" + isSeamlessResizeEnabled() + " title=" + ((Object) getTitle()) + " subtitle=" + ((Object) getSubtitle()) + " isLaunchIntoPip=" + isLaunchIntoPip() + NavigationBarInflaterView.KEY_CODE_END;
    }
}
