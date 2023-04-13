package com.android.internal.telephony.uicc;

import android.compat.annotation.UnsupportedAppUsage;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.CommandsInterface;
import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class IccFileHandler extends Handler implements IccConstants {
    protected static final int COMMAND_GET_RESPONSE = 192;
    protected static final int COMMAND_READ_BINARY = 176;
    protected static final int COMMAND_READ_RECORD = 178;
    protected static final int COMMAND_SEEK = 162;
    protected static final int COMMAND_UPDATE_BINARY = 214;
    protected static final int COMMAND_UPDATE_RECORD = 220;
    protected static final int EF_TYPE_CYCLIC = 3;
    protected static final int EF_TYPE_LINEAR_FIXED = 1;
    protected static final int EF_TYPE_TRANSPARENT = 0;
    protected static final int EVENT_GET_BINARY_SIZE_DONE = 4;
    protected static final int EVENT_GET_EF_LINEAR_RECORD_SIZE_DONE = 8;
    protected static final int EVENT_GET_EF_TRANSPARENT_SIZE_DONE = 12;
    protected static final int EVENT_GET_RECORD_SIZE_DONE = 6;
    protected static final int EVENT_GET_RECORD_SIZE_IMG_DONE = 11;
    protected static final int EVENT_READ_BINARY_DONE = 5;
    protected static final int EVENT_READ_ICON_DONE = 10;
    protected static final int EVENT_READ_IMG_DONE = 9;
    protected static final int EVENT_READ_RECORD_DONE = 7;
    protected static final int GET_RESPONSE_EF_IMG_SIZE_BYTES = 10;
    protected static final int GET_RESPONSE_EF_SIZE_BYTES = 15;
    protected static final int READ_RECORD_MODE_ABSOLUTE = 4;
    protected static final int RESPONSE_DATA_ACCESS_CONDITION_1 = 8;
    protected static final int RESPONSE_DATA_ACCESS_CONDITION_2 = 9;
    protected static final int RESPONSE_DATA_ACCESS_CONDITION_3 = 10;
    protected static final int RESPONSE_DATA_FILE_ID_1 = 4;
    protected static final int RESPONSE_DATA_FILE_ID_2 = 5;
    protected static final int RESPONSE_DATA_FILE_SIZE_1 = 2;
    protected static final int RESPONSE_DATA_FILE_SIZE_2 = 3;
    protected static final int RESPONSE_DATA_FILE_STATUS = 11;
    protected static final int RESPONSE_DATA_FILE_TYPE = 6;
    protected static final int RESPONSE_DATA_LENGTH = 12;
    protected static final int RESPONSE_DATA_RECORD_LENGTH = 14;
    protected static final int RESPONSE_DATA_RFU_1 = 0;
    protected static final int RESPONSE_DATA_RFU_2 = 1;
    protected static final int RESPONSE_DATA_RFU_3 = 7;
    protected static final int RESPONSE_DATA_STRUCTURE = 13;
    protected static final int TYPE_DF = 2;
    protected static final int TYPE_EF = 4;
    protected static final int TYPE_MF = 1;
    protected static final int TYPE_RFU = 0;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected final String mAid;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected final CommandsInterface mCi;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected final UiccCardApplication mParentApp;

    public void dispose() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getCommonIccEFPath(int i) {
        if (i == 12037 || i == 12258) {
            return IccConstants.MF_SIM;
        }
        if (i != 20256) {
            if (i != 20272) {
                if (i == 28480 || i == 28645 || i == 28474 || i == 28475) {
                    return "3F007F10";
                }
                switch (i) {
                    case IccConstants.EF_SDN /* 28489 */:
                    case IccConstants.EF_EXT1 /* 28490 */:
                    case IccConstants.EF_EXT2 /* 28491 */:
                    case IccConstants.EF_EXT3 /* 28492 */:
                        return "3F007F10";
                    default:
                        return null;
                }
            }
            return "3F007F105F3A";
        }
        return "3F007F105F50";
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected abstract String getEFPath(int i);

    protected abstract void logd(String str);

    protected abstract void loge(String str);

    /* loaded from: classes.dex */
    public static class LoadLinearFixedContext {
        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        int mCountRecords;
        int mEfid;
        boolean mLoadAll;
        Message mOnLoaded;
        String mPath;
        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        int mRecordNum;
        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        int mRecordSize;
        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        ArrayList<byte[]> results;

        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        LoadLinearFixedContext(int i, int i2, Message message) {
            this.mEfid = i;
            this.mRecordNum = i2;
            this.mOnLoaded = message;
            this.mLoadAll = false;
            this.mPath = null;
        }

        LoadLinearFixedContext(int i, int i2, String str, Message message) {
            this.mEfid = i;
            this.mRecordNum = i2;
            this.mOnLoaded = message;
            this.mLoadAll = false;
            this.mPath = str;
        }

        LoadLinearFixedContext(int i, String str, Message message) {
            this.mEfid = i;
            this.mRecordNum = 1;
            this.mLoadAll = true;
            this.mOnLoaded = message;
            this.mPath = str;
        }
    }

    @VisibleForTesting
    public int getEfid(LoadLinearFixedContext loadLinearFixedContext) {
        return loadLinearFixedContext.mEfid;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public IccFileHandler(UiccCardApplication uiccCardApplication, String str, CommandsInterface commandsInterface) {
        this.mParentApp = uiccCardApplication;
        this.mAid = str;
        this.mCi = commandsInterface;
    }

    @VisibleForTesting
    public IccFileHandler(CommandsInterface commandsInterface) {
        this.mParentApp = null;
        this.mAid = null;
        this.mCi = commandsInterface;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void loadEFLinearFixed(int i, String str, int i2, Message message) {
        if (str == null) {
            str = getEFPath(i);
        }
        String str2 = str;
        this.mCi.iccIOForApp(192, i, str2, 0, 0, 15, null, null, this.mAid, obtainMessage(6, new LoadLinearFixedContext(i, i2, str2, message)));
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void loadEFLinearFixed(int i, int i2, Message message) {
        loadEFLinearFixed(i, getEFPath(i), i2, message);
    }

    public void loadEFImgLinearFixed(int i, Message message) {
        this.mCi.iccIOForApp(192, 20256, getEFPath(20256), i, 4, 10, null, null, this.mAid, obtainMessage(11, new LoadLinearFixedContext(20256, i, message)));
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void getEFLinearRecordSize(int i, String str, Message message) {
        if (str == null) {
            str = getEFPath(i);
        }
        String str2 = str;
        this.mCi.iccIOForApp(192, i, str2, 0, 0, 15, null, null, this.mAid, obtainMessage(8, new LoadLinearFixedContext(i, str2, message)));
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void getEFLinearRecordSize(int i, Message message) {
        getEFLinearRecordSize(i, getEFPath(i), message);
    }

    public void getEFTransparentRecordSize(int i, Message message) {
        this.mCi.iccIOForApp(192, i, getEFPath(i), 0, 0, 15, null, null, this.mAid, obtainMessage(12, i, 0, message));
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void loadEFLinearFixedAll(int i, String str, Message message) {
        if (str == null) {
            str = getEFPath(i);
        }
        String str2 = str;
        this.mCi.iccIOForApp(192, i, str2, 0, 0, 15, null, null, this.mAid, obtainMessage(6, new LoadLinearFixedContext(i, str2, message)));
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void loadEFLinearFixedAll(int i, Message message) {
        loadEFLinearFixedAll(i, getEFPath(i), message);
    }

    @UnsupportedAppUsage
    public void loadEFTransparent(int i, Message message) {
        this.mCi.iccIOForApp(192, i, getEFPath(i), 0, 0, 15, null, null, this.mAid, obtainMessage(4, i, 0, message));
    }

    public void loadEFTransparent(int i, int i2, Message message) {
        this.mCi.iccIOForApp(COMMAND_READ_BINARY, i, getEFPath(i), 0, 0, i2, null, null, this.mAid, obtainMessage(5, i, 0, message));
    }

    public void loadEFImgTransparent(int i, int i2, int i3, int i4, Message message) {
        Message obtainMessage = obtainMessage(10, i, 0, message);
        logd("IccFileHandler: loadEFImgTransparent fileid = " + i + " filePath = " + getEFPath(20256) + " highOffset = " + i2 + " lowOffset = " + i3 + " length = " + i4);
        this.mCi.iccIOForApp(COMMAND_READ_BINARY, i, getEFPath(20256), i2, i3, i4, null, null, this.mAid, obtainMessage);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void updateEFLinearFixed(int i, String str, int i2, byte[] bArr, String str2, Message message) {
        this.mCi.iccIOForApp(COMMAND_UPDATE_RECORD, i, str == null ? getEFPath(i) : str, i2, 4, bArr.length, IccUtils.bytesToHexString(bArr), str2, this.mAid, message);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void updateEFLinearFixed(int i, int i2, byte[] bArr, String str, Message message) {
        this.mCi.iccIOForApp(COMMAND_UPDATE_RECORD, i, getEFPath(i), i2, 4, bArr.length, IccUtils.bytesToHexString(bArr), str, this.mAid, message);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void updateEFTransparent(int i, byte[] bArr, Message message) {
        this.mCi.iccIOForApp(214, i, getEFPath(i), 0, 0, bArr.length, IccUtils.bytesToHexString(bArr), null, this.mAid, message);
    }

    private void sendResult(Message message, Object obj, Throwable th) {
        if (message == null) {
            return;
        }
        AsyncResult.forMessage(message, obj, th);
        message.sendToTarget();
    }

    private boolean processException(Message message, AsyncResult asyncResult) {
        IccIoResult iccIoResult = (IccIoResult) asyncResult.result;
        Throwable th = asyncResult.exception;
        if (th != null) {
            sendResult(message, null, th);
            return true;
        }
        IccException exception = iccIoResult.getException();
        if (exception != null) {
            sendResult(message, null, exception);
            return true;
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:92:0x01ed  */
    /* JADX WARN: Removed duplicated region for block: B:93:0x01f1  */
    /* JADX WARN: Type inference failed for: r4v0 */
    /* JADX WARN: Type inference failed for: r4v10 */
    /* JADX WARN: Type inference failed for: r4v11 */
    /* JADX WARN: Type inference failed for: r4v2 */
    /* JADX WARN: Type inference failed for: r4v5 */
    @Override // android.os.Handler
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void handleMessage(Message message) {
        Message message2;
        try {
            message2 = 7;
            try {
                switch (message.what) {
                    case 4:
                        Object obj = message.obj;
                        AsyncResult asyncResult = (AsyncResult) obj;
                        Message message3 = (Message) asyncResult.userObj;
                        try {
                            IccIoResult iccIoResult = (IccIoResult) asyncResult.result;
                            if (!processException(message3, (AsyncResult) obj)) {
                                byte[] bArr = iccIoResult.payload;
                                int i = message.arg1;
                                if (4 != bArr[6]) {
                                    throw new IccFileTypeMismatch();
                                }
                                if (bArr[13] != 0) {
                                    throw new IccFileTypeMismatch();
                                }
                                this.mCi.iccIOForApp(COMMAND_READ_BINARY, i, getEFPath(i), 0, 0, getDataFileSize(bArr), null, null, this.mAid, obtainMessage(5, i, 0, message3));
                            }
                            return;
                        } catch (Exception e) {
                            e = e;
                            message2 = message3;
                            if (message2 != 0) {
                                sendResult(message2, null, e);
                                return;
                            }
                            loge("uncaught exception" + e);
                            return;
                        }
                    case 5:
                    case 10:
                        Object obj2 = message.obj;
                        AsyncResult asyncResult2 = (AsyncResult) obj2;
                        Message message4 = (Message) asyncResult2.userObj;
                        IccIoResult iccIoResult2 = (IccIoResult) asyncResult2.result;
                        if (!processException(message4, (AsyncResult) obj2)) {
                            sendResult(message4, iccIoResult2.payload, null);
                        }
                        return;
                    case 6:
                    case 11:
                        Object obj3 = message.obj;
                        AsyncResult asyncResult3 = (AsyncResult) obj3;
                        LoadLinearFixedContext loadLinearFixedContext = (LoadLinearFixedContext) asyncResult3.userObj;
                        IccIoResult iccIoResult3 = (IccIoResult) asyncResult3.result;
                        Message message5 = loadLinearFixedContext.mOnLoaded;
                        try {
                            if (processException(message5, (AsyncResult) obj3)) {
                                loge("exception caught from EVENT_GET_RECORD_SIZE");
                            } else {
                                byte[] bArr2 = iccIoResult3.payload;
                                String str = loadLinearFixedContext.mPath;
                                if (4 != bArr2[6]) {
                                    throw new IccFileTypeMismatch();
                                }
                                if (1 != bArr2[13]) {
                                    throw new IccFileTypeMismatch();
                                }
                                loadLinearFixedContext.mRecordSize = bArr2[14] & 255;
                                loadLinearFixedContext.mCountRecords = getDataFileSize(bArr2) / loadLinearFixedContext.mRecordSize;
                                if (loadLinearFixedContext.mLoadAll) {
                                    loadLinearFixedContext.results = new ArrayList<>(loadLinearFixedContext.mCountRecords);
                                }
                                if (str == null) {
                                    str = getEFPath(loadLinearFixedContext.mEfid);
                                }
                                this.mCi.iccIOForApp(COMMAND_READ_RECORD, loadLinearFixedContext.mEfid, str, loadLinearFixedContext.mRecordNum, 4, loadLinearFixedContext.mRecordSize, null, null, this.mAid, obtainMessage(7, loadLinearFixedContext));
                            }
                            return;
                        } catch (Exception e2) {
                            e = e2;
                            message2 = message5;
                            if (message2 != 0) {
                            }
                        }
                        break;
                    case 7:
                    case 9:
                        Object obj4 = message.obj;
                        AsyncResult asyncResult4 = (AsyncResult) obj4;
                        LoadLinearFixedContext loadLinearFixedContext2 = (LoadLinearFixedContext) asyncResult4.userObj;
                        IccIoResult iccIoResult4 = (IccIoResult) asyncResult4.result;
                        Message message6 = loadLinearFixedContext2.mOnLoaded;
                        try {
                            String str2 = loadLinearFixedContext2.mPath;
                            if (!processException(message6, (AsyncResult) obj4)) {
                                if (!loadLinearFixedContext2.mLoadAll) {
                                    sendResult(message6, iccIoResult4.payload, null);
                                } else {
                                    loadLinearFixedContext2.results.add(iccIoResult4.payload);
                                    int i2 = loadLinearFixedContext2.mRecordNum + 1;
                                    loadLinearFixedContext2.mRecordNum = i2;
                                    if (i2 > loadLinearFixedContext2.mCountRecords) {
                                        sendResult(message6, loadLinearFixedContext2.results, null);
                                    } else {
                                        if (str2 == null) {
                                            str2 = getEFPath(loadLinearFixedContext2.mEfid);
                                        }
                                        this.mCi.iccIOForApp(COMMAND_READ_RECORD, loadLinearFixedContext2.mEfid, str2, loadLinearFixedContext2.mRecordNum, 4, loadLinearFixedContext2.mRecordSize, null, null, this.mAid, obtainMessage(7, loadLinearFixedContext2));
                                    }
                                }
                            }
                            return;
                        } catch (Exception e3) {
                            e = e3;
                            message2 = message6;
                            if (message2 != 0) {
                            }
                        }
                        break;
                    case 8:
                        Object obj5 = message.obj;
                        AsyncResult asyncResult5 = (AsyncResult) obj5;
                        IccIoResult iccIoResult5 = (IccIoResult) asyncResult5.result;
                        Message message7 = ((LoadLinearFixedContext) asyncResult5.userObj).mOnLoaded;
                        if (!processException(message7, (AsyncResult) obj5)) {
                            byte[] bArr3 = iccIoResult5.payload;
                            if (4 != bArr3[6] || 1 != bArr3[13]) {
                                throw new IccFileTypeMismatch();
                            }
                            int dataFileSize = getDataFileSize(bArr3);
                            int[] iArr = {bArr3[14] & 255, dataFileSize, dataFileSize / iArr[0]};
                            sendResult(message7, iArr, null);
                        }
                        return;
                    case 12:
                        Object obj6 = message.obj;
                        AsyncResult asyncResult6 = (AsyncResult) obj6;
                        Message message8 = (Message) asyncResult6.userObj;
                        IccIoResult iccIoResult6 = (IccIoResult) asyncResult6.result;
                        if (!processException(message8, (AsyncResult) obj6)) {
                            byte[] bArr4 = iccIoResult6.payload;
                            if (4 != bArr4[6]) {
                                throw new IccFileTypeMismatch();
                            }
                            if (bArr4[13] != 0) {
                                throw new IccFileTypeMismatch();
                            }
                            sendResult(message8, Integer.valueOf(getDataFileSize(bArr4)), null);
                        }
                        return;
                    default:
                        return;
                }
            } catch (Exception e4) {
                e = e4;
            }
        } catch (Exception e5) {
            e = e5;
            message2 = 0;
        }
    }

    private static int getDataFileSize(byte[] bArr) {
        return ((bArr[2] & 255) << 8) + (bArr[3] & 255);
    }
}
