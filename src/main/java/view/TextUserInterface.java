package view;

import model.*;
import model.FAQ.FAQItem;
import model.FAQ.FAQSection;
import model.timetable.TimeSlot;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

public class TextUserInterface implements View {
    private final Scanner scanner = new Scanner(System.in);
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RESET = "\u001B[0m";

    @Override
    public String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    @Override
    public boolean getYesNoInput(String prompt) {
        System.out.println(prompt + " [Y/n]");
        String line = scanner.nextLine();
        if (line.equalsIgnoreCase("y") || line.equalsIgnoreCase("yes")) {
            return true;
        } else if (line.equalsIgnoreCase("n") || line.equalsIgnoreCase("no")) {
            return false;
        }
        return Boolean.parseBoolean(line);
    }

    @Override
    public void displayInfo(String text) {
        System.out.println(text);
    }

    @Override
    public void displaySuccess(String text) {
        System.out.println(ANSI_GREEN + text + ANSI_RESET);
    }

    @Override
    public void displayWarning(String text) {
        System.out.println(ANSI_YELLOW + text + ANSI_RESET);
    }

    @Override
    public void displayError(String text) {
        System.out.println(ANSI_RED + text + ANSI_RESET);
    }

    @Override
    public void displayException(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        displayError(sw.toString());
    }

    @Override
    public void displayDivider() {
        System.out.println("-------------------------");
    }

    @Override
    public void displayFAQ(FAQManager faqManager) {
        System.out.println("Frequently Asked Questions");
        displayDivider();
        if(faqManager.getSections().isEmpty()){
            System.out.print("FAQ is Empty.\n");
        }
        else{
            int i = 0;
            for (FAQSection section : faqManager.getSections()) {
                System.out.print("[");
                System.out.print(i++);
                System.out.print("] ");
                System.out.println(section.getTopic());
            }
        }
    }

    @Override
    public void displayFAQSection(FAQSection section) {
        System.out.println(section.getTopic());
        displayDivider();

        // Display super-topics
        if (section.getParent() != null) {
            System.out.println("Direct Super-Topics:");
            System.out.println(section.getParent().getTopic());
            System.out.println();
        }

        // Display FAQ items
        if (section.getItems().isEmpty()) {
            System.out.println("No FAQ items in this section.");
        } else {
            for (FAQItem item : section.getItems()) {
                System.out.print("[");
                System.out.print(item.getId());
                System.out.print("] ");
                System.out.println(item.getQuestion());
                System.out.print("> ");
                System.out.println(item.getAnswer());
            }
        }
        System.out.println();

        // Display subsections

        if (!section.getSubsections().isEmpty()) {
            System.out.println("Direct Subsections:");
            int i = 0;
            for (FAQSection subsection : section.getSubsections()) {
                System.out.print("[");
                System.out.print(i++);
                System.out.print("] ");
                System.out.println(subsection.getTopic());
            }
            System.out.println();
        }
    }

    @Override
    public void displayInquiry(Inquiry inquiry) {
        System.out.println("Inquirer: " + inquiry.getInquirerEmail());
        System.out.println("Created at: " + inquiry.getCreatedAt());
        System.out.println("Assigned to: " + (inquiry.getAssignedTo() == null ? "No one" : inquiry.getAssignedTo()));
        System.out.println("Query:");
        System.out.println(inquiry.getContent());
    }

    @Override
    public void displayCourse(Course course){
        System.out.println("Course Code: " + course.getCourseCode());
        System.out.println("Course Name: " + course.getName());
        System.out.println("Course Description: " + course.getDescription());
        System.out.println("Requires Computers: " + course.isRequiresComputers());
        System.out.println("Course Organiser Name: " + course.getCourseOrganiserName());
        System.out.println("Course Organiser Email: " + course.getCourseOrganiserEmail());
        System.out.println("Course Secretary Name: " + course.getCourseSecretaryName());
        System.out.println("Course Secretary Email: " + course.getCourseSecretaryEmail());
        System.out.println("Required Tutorials: " + course.getRequiredTutorials());
        System.out.println("Required Labs: " + course.getRequiredLabs());
    }
    public void displayTimetable(Timetable timetable) {
        System.out.println("Timetable for " + timetable.getStudentEmail());
        System.out.println("Time Slots:");
        for (TimeSlot timeSlot : timetable.getTimeSlots()) {
            System.out.println("Course Code: " + timeSlot.courseCode);
            System.out.println("Day: " + timeSlot.getDay());
            System.out.println("Start Date: " + timeSlot.getStartDate());
            System.out.println("Start Time: " + timeSlot.getStartTime());
            System.out.println("End Date: " + timeSlot.getEndDate());
            System.out.println("End Time: " + timeSlot.getEndTime());
            System.out.println("Activity ID: " + timeSlot.activityId);
            System.out.println("-------------------------");
        }
    }
}
