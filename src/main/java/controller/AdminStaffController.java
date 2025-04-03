package controller;

import external.AuthenticationService;
import external.EmailService;
import model.*;
import model.FAQ.FAQItem;
import model.FAQ.FAQSection;
import view.View;
import utils.Logger;
import model.activities.Activity;
import java.util.*;

public class AdminStaffController extends StaffController {
    public AdminStaffController(SharedContext sharedContext, View view, AuthenticationService auth, EmailService email) {
        super(sharedContext, view, auth, email);
    }
    public void manageFAQ() {
        FAQSection currentSection = null;

        while (true) {
            if (currentSection == null) {
                view.displayFAQ(sharedContext.getFAQ());
                view.displayInfo("[-1] Return to main menu");
            } else {
                view.displayFAQSection(currentSection);
                view.displayInfo("[-1] Return to " + (currentSection.getParent() == null ? "FAQ" : currentSection.getParent().getTopic()));
            }
            view.displayInfo("[-2] Add FAQ item");
            String input = view.getInput("Please choose an option: ");
            try {
                int optionNo = Integer.parseInt(input);

                if (optionNo == -2) {
                    addFAQItem(currentSection);
                } else if (optionNo == -1) {
                    if (currentSection == null) {
                        break;
                    } else {
                        currentSection = currentSection.getParent();
                    }
                } else {
                    try {
                        if (currentSection == null) {
                            currentSection = sharedContext.getFAQ().getSections().get(optionNo);
                        } else {
                            currentSection = currentSection.getSubsections().get(optionNo);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        view.displayError("Invalid option: " + optionNo);
                    }
                }
            } catch (NumberFormatException e) {
                view.displayError("Invalid option: " + input);
            }
        }
    }
    private void addFAQItem(FAQSection currentSection) {
        // dislay what the user is doing
        view.displayInfo("=== Add New FAQ Question-Answer Pair===");

        // When adding an item at root of FAQ, creating a section is mandatory
        boolean createSection = (currentSection == null);
        if (!createSection) {
            createSection = view.getYesNoInput("Would you like to create a new topic for the FAQ item?");
        }

        // if user wants to create a new section, get the topic title
        if (createSection) {
            String newTopic = view.getInput("Enter new topic title: ");
            if (newTopic == null || newTopic.isEmpty()){
                view.displayError("Topic cannot be empty");
                return;
            }

            // check if topic already exists
            FAQSection newSection = new FAQSection(newTopic);
            if (currentSection == null) {
                if (sharedContext.getFAQ().getSections().stream().anyMatch(section -> section.getTopic().equals(newTopic))) {
                    view.displayWarning("Topic '" + newTopic + "' already exists!");
                    newSection = sharedContext.getFAQ().getSections().stream().filter(section -> section.getTopic().equals(newTopic)).findFirst().orElseThrow();
                } else {
                    sharedContext.getFAQ().addSection(newSection);
                    view.displayInfo("Created topic '" + newTopic + "'");
                }
            } else {
                if (currentSection.getSubsections().stream().anyMatch(section -> section.getTopic().equals(newTopic))) {
                    view.displayWarning("Topic '" + newTopic + "' already exists under '" + currentSection.getTopic() + "'!");
                    newSection = currentSection.getSubsections().stream().filter(section -> section.getTopic().equals(newTopic)).findFirst().orElseThrow();
                } else {
                    currentSection.addSubsection(newSection);
                    view.displayInfo("Created topic '" + newTopic + "' under '" + currentSection.getTopic() + "'");
                }
            }
            currentSection = newSection;
        }

        String FAQSectionTopic = currentSection.getTopic();

        // enter question for FAQ
        String question = view.getInput("Enter the question: ");
        if(question == null || question.isEmpty()){
            Logger logger = Logger.getInstance();
            logger.log(
                System.currentTimeMillis(),
                ((AuthenticatedUser) sharedContext.currentUser).getEmail(),
                "addFAQItem",
                FAQSectionTopic,
                "FAILURE"+" (Error: the question cannot be empty )"
            );
            view.displayError("Question cannot be empty");
            return;
        }

        // enter answer for FAQ
        String answer = view.getInput("Enter the answer for new FAQ item: ");
        if(answer == null || answer.isEmpty()){
            Logger logger = Logger.getInstance();
            logger.log(
                System.currentTimeMillis(),
                ((AuthenticatedUser) sharedContext.currentUser).getEmail(),
                "addFAQItem",
                FAQSectionTopic,
                "FAILURE"+" (Error: the answer cannot be empty )"
            );
            view.displayError("Answer cannot be empty");
            return;
        }

        // check if user wants to add a course tag
        boolean addTag = view.getYesNoInput("Would you like to add a Course tag?");

        if (addTag) {
            CourseManager courseManager = sharedContext.getCourseManager();
            String courseDetails = courseManager.ViewCourses();

            String[] coursesSplit = courseDetails.split("\n");

            // define full course details as empty string
            String fullCourseDetailsAsString = "";

            for (String courseStr : coursesSplit) {
                // course is in the format "courseName - courseCode"
                String[] courseDetailsSplit = courseStr.split(" - ");

                Course courseName = courseManager.getCourse(courseDetailsSplit[0]);

                if (courseName == null) {
                    view.displayError("No course found with name " + courseDetailsSplit[0]);
                    return;
                }

                String fullActivityDetailsAsString = "";

                // full list of activities for given course
                Map<Integer, Activity> activities = courseName.getActivities();

                if (activities.isEmpty()) {
                    view.displayError("No activities available for course " + courseName.getCourseCode());
                    return;
                }

                // iterate through all activities and add to activity detail empty string
                for (Activity activity : activities.values()) {

                    String activityDetailsAsString = activity.toString() + ", ";
                    fullActivityDetailsAsString += activityDetailsAsString;

                    activityDetailsAsString = activity.toString() + ";";
                    fullActivityDetailsAsString += activityDetailsAsString;

                }

                // concatenate course name and course code with activity details
                fullCourseDetailsAsString = courseStr + "-" + fullActivityDetailsAsString + "\n";

                // fullCourseDetailsAsString is now in the format courseName - courseCode - activityDetails;
                // activityDetails; ...
            }

            String courseTag;

            // check if there are any courses available
            if (fullCourseDetailsAsString.isEmpty()) {
                view.displayError("No courses available in the system");
                return;
            } else {
                view.displayInfo("Available courses: ");

                String[] coursesAsStr = fullCourseDetailsAsString.split("\n");

                for (String course : coursesAsStr) {
                    // courseDetailsSplit is in the format "courseName - courseCode - allActivityDetails"
                    String[] courseDetailsSplit = course.split(" - ");

                    String courseName = courseDetailsSplit[0];
                    String courseCode = courseDetailsSplit[1];

                    view.displayInfo(courseCode + " : " + courseName);
                }

                // get course code from user
                courseTag = view.getInput("Enter course code to add as tag: ");
                boolean hasCourse = courseManager.hasCourse(courseTag);

                // check if course_code is valid
                if (!hasCourse) {
                    Logger logger = Logger.getInstance();
                    logger.log(
                            System.currentTimeMillis(),
                            ((AuthenticatedUser) sharedContext.currentUser).getEmail(),
                            "addFAQItem",
                            FAQSectionTopic,
                            "FAILURE" + " (Error: the tag must correspond to a course code)"
                    );

                    view.displayError("The tag must correspond to a course code");
                    return;
                }
            }

            // add FAQ item to FAQ section
            currentSection.addItem(question, answer, courseTag);
        } else {
            // add FAQ item to FAQ section without course tag
            currentSection.addItem(question, answer);
        }

        Logger logger = Logger.getInstance();
        logger.log(
                System.currentTimeMillis(),
                ((AuthenticatedUser) sharedContext.currentUser).getEmail(),
                "addFAQItem",
                FAQSectionTopic,
                "SUCCESS"+" (A new FAQ item was added)"
        );

        view.displaySuccess("The new FAQ item was added ");
    }
    public void manageInquiries() {
        String[] inquiryTitles = getInquiryTitles(sharedContext.inquiries);

        while (true) {
            view.displayInfo("Pending inquiries");
            int selection = selectFromMenu(inquiryTitles, "Back to main menu");
            if (selection == -1) {
                return;
            }
            Inquiry selectedInquiry = sharedContext.inquiries.get(selection);

            while (true) {
                view.displayDivider();
                view.displayInquiry(selectedInquiry);
                view.displayDivider();
                String[] followUpOptions = { "Redirect inquiry", "Respond to inquiry" };
                int followUpSelection = selectFromMenu(followUpOptions, "Back to all inquiries");

                if (followUpSelection == -1) {
                    break;
                } else if (followUpOptions[followUpSelection].equals("Redirect inquiry")) {
                    redirectInquiry(selectedInquiry);
                } else if (followUpOptions[followUpSelection].equals("Respond to inquiry")) {
                    respondToInquiry(selectedInquiry);
                    inquiryTitles = getInquiryTitles(sharedContext.inquiries); // required to remove responded inquiry from titles
                    break;
                }
            }
        }
    }
    private void redirectInquiry(Inquiry inquiry) {
        inquiry.setAssignedTo(view.getInput("Enter assignee email: "));
        email.sendEmail(
                SharedContext.ADMIN_STAFF_EMAIL,
                inquiry.getAssignedTo(),
                "New inquiry from " + inquiry.getInquirerEmail(),
                "Subject: " + inquiry.getSubject() + "\nPlease log into the Self Service Portal to review and respond to the inquiry."
        );
        view.displaySuccess("Inquiry has been reassigned");
    }
    public void manageCourses() {
        while (true) {
            view.displayInfo("[-1] Return to main menu");
            view.displayInfo("[-2] Add course");
            view.displayInfo("[-3] Remove course");
            String input = view.getInput("Please choose an option: ");
            try {
                int optionNo = Integer.parseInt(input);

                if (optionNo == -2) {
                    addCourse();
                } else if (optionNo == -1) {
                    break;
                } else if (optionNo == -3) {
                    //TODO: Implement course removal
                    break;
                } else {
                    view.displayError("Invalid option: " + optionNo);
                }
            } catch (NumberFormatException e) {
                view.displayError("Invalid option: " + input);
            }
        }
    }
    private void addCourse() {
        String courseCode = view.getInput("Enter course code: ");
        if (sharedContext.getCourseManager().checkCourseCode(courseCode)) {
            view.displayError("Course with the same code already exists");
            return;
        }
        String courseName = view.getInput("Enter course name: ");
        String courseDescription = view.getInput("Enter course description: ");
        boolean requiresComputers = view.getYesNoInput("Does this course require computers?");
        String courseOrganiserName = view.getInput("Enter course organiser name: ");
        //TODO: Add email validation
        String courseOrganiserEmail = view.getInput("Enter course organiser email: ");
        String courseSecretaryName = view.getInput("Enter course secretary name: ");
        String courseSecretaryEmail = view.getInput("Enter course secretary email: ");
        try {
            int requiredTutorials = Integer.parseInt(view.getInput("Enter required tutorials: "));
            int requiredLabs = Integer.parseInt(view.getInput("Enter required labs: "));
            if (sharedContext.getCourseManager().addCourse(courseCode, courseName, courseDescription, requiresComputers, courseOrganiserName, courseOrganiserEmail,
                    courseSecretaryName, courseSecretaryEmail, requiredTutorials, requiredLabs)) {
                view.displaySuccess("Course added successfully");
            }
        }catch (NumberFormatException e){
            view.displayError("Invalid input for required tutorials or labs");
        }
    }
}