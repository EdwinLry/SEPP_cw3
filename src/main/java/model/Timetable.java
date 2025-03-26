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

    public String[] checkConflicts(LocalDate startDate, LocalTime startTime,
                                   LocalDate endDate, LocalTime endTime) {
        List<String> conflicts = new ArrayList<>();
        LocalDateTime newStart = LocalDateTime.of(startDate, startTime);
        LocalDateTime newEnd = LocalDateTime.of(endDate, endTime);
        for (TimeSlot slot : timeSlots) {
            if (slot.isChosen()) {
                LocalDateTime slotStart = LocalDateTime.of(slot.getStartDate(), slot.getStartTime());
                LocalDateTime slotEnd = LocalDateTime.of(slot.getEndDate(), slot.getEndTime());
                if (newStart.isBefore(slotEnd) && newEnd.isAfter(slotStart)) {
                    conflicts.add("Conflict with course " + slot.courseCode +
                            " activity " + slot.activityId);
                }
            }
        }
        return conflicts.toArray(new String[0]);
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
        timeSlots.removeIf(slot -> slot.hasCourseCode(courseCode));
    }
}
