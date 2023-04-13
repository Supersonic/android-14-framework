package android.filterfw.core;

import java.lang.reflect.Field;
/* loaded from: classes.dex */
public class ProgramPort extends FieldPort {
    protected String mVarName;

    public ProgramPort(Filter filter, String name, String varName, Field field, boolean hasDefault) {
        super(filter, name, field, hasDefault);
        this.mVarName = varName;
    }

    @Override // android.filterfw.core.FieldPort, android.filterfw.core.FilterPort
    public String toString() {
        return "Program " + super.toString();
    }

    @Override // android.filterfw.core.FieldPort, android.filterfw.core.InputPort
    public synchronized void transfer(FilterContext context) {
        if (this.mValueWaiting) {
            try {
                Object fieldValue = this.mField.get(this.mFilter);
                if (fieldValue != null) {
                    Program program = (Program) fieldValue;
                    program.setHostValue(this.mVarName, this.mValue);
                    this.mValueWaiting = false;
                }
            } catch (ClassCastException e) {
                throw new RuntimeException("Non Program field '" + this.mField.getName() + "' annotated with ProgramParameter!");
            } catch (IllegalAccessException e2) {
                throw new RuntimeException("Access to program field '" + this.mField.getName() + "' was denied!");
            }
        }
    }
}
