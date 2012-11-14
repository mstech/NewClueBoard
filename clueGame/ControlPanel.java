package clueGame;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class ControlPanel extends JPanel {

	private Board b;
	private TextBox currentPlayer, dice, suggestion, response;
	private JButton nextTurn, makeAccusation;

	public ControlPanel(Board b) {
		this.b = b;
		currentPlayer = new TextBox("Whose Turn?", "");
		nextTurn = new JButton("Next Player");
		nextTurn.addActionListener(new NextPlayerButtonListener());
		makeAccusation = new JButton("Make Accusataion");

		dice = new TextBox("Roll", "");
		dice.setBorder(new TitledBorder(new EtchedBorder(), "Die"));
		suggestion = new TextBox("Guess", "");
		suggestion.setBorder(new TitledBorder(new EtchedBorder(), "Suggestion"));
		response = new TextBox("Response", "");
		response.setBorder(new TitledBorder(new EtchedBorder(), "suggestion Result"));

		add(currentPlayer);
		add(nextTurn);
		add(makeAccusation);
		add(dice);
		add(suggestion);
		add(response);
		setLayout(new GridLayout(2, 3));



	}

	public class NextPlayerButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			if(b.getCurrentPlayer() == null){
				b.setCurrentPlayer(b.getHumanPlayer());
			}
			
			
			if(b.getCurrentPlayer().isHuman()) {
				
				HumanPlayer hp = (HumanPlayer) b.getCurrentPlayer();
				if(!(hp.isEndTurn())) {
					JOptionPane.showMessageDialog(new JFrame(), "You must finish your turn!", "Error", JOptionPane.ERROR_MESSAGE);

				}
				
				 else{
//						b.nextPlayer();
						currentPlayer.setTextBox(b.getCurrentPlayer().getName());
						b.rollDice();
						dice.setTextBox(Integer.toString(b.getDiceRoll()));
						ArrayList<String> suggestionList = b.drawTargets();
						if(suggestionList == null) {
							suggestion.setTextBox("");
							response.setTextBox("");

						}
						else if(suggestionList.size() > 3) {
							suggestion.setTextBox(suggestionList.get(0) + " " + suggestionList.get(1) + " " + suggestionList.get(2));
							response.setTextBox(suggestionList.get(3));
						}
						b.nextPlayer();

					}
				
				
			} else{
				
				
				
				b.nextPlayer();
				currentPlayer.setTextBox(b.getCurrentPlayer().getName());
				b.rollDice();
				dice.setTextBox(Integer.toString(b.getDiceRoll()));
				ArrayList<String> suggestionList = b.drawTargets();
				if(suggestionList == null) {
					suggestion.setTextBox("");
					response.setTextBox("");

				}
				else if(suggestionList.size() > 3) {
					suggestion.setTextBox(suggestionList.get(0) + " " + suggestionList.get(1) + " " + suggestionList.get(2));
					response.setTextBox(suggestionList.get(3));
				}

			}
		}
	}


	public class TextBox extends JPanel {

		private JTextField textBox;

		public TextBox(String label, String content) {
			textBox = new JTextField(content, 10);
			add(new JLabel(label));
			add(textBox);
			textBox.setEditable(false);
		}

		public void setTextBox(String s) {
			textBox.setText(s);
		}



	}




}
