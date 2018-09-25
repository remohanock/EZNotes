package com.remo.material.easynotes.interfaces;

import com.remo.material.easynotes.model.NotesBuilder;

public interface ItemSelection {

    void selectedMe(NotesBuilder note);
    void deleteMe(NotesBuilder serverIndex);

}
