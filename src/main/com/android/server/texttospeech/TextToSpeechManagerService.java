package com.android.server.texttospeech;

import android.content.Context;
import android.os.UserHandle;
import android.speech.tts.ITextToSpeechManager;
import android.speech.tts.ITextToSpeechSessionCallback;
import com.android.server.infra.AbstractMasterSystemService;
import com.android.server.texttospeech.TextToSpeechManagerPerUserService;
/* loaded from: classes2.dex */
public final class TextToSpeechManagerService extends AbstractMasterSystemService<TextToSpeechManagerService, TextToSpeechManagerPerUserService> {
    public TextToSpeechManagerService(Context context) {
        super(context, null, null);
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("texttospeech", new TextToSpeechManagerServiceStub());
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public TextToSpeechManagerPerUserService newServiceLocked(int i, boolean z) {
        return new TextToSpeechManagerPerUserService(this, this.mLock, i);
    }

    /* loaded from: classes2.dex */
    public final class TextToSpeechManagerServiceStub extends ITextToSpeechManager.Stub {
        public TextToSpeechManagerServiceStub() {
        }

        public void createSession(String str, final ITextToSpeechSessionCallback iTextToSpeechSessionCallback) {
            synchronized (TextToSpeechManagerService.this.mLock) {
                TextToSpeechManagerPerUserService textToSpeechManagerPerUserService = (TextToSpeechManagerPerUserService) TextToSpeechManagerService.this.getServiceForUserLocked(UserHandle.getCallingUserId());
                if (textToSpeechManagerPerUserService != null) {
                    textToSpeechManagerPerUserService.createSessionLocked(str, iTextToSpeechSessionCallback);
                } else {
                    TextToSpeechManagerPerUserService.runSessionCallbackMethod(new TextToSpeechManagerPerUserService.ThrowingRunnable() { // from class: com.android.server.texttospeech.TextToSpeechManagerService$TextToSpeechManagerServiceStub$$ExternalSyntheticLambda0
                        @Override // com.android.server.texttospeech.TextToSpeechManagerPerUserService.ThrowingRunnable
                        public final void runOrThrow() {
                            iTextToSpeechSessionCallback.onError("Service is not available for user");
                        }
                    });
                }
            }
        }
    }
}
