package com.android.internal.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.media.MediaRouter;
import android.p008os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import com.android.internal.C4057R;
/* loaded from: classes4.dex */
public class MediaRouteControllerDialog extends AlertDialog {
    private static final int VOLUME_UPDATE_DELAY_MILLIS = 250;
    private boolean mAttachedToWindow;
    private final MediaRouterCallback mCallback;
    private View mControlView;
    private boolean mCreated;
    private Drawable mCurrentIconDrawable;
    private Drawable mMediaRouteButtonDrawable;
    private int[] mMediaRouteConnectingState;
    private int[] mMediaRouteOnState;
    private final MediaRouter.RouteInfo mRoute;
    private final MediaRouter mRouter;
    private boolean mVolumeControlEnabled;
    private LinearLayout mVolumeLayout;
    private SeekBar mVolumeSlider;
    private boolean mVolumeSliderTouched;

    public MediaRouteControllerDialog(Context context, int theme) {
        super(context, theme);
        this.mMediaRouteConnectingState = new int[]{16842912, 16842910};
        this.mMediaRouteOnState = new int[]{16843518, 16842910};
        this.mVolumeControlEnabled = true;
        MediaRouter mediaRouter = (MediaRouter) context.getSystemService(Context.MEDIA_ROUTER_SERVICE);
        this.mRouter = mediaRouter;
        this.mCallback = new MediaRouterCallback();
        this.mRoute = mediaRouter.getSelectedRoute();
    }

    public MediaRouter.RouteInfo getRoute() {
        return this.mRoute;
    }

    public View onCreateMediaControlView(Bundle savedInstanceState) {
        return null;
    }

    public View getMediaControlView() {
        return this.mControlView;
    }

    public void setVolumeControlEnabled(boolean enable) {
        if (this.mVolumeControlEnabled != enable) {
            this.mVolumeControlEnabled = enable;
            if (this.mCreated) {
                updateVolume();
            }
        }
    }

