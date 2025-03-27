package model;

import java.util.ArrayList;
import java.util.List;

public class CourseManager {
    private final List<Course> courses = new ArrayList<>();

    public boolean addCourse(String code, String name, String description, boolean requiresComputers,
                             String COName, String COEmail, String CSName, String CSEmail,
                             int reqTutorials, int reqLabs) {
        for (Course course : courses) {
            if (course.hasCode(code)) {
                return false; // Course with the same code already exists
            }
        }
        Course newCourse = new Course(code, name, description, requiresComputers, COName, COEmail, CSName, CSEmail, reqTutorials, reqLabs);
        courses.add(newCourse);
        return true;
    }

    public boolean checkCourseCode(String code) {
        for (Course course : courses) {
            if (course.hasCode(code)) {
                return true;
            }
        }
        return false;
    }

    public String[] removeCourse(String code) {
        for (Course course : courses) {
            if (course.hasCode(code)) {
                courses.remove(course);
                return new String[]{course.getCourseOrganiserEmail(), course.getCourseSecretaryEmail()};
            }
        }
        return null;
    }

    public void addCourseToStudentTimetable(String courseCode, String studentEmail) {
        //TODO: Implement this method unsure what it should do
    }
    //TODO: Implement the rest of the methods
}
