package Panels;

import Cards.Card;
import Core.Event;
import Core.FileLoader;
import Core.FileSaver;
import Frames.WrongAnswersFrame;

import javax.swing.*;
import javax.swing.border.Border;
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
    private JButton wrongAnswersButton;
    private JLabel scoreLabel;
    private String selectedType;
    private int answers, answersGood;
    private String selectedRadioPosition;
    private String selectedRadioRaise;
    private String selectedRadioCall;
    private List<Event> events;
    private List<Event> pomEvents;
    private int lastEvent = -1;
    private String smallerSpaceThan10 = "                        ";
    private String smallerSpaceThan100 = "                      ";
    private String smallerSpaceThan1000 = "                    ";
    private FileSaver fileSaver;
    private Card card1, card2;

    GamePanel(String selectedType, String selectedRadioPosition, String selectedRadioRaise, String selectedRadioCall){
        this.selectedType = selectedType;
        this.selectedRadioPosition = selectedRadioPosition;
        this.selectedRadioRaise = selectedRadioRaise;
        this.selectedRadioCall = selectedRadioCall;
        answers = 0;
        answersGood = 0;
        fileSaver = new FileSaver();
        fileSaver.clearFile();
        wrongAnswersButton = new JButton("Show wrong answers");
        wrongAnswersButton.addActionListener(this);
        setLayout(new BorderLayout());

        cardInHandPanel = new JPanel();
        cardInHandPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        setUpCards();
        add(cardInHandPanel, BorderLayout.CENTER);
        if(events.size() != 0) {
            JPanel buttonsPanel = new JPanel();
            JPanel pomPanel = new JPanel();
            pomPanel.setLayout(new BorderLayout());
            buttonsPanel.setLayout(new FlowLayout(10,30,10));
            foldButton = new JButton("Fold");
            foldButton.setPreferredSize(new Dimension(150, 30));
            callButton = new JButton("Call");
            callButton.setPreferredSize(new Dimension(150, 30));
            raiseButton = new JButton("Raise");
            raiseButton.setPreferredSize(new Dimension(150, 30));
            allInButton = new JButton("All in");
            allInButton.setPreferredSize(new Dimension(150, 30));
            foldButton.addActionListener(this);
            raiseButton.addActionListener(this);
            callButton.addActionListener(this);
            allInButton.addActionListener(this);
            scoreLabel = new JLabel(smallerSpaceThan10 + answersGood + " / " + answers);
            scoreLabel.setFont(new Font("Serif", Font.PLAIN, 16));
            JLabel textScoreLabel = new JLabel("Correct answers / All answers");
            textScoreLabel.setFont(new Font("Serif", Font.PLAIN, 16));
            buttonsPanel.add(raiseButton);
            buttonsPanel.add(foldButton);
            buttonsPanel.add(callButton);
            buttonsPanel.add(allInButton);
            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            infoPanel.add(textScoreLabel);
            infoPanel.add(wrongAnswersButton);
            pomPanel.add(buttonsPanel, BorderLayout.NORTH);
            pomPanel.add(infoPanel, BorderLayout.CENTER);
            pomPanel.add(scoreLabel, BorderLayout.SOUTH);
            add(pomPanel, BorderLayout.SOUTH);
        }
        invalidate();
    }

    // TODO: przy 167 się zacina, napraw to

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(source instanceof JButton){
            if(source == raiseButton){
                answers++;
                if(pomEvents.get(lastEvent).checkDecision("Raise"))
                    answersGood++;
                else
                    fileSaver.saveBadAnswer(card1, card2, "Raise", pomEvents.get(lastEvent).getCorrectChoose());
                setAgainCards();
            }else if(source == callButton){
                answers++;
                if(pomEvents.get(lastEvent).checkDecision("Call"))
                    answersGood++;
                else
                    fileSaver.saveBadAnswer(card1, card2, "Call", pomEvents.get(lastEvent).getCorrectChoose());
                setAgainCards();
            }else if(source == foldButton){
                answers++;
                if(pomEvents.get(lastEvent).checkDecision("Fold"))
                    answersGood++;
                else
                    fileSaver.saveBadAnswer(card1, card2, "Fold", pomEvents.get(lastEvent).getCorrectChoose());
                setAgainCards();
            }else if(source == allInButton){
                answers++;
                if(pomEvents.get(lastEvent).checkDecision("All in"))
                    answersGood++;
                else
                    fileSaver.saveBadAnswer(card1, card2, "All in", pomEvents.get(lastEvent).getCorrectChoose());
                setAgainCards();
            }else if(source == wrongAnswersButton){
                new WrongAnswersFrame();
            }
            if(answersGood < 10)
                scoreLabel.setText(smallerSpaceThan10 +answersGood + " / " + answers);
            else if(answersGood < 100)
                scoreLabel.setText(smallerSpaceThan100 +answersGood + " / " + answers);
            else if(answersGood < 1000)
                scoreLabel.setText(smallerSpaceThan1000 +answersGood + " / " + answers);
            pomEvents.remove(lastEvent);
            this.revalidate();

        }
    }

    private void setUpCards(){
        FileLoader loader = new FileLoader();
        events = loader.loadFile(selectedType+ "/" + selectedRadioRaise + "/" + selectedRadioCall + "/" + selectedRadioPosition +".txt");
        pomEvents = events;
        if(events.size() == 0){
            JOptionPane.showConfirmDialog(this, "Kombinacja: " + selectedType+ "/" + selectedRadioRaise + "/" + selectedRadioCall + "/" + selectedRadioPosition +".txt" + " nie może zostać załadowana", "Error: Błędna kombinacja", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
        }else{
            setAgainCards();
        }

    }

    private void setAgainCards(){
        if(firstCardInHand != null) {
            cardInHandPanel.remove(firstCardInHand);
            cardInHandPanel.remove(secondCardInHand);
        }
        if(pomEvents.size() == 0){
            pomEvents = events;
        }

        String []colors = {"C","D","H","S"};
        Random random = new Random();
        int pom;
        do {
            pom = random.nextInt(pomEvents.size()-1);
            if(pom < 0 )
                pom = 0;
        }while(pom == lastEvent);
        String c1 = pomEvents.get(pom).getCard1();
        String c2 = pomEvents.get(pom).getCard2();
        String color;
        if(pomEvents.get(pom).isSame()){
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
        cardInHandPanel.add(firstCardInHand);
        cardInHandPanel.add(secondCardInHand);
    }
}
