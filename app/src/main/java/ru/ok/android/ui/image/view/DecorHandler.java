package ru.ok.android.ui.image.view;

import android.graphics.drawable.Drawable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

public class DecorHandler {
    private Drawable alphaDrawable;
    private int alphaDrawableValue;
    private List<WeakReference<DecorCallback>> callbacks;
    private int drawingCount;
    private final DecorCallback innerDecorCallback;
    protected Map<Object, DecorComponentController> mDecorComponents;
    protected boolean mDecorShown;
    protected boolean mLocked;

    public interface DecorComponentController {
        void setComponentVisibility(Object obj, boolean z, boolean z2, DecorCallback decorCallback);
    }

    public interface DecorCallback {
        void visibilityChanged();
    }

    /* renamed from: ru.ok.android.ui.image.view.DecorHandler.1 */
    class C10021 implements DecorCallback {
        C10021() {
        }

        public void visibilityChanged() {
            if (DecorHandler.this.drawingCount > 0) {
                DecorHandler.this.drawingCount = DecorHandler.this.drawingCount - 1;
                if (DecorHandler.this.drawingCount == 0) {
                    DecorHandler.this.notifyDecorCallbacks();
                }
            }
        }
    }

    public DecorHandler() {
        this.mDecorShown = true;
        this.mDecorComponents = new WeakHashMap();
        this.callbacks = new ArrayList();
        this.drawingCount = 0;
        this.innerDecorCallback = new C10021();
    }

    public final void registerDecorComponent(Object component, DecorComponentController controller) {
        this.mDecorComponents.put(component, controller);
        this.drawingCount++;
        controller.setComponentVisibility(component, this.mDecorShown, false, this.innerDecorCallback);
    }

    public final void unRegisterDecorComponent(Object component) {
        this.mDecorComponents.remove(component);
    }

    public boolean isDecorShown() {
        return this.mDecorShown;
    }

    public final void setDecorVisibility(boolean visible, boolean animate) {
        if (!this.mLocked && this.mDecorShown != visible) {
            this.mDecorShown = visible;
            for (Entry<Object, DecorComponentController> entry : this.mDecorComponents.entrySet()) {
                this.drawingCount++;
                ((DecorComponentController) entry.getValue()).setComponentVisibility(entry.getKey(), visible, animate, this.innerDecorCallback);
            }
        }
    }

    public final void setVisibilityChangeLocked(boolean locked) {
        this.mLocked = locked;
    }

    public final void registerBackgroundDrawable(Drawable drawable, int initalAlpha) {
        this.alphaDrawable = drawable;
        this.alphaDrawableValue = initalAlpha;
        this.alphaDrawable.setAlpha(this.alphaDrawableValue);
    }

    public void setBackgroundDrawableAlpha(int alpha) {
        if (this.alphaDrawable != null) {
            this.alphaDrawable.setAlpha(alpha);
            this.alphaDrawableValue = alpha;
        }
    }

    public int getBackgroundDrawableAlpha() {
        return this.alphaDrawableValue;
    }

    private void notifyDecorCallbacks() {
        Iterator<WeakReference<DecorCallback>> it = this.callbacks.iterator();
        while (it.hasNext()) {
            DecorCallback callback = (DecorCallback) ((WeakReference) it.next()).get();
            if (callback == null) {
                it.remove();
            } else {
                callback.visibilityChanged();
            }
        }
    }

    public void addDecorCallback(DecorCallback callback) {
        this.callbacks.add(new WeakReference(callback));
    }
}
