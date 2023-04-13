package android.hardware.devicestate;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public final class DeviceStateRequest {
    public static final int FLAG_CANCEL_WHEN_BASE_CHANGES = 1;
    private final int mFlags;
    private final int mRequestedState;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface RequestFlags {
    }

    public static Builder newBuilder(int requestedState) {
        return new Builder(requestedState);
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private int mFlags;
        private final int mRequestedState;

        private Builder(int requestedState) {
            this.mRequestedState = requestedState;
        }

        public Builder setFlags(int flags) {
            this.mFlags |= flags;
            return this;
        }

        public DeviceStateRequest build() {
            return new DeviceStateRequest(this.mRequestedState, this.mFlags);
        }
    }

    /* loaded from: classes.dex */
    public interface Callback {
        default void onRequestActivated(DeviceStateRequest request) {
        }

        default void onRequestSuspended(DeviceStateRequest request) {
        }

        default void onRequestCanceled(DeviceStateRequest request) {
        }
    }

    private DeviceStateRequest(int requestedState, int flags) {
        this.mRequestedState = requestedState;
        this.mFlags = flags;
    }

    public int getState() {
        return this.mRequestedState;
    }

    public int getFlags() {
        return this.mFlags;
    }
}
