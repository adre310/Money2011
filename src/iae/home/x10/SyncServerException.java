package iae.home.x10;

public class SyncServerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1165017497398575221L;
	private final int m_code;
	
	public SyncServerException(int StatusCode, String StatusMessage) {
		super(StatusMessage);
		m_code=StatusCode;
	}
	
	public int getStatusCode() { return m_code; } 
}
