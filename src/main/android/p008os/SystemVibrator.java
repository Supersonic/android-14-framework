package android.p008os;

import android.content.Context;
import android.p008os.SystemVibrator;
import android.p008os.Vibrator;
import android.p008os.VibratorInfo;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Range;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Function;
/* renamed from: android.os.SystemVibrator */
/* loaded from: classes3.dex */
public class SystemVibrator extends Vibrator {
    private static final String TAG = "Vibrator";
    private final ArrayList<MultiVibratorStateListener> mBrokenListeners;
    private final Context mContext;
    private final Object mLock;
    private final ArrayMap<Vibrator.OnVibratorStateChangedListener, MultiVibratorStateListener> mRegisteredListeners;
    private VibratorInfo mVibratorInfo;
    private final VibratorManager mVibratorManager;

    public SystemVibrator(Context context) {
        super(context);
        this.mBrokenListeners = new ArrayList<>();
        this.mRegisteredListeners = new ArrayMap<>();
        this.mLock = new Object();
        this.mContext = context;
        this.mVibratorManager = (VibratorManager) context.getSystemService(VibratorManager.class);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.p008os.Vibrator
    public VibratorInfo getInfo() {
        synchronized (this.mLock) {
            VibratorInfo vibratorInfo = this.mVibratorInfo;
            if (vibratorInfo != null) {
                return vibratorInfo;
            }
            VibratorManager vibratorManager = this.mVibratorManager;
            if (vibratorManager == null) {
                Log.m104w(TAG, "Failed to retrieve vibrator info; no vibrator manager.");
                return VibratorInfo.EMPTY_VIBRATOR_INFO;
            }
            int[] vibratorIds = vibratorManager.getVibratorIds();
            if (vibratorIds.length == 0) {
                NoVibratorInfo noVibratorInfo = new NoVibratorInfo();
                this.mVibratorInfo = noVibratorInfo;
                return noVibratorInfo;
            }
            VibratorInfo[] vibratorInfos = new VibratorInfo[vibratorIds.length];
            for (int i = 0; i < vibratorIds.length; i++) {
                Vibrator vibrator = this.mVibratorManager.getVibrator(vibratorIds[i]);
                if (vibrator instanceof NullVibrator) {
                    Log.m104w(TAG, "Vibrator manager service not ready; Info not yet available for vibrator: " + vibratorIds[i]);
                    return VibratorInfo.EMPTY_VIBRATOR_INFO;
                }
                vibratorInfos[i] = vibrator.getInfo();
            }
            int i2 = vibratorInfos.length;
            if (i2 == 1) {
                VibratorInfo vibratorInfo2 = new VibratorInfo(-1, vibratorInfos[0]);
                this.mVibratorInfo = vibratorInfo2;
                return vibratorInfo2;
            }
            MultiVibratorInfo multiVibratorInfo = new MultiVibratorInfo(vibratorInfos);
            this.mVibratorInfo = multiVibratorInfo;
            return multiVibratorInfo;
        }
    }

    @Override // android.p008os.Vibrator
    public boolean hasVibrator() {
        VibratorManager vibratorManager = this.mVibratorManager;
        if (vibratorManager != null) {
            return vibratorManager.getVibratorIds().length > 0;
        }
        Log.m104w(TAG, "Failed to check if vibrator exists; no vibrator manager.");
        return false;
    }

    @Override // android.p008os.Vibrator
    public boolean isVibrating() {
        int[] vibratorIds;
        VibratorManager vibratorManager = this.mVibratorManager;
        if (vibratorManager == null) {
            Log.m104w(TAG, "Failed to vibrate; no vibrator manager.");
            return false;
        }
        for (int vibratorId : vibratorManager.getVibratorIds()) {
            if (this.mVibratorManager.getVibrator(vibratorId).isVibrating()) {
                return true;
            }
        }
        return false;
    }

    @Override // android.p008os.Vibrator
    public void addVibratorStateListener(Vibrator.OnVibratorStateChangedListener listener) {
        Objects.requireNonNull(listener);
        Context context = this.mContext;
        if (context == null) {
            Log.m104w(TAG, "Failed to add vibrate state listener; no vibrator context.");
        } else {
            addVibratorStateListener(context.getMainExecutor(), listener);
        }
    }

    @Override // android.p008os.Vibrator
    public void addVibratorStateListener(Executor executor, Vibrator.OnVibratorStateChangedListener listener) {
        Objects.requireNonNull(listener);
        Objects.requireNonNull(executor);
        if (this.mVibratorManager == null) {
            Log.m104w(TAG, "Failed to add vibrate state listener; no vibrator manager.");
            return;
        }
        MultiVibratorStateListener delegate = null;
        try {
            synchronized (this.mRegisteredListeners) {
                if (this.mRegisteredListeners.containsKey(listener)) {
                    Log.m104w(TAG, "Listener already registered.");
                    if (0 != 0 && delegate.hasRegisteredListeners()) {
                        synchronized (this.mBrokenListeners) {
                            this.mBrokenListeners.add(null);
                        }
                    }
                    tryUnregisterBrokenListeners();
                    return;
                }
                MultiVibratorStateListener delegate2 = new MultiVibratorStateListener(executor, listener);
                delegate2.register(this.mVibratorManager);
                this.mRegisteredListeners.put(listener, delegate2);
                MultiVibratorStateListener delegate3 = null;
                if (0 != 0 && delegate3.hasRegisteredListeners()) {
                    synchronized (this.mBrokenListeners) {
                        this.mBrokenListeners.add(null);
                    }
                }
                tryUnregisterBrokenListeners();
            }
        } catch (Throwable th) {
            if (0 != 0 && delegate.hasRegisteredListeners()) {
                synchronized (this.mBrokenListeners) {
                    this.mBrokenListeners.add(null);
                }
            }
            tryUnregisterBrokenListeners();
            throw th;
        }
    }

    @Override // android.p008os.Vibrator
    public void removeVibratorStateListener(Vibrator.OnVibratorStateChangedListener listener) {
        Objects.requireNonNull(listener);
        if (this.mVibratorManager == null) {
            Log.m104w(TAG, "Failed to remove vibrate state listener; no vibrator manager.");
            return;
        }
        synchronized (this.mRegisteredListeners) {
            if (this.mRegisteredListeners.containsKey(listener)) {
                MultiVibratorStateListener delegate = this.mRegisteredListeners.get(listener);
                delegate.unregister(this.mVibratorManager);
                this.mRegisteredListeners.remove(listener);
            }
        }
        tryUnregisterBrokenListeners();
    }

    @Override // android.p008os.Vibrator
    public boolean hasAmplitudeControl() {
        return getInfo().hasAmplitudeControl();
    }

    @Override // android.p008os.Vibrator
    public boolean setAlwaysOnEffect(int uid, String opPkg, int alwaysOnId, VibrationEffect effect, VibrationAttributes attrs) {
        if (this.mVibratorManager == null) {
            Log.m104w(TAG, "Failed to set always-on effect; no vibrator manager.");
            return false;
        }
        CombinedVibration combinedEffect = CombinedVibration.createParallel(effect);
        return this.mVibratorManager.setAlwaysOnEffect(uid, opPkg, alwaysOnId, combinedEffect, attrs);
    }

    @Override // android.p008os.Vibrator
    public void vibrate(int uid, String opPkg, VibrationEffect effect, String reason, VibrationAttributes attributes) {
        if (this.mVibratorManager == null) {
            Log.m104w(TAG, "Failed to vibrate; no vibrator manager.");
            return;
        }
        CombinedVibration combinedEffect = CombinedVibration.createParallel(effect);
        this.mVibratorManager.vibrate(uid, opPkg, combinedEffect, reason, attributes);
    }

    @Override // android.p008os.Vibrator
    public void cancel() {
        VibratorManager vibratorManager = this.mVibratorManager;
        if (vibratorManager == null) {
            Log.m104w(TAG, "Failed to cancel vibrate; no vibrator manager.");
        } else {
            vibratorManager.cancel();
        }
    }

    @Override // android.p008os.Vibrator
    public void cancel(int usageFilter) {
        VibratorManager vibratorManager = this.mVibratorManager;
        if (vibratorManager == null) {
            Log.m104w(TAG, "Failed to cancel vibrate; no vibrator manager.");
        } else {
            vibratorManager.cancel(usageFilter);
        }
    }

    private void tryUnregisterBrokenListeners() {
        synchronized (this.mBrokenListeners) {
            try {
                int i = this.mBrokenListeners.size();
                while (true) {
                    i--;
                    if (i < 0) {
                        break;
                    }
                    this.mBrokenListeners.get(i).unregister(this.mVibratorManager);
                    this.mBrokenListeners.remove(i);
                }
            } catch (RuntimeException e) {
                Log.m103w(TAG, "Failed to unregister broken listener", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: android.os.SystemVibrator$SingleVibratorStateListener */
    /* loaded from: classes3.dex */
    public static class SingleVibratorStateListener implements Vibrator.OnVibratorStateChangedListener {
        private final MultiVibratorStateListener mAllVibratorsListener;
        private final int mVibratorIdx;

        SingleVibratorStateListener(MultiVibratorStateListener listener, int vibratorIdx) {
            this.mAllVibratorsListener = listener;
            this.mVibratorIdx = vibratorIdx;
        }

        @Override // android.p008os.Vibrator.OnVibratorStateChangedListener
        public void onVibratorStateChanged(boolean isVibrating) {
            this.mAllVibratorsListener.onVibrating(this.mVibratorIdx, isVibrating);
        }
    }

    /* renamed from: android.os.SystemVibrator$NoVibratorInfo */
    /* loaded from: classes3.dex */
    public static class NoVibratorInfo extends VibratorInfo {
        public NoVibratorInfo() {
            super(-1, 0L, new SparseBooleanArray(), new SparseBooleanArray(), new SparseIntArray(), 0, 0, 0, 0, Float.NaN, new VibratorInfo.FrequencyProfile(Float.NaN, Float.NaN, Float.NaN, null));
        }
    }

    /* renamed from: android.os.SystemVibrator$MultiVibratorInfo */
    /* loaded from: classes3.dex */
    public static class MultiVibratorInfo extends VibratorInfo {
        private static final float EPSILON = 1.0E-5f;

        public MultiVibratorInfo(VibratorInfo[] vibrators) {
            this(vibrators, frequencyProfileIntersection(vibrators));
        }

        private MultiVibratorInfo(VibratorInfo[] vibrators, VibratorInfo.FrequencyProfile mergedProfile) {
            super(-1, capabilitiesIntersection(vibrators, mergedProfile.isEmpty()), supportedEffectsIntersection(vibrators), supportedBrakingIntersection(vibrators), supportedPrimitivesAndDurationsIntersection(vibrators), integerLimitIntersection(vibrators, new Function() { // from class: android.os.SystemVibrator$MultiVibratorInfo$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return Integer.valueOf(((VibratorInfo) obj).getPrimitiveDelayMax());
                }
            }), integerLimitIntersection(vibrators, new Function() { // from class: android.os.SystemVibrator$MultiVibratorInfo$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return Integer.valueOf(((VibratorInfo) obj).getCompositionSizeMax());
                }
            }), integerLimitIntersection(vibrators, new Function() { // from class: android.os.SystemVibrator$MultiVibratorInfo$$ExternalSyntheticLambda2
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return Integer.valueOf(((VibratorInfo) obj).getPwlePrimitiveDurationMax());
                }
            }), integerLimitIntersection(vibrators, new Function() { // from class: android.os.SystemVibrator$MultiVibratorInfo$$ExternalSyntheticLambda3
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return Integer.valueOf(((VibratorInfo) obj).getPwleSizeMax());
                }
            }), floatPropertyIntersection(vibrators, new Function() { // from class: android.os.SystemVibrator$MultiVibratorInfo$$ExternalSyntheticLambda4
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return Float.valueOf(((VibratorInfo) obj).getQFactor());
                }
            }), mergedProfile);
        }

        private static int capabilitiesIntersection(VibratorInfo[] infos, boolean frequencyProfileIsEmpty) {
            int intersection = -1;
            for (VibratorInfo info : infos) {
                intersection = (int) (intersection & info.getCapabilities());
            }
            if (frequencyProfileIsEmpty) {
                return intersection & (-513);
            }
            return intersection;
        }

        private static SparseBooleanArray supportedBrakingIntersection(VibratorInfo[] infos) {
            for (VibratorInfo info : infos) {
                if (!info.isBrakingSupportKnown()) {
                    return null;
                }
            }
            SparseBooleanArray intersection = new SparseBooleanArray();
            SparseBooleanArray firstVibratorBraking = infos[0].getSupportedBraking();
            for (int i = 0; i < firstVibratorBraking.size(); i++) {
                int brakingId = firstVibratorBraking.keyAt(i);
                if (firstVibratorBraking.valueAt(i)) {
                    int j = 1;
                    while (true) {
                        if (j < infos.length) {
                            if (!infos[j].hasBrakingSupport(brakingId)) {
                                break;
                            }
                            j++;
                        } else {
                            intersection.put(brakingId, true);
                            break;
                        }
                    }
                }
            }
            return intersection;
        }

        private static SparseBooleanArray supportedEffectsIntersection(VibratorInfo[] infos) {
            for (VibratorInfo info : infos) {
                if (!info.isEffectSupportKnown()) {
                    return null;
                }
            }
            SparseBooleanArray intersection = new SparseBooleanArray();
            SparseBooleanArray firstVibratorEffects = infos[0].getSupportedEffects();
            for (int i = 0; i < firstVibratorEffects.size(); i++) {
                int effectId = firstVibratorEffects.keyAt(i);
                if (firstVibratorEffects.valueAt(i)) {
                    int j = 1;
                    while (true) {
                        if (j < infos.length) {
                            if (infos[j].isEffectSupported(effectId) != 1) {
                                break;
                            }
                            j++;
                        } else {
                            intersection.put(effectId, true);
                            break;
                        }
                    }
                }
            }
            return intersection;
        }

        private static SparseIntArray supportedPrimitivesAndDurationsIntersection(VibratorInfo[] infos) {
            SparseIntArray intersection = new SparseIntArray();
            SparseIntArray firstVibratorPrimitives = infos[0].getSupportedPrimitives();
            for (int i = 0; i < firstVibratorPrimitives.size(); i++) {
                int primitiveId = firstVibratorPrimitives.keyAt(i);
                int primitiveDuration = firstVibratorPrimitives.valueAt(i);
                if (primitiveDuration != 0) {
                    int j = 1;
                    while (true) {
                        if (j < infos.length) {
                            int vibratorPrimitiveDuration = infos[j].getPrimitiveDuration(primitiveId);
                            if (vibratorPrimitiveDuration == 0) {
                                break;
                            }
                            primitiveDuration = Math.max(primitiveDuration, vibratorPrimitiveDuration);
                            j++;
                        } else {
                            intersection.put(primitiveId, primitiveDuration);
                            break;
                        }
                    }
                }
            }
            return intersection;
        }

        private static int integerLimitIntersection(VibratorInfo[] infos, Function<VibratorInfo, Integer> propertyGetter) {
            int limit = 0;
            for (VibratorInfo info : infos) {
                int vibratorLimit = propertyGetter.apply(info).intValue();
                if (limit == 0 || (vibratorLimit > 0 && vibratorLimit < limit)) {
                    limit = vibratorLimit;
                }
            }
            return limit;
        }

        private static float floatPropertyIntersection(VibratorInfo[] infos, Function<VibratorInfo, Float> propertyGetter) {
            float property = propertyGetter.apply(infos[0]).floatValue();
            if (Float.isNaN(property)) {
                return Float.NaN;
            }
            for (int i = 1; i < infos.length; i++) {
                if (Float.compare(property, propertyGetter.apply(infos[i]).floatValue()) != 0) {
                    return Float.NaN;
                }
            }
            return property;
        }

        private static VibratorInfo.FrequencyProfile frequencyProfileIntersection(VibratorInfo[] infos) {
            float freqResolution = floatPropertyIntersection(infos, new Function() { // from class: android.os.SystemVibrator$MultiVibratorInfo$$ExternalSyntheticLambda5
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Float valueOf;
                    valueOf = Float.valueOf(((VibratorInfo) obj).getFrequencyProfile().getFrequencyResolutionHz());
                    return valueOf;
                }
            });
            float resonantFreq = floatPropertyIntersection(infos, new Function() { // from class: android.os.SystemVibrator$MultiVibratorInfo$$ExternalSyntheticLambda6
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return Float.valueOf(((VibratorInfo) obj).getResonantFrequencyHz());
                }
            });
            Range<Float> freqRange = frequencyRangeIntersection(infos, freqResolution);
            if (freqRange == null || Float.isNaN(freqResolution)) {
                return new VibratorInfo.FrequencyProfile(resonantFreq, Float.NaN, freqResolution, null);
            }
            int amplitudeCount = Math.round(((freqRange.getUpper().floatValue() - freqRange.getLower().floatValue()) / freqResolution) + 1.0f);
            float[] maxAmplitudes = new float[amplitudeCount];
            Arrays.fill(maxAmplitudes, Float.MAX_VALUE);
            for (VibratorInfo info : infos) {
                Range<Float> vibratorFreqRange = info.getFrequencyProfile().getFrequencyRangeHz();
                float[] vibratorMaxAmplitudes = info.getFrequencyProfile().getMaxAmplitudes();
                int vibratorStartIdx = Math.round((freqRange.getLower().floatValue() - vibratorFreqRange.getLower().floatValue()) / freqResolution);
                int vibratorEndIdx = (maxAmplitudes.length + vibratorStartIdx) - 1;
                if (vibratorStartIdx < 0 || vibratorEndIdx >= vibratorMaxAmplitudes.length) {
                    Slog.m90w(SystemVibrator.TAG, "Error calculating the intersection of vibrator frequency profiles: attempted to fetch from vibrator " + info.getId() + " max amplitude with bad index " + vibratorStartIdx);
                    return new VibratorInfo.FrequencyProfile(resonantFreq, Float.NaN, Float.NaN, null);
                }
                for (int i = 0; i < maxAmplitudes.length; i++) {
                    maxAmplitudes[i] = Math.min(maxAmplitudes[i], vibratorMaxAmplitudes[vibratorStartIdx + i]);
                }
            }
            return new VibratorInfo.FrequencyProfile(resonantFreq, freqRange.getLower().floatValue(), freqResolution, maxAmplitudes);
        }

        private static Range<Float> frequencyRangeIntersection(VibratorInfo[] infos, float frequencyResolution) {
            Range<Float> firstRange = infos[0].getFrequencyProfile().getFrequencyRangeHz();
            if (firstRange == null) {
                return null;
            }
            float intersectionLower = firstRange.getLower().floatValue();
            float intersectionUpper = firstRange.getUpper().floatValue();
            for (int i = 1; i < infos.length; i++) {
                Range<Float> vibratorRange = infos[i].getFrequencyProfile().getFrequencyRangeHz();
                if (vibratorRange == null || vibratorRange.getLower().floatValue() >= intersectionUpper || vibratorRange.getUpper().floatValue() <= intersectionLower) {
                    return null;
                }
                float frequencyDelta = Math.abs(intersectionLower - vibratorRange.getLower().floatValue());
                if (frequencyDelta % frequencyResolution > EPSILON) {
                    return null;
                }
                intersectionLower = Math.max(intersectionLower, vibratorRange.getLower().floatValue());
                intersectionUpper = Math.min(intersectionUpper, vibratorRange.getUpper().floatValue());
            }
            if (intersectionUpper - intersectionLower < frequencyResolution) {
                return null;
            }
            return Range.create(Float.valueOf(intersectionLower), Float.valueOf(intersectionUpper));
        }
    }

    /* renamed from: android.os.SystemVibrator$MultiVibratorStateListener */
    /* loaded from: classes3.dex */
    public static class MultiVibratorStateListener {
        private final Vibrator.OnVibratorStateChangedListener mDelegate;
        private final Executor mExecutor;
        private int mInitializedMask;
        private int mVibratingMask;
        private final Object mLock = new Object();
        private final SparseArray<SingleVibratorStateListener> mVibratorListeners = new SparseArray<>();

        public MultiVibratorStateListener(Executor executor, Vibrator.OnVibratorStateChangedListener listener) {
            this.mExecutor = executor;
            this.mDelegate = listener;
        }

        public boolean hasRegisteredListeners() {
            boolean z;
            synchronized (this.mLock) {
                z = this.mVibratorListeners.size() > 0;
            }
            return z;
        }

        public void register(VibratorManager vibratorManager) {
            int[] vibratorIds = vibratorManager.getVibratorIds();
            synchronized (this.mLock) {
                for (int i = 0; i < vibratorIds.length; i++) {
                    int vibratorId = vibratorIds[i];
                    SingleVibratorStateListener listener = new SingleVibratorStateListener(this, i);
                    try {
                        vibratorManager.getVibrator(vibratorId).addVibratorStateListener(this.mExecutor, listener);
                        this.mVibratorListeners.put(vibratorId, listener);
                    } catch (RuntimeException e) {
                        try {
                            unregister(vibratorManager);
                        } catch (RuntimeException e1) {
                            Log.m103w(SystemVibrator.TAG, "Failed to unregister listener while recovering from a failed register call", e1);
                        }
                        throw e;
                    }
                }
            }
        }

        public void unregister(VibratorManager vibratorManager) {
            synchronized (this.mLock) {
                int i = this.mVibratorListeners.size();
                while (true) {
                    i--;
                    if (i >= 0) {
                        int vibratorId = this.mVibratorListeners.keyAt(i);
                        SingleVibratorStateListener listener = this.mVibratorListeners.valueAt(i);
                        vibratorManager.getVibrator(vibratorId).removeVibratorStateListener(listener);
                        this.mVibratorListeners.removeAt(i);
                    }
                }
            }
        }

        public void onVibrating(final int vibratorIdx, final boolean vibrating) {
            this.mExecutor.execute(new Runnable() { // from class: android.os.SystemVibrator$MultiVibratorStateListener$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SystemVibrator.MultiVibratorStateListener.this.lambda$onVibrating$0(vibratorIdx, vibrating);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onVibrating$0(int vibratorIdx, boolean vibrating) {
            boolean isAnyVibrating;
            int allInitializedMask;
            synchronized (this.mLock) {
                int i = 1;
                int allInitializedMask2 = (1 << this.mVibratorListeners.size()) - 1;
                int i2 = this.mVibratingMask;
                boolean previousIsAnyVibrating = i2 != 0;
                int i3 = this.mInitializedMask;
                boolean previousAreAllInitialized = i3 == allInitializedMask2;
                int vibratorMask = 1 << vibratorIdx;
                int i4 = i3 | vibratorMask;
                this.mInitializedMask = i4;
                boolean previousVibrating = (i2 & vibratorMask) != 0;
                if (previousVibrating != vibrating) {
                    this.mVibratingMask = i2 ^ vibratorMask;
                }
                isAnyVibrating = this.mVibratingMask != 0;
                boolean areAllInitialized = i4 == allInitializedMask2;
                boolean isStateChanging = previousIsAnyVibrating != isAnyVibrating;
                if (!areAllInitialized || (previousAreAllInitialized && !isStateChanging)) {
                    i = 0;
                }
                allInitializedMask = i;
            }
            if (allInitializedMask != 0) {
                this.mDelegate.onVibratorStateChanged(isAnyVibrating);
            }
        }
    }
}
