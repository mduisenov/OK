package ru.ok.android.utils.localization.visitor;

import android.content.Context;
import android.view.ContextMenu;

public final class MenuContextVisitableHolder extends BaseVisitableHolder<ContextMenu> {
    private final Context context;

    public MenuContextVisitableHolder(Context context, ContextMenu menu) {
        super(menu);
        this.context = context;
    }

    public void visit(ViewVisitor visitor, int resourceId) {
        visitor.visitMenu(this.context, (ContextMenu) getView(), resourceId);
    }
}
