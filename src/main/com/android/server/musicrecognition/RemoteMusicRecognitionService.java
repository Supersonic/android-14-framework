package com.android.server.musicrecognition;

import android.content.ComponentName;
import android.content.Context;
import android.media.AudioFormat;
import android.media.musicrecognition.IMusicRecognitionAttributionTagCallback;
import android.media.musicrecognition.IMusicRecognitionService;
import android.os.IBinder;
import android.os.IInterface;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.p005os.IInstalld;
import com.android.internal.infra.AbstractMultiplePendingRequestsRemoteService;
import com.android.internal.infra.AbstractRemoteService;
import com.android.server.musicrecognition.MusicRecognitionManagerPerUserService;
import java.util.concurrent.CompletableFuture;
/* loaded from: classes2.dex */
public class RemoteMusicRecognitionService extends AbstractMultiplePendingRequestsRemoteService<RemoteMusicRecognitionService, IMusicRecognitionService> {
    public final MusicRecognitionManagerPerUserService.MusicRecognitionServiceCallback mServerCallback;

    public long getTimeoutIdleBindMillis() {
        return 40000L;
    }

    public RemoteMusicRecognitionService(Context context, ComponentName componentName, int i, MusicRecognitionManagerPerUserService musicRecognitionManagerPerUserService, MusicRecognitionManagerPerUserService.MusicRecognitionServiceCallback musicRecognitionServiceCallback, boolean z, boolean z2) {
        super(context, "android.service.musicrecognition.MUSIC_RECOGNITION", componentName, i, musicRecognitionManagerPerUserService, context.getMainThreadHandler(), (z ? 4194304 : 0) | IInstalld.FLAG_USE_QUOTA, z2, 1);
        this.mServerCallback = musicRecognitionServiceCallback;
    }

    /* renamed from: getServiceInterface */
    public IMusicRecognitionService m4903getServiceInterface(IBinder iBinder) {
        return IMusicRecognitionService.Stub.asInterface(iBinder);
    }

    public MusicRecognitionManagerPerUserService.MusicRecognitionServiceCallback getServerCallback() {
        return this.mServerCallback;
    }

    public void onAudioStreamStarted(final ParcelFileDescriptor parcelFileDescriptor, final AudioFormat audioFormat) {
        scheduleAsyncRequest(new AbstractRemoteService.AsyncRequest() { // from class: com.android.server.musicrecognition.RemoteMusicRecognitionService$$ExternalSyntheticLambda0
            public final void run(IInterface iInterface) {
                RemoteMusicRecognitionService.this.lambda$onAudioStreamStarted$0(parcelFileDescriptor, audioFormat, (IMusicRecognitionService) iInterface);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onAudioStreamStarted$0(ParcelFileDescriptor parcelFileDescriptor, AudioFormat audioFormat, IMusicRecognitionService iMusicRecognitionService) throws RemoteException {
        iMusicRecognitionService.onAudioStreamStarted(parcelFileDescriptor, audioFormat, this.mServerCallback);
    }

    public CompletableFuture<String> getAttributionTag() {
        final CompletableFuture<String> completableFuture = new CompletableFuture<>();
        scheduleAsyncRequest(new AbstractRemoteService.AsyncRequest() { // from class: com.android.server.musicrecognition.RemoteMusicRecognitionService$$ExternalSyntheticLambda1
            public final void run(IInterface iInterface) {
                RemoteMusicRecognitionService.this.lambda$getAttributionTag$1(completableFuture, (IMusicRecognitionService) iInterface);
            }
        });
        return completableFuture;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getAttributionTag$1(final CompletableFuture completableFuture, IMusicRecognitionService iMusicRecognitionService) throws RemoteException {
        iMusicRecognitionService.getAttributionTag(new IMusicRecognitionAttributionTagCallback.Stub() { // from class: com.android.server.musicrecognition.RemoteMusicRecognitionService.1
            public void onAttributionTag(String str) throws RemoteException {
                completableFuture.complete(str);
            }
        });
    }
}
