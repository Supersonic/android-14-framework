package com.android.server.musicrecognition;

import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.media.AudioRecord;
import android.media.MediaMetadata;
import android.media.musicrecognition.IMusicRecognitionManagerCallback;
import android.media.musicrecognition.IMusicRecognitionServiceCallback;
import android.media.musicrecognition.RecognitionRequest;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.infra.AbstractRemoteService;
import com.android.server.infra.AbstractPerUserSystemService;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public final class MusicRecognitionManagerPerUserService extends AbstractPerUserSystemService<MusicRecognitionManagerPerUserService, MusicRecognitionManagerService> implements AbstractRemoteService.VultureCallback {
    public static final String TAG = MusicRecognitionManagerPerUserService.class.getSimpleName();
    public final AppOpsManager mAppOpsManager;
    public final String mAttributionMessage;
    public CompletableFuture<String> mAttributionTagFuture;
    @GuardedBy({"mLock"})
    public RemoteMusicRecognitionService mRemoteService;
    public ServiceInfo mServiceInfo;

    public static int getBufferSizeInBytes(int i, int i2) {
        return i * 2 * i2;
    }

    public MusicRecognitionManagerPerUserService(MusicRecognitionManagerService musicRecognitionManagerService, Object obj, int i) {
        super(musicRecognitionManagerService, obj, i);
        this.mAppOpsManager = (AppOpsManager) getContext().createAttributionContext(MusicRecognitionManagerService.TAG).getSystemService(AppOpsManager.class);
        this.mAttributionMessage = String.format("MusicRecognitionManager.invokedByUid.%s", Integer.valueOf(i));
        this.mAttributionTagFuture = null;
        this.mServiceInfo = null;
    }

    @Override // com.android.server.infra.AbstractPerUserSystemService
    @GuardedBy({"mLock"})
    public ServiceInfo newServiceInfoLocked(ComponentName componentName) throws PackageManager.NameNotFoundException {
        try {
            ServiceInfo serviceInfo = AppGlobals.getPackageManager().getServiceInfo(componentName, 128L, this.mUserId);
            if ("android.permission.BIND_MUSIC_RECOGNITION_SERVICE".equals(serviceInfo.permission)) {
                return serviceInfo;
            }
            String str = TAG;
            Slog.w(str, "MusicRecognitionService from '" + serviceInfo.packageName + "' does not require permission android.permission.BIND_MUSIC_RECOGNITION_SERVICE");
            throw new SecurityException("Service does not require permission android.permission.BIND_MUSIC_RECOGNITION_SERVICE");
        } catch (RemoteException unused) {
            throw new PackageManager.NameNotFoundException("Could not get service for " + componentName);
        }
    }

    @GuardedBy({"mLock"})
    public final RemoteMusicRecognitionService ensureRemoteServiceLocked(IMusicRecognitionManagerCallback iMusicRecognitionManagerCallback) {
        if (this.mRemoteService == null) {
            String componentNameLocked = getComponentNameLocked();
            if (componentNameLocked == null) {
                if (((MusicRecognitionManagerService) this.mMaster).verbose) {
                    Slog.v(TAG, "ensureRemoteServiceLocked(): not set");
                }
                return null;
            }
            this.mRemoteService = new RemoteMusicRecognitionService(getContext(), ComponentName.unflattenFromString(componentNameLocked), this.mUserId, this, new MusicRecognitionServiceCallback(iMusicRecognitionManagerCallback), ((MusicRecognitionManagerService) this.mMaster).isBindInstantServiceAllowed(), ((MusicRecognitionManagerService) this.mMaster).verbose);
            try {
                this.mServiceInfo = getContext().getPackageManager().getServiceInfo(this.mRemoteService.getComponentName(), 128);
                this.mAttributionTagFuture = this.mRemoteService.getAttributionTag();
                String str = TAG;
                Slog.i(str, "Remote service bound: " + this.mRemoteService.getComponentName());
            } catch (PackageManager.NameNotFoundException e) {
                Slog.e(TAG, "Service was not found.", e);
            }
        }
        return this.mRemoteService;
    }

    @GuardedBy({"mLock"})
    public void beginRecognitionLocked(final RecognitionRequest recognitionRequest, IBinder iBinder) {
        final IMusicRecognitionManagerCallback asInterface = IMusicRecognitionManagerCallback.Stub.asInterface(iBinder);
        RemoteMusicRecognitionService ensureRemoteServiceLocked = ensureRemoteServiceLocked(asInterface);
        this.mRemoteService = ensureRemoteServiceLocked;
        if (ensureRemoteServiceLocked == null) {
            try {
                asInterface.onRecognitionFailed(3);
                return;
            } catch (RemoteException unused) {
                return;
            }
        }
        Pair<ParcelFileDescriptor, ParcelFileDescriptor> createPipe = createPipe();
        if (createPipe == null) {
            try {
                asInterface.onRecognitionFailed(7);
                return;
            } catch (RemoteException unused2) {
                return;
            }
        }
        final ParcelFileDescriptor parcelFileDescriptor = (ParcelFileDescriptor) createPipe.second;
        this.mAttributionTagFuture.thenAcceptAsync(new Consumer() { // from class: com.android.server.musicrecognition.MusicRecognitionManagerPerUserService$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                MusicRecognitionManagerPerUserService.this.lambda$beginRecognitionLocked$0(recognitionRequest, asInterface, parcelFileDescriptor, (String) obj);
            }
        }, (Executor) ((MusicRecognitionManagerService) this.mMaster).mExecutorService);
        this.mRemoteService.onAudioStreamStarted((ParcelFileDescriptor) createPipe.first, recognitionRequest.getAudioFormat());
    }

    /* renamed from: streamAudio */
    public final void lambda$beginRecognitionLocked$0(String str, RecognitionRequest recognitionRequest, IMusicRecognitionManagerCallback iMusicRecognitionManagerCallback, ParcelFileDescriptor parcelFileDescriptor) {
        ParcelFileDescriptor.AutoCloseOutputStream autoCloseOutputStream;
        int min = Math.min(recognitionRequest.getMaxAudioLengthSeconds(), 24);
        if (min <= 0) {
            Slog.i(TAG, "No audio requested. Closing stream.");
            try {
                parcelFileDescriptor.close();
                iMusicRecognitionManagerCallback.onAudioStreamClosed();
                return;
            } catch (RemoteException unused) {
                return;
            } catch (IOException e) {
                Slog.e(TAG, "Problem closing stream.", e);
                return;
            }
        }
        try {
            startRecordAudioOp(str);
            AudioRecord createAudioRecord = createAudioRecord(recognitionRequest, min);
            try {
                try {
                    autoCloseOutputStream = new ParcelFileDescriptor.AutoCloseOutputStream(parcelFileDescriptor);
                } finally {
                    createAudioRecord.release();
                    finishRecordAudioOp(str);
                    try {
                        iMusicRecognitionManagerCallback.onAudioStreamClosed();
                    } catch (RemoteException unused2) {
                    }
                }
            } catch (IOException e2) {
                Slog.e(TAG, "Audio streaming stopped.", e2);
            }
            try {
                streamAudio(recognitionRequest, min, createAudioRecord, autoCloseOutputStream);
                autoCloseOutputStream.close();
                try {
                    iMusicRecognitionManagerCallback.onAudioStreamClosed();
                } catch (RemoteException unused3) {
                }
            } catch (Throwable th) {
                try {
                    autoCloseOutputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (SecurityException e3) {
            String str2 = TAG;
            Slog.e(str2, "RECORD_AUDIO op not permitted on behalf of " + this.mServiceInfo.getComponentName(), e3);
            try {
                iMusicRecognitionManagerCallback.onRecognitionFailed(7);
            } catch (RemoteException unused4) {
            }
        }
    }

    public final void streamAudio(RecognitionRequest recognitionRequest, int i, AudioRecord audioRecord, OutputStream outputStream) throws IOException {
        int bufferSizeInFrames = audioRecord.getBufferSizeInFrames() / i;
        byte[] bArr = new byte[bufferSizeInFrames];
        int ignoreBeginningFrames = recognitionRequest.getIgnoreBeginningFrames() * 2;
        audioRecord.startRecording();
        int i2 = 0;
        int i3 = 0;
        while (i2 >= 0 && i3 < audioRecord.getBufferSizeInFrames() * 2 && this.mRemoteService != null) {
            i2 = audioRecord.read(bArr, 0, bufferSizeInFrames);
            if (i2 > 0) {
                i3 += i2;
                if (ignoreBeginningFrames > 0) {
                    ignoreBeginningFrames -= i2;
                    if (ignoreBeginningFrames < 0) {
                        outputStream.write(bArr, i2 + ignoreBeginningFrames, -ignoreBeginningFrames);
                    }
                } else {
                    outputStream.write(bArr);
                }
            }
        }
        Slog.i(TAG, String.format("Streamed %s bytes from audio record", Integer.valueOf(i3)));
    }

    /* loaded from: classes2.dex */
    public final class MusicRecognitionServiceCallback extends IMusicRecognitionServiceCallback.Stub {
        public final IMusicRecognitionManagerCallback mClientCallback;

        public MusicRecognitionServiceCallback(IMusicRecognitionManagerCallback iMusicRecognitionManagerCallback) {
            this.mClientCallback = iMusicRecognitionManagerCallback;
        }

        public void onRecognitionSucceeded(MediaMetadata mediaMetadata, Bundle bundle) {
            try {
                MusicRecognitionManagerPerUserService.sanitizeBundle(bundle);
                this.mClientCallback.onRecognitionSucceeded(mediaMetadata, bundle);
            } catch (RemoteException unused) {
            }
            MusicRecognitionManagerPerUserService.this.destroyService();
        }

        public void onRecognitionFailed(int i) {
            try {
                this.mClientCallback.onRecognitionFailed(i);
            } catch (RemoteException unused) {
            }
            MusicRecognitionManagerPerUserService.this.destroyService();
        }

        public final IMusicRecognitionManagerCallback getClientCallback() {
            return this.mClientCallback;
        }
    }

    public void onServiceDied(RemoteMusicRecognitionService remoteMusicRecognitionService) {
        try {
            remoteMusicRecognitionService.getServerCallback().getClientCallback().onRecognitionFailed(5);
        } catch (RemoteException unused) {
        }
        String str = TAG;
        Slog.w(str, "remote service died: " + remoteMusicRecognitionService);
        destroyService();
    }

    @GuardedBy({"mLock"})
    public final void destroyService() {
        synchronized (this.mLock) {
            RemoteMusicRecognitionService remoteMusicRecognitionService = this.mRemoteService;
            if (remoteMusicRecognitionService != null) {
                remoteMusicRecognitionService.destroy();
                this.mRemoteService = null;
            }
        }
    }

    public final void startRecordAudioOp(String str) {
        AppOpsManager appOpsManager = this.mAppOpsManager;
        String permissionToOp = AppOpsManager.permissionToOp("android.permission.RECORD_AUDIO");
        Objects.requireNonNull(permissionToOp);
        ServiceInfo serviceInfo = this.mServiceInfo;
        int startProxyOp = appOpsManager.startProxyOp(permissionToOp, serviceInfo.applicationInfo.uid, serviceInfo.packageName, str, this.mAttributionMessage);
        if (startProxyOp != 0) {
            throw new SecurityException(String.format("Failed to obtain RECORD_AUDIO permission (status: %d) for receiving service: %s", Integer.valueOf(startProxyOp), this.mServiceInfo.getComponentName()));
        }
        String str2 = TAG;
        ServiceInfo serviceInfo2 = this.mServiceInfo;
        Slog.i(str2, String.format("Starting audio streaming. Attributing to %s (%d) with tag '%s'", serviceInfo2.packageName, Integer.valueOf(serviceInfo2.applicationInfo.uid), str));
    }

    public final void finishRecordAudioOp(String str) {
        AppOpsManager appOpsManager = this.mAppOpsManager;
        String permissionToOp = AppOpsManager.permissionToOp("android.permission.RECORD_AUDIO");
        Objects.requireNonNull(permissionToOp);
        ServiceInfo serviceInfo = this.mServiceInfo;
        appOpsManager.finishProxyOp(permissionToOp, serviceInfo.applicationInfo.uid, serviceInfo.packageName, str);
    }

    public static AudioRecord createAudioRecord(RecognitionRequest recognitionRequest, int i) {
        return new AudioRecord(recognitionRequest.getAudioAttributes(), recognitionRequest.getAudioFormat(), getBufferSizeInBytes(recognitionRequest.getAudioFormat().getSampleRate(), i), recognitionRequest.getCaptureSession());
    }

    public static Pair<ParcelFileDescriptor, ParcelFileDescriptor> createPipe() {
        try {
            ParcelFileDescriptor[] createPipe = ParcelFileDescriptor.createPipe();
            if (createPipe.length != 2) {
                Slog.e(TAG, "Failed to create audio stream pipe, unexpected number of file descriptors");
                return null;
            } else if (!createPipe[0].getFileDescriptor().valid() || !createPipe[1].getFileDescriptor().valid()) {
                Slog.e(TAG, "Failed to create audio stream pipe, didn't receive a pair of valid file descriptors.");
                return null;
            } else {
                return Pair.create(createPipe[0], createPipe[1]);
            }
        } catch (IOException e) {
            Slog.e(TAG, "Failed to create audio stream pipe", e);
            return null;
        }
    }

    public static void sanitizeBundle(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        for (String str : bundle.keySet()) {
            Object obj = bundle.get(str);
            if (obj instanceof Bundle) {
                sanitizeBundle((Bundle) obj);
            } else if ((obj instanceof IBinder) || (obj instanceof ParcelFileDescriptor)) {
                bundle.remove(str);
            }
        }
    }
}
