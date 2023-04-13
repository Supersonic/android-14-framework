package android.view;
/* loaded from: classes4.dex */
public class InputEventAssigner {
    private static final String TAG = "InputEventAssigner";
    private boolean mHasUnprocessedDown = false;
    private int mDownEventId = 0;

    public void notifyFrameProcessed() {
        this.mHasUnprocessedDown = false;
    }

    public int processEvent(InputEvent event) {
        if (event instanceof MotionEvent) {
            MotionEvent motionEvent = (MotionEvent) event;
            if (motionEvent.isFromSource(4098)) {
                int action = motionEvent.getActionMasked();
                if (action == 0) {
                    this.mHasUnprocessedDown = true;
                    this.mDownEventId = event.getId();
                }
                if (this.mHasUnprocessedDown && action == 2) {
                    return this.mDownEventId;
                }
                if (action == 3 || action == 1) {
                    this.mHasUnprocessedDown = false;
                }
            }
        }
        return event.getId();
    }
}
