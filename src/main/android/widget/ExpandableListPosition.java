package android.widget;

import java.util.ArrayList;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class ExpandableListPosition {
    public static final int CHILD = 1;
    public static final int GROUP = 2;
    private static final int MAX_POOL_SIZE = 5;
    private static ArrayList<ExpandableListPosition> sPool = new ArrayList<>(5);
    public int childPos;
    int flatListPos;
    public int groupPos;
    public int type;

    private void resetState() {
        this.groupPos = 0;
        this.childPos = 0;
        this.flatListPos = 0;
        this.type = 0;
    }

    private ExpandableListPosition() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getPackedPosition() {
        return this.type == 1 ? ExpandableListView.getPackedPositionForChild(this.groupPos, this.childPos) : ExpandableListView.getPackedPositionForGroup(this.groupPos);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ExpandableListPosition obtainGroupPosition(int groupPosition) {
        return obtain(2, groupPosition, 0, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ExpandableListPosition obtainChildPosition(int groupPosition, int childPosition) {
        return obtain(1, groupPosition, childPosition, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ExpandableListPosition obtainPosition(long packedPosition) {
        if (packedPosition == 4294967295L) {
            return null;
        }
        ExpandableListPosition elp = getRecycledOrCreate();
        elp.groupPos = ExpandableListView.getPackedPositionGroup(packedPosition);
        if (ExpandableListView.getPackedPositionType(packedPosition) == 1) {
            elp.type = 1;
            elp.childPos = ExpandableListView.getPackedPositionChild(packedPosition);
        } else {
            elp.type = 2;
        }
        return elp;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ExpandableListPosition obtain(int type, int groupPos, int childPos, int flatListPos) {
        ExpandableListPosition elp = getRecycledOrCreate();
        elp.type = type;
        elp.groupPos = groupPos;
        elp.childPos = childPos;
        elp.flatListPos = flatListPos;
        return elp;
    }

    private static ExpandableListPosition getRecycledOrCreate() {
        synchronized (sPool) {
            if (sPool.size() > 0) {
                ExpandableListPosition elp = sPool.remove(0);
                elp.resetState();
                return elp;
            }
            return new ExpandableListPosition();
        }
    }

    public void recycle() {
        synchronized (sPool) {
            if (sPool.size() < 5) {
                sPool.add(this);
            }
        }
    }
}
