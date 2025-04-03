package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.activities.Activity;
import model.activities.Lab;
import model.activities.Tutorial;
import utils.Logger;
import view.*;

/**
 * CourseManager is responsible for managing courses, activities, and student timetables.
 * It allows adding, removing, and viewing of. courses and activities.
 * It also handles the assignment of courses to students and checks for conflicts in their timetables.
 */

public class CourseManager {

    private final Map<String,Course> courses = new HashMap<>();
    private final List<Timetable> timetables = new ArrayList<>();
    private final StringBuilder courseList = new StringBuilder();
    private final View view;

    public CourseManager(View view) {
        this.view = view;
    }

    /**
     * @return The course list in "CourseCode - CourseName" format
     */
    public String ViewCourses() {//need to refactor name
        courseList.setLength(0);
        for (Course course : courses.values()) {
            courseList.append(course.getCourseCode()).append(" - ").append(course.getName()).append("\n");
        }
        return courseList.toString();
    }
    /**
     * Check if a course code is valid
     * @param courseCode The course code to check
     * @return true if the course code is valid, false otherwise
     */
    public boolean checkCourseCode(String courseCode) {
        return courses.containsKey(courseCode);
    }

    public boolean hasCourse(String courseCode) {
        return courses.containsKey(courseCode);
    }
    /**
     * Remove a course from the course list
     * @param courseCode The course code to remove
     * @return true if the course was removed, false otherwise
     */
    public boolean removeCourse(String courseCode) {
        if (courses.containsKey(courseCode)) {
            courses.remove(courseCode);
            return true;
        }
        return false;
    }
    /**
     * Add a course to the course list
     */
    public boolean addCourse(String code, String name, String description, boolean requiresComputers,
                             String COName, String COEmail, String CSName, String CSEmail,
                             int reqTutorials, int reqLabs) {
        Course newCourse = new Course(code, name, description, requiresComputers, COName, COEmail,
                CSName, CSEmail, reqTutorials, reqLabs);
        courses.put(code, newCourse);
        return true;
    }

