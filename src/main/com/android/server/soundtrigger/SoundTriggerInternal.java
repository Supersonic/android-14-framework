package com.android.server.soundtrigger;

import android.hardware.soundtrigger.IRecognitionStatusCallback;
import android.hardware.soundtrigger.ModelParams;
import android.hardware.soundtrigger.SoundTrigger;
import android.media.permission.Identity;
import android.os.IBinder;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
/* loaded from: classes2.dex */
public interface SoundTriggerInternal {

    /* loaded from: classes2.dex */
    public interface Session {
        SoundTrigger.ModuleProperties getModuleProperties();

        int getParameter(int i, @ModelParams int i2);

        SoundTrigger.ModelParamRange queryParameter(int i, @ModelParams int i2);

        int setParameter(int i, @ModelParams int i2, int i3);

        int startRecognition(int i, SoundTrigger.KeyphraseSoundModel keyphraseSoundModel, IRecognitionStatusCallback iRecognitionStatusCallback, SoundTrigger.RecognitionConfig recognitionConfig, boolean z);

        int stopRecognition(int i, IRecognitionStatusCallback iRecognitionStatusCallback);

        int unloadKeyphraseModel(int i);
    }

    Session attach(IBinder iBinder, SoundTrigger.ModuleProperties moduleProperties);

    void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

    List<SoundTrigger.ModuleProperties> listModuleProperties(Identity identity);
}
