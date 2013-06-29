/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.demo.notepad3;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.Query;
import com.kinvey.java.core.KinveyClientCallback;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NoteEdit extends Activity {

	private static final String TAG = "NoteEdit";
    private EditText mTitleText;
    private EditText mBodyText;
    private Long mRowId;
    private NotesDbAdapter mDbHelper;
    private static StringBuffer strBuff = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.note_edit);
        setTitle(R.string.edit_note);

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);

        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
									: null;
		}

		populateFields();

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }

        });
    }

    private void populateFields() {
    	strBuff.setLength(0);
    	
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
//            mTitleText.setText(note.getString(
//                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
//            mBodyText.setText(note.getString(
//                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
            strBuff.append(mRowId+1); // add 1 to sync index number
            Query myQuery = Globals.kinveyClient.query();
            myQuery.equals("_id",strBuff.toString());
            AsyncAppData<NotesEntity> myAppData = Globals.kinveyClient.appData("Notes", NotesEntity.class);

            myAppData.get(myQuery,new KinveyListCallback<NotesEntity>() {
                public void onFailure(Throwable t) { 
               	 Log.e(TAG, "could not query data");
               }
                public void onSuccess(NotesEntity[] entities) {
               	Log.d(TAG, "Loaded "+entities.length+" data");
           	  	mTitleText.setText(entities[0].getTitle());
           	  	mBodyText.setText(entities[0].getBody());
                }
            });
     
        }
    }

    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    private void saveState() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();

        if (mRowId == null) {
            long id = mDbHelper.createNote(title, body);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateNote(mRowId, title, body);
        }
    }

}
