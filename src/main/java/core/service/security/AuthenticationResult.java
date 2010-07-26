package core.service.security;

public class AuthenticationResult {
	
	public enum ErrorCode {
		INVALID_CREDENTIALS,
		SYSTEM_FAILURE,
	};
	
	private String message;
	
	private ErrorCode resultType;

	public AuthenticationResult(String message, ErrorCode resultType) {
		super();
		this.message = message;
		this.resultType = resultType;
	}

	public String getMessage() {
		return message;
	}

	public ErrorCode getResultType() {
		return resultType;
	}
	
	
	

}
