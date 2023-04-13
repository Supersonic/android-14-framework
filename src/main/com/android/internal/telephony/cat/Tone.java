package com.android.internal.telephony.cat;

import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public enum Tone implements Parcelable {
    DIAL(1),
    BUSY(2),
    CONGESTION(3),
    RADIO_PATH_ACK(4),
    RADIO_PATH_NOT_AVAILABLE(5),
    ERROR_SPECIAL_INFO(6),
    CALL_WAITING(7),
    RINGING(8),
    GENERAL_BEEP(16),
    POSITIVE_ACK(17),
    NEGATIVE_ACK(18),
    INCOMING_SPEECH_CALL(19),
    INCOMING_SMS(20),
    CRITICAL_ALERT(21),
    VIBRATE_ONLY(32),
    HAPPY(48),
    SAD(49),
    URGENT(50),
    QUESTION(51),
    MESSAGE_RECEIVED(52),
    MELODY_1(64),
    MELODY_2(65),
    MELODY_3(66),
    MELODY_4(67),
    MELODY_5(68),
    MELODY_6(69),
    MELODY_7(70),
    MELODY_8(71);
    
    public static final Parcelable.Creator<Tone> CREATOR = new Parcelable.Creator<Tone>() { // from class: com.android.internal.telephony.cat.Tone.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Tone createFromParcel(Parcel parcel) {
            return Tone.values()[parcel.readInt()];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Tone[] newArray(int i) {
            return new Tone[i];
        }
    };
    private int mValue;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    Tone(int i) {
        this.mValue = i;
    }

    public static Tone fromInt(int i) {
        Tone[] values;
        for (Tone tone : values()) {
            if (tone.mValue == i) {
                return tone;
            }
        }
        return null;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ordinal());
    }
}
