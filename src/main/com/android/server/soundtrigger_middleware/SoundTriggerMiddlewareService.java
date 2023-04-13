package com.android.server.soundtrigger_middleware;

import android.content.Context;
import android.media.permission.ClearCallingIdentityContext;
import android.media.permission.Identity;
import android.media.permission.PermissionUtil;
import android.media.permission.SafeCloseable;
import android.media.soundtrigger.ModelParameterRange;
import android.media.soundtrigger.PhraseSoundModel;
import android.media.soundtrigger.RecognitionConfig;
import android.media.soundtrigger.SoundModel;
import android.media.soundtrigger_middleware.ISoundTriggerCallback;
import android.media.soundtrigger_middleware.ISoundTriggerMiddlewareService;
import android.media.soundtrigger_middleware.ISoundTriggerModule;
import android.media.soundtrigger_middleware.SoundTriggerModuleDescriptor;
import android.os.RemoteException;
import com.android.server.SystemService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Objects;
/* loaded from: classes2.dex */
public class SoundTriggerMiddlewareService extends ISoundTriggerMiddlewareService.Stub {
    public final Context mContext;
    public final ISoundTriggerMiddlewareInternal mDelegate;

    public SoundTriggerMiddlewareService(ISoundTriggerMiddlewareInternal iSoundTriggerMiddlewareInternal, Context context) {
        Objects.requireNonNull(iSoundTriggerMiddlewareInternal);
        this.mDelegate = iSoundTriggerMiddlewareInternal;
        this.mContext = context;
    }

    public SoundTriggerModuleDescriptor[] listModulesAsOriginator(Identity identity) {
        SafeCloseable establishIdentityDirect = establishIdentityDirect(identity);
        try {
            SoundTriggerModuleDescriptor[] listModules = this.mDelegate.listModules();
            if (establishIdentityDirect != null) {
                establishIdentityDirect.close();
            }
            return listModules;
        } catch (Throwable th) {
            if (establishIdentityDirect != null) {
                try {
                    establishIdentityDirect.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public SoundTriggerModuleDescriptor[] listModulesAsMiddleman(Identity identity, Identity identity2) {
        SafeCloseable establishIdentityIndirect = establishIdentityIndirect(identity, identity2);
        try {
            SoundTriggerModuleDescriptor[] listModules = this.mDelegate.listModules();
            if (establishIdentityIndirect != null) {
                establishIdentityIndirect.close();
            }
            return listModules;
        } catch (Throwable th) {
            if (establishIdentityIndirect != null) {
                try {
                    establishIdentityIndirect.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public ISoundTriggerModule attachAsOriginator(int i, Identity identity, ISoundTriggerCallback iSoundTriggerCallback) {
        Objects.requireNonNull(identity);
        SafeCloseable establishIdentityDirect = establishIdentityDirect(identity);
        try {
            ModuleService moduleService = new ModuleService(this.mDelegate.attach(i, iSoundTriggerCallback));
            if (establishIdentityDirect != null) {
                establishIdentityDirect.close();
            }
            return moduleService;
        } catch (Throwable th) {
            if (establishIdentityDirect != null) {
                try {
                    establishIdentityDirect.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public ISoundTriggerModule attachAsMiddleman(int i, Identity identity, Identity identity2, ISoundTriggerCallback iSoundTriggerCallback) {
        Objects.requireNonNull(identity);
        Objects.requireNonNull(identity2);
        SafeCloseable establishIdentityIndirect = establishIdentityIndirect(identity, identity2);
        try {
            ModuleService moduleService = new ModuleService(this.mDelegate.attach(i, iSoundTriggerCallback));
            if (establishIdentityIndirect != null) {
                establishIdentityIndirect.close();
            }
            return moduleService;
        } catch (Throwable th) {
            if (establishIdentityIndirect != null) {
                try {
                    establishIdentityIndirect.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        ISoundTriggerMiddlewareInternal iSoundTriggerMiddlewareInternal = this.mDelegate;
        if (iSoundTriggerMiddlewareInternal instanceof Dumpable) {
            ((Dumpable) iSoundTriggerMiddlewareInternal).dump(printWriter);
        }
    }

    public final SafeCloseable establishIdentityIndirect(Identity identity, Identity identity2) {
        return PermissionUtil.establishIdentityIndirect(this.mContext, "android.permission.SOUNDTRIGGER_DELEGATE_IDENTITY", identity, identity2);
    }

    public final SafeCloseable establishIdentityDirect(Identity identity) {
        return PermissionUtil.establishIdentityDirect(identity);
    }

    /* loaded from: classes2.dex */
    public static final class ModuleService extends ISoundTriggerModule.Stub {
        public final ISoundTriggerModule mDelegate;

        public ModuleService(ISoundTriggerModule iSoundTriggerModule) {
            this.mDelegate = iSoundTriggerModule;
        }

        public int loadModel(SoundModel soundModel) throws RemoteException {
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                int loadModel = this.mDelegate.loadModel(soundModel);
                if (create != null) {
                    create.close();
                }
                return loadModel;
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public int loadPhraseModel(PhraseSoundModel phraseSoundModel) throws RemoteException {
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                int loadPhraseModel = this.mDelegate.loadPhraseModel(phraseSoundModel);
                if (create != null) {
                    create.close();
                }
                return loadPhraseModel;
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public void unloadModel(int i) throws RemoteException {
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                this.mDelegate.unloadModel(i);
                if (create != null) {
                    create.close();
                }
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public void startRecognition(int i, RecognitionConfig recognitionConfig) throws RemoteException {
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                this.mDelegate.startRecognition(i, recognitionConfig);
                if (create != null) {
                    create.close();
                }
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public void stopRecognition(int i) throws RemoteException {
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                this.mDelegate.stopRecognition(i);
                if (create != null) {
                    create.close();
                }
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public void forceRecognitionEvent(int i) throws RemoteException {
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                this.mDelegate.forceRecognitionEvent(i);
                if (create != null) {
                    create.close();
                }
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public void setModelParameter(int i, int i2, int i3) throws RemoteException {
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                this.mDelegate.setModelParameter(i, i2, i3);
                if (create != null) {
                    create.close();
                }
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public int getModelParameter(int i, int i2) throws RemoteException {
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                int modelParameter = this.mDelegate.getModelParameter(i, i2);
                if (create != null) {
                    create.close();
                }
                return modelParameter;
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public ModelParameterRange queryModelParameterSupport(int i, int i2) throws RemoteException {
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                ModelParameterRange queryModelParameterSupport = this.mDelegate.queryModelParameterSupport(i, i2);
                if (create != null) {
                    create.close();
                }
                return queryModelParameterSupport;
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public void detach() throws RemoteException {
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                this.mDelegate.detach();
                if (create != null) {
                    create.close();
                }
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
    }

    /* loaded from: classes2.dex */
    public static final class Lifecycle extends SystemService {
        public Lifecycle(Context context) {
            super(context);
        }

        @Override // com.android.server.SystemService
        public void onStart() {
            publishBinderService("soundtrigger_middleware", new SoundTriggerMiddlewareService(new SoundTriggerMiddlewareLogging(getContext(), new SoundTriggerMiddlewarePermission(new SoundTriggerMiddlewareValidation(new SoundTriggerMiddlewareImpl(new HalFactory[]{new DefaultHalFactory()}, new AudioSessionProviderImpl())), getContext())), getContext()));
        }
    }
}
