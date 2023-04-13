package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import com.android.internal.telephony.IntRangeManager;
import com.android.internal.telephony.util.NetworkStackConstants;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public abstract class IntRangeManager {
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private ArrayList<IntRange> mRanges = new ArrayList<>();

    protected abstract void addRange(int i, int i2, boolean z);

    protected abstract boolean finishUpdate();

    protected abstract void startUpdate();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class IntRange {
        final ArrayList<ClientRange> mClients;
        int mEndId;
        int mStartId;

        IntRange(int i, int i2, String str) {
            this.mStartId = i;
            this.mEndId = i2;
            ArrayList<ClientRange> arrayList = new ArrayList<>(4);
            this.mClients = arrayList;
            arrayList.add(new ClientRange(i, i2, str));
        }

        IntRange(ClientRange clientRange) {
            this.mStartId = clientRange.mStartId;
            this.mEndId = clientRange.mEndId;
            ArrayList<ClientRange> arrayList = new ArrayList<>(4);
            this.mClients = arrayList;
            arrayList.add(clientRange);
        }

        IntRange(IntRange intRange, int i) {
            this.mStartId = intRange.mStartId;
            this.mEndId = intRange.mEndId;
            this.mClients = new ArrayList<>(intRange.mClients.size());
            for (int i2 = 0; i2 < i; i2++) {
                this.mClients.add(intRange.mClients.get(i2));
            }
        }

        void insert(ClientRange clientRange) {
            int size = this.mClients.size();
            int i = -1;
            for (int i2 = 0; i2 < size; i2++) {
                ClientRange clientRange2 = this.mClients.get(i2);
                if (clientRange.mStartId <= clientRange2.mStartId) {
                    if (clientRange.equals(clientRange2)) {
                        return;
                    }
                    if (clientRange.mStartId != clientRange2.mStartId || clientRange.mEndId <= clientRange2.mEndId) {
                        this.mClients.add(i2, clientRange);
                        return;
                    }
                    i = i2 + 1;
                    if (i >= size) {
                        break;
                    }
                }
            }
            if (i != -1 && i < size) {
                this.mClients.add(i, clientRange);
            } else {
                this.mClients.add(clientRange);
            }
        }

        public String toString() {
            return "[" + this.mStartId + "-" + this.mEndId + "]";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ClientRange {
        final String mClient;
        final int mEndId;
        final int mStartId;

        ClientRange(int i, int i2, String str) {
            this.mStartId = i;
            this.mEndId = i2;
            this.mClient = str;
        }

        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof ClientRange)) {
                return false;
            }
            ClientRange clientRange = (ClientRange) obj;
            return this.mStartId == clientRange.mStartId && this.mEndId == clientRange.mEndId && this.mClient.equals(clientRange.mClient);
        }

        public int hashCode() {
            return (((this.mStartId * 31) + this.mEndId) * 31) + this.mClient.hashCode();
        }
    }

    public synchronized void clearRanges() {
        this.mRanges.clear();
    }

    /* JADX WARN: Removed duplicated region for block: B:36:0x0064 A[Catch: all -> 0x020f, TryCatch #0 {, blocks: (B:3:0x0001, B:5:0x000b, B:7:0x0011, B:14:0x0022, B:16:0x002e, B:18:0x0032, B:22:0x003e, B:24:0x0042, B:26:0x0046, B:28:0x0054, B:30:0x0058, B:34:0x005e, B:36:0x0064, B:38:0x0070, B:40:0x0076, B:41:0x0078, B:49:0x008e, B:51:0x0094, B:58:0x00a5, B:60:0x00ab, B:67:0x00bf, B:69:0x00cb, B:71:0x00d1, B:73:0x00e2, B:79:0x00fd, B:81:0x0101, B:83:0x0108, B:85:0x011b, B:92:0x0139, B:94:0x013f, B:96:0x0150, B:105:0x0171, B:110:0x0181, B:113:0x0190, B:115:0x0198, B:117:0x01a1, B:122:0x01af, B:124:0x01bb, B:126:0x01c0, B:128:0x01c9, B:132:0x01cf, B:134:0x01dc, B:141:0x01fb, B:143:0x0201), top: B:151:0x0001 }] */
    /* JADX WARN: Removed duplicated region for block: B:44:0x0086 A[DONT_GENERATE] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public synchronized boolean enableRange(int i, int i2, String str) {
        int i3;
        int size = this.mRanges.size();
        if (size == 0) {
            if (tryAddRanges(i, i2, true)) {
                this.mRanges.add(new IntRange(i, i2, str));
                return true;
            }
            return false;
        }
        for (int i4 = 0; i4 < size; i4++) {
            IntRange intRange = this.mRanges.get(i4);
            int i5 = intRange.mStartId;
            if (i >= i5 && i2 <= intRange.mEndId) {
                intRange.insert(new ClientRange(i, i2, str));
                return true;
            }
            int i6 = i - 1;
            int i7 = intRange.mEndId;
            if (i6 == i7) {
                int i8 = i4 + 1;
                IntRange intRange2 = null;
                if (i8 < size) {
                    IntRange intRange3 = this.mRanges.get(i8);
                    int i9 = intRange3.mStartId;
                    if (i9 - 1 <= i2) {
                        i3 = i2 <= intRange3.mEndId ? i9 - 1 : i2;
                        intRange2 = intRange3;
                        if (tryAddRanges(i, i3, true)) {
                            return false;
                        }
                        intRange.mEndId = i2;
                        intRange.insert(new ClientRange(i, i2, str));
                        if (intRange2 != null) {
                            int i10 = intRange.mEndId;
                            int i11 = intRange2.mEndId;
                            if (i10 < i11) {
                                intRange.mEndId = i11;
                            }
                            intRange.mClients.addAll(intRange2.mClients);
                            this.mRanges.remove(intRange2);
                        }
                        return true;
                    }
                }
                i3 = i2;
                if (tryAddRanges(i, i3, true)) {
                }
            } else if (i < i5) {
                int i12 = i2 + 1;
                if (i12 < i5) {
                    if (tryAddRanges(i, i2, true)) {
                        this.mRanges.add(i4, new IntRange(i, i2, str));
                        return true;
                    }
                    return false;
                } else if (i2 <= i7) {
                    if (tryAddRanges(i, i5 - 1, true)) {
                        intRange.mStartId = i;
                        intRange.mClients.add(0, new ClientRange(i, i2, str));
                        return true;
                    }
                    return false;
                } else {
                    int i13 = i4 + 1;
                    for (int i14 = i13; i14 < size; i14++) {
                        IntRange intRange4 = this.mRanges.get(i14);
                        int i15 = intRange4.mStartId;
                        if (i12 < i15) {
                            if (tryAddRanges(i, i2, true)) {
                                intRange.mStartId = i;
                                intRange.mEndId = i2;
                                intRange.mClients.add(0, new ClientRange(i, i2, str));
                                for (int i16 = i13; i16 < i14; i16++) {
                                    IntRange intRange5 = this.mRanges.get(i13);
                                    intRange.mClients.addAll(intRange5.mClients);
                                    this.mRanges.remove(intRange5);
                                }
                                return true;
                            }
                            return false;
                        } else if (i2 <= intRange4.mEndId) {
                            if (tryAddRanges(i, i15 - 1, true)) {
                                intRange.mStartId = i;
                                intRange.mEndId = intRange4.mEndId;
                                intRange.mClients.add(0, new ClientRange(i, i2, str));
                                for (int i17 = i13; i17 <= i14; i17++) {
                                    IntRange intRange6 = this.mRanges.get(i13);
                                    intRange.mClients.addAll(intRange6.mClients);
                                    this.mRanges.remove(intRange6);
                                }
                                return true;
                            }
                            return false;
                        }
                    }
                    if (tryAddRanges(i, i2, true)) {
                        intRange.mStartId = i;
                        intRange.mEndId = i2;
                        intRange.mClients.add(0, new ClientRange(i, i2, str));
                        for (int i18 = i13; i18 < size; i18++) {
                            IntRange intRange7 = this.mRanges.get(i13);
                            intRange.mClients.addAll(intRange7.mClients);
                            this.mRanges.remove(intRange7);
                        }
                        return true;
                    }
                    return false;
                }
            } else if (i + 1 <= i7) {
                if (i2 <= i7) {
                    intRange.insert(new ClientRange(i, i2, str));
                    return true;
                }
                int i19 = i4 + 1;
                int i20 = i4;
                for (int i21 = i19; i21 < size && i2 + 1 >= this.mRanges.get(i21).mStartId; i21++) {
                    i20 = i21;
                }
                if (i20 == i4) {
                    if (tryAddRanges(intRange.mEndId + 1, i2, true)) {
                        intRange.mEndId = i2;
                        intRange.insert(new ClientRange(i, i2, str));
                        return true;
                    }
                    return false;
                }
                IntRange intRange8 = this.mRanges.get(i20);
                if (tryAddRanges(intRange.mEndId + 1, i2 <= intRange8.mEndId ? intRange8.mStartId - 1 : i2, true)) {
                    int i22 = intRange8.mEndId;
                    if (i2 > i22) {
                        i22 = i2;
                    }
                    intRange.mEndId = i22;
                    intRange.insert(new ClientRange(i, i2, str));
                    for (int i23 = i19; i23 <= i20; i23++) {
                        IntRange intRange9 = this.mRanges.get(i19);
                        intRange.mClients.addAll(intRange9.mClients);
                        this.mRanges.remove(intRange9);
                    }
                    return true;
                }
                return false;
            }
        }
        if (tryAddRanges(i, i2, true)) {
            this.mRanges.add(new IntRange(i, i2, str));
            return true;
        }
        return false;
    }

    public synchronized boolean disableRange(int i, int i2, String str) {
        boolean z;
        int size = this.mRanges.size();
        for (int i3 = 0; i3 < size; i3++) {
            IntRange intRange = this.mRanges.get(i3);
            if (i < intRange.mStartId) {
                return false;
            }
            if (i2 <= intRange.mEndId) {
                ArrayList<ClientRange> arrayList = intRange.mClients;
                int size2 = arrayList.size();
                if (size2 == 1) {
                    ClientRange clientRange = arrayList.get(0);
                    if (clientRange.mStartId == i && clientRange.mEndId == i2 && clientRange.mClient.equals(str)) {
                        this.mRanges.remove(i3);
                        if (updateRanges()) {
                            return true;
                        }
                        this.mRanges.add(i3, intRange);
                        return false;
                    }
                    return false;
                }
                int i4 = NetworkStackConstants.NEIGHBOR_ADVERTISEMENT_FLAG_ROUTER;
                for (int i5 = 0; i5 < size2; i5++) {
                    ClientRange clientRange2 = arrayList.get(i5);
                    if (clientRange2.mStartId == i && clientRange2.mEndId == i2 && clientRange2.mClient.equals(str)) {
                        if (i5 == size2 - 1) {
                            if (intRange.mEndId == i4) {
                                arrayList.remove(i5);
                                return true;
                            }
                            arrayList.remove(i5);
                            intRange.mEndId = i4;
                            if (updateRanges()) {
                                return true;
                            }
                            arrayList.add(i5, clientRange2);
                            intRange.mEndId = clientRange2.mEndId;
                            return false;
                        }
                        IntRange intRange2 = new IntRange(intRange, i5);
                        if (i5 == 0) {
                            int i6 = arrayList.get(1).mStartId;
                            if (i6 != intRange.mStartId) {
                                intRange2.mStartId = i6;
                                z = true;
                            } else {
                                z = false;
                            }
                            i4 = arrayList.get(1).mEndId;
                        } else {
                            z = false;
                        }
                        ArrayList arrayList2 = new ArrayList();
                        for (int i7 = i5 + 1; i7 < size2; i7++) {
                            ClientRange clientRange3 = arrayList.get(i7);
                            if (clientRange3.mStartId > i4 + 1) {
                                intRange2.mEndId = i4;
                                arrayList2.add(intRange2);
                                intRange2 = new IntRange(clientRange3);
                                z = true;
                            } else {
                                int i8 = intRange2.mEndId;
                                int i9 = clientRange3.mEndId;
                                if (i8 < i9) {
                                    intRange2.mEndId = i9;
                                }
                                intRange2.mClients.add(clientRange3);
                            }
                            int i10 = clientRange3.mEndId;
                            if (i10 > i4) {
                                i4 = i10;
                            }
                        }
                        if (i4 < i2) {
                            intRange2.mEndId = i4;
                            z = true;
                        }
                        arrayList2.add(intRange2);
                        this.mRanges.remove(i3);
                        this.mRanges.addAll(i3, arrayList2);
                        if (!z || updateRanges()) {
                            return true;
                        }
                        this.mRanges.removeAll(arrayList2);
                        this.mRanges.add(i3, intRange);
                        return false;
                    }
                    int i11 = clientRange2.mEndId;
                    if (i11 > i4) {
                        i4 = i11;
                    }
                }
                continue;
            }
        }
        return false;
    }

    public boolean updateRanges() {
        startUpdate();
        populateAllRanges();
        return finishUpdate();
    }

    protected boolean tryAddRanges(int i, int i2, boolean z) {
        startUpdate();
        populateAllRanges();
        addRange(i, i2, z);
        return finishUpdate();
    }

    public boolean isEmpty() {
        return this.mRanges.isEmpty();
    }

    private void populateAllRanges() {
        Iterator<IntRange> it = this.mRanges.iterator();
        while (it.hasNext()) {
            IntRange next = it.next();
            addRange(next.mStartId, next.mEndId, true);
        }
    }

    public String toString() {
        return (String) this.mRanges.stream().map(new Function() { // from class: com.android.internal.telephony.IntRangeManager$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((IntRangeManager.IntRange) obj).toString();
            }
        }).collect(Collectors.joining(","));
    }
}
