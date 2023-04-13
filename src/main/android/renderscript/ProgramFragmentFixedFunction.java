package android.renderscript;

import android.renderscript.Element;
import android.renderscript.Program;
import android.renderscript.Type;
@Deprecated
/* loaded from: classes3.dex */
public class ProgramFragmentFixedFunction extends ProgramFragment {
    ProgramFragmentFixedFunction(long id, RenderScript rs) {
        super(id, rs);
    }

    /* loaded from: classes3.dex */
    static class InternalBuilder extends Program.BaseProgramBuilder {
        public InternalBuilder(RenderScript rs) {
            super(rs);
        }

        public ProgramFragmentFixedFunction create() {
            this.mRS.validate();
            long[] tmp = new long[(this.mInputCount + this.mOutputCount + this.mConstantCount + this.mTextureCount) * 2];
            String[] texNames = new String[this.mTextureCount];
            int idx = 0;
            for (int i = 0; i < this.mInputCount; i++) {
                int idx2 = idx + 1;
                tmp[idx] = Program.ProgramParam.INPUT.mID;
                idx = idx2 + 1;
                tmp[idx2] = this.mInputs[i].getID(this.mRS);
            }
            for (int i2 = 0; i2 < this.mOutputCount; i2++) {
                int idx3 = idx + 1;
                tmp[idx] = Program.ProgramParam.OUTPUT.mID;
                idx = idx3 + 1;
                tmp[idx3] = this.mOutputs[i2].getID(this.mRS);
            }
            for (int i3 = 0; i3 < this.mConstantCount; i3++) {
                int idx4 = idx + 1;
                tmp[idx] = Program.ProgramParam.CONSTANT.mID;
                idx = idx4 + 1;
                tmp[idx4] = this.mConstants[i3].getID(this.mRS);
            }
            for (int i4 = 0; i4 < this.mTextureCount; i4++) {
                int idx5 = idx + 1;
                tmp[idx] = Program.ProgramParam.TEXTURE_TYPE.mID;
                idx = idx5 + 1;
                tmp[idx5] = this.mTextureTypes[i4].mID;
                texNames[i4] = this.mTextureNames[i4];
            }
            long id = this.mRS.nProgramFragmentCreate(this.mShader, texNames, tmp);
            ProgramFragmentFixedFunction pf = new ProgramFragmentFixedFunction(id, this.mRS);
            initProgram(pf);
            return pf;
        }
    }

    /* loaded from: classes3.dex */
    public static class Builder {
        public static final int MAX_TEXTURE = 2;
        int mNumTextures;
        RenderScript mRS;
        String mShader;
        boolean mVaryingColorEnable;
        Slot[] mSlots = new Slot[2];
        boolean mPointSpriteEnable = false;

        /* loaded from: classes3.dex */
        public enum EnvMode {
            REPLACE(1),
            MODULATE(2),
            DECAL(3);
            
            int mID;

            EnvMode(int id) {
                this.mID = id;
            }
        }

        /* loaded from: classes3.dex */
        public enum Format {
            ALPHA(1),
            LUMINANCE_ALPHA(2),
            RGB(3),
            RGBA(4);
            
            int mID;

