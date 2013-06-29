package com.android.demo.notepad3;

import com.google.api.client.json.GenericJson;
import com.kinvey.java.model.KinveyMetaData;
import com.kinvey.java.model.KinveyMetaData.AccessControlList;
import com.google.api.client.util.Key;

public class NotesEntity extends GenericJson {
	

	public NotesEntity() {
		super();
	}

	@Key("_id")
    private String id; 
    @Key("Title")
    private String title;
    @Key("Body")
    private String body;
    @Key("_kmd")
    private KinveyMetaData meta; // Kinvey metadata, OPTIONAL
    @Key("_acl")
    private KinveyMetaData.AccessControlList acl; //Kinvey access control, OPTIONAL
	
    private static StringBuffer mStrBuff = new StringBuffer();
    
    public NotesEntity(long mRowId, String mTitleText, String mBodyText) {
    	mStrBuff.setLength(0);
    	mStrBuff.append(mRowId);
    	this.id = mStrBuff.toString();
		this.title = mTitleText;
		this.body = mBodyText;
	}
	// Getters and Setters
    public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	@Override
	public String toString(){
		return title;
		
	}
}
