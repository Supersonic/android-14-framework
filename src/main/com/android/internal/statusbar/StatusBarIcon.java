package com.android.internal.statusbar;

import android.graphics.drawable.Icon;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.UserHandle;
import android.text.TextUtils;
/* loaded from: classes4.dex */
public class StatusBarIcon implements Parcelable {
    public static final Parcelable.Creator<StatusBarIcon> CREATOR = new Parcelable.Creator<StatusBarIcon>() { // from class: com.android.internal.statusbar.StatusBarIcon.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StatusBarIcon createFromParcel(Parcel parcel) {
            return new StatusBarIcon(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StatusBarIcon[] newArray(int size) {
            return new StatusBarIcon[size];
        }
    };
    public CharSequence contentDescription;
    public Icon icon;
    public int iconLevel;
    public int number;
    public String pkg;
    public UserHandle user;
    public boolean visible;

    public StatusBarIcon(UserHandle user, String resPackage, Icon icon, int iconLevel, int number, CharSequence contentDescription) {
        this.visible = true;
        if (icon.getType() == 2 && TextUtils.isEmpty(icon.getResPackage())) {
            icon = Icon.createWithResource(resPackage, icon.getResId());
        }
        this.pkg = resPackage;
        this.user = user;
        this.icon = icon;
        this.iconLevel = iconLevel;
        this.number = number;
        this.contentDescription = contentDescription;
    }

    public StatusBarIcon(String iconPackage, UserHandle user, int iconId, int iconLevel, int number, CharSequence contentDescription) {
        this(user, iconPackage, Icon.createWithResource(iconPackage, iconId), iconLevel, number, contentDescription);
    }

    public String toString() {
        return "StatusBarIcon(icon=" + this.icon + (this.iconLevel != 0 ? " level=" + this.iconLevel : "") + (this.visible ? " visible" : "") + " user=" + this.user.getIdentifier() + (this.number != 0 ? " num=" + this.number : "") + " )";
    }

    /* renamed from: clone */
    public StatusBarIcon m6918clone() {
        StatusBarIcon that = new StatusBarIcon(this.user, this.pkg, this.icon, this.iconLevel, this.number, this.contentDescription);
        that.visible = this.visible;
        return that;
    }

    public StatusBarIcon(Parcel in) {
        this.visible = true;
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.icon = (Icon) in.readParcelable(null, Icon.class);
        this.pkg = in.readString();
        this.user = (UserHandle) in.readParcelable(null, UserHandle.class);
        this.iconLevel = in.readInt();
        this.visible = in.readInt() != 0;
        this.number = in.readInt();
        this.contentDescription = in.readCharSequence();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(this.icon, 0);
        out.writeString(this.pkg);
        out.writeParcelable(this.user, 0);
        out.writeInt(this.iconLevel);
        out.writeInt(this.visible ? 1 : 0);
        out.writeInt(this.number);
        out.writeCharSequence(this.contentDescription);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
