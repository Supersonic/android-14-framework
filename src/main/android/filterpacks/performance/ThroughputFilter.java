package android.filterpacks.performance;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.format.ObjectFormat;
import android.p008os.SystemClock;
/* loaded from: classes.dex */
public class ThroughputFilter extends Filter {
    private long mLastTime;
    private FrameFormat mOutputFormat;
    @GenerateFieldPort(hasDefault = true, name = "period")
    private int mPeriod;
    private int mPeriodFrameCount;
    private int mTotalFrameCount;

    public ThroughputFilter(String name) {
        super(name);
        this.mPeriod = 5;
        this.mLastTime = 0L;
        this.mTotalFrameCount = 0;
        this.mPeriodFrameCount = 0;
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addInputPort("frame");
        this.mOutputFormat = ObjectFormat.fromClass(Throughput.class, 1);
        addOutputBasedOnInput("frame", "frame");
        addOutputPort("throughput", this.mOutputFormat);
    }

    @Override // android.filterfw.core.Filter
    public FrameFormat getOutputFormat(String portName, FrameFormat inputFormat) {
        return inputFormat;
    }

    @Override // android.filterfw.core.Filter
    public void open(FilterContext env) {
        this.mTotalFrameCount = 0;
        this.mPeriodFrameCount = 0;
        this.mLastTime = 0L;
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        Frame input = pullInput("frame");
        pushOutput("frame", input);
        this.mTotalFrameCount++;
        this.mPeriodFrameCount++;
        if (this.mLastTime == 0) {
            this.mLastTime = SystemClock.elapsedRealtime();
        }
        long curTime = SystemClock.elapsedRealtime();
        if (curTime - this.mLastTime >= this.mPeriod * 1000) {
            FrameFormat inputFormat = input.getFormat();
            int pixelCount = inputFormat.getWidth() * inputFormat.getHeight();
            Throughput throughput = new Throughput(this.mTotalFrameCount, this.mPeriodFrameCount, this.mPeriod, pixelCount);
            Frame throughputFrame = context.getFrameManager().newFrame(this.mOutputFormat);
            throughputFrame.setObjectValue(throughput);
            pushOutput("throughput", throughputFrame);
            this.mLastTime = curTime;
            this.mPeriodFrameCount = 0;
        }
    }
}
