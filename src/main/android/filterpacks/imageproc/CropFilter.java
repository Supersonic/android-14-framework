package android.filterpacks.imageproc;

import android.app.slice.SliceItem;
import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.filterfw.format.ObjectFormat;
import android.filterfw.geometry.Quad;
/* loaded from: classes.dex */
public class CropFilter extends Filter {
    @GenerateFieldPort(name = "fillblack")
    private boolean mFillBlack;
    private final String mFragShader;
    private FrameFormat mLastFormat;
    @GenerateFieldPort(name = "oheight")
    private int mOutputHeight;
    @GenerateFieldPort(name = "owidth")
    private int mOutputWidth;
    private Program mProgram;

    public CropFilter(String name) {
        super(name);
        this.mLastFormat = null;
        this.mOutputWidth = -1;
        this.mOutputHeight = -1;
        this.mFillBlack = false;
        this.mFragShader = "precision mediump float;\nuniform sampler2D tex_sampler_0;\nvarying vec2 v_texcoord;\nvoid main() {\n  const vec2 lo = vec2(0.0, 0.0);\n  const vec2 hi = vec2(1.0, 1.0);\n  const vec4 black = vec4(0.0, 0.0, 0.0, 1.0);\n  bool out_of_bounds =\n    any(lessThan(v_texcoord, lo)) ||\n    any(greaterThan(v_texcoord, hi));\n  if (out_of_bounds) {\n    gl_FragColor = black;\n  } else {\n    gl_FragColor = texture2D(tex_sampler_0, v_texcoord);\n  }\n}\n";
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addMaskedInputPort(SliceItem.FORMAT_IMAGE, ImageFormat.create(3));
        addMaskedInputPort("box", ObjectFormat.fromClass(Quad.class, 1));
        addOutputBasedOnInput(SliceItem.FORMAT_IMAGE, SliceItem.FORMAT_IMAGE);
    }

    @Override // android.filterfw.core.Filter
    public FrameFormat getOutputFormat(String portName, FrameFormat inputFormat) {
        MutableFrameFormat outputFormat = inputFormat.mutableCopy();
        outputFormat.setDimensions(0, 0);
        return outputFormat;
    }

    protected void createProgram(FilterContext context, FrameFormat format) {
        FrameFormat frameFormat = this.mLastFormat;
        if (frameFormat == null || frameFormat.getTarget() != format.getTarget()) {
            this.mLastFormat = format;
            this.mProgram = null;
            switch (format.getTarget()) {
                case 3:
                    if (this.mFillBlack) {
                        this.mProgram = new ShaderProgram(context, "precision mediump float;\nuniform sampler2D tex_sampler_0;\nvarying vec2 v_texcoord;\nvoid main() {\n  const vec2 lo = vec2(0.0, 0.0);\n  const vec2 hi = vec2(1.0, 1.0);\n  const vec4 black = vec4(0.0, 0.0, 0.0, 1.0);\n  bool out_of_bounds =\n    any(lessThan(v_texcoord, lo)) ||\n    any(greaterThan(v_texcoord, hi));\n  if (out_of_bounds) {\n    gl_FragColor = black;\n  } else {\n    gl_FragColor = texture2D(tex_sampler_0, v_texcoord);\n  }\n}\n");
                        break;
                    } else {
                        this.mProgram = ShaderProgram.createIdentity(context);
                        break;
                    }
            }
            if (this.mProgram == null) {
                throw new RuntimeException("Could not create a program for crop filter " + this + "!");
            }
        }
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext env) {
        Frame imageFrame = pullInput(SliceItem.FORMAT_IMAGE);
        Frame boxFrame = pullInput("box");
        createProgram(env, imageFrame.getFormat());
        Quad box = (Quad) boxFrame.getObjectValue();
        MutableFrameFormat outputFormat = imageFrame.getFormat().mutableCopy();
        int i = this.mOutputWidth;
        if (i == -1) {
            i = outputFormat.getWidth();
        }
        int i2 = this.mOutputHeight;
        if (i2 == -1) {
            i2 = outputFormat.getHeight();
        }
        outputFormat.setDimensions(i, i2);
        Frame output = env.getFrameManager().newFrame(outputFormat);
        Program program = this.mProgram;
        if (program instanceof ShaderProgram) {
            ShaderProgram shaderProgram = (ShaderProgram) program;
            shaderProgram.setSourceRegion(box);
        }
        this.mProgram.process(imageFrame, output);
        pushOutput(SliceItem.FORMAT_IMAGE, output);
        output.release();
    }
}
