package controller;

import external.AuthenticationService;
import external.EmailService;
import model.*;
import model.FAQ.FAQSection;
import view.View;

public class StudentController extends Controller {

    public StudentController(SharedContext sharedContext, View view, AuthenticationService auth, EmailService email) {
        super(sharedContext, view, auth, email);
    }

    public void manageTimetable(){
        view.displayDivider();
        view.displayInfo("[1] Add course to timetable");
        view.displayInfo("[2] Remove course from timetable");
        view.displayInfo("[3] View timetable");
        view.displayInfo("[4] Choose activity for course");
        view.displayInfo("[-1] Return to main menu");
        String input = view.getInput("What would you like to do with timetable?");
        int optionNo = 0;

        try {
            optionNo = Integer.parseInt(input);

            if (optionNo == 1) {
                addCourseToTimetable();
            }
            if (optionNo == 2) {
                removeCourseFromTimetable();
            }
            if (optionNo == 3) {
                viewTimetable();
            }
            if (optionNo == 4) {
                chooseActivityForCourse();
            }

        } catch (NumberFormatException e) {
            view.displayError("Invalid option: " + input);
        }

    }

    private void addCourseToTimetable(){
        CourseManager courseManager = sharedContext.getCourseManager();
        String courseCode = view.getInput("Enter course code to add:");
        String studentEmail;
        AuthenticatedUser user = (AuthenticatedUser) sharedContext.currentUser;
        studentEmail = user.getEmail();
        courseManager.addCourseToStudentTimetable(studentEmail, courseCode);


    }
    private void removeCourseFromTimetable(){
        CourseManager courseManager = sharedContext.getCourseManager();
        String courseCode = view.getInput("Enter course code to remove:");
        String studentEmail;
        AuthenticatedUser user = (AuthenticatedUser) sharedContext.currentUser;
        studentEmail = user.getEmail();

        Timetable timetable = courseManager.getTimetable(studentEmail);
        timetable.removeSlotsForCourse(courseCode);
        //TODO: does this update everything?

    }

    private void viewTimetable(){
        AuthenticatedUser user = (AuthenticatedUser) sharedContext.currentUser;
        String studentEmail = user.getEmail();
        CourseManager courseManager = sharedContext.getCourseManager();
        courseManager.viewTimetable(studentEmail);
    }

    private void chooseActivityForCourse(){
        // with course code
        //String studentEmail, String courseCode, int activityId
        CourseManager courseManager = sharedContext.getCourseManager();
        String courseCode = view.getInput("Enter course code:");
        int activityId = Integer.parseInt(view.getInput("Enter activity ID:"));
        String studentEmail;
        AuthenticatedUser user = (AuthenticatedUser) sharedContext.currentUser;
        studentEmail = user.getEmail();
        courseManager.chooseActivityForCourse(studentEmail, courseCode, activityId);

    }

}
