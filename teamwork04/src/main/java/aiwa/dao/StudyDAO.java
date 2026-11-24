package aiwa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import aiwa.entity.Category;
import aiwa.entity.Chapter;
import aiwa.entity.Study;
import aiwa.ulti.Ulti;

/**
 * categories / courses / chapters を扱う DAO（※新規追加ファイル）
 */
public class StudyDAO {

	private static final String COURSE_SQL = "SELECT c.course_id, c.category_id, cat.category_name, c.course_name, c.description, c.image, c.level, "
			+ "  (SELECT COUNT(*) FROM lessons l JOIN chapters ch ON l.chapter_id = ch.chapter_id "
			+ "    WHERE ch.course_id = c.course_id) AS total_lessons, "
			+ "  (SELECT COUNT(*) FROM lessons l JOIN chapters ch ON l.chapter_id = ch.chapter_id "
			+ "    JOIN lesson_progress p ON p.lesson_id = l.lesson_id AND p.is_completed = 1 "
			+ "      AND p.user_id = (SELECT user_id FROM users WHERE email = 'quiz-guest@studyflow.local') "
			+ "    WHERE ch.course_id = c.course_id) AS completed_lessons "
			+ "FROM courses c JOIN categories cat ON cat.category_id = c.category_id ";

	/** カテゴリ一覧（コース数つき） */
	public List<Category> findAllCategories() throws SQLException {
		String sql = "SELECT cat.category_id, cat.category_name, cat.description, cat.image, "
				+ "  (SELECT COUNT(*) FROM courses c WHERE c.category_id = cat.category_id) AS course_count "
				+ "FROM categories cat ORDER BY cat.category_id";
		List<Category> list = new ArrayList<>();
		try (Connection con = Ulti.getConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) {
				Category c = new Category();
				c.setCategoryId(rs.getInt("category_id"));
				c.setCategoryName(rs.getString("category_name"));
				c.setDescription(rs.getString("description"));
				c.setImage(rs.getString("image"));
				c.setCourseCount(rs.getInt("course_count"));
				list.add(c);
			}
		}
		return list;
	}

	public Category findCategoryById(int categoryId) throws SQLException {
		String sql = "SELECT category_id, category_name, description, image FROM categories WHERE category_id = ?";
		try (Connection con = Ulti.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, categoryId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Category c = new Category();
					c.setCategoryId(rs.getInt("category_id"));
					c.setCategoryName(rs.getString("category_name"));
					c.setDescription(rs.getString("description"));
					c.setImage(rs.getString("image"));
					return c;
				}
			}
		}
		return null;
	}

	/** カテゴリ別のコース一覧（進捗つき） */
	public List<Study> findCoursesByCategory(int categoryId) throws SQLException {
		List<Study> list = new ArrayList<>();
		try (Connection con = Ulti.getConnection();
				PreparedStatement ps = con
						.prepareStatement(COURSE_SQL + "WHERE c.category_id = ? ORDER BY c.course_id")) {
			ps.setInt(1, categoryId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapStudy(rs));
				}
			}
		}
		return list;
	}

	/** 全コース一覧（ダッシュボードの進捗表示用） */
	public List<Study> findAllCourses() throws SQLException {
		List<Study> list = new ArrayList<>();
		try (Connection con = Ulti.getConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(COURSE_SQL + "ORDER BY c.course_id")) {
			while (rs.next()) {
				list.add(mapStudy(rs));
			}
		}
		return list;
	}

	public Study findCourseById(int courseId) throws SQLException {
		try (Connection con = Ulti.getConnection();
				PreparedStatement ps = con.prepareStatement(COURSE_SQL + "WHERE c.course_id = ?")) {
			ps.setInt(1, courseId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapStudy(rs);
				}
			}
		}
		return null;
	}

	/** コースのチャプター一覧（chapter_order 順。tasks は空） */
	public List<Chapter> findChaptersByCourse(int courseId) throws SQLException {
		String sql = "SELECT chapter_id, course_id, chapter_name, chapter_order "
				+ "FROM chapters WHERE course_id = ? ORDER BY chapter_order";
		List<Chapter> list = new ArrayList<>();
		try (Connection con = Ulti.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, courseId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Chapter ch = new Chapter();
					ch.setChapterId(rs.getInt("chapter_id"));
					ch.setCourseId(rs.getInt("course_id"));
					ch.setChapterName(rs.getString("chapter_name"));
					ch.setChapterOrder(rs.getInt("chapter_order"));
					list.add(ch);
				}
			}
		}
		return list;
	}

	/** レッスンIDから、所属するコースIDを調べる（戻りリンク用） */
	public int findCourseIdByLesson(int lessonId) throws SQLException {
		String sql = "SELECT ch.course_id FROM lessons l JOIN chapters ch ON ch.chapter_id = l.chapter_id "
				+ "WHERE l.lesson_id = ?";
		try (Connection con = Ulti.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, lessonId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? rs.getInt(1) : -1;
			}
		}
	}

	private Study mapStudy(ResultSet rs) throws SQLException {
		Study s = new Study();
		s.setCourseId(rs.getInt("course_id"));
		s.setCategoryId(rs.getInt("category_id"));
		s.setCategoryName(rs.getString("category_name"));
		s.setCourseName(rs.getString("course_name"));
		s.setDescription(rs.getString("description"));
		s.setImage(rs.getString("image"));
		s.setLevel(rs.getString("level"));
		s.setTotalLessons(rs.getInt("total_lessons"));
		s.setCompletedLessons(rs.getInt("completed_lessons"));
		return s;
	}
}
