package com.android.ims.rcs.uce.request;

import android.net.Uri;
import android.util.Log;
import com.android.ims.rcs.uce.request.ContactThrottlingList;
import com.android.ims.rcs.uce.util.UceUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class ContactThrottlingList {
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "ThrottlingList";
    private final int mSubId;
    private final List<ContactInfo> mThrottlingList = new ArrayList();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ContactInfo {
        Uri mContactUri;
        int mSipCode;
        Instant mThrottleEndTimestamp;

        public ContactInfo(Uri contactUri, int sipCode, Instant timestamp) {
            this.mContactUri = contactUri;
            this.mSipCode = sipCode;
            this.mThrottleEndTimestamp = timestamp;
        }
    }

    public ContactThrottlingList(int subId) {
        this.mSubId = subId;
    }

    public synchronized void reset() {
        this.mThrottlingList.clear();
    }

    public synchronized void addToThrottlingList(List<Uri> uriList, final int sipCode) {
        cleanUpExpiredContacts();
        List<Uri> addToThrottlingList = getNotInThrottlingListUris(uriList);
        long expiration = UceUtils.getAvailabilityCacheExpiration(this.mSubId);
        final Instant timestamp = Instant.now().plusSeconds(expiration);
        List<ContactInfo> list = (List) addToThrottlingList.stream().map(new Function() { // from class: com.android.ims.rcs.uce.request.ContactThrottlingList$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ContactThrottlingList.lambda$addToThrottlingList$0(sipCode, timestamp, (Uri) obj);
            }
        }).collect(Collectors.toList());
        int previousSize = this.mThrottlingList.size();
        this.mThrottlingList.addAll(list);
        logd("addToThrottlingList: previous size=" + previousSize + ", current size=" + this.mThrottlingList.size() + ", expired time=" + timestamp);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ ContactInfo lambda$addToThrottlingList$0(int sipCode, Instant timestamp, Uri uri) {
        return new ContactInfo(uri, sipCode, timestamp);
    }

    private synchronized List<Uri> getNotInThrottlingListUris(List<Uri> uriList) {
        List<Uri> addToThrottlingUris;
        Collection<?> throttlingUris = (List) this.mThrottlingList.stream().map(new Function() { // from class: com.android.ims.rcs.uce.request.ContactThrottlingList$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Uri uri;
                uri = ((ContactThrottlingList.ContactInfo) obj).mContactUri;
                return uri;
            }
        }).collect(Collectors.toList());
        addToThrottlingUris = new ArrayList<>(uriList);
        addToThrottlingUris.removeAll(throttlingUris);
        return addToThrottlingUris;
    }

    public synchronized List<Uri> getInThrottlingListUris(List<Uri> uriList) {
        cleanUpExpiredContacts();
        return (List) uriList.stream().filter(new Predicate() { // from class: com.android.ims.rcs.uce.request.ContactThrottlingList$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getInThrottlingListUris$3;
                lambda$getInThrottlingListUris$3 = ContactThrottlingList.this.lambda$getInThrottlingListUris$3((Uri) obj);
                return lambda$getInThrottlingListUris$3;
            }
        }).collect(Collectors.toList());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$getInThrottlingListUris$3(final Uri uri) {
        return this.mThrottlingList.stream().anyMatch(new Predicate() { // from class: com.android.ims.rcs.uce.request.ContactThrottlingList$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean equals;
                equals = ((ContactThrottlingList.ContactInfo) obj).mContactUri.equals(uri);
                return equals;
            }
        });
    }

    private synchronized void cleanUpExpiredContacts() {
        int previousSize = this.mThrottlingList.size();
        List<ContactInfo> expiredContacts = (List) this.mThrottlingList.stream().filter(new Predicate() { // from class: com.android.ims.rcs.uce.request.ContactThrottlingList$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean isAfter;
                isAfter = Instant.now().isAfter(((ContactThrottlingList.ContactInfo) obj).mThrottleEndTimestamp);
                return isAfter;
            }
        }).collect(Collectors.toList());
        this.mThrottlingList.removeAll(expiredContacts);
        logd("cleanUpExpiredContacts: previous size=" + previousSize + ", current size=" + this.mThrottlingList.size());
    }

    private void logd(String log) {
        Log.d(LOG_TAG, getLogPrefix().append(log).toString());
    }

    private StringBuilder getLogPrefix() {
        StringBuilder builder = new StringBuilder("[");
        builder.append(this.mSubId);
        builder.append("] ");
        return builder;
    }
}
