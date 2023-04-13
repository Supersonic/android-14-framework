package com.android.internal.telephony.util;

import android.os.Parcel;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
/* loaded from: classes.dex */
public class InetAddressUtils {
    public static void parcelInetAddress(Parcel parcel, InetAddress inetAddress, int i) {
        parcel.writeByteArray(inetAddress != null ? inetAddress.getAddress() : null);
        if (inetAddress instanceof Inet6Address) {
            Inet6Address inet6Address = (Inet6Address) inetAddress;
            boolean z = inet6Address.getScopeId() != 0;
            parcel.writeBoolean(z);
            if (z) {
                parcel.writeInt(inet6Address.getScopeId());
            }
        }
    }

    public static InetAddress unparcelInetAddress(Parcel parcel) {
        byte[] createByteArray = parcel.createByteArray();
        if (createByteArray == null) {
            return null;
        }
        try {
            if (createByteArray.length == 16) {
                return Inet6Address.getByAddress((String) null, createByteArray, parcel.readBoolean() ? parcel.readInt() : 0);
            }
            return InetAddress.getByAddress(createByteArray);
        } catch (UnknownHostException unused) {
            return null;
        }
    }

    private InetAddressUtils() {
    }
}
