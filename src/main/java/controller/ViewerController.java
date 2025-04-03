package controller;

import external.AuthenticationService;
import external.EmailService;
import model.SharedContext;
import view.View;

public class ViewerController extends Controller{
    public ViewerController(SharedContext sharedContext, View view, AuthenticationService auth, EmailService email) {
        super(sharedContext, view, auth, email);
    }

    public void viewCourses() {
        String courseList = sharedContext.courseManager.ViewCourses();
        if (courseList.isEmpty()) {
            view.displayInfo("No courses available.");
        } else {
            view.displayInfo("Available Courses:\n");
            sharedContext.courseManager.viewCourses();
        }
    }

    public void viewCourseDetails(String courseCode) {
        if (sharedContext.courseManager.hasCourse(courseCode)) {
            view.displayInfo("Course Details:\n");
            view.displayCourse(sharedContext.courseManager.getCourse(courseCode));
        } else {
            view.displayError("Course not found.");
        }
    }
}
