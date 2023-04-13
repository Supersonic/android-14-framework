package android.hardware.location;

import android.annotation.SystemApi;
import android.content.Intent;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import java.util.Objects;
@SystemApi
/* loaded from: classes2.dex */
public class ContextHubIntentEvent {
    private final int mClientAuthorizationState;
    private final ContextHubInfo mContextHubInfo;
    private final int mEventType;
    private final int mNanoAppAbortCode;
    private final long mNanoAppId;
    private final NanoAppMessage mNanoAppMessage;

    private ContextHubIntentEvent(ContextHubInfo contextHubInfo, int eventType, long nanoAppId, NanoAppMessage nanoAppMessage, int nanoAppAbortCode, int clientAuthorizationState) {
        this.mContextHubInfo = contextHubInfo;
        this.mEventType = eventType;
        this.mNanoAppId = nanoAppId;
        this.mNanoAppMessage = nanoAppMessage;
        this.mNanoAppAbortCode = nanoAppAbortCode;
        this.mClientAuthorizationState = clientAuthorizationState;
    }

    private ContextHubIntentEvent(ContextHubInfo contextHubInfo, int eventType) {
        this(contextHubInfo, eventType, -1L, null, -1, 0);
    }

    private ContextHubIntentEvent(ContextHubInfo contextHubInfo, int eventType, long nanoAppId) {
        this(contextHubInfo, eventType, nanoAppId, null, -1, 0);
    }

    private ContextHubIntentEvent(ContextHubInfo contextHubInfo, int eventType, long nanoAppId, NanoAppMessage nanoAppMessage) {
        this(contextHubInfo, eventType, nanoAppId, nanoAppMessage, -1, 0);
    }

    private ContextHubIntentEvent(ContextHubInfo contextHubInfo, int eventType, long nanoAppId, int nanoAppAbortCode) {
        this(contextHubInfo, eventType, nanoAppId, null, nanoAppAbortCode, 0);
    }

    public static ContextHubIntentEvent fromIntent(Intent intent) {
        Objects.requireNonNull(intent, "Intent cannot be null");
        hasExtraOrThrow(intent, ContextHubManager.EXTRA_CONTEXT_HUB_INFO);
        ContextHubInfo info = (ContextHubInfo) intent.getParcelableExtra(ContextHubManager.EXTRA_CONTEXT_HUB_INFO, ContextHubInfo.class);
        if (info == null) {
            throw new IllegalArgumentException("ContextHubInfo extra was null");
        }
        int eventType = getIntExtraOrThrow(intent, ContextHubManager.EXTRA_EVENT_TYPE);
        switch (eventType) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 7:
                long nanoAppId = getLongExtraOrThrow(intent, ContextHubManager.EXTRA_NANOAPP_ID);
                if (eventType == 5) {
                    hasExtraOrThrow(intent, ContextHubManager.EXTRA_MESSAGE);
                    NanoAppMessage message = (NanoAppMessage) intent.getParcelableExtra(ContextHubManager.EXTRA_MESSAGE, NanoAppMessage.class);
                    if (message == null) {
                        throw new IllegalArgumentException("NanoAppMessage extra was null");
                    }
                    ContextHubIntentEvent event = new ContextHubIntentEvent(info, eventType, nanoAppId, message);
                    return event;
                } else if (eventType == 4) {
                    int nanoAppAbortCode = getIntExtraOrThrow(intent, ContextHubManager.EXTRA_NANOAPP_ABORT_CODE);
                    ContextHubIntentEvent event2 = new ContextHubIntentEvent(info, eventType, nanoAppId, nanoAppAbortCode);
                    return event2;
                } else if (eventType == 7) {
                    int authState = getIntExtraOrThrow(intent, ContextHubManager.EXTRA_CLIENT_AUTHORIZATION_STATE);
                    ContextHubIntentEvent event3 = new ContextHubIntentEvent(info, eventType, nanoAppId, null, -1, authState);
                    return event3;
                } else {
                    ContextHubIntentEvent event4 = new ContextHubIntentEvent(info, eventType, nanoAppId);
                    return event4;
                }
            case 6:
                ContextHubIntentEvent event5 = new ContextHubIntentEvent(info, eventType);
                return event5;
            default:
                throw new IllegalArgumentException("Unknown intent event type " + eventType);
        }
    }

    public int getEventType() {
        return this.mEventType;
    }

    public ContextHubInfo getContextHubInfo() {
        return this.mContextHubInfo;
    }

    public long getNanoAppId() {
        if (this.mEventType == 6) {
            throw new UnsupportedOperationException("Cannot invoke getNanoAppId() on Context Hub reset event");
        }
        return this.mNanoAppId;
    }

    public int getNanoAppAbortCode() {
        if (this.mEventType != 4) {
            throw new UnsupportedOperationException("Cannot invoke getNanoAppAbortCode() on non-abort event: " + this.mEventType);
        }
        return this.mNanoAppAbortCode;
    }

    public NanoAppMessage getNanoAppMessage() {
        if (this.mEventType != 5) {
            throw new UnsupportedOperationException("Cannot invoke getNanoAppMessage() on non-message event: " + this.mEventType);
        }
        return this.mNanoAppMessage;
    }

    public int getClientAuthorizationState() {
        if (this.mEventType != 7) {
            throw new UnsupportedOperationException("Cannot invoke getClientAuthorizationState() on non-authorization event: " + this.mEventType);
        }
        return this.mClientAuthorizationState;
    }

    public String toString() {
        String out = "ContextHubIntentEvent[eventType = " + this.mEventType + ", contextHubId = " + this.mContextHubInfo.getId();
        if (this.mEventType != 6) {
            out = out + ", nanoAppId = 0x" + Long.toHexString(this.mNanoAppId);
        }
        if (this.mEventType == 4) {
            out = out + ", nanoAppAbortCode = " + this.mNanoAppAbortCode;
        }
        if (this.mEventType == 5) {
            out = out + ", nanoAppMessage = " + this.mNanoAppMessage;
        }
        if (this.mEventType == 7) {
            out = out + ", clientAuthState = " + this.mClientAuthorizationState;
        }
        return out + NavigationBarInflaterView.SIZE_MOD_END;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof ContextHubIntentEvent)) {
            return false;
        }
        ContextHubIntentEvent other = (ContextHubIntentEvent) object;
        if (other.getEventType() != this.mEventType || !other.getContextHubInfo().equals(this.mContextHubInfo)) {
            return false;
        }
        boolean isEqual = true;
        try {
            if (this.mEventType != 6) {
                isEqual = true & (other.getNanoAppId() == this.mNanoAppId);
            }
            if (this.mEventType == 4) {
                isEqual &= other.getNanoAppAbortCode() == this.mNanoAppAbortCode;
            }
            if (this.mEventType == 5) {
                isEqual &= other.getNanoAppMessage().equals(this.mNanoAppMessage);
            }
            if (this.mEventType == 7) {
                return (other.getClientAuthorizationState() == this.mClientAuthorizationState) & isEqual;
            }
            return isEqual;
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }

    private static void hasExtraOrThrow(Intent intent, String extra) {
        if (!intent.hasExtra(extra)) {
            throw new IllegalArgumentException("Intent did not have extra: " + extra);
        }
    }

    private static int getIntExtraOrThrow(Intent intent, String extra) {
        hasExtraOrThrow(intent, extra);
        return intent.getIntExtra(extra, -1);
    }

    private static long getLongExtraOrThrow(Intent intent, String extra) {
        hasExtraOrThrow(intent, extra);
        return intent.getLongExtra(extra, -1L);
    }
}
