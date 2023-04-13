package android.renderscript;

import android.renderscript.Script;
@Deprecated
/* loaded from: classes3.dex */
public final class ScriptIntrinsicResize extends ScriptIntrinsic {
    private Allocation mInput;

    private ScriptIntrinsicResize(long id, RenderScript rs) {
        super(id, rs);
    }

    public static ScriptIntrinsicResize create(RenderScript rs) {
        long id = rs.nScriptIntrinsicCreate(12, 0L);
        ScriptIntrinsicResize si = new ScriptIntrinsicResize(id, rs);
        return si;
    }

    public void setInput(Allocation ain) {
        Element e = ain.getElement();
        if (!e.isCompatible(Element.m142U8(this.mRS)) && !e.isCompatible(Element.U8_2(this.mRS)) && !e.isCompatible(Element.U8_3(this.mRS)) && !e.isCompatible(Element.U8_4(this.mRS)) && !e.isCompatible(Element.F32(this.mRS)) && !e.isCompatible(Element.F32_2(this.mRS)) && !e.isCompatible(Element.F32_3(this.mRS)) && !e.isCompatible(Element.F32_4(this.mRS))) {
            throw new RSIllegalArgumentException("Unsupported element type.");
        }
        this.mInput = ain;
        setVar(0, ain);
    }

    public Script.FieldID getFieldID_Input() {
        return createFieldID(0, null);
    }

    public void forEach_bicubic(Allocation aout) {
        if (aout == this.mInput) {
            throw new RSIllegalArgumentException("Output cannot be same as Input.");
        }
        forEach_bicubic(aout, null);
    }

    public void forEach_bicubic(Allocation aout, Script.LaunchOptions opt) {
        forEach(0, (Allocation) null, aout, (FieldPacker) null, opt);
    }

    public Script.KernelID getKernelID_bicubic() {
        return createKernelID(0, 2, null, null);
    }
}
