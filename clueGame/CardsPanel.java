package clueGame;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class CardsPanel extends JPanel {
	
	CardPanel card1, card2, card3;
	
	public CardsPanel(Board b) {
		card1 = new CardPanel(b.getHumanPlayer().getCards()[0].getName());
		card2 = new CardPanel(b.getHumanPlayer().getCards()[1].getName());
		card3 = new CardPanel(b.getHumanPlayer().getCards()[2].getName());
		setLayout(new GridLayout(0, 1));
		setName("My Cards");
		add(card1);
		add(card2);
		add(card3);
		add(new FillerPanel());
		add(new FillerPanel());
		add(new FillerPanel());
		card1.setBorder(new TitledBorder(new EtchedBorder(), "Card 1"));
		card2.setBorder(new TitledBorder(new EtchedBorder(), "Card 2"));
		card3.setBorder(new TitledBorder(new EtchedBorder(), "Card 3"));
		
		
	}
	
	public class CardPanel extends JPanel {
		
		public CardPanel(String name) {
			JTextField cardName = new JTextField(name);
			add(cardName);
			cardName.setEditable(false);
			
		}
		
	}
	
	public class FillerPanel extends JPanel {
		
		public FillerPanel() {
			
		}
	}
	
}
