
package aiwa.entity;

public class StudySession {

    private int courseId;
    private int categoryId;
    private String courseName;
    private String description;
    private String image;
    private String level;

    
    public StudySession() {
    }

    public StudySession(int courseId, int categoryId,
            String courseName, String description,
            String image, String level) {

        this.courseId = courseId;
        this.categoryId = categoryId;
        this.courseName = courseName;
        this.description = description;
        this.image = image;
        this.level = level;
    }

   

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
