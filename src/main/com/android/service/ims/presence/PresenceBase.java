package com.android.service.ims.presence;

import android.content.Context;
import android.content.Intent;
import com.android.ims.internal.Logger;
import com.android.service.ims.Task;
import com.android.service.ims.TaskManager;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
/* loaded from: classes.dex */
public class PresenceBase {
    public static final int PUBLISH_STATE_200_OK = 0;
    public static final int PUBLISH_STATE_NOT_PUBLISHED = 1;
    public static final int PUBLISH_STATE_OTHER_ERROR = 5;
    public static final int PUBLISH_STATE_RCS_PROVISION_ERROR = 3;
    public static final int PUBLISH_STATE_REQUEST_TIMEOUT = 4;
    public static final int PUBLISH_STATE_VOLTE_PROVISION_ERROR = 2;
    private static Logger logger = Logger.getLogger("PresenceBase");
    protected Context mContext;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface PresencePublishState {
    }

    public PresenceBase(Context context) {
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleCallback(Task task, int resultCode, boolean forCmdStatus) {
        if (task == null) {
            logger.debug("task == null");
            return;
        }
        if (task.mListener != null) {
            if (resultCode >= 0) {
                if (!forCmdStatus) {
                    task.mListener.onSuccess(task.mTaskId);
                }
            } else {
                task.mListener.onError(task.mTaskId, resultCode);
            }
        }
        if (resultCode != 0) {
            if (task instanceof PresencePublishTask) {
                PresencePublishTask publishTask = (PresencePublishTask) task;
                logger.debug("handleCallback for publishTask=" + publishTask);
                if (resultCode == 2) {
                    if (publishTask.getRetryCount() < 3) {
                        publishTask.setRetryCount(publishTask.getRetryCount() + 1);
                        return;
                    }
                    logger.debug("handleCallback remove task=" + task);
                    TaskManager.getDefault().removeTask(task.mTaskId);
                    return;
                }
                logger.debug("handleCallback remove task=" + task);
                TaskManager.getDefault().removeTask(task.mTaskId);
                return;
            }
            logger.debug("handleCallback remove task=" + task);
            TaskManager.getDefault().removeTask(task.mTaskId);
        } else if (forCmdStatus || (!forCmdStatus && (task instanceof PresenceCapabilityTask))) {
            logger.debug("handleCallback remove task later");
            if (!forCmdStatus) {
                ((PresenceCapabilityTask) task).setWaitingForNotify(true);
            }
        } else if (!forCmdStatus && (task instanceof PresenceAvailabilityTask) && resultCode == 0) {
            logger.debug("handleCallback PresenceAvailabilityTask cache for 60s task=" + task);
        } else {
            logger.debug("handleCallback remove task=" + task);
            TaskManager.getDefault().removeTask(task.mTaskId);
        }
    }

    public void onCommandStatusUpdated(int taskId, int requestId, int resultCode) {
        Task task = TaskManager.getDefault().getTask(taskId);
        if (task != null) {
            task.mSipRequestId = requestId;
            task.mCmdStatus = resultCode;
            TaskManager.getDefault().putTask(task.mTaskId, task);
        }
        handleCallback(task, resultCode, true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void notifyDm() {
        logger.debug("notifyDm");
        Intent intent = new Intent("com.android.internal.intent.action.ACTION_FORBIDDEN_NO_SERVICE_AUTHORIZATION");
        intent.addFlags(536870912);
        this.mContext.sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isInConfigList(int errorNo, String phrase, String[] errorArray) {
        String inErrorString = ("" + errorNo).trim();
        logger.debug("errorArray length=" + errorArray.length + " errorArray=" + Arrays.toString(errorArray));
        for (String errorStr : errorArray) {
            if (errorStr != null && errorStr.startsWith(inErrorString)) {
                String errorPhrase = errorStr.substring(inErrorString.length());
                if (errorPhrase == null || errorPhrase.isEmpty()) {
                    return true;
                }
                if (phrase == null || phrase.isEmpty()) {
                    return false;
                }
                return phrase.toLowerCase().contains(errorPhrase.toLowerCase());
            }
        }
        return false;
    }
}
