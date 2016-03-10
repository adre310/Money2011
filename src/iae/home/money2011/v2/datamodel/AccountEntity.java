package iae.home.money2011.v2.datamodel;

import iae.home.x10.model.IAccountServer;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName=AccountEntity.TABLE)
public class AccountEntity  implements Serializable, IBaseEntity, ILookupEntity, IAccountServer {

	private static final long serialVersionUID = 8323985551188367956L;

    public static final String TABLE="accounts";
    public static final String CURRENCY="currency";
    public static final String MONTHLY_LIMIT="monthly_limit";
    public static final String LIMIT_NOTIFICATION="limit_notification";
    
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

	@DatabaseField(columnName=NAME, canBeNull=false)
	private String m_name;

	public String getName() { return m_name; }
	public void setName(String name) { m_name=name; }
	
	@DatabaseField(columnName=CURRENCY)
	private String m_currency;

	public String getCurrency() { return m_currency; }
	public void setCurrency(String currency) { m_currency=currency; }

	@DatabaseField(columnName=MONTHLY_LIMIT)
	private Double m_monthlyLimit;
	
	public Double getMonthlyLimit() { return m_monthlyLimit; }
	public void setMonthlyLimit(Double monthlyLimit) { m_monthlyLimit=monthlyLimit; }
	
	@DatabaseField(columnName=LIMIT_NOTIFICATION)
	private Boolean m_limitNotification;
	
	public Boolean getLimitNotification() { return m_limitNotification!=null?m_limitNotification:false; }
	public void setLimitNotification(Boolean limitNotification) { m_limitNotification=limitNotification; }

	private List<PayEntity> m_paylist;
	
	public List<PayEntity> getPaylist() { return m_paylist; }
	public void setPaylist(List<PayEntity> list) { m_paylist=list; }

	private Double m_balance;
	public Double getBalance() { return m_balance; }
	public void setBalance(Double balance) { m_balance=balance; }

	private Double m_currentMonth;
	
	public Double getCurrentMonth() { return m_currentMonth; }
	public void setCurrentMonth(Double currentMonth) { m_currentMonth=currentMonth; }
	
	AccountEntity() {}
	
	public AccountEntity(Integer Id, String Name, String Uid) {
		m_Id=Id;
		m_name=Name;
		m_currency="RUB";
		m_monthlyLimit=0D;
		m_limitNotification=false;
		
		m_uuid=UUID.fromString(Uid).toString();
		m_modified=true;
		m_deleted=false;
		m_balance=0D;
	}

	public AccountEntity(Integer Id, String Name, String Description, String Currency, Double balance, Double curentMonth) {
		m_Id=Id;
		m_name=Name;
		m_description=Description;
		m_currency=Currency;
		m_modified=true;
		m_deleted=false;
		m_balance=balance;
		m_currentMonth=curentMonth;

		m_monthlyLimit=0D;
		m_limitNotification=false;
	}
	
	public AccountEntity(Integer id) {
		m_Id=id;
	}
	
	@Override
	public Integer getThemeId() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
