package android.net;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class TcpKeepalivePacketDataParcelable implements Parcelable {
    public static final Parcelable.Creator<TcpKeepalivePacketDataParcelable> CREATOR = new Parcelable.Creator<TcpKeepalivePacketDataParcelable>() { // from class: android.net.TcpKeepalivePacketDataParcelable.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TcpKeepalivePacketDataParcelable createFromParcel(Parcel parcel) {
            TcpKeepalivePacketDataParcelable tcpKeepalivePacketDataParcelable = new TcpKeepalivePacketDataParcelable();
            tcpKeepalivePacketDataParcelable.readFromParcel(parcel);
            return tcpKeepalivePacketDataParcelable;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TcpKeepalivePacketDataParcelable[] newArray(int i) {
            return new TcpKeepalivePacketDataParcelable[i];
        }
    };
    public byte[] dstAddress;
    public byte[] srcAddress;
    public int srcPort = 0;
    public int dstPort = 0;
    public int seq = 0;
    public int ack = 0;
    public int rcvWnd = 0;
    public int rcvWndScale = 0;
    public int tos = 0;
    public int ttl = 0;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeByteArray(this.srcAddress);
        parcel.writeInt(this.srcPort);
        parcel.writeByteArray(this.dstAddress);
        parcel.writeInt(this.dstPort);
        parcel.writeInt(this.seq);
        parcel.writeInt(this.ack);
        parcel.writeInt(this.rcvWnd);
        parcel.writeInt(this.rcvWndScale);
        parcel.writeInt(this.tos);
        parcel.writeInt(this.ttl);
        int dataPosition2 = parcel.dataPosition();
        parcel.setDataPosition(dataPosition);
        parcel.writeInt(dataPosition2 - dataPosition);
        parcel.setDataPosition(dataPosition2);
    }

    public final void readFromParcel(Parcel parcel) {
        int dataPosition = parcel.dataPosition();
        int readInt = parcel.readInt();
        try {
            if (readInt < 4) {
                throw new BadParcelableException("Parcelable too small");
            }
            if (parcel.dataPosition() - dataPosition < readInt) {
                this.srcAddress = parcel.createByteArray();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.srcPort = parcel.readInt();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.dstAddress = parcel.createByteArray();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.dstPort = parcel.readInt();
                            if (parcel.dataPosition() - dataPosition < readInt) {
                                this.seq = parcel.readInt();
                                if (parcel.dataPosition() - dataPosition < readInt) {
                                    this.ack = parcel.readInt();
                                    if (parcel.dataPosition() - dataPosition < readInt) {
                                        this.rcvWnd = parcel.readInt();
                                        if (parcel.dataPosition() - dataPosition < readInt) {
                                            this.rcvWndScale = parcel.readInt();
                                            if (parcel.dataPosition() - dataPosition < readInt) {
                                                this.tos = parcel.readInt();
                                                if (parcel.dataPosition() - dataPosition < readInt) {
                                                    this.ttl = parcel.readInt();
                                                    if (dataPosition > Integer.MAX_VALUE - readInt) {
                                                        throw new BadParcelableException("Overflow in the size of parcelable");
                                                    }
                                                    parcel.setDataPosition(dataPosition + readInt);
                                                    return;
                                                } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                                                    throw new BadParcelableException("Overflow in the size of parcelable");
                                                }
                                            } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                                                throw new BadParcelableException("Overflow in the size of parcelable");
                                            }
                                        } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                                            throw new BadParcelableException("Overflow in the size of parcelable");
                                        }
                                    } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                                        throw new BadParcelableException("Overflow in the size of parcelable");
                                    }
                                } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                                    throw new BadParcelableException("Overflow in the size of parcelable");
                                }
                            } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                                throw new BadParcelableException("Overflow in the size of parcelable");
                            }
                        } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                            throw new BadParcelableException("Overflow in the size of parcelable");
                        }
                    } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
            } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                throw new BadParcelableException("Overflow in the size of parcelable");
            }
            parcel.setDataPosition(dataPosition + readInt);
        } catch (Throwable th) {
            if (dataPosition > Integer.MAX_VALUE - readInt) {
                throw new BadParcelableException("Overflow in the size of parcelable");
            }
            parcel.setDataPosition(dataPosition + readInt);
            throw th;
        }
    }

    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
        stringJoiner.add("srcAddress: " + Arrays.toString(this.srcAddress));
        stringJoiner.add("srcPort: " + this.srcPort);
        stringJoiner.add("dstAddress: " + Arrays.toString(this.dstAddress));
        stringJoiner.add("dstPort: " + this.dstPort);
        stringJoiner.add("seq: " + this.seq);
        stringJoiner.add("ack: " + this.ack);
        stringJoiner.add("rcvWnd: " + this.rcvWnd);
        stringJoiner.add("rcvWndScale: " + this.rcvWndScale);
        stringJoiner.add("tos: " + this.tos);
        stringJoiner.add("ttl: " + this.ttl);
        return "android.net.TcpKeepalivePacketDataParcelable" + stringJoiner.toString();
    }
}
