package android.net.metrics;

import java.util.Arrays;
import java.util.BitSet;
/* loaded from: classes2.dex */
public final class DnsEvent {
    private static final int SIZE_LIMIT = 20000;
    public int eventCount;
    public byte[] eventTypes;
    public int[] latenciesMs;
    public final int netId;
    public byte[] returnCodes;
    public int successCount;
    public final long transports;

    public DnsEvent(int netId, long transports, int initialCapacity) {
        this.netId = netId;
        this.transports = transports;
        this.eventTypes = new byte[initialCapacity];
        this.returnCodes = new byte[initialCapacity];
        this.latenciesMs = new int[initialCapacity];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean addResult(byte eventType, byte returnCode, int latencyMs) {
        boolean isSuccess = returnCode == 0;
        int i = this.eventCount;
        if (i >= 20000) {
            return isSuccess;
        }
        if (i == this.eventTypes.length) {
            resize((int) (i * 1.4d));
        }
        byte[] bArr = this.eventTypes;
        int i2 = this.eventCount;
        bArr[i2] = eventType;
        this.returnCodes[i2] = returnCode;
        this.latenciesMs[i2] = latencyMs;
        this.eventCount = i2 + 1;
        if (isSuccess) {
            this.successCount++;
        }
        return isSuccess;
    }

    public void resize(int newLength) {
        this.eventTypes = Arrays.copyOf(this.eventTypes, newLength);
        this.returnCodes = Arrays.copyOf(this.returnCodes, newLength);
        this.latenciesMs = Arrays.copyOf(this.latenciesMs, newLength);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("DnsEvent(").append("netId=").append(this.netId).append(", transports=").append(BitSet.valueOf(new long[]{this.transports})).append(", ");
        builder.append(String.format("%d events, ", Integer.valueOf(this.eventCount)));
        builder.append(String.format("%d success)", Integer.valueOf(this.successCount)));
        return builder.toString();
    }
}
