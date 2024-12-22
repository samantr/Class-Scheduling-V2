package com.esenyurt.geneticalgorithm;

import com.esenyurt.entity.*;
import com.esenyurt.enums.Hours;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

public class TimeSlotGenerator {

    // Check if a teacher and classroom are available at the given day and time
    public static boolean isTeacherAndClassroomAvailable(
            Teacher teacher,
            Classroom classroom,
            int day,
            int startHour,
            int duration,
            List<Schedule> chromosome
    ) {
        // Check that teacher has no overlapping classes
        if (teacherHasConflict(teacher, day, startHour, duration, chromosome)) {
            return false;
        }

        // Check that classroom has no overlapping classes
        if (classroomHasConflict(classroom, day, startHour, duration, chromosome)) {
            return false;
        }

        return true; // Both are available
    }

    // Check teacher's schedule for conflicts
    private static boolean teacherHasConflict(Teacher teacher, int day, int startHour, int duration, List<Schedule> chromosome) {
        List <TimeSlot> timeSlotList = new ArrayList<>();
        for (Schedule schedule: chromosome) {
            if(schedule.getTeacher().id == teacher.getId())
                timeSlotList.add(schedule.getTimeSlot());
        }

        for (TimeSlot timeSlot: timeSlotList) {
            if(timeSlot.day.getValue() == day)
            {
                int existingStart = timeSlot.getTime().value;
                int existingEnd = existingStart + duration;
                int newEnd = startHour + duration;

                // Check for overlap
                if (startHour < existingEnd && newEnd > existingStart) {
                    return true; // Conflict detected
                }
            }
        }

        return false; // No conflict
    }

    // Check classroom's schedule for conflicts
    private static boolean classroomHasConflict(Classroom classroom, int day, int startHour, int duration, List<Schedule> chromosome) {

        List <TimeSlot> timeSlotList = new ArrayList<>();
        for (Schedule schedule: chromosome) {
            if(schedule.getClassroom().id == classroom.getId())
                timeSlotList.add(schedule.getTimeSlot());
        }

        for (TimeSlot timeSlot: timeSlotList) {
            if(timeSlot.day.getValue() == day)
            {
                int existingStart = timeSlot.getTime().value;
                int existingEnd = existingStart + duration;
                int newEnd = startHour + duration;

                // Check for overlap
                if (startHour < existingEnd && newEnd > existingStart) {
                    return true; // Conflict detected
                }
            }
        }

        return false; // No conflict
    }


}
