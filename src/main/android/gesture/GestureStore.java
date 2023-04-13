package android.gesture;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/* loaded from: classes.dex */
public class GestureStore {
    private static final short FILE_FORMAT_VERSION = 1;
    public static final int ORIENTATION_INVARIANT = 1;
    public static final int ORIENTATION_SENSITIVE = 2;
    static final int ORIENTATION_SENSITIVE_4 = 4;
    static final int ORIENTATION_SENSITIVE_8 = 8;
    private static final boolean PROFILE_LOADING_SAVING = false;
    public static final int SEQUENCE_INVARIANT = 1;
    public static final int SEQUENCE_SENSITIVE = 2;
    private int mSequenceType = 2;
    private int mOrientationStyle = 2;
    private final HashMap<String, ArrayList<Gesture>> mNamedGestures = new HashMap<>();
    private boolean mChanged = false;
    private Learner mClassifier = new InstanceLearner();

    public void setOrientationStyle(int style) {
        this.mOrientationStyle = style;
    }

    public int getOrientationStyle() {
        return this.mOrientationStyle;
    }

    public void setSequenceType(int type) {
        this.mSequenceType = type;
    }

    public int getSequenceType() {
        return this.mSequenceType;
    }

    public Set<String> getGestureEntries() {
        return this.mNamedGestures.keySet();
    }

    public ArrayList<Prediction> recognize(Gesture gesture) {
        Instance instance = Instance.createInstance(this.mSequenceType, this.mOrientationStyle, gesture, null);
        return this.mClassifier.classify(this.mSequenceType, this.mOrientationStyle, instance.vector);
    }

    public void addGesture(String entryName, Gesture gesture) {
        if (entryName == null || entryName.length() == 0) {
            return;
        }
        ArrayList<Gesture> gestures = this.mNamedGestures.get(entryName);
        if (gestures == null) {
            gestures = new ArrayList<>();
            this.mNamedGestures.put(entryName, gestures);
        }
        gestures.add(gesture);
        this.mClassifier.addInstance(Instance.createInstance(this.mSequenceType, this.mOrientationStyle, gesture, entryName));
        this.mChanged = true;
    }

    public void removeGesture(String entryName, Gesture gesture) {
        ArrayList<Gesture> gestures = this.mNamedGestures.get(entryName);
        if (gestures == null) {
            return;
        }
        gestures.remove(gesture);
        if (gestures.isEmpty()) {
            this.mNamedGestures.remove(entryName);
        }
        this.mClassifier.removeInstance(gesture.getID());
        this.mChanged = true;
    }

    public void removeEntry(String entryName) {
        this.mNamedGestures.remove(entryName);
        this.mClassifier.removeInstances(entryName);
        this.mChanged = true;
    }

    public ArrayList<Gesture> getGestures(String entryName) {
        ArrayList<Gesture> gestures = this.mNamedGestures.get(entryName);
        if (gestures != null) {
            return new ArrayList<>(gestures);
        }
        return null;
    }

    public boolean hasChanged() {
        return this.mChanged;
    }

    public void save(OutputStream stream) throws IOException {
        save(stream, false);
    }

    public void save(OutputStream stream, boolean closeStream) throws IOException {
        DataOutputStream out = null;
        try {
            HashMap<String, ArrayList<Gesture>> maps = this.mNamedGestures;
            out = new DataOutputStream(stream instanceof BufferedOutputStream ? stream : new BufferedOutputStream(stream, 32768));
            out.writeShort(1);
            out.writeInt(maps.size());
            for (Map.Entry<String, ArrayList<Gesture>> entry : maps.entrySet()) {
                String key = entry.getKey();
                ArrayList<Gesture> examples = entry.getValue();
                int count = examples.size();
                out.writeUTF(key);
                out.writeInt(count);
                for (int i = 0; i < count; i++) {
                    examples.get(i).serialize(out);
                }
            }
            out.flush();
            this.mChanged = false;
        } finally {
            if (closeStream) {
                GestureUtils.closeStream(out);
            }
        }
    }

    public void load(InputStream stream) throws IOException {
        load(stream, false);
    }

    public void load(InputStream stream, boolean closeStream) throws IOException {
        DataInputStream in = null;
        try {
            in = new DataInputStream(stream instanceof BufferedInputStream ? stream : new BufferedInputStream(stream, 32768));
            short versionNumber = in.readShort();
            switch (versionNumber) {
                case 1:
                    readFormatV1(in);
                    break;
            }
        } finally {
            if (closeStream) {
                GestureUtils.closeStream(in);
            }
        }
    }

    private void readFormatV1(DataInputStream in) throws IOException {
        Learner classifier = this.mClassifier;
        HashMap<String, ArrayList<Gesture>> namedGestures = this.mNamedGestures;
        namedGestures.clear();
        int entriesCount = in.readInt();
        for (int i = 0; i < entriesCount; i++) {
            String name = in.readUTF();
            int gestureCount = in.readInt();
            ArrayList<Gesture> gestures = new ArrayList<>(gestureCount);
            for (int j = 0; j < gestureCount; j++) {
                Gesture gesture = Gesture.deserialize(in);
                gestures.add(gesture);
                classifier.addInstance(Instance.createInstance(this.mSequenceType, this.mOrientationStyle, gesture, name));
            }
            namedGestures.put(name, gestures);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Learner getLearner() {
        return this.mClassifier;
    }
}
