package android.telephony.mbms;

import android.p008os.Binder;
import android.p008os.RemoteException;
import android.telephony.mbms.IDownloadStatusListener;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public class InternalDownloadStatusListener extends IDownloadStatusListener.Stub {
    private final DownloadStatusListener mAppListener;
    private final Executor mExecutor;
    private volatile boolean mIsStopped = false;

    public InternalDownloadStatusListener(DownloadStatusListener appCallback, Executor executor) {
        this.mAppListener = appCallback;
        this.mExecutor = executor;
    }

    @Override // android.telephony.mbms.IDownloadStatusListener
    public void onStatusUpdated(final DownloadRequest request, final FileInfo fileInfo, final int status) throws RemoteException {
        if (this.mIsStopped) {
            return;
        }
        long token = Binder.clearCallingIdentity();
        try {
            this.mExecutor.execute(new Runnable() { // from class: android.telephony.mbms.InternalDownloadStatusListener.1
                @Override // java.lang.Runnable
                public void run() {
                    InternalDownloadStatusListener.this.mAppListener.onStatusUpdated(request, fileInfo, status);
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
