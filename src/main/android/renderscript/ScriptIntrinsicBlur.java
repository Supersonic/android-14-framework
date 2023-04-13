package android.renderscript;

import android.renderscript.Script;
@Deprecated
/* loaded from: classes3.dex */
public final class ScriptIntrinsicBlur extends ScriptIntrinsic {
    private Allocation mInput;
    private final float[] mValues;

    private ScriptIntrinsicBlur(long id, RenderScript rs) {
        super(id, rs);
        this.mValues = new float[9];
    }

    public static ScriptIntrinsicBlur create(RenderScript rs, Element e) {
        if (!e.isCompatible(Element.U8_4(rs)) && !e.isCompatible(Element.m142U8(rs))) {
            throw new RSIllegalArgumentException("Unsupported element type.");
        }
        long id = rs.nScriptIntrinsicCreate(5, e.getID(rs));
        ScriptIntrinsicBlur sib = new ScriptIntrinsicBlur(id, rs);
        sib.setRadius(5.0f);
        return sib;
    }

    public void setInput(Allocation ain) {
        if (ain.getType().getY() == 0) {
            throw new RSIllegalArgumentException("Input set to a 1D Allocation");
        }
        Element e = ain.getElement();
        if (!e.isCompatible(Element.U8_4(this.mRS)) && !e.isCompatible(Element.m142U8(this.mRS))) {
            throw new RSIllegalArgumentException("Unsupported element type.");
        }
        this.mInput = ain;
        setVar(1, ain);
    }

    public void setRadius(float radius) {
        if (radius <= 0.0f || radius > 25.0f) {
            throw new RSIllegalArgumentException("Radius out of range (0 < r <= 25).");
        }
        setVar(0, radius);
    }

    public void forEach(Allocation aout) {
        if (aout.getType().getY() == 0) {
            throw new RSIllegalArgumentException("Output is a 1D Allocation");
        }
        forEach(0, (Allocation) null, aout, (FieldPacker) null);
    }

    public void forEach(Allocation aout, Script.LaunchOptions opt) {
        if (aout.getType().getY() == 0) {
            throw new RSIllegalArgumentException("Output is a 1D Allocation");
        }
        forEach(0, (Allocation) null, aout, (FieldPacker) null, opt);
    }

    public Script.KernelID getKernelID() {
        return createKernelID(0, 2, null, null);
    }

    public Script.FieldID getFieldID_Input() {
        return createFieldID(1, null);
    }
}
