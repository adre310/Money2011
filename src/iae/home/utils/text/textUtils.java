package iae.home.utils.text;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Date;

public class textUtils {
	private final static DateFormat m_dateformat;
	private final static NumberFormat m_currencyformat;
	private final static NumberFormat m_integerformat;

	public static String DateToString(Date dt) {
		return m_dateformat.format(dt);
	}
	
	public static String CurrencyToString(Double Value) {
		return m_currencyformat.format(Value);
	}

	public static String CurrencyToString(Long Value) {
		return m_currencyformat.format(Value);
	}

	public static String CurrencyToString(Long Value,String Code) {
		NumberFormat curFormat=NumberFormat.getCurrencyInstance();
		curFormat.setCurrency(Currency.getInstance(Code));
		return curFormat.format(Value);
	}

	public static String CurrencyToString(Double Value,String Code) {
		NumberFormat curFormat=NumberFormat.getCurrencyInstance();
		curFormat.setCurrency(Currency.getInstance(Code));
		return curFormat.format(Value);
	}
	
	public static String LongToString(Long Value) {
		return m_integerformat.format(Value);
	}

	public static String IntegerToString(Integer Value) {
		return m_integerformat.format(Value);
	}
	
	static {
		m_dateformat=DateFormat.getDateInstance(DateFormat.MEDIUM);
		m_currencyformat=NumberFormat.getCurrencyInstance();
		m_integerformat=NumberFormat.getIntegerInstance();
	}	
}
