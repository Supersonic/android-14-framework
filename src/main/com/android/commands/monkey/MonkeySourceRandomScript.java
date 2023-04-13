package com.android.commands.monkey;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
/* loaded from: classes.dex */
public class MonkeySourceRandomScript implements MonkeyEventSource {
    private MonkeySourceScript mCurrentSource;
    private Random mRandom;
    private boolean mRandomizeScript;
    private int mScriptCount;
    private ArrayList<MonkeySourceScript> mScriptSources;
    private MonkeySourceScript mSetupSource;
    private int mVerbose;

    public MonkeySourceRandomScript(String setupFileName, ArrayList<String> scriptFileNames, long throttle, boolean randomizeThrottle, Random random, long profileWaitTime, long deviceSleepTime, boolean randomizeScript) {
        this.mVerbose = 0;
        this.mSetupSource = null;
        this.mScriptSources = new ArrayList<>();
        this.mCurrentSource = null;
        this.mRandomizeScript = false;
        this.mScriptCount = 0;
        if (setupFileName != null) {
            MonkeySourceScript monkeySourceScript = new MonkeySourceScript(random, setupFileName, throttle, randomizeThrottle, profileWaitTime, deviceSleepTime);
            this.mSetupSource = monkeySourceScript;
            this.mCurrentSource = monkeySourceScript;
        }
        Iterator<String> it = scriptFileNames.iterator();
        while (it.hasNext()) {
            String fileName = it.next();
            this.mScriptSources.add(new MonkeySourceScript(random, fileName, throttle, randomizeThrottle, profileWaitTime, deviceSleepTime));
        }
        this.mRandom = random;
        this.mRandomizeScript = randomizeScript;
    }

    public MonkeySourceRandomScript(ArrayList<String> scriptFileNames, long throttle, boolean randomizeThrottle, Random random, long profileWaitTime, long deviceSleepTime, boolean randomizeScript) {
        this(null, scriptFileNames, throttle, randomizeThrottle, random, profileWaitTime, deviceSleepTime, randomizeScript);
    }

    @Override // com.android.commands.monkey.MonkeyEventSource
    public MonkeyEvent getNextEvent() {
        if (this.mCurrentSource == null) {
            int numSources = this.mScriptSources.size();
            if (numSources == 1) {
                this.mCurrentSource = this.mScriptSources.get(0);
            } else if (numSources > 1) {
                if (this.mRandomizeScript) {
                    this.mCurrentSource = this.mScriptSources.get(this.mRandom.nextInt(numSources));
                } else {
                    this.mCurrentSource = this.mScriptSources.get(this.mScriptCount % numSources);
                    this.mScriptCount++;
                }
            }
        }
        MonkeySourceScript monkeySourceScript = this.mCurrentSource;
        if (monkeySourceScript != null) {
            MonkeyEvent nextEvent = monkeySourceScript.getNextEvent();
            if (nextEvent == null) {
                this.mCurrentSource = null;
            }
            return nextEvent;
        }
        return null;
    }

    @Override // com.android.commands.monkey.MonkeyEventSource
    public void setVerbose(int verbose) {
        this.mVerbose = verbose;
        MonkeySourceScript monkeySourceScript = this.mSetupSource;
        if (monkeySourceScript != null) {
            monkeySourceScript.setVerbose(verbose);
        }
        Iterator<MonkeySourceScript> it = this.mScriptSources.iterator();
        while (it.hasNext()) {
            MonkeySourceScript source = it.next();
            source.setVerbose(verbose);
        }
    }

    @Override // com.android.commands.monkey.MonkeyEventSource
    public boolean validate() {
        MonkeySourceScript monkeySourceScript = this.mSetupSource;
        if (monkeySourceScript == null || monkeySourceScript.validate()) {
            Iterator<MonkeySourceScript> it = this.mScriptSources.iterator();
            while (it.hasNext()) {
                MonkeySourceScript source = it.next();
                if (!source.validate()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
