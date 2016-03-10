package iae.home.x10.model;

public interface IAccountServer {
	public String getUUID();	
	public void setUUID(String uuiid);

	public String getDescription();
	public void setDescription(String description);
	
	public Boolean getDeleted();
	public void setDeleted(Boolean deleted);

	public String getName();
	public void setName(String name);
	
	public String getCurrency();
	public void setCurrency(String currency);
	
	public Double getMonthlyLimit();
	public void setMonthlyLimit(Double monthlyLimit);
	
	public Boolean getLimitNotification();
	public void setLimitNotification(Boolean limitNotification);

}
