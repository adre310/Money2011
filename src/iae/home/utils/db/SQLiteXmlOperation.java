package iae.home.utils.db;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlSerializer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteXmlOperation {

	public static final String OPEN_XML_TAG = "dataset";  
	public static final String TABLE_TAG = "table";  
	public static final String TABLE_NAME_ATTR = "name";  
	public static final String ROW_TAG = "row";  
	public static final String COLUMN_TAG = "col";  
	public static final String COLUMN_NAME_ATTR = "name";  

	public static void exportDb(SQLiteDatabase db, XmlSerializer serial) throws IOException {
		serial.startTag("", OPEN_XML_TAG);
		Cursor c=db.rawQuery("select * from sqlite_master", null);
		if(c!=null && c.moveToFirst()) {
			do {
				String table=dbUtils.getStringFromCursor(c, "name");
				exportTable(table, db, serial);
			} while(c.moveToNext());
		}
		serial.endTag("", OPEN_XML_TAG);
	}

	private static void exportTable(String table,SQLiteDatabase db, XmlSerializer serial) throws IOException {
		if (table.equals("android_metadata") || 
			table.equals("sqlite_sequence") || 
			table.startsWith("uidx"))
			return;

		serial.startTag("", TABLE_TAG);
		serial.attribute("", TABLE_NAME_ATTR, table);
		Cursor c=db.rawQuery("select * from "+table, null);
		if(c!=null && c.moveToFirst()) {
			int cols=c.getColumnCount();
			do {
				serial.startTag("", ROW_TAG);
				for(int i=0;i<cols;i++) {
					if(c.getString(i)!=null && !c.getString(i).equals("")) {
						serial.startTag("", COLUMN_TAG);
						serial.attribute("", COLUMN_NAME_ATTR, c.getColumnName(i));
						serial.text(c.getString(i));
						serial.endTag("", COLUMN_TAG);
					}
				}
				serial.endTag("", ROW_TAG);
			} while(c.moveToNext());
		}
		serial.endTag("", TABLE_TAG);
	}
	
	public static void importDb(SQLiteDatabase db, InputSource source, OnRestoreRow restore)  
		throws IOException, ParserConfigurationException, SAXException 
	{
		SAXParserFactory spf = SAXParserFactory.newInstance();     
		SAXParser sp = spf.newSAXParser();      
		XMLReader xr = sp.getXMLReader();      
		RestoreDBParser dataHandler = new RestoreDBParser(db,restore);
		xr.setContentHandler(dataHandler);      
		xr.parse(source); 	
	}
	
}
