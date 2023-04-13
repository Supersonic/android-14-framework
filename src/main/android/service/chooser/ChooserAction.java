package android.service.chooser;

import android.app.PendingIntent;
import android.graphics.drawable.Icon;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class ChooserAction implements Parcelable {
    public static final Parcelable.Creator<ChooserAction> CREATOR = new Parcelable.Creator<ChooserAction>() { // from class: android.service.chooser.ChooserAction.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ChooserAction createFromParcel(Parcel source) {
            return new ChooserAction(Icon.CREATOR.createFromParcel(source), TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source), PendingIntent.CREATOR.createFromParcel(source));
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ChooserAction[] newArray(int size) {
            return new ChooserAction[size];
        }
    };
    private final PendingIntent mAction;
    private final Icon mIcon;
    private final CharSequence mLabel;

    private ChooserAction(Icon icon, CharSequence label, PendingIntent action) {
        this.mIcon = icon;
        this.mLabel = label;
        this.mAction = action;
    }

    public CharSequence getLabel() {
        return this.mLabel;
    }

    public Icon getIcon() {
        return this.mIcon;
    }

    public PendingIntent getAction() {
        return this.mAction;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        this.mIcon.writeToParcel(dest, flags);
        TextUtils.writeToParcel(this.mLabel, dest, flags);
        this.mAction.writeToParcel(dest, flags);
    }

    public String toString() {
        return "ChooserAction {label=" + ((Object) this.mLabel) + ", intent=" + this.mAction + "}";
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private final PendingIntent mAction;
        private final Icon mIcon;
        private final CharSequence mLabel;

        public Builder(Icon icon, CharSequence label, PendingIntent action) {
            Objects.requireNonNull(icon, "icon can not be null");
            Objects.requireNonNull(label, "label can not be null");
            Objects.requireNonNull(action, "pending intent can not be null");
            this.mIcon = icon;
            this.mLabel = label;
            this.mAction = action;
        }

        public ChooserAction build() {
            return new ChooserAction(this.mIcon, this.mLabel, this.mAction);
        }
    }
}
