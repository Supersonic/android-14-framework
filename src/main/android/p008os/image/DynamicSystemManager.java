package android.p008os.image;

import android.gsi.AvbPublicKey;
import android.gsi.GsiProgress;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.util.Pair;
/* renamed from: android.os.image.DynamicSystemManager */
/* loaded from: classes3.dex */
public class DynamicSystemManager {
    private static final String TAG = "DynamicSystemManager";
    private final IDynamicSystemService mService;

    public DynamicSystemManager(IDynamicSystemService service) {
        this.mService = service;
    }

    /* renamed from: android.os.image.DynamicSystemManager$Session */
    /* loaded from: classes3.dex */
    public class Session {
        private Session() {
        }

        public boolean setAshmem(ParcelFileDescriptor ashmem, long size) {
            try {
                return DynamicSystemManager.this.mService.setAshmem(ashmem, size);
            } catch (RemoteException e) {
                throw new RuntimeException(e.toString());
            }
        }

        public boolean submitFromAshmem(int size) {
            try {
                return DynamicSystemManager.this.mService.submitFromAshmem(size);
            } catch (RemoteException e) {
                throw new RuntimeException(e.toString());
            }
        }

        public boolean getAvbPublicKey(AvbPublicKey dst) {
            try {
                return DynamicSystemManager.this.mService.getAvbPublicKey(dst);
            } catch (RemoteException e) {
                throw new RuntimeException(e.toString());
            }
        }

        public boolean commit() {
            try {
                return DynamicSystemManager.this.mService.setEnable(true, true);
            } catch (RemoteException e) {
                throw new RuntimeException(e.toString());
            }
        }
    }

    public boolean startInstallation(String dsuSlot) {
        try {
            return this.mService.startInstallation(dsuSlot);
        } catch (RemoteException e) {
            throw new RuntimeException(e.toString());
        }
    }

    public Pair<Integer, Session> createPartition(String name, long size, boolean readOnly) {
        try {
            int status = this.mService.createPartition(name, size, readOnly);
            if (status == 0) {
                return new Pair<>(Integer.valueOf(status), new Session());
            }
            return new Pair<>(Integer.valueOf(status), null);
        } catch (RemoteException e) {
            throw new RuntimeException(e.toString());
        }
    }

    public boolean closePartition() {
        try {
            return this.mService.closePartition();
        } catch (RemoteException e) {
            throw new RuntimeException(e.toString());
        }
    }

    public boolean finishInstallation() {
        try {
            return this.mService.finishInstallation();
        } catch (RemoteException e) {
            throw new RuntimeException(e.toString());
        }
    }

    public GsiProgress getInstallationProgress() {
        try {
            return this.mService.getInstallationProgress();
        } catch (RemoteException e) {
            throw new RuntimeException(e.toString());
        }
    }

    public boolean abort() {
        try {
            return this.mService.abort();
        } catch (RemoteException e) {
            throw new RuntimeException(e.toString());
        }
    }

    public boolean isInUse() {
        try {
            return this.mService.isInUse();
        } catch (RemoteException e) {
            throw new RuntimeException(e.toString());
        }
    }

    public boolean isInstalled() {
        try {
            return this.mService.isInstalled();
        } catch (RemoteException e) {
            throw new RuntimeException(e.toString());
        }
    }

    public boolean isEnabled() {
        try {
            return this.mService.isEnabled();
        } catch (RemoteException e) {
            throw new RuntimeException(e.toString());
        }
    }

    public boolean remove() {
        try {
            return this.mService.remove();
        } catch (RemoteException e) {
            throw new RuntimeException(e.toString());
        }
    }

    public boolean setEnable(boolean enable, boolean oneShot) {
        try {
            return this.mService.setEnable(enable, oneShot);
        } catch (RemoteException e) {
            throw new RuntimeException(e.toString());
        }
    }

    public long suggestScratchSize() {
        try {
            return this.mService.suggestScratchSize();
        } catch (RemoteException e) {
            throw new RuntimeException(e.toString());
        }
    }
}
