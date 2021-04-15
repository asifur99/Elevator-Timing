package sharedObjects;

/**
 * A pair of data to store two values paired with one another.
 * 
 * @author Ashton Mohns
 *
 * @param <T>
 * @param <V>
 */
public class Pair<T, V> {

	private T t;
	private V v;
	
	/**
	 * Default constructor initializing instance variables
	 * @param t
	 * @param v
	 */
	public Pair(T t, V v) {
		this.t = t;
		this.v = v;
	}
	
	/**
	 * Get T
	 * @return
	 */
	public T getT() {
		return this.t;
	}
	
	/**
	 * Get V
	 * @return v
	 */
	
	public V getV() {
		return this.v;
	}

	/**
	 * Set T
	 * @param t
	 */
	public void setT(T t) {
		this.t = t;
	}
	
	/**
	 * Set V
	 * @param v
	 */
	public void setV(V v) {
		this.v = v;
	}
	
	/**
	 * Create a new Pair with same T and V.
	 */
	public Pair<T, V> clone() {
		return new Pair<T, V>(t, v);
	}
}
