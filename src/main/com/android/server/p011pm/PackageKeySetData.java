package com.android.server.p011pm;

import android.util.ArrayMap;
import com.android.internal.util.ArrayUtils;
import java.util.Map;
/* renamed from: com.android.server.pm.PackageKeySetData */
/* loaded from: classes2.dex */
public class PackageKeySetData {
    public final ArrayMap<String, Long> mKeySetAliases;
    public long mProperSigningKeySet;
    public long[] mUpgradeKeySets;

    public PackageKeySetData() {
        this.mKeySetAliases = new ArrayMap<>();
        this.mProperSigningKeySet = -1L;
    }

    public PackageKeySetData(PackageKeySetData packageKeySetData) {
        ArrayMap<String, Long> arrayMap = new ArrayMap<>();
        this.mKeySetAliases = arrayMap;
        this.mProperSigningKeySet = packageKeySetData.mProperSigningKeySet;
        this.mUpgradeKeySets = ArrayUtils.cloneOrNull(packageKeySetData.mUpgradeKeySets);
        arrayMap.putAll((ArrayMap<? extends String, ? extends Long>) packageKeySetData.mKeySetAliases);
    }

    public void setProperSigningKeySet(long j) {
        this.mProperSigningKeySet = j;
    }

    public long getProperSigningKeySet() {
        return this.mProperSigningKeySet;
    }

    public void addUpgradeKeySet(String str) {
        if (str == null) {
            return;
        }
        Long l = this.mKeySetAliases.get(str);
        if (l != null) {
            this.mUpgradeKeySets = ArrayUtils.appendLong(this.mUpgradeKeySets, l.longValue());
            return;
        }
        throw new IllegalArgumentException("Upgrade keyset alias " + str + "does not refer to a defined keyset alias!");
    }

    public void addUpgradeKeySetById(long j) {
        this.mUpgradeKeySets = ArrayUtils.appendLong(this.mUpgradeKeySets, j);
    }

    public void removeAllUpgradeKeySets() {
        this.mUpgradeKeySets = null;
    }

    public long[] getUpgradeKeySets() {
        return this.mUpgradeKeySets;
    }

    public ArrayMap<String, Long> getAliases() {
        return this.mKeySetAliases;
    }

    public void setAliases(Map<String, Long> map) {
        removeAllDefinedKeySets();
        this.mKeySetAliases.putAll(map);
    }

    public void addDefinedKeySet(long j, String str) {
        this.mKeySetAliases.put(str, Long.valueOf(j));
    }

    public void removeAllDefinedKeySets() {
        this.mKeySetAliases.erase();
    }

    public boolean isUsingDefinedKeySets() {
        return this.mKeySetAliases.size() > 0;
    }

    public boolean isUsingUpgradeKeySets() {
        long[] jArr = this.mUpgradeKeySets;
        return jArr != null && jArr.length > 0;
    }
}
