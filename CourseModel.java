
package aiwa.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import aiwa.entity.StudySession;

public class CourseModel {

   
    private static final String URL =
            "jdbc:sqlite:C:/Users/user/Downloads/studyflow.db";

  
    public ArrayList<StudySession> getAllCourses()
            throws SQLException {

        ArrayList<StudySession> courses = new ArrayList<>();

        String sql = "SELECT * FROM courses ORDER BY course_id";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                StudySession course = new StudySession();

                course.setCourseId(rs.getInt("course_id"));
                course.setCategoryId(rs.getInt("category_id"));
                course.setCourseName(rs.getString("course_name"));
                course.setDescription(rs.getString("description"));
                course.setImage(rs.getString("image"));
                course.setLevel(rs.getString("level"));

                courses.add(course);
            }
        }

        return courses;
    }

  
    public StudySession getCourseById(int courseId)
            throws SQLException {

        String sql = "SELECT * FROM courses WHERE course_id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    StudySession course = new StudySession();

                    course.setCourseId(rs.getInt("course_id"));
                    course.setCategoryId(rs.getInt("category_id"));
                    course.setCourseName(rs.getString("course_name"));
                    course.setDescription(rs.getString("description"));
                    course.setImage(rs.getString("image"));
                    course.setLevel(rs.getString("level"));

                    return course;
                }
            }
        }

        return null;
    }

    
    public ArrayList<StudySession> getCoursesByCategoryId(int categoryId)
            throws SQLException {

        ArrayList<StudySession> courses = new ArrayList<>();

        String sql =
                "SELECT * FROM courses WHERE category_id = ? ORDER BY course_id";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    StudySession course = new StudySession();

                    course.setCourseId(rs.getInt("course_id"));
                    course.setCategoryId(rs.getInt("category_id"));
                    course.setCourseName(rs.getString("course_name"));
                    course.setDescription(rs.getString("description"));
                    course.setImage(rs.getString("image"));
                    course.setLevel(rs.getString("level"));

                    courses.add(course);
                }
            }
        }

        return courses;
    }
}
