package mathInterface;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DoubleGene;
import org.jgap.impl.IntegerGene;

import processing.core.PImage;



/*
 * class that is responsible for translating an individual of the population 
 * of chromosomes into feedback variables 
 *  
 */
public class ChromosomeToFeedbackManifester  {
	public static PImage DEFAULT_CORRECT_VERIFICATION_IMAGE=null;
	public static PImage DEFAULT_INCORRECT_VERIFICATION_IMAGE=null;
	static ProcessingApplication processing;
	static ChromosomeToFeedbackManifester theInstance;
	private static IChromosome curr_genotype;
	private static Feedback curr_phenotype;

   public String currFeedback(){
	   return curr_phenotype.toString();
   }

	private ChromosomeToFeedbackManifester(){
		processing=(ProcessingApplication) ProcessingApplication.getInstance();
		loadImages();
	}
	
	private void loadImages() {
		ProcessingApplication processing=ProcessingApplication.getInstance();
		DEFAULT_INCORRECT_VERIFICATION_IMAGE=processing.loadImage("C:/Users/root960/Desktop/emily/IAT813/project/applicationData/images/incorrect.png");
		DEFAULT_CORRECT_VERIFICATION_IMAGE=processing.loadImage("C:/Users/root960/Desktop/emily/IAT813/project/applicationData/images/correct.png");
	}
	
	public static IChromosome getCurrentGenotype(){
		return curr_genotype;
	}


	public static ChromosomeToFeedbackManifester getInstance(){
		if(theInstance==null)
			theInstance=new ChromosomeToFeedbackManifester();
		return theInstance;
	}

	public void setCurrentFeedbackChromosome(IChromosome iChromosome){
		this.curr_genotype=iChromosome;
		this.curr_phenotype=new Feedback();
		
		setStaticFeedbackParameters(curr_phenotype,curr_genotype);
	}


	/* caches the values of the feedback parametes, with respect to the current chromosome (individual) being evaluated */
	private static void setStaticFeedbackParameters(Feedback phenotype, IChromosome genotype){
		updateFeedbackDelay(phenotype, genotype);

		updateVerificationParameters(phenotype, genotype);
		updateElaborationParameters(phenotype, genotype);
		updateDirectiveFeedbackParameters(phenotype, genotype);

		updateAllowingResubmission(phenotype, genotype);
		
		
	}

	
	


	private static void updateAllowingResubmission(Feedback phenotype, IChromosome genotype){
		phenotype.updateAllowingResubmission(getDoubleAllelle(FeedbackGeneType.P_ALLOW_RESUBMIT, genotype));

	}

	private static void updateDirectiveFeedbackParameters(Feedback phenotype, IChromosome genotype) {
		phenotype.updateDirectiveFeedbackParameters(getDoubleAllelle(FeedbackGeneType.P_DIRECTIVE, genotype));
		/* types of directive feedback */
		//provide the correct response
		phenotype.updateProvideCorrectAnswerParameters(getDoubleAllelle(FeedbackGeneType.P_CORRECT_RESPONSE,genotype),getIntegerAllelle(FeedbackGeneType.CORRECT_RESPONSE_DELAY, genotype));

		//highlight the errors in the childs solution

		phenotype.updateErrorFlagParameters(getDoubleAllelle(FeedbackGeneType.P_ERROR_FLAG, genotype), getIntegerAllelle(FeedbackGeneType.ERROR_FLAG_DELAY, genotype));

	}

	private static void updateElaborationParameters(Feedback phenotype, IChromosome genotype) {
		phenotype.updateElaborationParameters(getDoubleAllelle(FeedbackGeneType.P_ELABORATE, genotype));

		phenotype.updateAttributeIsolationParameters(getDoubleAllelle(FeedbackGeneType.P_ATTRIBUTE_ISOLATION, genotype), getIntegerAllelle(FeedbackGeneType.ATTRIBUTE_ISOLATION_DELAY, genotype));
	}


	private static void updateVerificationParameters(Feedback phenotype, IChromosome genotype) {

		phenotype.updateVerificationParameters(getIntegerAllelle(FeedbackGeneType.VERIFICATION_TYPE, genotype),getIntegerAllelle(FeedbackGeneType.VERIFICATION_MODALITY, genotype));
	}



	private static void updateFeedbackDelay(Feedback phenotype, IChromosome genotype) {
		phenotype.updateFeedbackDelay(getIntegerAllelle(FeedbackGeneType.FEEDBACK_DELAY, genotype));
	}



	/* methods for providing the feedback, assuming the variables were set */
	public void provideFeedback(MathProblem problem) {
		curr_phenotype.provideFeedback(problem);
		processing.mathProblemUI.setFeedbackScreens(getFeedbackScreens());
	}




	public List<DisplayScreen> getFeedbackScreens() {
		return curr_phenotype.getFeedbackScreens();
	}

	public void initializeFeedbackScreens() {
		curr_phenotype.initializeVerificationScreen(processing.makeEmptyScreenSizedToApplication());
		curr_phenotype.initializeCorrectAnswerScreen(processing.mathProblemUI.getAnswerScreenTransform());
		curr_phenotype.initializeErrorFlagScreen(processing.mathProblemUI.getAnswerScreenTransform());
		curr_phenotype.initializeAttributeIsolationScreen(processing.mathProblemUI.getProblemScreenTransform());
		curr_phenotype.initializeResubmitScreen(processing.makeEmptyScreenSizedToApplication());
	}


	public boolean acceptingResponse(){
		return !feedbackInProcess();
	}

	private boolean feedbackInProcess(){
		return curr_phenotype.feedbackInProcess();

	}

	
	private static double getDoubleAllelle(FeedbackGeneType pos,IChromosome genotype){
		return (Double)genotype.getGene(pos.ordinal()).getAllele();

	}

	private static int getIntegerAllelle(FeedbackGeneType pos, IChromosome genotype){
		return (Integer)genotype.getGene(pos.ordinal()).getAllele();

	}


	public void resetFeedbackScreens() {
		curr_phenotype.resetFeedbackScreens();
		
	}

	public int[] currFeedbackColor() {
		return curr_phenotype.getIdColor();
	}

	public static Feedback createFeedback(IChromosome a_subject) {
		Feedback result=new Feedback();
		
		setStaticFeedbackParameters(result,a_subject);
		return result;
	}





}
