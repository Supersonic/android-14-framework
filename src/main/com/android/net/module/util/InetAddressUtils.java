package com.android.net.module.util;

import android.p008os.Parcel;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
/* loaded from: classes5.dex */
public class InetAddressUtils {
    private static final int INET6_ADDR_LENGTH = 16;

    public static void parcelInetAddress(Parcel parcel, InetAddress address, int flags) {
        byte[] addressArray = address != null ? address.getAddress() : null;
        parcel.writeByteArray(addressArray);
        if (address instanceof Inet6Address) {
            Inet6Address v6Address = (Inet6Address) address;
            boolean hasScopeId = v6Address.getScopeId() != 0;
            parcel.writeBoolean(hasScopeId);
            if (hasScopeId) {
                parcel.writeInt(v6Address.getScopeId());
            }
        }
    }

    public static InetAddress unparcelInetAddress(Parcel in) {
        byte[] addressArray = in.createByteArray();
        if (addressArray == null) {
            return null;
        }
        try {
            if (addressArray.length == 16) {
                boolean hasScopeId = in.readBoolean();
                int scopeId = hasScopeId ? in.readInt() : 0;
                return Inet6Address.getByAddress((String) null, addressArray, scopeId);
            }
            return InetAddress.getByAddress(addressArray);
        } catch (UnknownHostException e) {
            return null;
        }
    }

    private InetAddressUtils() {
    }
}
