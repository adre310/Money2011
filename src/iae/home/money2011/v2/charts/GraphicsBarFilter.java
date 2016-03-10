package iae.home.money2011.v2.charts;

import iae.home.utils.text.CurrencyCode;

import java.io.Serializable;

public class GraphicsBarFilter implements Serializable {
	private static final long serialVersionUID = -8208449337424989486L;
	
	public static final Integer DEPOSIT=1;
	public static final Integer WITHDRAWAL=0;
	
	private String m_currency;
	private Integer m_accountId;
	private Integer m_mode;
	
	public GraphicsBarFilter(String currency,Integer AccointId,Integer Mode) {
		m_currency=currency;
		m_accountId=AccointId;
		m_mode=Mode;
	}
	
	public GraphicsBarFilter() {
		m_currency=CurrencyCode.getDefaultCurrency();
		m_accountId=-1;
		m_mode=WITHDRAWAL;
	}
	
	public String getCurrency() { return m_currency; }
	public void setCurrency(String currency) { m_currency=currency; }
	
	public Integer getAccountId() { return m_accountId; }
	public void setAccountId(Integer accountId) { m_accountId=accountId; }
	
	public Integer getMode() { return m_mode; }
	public void setMode(Integer mode) { m_mode=mode; } 
}