            Format(int id) {
                this.mID = id;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public class Slot {
            EnvMode env;
            Format format;

            Slot(EnvMode _env, Format _fmt) {
                this.env = _env;
                this.format = _fmt;
            }
        }

        private void buildShaderString() {
            this.mShader = "//rs_shader_internal\n";
            this.mShader += "varying lowp vec4 varColor;\n";
            this.mShader += "varying vec2 varTex0;\n";
            this.mShader += "void main() {\n";
            if (this.mVaryingColorEnable) {
                this.mShader += "  lowp vec4 col = varColor;\n";
            } else {
                this.mShader += "  lowp vec4 col = UNI_Color;\n";
            }
            if (this.mNumTextures != 0) {
                if (this.mPointSpriteEnable) {
                    this.mShader += "  vec2 t0 = gl_PointCoord;\n";
                } else {
                    this.mShader += "  vec2 t0 = varTex0.xy;\n";
                }
            }
            for (int i = 0; i < this.mNumTextures; i++) {
                switch (C23701.f401x21e46749[this.mSlots[i].env.ordinal()]) {
                    case 1:
                        switch (C23701.f402xa802c1be[this.mSlots[i].format.ordinal()]) {
                            case 1:
                                this.mShader += "  col.a = texture2D(UNI_Tex0, t0).a;\n";
                                continue;
                            case 2:
                                this.mShader += "  col.rgba = texture2D(UNI_Tex0, t0).rgba;\n";
                                continue;
                            case 3:
                                this.mShader += "  col.rgb = texture2D(UNI_Tex0, t0).rgb;\n";
                                continue;
                            case 4:
                                this.mShader += "  col.rgba = texture2D(UNI_Tex0, t0).rgba;\n";
                                continue;
                        }
                    case 2:
                        switch (C23701.f402xa802c1be[this.mSlots[i].format.ordinal()]) {
                            case 1:
                                this.mShader += "  col.a *= texture2D(UNI_Tex0, t0).a;\n";
                                continue;
                            case 2:
                                this.mShader += "  col.rgba *= texture2D(UNI_Tex0, t0).rgba;\n";
                                continue;
                            case 3:
                                this.mShader += "  col.rgb *= texture2D(UNI_Tex0, t0).rgb;\n";
                                continue;
                            case 4:
                                this.mShader += "  col.rgba *= texture2D(UNI_Tex0, t0).rgba;\n";
                                continue;
                        }
                    case 3:
                        this.mShader += "  col = texture2D(UNI_Tex0, t0);\n";
                        break;
                }
            }
            this.mShader += "  gl_FragColor = col;\n";
            this.mShader += "}\n";
        }

        public Builder(RenderScript rs) {
            this.mRS = rs;
        }

        public Builder setTexture(EnvMode env, Format fmt, int slot) throws IllegalArgumentException {
            if (slot < 0 || slot >= 2) {
                throw new IllegalArgumentException("MAX_TEXTURE exceeded.");
            }
            this.mSlots[slot] = new Slot(env, fmt);
            return this;
        }

        public Builder setPointSpriteTexCoordinateReplacement(boolean enable) {
            this.mPointSpriteEnable = enable;
            return this;
        }

        public Builder setVaryingColor(boolean enable) {
            this.mVaryingColorEnable = enable;
            return this;
        }

        public ProgramFragmentFixedFunction create() {
            InternalBuilder sb = new InternalBuilder(this.mRS);
            this.mNumTextures = 0;
            for (int i = 0; i < 2; i++) {
                if (this.mSlots[i] != null) {
                    this.mNumTextures++;
                }
            }
            buildShaderString();
            sb.setShader(this.mShader);
            Type constType = null;
            if (!this.mVaryingColorEnable) {
                Element.Builder b = new Element.Builder(this.mRS);
                b.add(Element.F32_4(this.mRS), "Color");
                Type.Builder typeBuilder = new Type.Builder(this.mRS, b.create());
                typeBuilder.setX(1);
                constType = typeBuilder.create();
                sb.addConstant(constType);
            }
            for (int i2 = 0; i2 < this.mNumTextures; i2++) {
                sb.addTexture(Program.TextureType.TEXTURE_2D);
            }
            ProgramFragmentFixedFunction pf = sb.create();
            pf.mTextureCount = 2;
            if (!this.mVaryingColorEnable) {
                Allocation constantData = Allocation.createTyped(this.mRS, constType);
                FieldPacker fp = new FieldPacker(16);
                Float4 f4 = new Float4(1.0f, 1.0f, 1.0f, 1.0f);
                fp.addF32(f4);
                constantData.setFromFieldPacker(0, fp);
                pf.bindConstants(constantData, 0);
            }
            return pf;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.renderscript.ProgramFragmentFixedFunction$1 */
    /* loaded from: classes3.dex */
    public static /* synthetic */ class C23701 {

        /* renamed from: $SwitchMap$android$renderscript$ProgramFragmentFixedFunction$Builder$EnvMode */
        static final /* synthetic */ int[] f401x21e46749;

        /* renamed from: $SwitchMap$android$renderscript$ProgramFragmentFixedFunction$Builder$Format */
        static final /* synthetic */ int[] f402xa802c1be;

        static {
            int[] iArr = new int[Builder.EnvMode.values().length];
            f401x21e46749 = iArr;
            try {
                iArr[Builder.EnvMode.REPLACE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f401x21e46749[Builder.EnvMode.MODULATE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f401x21e46749[Builder.EnvMode.DECAL.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            int[] iArr2 = new int[Builder.Format.values().length];
            f402xa802c1be = iArr2;
            try {
                iArr2[Builder.Format.ALPHA.ordinal()] = 1;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f402xa802c1be[Builder.Format.LUMINANCE_ALPHA.ordinal()] = 2;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f402xa802c1be[Builder.Format.RGB.ordinal()] = 3;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f402xa802c1be[Builder.Format.RGBA.ordinal()] = 4;
            } catch (NoSuchFieldError e7) {
            }
        }
    }
}
