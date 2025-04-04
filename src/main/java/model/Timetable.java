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
                courseCode, activityId, TimeSlotStatus.UNCHOSEN);
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

    /**
     * Get the chosen activities for a given course code.
     * Prepare for further verification of number of chosen labs and tutorials.
     * @param courseCode the course code to check
     * @return an array of activity IDs that are chosen for the given course code
     */
    public int[] chosenActivities(String courseCode) {
        List<Integer> activities = new ArrayList<>();
        for (TimeSlot slot : timeSlots) {
            if (slot.hasCourseCode(courseCode) && slot.isChosen()) {
                activities.add(slot.activityId);
            }
        }
        return activities.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Check for conflicts with the given time slot.
     * Returns a 2D array of for further processing to check whether the conflicts are unrecorded lectures.
     * @param startDate start date of the time slot
     * @param startTime start time of the time slot
     * @param endDate end date of the time slot
     * @param endTime end time of the time slot
     * @param day day of the week
     * @return 2D array of strings, where each string is a course code and activity ID of the conflicting time slots.
     */
    public String[][] checkConflicts(LocalDate startDate, LocalTime startTime,
                                     LocalDate endDate, LocalTime endTime, DayOfWeek day) {
        LocalDateTime start = LocalDateTime.of(startDate, startTime);
        LocalDateTime end = LocalDateTime.of(endDate, endTime);

        List<String[]> conflicts = new ArrayList<>();

        for (TimeSlot slot : timeSlots) {
            // Check if the slot is on the same day and has been chosen
            if (slot.getDay() == day && slot.isChosen()) {
                LocalDateTime slotStart = LocalDateTime.of(slot.getStartDate(), slot.getStartTime());
                LocalDateTime slotEnd = LocalDateTime.of(slot.getEndDate(), slot.getEndTime());

                // Check if the new time slot overlaps with the existing slot.
                if ((start.isBefore(slotEnd) && end.isAfter(slotStart))
                        || start.isEqual(slotStart) || end.isEqual(slotEnd)) {
                    conflicts.add(new String[]{slot.courseCode, String.valueOf(slot.activityId)});
                }
            }
        }
        // Return null if no conflicts were found.
        if (conflicts.isEmpty()) {
            return null;
        }
        return conflicts.toArray(new String[0][]);
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
