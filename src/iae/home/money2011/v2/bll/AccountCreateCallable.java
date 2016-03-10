package iae.home.money2011.v2.bll;

import iae.home.money2011.v2.datamodel.AccountEntity;
import iae.home.money2011.v2.datamodel.DatabaseHelper;

import java.util.Date;
import java.util.concurrent.Callable;

public class AccountCreateCallable implements Callable<AccountEntity> {
	private DatabaseHelper m_dbhelper;
	private final String m_name;
	private final String m_currency;
	private final String m_description;
	private final Date m_createDate;
	private final Double m_balance;
	
	public AccountCreateCallable(DatabaseHelper DBhelper,String Name,String Currency,String description,Date createDate,Double balance) {
		m_dbhelper=DBhelper;
		m_name=Name;
		m_currency=Currency;
		m_description=description;
		m_createDate=createDate;
		m_balance=balance;
	}
	
	@Override
	public AccountEntity call() throws Exception {
		AccountEntity account=new AccountEntity(-1);
		account.setCurrency(m_currency);
		account.setName(m_name);
		account.setDescription(m_description);
		
		BaseAction.createObject(m_dbhelper.getAccountsDao(), account);
		
		PayAction.internalCreatePay(m_dbhelper, account, m_balance, m_createDate);
		return account;
	}
	
}
