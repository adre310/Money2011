package iae.home.money2011.v2.datamodel;

public interface IBaseEntity {
    public static final String R_GUID="r_guid";
    public static final String MODIFIED="modified";
    public static final String DESCRIPTION="notes";
    public static final String DELETED="deleted";

	public Integer getId();
	
	public String getUUID();
	public void setUUID();
	
	public Boolean getModifiedDate();
	public void setModified(Boolean modified);

	public Boolean getDeleted();
	public void setDeleted(Boolean deleted);
	
	public String getDescription();
	public void setDescription(String description);
}
