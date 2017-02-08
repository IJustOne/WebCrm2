package cn.ijustone.crm.exception;
/**
 * 日期转换异常,继承于运行期异常，以避免进行强制try catch
 *
 */
public class DateParseException extends RuntimeException {
	
	private static final long serialVersionUID = 4727819380287187682L;

	public DateParseException() {
		
	}

	public DateParseException(String message) {
		super(message);
	}

	public DateParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public DateParseException(Throwable cause) {
		super(cause);
	}
}
