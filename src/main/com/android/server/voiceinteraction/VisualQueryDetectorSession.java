package com.android.server.voiceinteraction;

import android.content.Context;
import android.media.AudioFormat;
import android.media.permission.Identity;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.service.voice.IDetectorSessionVisualQueryDetectionCallback;
import android.service.voice.IMicrophoneHotwordDetectionVoiceInteractionCallback;
import android.service.voice.ISandboxedDetectionService;
import android.service.voice.IVisualQueryDetectionVoiceInteractionCallback;
import android.service.voice.VisualQueryDetectionServiceFailure;
import android.util.Slog;
import com.android.internal.app.IHotwordRecognitionStatusCallback;
import com.android.internal.app.IVisualQueryDetectionAttentionListener;
import com.android.internal.infra.ServiceConnector;
import com.android.server.voiceinteraction.HotwordDetectionConnection;
import com.android.server.voiceinteraction.VoiceInteractionManagerServiceImpl;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
/* loaded from: classes2.dex */
public final class VisualQueryDetectorSession extends DetectorSession {
    public IVisualQueryDetectionAttentionListener mAttentionListener;
    public boolean mEgressingData;
    public boolean mQueryStreaming;

    public VisualQueryDetectorSession(HotwordDetectionConnection.ServiceConnection serviceConnection, Object obj, Context context, IBinder iBinder, IHotwordRecognitionStatusCallback iHotwordRecognitionStatusCallback, int i, Identity identity, ScheduledExecutorService scheduledExecutorService, boolean z, VoiceInteractionManagerServiceImpl.DetectorRemoteExceptionListener detectorRemoteExceptionListener) {
        super(serviceConnection, obj, context, iBinder, iHotwordRecognitionStatusCallback, i, identity, scheduledExecutorService, z, detectorRemoteExceptionListener);
        this.mEgressingData = false;
        this.mQueryStreaming = false;
        this.mAttentionListener = null;
    }

    @Override // com.android.server.voiceinteraction.DetectorSession
    public void informRestartProcessLocked() {
        Slog.v("VisualQueryDetectorSession", "informRestartProcessLocked");
        this.mUpdateStateAfterStartFinished.set(false);
    }

    public void setVisualQueryDetectionAttentionListenerLocked(IVisualQueryDetectionAttentionListener iVisualQueryDetectionAttentionListener) {
        this.mAttentionListener = iVisualQueryDetectionAttentionListener;
    }

