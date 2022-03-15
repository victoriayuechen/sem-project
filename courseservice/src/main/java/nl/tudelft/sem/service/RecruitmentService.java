package nl.tudelft.sem.service;

import java.util.Collection;
import java.util.stream.Collectors;
import nl.tudelft.sem.repositories.CourseRepository;
import nl.tudelft.sem.repositories.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecruitmentService {
    @Autowired
    private transient CourseRepository courseRepository;
    @Autowired
    private transient GradeRepository gradeRepository;

    /**
     * Constructor for course service.
     *
     * @param courseRepository the course repository.
     * @param gradeRepository the grade repository.
     */
    public RecruitmentService(CourseRepository courseRepository, GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * Retrieves the list of courses the student with username can
     * apply for.
     *
     * @param username The username of the student.
     * @return ResponseEntity containing the list of course codes the TA is eligible for.
     */
    public String getRecruitment(String username) {
        Collection<String> list = gradeRepository.selectApplicableCoursesByUsername(username);

        list = list.stream().filter(s ->
                        courseRepository.getRecruitment(s).isPresent()
                                ? courseRepository.getRecruitment(s).get() : false)
                .collect(Collectors.toList());
        if (list.isEmpty()) {
            return "No applicable courses found with this username.";
        }
        return toOverviewString(list);
    }

    /**
     * Turns a list of course codes into a clear overview.
     *
     * @param courseCodes Collection of course code strings.
     * @return String containing the overview of the course codes.
     */
    private static String toOverviewString(Collection<String> courseCodes) {
        StringBuilder total = new StringBuilder();
        total.append("You are able to apply for:\n");
        for (String string : courseCodes) {
            total.append("\t").append(string).append("\n");
        }
        return total.toString();
    }
}
