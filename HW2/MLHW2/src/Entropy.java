import java.util.ArrayList;

/**
 * @author nagabharan
 * This class implements the heuristics for selecting
 * the attributes.
 */
public class Entropy {
	
	//Computes the entropy of a data set.
	public double calculateEntropy(ArrayList<ArrayList<Attr>> data) {
		// Assuming there are two values 0,1 possible, count how many values are
		// in 0 and 1
		double[] valueCount = new double[2];
		for (ArrayList<Attr> Attr : data)
			valueCount[Attr.get(Attr.size() - 1).value]++;

		// Entropy = sum of K/N(log(K/N)) = (sum of KlogK) / N - logN
		double entropy = 0;
		for (int j = 0; j < valueCount.length; j++)
			if ((int) valueCount[j] != 0)
				entropy -= valueCount[j]
						* (Math.log(valueCount[j]) / Math.log(2));

		entropy /= data.size();
		return entropy + (Math.log(data.size()) / Math.log(2));
	}

	//Calculates the variance impurity 
	public double calculateVarianceImpurity(ArrayList<ArrayList<Attr>> data) {

		double[] K = new double[2];
		for (ArrayList<Attr> Attr : data)
			K[Attr.get(Attr.size() - 1).value]++;
		double impurity = 1;
		for (int i = 0; i < K.length; i++)
			impurity *= K[i];

		return impurity / (Math.pow(data.size(), K.length));
	}

	//Computes the information gain for subset of data
	public double infoGain(double currentEntropy,
			ArrayList<Double> subsetEntropies,
			ArrayList<Integer> subsetSize, double totalExamples) {
	
		// Compute gain as root entropy - (sum of (K/N(entropy of K))
		double gain = currentEntropy;
		for (int j = 0; j < subsetEntropies.size(); j++)
			gain -= (subsetSize.get(j) / totalExamples)
					* subsetEntropies.get(j);

		return gain;
	}
}