package com.android.commands.monkey;
/* loaded from: classes.dex */
public class MonkeyTrackballEvent extends MonkeyMotionEvent {
    public MonkeyTrackballEvent(int action) {
        super(2, 65540, action);
    }

    @Override // com.android.commands.monkey.MonkeyMotionEvent
    protected String getTypeLabel() {
        return "Trackball";
    }
}
