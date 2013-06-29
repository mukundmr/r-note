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
import com.kinvey.android.Client;
import com.kinvey.android.callback.*;
import com.kinvey.java.AppData;
import com.kinvey.java.Query;
import com.kinvey.java.User;


import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class Notepadv3 extends ListActivity {
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final String TAG="NotePad";

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;

    private NotesDbAdapter mDbHelper;

   
    private void initializeKinvey() {
    	Globals.kinveyClient = new Client.Builder(this.getApplicationContext()).build();
    	Globals.kinveyClient.user().login(new KinveyUserCallback() {
            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "Login Failure", error);
            }
            @Override
            public void onSuccess(User result) {
                Log.i(TAG,"Logged in successfully as " + result.getId());
                Globals.kinveyClient.ping(new KinveyPingCallback() {

                    public void onFailure(Throwable t) {
                        Log.e(TAG, "Kinvey Ping Failed", t);
                    }

                    public void onSuccess(Boolean b) {
                        Log.d(TAG, "Kinvey Ping Success");
                    }
                });
            }
        });
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_list);
        initializeKinvey();
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        
        fillData();
        registerForContextMenu(getListView());
    }

//    private void fillData() {
//        Cursor notesCursor = mDbHelper.fetchAllNotes();
//        startManagingCursor(notesCursor);
//
//        // Create an array to specify the fields we want to display in the list (only TITLE)
//        String[] from = new String[]{NotesDbAdapter.KEY_TITLE};
//
//        // and an array of the fields we want to bind those fields to (in this case just text1)
//        int[] to = new int[]{R.id.text1};
//
//        // Now create a simple cursor adapter and set it to display
//        SimpleCursorAdapter notes = 
//            new SimpleCursorAdapter(this, R.layout.notes_row, notesCursor, from, to);
//        setListAdapter(notes);
//    }
    
    private void fillData() {
 	 AsyncAppData<NotesEntity> myAppData = Globals.kinveyClient.appData("Notes", NotesEntity.class);

         myAppData.get(new Query(),new KinveyListCallback<NotesEntity>() {
             public void onFailure(Throwable t) { 
            	 Log.e(TAG, "could not query data");
            }
             public void onSuccess(NotesEntity[] entities) {
            	Log.d(TAG, "Loaded "+entities.length+" data");
        	  	NotesEntityArrayAdapter entityAdapter = new NotesEntityArrayAdapter(getApplicationContext(), R.layout.notes_row, entities);
            	setListAdapter(entityAdapter); 
             }
         });
  
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_ID:
                createNote();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteNote(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createNote() {
        Intent i = new Intent(this, NoteEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, NoteEdit.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
}
