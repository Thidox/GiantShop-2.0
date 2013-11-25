package nl.giantit.minecraft.giantshop.API.GSW.Exceptions;

/**
 *
 * @author Giant
 */
public class RSAKeySaveException extends Exception {
	
	private static final long serialVersionUID = 5326215166385708935L;
	
	public static enum ErrType {
		PublicKeyFileNotFoundError(),
		PublicKeyFileCloseError(),
		
		PrivateKeyFileNotFoundError(),
		PrivateKeyFileCloseError()
	}
	
	private String e;
	private ErrType t;
	
	public RSAKeySaveException(String e, ErrType t) {
		super();
		this.e = e;
		this.t = t;
	}
	
	public String getMsg() {
		return this.e;
	}
	
	public ErrType getErrType() {
		return this.t;
	}
}