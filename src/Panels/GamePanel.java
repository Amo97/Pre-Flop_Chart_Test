package Panels;

import Cards.Card;
import Core.Event;
import Core.FileLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    private JPanel firstCardInHand;
    private JPanel secondCardInHand;
    private JPanel cardInHandPanel;
    private JButton foldButton;
    private JButton callButton;
    private JButton raiseButton;
    private JButton allInButton;
    private JLabel scoreLabel;
    private String selectedType;
    private int answers, answersGood;
    private String selectedRadioPosition;
    private String selectedRadioRaise;
    private String selectedRadioCall;
    private List<Event> events;
    private int lastEvent = -1;

    GamePanel(String selectedType, String selectedRadioPosition, String selectedRadioRaise, String selectedRadioCall){
        this.selectedType = selectedType;
        this.selectedRadioPosition = selectedRadioPosition;
        this.selectedRadioRaise = selectedRadioRaise;
        this.selectedRadioCall = selectedRadioCall;
        answers = 0;
        answersGood = 0;

        setLayout(new BorderLayout());

        cardInHandPanel = new JPanel();
        cardInHandPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        setUpCards();
        add(cardInHandPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        foldButton = new JButton("Fold");
        foldButton.setPreferredSize(new Dimension(150, 30));
        foldButton.setBackground(new Color(255,55,33,255));
        callButton = new JButton("Call");
        callButton.setBackground(new Color(16,255,29,255));
        callButton.setPreferredSize(new Dimension(150,30));
        raiseButton = new JButton("Raise");
        raiseButton.setBackground(new Color(255,253,32,255));
        raiseButton.setPreferredSize(new Dimension(150,30 ));
        allInButton = new JButton("All in");
        allInButton.setBackground(new Color(30, 117, 216,255));
        allInButton.setPreferredSize(new Dimension(150,30 ));
        foldButton.addActionListener(this);
        raiseButton.addActionListener(this);
        callButton.addActionListener(this);
        allInButton.addActionListener(this);
        scoreLabel = new JLabel(answersGood + " / " + answers);
        scoreLabel.setFont(new Font("Serif", Font.PLAIN, 16));
        buttonsPanel.add(foldButton);
        buttonsPanel.add(callButton);
        buttonsPanel.add(raiseButton);
        buttonsPanel.add(allInButton);
        buttonsPanel.add(scoreLabel);
        add(buttonsPanel, BorderLayout.SOUTH);

        invalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(source instanceof JButton){
            if(source == raiseButton){
                answers++;
                if(events.get(lastEvent).checkDecision("Raise"))
                    answersGood++;
                setAgainCards();
            }else if(source == callButton){
                answers++;
                if(events.get(lastEvent).checkDecision("Check"))
                    answersGood++;
                setAgainCards();
            }else if(source == foldButton){
                answers++;
                if(events.get(lastEvent).checkDecision("Fold"))
                    answersGood++;
                setAgainCards();
            }else if(source == allInButton){
                answers++;
                if(events.get(lastEvent).checkDecision("All in"))
                    answersGood++;
                setAgainCards();
            }
            scoreLabel.setText(answersGood + " / " + answers);
            this.revalidate();

        }
    }

    private void setUpCards(){
        FileLoader loader = new FileLoader();
        events = loader.loadFile(selectedType+ "/" + selectedRadioRaise + "/" + selectedRadioCall + "/" + selectedRadioPosition +".txt");
        if(events.size() == 0){
            JOptionPane.showConfirmDialog(this, "Kombinacja: " + selectedType+ "/" + selectedRadioRaise + "/" + selectedRadioCall + "/" + selectedRadioPosition +".txt" + " nie może zostać załadowana", "Error: Błędna kombinacja", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
        }
        setAgainCards();
    }

    private void setAgainCards(){
        if(firstCardInHand != null) {
            cardInHandPanel.remove(firstCardInHand);
            cardInHandPanel.remove(secondCardInHand);
        }

        String colors[] = {"C","D","H","S"};
        Random random = new Random();
        int pom;
        do {
            pom = random.nextInt(events.size());
        }while(pom == lastEvent);
        Card card1, card2;
        String c1 = events.get(pom).getCard1();
        String c2 = events.get(pom).getCard2();
        String color;
        if(events.get(pom).isSame()){
            color = colors[random.nextInt(3)];
            card1 = new Card(c1,color);
            card2 = new Card(c2, color);
        }else{
            color = colors[random.nextInt(3)];
            card1 = new Card(c1,color);
            String newColor;
            do{
                newColor = colors[random.nextInt(3)];
            }while(newColor.equals(color));
            card2 = new Card(c2, newColor);
        }
        firstCardInHand = new CardPanel(card1.getImage());
        secondCardInHand = new CardPanel(card2.getImage());
        lastEvent = pom;
        System.out.println(lastEvent);
        cardInHandPanel.add(firstCardInHand);
        cardInHandPanel.add(secondCardInHand);
    }
}
