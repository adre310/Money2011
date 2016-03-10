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
			new CurrencyCode("RUB","Рубли РФ"),
			new CurrencyCode("USD","Доллар США"), 
			new CurrencyCode("EUR","ЕВРО"), 
			new CurrencyCode("AUD","Австралийский доллар"), 
			new CurrencyCode("AZN","Азербайджанский манат"), 
			new CurrencyCode("AMD","Армянский драм"),
			new CurrencyCode("BYR","Белорусский рубль"), 
			new CurrencyCode("BGN","Болгарский лев"),
			new CurrencyCode("BRL","Бразильский реал"), 
			new CurrencyCode("HUF","Венгерский форинт"), 
			new CurrencyCode("KRW","Вон Республики Корея"), 
			new CurrencyCode("DKK","Датская крона"),
			new CurrencyCode("INR","Индийская рупия"), 
			new CurrencyCode("KZT","Казахский тенге"),
			new CurrencyCode("CAD","Канадский доллар"), 
			new CurrencyCode("KGS","Киргизский сом"), 
			new CurrencyCode("CNY","Китайский юань"), 
			new CurrencyCode("LVL","Латвийский лат"),
			new CurrencyCode("LTL","Литовский лит"),
			new CurrencyCode("MDL","Молдавский лей"),
			new CurrencyCode("RON","Новый румынский лей"), 
			new CurrencyCode("TMT","Новый туркменский манат"), 
			new CurrencyCode("NOK","Норвежская крона"),
			new CurrencyCode("PLN","Польский злотый"),
			new CurrencyCode("SGD","Сингапурский доллар"), 
			new CurrencyCode("TJS","Таджикский сомони"),
			new CurrencyCode("TRY","Турецкая лира"),
			new CurrencyCode("UZS","Узбекский сум"),
			new CurrencyCode("UAH","Украинская гривна"), 
			new CurrencyCode("GBP","Фунт стерлинг. С. Корол-ва"), 
			new CurrencyCode("CZK","Чешская крона"),
			new CurrencyCode("SEK","Шведская крона"),
			new CurrencyCode("CHF","Швейцарский франк"), 
			new CurrencyCode("ZAR","Южноафриканский рэнд"), 
			new CurrencyCode("JPY","Японская иена")
		};

	private static final CurrencyCode[] arr_en=new CurrencyCode[] {
		new CurrencyCode("USD","US Dollars"), 
		new CurrencyCode("EUR","Euro"), 
		new CurrencyCode("JPY","Japan Yen"),
		new CurrencyCode("GBP","United Kingdom Pound"), 
		new CurrencyCode("AUD","Australia Dollar"), 
		new CurrencyCode("AZN","Azerbaijan New Manat"), 
		//new CurrencyCode("AMD","Армянский драм"),
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
		//new CurrencyCode("MDL","Молдавский лей"),
		new CurrencyCode("RON","Romania New Leu"), 
		new CurrencyCode("RUB","Russia Ruble"),
		//new CurrencyCode("TMT","Новый туркменский манат"), 
		new CurrencyCode("NOK","Norway Krone"),
		new CurrencyCode("PLN","Poland Zloty"),
		new CurrencyCode("SGD","Singapore Dollar"), 
		//new CurrencyCode("TJS","Таджикский сомони"),
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
