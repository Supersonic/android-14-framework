package com.android.internal.telephony.cat;

import android.compat.annotation.UnsupportedAppUsage;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.telephony.PhoneConfigurationManager;
/* loaded from: classes.dex */
public class TextMessage implements Parcelable {
    public static final Parcelable.Creator<TextMessage> CREATOR = new Parcelable.Creator<TextMessage>() { // from class: com.android.internal.telephony.cat.TextMessage.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TextMessage createFromParcel(Parcel parcel) {
            return new TextMessage(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TextMessage[] newArray(int i) {
            return new TextMessage[i];
        }
    };
    public Duration duration;
    public Bitmap icon;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean iconSelfExplanatory;
    public boolean isHighPriority;
    public boolean responseNeeded;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public String text;
    public String title;
    public boolean userClear;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public TextMessage() {
        this.title = PhoneConfigurationManager.SSSS;
        this.text = null;
        this.icon = null;
        this.iconSelfExplanatory = false;
        this.isHighPriority = false;
        this.responseNeeded = true;
        this.userClear = false;
        this.duration = null;
    }

    private TextMessage(Parcel parcel) {
        this.title = PhoneConfigurationManager.SSSS;
        this.text = null;
        this.icon = null;
        this.iconSelfExplanatory = false;
        this.isHighPriority = false;
        this.responseNeeded = true;
        this.userClear = false;
        this.duration = null;
        this.title = parcel.readString();
        this.text = parcel.readString();
        this.icon = (Bitmap) parcel.readParcelable(Bitmap.class.getClassLoader());
        this.iconSelfExplanatory = parcel.readInt() == 1;
        this.isHighPriority = parcel.readInt() == 1;
        this.responseNeeded = parcel.readInt() == 1;
        this.userClear = parcel.readInt() == 1;
        this.duration = (Duration) parcel.readParcelable(null);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.title);
        parcel.writeString(this.text);
        parcel.writeParcelable(this.icon, 0);
        parcel.writeInt(this.iconSelfExplanatory ? 1 : 0);
        parcel.writeInt(this.isHighPriority ? 1 : 0);
        parcel.writeInt(this.responseNeeded ? 1 : 0);
        parcel.writeInt(this.userClear ? 1 : 0);
        parcel.writeParcelable(this.duration, 0);
    }

    public String toString() {
        return "title=" + this.title + " text=" + this.text + " icon=" + this.icon + " iconSelfExplanatory=" + this.iconSelfExplanatory + " isHighPriority=" + this.isHighPriority + " responseNeeded=" + this.responseNeeded + " userClear=" + this.userClear + " duration=" + this.duration;
    }
}
