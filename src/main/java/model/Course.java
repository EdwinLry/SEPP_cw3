package model;
import model.activities.Activity;
import model.activities.Lecture;
import model.activities.Tutorial;
import model.activities.Lab;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;


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
    private final Map<Integer, Activity> activities = new HashMap<>();

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

        activities.put(id, activity);
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
        return activities.containsKey(id);
    }

    public String getActivityAsString(int id) {
        return activities.get(id).toString();
    }

    public String getActivitiesAsString() {
        StringBuilder activitiesList = new StringBuilder();
        for (Activity activity : activities.values()) {
            activitiesList.append(activity.toString());
        }
        return activitiesList.toString();
    }

    public boolean isUnrecordedLecture(int activityId) {
        return activities.get(activityId) instanceof Lecture &&
               !((Lecture) activities.get(activityId)).isRecorded();
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseCode='" + courseCode + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", requiresComputers=" + requiresComputers +
                ", courseOrganiserName='" + courseOrganiserName + '\'' +
                ", courseOrganiserEmail='" + courseOrganiserEmail + '\'' +
                ", courseSecretaryName='" + courseSecretaryName + '\'' +
                ", courseSecretaryEmail='" + courseSecretaryEmail + '\'' +
                ", requiredTutorials=" + requiredTutorials +
                ", requiredLabs=" + requiredLabs +
                ", availableIds=" + availableIds +
                ", nextId=" + nextId +
                ", activities=" + activities +
                '}';
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

    public Map<Integer,Activity> getActivities() {
        return activities;
    }
}
