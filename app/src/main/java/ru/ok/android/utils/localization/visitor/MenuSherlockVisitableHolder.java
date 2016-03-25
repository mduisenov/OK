package ru.ok.android.utils.localization.visitor;

import android.content.Context;
import android.view.Menu;

public final class MenuSherlockVisitableHolder extends BaseVisitableHolder<Menu> {
    private final Context context;

    public MenuSherlockVisitableHolder(Context context, Menu view) {
        super(view);
        this.context = context;
    }

    public void visit(ViewVisitor visitor, int resourceId) {
        visitor.visitMenu(this.context, (Menu) getView(), resourceId);
    }
}
