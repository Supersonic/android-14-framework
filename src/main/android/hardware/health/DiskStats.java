package android.hardware.health;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public class DiskStats implements Parcelable {
    public static final Parcelable.Creator<DiskStats> CREATOR = new Parcelable.Creator<DiskStats>() { // from class: android.hardware.health.DiskStats.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DiskStats createFromParcel(Parcel parcel) {
            DiskStats diskStats = new DiskStats();
            diskStats.readFromParcel(parcel);
            return diskStats;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DiskStats[] newArray(int i) {
            return new DiskStats[i];
        }
    };
    public long reads = 0;
    public long readMerges = 0;
    public long readSectors = 0;
    public long readTicks = 0;
    public long writes = 0;
    public long writeMerges = 0;
    public long writeSectors = 0;
    public long writeTicks = 0;
    public long ioInFlight = 0;
    public long ioTicks = 0;
    public long ioInQueue = 0;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public final int getStability() {
        return 1;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeLong(this.reads);
        parcel.writeLong(this.readMerges);
        parcel.writeLong(this.readSectors);
        parcel.writeLong(this.readTicks);
        parcel.writeLong(this.writes);
        parcel.writeLong(this.writeMerges);
        parcel.writeLong(this.writeSectors);
        parcel.writeLong(this.writeTicks);
        parcel.writeLong(this.ioInFlight);
        parcel.writeLong(this.ioTicks);
        parcel.writeLong(this.ioInQueue);
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
                this.reads = parcel.readLong();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.readMerges = parcel.readLong();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.readSectors = parcel.readLong();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.readTicks = parcel.readLong();
                            if (parcel.dataPosition() - dataPosition < readInt) {
                                this.writes = parcel.readLong();
                                if (parcel.dataPosition() - dataPosition < readInt) {
                                    this.writeMerges = parcel.readLong();
                                    if (parcel.dataPosition() - dataPosition < readInt) {
                                        this.writeSectors = parcel.readLong();
                                        if (parcel.dataPosition() - dataPosition < readInt) {
                                            this.writeTicks = parcel.readLong();
                                            if (parcel.dataPosition() - dataPosition < readInt) {
                                                this.ioInFlight = parcel.readLong();
                                                if (parcel.dataPosition() - dataPosition < readInt) {
                                                    this.ioTicks = parcel.readLong();
                                                    if (parcel.dataPosition() - dataPosition < readInt) {
                                                        this.ioInQueue = parcel.readLong();
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
}
