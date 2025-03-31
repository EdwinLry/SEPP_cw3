package model;

import java.util.ArrayList;
import java.util.List;

public class CourseManager {
    private final List<Course> courses = new ArrayList<>();

    public String ViewCourses() {
        StringBuilder courseList = new StringBuilder();
        for (Course course : courses) {
            courseList.append(course.toString());
        }
        return courseList.toString();
    }
    public boolean addCourse(String code, String name, String description, boolean requiresComputers,
                             String COName, String COEmail, String CSName, String CSEmail,
                             int reqTutorials, int reqLabs) {
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
