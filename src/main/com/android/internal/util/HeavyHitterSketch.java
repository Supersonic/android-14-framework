package com.android.internal.util;

import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.util.HeavyHitterSketch;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes3.dex */
public interface HeavyHitterSketch<T> {
    void add(T t);

    List<T> getCandidates(List<T> list);

    float getRequiredValidationInputRatio();

    List<T> getTopHeavyHitters(int i, List<T> list, List<Float> list2);

    void reset();

    void setConfig(int i, int i2);

    static <V> HeavyHitterSketch<V> newDefault() {
        return new HeavyHitterSketchImpl();
    }

    /* loaded from: classes3.dex */
    public static final class HeavyHitterSketchImpl<T> implements HeavyHitterSketch<T> {
        private int mCapacity;
        private boolean mConfigured;
        private int mNumInputs;
        private int mPassSize;
        private int mTotalSize;
        private final SparseArray<T> mObjects = new SparseArray<>();
        private final SparseIntArray mFrequencies = new SparseIntArray();

        @Override // com.android.internal.util.HeavyHitterSketch
        public void setConfig(int inputSize, int capacity) {
            if (inputSize < capacity || inputSize <= 1) {
                this.mConfigured = false;
                throw new IllegalArgumentException();
            }
            reset();
            this.mTotalSize = inputSize;
            this.mPassSize = inputSize >> 1;
            this.mCapacity = capacity;
            this.mConfigured = true;
        }

        @Override // com.android.internal.util.HeavyHitterSketch
        public void add(T newInstance) {
            if (!this.mConfigured) {
                throw new IllegalStateException();
            }
            int i = this.mNumInputs;
            if (i < this.mPassSize) {
                addToMGSummary(newInstance);
            } else if (i < this.mTotalSize) {
                validate(newInstance);
            }
        }

        private void addToMGSummary(T newInstance) {
            int hashCode = newInstance != null ? newInstance.hashCode() : 0;
            int index = this.mObjects.indexOfKey(hashCode);
            if (index < 0) {
                if (this.mObjects.size() >= this.mCapacity - 1) {
                    for (int i = this.mFrequencies.size() - 1; i >= 0; i--) {
                        int val = this.mFrequencies.valueAt(i) - 1;
                        if (val == 0) {
                            this.mObjects.removeAt(i);
                            this.mFrequencies.removeAt(i);
                        } else {
                            this.mFrequencies.setValueAt(i, val);
                        }
                    }
                } else {
                    this.mObjects.put(hashCode, newInstance);
                    this.mFrequencies.put(hashCode, 1);
                }
            } else {
                SparseIntArray sparseIntArray = this.mFrequencies;
                sparseIntArray.setValueAt(index, sparseIntArray.valueAt(index) + 1);
            }
            int i2 = this.mNumInputs;
            int i3 = i2 + 1;
            this.mNumInputs = i3;
            if (i3 == this.mPassSize) {
                for (int i4 = this.mFrequencies.size() - 1; i4 >= 0; i4--) {
                    this.mFrequencies.setValueAt(i4, 0);
                }
            }
        }

        private void validate(T newInstance) {
            int hashCode = newInstance != null ? newInstance.hashCode() : 0;
            int index = this.mObjects.indexOfKey(hashCode);
            if (index >= 0) {
                SparseIntArray sparseIntArray = this.mFrequencies;
                sparseIntArray.setValueAt(index, sparseIntArray.valueAt(index) + 1);
            }
            int i = this.mNumInputs + 1;
            this.mNumInputs = i;
            if (i == this.mTotalSize) {
                int lower = this.mPassSize / this.mCapacity;
                for (int i2 = this.mFrequencies.size() - 1; i2 >= 0; i2--) {
                    int val = this.mFrequencies.valueAt(i2);
                    if (val < lower) {
                        this.mFrequencies.removeAt(i2);
                        this.mObjects.removeAt(i2);
                    }
                }
            }
        }

        @Override // com.android.internal.util.HeavyHitterSketch
        public List<T> getTopHeavyHitters(int k, List<T> holder, List<Float> freqs) {
            if (!this.mConfigured) {
                throw new IllegalStateException();
            }
            if (k >= this.mCapacity) {
                throw new IllegalArgumentException();
            }
            if (this.mNumInputs < this.mTotalSize) {
                throw new IllegalStateException();
            }
            ArrayList<Integer> indexes = null;
            for (int i = this.mFrequencies.size() - 1; i >= 0; i--) {
                int val = this.mFrequencies.valueAt(i);
                if (val > 0) {
                    if (indexes == null) {
                        indexes = new ArrayList<>();
                    }
                    indexes.add(Integer.valueOf(i));
                }
            }
            if (indexes == null) {
                return null;
            }
            Collections.sort(indexes, new Comparator() { // from class: com.android.internal.util.HeavyHitterSketch$HeavyHitterSketchImpl$$ExternalSyntheticLambda0
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    int lambda$getTopHeavyHitters$0;
                    lambda$getTopHeavyHitters$0 = HeavyHitterSketch.HeavyHitterSketchImpl.this.lambda$getTopHeavyHitters$0((Integer) obj, (Integer) obj2);
                    return lambda$getTopHeavyHitters$0;
                }
            });
            List<T> result = holder != null ? holder : new ArrayList<>();
            int max = Math.min(k == 0 ? this.mCapacity - 1 : k, indexes.size());
            for (int i2 = 0; i2 < max; i2++) {
                int index = indexes.get(i2).intValue();
                T obj = this.mObjects.valueAt(index);
                if (obj != null) {
                    result.add(obj);
                    if (freqs != null) {
                        freqs.add(Float.valueOf(this.mFrequencies.valueAt(index) / this.mPassSize));
                    }
                }
            }
            return result;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ int lambda$getTopHeavyHitters$0(Integer a, Integer b) {
            return this.mFrequencies.valueAt(b.intValue()) - this.mFrequencies.valueAt(a.intValue());
        }

        @Override // com.android.internal.util.HeavyHitterSketch
        public List<T> getCandidates(List<T> holder) {
            if (!this.mConfigured) {
                throw new IllegalStateException();
            }
            if (this.mNumInputs < this.mPassSize) {
                return null;
            }
            List<T> result = holder != null ? holder : new ArrayList<>();
            for (int i = this.mObjects.size() - 1; i >= 0; i--) {
                T obj = this.mObjects.valueAt(i);
                if (obj != null) {
                    result.add(obj);
                }
            }
            return result;
        }

        @Override // com.android.internal.util.HeavyHitterSketch
        public void reset() {
            this.mNumInputs = 0;
            this.mObjects.clear();
            this.mFrequencies.clear();
        }

        @Override // com.android.internal.util.HeavyHitterSketch
        public float getRequiredValidationInputRatio() {
            return 0.5f;
        }
    }
}
