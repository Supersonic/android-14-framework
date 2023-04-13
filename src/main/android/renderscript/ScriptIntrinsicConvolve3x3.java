package android.renderscript;

import android.renderscript.Script;
@Deprecated
/* loaded from: classes3.dex */
public final class ScriptIntrinsicConvolve3x3 extends ScriptIntrinsic {
    private Allocation mInput;
    private final float[] mValues;

    private ScriptIntrinsicConvolve3x3(long id, RenderScript rs) {
        super(id, rs);
        this.mValues = new float[9];
    }

    public static ScriptIntrinsicConvolve3x3 create(RenderScript rs, Element e) {
        float[] f = {0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        if (!e.isCompatible(Element.m142U8(rs)) && !e.isCompatible(Element.U8_2(rs)) && !e.isCompatible(Element.U8_3(rs)) && !e.isCompatible(Element.U8_4(rs)) && !e.isCompatible(Element.F32(rs)) && !e.isCompatible(Element.F32_2(rs)) && !e.isCompatible(Element.F32_3(rs)) && !e.isCompatible(Element.F32_4(rs))) {
            throw new RSIllegalArgumentException("Unsupported element type.");
        }
        long id = rs.nScriptIntrinsicCreate(1, e.getID(rs));
        ScriptIntrinsicConvolve3x3 si = new ScriptIntrinsicConvolve3x3(id, rs);
        si.setCoefficients(f);
        return si;
    }

    public void setInput(Allocation ain) {
        this.mInput = ain;
        setVar(1, ain);
    }

    public void setCoefficients(float[] v) {
        FieldPacker fp = new FieldPacker(36);
        int ct = 0;
        while (true) {
            float[] fArr = this.mValues;
            if (ct < fArr.length) {
                float f = v[ct];
                fArr[ct] = f;
                fp.addF32(f);
                ct++;
            } else {
                setVar(0, fp);
                return;
            }
        }
    }

    public void forEach(Allocation aout) {
        forEach(0, (Allocation) null, aout, (FieldPacker) null);
    }

    public void forEach(Allocation aout, Script.LaunchOptions opt) {
        forEach(0, (Allocation) null, aout, (FieldPacker) null, opt);
    }

    public Script.KernelID getKernelID() {
        return createKernelID(0, 2, null, null);
    }

    public Script.FieldID getFieldID_Input() {
        return createFieldID(1, null);
    }
}
