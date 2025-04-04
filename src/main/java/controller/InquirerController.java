package controller;

import external.AuthenticationService;
import external.EmailService;
import model.*;
import model.FAQ.FAQSection;
import view.View;

public class InquirerController extends Controller {
    public InquirerController(SharedContext sharedContext, View view, AuthenticationService auth, EmailService email) {
        super(sharedContext, view, auth, email);
    }

    public void consultFAQ() {
        FAQSection currentSection = null;
        String userEmail;

        if (sharedContext.currentUser instanceof AuthenticatedUser) {
            userEmail = ((AuthenticatedUser) sharedContext.currentUser).getEmail();
        } else {
            userEmail = null;
        }

        while (true) {
            if (currentSection == null) {
                view.displayFAQ(sharedContext.getFAQ());
                view.displayInfo("[-1] Return to main menu");
            } else {
                view.displayFAQSection(currentSection);
                view.displayInfo("[-1] Return to " + (currentSection.getParent() == null ? "FAQ" : currentSection.getParent().getTopic()));
            }

            String input = view.getInput("Please choose an option: ");

            try {
                int optionNo = Integer.parseInt(input);

                if (optionNo == -1) {
                    if (currentSection == null) {
                        break;  // Exit to main menu
                    } else if (currentSection.getParent() == null) {
                        currentSection = null;  // Go back to FAQ root
                    } else {
                        currentSection = currentSection.getParent();  // Go up one level
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

    public void contactStaff() {
        String inquirerEmail;
        String staffEmail = SharedContext.ADMIN_STAFF_EMAIL;

        if (sharedContext.currentUser instanceof AuthenticatedUser) {
            AuthenticatedUser user = (AuthenticatedUser) sharedContext.currentUser;
            inquirerEmail = user.getEmail();
        } else {
            inquirerEmail = view.getInput("Enter your email address: ");
            // From https://owasp.org/www-community/OWASP_Validation_Regex_Repository
            if (!inquirerEmail.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
                view.displayError("Invalid email address!");
                return;
            }
        }

        boolean withTag = view.getYesNoInput("Would you like to provide a course code for this inquiry?");

        if (withTag) {
            if(sharedContext.courseManager.viewCoursesFormatted() == ""){
                view.displayError("No courses currently in the system.");
                return;
            }
            String courseCode = view.getInput("Please enter course code:");
            if(sharedContext.courseManager.checkCourseCode(courseCode)){
                Course course = sharedContext.courseManager.getCourse(courseCode);
                staffEmail = course.getCourseOrganiserEmail();
            }
            else{
                view.displayError("Invalid course code. No course found with code '" + courseCode + "'.");
                return;
            }
        }

        String subject = view.getInput("Describe the topic of your inquiry in a few words: ");
        if (subject.strip().isBlank()) {
            view.displayError("Inquiry subject cannot be blank! Please try again");
            return;
        }

        String text = view.getInput("Write your inquiry:" + System.lineSeparator());
        if (text.strip().isBlank()) {
            view.displayError("Inquiry content cannot be blank! Please try again");
            return;
        }

        Inquiry inquiry = new Inquiry(inquirerEmail, subject, text);
        sharedContext.inquiries.add(inquiry);

        email.sendEmail(
                inquirerEmail,
                staffEmail,
                "New inquiry from " + inquirerEmail,
                "Subject line: " + subject + System.lineSeparator() + "Please log into the Self Service Portal to review and respond to the inquiry."
        );


        view.displaySuccess("Your inquiry has been recorded. Someone will be in touch via email soon!");
    }
}
