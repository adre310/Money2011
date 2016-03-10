package iae.home.x10.model;

import java.util.Date;

public interface IPayServer {
	public String getUUID();	
	public void setUUID(String uuid);
	public String getDescription();
	public void setDescription(String description);
	public Boolean getDeleted();
	public void setDeleted(Boolean deleted);	
	public Double getValue();
	public void setValue(Double value);
	public Date getDate();
	public void setDate(Date value);
	public IAccountServer getAccount();
	public void setAccount(IAccountServer account);
	public ICategoryServer getCategory();
	public void setCategory(ICategoryServer category);
	public Boolean getSystem();
	public void setSystem(Boolean system);
	public IPayServer getLinked();
	public void setLinked(IPayServer link);

}
