package com.android.server.permission.access;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.AtomicFile;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseLongArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.BackgroundThread;
import com.android.modules.utils.BinaryXmlPullParser;
import com.android.modules.utils.BinaryXmlSerializer;
import com.android.server.permission.access.collection.IntSet;
import com.android.server.permission.access.util.PermissionApex;
import com.android.server.permission.jarjar.kotlin.Unit;
import com.android.server.permission.jarjar.kotlin.jvm.internal.DefaultConstructorMarker;
import com.android.server.permission.jarjar.kotlin.jvm.internal.Intrinsics;
import com.android.server.permission.jarjar.kotlin.jvm.internal.Ref$ObjectRef;
import com.android.server.permission.jarjar.kotlin.p010io.CloseableKt;
import com.android.server.permission.jarjar.kotlin.ranges.RangesKt___RangesKt;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
/* compiled from: AccessPersistence.kt */
/* loaded from: classes2.dex */
public final class AccessPersistence {
    public static final Companion Companion = new Companion(null);
    public static final String LOG_TAG = AccessPersistence.class.getSimpleName();
    public final AccessPolicy policy;
    @GuardedBy({"scheduleLock"})
    public WriteHandler writeHandler;
    public final Object scheduleLock = new Object();
    @GuardedBy({"scheduleLock"})
    public final SparseLongArray pendingMutationTimesMillis = new SparseLongArray();
    @GuardedBy({"scheduleLock"})
    public final SparseArray<AccessState> pendingStates = new SparseArray<>();
    public final Object writeLock = new Object();

    public AccessPersistence(AccessPolicy accessPolicy) {
        this.policy = accessPolicy;
    }

    public final void initialize() {
        this.writeHandler = new WriteHandler(BackgroundThread.getHandler().getLooper());
    }

    public final void read(AccessState accessState) {
        readSystemState(accessState);
        IntSet userIds = accessState.getSystemState().getUserIds();
        int size = userIds.getSize();
        for (int i = 0; i < size; i++) {
            readUserState(accessState, userIds.elementAt(i));
        }
    }

    public final void readSystemState(AccessState accessState) {
        File systemFile = getSystemFile();
        try {
            FileInputStream openRead = new AtomicFile(systemFile).openRead();
            try {
                BinaryXmlPullParser binaryXmlPullParser = new BinaryXmlPullParser();
                binaryXmlPullParser.setInput(openRead, (String) null);
                this.policy.parseSystemState(binaryXmlPullParser, accessState);
                Unit unit = Unit.INSTANCE;
                CloseableKt.closeFinally(openRead, null);
            } catch (Throwable th) {
                try {
                    throw th;
                } catch (Throwable th2) {
                    CloseableKt.closeFinally(openRead, th);
                    throw th2;
                }
            }
        } catch (FileNotFoundException unused) {
            String str = LOG_TAG;
            Log.i(str, systemFile + " not found");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read " + systemFile, e);
        }
    }

    public final void readUserState(AccessState accessState, int i) {
        File userFile = getUserFile(i);
        try {
            FileInputStream openRead = new AtomicFile(userFile).openRead();
            try {
                BinaryXmlPullParser binaryXmlPullParser = new BinaryXmlPullParser();
                binaryXmlPullParser.setInput(openRead, (String) null);
                this.policy.parseUserState(binaryXmlPullParser, accessState, i);
                Unit unit = Unit.INSTANCE;
                CloseableKt.closeFinally(openRead, null);
            } catch (Throwable th) {
                try {
                    throw th;
                } catch (Throwable th2) {
                    CloseableKt.closeFinally(openRead, th);
                    throw th2;
                }
            }
        } catch (FileNotFoundException unused) {
            String str = LOG_TAG;
            Log.i(str, userFile + " not found");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read " + userFile, e);
        }
    }

    public final void write(AccessState accessState) {
        write(accessState.getSystemState(), accessState, -1);
        SparseArray<UserState> userStates = accessState.getUserStates();
        int size = userStates.size();
        for (int i = 0; i < size; i++) {
            write(userStates.valueAt(i), accessState, userStates.keyAt(i));
        }
    }

