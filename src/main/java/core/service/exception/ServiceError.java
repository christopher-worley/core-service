package core.service.exception;

public class ServiceError extends ServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5956077078532570922L;

	public ServiceError() {
		super();
	}

	public ServiceError(String message, Object... params) {
		super(message, params);
	}

	public ServiceError(String message, Throwable cause, Object... params) {
		super(message, cause, params);
	}

	public ServiceError(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceError(String message) {
		super(message);
	}

	public ServiceError(Throwable cause) {
		super(cause);
	}

	
}
