import java.util.ArrayList;
import java.util.Random;

/**
 * @author nagabharan This class implements the ID3 algorithm for generating the
 *         DT using Info gain and variance impurity.
 */
public class DecisionTree {

	public Node root;

	private int index;
	
	public int leaf = 0;
	
	public int numnodes = 0;
	
	public int sumdepth =0;

	// Method building decision tree using entropy/impurity as a entropy
	public void buildID3Tree(ArrayList<ArrayList<Attr>> data, Node root, String entropy) {

		Entropy h = new Entropy();
		double maxGain = 0.0;
		Attr dAttr = null;
		ArrayList<ArrayList<Attr>> left = new ArrayList<>();
		ArrayList<ArrayList<Attr>> right = new ArrayList<>();
		int index = -1;

		// Compute root`s entropy
		if (entropy.equalsIgnoreCase("entropy"))
			root.entropy = h.calculateEntropy(data);
		else
			root.entropy = h.calculateVarianceImpurity(data);

		// Split data in class of 0 as left tree and 1 as right tree
		for (int i = 0; i < data.get(0).size() - 1; i++) {
			ArrayList<ArrayList<Attr>> leftSubset = new ArrayList<>();
			ArrayList<ArrayList<Attr>> rightSubset = new ArrayList<>();

			// Calculate subtree split on this Attr
			for (int j = 0; j < data.size(); j++) {
				ArrayList<Attr> list = new ArrayList<>();
				list.addAll(data.get(j));
				if (data.get(j).get(i).value == 0)
					leftSubset.add(list);
				else
					rightSubset.add(list);
			}

			// Calculate sub entropies
			ArrayList<Double> subEntropies = new ArrayList<>();
			if (entropy.equalsIgnoreCase("entropy")) {
				subEntropies.add(h.calculateEntropy(leftSubset));
				subEntropies.add(h.calculateEntropy(rightSubset));
			} else {
				subEntropies.add(h.calculateVarianceImpurity(rightSubset));
				subEntropies.add(h.calculateVarianceImpurity(leftSubset));

			}

			// Calculate size
			ArrayList<Integer> sizesOfSubsets = new ArrayList<>();
			sizesOfSubsets.add(leftSubset.size());
			sizesOfSubsets.add(rightSubset.size());

			// Compute Attr with maximum information gain
			double gain = h.infoGain(root.entropy, subEntropies, sizesOfSubsets, data.size());
			if ((int) (gain * 1000) > (int) (maxGain * 1000)) {
				maxGain = gain;
				dAttr = data.get(0).get(i);
				index = i;
				left = (ArrayList<ArrayList<Attr>>) leftSubset.clone();
				right = (ArrayList<ArrayList<Attr>>) rightSubset.clone();
			}
		}

		// If index was set, means we have an Attr on which data can be
		// split
		if (index > -1) {
			// Remove the Attr used from further consideration
			for (ArrayList<Attr> Attrs : left)
				Attrs.remove(index);
			for (ArrayList<Attr> Attrs : right)
				Attrs.remove(index);

			// Set the nodes for the recursive call to the subtree
			Node leftChild = new Node();
			Node rightChild = new Node();
			leftChild.data = left;
			rightChild.data = right;
			

			root.children = new Node[2];
			root.children[0] = leftChild;
			root.children[1] = rightChild;
			root.splitAttr = dAttr;
			root.index = ++this.index;
			
			leftChild.depth = root.depth+1;
			rightChild.depth = root.depth+1;
			
			numnodes++;

			// Recursively call subtrees
			buildID3Tree(right, rightChild, entropy);
			buildID3Tree(left, leftChild, entropy);

		} else {
			// Else, no more splitting is possible for this subtree
			root.value = data.get(0).get(data.get(0).size() - 1).value;
			leaf++;
			sumdepth += root.depth;
			return;
		}

		this.root = root;
	}

