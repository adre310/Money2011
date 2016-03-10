package iae.home.money2011.v2.datamodel;

import iae.home.x10.model.IAccountServer;
import iae.home.x10.model.ICategoryServer;
import iae.home.x10.model.IPayServer;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName=PayEntity.TABLE)
public class PayEntity  implements Serializable , IBaseEntity, IPayServer {

	private static final long serialVersionUID = 1614481324068872361L;

    public static final String TABLE="pays";
    public static final String VALUE="pay_value";
    public static final String DATE="pay_date";
    public static final String ACCOUNT="account_id";
    public static final String CATEGORY="category_id";
    public static final String IS_SYSTEM="is_system";
    public static final String LINKED="linked_id";
    
	@DatabaseField(generatedId = true,columnName="_id")
	private Integer m_Id;
    
	public Integer getId() { return m_Id; }
	
	@DatabaseField(columnName=R_GUID,unique=true,canBeNull=false)
	private String m_uuid;
	public String getUUID() { return m_uuid; }
	public void setUUID(String uuid) {this.m_uuid=uuid;}

	@DatabaseField(columnName=MODIFIED,dataType=DataType.BOOLEAN)
	private Boolean m_modified;
	public Boolean getModifiedDate() { return m_modified; }
	
	public void setUUID() { m_uuid=UUID.randomUUID().toString(); }
	public void setModified(Boolean modified) { m_modified=modified; }

	@DatabaseField(columnName=DESCRIPTION,canBeNull=true)
	private String m_description;
	public String getDescription() { return m_description; }
	public void setDescription(String description) { m_description=description; }

	@DatabaseField(columnName=DELETED,dataType=DataType.BOOLEAN)
	private Boolean m_deleted;
	public Boolean getDeleted() { return m_deleted; }
	public void setDeleted(Boolean deleted) { m_deleted=deleted; }
	
	@DatabaseField(columnName=VALUE)
	private Double m_value;
	
	public Double getValue() { return m_value; }
	public void setValue(Double value) { m_value=value; }

	@DatabaseField(columnName=DATE,dataType=DataType.DATE_LONG)
	private Date m_date;

	public Date getDate() { return m_date; }
	public void setDate(Date value) { m_date=value; }
	
	@DatabaseField(columnName=ACCOUNT,foreign=true)
	private AccountEntity m_budget;

	public AccountEntity getAccount() { return m_budget; }
	public void setAccount(AccountEntity budget) { m_budget=budget; }
	public void setAccount(IAccountServer account) {
		if(account!=null)
			m_budget=(AccountEntity)account;
		else
			m_budget=null;		
	}
	
	@DatabaseField(columnName=CATEGORY,foreign=true)
	private CategoryEntity m_category;

	public CategoryEntity getCategory() { return m_category; }
	public void setCategory(CategoryEntity category) { m_category=category; }
	public void setCategory(ICategoryServer category) {
		if(category!=null)
			m_category=(CategoryEntity)category;
		else
			m_category=null;
	}
	
	@DatabaseField(columnName=IS_SYSTEM)
	private Boolean m_system;
	
	public Boolean getSystem() { return m_system!=null?m_system:false; }
	public void setSystem(Boolean system) { m_system=system; }

	@DatabaseField(columnName=LINKED,foreign=true)
	private PayEntity m_linked;

	public PayEntity getLinked() { return m_linked; }
	public void setLinked(PayEntity link) { m_linked=link; }
	public void setLinked(IPayServer link) {
		if(link!=null)
			m_linked=(PayEntity)link;
		else
			m_linked=null;		
	}

	private String m_category_name;
	public String getCategoryName() { return m_category_name; }
	public void setCategoryName(String name) { m_category_name=name; }
	
	private Integer m_category_theme;
	public Integer getCategoryTheme() { return m_category_theme; }
	public void setCategoryTheme(Integer theme) { m_category_theme=theme; }
	
	public PayEntity() {}

	public PayEntity(Integer id) {
		m_Id=id;
		m_uuid=UUID.randomUUID().toString();
		m_modified=true;
		m_deleted=false;
		m_value=0D;
		m_date=new Date();
		m_system=false;
	}
	
	public PayEntity(Integer id, String Uid) {
		m_Id=id;
		m_uuid=UUID.fromString(Uid).toString();
		m_modified=true;
		m_deleted=false;
	}
	
	public PayEntity(Integer id, Double value, Date date, String CategoryName, Integer CategoryTheme) {
		m_Id=id;
		m_value=value;
		m_date=date;
		m_category_name=CategoryName;
		m_category_theme=CategoryTheme;
	}
}
