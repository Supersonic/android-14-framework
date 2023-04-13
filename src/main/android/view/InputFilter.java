package android.view;

import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.view.IInputFilter;
/* loaded from: classes4.dex */
public abstract class InputFilter extends IInputFilter.Stub {
    private static final int MSG_INPUT_EVENT = 3;
    private static final int MSG_INSTALL = 1;
    private static final int MSG_UNINSTALL = 2;

    /* renamed from: mH */
    private final HandlerC3472H f481mH;
    private IInputFilterHost mHost;
    private final InputEventConsistencyVerifier mInboundInputEventConsistencyVerifier;
    private final InputEventConsistencyVerifier mOutboundInputEventConsistencyVerifier;

    public InputFilter(Looper looper) {
        InputEventConsistencyVerifier inputEventConsistencyVerifier;
        if (InputEventConsistencyVerifier.isInstrumentationEnabled()) {
            inputEventConsistencyVerifier = new InputEventConsistencyVerifier(this, 1, "InputFilter#InboundInputEventConsistencyVerifier");
        } else {
            inputEventConsistencyVerifier = null;
        }
        this.mInboundInputEventConsistencyVerifier = inputEventConsistencyVerifier;
        this.mOutboundInputEventConsistencyVerifier = InputEventConsistencyVerifier.isInstrumentationEnabled() ? new InputEventConsistencyVerifier(this, 1, "InputFilter#OutboundInputEventConsistencyVerifier") : null;
        this.f481mH = new HandlerC3472H(looper);
    }

    @Override // android.view.IInputFilter
    public final void install(IInputFilterHost host) {
        this.f481mH.obtainMessage(1, host).sendToTarget();
    }

    @Override // android.view.IInputFilter
    public final void uninstall() {
        this.f481mH.obtainMessage(2).sendToTarget();
    }

    @Override // android.view.IInputFilter
    public final void filterInputEvent(InputEvent event, int policyFlags) {
        this.f481mH.obtainMessage(3, policyFlags, 0, event).sendToTarget();
    }

    public void sendInputEvent(InputEvent event, int policyFlags) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        if (this.mHost == null) {
            throw new IllegalStateException("Cannot send input event because the input filter is not installed.");
        }
        InputEventConsistencyVerifier inputEventConsistencyVerifier = this.mOutboundInputEventConsistencyVerifier;
        if (inputEventConsistencyVerifier != null) {
            inputEventConsistencyVerifier.onInputEvent(event, 0);
        }
        try {
            this.mHost.sendInputEvent(event, policyFlags);
        } catch (RemoteException e) {
        }
    }

    public void onInputEvent(InputEvent event, int policyFlags) {
        sendInputEvent(event, policyFlags);
    }

    public void onInstalled() {
    }

    public void onUninstalled() {
    }

    /* renamed from: android.view.InputFilter$H */
    /* loaded from: classes4.dex */
    private final class HandlerC3472H extends Handler {
        public HandlerC3472H(Looper looper) {
            super(looper);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    InputFilter.this.mHost = (IInputFilterHost) msg.obj;
                    if (InputFilter.this.mInboundInputEventConsistencyVerifier != null) {
                        InputFilter.this.mInboundInputEventConsistencyVerifier.reset();
                    }
                    if (InputFilter.this.mOutboundInputEventConsistencyVerifier != null) {
                        InputFilter.this.mOutboundInputEventConsistencyVerifier.reset();
                    }
                    InputFilter.this.onInstalled();
                    return;
                case 2:
                    try {
                        InputFilter.this.onUninstalled();
                        return;
                    } finally {
                        InputFilter.this.mHost = null;
                    }
                case 3:
                    InputEvent event = (InputEvent) msg.obj;
                    try {
                        if (InputFilter.this.mInboundInputEventConsistencyVerifier != null) {
                            InputFilter.this.mInboundInputEventConsistencyVerifier.onInputEvent(event, 0);
                        }
                        InputFilter.this.onInputEvent(event, msg.arg1);
                        return;
                    } finally {
                        event.recycle();
                    }
                default:
                    return;
            }
        }
    }
}
