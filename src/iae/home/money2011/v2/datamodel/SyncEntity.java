package iae.home.money2011.v2.datamodel;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName=SyncEntity.TABLE)
public class SyncEntity {
    public static final String TABLE="sync_info";
    public static final String DATE="sync_date";

	@DatabaseField(generatedId = true,columnName="_id")
	private Integer m_Id;
    
	public Integer getId() { return m_Id; }

	@DatabaseField(columnName=DATE,dataType=DataType.DATE_LONG)
	private Date m_date;

	public Date getDate() { return m_date; }
	public void setDate(Date value) { m_date=value; }
	
	public SyncEntity() { }
}
