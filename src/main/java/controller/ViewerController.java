package controller;

import external.AuthenticationService;
import external.EmailService;
import model.SharedContext;
import view.View;

public class ViewerController extends Controller{
    public ViewerController(SharedContext sharedContext, View view, AuthenticationService auth, EmailService email) {
        super(sharedContext, view, auth, email);
    }

    public void viewCoursesMenu() {
        boolean endLoop = false;
        while (!endLoop) {
            view.displayInfo("[1] View all courses");
            view.displayInfo("[2] View specific course");
            view.displayInfo("[3] Back");

            String choice = view.getInput("Please enter your choice: ");
            switch (choice) {
                case "1" -> viewCourses();
                case "2" -> {
                    String courseCode = view.getInput("Enter the course code: ");
                    viewSpecificCourse(courseCode);
                }
                case "3" -> endLoop = true;
                default -> view.displayError("Invalid choice. Please try again.");
            }
        }
    }
    private void viewCourses() {
        String courseList = sharedContext.courseManager.simpleCoursesDetails();
        if (courseList.isEmpty()) {
            view.displayInfo("No courses available.");
        } else {
            view.displayInfo("Available Courses:\n");
            sharedContext.courseManager.viewCourses();
        }
    }

    private void viewSpecificCourse(String courseCode) {
        if (sharedContext.courseManager.checkCourseCode(courseCode)) {
            view.displayInfo("Course Details:\n");
            sharedContext.courseManager.viewSpecificCourse(courseCode);
        } else {
            view.displayError("Course not found.");
        }
    }
}
