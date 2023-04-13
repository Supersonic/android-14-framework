package android.app;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.display.DisplayManager;
import android.p008os.Handler;
import android.p008os.Looper;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import java.util.Objects;
/* loaded from: classes.dex */
public class Presentation extends Dialog {
    private static final String TAG = "Presentation";
    private final Display mDisplay;
    private final DisplayManager.DisplayListener mDisplayListener;
    private final DisplayManager mDisplayManager;
    private final Handler mHandler;

    public Presentation(Context outerContext, Display display) {
        this(outerContext, display, 0);
    }

    public Presentation(Context outerContext, Display display, int theme) {
        this(outerContext, display, theme, -1);
    }

    public Presentation(Context outerContext, Display display, int theme, int type) {
        super(createPresentationContext(outerContext, display, theme, type), theme, false);
        this.mHandler = new Handler((Looper) Objects.requireNonNull(Looper.myLooper(), "Presentation must be constructed on a looper thread."));
        this.mDisplayListener = new DisplayManager.DisplayListener() { // from class: android.app.Presentation.1
            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayAdded(int displayId) {
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayRemoved(int displayId) {
                if (displayId == Presentation.this.mDisplay.getDisplayId()) {
                    Presentation.this.handleDisplayRemoved();
                }
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayChanged(int displayId) {
                if (displayId == Presentation.this.mDisplay.getDisplayId()) {
                    Presentation.this.handleDisplayChanged();
                }
            }
        };
        this.mDisplay = display;
        this.mDisplayManager = (DisplayManager) getContext().getSystemService(DisplayManager.class);
        Window w = getWindow();
        WindowManager.LayoutParams attr = w.getAttributes();
        w.setAttributes(attr);
        w.setGravity(119);
        w.setType(getWindowType(type, display));
        setCanceledOnTouchOutside(false);
    }

    private static int getWindowType(int type, Display display) {
        if (type != -1) {
            return type;
        }
        return (display.getFlags() & 4) != 0 ? 2030 : 2037;
    }

    public Display getDisplay() {
        return this.mDisplay;
    }

    public Resources getResources() {
        return getContext().getResources();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Dialog
    public void onStart() {
        super.onStart();
        this.mDisplayManager.registerDisplayListener(this.mDisplayListener, this.mHandler);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Dialog
    public void onStop() {
        this.mDisplayManager.unregisterDisplayListener(this.mDisplayListener);
        super.onStop();
    }

    @Override // android.app.Dialog
    public void show() {
        super.show();
    }

    public void onDisplayRemoved() {
    }

    public void onDisplayChanged() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDisplayRemoved() {
        onDisplayRemoved();
        cancel();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDisplayChanged() {
        onDisplayChanged();
    }

    private static Context createPresentationContext(Context outerContext, Display display, int theme) {
        return createPresentationContext(outerContext, display, theme, -1);
    }

    private static Context createPresentationContext(Context outerContext, Display display, int theme, int type) {
        if (outerContext == null) {
            throw new IllegalArgumentException("outerContext must not be null");
        }
        if (display == null) {
            throw new IllegalArgumentException("display must not be null");
        }
        Context windowContext = outerContext.createDisplayContext(display).createWindowContext(getWindowType(type, display), null);
        if (theme == 0) {
            TypedValue outValue = new TypedValue();
            windowContext.getTheme().resolveAttribute(16843712, outValue, true);
            theme = outValue.resourceId;
        }
        return new ContextThemeWrapper(windowContext, theme);
    }
}
