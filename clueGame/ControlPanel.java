package clueGame;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import clueGame.Board.SuspectPanel;
import clueGame.Board.WeaponPanel;

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
		makeAccusation.addActionListener(new MakeAccusationButton());

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

	public TextBox getSuggestion() {
		return suggestion;
	}
	public TextBox getResponse() {
		return response;
	}

	public class NextPlayerButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {





			if(b.getCurrentPlayer() == null){
				b.setCurrentPlayer(b.getHumanPlayer());
			}

			if(b.getCurrentPlayer().isHuman()) {

				HumanPlayer hp = (HumanPlayer) b.getCurrentPlayer();
				if(!(hp.isEndTurn()) && !(b.isGameBegin())) {
					JOptionPane.showMessageDialog(new JFrame(), "You must finish your turn!", "Error", JOptionPane.ERROR_MESSAGE);

				}
				else {
					if(!(b.isGameBegin())) {
						b.nextPlayer();
					}
					b.setGameBegin(false);
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
					else if(suggestionList.size() == 3){
						suggestion.setTextBox(suggestionList.get(0) + " " + suggestionList.get(1) + " " + suggestionList.get(2));
						response.setTextBox("no new clue");
					}

				}



			} 

			else{



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

	public class MakeAccusationButton implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if(b.getCurrentPlayer() != null){
				if(b.getCurrentPlayer().isHuman()){
					
					HumanPlayer hp = (HumanPlayer) b.getCurrentPlayer();
					
					if(!(hp.isEndTurn())){
					HumanMakeAccusation accusation = new HumanMakeAccusation();
					accusation.setSize(200, 200);
					accusation.setVisible(true);						
					} else{
						JOptionPane.showMessageDialog(new JFrame(), "Its not your turn!", "Error", JOptionPane.ERROR_MESSAGE);
					}
					

				}
				else{
					JOptionPane.showMessageDialog(new JFrame(), "Its not your turn!", "Error", JOptionPane.ERROR_MESSAGE);
	
				}
			}
			else{
				JOptionPane.showMessageDialog(new JFrame(), "Its not your turn!", "Error", JOptionPane.ERROR_MESSAGE);
			}

		}





	}

	public class HumanMakeAccusation extends JDialog {
		private MakeAccusationRoomPanel roomPanel;
		private WeaponPanel weaponPanel;
		private SuspectPanel suspectPanel;

		public HumanMakeAccusation() {
			roomPanel = new MakeAccusationRoomPanel();
			weaponPanel = b.new WeaponPanel();
			suspectPanel = b.new SuspectPanel();
			add(roomPanel);
			add(weaponPanel);
			add(suspectPanel);
			JButton accusationButton = new JButton("Make Accusation");
			accusationButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if(b.getCurrentPlayer() != null){
						if(b.getCurrentPlayer().isHuman()){
							Card roomCard = b.getRoom().get(roomPanel.getRoomChoice().getSelectedItem());
							Card weaponCard = b.getWeapon().get(weaponPanel.getWeaponChoice().getSelectedItem());
							Card suspectCard = b.getSuspect().get(suspectPanel.getSuspectChoice().getSelectedItem());
							boolean winner = b.checkAccusation(suspectCard, roomCard, weaponCard);
							HumanPlayer hp = (HumanPlayer) b.getCurrentPlayer();
							hp.setEndTurn(true);
							if(winner){
								JOptionPane.showMessageDialog(new JFrame(), "You Win! Accusation: " + suspectCard.getName() + " in the " + roomCard.getName() + " with the " + weaponCard.getName() , "Game Over", JOptionPane.INFORMATION_MESSAGE);
							}
							else{
								JOptionPane.showMessageDialog(new JFrame(), "Incorrect Accusation, Sorry!" , "Bad Accusation", JOptionPane.INFORMATION_MESSAGE);
							}
							hp.setEndTurn(true);
							setVisible(false);
						}
					} else{
						
						JOptionPane.showMessageDialog(new JFrame(), "Its not your turn!", "Error", JOptionPane.ERROR_MESSAGE);
						
					}
				}

			}); 
			add(accusationButton);
			setLayout(new GridLayout(4, 0));
		}
	}

	public class MakeAccusationRoomPanel extends JPanel {

		private JComboBox<String> roomChoice;

		public MakeAccusationRoomPanel() {
			JLabel roomLabel = new JLabel("Room");
			roomChoice = new JComboBox<String>();
			Iterator<Entry<String, Card>> iter = b.getRoom().entrySet().iterator();
			while(iter.hasNext()) {
				roomChoice.addItem(iter.next().getValue().getName());
			}
			add(roomLabel);
			add(roomChoice);

		}

		public JComboBox<String> getRoomChoice(){
			return roomChoice;
		}

	}


	public class TextBox extends JPanel {

		private JTextField textBox;

		public TextBox(String label, String content) {
			textBox = new JTextField(content, 20);
			add(new JLabel(label));
			add(textBox);
			textBox.setEditable(false);
		}

		public void setTextBox(String s) {
			textBox.setText(s);
		}



	}




}
