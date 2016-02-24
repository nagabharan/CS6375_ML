import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.text.AbstractDocument.LeafElement;

/**
 * @author nagabharan
 * This is the main class where the execution begins.
 * We call the respective functions here to train the DT,
 * validate and test it using info gain.
 */
public class Main {
	
	public static void main(String[] args) throws Exception {
		// Parse arguments
		int L = Integer.parseInt(args[0]);
		int K = Integer.parseInt(args[1]);
		ArrayList<ArrayList<Attr>> trainingSet = parseCSV(args[2]);
		ArrayList<ArrayList<Attr>> validationSet = parseCSV(args[3]);
		ArrayList<ArrayList<Attr>> testSet = parseCSV(args[4]);
		boolean print = args[5].equalsIgnoreCase("yes");

		// Build tree using information gain and print its accuracy over training
		// set
		DecisionTree tree = new DecisionTree();
		tree.buildID3Tree(trainingSet, new Node(), "entropy");
		System.out
				.println("Accuracy of  tree constructed using information gain before pruning: "
						+ tree.getAccuracy(testSet, tree.root));
		System.out.println("Total number of leaf nodes generated:"+tree.leaf);
		System.out.println("Total number of internal nodes generated:"+tree.numnodes);
		System.out.println("Sum of depth of nodes generated:"+tree.sumdepth);
		System.out.println("Average Depth:"+(tree.sumdepth/tree.leaf));
		
		if (print) {
			System.out.println("Before pruning: ");
			System.out.println(tree);
			
		}

		// Post prune the tree and print its accuracy over test set
		if(K == 0){
			System.out
			.println("Accuracy of  tree constructed using information gain after pruning: "
					+ tree.getAccuracy(testSet, tree.root));
		} else {
			tree.postPruning(L, K, validationSet);
			System.out
					.println("Accuracy of  tree constructed using information gain after pruning: "
							+ tree.getAccuracy(testSet, tree.root));
		}
		
		// If to-Print is yes, print the decision tree
		if (print) {
			System.out.println("After pruning: ");
			System.out.println(tree);
		}

		// Build tree using impurity gain
		tree = new DecisionTree();
		tree.buildID3Tree(trainingSet, new Node(), "impurity");
		System.out
				.println("Accuracy of  tree constructed using impurity gain before pruning: "
						+ tree.getAccuracy(testSet, tree.root));
		System.out.println("Total number of leaf nodes generated:"+tree.leaf);
		System.out.println("Total number of internal nodes generated:"+tree.numnodes);
		System.out.println("Sum of depth of nodes generated:"+tree.sumdepth);
		System.out.println("Average Depth:"+(tree.sumdepth/tree.leaf));


		if (print) {
			System.out.println("Decision tree before pruning: ");
			System.out.println(tree);
		}

		// Post prune the tree and print its accuracy over test set
		if(K == 0) {
			System.out
			.println("Accuracy of  tree constructed using impurity gain after pruning: "
					+ tree.getAccuracy(testSet, tree.root));
		} else {
			tree.postPruning(L, K, validationSet);
			System.out
					.println("Accuracy of  tree constructed using impurity gain after pruning: "
							+ tree.getAccuracy(testSet, tree.root));
		}		

		// If to-Print is yes, print the decision tree
		if (print) {
			System.out.println("Decision tree after pruning: ");
			System.out.println(tree);
		}
		
		tree.buildRndTree(trainingSet, new Node());
		System.out
				.println("Accuracy of  tree constructed using random before pruning: "
						+ tree.getAccuracy(testSet, tree.root));
		System.out.println("Total number of leaf nodes generated:"+tree.leaf);
		System.out.println("Total number of internal nodes generated:"+tree.numnodes);
		System.out.println("Sum of depth of nodes generated:"+tree.sumdepth);
		System.out.println("Average Depth:"+(tree.sumdepth/tree.leaf));
		
		// If to-Print is yes, print the decision tree
		if (print) {
			System.out.println("Decision tree after pruning: ");
			System.out.println(tree);
		}
				
		if(K == 0){
			System.out
			.println("Accuracy of  tree constructed using random after pruning: "
					+ tree.getAccuracy(testSet, tree.root));
		} else {
			tree.postPruning(L, K, validationSet);
			System.out
					.println("Accuracy of  tree constructed using random after pruning: "
							+ tree.getAccuracy(testSet, tree.root));
		}
		
		// If to-Print is yes, print the decision tree
		if (print) {
			System.out.println("Decision tree after pruning: ");
			System.out.println(tree);
		}
	}

	//Parses a CSV file to read a data set
	@SuppressWarnings("finally")
	private static ArrayList<ArrayList<Attr>> parseCSV(String fileLocation) {
		BufferedReader br = null;
		String line = "";
		ArrayList<ArrayList<Attr>> Attrs = new ArrayList<>();

		try {
			// Read the first line as header and parse the Attr
			br = new BufferedReader(new FileReader(fileLocation));
			String[] header = br.readLine().split(",");

			// Read each line
			while ((line = br.readLine()) != null) {

				// Use comma as separator
				String[] values = line.split(",");

				// Parse each value, assign it to the specified Attr and
				// add the Attr to the list
				ArrayList<Attr> row = new ArrayList<>();
				for (int i = 0; i < values.length; i++)
					row.add(new Attr(header[i], Integer
							.parseInt(values[i])));

				Attrs.add(row);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Finally, try to close reader
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return Attrs;
		}
	}
}