package android.net.util;

import android.net.KeepalivePacketData;
import android.net.NattKeepalivePacketData;
import android.net.NattKeepalivePacketDataParcelable;
import android.net.TcpKeepalivePacketData;
import android.net.TcpKeepalivePacketDataParcelable;
import android.util.Log;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
/* loaded from: classes.dex */
public final class KeepalivePacketDataUtil {
    private static final int IPV4_HEADER_LENGTH = 20;
    private static final int IPV6_HEADER_LENGTH = 40;
    private static final String TAG = "KeepalivePacketDataUtil";

    public static NattKeepalivePacketDataParcelable toStableParcelable(NattKeepalivePacketData nattKeepalivePacketData) {
        NattKeepalivePacketDataParcelable nattKeepalivePacketDataParcelable = new NattKeepalivePacketDataParcelable();
        InetAddress srcAddress = nattKeepalivePacketData.getSrcAddress();
        InetAddress dstAddress = nattKeepalivePacketData.getDstAddress();
        nattKeepalivePacketDataParcelable.srcAddress = srcAddress.getAddress();
        nattKeepalivePacketDataParcelable.srcPort = nattKeepalivePacketData.getSrcPort();
        nattKeepalivePacketDataParcelable.dstAddress = dstAddress.getAddress();
        nattKeepalivePacketDataParcelable.dstPort = nattKeepalivePacketData.getDstPort();
        return nattKeepalivePacketDataParcelable;
    }

    public static TcpKeepalivePacketDataParcelable toStableParcelable(TcpKeepalivePacketData tcpKeepalivePacketData) {
        TcpKeepalivePacketDataParcelable tcpKeepalivePacketDataParcelable = new TcpKeepalivePacketDataParcelable();
        InetAddress srcAddress = tcpKeepalivePacketData.getSrcAddress();
        InetAddress dstAddress = tcpKeepalivePacketData.getDstAddress();
        tcpKeepalivePacketDataParcelable.srcAddress = srcAddress.getAddress();
        tcpKeepalivePacketDataParcelable.srcPort = tcpKeepalivePacketData.getSrcPort();
        tcpKeepalivePacketDataParcelable.dstAddress = dstAddress.getAddress();
        tcpKeepalivePacketDataParcelable.dstPort = tcpKeepalivePacketData.getDstPort();
        tcpKeepalivePacketDataParcelable.seq = tcpKeepalivePacketData.getTcpSeq();
        tcpKeepalivePacketDataParcelable.ack = tcpKeepalivePacketData.getTcpAck();
        tcpKeepalivePacketDataParcelable.rcvWnd = tcpKeepalivePacketData.getTcpWindow();
        tcpKeepalivePacketDataParcelable.rcvWndScale = tcpKeepalivePacketData.getTcpWindowScale();
        tcpKeepalivePacketDataParcelable.tos = tcpKeepalivePacketData.getIpTos();
        tcpKeepalivePacketDataParcelable.ttl = tcpKeepalivePacketData.getIpTtl();
        return tcpKeepalivePacketDataParcelable;
    }

    @Deprecated
    public static TcpKeepalivePacketDataParcelable parseTcpKeepalivePacketData(KeepalivePacketData keepalivePacketData) {
        if (keepalivePacketData == null) {
            return null;
        }
        Log.wtf(TAG, "parseTcpKeepalivePacketData should not be used after R, use TcpKeepalivePacketData instead.");
        ByteBuffer wrap = ByteBuffer.wrap(keepalivePacketData.getPacket());
        wrap.order(ByteOrder.BIG_ENDIAN);
        try {
            int i = wrap.getInt(24);
            int i2 = wrap.getInt(28);
            short s = wrap.getShort(34);
            byte b = wrap.get(1);
            byte b2 = wrap.get(8);
            TcpKeepalivePacketDataParcelable tcpKeepalivePacketDataParcelable = new TcpKeepalivePacketDataParcelable();
            tcpKeepalivePacketDataParcelable.srcAddress = keepalivePacketData.getSrcAddress().getAddress();
            tcpKeepalivePacketDataParcelable.srcPort = keepalivePacketData.getSrcPort();
            tcpKeepalivePacketDataParcelable.dstAddress = keepalivePacketData.getDstAddress().getAddress();
            tcpKeepalivePacketDataParcelable.dstPort = keepalivePacketData.getDstPort();
            tcpKeepalivePacketDataParcelable.seq = i;
            tcpKeepalivePacketDataParcelable.ack = i2;
            tcpKeepalivePacketDataParcelable.rcvWnd = s;
            tcpKeepalivePacketDataParcelable.rcvWndScale = 0;
            tcpKeepalivePacketDataParcelable.tos = b;
            tcpKeepalivePacketDataParcelable.ttl = b2;
            return tcpKeepalivePacketDataParcelable;
        } catch (IndexOutOfBoundsException unused) {
            return null;
        }
    }
}
