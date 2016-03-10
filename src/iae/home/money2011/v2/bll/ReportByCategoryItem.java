package iae.home.money2011.v2.bll;

import java.io.Serializable;

public class ReportByCategoryItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1986844108723721429L;

	private final String m_category;
	public String getCategory() { return m_category; }
	
	private final Double m_value;
	public Double getValue() { return m_value; }
	
	public ReportByCategoryItem(String category, Double value) {
		m_category=category;
		m_value=value;
	}
}
