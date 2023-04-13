package android.gesture;

import java.util.ArrayList;
/* loaded from: classes.dex */
abstract class Learner {
    private final ArrayList<Instance> mInstances = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract ArrayList<Prediction> classify(int i, int i2, float[] fArr);

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addInstance(Instance instance) {
        this.mInstances.add(instance);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayList<Instance> getInstances() {
        return this.mInstances;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeInstance(long id) {
        ArrayList<Instance> instances = this.mInstances;
        int count = instances.size();
        for (int i = 0; i < count; i++) {
            Instance instance = instances.get(i);
            if (id == instance.f60id) {
                instances.remove(instance);
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeInstances(String name) {
        ArrayList<Instance> toDelete = new ArrayList<>();
        ArrayList<Instance> instances = this.mInstances;
        int count = instances.size();
        for (int i = 0; i < count; i++) {
            Instance instance = instances.get(i);
            if ((instance.label == null && name == null) || (instance.label != null && instance.label.equals(name))) {
                toDelete.add(instance);
            }
        }
        instances.removeAll(toDelete);
    }
}
