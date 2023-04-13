package android.hardware.usb;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public class PortStatus implements Parcelable {
    public static final Parcelable.Creator<PortStatus> CREATOR = new Parcelable.Creator<PortStatus>() { // from class: android.hardware.usb.PortStatus.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PortStatus createFromParcel(Parcel parcel) {
            PortStatus portStatus = new PortStatus();
            portStatus.readFromParcel(parcel);
            return portStatus;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PortStatus[] newArray(int i) {
            return new PortStatus[i];
        }
    };
    public String portName;
    public byte powerBrickStatus;
    public byte[] supportedContaminantProtectionModes;
    public byte[] supportedModes;
    public byte[] usbDataStatus;
    public byte currentDataRole = 0;
    public byte currentPowerRole = 0;
    public byte currentMode = 0;
    public boolean canChangeMode = false;
    public boolean canChangeDataRole = false;
    public boolean canChangePowerRole = false;
    public boolean supportsEnableContaminantPresenceProtection = false;
    public byte contaminantProtectionStatus = 0;
    public boolean supportsEnableContaminantPresenceDetection = false;
    public byte contaminantDetectionStatus = 0;
    public boolean powerTransferLimited = false;
    public boolean supportsComplianceWarnings = false;
    public int[] complianceWarnings = new int[0];
    public int plugOrientation = 0;
    public AltModeData[] supportedAltModes = new AltModeData[0];

    public final int getStability() {
        return 1;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeString(this.portName);
        parcel.writeByte(this.currentDataRole);
        parcel.writeByte(this.currentPowerRole);
        parcel.writeByte(this.currentMode);
        parcel.writeBoolean(this.canChangeMode);
        parcel.writeBoolean(this.canChangeDataRole);
        parcel.writeBoolean(this.canChangePowerRole);
        parcel.writeByteArray(this.supportedModes);
        parcel.writeByteArray(this.supportedContaminantProtectionModes);
        parcel.writeBoolean(this.supportsEnableContaminantPresenceProtection);
        parcel.writeByte(this.contaminantProtectionStatus);
        parcel.writeBoolean(this.supportsEnableContaminantPresenceDetection);
        parcel.writeByte(this.contaminantDetectionStatus);
        parcel.writeByteArray(this.usbDataStatus);
        parcel.writeBoolean(this.powerTransferLimited);
        parcel.writeByte(this.powerBrickStatus);
        parcel.writeBoolean(this.supportsComplianceWarnings);
        parcel.writeIntArray(this.complianceWarnings);
        parcel.writeInt(this.plugOrientation);
        parcel.writeTypedArray(this.supportedAltModes, i);
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
                this.portName = parcel.readString();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.currentDataRole = parcel.readByte();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.currentPowerRole = parcel.readByte();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.currentMode = parcel.readByte();
                            if (parcel.dataPosition() - dataPosition < readInt) {
                                this.canChangeMode = parcel.readBoolean();
                                if (parcel.dataPosition() - dataPosition < readInt) {
                                    this.canChangeDataRole = parcel.readBoolean();
                                    if (parcel.dataPosition() - dataPosition < readInt) {
                                        this.canChangePowerRole = parcel.readBoolean();
                                        if (parcel.dataPosition() - dataPosition < readInt) {
                                            this.supportedModes = parcel.createByteArray();
                                            if (parcel.dataPosition() - dataPosition < readInt) {
                                                this.supportedContaminantProtectionModes = parcel.createByteArray();
                                                if (parcel.dataPosition() - dataPosition < readInt) {
                                                    this.supportsEnableContaminantPresenceProtection = parcel.readBoolean();
                                                    if (parcel.dataPosition() - dataPosition < readInt) {
                                                        this.contaminantProtectionStatus = parcel.readByte();
                                                        if (parcel.dataPosition() - dataPosition < readInt) {
                                                            this.supportsEnableContaminantPresenceDetection = parcel.readBoolean();
                                                            if (parcel.dataPosition() - dataPosition < readInt) {
                                                                this.contaminantDetectionStatus = parcel.readByte();
                                                                if (parcel.dataPosition() - dataPosition < readInt) {
                                                                    this.usbDataStatus = parcel.createByteArray();
                                                                    if (parcel.dataPosition() - dataPosition < readInt) {
                                                                        this.powerTransferLimited = parcel.readBoolean();
                                                                        if (parcel.dataPosition() - dataPosition < readInt) {
                                                                            this.powerBrickStatus = parcel.readByte();
                                                                            if (parcel.dataPosition() - dataPosition < readInt) {
                                                                                this.supportsComplianceWarnings = parcel.readBoolean();
                                                                                if (parcel.dataPosition() - dataPosition < readInt) {
                                                                                    this.complianceWarnings = parcel.createIntArray();
                                                                                    if (parcel.dataPosition() - dataPosition < readInt) {
                                                                                        this.plugOrientation = parcel.readInt();
                                                                                        if (parcel.dataPosition() - dataPosition < readInt) {
                                                                                            this.supportedAltModes = (AltModeData[]) parcel.createTypedArray(AltModeData.CREATOR);
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
        return describeContents(this.supportedAltModes) | 0;
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
