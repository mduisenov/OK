package ru.ok.android.ui.fragments.messages.adapter;

import java.util.List;

public interface IChatStateProvider {
    List<Long> getServerState(String str);
}
