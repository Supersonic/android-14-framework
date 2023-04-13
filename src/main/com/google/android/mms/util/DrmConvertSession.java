package com.google.android.mms.util;

import android.content.Context;
import android.drm.DrmConvertedStatus;
import android.drm.DrmManagerClient;
import android.media.MediaMetrics;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
/* loaded from: classes5.dex */
public class DrmConvertSession {
    public static final int STATUS_FILE_ERROR = 492;
    public static final int STATUS_NOT_ACCEPTABLE = 406;
    public static final int STATUS_SUCCESS = 200;
    public static final int STATUS_UNKNOWN_ERROR = 491;
    private static final String TAG = "DrmConvertSession";
    private int mConvertSessionId;
    private DrmManagerClient mDrmClient;

    private DrmConvertSession(DrmManagerClient drmClient, int convertSessionId) {
        this.mDrmClient = drmClient;
        this.mConvertSessionId = convertSessionId;
    }

    public static DrmConvertSession open(Context context, String mimeType) {
        String str = TAG;
        DrmManagerClient drmClient = null;
        int convertSessionId = -1;
        if (context != null && mimeType != null && !mimeType.equals("")) {
            try {
                drmClient = new DrmManagerClient(context);
                try {
                    int convertSessionId2 = drmClient.openConvertSession(mimeType);
                    convertSessionId = convertSessionId2;
                    str = convertSessionId2;
                } catch (IllegalArgumentException e) {
                    Log.m103w(TAG, "Conversion of Mimetype: " + mimeType + " is not supported.", e);
                    str = str;
                } catch (IllegalStateException e2) {
                    Log.m103w(TAG, "Could not access Open DrmFramework.", e2);
                    str = str;
                }
            } catch (IllegalArgumentException e3) {
                Log.m104w(str, "DrmManagerClient instance could not be created, context is Illegal.");
            } catch (IllegalStateException e4) {
                Log.m104w(str, "DrmManagerClient didn't initialize properly.");
            }
        }
        if (drmClient == null || convertSessionId < 0) {
            return null;
        }
        return new DrmConvertSession(drmClient, convertSessionId);
    }

    public byte[] convert(byte[] inBuffer, int size) {
        DrmConvertedStatus convertedStatus;
        if (inBuffer != null) {
            try {
                if (size != inBuffer.length) {
                    byte[] buf = new byte[size];
                    System.arraycopy(inBuffer, 0, buf, 0, size);
                    convertedStatus = this.mDrmClient.convertData(this.mConvertSessionId, buf);
                } else {
                    convertedStatus = this.mDrmClient.convertData(this.mConvertSessionId, inBuffer);
                }
                if (convertedStatus == null || convertedStatus.statusCode != 1 || convertedStatus.convertedData == null) {
                    return null;
                }
                byte[] result = convertedStatus.convertedData;
                return result;
            } catch (IllegalArgumentException e) {
                Log.m103w(TAG, "Buffer with data to convert is illegal. Convertsession: " + this.mConvertSessionId, e);
                return null;
            } catch (IllegalStateException e2) {
                Log.m103w(TAG, "Could not convert data. Convertsession: " + this.mConvertSessionId, e2);
                return null;
            }
        }
        throw new IllegalArgumentException("Parameter inBuffer is null");
    }

    public int close(String filename) {
        int i;
        String str;
        int result = 491;
        DrmManagerClient drmManagerClient = this.mDrmClient;
        if (drmManagerClient == null || (i = this.mConvertSessionId) < 0) {
            return 491;
        }
        try {
            DrmConvertedStatus convertedStatus = drmManagerClient.closeConvertSession(i);
            if (convertedStatus == null || convertedStatus.statusCode != 1 || convertedStatus.convertedData == null) {
                return 406;
            }
            RandomAccessFile rndAccessFile = null;
            try {
                try {
                    try {
                        try {
                            rndAccessFile = new RandomAccessFile(filename, "rw");
                            rndAccessFile.seek(convertedStatus.offset);
                            rndAccessFile.write(convertedStatus.convertedData);
                            result = 200;
                            try {
                                rndAccessFile.close();
                                return 200;
                            } catch (IOException e) {
                                e = e;
                                result = 492;
                                str = "Failed to close File:" + filename + MediaMetrics.SEPARATOR;
                                Log.m103w(TAG, str, e);
                                return result;
                            }
                        } catch (IOException e2) {
                            result = 492;
                            Log.m103w(TAG, "Could not access File: " + filename + " .", e2);
                            if (rndAccessFile != null) {
                                try {
                                    rndAccessFile.close();
                                    return 492;
                                } catch (IOException e3) {
                                    e = e3;
                                    result = 492;
                                    str = "Failed to close File:" + filename + MediaMetrics.SEPARATOR;
                                    Log.m103w(TAG, str, e);
                                    return result;
                                }
                            }
                            return 492;
                        }
                    } catch (IllegalArgumentException e4) {
                        result = 492;
                        Log.m103w(TAG, "Could not open file in mode: rw", e4);
                        if (rndAccessFile != null) {
                            try {
                                rndAccessFile.close();
                                return 492;
                            } catch (IOException e5) {
                                e = e5;
                                result = 492;
                                str = "Failed to close File:" + filename + MediaMetrics.SEPARATOR;
                                Log.m103w(TAG, str, e);
                                return result;
                            }
                        }
                        return 492;
                    }
                } catch (FileNotFoundException e6) {
                    result = 492;
                    Log.m103w(TAG, "File: " + filename + " could not be found.", e6);
                    if (rndAccessFile != null) {
                        try {
                            rndAccessFile.close();
                            return 492;
                        } catch (IOException e7) {
                            e = e7;
                            result = 492;
                            str = "Failed to close File:" + filename + MediaMetrics.SEPARATOR;
                            Log.m103w(TAG, str, e);
                            return result;
                        }
                    }
                    return 492;
                }
            } catch (SecurityException e8) {
                Log.m103w(TAG, "Access to File: " + filename + " was denied denied by SecurityManager.", e8);
                if (rndAccessFile != null) {
                    try {
                        rndAccessFile.close();
                        return 491;
                    } catch (IOException e9) {
                        e = e9;
                        result = 492;
                        str = "Failed to close File:" + filename + MediaMetrics.SEPARATOR;
                        Log.m103w(TAG, str, e);
                        return result;
                    }
                }
                return 491;
            }
        } catch (IllegalStateException e10) {
            Log.m103w(TAG, "Could not close convertsession. Convertsession: " + this.mConvertSessionId, e10);
            return result;
        }
    }
}
