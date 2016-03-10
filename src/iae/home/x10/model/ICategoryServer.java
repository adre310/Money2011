package iae.home.x10.model;

public interface ICategoryServer {
	public String getUUID();	
	public void setUUID(String uuiid);
	public String getDescription();
	public void setDescription(String description);

	public Boolean getDeleted();
	public void setDeleted(Boolean deleted);

	public String getName();
	public void setName(String name);
	
	public Boolean getDefault();
	public void setDefault(Boolean Default);
	
	public Integer getThemeId();
	public void setThemeId(Integer theme);
	
}
