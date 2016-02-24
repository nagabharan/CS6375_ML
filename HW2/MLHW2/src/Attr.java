/**
 * @author nagabharan 
 * This class contains the parameters to hold values in a
 *         node.
 */
public class Attr {

	// Name
	public String name;

	// Value
	public int value;

	// Constructor
	public Attr(String name, int value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(int value) {
		this.value = value;
	}
}