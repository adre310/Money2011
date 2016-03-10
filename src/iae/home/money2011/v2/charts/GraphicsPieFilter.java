package iae.home.money2011.v2.charts;

import java.util.Date;

public class GraphicsPieFilter extends GraphicsBarFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 584251737340769229L;
	
	public static final Integer PIE=0;
	public static final Integer BAR=1;
	
	private Date m_begin;
	private Date m_end;
	private Integer m_viewmode;

	public Date getBeginDate() { return m_begin; }
	public void setBeginDate(Date begin) { m_begin=begin; }

	public Date getEndDate() { return m_end; }
	public void setEndDate(Date end) { m_end=end; }
	
	public Integer getViewMode() { return m_viewmode; }
	public void setViewMode(Integer mode) { m_viewmode=mode; }
	
	public GraphicsPieFilter(String currency, Integer AccointId, Integer Mode,Date begin,Date end) {
		super(currency, AccointId, Mode);
		m_begin=begin;
		m_end=end;
		m_viewmode=PIE;
	}

	public GraphicsPieFilter() {
		super();
		Date now=new Date();
		m_begin=new Date(now.getYear(),now.getMonth(),1);
		m_end=new Date(now.getYear(),now.getMonth()+1,1);
		m_viewmode=PIE;
	}

}
