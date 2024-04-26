package com.themoviedb.app;

import androidx.recyclerview.widget.DiffUtil;
import java.util.List;

class MainDiffCallback<T> extends DiffUtil.Callback {
    private final List<T> oldList;
    private final List<T> newList;

    MainDiffCallback(List<T> oldList, List<T> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // Implement logic to check if items are the same
        // Example: return oldList.get(oldItemPosition).getId() ==
        // newList.get(newItemPosition).getId();
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // Implement logic to check if item contents are the same
        // Example: return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        return false;
    }
}
