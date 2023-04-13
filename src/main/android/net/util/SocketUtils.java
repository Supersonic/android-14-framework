package android.net.util;

import android.annotation.SystemApi;
import android.system.ErrnoException;
import android.system.NetlinkSocketAddress;
import android.system.Os;
import android.system.OsConstants;
import android.system.PacketSocketAddress;
import com.android.internal.net.NetworkUtilsInternal;
import java.io.FileDescriptor;
import java.io.IOException;
import java.net.SocketAddress;
import libcore.io.IoBridge;
@SystemApi
/* loaded from: classes2.dex */
public final class SocketUtils {
    public static void bindSocketToInterface(FileDescriptor socket, String iface) throws ErrnoException {
        Os.setsockoptIfreq(socket, OsConstants.SOL_SOCKET, OsConstants.SO_BINDTODEVICE, iface);
        NetworkUtilsInternal.protectFromVpn(socket);
    }

    public static SocketAddress makeNetlinkSocketAddress(int portId, int groupsMask) {
        return new NetlinkSocketAddress(portId, groupsMask);
    }

    public static SocketAddress makePacketSocketAddress(int protocol, int ifIndex) {
        return new PacketSocketAddress(protocol, ifIndex, (byte[]) null);
    }

    @Deprecated
    public static SocketAddress makePacketSocketAddress(int ifIndex, byte[] hwAddr) {
        return new PacketSocketAddress(0, ifIndex, hwAddr);
    }

    public static SocketAddress makePacketSocketAddress(int protocol, int ifIndex, byte[] hwAddr) {
        return new PacketSocketAddress(protocol, ifIndex, hwAddr);
    }

    public static void closeSocket(FileDescriptor fd) throws IOException {
        IoBridge.closeAndSignalBlockedThreads(fd);
    }

    private SocketUtils() {
    }
}
