package com.android.internal.p028os;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.p008os.SystemClock;
import android.p008os.Trace;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructPollfd;
import android.util.Log;
import android.util.Slog;
import dalvik.system.ZygoteHooks;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.ToIntFunction;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: com.android.internal.os.ZygoteServer */
/* loaded from: classes4.dex */
public class ZygoteServer {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int INVALID_TIMESTAMP = -1;
    public static final String TAG = "ZygoteServer";
    private boolean mCloseSocketFd;
    private boolean mIsFirstPropertyCheck;
    private boolean mIsForkChild;
    private long mLastPropCheckTimestamp;
    private boolean mUsapPoolEnabled;
    private final FileDescriptor mUsapPoolEventFD;
    private UsapPoolRefillAction mUsapPoolRefillAction;
    private int mUsapPoolRefillDelayMs;
    private int mUsapPoolRefillThreshold;
    private long mUsapPoolRefillTriggerTimestamp;
    private int mUsapPoolSizeMax;
    private int mUsapPoolSizeMin;
    private final LocalServerSocket mUsapPoolSocket;
    private final boolean mUsapPoolSupported;
    private LocalServerSocket mZygoteSocket;

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: com.android.internal.os.ZygoteServer$UsapPoolRefillAction */
    /* loaded from: classes4.dex */
    public enum UsapPoolRefillAction {
        DELAYED,
        IMMEDIATE,
        NONE
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ZygoteServer() {
        this.mUsapPoolEnabled = false;
        this.mUsapPoolSizeMax = 0;
        this.mUsapPoolSizeMin = 0;
        this.mUsapPoolRefillThreshold = 0;
        this.mUsapPoolRefillDelayMs = -1;
        this.mIsFirstPropertyCheck = true;
        this.mLastPropCheckTimestamp = 0L;
        this.mUsapPoolEventFD = null;
        this.mZygoteSocket = null;
        this.mUsapPoolSocket = null;
        this.mUsapPoolSupported = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ZygoteServer(boolean isPrimaryZygote) {
        this.mUsapPoolEnabled = false;
        this.mUsapPoolSizeMax = 0;
        this.mUsapPoolSizeMin = 0;
        this.mUsapPoolRefillThreshold = 0;
        this.mUsapPoolRefillDelayMs = -1;
        this.mIsFirstPropertyCheck = true;
        this.mLastPropCheckTimestamp = 0L;
        this.mUsapPoolEventFD = Zygote.getUsapPoolEventFD();
        if (isPrimaryZygote) {
            this.mZygoteSocket = Zygote.createManagedSocketFromInitSocket(Zygote.PRIMARY_SOCKET_NAME);
            this.mUsapPoolSocket = Zygote.createManagedSocketFromInitSocket(Zygote.USAP_POOL_PRIMARY_SOCKET_NAME);
        } else {
            this.mZygoteSocket = Zygote.createManagedSocketFromInitSocket(Zygote.SECONDARY_SOCKET_NAME);
            this.mUsapPoolSocket = Zygote.createManagedSocketFromInitSocket(Zygote.USAP_POOL_SECONDARY_SOCKET_NAME);
        }
        this.mUsapPoolSupported = true;
        fetchUsapPoolPolicyProps();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setForkChild() {
        this.mIsForkChild = true;
    }

    public boolean isUsapPoolEnabled() {
        return this.mUsapPoolEnabled;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void registerServerSocketAtAbstractName(String socketName) {
        if (this.mZygoteSocket == null) {
            try {
                this.mZygoteSocket = new LocalServerSocket(socketName);
                this.mCloseSocketFd = false;
            } catch (IOException ex) {
                throw new RuntimeException("Error binding to abstract socket '" + socketName + "'", ex);
            }
        }
    }

    private ZygoteConnection acceptCommandPeer(String abiList) {
        try {
            return createNewConnection(this.mZygoteSocket.accept(), abiList);
        } catch (IOException ex) {
            throw new RuntimeException("IOException during accept()", ex);
        }
    }

    protected ZygoteConnection createNewConnection(LocalSocket socket, String abiList) throws IOException {
        return new ZygoteConnection(socket, abiList);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void closeServerSocket() {
        try {
            LocalServerSocket localServerSocket = this.mZygoteSocket;
            if (localServerSocket != null) {
                FileDescriptor fd = localServerSocket.getFileDescriptor();
                this.mZygoteSocket.close();
                if (fd != null && this.mCloseSocketFd) {
                    Os.close(fd);
                }
            }
        } catch (ErrnoException ex) {
            Log.m109e(TAG, "Zygote:  error closing descriptor", ex);
        } catch (IOException ex2) {
            Log.m109e(TAG, "Zygote:  error closing sockets", ex2);
        }
        this.mZygoteSocket = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FileDescriptor getZygoteSocketFileDescriptor() {
        return this.mZygoteSocket.getFileDescriptor();
    }

    private void fetchUsapPoolPolicyProps() {
        if (this.mUsapPoolSupported) {
            this.mUsapPoolSizeMax = Integer.min(ZygoteConfig.getInt(ZygoteConfig.USAP_POOL_SIZE_MAX, 3), 100);
            this.mUsapPoolSizeMin = Integer.max(ZygoteConfig.getInt(ZygoteConfig.USAP_POOL_SIZE_MIN, 1), 1);
            this.mUsapPoolRefillThreshold = Integer.min(ZygoteConfig.getInt(ZygoteConfig.USAP_POOL_REFILL_THRESHOLD, 1), this.mUsapPoolSizeMax);
            this.mUsapPoolRefillDelayMs = ZygoteConfig.getInt(ZygoteConfig.USAP_POOL_REFILL_DELAY_MS, 3000);
            if (this.mUsapPoolSizeMin >= this.mUsapPoolSizeMax) {
                Log.m104w(TAG, "The max size of the USAP pool must be greater than the minimum size.  Restoring default values.");
                this.mUsapPoolSizeMax = 3;
                this.mUsapPoolSizeMin = 1;
                this.mUsapPoolRefillThreshold = 3 / 2;
            }
        }
    }

    private void fetchUsapPoolPolicyPropsWithMinInterval() {
        long currentTimestamp = SystemClock.elapsedRealtime();
        if (this.mIsFirstPropertyCheck || currentTimestamp - this.mLastPropCheckTimestamp >= 60000) {
            this.mIsFirstPropertyCheck = false;
            this.mLastPropCheckTimestamp = currentTimestamp;
            fetchUsapPoolPolicyProps();
        }
    }

    private void fetchUsapPoolPolicyPropsIfUnfetched() {
        if (this.mIsFirstPropertyCheck) {
            this.mIsFirstPropertyCheck = false;
            fetchUsapPoolPolicyProps();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Runnable fillUsapPool(int[] sessionSocketRawFDs, boolean isPriorityRefill) {
        int numUsapsToSpawn;
        Runnable caller;
        Trace.traceBegin(64L, "Zygote:FillUsapPool");
        fetchUsapPoolPolicyPropsIfUnfetched();
        int usapPoolCount = Zygote.getUsapPoolCount();
        if (isPriorityRefill) {
            numUsapsToSpawn = this.mUsapPoolSizeMin - usapPoolCount;
            Log.m108i(Zygote.PRIMARY_SOCKET_NAME, "Priority USAP Pool refill. New USAPs: " + numUsapsToSpawn);
        } else {
            numUsapsToSpawn = this.mUsapPoolSizeMax - usapPoolCount;
            Log.m108i(Zygote.PRIMARY_SOCKET_NAME, "Delayed USAP Pool refill. New USAPs: " + numUsapsToSpawn);
        }
        ZygoteHooks.preFork();
        do {
            numUsapsToSpawn--;
            if (numUsapsToSpawn >= 0) {
                caller = Zygote.forkUsap(this.mUsapPoolSocket, sessionSocketRawFDs, isPriorityRefill);
            } else {
                ZygoteHooks.postForkCommon();
                resetUsapRefillState();
                Trace.traceEnd(64L);
                return null;
            }
        } while (caller == null);
        return caller;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Runnable setUsapPoolStatus(boolean newStatus, LocalSocket sessionSocket) {
        if (!this.mUsapPoolSupported) {
            Log.m104w(TAG, "Attempting to enable a USAP pool for a Zygote that doesn't support it.");
            return null;
        } else if (this.mUsapPoolEnabled == newStatus) {
            return null;
        } else {
            Log.m108i(TAG, "USAP Pool status change: " + (newStatus ? "ENABLED" : "DISABLED"));
            this.mUsapPoolEnabled = newStatus;
            if (newStatus) {
                return fillUsapPool(new int[]{sessionSocket.getFileDescriptor().getInt$()}, false);
            }
            Zygote.emptyUsapPool();
            return null;
        }
    }

    private void resetUsapRefillState() {
        this.mUsapPoolRefillAction = UsapPoolRefillAction.NONE;
        this.mUsapPoolRefillTriggerTimestamp = -1L;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Runnable runSelectLoop(String abiList) {
        StructPollfd[] pollFDs;
        int[] usapPipeFDs;
        int pollIndex;
        int pollTimeoutMs;
        ZygoteConnection connection;
        Runnable command;
        byte[] buffer;
        int readBytes;
        ArrayList<FileDescriptor> socketFDs = new ArrayList<>();
        ArrayList<ZygoteConnection> peers = new ArrayList<>();
        socketFDs.add(this.mZygoteSocket.getFileDescriptor());
        peers.add(null);
        long j = -1;
        this.mUsapPoolRefillTriggerTimestamp = -1L;
        while (true) {
            fetchUsapPoolPolicyPropsWithMinInterval();
            this.mUsapPoolRefillAction = UsapPoolRefillAction.NONE;
            if (this.mUsapPoolEnabled) {
                int[] usapPipeFDs2 = Zygote.getUsapPipeFDs();
                pollFDs = new StructPollfd[socketFDs.size() + 1 + usapPipeFDs2.length];
                usapPipeFDs = usapPipeFDs2;
            } else {
                pollFDs = new StructPollfd[socketFDs.size()];
                usapPipeFDs = null;
            }
            int pollIndex2 = 0;
            Iterator<FileDescriptor> it = socketFDs.iterator();
            while (it.hasNext()) {
                FileDescriptor socketFD = it.next();
                pollFDs[pollIndex2] = new StructPollfd();
                pollFDs[pollIndex2].fd = socketFD;
                pollFDs[pollIndex2].events = (short) OsConstants.POLLIN;
                pollIndex2++;
            }
            int usapPoolEventFDIndex = pollIndex2;
            if (this.mUsapPoolEnabled) {
                pollFDs[pollIndex2] = new StructPollfd();
                pollFDs[pollIndex2].fd = this.mUsapPoolEventFD;
                pollFDs[pollIndex2].events = (short) OsConstants.POLLIN;
                int pollIndex3 = pollIndex2 + 1;
                for (int usapPipeFD : usapPipeFDs) {
                    FileDescriptor managedFd = new FileDescriptor();
                    managedFd.setInt$(usapPipeFD);
                    pollFDs[pollIndex3] = new StructPollfd();
                    pollFDs[pollIndex3].fd = managedFd;
                    pollFDs[pollIndex3].events = (short) OsConstants.POLLIN;
                    pollIndex3++;
                }
                pollIndex = pollIndex3;
            } else {
                pollIndex = pollIndex2;
            }
            if (this.mUsapPoolRefillTriggerTimestamp == j) {
                pollTimeoutMs = -1;
            } else {
                long elapsedTimeMs = System.currentTimeMillis() - this.mUsapPoolRefillTriggerTimestamp;
                int pollTimeoutMs2 = this.mUsapPoolRefillDelayMs;
                if (elapsedTimeMs >= pollTimeoutMs2) {
                    this.mUsapPoolRefillTriggerTimestamp = j;
                    this.mUsapPoolRefillAction = UsapPoolRefillAction.DELAYED;
                    pollTimeoutMs = 0;
                } else {
                    pollTimeoutMs = elapsedTimeMs <= 0 ? this.mUsapPoolRefillDelayMs : (int) (pollTimeoutMs2 - elapsedTimeMs);
                }
            }
            try {
                int pollReturnValue = Os.poll(pollFDs, pollTimeoutMs);
                if (pollReturnValue == 0) {
                    this.mUsapPoolRefillTriggerTimestamp = j;
                    this.mUsapPoolRefillAction = UsapPoolRefillAction.DELAYED;
                } else {
                    int i = pollIndex;
                    boolean usapPoolFDRead = false;
                    int pollIndex4 = i;
                    while (true) {
                        int pollIndex5 = pollIndex4 - 1;
                        if (pollIndex5 >= 0) {
                            if ((pollFDs[pollIndex5].revents & OsConstants.POLLIN) != 0) {
                                if (pollIndex5 == 0) {
                                    ZygoteConnection newPeer = acceptCommandPeer(abiList);
                                    peers.add(newPeer);
                                    socketFDs.add(newPeer.getFileDescriptor());
                                } else if (pollIndex5 < usapPoolEventFDIndex) {
                                    try {
                                        try {
                                            connection = peers.get(pollIndex5);
                                            boolean multipleForksOK = !isUsapPoolEnabled() && ZygoteHooks.isIndefiniteThreadSuspensionSafe();
                                            command = connection.processCommand(this, multipleForksOK);
                                        } finally {
                                            this.mIsForkChild = false;
                                        }
                                    } catch (Exception e) {
                                        if (this.mIsForkChild) {
                                            Log.m109e(TAG, "Caught post-fork exception in child process.", e);
                                            throw e;
                                        }
                                        Slog.m95e(TAG, "Exception executing zygote command: ", e);
                                        ZygoteConnection conn = peers.remove(pollIndex5);
                                        conn.closeSocket();
                                        socketFDs.remove(pollIndex5);
                                    }
                                    if (this.mIsForkChild) {
                                        if (command != null) {
                                            return command;
                                        }
                                        throw new IllegalStateException("command == null");
                                    } else if (command != null) {
                                        throw new IllegalStateException("command != null");
                                    } else {
                                        if (connection.isClosedByPeer()) {
                                            connection.closeSocket();
                                            peers.remove(pollIndex5);
                                            socketFDs.remove(pollIndex5);
                                        }
                                    }
                                } else {
                                    try {
                                        buffer = new byte[8];
                                        readBytes = Os.read(pollFDs[pollIndex5].fd, buffer, 0, buffer.length);
                                    } catch (Exception ex) {
                                        if (pollIndex5 == usapPoolEventFDIndex) {
                                            Log.m110e(TAG, "Failed to read from USAP pool event FD: " + ex.getMessage());
                                        } else {
                                            Log.m110e(TAG, "Failed to read from USAP reporting pipe: " + ex.getMessage());
                                        }
                                    }
                                    if (readBytes == 8) {
                                        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(buffer));
                                        long messagePayload = inputStream.readLong();
                                        if (pollIndex5 > usapPoolEventFDIndex) {
                                            Zygote.removeUsapTableEntry((int) messagePayload);
                                        }
                                        usapPoolFDRead = true;
                                        pollIndex4 = pollIndex5;
                                    } else {
                                        Log.m110e(TAG, "Incomplete read from USAP management FD of size " + readBytes);
                                    }
                                }
                            }
                            pollIndex4 = pollIndex5;
                        } else if (usapPoolFDRead) {
                            int usapPoolCount = Zygote.getUsapPoolCount();
                            if (usapPoolCount < this.mUsapPoolSizeMin) {
                                this.mUsapPoolRefillAction = UsapPoolRefillAction.IMMEDIATE;
                            } else if (this.mUsapPoolSizeMax - usapPoolCount >= this.mUsapPoolRefillThreshold) {
                                this.mUsapPoolRefillTriggerTimestamp = System.currentTimeMillis();
                            }
                        }
                    }
                }
                if (this.mUsapPoolRefillAction != UsapPoolRefillAction.NONE) {
                    int[] sessionSocketRawFDs = socketFDs.subList(1, socketFDs.size()).stream().mapToInt(new ToIntFunction() { // from class: com.android.internal.os.ZygoteServer$$ExternalSyntheticLambda0
                        @Override // java.util.function.ToIntFunction
                        public final int applyAsInt(Object obj) {
                            return ((FileDescriptor) obj).getInt$();
                        }
                    }).toArray();
                    boolean isPriorityRefill = this.mUsapPoolRefillAction == UsapPoolRefillAction.IMMEDIATE;
                    Runnable command2 = fillUsapPool(sessionSocketRawFDs, isPriorityRefill);
                    if (command2 != null) {
                        return command2;
                    }
                    if (isPriorityRefill) {
                        this.mUsapPoolRefillTriggerTimestamp = System.currentTimeMillis();
                    }
                }
                j = -1;
            } catch (ErrnoException ex2) {
                throw new RuntimeException("poll failed", ex2);
            }
        }
    }
}
