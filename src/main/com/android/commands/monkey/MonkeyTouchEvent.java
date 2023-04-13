package com.android.commands.monkey;
/* loaded from: classes.dex */
public class MonkeyTouchEvent extends MonkeyMotionEvent {
    public MonkeyTouchEvent(int action) {
        super(1, 4098, action);
    }

    @Override // com.android.commands.monkey.MonkeyMotionEvent
    protected String getTypeLabel() {
        return "Touch";
    }
}
