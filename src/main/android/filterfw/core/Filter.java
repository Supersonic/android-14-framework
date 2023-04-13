package android.filterfw.core;

import android.filterfw.format.ObjectFormat;
import android.filterfw.p003io.GraphIOException;
import android.filterfw.p003io.TextGraphReader;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.util.Log;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/* loaded from: classes.dex */
public abstract class Filter {
    static final int STATUS_ERROR = 6;
    static final int STATUS_FINISHED = 5;
    static final int STATUS_PREINIT = 0;
    static final int STATUS_PREPARED = 2;
    static final int STATUS_PROCESSING = 3;
    static final int STATUS_RELEASED = 7;
    static final int STATUS_SLEEPING = 4;
    static final int STATUS_UNPREPARED = 1;
    private static final String TAG = "Filter";
    private long mCurrentTimestamp;
    private HashMap<String, InputPort> mInputPorts;
    private String mName;
    private HashMap<String, OutputPort> mOutputPorts;
    private int mSleepDelay;
    private int mStatus;
    private int mInputCount = -1;
    private int mOutputCount = -1;
    private boolean mIsOpen = false;
    private HashSet<Frame> mFramesToRelease = new HashSet<>();
    private HashMap<String, Frame> mFramesToSet = new HashMap<>();
    private boolean mLogVerbose = Log.isLoggable(TAG, 2);

    public abstract void process(FilterContext filterContext);

    public abstract void setupPorts();

    public Filter(String name) {
        this.mStatus = 0;
        this.mName = name;
        this.mStatus = 0;
    }

    public static final boolean isAvailable(String filterName) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Class filterClass = contextClassLoader.loadClass(filterName);
            if (!Filter.class.isAssignableFrom(filterClass)) {
                return false;
            }
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public final void initWithValueMap(KeyValueMap valueMap) {
        initFinalPorts(valueMap);
        initRemainingPorts(valueMap);
        this.mStatus = 1;
    }

