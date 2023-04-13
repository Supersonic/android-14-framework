package android.renderscript;

import android.renderscript.Script;
@Deprecated
/* loaded from: classes3.dex */
public final class ScriptIntrinsic3DLUT extends ScriptIntrinsic {
    private Element mElement;
    private Allocation mLUT;

    private ScriptIntrinsic3DLUT(long id, RenderScript rs, Element e) {
        super(id, rs);
        this.mElement = e;
    }

    public static ScriptIntrinsic3DLUT create(RenderScript rs, Element e) {
        long id = rs.nScriptIntrinsicCreate(8, e.getID(rs));
        if (!e.isCompatible(Element.U8_4(rs))) {
            throw new RSIllegalArgumentException("Element must be compatible with uchar4.");
        }
        return new ScriptIntrinsic3DLUT(id, rs, e);
    }

    public void setLUT(Allocation lut) {
        Type t = lut.getType();
        if (t.getZ() == 0) {
            throw new RSIllegalArgumentException("LUT must be 3d.");
        }
        if (!t.getElement().isCompatible(this.mElement)) {
            throw new RSIllegalArgumentException("LUT element type must match.");
        }
        this.mLUT = lut;
        setVar(0, lut);
    }

    public void forEach(Allocation ain, Allocation aout) {
        forEach(ain, aout, null);
    }

    public void forEach(Allocation ain, Allocation aout, Script.LaunchOptions opt) {
        forEach(0, ain, aout, (FieldPacker) null, opt);
    }

    public Script.KernelID getKernelID() {
        return createKernelID(0, 3, null, null);
    }
}
