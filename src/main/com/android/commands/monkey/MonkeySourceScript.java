package com.android.commands.monkey;

import android.content.ComponentName;
import android.os.SystemClock;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;
import java.util.Random;
/* loaded from: classes.dex */
public class MonkeySourceScript implements MonkeyEventSource {
    private static final String EVENT_KEYWORD_ACTIVITY = "LaunchActivity";
    private static final String EVENT_KEYWORD_DEVICE_WAKEUP = "DeviceWakeUp";
    private static final String EVENT_KEYWORD_DRAG = "Drag";
    private static final String EVENT_KEYWORD_END_APP_FRAMERATE_CAPTURE = "EndCaptureAppFramerate";
    private static final String EVENT_KEYWORD_END_FRAMERATE_CAPTURE = "EndCaptureFramerate";
    private static final String EVENT_KEYWORD_FLIP = "DispatchFlip";
    private static final String EVENT_KEYWORD_INPUT_STRING = "DispatchString";
    private static final String EVENT_KEYWORD_INSTRUMENTATION = "LaunchInstrumentation";
    private static final String EVENT_KEYWORD_KEY = "DispatchKey";
    private static final String EVENT_KEYWORD_KEYPRESS = "DispatchPress";
    private static final String EVENT_KEYWORD_LONGPRESS = "LongPress";
    private static final String EVENT_KEYWORD_PINCH_ZOOM = "PinchZoom";
    private static final String EVENT_KEYWORD_POINTER = "DispatchPointer";
    private static final String EVENT_KEYWORD_POWERLOG = "PowerLog";
    private static final String EVENT_KEYWORD_PRESSANDHOLD = "PressAndHold";
    private static final String EVENT_KEYWORD_PROFILE_WAIT = "ProfileWait";
    private static final String EVENT_KEYWORD_ROTATION = "RotateScreen";
    private static final String EVENT_KEYWORD_RUNCMD = "RunCmd";
    private static final String EVENT_KEYWORD_START_APP_FRAMERATE_CAPTURE = "StartCaptureAppFramerate";
    private static final String EVENT_KEYWORD_START_FRAMERATE_CAPTURE = "StartCaptureFramerate";
    private static final String EVENT_KEYWORD_TAP = "Tap";
    private static final String EVENT_KEYWORD_TRACKBALL = "DispatchTrackball";
    private static final String EVENT_KEYWORD_WAIT = "UserWait";
    private static final String EVENT_KEYWORD_WRITEPOWERLOG = "WriteLog";
    private static final String HEADER_COUNT = "count=";
    private static final String HEADER_LINE_BY_LINE = "linebyline";
    private static final String HEADER_SPEED = "speed=";
    private static int LONGPRESS_WAIT_TIME = 2000;
    private static final int MAX_ONE_TIME_READS = 100;
    private static final long SLEEP_COMPENSATE_DIFF = 16;
    private static final String STARTING_DATA_LINE = "start data >>";
    private static final boolean THIS_DEBUG = false;
    BufferedReader mBufferedReader;
    private long mDeviceSleepTime;
    FileInputStream mFStream;
    DataInputStream mInputStream;
    private long mProfileWaitTime;

    /* renamed from: mQ */
    private MonkeyEventQueue f2mQ;
    private String mScriptFileName;
    private int mEventCountInScript = 0;
    private int mVerbose = 0;
    private double mSpeed = 1.0d;
    private long mLastRecordedDownTimeKey = 0;
    private long mLastRecordedDownTimeMotion = 0;
    private long mLastExportDownTimeKey = 0;
    private long mLastExportDownTimeMotion = 0;
    private long mLastExportEventTime = -1;
    private long mLastRecordedEventTime = -1;
    private boolean mReadScriptLineByLine = THIS_DEBUG;
    private boolean mFileOpened = THIS_DEBUG;
    private float[] mLastX = new float[2];
    private float[] mLastY = new float[2];
    private long mScriptStartTime = -1;
    private long mMonkeyStartTime = -1;

