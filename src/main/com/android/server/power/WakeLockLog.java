package com.android.server.power;

import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;
/* loaded from: classes2.dex */
public final class WakeLockLog {
    public final SimpleDateFormat mDumpsysDateFormat;
    public final Injector mInjector;
    public final Object mLock;
    public final TheLog mLog;
    public final TagDatabase mTagDatabase;
    public static final String[] LEVEL_TO_STRING = {"unknown", "partial", "full", "screen-dim", "screen-bright", "prox", "doze", "draw"};
    public static final String[] REDUCED_TAG_PREFIXES = {"*job*/", "*gms_scheduler*/", "IntentOp:"};
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");

    public WakeLockLog() {
        this(new Injector());
    }

    @VisibleForTesting
    public WakeLockLog(Injector injector) {
        this.mLock = new Object();
        this.mInjector = injector;
        TagDatabase tagDatabase = new TagDatabase(injector);
        this.mTagDatabase = tagDatabase;
        this.mLog = new TheLog(injector, new EntryByteTranslator(tagDatabase), tagDatabase);
        this.mDumpsysDateFormat = injector.getDateFormat();
    }

    public void onWakeLockAcquired(String str, int i, int i2) {
        onWakeLockEvent(1, str, i, i2);
    }

    public void onWakeLockReleased(String str, int i) {
        onWakeLockEvent(2, str, i, 0);
    }

    public void dump(PrintWriter printWriter) {
        dump(printWriter, false);
    }

    @VisibleForTesting
    public void dump(PrintWriter printWriter, boolean z) {
        try {
            synchronized (this.mLock) {
                printWriter.println("Wake Lock Log");
                Iterator<LogEntry> allItems = this.mLog.getAllItems(new LogEntry());
                int i = 0;
                int i2 = 0;
                while (allItems.hasNext()) {
                    LogEntry next = allItems.next();
                    if (next != null) {
                        if (next.type == 0) {
                            i2++;
                        } else {
                            i++;
                            next.dump(printWriter, this.mDumpsysDateFormat);
                        }
                    }
                }
                printWriter.println("  -");
                printWriter.println("  Events: " + i + ", Time-Resets: " + i2);
                StringBuilder sb = new StringBuilder();
                sb.append("  Buffer, Bytes used: ");
                sb.append(this.mLog.getUsedBufferSize());
                printWriter.println(sb.toString());
                if (z) {
                    printWriter.println("  " + this.mTagDatabase);
                }
            }
        } catch (Exception e) {
            printWriter.println("Exception dumping wake-lock log: " + e.toString());
        }
    }

    public final void onWakeLockEvent(int i, String str, int i2, int i3) {
        if (str == null) {
            Slog.w("PowerManagerService.WLLog", "Insufficient data to log wakelock [tag: " + str + ", ownerUid: " + i2 + ", flags: 0x" + Integer.toHexString(i3));
            return;
        }
        long currentTimeMillis = this.mInjector.currentTimeMillis();
        handleWakeLockEventInternal(i, tagNameReducer(str), i2, i == 1 ? translateFlagsFromPowerManager(i3) : 0, currentTimeMillis);
    }

    public final void handleWakeLockEventInternal(int i, String str, int i2, int i3, long j) {
        synchronized (this.mLock) {
            this.mLog.addEntry(new LogEntry(j, i, this.mTagDatabase.findOrCreateTag(str, i2, true), i3));
        }
    }

    public int translateFlagsFromPowerManager(int i) {
        int i2 = 65535 & i;
        int i3 = 1;
        if (i2 != 1) {
            i3 = 6;
            if (i2 == 6) {
                i3 = 3;
            } else if (i2 == 10) {
                i3 = 4;
            } else if (i2 == 26) {
                i3 = 2;
            } else if (i2 == 32) {
                i3 = 5;
            } else if (i2 != 64) {
                if (i2 != 128) {
                    Slog.w("PowerManagerService.WLLog", "Unsupported lock level for logging, flags: " + i);
                    i3 = 0;
                } else {
                    i3 = 7;
                }
            }
        }
        if ((268435456 & i) != 0) {
            i3 |= 16;
        }
        if ((536870912 & i) != 0) {
            i3 |= 8;
        }
        return (Integer.MIN_VALUE & i) != 0 ? i3 | 32 : i3;
    }

