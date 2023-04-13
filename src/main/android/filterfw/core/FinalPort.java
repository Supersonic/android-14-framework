package android.filterfw.core;

import java.lang.reflect.Field;
/* loaded from: classes.dex */
public class FinalPort extends FieldPort {
    public FinalPort(Filter filter, String name, Field field, boolean hasDefault) {
        super(filter, name, field, hasDefault);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.filterfw.core.FieldPort
    public synchronized void setFieldFrame(Frame frame, boolean isAssignment) {
        assertPortIsOpen();
        checkFrameType(frame, isAssignment);
        if (this.mFilter.getStatus() != 0) {
            throw new RuntimeException("Attempting to modify " + this + "!");
        }
        super.setFieldFrame(frame, isAssignment);
        super.transfer(null);
    }

    @Override // android.filterfw.core.FieldPort, android.filterfw.core.FilterPort
    public String toString() {
        return "final " + super.toString();
    }
}