    public MonkeySourceScript(Random random, String filename, long throttle, boolean randomizeThrottle, long profileWaitTime, long deviceSleepTime) {
        this.mProfileWaitTime = 5000L;
        this.mDeviceSleepTime = 30000L;
        this.mScriptFileName = filename;
        this.f2mQ = new MonkeyEventQueue(random, throttle, randomizeThrottle);
        this.mProfileWaitTime = profileWaitTime;
        this.mDeviceSleepTime = deviceSleepTime;
    }

    private void resetValue() {
        this.mLastRecordedDownTimeKey = 0L;
        this.mLastRecordedDownTimeMotion = 0L;
        this.mLastRecordedEventTime = -1L;
        this.mLastExportDownTimeKey = 0L;
        this.mLastExportDownTimeMotion = 0L;
        this.mLastExportEventTime = -1L;
    }

    private boolean readHeader() throws IOException {
        this.mFileOpened = true;
        this.mFStream = new FileInputStream(this.mScriptFileName);
        this.mInputStream = new DataInputStream(this.mFStream);
        this.mBufferedReader = new BufferedReader(new InputStreamReader(this.mInputStream));
        while (true) {
            String line = this.mBufferedReader.readLine();
            if (line == null) {
                return THIS_DEBUG;
            }
            String line2 = line.trim();
            if (line2.indexOf(HEADER_COUNT) >= 0) {
                try {
                    String value = line2.substring(HEADER_COUNT.length() + 1).trim();
                    this.mEventCountInScript = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    Logger.err.println("" + e);
                    return THIS_DEBUG;
                }
            } else if (line2.indexOf(HEADER_SPEED) >= 0) {
                try {
                    String value2 = line2.substring(HEADER_COUNT.length() + 1).trim();
                    this.mSpeed = Double.parseDouble(value2);
                } catch (NumberFormatException e2) {
                    Logger.err.println("" + e2);
                    return THIS_DEBUG;
                }
            } else if (line2.indexOf(HEADER_LINE_BY_LINE) >= 0) {
                this.mReadScriptLineByLine = true;
            } else if (line2.indexOf(STARTING_DATA_LINE) >= 0) {
                return true;
            }
        }
    }

    private int readLines() throws IOException {
        for (int i = 0; i < MAX_ONE_TIME_READS; i++) {
            String line = this.mBufferedReader.readLine();
            if (line == null) {
                return i;
            }
            processLine(line.trim());
        }
        return MAX_ONE_TIME_READS;
    }

    private int readOneLine() throws IOException {
        String line = this.mBufferedReader.readLine();
        if (line == null) {
            return 0;
        }
        processLine(line.trim());
        return 1;
    }

