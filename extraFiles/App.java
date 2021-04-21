package modelChecker;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.stream.Collectors;

import modelChecker.controller.KripkeStructure;
import modelChecker.controller.ctlFormula;
import modelChecker.model.Model;
import modelChecker.model.State;
public class App {
	
	private static KripkeStructure _kripke;
	
	
	public static String readFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
        String x =  reader.lines().collect(Collectors.joining(System.lineSeparator()));
        reader.close();
        return x;
    }
	
	
	public static String GetMessage(boolean isSatisfy, String expression, String stateID)
    {
        String message = String.format("Property %s %s in state %s"
            , expression
            , isSatisfy ? "holds" : "does not hold"
            , stateID);

        return message;
    }
	
	private static String cleanTextContent(String text) 
    {
        // strips off all non-ASCII characters
        text = text.replaceAll("[^\\x00-\\x7F]", "");
 
        // erases all the ASCII control characters
        text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
         
        // removes non-printable characters from Unicode
        text = text.replaceAll("\\p{C}", "");
 
        return text.trim();
    }
	
	
	public static void main(String[] args) throws Exception {
	System.out.println("Hello World!");
	
	
	System.out.println(args[0]);
	System.out.println(new File(".").getAbsolutePath());
	
	try {
	//Read file
	String path = "/Users/rohitlode/eclipse-workspace/ModelChecker/src/modelChecker/T2.2.txt";
	String exp = readFile(path);
	
	KripkeStructure kripke = new KripkeStructure(cleanTextContent(exp));
	_kripke = kripke;
	
	if (_kripke == null)
    {
        System.out.println("Please load Kripke model" + "Error");
        return;
    }
	
	
	String originalExpression = "AFq";
    String expression = originalExpression.replaceAll("\\s", "");
//    System.out.println("Converted exp "+expression);
    String checkedStateID = "s1";
    
    State checkedState = new State(checkedStateID);

    ctlFormula ctlFormula = new ctlFormula(expression, checkedState, _kripke);
    boolean isSatisfy = ctlFormula.IsSatisfy();
    String message = GetMessage(isSatisfy, originalExpression, checkedStateID);
    System.out.println(message);
    
	
	}catch(Exception e) {
		e.printStackTrace();
	}
	}
}
