package com.android.server.credentials;

import android.credentials.CredentialDescription;
import android.credentials.RegisterCredentialDescriptionRequest;
import android.credentials.UnregisterCredentialDescriptionRequest;
import android.service.credentials.CredentialEntry;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
/* loaded from: classes.dex */
public class CredentialDescriptionRegistry {
    @GuardedBy({"sLock"})
    public static final SparseArray<CredentialDescriptionRegistry> sCredentialDescriptionSessionPerUser = new SparseArray<>();
    public static final ReentrantLock sLock = new ReentrantLock();
    public Map<String, Set<CredentialDescription>> mCredentialDescriptions = new HashMap();
    public int mTotalDescriptionCount = 0;

    /* loaded from: classes.dex */
    public static final class FilterResult {
        public final List<CredentialEntry> mCredentialEntries;
        public final String mFlattenedRequest;
        public final String mPackageName;

        @VisibleForTesting
        public FilterResult(String str, String str2, List<CredentialEntry> list) {
            this.mPackageName = str;
            this.mFlattenedRequest = str2;
            this.mCredentialEntries = list;
        }
    }

    @GuardedBy({"sLock"})
    public static CredentialDescriptionRegistry forUser(int i) {
        ReentrantLock reentrantLock = sLock;
        reentrantLock.lock();
        try {
            SparseArray<CredentialDescriptionRegistry> sparseArray = sCredentialDescriptionSessionPerUser;
            CredentialDescriptionRegistry credentialDescriptionRegistry = sparseArray.get(i, null);
            if (credentialDescriptionRegistry == null) {
                credentialDescriptionRegistry = new CredentialDescriptionRegistry();
                sparseArray.put(i, credentialDescriptionRegistry);
            }
            reentrantLock.unlock();
            return credentialDescriptionRegistry;
        } catch (Throwable th) {
            sLock.unlock();
            throw th;
        }
    }

    @GuardedBy({"sLock"})
    public static void clearUserSession(int i) {
        ReentrantLock reentrantLock = sLock;
        reentrantLock.lock();
        try {
            sCredentialDescriptionSessionPerUser.remove(i);
            reentrantLock.unlock();
        } catch (Throwable th) {
            sLock.unlock();
            throw th;
        }
    }

    @GuardedBy({"sLock"})
    @VisibleForTesting
    public static void clearAllSessions() {
        ReentrantLock reentrantLock = sLock;
        reentrantLock.lock();
        try {
            sCredentialDescriptionSessionPerUser.clear();
            reentrantLock.unlock();
        } catch (Throwable th) {
            sLock.unlock();
            throw th;
        }
    }

    @GuardedBy({"sLock"})
    @VisibleForTesting
    public static void setSession(int i, CredentialDescriptionRegistry credentialDescriptionRegistry) {
        ReentrantLock reentrantLock = sLock;
        reentrantLock.lock();
        try {
            sCredentialDescriptionSessionPerUser.put(i, credentialDescriptionRegistry);
            reentrantLock.unlock();
        } catch (Throwable th) {
            sLock.unlock();
            throw th;
        }
    }

    public void executeRegisterRequest(RegisterCredentialDescriptionRequest registerCredentialDescriptionRequest, String str) {
        if (!this.mCredentialDescriptions.containsKey(str)) {
            this.mCredentialDescriptions.put(str, new HashSet());
        }
        if (this.mTotalDescriptionCount > 128 || this.mCredentialDescriptions.get(str).size() > 16) {
            return;
        }
        Set credentialDescriptions = registerCredentialDescriptionRequest.getCredentialDescriptions();
        int size = this.mCredentialDescriptions.get(str).size();
        this.mCredentialDescriptions.get(str).addAll(credentialDescriptions);
        this.mTotalDescriptionCount += this.mCredentialDescriptions.get(str).size() - size;
    }

    public void executeUnregisterRequest(UnregisterCredentialDescriptionRequest unregisterCredentialDescriptionRequest, String str) {
        if (this.mCredentialDescriptions.containsKey(str)) {
            int size = this.mCredentialDescriptions.get(str).size();
            this.mCredentialDescriptions.get(str).removeAll(unregisterCredentialDescriptionRequest.getCredentialDescriptions());
            this.mTotalDescriptionCount -= size - this.mCredentialDescriptions.get(str).size();
        }
    }

    public Set<FilterResult> getFilteredResultForProvider(String str, String str2) {
        HashSet hashSet = new HashSet();
        if (this.mCredentialDescriptions.containsKey(str)) {
            for (CredentialDescription credentialDescription : this.mCredentialDescriptions.get(str)) {
                if (str2.equals(credentialDescription.getFlattenedRequestString())) {
                    hashSet.add(new FilterResult(str, credentialDescription.getFlattenedRequestString(), credentialDescription.getCredentialEntries()));
                }
            }
            return hashSet;
        }
        return hashSet;
    }

    public Set<FilterResult> getMatchingProviders(Set<String> set) {
        HashSet hashSet = new HashSet();
        for (String str : this.mCredentialDescriptions.keySet()) {
            for (CredentialDescription credentialDescription : this.mCredentialDescriptions.get(str)) {
                if (set.contains(credentialDescription.getFlattenedRequestString())) {
                    hashSet.add(new FilterResult(str, credentialDescription.getFlattenedRequestString(), credentialDescription.getCredentialEntries()));
                }
            }
        }
        return hashSet;
    }

    public void evictProviderWithPackageName(String str) {
        if (this.mCredentialDescriptions.containsKey(str)) {
            this.mCredentialDescriptions.remove(str);
        }
    }
}
