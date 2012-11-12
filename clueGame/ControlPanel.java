package clueGame;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class ControlPanel extends JPanel {

	public ControlPanel(Board b) {
		TextBox currentPlayer = new TextBox("Whose Turn?", "");
		JButton nextTurn = new JButton("Next Player");
		
		JButton makeAccusation = new JButton("Make Accusataion");
		
		TextBox dice = new TextBox("Roll", "");
		dice.setBorder(new TitledBorder(new EtchedBorder(), "Die"));
		TextBox suggestion = new TextBox("Guess", "");
		suggestion.setBorder(new TitledBorder(new EtchedBorder(), "Suggestion"));
		TextBox response = new TextBox("Response", "");
		response.setBorder(new TitledBorder(new EtchedBorder(), "suggestion Result"));
		
		add(currentPlayer);
		add(nextTurn);
		add(makeAccusation);
		add(dice);
		add(suggestion);
		add(response);
		setLayout(new GridLayout(2, 3));
	}
	
	public class TextBox extends JPanel {
		
		public TextBox(String label, String content) {
			JTextField textBox = new JTextField(content);
			add(new JLabel(label));
			add(textBox);
			
		}
		
	}
	
	
}
