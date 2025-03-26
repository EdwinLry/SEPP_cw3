package model.timetable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class TimeSlot {
    private final DayOfWeek day;
    private final LocalDate startDate;
    private final LocalTime startTime;
    private final LocalDate endDate;
    private final LocalTime endTime;
    public final String courseCode;
    public final int activityId;
    public final TimeSlotStatus status;

    public TimeSlot(DayOfWeek day, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
                    String courseCode, int activityId, TimeSlotStatus status) {
        this.day = day;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.courseCode = courseCode;
        this.activityId = activityId;
        this.status = status;
    }

    public boolean hasCourseCode(String courseCode) {
        return this.courseCode.equals(courseCode);
    }

    public boolean hasActivityId(int activityId) {
        return this.activityId == activityId;
    }

    public boolean isChosen() {
        return status == TimeSlotStatus.CHOSEN;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}