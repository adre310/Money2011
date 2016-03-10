package iae.home.utils.db;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RestoreDBParser extends DefaultHandler {
	private SQLiteDatabase m_db;
	private String m_table;
	private OnRestoreRow m_onrestorerow=null;
	
	Boolean m_inTable=false;
	Boolean m_inRow=false;
	Boolean m_inColumn=false;
	Boolean m_rowUpdated=false;
	Long m_id;
	ContentValues m_rowValues=null;
	String m_colName;
	String m_colValue;
	
	public RestoreDBParser(SQLiteDatabase db, OnRestoreRow restore) {
		this.m_db=db;
		this.m_onrestorerow=restore;
	}
		
	@Override   
	public void startDocument() throws SAXException {     
		this.m_table="";   
	}
	
	@Override   
	public void endDocument() throws SAXException {    
		
	}  	
	
	@Override   
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		if(localName.equals(SQLiteXmlOperation.TABLE_TAG)) {
			this.m_inRow=false;
			this.m_inColumn=false;
			this.m_table=atts.getValue(SQLiteXmlOperation.TABLE_NAME_ATTR);
			if (this.m_table.equals("android_metadata") || 
				this.m_table.equals("sqlite_sequence") || 
				this.m_table.startsWith("uidx")) {
					this.m_inTable=false;
					return;
			}
			
			this.m_inTable=true;
		} else if(localName.equals(SQLiteXmlOperation.ROW_TAG)) {
			this.m_inColumn=false;
			if(!this.m_inTable)
				return;
			
			this.m_rowValues=new ContentValues();
			this.m_inRow=true;
			this.m_id=-1L;
		} else if(localName.equals(SQLiteXmlOperation.COLUMN_TAG)) {
			if(!this.m_inRow)
				return;
			this.m_colName=atts.getValue(SQLiteXmlOperation.COLUMN_NAME_ATTR);
			this.m_colValue="";
			this.m_inColumn=true;
		}
	}
	
	@Override   
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException { 
		if(localName.equals(SQLiteXmlOperation.TABLE_TAG)) {
			this.m_table="";   
			this.m_inTable=false;
		} else if(localName.equals(SQLiteXmlOperation.ROW_TAG)) {
			if(!this.m_inTable)
				return;
			
			if(m_onrestorerow!=null) {
				m_onrestorerow.OnRestore(this.m_table, this.m_rowValues);
			} else {
				Cursor c=this.m_db.rawQuery("SELECT * FROM "+this.m_table+" WHERE _id="+this.m_id, null);
			
				if(c!=null && c.moveToFirst()) {
					this.m_db.update(this.m_table, this.m_rowValues, "_id="+this.m_id, null);
				} else {
					this.m_db.insert(this.m_table, null, this.m_rowValues);
				}
			}
			this.m_inRow=false;
				
		} else if(localName.equals(SQLiteXmlOperation.COLUMN_TAG)) {
			if(!this.m_inRow)
				return;
			
			if(this.m_colName.equals("_id"))
				this.m_id=Long.parseLong(this.m_colValue);
			this.m_rowValues.put(this.m_colName, this.m_colValue);
			this.m_inColumn=false;
			this.m_rowUpdated=true;
		}
	}
	
	@Override   
	public void characters(char ch[], int start, int length) { 
		if(this.m_inColumn) {
			this.m_colValue = new String(ch, start, length);     
			this.m_colValue = this.m_colValue.trim(); 
		}
	}
}