	// Method building decision tree using random attributes
	public void buildRndTree(ArrayList<ArrayList<Attr>> data, Node root) {

		Attr dAttr = null;
		ArrayList<ArrayList<Attr>> left = new ArrayList<>();
		ArrayList<ArrayList<Attr>> right = new ArrayList<>();
		int index = -1;

		Random random = new Random();
		int S = data.get(0).size();
		int M = random.nextInt(S);

		// Split data
		dAttr = data.get(0).get(M);

		ArrayList<ArrayList<Attr>> leftSubset = new ArrayList<>();
		ArrayList<ArrayList<Attr>> rightSubset = new ArrayList<>();

		// Calculate subtree split on this Attr
		for (int j = 0; j < data.size(); j++) {
			ArrayList<Attr> list = new ArrayList<>();
			list.addAll(data.get(j));

			if (data.get(j).get(M).value == 0)
				leftSubset.add(list);
			else
				rightSubset.add(list);
		}

		if (leftSubset.size()!=0 && rightSubset.size()!=0) {
			index = M;
			left = (ArrayList<ArrayList<Attr>>) leftSubset.clone();
			right = (ArrayList<ArrayList<Attr>>) rightSubset.clone();
		}

		// If index was set, means we have an Attr on which data can be
		// split
		if (index > -1) {
			// Remove the Attr used from further consideration
			for (ArrayList<Attr> Attrs : left)
				Attrs.remove(index);
			for (ArrayList<Attr> Attrs : right)
				Attrs.remove(index);

			// Set the nodes for the recursive call to the subtree
			Node leftChild = new Node();
			Node rightChild = new Node();
			leftChild.data = left;
			rightChild.data = right;

			root.children = new Node[2];
			root.children[0] = leftChild;
			root.children[1] = rightChild;
			root.splitAttr = dAttr;
			root.index = ++this.index;
			root.depth ++;
			numnodes++;
			
			// Recursively call subtrees
			buildRndTree(right, rightChild);
			buildRndTree(left, leftChild);

		} else {
			// Else, no more splitting is possible for this subtree
			root.value = data.get(0).get(data.get(0).size() - 1).value;
			leaf++;
			sumdepth += root.depth;
			return;
		}

		this.root = root;
	}

	// Computes the accuracy of a decision tree
	public double getAccuracy(ArrayList<ArrayList<Attr>> data, Node root) {
		double accuracy = 0.0;

		// For each data set, check if result is correct or not
		for (ArrayList<Attr> arrayList : data)
			if (treeClass(root, arrayList))
				accuracy++;

		// Return the computed accuracy
		return (accuracy * 100 / data.size());
	}

	public int getIndex() {
		return index;
	}

	public Node getRoot() {
		return root;
	}

	private void pruneClass(Node root, int index) {
		// If we found the root with the index
		if (root.index == index) {

			// Decide which is the majority class, and make it a leaf node
			if (root.children[0].data.size() > root.children[1].data.size()) {
				int[] valuecount = new int[2];
				for (ArrayList<Attr> Attr : root.children[0].data)
					valuecount[Attr.get(Attr.size() - 1).value]++;

				root.children[0].value = valuecount[0] > valuecount[1] ? 0 : 1;
				root.children[0].children = null;
				root.children[0].index = -1;
			} else {
				int[] valueCount = new int[2];
				for (ArrayList<Attr> Attr : root.children[1].data)
					valueCount[Attr.get(Attr.size() - 1).value]++;

				root.children[1].value = valueCount[0] > valueCount[1] ? 0 : 1;
				root.children[1].children = null;
				root.children[1].index = -1;
			}
		} else if (root.children != null) {

			// recursively explore the subtrees
			pruneClass(root.children[0], index);
			pruneClass(root.children[1], index);
		}
	}

	// Prunes a tree
	public void postPruning(int L, int K, ArrayList<ArrayList<Attr>> data) throws CloneNotSupportedException {

		// Assign the tree as best
		Node best = (Node) root.clone();
		for (int i = 0; i < L; i++) {

			// Copy root to D
			Node D = (Node) root.clone();

			// Generate a random number between 1 and K
			Random random = new Random();
			int M = random.nextInt(K);
			for (int j = 0; j < M; j++) {
				// Generate a random number between 1 and index
				int P = random.nextInt(index);
				// Find subtree rooted at P and remove it
				pruneClass(D, P);
			}

			// Set D to best if its accuracy is better
			if (getAccuracy(data, D) > getAccuracy(data, best))
				best = (Node) D.clone();
		}

		// Set root as the best tree formed
		root = (Node) best.clone();
	}

	// Custom output printer
	private String print(Node root, int level) {

		StringBuilder stringBuilder = new StringBuilder();
		for (int j = 0; j < root.children.length; j++) {
			for (int i = 0; i < level; i++)
				stringBuilder.append("| ");
			stringBuilder.append(root.splitAttr.name + " = " + j + " :");
			if (root.children[j].children != null)
				stringBuilder.append("\n" + print(root.children[j], level + 1));
			else
				stringBuilder.append(" " + root.children[j].value + "\n");
		}

		return stringBuilder.toString();
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	@Override
	public String toString() {
		return print(this.root, 0);
	}

	// Traverses a DT and checks for purity
	private boolean treeClass(Node root, ArrayList<Attr> data) {

		if (root.children == null)
			return data.get(data.size() - 1).value == root.value;
		else {
			// Compute the value of Attr in the data set
			int value = -1;
			for (Attr Attr : data)
				if (Attr.name.equals(root.splitAttr.name)) {
					value = Attr.value;
					break;
				}

			// Call recursively the child node
			return treeClass(root.children[value], data);
		}
	}
}