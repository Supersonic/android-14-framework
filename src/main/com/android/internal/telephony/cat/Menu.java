package com.android.internal.telephony.cat;

import android.compat.annotation.UnsupportedAppUsage;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class Menu implements Parcelable {
    public static final Parcelable.Creator<Menu> CREATOR = new Parcelable.Creator<Menu>() { // from class: com.android.internal.telephony.cat.Menu.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Menu createFromParcel(Parcel parcel) {
            return new Menu(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Menu[] newArray(int i) {
            return new Menu[i];
        }
    };
    public int defaultItem;
    public boolean helpAvailable;
    public List<Item> items;
    public boolean itemsIconSelfExplanatory;
    public PresentationType presentationType;
    public boolean softKeyPreferred;
    public String title;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public List<TextAttribute> titleAttrs;
    public Bitmap titleIcon;
    public boolean titleIconSelfExplanatory;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Menu() {
        this.items = new ArrayList();
        this.title = null;
        this.titleAttrs = null;
        this.defaultItem = 0;
        this.softKeyPreferred = false;
        this.helpAvailable = false;
        this.titleIconSelfExplanatory = false;
        this.itemsIconSelfExplanatory = false;
        this.titleIcon = null;
        this.presentationType = PresentationType.NAVIGATION_OPTIONS;
    }

    private Menu(Parcel parcel) {
        this.title = parcel.readString();
        this.titleIcon = (Bitmap) parcel.readParcelable(Bitmap.class.getClassLoader());
        this.items = new ArrayList();
        int readInt = parcel.readInt();
        for (int i = 0; i < readInt; i++) {
            this.items.add((Item) parcel.readParcelable(Item.class.getClassLoader()));
        }
        this.defaultItem = parcel.readInt();
        this.softKeyPreferred = parcel.readInt() == 1;
        this.helpAvailable = parcel.readInt() == 1;
        this.titleIconSelfExplanatory = parcel.readInt() == 1;
        this.itemsIconSelfExplanatory = parcel.readInt() == 1;
        this.presentationType = PresentationType.values()[parcel.readInt()];
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.title);
        parcel.writeParcelable(this.titleIcon, i);
        int size = this.items.size();
        parcel.writeInt(size);
        for (int i2 = 0; i2 < size; i2++) {
            parcel.writeParcelable(this.items.get(i2), i);
        }
        parcel.writeInt(this.defaultItem);
        parcel.writeInt(this.softKeyPreferred ? 1 : 0);
        parcel.writeInt(this.helpAvailable ? 1 : 0);
        parcel.writeInt(this.titleIconSelfExplanatory ? 1 : 0);
        parcel.writeInt(this.itemsIconSelfExplanatory ? 1 : 0);
        parcel.writeInt(this.presentationType.ordinal());
    }
}