    public boolean isVolumeControlEnabled() {
        return this.mVolumeControlEnabled;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.AlertDialog, android.app.Dialog
    public void onCreate(Bundle savedInstanceState) {
        setTitle(this.mRoute.getName());
        Resources res = getContext().getResources();
        setButton(-2, res.getString(C4057R.string.media_route_controller_disconnect), new DialogInterface.OnClickListener() { // from class: com.android.internal.app.MediaRouteControllerDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int id) {
                if (MediaRouteControllerDialog.this.mRoute.isSelected()) {
                    if (MediaRouteControllerDialog.this.mRoute.isBluetooth()) {
                        MediaRouteControllerDialog.this.mRouter.getDefaultRoute().select();
                    } else {
                        MediaRouteControllerDialog.this.mRouter.getFallbackRoute().select();
                    }
                }
                MediaRouteControllerDialog.this.dismiss();
            }
        });
        View customView = getLayoutInflater().inflate(C4057R.layout.media_route_controller_dialog, (ViewGroup) null);
        setView(customView, 0, 0, 0, 0);
        super.onCreate(savedInstanceState);
        View customPanelView = getWindow().findViewById(C4057R.C4059id.customPanel);
        if (customPanelView != null) {
            customPanelView.setMinimumHeight(0);
        }
        this.mVolumeLayout = (LinearLayout) customView.findViewById(C4057R.C4059id.media_route_volume_layout);
        SeekBar seekBar = (SeekBar) customView.findViewById(C4057R.C4059id.media_route_volume_slider);
        this.mVolumeSlider = seekBar;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.internal.app.MediaRouteControllerDialog.2
            private final Runnable mStopTrackingTouch = new Runnable() { // from class: com.android.internal.app.MediaRouteControllerDialog.2.1
                @Override // java.lang.Runnable
                public void run() {
                    if (MediaRouteControllerDialog.this.mVolumeSliderTouched) {
                        MediaRouteControllerDialog.this.mVolumeSliderTouched = false;
                        MediaRouteControllerDialog.this.updateVolume();
                    }
                }
            };

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar2) {
                if (MediaRouteControllerDialog.this.mVolumeSliderTouched) {
                    MediaRouteControllerDialog.this.mVolumeSlider.removeCallbacks(this.mStopTrackingTouch);
                } else {
                    MediaRouteControllerDialog.this.mVolumeSliderTouched = true;
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar2) {
                MediaRouteControllerDialog.this.mVolumeSlider.postDelayed(this.mStopTrackingTouch, 250L);
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar2, int progress, boolean fromUser) {
                if (fromUser) {
                    MediaRouteControllerDialog.this.mRoute.requestSetVolume(progress);
                }
            }
        });
        this.mMediaRouteButtonDrawable = obtainMediaRouteButtonDrawable();
        this.mCreated = true;
        if (update()) {
            this.mControlView = onCreateMediaControlView(savedInstanceState);
            FrameLayout controlFrame = (FrameLayout) customView.findViewById(C4057R.C4059id.media_route_control_frame);
            View view = this.mControlView;
            if (view != null) {
                controlFrame.addView(view);
                controlFrame.setVisibility(0);
                return;
            }
            controlFrame.setVisibility(8);
        }
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAttachedToWindow = true;
        this.mRouter.addCallback(0, this.mCallback, 2);
        update();
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onDetachedFromWindow() {
        this.mRouter.removeCallback(this.mCallback);
        this.mAttachedToWindow = false;
        super.onDetachedFromWindow();
    }

    @Override // android.app.AlertDialog, android.app.Dialog, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 25 || keyCode == 24) {
            this.mRoute.requestUpdateVolume(keyCode == 25 ? -1 : 1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // android.app.AlertDialog, android.app.Dialog, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 25 || keyCode == 24) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean update() {
        if (!this.mRoute.isSelected() || this.mRoute.isDefault()) {
            dismiss();
            return false;
        }
        setTitle(this.mRoute.getName());
        updateVolume();
        Drawable icon = getIconDrawable();
        if (icon != this.mCurrentIconDrawable) {
            this.mCurrentIconDrawable = icon;
            if (icon instanceof AnimationDrawable) {
                AnimationDrawable animDrawable = (AnimationDrawable) icon;
                if (!this.mAttachedToWindow && !this.mRoute.isConnecting()) {
                    if (animDrawable.isRunning()) {
                        animDrawable.stop();
                    }
                    icon = animDrawable.getFrame(animDrawable.getNumberOfFrames() - 1);
                } else if (!animDrawable.isRunning()) {
                    animDrawable.start();
                }
            }
            setIcon(icon);
        }
        return true;
    }

    private Drawable obtainMediaRouteButtonDrawable() {
        Context context = getContext();
        TypedValue value = new TypedValue();
        if (!context.getTheme().resolveAttribute(16843693, value, true)) {
            return null;
        }
        int[] drawableAttrs = {C4057R.attr.externalRouteEnabledDrawable};
        TypedArray a = context.obtainStyledAttributes(value.data, drawableAttrs);
        Drawable drawable = a.getDrawable(0);
        a.recycle();
        return drawable;
    }

    private Drawable getIconDrawable() {
        Drawable drawable = this.mMediaRouteButtonDrawable;
        if (!(drawable instanceof StateListDrawable)) {
            return drawable;
        }
        if (this.mRoute.isConnecting()) {
            StateListDrawable stateListDrawable = (StateListDrawable) this.mMediaRouteButtonDrawable;
            stateListDrawable.setState(this.mMediaRouteConnectingState);
            return stateListDrawable.getCurrent();
        }
        StateListDrawable stateListDrawable2 = (StateListDrawable) this.mMediaRouteButtonDrawable;
        stateListDrawable2.setState(this.mMediaRouteOnState);
        return stateListDrawable2.getCurrent();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateVolume() {
        if (!this.mVolumeSliderTouched) {
            if (isVolumeControlAvailable()) {
                this.mVolumeLayout.setVisibility(0);
                this.mVolumeSlider.setMax(this.mRoute.getVolumeMax());
                this.mVolumeSlider.setProgress(this.mRoute.getVolume());
                return;
            }
            this.mVolumeLayout.setVisibility(8);
        }
    }

    private boolean isVolumeControlAvailable() {
        return this.mVolumeControlEnabled && this.mRoute.getVolumeHandling() == 1;
    }

    /* loaded from: classes4.dex */
    private final class MediaRouterCallback extends MediaRouter.SimpleCallback {
        private MediaRouterCallback() {
        }

        @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
        public void onRouteUnselected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
            MediaRouteControllerDialog.this.update();
        }

        @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
        public void onRouteChanged(MediaRouter router, MediaRouter.RouteInfo route) {
            MediaRouteControllerDialog.this.update();
        }

        @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
        public void onRouteVolumeChanged(MediaRouter router, MediaRouter.RouteInfo route) {
            if (route == MediaRouteControllerDialog.this.mRoute) {
                MediaRouteControllerDialog.this.updateVolume();
            }
        }

        @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
        public void onRouteGrouped(MediaRouter router, MediaRouter.RouteInfo info, MediaRouter.RouteGroup group, int index) {
            MediaRouteControllerDialog.this.update();
        }

        @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
        public void onRouteUngrouped(MediaRouter router, MediaRouter.RouteInfo info, MediaRouter.RouteGroup group) {
            MediaRouteControllerDialog.this.update();
        }
    }
}