    public void startPerceivingLocked(final IVisualQueryDetectionVoiceInteractionCallback iVisualQueryDetectionVoiceInteractionCallback) {
        final IDetectorSessionVisualQueryDetectionCallback.Stub stub = new IDetectorSessionVisualQueryDetectionCallback.Stub() { // from class: com.android.server.voiceinteraction.VisualQueryDetectorSession.1
            public void onAttentionGained() {
                Slog.v("VisualQueryDetectorSession", "BinderCallback#onAttentionGained");
                VisualQueryDetectorSession.this.mEgressingData = true;
                if (VisualQueryDetectorSession.this.mAttentionListener == null) {
                    return;
                }
                try {
                    VisualQueryDetectorSession.this.mAttentionListener.onAttentionGained();
                } catch (RemoteException e) {
                    Slog.e("VisualQueryDetectorSession", "Error delivering attention gained event.", e);
                    try {
                        iVisualQueryDetectionVoiceInteractionCallback.onDetectionFailure(new VisualQueryDetectionServiceFailure(3, "Attention listener failed to switch to GAINED state."));
                    } catch (RemoteException unused) {
                        Slog.v("VisualQueryDetectorSession", "Fail to call onDetectionFailure");
                    }
                }
            }

            public void onAttentionLost() {
                Slog.v("VisualQueryDetectorSession", "BinderCallback#onAttentionLost");
                VisualQueryDetectorSession.this.mEgressingData = false;
                if (VisualQueryDetectorSession.this.mAttentionListener == null) {
                    return;
                }
                try {
                    VisualQueryDetectorSession.this.mAttentionListener.onAttentionLost();
                } catch (RemoteException e) {
                    Slog.e("VisualQueryDetectorSession", "Error delivering attention lost event.", e);
                    try {
                        iVisualQueryDetectionVoiceInteractionCallback.onDetectionFailure(new VisualQueryDetectionServiceFailure(3, "Attention listener failed to switch to LOST state."));
                    } catch (RemoteException unused) {
                        Slog.v("VisualQueryDetectorSession", "Fail to call onDetectionFailure");
                    }
                }
            }

            public void onQueryDetected(String str) throws RemoteException {
                Objects.requireNonNull(str);
                Slog.v("VisualQueryDetectorSession", "BinderCallback#onQueryDetected");
                if (!VisualQueryDetectorSession.this.mEgressingData) {
                    Slog.v("VisualQueryDetectorSession", "Query should not be egressed within the unattention state.");
                    iVisualQueryDetectionVoiceInteractionCallback.onDetectionFailure(new VisualQueryDetectionServiceFailure(4, "Cannot stream queries without attention signals."));
                    return;
                }
                VisualQueryDetectorSession.this.mQueryStreaming = true;
                iVisualQueryDetectionVoiceInteractionCallback.onQueryDetected(str);
                Slog.i("VisualQueryDetectorSession", "Egressed from visual query detection process.");
            }

            public void onQueryFinished() throws RemoteException {
                Slog.v("VisualQueryDetectorSession", "BinderCallback#onQueryFinished");
                if (!VisualQueryDetectorSession.this.mQueryStreaming) {
                    Slog.v("VisualQueryDetectorSession", "Query streaming state signal FINISHED is block since there is no active query being streamed.");
                    iVisualQueryDetectionVoiceInteractionCallback.onDetectionFailure(new VisualQueryDetectionServiceFailure(4, "Cannot send FINISHED signal with no query streamed."));
                    return;
                }
                iVisualQueryDetectionVoiceInteractionCallback.onQueryFinished();
                VisualQueryDetectorSession.this.mQueryStreaming = false;
            }

            public void onQueryRejected() throws RemoteException {
                Slog.v("VisualQueryDetectorSession", "BinderCallback#onQueryRejected");
                if (!VisualQueryDetectorSession.this.mQueryStreaming) {
                    Slog.v("VisualQueryDetectorSession", "Query streaming state signal REJECTED is block since there is no active query being streamed.");
                    iVisualQueryDetectionVoiceInteractionCallback.onDetectionFailure(new VisualQueryDetectionServiceFailure(4, "Cannot send REJECTED signal with no query streamed."));
                    return;
                }
                iVisualQueryDetectionVoiceInteractionCallback.onQueryRejected();
                VisualQueryDetectorSession.this.mQueryStreaming = false;
            }
        };
        this.mRemoteDetectionService.run(new ServiceConnector.VoidJob() { // from class: com.android.server.voiceinteraction.VisualQueryDetectorSession$$ExternalSyntheticLambda0
            public final void runNoResult(Object obj) {
                ((ISandboxedDetectionService) obj).detectWithVisualSignals(stub);
            }
        });
    }

    public void stopPerceivingLocked() {
        this.mRemoteDetectionService.run(new SoftwareTrustedHotwordDetectorSession$$ExternalSyntheticLambda0());
    }

    @Override // com.android.server.voiceinteraction.DetectorSession
    public void startListeningFromExternalSourceLocked(ParcelFileDescriptor parcelFileDescriptor, AudioFormat audioFormat, PersistableBundle persistableBundle, IMicrophoneHotwordDetectionVoiceInteractionCallback iMicrophoneHotwordDetectionVoiceInteractionCallback) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("HotwordDetectionService method should not be called from VisualQueryDetectorSession.");
    }

    @Override // com.android.server.voiceinteraction.DetectorSession
    public void dumpLocked(String str, PrintWriter printWriter) {
        super.dumpLocked(str, printWriter);
        printWriter.print(str);
    }
}
