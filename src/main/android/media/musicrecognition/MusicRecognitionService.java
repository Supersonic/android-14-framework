package android.media.musicrecognition;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.MediaMetadata;
import android.media.musicrecognition.IMusicRecognitionService;
import android.media.musicrecognition.MusicRecognitionService;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.util.Log;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
@SystemApi
/* loaded from: classes2.dex */
public abstract class MusicRecognitionService extends Service {
    public static final String ACTION_MUSIC_SEARCH_LOOKUP = "android.service.musicrecognition.MUSIC_RECOGNITION";
    private static final String TAG = MusicRecognitionService.class.getSimpleName();
    private Handler mHandler;
    private final IMusicRecognitionService mServiceInterface = new IMusicRecognitionService.Stub() { // from class: android.media.musicrecognition.MusicRecognitionService.1
        @Override // android.media.musicrecognition.IMusicRecognitionService
        public void onAudioStreamStarted(ParcelFileDescriptor fd, AudioFormat audioFormat, final IMusicRecognitionServiceCallback callback) {
            Handler handler = MusicRecognitionService.this.mHandler;
            final MusicRecognitionService musicRecognitionService = MusicRecognitionService.this;
            handler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: android.media.musicrecognition.MusicRecognitionService$1$$ExternalSyntheticLambda0
                @Override // com.android.internal.util.function.TriConsumer
                public final void accept(Object obj, Object obj2, Object obj3) {
                    MusicRecognitionService.this.onRecognize((ParcelFileDescriptor) obj, (AudioFormat) obj2, (MusicRecognitionService.BinderC18511.C18521) obj3);
                }
            }, fd, audioFormat, new Callback() { // from class: android.media.musicrecognition.MusicRecognitionService.1.1
                @Override // android.media.musicrecognition.MusicRecognitionService.Callback
                public void onRecognitionSucceeded(MediaMetadata result, Bundle extras) {
                    try {
                        callback.onRecognitionSucceeded(result, extras);
                    } catch (RemoteException e) {
                        throw e.rethrowFromSystemServer();
                    }
                }

                @Override // android.media.musicrecognition.MusicRecognitionService.Callback
                public void onRecognitionFailed(int failureCode) {
                    try {
                        callback.onRecognitionFailed(failureCode);
                    } catch (RemoteException e) {
                        throw e.rethrowFromSystemServer();
                    }
                }
            }));
        }

        @Override // android.media.musicrecognition.IMusicRecognitionService
        public void getAttributionTag(IMusicRecognitionAttributionTagCallback callback) throws RemoteException {
            String tag = MusicRecognitionService.this.getAttributionTag();
            callback.onAttributionTag(tag);
        }
    };

    /* loaded from: classes2.dex */
    public interface Callback {
        void onRecognitionFailed(int i);

        void onRecognitionSucceeded(MediaMetadata mediaMetadata, Bundle bundle);
    }

    public abstract void onRecognize(ParcelFileDescriptor parcelFileDescriptor, AudioFormat audioFormat, Callback callback);

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.mHandler = new Handler(Looper.getMainLooper(), null, true);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (ACTION_MUSIC_SEARCH_LOOKUP.equals(intent.getAction())) {
            return this.mServiceInterface.asBinder();
        }
        Log.m104w(TAG, "Tried to bind to wrong intent (should be android.service.musicrecognition.MUSIC_RECOGNITION: " + intent);
        return null;
    }
}