    public final void initWithAssignmentString(String assignments) {
        try {
            KeyValueMap valueMap = new TextGraphReader().readKeyValueAssignments(assignments);
            initWithValueMap(valueMap);
        } catch (GraphIOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public final void initWithAssignmentList(Object... keyValues) {
        KeyValueMap valueMap = new KeyValueMap();
        valueMap.setKeyValues(keyValues);
        initWithValueMap(valueMap);
    }

    public final void init() throws ProtocolException {
        KeyValueMap valueMap = new KeyValueMap();
        initWithValueMap(valueMap);
    }

    public String getFilterClassName() {
        return getClass().getSimpleName();
    }

    public final String getName() {
        return this.mName;
    }

    public boolean isOpen() {
        return this.mIsOpen;
    }

    public void setInputFrame(String inputName, Frame frame) {
        FilterPort port = getInputPort(inputName);
        if (!port.isOpen()) {
            port.open();
        }
        port.setFrame(frame);
    }

    public final void setInputValue(String inputName, Object value) {
        setInputFrame(inputName, wrapInputValue(inputName, value));
    }

    protected void prepare(FilterContext context) {
    }

    protected void parametersUpdated(Set<String> updated) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void delayNextProcess(int millisecs) {
        this.mSleepDelay = millisecs;
        this.mStatus = 4;
    }

    public FrameFormat getOutputFormat(String portName, FrameFormat inputFormat) {
        return null;
    }

    public final FrameFormat getInputFormat(String portName) {
        InputPort inputPort = getInputPort(portName);
        return inputPort.getSourceFormat();
    }

    public void open(FilterContext context) {
    }

    public final int getSleepDelay() {
        return 250;
    }

    public void close(FilterContext context) {
    }

    public void tearDown(FilterContext context) {
    }

    public final int getNumberOfConnectedInputs() {
        int c = 0;
        for (InputPort inputPort : this.mInputPorts.values()) {
            if (inputPort.isConnected()) {
                c++;
            }
        }
        return c;
    }

    public final int getNumberOfConnectedOutputs() {
        int c = 0;
        for (OutputPort outputPort : this.mOutputPorts.values()) {
            if (outputPort.isConnected()) {
                c++;
            }
        }
        return c;
    }

    public final int getNumberOfInputs() {
        if (this.mOutputPorts == null) {
            return 0;
        }
        return this.mInputPorts.size();
    }

    public final int getNumberOfOutputs() {
        if (this.mInputPorts == null) {
            return 0;
        }
        return this.mOutputPorts.size();
    }

    public final InputPort getInputPort(String portName) {
        HashMap<String, InputPort> hashMap = this.mInputPorts;
        if (hashMap == null) {
            throw new NullPointerException("Attempting to access input port '" + portName + "' of " + this + " before Filter has been initialized!");
        }
        InputPort result = hashMap.get(portName);
        if (result == null) {
            throw new IllegalArgumentException("Unknown input port '" + portName + "' on filter " + this + "!");
        }
        return result;
    }

    public final OutputPort getOutputPort(String portName) {
        if (this.mInputPorts == null) {
            throw new NullPointerException("Attempting to access output port '" + portName + "' of " + this + " before Filter has been initialized!");
        }
        OutputPort result = this.mOutputPorts.get(portName);
        if (result == null) {
            throw new IllegalArgumentException("Unknown output port '" + portName + "' on filter " + this + "!");
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void pushOutput(String name, Frame frame) {
        if (frame.getTimestamp() == -2) {
            if (this.mLogVerbose) {
                Log.m106v(TAG, "Default-setting output Frame timestamp on port " + name + " to " + this.mCurrentTimestamp);
            }
            frame.setTimestamp(this.mCurrentTimestamp);
        }
        getOutputPort(name).pushFrame(frame);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Frame pullInput(String name) {
        Frame result = getInputPort(name).pullFrame();
        if (this.mCurrentTimestamp == -1) {
            this.mCurrentTimestamp = result.getTimestamp();
            if (this.mLogVerbose) {
                Log.m106v(TAG, "Default-setting current timestamp from input port " + name + " to " + this.mCurrentTimestamp);
            }
        }
        this.mFramesToRelease.add(result);
        return result;
    }

    public void fieldPortValueUpdated(String name, FilterContext context) {
    }

    protected void transferInputPortFrame(String name, FilterContext context) {
        getInputPort(name).transfer(context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initProgramInputs(Program program, FilterContext context) {
        if (program != null) {
            for (InputPort inputPort : this.mInputPorts.values()) {
                if (inputPort.getTarget() == program) {
                    inputPort.transfer(context);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void addInputPort(String name) {
        addMaskedInputPort(name, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void addMaskedInputPort(String name, FrameFormat formatMask) {
        InputPort port = new StreamPort(this, name);
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Filter " + this + " adding " + port);
        }
        this.mInputPorts.put(name, port);
        port.setPortFormat(formatMask);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void addOutputPort(String name, FrameFormat format) {
        OutputPort port = new OutputPort(this, name);
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Filter " + this + " adding " + port);
        }
        port.setPortFormat(format);
        this.mOutputPorts.put(name, port);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void addOutputBasedOnInput(String outputName, String inputName) {
        OutputPort port = new OutputPort(this, outputName);
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Filter " + this + " adding " + port);
        }
        port.setBasePort(getInputPort(inputName));
        this.mOutputPorts.put(outputName, port);
    }

    protected void addFieldPort(String name, Field field, boolean hasDefault, boolean isFinal) {
        InputPort fieldPort;
        field.setAccessible(true);
        if (isFinal) {
            fieldPort = new FinalPort(this, name, field, hasDefault);
        } else {
            fieldPort = new FieldPort(this, name, field, hasDefault);
        }
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Filter " + this + " adding " + fieldPort);
        }
        MutableFrameFormat format = ObjectFormat.fromClass(field.getType(), 1);
        fieldPort.setPortFormat(format);
        this.mInputPorts.put(name, fieldPort);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void addProgramPort(String name, String varName, Field field, Class varType, boolean hasDefault) {
        field.setAccessible(true);
        InputPort programPort = new ProgramPort(this, name, varName, field, hasDefault);
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Filter " + this + " adding " + programPort);
        }
        MutableFrameFormat format = ObjectFormat.fromClass(varType, 1);
        programPort.setPortFormat(format);
        this.mInputPorts.put(name, programPort);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void closeOutputPort(String name) {
        getOutputPort(name).close();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setWaitsOnInputPort(String portName, boolean waits) {
        getInputPort(portName).setBlocking(waits);
    }

    protected void setWaitsOnOutputPort(String portName, boolean waits) {
        getOutputPort(portName).setBlocking(waits);
    }

    public String toString() {
        return "'" + getName() + "' (" + getFilterClassName() + NavigationBarInflaterView.KEY_CODE_END;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Collection<InputPort> getInputPorts() {
        return this.mInputPorts.values();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Collection<OutputPort> getOutputPorts() {
        return this.mOutputPorts.values();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized int getStatus() {
        return this.mStatus;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized void unsetStatus(int flag) {
        this.mStatus &= ~flag;
    }

    final synchronized void performOpen(FilterContext context) {
        if (!this.mIsOpen) {
            if (this.mStatus == 1) {
                if (this.mLogVerbose) {
                    Log.m106v(TAG, "Preparing " + this);
                }
                prepare(context);
                this.mStatus = 2;
            }
            if (this.mStatus == 2) {
                if (this.mLogVerbose) {
                    Log.m106v(TAG, "Opening " + this);
                }
                open(context);
                this.mStatus = 3;
            }
            if (this.mStatus != 3) {
                throw new RuntimeException("Filter " + this + " was brought into invalid state during opening (state: " + this.mStatus + ")!");
            }
            this.mIsOpen = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized void performProcess(FilterContext context) {
        if (this.mStatus == 7) {
            throw new RuntimeException("Filter " + this + " is already torn down!");
        }
        transferInputFrames(context);
        if (this.mStatus < 3) {
            performOpen(context);
        }
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Processing " + this);
        }
        this.mCurrentTimestamp = -1L;
        process(context);
        releasePulledFrames(context);
        if (filterMustClose()) {
            performClose(context);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized void performClose(FilterContext context) {
        if (this.mIsOpen) {
            if (this.mLogVerbose) {
                Log.m106v(TAG, "Closing " + this);
            }
            this.mIsOpen = false;
            this.mStatus = 2;
            close(context);
            closePorts();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized void performTearDown(FilterContext context) {
        performClose(context);
        if (this.mStatus != 7) {
            tearDown(context);
            this.mStatus = 7;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized boolean canProcess() {
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Checking if can process: " + this + " (" + this.mStatus + ").");
        }
        boolean z = false;
        if (this.mStatus <= 3) {
            if (inputConditionsMet()) {
                if (outputConditionsMet()) {
                    z = true;
                }
            }
            return z;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void openOutputs() {
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Opening all output ports on " + this + "!");
        }
        for (OutputPort outputPort : this.mOutputPorts.values()) {
            if (!outputPort.isOpen()) {
                outputPort.open();
            }
        }
    }

    final void clearInputs() {
        for (InputPort inputPort : this.mInputPorts.values()) {
            inputPort.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void clearOutputs() {
        for (OutputPort outputPort : this.mOutputPorts.values()) {
            outputPort.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void notifyFieldPortValueUpdated(String name, FilterContext context) {
        int i = this.mStatus;
        if (i == 3 || i == 2) {
            fieldPortValueUpdated(name, context);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized void pushInputFrame(String inputName, Frame frame) {
        FilterPort port = getInputPort(inputName);
        if (!port.isOpen()) {
            port.open();
        }
        port.pushFrame(frame);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized void pushInputValue(String inputName, Object value) {
        pushInputFrame(inputName, wrapInputValue(inputName, value));
    }

    private final void initFinalPorts(KeyValueMap values) {
        this.mInputPorts = new HashMap<>();
        this.mOutputPorts = new HashMap<>();
        addAndSetFinalPorts(values);
    }

    private final void initRemainingPorts(KeyValueMap values) {
        addAnnotatedPorts();
        setupPorts();
        setInitialInputValues(values);
    }

    private final void addAndSetFinalPorts(KeyValueMap values) {
        Field[] declaredFields;
        Class filterClass = getClass();
        for (Field field : filterClass.getDeclaredFields()) {
            Annotation annotation = field.getAnnotation(GenerateFinalPort.class);
            if (annotation != null) {
                GenerateFinalPort generator = (GenerateFinalPort) annotation;
                String name = generator.name().isEmpty() ? field.getName() : generator.name();
                boolean hasDefault = generator.hasDefault();
                addFieldPort(name, field, hasDefault, true);
                if (values.containsKey(name)) {
                    setImmediateInputValue(name, values.get(name));
                    values.remove(name);
                } else if (!generator.hasDefault()) {
                    throw new RuntimeException("No value specified for final input port '" + name + "' of filter " + this + "!");
                }
            }
        }
    }

    private final void addAnnotatedPorts() {
        Field[] declaredFields;
        GenerateProgramPort[] value;
        Class filterClass = getClass();
        for (Field field : filterClass.getDeclaredFields()) {
            Annotation annotation = field.getAnnotation(GenerateFieldPort.class);
            if (annotation != null) {
                GenerateFieldPort generator = (GenerateFieldPort) annotation;
                addFieldGenerator(generator, field);
            } else {
                Annotation annotation2 = field.getAnnotation(GenerateProgramPort.class);
                if (annotation2 != null) {
                    GenerateProgramPort generator2 = (GenerateProgramPort) annotation2;
                    addProgramGenerator(generator2, field);
                } else {
                    Annotation annotation3 = field.getAnnotation(GenerateProgramPorts.class);
                    if (annotation3 != null) {
                        GenerateProgramPorts generators = (GenerateProgramPorts) annotation3;
                        for (GenerateProgramPort generator3 : generators.value()) {
                            addProgramGenerator(generator3, field);
                        }
                    }
                }
            }
        }
    }

    private final void addFieldGenerator(GenerateFieldPort generator, Field field) {
        String name = generator.name().isEmpty() ? field.getName() : generator.name();
        boolean hasDefault = generator.hasDefault();
        addFieldPort(name, field, hasDefault, false);
    }

    private final void addProgramGenerator(GenerateProgramPort generator, Field field) {
        String name = generator.name();
        String varName = generator.variableName().isEmpty() ? name : generator.variableName();
        Class varType = generator.type();
        boolean hasDefault = generator.hasDefault();
        addProgramPort(name, varName, field, varType, hasDefault);
    }

    private final void setInitialInputValues(KeyValueMap values) {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            setInputValue(entry.getKey(), entry.getValue());
        }
    }

    private final void setImmediateInputValue(String name, Object value) {
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Setting immediate value " + value + " for port " + name + "!");
        }
        FilterPort port = getInputPort(name);
        port.open();
        port.setFrame(SimpleFrame.wrapObject(value, null));
    }

    private final void transferInputFrames(FilterContext context) {
        for (InputPort inputPort : this.mInputPorts.values()) {
            inputPort.transfer(context);
        }
    }

    private final Frame wrapInputValue(String inputName, Object value) {
        Frame simpleFrame;
        boolean shouldSerialize = true;
        MutableFrameFormat inputFormat = ObjectFormat.fromObject(value, 1);
        if (value == null) {
            FrameFormat portFormat = getInputPort(inputName).getPortFormat();
            Class portClass = portFormat == null ? null : portFormat.getObjectClass();
            inputFormat.setObjectClass(portClass);
        }
        shouldSerialize = ((value instanceof Number) || (value instanceof Boolean) || (value instanceof String) || !(value instanceof Serializable)) ? false : false;
        if (shouldSerialize) {
            simpleFrame = new SerializedFrame(inputFormat, null);
        } else {
            simpleFrame = new SimpleFrame(inputFormat, null);
        }
        Frame frame = simpleFrame;
        frame.setObjectValue(value);
        return frame;
    }

    private final void releasePulledFrames(FilterContext context) {
        Iterator<Frame> it = this.mFramesToRelease.iterator();
        while (it.hasNext()) {
            Frame frame = it.next();
            context.getFrameManager().releaseFrame(frame);
        }
        this.mFramesToRelease.clear();
    }

    private final boolean inputConditionsMet() {
        for (FilterPort port : this.mInputPorts.values()) {
            if (!port.isReady()) {
                if (this.mLogVerbose) {
                    Log.m106v(TAG, "Input condition not met: " + port + "!");
                    return false;
                }
                return false;
            }
        }
        return true;
    }

    private final boolean outputConditionsMet() {
        for (FilterPort port : this.mOutputPorts.values()) {
            if (!port.isReady()) {
                if (this.mLogVerbose) {
                    Log.m106v(TAG, "Output condition not met: " + port + "!");
                    return false;
                }
                return false;
            }
        }
        return true;
    }

    private final void closePorts() {
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Closing all ports on " + this + "!");
        }
        for (InputPort inputPort : this.mInputPorts.values()) {
            inputPort.close();
        }
        for (OutputPort outputPort : this.mOutputPorts.values()) {
            outputPort.close();
        }
    }

    private final boolean filterMustClose() {
        for (InputPort inputPort : this.mInputPorts.values()) {
            if (inputPort.filterMustClose()) {
                if (this.mLogVerbose) {
                    Log.m106v(TAG, "Filter " + this + " must close due to port " + inputPort);
                }
                return true;
            }
        }
        for (OutputPort outputPort : this.mOutputPorts.values()) {
            if (outputPort.filterMustClose()) {
                if (this.mLogVerbose) {
                    Log.m106v(TAG, "Filter " + this + " must close due to port " + outputPort);
                }
                return true;
            }
        }
        return false;
    }
}
