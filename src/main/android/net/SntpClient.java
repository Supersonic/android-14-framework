package android.net;

import android.net.sntp.Duration64;
import android.net.sntp.Timestamp64;
import android.p008os.SystemClock;
import android.util.Log;
import android.util.Slog;
import com.android.internal.telephony.GsmAlphabet;
import com.android.internal.util.TrafficStatsConstants;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;
/* loaded from: classes2.dex */
public class SntpClient {
    private static final boolean DBG = true;
    private static final int NTP_LEAP_NOSYNC = 3;
    private static final int NTP_MODE_BROADCAST = 5;
    private static final int NTP_MODE_CLIENT = 3;
    private static final int NTP_MODE_SERVER = 4;
    private static final int NTP_PACKET_SIZE = 48;
    private static final int NTP_STRATUM_DEATH = 0;
    private static final int NTP_STRATUM_MAX = 15;
    private static final int NTP_VERSION = 3;
    private static final int ORIGINATE_TIME_OFFSET = 24;
    private static final int RECEIVE_TIME_OFFSET = 32;
    private static final int REFERENCE_TIME_OFFSET = 16;
    public static final int STANDARD_NTP_PORT = 123;
    private static final String TAG = "SntpClient";
    private static final int TRANSMIT_TIME_OFFSET = 40;
    private long mClockOffset;
    private long mNtpTime;
    private long mNtpTimeReference;
    private final Random mRandom;
    private long mRoundTripTime;
    private InetSocketAddress mServerSocketAddress;
    private final Supplier<Instant> mSystemTimeSupplier;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class InvalidServerReplyException extends Exception {
        public InvalidServerReplyException(String message) {
            super(message);
        }
    }

