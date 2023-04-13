package android.renderscript;

import android.renderscript.Script;
import android.telecom.Logging.Session;
import android.util.Log;
import android.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Deprecated
/* loaded from: classes3.dex */
public final class ScriptGroup extends BaseObj {
    private static final String TAG = "ScriptGroup";
    private List<Closure> mClosures;
    C2371IO[] mInputs;
    private List<Input> mInputs2;
    private String mName;
    C2371IO[] mOutputs;
    private Future[] mOutputs2;

    /* renamed from: android.renderscript.ScriptGroup$IO */
    /* loaded from: classes3.dex */
    static class C2371IO {
        Allocation mAllocation;
        Script.KernelID mKID;

        C2371IO(Script.KernelID s) {
            this.mKID = s;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static class ConnectLine {
        Type mAllocationType;
        Script.KernelID mFrom;
        Script.FieldID mToF;
        Script.KernelID mToK;

        ConnectLine(Type t, Script.KernelID from, Script.KernelID to) {
            this.mFrom = from;
            this.mToK = to;
            this.mAllocationType = t;
        }

        ConnectLine(Type t, Script.KernelID from, Script.FieldID to) {
            this.mFrom = from;
            this.mToF = to;
            this.mAllocationType = t;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static class Node {
        int dagNumber;
        Node mNext;
        Script mScript;
        ArrayList<Script.KernelID> mKernels = new ArrayList<>();
        ArrayList<ConnectLine> mInputs = new ArrayList<>();
        ArrayList<ConnectLine> mOutputs = new ArrayList<>();

        Node(Script s) {
            this.mScript = s;
        }
    }

    /* loaded from: classes3.dex */
    public static final class Closure extends BaseObj {
        private static final String TAG = "Closure";
        private Object[] mArgs;
        private Map<Script.FieldID, Object> mBindings;
        private FieldPacker mFP;
        private Map<Script.FieldID, Future> mGlobalFuture;
        private Future mReturnFuture;
        private Allocation mReturnValue;

        Closure(long id, RenderScript rs) {
            super(id, rs);
        }

        Closure(RenderScript rs, Script.KernelID kernelID, Type returnType, Object[] args, Map<Script.FieldID, Object> globals) {
            super(0L, rs);
            this.mArgs = args;
            this.mReturnValue = Allocation.createTyped(rs, returnType);
            this.mBindings = globals;
            this.mGlobalFuture = new HashMap();
            int numValues = args.length + globals.size();
            long[] fieldIDs = new long[numValues];
            long[] values = new long[numValues];
            int[] sizes = new int[numValues];
            long[] depClosures = new long[numValues];
            long[] depFieldIDs = new long[numValues];
            int i = 0;
            while (i < args.length) {
                fieldIDs[i] = 0;
                long[] depFieldIDs2 = depFieldIDs;
                long[] depClosures2 = depClosures;
                int[] sizes2 = sizes;
                retrieveValueAndDependenceInfo(rs, i, null, args[i], values, sizes2, depClosures2, depFieldIDs2);
                i++;
                depFieldIDs = depFieldIDs2;
                depClosures = depClosures2;
                sizes = sizes2;
                values = values;
                fieldIDs = fieldIDs;
                numValues = numValues;
            }
            long[] depFieldIDs3 = depFieldIDs;
            long[] depClosures3 = depClosures;
            int[] sizes3 = sizes;
            long[] values2 = values;
            long[] fieldIDs2 = fieldIDs;
            int i2 = i;
            for (Map.Entry<Script.FieldID, Object> entry : globals.entrySet()) {
                Object obj = entry.getValue();
                Script.FieldID fieldID = entry.getKey();
                fieldIDs2[i2] = fieldID.getID(rs);
                retrieveValueAndDependenceInfo(rs, i2, fieldID, obj, values2, sizes3, depClosures3, depFieldIDs3);
                i2++;
            }
            long id = rs.nClosureCreate(kernelID.getID(rs), this.mReturnValue.getID(rs), fieldIDs2, values2, sizes3, depClosures3, depFieldIDs3);
            setID(id);
            this.guard.open("destroy");
        }

        Closure(RenderScript rs, Script.InvokeID invokeID, Object[] args, Map<Script.FieldID, Object> globals) {
            super(0L, rs);
            this.mFP = FieldPacker.createFromArray(args);
            this.mArgs = args;
            this.mBindings = globals;
            this.mGlobalFuture = new HashMap();
            int numValues = globals.size();
            long[] fieldIDs = new long[numValues];
            long[] values = new long[numValues];
            int[] sizes = new int[numValues];
            long[] jArr = new long[numValues];
            long[] depFieldIDs = new long[numValues];
            int i = 0;
            for (Map.Entry<Script.FieldID, Object> entry : globals.entrySet()) {
                Object obj = entry.getValue();
                Script.FieldID fieldID = entry.getKey();
                fieldIDs[i] = fieldID.getID(rs);
                long[] depFieldIDs2 = depFieldIDs;
                retrieveValueAndDependenceInfo(rs, i, fieldID, obj, values, sizes, jArr, depFieldIDs2);
                i++;
                depFieldIDs = depFieldIDs2;
                sizes = sizes;
            }
            long id = rs.nInvokeClosureCreate(invokeID.getID(rs), this.mFP.getData(), fieldIDs, values, sizes);
            setID(id);
            this.guard.open("destroy");
        }

        @Override // android.renderscript.BaseObj
        public void destroy() {
            super.destroy();
            Allocation allocation = this.mReturnValue;
            if (allocation != null) {
                allocation.destroy();
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.renderscript.BaseObj
        public void finalize() throws Throwable {
            this.mReturnValue = null;
            super.finalize();
        }

        private void retrieveValueAndDependenceInfo(RenderScript rs, int index, Script.FieldID fid, Object obj, long[] values, int[] sizes, long[] depClosures, long[] depFieldIDs) {
            if (obj instanceof Future) {
                Future f = (Future) obj;
                obj = f.getValue();
                depClosures[index] = f.getClosure().getID(rs);
                Script.FieldID fieldID = f.getFieldID();
                depFieldIDs[index] = fieldID != null ? fieldID.getID(rs) : 0L;
            } else {
                depClosures[index] = 0;
                depFieldIDs[index] = 0;
            }
            if (obj instanceof Input) {
                Input unbound = (Input) obj;
                if (index < this.mArgs.length) {
                    unbound.addReference(this, index);
                } else {
                    unbound.addReference(this, fid);
                }
                values[index] = 0;
                sizes[index] = 0;
                return;
            }
            ValueAndSize vs = new ValueAndSize(rs, obj);
            values[index] = vs.value;
            sizes[index] = vs.size;
        }

        public Future getReturn() {
            if (this.mReturnFuture == null) {
                this.mReturnFuture = new Future(this, null, this.mReturnValue);
            }
            return this.mReturnFuture;
        }

        public Future getGlobal(Script.FieldID field) {
            Future f = this.mGlobalFuture.get(field);
            if (f == null) {
                Object obj = this.mBindings.get(field);
                if (obj instanceof Future) {
                    obj = ((Future) obj).getValue();
                }
                Future f2 = new Future(this, field, obj);
                this.mGlobalFuture.put(field, f2);
                return f2;
            }
            return f;
        }

        void setArg(int index, Object obj) {
            if (obj instanceof Future) {
                obj = ((Future) obj).getValue();
            }
            this.mArgs[index] = obj;
            ValueAndSize vs = new ValueAndSize(this.mRS, obj);
            this.mRS.nClosureSetArg(getID(this.mRS), index, vs.value, vs.size);
        }

        void setGlobal(Script.FieldID fieldID, Object obj) {
            if (obj instanceof Future) {
                obj = ((Future) obj).getValue();
            }
            this.mBindings.put(fieldID, obj);
            ValueAndSize vs = new ValueAndSize(this.mRS, obj);
            this.mRS.nClosureSetGlobal(getID(this.mRS), fieldID.getID(this.mRS), vs.value, vs.size);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static final class ValueAndSize {
            public int size;
            public long value;

            public ValueAndSize(RenderScript rs, Object obj) {
                if (obj instanceof Allocation) {
                    this.value = ((Allocation) obj).getID(rs);
                    this.size = -1;
                } else if (obj instanceof Boolean) {
                    this.value = ((Boolean) obj).booleanValue() ? 1L : 0L;
                    this.size = 4;
                } else if (obj instanceof Integer) {
                    this.value = ((Integer) obj).longValue();
                    this.size = 4;
                } else if (obj instanceof Long) {
                    this.value = ((Long) obj).longValue();
                    this.size = 8;
                } else if (obj instanceof Float) {
                    this.value = Float.floatToRawIntBits(((Float) obj).floatValue());
                    this.size = 4;
                } else if (obj instanceof Double) {
                    this.value = Double.doubleToRawLongBits(((Double) obj).doubleValue());
                    this.size = 8;
                }
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class Future {
        Closure mClosure;
        Script.FieldID mFieldID;
        Object mValue;

        Future(Closure closure, Script.FieldID fieldID, Object value) {
            this.mClosure = closure;
            this.mFieldID = fieldID;
            this.mValue = value;
        }

        Closure getClosure() {
            return this.mClosure;
        }

        Script.FieldID getFieldID() {
            return this.mFieldID;
        }

        Object getValue() {
            return this.mValue;
        }
    }

    /* loaded from: classes3.dex */
    public static final class Input {
        Object mValue;
        List<Pair<Closure, Script.FieldID>> mFieldID = new ArrayList();
        List<Pair<Closure, Integer>> mArgIndex = new ArrayList();

        Input() {
        }

        void addReference(Closure closure, int index) {
            this.mArgIndex.add(Pair.create(closure, Integer.valueOf(index)));
        }

        void addReference(Closure closure, Script.FieldID fieldID) {
            this.mFieldID.add(Pair.create(closure, fieldID));
        }

        void set(Object value) {
            this.mValue = value;
            for (Pair<Closure, Integer> p : this.mArgIndex) {
                Closure closure = p.first;
                int index = p.second.intValue();
                closure.setArg(index, value);
            }
            for (Pair<Closure, Script.FieldID> p2 : this.mFieldID) {
                Closure closure2 = p2.first;
                Script.FieldID fieldID = p2.second;
                closure2.setGlobal(fieldID, value);
            }
        }

        Object get() {
            return this.mValue;
        }
    }

    ScriptGroup(long id, RenderScript rs) {
        super(id, rs);
        this.guard.open("destroy");
    }

    ScriptGroup(RenderScript rs, String name, List<Closure> closures, List<Input> inputs, Future[] outputs) {
        super(0L, rs);
        this.mName = name;
        this.mClosures = closures;
        this.mInputs2 = inputs;
        this.mOutputs2 = outputs;
        long[] closureIDs = new long[closures.size()];
        for (int i = 0; i < closureIDs.length; i++) {
            closureIDs[i] = closures.get(i).getID(rs);
        }
        long id = rs.nScriptGroup2Create(name, RenderScript.getCachePath(), closureIDs);
        setID(id);
        this.guard.open("destroy");
    }

    public Object[] execute(Object... inputs) {
        if (inputs.length < this.mInputs2.size()) {
            Log.m110e(TAG, toString() + " receives " + inputs.length + " inputs, less than expected " + this.mInputs2.size());
            return null;
        }
        if (inputs.length > this.mInputs2.size()) {
            Log.m108i(TAG, toString() + " receives " + inputs.length + " inputs, more than expected " + this.mInputs2.size());
        }
        for (int i = 0; i < this.mInputs2.size(); i++) {
            Object obj = inputs[i];
            if ((obj instanceof Future) || (obj instanceof Input)) {
                Log.m110e(TAG, toString() + ": input " + i + " is a future or unbound value");
                return null;
            }
            Input unbound = this.mInputs2.get(i);
            unbound.set(obj);
        }
        this.mRS.nScriptGroup2Execute(getID(this.mRS));
        Future[] futureArr = this.mOutputs2;
        Object[] outputObjs = new Object[futureArr.length];
        int i2 = 0;
        int length = futureArr.length;
        int i3 = 0;
        while (i3 < length) {
            Future f = futureArr[i3];
            Object output = f.getValue();
            if (output instanceof Input) {
                output = ((Input) output).get();
            }
            outputObjs[i2] = output;
            i3++;
            i2++;
        }
        return outputObjs;
    }

    public void setInput(Script.KernelID s, Allocation a) {
        int ct = 0;
        while (true) {
            C2371IO[] c2371ioArr = this.mInputs;
            if (ct < c2371ioArr.length) {
                if (c2371ioArr[ct].mKID != s) {
                    ct++;
                } else {
                    this.mInputs[ct].mAllocation = a;
                    this.mRS.nScriptGroupSetInput(getID(this.mRS), s.getID(this.mRS), this.mRS.safeID(a));
                    return;
                }
            } else {
                throw new RSIllegalArgumentException("Script not found");
            }
        }
    }

    public void setOutput(Script.KernelID s, Allocation a) {
        int ct = 0;
        while (true) {
            C2371IO[] c2371ioArr = this.mOutputs;
            if (ct < c2371ioArr.length) {
                if (c2371ioArr[ct].mKID != s) {
                    ct++;
                } else {
                    this.mOutputs[ct].mAllocation = a;
                    this.mRS.nScriptGroupSetOutput(getID(this.mRS), s.getID(this.mRS), this.mRS.safeID(a));
                    return;
                }
            } else {
                throw new RSIllegalArgumentException("Script not found");
            }
        }
    }

    public void execute() {
        this.mRS.nScriptGroupExecute(getID(this.mRS));
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private int mKernelCount;
        private RenderScript mRS;
        private ArrayList<Node> mNodes = new ArrayList<>();
        private ArrayList<ConnectLine> mLines = new ArrayList<>();

        public Builder(RenderScript rs) {
            this.mRS = rs;
        }

        private void validateCycle(Node target, Node original) {
            for (int ct = 0; ct < target.mOutputs.size(); ct++) {
                ConnectLine cl = target.mOutputs.get(ct);
                if (cl.mToK != null) {
                    Node tn = findNode(cl.mToK.mScript);
                    if (tn.equals(original)) {
                        throw new RSInvalidStateException("Loops in group not allowed.");
                    }
                    validateCycle(tn, original);
                }
                if (cl.mToF != null) {
                    Node tn2 = findNode(cl.mToF.mScript);
                    if (tn2.equals(original)) {
                        throw new RSInvalidStateException("Loops in group not allowed.");
                    }
                    validateCycle(tn2, original);
                }
            }
        }

        private void mergeDAGs(int valueUsed, int valueKilled) {
            for (int ct = 0; ct < this.mNodes.size(); ct++) {
                if (this.mNodes.get(ct).dagNumber == valueKilled) {
                    this.mNodes.get(ct).dagNumber = valueUsed;
                }
            }
        }

        private void validateDAGRecurse(Node n, int dagNumber) {
            if (n.dagNumber != 0 && n.dagNumber != dagNumber) {
                mergeDAGs(n.dagNumber, dagNumber);
                return;
            }
            n.dagNumber = dagNumber;
            for (int ct = 0; ct < n.mOutputs.size(); ct++) {
                ConnectLine cl = n.mOutputs.get(ct);
                if (cl.mToK != null) {
                    Node tn = findNode(cl.mToK.mScript);
                    validateDAGRecurse(tn, dagNumber);
                }
                if (cl.mToF != null) {
                    Node tn2 = findNode(cl.mToF.mScript);
                    validateDAGRecurse(tn2, dagNumber);
                }
            }
        }

        private void validateDAG() {
            for (int ct = 0; ct < this.mNodes.size(); ct++) {
                Node n = this.mNodes.get(ct);
                if (n.mInputs.size() == 0) {
                    if (n.mOutputs.size() == 0 && this.mNodes.size() > 1) {
                        throw new RSInvalidStateException("Groups cannot contain unconnected scripts");
                    }
                    validateDAGRecurse(n, ct + 1);
                }
            }
            int dagNumber = this.mNodes.get(0).dagNumber;
            for (int ct2 = 0; ct2 < this.mNodes.size(); ct2++) {
                if (this.mNodes.get(ct2).dagNumber != dagNumber) {
                    throw new RSInvalidStateException("Multiple DAGs in group not allowed.");
                }
            }
        }

        private Node findNode(Script s) {
            for (int ct = 0; ct < this.mNodes.size(); ct++) {
                if (s == this.mNodes.get(ct).mScript) {
                    return this.mNodes.get(ct);
                }
            }
            return null;
        }

        private Node findNode(Script.KernelID k) {
            for (int ct = 0; ct < this.mNodes.size(); ct++) {
                Node n = this.mNodes.get(ct);
                for (int ct2 = 0; ct2 < n.mKernels.size(); ct2++) {
                    if (k == n.mKernels.get(ct2)) {
                        return n;
                    }
                }
            }
            return null;
        }

        public Builder addKernel(Script.KernelID k) {
            if (this.mLines.size() != 0) {
                throw new RSInvalidStateException("Kernels may not be added once connections exist.");
            }
            if (findNode(k) != null) {
                return this;
            }
            this.mKernelCount++;
            Node n = findNode(k.mScript);
            if (n == null) {
                n = new Node(k.mScript);
                this.mNodes.add(n);
            }
            n.mKernels.add(k);
            return this;
        }

        public Builder addConnection(Type t, Script.KernelID from, Script.FieldID to) {
            Node nf = findNode(from);
            if (nf == null) {
                throw new RSInvalidStateException("From script not found.");
            }
            Node nt = findNode(to.mScript);
            if (nt == null) {
                throw new RSInvalidStateException("To script not found.");
            }
            ConnectLine cl = new ConnectLine(t, from, to);
            this.mLines.add(new ConnectLine(t, from, to));
            nf.mOutputs.add(cl);
            nt.mInputs.add(cl);
            validateCycle(nf, nf);
            return this;
        }

        public Builder addConnection(Type t, Script.KernelID from, Script.KernelID to) {
            Node nf = findNode(from);
            if (nf == null) {
                throw new RSInvalidStateException("From script not found.");
            }
            Node nt = findNode(to);
            if (nt == null) {
                throw new RSInvalidStateException("To script not found.");
            }
            ConnectLine cl = new ConnectLine(t, from, to);
            this.mLines.add(new ConnectLine(t, from, to));
            nf.mOutputs.add(cl);
            nt.mInputs.add(cl);
            validateCycle(nf, nf);
            return this;
        }

        public ScriptGroup create() {
            if (this.mNodes.size() == 0) {
                throw new RSInvalidStateException("Empty script groups are not allowed");
            }
            for (int ct = 0; ct < this.mNodes.size(); ct++) {
                this.mNodes.get(ct).dagNumber = 0;
            }
            validateDAG();
            ArrayList<C2371IO> inputs = new ArrayList<>();
            ArrayList<C2371IO> outputs = new ArrayList<>();
            long[] kernels = new long[this.mKernelCount];
            int idx = 0;
            for (int ct2 = 0; ct2 < this.mNodes.size(); ct2++) {
                Node n = this.mNodes.get(ct2);
                int ct22 = 0;
                while (ct22 < n.mKernels.size()) {
                    Script.KernelID kid = n.mKernels.get(ct22);
                    int idx2 = idx + 1;
                    kernels[idx] = kid.getID(this.mRS);
                    boolean hasInput = false;
                    boolean hasOutput = false;
                    for (int ct3 = 0; ct3 < n.mInputs.size(); ct3++) {
                        if (n.mInputs.get(ct3).mToK == kid) {
                            hasInput = true;
                        }
                    }
                    for (int ct32 = 0; ct32 < n.mOutputs.size(); ct32++) {
                        if (n.mOutputs.get(ct32).mFrom == kid) {
                            hasOutput = true;
                        }
                    }
                    if (!hasInput) {
                        inputs.add(new C2371IO(kid));
                    }
                    if (!hasOutput) {
                        outputs.add(new C2371IO(kid));
                    }
                    ct22++;
                    idx = idx2;
                }
            }
            if (idx != this.mKernelCount) {
                throw new RSRuntimeException("Count mismatch, should not happen.");
            }
            long[] src = new long[this.mLines.size()];
            long[] dstk = new long[this.mLines.size()];
            long[] dstf = new long[this.mLines.size()];
            long[] types = new long[this.mLines.size()];
            for (int ct4 = 0; ct4 < this.mLines.size(); ct4++) {
                ConnectLine cl = this.mLines.get(ct4);
                src[ct4] = cl.mFrom.getID(this.mRS);
                if (cl.mToK != null) {
                    dstk[ct4] = cl.mToK.getID(this.mRS);
                }
                if (cl.mToF != null) {
                    dstf[ct4] = cl.mToF.getID(this.mRS);
                }
                types[ct4] = cl.mAllocationType.getID(this.mRS);
            }
            long id = this.mRS.nScriptGroupCreate(kernels, src, dstk, dstf, types);
            if (id == 0) {
                throw new RSRuntimeException("Object creation error, should not happen.");
            }
            ScriptGroup sg = new ScriptGroup(id, this.mRS);
            sg.mOutputs = new C2371IO[outputs.size()];
            for (int ct5 = 0; ct5 < outputs.size(); ct5++) {
                sg.mOutputs[ct5] = outputs.get(ct5);
            }
            int ct6 = inputs.size();
            sg.mInputs = new C2371IO[ct6];
            for (int ct7 = 0; ct7 < inputs.size(); ct7++) {
                sg.mInputs[ct7] = inputs.get(ct7);
            }
            return sg;
        }
    }

    /* loaded from: classes3.dex */
    public static final class Binding {
        private final Script.FieldID mField;
        private final Object mValue;

        public Binding(Script.FieldID field, Object value) {
            this.mField = field;
            this.mValue = value;
        }

        Script.FieldID getField() {
            return this.mField;
        }

        Object getValue() {
            return this.mValue;
        }
    }

    /* loaded from: classes3.dex */
    public static final class Builder2 {
        private static final String TAG = "ScriptGroup.Builder2";
        List<Closure> mClosures = new ArrayList();
        List<Input> mInputs = new ArrayList();
        RenderScript mRS;

        public Builder2(RenderScript rs) {
            this.mRS = rs;
        }

        private Closure addKernelInternal(Script.KernelID k, Type returnType, Object[] args, Map<Script.FieldID, Object> globalBindings) {
            Closure c = new Closure(this.mRS, k, returnType, args, globalBindings);
            this.mClosures.add(c);
            return c;
        }

        private Closure addInvokeInternal(Script.InvokeID invoke, Object[] args, Map<Script.FieldID, Object> globalBindings) {
            Closure c = new Closure(this.mRS, invoke, args, globalBindings);
            this.mClosures.add(c);
            return c;
        }

        public Input addInput() {
            Input unbound = new Input();
            this.mInputs.add(unbound);
            return unbound;
        }

        public Closure addKernel(Script.KernelID k, Type returnType, Object... argsAndBindings) {
            ArrayList<Object> args = new ArrayList<>();
            Map<Script.FieldID, Object> bindingMap = new HashMap<>();
            if (!seperateArgsAndBindings(argsAndBindings, args, bindingMap)) {
                return null;
            }
            return addKernelInternal(k, returnType, args.toArray(), bindingMap);
        }

        public Closure addInvoke(Script.InvokeID invoke, Object... argsAndBindings) {
            ArrayList<Object> args = new ArrayList<>();
            Map<Script.FieldID, Object> bindingMap = new HashMap<>();
            if (!seperateArgsAndBindings(argsAndBindings, args, bindingMap)) {
                return null;
            }
            return addInvokeInternal(invoke, args.toArray(), bindingMap);
        }

        public ScriptGroup create(String name, Future... outputs) {
            if (name == null || name.isEmpty() || name.length() > 100 || !name.equals(name.replaceAll("[^a-zA-Z0-9-]", Session.SESSION_SEPARATION_CHAR_CHILD))) {
                throw new RSIllegalArgumentException("invalid script group name");
            }
            ScriptGroup ret = new ScriptGroup(this.mRS, name, this.mClosures, this.mInputs, outputs);
            this.mClosures = new ArrayList();
            this.mInputs = new ArrayList();
            return ret;
        }

        private boolean seperateArgsAndBindings(Object[] argsAndBindings, ArrayList<Object> args, Map<Script.FieldID, Object> bindingMap) {
            int i = 0;
            while (i < argsAndBindings.length && !(argsAndBindings[i] instanceof Binding)) {
                args.add(argsAndBindings[i]);
                i++;
            }
            while (i < argsAndBindings.length) {
                if (!(argsAndBindings[i] instanceof Binding)) {
                    return false;
                }
                Binding b = (Binding) argsAndBindings[i];
                bindingMap.put(b.getField(), b.getValue());
                i++;
            }
            return true;
        }
    }

    @Override // android.renderscript.BaseObj
    public void destroy() {
        super.destroy();
        List<Closure> list = this.mClosures;
        if (list != null) {
            for (Closure c : list) {
                c.destroy();
            }
        }
    }
}
