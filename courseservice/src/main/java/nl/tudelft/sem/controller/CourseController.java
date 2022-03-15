package nl.tudelft.sem.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.AverageWorkload;
import nl.tudelft.sem.CourseInfo;
import nl.tudelft.sem.ErrorString;
import nl.tudelft.sem.GradeInfo;
import nl.tudelft.sem.entities.Course;
import nl.tudelft.sem.entities.Grade;
import nl.tudelft.sem.exceptions.EmptyTargetException;
import nl.tudelft.sem.exceptions.InvalidCourseException;
import nl.tudelft.sem.service.CourseService;
import nl.tudelft.sem.service.GradeService;
import nl.tudelft.sem.service.RecruitmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/courses")
@SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "PMD.AvoidDuplicateLiterals"})
public class CourseController {
    @Autowired
    private transient CourseService courseService;
    @Autowired
    private transient GradeService gradeService;
    @Autowired
    private transient RecruitmentService recruitmentService;

    /**
     * Constructor for Course Controller.
     *
     * @param courseService Course Service.
     * @param gradeService  Grade Service.
     */
    public CourseController(GradeService gradeService,
                            CourseService courseService) {
        this.gradeService = gradeService;
        this.courseService = courseService;
    }

    /**
     * Obtains the quarter associated with a course.
     *
     * @param request    The request object.
     * @param courseCode The course code of the course in question.
     * @return The quarter during which these course is held.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_LECTURER')")
    @GetMapping("/getCourseQuarter/{courseCode}")
    public ResponseEntity<?> obtainCourseQuarter(HttpServletRequest request,
                                          @PathVariable String courseCode) {
        try {
            int courseQuarter = courseService.getCourseQuarter(courseCode);
            return ResponseEntity
                .ok()
                .body(courseQuarter);
        } catch (EmptyTargetException e) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorString(e.getMessage()));
        }
    }

    /**
     * Retrieves the list of courses the student with username can
     * apply for.
     *
     * @param request  The request object.
     * @param username The username of the student.
     * @return ResponseEntity containing the list of course codes the TA is eligible for.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT', 'ROLE_ADMIN')")
    @GetMapping("/getRecruitment/{username}")
    public ResponseEntity<?> getRecruitment(HttpServletRequest request,
                                            @PathVariable String username) {
        return ResponseEntity.ok(recruitmentService.getRecruitment(username));
    }


    /**
     * Retrieves the openForRecruitment status of a course.
     *
     * @param courseCode The course code of the course.
     * @return ResponseEntity containing the openForRecruitment boolean.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_LECTURER', 'ROLE_STUDENT')")
    @GetMapping("/courseOpen/{courseCode}")
    public Boolean openForRecruitment(@PathVariable String courseCode) {
        return courseService.openForRecruitment(courseCode);
    }

    /**
     * Closes the course for registration.
     * It already has enough TAs.
     *
     * @param courseCode the course code of the respective course.
     * @return the course code of the updated course.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_LECTURER')")
    @PutMapping("/closeCourseEnoughTas/{courseCode}")
    public ResponseEntity<?> closeCourseHasEnoughTas(HttpServletRequest request,
                                         @PathVariable String courseCode) {
        try {
            String code = courseService.closeCourseEnoughTas(courseCode);
            return ResponseEntity
                    .ok()
                    .body(code);
        } catch (IOException | InterruptedException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorString("Error occurred in corresponding service."));
        } catch (InvalidCourseException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        }

    }

    /**
     * Closes the course for registration.
     * Checks whether the deadline has passed or not.
     *
     * @param courseCode the course code of the respective course.
     * @return the course code of the updated course.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_LECTURER')")
    @PutMapping("/closeCourseDeadline/{courseCode}")
    public ResponseEntity<?> closeCourseDeadlineHasPassed(HttpServletRequest request,
                                                     @PathVariable String courseCode) {
        try {
            String code = courseService.closeCourseDeadlinePassed(courseCode);
            return ResponseEntity
                    .ok()
                    .body(code);
        } catch (IOException | InterruptedException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorString("Error occurred in corresponding service."));
        } catch (InvalidCourseException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        }
    }

    /** Closes the course for recruitment.
     *
     * @param request the client request.
     * @param courseCode the courseCode of the course.
     * @return the courseCode of the closed course.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_LECTURER')")
    @PutMapping("/closeCourse/{courseCode}")
    public ResponseEntity<?> closeCourse(HttpServletRequest request,
                                         @PathVariable String courseCode) {
        try {
            Course course = courseService.closeCourse(courseCode);
            return ResponseEntity
                    .ok()
                    .body(course);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        } catch (InterruptedException | IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorString("Error when communicating with other services."));
        }

    }

    /**
     * This method adds a course.
     *
     * @param course the course to be added.
     * @return the course code.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PostMapping("/addCourse")
    public ResponseEntity<?> addCourse(HttpServletRequest request,
                                       @RequestBody Course course) {

        try {
            Course course1 = courseService.addCourse(course);
            return ResponseEntity
                    .ok()
                    .body(course1.getCourseCode());
        } catch (IOException | InterruptedException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorString("Error occurred in corresponding service."));
        } catch (InvalidCourseException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        }

    }

    /**
     * Method for retrieving grade of student
     * for a particular course.
     *
     * @param request    The request body.
     * @param courseCode The course code for the grade.
     * @param userName   The user for which to retrieve.
     * @return The grade (double) of the user for that course.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_LECTURER', 'ROLE_STUDENT')")
    @GetMapping(path = "/courseGrade/{courseCode}/{userName}")
    public ResponseEntity<?> getCourseGrade(HttpServletRequest request,
                                            @PathVariable String courseCode,
                                            @PathVariable String userName) {
        try {
            Grade grade = gradeService.getGradeForStudent(userName, courseCode);
            return ResponseEntity
                .ok()
                .body(grade.getValue());
        } catch (EmptyTargetException e) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorString(e.getMessage()));
        }
    }

    /**
     * Retrieves the average workload hours per week as indicated by previous TAs.
     *
     * @param courseCode The course code.
     * @return The average number of hours a TA needs to spend per week.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_LECTURER')")
    @GetMapping("/averageWorkload/{courseCode}")
    public ResponseEntity<?> averageWorkloadPerCourse(HttpServletRequest request,
                                                      @PathVariable String courseCode) {
        String token = request.getHeader("Authorization");
        try {
            // String token = request.getHeader("Authorization");

            AverageWorkload averageWorkload = courseService
                .obtainAverageWorkloadPerCourse(courseCode, token);

            return ResponseEntity.ok().body(averageWorkload);
        } catch (InvalidCourseException | EmptyTargetException e) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorString(e.getMessage()));
        } catch (InterruptedException | IOException e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorString("Error occurred with"
                    + " communicating with different services."));
        }
    }

    /**
     * Endpoint to update a Course entity.
     *
     * @param request the request object
     * @param courseInfo The (updated) CourseInfo entity
     * @return The updated Course entity as confirmation
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_LECTURER')")
    @PutMapping("/course/updateCourse")
    public ResponseEntity<?> updateCourse(HttpServletRequest request,
                                          @RequestBody CourseInfo courseInfo) {
        try {
            CourseInfo updatedCourse = courseService.updateCourse(courseInfo);
            return ResponseEntity.ok(updatedCourse);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        }
    }

    /**
     * Deletes a Course by ID.
     *
     * @param request the request object
     * @param courseCode The courseCode ID
     * @return The deleted Course entity
     */
    @DeleteMapping("/course/deleteCourse/{courseCode}")
    public ResponseEntity<?> deleteCourse(HttpServletRequest request,
                                          @PathVariable String courseCode) {
        try {
            Course course = courseService.deleteCourse(courseCode);
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        }
    }

