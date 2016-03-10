package iae.home.money2011.v2.datamodel;

import iae.home.x10.model.ICategoryServer;

import java.io.Serializable;
import java.util.UUID;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName=CategoryEntity.TABLE)
public class CategoryEntity implements Serializable, IBaseEntity, ILookupEntity, ICategoryServer {

	private static final long serialVersionUID = 3669243490233704020L;
    
    public static final String TABLE="categories";
    public static final String IS_DEFAULT="is_default";
    public static final String THEME="theme_id";

	@DatabaseField(generatedId = true,columnName="_id")
	private Integer m_Id;
    
	public Integer getId() { return m_Id; }
	
	@DatabaseField(columnName=R_GUID,unique=true,canBeNull=false)
	private String m_uuid;
	public String getUUID() { return m_uuid; }

	@DatabaseField(columnName=MODIFIED,dataType=DataType.BOOLEAN)
	private Boolean m_modified;
	public Boolean getModifiedDate() { return m_modified; }
	
	public void setUUID() { m_uuid=UUID.randomUUID().toString(); }
	public void setUUID(String uuid) {this.m_uuid=uuid;}
	public void setModified(Boolean modified) { m_modified=modified; }

	@DatabaseField(columnName=DESCRIPTION,canBeNull=true)
	private String m_description;
	public String getDescription() { return m_description; }
	public void setDescription(String description) { m_description=description; }

	@DatabaseField(columnName=DELETED,dataType=DataType.BOOLEAN)
	private Boolean m_deleted;
	public Boolean getDeleted() { return m_deleted; }
	public void setDeleted(Boolean deleted) { m_deleted=deleted; }

	@DatabaseField(columnName=NAME,canBeNull=false)
	private String m_name;

	public String getName() { return m_name; }
	public void setName(String name) { m_name=name; }
	
	@DatabaseField(columnName=IS_DEFAULT)
	private Boolean m_default;
	public Boolean getDefault() { return m_default!=null?m_default:false; }
	public void setDefault(Boolean Default) { m_default=Default; }
	
	@DatabaseField(columnName=THEME)
	private Integer m_theme;
	public Integer getThemeId() { return m_theme; }
	public void setThemeId(Integer theme) { m_theme=theme; }
	
	CategoryEntity() {}
	
	public CategoryEntity(Integer id, String Name, Boolean IsDefault, String Uid,Integer Theme) {
		m_Id=id;
		m_name=Name;
		m_default=IsDefault;
		m_uuid=UUID.fromString(Uid).toString();
		m_modified=true;
		m_deleted=false;
		m_theme=Theme;
	}

	public CategoryEntity(Integer id) {
		m_Id=id;
		if(m_Id<0) {
			m_name="";
			m_uuid=UUID.randomUUID().toString();
			m_modified=true;
			m_deleted=false;
			m_theme=0;
		}
	}
}
