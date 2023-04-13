package com.android.commands.monkey;

import android.app.IActivityManager;
import android.util.Log;
import android.view.IWindowManager;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public class MonkeyGetFrameRateEvent extends MonkeyEvent {
    private static final String LOG_FILE = "/sdcard/avgFrameRateOut.txt";
    private static final String TAG = "MonkeyGetFrameRateEvent";
    private static float mDuration;
    private static int mEndFrameNo;
    private static long mEndTime;
    private static int mStartFrameNo;
    private static long mStartTime;
    private String GET_FRAMERATE_CMD;
    private String mStatus;
    private static String mTestCaseName = null;
    private static final Pattern NO_OF_FRAMES_PATTERN = Pattern.compile(".*\\(([a-f[A-F][0-9]].*?)\\s.*\\)");

    public MonkeyGetFrameRateEvent(String status, String testCaseName) {
        super(4);
        this.GET_FRAMERATE_CMD = "service call SurfaceFlinger 1013";
        this.mStatus = status;
        mTestCaseName = testCaseName;
    }

    public MonkeyGetFrameRateEvent(String status) {
        super(4);
        this.GET_FRAMERATE_CMD = "service call SurfaceFlinger 1013";
        this.mStatus = status;
    }

    private float getAverageFrameRate(int totalNumberOfFrame, float duration) {
        if (duration <= 0.0f) {
            return 0.0f;
        }
        float avgFrameRate = totalNumberOfFrame / duration;
        return avgFrameRate;
    }

    private void writeAverageFrameRate() {
        String str = "IOException ";
        FileWriter writer = null;
        try {
            try {
                writer = new FileWriter(LOG_FILE, true);
                int totalNumberOfFrame = mEndFrameNo - mStartFrameNo;
                float avgFrameRate = getAverageFrameRate(totalNumberOfFrame, mDuration);
                writer.write(String.format("%s:%.2f\n", mTestCaseName, Float.valueOf(avgFrameRate)));
                writer.close();
                try {
                    writer.close();
                } catch (IOException e) {
                    str = "IOException " + e.toString();
                    Log.e(TAG, str);
                }
            } catch (IOException e2) {
                Log.w(TAG, "Can't write sdcard log file", e2);
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e3) {
                        Log.e(TAG, "IOException " + e3.toString());
                    }
                }
            }
        } catch (Throwable th) {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e4) {
                    Log.e(TAG, str + e4.toString());
                }
            }
            throw th;
        }
    }

    private String getNumberOfFrames(String input) {
        Matcher m = NO_OF_FRAMES_PATTERN.matcher(input);
        if (!m.matches()) {
            return null;
        }
        String noOfFrames = m.group(1);
        return noOfFrames;
    }

    @Override // com.android.commands.monkey.MonkeyEvent
    public int injectEvent(IWindowManager iwm, IActivityManager iam, int verbose) {
        Process p = null;
        BufferedReader result = null;
        try {
            try {
                try {
                    Process p2 = Runtime.getRuntime().exec(this.GET_FRAMERATE_CMD);
                    int status = p2.waitFor();
                    if (status != 0) {
                        Logger.err.println(String.format("// Shell command %s status was %s", this.GET_FRAMERATE_CMD, Integer.valueOf(status)));
                    }
                    BufferedReader result2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
                    String output = result2.readLine();
                    if (output != null) {
                        String str = this.mStatus;
                        if (str == "start") {
                            mStartFrameNo = Integer.parseInt(getNumberOfFrames(output), 16);
                            mStartTime = System.currentTimeMillis();
                        } else if (str == "end") {
                            mEndFrameNo = Integer.parseInt(getNumberOfFrames(output), 16);
                            long currentTimeMillis = System.currentTimeMillis();
                            mEndTime = currentTimeMillis;
                            long diff = currentTimeMillis - mStartTime;
                            mDuration = (float) (diff / 1000.0d);
                            writeAverageFrameRate();
                        }
                    }
                    result2.close();
                    if (p2 != null) {
                        p2.destroy();
                    }
                } catch (IOException e) {
                    Logger.err.println(e.toString());
                }
            } catch (Exception e2) {
                Logger.err.println("// Exception from " + this.GET_FRAMERATE_CMD + ":");
                Logger.err.println(e2.toString());
                if (0 != 0) {
                    result.close();
                }
                if (0 != 0) {
                    p.destroy();
                }
            }
            return 1;
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    result.close();
                } catch (IOException e3) {
                    Logger.err.println(e3.toString());
                    throw th;
                }
            }
            if (0 != 0) {
                p.destroy();
            }
            throw th;
        }
    }
}
