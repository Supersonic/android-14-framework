package android.app.blob;

import android.app.blob.IBlobCommitCallback;
import android.content.Context;
import android.p008os.Bundle;
import android.p008os.LimitExceededException;
import android.p008os.ParcelFileDescriptor;
import android.p008os.ParcelableException;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import com.android.internal.util.function.pooled.PooledLambda;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class BlobStoreManager {
    public static final int COMMIT_RESULT_ERROR = 1;
    public static final int COMMIT_RESULT_SUCCESS = 0;
    public static final int INVALID_RES_ID = -1;
    private final Context mContext;
    private final IBlobStoreManager mService;

    public BlobStoreManager(Context context, IBlobStoreManager service) {
        this.mContext = context;
        this.mService = service;
    }

    public long createSession(BlobHandle blobHandle) throws IOException {
        try {
            return this.mService.createSession(blobHandle, this.mContext.getOpPackageName());
        } catch (ParcelableException e) {
            e.maybeRethrow(IOException.class);
            e.maybeRethrow(LimitExceededException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    public Session openSession(long sessionId) throws IOException {
        try {
            return new Session(this.mService.openSession(sessionId, this.mContext.getOpPackageName()));
        } catch (ParcelableException e) {
            e.maybeRethrow(IOException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    public void abandonSession(long sessionId) throws IOException {
        try {
            this.mService.abandonSession(sessionId, this.mContext.getOpPackageName());
        } catch (ParcelableException e) {
            e.maybeRethrow(IOException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    public ParcelFileDescriptor openBlob(BlobHandle blobHandle) throws IOException {
        try {
            return this.mService.openBlob(blobHandle, this.mContext.getOpPackageName());
        } catch (ParcelableException e) {
            e.maybeRethrow(IOException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    public void acquireLease(BlobHandle blobHandle, int descriptionResId, long leaseExpiryTimeMillis) throws IOException {
        try {
            this.mService.acquireLease(blobHandle, descriptionResId, null, leaseExpiryTimeMillis, this.mContext.getOpPackageName());
        } catch (ParcelableException e) {
            e.maybeRethrow(IOException.class);
            e.maybeRethrow(LimitExceededException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    public void acquireLease(BlobHandle blobHandle, CharSequence description, long leaseExpiryTimeMillis) throws IOException {
        try {
            this.mService.acquireLease(blobHandle, -1, description, leaseExpiryTimeMillis, this.mContext.getOpPackageName());
        } catch (ParcelableException e) {
            e.maybeRethrow(IOException.class);
            e.maybeRethrow(LimitExceededException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    public void acquireLease(BlobHandle blobHandle, int descriptionResId) throws IOException {
        acquireLease(blobHandle, descriptionResId, 0L);
    }

    public void acquireLease(BlobHandle blobHandle, CharSequence description) throws IOException {
        acquireLease(blobHandle, description, 0L);
    }

    public void releaseLease(BlobHandle blobHandle) throws IOException {
        try {
            this.mService.releaseLease(blobHandle, this.mContext.getOpPackageName());
        } catch (ParcelableException e) {
            e.maybeRethrow(IOException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    public void releaseAllLeases() throws Exception {
        try {
            this.mService.releaseAllLeases(this.mContext.getOpPackageName());
        } catch (ParcelableException e) {
            e.maybeRethrow(IOException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    public long getRemainingLeaseQuotaBytes() {
        try {
            return this.mService.getRemainingLeaseQuotaBytes(this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void waitForIdle(long timeoutMillis) throws InterruptedException, TimeoutException {
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            this.mService.waitForIdle(new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: android.app.blob.BlobStoreManager$$ExternalSyntheticLambda0
                @Override // android.p008os.RemoteCallback.OnResultListener
                public final void onResult(Bundle bundle) {
                    countDownLatch.countDown();
                }
            }));
            if (!countDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS)) {
                throw new TimeoutException("Timed out waiting for service to become idle");
            }
        } catch (ParcelableException e) {
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    public List<BlobInfo> queryBlobsForUser(UserHandle user) throws IOException {
        try {
            return this.mService.queryBlobsForUser(user.getIdentifier());
        } catch (ParcelableException e) {
            e.maybeRethrow(IOException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    public void deleteBlob(BlobInfo blobInfo) throws IOException {
        try {
            this.mService.deleteBlob(blobInfo.getId());
        } catch (ParcelableException e) {
            e.maybeRethrow(IOException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    public List<BlobHandle> getLeasedBlobs() throws IOException {
        try {
            return this.mService.getLeasedBlobs(this.mContext.getOpPackageName());
        } catch (ParcelableException e) {
            e.maybeRethrow(IOException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    public LeaseInfo getLeaseInfo(BlobHandle blobHandle) throws IOException {
        try {
            return this.mService.getLeaseInfo(blobHandle, this.mContext.getOpPackageName());
        } catch (ParcelableException e) {
            e.maybeRethrow(IOException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    /* loaded from: classes.dex */
    public static class Session implements Closeable {
        private final IBlobStoreSession mSession;

        private Session(IBlobStoreSession session) {
            this.mSession = session;
        }

        public ParcelFileDescriptor openWrite(long offsetBytes, long lengthBytes) throws IOException {
            try {
                ParcelFileDescriptor pfd = this.mSession.openWrite(offsetBytes, lengthBytes);
                pfd.seekTo(offsetBytes);
                return pfd;
            } catch (ParcelableException e) {
                e.maybeRethrow(IOException.class);
                throw new RuntimeException(e);
            } catch (RemoteException e2) {
                throw e2.rethrowFromSystemServer();
            }
        }

        public ParcelFileDescriptor openRead() throws IOException {
            try {
                return this.mSession.openRead();
            } catch (ParcelableException e) {
                e.maybeRethrow(IOException.class);
                throw new RuntimeException(e);
            } catch (RemoteException e2) {
                throw e2.rethrowFromSystemServer();
            }
        }

        public long getSize() throws IOException {
            try {
                return this.mSession.getSize();
            } catch (ParcelableException e) {
                e.maybeRethrow(IOException.class);
                throw new RuntimeException(e);
            } catch (RemoteException e2) {
                throw e2.rethrowFromSystemServer();
            }
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            try {
                this.mSession.close();
            } catch (ParcelableException e) {
                e.maybeRethrow(IOException.class);
                throw new RuntimeException(e);
            } catch (RemoteException e2) {
                throw e2.rethrowFromSystemServer();
            }
        }

        public void abandon() throws IOException {
            try {
                this.mSession.abandon();
            } catch (ParcelableException e) {
                e.maybeRethrow(IOException.class);
                throw new RuntimeException(e);
            } catch (RemoteException e2) {
                throw e2.rethrowFromSystemServer();
            }
        }

        public void allowPackageAccess(String packageName, byte[] certificate) throws IOException {
            try {
                this.mSession.allowPackageAccess(packageName, certificate);
            } catch (ParcelableException e) {
                e.maybeRethrow(IOException.class);
                e.maybeRethrow(LimitExceededException.class);
                throw new RuntimeException(e);
            } catch (RemoteException e2) {
                throw e2.rethrowFromSystemServer();
            }
        }

        public boolean isPackageAccessAllowed(String packageName, byte[] certificate) throws IOException {
            try {
                return this.mSession.isPackageAccessAllowed(packageName, certificate);
            } catch (ParcelableException e) {
                e.maybeRethrow(IOException.class);
                throw new RuntimeException(e);
            } catch (RemoteException e2) {
                throw e2.rethrowFromSystemServer();
            }
        }

        public void allowSameSignatureAccess() throws IOException {
            try {
                this.mSession.allowSameSignatureAccess();
            } catch (ParcelableException e) {
                e.maybeRethrow(IOException.class);
                throw new RuntimeException(e);
            } catch (RemoteException e2) {
                throw e2.rethrowFromSystemServer();
            }
        }

        public boolean isSameSignatureAccessAllowed() throws IOException {
            try {
                return this.mSession.isSameSignatureAccessAllowed();
            } catch (ParcelableException e) {
                e.maybeRethrow(IOException.class);
                throw new RuntimeException(e);
            } catch (RemoteException e2) {
                throw e2.rethrowFromSystemServer();
            }
        }

        public void allowPublicAccess() throws IOException {
            try {
                this.mSession.allowPublicAccess();
            } catch (ParcelableException e) {
                e.maybeRethrow(IOException.class);
                throw new RuntimeException(e);
            } catch (RemoteException e2) {
                throw e2.rethrowFromSystemServer();
            }
        }

        public boolean isPublicAccessAllowed() throws IOException {
            try {
                return this.mSession.isPublicAccessAllowed();
            } catch (ParcelableException e) {
                e.maybeRethrow(IOException.class);
                throw new RuntimeException(e);
            } catch (RemoteException e2) {
                throw e2.rethrowFromSystemServer();
            }
        }

        public void commit(final Executor executor, final Consumer<Integer> resultCallback) throws IOException {
            try {
                this.mSession.commit(new IBlobCommitCallback.Stub() { // from class: android.app.blob.BlobStoreManager.Session.1
                    @Override // android.app.blob.IBlobCommitCallback
                    public void onResult(int result) {
                        executor.execute(PooledLambda.obtainRunnable(new BiConsumer() { // from class: android.app.blob.BlobStoreManager$Session$1$$ExternalSyntheticLambda0
                            @Override // java.util.function.BiConsumer
                            public final void accept(Object obj, Object obj2) {
                                ((Consumer) obj).accept((Integer) obj2);
                            }
                        }, resultCallback, Integer.valueOf(result)));
                    }
                });
            } catch (ParcelableException e) {
                e.maybeRethrow(IOException.class);
                throw new RuntimeException(e);
            } catch (RemoteException e2) {
                throw e2.rethrowFromSystemServer();
            }
        }
    }
}
