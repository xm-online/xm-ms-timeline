package com.icthh.xm.ms.timeline.domain.ext;

import java.util.Objects;

/**
 * The {@link IdOrKey} class.
 */
public final class IdOrKey {

    private final String idOrKey;
    private Long id;
    private String key;
    private Boolean isKey;

    private IdOrKey(String idOrKey) {
        this.idOrKey = Objects.requireNonNull(idOrKey, "idOrKey can't be null");
    }

    public static IdOrKey of(String idOrKey) {
        return new IdOrKey(idOrKey);
    }

    private void lazyInit() {
        // check is initialized ?
        if (isKey == null) {
            try {
                id = Long.parseLong(idOrKey);
                isKey = false;
            } catch (NumberFormatException e) {
                key = idOrKey;
                isKey = true;
            }
        }
    }

    public boolean isKey() {
        lazyInit();

        return isKey;
    }

    public boolean isId() {
        lazyInit();

        return !isKey();
    }

    public Long getId() {
        lazyInit();

        return id;
    }

    public String getKey() {
        lazyInit();

        return key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return isId() ? String.valueOf(getId()) : String.valueOf(getKey());
    }

}