    /**
     * Endpoint to update a Grade entity.
     *
     * @param request the request object
     * @param gradeInfo The (updated) GradeInfo entity
     * @return The updated Grade entity as confirmation
     */
    @PutMapping("/grade/updateGrade")
    public ResponseEntity<?> updateGrade(HttpServletRequest request,
                                         @RequestBody GradeInfo gradeInfo) {
        try {
            GradeInfo updatedGrade = gradeService.updateGrade(gradeInfo);
            return ResponseEntity.ok(updatedGrade);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        }
    }

    /**
     * Deletes a Grade by ID.
     *
     * @param request the request object
     * @param gradeId The Grade ID as a String
     * @return The deleted Grade entity
     */
    @DeleteMapping("/grade/deleteGrade/{gradeId}")
    public ResponseEntity<?> deleteGrade(HttpServletRequest request,
                                         @PathVariable String gradeId) {
        try {
            Grade grade = gradeService.deleteGrade(gradeId);
            return ResponseEntity.ok(grade);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        }
    }

    /** Changes the ta:student ratio of the given course.
     *
     * @param courseCode The course code.
     * @param ratio The new ratio.
     * @return The new student ratio.
     */
    @PutMapping("/changeRatio/{courseCode}/{ratio}")
    public ResponseEntity<?> changeRatio(@PathVariable String courseCode,
                                         @PathVariable int ratio) {
        try {
            courseService.changeRatio(courseCode, ratio);

            return ResponseEntity.ok().body(ratio);
        } catch (InvalidCourseException | EmptyTargetException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        } catch (InterruptedException | IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorString("Error occurred with"
                            + " communicating with different services."));
        }
    }
}
