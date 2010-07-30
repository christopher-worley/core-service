package core.service.validation;

public class ServiceValidationException extends RuntimeException {

	public ServiceValidationException() {
		super();
	}

	public ServiceValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceValidationException(String message) {
		super(message);
	}

	public ServiceValidationException(Throwable cause) {
		super(cause);
	}

}
