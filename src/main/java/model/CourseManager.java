package model;

import java.util.ArrayList;
import java.util.List;

public class CourseManager {

    private final List<Course> courses = new ArrayList<>();
    private StringBuilder courseList = new StringBuilder();

    public String ViewCourses() {
        for (Course course : courses) {
            courseList.append(course.toString());
        }
        return courseList.toString();
    }

    public boolean checkCourseCode(String courseCode) {
        for (Course course : courses) {
            if (course.getCourseCode().equals(courseCode)) {
                return true;
            }
        }
        return false;
    }

    public boolean removeCourse(String courseCode) {
        for (Course course : courses) {
            if (course.getCourseCode().equals(courseCode)) {
                courses.remove(course);
                return true;
            }
        }
        return false;
    }

    public boolean addCourse(String code, String name, String description, boolean requiresComputers,
                             String COName, String COEmail, String CSName, String CSEmail,
                             int reqTutorials, int reqLabs) {
        Course newCourse = new Course(code, name, description, requiresComputers, COName, COEmail,
                CSName, CSEmail, reqTutorials, reqLabs);
        courses.add(newCourse);
        return true;
    }
}
