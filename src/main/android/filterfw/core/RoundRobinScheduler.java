package android.filterfw.core;

import java.util.Set;
/* loaded from: classes.dex */
public class RoundRobinScheduler extends Scheduler {
    private int mLastPos;

    public RoundRobinScheduler(FilterGraph graph) {
        super(graph);
        this.mLastPos = -1;
    }

    @Override // android.filterfw.core.Scheduler
    public void reset() {
        this.mLastPos = -1;
    }

    @Override // android.filterfw.core.Scheduler
    public Filter scheduleNextNode() {
        Set<Filter> all_filters = getGraph().getFilters();
        if (this.mLastPos >= all_filters.size()) {
            this.mLastPos = -1;
        }
        int pos = 0;
        Filter first = null;
        int firstNdx = -1;
        for (Filter filter : all_filters) {
            if (filter.canProcess()) {
                if (pos <= this.mLastPos) {
                    if (first == null) {
                        first = filter;
                        firstNdx = pos;
                    }
                } else {
                    this.mLastPos = pos;
                    return filter;
                }
            }
            pos++;
        }
        if (first != null) {
            this.mLastPos = firstNdx;
            return first;
        }
        return null;
    }
}
