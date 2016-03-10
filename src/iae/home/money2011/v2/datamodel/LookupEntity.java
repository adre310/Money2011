package iae.home.money2011.v2.datamodel;

import java.io.Serializable;

public class LookupEntity implements Serializable, ILookupEntity {
	private static final long serialVersionUID = -9080898843487934487L;
	
	private final Integer m_id;
	private final String m_name;
	private final Integer m_themeId;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return m_id;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return m_name;
	}

	@Override
	public Integer getThemeId() {
		// TODO Auto-generated method stub
		return m_themeId;
	}
	
	public LookupEntity(Integer Id,String Name, Integer Theme) {
		m_id=Id;
		m_name=Name;
		m_themeId=Theme;
	}

}
