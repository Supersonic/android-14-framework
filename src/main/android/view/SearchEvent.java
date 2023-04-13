package android.view;
/* loaded from: classes4.dex */
public class SearchEvent {
    private InputDevice mInputDevice;

    public SearchEvent(InputDevice inputDevice) {
        this.mInputDevice = inputDevice;
    }

    public InputDevice getInputDevice() {
        return this.mInputDevice;
    }
}
