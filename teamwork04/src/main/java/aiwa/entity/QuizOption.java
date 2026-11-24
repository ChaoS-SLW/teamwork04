package aiwa.entity;

public class QuizOption {
	private int optionId;
	private String optionText;
	private boolean correct;

	public int getOptionId() { return optionId; }
	public void setOptionId(int optionId) { this.optionId = optionId; }
	public String getOptionText() { return optionText; }
	public void setOptionText(String optionText) { this.optionText = optionText; }
	public boolean isCorrect() { return correct; }
	public void setCorrect(boolean correct) { this.correct = correct; }
}