    public final String tagNameReducer(String str) {
        String str2 = null;
        if (str == null) {
            return null;
        }
        String[] strArr = REDUCED_TAG_PREFIXES;
        int length = strArr.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            String str3 = strArr[i];
            if (str.startsWith(str3)) {
                str2 = str3;
                break;
            }
            i++;
        }
        if (str2 != null) {
            StringBuilder sb = new StringBuilder();
            sb.append((CharSequence) str, 0, str2.length());
            int max = Math.max(str.lastIndexOf("/"), str.lastIndexOf("."));
            int length2 = sb.length();
            boolean z = true;
            while (length2 < max) {
                char charAt = str.charAt(length2);
                boolean z2 = charAt == '.' || charAt == '/';
                if (z2 || z) {
                    sb.append(charAt);
                }
                length2++;
                z = z2;
            }
            sb.append(str.substring(length2));
            return sb.toString();
        }
        return str;
    }

    /* loaded from: classes2.dex */
    public static class LogEntry {
        public int flags;
        public TagData tag;
        public long time;
        public int type;

        public LogEntry() {
        }

        public LogEntry(long j, int i, TagData tagData, int i2) {
            set(j, i, tagData, i2);
        }

        public void set(long j, int i, TagData tagData, int i2) {
            this.time = j;
            this.type = i;
            this.tag = tagData;
            this.flags = i2;
        }

        public void dump(PrintWriter printWriter, SimpleDateFormat simpleDateFormat) {
            printWriter.println("  " + toStringInternal(simpleDateFormat));
        }

        public String toString() {
            return toStringInternal(WakeLockLog.DATE_FORMAT);
        }

        public final String toStringInternal(SimpleDateFormat simpleDateFormat) {
            StringBuilder sb = new StringBuilder();
            if (this.type == 0) {
                return simpleDateFormat.format(new Date(this.time)) + " - RESET";
            }
            sb.append(simpleDateFormat.format(new Date(this.time)));
            sb.append(" - ");
            TagData tagData = this.tag;
            sb.append(tagData == null ? "---" : Integer.valueOf(tagData.ownerUid));
            sb.append(" - ");
            sb.append(this.type == 1 ? "ACQ" : "REL");
            sb.append(" ");
            TagData tagData2 = this.tag;
            sb.append(tagData2 == null ? "UNKNOWN" : tagData2.tag);
            if (this.type == 1) {
                sb.append(" (");
                flagsToString(sb);
                sb.append(")");
            }
            return sb.toString();
        }

        public final void flagsToString(StringBuilder sb) {
            sb.append(WakeLockLog.LEVEL_TO_STRING[this.flags & 7]);
            if ((this.flags & 8) == 8) {
                sb.append(",on-after-release");
            }
            if ((this.flags & 16) == 16) {
                sb.append(",acq-causes-wake");
            }
            if ((this.flags & 32) == 32) {
                sb.append(",system-wakelock");
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class EntryByteTranslator {
        public final TagDatabase mTagDatabase;

        public final int getRelativeTime(long j, long j2) {
            if (j2 < j) {
                return -1;
            }
            long j3 = j2 - j;
            if (j3 > 255) {
                return -2;
            }
            return (int) j3;
        }

        public EntryByteTranslator(TagDatabase tagDatabase) {
            this.mTagDatabase = tagDatabase;
        }

        public LogEntry fromBytes(byte[] bArr, long j, LogEntry logEntry) {
            if (bArr == null || bArr.length == 0) {
                return null;
            }
            if (logEntry == null) {
                logEntry = new LogEntry();
            }
            int i = bArr[0];
            int i2 = (i >> 6) & 3;
            if ((i2 & 2) == 2) {
                i2 = 2;
            }
            if (i2 != 0) {
                if (i2 != 1) {
                    if (i2 == 2) {
                        if (bArr.length >= 2) {
                            logEntry.set((bArr[1] & 255) + j, 2, this.mTagDatabase.getTag(i & 127), 0);
                            return logEntry;
                        }
                    } else {
                        Slog.w("PowerManagerService.WLLog", "Type not recognized [" + i2 + "]", new Exception());
                    }
                } else if (bArr.length >= 3) {
                    int i3 = i & 63;
                    logEntry.set((bArr[2] & 255) + j, 1, this.mTagDatabase.getTag(bArr[1] & Byte.MAX_VALUE), i3);
                    return logEntry;
                }
            } else if (bArr.length >= 9) {
                logEntry.set(((bArr[1] & 255) << 56) | ((bArr[2] & 255) << 48) | ((bArr[3] & 255) << 40) | ((bArr[4] & 255) << 32) | ((bArr[5] & 255) << 24) | ((bArr[6] & 255) << 16) | ((bArr[7] & 255) << 8) | (bArr[8] & 255), 0, null, 0);
                return logEntry;
            }
            return null;
        }

        public int toBytes(LogEntry logEntry, byte[] bArr, long j) {
            int i = logEntry.type;
            if (i == 0) {
                long j2 = logEntry.time;
                if (bArr != null && bArr.length >= 9) {
                    bArr[0] = 0;
                    bArr[1] = (byte) ((j2 >> 56) & 255);
                    bArr[2] = (byte) ((j2 >> 48) & 255);
                    bArr[3] = (byte) ((j2 >> 40) & 255);
                    bArr[4] = (byte) ((j2 >> 32) & 255);
                    bArr[5] = (byte) ((j2 >> 24) & 255);
                    bArr[6] = (byte) ((j2 >> 16) & 255);
                    bArr[7] = (byte) ((j2 >> 8) & 255);
                    bArr[8] = (byte) (j2 & 255);
                }
                return 9;
            } else if (i == 1) {
                if (bArr == null || bArr.length < 3) {
                    return 3;
                }
                int relativeTime = getRelativeTime(j, logEntry.time);
                if (relativeTime < 0) {
                    return relativeTime;
                }
                bArr[0] = (byte) ((logEntry.flags & 63) | 64);
                bArr[1] = (byte) this.mTagDatabase.getTagIndex(logEntry.tag);
                bArr[2] = (byte) (relativeTime & 255);
                return 3;
            } else if (i == 2) {
                if (bArr != null && bArr.length >= 2) {
                    int relativeTime2 = getRelativeTime(j, logEntry.time);
                    if (relativeTime2 < 0) {
                        return relativeTime2;
                    }
                    bArr[0] = (byte) (this.mTagDatabase.getTagIndex(logEntry.tag) | 128);
                    bArr[1] = (byte) (relativeTime2 & 255);
                }
                return 2;
            } else {
                throw new RuntimeException("Unknown type " + logEntry);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class TheLog {
        public final byte[] mBuffer;
        public final TagDatabase mTagDatabase;
        public final EntryByteTranslator mTranslator;
        public final byte[] mTempBuffer = new byte[9];
        public final byte[] mReadWriteTempBuffer = new byte[9];
        public int mStart = 0;
        public int mEnd = 0;
        public long mStartTime = 0;
        public long mLatestTime = 0;
        public long mChangeCount = 0;

        public TheLog(Injector injector, EntryByteTranslator entryByteTranslator, TagDatabase tagDatabase) {
            this.mBuffer = new byte[Math.max(injector.getLogSize(), 10)];
            this.mTranslator = entryByteTranslator;
            this.mTagDatabase = tagDatabase;
            tagDatabase.setCallback(new TagDatabase.Callback() { // from class: com.android.server.power.WakeLockLog.TheLog.1
                @Override // com.android.server.power.WakeLockLog.TagDatabase.Callback
                public void onIndexRemoved(int i) {
                    TheLog.this.removeTagIndex(i);
                }
            });
        }

        public int getUsedBufferSize() {
            return this.mBuffer.length - getAvailableSpace();
        }

        public void addEntry(LogEntry logEntry) {
            if (isBufferEmpty()) {
                long j = logEntry.time;
                this.mLatestTime = j;
                this.mStartTime = j;
            }
            int bytes = this.mTranslator.toBytes(logEntry, this.mTempBuffer, this.mLatestTime);
            if (bytes == -1) {
                return;
            }
            if (bytes == -2) {
                addEntry(new LogEntry(logEntry.time, 0, null, 0));
                bytes = this.mTranslator.toBytes(logEntry, this.mTempBuffer, this.mLatestTime);
            }
            if (bytes > 9 || bytes <= 0) {
                Slog.w("PowerManagerService.WLLog", "Log entry size is out of expected range: " + bytes);
            } else if (makeSpace(bytes)) {
                writeBytesAt(this.mEnd, this.mTempBuffer, bytes);
                this.mEnd = (this.mEnd + bytes) % this.mBuffer.length;
                long j2 = logEntry.time;
                this.mLatestTime = j2;
                TagDatabase.updateTagTime(logEntry.tag, j2);
                this.mChangeCount++;
            }
        }

        public Iterator<LogEntry> getAllItems(final LogEntry logEntry) {
            return new Iterator<LogEntry>() { // from class: com.android.server.power.WakeLockLog.TheLog.2
                public final long mChangeValue;
                public int mCurrent;
                public long mCurrentTimeReference;

                {
                    this.mCurrent = TheLog.this.mStart;
                    this.mCurrentTimeReference = TheLog.this.mStartTime;
                    this.mChangeValue = TheLog.this.mChangeCount;
                }

                @Override // java.util.Iterator
                public boolean hasNext() {
                    checkState();
                    return this.mCurrent != TheLog.this.mEnd;
                }

                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.Iterator
                public LogEntry next() {
                    checkState();
                    if (!hasNext()) {
                        throw new NoSuchElementException("No more entries left.");
                    }
                    LogEntry readEntryAt = TheLog.this.readEntryAt(this.mCurrent, this.mCurrentTimeReference, logEntry);
                    this.mCurrent = (this.mCurrent + TheLog.this.mTranslator.toBytes(readEntryAt, null, TheLog.this.mStartTime)) % TheLog.this.mBuffer.length;
                    this.mCurrentTimeReference = readEntryAt.time;
                    return readEntryAt;
                }

                public String toString() {
                    return "@" + this.mCurrent;
                }

                public final void checkState() {
                    if (this.mChangeValue == TheLog.this.mChangeCount) {
                        return;
                    }
                    throw new ConcurrentModificationException("Buffer modified, old change: " + this.mChangeValue + ", new change: " + TheLog.this.mChangeCount);
                }
            };
        }

        public final void removeTagIndex(int i) {
            if (isBufferEmpty()) {
                return;
            }
            int i2 = this.mStart;
            long j = this.mStartTime;
            LogEntry logEntry = new LogEntry();
            while (i2 != this.mEnd) {
                LogEntry readEntryAt = readEntryAt(i2, j, logEntry);
                if (readEntryAt == null) {
                    Slog.w("PowerManagerService.WLLog", "Entry is unreadable - Unexpected @ " + i2);
                    return;
                }
                TagData tagData = readEntryAt.tag;
                if (tagData != null && tagData.index == i) {
                    readEntryAt.tag = null;
                    writeEntryAt(i2, readEntryAt, j);
                }
                j = readEntryAt.time;
                i2 = (i2 + this.mTranslator.toBytes(readEntryAt, null, 0L)) % this.mBuffer.length;
            }
        }

        public final boolean makeSpace(int i) {
            int i2 = i + 1;
            if (this.mBuffer.length < i2) {
                return false;
            }
            while (getAvailableSpace() < i2) {
                removeOldestItem();
            }
            return true;
        }

        public final int getAvailableSpace() {
            int i = this.mEnd;
            int i2 = this.mStart;
            return i > i2 ? this.mBuffer.length - (i - i2) : i < i2 ? i2 - i : this.mBuffer.length;
        }

        public final void removeOldestItem() {
            if (isBufferEmpty()) {
                return;
            }
            LogEntry readEntryAt = readEntryAt(this.mStart, this.mStartTime, null);
            this.mStart = (this.mStart + this.mTranslator.toBytes(readEntryAt, null, this.mStartTime)) % this.mBuffer.length;
            this.mStartTime = readEntryAt.time;
            this.mChangeCount++;
        }

        public final boolean isBufferEmpty() {
            return this.mStart == this.mEnd;
        }

        public final LogEntry readEntryAt(int i, long j, LogEntry logEntry) {
            for (int i2 = 0; i2 < 9; i2++) {
                byte[] bArr = this.mBuffer;
                int length = (i + i2) % bArr.length;
                if (length == this.mEnd) {
                    break;
                }
                this.mReadWriteTempBuffer[i2] = bArr[length];
            }
            return this.mTranslator.fromBytes(this.mReadWriteTempBuffer, j, logEntry);
        }

        public final void writeEntryAt(int i, LogEntry logEntry, long j) {
            int bytes = this.mTranslator.toBytes(logEntry, this.mReadWriteTempBuffer, j);
            if (bytes > 0) {
                writeBytesAt(i, this.mReadWriteTempBuffer, bytes);
            }
        }

        public final void writeBytesAt(int i, byte[] bArr, int i2) {
            for (int i3 = 0; i3 < i2; i3++) {
                byte[] bArr2 = this.mBuffer;
                bArr2[(i + i3) % bArr2.length] = bArr[i3];
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class TagDatabase {
        public final TagData[] mArray;
        public Callback mCallback;
        public final int mInvalidIndex;

        /* loaded from: classes2.dex */
        public interface Callback {
            void onIndexRemoved(int i);
        }

        public TagDatabase(Injector injector) {
            int min = Math.min(injector.getTagDatabaseSize(), 128) - 1;
            this.mArray = new TagData[min];
            this.mInvalidIndex = min;
        }

        public String toString() {
            TagData[] tagDataArr;
            StringBuilder sb = new StringBuilder();
            sb.append("Tag Database: size(");
            sb.append(this.mArray.length);
            sb.append(")");
            int i = 0;
            int i2 = 0;
            for (TagData tagData : this.mArray) {
                i2 += 8;
                if (tagData != null) {
                    i++;
                    i2 += tagData.getByteSize();
                    String str = tagData.tag;
                    if (str != null) {
                        str.length();
                    }
                }
            }
            sb.append(", entries: ");
            sb.append(i);
            sb.append(", Bytes used: ");
            sb.append(i2);
            return sb.toString();
        }

        public void setCallback(Callback callback) {
            this.mCallback = callback;
        }

        public TagData getTag(int i) {
            if (i >= 0) {
                TagData[] tagDataArr = this.mArray;
                if (i >= tagDataArr.length || i == this.mInvalidIndex) {
                    return null;
                }
                return tagDataArr[i];
            }
            return null;
        }

        public int getTagIndex(TagData tagData) {
            return tagData == null ? this.mInvalidIndex : tagData.index;
        }

        public TagData findOrCreateTag(String str, int i, boolean z) {
            Callback callback;
            TagData tagData = new TagData(str, i);
            int i2 = -1;
            int i3 = -1;
            TagData tagData2 = null;
            int i4 = 0;
            while (true) {
                TagData[] tagDataArr = this.mArray;
                if (i4 >= tagDataArr.length) {
                    if (z) {
                        if ((i2 == -1) && (callback = this.mCallback) != null) {
                            callback.onIndexRemoved(i3);
                        }
                        if (i2 == -1) {
                            i2 = i3;
                        }
                        setToIndex(tagData, i2);
                        return tagData;
                    }
                    return null;
                }
                TagData tagData3 = tagDataArr[i4];
                if (tagData.equals(tagData3)) {
                    return tagData3;
                }
                if (z) {
                    if (tagData3 != null) {
                        if (tagData2 == null || tagData3.lastUsedTime < tagData2.lastUsedTime) {
                            i3 = i4;
                            tagData2 = tagData3;
                        }
                    } else if (i2 == -1) {
                        i2 = i4;
                    }
                }
                i4++;
            }
        }

        public static void updateTagTime(TagData tagData, long j) {
            if (tagData != null) {
                tagData.lastUsedTime = j;
            }
        }

        public final void setToIndex(TagData tagData, int i) {
            if (i >= 0) {
                TagData[] tagDataArr = this.mArray;
                if (i >= tagDataArr.length) {
                    return;
                }
                TagData tagData2 = tagDataArr[i];
                if (tagData2 != null) {
                    tagData2.index = this.mInvalidIndex;
                }
                tagDataArr[i] = tagData;
                tagData.index = i;
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class TagData {
        public int index;
        public long lastUsedTime;
        public int ownerUid;
        public String tag;

        public TagData(String str, int i) {
            this.tag = str;
            this.ownerUid = i;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof TagData) {
                TagData tagData = (TagData) obj;
                return TextUtils.equals(this.tag, tagData.tag) && this.ownerUid == tagData.ownerUid;
            }
            return false;
        }

        public String toString() {
            return "[" + this.ownerUid + " ; " + this.tag + "]";
        }

        public int getByteSize() {
            String str = this.tag;
            return (str == null ? 0 : str.length() * 2) + 8 + 4 + 4 + 8;
        }
    }

    /* loaded from: classes2.dex */
    public static class Injector {
        public int getLogSize() {
            return 10240;
        }

        public int getTagDatabaseSize() {
            return 128;
        }

        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }

        public SimpleDateFormat getDateFormat() {
            return WakeLockLog.DATE_FORMAT;
        }
    }
}
