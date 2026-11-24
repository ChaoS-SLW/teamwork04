package aiwa.model;

import java.util.ArrayList;
import java.util.List;

import aiwa.entity.Chapter;
import aiwa.entity.Study;
import aiwa.entity.Task;

/**
 * コース詳細画面用のまとまり：コース + チャプター + レッスン + 進捗
 */
public class StudyModel {
	private Study study;
	private List<Chapter> chapters = new ArrayList<>();

	public StudyModel() {
	}

	public StudyModel(Study study, List<Chapter> chapters) {
		this.study = study;
		this.chapters = chapters;
	}

	public int getTotalLessons() {
		int n = 0;
		for (Chapter c : chapters) {
			n += c.getTasks().size();
		}
		return n;
	}

	public int getCompletedLessons() {
		int n = 0;
		for (Chapter c : chapters) {
			for (Task t : c.getTasks()) {
				if (t.isCompleted()) {
					n++;
				}
			}
		}
		return n;
	}

	public int getProgressPercent() {
		int total = getTotalLessons();
		return total == 0 ? 0 : (int) Math.round(getCompletedLessons() * 100.0 / total);
	}

	public Study getStudy() {
		return study;
	}

	public void setStudy(Study study) {
		this.study = study;
	}

	public List<Chapter> getChapters() {
		return chapters;
	}

	public void setChapters(List<Chapter> chapters) {
		this.chapters = chapters;
	}
}
