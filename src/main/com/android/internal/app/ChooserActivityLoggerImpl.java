package com.android.internal.app;

import com.android.internal.app.ChooserActivityLogger;
import com.android.internal.logging.InstanceId;
import com.android.internal.logging.InstanceIdSequence;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.logging.UiEventLoggerImpl;
import com.android.internal.util.FrameworkStatsLog;
/* loaded from: classes4.dex */
public class ChooserActivityLoggerImpl implements ChooserActivityLogger {
    private static final int SHARESHEET_INSTANCE_ID_MAX = 8192;
    private static InstanceIdSequence sInstanceIdSequence;
    private InstanceId mInstanceId;
    private UiEventLogger mUiEventLogger = new UiEventLoggerImpl();

    @Override // com.android.internal.app.ChooserActivityLogger
    public void logShareStarted(int eventId, String packageName, String mimeType, int appProvidedDirect, int appProvidedApp, boolean isWorkprofile, int previewType, String intent) {
        FrameworkStatsLog.write(259, ChooserActivityLogger.SharesheetStartedEvent.SHARE_STARTED.getId(), packageName, getInstanceId().getId(), mimeType, appProvidedDirect, appProvidedApp, isWorkprofile, typeFromPreviewInt(previewType), typeFromIntentString(intent), 0, false);
    }

    @Override // com.android.internal.app.ChooserActivityLogger
    public void logShareTargetSelected(int targetType, String packageName, int positionPicked, boolean isPinned) {
        FrameworkStatsLog.write(260, ChooserActivityLogger.SharesheetTargetSelectedEvent.fromTargetType(targetType).getId(), packageName, getInstanceId().getId(), positionPicked, isPinned);
    }

    @Override // com.android.internal.app.ChooserActivityLogger
    public void log(UiEventLogger.UiEventEnum event, InstanceId instanceId) {
        this.mUiEventLogger.logWithInstanceId(event, 0, null, instanceId);
    }

    @Override // com.android.internal.app.ChooserActivityLogger
    public InstanceId getInstanceId() {
        if (this.mInstanceId == null) {
            if (sInstanceIdSequence == null) {
                sInstanceIdSequence = new InstanceIdSequence(8192);
            }
            this.mInstanceId = sInstanceIdSequence.newInstanceId();
        }
        return this.mInstanceId;
    }
}
