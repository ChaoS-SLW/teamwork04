package aiwa.model;

import java.sql.SQLException;
import java.util.List;

import aiwa.dao.CourseDAO;
import aiwa.entity.Category;
import aiwa.entity.Study;

public class CourseModel {
	private final CourseDAO dao = new CourseDAO();
	public List<Study> getCourses(long userId) throws SQLException { return dao.findAll(userId); }
	public List<Category> getCategories() throws SQLException { return dao.findCategories(); }
	public int create(Study course) throws SQLException { return dao.create(course); }
	public boolean update(Study course) throws SQLException { return dao.update(course); }
	public boolean delete(int courseId) throws SQLException { return dao.delete(courseId); }
}
