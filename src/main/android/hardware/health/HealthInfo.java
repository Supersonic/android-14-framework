package android.hardware.health;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public class HealthInfo implements Parcelable {
    public static final Parcelable.Creator<HealthInfo> CREATOR = new Parcelable.Creator<HealthInfo>() { // from class: android.hardware.health.HealthInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public HealthInfo createFromParcel(Parcel parcel) {
            HealthInfo healthInfo = new HealthInfo();
            healthInfo.readFromParcel(parcel);
            return healthInfo;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public HealthInfo[] newArray(int i) {
            return new HealthInfo[i];
        }
    };
    public int batteryCapacityLevel;
    public int batteryHealth;
    public BatteryHealthData batteryHealthData;
    public int batteryStatus;
    public String batteryTechnology;
    public int chargingPolicy;
    public int chargingState;
    public DiskStats[] diskStats;
    public StorageInfo[] storageInfos;
    public boolean chargerAcOnline = false;
    public boolean chargerUsbOnline = false;
    public boolean chargerWirelessOnline = false;
    public boolean chargerDockOnline = false;
    public int maxChargingCurrentMicroamps = 0;
    public int maxChargingVoltageMicrovolts = 0;
    public boolean batteryPresent = false;
    public int batteryLevel = 0;
    public int batteryVoltageMillivolts = 0;
    public int batteryTemperatureTenthsCelsius = 0;
    public int batteryCurrentMicroamps = 0;
    public int batteryCycleCount = 0;
    public int batteryFullChargeUah = 0;
    public int batteryChargeCounterUah = 0;
    public int batteryCurrentAverageMicroamps = 0;
    public long batteryChargeTimeToFullNowSeconds = 0;
    public int batteryFullChargeDesignCapacityUah = 0;

    public final int getStability() {
        return 1;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeBoolean(this.chargerAcOnline);
        parcel.writeBoolean(this.chargerUsbOnline);
        parcel.writeBoolean(this.chargerWirelessOnline);
        parcel.writeBoolean(this.chargerDockOnline);
        parcel.writeInt(this.maxChargingCurrentMicroamps);
        parcel.writeInt(this.maxChargingVoltageMicrovolts);
        parcel.writeInt(this.batteryStatus);
        parcel.writeInt(this.batteryHealth);
        parcel.writeBoolean(this.batteryPresent);
        parcel.writeInt(this.batteryLevel);
        parcel.writeInt(this.batteryVoltageMillivolts);
        parcel.writeInt(this.batteryTemperatureTenthsCelsius);
        parcel.writeInt(this.batteryCurrentMicroamps);
        parcel.writeInt(this.batteryCycleCount);
        parcel.writeInt(this.batteryFullChargeUah);
        parcel.writeInt(this.batteryChargeCounterUah);
        parcel.writeString(this.batteryTechnology);
        parcel.writeInt(this.batteryCurrentAverageMicroamps);
        parcel.writeTypedArray(this.diskStats, i);
        parcel.writeTypedArray(this.storageInfos, i);
        parcel.writeInt(this.batteryCapacityLevel);
        parcel.writeLong(this.batteryChargeTimeToFullNowSeconds);
        parcel.writeInt(this.batteryFullChargeDesignCapacityUah);
        parcel.writeInt(this.chargingState);
        parcel.writeInt(this.chargingPolicy);
        parcel.writeTypedObject(this.batteryHealthData, i);
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
                this.chargerAcOnline = parcel.readBoolean();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.chargerUsbOnline = parcel.readBoolean();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.chargerWirelessOnline = parcel.readBoolean();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.chargerDockOnline = parcel.readBoolean();
                            if (parcel.dataPosition() - dataPosition < readInt) {
                                this.maxChargingCurrentMicroamps = parcel.readInt();
                                if (parcel.dataPosition() - dataPosition < readInt) {
                                    this.maxChargingVoltageMicrovolts = parcel.readInt();
                                    if (parcel.dataPosition() - dataPosition < readInt) {
                                        this.batteryStatus = parcel.readInt();
                                        if (parcel.dataPosition() - dataPosition < readInt) {
                                            this.batteryHealth = parcel.readInt();
                                            if (parcel.dataPosition() - dataPosition < readInt) {
                                                this.batteryPresent = parcel.readBoolean();
                                                if (parcel.dataPosition() - dataPosition < readInt) {
                                                    this.batteryLevel = parcel.readInt();
                                                    if (parcel.dataPosition() - dataPosition < readInt) {
                                                        this.batteryVoltageMillivolts = parcel.readInt();
                                                        if (parcel.dataPosition() - dataPosition < readInt) {
                                                            this.batteryTemperatureTenthsCelsius = parcel.readInt();
                                                            if (parcel.dataPosition() - dataPosition < readInt) {
                                                                this.batteryCurrentMicroamps = parcel.readInt();
                                                                if (parcel.dataPosition() - dataPosition < readInt) {
                                                                    this.batteryCycleCount = parcel.readInt();
                                                                    if (parcel.dataPosition() - dataPosition < readInt) {
                                                                        this.batteryFullChargeUah = parcel.readInt();
                                                                        if (parcel.dataPosition() - dataPosition < readInt) {
                                                                            this.batteryChargeCounterUah = parcel.readInt();
                                                                            if (parcel.dataPosition() - dataPosition < readInt) {
                                                                                this.batteryTechnology = parcel.readString();
                                                                                if (parcel.dataPosition() - dataPosition < readInt) {
                                                                                    this.batteryCurrentAverageMicroamps = parcel.readInt();
                                                                                    if (parcel.dataPosition() - dataPosition < readInt) {
                                                                                        this.diskStats = (DiskStats[]) parcel.createTypedArray(DiskStats.CREATOR);
                                                                                        if (parcel.dataPosition() - dataPosition < readInt) {
                                                                                            this.storageInfos = (StorageInfo[]) parcel.createTypedArray(StorageInfo.CREATOR);
                                                                                            if (parcel.dataPosition() - dataPosition < readInt) {
                                                                                                this.batteryCapacityLevel = parcel.readInt();
                                                                                                if (parcel.dataPosition() - dataPosition < readInt) {
                                                                                                    this.batteryChargeTimeToFullNowSeconds = parcel.readLong();
                                                                                                    if (parcel.dataPosition() - dataPosition < readInt) {
                                                                                                        this.batteryFullChargeDesignCapacityUah = parcel.readInt();
                                                                                                        if (parcel.dataPosition() - dataPosition < readInt) {
                                                                                                            this.chargingState = parcel.readInt();
                                                                                                            if (parcel.dataPosition() - dataPosition < readInt) {
                                                                                                                this.chargingPolicy = parcel.readInt();
                                                                                                                if (parcel.dataPosition() - dataPosition < readInt) {
                                                                                                                    this.batteryHealthData = (BatteryHealthData) parcel.readTypedObject(BatteryHealthData.CREATOR);
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

    @Override // android.os.Parcelable
    public int describeContents() {
        return describeContents(this.batteryHealthData) | describeContents(this.diskStats) | 0 | describeContents(this.storageInfos);
    }

    public final int describeContents(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj instanceof Object[]) {
            int i = 0;
            for (Object obj2 : (Object[]) obj) {
                i |= describeContents(obj2);
            }
            return i;
        } else if (obj instanceof Parcelable) {
            return ((Parcelable) obj).describeContents();
        } else {
            return 0;
        }
    }
}
