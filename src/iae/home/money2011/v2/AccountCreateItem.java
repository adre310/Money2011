package iae.home.money2011.v2;

import iae.home.utils.text.CurrencyCode;

import java.io.Serializable;
import java.util.Date;

public class AccountCreateItem implements Serializable {
	private static final long serialVersionUID = -8902196991760274259L;

	private String m_name;
	public String getName() { return m_name; }
	public void setName(String name) { m_name=name; }

	private String m_description;
	public String getDescription() { return m_description; }
	public void setDescription(String description) { m_description=description; }
	
	private Date m_date;
	public Date getDate() { return m_date; }
	public void setDate(Date date) { m_date=date; }
	
	private String m_currency;
	public String getCurrency() { return m_currency; }
	public void setCurrency(String currency) { m_currency=currency; }
	
	private Double m_balance;
	public Double getBalance() { return m_balance; }
	public void setBalance(Double balance) { m_balance=balance; }
	
	public AccountCreateItem() {
		m_name="";
		m_date=new Date();
		m_currency=CurrencyCode.getDefaultCurrency();
		
		m_balance=0D;
	}
}
