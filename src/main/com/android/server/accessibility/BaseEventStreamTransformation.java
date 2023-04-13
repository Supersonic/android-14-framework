package com.android.server.accessibility;
/* loaded from: classes.dex */
public abstract class BaseEventStreamTransformation implements EventStreamTransformation {
    public EventStreamTransformation mNext;

    @Override // com.android.server.accessibility.EventStreamTransformation
    public void setNext(EventStreamTransformation eventStreamTransformation) {
        this.mNext = eventStreamTransformation;
    }

    @Override // com.android.server.accessibility.EventStreamTransformation
    public EventStreamTransformation getNext() {
        return this.mNext;
    }
}
