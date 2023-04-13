package com.android.server.job.controllers;

import com.android.server.job.JobSchedulerService;
/* loaded from: classes.dex */
public abstract class RestrictingController extends StateController {
    public abstract void startTrackingRestrictedJobLocked(JobStatus jobStatus);

    public abstract void stopTrackingRestrictedJobLocked(JobStatus jobStatus);

    public RestrictingController(JobSchedulerService jobSchedulerService) {
        super(jobSchedulerService);
    }
}