    /**
     * Add an activity to a course, display errors if lecture conflicts appears
     * @param courseCode The course code
     * @param studentEmail The student's email
     */
    public void addCourseToStudentTimetable(String studentEmail, String courseCode) {
        Course course = getCourse(courseCode);
        if(course == null){
            Logger logger = Logger.getInstance();
            logger.log(System.currentTimeMillis(),studentEmail,"addCoursetoStudentTimetable",
                    studentEmail+courseCode,"FAILURE"+"(Error: Incorrect course code provided)");
            view.displayError("Incorrect course code");
            return;
        }

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

        for(Activity activity : course.getActivities().values()) {
            String[][] conflicts = currentTimeTable.checkConflicts(activity.getStartDate(), activity.getStartTime(),
                    activity.getEndDate(), activity.getEndTime());
            if(conflicts != null) {
                boolean isUnrecordedLecture1 = course.isUnrecordedLecture(activity.getId());
                boolean isUnrecordedLecture2 = false;
                Logger logger = Logger.getInstance();
                // Check if the activity is a lecture and if it is recorded
                for(String[] conflict : conflicts){
                    Course conflictingCourse = getCourse(conflict[0]);
                    if(conflictingCourse != null) {
                        isUnrecordedLecture2 = conflictingCourse.isUnrecordedLecture(Integer.parseInt(conflict[1]));
                        if(isUnrecordedLecture2)
                            break;
                    }
                }
                if(isUnrecordedLecture1 && isUnrecordedLecture2) {
                    logger.log(System.currentTimeMillis(),studentEmail,"addCoursetoStudentTimetable",
                            studentEmail+courseCode,
                            "FAILURE"+"(Error: at least one clash with an unrecorded lecture)");
                    view.displayError("You have at least one clash win an unrecorded lecture. " +
                            "The course cannot be added to your timetable");
                    return;
                }
                else{
                    logger.log(System.currentTimeMillis(),studentEmail,"addCoursetoStudentTimetable",
                            studentEmail+courseCode,
                            "FAILURE"+"(Warning: at least one clash with another activity)");
                    view.displayError("You have at least one clash with another activity. ");

                }
            }
            currentTimeTable.addTimeSlot(courseCode, activity.getDay(), activity.getStartDate(), activity.getStartTime(),
                    activity.getEndDate(), activity.getEndTime(), activity.getId());
        }

        int[] chosenActivities = currentTimeTable.chosenActivities(course.getCourseCode());
        Map<Integer,Activity> activities = course.getActivities();

        int count = CheckChosenTutorials(activities,chosenActivities);
        if(count < course.getRequiredTutorials()){
            Logger logger = Logger.getInstance();
            logger.log(System.currentTimeMillis(),studentEmail,"addCoursetoStudentTimetable",
                    studentEmail+courseCode,
                    "FAILURE"+"(Warning: number of required tutorials"+
                            course.getRequiredTutorials() + "not yet chosen)");
            view.displayError("You have to choose " + course.getRequiredTutorials() + " tutorials for this course");
        }

        count = CheckChosenLabs(activities,chosenActivities);
        if(count < course.getRequiredLabs()){
            Logger logger = Logger.getInstance();
            logger.log(System.currentTimeMillis(),studentEmail,"addCoursetoStudentTimetable",
                    studentEmail+courseCode,
                    "FAILURE"+"(Warning: number of required labs"+
                            course.getRequiredLabs() + "not yet chosen)");
            view.displayError("You have to choose " + course.getRequiredLabs() + " labs for this course");
        }

        Logger logger = Logger.getInstance();
        logger.log(System.currentTimeMillis(),studentEmail,"addCoursetoStudentTimetable",
                studentEmail+courseCode,"SUCCESS");
        view.displaySuccess("The course was successfully added to your timetable");
    }
    /**
     * Adds an activity to a student timetable
     * @param studentEmail The student's email
     * @param courseCode The course code
     * @param activityId The activity ID
     */
    public void chooseActivityForCourse(String studentEmail, String courseCode, int activityId) {
        Course course = getCourse(courseCode);
        if (course == null) {
            Logger logger = Logger.getInstance();
            logger.log(System.currentTimeMillis(), studentEmail, "chooseActivityForCourse",
                    studentEmail + courseCode, "FAILURE" + "(Error: Incorrect course code provided)");
            view.displayError("Incorrect course code");
            return;
        }

        Timetable currentTimeTable = null;
        boolean found = false;
        for (Timetable timetable : timetables) {
            if (timetable.hasStudentEmail(studentEmail)) {
                currentTimeTable = timetable;
                found = true;
                break;
            }
        }
        if (!found) {
            Logger logger = Logger.getInstance();
            logger.log(System.currentTimeMillis(), studentEmail, "chooseActivityForCourse",
                    studentEmail + courseCode, "FAILURE" + "(Error: No timetable found for the student)");
            view.displayError("No timetable found for the student");
            return;
        }
        if (currentTimeTable.chooseActivity(courseCode, activityId)) {
            Logger logger = Logger.getInstance();
            logger.log(System.currentTimeMillis(), studentEmail, "chooseActivityForCourse",
                    studentEmail + courseCode, "SUCCESS");
            view.displaySuccess("The activity was successfully chosen");
        } else {
            Logger logger = Logger.getInstance();
            logger.log(System.currentTimeMillis(), studentEmail, "chooseActivityForCourse",
                    studentEmail + courseCode, "FAILURE" + "(Error: Activity not found or already chosen)");
            view.displayError("Activity not found or already chosen");
        }
    }

    private int CheckChosenTutorials(Map<Integer,Activity> activities, int[] chosenActivities){
        int count = 0;
        for(int tutorialId : chosenActivities){
            if(activities.get(tutorialId) instanceof Tutorial){
                count++;
            }
        }
        return count;
    }

    private int CheckChosenLabs(Map<Integer,Activity> activities, int[] chosenActivities){
        int count = 0;
        for(int labId : chosenActivities){
            if(activities.get(labId) instanceof Lab){
                count++;
            }
        }
        return count;
    }
    public Course getCourse(String courseCode) {return courses.get(courseCode);}
    /**
     * Display the timetable for a student
     */
    public void viewTimetable(String email){
        Timetable currentTimeTable = null;
        boolean found = false;
        for (Timetable timetable : timetables) {
            if (timetable.hasStudentEmail(email)) {
                currentTimeTable = timetable;
                found = true;
                break;
            }
        }
        if (!found) {
            view.displayError("No timetable found for the student");
            return;
        }
        view.displayTimetable(currentTimeTable);
    }
    public void viewCourses(){
        for(Course course : courses.values()){
            view.displayCourse(course);
        }
    }
    public void viewCourse(String name){
        view.displayCourse(courses.get(name));
    }
    private Map<String,Course> getCourses(){return courses;}
    public Timetable getTimetable(String email){
        for(Timetable timetable : timetables){
            if(timetable.hasStudentEmail(email)){
                return timetable;
            }
        }
        return null;
    }

}
