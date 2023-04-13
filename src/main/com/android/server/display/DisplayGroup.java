package com.android.server.display;

import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class DisplayGroup {
    public int mChangeCount;
    public final List<LogicalDisplay> mDisplays = new ArrayList();
    public final int mGroupId;

    public DisplayGroup(int i) {
        this.mGroupId = i;
    }

    public int getGroupId() {
        return this.mGroupId;
    }

    public void addDisplayLocked(LogicalDisplay logicalDisplay) {
        if (containsLocked(logicalDisplay)) {
            return;
        }
        this.mChangeCount++;
        this.mDisplays.add(logicalDisplay);
    }

    public boolean containsLocked(LogicalDisplay logicalDisplay) {
        return this.mDisplays.contains(logicalDisplay);
    }

    public boolean removeDisplayLocked(LogicalDisplay logicalDisplay) {
        this.mChangeCount++;
        return this.mDisplays.remove(logicalDisplay);
    }

    public boolean isEmptyLocked() {
        return this.mDisplays.isEmpty();
    }

    public int getChangeCountLocked() {
        return this.mChangeCount;
    }

    public int getSizeLocked() {
        return this.mDisplays.size();
    }

    public int getIdLocked(int i) {
        return this.mDisplays.get(i).getDisplayIdLocked();
    }
}
