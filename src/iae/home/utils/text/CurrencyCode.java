package iae.home.utils.text;

import java.io.Serializable;
import java.util.Currency;
import java.util.Locale;

public class CurrencyCode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8288901663089142599L;

	private static final CurrencyCode[] arr_ru=new CurrencyCode[] {
			new CurrencyCode("RUB","����� ��"),
			new CurrencyCode("USD","������ ���"), 
			new CurrencyCode("EUR","����"), 
			new CurrencyCode("AUD","������������� ������"), 
			new CurrencyCode("AZN","��������������� �����"), 
			new CurrencyCode("AMD","��������� ����"),
			new CurrencyCode("BYR","����������� �����"), 
			new CurrencyCode("BGN","���������� ���"),
			new CurrencyCode("BRL","����������� ����"), 
			new CurrencyCode("HUF","���������� ������"), 
			new CurrencyCode("KRW","��� ���������� �����"), 
			new CurrencyCode("DKK","������� �����"),
			new CurrencyCode("INR","��������� �����"), 
			new CurrencyCode("KZT","��������� �����"),
			new CurrencyCode("CAD","��������� ������"), 
			new CurrencyCode("KGS","���������� ���"), 
			new CurrencyCode("CNY","��������� ����"), 
			new CurrencyCode("LVL","���������� ���"),
			new CurrencyCode("LTL","��������� ���"),
			new CurrencyCode("MDL","���������� ���"),
			new CurrencyCode("RON","����� ��������� ���"), 
			new CurrencyCode("TMT","����� ����������� �����"), 
			new CurrencyCode("NOK","���������� �����"),
			new CurrencyCode("PLN","�������� ������"),
			new CurrencyCode("SGD","������������ ������"), 
			new CurrencyCode("TJS","���������� ������"),
			new CurrencyCode("TRY","�������� ����"),
			new CurrencyCode("UZS","��������� ���"),
			new CurrencyCode("UAH","���������� ������"), 
			new CurrencyCode("GBP","���� ��������. �. �����-��"), 
			new CurrencyCode("CZK","������� �����"),
			new CurrencyCode("SEK","�������� �����"),
			new CurrencyCode("CHF","����������� �����"), 
			new CurrencyCode("ZAR","��������������� ����"), 
			new CurrencyCode("JPY","�������� ����")
		};

	private static final CurrencyCode[] arr_en=new CurrencyCode[] {
		new CurrencyCode("USD","US Dollars"), 
		new CurrencyCode("EUR","Euro"), 
		new CurrencyCode("JPY","Japan Yen"),
		new CurrencyCode("GBP","United Kingdom Pound"), 
		new CurrencyCode("AUD","Australia Dollar"), 
		new CurrencyCode("AZN","Azerbaijan New Manat"), 
		//new CurrencyCode("AMD","��������� ����"),
		new CurrencyCode("BYR","Belarus Ruble"), 
		new CurrencyCode("BGN","Bulgaria Lev"),
		new CurrencyCode("BRL","Brazil Real"), 
		new CurrencyCode("HUF","Hungary Forint"), 
		new CurrencyCode("KRW","Korea (South) Won"), 
		new CurrencyCode("DKK","Denmark Krone"),
		new CurrencyCode("INR","India Rupee"), 
		new CurrencyCode("KZT","Kazakhstan Tenge"),
		new CurrencyCode("CAD","Canada Dollar"), 
		new CurrencyCode("KGS","Kyrgyzstan Som"), 
		new CurrencyCode("CNY","China Yuan Renminbi"), 
		new CurrencyCode("LVL","Latvia Lat"),
		new CurrencyCode("LTL","Lithuania Litas"),
		//new CurrencyCode("MDL","���������� ���"),
		new CurrencyCode("RON","Romania New Leu"), 
		new CurrencyCode("RUB","Russia Ruble"),
		//new CurrencyCode("TMT","����� ����������� �����"), 
		new CurrencyCode("NOK","Norway Krone"),
		new CurrencyCode("PLN","Poland Zloty"),
		new CurrencyCode("SGD","Singapore Dollar"), 
		//new CurrencyCode("TJS","���������� ������"),
		new CurrencyCode("TRY","Turkey Lira"),
		new CurrencyCode("UZS","Uzbekistan Som"),
		new CurrencyCode("UAH","Ukraine Hryvna"), 
		new CurrencyCode("CZK","Czech Republic Koruna"),
		new CurrencyCode("SEK","Sweden Krona"),
		new CurrencyCode("CHF","Switzerland Franc"), 
		new CurrencyCode("ZAR","South Africa Rand") 
	};
		
	public static CurrencyCode[] getList() {
		Locale curLocale=Locale.getDefault();
		if(curLocale.getLanguage().equals("ru"))
			return arr_ru; 
		else
			return arr_en;  
	}
	
	private final String m_code;
	public String getCode() { return m_code; }
	
	private final String m_name;
	public String getName() { return m_name; } 
		
	public CurrencyCode(String NameCode, String Name) {
		m_code=NameCode;
		m_name=Name;
	}
	
	public static String getDefaultCurrency() {
		Locale curLocale=Locale.getDefault();
	
		if(curLocale.getLanguage().equals("ru")) {
			return "RUB";
		} else {
			Currency currency=Currency.getInstance(curLocale);
			String curCode=currency.getCurrencyCode();
			
			for(CurrencyCode c : getList()) {
				if(c.getCode().equals(curCode))
					return curCode;
			}
			
			return "USD";
		}
	}
}
