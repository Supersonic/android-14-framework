package com.android.server.p008om;

import android.os.FabricatedOverlayInfo;
import android.os.FabricatedOverlayInternal;
import android.os.IBinder;
import android.os.IIdmap2;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemService;
import android.text.TextUtils;
import android.util.Slog;
import com.android.server.FgThread;
import com.android.server.p008om.IdmapDaemon;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
/* renamed from: com.android.server.om.IdmapDaemon */
/* loaded from: classes2.dex */
public class IdmapDaemon {
    public static IdmapDaemon sInstance;
    public volatile IIdmap2 mService;
    public final AtomicInteger mOpenedCount = new AtomicInteger();
    public final Object mIdmapToken = new Object();

    /* renamed from: com.android.server.om.IdmapDaemon$Connection */
    /* loaded from: classes2.dex */
    public class Connection implements AutoCloseable {
        public final IIdmap2 mIdmap2;
        public boolean mOpened;

        public Connection(IIdmap2 iIdmap2) {
            this.mOpened = true;
            synchronized (IdmapDaemon.this.mIdmapToken) {
                IdmapDaemon.this.mOpenedCount.incrementAndGet();
                this.mIdmap2 = iIdmap2;
            }
        }

        @Override // java.lang.AutoCloseable
        public void close() {
            synchronized (IdmapDaemon.this.mIdmapToken) {
                if (this.mOpened) {
                    this.mOpened = false;
                    if (IdmapDaemon.this.mOpenedCount.decrementAndGet() != 0) {
                        return;
                    }
                    FgThread.getHandler().postDelayed(new Runnable() { // from class: com.android.server.om.IdmapDaemon$Connection$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            IdmapDaemon.Connection.this.lambda$close$0();
                        }
                    }, IdmapDaemon.this.mIdmapToken, 10000L);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$close$0() {
            synchronized (IdmapDaemon.this.mIdmapToken) {
                if (IdmapDaemon.this.mService != null && IdmapDaemon.this.mOpenedCount.get() == 0) {
                    IdmapDaemon.stopIdmapService();
                    IdmapDaemon.this.mService = null;
                }
            }
        }

        public IIdmap2 getIdmap2() {
            return this.mIdmap2;
        }
    }

    public static IdmapDaemon getInstance() {
        if (sInstance == null) {
            sInstance = new IdmapDaemon();
        }
        return sInstance;
    }

    public String createIdmap(String str, String str2, String str3, int i, boolean z, int i2) throws TimeoutException, RemoteException {
        Connection connect = connect();
        try {
            IIdmap2 idmap2 = connect.getIdmap2();
            if (idmap2 == null) {
                Slog.w("OverlayManager", "idmap2d service is not ready for createIdmap(\"" + str + "\", \"" + str2 + "\", \"" + str3 + "\", " + i + ", " + z + ", " + i2 + ")");
                connect.close();
                return null;
            }
            String createIdmap = idmap2.createIdmap(str, str2, TextUtils.emptyIfNull(str3), i, z, i2);
            connect.close();
            return createIdmap;
        } catch (Throwable th) {
            if (connect != null) {
                try {
                    connect.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public boolean removeIdmap(String str, int i) throws TimeoutException, RemoteException {
        Connection connect = connect();
        try {
            IIdmap2 idmap2 = connect.getIdmap2();
            if (idmap2 == null) {
                Slog.w("OverlayManager", "idmap2d service is not ready for removeIdmap(\"" + str + "\", " + i + ")");
                connect.close();
                return false;
            }
            boolean removeIdmap = idmap2.removeIdmap(str, i);
            connect.close();
            return removeIdmap;
        } catch (Throwable th) {
            if (connect != null) {
                try {
                    connect.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public boolean verifyIdmap(String str, String str2, String str3, int i, boolean z, int i2) throws Exception {
        Connection connect = connect();
        try {
            IIdmap2 idmap2 = connect.getIdmap2();
            if (idmap2 == null) {
                Slog.w("OverlayManager", "idmap2d service is not ready for verifyIdmap(\"" + str + "\", \"" + str2 + "\", \"" + str3 + "\", " + i + ", " + z + ", " + i2 + ")");
                connect.close();
                return false;
            }
            boolean verifyIdmap = idmap2.verifyIdmap(str, str2, TextUtils.emptyIfNull(str3), i, z, i2);
            connect.close();
            return verifyIdmap;
        } catch (Throwable th) {
            if (connect != null) {
                try {
                    connect.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public boolean idmapExists(String str, int i) {
        try {
            Connection connect = connect();
            IIdmap2 idmap2 = connect.getIdmap2();
            if (idmap2 == null) {
                Slog.w("OverlayManager", "idmap2d service is not ready for idmapExists(\"" + str + "\", " + i + ")");
                connect.close();
                return false;
            }
            boolean isFile = new File(idmap2.getIdmapPath(str, i)).isFile();
            connect.close();
            return isFile;
        } catch (Exception e) {
            Slog.wtf("OverlayManager", "failed to check if idmap exists for " + str, e);
            return false;
        }
    }

    public FabricatedOverlayInfo createFabricatedOverlay(FabricatedOverlayInternal fabricatedOverlayInternal) {
        try {
            Connection connect = connect();
            IIdmap2 idmap2 = connect.getIdmap2();
            if (idmap2 == null) {
                Slog.w("OverlayManager", "idmap2d service is not ready for createFabricatedOverlay()");
                connect.close();
                return null;
            }
            FabricatedOverlayInfo createFabricatedOverlay = idmap2.createFabricatedOverlay(fabricatedOverlayInternal);
            connect.close();
            return createFabricatedOverlay;
        } catch (Exception e) {
            Slog.wtf("OverlayManager", "failed to fabricate overlay " + fabricatedOverlayInternal, e);
            return null;
        }
    }

    public boolean deleteFabricatedOverlay(String str) {
        try {
            Connection connect = connect();
            IIdmap2 idmap2 = connect.getIdmap2();
            if (idmap2 == null) {
                Slog.w("OverlayManager", "idmap2d service is not ready for deleteFabricatedOverlay(\"" + str + "\")");
                connect.close();
                return false;
            }
            boolean deleteFabricatedOverlay = idmap2.deleteFabricatedOverlay(str);
            connect.close();
            return deleteFabricatedOverlay;
        } catch (Exception e) {
            Slog.wtf("OverlayManager", "failed to delete fabricated overlay '" + str + "'", e);
            return false;
        }
    }

    public synchronized List<FabricatedOverlayInfo> getFabricatedOverlayInfos() {
        int i;
        ArrayList arrayList = new ArrayList();
        Connection connection = null;
        try {
            connection = connect();
            IIdmap2 idmap2 = connection.getIdmap2();
            if (idmap2 == null) {
                Slog.w("OverlayManager", "idmap2d service is not ready for getFabricatedOverlayInfos()");
                List<FabricatedOverlayInfo> emptyList = Collections.emptyList();
                try {
                    connection.getIdmap2();
                } catch (RemoteException unused) {
                }
                connection.close();
                return emptyList;
            }
            i = idmap2.acquireFabricatedOverlayIterator();
            while (true) {
                try {
                    try {
                        List nextFabricatedOverlayInfos = idmap2.nextFabricatedOverlayInfos(i);
                        if (nextFabricatedOverlayInfos.isEmpty()) {
                            try {
                                break;
                            } catch (RemoteException unused2) {
                            }
                        } else {
                            arrayList.addAll(nextFabricatedOverlayInfos);
                        }
                    } catch (Exception e) {
                        e = e;
                        Slog.wtf("OverlayManager", "failed to get all fabricated overlays", e);
                        try {
                            if (connection.getIdmap2() != null && i != -1) {
                                connection.getIdmap2().releaseFabricatedOverlayIterator(i);
                            }
                        } catch (RemoteException unused3) {
                        }
                        connection.close();
                        return arrayList;
                    }
                } catch (Throwable th) {
                    th = th;
                    try {
                        if (connection.getIdmap2() != null && i != -1) {
                            connection.getIdmap2().releaseFabricatedOverlayIterator(i);
                        }
                    } catch (RemoteException unused4) {
                    }
                    connection.close();
                    throw th;
                }
            }
            if (connection.getIdmap2() != null && i != -1) {
                connection.getIdmap2().releaseFabricatedOverlayIterator(i);
            }
            connection.close();
            return arrayList;
        } catch (Exception e2) {
            e = e2;
            i = -1;
        } catch (Throwable th2) {
            th = th2;
            i = -1;
            if (connection.getIdmap2() != null) {
                connection.getIdmap2().releaseFabricatedOverlayIterator(i);
            }
            connection.close();
            throw th;
        }
    }

    public String dumpIdmap(String str) {
        try {
            Connection connect = connect();
            IIdmap2 idmap2 = connect.getIdmap2();
            if (idmap2 == null) {
                Slog.w("OverlayManager", "idmap2d service is not ready for dumpIdmap()");
                connect.close();
                return "idmap2d service is not ready for dumpIdmap()";
            }
            String nullIfEmpty = TextUtils.nullIfEmpty(idmap2.dumpIdmap(str));
            connect.close();
            return nullIfEmpty;
        } catch (Exception e) {
            Slog.wtf("OverlayManager", "failed to dump idmap", e);
            return null;
        }
    }

    public final IBinder getIdmapService() throws TimeoutException, RemoteException {
        try {
            SystemService.start("idmap2d");
        } catch (RuntimeException e) {
            Slog.wtf("OverlayManager", "Failed to enable idmap2 daemon", e);
            if (e.getMessage().contains("failed to set system property")) {
                return null;
            }
        }
        long uptimeMillis = SystemClock.uptimeMillis() + 5000;
        do {
            IBinder service = ServiceManager.getService("idmap");
            if (service != null) {
                service.linkToDeath(new IBinder.DeathRecipient() { // from class: com.android.server.om.IdmapDaemon$$ExternalSyntheticLambda0
                    @Override // android.os.IBinder.DeathRecipient
                    public final void binderDied() {
                        IdmapDaemon.lambda$getIdmapService$0();
                    }
                }, 0);
                return service;
            }
            SystemClock.sleep(5L);
        } while (SystemClock.uptimeMillis() <= uptimeMillis);
        throw new TimeoutException(String.format("Failed to connect to '%s' in %d milliseconds", "idmap", 5000));
    }

    public static /* synthetic */ void lambda$getIdmapService$0() {
        Slog.w("OverlayManager", String.format("service '%s' died", "idmap"));
    }

    public static void stopIdmapService() {
        try {
            SystemService.stop("idmap2d");
        } catch (RuntimeException e) {
            Slog.w("OverlayManager", "Failed to disable idmap2 daemon", e);
        }
    }

    public final Connection connect() throws TimeoutException, RemoteException {
        synchronized (this.mIdmapToken) {
            FgThread.getHandler().removeCallbacksAndMessages(this.mIdmapToken);
            if (this.mService != null) {
                return new Connection(this.mService);
            }
            IBinder idmapService = getIdmapService();
            if (idmapService == null) {
                return new Connection(null);
            }
            this.mService = IIdmap2.Stub.asInterface(idmapService);
            return new Connection(this.mService);
        }
    }
}
