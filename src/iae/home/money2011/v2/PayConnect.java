package iae.home.money2011.v2;

import java.io.Serializable;

public class PayConnect implements Serializable {
	private static final long serialVersionUID = 6954365601513999690L;

	private final Integer m_payId;
	public Integer getPayId() { return m_payId; }

	private final Integer m_budgetId;
	public Integer getBudgetId() { return m_budgetId; }
		
	public PayConnect(Integer payId, Integer budgetId) {
		m_payId=payId;
		m_budgetId=budgetId;
	}

	public PayConnect(Integer budgetId) {
		m_payId=-1;
		m_budgetId=budgetId;
	}
}
