package modelChecker.Test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import modelChecker.controller.KripkeStructure;
import modelChecker.controller.ctlFormula;
import modelChecker.model.State;

public class ModelCheckerTest {
	
	private String testPath = "/Users/rohitlode/eclipse-workspace/ModelChecker/src/modelChecker/Test/Test_Files/";
	private KripkeStructure kripke;
	
	
	
//	
//	@Test
//	public void Model1_TestCases() throws Exception
//    {
//		System.out.println("Test Path :"+testPath);
//        String modelFileName = testPath+"Model 1.txt";
//        System.out.println("Model file Name" +modelFileName);
//        String testCasesFileName = testPath+ "Model 1 - Test Formulas.txt";
//        TestModel(modelFileName, testCasesFileName);
//    }
	
	@Test
	public void Model2_TestCases() throws Exception
    {
		System.out.println("Test Path :"+testPath);
        String modelFileName = testPath+"Model 2.txt";
        System.out.println("Model file Name" +modelFileName);
        String testCasesFileName = testPath+ "Model 2 - Test Formulas.txt";
        TestModel(modelFileName, testCasesFileName);
    }
	
//	@Test
//	public void Model3_TestCases() throws Exception
//    {
//		System.out.println("Test Path :"+testPath);
//        String modelFileName = testPath+"Model 3.txt";
//        System.out.println("Model file Name" +modelFileName);
//        String testCasesFileName = testPath+ "Model 3 - Test Formulas.txt";
//        TestModel(modelFileName, testCasesFileName);
//    }
	
//	@Test
//	public void Model4_TestCases() throws Exception
//    {
//		System.out.println("Test Path :"+testPath);
//        String modelFileName = testPath+"Model 4.txt";
//        System.out.println("Model file Name" +modelFileName);
//        String testCasesFileName = testPath+ "Model 4 - Test Formulas.txt";
//        TestModel(modelFileName, testCasesFileName);
//    }
//	
//	@Test
//	public void Model5_TestCases() throws Exception
//    {
//		System.out.println("Test Path :"+testPath);
//        String modelFileName = testPath+"Model 5.txt";
//        System.out.println("Model file Name" +modelFileName);
//        String testCasesFileName = testPath+ "Model 5 - Test Formulas.txt";
//        TestModel(modelFileName, testCasesFileName);
//    }
//	
//	@Test
//	public void Model6_TestCases() throws Exception
//    {
//		System.out.println("Test Path :"+testPath);
//        String modelFileName = testPath+"Model 6.txt";
//        System.out.println("Model file Name" +modelFileName);
//        String testCasesFileName = testPath+ "Model 6 - Test Formulas.txt";
//        TestModel(modelFileName, testCasesFileName);
//    }
//	@Test
//	public void Model7_TestCases() throws Exception
//    {
//		System.out.println("Test Path :"+testPath);
//        String modelFileName = testPath+"Model 7.txt";
//        System.out.println("Model file Name" +modelFileName);
//        String testCasesFileName = testPath+ "Model 7 - Test Formulas.txt";
//        TestModel(modelFileName, testCasesFileName);
//    }
	
	
	
	
	public static String readFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
        String x =  reader.lines().collect(Collectors.joining(System.lineSeparator()));
//        System.out.println("*** "+x);
        reader.close();
        return x;
    }
	
	
	public boolean check(List<String> expressions) throws Exception {
		System.out.println(expressions.size());
		for (String expression: expressions)
        {
        	System.out.println("**************** EXPRESSION **************");
            String[] parsedExpression = expression.split(";");
            String stateID = parsedExpression[0];
            String formula = parsedExpression[1].replace("\\s", "");
            System.out.println(parsedExpression[2]);
            boolean expectedResult = Boolean.parseBoolean(parsedExpression[2]);

            ctlFormula ctlFormula = new ctlFormula(formula, new State(stateID), kripke);
            boolean checkResult = ctlFormula.IsSatisfy();
            System.out.println();
            System.out.println("****** Each CASE ******* "+stateID);
            System.out.println(formula+" "+expectedResult+" "+checkResult);
            if(expectedResult != checkResult) {
            	return false;
            }
            
//            return expectedResult == checkResult;
        }
		
		return true;
		
	}
	
	public void TestModel(String modelFileName, String testCasesFileName) throws Exception
    {
        String kripkeString = readFile(modelFileName);
		kripke = new KripkeStructure(cleanTextContent(kripkeString));
        List<String> expressions = GetTestFileExpressions(cleanTextContent(testCasesFileName));
        System.out.println("Expresssions "+expressions.toString() +" "+expressions.size());
        assertEquals(true, check(expressions));
    }

    private String cleanTextContent(String text) {
		// TODO Auto-generated method stub
    	// strips off all non-ASCII characters
        text = text.replaceAll("[^\\x00-\\x7F]", "");
 
        // erases all the ASCII control characters
        text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
         
        // removes non-printable characters from Unicode
        text = text.replaceAll("\\p{C}", "");
 
        return text.trim();
	}

	public List<String> GetTestFileExpressions(String testFileName) throws IOException
    {
        List<String> expressions = new ArrayList<String>();
        return new ArrayList<String>(Arrays.asList(readFile(testFileName).split("\\r?\\n")));
    }
}

