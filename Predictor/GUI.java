import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JTextField;

public class GUI extends JFrame{

    // User input textfield
    private JTextField input;

    // Textfield to display word predictions
    private JTextField output;
    
    /*  Constructs GUI interface objects
     * @param level - n-gram level (2, 3, or 5)
     * @param viterbi - viterbi decoding level (2 or 3)
     */
    public GUI(int level, int viterbi){
	JFrame frame = new JFrame();
	
	Container contentPane = frame.getContentPane();
	input = new JTextField();
	output = new JTextField();

	// Custom KeyListener used for word prediction depending on user input
	input.addKeyListener(new Listener(input,output,level,viterbi));

	contentPane.add(input, BorderLayout.CENTER);
	contentPane.add(output,BorderLayout.SOUTH);
	frame.pack();
	frame.setSize(500,500);
	frame.setVisible(true);
    }


    // If incorrect input into terminal
    static void printHelpMessage() {
	System.err.println( "The following parameters are available: " );
	System.err.println( " -n <number> : either 2, 3 or 4 (=bi-, tri-, or quad-gram) word prediction " );
	System.err.println( " -v <number> : either 2 (=bigram) or 3 (=trigram) letter-level viterbi decoding" );
    }

    public static void main(String[] args){

	// Stock input values if nothing input as values
	int level = 2;
	int viterbi = 2;
	int i = 0;
	while( i<args.length ) {

	    // Gram-level
	    if( args[i].equals( "-n" ) ) {
		i++;    
		if( i <args.length && Integer.parseInt(args[i])>1 && Integer.parseInt(args[i])<5) {
		    level = new Integer( args[i++] );
		} else {
		    printHelpMessage();
		    return;
		}

		// Viterbi decoding level
	    } else if( args[i].equals( "-v" ) ) {
		i++;
		if ( i<args.length && Integer.parseInt(args[i])>1 && Integer.parseInt(args[i])<4 ) {
		    viterbi = new Integer( args[i++] );
		} else {
		    printHelpMessage();
		    return;
		}
	    }
	}

	GUI test = new GUI(level, viterbi);
	System.out.println("Created GUI");
    }
   
}