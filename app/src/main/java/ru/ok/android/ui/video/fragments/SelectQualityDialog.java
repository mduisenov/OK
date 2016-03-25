package ru.ok.android.ui.video.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import java.util.ArrayList;
import ru.ok.android.ui.video.player.Quality;
import ru.ok.android.utils.localization.LocalizationManager;

public class SelectQualityDialog extends DialogFragment {

    public interface Listener {
        void onQualitySelected(Quality quality, int i);
    }

    /* renamed from: ru.ok.android.ui.video.fragments.SelectQualityDialog.1 */
    class C13621 implements OnClickListener {
        final /* synthetic */ ArrayList val$qualities;

        C13621(ArrayList arrayList) {
            this.val$qualities = arrayList;
        }

        public void onClick(DialogInterface dialog, int which) {
            SelectQualityDialog.this.obtainListenerFromParent().onQualitySelected((Quality) this.val$qualities.get(which), which);
            dialog.dismiss();
        }
    }

    /* renamed from: ru.ok.android.ui.video.fragments.SelectQualityDialog.2 */
    class C13632 implements Listener {
        C13632() {
        }

        public void onQualitySelected(Quality quality, int index) {
        }
    }

    public static void show(Fragment parent, ArrayList<Quality> qualities, Quality selectedQuality) {
        Bundle args = new Bundle();
        args.putSerializable("video qualitys", qualities);
        if (selectedQuality != null) {
            args.putSerializable("selected quality", selectedQuality);
        }
        SelectQualityDialog dialog = new SelectQualityDialog();
        dialog.setArguments(args);
        dialog.show(parent.getChildFragmentManager(), "SelectQualityDialog");
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ArrayList<Quality> qualities = getListQuality();
        int selectedIndex = qualities.indexOf(getSelectedQualityArg());
        Context context = getActivity();
        String[] options = new String[qualities.size()];
        for (int i = 0; i < qualities.size(); i++) {
            options[i] = LocalizationManager.getString(context, ((Quality) qualities.get(i)).resId);
        }
        return new Builder(context).setTitle(LocalizationManager.getString(context, 2131166505)).setSingleChoiceItems(options, selectedIndex, new C13621(qualities)).setNegativeButton(2131165476, null).create();
    }

    private ArrayList<Quality> getListQuality() {
        return (ArrayList) getArguments().getSerializable("video qualitys");
    }

    private Quality getSelectedQualityArg() {
        return (Quality) getArguments().getSerializable("selected quality");
    }

    private Listener obtainListenerFromParent() {
        Fragment fragment = getParentFragment();
        if (fragment instanceof Listener) {
            return (Listener) fragment;
        }
        FragmentActivity activity = getActivity();
        if (activity instanceof Listener) {
            return (Listener) activity;
        }
        Log.e("SelectQualityDialog", "Parent fragment or activity should implement " + Listener.class);
        return new C13632();
    }
}
