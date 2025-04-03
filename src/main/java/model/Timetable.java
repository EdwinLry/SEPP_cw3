package model;

import model.timetable.TimeSlot;
import model.timetable.TimeSlotStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class Timetable {
    private final List<TimeSlot> timeSlots = new ArrayList<>();
    private final String studentEmail;

    public Timetable(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public void addTimeSlot(String courseCode, DayOfWeek day, LocalDate startDate, LocalTime startTime,
                            LocalDate endDate, LocalTime endTime, int activityId) {
        TimeSlot timeSlot = new TimeSlot(day, startDate, startTime, endDate, endTime,
                courseCode, activityId, TimeSlotStatus.CHOSEN);
        timeSlots.add(timeSlot);
    }

    public int numChosenActivities(String courseCode) {
        int count = 0;
        for (TimeSlot slot : timeSlots) {
            if (slot.hasCourseCode(courseCode) && slot.isChosen()) {
                count++;
            }
        }
        return count;
    }

    public int[] chosenActivities(String courseCode) {
        List<Integer> activities = new ArrayList<>();
        for (TimeSlot slot : timeSlots) {
            if (slot.hasCourseCode(courseCode) && slot.isChosen()) {
                activities.add(slot.activityId);
            }
        }
        return activities.stream().mapToInt(i -> i).toArray();
    }

    public String[][] checkConflicts(LocalDate startDate, LocalTime startTime,
                                   LocalDate endDate, LocalTime endTime) {
        List<String[]> conflicts = new ArrayList<>();
        for (TimeSlot slot : timeSlots) {
            if (slot.getStartDate().isBefore(endDate) || slot.getEndDate().isAfter(startDate)) {
                if (slot.getStartTime().isBefore(endTime) || slot.getEndTime().isAfter(startTime)) {
                    String[] conflict = {slot.courseCode, slot.activityId + ""};
                    conflicts.add(conflict);
                }
            }
        }
        if(conflicts.isEmpty()){
            return null;
        }
        return conflicts.toArray(new String[0][0]);
    }

    public boolean hasStudentEmail(String email) {
        return email.equals(studentEmail);
    }

    public boolean chooseActivity(String courseCode, int activityId) {
        for (int i = 0; i < timeSlots.size(); i++) {
            TimeSlot slot = timeSlots.get(i);
            if (slot.hasCourseCode(courseCode) && slot.hasActivityId(activityId) && !slot.isChosen()) {
                TimeSlot chosenSlot = new TimeSlot(slot.getDay(), slot.getStartDate(), slot.getStartTime(),
                        slot.getEndDate(), slot.getEndTime(), slot.courseCode, slot.activityId, TimeSlotStatus.CHOSEN);
                timeSlots.set(i, chosenSlot);
                return true;
            }
        }
        return false;
    }

    public boolean hasSlotsForCourse(String courseCode) {
        for (TimeSlot slot : timeSlots) {
            if (slot.hasCourseCode(courseCode)) {
                return true;
            }
        }
        return false;
    }

    public void removeSlotsForCourse(String courseCode) {
        if(!hasSlotsForCourse(courseCode)) {
            return;
        }
        timeSlots.removeIf(slot -> slot.hasCourseCode(courseCode));
    }

    public List<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public String getStudentEmail() {
        return studentEmail;
    }
}
