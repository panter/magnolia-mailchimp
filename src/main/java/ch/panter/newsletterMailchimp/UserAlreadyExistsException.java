package ch.panter.newsletterMailchimp;


public class UserAlreadyExistsException extends RuntimeException {

	public UserAlreadyExistsException(String message, Throwable e) {
		super(message, e);
	}

}
