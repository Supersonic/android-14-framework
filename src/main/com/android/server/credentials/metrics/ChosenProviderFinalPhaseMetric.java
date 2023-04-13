package com.android.server.credentials.metrics;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ChosenProviderFinalPhaseMetric {
    public int mSessionId = -1;
    public boolean mUiReturned = false;
    public int mChosenUid = -1;
    public int mPreQueryPhaseLatencyMicroseconds = -1;
    public int mQueryPhaseLatencyMicroseconds = -1;
    public long mServiceBeganTimeNanoseconds = -1;
    public long mQueryStartTimeNanoseconds = -1;
    public long mQueryEndTimeNanoseconds = -1;
    public long mUiCallStartTimeNanoseconds = -1;
    public long mUiCallEndTimeNanoseconds = -1;
    public long mFinalFinishTimeNanoseconds = -1;
    public int mChosenProviderStatus = -1;
    public boolean mHasException = false;
    public int mNumEntriesTotal = -1;
    public int mActionEntryCount = -1;
    public int mCredentialEntryCount = -1;
    public int mCredentialEntryTypeCount = -1;
    public int mRemoteEntryCount = -1;
    public int mAuthenticationEntryCount = -1;
    public List<Integer> mAvailableEntries = new ArrayList();

    public int getChosenUid() {
        return this.mChosenUid;
    }

    public void setChosenUid(int i) {
        this.mChosenUid = i;
    }

    public void setQueryPhaseLatencyMicroseconds(int i) {
        this.mQueryPhaseLatencyMicroseconds = i;
    }

    public void setServiceBeganTimeNanoseconds(long j) {
        this.mServiceBeganTimeNanoseconds = j;
    }

    public void setQueryStartTimeNanoseconds(long j) {
        this.mQueryStartTimeNanoseconds = j;
    }

    public void setQueryEndTimeNanoseconds(long j) {
        this.mQueryEndTimeNanoseconds = j;
    }

    public void setUiCallStartTimeNanoseconds(long j) {
        this.mUiCallStartTimeNanoseconds = j;
    }

    public void setUiCallEndTimeNanoseconds(long j) {
        this.mUiCallEndTimeNanoseconds = j;
    }

    public void setFinalFinishTimeNanoseconds(long j) {
        this.mFinalFinishTimeNanoseconds = j;
    }

    public long getQueryStartTimeNanoseconds() {
        return this.mQueryStartTimeNanoseconds;
    }

    public long getQueryEndTimeNanoseconds() {
        return this.mQueryEndTimeNanoseconds;
    }

    public long getUiCallStartTimeNanoseconds() {
        return this.mUiCallStartTimeNanoseconds;
    }

    public long getUiCallEndTimeNanoseconds() {
        return this.mUiCallEndTimeNanoseconds;
    }

    public long getFinalFinishTimeNanoseconds() {
        return this.mFinalFinishTimeNanoseconds;
    }

    public int getTimestampFromReferenceStartMicroseconds(long j) {
        long j2 = this.mServiceBeganTimeNanoseconds;
        if (j < j2) {
            Log.i("ChosenFinalPhaseMetric", "The timestamp is before service started, falling back to default int");
            return -1;
        }
        return (int) ((j - j2) / 1000);
    }

    public int getChosenProviderStatus() {
        return this.mChosenProviderStatus;
    }

    public void setChosenProviderStatus(int i) {
        this.mChosenProviderStatus = i;
    }

    public void setSessionId(int i) {
        this.mSessionId = i;
    }

    public int getSessionId() {
        return this.mSessionId;
    }

    public void setUiReturned(boolean z) {
        this.mUiReturned = z;
    }

    public boolean isUiReturned() {
        return this.mUiReturned;
    }

    public void setNumEntriesTotal(int i) {
        this.mNumEntriesTotal = i;
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

    public void setAvailableEntries(List<Integer> list) {
        this.mAvailableEntries = new ArrayList(list);
    }

    public List<Integer> getAvailableEntries() {
        return new ArrayList(this.mAvailableEntries);
    }

    public void setHasException(boolean z) {
        this.mHasException = z;
    }

    public boolean isHasException() {
        return this.mHasException;
    }
}
