package model;
import model.activities.Activity;
import model.activities.Lecture;
import model.activities.Tutorial;
import model.activities.Lab;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;


public class Course {
    private final String courseCode;
    private final String name;
    private final String description;
    private final boolean requiresComputers;
    private final String courseOrganiserName;
    private final String courseOrganiserEmail;
    private final String courseSecretaryName;
    private final String courseSecretaryEmail;
    private final int requiredTutorials;
    private final int requiredLabs;
    private final PriorityQueue<Integer> availableIds = new PriorityQueue<>();
    private int nextId = 0;
    private final List<Activity> activities = new ArrayList<>();

    public Course(String courseCode, String name, String description, boolean requiresComputers,
                  String courseOrganiserName, String courseOrganiserEmail, String courseSecretaryName, String courseSecretaryEmail,
                  int requiredTutorials, int requiredLabs) {
        this.courseCode = courseCode;
        this.name = name;
        this.description = description;
        this.requiresComputers = requiresComputers;
        this.courseOrganiserName = courseOrganiserName;
        this.courseOrganiserEmail = courseOrganiserEmail;
        this.courseSecretaryName = courseSecretaryName;
        this.courseSecretaryEmail = courseSecretaryEmail;
        this.requiredTutorials = requiredTutorials;
        this.requiredLabs = requiredLabs;
    }

    public void addActivity(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
                            String location, DayOfWeek day, String type, Object data) {
        int id;
        if (!availableIds.isEmpty()) {
            id = availableIds.poll();
        } else {
            id = nextId++;
        }

        Activity activity = switch (type) {
            case "Lecture" -> new Lecture(id, startDate, startTime, endDate, endTime, location, day, (Boolean) data);
            case "Tutorial" -> new Tutorial(id, startDate, startTime, endDate, endTime, location, day, (Integer) data);
            case "Lab" -> new Lab(id, startDate, startTime, endDate, endTime, location, day, (Integer) data);
            default -> throw new IllegalArgumentException("Invalid activity type");
        };

        activities.add(activity);
    }
    public void removeActivities() {
        activities.clear();
        availableIds.clear();
        nextId = 0;
    }

    public boolean hasCode(String code){
        return courseCode.equals(code);
    }

    public boolean hasActivity(int id) {
        for (Activity activity : activities) {
            if (activity.hasId(id)) {
                return true;
            }
        }
        return false;
    }

    public String getActivityAsString(int id) {
        for (Activity activity : activities) {
            if (activity.hasId(id)) {
                return activity.toString();
            }
        }
        return null;
    }

    public boolean isUnrecordedLecture(int activityId) {
        for (Activity activity : activities) {
            if (activity.hasId(activityId) && activity instanceof Lecture) {
                return ((Lecture) activity).isRecorded();
            }
        }
        return false;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequiresComputers() {
        return requiresComputers;
    }

    public String getCourseOrganiserName() {
        return courseOrganiserName;
    }

    public String getCourseOrganiserEmail() {
        return courseOrganiserEmail;
    }

    public String getCourseSecretaryName() {
        return courseSecretaryName;
    }

    public String getCourseSecretaryEmail() {
        return courseSecretaryEmail;
    }

    public int getRequiredTutorials() {
        return requiredTutorials;
    }

    public int getRequiredLabs() {
        return requiredLabs;
    }
}
