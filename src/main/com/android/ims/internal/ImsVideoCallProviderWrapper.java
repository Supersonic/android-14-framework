package com.android.ims.internal;

import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.telecom.Connection;
import android.telecom.VideoProfile;
import android.util.Log;
import android.view.Surface;
import com.android.ims.RegistrantList;
import com.android.ims.SomeArgs;
import com.android.ims.internal.IImsVideoCallCallback;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
/* loaded from: classes.dex */
public class ImsVideoCallProviderWrapper extends Connection.VideoProvider {
    private static final String LOG_TAG = ImsVideoCallProviderWrapper.class.getSimpleName();
    private static final int MSG_CHANGE_CALL_DATA_USAGE = 5;
    private static final int MSG_CHANGE_CAMERA_CAPABILITIES = 6;
    private static final int MSG_CHANGE_PEER_DIMENSIONS = 4;
    private static final int MSG_CHANGE_VIDEO_QUALITY = 7;
    private static final int MSG_HANDLE_CALL_SESSION_EVENT = 3;
    private static final int MSG_RECEIVE_SESSION_MODIFY_REQUEST = 1;
    private static final int MSG_RECEIVE_SESSION_MODIFY_RESPONSE = 2;
    private final ImsVideoCallCallback mBinder;
    private final Set<ImsVideoProviderWrapperCallback> mCallbacks;
    private int mCurrentVideoState;
    private RegistrantList mDataUsageUpdateRegistrants;
    private IBinder.DeathRecipient mDeathRecipient;
    private final Handler mHandler;
    private boolean mIsVideoEnabled;
    private boolean mUseVideoPauseWorkaround;
    private final IImsVideoCallProvider mVideoCallProvider;
    private VideoPauseTracker mVideoPauseTracker;

    /* loaded from: classes.dex */
    public interface ImsVideoProviderWrapperCallback {
        void onReceiveSessionModifyResponse(int i, VideoProfile videoProfile, VideoProfile videoProfile2);
    }

    /* loaded from: classes.dex */
    private final class ImsVideoCallCallback extends IImsVideoCallCallback.Stub {
        private ImsVideoCallCallback() {
        }

        public void receiveSessionModifyRequest(VideoProfile VideoProfile) {
            ImsVideoCallProviderWrapper.this.mHandler.obtainMessage(1, VideoProfile).sendToTarget();
        }

        public void receiveSessionModifyResponse(int status, VideoProfile requestProfile, VideoProfile responseProfile) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = Integer.valueOf(status);
            args.arg2 = requestProfile;
            args.arg3 = responseProfile;
            ImsVideoCallProviderWrapper.this.mHandler.obtainMessage(2, args).sendToTarget();
        }

        public void handleCallSessionEvent(int event) {
            ImsVideoCallProviderWrapper.this.mHandler.obtainMessage(3, Integer.valueOf(event)).sendToTarget();
        }

