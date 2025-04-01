package model;

import java.util.ArrayList;
import java.util.List;
import utils.Logger;

public class CourseManager {

    private final List<Course> courses = new ArrayList<>();
<<<<<<< HEAD
    private final List<Timetable> timetables = new ArrayList<>();
=======
    private StringBuilder courseList = new StringBuilder();
>>>>>>> f54a41423c7f2e242d580ffae851ebbdb0d03158

    public String ViewCourses() {
        StringBuilder courseList = new StringBuilder();
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

    public void addCourseToStudentTimetable(String studentEmail, String courseCode) {
        Timetable currentTimeTable = null;
        boolean found = false;
        for(Timetable timetable : timetables) {
            if (timetable.hasStudentEmail(studentEmail)) {
                currentTimeTable = timetable;
                found = true;
                break;
            }
        }
        if(!found) {
            currentTimeTable = new Timetable(studentEmail);
            timetables.add(currentTimeTable);
        }

    }
    private boolean hasCourse(String courseCode) {
        for (Course course : courses) {
            if (course.getCourseCode().equals(courseCode)) {
                return true;
            }
        }
        Logger logger = Logger.getInstance();
        logger.log(System.currentTimeMillis(),"addCourseToTimeTable",courseCode,"Course not found in courses list","Failed");
        return false;
    }
}
