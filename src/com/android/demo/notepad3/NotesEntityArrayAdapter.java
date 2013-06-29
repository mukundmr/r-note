package com.android.demo.notepad3;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class NotesEntityArrayAdapter extends ArrayAdapter<NotesEntity> {

	// HashMap<Integer, NotesEntity> mIdMap = new HashMap<Integer, NotesEntity>();
	
	public NotesEntityArrayAdapter(Context context, int textViewResourceId,
			NotesEntity[] entities) {
		super(context, textViewResourceId, entities);
	}

}
