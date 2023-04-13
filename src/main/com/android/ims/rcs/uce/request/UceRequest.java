package com.android.ims.rcs.uce.request;

import android.net.Uri;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
/* loaded from: classes.dex */
public interface UceRequest {
    public static final int REQUEST_TYPE_AVAILABILITY = 2;
    public static final int REQUEST_TYPE_CAPABILITY = 1;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface UceRequestType {
    }

    void executeRequest();

    long getRequestCoordinatorId();

    long getTaskId();

    void onFinish();

    void setContactUri(List<Uri> list);

    void setRequestCoordinatorId(long j);
}
