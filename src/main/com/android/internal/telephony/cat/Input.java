package com.android.internal.telephony.cat;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.telephony.PhoneConfigurationManager;
/* loaded from: classes.dex */
public class Input implements Parcelable {
    public static final Parcelable.Creator<Input> CREATOR = new Parcelable.Creator<Input>() { // from class: com.android.internal.telephony.cat.Input.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Input createFromParcel(Parcel parcel) {
            return new Input(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Input[] newArray(int i) {
            return new Input[i];
        }
    };
    public String defaultText;
    public boolean digitOnly;
    public Duration duration;
    public boolean echo;
    public boolean helpAvailable;
    public Bitmap icon;
    public boolean iconSelfExplanatory;
    public int maxLen;
    public int minLen;
    public boolean packed;
    public String text;
    public boolean ucs2;
    public boolean yesNo;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Input() {
        this.text = PhoneConfigurationManager.SSSS;
        this.defaultText = null;
        this.icon = null;
        this.minLen = 0;
        this.maxLen = 1;
        this.ucs2 = false;
        this.packed = false;
        this.digitOnly = false;
        this.echo = false;
        this.yesNo = false;
        this.helpAvailable = false;
        this.duration = null;
        this.iconSelfExplanatory = false;
    }

    private Input(Parcel parcel) {
        this.text = parcel.readString();
        this.defaultText = parcel.readString();
        this.icon = (Bitmap) parcel.readParcelable(Bitmap.class.getClassLoader());
        this.minLen = parcel.readInt();
        this.maxLen = parcel.readInt();
        this.ucs2 = parcel.readInt() == 1;
        this.packed = parcel.readInt() == 1;
        this.digitOnly = parcel.readInt() == 1;
        this.echo = parcel.readInt() == 1;
        this.yesNo = parcel.readInt() == 1;
        this.helpAvailable = parcel.readInt() == 1;
        this.duration = (Duration) parcel.readParcelable(null);
        this.iconSelfExplanatory = parcel.readInt() == 1;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.text);
        parcel.writeString(this.defaultText);
        parcel.writeParcelable(this.icon, 0);
        parcel.writeInt(this.minLen);
        parcel.writeInt(this.maxLen);
        parcel.writeInt(this.ucs2 ? 1 : 0);
        parcel.writeInt(this.packed ? 1 : 0);
        parcel.writeInt(this.digitOnly ? 1 : 0);
        parcel.writeInt(this.echo ? 1 : 0);
        parcel.writeInt(this.yesNo ? 1 : 0);
        parcel.writeInt(this.helpAvailable ? 1 : 0);
        parcel.writeParcelable(this.duration, 0);
        parcel.writeInt(this.iconSelfExplanatory ? 1 : 0);
    }
}
