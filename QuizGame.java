import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuizGame extends JFrame {

    private List<QuizQuestion> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;

    private JLabel questionLabel;
    private JRadioButton[] optionButtons;
    private ButtonGroup optionGroup;
    private JButton submitButton;
    private JLabel scoreLabel;

    // SHTESA
    private JProgressBar progressBar;
    private Timer timer;
    private int timeLeft = 10;
    private JLabel timerLabel;

    public QuizGame() {
        loadQuestions();
        Collections.shuffle(questions); // PYETJE RANDOM
        initializeUI();
        displayQuestion();
    }

    private void loadQuestions() {
        questions.add(new QuizQuestion(
                "Cili është kryeqyteti i Shqipërisë?",
                Arrays.asList("Tiranë", "Durrës", "Vlorë", "Shkodër"),
                0
        ));

        questions.add(new QuizQuestion(
                "Cila është kafsha më e madhe në botë?",
                Arrays.asList("Elefant", "Balena e kaltër", "Luan", "Ujku"),
                1
        ));

        questions.add(new QuizQuestion(
                "Sa është 5 + 3?",
                Arrays.asList("7", "8", "9", "6"),
                1
        ));

        questions.add(new QuizQuestion(
                "Cila ngjyrë është flamuri i Shqipërisë?",
                Arrays.asList("Kuq me të zezë", "E kuqe me të bardhë", "Blu me të verdhë", "E gjelbër me të kuqe"),
                0
        ));

        questions.add(new QuizQuestion(
                "Cili planet quhet Planeti i Kuq?",
                Arrays.asList("Toka", "Mars", "Jupiter", "Venus"),
                1
        ));
    }

    private void initializeUI() {
        setTitle("Quiz Game Shqip");
        setSize(650, 420);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // PANEL SIPËR
        JPanel topPanel = new JPanel(new GridLayout(3, 1));

        scoreLabel = new JLabel("Score: 0/" + questions.size());
        timerLabel = new JLabel("Koha: 10 sek");

        progressBar = new JProgressBar(0, questions.size());
        progressBar.setValue(0);

        topPanel.add(scoreLabel);
        topPanel.add(timerLabel);
        topPanel.add(progressBar);

        add(topPanel, BorderLayout.NORTH);

        // PYETJA
        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        questionLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(questionLabel, BorderLayout.CENTER);

        // OPSIONET
        optionButtons = new JRadioButton[4];
        optionGroup = new ButtonGroup();
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton();
            optionGroup.add(optionButtons[i]);
            optionsPanel.add(optionButtons[i]);
        }

        add(optionsPanel, BorderLayout.WEST);

        // BUTONI
        submitButton = new JButton("Submit");
        submitButton.addActionListener(this::checkAnswer);
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(submitButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void displayQuestion() {

        // Kontrollon nëse kemi kaluar të gjitha pyetjet
        if (currentQuestionIndex >= questions.size()) {
            // Nëse po, shfaq rezultatin final
            showFinalScore();
            return; // ndalon ekzekutimin e mëtejshëm të metodës
        }

        // Merr pyetjen aktuale nga lista e pyetjeve
        QuizQuestion q = questions.get(currentQuestionIndex);

        // Vendos tekstin e pyetjes në label
        // (numri i pyetjes + teksti i pyetjes)
        questionLabel.setText((currentQuestionIndex + 1) + ". " + q.getQuestion());

        // Pastron çdo zgjedhje të mëparshme të opsioneve
        optionGroup.clearSelection();

        // shfaq opsionet ne ekran por nuk tregon cili i sakt
        for (int i = 0; i < optionButtons.length; i++) {
            optionButtons[i].setText(q.getOptions().get(i));
        }

        // Nis kohëmatësin për pyetjen aktuale
        startTimer();
    }

    private void startTimer() {
        timeLeft = 10;
        timerLabel.setText("Koha: " + timeLeft + " sek");

        if (timer != null) timer.stop();

        timer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("Koha: " + timeLeft + " sek");

            if (timeLeft == 0) {
                timer.stop();
                JOptionPane.showMessageDialog(this, "Koha mbaroi!"); // Feedback kur mbaron koha
                currentQuestionIndex++;
                progressBar.setValue(currentQuestionIndex);
                displayQuestion();
            }
        });

        timer.start();
    }

    // ✅ checkAnswer me feedback
    private void checkAnswer(ActionEvent e) {
        int selected = -1;

        for (int i = 0; i < optionButtons.length; i++) {
            if (optionButtons[i].isSelected()) {
                selected = i;
                break;
            }
        }

        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Zgjidh një përgjigje!");
            return;
        }

        timer.stop();

        QuizQuestion currentQuestion = questions.get(currentQuestionIndex);

        // Feedback për përdoruesin
        if (currentQuestion.isCorrect(selected)) {
            score++;
            JOptionPane.showMessageDialog(this, "Përgjigja është e saktë ✅");
        } else {
            String correctAnswer = currentQuestion.getOptions().get(currentQuestion.getCorrectAnswer());
            JOptionPane.showMessageDialog(this, "Gabim ❌. Përgjigja e saktë: " + correctAnswer);
        }

        scoreLabel.setText("Score: " + score + "/" + questions.size());
        currentQuestionIndex++;
        progressBar.setValue(currentQuestionIndex);
        displayQuestion();
    }

    private void showFinalScore() {
        try {
            FileWriter fw = new FileWriter("scores.txt", true);
            fw.write("Rezultati: " + score + "/" + questions.size() + "\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(this,
                "Quiz përfundoi!\nPikët: " + score + "/" + questions.size());

        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QuizGame().setVisible(true));
    }
}