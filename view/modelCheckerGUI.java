package modelChecker.view;

import java.awt.*;        // Using AWT container and component classes
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import modelChecker.controller.KripkeStructure;
import modelChecker.controller.ctlFormula;
import modelChecker.model.State; 

public class modelCheckerGUI extends JFrame{
	private JTextField ctlFormula;
	private JLabel modelLabel;
	private JTextArea resultArea, modelArea;
	private JComboBox<String> jc;
	private JFrame jf;
	private BufferedReader orginalBI;
	private File path;
	
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
	
	private void ResetModel() {
		modelArea.setText("");
		modelLabel.setText("Model");
		if(jc.getSelectedIndex() != -1) {
			DefaultComboBoxModel theModel = (DefaultComboBoxModel)jc.getModel();
			theModel.removeAllElements();
			System.out.println("After remivea: "+jc.getSelectedIndex());
		}	
	}
	
	class MenuActionListener implements ActionListener {
		  public void actionPerformed(ActionEvent e) {
			ResetModel();
		    JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setFileFilter(new FileNameExtensionFilter("TEXT FILES", "txt", "text"));
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int returnValue  = chooser.showOpenDialog(jf);
            if(returnValue == JFileChooser.APPROVE_OPTION) {
            	try {
            		orginalBI = new BufferedReader(new FileReader(chooser.getSelectedFile()));
            		System.out.println("Selected File"+chooser.getSelectedFile());
            		path = chooser.getSelectedFile();
            		
            		try {
            			if(path == null) {
        		    		String message  = "Please upload a Model File!";
        					JOptionPane.showMessageDialog(new JFrame(), message, "Dialog",
        					        JOptionPane.ERROR_MESSAGE);
        		    	}
        		    	System.out.println("PATH: "+path.getAbsolutePath());
        		    	String file = readFile(path.getAbsolutePath());
        				KripkeStructure kripke = new KripkeStructure(cleanTextContent(file));
        				_kripke = kripke;
        				
        				if (_kripke == null)
        			    {
        			        Exception kf =  new Exception("Please load Kripke model");
        					throw kf;
        			    }else {
        			    	ResetModel();
        			    	for(String s: _kripke.getStates()) {
        			    		jc.addItem(s);
        			    	}
        			    	String modelName = path.getAbsolutePath().substring(path.getAbsolutePath().lastIndexOf('M'));
        			    	modelLabel.setText(modelName);
        			    	modelArea.setText(_kripke.toString());
        			    }
            			
            			
            		}catch(Exception kse) {
            			kse.printStackTrace();
            			JOptionPane.showMessageDialog(new JFrame(), kse.getMessage(), "Dialog",
        				        JOptionPane.ERROR_MESSAGE);
            		}
            		
            		
            	}catch(IOException e1) {
            		e1.printStackTrace();
            		JOptionPane.showMessageDialog(new JFrame(), e1.getMessage(), "Dialog",
    				        JOptionPane.ERROR_MESSAGE);
            	}
            	
            }

		  }
		  
		}
	
	class CheckActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			resultArea.setText("");
		    System.out.println("Clicked: " + e.getActionCommand()+" "+ctlFormula.getText());
		    try {
		    	if (_kripke == null)
			    {
			        Exception kf =  new Exception("Please load Kripke model");
					throw kf;
			    }
				if(ctlFormula.getText().isEmpty()) {
					Exception cf =  new Exception("Please enter CTL formula!");
					throw cf;
					
				}
				String originalExpression = ctlFormula.getText();
			    String expression = originalExpression.replaceAll("\\s", "");
			    System.out.println("State  "+jc.getSelectedItem().toString());
			    String checkedStateID = jc.getSelectedItem().toString();
			    
			    State checkedState = new State(checkedStateID);
			    
			    ctlFormula ctlFormula = new ctlFormula(expression, checkedState, _kripke);
			    boolean isSatisfy = ctlFormula.IsSatisfy();
			    String message = GetMessage(isSatisfy, originalExpression, checkedStateID);
			    resultArea.append(message);
			    System.out.println(message);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				JOptionPane.showMessageDialog(new JFrame(), e1.getMessage(), "Dialog",
				        JOptionPane.ERROR_MESSAGE);
			}

		  }
	}
	
	public modelCheckerGUI() {
		// TODO Auto-generated constructor stub
		jf = new JFrame();
		jf.setTitle("CTL Model Checker");
        jf.setSize(new Dimension(500,500));
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem upload =  new JMenuItem("upload");
        upload.addActionListener(new MenuActionListener());
        menu.add(upload);
        bar.add(menu);
        
        //Here goes your code
        JPanel p= (JPanel) getContentPane();
        p.setLayout(new GridLayout(1,2)); //set your own layout
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        
        JPanel mjp1 =  new JPanel();
        mjp1.setLayout(new GridLayout(2,1));
        mjp1.setBorder(new EmptyBorder(10, 10, 10, 10));
        
          jc =  new JComboBox<String>();
        
        //top
        JPanel jp11 = new JPanel();
        jp11.setLayout(new GridLayout(3,2));
        jp11.add(new JLabel("State"));
        jp11.add(jc);
        jp11.add(new JLabel("CTL Fromula"));
        ctlFormula =  new JTextField(10);
        jp11.add(ctlFormula);
        jp11.add(new JLabel(""));
        JButton button = new JButton("Check!");
        button.addActionListener(new CheckActionListener());
        button.setBackground(Color.black);
        button.setOpaque(true);
        jp11.add(button);
        
        //Bottom
        JPanel jp22 = new JPanel();
        jp22.setLayout(new FlowLayout());
        jp22.add(new JLabel("Result"));
        resultArea = new JTextArea(10, 20);
        resultArea.setEditable(false);
        jp22.add(resultArea);

        
        mjp1.add(jp11);
        mjp1.add(jp22);
        
        JPanel mjp2 =  new JPanel(new FlowLayout());
        mjp2.setBorder(new EmptyBorder(10, 10, 10, 10));
        modelLabel = new JLabel("Model");
        mjp2.add(modelLabel);
        
        JPanel jp2 = new JPanel();
        modelArea = new JTextArea(20, 20);
        modelArea.setEditable(false);
        jp2.add(modelArea);
        JScrollPane scrollPane = new JScrollPane(jp2);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(400,400));
//        scrollPane.setBounds(30, 30, 30, 50);
        
        
        mjp2.add(scrollPane);
        p.add(mjp1); //add panel with blue border
        p.add(mjp2);//add panel with green border
        
       
      
	      
      jf.setPreferredSize(new Dimension(800, 500));
      jf.setJMenuBar(bar);
      jf.setContentPane(p);
      jf.pack();
      jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jf.setLocationRelativeTo(null);
      jf.setVisible(true);
	
		
		
		
	}
	
	public static void main(String [] args) {
		modelCheckerGUI gui =   new modelCheckerGUI();
		
		
	}
	

}
