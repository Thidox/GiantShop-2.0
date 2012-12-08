package nl.giantit.minecraft.GiantShop.API.GSW.Exceptions;

/**
 *
 * @author Giant
 */
public class RSAKeyGenException extends Exception {
	
	private static final long serialVersionUID = 5326215166385708935L;
	
	public static enum ErrType {
		RSAAlgorithmNotFoundError(),
		InvalidAlgorithmError()
	}
	
	private String e;
	private ErrType t;
	
	public RSAKeyGenException(String e, ErrType t) {
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
