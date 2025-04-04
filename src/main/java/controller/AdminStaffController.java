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
                // Display top-level FAQ
                view.displayFAQ(sharedContext.getFAQ());
                view.displayInfo("[-1] Return to main menu");
            } else {
                // Display a specific topic or subsection
                view.displayFAQSection(currentSection);
                if (currentSection.getSubsections().isEmpty()) {
                    view.displayInfo("[-1] Return to FAQ");
                } else {
                    view.displayInfo("[-1] Go up");
                }
            }

            view.displayInfo("[-2] Add FAQ item");
            String input = view.getInput("Please choose an option: ");

            try {
                int optionNo = Integer.parseInt(input);

                if (optionNo == -2) {
                    addFAQItem(currentSection);

                } else if (optionNo == -1) {
                    if (currentSection == null) {
                        // Exit to main menu
                        break;
                    } else if (currentSection.getParent() == null) {
                        // Top-level section → go back to full FAQ
                        currentSection = null;
                    } else {
                        // Go up one level
                        currentSection = currentSection.getParent();
                    }

                } else {
                    if (currentSection == null) {
                        if (optionNo >= 0 && optionNo < sharedContext.getFAQ().getSections().size()) {
                            currentSection = sharedContext.getFAQ().getSections().get(optionNo);
                        } else {
                            view.displayError("Invalid option: " + optionNo);
                        }
                    } else {
                        if (optionNo >= 0 && optionNo < currentSection.getSubsections().size()) {
                            currentSection = currentSection.getSubsections().get(optionNo);
                        } else {
                            view.displayError("Invalid option: " + optionNo);
                        }
                    }
                }

            } catch (NumberFormatException e) {
                view.displayError("Invalid input: " + input);
            }
        }
    }
    private void addFAQItem(FAQSection currentSection) {
        view.displayInfo("=== Add New FAQ Question-Answer Pair===");

        boolean createSection = (currentSection == null);
        if (!createSection) {
            createSection = view.getYesNoInput("Would you like to create a new topic for the FAQ item?");
        }

        if (createSection) {
            String newTopic = view.getInput("Enter new topic title: ");
            if (newTopic == null || newTopic.trim().isEmpty()) {
                view.displayError("Topic cannot be empty");
                return;
            }

            FAQSection newSection = new FAQSection(newTopic);
            if (currentSection == null) {
                if (sharedContext.getFAQ().getSections().stream().anyMatch(section -> section.getTopic().equals(newTopic))) {
                    view.displayWarning("Topic '" + newTopic + "' already exists!");
                    newSection = sharedContext.getFAQ().getSections().stream()
                            .filter(section -> section.getTopic().equals(newTopic))
                            .findFirst().orElseThrow();
                } else {
                    sharedContext.getFAQ().addSection(newSection);
                    view.displayInfo("Created topic '" + newTopic + "'");
                }
            } else {
                if (currentSection.getSubsections().stream().anyMatch(section -> section.getTopic().equals(newTopic))) {
                    view.displayWarning("Topic '" + newTopic + "' already exists under '" + currentSection.getTopic() + "'!");
                    newSection = currentSection.getSubsections().stream()
                            .filter(section -> section.getTopic().equals(newTopic))
                            .findFirst().orElseThrow();
                } else {
                    currentSection.addSubsection(newSection);
                    view.displayInfo("Created topic '" + newTopic + "' under '" + currentSection.getTopic() + "'");
                }
            }
            currentSection = newSection;
        }

        String FAQSectionTopic = currentSection.getTopic();

        String question = view.getInput("Enter the question: ");
        if (question == null || question.trim().isEmpty()) {
            Logger.getInstance().log(System.currentTimeMillis(),
                    ((AuthenticatedUser) sharedContext.currentUser).getEmail(),
                    "addFAQItem", FAQSectionTopic,
                    "FAILURE (Error: the question cannot be empty)");
            view.displayError("Question cannot be empty");
            return;
        }

        String answer = view.getInput("Enter the answer for new FAQ item: ");
        if (answer == null || answer.trim().isEmpty()) {
            Logger.getInstance().log(System.currentTimeMillis(),
                    ((AuthenticatedUser) sharedContext.currentUser).getEmail(),
                    "addFAQItem", FAQSectionTopic,
                    "FAILURE (Error: the answer cannot be empty)");
            view.displayError("Answer cannot be empty");
            return;
        }

        boolean addTag = view.getYesNoInput("Would you like to add a Course tag?");
        if (addTag) {
            CourseManager courseManager = sharedContext.getCourseManager();


            if (courseManager.viewCoursesFormatted() == "") {
                view.displayError("No courses currently available in the system.");
                return;
            }

            String courseTag = view.getInput("Enter course code to add as tag:");
            if (!courseManager.hasCourse(courseTag)) {
                Logger.getInstance().log(System.currentTimeMillis(),
                        ((AuthenticatedUser) sharedContext.currentUser).getEmail(),
                        "addFAQItem", FAQSectionTopic,
                        "FAILURE (Error: the tag must correspond to a course code)");
                view.displayError("The tag must correspond to a course code.");
                return;
            }

            currentSection.addItem(question, answer, courseTag);
        } else {
            currentSection.addItem(question, answer);
        }

        Logger.getInstance().log(System.currentTimeMillis(),
                ((AuthenticatedUser) sharedContext.currentUser).getEmail(),
                "addFAQItem", FAQSectionTopic,
                "SUCCESS (A new FAQ item was added)");

        if(addTag){
            view.displaySuccess("New FAQ item was added with tag.");
        }
        else{
            view.displaySuccess("New FAQ item was added without tag.");
        }
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
            view.displayInfo("[-3] Add activity to course");
            view.displayInfo("[-4] Remove course");
            String input = view.getInput("Please choose an option: ");
            try {
                int optionNo = Integer.parseInt(input);
                switch (optionNo) {
                    case -1 -> {
                        return;
                    }
                    case -2 -> addCourse();
                    case -3 -> addActivityToCourse();
                    case -4 -> removeCourse();
                    default -> view.displayError("Invalid option: " + optionNo);
                }
            } catch (NumberFormatException e) {
                view.displayError("Invalid option: " + input);
            }
        }
    }
    private void addCourse() {
        String courseCode = view.getInput("Enter course code: ");
        if (courseCode == null || courseCode.trim().isEmpty()) {
            view.displayError("Course code cannot be empty.");
            return;
        }
        if (sharedContext.getCourseManager().checkCourseCode(courseCode)) {
            view.displayError("Course with the same code already exists.");
            return;
        }

        String courseName = view.getInput("Enter course name: ");
        if (courseName == null || courseName.trim().isEmpty()) {
            view.displayError("Course name cannot be empty.");
            return;
        }

        String courseDescription = view.getInput("Enter course description: ");
        if (courseDescription == null || courseDescription.trim().isEmpty()) {
            view.displayError("Course description cannot be empty.");
            return;
        }

        boolean requiresComputers = view.getYesNoInput("Does this course require computers?");

        String courseOrganiserName = view.getInput("Enter course organiser name: ");
        if (courseOrganiserName == null || courseOrganiserName.trim().isEmpty()) {
            view.displayError("Course organiser name cannot be empty.");
            return;
        }

        String courseOrganiserEmail = view.getInput("Enter course organiser email: ");
        if (courseOrganiserEmail == null || !courseOrganiserEmail.contains("@")) {
            view.displayError("Invalid course organiser email.");
            return;
        }

        //TODO: email validation

        String courseSecretaryName = view.getInput("Enter course secretary name: ");
        if (courseSecretaryName == null || courseSecretaryName.trim().isEmpty()) {
            view.displayError("Course secretary name cannot be empty.");
            return;
        }

        String courseSecretaryEmail = view.getInput("Enter course secretary email: ");
        if (courseSecretaryEmail == null || !courseSecretaryEmail.contains("@")) {
            view.displayError("Invalid course secretary email.");
            return;
        }

        try {
            String tutorialsInput = view.getInput("Enter required tutorials: ");
            String labsInput = view.getInput("Enter required labs: ");

            if (tutorialsInput == null || labsInput == null ||
                    tutorialsInput.trim().isEmpty() || labsInput.trim().isEmpty()) {
                view.displayError("Required fields for tutorials and labs must not be empty.");
                return;
            }

            int requiredTutorials = Integer.parseInt(tutorialsInput);
            int requiredLabs = Integer.parseInt(labsInput);

            if (requiredTutorials < 0 || requiredLabs < 0) {
                view.displayError("Need 0 or more of each.");
                return;
            }

            boolean success = sharedContext.getCourseManager().addCourse(
                    courseCode, courseName, courseDescription, requiresComputers,
                    courseOrganiserName, courseOrganiserEmail,
                    courseSecretaryName, courseSecretaryEmail,
                    requiredTutorials, requiredLabs
            );

            if (success) {
                view.displaySuccess("Course added successfully");
            } else {
                view.displayError("An unexpected error occurred while adding the course.");
            }

        } catch (NumberFormatException e) {
            view.displayError("Invalid input for required tutorials or labs. Please enter a number.");
        }
    }
    private void removeCourse() {
        view.displayInfo("Available courses:");
        String courseList = sharedContext.getCourseManager().ViewCourses();

        Logger logger = Logger.getInstance();

        if (courseList.isEmpty()) {
            view.displayInfo("No courses available.");
            return;
        } else {
            view.displayInfo(courseList);
        }
        String courseCode = view.getInput("Enter course code: ");
        if (sharedContext.getCourseManager().removeCourse(courseCode)) {
            view.displaySuccess("Course removed successfully");
            logger.log(
                    System.currentTimeMillis(),
                    ((AuthenticatedUser) sharedContext.currentUser).getEmail(),
                    "removeCourse",
                    courseCode,
                    "SUCCESS" + " (Course removed successfully)"
            );
        } else {
            view.displayError("Course not found");
            logger.log(
                    System.currentTimeMillis(),
                    ((AuthenticatedUser) sharedContext.currentUser).getEmail(),
                    "removeCourse",
                    courseCode,
                    "FAILURE" + " (Error: Course could not be removed)"
            );
        }
    }
    private void addActivityToCourse() {
        //TODO: Implement this method
    }
}