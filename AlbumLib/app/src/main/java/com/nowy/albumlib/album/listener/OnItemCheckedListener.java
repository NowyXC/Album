package com.nowy.albumlib.album.listener;

import android.widget.CompoundButton;

public interface OnItemCheckedListener {

    /**
     * When the selected state of Item changes.
     *
     * @param compoundButton {@link CompoundButton}.
     * @param position       item position.
     * @param isChecked      checked state.
     */
    void onCheckedChanged(CompoundButton compoundButton, int position, boolean isChecked);

}