package com.android.server.credentials.metrics;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class CandidatePhaseMetric {
    public int mSessionId = -1;
    public boolean mQueryReturned = false;
    public int mCandidateUid = -1;
    public long mServiceBeganTimeNanoseconds = -1;
    public long mStartQueryTimeNanoseconds = -1;
    public long mQueryFinishTimeNanoseconds = -1;
    public int mProviderQueryStatus = -1;
    public boolean mHasException = false;
    public int mNumEntriesTotal = -1;
    public int mActionEntryCount = -1;
    public int mCredentialEntryCount = -1;
    public int mCredentialEntryTypeCount = -1;
    public int mRemoteEntryCount = -1;
    public int mAuthenticationEntryCount = -1;
    public final List<Integer> mAvailableEntries = new ArrayList();

    public void setServiceBeganTimeNanoseconds(long j) {
        this.mServiceBeganTimeNanoseconds = j;
    }

    public void setStartQueryTimeNanoseconds(long j) {
        this.mStartQueryTimeNanoseconds = j;
    }

    public void setQueryFinishTimeNanoseconds(long j) {
        this.mQueryFinishTimeNanoseconds = j;
    }

    public long getServiceBeganTimeNanoseconds() {
        return this.mServiceBeganTimeNanoseconds;
    }

    public long getStartQueryTimeNanoseconds() {
        return this.mStartQueryTimeNanoseconds;
    }

    public long getQueryFinishTimeNanoseconds() {
        return this.mQueryFinishTimeNanoseconds;
    }

    public int getQueryLatencyMicroseconds() {
        return (int) ((getQueryFinishTimeNanoseconds() - getStartQueryTimeNanoseconds()) / 1000);
    }

    public int getTimestampFromReferenceStartMicroseconds(long j) {
        long j2 = this.mServiceBeganTimeNanoseconds;
        if (j < j2) {
            Log.i("CandidateProviderMetric", "The timestamp is before service started, falling back to default int");
            return -1;
        }
        return (int) ((j - j2) / 1000);
    }

    public void setProviderQueryStatus(int i) {
        this.mProviderQueryStatus = i;
    }

    public int getProviderQueryStatus() {
        return this.mProviderQueryStatus;
    }

    public void setCandidateUid(int i) {
        this.mCandidateUid = i;
    }

    public int getCandidateUid() {
        return this.mCandidateUid;
    }

    public void setSessionId(int i) {
        this.mSessionId = i;
    }

    public int getSessionId() {
        return this.mSessionId;
    }

    public void setQueryReturned(boolean z) {
        this.mQueryReturned = z;
    }

    public boolean isQueryReturned() {
        return this.mQueryReturned;
    }

    public void setHasException(boolean z) {
        this.mHasException = z;
    }

    public boolean isHasException() {
        return this.mHasException;
    }

    public void setNumEntriesTotal(int i) {
        this.mNumEntriesTotal = i;
    }

    public int getNumEntriesTotal() {
        return this.mNumEntriesTotal;
    }

    public void setActionEntryCount(int i) {
        this.mActionEntryCount = i;
    }

    public int getActionEntryCount() {
        return this.mActionEntryCount;
    }

    public void setCredentialEntryCount(int i) {
        this.mCredentialEntryCount = i;
    }

    public int getCredentialEntryCount() {
        return this.mCredentialEntryCount;
    }

    public void setCredentialEntryTypeCount(int i) {
        this.mCredentialEntryTypeCount = i;
    }

    public int getCredentialEntryTypeCount() {
        return this.mCredentialEntryTypeCount;
    }

    public void setRemoteEntryCount(int i) {
        this.mRemoteEntryCount = i;
    }

    public int getRemoteEntryCount() {
        return this.mRemoteEntryCount;
    }

    public void setAuthenticationEntryCount(int i) {
        this.mAuthenticationEntryCount = i;
    }

    public int getAuthenticationEntryCount() {
        return this.mAuthenticationEntryCount;
    }

    public void addEntry(EntryEnum entryEnum) {
        this.mAvailableEntries.add(Integer.valueOf(entryEnum.getMetricCode()));
    }

    public List<Integer> getAvailableEntries() {
        return new ArrayList(this.mAvailableEntries);
    }
}
