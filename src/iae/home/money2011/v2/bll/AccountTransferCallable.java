package iae.home.money2011.v2.bll;

import iae.home.money2011.v2.R;
import iae.home.money2011.v2.datamodel.AccountEntity;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.datamodel.PayEntity;
import iae.home.utils.text.textUtils;

import java.util.Date;
import java.util.concurrent.Callable;

import android.content.Context;

import com.j256.ormlite.dao.Dao;


public class AccountTransferCallable implements Callable<Void> {
//DatabaseHelper dbhelper, Integer idFrom, Integer idTo, Double value
	private final Context m_context;
	private DatabaseHelper m_dbhelper;
	private final Integer m_from;
	private final Integer m_to;
	private final Double m_value;
	private final Date m_date;
	
	public AccountTransferCallable(DatabaseHelper dbhelper, Context context, Integer idFrom, Integer idTo, Double value, Date date) {
		m_dbhelper=dbhelper;
		m_context=context;
		m_from=idFrom;
		m_to=idTo;
		m_value=value;
		m_date=date;
	}
	
	@Override
	public Void call() throws Exception {
		Dao<PayEntity,Integer> payDao=m_dbhelper.getPaysDao();
		Dao<AccountEntity,Integer> accountDao=m_dbhelper.getAccountsDao();
		AccountEntity accFrom=accountDao.queryForId(m_from);
		AccountEntity accTo=accountDao.queryForId(m_to);
		
		String notes=m_context.getResources().getString(R.string.transfer_note_format, 
				textUtils.CurrencyToString(m_value, accFrom.getCurrency()),
				accFrom.getName(),
				accTo.getName());
		
		PayEntity pay=new PayEntity(-1);
		pay.setValue(-m_value);
		pay.setDate(m_date);
		pay.setDescription(notes);
		pay.setAccount(accFrom);
		pay.setSystem(true);
		BaseAction.createObject(payDao, pay);
		
		PayEntity payLinked=new PayEntity(-1);
		payLinked.setValue(m_value);
		payLinked.setDate(m_date);
		payLinked.setDescription(notes);
		payLinked.setAccount(accTo);
		payLinked.setSystem(true);
		BaseAction.createObject(payDao, payLinked);

		pay.setLinked(payLinked);
		BaseAction.updateObject(payDao, pay);
		
		payLinked.setLinked(pay);
		BaseAction.updateObject(payDao, payLinked);
		
		return null;
	}

}
