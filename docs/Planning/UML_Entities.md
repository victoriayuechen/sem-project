# UML Entities 

## User 
**Attributes**:
- Username (String)
- Password (String)
- Regular Name (String)

**Methods**: 

## Student 
*inherits User* 

**Attributes**:
- Experiences (`List<String> courseCodes`)

**Methods**: 
- `applyforPosition(course code, username)`: *creates an application. Check for number of courses.*
- `getApplicationsPerQuarter(quarter)`: *returns the courses per quarter.*
- `getOpenCourses()`: *This displays courses I can apply for.* 
- `getApplications(username)`: *This displays the courses I applied for.*
- `getNotifications(username)`: *This displays the notifications.* 
- `signContract(contract ID)`: *Signs the contract.* 
- `getContract(course code, username)`: *if student is selected, then contract can be created and retrieved.*
- `cancelApplication(course code, username)`: *deletes an application.*
- (private) `isTA(username, course code)`: *checks if the user is a TA.* 
- `declareHours(course code, username, hours)`: *creates a WorkLoad object*.
- `getAverageRatings(username)`: *gets the TA's ratings if any.*
- `displayReviews(username)`: *displays the reviews of a TA.* 

## WorkLoad 
**Attributes**:
- Course Code (FK) 
- Username (FK) 
- Total Hours
- Status (Enum)
- statusUpdated (Boolean)

**Methods**: 
- `getStatus(course code, username)`: *for notifications*
- `setStatus(course code, username)`: *for lecturers* 

## Lecturer 
*inherits User* 

**Attributes**:
- (Implicitly) Courses (`List<String> course codes`) 

**Methods**: 
- `getApplicants(course code)`: *show applicants for a specific course.*
- `recommendApplicants(course code)`: *recommends applicants for a specific course based on selection criteria. Get application and then join again on grade table.*
- `selectCandidate(application ID)`: *Selects student as TA, updates status.*
- `rejectCandidate(application ID)`: *Rejects the student as TA.* 
- `closeApplications(course code)`: *closes the course for recruitment, and then gets all applicants and then rejects them too.*
- `checkWorkLoads(course code)`: *checks the workloads declared by TAs of this course.*
- `approveWorkLoad(course code, username)`: *approves work load declared by TA.* 
- `rejectWorkload(course code, username)`: *rejects a work load delcared by TA.*
- `createReview(username, course code)`: *creates a review for a TA*


## Lecturer-Course 
**Attributes**:
- Username (associated lecturer)
- Course Code (Assocated course)

**Methods**: 
- To be determined 

## Review 
**Attributes**:
- Username 
- Course Code 
- Text (String) 
- Rating (Integer 1 - 10 stars)

**Methods**: 
- `setReview(text)`: *writes a review* 
- `setRating(rating)`: *rates a student*


## Course 
**Attributes**:
- Course Name (String)
- Quarter (Short)
- Course Code (String)
- Open For Recruitment (Boolean)
- taCapacity (Integer)

**Methods**: 
- `displayAverageHours(course code)`: *returns the avergae number of hours a TA needs to spend*. 

## Grade 
*(links course and student)*

**Attributes**:
- Course Code (FK)
- Username (FK)
- Grade (Float)

**Methods**: 
- `getGrade(course code, username)`: *Returns grade of student for a given course.* 

## Application 
**Attributes**:
- Application ID (PK)
- Course Code (String, FK)
- Username (String, FK)
- Status (Enum)
- statusUpdated (Boolean)

**Methods**: 
- `getStatus(course code, username)`: *for notifications*
- `setStatus(course code, username)`: *for lecturers* 


## Contracts 
**Attributes**:
- Application ID (FK)
- Contract ID (PK)
- Signed (Boolean)

**Methods**: 
- `createContract(Application ID)`: *returns an contract for this application*. 