    private void handleEvent(String s, String[] args) {
        MonkeyMotionEvent e;
        float y;
        long eventTime;
        MonkeyMotionEvent e2;
        String[] strArr;
        String str;
        String[] strArr2;
        if (s.indexOf(EVENT_KEYWORD_KEY) >= 0 && args.length == 8) {
            try {
                Logger.out.println(" old key\n");
                long downTime = Long.parseLong(args[0]);
                long eventTime2 = Long.parseLong(args[1]);
                int action = Integer.parseInt(args[2]);
                int code = Integer.parseInt(args[3]);
                int repeat = Integer.parseInt(args[4]);
                int metaState = Integer.parseInt(args[5]);
                int device = Integer.parseInt(args[6]);
                int scancode = Integer.parseInt(args[7]);
                MonkeyKeyEvent e3 = new MonkeyKeyEvent(downTime, eventTime2, action, code, repeat, metaState, device, scancode);
                Logger.out.println(" Key code " + code + "\n");
                this.f2mQ.addLast((MonkeyEvent) e3);
                Logger.out.println("Added key up \n");
            } catch (NumberFormatException e4) {
            }
        } else if ((s.indexOf(EVENT_KEYWORD_POINTER) >= 0 || s.indexOf(EVENT_KEYWORD_TRACKBALL) >= 0) && args.length == 12) {
            try {
                long downTime2 = Long.parseLong(args[0]);
                long eventTime3 = Long.parseLong(args[1]);
                int action2 = Integer.parseInt(args[2]);
                float x = Float.parseFloat(args[3]);
                float y2 = Float.parseFloat(args[4]);
                float pressure = Float.parseFloat(args[5]);
                float size = Float.parseFloat(args[6]);
                int metaState2 = Integer.parseInt(args[7]);
                float xPrecision = Float.parseFloat(args[8]);
                float yPrecision = Float.parseFloat(args[9]);
                int device2 = Integer.parseInt(args[10]);
                int edgeFlags = Integer.parseInt(args[11]);
                if (s.indexOf("Pointer") > 0) {
                    e = new MonkeyTouchEvent(action2);
                } else {
                    e = new MonkeyTrackballEvent(action2);
                }
                e.setDownTime(downTime2).setEventTime(eventTime3).setMetaState(metaState2).setPrecision(xPrecision, yPrecision).setDeviceId(device2).setEdgeFlags(edgeFlags).addPointer(0, x, y2, pressure, size);
                this.f2mQ.addLast((MonkeyEvent) e);
            } catch (NumberFormatException e5) {
            }
        } else if ((s.indexOf(EVENT_KEYWORD_POINTER) >= 0 || s.indexOf(EVENT_KEYWORD_TRACKBALL) >= 0) && args.length == 13) {
            try {
                long downTime3 = Long.parseLong(args[0]);
                long eventTime4 = Long.parseLong(args[1]);
                int action3 = Integer.parseInt(args[2]);
                float x2 = Float.parseFloat(args[3]);
                float y3 = Float.parseFloat(args[4]);
                float pressure2 = Float.parseFloat(args[5]);
                float size2 = Float.parseFloat(args[6]);
                int metaState3 = Integer.parseInt(args[7]);
                float xPrecision2 = Float.parseFloat(args[8]);
                float yPrecision2 = Float.parseFloat(args[9]);
                int device3 = Integer.parseInt(args[10]);
                int edgeFlags2 = Integer.parseInt(args[11]);
                int pointerId = Integer.parseInt(args[12]);
                if (s.indexOf("Pointer") > 0) {
                    if (action3 == 5) {
                        e2 = new MonkeyTouchEvent((pointerId << 8) | 5).setIntermediateNote(true);
                    } else {
                        e2 = new MonkeyTouchEvent(action3);
                    }
                    y = y3;
                    if (this.mScriptStartTime < 0) {
                        this.mMonkeyStartTime = SystemClock.uptimeMillis();
                        eventTime = eventTime4;
                        this.mScriptStartTime = eventTime;
                    } else {
                        eventTime = eventTime4;
                    }
                } else {
                    y = y3;
                    eventTime = eventTime4;
                    e2 = new MonkeyTrackballEvent(action3);
                }
                if (pointerId == 1) {
                    e2.setDownTime(downTime3).setEventTime(eventTime).setMetaState(metaState3).setPrecision(xPrecision2, yPrecision2).setDeviceId(device3).setEdgeFlags(edgeFlags2).addPointer(0, this.mLastX[0], this.mLastY[0], pressure2, size2).addPointer(1, x2, y, pressure2, size2);
                    this.mLastX[1] = x2;
                    this.mLastY[1] = y;
                } else if (pointerId == 0) {
                    e2.setDownTime(downTime3).setEventTime(eventTime).setMetaState(metaState3).setPrecision(xPrecision2, yPrecision2).setDeviceId(device3).setEdgeFlags(edgeFlags2).addPointer(0, x2, y, pressure2, size2);
                    if (action3 == 6) {
                        e2.addPointer(1, this.mLastX[1], this.mLastY[1]);
                    }
                    this.mLastX[0] = x2;
                    this.mLastY[0] = y;
                }
                if (this.mReadScriptLineByLine) {
                    long curUpTime = SystemClock.uptimeMillis();
                    long realElapsedTime = curUpTime - this.mMonkeyStartTime;
                    long scriptElapsedTime = eventTime - this.mScriptStartTime;
                    if (realElapsedTime < scriptElapsedTime) {
                        long waitDuration = scriptElapsedTime - realElapsedTime;
                        this.f2mQ.addLast((MonkeyEvent) new MonkeyWaitEvent(waitDuration));
                    }
                }
                this.f2mQ.addLast((MonkeyEvent) e2);
            } catch (NumberFormatException e6) {
            }
        } else {
            if (s.indexOf(EVENT_KEYWORD_ROTATION) >= 0) {
                strArr = args;
                if (strArr.length == 2) {
                    try {
                        int rotationDegree = Integer.parseInt(strArr[0]);
                        int persist = Integer.parseInt(strArr[1]);
                        if (rotationDegree == 0 || rotationDegree == 1 || rotationDegree == 2 || rotationDegree == 3) {
                            this.f2mQ.addLast((MonkeyEvent) new MonkeyRotationEvent(rotationDegree, persist != 0 ? true : THIS_DEBUG));
                            return;
                        }
                        return;
                    } catch (NumberFormatException e7) {
                        return;
                    }
                }
            } else {
                strArr = args;
            }
            if (s.indexOf(EVENT_KEYWORD_TAP) >= 0 && strArr.length >= 2) {
                try {
                    float x3 = Float.parseFloat(strArr[0]);
                    float y4 = Float.parseFloat(strArr[1]);
                    long tapDuration = 0;
                    if (strArr.length == 3) {
                        tapDuration = Long.parseLong(strArr[2]);
                    }
                    long downTime4 = SystemClock.uptimeMillis();
                    MonkeyMotionEvent e1 = new MonkeyTouchEvent(0).setDownTime(downTime4).setEventTime(downTime4).addPointer(0, x3, y4, 1.0f, 5.0f);
                    this.f2mQ.addLast((MonkeyEvent) e1);
                    if (tapDuration > 0) {
                        this.f2mQ.addLast((MonkeyEvent) new MonkeyWaitEvent(tapDuration));
                    }
                    this.f2mQ.addLast((MonkeyEvent) new MonkeyTouchEvent(1).setDownTime(downTime4).setEventTime(downTime4).addPointer(0, x3, y4, 1.0f, 5.0f));
                } catch (NumberFormatException e8) {
                    Logger.err.println("// " + e8.toString());
                }
            } else if (s.indexOf(EVENT_KEYWORD_PRESSANDHOLD) >= 0 && strArr.length == 3) {
                try {
                    float x4 = Float.parseFloat(strArr[0]);
                    float y5 = Float.parseFloat(strArr[1]);
                    long pressDuration = Long.parseLong(strArr[2]);
                    long downTime5 = SystemClock.uptimeMillis();
                    MonkeyMotionEvent e12 = new MonkeyTouchEvent(0).setDownTime(downTime5).setEventTime(downTime5).addPointer(0, x4, y5, 1.0f, 5.0f);
                    MonkeyWaitEvent e22 = new MonkeyWaitEvent(pressDuration);
                    new MonkeyTouchEvent(1).setDownTime(downTime5 + pressDuration).setEventTime(downTime5 + pressDuration).addPointer(0, x4, y5, 1.0f, 5.0f);
                    this.f2mQ.addLast((MonkeyEvent) e12);
                    this.f2mQ.addLast((MonkeyEvent) e22);
                    this.f2mQ.addLast((MonkeyEvent) e22);
                } catch (NumberFormatException e9) {
                    Logger.err.println("// " + e9.toString());
                }
            } else {
                if (s.indexOf(EVENT_KEYWORD_DRAG) >= 0 && strArr.length == 5) {
                    float xStart = Float.parseFloat(strArr[0]);
                    float yStart = Float.parseFloat(strArr[1]);
                    float xEnd = Float.parseFloat(strArr[2]);
                    float yEnd = Float.parseFloat(strArr[3]);
                    int stepCount = Integer.parseInt(strArr[4]);
                    long downTime6 = SystemClock.uptimeMillis();
                    long eventTime5 = SystemClock.uptimeMillis();
                    if (stepCount > 0) {
                        float xStep = (xEnd - xStart) / stepCount;
                        float yStep = (yEnd - yStart) / stepCount;
                        MonkeyMotionEvent e10 = new MonkeyTouchEvent(0).setDownTime(downTime6).setEventTime(eventTime5).addPointer(0, xStart, yStart, 1.0f, 5.0f);
                        this.f2mQ.addLast((MonkeyEvent) e10);
                        int i = 0;
                        float x5 = xStart;
                        float y6 = yStart;
                        while (i < stepCount) {
                            x5 += xStep;
                            y6 += yStep;
                            long eventTime6 = SystemClock.uptimeMillis();
                            e10 = new MonkeyTouchEvent(2).setDownTime(downTime6).setEventTime(eventTime6).addPointer(0, x5, y6, 1.0f, 5.0f);
                            this.f2mQ.addLast((MonkeyEvent) e10);
                            i++;
                            xStart = xStart;
                        }
                        long eventTime7 = SystemClock.uptimeMillis();
                        MonkeyMotionEvent e11 = new MonkeyTouchEvent(1).setDownTime(downTime6).setEventTime(eventTime7).addPointer(0, x5, y6, 1.0f, 5.0f);
                        this.f2mQ.addLast((MonkeyEvent) e11);
                    }
                }
                if (s.indexOf(EVENT_KEYWORD_PINCH_ZOOM) < 0 || strArr.length != 9) {
                    str = "// ";
                } else {
                    float pt1xStart = Float.parseFloat(strArr[0]);
                    float pt1yStart = Float.parseFloat(strArr[1]);
                    float pt1xEnd = Float.parseFloat(strArr[2]);
                    float pt1yEnd = Float.parseFloat(strArr[3]);
                    float pt2xStart = Float.parseFloat(strArr[4]);
                    float pt2yStart = Float.parseFloat(strArr[5]);
                    float pt2xEnd = Float.parseFloat(strArr[6]);
                    float pt2yEnd = Float.parseFloat(strArr[7]);
                    int stepCount2 = Integer.parseInt(strArr[8]);
                    float x1 = pt1xStart;
                    long downTime7 = SystemClock.uptimeMillis();
                    str = "// ";
                    long eventTime8 = SystemClock.uptimeMillis();
                    if (stepCount2 > 0) {
                        float pt1xStep = (pt1xEnd - pt1xStart) / stepCount2;
                        float pt1yStep = (pt1yEnd - pt1yStart) / stepCount2;
                        float pt1xEnd2 = stepCount2;
                        float pt2xStep = (pt2xEnd - pt2xStart) / pt1xEnd2;
                        float pt1yEnd2 = stepCount2;
                        float pt2yStep = (pt2yEnd - pt2yStart) / pt1yEnd2;
                        this.f2mQ.addLast((MonkeyEvent) new MonkeyTouchEvent(0).setDownTime(downTime7).setEventTime(eventTime8).addPointer(0, x1, pt1yStart, 1.0f, 5.0f));
                        this.f2mQ.addLast((MonkeyEvent) new MonkeyTouchEvent(261).setDownTime(downTime7).addPointer(0, x1, pt1yStart).addPointer(1, pt2xStart, pt2yStart).setIntermediateNote(true));
                        float x22 = pt2xStart;
                        int i2 = 0;
                        float y22 = pt2yStart;
                        float y1 = pt1yStart;
                        while (i2 < stepCount2) {
                            x1 += pt1xStep;
                            y1 += pt1yStep;
                            x22 += pt2xStep;
                            y22 += pt2yStep;
                            long eventTime9 = SystemClock.uptimeMillis();
                            this.f2mQ.addLast((MonkeyEvent) new MonkeyTouchEvent(2).setDownTime(downTime7).setEventTime(eventTime9).addPointer(0, x1, y1, 1.0f, 5.0f).addPointer(1, x22, y22, 1.0f, 5.0f));
                            i2++;
                            pt1yStep = pt1yStep;
                            pt1xStep = pt1xStep;
                            pt2xStep = pt2xStep;
                            pt2yStep = pt2yStep;
                        }
                        long eventTime10 = SystemClock.uptimeMillis();
                        this.f2mQ.addLast((MonkeyEvent) new MonkeyTouchEvent(6).setDownTime(downTime7).setEventTime(eventTime10).addPointer(0, x1, y1).addPointer(1, x22, y22));
                    }
                }
                if (s.indexOf(EVENT_KEYWORD_FLIP) >= 0) {
                    strArr2 = args;
                    if (strArr2.length == 1) {
                        boolean keyboardOpen = Boolean.parseBoolean(strArr2[0]);
                        MonkeyFlipEvent e13 = new MonkeyFlipEvent(keyboardOpen);
                        this.f2mQ.addLast((MonkeyEvent) e13);
                    }
                } else {
                    strArr2 = args;
                }
                if (s.indexOf(EVENT_KEYWORD_ACTIVITY) >= 0 && strArr2.length >= 2) {
                    String pkg_name = strArr2[0];
                    String cl_name = strArr2[1];
                    long alarmTime = 0;
                    ComponentName mApp = new ComponentName(pkg_name, cl_name);
                    if (strArr2.length > 2) {
                        try {
                            alarmTime = Long.parseLong(strArr2[2]);
                        } catch (NumberFormatException e14) {
                            Logger.err.println(str + e14.toString());
                            return;
                        }
                    }
                    if (strArr2.length == 2) {
                        MonkeyActivityEvent e15 = new MonkeyActivityEvent(mApp);
                        this.f2mQ.addLast((MonkeyEvent) e15);
                        return;
                    }
                    MonkeyActivityEvent e16 = new MonkeyActivityEvent(mApp, alarmTime);
                    this.f2mQ.addLast((MonkeyEvent) e16);
                } else if (s.indexOf(EVENT_KEYWORD_DEVICE_WAKEUP) >= 0) {
                    long deviceSleepTime = this.mDeviceSleepTime;
                    this.f2mQ.addLast((MonkeyEvent) new MonkeyActivityEvent(new ComponentName("com.google.android.powerutil", "com.google.android.powerutil.WakeUpScreen"), deviceSleepTime));
                    this.f2mQ.addLast((MonkeyEvent) new MonkeyKeyEvent(0, 7));
                    this.f2mQ.addLast((MonkeyEvent) new MonkeyKeyEvent(1, 7));
                    this.f2mQ.addLast((MonkeyEvent) new MonkeyWaitEvent(3000 + deviceSleepTime));
                    this.f2mQ.addLast((MonkeyEvent) new MonkeyKeyEvent(0, 82));
                    this.f2mQ.addLast((MonkeyEvent) new MonkeyKeyEvent(1, 82));
                    this.f2mQ.addLast((MonkeyEvent) new MonkeyKeyEvent(0, 4));
                    this.f2mQ.addLast((MonkeyEvent) new MonkeyKeyEvent(1, 4));
                } else if (s.indexOf(EVENT_KEYWORD_INSTRUMENTATION) >= 0 && strArr2.length == 2) {
                    String test_name = strArr2[0];
                    String runner_name = strArr2[1];
                    MonkeyInstrumentationEvent e17 = new MonkeyInstrumentationEvent(test_name, runner_name);
                    this.f2mQ.addLast((MonkeyEvent) e17);
                } else if (s.indexOf(EVENT_KEYWORD_WAIT) >= 0 && strArr2.length == 1) {
                    try {
                        long sleeptime = Integer.parseInt(strArr2[0]);
                        MonkeyWaitEvent e18 = new MonkeyWaitEvent(sleeptime);
                        this.f2mQ.addLast((MonkeyEvent) e18);
                    } catch (NumberFormatException e19) {
                    }
                } else if (s.indexOf(EVENT_KEYWORD_PROFILE_WAIT) >= 0) {
                    MonkeyWaitEvent e20 = new MonkeyWaitEvent(this.mProfileWaitTime);
                    this.f2mQ.addLast((MonkeyEvent) e20);
                } else if (s.indexOf(EVENT_KEYWORD_KEYPRESS) >= 0 && strArr2.length == 1) {
                    String key_name = strArr2[0];
                    int keyCode = MonkeySourceRandom.getKeyCode(key_name);
                    if (keyCode == 0) {
                        return;
                    }
                    MonkeyKeyEvent e21 = new MonkeyKeyEvent(0, keyCode);
                    this.f2mQ.addLast((MonkeyEvent) e21);
                    MonkeyKeyEvent e23 = new MonkeyKeyEvent(1, keyCode);
                    this.f2mQ.addLast((MonkeyEvent) e23);
                } else {
                    if (s.indexOf(EVENT_KEYWORD_LONGPRESS) >= 0) {
                        MonkeyKeyEvent e24 = new MonkeyKeyEvent(0, 23);
                        this.f2mQ.addLast((MonkeyEvent) e24);
                        MonkeyWaitEvent we = new MonkeyWaitEvent(LONGPRESS_WAIT_TIME);
                        this.f2mQ.addLast((MonkeyEvent) we);
                        MonkeyKeyEvent e25 = new MonkeyKeyEvent(1, 23);
                        this.f2mQ.addLast((MonkeyEvent) e25);
                    }
                    if (s.indexOf(EVENT_KEYWORD_POWERLOG) >= 0 && strArr2.length > 0) {
                        String power_log_type = strArr2[0];
                        if (strArr2.length == 1) {
                            MonkeyPowerEvent e26 = new MonkeyPowerEvent(power_log_type);
                            this.f2mQ.addLast((MonkeyEvent) e26);
                        } else if (strArr2.length == 2) {
                            String test_case_status = strArr2[1];
                            MonkeyPowerEvent e27 = new MonkeyPowerEvent(power_log_type, test_case_status);
                            this.f2mQ.addLast((MonkeyEvent) e27);
                        }
                    }
                    if (s.indexOf(EVENT_KEYWORD_WRITEPOWERLOG) >= 0) {
                        MonkeyPowerEvent e28 = new MonkeyPowerEvent();
                        this.f2mQ.addLast((MonkeyEvent) e28);
                    }
                    if (s.indexOf(EVENT_KEYWORD_RUNCMD) >= 0 && strArr2.length == 1) {
                        String cmd = strArr2[0];
                        MonkeyCommandEvent e29 = new MonkeyCommandEvent(cmd);
                        this.f2mQ.addLast((MonkeyEvent) e29);
                    }
                    if (s.indexOf(EVENT_KEYWORD_INPUT_STRING) >= 0 && strArr2.length == 1) {
                        String input = strArr2[0];
                        String cmd2 = "input text " + input;
                        MonkeyCommandEvent e30 = new MonkeyCommandEvent(cmd2);
                        this.f2mQ.addLast((MonkeyEvent) e30);
                    } else if (s.indexOf(EVENT_KEYWORD_START_FRAMERATE_CAPTURE) >= 0) {
                        MonkeyGetFrameRateEvent e31 = new MonkeyGetFrameRateEvent("start");
                        this.f2mQ.addLast((MonkeyEvent) e31);
                    } else if (s.indexOf(EVENT_KEYWORD_END_FRAMERATE_CAPTURE) >= 0 && strArr2.length == 1) {
                        String input2 = strArr2[0];
                        MonkeyGetFrameRateEvent e32 = new MonkeyGetFrameRateEvent("end", input2);
                        this.f2mQ.addLast((MonkeyEvent) e32);
                    } else if (s.indexOf(EVENT_KEYWORD_START_APP_FRAMERATE_CAPTURE) >= 0 && strArr2.length == 1) {
                        String app = strArr2[0];
                        MonkeyGetAppFrameRateEvent e33 = new MonkeyGetAppFrameRateEvent("start", app);
                        this.f2mQ.addLast((MonkeyEvent) e33);
                    } else if (s.indexOf(EVENT_KEYWORD_END_APP_FRAMERATE_CAPTURE) >= 0 && strArr2.length == 2) {
                        String app2 = strArr2[0];
                        String label = strArr2[1];
                        MonkeyGetAppFrameRateEvent e34 = new MonkeyGetAppFrameRateEvent("end", app2, label);
                        this.f2mQ.addLast((MonkeyEvent) e34);
                    }
                }
            }
        }
    }

