import java.util.ArrayList;

/**
 * @author nagabharan This class contains the node data structure for the DT.
 *         Object cloning is a process of generating the exact copy of object
 *         with the different name. i.e Same type of child node different
 *         selection attribute.
 */
public class Node implements Cloneable {

	// Children
	public Node[] children;

	// Value in case of leaf
	public int value;

	// Splitting Attribute
	public Attr splitAttr;

	// Node index for pruning
	public int index;

	// Impurity at node
	public double entropy;

	// Subtree
	public ArrayList<ArrayList<Attr>> data;
	
	// Depth
	public int depth;

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	// Constructor
	public Node() {
		children = null;
		entropy = 0.0;
		data = new ArrayList<>();
		value = 0;
		splitAttr = new Attr("", 0);
		index = -1;
		depth = 0;
	}

	// Creates a copy of the node object for generating the subtree
	@Override
	public Object clone() throws CloneNotSupportedException {
		Node cloned = new Node();
		cloned.setIndex(this.index);
		cloned.setDepth(this.depth);
		cloned.setValue(this.value);
		cloned.setSplitAttr(new Attr(this.splitAttr.name, this.splitAttr.value));
		cloned.setEntropy(this.entropy);

		// Copy the data
		for (ArrayList<Attr> Attributes : this.data) {
			ArrayList<Attr> clonedAttributes = new ArrayList<>();
			for (Attr Attr : Attributes)
				clonedAttributes.add(new Attr(Attr.getName(), Attr.getValue()));

			cloned.data.add(clonedAttributes);
		}

		// Recursively clone children
		if (this.children != null) {
			cloned.children = new Node[2];
			for (int i = 0; i < this.children.length; i++)
				cloned.children[i] = (Node) this.children[i].clone();
		}

		// Return the cloned node
		return cloned;
	}

	public Node[] getChildren() {
		return children;
	}

	public ArrayList<ArrayList<Attr>> getData() {
		return data;
	}

	public double getEntropy() {
		return entropy;
	}

	public int getIndex() {
		return index;
	}

	public Attr getSplitAttr() {
		return splitAttr;
	}

	public int getValue() {
		return value;
	}

	public void setChildren(Node[] children) {
		this.children = children;
	}

	public void setData(ArrayList<ArrayList<Attr>> data) {
		this.data = data;
	}

	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setSplitAttr(Attr splitAttr) {
		this.splitAttr = splitAttr;
	}

	public void setValue(int value) {
		this.value = value;
	}
}