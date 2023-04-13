package com.android.server.audio;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlayerBase;
import android.media.SoundPool;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.PrintWriterPrinter;
import com.android.internal.util.XmlUtils;
import com.android.server.utils.EventLogger;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class SoundEffectsHelper {
    public final Context mContext;
    public final Consumer<PlayerBase> mPlayerAvailableCb;
    public final int mSfxAttenuationDb;
    public SfxHandler mSfxHandler;
    public SfxWorker mSfxWorker;
    public SoundPool mSoundPool;
    public SoundPoolLoader mSoundPoolLoader;
    public final EventLogger mSfxLogger = new EventLogger(26, "Sound Effects Loading");
    public final List<Resource> mResources = new ArrayList();
    public final int[] mEffects = new int[16];

    /* loaded from: classes.dex */
    public interface OnEffectsLoadCompleteHandler {
        void run(boolean z);
    }

    /* loaded from: classes.dex */
    public static final class Resource {
        public final String mFileName;
        public boolean mLoaded;
        public int mSampleId = 0;

        public Resource(String str) {
            this.mFileName = str;
        }

        public void unload() {
            this.mSampleId = 0;
            this.mLoaded = false;
        }
    }

    public SoundEffectsHelper(Context context, Consumer<PlayerBase> consumer) {
        this.mContext = context;
        this.mSfxAttenuationDb = context.getResources().getInteger(17694968);
        this.mPlayerAvailableCb = consumer;
        startWorker();
    }

    public void loadSoundEffects(OnEffectsLoadCompleteHandler onEffectsLoadCompleteHandler) {
        sendMsg(0, 0, 0, onEffectsLoadCompleteHandler, 0);
    }

    public void unloadSoundEffects() {
        sendMsg(1, 0, 0, null, 0);
    }

    public void playSoundEffect(int i, int i2) {
        sendMsg(2, i, i2, null, 0);
    }

    public void dump(PrintWriter printWriter, String str) {
        if (this.mSfxHandler != null) {
            printWriter.println(str + "Message handler (watch for unhandled messages):");
            this.mSfxHandler.dump(new PrintWriterPrinter(printWriter), "  ");
        } else {
            printWriter.println(str + "Message handler is null");
        }
        printWriter.println(str + "Default attenuation (dB): " + this.mSfxAttenuationDb);
        this.mSfxLogger.dump(printWriter);
    }

    public final void startWorker() {
        SfxWorker sfxWorker = new SfxWorker();
        this.mSfxWorker = sfxWorker;
        sfxWorker.start();
        synchronized (this) {
            while (this.mSfxHandler == null) {
                try {
                    wait();
                } catch (InterruptedException unused) {
                    Log.w("AS.SfxHelper", "Interrupted while waiting " + this.mSfxWorker.getName() + " to start");
                }
            }
        }
    }

    public final void sendMsg(int i, int i2, int i3, Object obj, int i4) {
        SfxHandler sfxHandler = this.mSfxHandler;
        sfxHandler.sendMessageDelayed(sfxHandler.obtainMessage(i, i2, i3, obj), i4);
    }

    public final void logEvent(String str) {
        this.mSfxLogger.enqueue(new EventLogger.StringEvent(str));
    }

    public final void onLoadSoundEffects(OnEffectsLoadCompleteHandler onEffectsLoadCompleteHandler) {
        SoundPoolLoader soundPoolLoader = this.mSoundPoolLoader;
        if (soundPoolLoader != null) {
            soundPoolLoader.addHandler(onEffectsLoadCompleteHandler);
        } else if (this.mSoundPool != null) {
            if (onEffectsLoadCompleteHandler != null) {
                onEffectsLoadCompleteHandler.run(true);
            }
        } else {
            logEvent("effects loading started");
            PlayerBase build = new SoundPool.Builder().setMaxStreams(4).setAudioAttributes(new AudioAttributes.Builder().setUsage(13).setContentType(4).build()).build();
            this.mSoundPool = build;
            this.mPlayerAvailableCb.accept(build);
            loadSoundAssets();
            SoundPoolLoader soundPoolLoader2 = new SoundPoolLoader();
            this.mSoundPoolLoader = soundPoolLoader2;
            soundPoolLoader2.addHandler(new OnEffectsLoadCompleteHandler() { // from class: com.android.server.audio.SoundEffectsHelper.1
                @Override // com.android.server.audio.SoundEffectsHelper.OnEffectsLoadCompleteHandler
                public void run(boolean z) {
                    SoundEffectsHelper.this.mSoundPoolLoader = null;
                    if (z) {
                        return;
                    }
                    Log.w("AS.SfxHelper", "onLoadSoundEffects(), Error while loading samples");
                    SoundEffectsHelper.this.onUnloadSoundEffects();
                }
            });
            this.mSoundPoolLoader.addHandler(onEffectsLoadCompleteHandler);
            int i = 0;
            for (Resource resource : this.mResources) {
                String resourceFilePath = getResourceFilePath(resource);
                int load = this.mSoundPool.load(resourceFilePath, 0);
                if (load > 0) {
                    resource.mSampleId = load;
                    resource.mLoaded = false;
                    i++;
                } else {
                    logEvent("effect " + resourceFilePath + " rejected by SoundPool");
                    StringBuilder sb = new StringBuilder();
                    sb.append("SoundPool could not load file: ");
                    sb.append(resourceFilePath);
                    Log.w("AS.SfxHelper", sb.toString());
                }
            }
            if (i > 0) {
                sendMsg(3, 0, 0, null, 15000);
                return;
            }
            logEvent("effects loading completed, no effects to load");
            this.mSoundPoolLoader.onComplete(true);
        }
    }

    public void onUnloadSoundEffects() {
        if (this.mSoundPool == null) {
            return;
        }
        SoundPoolLoader soundPoolLoader = this.mSoundPoolLoader;
        if (soundPoolLoader != null) {
            soundPoolLoader.addHandler(new OnEffectsLoadCompleteHandler() { // from class: com.android.server.audio.SoundEffectsHelper.2
                @Override // com.android.server.audio.SoundEffectsHelper.OnEffectsLoadCompleteHandler
                public void run(boolean z) {
                    SoundEffectsHelper.this.onUnloadSoundEffects();
                }
            });
        }
        logEvent("effects unloading started");
        for (Resource resource : this.mResources) {
            int i = resource.mSampleId;
            if (i != 0) {
                this.mSoundPool.unload(i);
                resource.unload();
            }
        }
        this.mSoundPool.release();
        this.mSoundPool = null;
        logEvent("effects unloading completed");
    }

    public void onPlaySoundEffect(int i, int i2) {
        int i3;
        float pow = i2 < 0 ? (float) Math.pow(10.0d, this.mSfxAttenuationDb / 20.0f) : i2 / 1000.0f;
        Resource resource = this.mResources.get(this.mEffects[i]);
        SoundPool soundPool = this.mSoundPool;
        if (soundPool != null && (i3 = resource.mSampleId) != 0 && resource.mLoaded) {
            soundPool.play(i3, pow, pow, 0, 0, 1.0f);
            return;
        }
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(getResourceFilePath(resource));
            mediaPlayer.setAudioStreamType(1);
            mediaPlayer.prepare();
            mediaPlayer.setVolume(pow);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.android.server.audio.SoundEffectsHelper.3
                @Override // android.media.MediaPlayer.OnCompletionListener
                public void onCompletion(MediaPlayer mediaPlayer2) {
                    SoundEffectsHelper.cleanupPlayer(mediaPlayer2);
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: com.android.server.audio.SoundEffectsHelper.4
                @Override // android.media.MediaPlayer.OnErrorListener
                public boolean onError(MediaPlayer mediaPlayer2, int i4, int i5) {
                    SoundEffectsHelper.cleanupPlayer(mediaPlayer2);
                    return true;
                }
            });
            mediaPlayer.start();
        } catch (IOException e) {
            Log.w("AS.SfxHelper", "MediaPlayer IOException: " + e);
        } catch (IllegalArgumentException e2) {
            Log.w("AS.SfxHelper", "MediaPlayer IllegalArgumentException: " + e2);
        } catch (IllegalStateException e3) {
            Log.w("AS.SfxHelper", "MediaPlayer IllegalStateException: " + e3);
        }
    }

    public static void cleanupPlayer(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (IllegalStateException e) {
                Log.w("AS.SfxHelper", "MediaPlayer IllegalStateException: " + e);
            }
        }
    }

    public final String getResourceFilePath(Resource resource) {
        String str = Environment.getProductDirectory() + "/media/audio/ui/" + resource.mFileName;
        if (new File(str).isFile()) {
            return str;
        }
        return Environment.getRootDirectory() + "/media/audio/ui/" + resource.mFileName;
    }

    public final void loadSoundAssetDefaults() {
        int size = this.mResources.size();
        this.mResources.add(new Resource("Effect_Tick.ogg"));
        Arrays.fill(this.mEffects, size);
    }

    public final void loadSoundAssets() {
        if (!this.mResources.isEmpty()) {
            return;
        }
        loadSoundAssetDefaults();
        XmlResourceParser xmlResourceParser = null;
        try {
            try {
                XmlResourceParser xml = this.mContext.getResources().getXml(18284545);
                try {
                    try {
                        XmlUtils.beginDocument(xml, "audio_assets");
                        String attributeValue = xml.getAttributeValue(null, "version");
                        HashMap hashMap = new HashMap();
                        if ("1.0".equals(attributeValue)) {
                            while (true) {
                                XmlUtils.nextElement(xml);
                                String name = xml.getName();
                                if (name == null) {
                                    break;
                                } else if (name.equals("group")) {
                                    String attributeValue2 = xml.getAttributeValue(null, "name");
                                    if (!"touch_sounds".equals(attributeValue2)) {
                                        Log.w("AS.SfxHelper", "Unsupported group name: " + attributeValue2);
                                    }
                                } else if (!name.equals("asset")) {
                                    break;
                                } else {
                                    String attributeValue3 = xml.getAttributeValue(null, "id");
                                    String attributeValue4 = xml.getAttributeValue(null, "file");
                                    try {
                                        int i = AudioManager.class.getField(attributeValue3).getInt(null);
                                        int intValue = hashMap.getOrDefault(Integer.valueOf(i), 0).intValue() + 1;
                                        hashMap.put(Integer.valueOf(i), Integer.valueOf(intValue));
                                        if (intValue > 1) {
                                            Log.w("AS.SfxHelper", "Duplicate definition for sound ID: " + attributeValue3);
                                        }
                                        this.mEffects[i] = findOrAddResourceByFileName(attributeValue4);
                                    } catch (Exception unused) {
                                        Log.w("AS.SfxHelper", "Invalid sound ID: " + attributeValue3);
                                    }
                                }
                            }
                            boolean allNavigationRepeatSoundsParsed = allNavigationRepeatSoundsParsed(hashMap);
                            boolean z = hashMap.getOrDefault(11, 0).intValue() > 0;
                            if (allNavigationRepeatSoundsParsed || z) {
                                AudioManager audioManager = (AudioManager) this.mContext.getSystemService(AudioManager.class);
                                if (audioManager != null && allNavigationRepeatSoundsParsed) {
                                    audioManager.setNavigationRepeatSoundEffectsEnabled(true);
                                }
                                if (audioManager != null && z) {
                                    audioManager.setHomeSoundEffectEnabled(true);
                                }
                            }
                        }
                        xml.close();
                    } catch (Throwable th) {
                        th = th;
                        xmlResourceParser = xml;
                        if (xmlResourceParser != null) {
                            xmlResourceParser.close();
                        }
                        throw th;
                    }
                } catch (Resources.NotFoundException e) {
                    e = e;
                    xmlResourceParser = xml;
                    Log.w("AS.SfxHelper", "audio assets file not found", e);
                    if (xmlResourceParser == null) {
                        return;
                    }
                    xmlResourceParser.close();
                } catch (IOException e2) {
                    e = e2;
                    xmlResourceParser = xml;
                    Log.w("AS.SfxHelper", "I/O exception reading sound assets", e);
                    if (xmlResourceParser != null) {
                        xmlResourceParser.close();
                    }
                } catch (XmlPullParserException e3) {
                    e = e3;
                    xmlResourceParser = xml;
                    Log.w("AS.SfxHelper", "XML parser exception reading sound assets", e);
                    if (xmlResourceParser != null) {
                        xmlResourceParser.close();
                    }
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Resources.NotFoundException e4) {
            e = e4;
        } catch (IOException e5) {
            e = e5;
        } catch (XmlPullParserException e6) {
            e = e6;
        }
    }

    public final boolean allNavigationRepeatSoundsParsed(Map<Integer, Integer> map) {
        return ((map.getOrDefault(12, 0).intValue() + map.getOrDefault(13, 0).intValue()) + map.getOrDefault(14, 0).intValue()) + map.getOrDefault(15, 0).intValue() == 4;
    }

    public final int findOrAddResourceByFileName(String str) {
        for (int i = 0; i < this.mResources.size(); i++) {
            if (this.mResources.get(i).mFileName.equals(str)) {
                return i;
            }
        }
        int size = this.mResources.size();
        this.mResources.add(new Resource(str));
        return size;
    }

    public final Resource findResourceBySampleId(int i) {
        for (Resource resource : this.mResources) {
            if (resource.mSampleId == i) {
                return resource;
            }
        }
        return null;
    }

    /* loaded from: classes.dex */
    public class SfxWorker extends Thread {
        public SfxWorker() {
            super("AS.SfxWorker");
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Looper.prepare();
            synchronized (SoundEffectsHelper.this) {
                SoundEffectsHelper.this.mSfxHandler = new SfxHandler();
                SoundEffectsHelper.this.notify();
            }
            Looper.loop();
        }
    }

    /* loaded from: classes.dex */
    public class SfxHandler extends Handler {
        public SfxHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                SoundEffectsHelper.this.onLoadSoundEffects((OnEffectsLoadCompleteHandler) message.obj);
            } else if (i == 1) {
                SoundEffectsHelper.this.onUnloadSoundEffects();
            } else if (i == 2) {
                final int i2 = message.arg1;
                final int i3 = message.arg2;
                SoundEffectsHelper.this.onLoadSoundEffects(new OnEffectsLoadCompleteHandler() { // from class: com.android.server.audio.SoundEffectsHelper.SfxHandler.1
                    @Override // com.android.server.audio.SoundEffectsHelper.OnEffectsLoadCompleteHandler
                    public void run(boolean z) {
                        if (z) {
                            SoundEffectsHelper.this.onPlaySoundEffect(i2, i3);
                        }
                    }
                });
            } else if (i == 3 && SoundEffectsHelper.this.mSoundPoolLoader != null) {
                SoundEffectsHelper.this.mSoundPoolLoader.onTimeout();
            }
        }
    }

    /* loaded from: classes.dex */
    public class SoundPoolLoader implements SoundPool.OnLoadCompleteListener {
        public List<OnEffectsLoadCompleteHandler> mLoadCompleteHandlers = new ArrayList();

        public SoundPoolLoader() {
            SoundEffectsHelper.this.mSoundPool.setOnLoadCompleteListener(this);
        }

        public void addHandler(OnEffectsLoadCompleteHandler onEffectsLoadCompleteHandler) {
            if (onEffectsLoadCompleteHandler != null) {
                this.mLoadCompleteHandlers.add(onEffectsLoadCompleteHandler);
            }
        }

        @Override // android.media.SoundPool.OnLoadCompleteListener
        public void onLoadComplete(SoundPool soundPool, int i, int i2) {
            int i3 = 0;
            if (i2 == 0) {
                for (Resource resource : SoundEffectsHelper.this.mResources) {
                    if (resource.mSampleId == i && !resource.mLoaded) {
                        SoundEffectsHelper.this.logEvent("effect " + resource.mFileName + " loaded");
                        resource.mLoaded = true;
                    }
                    if (resource.mSampleId != 0 && !resource.mLoaded) {
                        i3++;
                    }
                }
                if (i3 == 0) {
                    onComplete(true);
                    return;
                }
                return;
            }
            Resource findResourceBySampleId = SoundEffectsHelper.this.findResourceBySampleId(i);
            String resourceFilePath = findResourceBySampleId != null ? SoundEffectsHelper.this.getResourceFilePath(findResourceBySampleId) : "with unknown sample ID " + i;
            SoundEffectsHelper.this.logEvent("effect " + resourceFilePath + " loading failed, status " + i2);
            Log.w("AS.SfxHelper", "onLoadSoundEffects(), Error " + i2 + " while loading sample " + resourceFilePath);
            onComplete(false);
        }

        public void onTimeout() {
            onComplete(false);
        }

        public void onComplete(boolean z) {
            if (SoundEffectsHelper.this.mSoundPool != null) {
                SoundEffectsHelper.this.mSoundPool.setOnLoadCompleteListener(null);
            }
            for (OnEffectsLoadCompleteHandler onEffectsLoadCompleteHandler : this.mLoadCompleteHandlers) {
                onEffectsLoadCompleteHandler.run(z);
            }
            SoundEffectsHelper soundEffectsHelper = SoundEffectsHelper.this;
            StringBuilder sb = new StringBuilder();
            sb.append("effects loading ");
            sb.append(z ? "completed" : "failed");
            soundEffectsHelper.logEvent(sb.toString());
        }
    }
}
