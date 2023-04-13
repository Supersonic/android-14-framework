package com.android.server.broadcastradio.hal2;

import android.hardware.radio.ProgramList;
import android.hardware.radio.ProgramSelector;
import android.hardware.radio.RadioManager;
import com.android.internal.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
/* loaded from: classes.dex */
public final class ProgramInfoCache {
    public boolean mComplete;
    public final ProgramList.Filter mFilter;
    public final Map<ProgramSelector.Identifier, RadioManager.ProgramInfo> mProgramInfoMap;

    public ProgramInfoCache(ProgramList.Filter filter) {
        this.mProgramInfoMap = new HashMap();
        this.mComplete = true;
        this.mFilter = filter;
    }

    @VisibleForTesting
    public ProgramInfoCache(ProgramList.Filter filter, boolean z, RadioManager.ProgramInfo... programInfoArr) {
        this.mProgramInfoMap = new HashMap();
        this.mFilter = filter;
        this.mComplete = z;
        for (RadioManager.ProgramInfo programInfo : programInfoArr) {
            this.mProgramInfoMap.put(programInfo.getSelector().getPrimaryId(), programInfo);
        }
    }

    @VisibleForTesting
    public boolean programInfosAreExactly(RadioManager.ProgramInfo... programInfoArr) {
        HashMap hashMap = new HashMap();
        for (RadioManager.ProgramInfo programInfo : programInfoArr) {
            hashMap.put(programInfo.getSelector().getPrimaryId(), programInfo);
        }
        return hashMap.equals(this.mProgramInfoMap);
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder("ProgramInfoCache(mComplete = ");
        sb.append(this.mComplete);
        sb.append(", mFilter = ");
        sb.append(this.mFilter);
        sb.append(", mProgramInfoMap = [");
        this.mProgramInfoMap.forEach(new BiConsumer() { // from class: com.android.server.broadcastradio.hal2.ProgramInfoCache$$ExternalSyntheticLambda0
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ProgramInfoCache.lambda$toString$0(sb, (ProgramSelector.Identifier) obj, (RadioManager.ProgramInfo) obj2);
            }
        });
        sb.append("]");
        return sb.toString();
    }

    public static /* synthetic */ void lambda$toString$0(StringBuilder sb, ProgramSelector.Identifier identifier, RadioManager.ProgramInfo programInfo) {
        sb.append("\n");
        sb.append(programInfo.toString());
    }

    public ProgramList.Filter getFilter() {
        return this.mFilter;
    }

    public List<ProgramList.Chunk> filterAndUpdateFrom(ProgramInfoCache programInfoCache, boolean z) {
        return filterAndUpdateFromInternal(programInfoCache, z, 100, 500);
    }

    @VisibleForTesting
    public List<ProgramList.Chunk> filterAndUpdateFromInternal(ProgramInfoCache programInfoCache, boolean z, int i, int i2) {
        if (z) {
            this.mProgramInfoMap.clear();
        }
        if (this.mProgramInfoMap.isEmpty()) {
            z = true;
        }
        boolean z2 = z;
        HashSet hashSet = new HashSet();
        HashSet<ProgramSelector.Identifier> hashSet2 = new HashSet(this.mProgramInfoMap.keySet());
        for (Map.Entry<ProgramSelector.Identifier, RadioManager.ProgramInfo> entry : programInfoCache.mProgramInfoMap.entrySet()) {
            ProgramSelector.Identifier key = entry.getKey();
            if (passesFilter(key)) {
                hashSet2.remove(key);
                RadioManager.ProgramInfo value = entry.getValue();
                if (shouldIncludeInModified(value)) {
                    this.mProgramInfoMap.put(key, value);
                    hashSet.add(value);
                }
            }
        }
        for (ProgramSelector.Identifier identifier : hashSet2) {
            this.mProgramInfoMap.remove(identifier);
        }
        boolean z3 = programInfoCache.mComplete;
        this.mComplete = z3;
        return buildChunks(z2, z3, hashSet, i, hashSet2, i2);
    }

    public List<ProgramList.Chunk> filterAndApplyChunk(ProgramList.Chunk chunk) {
        return filterAndApplyChunkInternal(chunk, 100, 500);
    }

    @VisibleForTesting
    public List<ProgramList.Chunk> filterAndApplyChunkInternal(ProgramList.Chunk chunk, int i, int i2) {
        if (chunk.isPurge()) {
            this.mProgramInfoMap.clear();
        }
        HashSet hashSet = new HashSet();
        HashSet hashSet2 = new HashSet();
        for (RadioManager.ProgramInfo programInfo : chunk.getModified()) {
            ProgramSelector.Identifier primaryId = programInfo.getSelector().getPrimaryId();
            if (passesFilter(primaryId) && shouldIncludeInModified(programInfo)) {
                this.mProgramInfoMap.put(primaryId, programInfo);
                hashSet.add(programInfo);
            }
        }
        for (ProgramSelector.Identifier identifier : chunk.getRemoved()) {
            if (this.mProgramInfoMap.containsKey(identifier)) {
                this.mProgramInfoMap.remove(identifier);
                hashSet2.add(identifier);
            }
        }
        if (hashSet.isEmpty() && hashSet2.isEmpty() && this.mComplete == chunk.isComplete() && !chunk.isPurge()) {
            return null;
        }
        this.mComplete = chunk.isComplete();
        return buildChunks(chunk.isPurge(), this.mComplete, hashSet, i, hashSet2, i2);
    }

    public final boolean passesFilter(ProgramSelector.Identifier identifier) {
        ProgramList.Filter filter = this.mFilter;
        if (filter == null) {
            return true;
        }
        if (filter.getIdentifierTypes().isEmpty() || this.mFilter.getIdentifierTypes().contains(Integer.valueOf(identifier.getType()))) {
            if (this.mFilter.getIdentifiers().isEmpty() || this.mFilter.getIdentifiers().contains(identifier)) {
                return this.mFilter.areCategoriesIncluded() || !identifier.isCategoryType();
            }
            return false;
        }
        return false;
    }

    public final boolean shouldIncludeInModified(RadioManager.ProgramInfo programInfo) {
        RadioManager.ProgramInfo programInfo2 = this.mProgramInfoMap.get(programInfo.getSelector().getPrimaryId());
        if (programInfo2 == null) {
            return true;
        }
        ProgramList.Filter filter = this.mFilter;
        if (filter == null || !filter.areModificationsExcluded()) {
            return !programInfo2.equals(programInfo);
        }
        return false;
    }

    public static int roundUpFraction(int i, int i2) {
        return (i / i2) + (i % i2 > 0 ? 1 : 0);
    }

    public static List<ProgramList.Chunk> buildChunks(boolean z, boolean z2, Collection<RadioManager.ProgramInfo> collection, int i, Collection<ProgramSelector.Identifier> collection2, int i2) {
        int i3;
        int i4;
        Iterator<RadioManager.ProgramInfo> it;
        Iterator<ProgramSelector.Identifier> it2;
        int i5;
        if (z) {
            collection2 = null;
        }
        if (collection != null) {
            i3 = Math.max(z ? 1 : 0, roundUpFraction(collection.size(), i));
        } else {
            i3 = z ? 1 : 0;
        }
        if (collection2 != null) {
            i3 = Math.max(i3, roundUpFraction(collection2.size(), i2));
        }
        if (i3 == 0) {
            return new ArrayList();
        }
        if (collection != null) {
            i4 = roundUpFraction(collection.size(), i3);
            it = collection.iterator();
        } else {
            i4 = 0;
            it = null;
        }
        if (collection2 != null) {
            i5 = roundUpFraction(collection2.size(), i3);
            it2 = collection2.iterator();
        } else {
            it2 = null;
            i5 = 0;
        }
        ArrayList arrayList = new ArrayList(i3);
        int i6 = 0;
        while (i6 < i3) {
            HashSet hashSet = new HashSet();
            HashSet hashSet2 = new HashSet();
            if (it != null) {
                for (int i7 = 0; i7 < i4 && it.hasNext(); i7++) {
                    hashSet.add(it.next());
                }
            }
            if (it2 != null) {
                for (int i8 = 0; i8 < i5 && it2.hasNext(); i8++) {
                    hashSet2.add(it2.next());
                }
            }
            boolean z3 = true;
            boolean z4 = z && i6 == 0;
            if (!z2 || i6 != i3 - 1) {
                z3 = false;
            }
            arrayList.add(new ProgramList.Chunk(z4, z3, hashSet, hashSet2));
            i6++;
        }
        return arrayList;
    }
}
