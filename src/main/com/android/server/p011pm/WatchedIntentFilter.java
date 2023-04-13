package com.android.server.p011pm;

import android.content.IntentFilter;
import android.os.PatternMatcher;
import android.util.Printer;
import com.android.server.utils.Snappable;
import com.android.server.utils.WatchableImpl;
import java.util.ArrayList;
import java.util.List;
/* renamed from: com.android.server.pm.WatchedIntentFilter */
/* loaded from: classes2.dex */
public class WatchedIntentFilter extends WatchableImpl implements Snappable<WatchedIntentFilter> {
    public IntentFilter mFilter;

    public final void onChanged() {
        dispatchChange(this);
    }

    public WatchedIntentFilter() {
        this.mFilter = new IntentFilter();
    }

    public WatchedIntentFilter(IntentFilter intentFilter) {
        this.mFilter = new IntentFilter(intentFilter);
    }

    public WatchedIntentFilter(WatchedIntentFilter watchedIntentFilter) {
        this(watchedIntentFilter.getIntentFilter());
    }

    public WatchedIntentFilter(String str) {
        this.mFilter = new IntentFilter(str);
    }

    public IntentFilter getIntentFilter() {
        return this.mFilter;
    }

    public final int getPriority() {
        return this.mFilter.getPriority();
    }

    public final void addAction(String str) {
        this.mFilter.addAction(str);
        onChanged();
    }

    public final int countActions() {
        return this.mFilter.countActions();
    }

    public final boolean hasAction(String str) {
        return this.mFilter.hasAction(str);
    }

    public final void addDataType(String str) throws IntentFilter.MalformedMimeTypeException {
        this.mFilter.addDataType(str);
        onChanged();
    }

    public final int countDataTypes() {
        return this.mFilter.countDataTypes();
    }

    public final void addDataScheme(String str) {
        this.mFilter.addDataScheme(str);
        onChanged();
    }

    public final int countDataSchemes() {
        return this.mFilter.countDataSchemes();
    }

    public final String getDataScheme(int i) {
        return this.mFilter.getDataScheme(i);
    }

    public final void addDataSchemeSpecificPart(String str, int i) {
        this.mFilter.addDataSchemeSpecificPart(str, i);
        onChanged();
    }

    public final void addDataAuthority(IntentFilter.AuthorityEntry authorityEntry) {
        this.mFilter.addDataAuthority(authorityEntry);
        onChanged();
    }

    public final int countDataAuthorities() {
        return this.mFilter.countDataAuthorities();
    }

    public final void addDataPath(PatternMatcher patternMatcher) {
        this.mFilter.addDataPath(patternMatcher);
        onChanged();
    }

    public final int countDataPaths() {
        return this.mFilter.countDataPaths();
    }

    public final void addCategory(String str) {
        this.mFilter.addCategory(str);
    }

    public final boolean hasCategory(String str) {
        return this.mFilter.hasCategory(str);
    }

    public void dump(Printer printer, String str) {
        this.mFilter.dump(printer, str);
    }

    public boolean checkDataPathAndSchemeSpecificParts() {
        return this.mFilter.checkDataPathAndSchemeSpecificParts();
    }

    public static List<WatchedIntentFilter> toWatchedIntentFilterList(List<IntentFilter> list) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            arrayList.add(new WatchedIntentFilter(list.get(i)));
        }
        return arrayList;
    }

    @Override // com.android.server.utils.Snappable
    public WatchedIntentFilter snapshot() {
        return new WatchedIntentFilter(this);
    }
}
