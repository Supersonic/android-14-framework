package android.net;

import android.net.apf.ApfCapabilities;
import android.net.networkstack.aidl.dhcp.DhcpOption;
import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class ProvisioningConfigurationParcelable implements Parcelable {
    public static final Parcelable.Creator<ProvisioningConfigurationParcelable> CREATOR = new Parcelable.Creator<ProvisioningConfigurationParcelable>() { // from class: android.net.ProvisioningConfigurationParcelable.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProvisioningConfigurationParcelable createFromParcel(Parcel parcel) {
            ProvisioningConfigurationParcelable provisioningConfigurationParcelable = new ProvisioningConfigurationParcelable();
            provisioningConfigurationParcelable.readFromParcel(parcel);
            return provisioningConfigurationParcelable;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProvisioningConfigurationParcelable[] newArray(int i) {
            return new ProvisioningConfigurationParcelable[i];
        }
    };
    public ApfCapabilities apfCapabilities;
    public String displayName;
    public InitialConfigurationParcelable initialConfig;
    public Layer2InformationParcelable layer2Info;
    public Network network;
    public List<DhcpOption> options;
    public ScanResultInfoParcelable scanResultInfo;
    public StaticIpConfiguration staticIpConfig;
    @Deprecated
    public boolean enableIPv4 = false;
    @Deprecated
    public boolean enableIPv6 = false;
    public boolean usingMultinetworkPolicyTracker = false;
    public boolean usingIpReachabilityMonitor = false;
    public int requestedPreDhcpActionMs = 0;
    public int provisioningTimeoutMs = 0;
    public int ipv6AddrGenMode = 0;
    public boolean enablePreconnection = false;
    public int ipv4ProvisioningMode = 0;
    public int ipv6ProvisioningMode = 0;
    public boolean uniqueEui64AddressesOnly = false;

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeBoolean(this.enableIPv4);
        parcel.writeBoolean(this.enableIPv6);
        parcel.writeBoolean(this.usingMultinetworkPolicyTracker);
        parcel.writeBoolean(this.usingIpReachabilityMonitor);
        parcel.writeInt(this.requestedPreDhcpActionMs);
        parcel.writeTypedObject(this.initialConfig, i);
        parcel.writeTypedObject(this.staticIpConfig, i);
        parcel.writeTypedObject(this.apfCapabilities, i);
        parcel.writeInt(this.provisioningTimeoutMs);
        parcel.writeInt(this.ipv6AddrGenMode);
        parcel.writeTypedObject(this.network, i);
        parcel.writeString(this.displayName);
        parcel.writeBoolean(this.enablePreconnection);
        parcel.writeTypedObject(this.scanResultInfo, i);
        parcel.writeTypedObject(this.layer2Info, i);
        _Parcel.writeTypedList(parcel, this.options, i);
        parcel.writeInt(this.ipv4ProvisioningMode);
        parcel.writeInt(this.ipv6ProvisioningMode);
        parcel.writeBoolean(this.uniqueEui64AddressesOnly);
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
                this.enableIPv4 = parcel.readBoolean();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.enableIPv6 = parcel.readBoolean();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.usingMultinetworkPolicyTracker = parcel.readBoolean();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.usingIpReachabilityMonitor = parcel.readBoolean();
                            if (parcel.dataPosition() - dataPosition < readInt) {
                                this.requestedPreDhcpActionMs = parcel.readInt();
                                if (parcel.dataPosition() - dataPosition < readInt) {
                                    this.initialConfig = (InitialConfigurationParcelable) parcel.readTypedObject(InitialConfigurationParcelable.CREATOR);
                                    if (parcel.dataPosition() - dataPosition < readInt) {
                                        this.staticIpConfig = (StaticIpConfiguration) parcel.readTypedObject(StaticIpConfiguration.CREATOR);
                                        if (parcel.dataPosition() - dataPosition < readInt) {
                                            this.apfCapabilities = (ApfCapabilities) parcel.readTypedObject(ApfCapabilities.CREATOR);
                                            if (parcel.dataPosition() - dataPosition < readInt) {
                                                this.provisioningTimeoutMs = parcel.readInt();
                                                if (parcel.dataPosition() - dataPosition < readInt) {
                                                    this.ipv6AddrGenMode = parcel.readInt();
                                                    if (parcel.dataPosition() - dataPosition < readInt) {
                                                        this.network = (Network) parcel.readTypedObject(Network.CREATOR);
                                                        if (parcel.dataPosition() - dataPosition < readInt) {
                                                            this.displayName = parcel.readString();
                                                            if (parcel.dataPosition() - dataPosition < readInt) {
                                                                this.enablePreconnection = parcel.readBoolean();
                                                                if (parcel.dataPosition() - dataPosition < readInt) {
                                                                    this.scanResultInfo = (ScanResultInfoParcelable) parcel.readTypedObject(ScanResultInfoParcelable.CREATOR);
                                                                    if (parcel.dataPosition() - dataPosition < readInt) {
                                                                        this.layer2Info = (Layer2InformationParcelable) parcel.readTypedObject(Layer2InformationParcelable.CREATOR);
                                                                        if (parcel.dataPosition() - dataPosition < readInt) {
                                                                            this.options = parcel.createTypedArrayList(DhcpOption.CREATOR);
                                                                            if (parcel.dataPosition() - dataPosition < readInt) {
                                                                                this.ipv4ProvisioningMode = parcel.readInt();
                                                                                if (parcel.dataPosition() - dataPosition < readInt) {
                                                                                    this.ipv6ProvisioningMode = parcel.readInt();
                                                                                    if (parcel.dataPosition() - dataPosition < readInt) {
                                                                                        this.uniqueEui64AddressesOnly = parcel.readBoolean();
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
        stringJoiner.add("enableIPv4: " + this.enableIPv4);
        stringJoiner.add("enableIPv6: " + this.enableIPv6);
        stringJoiner.add("usingMultinetworkPolicyTracker: " + this.usingMultinetworkPolicyTracker);
        stringJoiner.add("usingIpReachabilityMonitor: " + this.usingIpReachabilityMonitor);
        stringJoiner.add("requestedPreDhcpActionMs: " + this.requestedPreDhcpActionMs);
        stringJoiner.add("initialConfig: " + Objects.toString(this.initialConfig));
        stringJoiner.add("staticIpConfig: " + Objects.toString(this.staticIpConfig));
        stringJoiner.add("apfCapabilities: " + Objects.toString(this.apfCapabilities));
        stringJoiner.add("provisioningTimeoutMs: " + this.provisioningTimeoutMs);
        stringJoiner.add("ipv6AddrGenMode: " + this.ipv6AddrGenMode);
        stringJoiner.add("network: " + Objects.toString(this.network));
        stringJoiner.add("displayName: " + Objects.toString(this.displayName));
        stringJoiner.add("enablePreconnection: " + this.enablePreconnection);
        stringJoiner.add("scanResultInfo: " + Objects.toString(this.scanResultInfo));
        stringJoiner.add("layer2Info: " + Objects.toString(this.layer2Info));
        stringJoiner.add("options: " + Objects.toString(this.options));
        stringJoiner.add("ipv4ProvisioningMode: " + this.ipv4ProvisioningMode);
        stringJoiner.add("ipv6ProvisioningMode: " + this.ipv6ProvisioningMode);
        stringJoiner.add("uniqueEui64AddressesOnly: " + this.uniqueEui64AddressesOnly);
        return "android.net.ProvisioningConfigurationParcelable" + stringJoiner.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return describeContents(this.options) | describeContents(this.initialConfig) | 0 | describeContents(this.staticIpConfig) | describeContents(this.apfCapabilities) | describeContents(this.network) | describeContents(this.scanResultInfo) | describeContents(this.layer2Info);
    }

    private int describeContents(Object obj) {
        int i = 0;
        if (obj == null) {
            return 0;
        }
        if (obj instanceof Collection) {
            for (Object obj2 : (Collection) obj) {
                i |= describeContents(obj2);
            }
            return i;
        } else if (obj instanceof Parcelable) {
            return ((Parcelable) obj).describeContents();
        } else {
            return 0;
        }
    }

    /* loaded from: classes.dex */
    public static class _Parcel {
        public static <T extends Parcelable> void writeTypedList(Parcel parcel, List<T> list, int i) {
            if (list == null) {
                parcel.writeInt(-1);
                return;
            }
            int size = list.size();
            parcel.writeInt(size);
            for (int i2 = 0; i2 < size; i2++) {
                parcel.writeTypedObject(list.get(i2), i);
            }
        }
    }
}
