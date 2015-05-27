package com.mantiso;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.trees.J48;
import weka.core.FastVector;
import weka.core.Instances;
 
public class J48Classifier {
	public static BufferedReader readDataFile(String filename) {
		BufferedReader inputReader = null;
 
		try {
			inputReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException ex) {
			System.err.println("File not found: " + filename);
		}
 
		return inputReader;
	}
 
	public static Evaluation classify(Classifier model,
			Instances trainingSet, Instances testingSet) throws Exception {
		Evaluation evaluation = new Evaluation(trainingSet);
 
		model.buildClassifier(trainingSet);
		evaluation.evaluateModel(model, testingSet);
 
		return evaluation;
	}
 
	public static double calculateAccuracy(FastVector predictions) {
		double correct = 0;
 
		for (int i = 0; i < predictions.size(); i++) {
			NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
			if (np.predicted() == np.actual()) {
				correct++;
			}
		}
 
		return 100 * correct / predictions.size();
	}
 
	public static Instances[] crossValidationSplit(Instances data, int numberOfFolds) {
		Instances[] split = new Instances[2];
 
	/*	for (int i = 0; i < numberOfFolds; i++) {
			split[0][i] = data.trainCV(numberOfFolds, i);
			split[1][i] = data.testCV(numberOfFolds, i);
		}*/
		int trainSize= (int)Math.round(data.numInstances()*0.8);
		int testSize= data.numInstances()-trainSize;
		split[0] = new Instances(data, 0, trainSize);
		split[1] = new Instances(data, trainSize,testSize);
		return split;
	}
 
	public String[] CalResult(BufferedReader filename) throws Exception {
		BufferedReader datafile = filename;
		String result="";
		String resultArray[] = null;
		Instances data = new Instances(datafile);
		data.setClassIndex(data.numAttributes() - 1);
		data.randomize(new java.util.Random());
		// Do 10-split cross validation
		Instances[] split =crossValidationSplit(data, 10);
 
		// Separate split into training and testing arrays
		Instances trainingSplits = split[0];
		Instances testingSplits = split[1];
 
		// Use a set of classifiers
		Classifier[] models = { 
				//new NaiveBayes() // a decision tree
			//	new IBk(20), 
				new J48()//decision table majority classifier
				//new DecisionStump() //one-level decision tree
		};
		//set up option
		String[] options = new String[1];
		options[0]="-C 0.25 -M 2";
		models[0].setOptions(options);
		// Run for each model
		for (int j = 0; j < models.length; j++) {
 
			// Collect every group of predictions for current model in a FastVector
			FastVector predictions = new FastVector();
 
			// For each training-testing split pair, train and test the classifier
		//	for (int i = 0; i < trainingSplits.length; i++) {
				Evaluation validation = classify(models[j], trainingSplits, testingSplits);
				result = validation.toSummaryString();
			//	result=result.replaceAll("\n", "</b>");
				resultArray = result.split("\n");
				predictions.appendElements(validation.predictions());
 
				// Uncomment to see the summary for each training-testing pair.
				//System.out.println(models[j].toString());
			//}
 
			// Calculate overall accuracy of current classifier on all splits
			double accuracy = calculateAccuracy(predictions);
 
			// Print current classifier's name and accuracy in a complicated,
			// but nice-looking way.
			/*result = "Accuracy of " + models[j].getClass().getSimpleName() + ": "
					+ String.format("%.2f%%", accuracy)
					+ "\n---------------------------------";*/
			
		}
		return resultArray;
 
	}
}