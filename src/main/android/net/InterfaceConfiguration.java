package android.net;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.HashSet;
import java.util.Iterator;
/* loaded from: classes2.dex */
public class InterfaceConfiguration implements Parcelable {
    private static final String FLAG_DOWN = "down";
    private static final String FLAG_UP = "up";
    private LinkAddress mAddr;
    private HashSet<String> mFlags = new HashSet<>();
    private String mHwAddr;
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final Parcelable.Creator<InterfaceConfiguration> CREATOR = new Parcelable.Creator<InterfaceConfiguration>() { // from class: android.net.InterfaceConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InterfaceConfiguration createFromParcel(Parcel in) {
            InterfaceConfiguration info = new InterfaceConfiguration();
            info.mHwAddr = in.readString();
            if (in.readByte() == 1) {
                info.mAddr = (LinkAddress) in.readParcelable(null);
            }
            int size = in.readInt();
            for (int i = 0; i < size; i++) {
                info.mFlags.add(in.readString());
            }
            return info;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InterfaceConfiguration[] newArray(int size) {
            return new InterfaceConfiguration[size];
        }
    };

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("mHwAddr=").append(this.mHwAddr);
        builder.append(" mAddr=").append(String.valueOf(this.mAddr));
        builder.append(" mFlags=").append(getFlags());
        return builder.toString();
    }

    public Iterable<String> getFlags() {
        return this.mFlags;
    }

    public boolean hasFlag(String flag) {
        validateFlag(flag);
        return this.mFlags.contains(flag);
    }

    public void clearFlag(String flag) {
        validateFlag(flag);
        this.mFlags.remove(flag);
    }

    public void setFlag(String flag) {
        validateFlag(flag);
        this.mFlags.add(flag);
    }

    public void setInterfaceUp() {
        this.mFlags.remove("down");
        this.mFlags.add("up");
    }

    public void setInterfaceDown() {
        this.mFlags.remove("up");
        this.mFlags.add("down");
    }

    public void ignoreInterfaceUpDownStatus() {
        this.mFlags.remove("up");
        this.mFlags.remove("down");
    }

    public LinkAddress getLinkAddress() {
        return this.mAddr;
    }

    public void setLinkAddress(LinkAddress addr) {
        this.mAddr = addr;
    }

    public String getHardwareAddress() {
        return this.mHwAddr;
    }

    public void setHardwareAddress(String hwAddr) {
        this.mHwAddr = hwAddr;
    }

    public boolean isActive() {
        byte[] address;
        try {
            if (isUp()) {
                for (byte b : this.mAddr.getAddress().getAddress()) {
                    if (b != 0) {
                        return true;
                    }
                }
            }
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean isUp() {
        return hasFlag("up");
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mHwAddr);
        if (this.mAddr != null) {
            dest.writeByte((byte) 1);
            dest.writeParcelable(this.mAddr, flags);
        } else {
            dest.writeByte((byte) 0);
        }
        dest.writeInt(this.mFlags.size());
        Iterator<String> it = this.mFlags.iterator();
        while (it.hasNext()) {
            String flag = it.next();
            dest.writeString(flag);
        }
    }

    private static void validateFlag(String flag) {
        if (flag.indexOf(32) >= 0) {
            throw new IllegalArgumentException("flag contains space: " + flag);
        }
    }
}
