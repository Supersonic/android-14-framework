package com.android.internal.app;

import android.content.Intent;
import com.android.internal.logging.InstanceId;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.logging.nano.MetricsProto;
/* loaded from: classes4.dex */
public interface ChooserActivityLogger {
    InstanceId getInstanceId();

    void log(UiEventLogger.UiEventEnum uiEventEnum, InstanceId instanceId);

    void logShareStarted(int i, String str, String str2, int i2, int i3, boolean z, int i4, String str3);

    void logShareTargetSelected(int i, String str, int i2, boolean z);

    default void logSharesheetTriggered() {
        log(SharesheetStandardEvent.SHARESHEET_TRIGGERED, getInstanceId());
    }

    default void logSharesheetAppLoadComplete() {
        log(SharesheetStandardEvent.SHARESHEET_APP_LOAD_COMPLETE, getInstanceId());
    }

    default void logSharesheetDirectLoadComplete() {
        log(SharesheetStandardEvent.SHARESHEET_DIRECT_LOAD_COMPLETE, getInstanceId());
    }

    default void logSharesheetDirectLoadTimeout() {
        log(SharesheetStandardEvent.SHARESHEET_DIRECT_LOAD_TIMEOUT, getInstanceId());
    }

    default void logShareheetProfileChanged() {
        log(SharesheetStandardEvent.SHARESHEET_PROFILE_CHANGED, getInstanceId());
    }

    default void logSharesheetExpansionChanged(boolean isCollapsed) {
        log(isCollapsed ? SharesheetStandardEvent.SHARESHEET_COLLAPSED : SharesheetStandardEvent.SHARESHEET_EXPANDED, getInstanceId());
    }

    default void logSharesheetAppShareRankingTimeout() {
        log(SharesheetStandardEvent.SHARESHEET_APP_SHARE_RANKING_TIMEOUT, getInstanceId());
    }

    default void logSharesheetEmptyDirectShareRow() {
        log(SharesheetStandardEvent.SHARESHEET_EMPTY_DIRECT_SHARE_ROW, getInstanceId());
    }

    /* loaded from: classes4.dex */
    public enum SharesheetStartedEvent implements UiEventLogger.UiEventEnum {
        SHARE_STARTED(228);
        
        private final int mId;

        SharesheetStartedEvent(int id) {
            this.mId = id;
        }

        @Override // com.android.internal.logging.UiEventLogger.UiEventEnum
        public int getId() {
            return this.mId;
        }
    }

    /* loaded from: classes4.dex */
    public enum SharesheetTargetSelectedEvent implements UiEventLogger.UiEventEnum {
        INVALID(0),
        SHARESHEET_SERVICE_TARGET_SELECTED(232),
        SHARESHEET_APP_TARGET_SELECTED(233),
        SHARESHEET_STANDARD_TARGET_SELECTED(234),
        SHARESHEET_COPY_TARGET_SELECTED(235),
        SHARESHEET_NEARBY_TARGET_SELECTED(MetricsProto.MetricsEvent.PROVISIONING_COPY_ACCOUNT_STATUS),
        SHARESHEET_EDIT_TARGET_SELECTED(MetricsProto.MetricsEvent.ACTION_PERMISSION_REVOKE_RECORD_AUDIO);
        
        private final int mId;

        SharesheetTargetSelectedEvent(int id) {
            this.mId = id;
        }

        @Override // com.android.internal.logging.UiEventLogger.UiEventEnum
        public int getId() {
            return this.mId;
        }

        public static SharesheetTargetSelectedEvent fromTargetType(int targetType) {
            switch (targetType) {
                case 1:
                    return SHARESHEET_SERVICE_TARGET_SELECTED;
                case 2:
                    return SHARESHEET_APP_TARGET_SELECTED;
                case 3:
                    return SHARESHEET_STANDARD_TARGET_SELECTED;
                case 4:
                    return SHARESHEET_COPY_TARGET_SELECTED;
                case 5:
                    return SHARESHEET_NEARBY_TARGET_SELECTED;
                case 6:
                    return SHARESHEET_EDIT_TARGET_SELECTED;
                default:
                    return INVALID;
            }
        }
    }

    /* loaded from: classes4.dex */
    public enum SharesheetStandardEvent implements UiEventLogger.UiEventEnum {
        INVALID(0),
        SHARESHEET_TRIGGERED(227),
        SHARESHEET_PROFILE_CHANGED(229),
        SHARESHEET_EXPANDED(230),
        SHARESHEET_COLLAPSED(231),
        SHARESHEET_APP_LOAD_COMPLETE(322),
        SHARESHEET_DIRECT_LOAD_COMPLETE(323),
        SHARESHEET_DIRECT_LOAD_TIMEOUT(324),
        SHARESHEET_APP_SHARE_RANKING_TIMEOUT(MetricsProto.MetricsEvent.NOTIFICATION_SNOOZED),
        SHARESHEET_EMPTY_DIRECT_SHARE_ROW(MetricsProto.MetricsEvent.CARRIER_DEMO_MODE_PASSWORD);
        
        private final int mId;

        SharesheetStandardEvent(int id) {
            this.mId = id;
        }

        @Override // com.android.internal.logging.UiEventLogger.UiEventEnum
        public int getId() {
            return this.mId;
        }
    }

    default int typeFromPreviewInt(int previewType) {
        switch (previewType) {
            case 1:
                return 1;
            case 2:
                return 2;
            default:
                return 0;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    default int typeFromIntentString(String intent) {
        char c;
        if (intent == null) {
            return 0;
        }
        switch (intent.hashCode()) {
            case -1960745709:
                if (intent.equals("android.media.action.IMAGE_CAPTURE")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -1173683121:
                if (intent.equals(Intent.ACTION_EDIT)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -1173447682:
                if (intent.equals(Intent.ACTION_MAIN)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -1173264947:
                if (intent.equals(Intent.ACTION_SEND)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -1173171990:
                if (intent.equals("android.intent.action.VIEW")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -58484670:
                if (intent.equals(Intent.ACTION_SEND_MULTIPLE)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 2068787464:
                if (intent.equals(Intent.ACTION_SENDTO)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 4;
            case 4:
                return 5;
            case 5:
                return 6;
            case 6:
                return 7;
            default:
                return 0;
        }
    }
}
