package android.filterpacks.imageproc;

import android.app.slice.Slice;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
/* loaded from: classes.dex */
public class AlphaBlendFilter extends ImageCombineFilter {
    private final String mAlphaBlendShader;

    public AlphaBlendFilter(String name) {
        super(name, new String[]{Slice.SUBTYPE_SOURCE, "overlay", "mask"}, "blended", "weight");
        this.mAlphaBlendShader = "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform float weight;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 colorL = texture2D(tex_sampler_0, v_texcoord);\n  vec4 colorR = texture2D(tex_sampler_1, v_texcoord);\n  float blend = texture2D(tex_sampler_2, v_texcoord).r * weight;\n  gl_FragColor = colorL * (1.0 - blend) + colorR * blend;\n}\n";
    }

    @Override // android.filterpacks.imageproc.ImageCombineFilter
    protected Program getNativeProgram(FilterContext context) {
        throw new RuntimeException("TODO: Write native implementation for AlphaBlend!");
    }

    @Override // android.filterpacks.imageproc.ImageCombineFilter
    protected Program getShaderProgram(FilterContext context) {
        return new ShaderProgram(context, "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform float weight;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 colorL = texture2D(tex_sampler_0, v_texcoord);\n  vec4 colorR = texture2D(tex_sampler_1, v_texcoord);\n  float blend = texture2D(tex_sampler_2, v_texcoord).r * weight;\n  gl_FragColor = colorL * (1.0 - blend) + colorR * blend;\n}\n");
    }
}
