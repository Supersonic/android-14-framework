package com.android.server.p014wm;

import android.view.ContentRecordingSession;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
/* renamed from: com.android.server.wm.ContentRecordingController */
/* loaded from: classes2.dex */
public final class ContentRecordingController {
    public ContentRecordingSession mSession = null;
    public DisplayContent mDisplayContent = null;

    @VisibleForTesting
    public ContentRecordingSession getContentRecordingSessionLocked() {
        return this.mSession;
    }

    public void setContentRecordingSessionLocked(ContentRecordingSession contentRecordingSession, WindowManagerService windowManagerService) {
        DisplayContent displayContent;
        if (contentRecordingSession == null || (ContentRecordingSession.isValid(contentRecordingSession) && !ContentRecordingSession.isSameDisplay(this.mSession, contentRecordingSession))) {
            if (contentRecordingSession != null) {
                if (ProtoLogCache.WM_DEBUG_CONTENT_RECORDING_enabled) {
                    long displayId = contentRecordingSession.getDisplayId();
                    ContentRecordingSession contentRecordingSession2 = this.mSession;
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONTENT_RECORDING, 1401287081, 1, (String) null, new Object[]{Long.valueOf(displayId), String.valueOf(contentRecordingSession2 == null ? null : Integer.valueOf(contentRecordingSession2.getDisplayId()))});
                }
                displayContent = windowManagerService.mRoot.getDisplayContentOrCreate(contentRecordingSession.getDisplayId());
                displayContent.setContentRecordingSession(contentRecordingSession);
            } else {
                displayContent = null;
            }
            if (this.mSession != null) {
                if (ProtoLogCache.WM_DEBUG_CONTENT_RECORDING_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONTENT_RECORDING, -237664290, 0, (String) null, new Object[]{String.valueOf(this.mDisplayContent.getDisplayId())});
                }
                this.mDisplayContent.pauseRecording();
                this.mDisplayContent.setContentRecordingSession(null);
            }
            this.mDisplayContent = displayContent;
            this.mSession = contentRecordingSession;
        }
    }
}