    public SntpClient() {
        this(new Supplier() { // from class: android.net.SntpClient$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                return Instant.now();
            }
        }, defaultRandom());
    }

    public SntpClient(Supplier<Instant> systemTimeSupplier, Random random) {
        this.mSystemTimeSupplier = (Supplier) Objects.requireNonNull(systemTimeSupplier);
        this.mRandom = (Random) Objects.requireNonNull(random);
    }

    public boolean requestTime(String host, int port, int timeout, Network network) {
        Network networkForResolv = network.getPrivateDnsBypassingCopy();
        try {
            InetAddress[] addresses = networkForResolv.getAllByName(host);
            for (InetAddress inetAddress : addresses) {
                if (requestTime(inetAddress, port, timeout, networkForResolv)) {
                    return true;
                }
            }
        } catch (UnknownHostException e) {
            Log.m104w(TAG, "Unknown host: " + host);
            EventLogTags.writeNtpFailure(host, e.toString());
        }
        Log.m112d(TAG, "request time failed");
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:29:0x0185  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean requestTime(InetAddress address, int port, int timeout, Network network) {
        int oldTag;
        DatagramSocket socket = null;
        int oldTag2 = TrafficStats.getAndSetThreadStatsTag(TrafficStatsConstants.TAG_SYSTEM_NTP);
        try {
            socket = new DatagramSocket();
            try {
                network.bindSocket(socket);
                socket.setSoTimeout(timeout);
                byte[] buffer = new byte[48];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, port);
                buffer[0] = GsmAlphabet.GSM_EXTENDED_ESCAPE;
                Instant requestTime = this.mSystemTimeSupplier.get();
                Timestamp64 requestTimestamp = Timestamp64.fromInstant(requestTime);
                Timestamp64 randomizedRequestTimestamp = requestTimestamp.randomizeSubMillis(this.mRandom);
                long requestTicks = SystemClock.elapsedRealtime();
                writeTimeStamp(buffer, 40, randomizedRequestTimestamp);
                socket.send(request);
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);
                long responseTicks = SystemClock.elapsedRealtime();
                Instant responseTime = requestTime.plusMillis(responseTicks - requestTicks);
                Timestamp64 responseTimestamp = Timestamp64.fromInstant(responseTime);
                byte leap = (byte) ((buffer[0] >> 6) & 3);
                byte mode = (byte) (buffer[0] & 7);
                byte leap2 = buffer[1];
                int stratum = leap2 & 255;
                Timestamp64 referenceTimestamp = readTimeStamp(buffer, 16);
                Timestamp64 originateTimestamp = readTimeStamp(buffer, 24);
                Timestamp64 receiveTimestamp = readTimeStamp(buffer, 32);
                Timestamp64 transmitTimestamp = readTimeStamp(buffer, 40);
                checkValidServerReply(leap, mode, stratum, transmitTimestamp, referenceTimestamp, randomizedRequestTimestamp, originateTimestamp);
                long totalTransactionDurationMillis = responseTicks - requestTicks;
                long serverDurationMillis = Duration64.between(receiveTimestamp, transmitTimestamp).toDuration().toMillis();
                oldTag = oldTag2;
                long roundTripTimeMillis = totalTransactionDurationMillis - serverDurationMillis;
                try {
                    Duration clockOffsetDuration = calculateClockOffset(requestTimestamp, receiveTimestamp, transmitTimestamp, responseTimestamp);
                    long clockOffsetMillis = clockOffsetDuration.toMillis();
                    EventLogTags.writeNtpSuccess(address.toString(), roundTripTimeMillis, clockOffsetMillis);
                    Log.m112d(TAG, "round trip: " + roundTripTimeMillis + "ms, clock offset: " + clockOffsetMillis + "ms");
                    this.mClockOffset = clockOffsetMillis;
                    this.mNtpTime = responseTime.plus((TemporalAmount) clockOffsetDuration).toEpochMilli();
                    this.mNtpTimeReference = responseTicks;
                    this.mRoundTripTime = roundTripTimeMillis;
                    this.mServerSocketAddress = new InetSocketAddress(address, port);
                    socket.close();
                    TrafficStats.setThreadStatsTag(oldTag);
                    return true;
                } catch (Exception e) {
                    e = e;
                    socket = socket;
                    try {
                        EventLogTags.writeNtpFailure(address.toString(), e.toString());
                        Log.m112d(TAG, "request time failed: " + e);
                        if (socket != null) {
                            socket.close();
                        }
                        TrafficStats.setThreadStatsTag(oldTag);
                        return false;
                    } catch (Throwable th) {
                        e = th;
                        if (socket != null) {
                            socket.close();
                        }
                        TrafficStats.setThreadStatsTag(oldTag);
                        throw e;
                    }
                } catch (Throwable th2) {
                    e = th2;
                    socket = socket;
                    if (socket != null) {
                    }
                    TrafficStats.setThreadStatsTag(oldTag);
                    throw e;
                }
            } catch (Exception e2) {
                e = e2;
                oldTag = oldTag2;
            } catch (Throwable th3) {
                e = th3;
                oldTag = oldTag2;
            }
        } catch (Exception e3) {
            e = e3;
            oldTag = oldTag2;
        } catch (Throwable th4) {
            e = th4;
            oldTag = oldTag2;
        }
    }

    public static Duration calculateClockOffset(Timestamp64 clientRequestTimestamp, Timestamp64 serverReceiveTimestamp, Timestamp64 serverTransmitTimestamp, Timestamp64 clientResponseTimestamp) {
        return Duration64.between(clientRequestTimestamp, serverReceiveTimestamp).plus(Duration64.between(clientResponseTimestamp, serverTransmitTimestamp)).dividedBy(2L);
    }

    @Deprecated
    public boolean requestTime(String host, int timeout) {
        Log.m104w(TAG, "Shame on you for calling the hidden API requestTime()!");
        return false;
    }

    public long getClockOffset() {
        return this.mClockOffset;
    }

    public long getNtpTime() {
        return this.mNtpTime;
    }

    public long getNtpTimeReference() {
        return this.mNtpTimeReference;
    }

    public long getRoundTripTime() {
        return this.mRoundTripTime;
    }

    public InetSocketAddress getServerSocketAddress() {
        return this.mServerSocketAddress;
    }

    private static void checkValidServerReply(byte leap, byte mode, int stratum, Timestamp64 transmitTimestamp, Timestamp64 referenceTimestamp, Timestamp64 randomizedRequestTimestamp, Timestamp64 originateTimestamp) throws InvalidServerReplyException {
        if (leap == 3) {
            throw new InvalidServerReplyException("unsynchronized server");
        }
        if (mode != 4 && mode != 5) {
            throw new InvalidServerReplyException("untrusted mode: " + ((int) mode));
        }
        if (stratum == 0 || stratum > 15) {
            throw new InvalidServerReplyException("untrusted stratum: " + stratum);
        }
        if (!randomizedRequestTimestamp.equals(originateTimestamp)) {
            throw new InvalidServerReplyException("originateTimestamp != randomizedRequestTimestamp");
        }
        if (transmitTimestamp.equals(Timestamp64.ZERO)) {
            throw new InvalidServerReplyException("zero transmitTimestamp");
        }
        if (referenceTimestamp.equals(Timestamp64.ZERO)) {
            throw new InvalidServerReplyException("zero referenceTimestamp");
        }
    }

    private long readUnsigned32(byte[] buffer, int offset) {
        int offset2 = offset + 1;
        int i0 = buffer[offset] & 255;
        int offset3 = offset2 + 1;
        int i1 = buffer[offset2] & 255;
        int offset4 = offset3 + 1;
        int i2 = buffer[offset3] & 255;
        int i3 = buffer[offset4] & 255;
        int bits = (i0 << 24) | (i1 << 16) | (i2 << 8) | i3;
        return bits & 4294967295L;
    }

    private Timestamp64 readTimeStamp(byte[] buffer, int offset) {
        long seconds = readUnsigned32(buffer, offset);
        int fractionBits = (int) readUnsigned32(buffer, offset + 4);
        return Timestamp64.fromComponents(seconds, fractionBits);
    }

    private void writeTimeStamp(byte[] buffer, int offset, Timestamp64 timestamp) {
        long seconds = timestamp.getEraSeconds();
        int offset2 = offset + 1;
        buffer[offset] = (byte) (seconds >>> 24);
        int offset3 = offset2 + 1;
        buffer[offset2] = (byte) (seconds >>> 16);
        int offset4 = offset3 + 1;
        buffer[offset3] = (byte) (seconds >>> 8);
        int offset5 = offset4 + 1;
        buffer[offset4] = (byte) seconds;
        int fractionBits = timestamp.getFractionBits();
        int offset6 = offset5 + 1;
        buffer[offset5] = (byte) (fractionBits >>> 24);
        int offset7 = offset6 + 1;
        buffer[offset6] = (byte) (fractionBits >>> 16);
        buffer[offset7] = (byte) (fractionBits >>> 8);
        buffer[offset7 + 1] = (byte) fractionBits;
    }

    private static Random defaultRandom() {
        try {
            Random random = SecureRandom.getInstanceStrong();
            return random;
        } catch (NoSuchAlgorithmException e) {
            Slog.wtf(TAG, "Unable to access SecureRandom", e);
            Random random2 = new Random(System.currentTimeMillis());
            return random2;
        }
    }
}
