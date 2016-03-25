package ru.ok.android.model.pagination;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import java.util.List;

public interface Page<T> extends Parcelable {

    public static class Id {
        private final String id;

        public Id(@NonNull String id) {
            this.id = id;
        }

        @NonNull
        public String getId() {
            return this.id;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            return this.id.equals(((Id) o).id);
        }

        public int hashCode() {
            return this.id.hashCode();
        }
    }

    boolean contains(@NonNull T t);

    @NonNull
    PageAnchor getAnchor();

    int getCount();

    int getElementOffset(@NonNull T t);

    @NonNull
    List<T> getElements();

    @NonNull
    Id getId();
}
