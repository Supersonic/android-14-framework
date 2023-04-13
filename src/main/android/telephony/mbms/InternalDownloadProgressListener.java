package android.telephony.mbms;

import android.p008os.Binder;
import android.p008os.RemoteException;
import android.telephony.mbms.IDownloadProgressListener;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public class InternalDownloadProgressListener extends IDownloadProgressListener.Stub {
    private final DownloadProgressListener mAppListener;
    private final Executor mExecutor;
    private volatile boolean mIsStopped = false;

    public InternalDownloadProgressListener(DownloadProgressListener appListener, Executor executor) {
        this.mAppListener = appListener;
        this.mExecutor = executor;
    }

    @Override // android.telephony.mbms.IDownloadProgressListener
    public void onProgressUpdated(final DownloadRequest request, final FileInfo fileInfo, final int currentDownloadSize, final int fullDownloadSize, final int currentDecodedSize, final int fullDecodedSize) throws RemoteException {
        if (this.mIsStopped) {
            return;
        }
        long token = Binder.clearCallingIdentity();
        try {
            this.mExecutor.execute(new Runnable() { // from class: android.telephony.mbms.InternalDownloadProgressListener.1
                @Override // java.lang.Runnable
                public void run() {
                    InternalDownloadProgressListener.this.mAppListener.onProgressUpdated(request, fileInfo, currentDownloadSize, fullDownloadSize, currentDecodedSize, fullDecodedSize);
                }
            });
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public void stop() {
        this.mIsStopped = true;
    }
}
