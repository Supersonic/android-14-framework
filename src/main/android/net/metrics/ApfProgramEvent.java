package android.net.metrics;

import android.annotation.SystemApi;
import android.app.blob.XmlTags;
import android.net.metrics.IpConnectivityLog;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import android.util.NtpTrustedTime;
import android.util.SparseArray;
import com.android.internal.util.MessageUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
@SystemApi
@Deprecated
/* loaded from: classes2.dex */
public final class ApfProgramEvent implements IpConnectivityLog.Event {
    public static final Parcelable.Creator<ApfProgramEvent> CREATOR = new Parcelable.Creator<ApfProgramEvent>() { // from class: android.net.metrics.ApfProgramEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ApfProgramEvent createFromParcel(Parcel in) {
            return new ApfProgramEvent(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ApfProgramEvent[] newArray(int size) {
            return new ApfProgramEvent[size];
        }
    };
    public static final int FLAG_HAS_IPV4_ADDRESS = 1;
    public static final int FLAG_MULTICAST_FILTER_ON = 0;
    public final long actualLifetime;
    public final int currentRas;
    public final int filteredRas;
    public final int flags;
    public final long lifetime;
    public final int programLength;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface Flags {
    }

    private ApfProgramEvent(long lifetime, long actualLifetime, int filteredRas, int currentRas, int programLength, int flags) {
        this.lifetime = lifetime;
        this.actualLifetime = actualLifetime;
        this.filteredRas = filteredRas;
        this.currentRas = currentRas;
        this.programLength = programLength;
        this.flags = flags;
    }

    private ApfProgramEvent(Parcel in) {
        this.lifetime = in.readLong();
        this.actualLifetime = in.readLong();
        this.filteredRas = in.readInt();
        this.currentRas = in.readInt();
        this.programLength = in.readInt();
        this.flags = in.readInt();
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private long mActualLifetime;
        private int mCurrentRas;
        private int mFilteredRas;
        private int mFlags;
        private long mLifetime;
        private int mProgramLength;

        public Builder setLifetime(long lifetime) {
            this.mLifetime = lifetime;
            return this;
        }

        public Builder setActualLifetime(long lifetime) {
            this.mActualLifetime = lifetime;
            return this;
        }

        public Builder setFilteredRas(int filteredRas) {
            this.mFilteredRas = filteredRas;
            return this;
        }

        public Builder setCurrentRas(int currentRas) {
            this.mCurrentRas = currentRas;
            return this;
        }

        public Builder setProgramLength(int programLength) {
            this.mProgramLength = programLength;
            return this;
        }

        public Builder setFlags(boolean hasIPv4, boolean multicastFilterOn) {
            this.mFlags = ApfProgramEvent.flagsFor(hasIPv4, multicastFilterOn);
            return this;
        }

        public ApfProgramEvent build() {
            return new ApfProgramEvent(this.mLifetime, this.mActualLifetime, this.mFilteredRas, this.mCurrentRas, this.mProgramLength, this.mFlags);
        }
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(this.lifetime);
        out.writeLong(this.actualLifetime);
        out.writeInt(this.filteredRas);
        out.writeInt(this.currentRas);
        out.writeInt(this.programLength);
        out.writeInt(this.flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        String lifetimeString = this.lifetime < Long.MAX_VALUE ? this.lifetime + XmlTags.TAG_SESSION : "forever";
        return String.format("ApfProgramEvent(%d/%d RAs %dB %ds/%s %s)", Integer.valueOf(this.filteredRas), Integer.valueOf(this.currentRas), Integer.valueOf(this.programLength), Long.valueOf(this.actualLifetime), lifetimeString, namesOf(this.flags));
    }

    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(ApfProgramEvent.class)) {
            return false;
        }
        ApfProgramEvent other = (ApfProgramEvent) obj;
        return this.lifetime == other.lifetime && this.actualLifetime == other.actualLifetime && this.filteredRas == other.filteredRas && this.currentRas == other.currentRas && this.programLength == other.programLength && this.flags == other.flags;
    }

    public static int flagsFor(boolean hasIPv4, boolean multicastFilterOn) {
        int bitfield = 0;
        if (hasIPv4) {
            bitfield = 0 | 2;
        }
        if (multicastFilterOn) {
            return bitfield | 1;
        }
        return bitfield;
    }

    private static String namesOf(int bitfield) {
        List<String> names = new ArrayList<>(Integer.bitCount(bitfield));
        BitSet set = BitSet.valueOf(new long[]{Integer.MAX_VALUE & bitfield});
        for (int bit = set.nextSetBit(0); bit >= 0; bit = set.nextSetBit(bit + 1)) {
            names.add(Decoder.constants.get(bit));
        }
        return TextUtils.join(NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER, names);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static final class Decoder {
        static final SparseArray<String> constants = MessageUtils.findMessageNames(new Class[]{ApfProgramEvent.class}, new String[]{"FLAG_"});

        Decoder() {
        }
    }
}
