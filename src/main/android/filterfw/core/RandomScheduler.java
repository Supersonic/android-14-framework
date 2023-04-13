package android.filterfw.core;

import java.util.Random;
import java.util.Vector;
/* loaded from: classes.dex */
public class RandomScheduler extends Scheduler {
    private Random mRand;

    public RandomScheduler(FilterGraph graph) {
        super(graph);
        this.mRand = new Random();
    }

    @Override // android.filterfw.core.Scheduler
    public void reset() {
    }

    @Override // android.filterfw.core.Scheduler
    public Filter scheduleNextNode() {
        Vector<Filter> candidates = new Vector<>();
        for (Filter filter : getGraph().getFilters()) {
            if (filter.canProcess()) {
                candidates.add(filter);
            }
        }
        if (candidates.size() > 0) {
            int r = this.mRand.nextInt(candidates.size());
            return candidates.elementAt(r);
        }
        return null;
    }
}
