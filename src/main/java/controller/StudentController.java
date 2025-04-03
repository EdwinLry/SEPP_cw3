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
        while (true){
            view.displayDivider();
            view.displayInfo("[1] Add course to timetable");
            view.displayInfo("[2] Remove course from timetable");
            view.displayInfo("[3] View timetable");
            view.displayInfo("[4] Choose activity for course");
            view.displayInfo("[-1] Return to main menu");

            String input = view.getInput("What would you like to do with timetable?");
            int optionNo;

            try {
                optionNo = Integer.parseInt(input);
                switch (optionNo) {
                    case 1 -> addCourseToTimetable();
                    case 2 -> removeCourseFromTimetable();
                    case 3 -> viewTimetable();
                    case 4 -> chooseActivityForCourse();
                    case -1 -> {
                        return;
                    }
                    default -> view.displayError("Invalid option: " + input);
                }
            } catch (NumberFormatException e) {
                view.displayError("Invalid option: " + input);
            }
        }


    }

    private void addCourseToTimetable(){
        CourseManager courseManager = sharedContext.getCourseManager();
        String courseCode = view.getInput("Enter course code to add:");
        AuthenticatedUser user = (AuthenticatedUser) sharedContext.currentUser;
        String studentEmail = user.getEmail();

        if (courseCode == null || courseCode.trim().isEmpty()) {
            view.displayError("Must input a course code.");
            return;
        }

        if (courseManager.hasCourse(courseCode)) {
            courseManager.addCourseToStudentTimetable(studentEmail, courseCode);
        } else {
            view.displayError("Incorrect course code, failed to add course.");
        }
    }
    private void removeCourseFromTimetable(){
        CourseManager courseManager = sharedContext.getCourseManager();
        String courseCode = view.getInput("Enter course code to remove:");
        String studentEmail;
        AuthenticatedUser user = (AuthenticatedUser) sharedContext.currentUser;
        studentEmail = user.getEmail();

        if (courseCode == null || courseCode.trim().isEmpty()) {
            view.displayError("Must input a course code.");
            return;
        }

        if (courseManager.hasCourse(courseCode)) {
            Timetable timetable = courseManager.getTimetable(studentEmail);
            timetable.removeSlotsForCourse(courseCode);
            view.displaySuccess("The course was successfully removed from your timetable.");
        } else {
            view.displayError("Incorrect course code, failed to remove course.");
        }

    }

    private void viewTimetable(){
        AuthenticatedUser user = (AuthenticatedUser) sharedContext.currentUser;
        String studentEmail = user.getEmail();
        CourseManager courseManager = sharedContext.getCourseManager();
        courseManager.viewTimetable(studentEmail);
    }

    private void chooseActivityForCourse(){
        String studentEmail;
        AuthenticatedUser user = (AuthenticatedUser) sharedContext.currentUser;
        studentEmail = user.getEmail();

        CourseManager courseManager = sharedContext.getCourseManager();
        String courseCode = view.getInput("Enter course code:");

        if (courseCode == null || courseCode.trim().isEmpty()) {
            view.displayError("Must input a course code.");
            return;
        }
        if (!courseManager.hasCourse(courseCode)) {
            view.displayError("Incorrect course code.");
            return;
        }

        String activityId = view.getInput("Enter activity ID:");

        if (activityId == null || activityId.trim().isEmpty()) {
            view.displayError("Must input an activity ID.");
            return;
        }
        courseManager.chooseActivityForCourse(studentEmail, courseCode, Integer.parseInt(activityId));

    }

}
