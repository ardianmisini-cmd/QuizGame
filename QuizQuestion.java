import java.util.List;

public class QuizQuestion {

    private String question;
    private List<String> options;
    private int correctAnswer;

    public QuizQuestion(String question, List<String> options, int correctAnswer) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public boolean isCorrect(int selectedIndex) {
        return selectedIndex == correctAnswer;
    }

    // ✅ Getter për indeksin e përgjigjes së saktë
    public int getCorrectAnswer() {
        return correctAnswer;
    }
}

