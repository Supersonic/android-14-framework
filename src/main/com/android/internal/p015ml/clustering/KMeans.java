package com.android.internal.p015ml.clustering;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
/* renamed from: com.android.internal.ml.clustering.KMeans */
/* loaded from: classes4.dex */
public class KMeans {
    private static final boolean DEBUG = false;
    private static final String TAG = "KMeans";
    private final int mMaxIterations;
    private final Random mRandomState;
    private float mSqConvergenceEpsilon;

    public KMeans() {
        this(new Random());
    }

    public KMeans(Random random) {
        this(random, 30, 0.005f);
    }

    public KMeans(Random random, int maxIterations, float convergenceEpsilon) {
        this.mRandomState = random;
        this.mMaxIterations = maxIterations;
        this.mSqConvergenceEpsilon = convergenceEpsilon * convergenceEpsilon;
    }

    public List<Mean> predict(int k, float[][] inputData) {
        checkDataSetSanity(inputData);
        int dimension = inputData[0].length;
        ArrayList<Mean> means = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            Mean m = new Mean(dimension);
            for (int j = 0; j < dimension; j++) {
                m.mCentroid[j] = this.mRandomState.nextFloat();
            }
            means.add(m);
        }
        for (int i2 = 0; i2 < this.mMaxIterations; i2++) {
            boolean converged = step(means, inputData);
            if (converged) {
                break;
            }
        }
        return means;
    }

    public static double score(List<Mean> means) {
        double score = 0.0d;
        int meansSize = means.size();
        for (int i = 0; i < meansSize; i++) {
            Mean mean = means.get(i);
            for (int j = 0; j < meansSize; j++) {
                Mean compareTo = means.get(j);
                if (mean != compareTo) {
                    double distance = Math.sqrt(sqDistance(mean.mCentroid, compareTo.mCentroid));
                    score += distance;
                }
            }
        }
        return score;
    }

    public void checkDataSetSanity(float[][] inputData) {
        if (inputData == null) {
            throw new IllegalArgumentException("Data set is null.");
        }
        if (inputData.length == 0) {
            throw new IllegalArgumentException("Data set is empty.");
        }
        if (inputData[0] == null) {
            throw new IllegalArgumentException("Bad data set format.");
        }
        int dimension = inputData[0].length;
        int length = inputData.length;
        for (int i = 1; i < length; i++) {
            if (inputData[i] == null || inputData[i].length != dimension) {
                throw new IllegalArgumentException("Bad data set format.");
            }
        }
    }

    private boolean step(ArrayList<Mean> means, float[][] inputData) {
        for (int i = means.size() - 1; i >= 0; i--) {
            means.get(i).mClosestItems.clear();
        }
        int i2 = inputData.length;
        for (int i3 = i2 - 1; i3 >= 0; i3--) {
            float[] current = inputData[i3];
            Mean nearest = nearestMean(current, means);
            nearest.mClosestItems.add(current);
        }
        boolean converged = true;
        for (int i4 = means.size() - 1; i4 >= 0; i4--) {
            Mean mean = means.get(i4);
            if (mean.mClosestItems.size() != 0) {
                float[] oldCentroid = mean.mCentroid;
                mean.mCentroid = new float[oldCentroid.length];
                for (int j = 0; j < mean.mClosestItems.size(); j++) {
                    for (int p = 0; p < mean.mCentroid.length; p++) {
                        float[] fArr = mean.mCentroid;
                        fArr[p] = fArr[p] + mean.mClosestItems.get(j)[p];
                    }
                }
                for (int j2 = 0; j2 < mean.mCentroid.length; j2++) {
                    float[] fArr2 = mean.mCentroid;
                    fArr2[j2] = fArr2[j2] / mean.mClosestItems.size();
                }
                if (sqDistance(oldCentroid, mean.mCentroid) > this.mSqConvergenceEpsilon) {
                    converged = false;
                }
            }
        }
        return converged;
    }

    public static Mean nearestMean(float[] point, List<Mean> means) {
        Mean nearest = null;
        float nearestDistance = Float.MAX_VALUE;
        int meanCount = means.size();
        for (int i = 0; i < meanCount; i++) {
            Mean next = means.get(i);
            float nextDistance = sqDistance(point, next.mCentroid);
            if (nextDistance < nearestDistance) {
                nearest = next;
                nearestDistance = nextDistance;
            }
        }
        return nearest;
    }

    public static float sqDistance(float[] a, float[] b) {
        float dist = 0.0f;
        int length = a.length;
        for (int i = 0; i < length; i++) {
            dist += (a[i] - b[i]) * (a[i] - b[i]);
        }
        return dist;
    }

    /* renamed from: com.android.internal.ml.clustering.KMeans$Mean */
    /* loaded from: classes4.dex */
    public static class Mean {
        float[] mCentroid;
        final ArrayList<float[]> mClosestItems = new ArrayList<>();

        public Mean(int dimension) {
            this.mCentroid = new float[dimension];
        }

        public Mean(float... centroid) {
            this.mCentroid = centroid;
        }

        public float[] getCentroid() {
            return this.mCentroid;
        }

        public List<float[]> getItems() {
            return this.mClosestItems;
        }

        public String toString() {
            return "Mean(centroid: " + Arrays.toString(this.mCentroid) + ", size: " + this.mClosestItems.size() + NavigationBarInflaterView.KEY_CODE_END;
        }
    }
}
