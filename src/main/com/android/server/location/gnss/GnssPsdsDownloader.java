package com.android.server.location.gnss;

import android.net.TrafficStats;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class GnssPsdsDownloader {
    public static final int CONNECTION_TIMEOUT_MS;
    public static final boolean DEBUG = Log.isLoggable("GnssPsdsDownloader", 3);
    public static final int READ_TIMEOUT_MS;
    public final String[] mLongTermPsdsServers;
    public int mNextServerIndex;
    public final String[] mPsdsServers;

    static {
        TimeUnit timeUnit = TimeUnit.SECONDS;
        CONNECTION_TIMEOUT_MS = (int) timeUnit.toMillis(30L);
        READ_TIMEOUT_MS = (int) timeUnit.toMillis(60L);
    }

    public GnssPsdsDownloader(Properties properties) {
        String property = properties.getProperty("LONGTERM_PSDS_SERVER_1");
        String property2 = properties.getProperty("LONGTERM_PSDS_SERVER_2");
        String property3 = properties.getProperty("LONGTERM_PSDS_SERVER_3");
        int i = 1;
        int i2 = property != null ? 1 : 0;
        i2 = property2 != null ? i2 + 1 : i2;
        i2 = property3 != null ? i2 + 1 : i2;
        if (i2 == 0) {
            Log.e("GnssPsdsDownloader", "No Long-Term PSDS servers were specified in the GnssConfiguration");
            this.mLongTermPsdsServers = null;
        } else {
            String[] strArr = new String[i2];
            this.mLongTermPsdsServers = strArr;
            if (property != null) {
                strArr[0] = property;
            } else {
                i = 0;
            }
            if (property2 != null) {
                strArr[i] = property2;
                i++;
            }
            if (property3 != null) {
                strArr[i] = property3;
                i++;
            }
            this.mNextServerIndex = new Random().nextInt(i);
        }
        String property4 = properties.getProperty("NORMAL_PSDS_SERVER");
        String property5 = properties.getProperty("REALTIME_PSDS_SERVER");
        String[] strArr2 = new String[4];
        this.mPsdsServers = strArr2;
        strArr2[2] = property4;
        strArr2[3] = property5;
    }

    public byte[] downloadPsdsData(int i) {
        int i2 = this.mNextServerIndex;
        byte[] bArr = null;
        if (i == 1 && this.mLongTermPsdsServers == null) {
            return null;
        }
        if (i <= 1 || i > 3 || this.mPsdsServers[i] != null) {
            if (i != 1) {
                if (i <= 1 || i > 3) {
                    return null;
                }
                return doDownloadWithTrafficAccounted(this.mPsdsServers[i]);
            }
            while (bArr == null) {
                bArr = doDownloadWithTrafficAccounted(this.mLongTermPsdsServers[this.mNextServerIndex]);
                int i3 = this.mNextServerIndex + 1;
                this.mNextServerIndex = i3;
                if (i3 == this.mLongTermPsdsServers.length) {
                    this.mNextServerIndex = 0;
                }
                if (this.mNextServerIndex == i2) {
                    return bArr;
                }
            }
            return bArr;
        }
        return null;
    }

    public final byte[] doDownloadWithTrafficAccounted(String str) {
        int andSetThreadStatsTag = TrafficStats.getAndSetThreadStatsTag(-188);
        try {
            return doDownload(str);
        } finally {
            TrafficStats.setThreadStatsTag(andSetThreadStatsTag);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:51:0x00c8  */
    /* JADX WARN: Type inference failed for: r1v0 */
    /* JADX WARN: Type inference failed for: r1v1, types: [java.net.HttpURLConnection] */
    /* JADX WARN: Type inference failed for: r1v2 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final byte[] doDownload(String str) {
        HttpURLConnection httpURLConnection;
        boolean z = DEBUG;
        if (z) {
            Log.d("GnssPsdsDownloader", "Downloading PSDS data from " + str);
        }
        ?? r1 = 0;
        try {
            try {
                httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
                try {
                    httpURLConnection.setRequestProperty("Accept", "*/*, application/vnd.wap.mms-message, application/vnd.wap.sic");
                    httpURLConnection.setRequestProperty("x-wap-profile", "http://www.openmobilealliance.org/tech/profiles/UAPROF/ccppschema-20021212#");
                    httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT_MS);
                    httpURLConnection.setReadTimeout(READ_TIMEOUT_MS);
                    httpURLConnection.connect();
                    int responseCode = httpURLConnection.getResponseCode();
                    if (responseCode != 200) {
                        if (z) {
                            Log.d("GnssPsdsDownloader", "HTTP error downloading gnss PSDS: " + responseCode);
                        }
                        httpURLConnection.disconnect();
                        return null;
                    }
                    InputStream inputStream = httpURLConnection.getInputStream();
                    try {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        byte[] bArr = new byte[1024];
                        do {
                            int read = inputStream.read(bArr);
                            if (read == -1) {
                                byte[] byteArray = byteArrayOutputStream.toByteArray();
                                inputStream.close();
                                httpURLConnection.disconnect();
                                return byteArray;
                            }
                            byteArrayOutputStream.write(bArr, 0, read);
                        } while (byteArrayOutputStream.size() <= 1000000);
                        if (DEBUG) {
                            Log.d("GnssPsdsDownloader", "PSDS file too large");
                        }
                        inputStream.close();
                        httpURLConnection.disconnect();
                        return null;
                    } catch (Throwable th) {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                } catch (IOException e) {
                    e = e;
                    if (DEBUG) {
                        Log.d("GnssPsdsDownloader", "Error downloading gnss PSDS: ", e);
                    }
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    return null;
                }
            } catch (Throwable th3) {
                th = th3;
                r1 = str;
                if (r1 != 0) {
                    r1.disconnect();
                }
                throw th;
            }
        } catch (IOException e2) {
            e = e2;
            httpURLConnection = null;
        } catch (Throwable th4) {
            th = th4;
            if (r1 != 0) {
            }
            throw th;
        }
    }
}
