package com.android.server.p012tv.tunerresourcemanager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/* renamed from: com.android.server.tv.tunerresourcemanager.CasResource */
/* loaded from: classes2.dex */
public class CasResource {
    public int mAvailableSessionNum;
    public int mMaxSessionNum;
    public Map<Integer, Integer> mOwnerClientIdsToSessionNum = new HashMap();
    public final int mSystemId;

    public CasResource(Builder builder) {
        this.mSystemId = builder.mSystemId;
        int i = builder.mMaxSessionNum;
        this.mMaxSessionNum = i;
        this.mAvailableSessionNum = i;
    }

    public int getSystemId() {
        return this.mSystemId;
    }

    public int getMaxSessionNum() {
        return this.mMaxSessionNum;
    }

    public int getUsedSessionNum() {
        return this.mMaxSessionNum - this.mAvailableSessionNum;
    }

    public boolean isFullyUsed() {
        return this.mAvailableSessionNum == 0;
    }

    public void updateMaxSessionNum(int i) {
        this.mAvailableSessionNum = Math.max(0, this.mAvailableSessionNum + (i - this.mMaxSessionNum));
        this.mMaxSessionNum = i;
    }

    public void setOwner(int i) {
        this.mOwnerClientIdsToSessionNum.put(Integer.valueOf(i), Integer.valueOf(this.mOwnerClientIdsToSessionNum.get(Integer.valueOf(i)) == null ? 1 : this.mOwnerClientIdsToSessionNum.get(Integer.valueOf(i)).intValue() + 1));
        this.mAvailableSessionNum--;
    }

    public void removeOwner(int i) {
        this.mAvailableSessionNum += this.mOwnerClientIdsToSessionNum.get(Integer.valueOf(i)).intValue();
        this.mOwnerClientIdsToSessionNum.remove(Integer.valueOf(i));
    }

    public Set<Integer> getOwnerClientIds() {
        return this.mOwnerClientIdsToSessionNum.keySet();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CasResource[systemId=");
        sb.append(this.mSystemId);
        sb.append(", isFullyUsed=");
        sb.append(this.mAvailableSessionNum == 0);
        sb.append(", maxSessionNum=");
        sb.append(this.mMaxSessionNum);
        sb.append(", ownerClients=");
        sb.append(ownersMapToString());
        sb.append("]");
        return sb.toString();
    }

    /* renamed from: com.android.server.tv.tunerresourcemanager.CasResource$Builder */
    /* loaded from: classes2.dex */
    public static class Builder {
        public int mMaxSessionNum;
        public int mSystemId;

        public Builder(int i) {
            this.mSystemId = i;
        }

        public Builder maxSessionNum(int i) {
            this.mMaxSessionNum = i;
            return this;
        }

        public CasResource build() {
            return new CasResource(this);
        }
    }

    public String ownersMapToString() {
        StringBuilder sb = new StringBuilder("{");
        for (Integer num : this.mOwnerClientIdsToSessionNum.keySet()) {
            int intValue = num.intValue();
            sb.append(" clientId=");
            sb.append(intValue);
            sb.append(", owns session num=");
            sb.append(this.mOwnerClientIdsToSessionNum.get(Integer.valueOf(intValue)));
            sb.append(",");
        }
        sb.append("}");
        return sb.toString();
    }
}
