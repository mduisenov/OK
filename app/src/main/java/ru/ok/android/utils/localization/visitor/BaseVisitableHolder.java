package ru.ok.android.utils.localization.visitor;

import java.lang.ref.WeakReference;

public abstract class BaseVisitableHolder<T> implements ViewVisitable {
    private final WeakReference<T> _view;

    BaseVisitableHolder(T view) {
        this._view = new WeakReference(view);
    }

    public T getView() {
        return this._view.get();
    }
}
