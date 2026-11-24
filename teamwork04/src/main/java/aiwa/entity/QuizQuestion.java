package aiwa.entity;

import java.util.ArrayList;
import java.util.List;

public class QuizQuestion {
	private int questionId;
	private String questionText;
	private String explanation;
	private final List<QuizOption> options = new ArrayList<>();

	public int getQuestionId() { return questionId; }
	public void setQuestionId(int questionId) { this.questionId = questionId; }
	public String getQuestionText() { return questionText; }
	public void setQuestionText(String questionText) { this.questionText = questionText; }
	public String getExplanation() { return explanation; }
	public void setExplanation(String explanation) { this.explanation = explanation; }
	public List<QuizOption> getOptions() { return options; }
}