    public final void write(WritableState writableState, AccessState accessState, int i) {
        long j;
        int writeMode = writableState.getWriteMode();
        if (writeMode != 0) {
            if (writeMode == 1) {
                synchronized (this.scheduleLock) {
                    this.pendingStates.set(i, accessState);
                    Unit unit = Unit.INSTANCE;
                }
                writePendingState(i);
            } else if (writeMode == 2) {
                synchronized (this.scheduleLock) {
                    WriteHandler writeHandler = this.writeHandler;
                    WriteHandler writeHandler2 = null;
                    if (writeHandler == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("writeHandler");
                        writeHandler = null;
                    }
                    writeHandler.removeMessages(i);
                    this.pendingStates.set(i, accessState);
                    long uptimeMillis = SystemClock.uptimeMillis();
                    SparseLongArray sparseLongArray = this.pendingMutationTimesMillis;
                    int indexOfKey = sparseLongArray.indexOfKey(i);
                    if (indexOfKey >= 0) {
                        j = sparseLongArray.valueAt(indexOfKey);
                    } else {
                        sparseLongArray.put(i, uptimeMillis);
                        j = uptimeMillis;
                    }
                    long j2 = uptimeMillis - j;
                    WriteHandler writeHandler3 = this.writeHandler;
                    if (writeHandler3 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("writeHandler");
                        writeHandler3 = null;
                    }
                    Message obtainMessage = writeHandler3.obtainMessage(i);
                    if (j2 > 2000) {
                        obtainMessage.sendToTarget();
                        Unit unit2 = Unit.INSTANCE;
                    } else {
                        long coerceAtMost = RangesKt___RangesKt.coerceAtMost(1000L, 2000 - j2);
                        WriteHandler writeHandler4 = this.writeHandler;
                        if (writeHandler4 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("writeHandler");
                        } else {
                            writeHandler2 = writeHandler4;
                        }
                        writeHandler2.sendMessageDelayed(obtainMessage, coerceAtMost);
                    }
                }
            } else {
                throw new IllegalStateException(Integer.valueOf(writeMode).toString());
            }
        }
    }

    /* JADX WARN: Type inference failed for: r3v2, types: [T, java.lang.Object] */
    public final void writePendingState(int i) {
        synchronized (this.writeLock) {
            Ref$ObjectRef ref$ObjectRef = new Ref$ObjectRef();
            synchronized (this.scheduleLock) {
                this.pendingMutationTimesMillis.delete(i);
                ref$ObjectRef.element = this.pendingStates.removeReturnOld(i);
                WriteHandler writeHandler = this.writeHandler;
                if (writeHandler == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("writeHandler");
                    writeHandler = null;
                }
                writeHandler.removeMessages(i);
                Unit unit = Unit.INSTANCE;
            }
            T t = ref$ObjectRef.element;
            if (t == 0) {
                return;
            }
            if (i == -1) {
                writeSystemState((AccessState) t);
            } else {
                writeUserState((AccessState) t, i);
            }
        }
    }

    public final void writeSystemState(AccessState accessState) {
        File systemFile = getSystemFile();
        try {
            AtomicFile atomicFile = new AtomicFile(systemFile);
            FileOutputStream startWrite = atomicFile.startWrite();
            BinaryXmlSerializer binaryXmlSerializer = new BinaryXmlSerializer();
            binaryXmlSerializer.setOutput(startWrite, (String) null);
            binaryXmlSerializer.startDocument((String) null, Boolean.TRUE);
            this.policy.serializeSystemState(binaryXmlSerializer, accessState);
            binaryXmlSerializer.endDocument();
            atomicFile.finishWrite(startWrite);
            Unit unit = Unit.INSTANCE;
            CloseableKt.closeFinally(startWrite, null);
        } catch (Exception e) {
            String str = LOG_TAG;
            Log.e(str, "Failed to serialize " + systemFile, e);
        }
    }

    public final void writeUserState(AccessState accessState, int i) {
        File userFile = getUserFile(i);
        try {
            AtomicFile atomicFile = new AtomicFile(userFile);
            FileOutputStream startWrite = atomicFile.startWrite();
            BinaryXmlSerializer binaryXmlSerializer = new BinaryXmlSerializer();
            binaryXmlSerializer.setOutput(startWrite, (String) null);
            binaryXmlSerializer.startDocument((String) null, Boolean.TRUE);
            this.policy.serializeUserState(binaryXmlSerializer, accessState, i);
            binaryXmlSerializer.endDocument();
            atomicFile.finishWrite(startWrite);
            Unit unit = Unit.INSTANCE;
            CloseableKt.closeFinally(startWrite, null);
        } catch (Exception e) {
            String str = LOG_TAG;
            Log.e(str, "Failed to serialize " + userFile, e);
        }
    }

    public final File getSystemFile() {
        return new File(PermissionApex.INSTANCE.getSystemDataDirectory(), "access.abx");
    }

    public final File getUserFile(int i) {
        return new File(PermissionApex.INSTANCE.getUserDataDirectory(i), "access.abx");
    }

    /* compiled from: AccessPersistence.kt */
    /* loaded from: classes2.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public Companion() {
        }
    }

    /* compiled from: AccessPersistence.kt */
    /* loaded from: classes2.dex */
    public final class WriteHandler extends Handler {
        public WriteHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            AccessPersistence.this.writePendingState(message.what);
        }
    }
}