    private void processLine(String line) {
        int index1 = line.indexOf(40);
        int index2 = line.indexOf(41);
        if (index1 < 0 || index2 < 0) {
            return;
        }
        String[] args = line.substring(index1 + 1, index2).split(",");
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].trim();
        }
        handleEvent(line, args);
    }

    private void closeFile() throws IOException {
        this.mFileOpened = THIS_DEBUG;
        try {
            this.mFStream.close();
            this.mInputStream.close();
        } catch (NullPointerException e) {
        }
    }

    private void readNextBatch() throws IOException {
        int linesRead;
        if (!this.mFileOpened) {
            resetValue();
            readHeader();
        }
        if (this.mReadScriptLineByLine) {
            linesRead = readOneLine();
        } else {
            linesRead = readLines();
        }
        if (linesRead == 0) {
            closeFile();
        }
    }

    private void needSleep(long time) {
        if (time < 1) {
            return;
        }
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }

    @Override // com.android.commands.monkey.MonkeyEventSource
    public boolean validate() {
        try {
            boolean validHeader = readHeader();
            closeFile();
            if (this.mVerbose > 0) {
                Logger.out.println("Replaying " + this.mEventCountInScript + " events with speed " + this.mSpeed);
            }
            return validHeader;
        } catch (IOException e) {
            return THIS_DEBUG;
        }
    }

    @Override // com.android.commands.monkey.MonkeyEventSource
    public void setVerbose(int verbose) {
        this.mVerbose = verbose;
    }

    private void adjustKeyEventTime(MonkeyKeyEvent e) {
        long thisDownTime;
        long thisEventTime;
        if (e.getEventTime() < 0) {
            return;
        }
        if (this.mLastRecordedEventTime <= 0) {
            thisDownTime = SystemClock.uptimeMillis();
            thisEventTime = thisDownTime;
        } else {
            long thisEventTime2 = e.getDownTime();
            if (thisEventTime2 != this.mLastRecordedDownTimeKey) {
                thisDownTime = e.getDownTime();
            } else {
                thisDownTime = this.mLastExportDownTimeKey;
            }
            long expectedDelay = (long) ((e.getEventTime() - this.mLastRecordedEventTime) * this.mSpeed);
            thisEventTime = this.mLastExportEventTime + expectedDelay;
            needSleep(expectedDelay - SLEEP_COMPENSATE_DIFF);
        }
        this.mLastRecordedDownTimeKey = e.getDownTime();
        this.mLastRecordedEventTime = e.getEventTime();
        e.setDownTime(thisDownTime);
        e.setEventTime(thisEventTime);
        this.mLastExportDownTimeKey = thisDownTime;
        this.mLastExportEventTime = thisEventTime;
    }

    private void adjustMotionEventTime(MonkeyMotionEvent e) {
        long thisEventTime = SystemClock.uptimeMillis();
        long thisDownTime = e.getDownTime();
        if (thisDownTime == this.mLastRecordedDownTimeMotion) {
            e.setDownTime(this.mLastExportDownTimeMotion);
        } else {
            this.mLastRecordedDownTimeMotion = thisDownTime;
            e.setDownTime(thisEventTime);
            this.mLastExportDownTimeMotion = thisEventTime;
        }
        e.setEventTime(thisEventTime);
    }

    @Override // com.android.commands.monkey.MonkeyEventSource
    public MonkeyEvent getNextEvent() {
        if (this.f2mQ.isEmpty()) {
            try {
                readNextBatch();
            } catch (IOException e) {
                return null;
            }
        }
        try {
            MonkeyEvent ev = this.f2mQ.getFirst();
            this.f2mQ.removeFirst();
            if (ev.getEventType() == 0) {
                adjustKeyEventTime((MonkeyKeyEvent) ev);
            } else if (ev.getEventType() == 1 || ev.getEventType() == 2) {
                adjustMotionEventTime((MonkeyMotionEvent) ev);
            }
            return ev;
        } catch (NoSuchElementException e2) {
            return null;
        }
    }
}