        public void changePeerDimensions(int width, int height) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = Integer.valueOf(width);
            args.arg2 = Integer.valueOf(height);
            ImsVideoCallProviderWrapper.this.mHandler.obtainMessage(4, args).sendToTarget();
        }

        public void changeVideoQuality(int videoQuality) {
            ImsVideoCallProviderWrapper.this.mHandler.obtainMessage(7, videoQuality, 0).sendToTarget();
        }

        public void changeCallDataUsage(long dataUsage) {
            ImsVideoCallProviderWrapper.this.mHandler.obtainMessage(5, Long.valueOf(dataUsage)).sendToTarget();
        }

        public void changeCameraCapabilities(VideoProfile.CameraCapabilities cameraCapabilities) {
            ImsVideoCallProviderWrapper.this.mHandler.obtainMessage(6, cameraCapabilities).sendToTarget();
        }
    }

    public void registerForDataUsageUpdate(Handler h, int what, Object obj) {
        this.mDataUsageUpdateRegistrants.addUnique(h, what, obj);
    }

    public void unregisterForDataUsageUpdate(Handler h) {
        this.mDataUsageUpdateRegistrants.remove(h);
    }

    public void addImsVideoProviderCallback(ImsVideoProviderWrapperCallback callback) {
        this.mCallbacks.add(callback);
    }

    public void removeImsVideoProviderCallback(ImsVideoProviderWrapperCallback callback) {
        this.mCallbacks.remove(callback);
    }

    public ImsVideoCallProviderWrapper(IImsVideoCallProvider videoProvider) throws RemoteException {
        this.mDataUsageUpdateRegistrants = new RegistrantList();
        this.mCallbacks = Collections.newSetFromMap(new ConcurrentHashMap(8, 0.9f, 1));
        this.mVideoPauseTracker = new VideoPauseTracker();
        this.mUseVideoPauseWorkaround = false;
        this.mIsVideoEnabled = true;
        this.mDeathRecipient = new IBinder.DeathRecipient() { // from class: com.android.ims.internal.ImsVideoCallProviderWrapper.1
            @Override // android.os.IBinder.DeathRecipient
            public void binderDied() {
                try {
                    ImsVideoCallProviderWrapper.this.mVideoCallProvider.asBinder().unlinkToDeath(this, 0);
                } catch (NoSuchElementException e) {
                }
            }
        };
        this.mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.ims.internal.ImsVideoCallProviderWrapper.2
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                SomeArgs args;
                switch (msg.what) {
                    case 1:
                        VideoProfile videoProfile = (VideoProfile) msg.obj;
                        if (!VideoProfile.isVideo(ImsVideoCallProviderWrapper.this.mCurrentVideoState) && VideoProfile.isVideo(videoProfile.getVideoState()) && !ImsVideoCallProviderWrapper.this.mIsVideoEnabled) {
                            Log.i(ImsVideoCallProviderWrapper.LOG_TAG, String.format("receiveSessionModifyRequest: requestedVideoState=%s; rejecting as video is disabled.", Integer.valueOf(videoProfile.getVideoState())));
                            try {
                                ImsVideoCallProviderWrapper.this.mVideoCallProvider.sendSessionModifyResponse(new VideoProfile(0));
                                return;
                            } catch (RemoteException e) {
                                return;
                            }
                        }
                        ImsVideoCallProviderWrapper.this.receiveSessionModifyRequest(videoProfile);
                        return;
                    case 2:
                        args = (SomeArgs) msg.obj;
                        try {
                            int status = ((Integer) args.arg1).intValue();
                            VideoProfile requestProfile = (VideoProfile) args.arg2;
                            VideoProfile responseProfile = (VideoProfile) args.arg3;
                            ImsVideoCallProviderWrapper.this.receiveSessionModifyResponse(status, requestProfile, responseProfile);
                            for (ImsVideoProviderWrapperCallback callback : ImsVideoCallProviderWrapper.this.mCallbacks) {
                                if (callback != null) {
                                    callback.onReceiveSessionModifyResponse(status, requestProfile, responseProfile);
                                }
                            }
                            return;
                        } finally {
                        }
                    case 3:
                        ImsVideoCallProviderWrapper.this.handleCallSessionEvent(((Integer) msg.obj).intValue());
                        return;
                    case 4:
                        args = (SomeArgs) msg.obj;
                        try {
                            int width = ((Integer) args.arg1).intValue();
                            int height = ((Integer) args.arg2).intValue();
                            ImsVideoCallProviderWrapper.this.changePeerDimensions(width, height);
                            return;
                        } finally {
                        }
                    case 5:
                        ImsVideoCallProviderWrapper.this.setCallDataUsage(((Long) msg.obj).longValue());
                        ImsVideoCallProviderWrapper.this.mDataUsageUpdateRegistrants.notifyResult(msg.obj);
                        return;
                    case 6:
                        ImsVideoCallProviderWrapper.this.changeCameraCapabilities((VideoProfile.CameraCapabilities) msg.obj);
                        return;
                    case 7:
                        ImsVideoCallProviderWrapper.this.changeVideoQuality(msg.arg1);
                        return;
                    default:
                        return;
                }
            }
        };
        this.mVideoCallProvider = videoProvider;
        if (videoProvider != null) {
            videoProvider.asBinder().linkToDeath(this.mDeathRecipient, 0);
            ImsVideoCallCallback imsVideoCallCallback = new ImsVideoCallCallback();
            this.mBinder = imsVideoCallCallback;
            videoProvider.setCallback(imsVideoCallCallback);
            return;
        }
        this.mBinder = null;
    }

    public ImsVideoCallProviderWrapper(IImsVideoCallProvider videoProvider, VideoPauseTracker videoPauseTracker) throws RemoteException {
        this(videoProvider);
        this.mVideoPauseTracker = videoPauseTracker;
    }

    @Override // android.telecom.Connection.VideoProvider
    public void onSetCamera(String cameraId) {
        try {
            this.mVideoCallProvider.setCamera(cameraId, Binder.getCallingUid());
        } catch (RemoteException e) {
        }
    }

    @Override // android.telecom.Connection.VideoProvider
    public void onSetPreviewSurface(Surface surface) {
        try {
            this.mVideoCallProvider.setPreviewSurface(surface);
        } catch (RemoteException e) {
        }
    }

    @Override // android.telecom.Connection.VideoProvider
    public void onSetDisplaySurface(Surface surface) {
        try {
            this.mVideoCallProvider.setDisplaySurface(surface);
        } catch (RemoteException e) {
        }
    }

    @Override // android.telecom.Connection.VideoProvider
    public void onSetDeviceOrientation(int rotation) {
        try {
            this.mVideoCallProvider.setDeviceOrientation(rotation);
        } catch (RemoteException e) {
        }
    }

    @Override // android.telecom.Connection.VideoProvider
    public void onSetZoom(float value) {
        try {
            this.mVideoCallProvider.setZoom(value);
        } catch (RemoteException e) {
        }
    }

    @Override // android.telecom.Connection.VideoProvider
    public void onSendSessionModifyRequest(VideoProfile fromProfile, VideoProfile toProfile) {
        if (fromProfile == null || toProfile == null) {
            Log.w(LOG_TAG, "onSendSessionModifyRequest: null profile in request.");
            return;
        }
        try {
            if (isResumeRequest(fromProfile.getVideoState(), toProfile.getVideoState()) && !VideoProfile.isPaused(this.mCurrentVideoState)) {
                Log.i(LOG_TAG, String.format("onSendSessionModifyRequest: fromVideoState=%s, toVideoState=%s; skipping resume request - already resumed.", VideoProfile.videoStateToString(fromProfile.getVideoState()), VideoProfile.videoStateToString(toProfile.getVideoState())));
                return;
            }
            VideoProfile toProfile2 = maybeFilterPauseResume(fromProfile, toProfile, 1);
            fromProfile.getVideoState();
            toProfile2.getVideoState();
            Log.i(LOG_TAG, String.format("onSendSessionModifyRequest: fromVideoState=%s, toVideoState=%s; ", VideoProfile.videoStateToString(fromProfile.getVideoState()), VideoProfile.videoStateToString(toProfile2.getVideoState())));
            this.mVideoCallProvider.sendSessionModifyRequest(fromProfile, toProfile2);
        } catch (RemoteException e) {
        }
    }

    @Override // android.telecom.Connection.VideoProvider
    public void onSendSessionModifyResponse(VideoProfile responseProfile) {
        try {
            this.mVideoCallProvider.sendSessionModifyResponse(responseProfile);
        } catch (RemoteException e) {
        }
    }

    @Override // android.telecom.Connection.VideoProvider
    public void onRequestCameraCapabilities() {
        try {
            this.mVideoCallProvider.requestCameraCapabilities();
        } catch (RemoteException e) {
        }
    }

    @Override // android.telecom.Connection.VideoProvider
    public void onRequestConnectionDataUsage() {
        try {
            this.mVideoCallProvider.requestCallDataUsage();
        } catch (RemoteException e) {
        }
    }

    @Override // android.telecom.Connection.VideoProvider
    public void onSetPauseImage(Uri uri) {
        try {
            this.mVideoCallProvider.setPauseImage(uri);
        } catch (RemoteException e) {
        }
    }

    public static boolean isPauseRequest(int from, int to) {
        boolean fromPaused = VideoProfile.isPaused(from);
        boolean toPaused = VideoProfile.isPaused(to);
        return !fromPaused && toPaused;
    }

    public static boolean isResumeRequest(int from, int to) {
        boolean fromPaused = VideoProfile.isPaused(from);
        boolean toPaused = VideoProfile.isPaused(to);
        return fromPaused && !toPaused;
    }

    public static boolean isTurnOffCameraRequest(int from, int to) {
        return VideoProfile.isTransmissionEnabled(from) && !VideoProfile.isTransmissionEnabled(to);
    }

    public static boolean isTurnOnCameraRequest(int from, int to) {
        return !VideoProfile.isTransmissionEnabled(from) && VideoProfile.isTransmissionEnabled(to);
    }

    public VideoProfile maybeFilterPauseResume(VideoProfile fromProfile, VideoProfile toProfile, int source) {
        int fromVideoState = fromProfile.getVideoState();
        int toVideoState = toProfile.getVideoState();
        boolean isPauseRequest = false;
        boolean isPauseSpecialCase = source == 1 && VideoProfile.isPaused(fromVideoState) && VideoProfile.isPaused(toVideoState);
        if (isPauseRequest(fromVideoState, toVideoState) || isPauseSpecialCase) {
            isPauseRequest = true;
        }
        boolean isResumeRequest = isResumeRequest(fromVideoState, toVideoState);
        if (isPauseRequest) {
            Log.i(LOG_TAG, String.format("maybeFilterPauseResume: isPauseRequest (from=%s, to=%s)", VideoProfile.videoStateToString(fromVideoState), VideoProfile.videoStateToString(toVideoState)));
            if (!this.mVideoPauseTracker.shouldPauseVideoFor(source) && !isPauseSpecialCase) {
                return new VideoProfile(toVideoState & (-5), toProfile.getQuality());
            }
            return toProfile;
        } else if (isResumeRequest) {
            boolean isTurnOffCameraRequest = isTurnOffCameraRequest(fromVideoState, toVideoState);
            boolean isTurnOnCameraRequest = isTurnOnCameraRequest(fromVideoState, toVideoState);
            if (this.mUseVideoPauseWorkaround && (isTurnOffCameraRequest || isTurnOnCameraRequest)) {
                Log.i(LOG_TAG, String.format("maybeFilterPauseResume: isResumeRequest, but camera turning on/off so skipping (from=%s, to=%s)", VideoProfile.videoStateToString(fromVideoState), VideoProfile.videoStateToString(toVideoState)));
                return toProfile;
            }
            Log.i(LOG_TAG, String.format("maybeFilterPauseResume: isResumeRequest (from=%s, to=%s)", VideoProfile.videoStateToString(fromVideoState), VideoProfile.videoStateToString(toVideoState)));
            if (!this.mVideoPauseTracker.shouldResumeVideoFor(source)) {
                return new VideoProfile(toVideoState | 4, toProfile.getQuality());
            }
            return toProfile;
        } else {
            return toProfile;
        }
    }

    public void pauseVideo(int fromVideoState, int source) {
        if (this.mVideoPauseTracker.shouldPauseVideoFor(source)) {
            VideoProfile fromProfile = new VideoProfile(fromVideoState);
            VideoProfile toProfile = new VideoProfile(fromVideoState | 4);
            try {
                Log.i(LOG_TAG, String.format("pauseVideo: fromVideoState=%s, toVideoState=%s", VideoProfile.videoStateToString(fromProfile.getVideoState()), VideoProfile.videoStateToString(toProfile.getVideoState())));
                this.mVideoCallProvider.sendSessionModifyRequest(fromProfile, toProfile);
                return;
            } catch (RemoteException e) {
                return;
            }
        }
        Log.i(LOG_TAG, "pauseVideo: video already paused");
    }

    public void resumeVideo(int fromVideoState, int source) {
        if (this.mVideoPauseTracker.shouldResumeVideoFor(source)) {
            VideoProfile fromProfile = new VideoProfile(fromVideoState);
            VideoProfile toProfile = new VideoProfile(fromVideoState & (-5));
            try {
                Log.i(LOG_TAG, String.format("resumeVideo: fromVideoState=%s, toVideoState=%s", VideoProfile.videoStateToString(fromProfile.getVideoState()), VideoProfile.videoStateToString(toProfile.getVideoState())));
                this.mVideoCallProvider.sendSessionModifyRequest(fromProfile, toProfile);
                return;
            } catch (RemoteException e) {
                return;
            }
        }
        Log.i(LOG_TAG, "resumeVideo: remaining paused (paused from other sources)");
    }

    public boolean wasVideoPausedFromSource(int source) {
        return this.mVideoPauseTracker.wasVideoPausedFromSource(source);
    }

    public void setUseVideoPauseWorkaround(boolean useVideoPauseWorkaround) {
        this.mUseVideoPauseWorkaround = useVideoPauseWorkaround;
    }

    public void onVideoStateChanged(int newVideoState) {
        if (VideoProfile.isPaused(this.mCurrentVideoState) && !VideoProfile.isPaused(newVideoState)) {
            Log.i(LOG_TAG, String.format("onVideoStateChanged: currentVideoState=%s, newVideoState=%s, clearing pending pause requests.", VideoProfile.videoStateToString(this.mCurrentVideoState), VideoProfile.videoStateToString(newVideoState)));
            this.mVideoPauseTracker.clearPauseRequests();
        } else {
            Log.d(LOG_TAG, String.format("onVideoStateChanged: currentVideoState=%s, newVideoState=%s", VideoProfile.videoStateToString(this.mCurrentVideoState), VideoProfile.videoStateToString(newVideoState)));
        }
        this.mCurrentVideoState = newVideoState;
    }

    public void setIsVideoEnabled(boolean isVideoEnabled) {
        this.mIsVideoEnabled = isVideoEnabled;
    }

    public void tearDown() {
        if (this.mDeathRecipient != null) {
            try {
                this.mVideoCallProvider.asBinder().unlinkToDeath(this.mDeathRecipient, 0);
            } catch (NoSuchElementException e) {
            }
        }
    }
}
