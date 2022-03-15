package nl.tudelft.sem.util;

import java.time.ZonedDateTime;
import nl.tudelft.sem.entities.Course;

public class CourseChecks {
    /**
     * Checks if course has enough TAs.
     * The ratio is a variable.
     *
     * @param course the course in instance.
     * @return true if there are already enough TAs for this course, false otherwise.
     */
    public static boolean courseHasEnoughTas(Course course) {
        return course.getNumberOfStudents() / course.getStudentTaRatio() < course.getNumberOfTas();
    }


    /**
     * Checks if the deadline (3 weeks) for registration has passed.
     *
     * @param course the course in instance.
     * @return true if the deadline has passed, false otherwise.
     */
    public static boolean hasDeadlinePassed(Course course) {

        if (course.getStartDate() == null) {
            return false;
        }

        int currentYear = ZonedDateTime.now().getYear();
        int deadlineYear = course.getStartDate().minusWeeks(3).getYear();
        if (currentYear > deadlineYear) {
            return true;
        }
        int currentMonth = ZonedDateTime.now().getMonthValue();
        int openMonth = course.getStartDate().minusWeeks(3).getMonthValue();
        if (currentYear == deadlineYear && currentMonth > openMonth) {
            return true;
        }
        int presentDay = ZonedDateTime.now().getDayOfMonth();
        int deadline = course.getStartDate().minusWeeks(3).getDayOfMonth();
        return currentYear == deadlineYear && currentMonth == openMonth && presentDay > deadline;
    }
}
