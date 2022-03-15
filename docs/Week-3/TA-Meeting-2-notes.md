# Notes meeting week 2
25-11-2021

---

**Spring Security**
 - store the passwords, but hashed or use one-way encryption
 - the TA will send us some resources today/tomorrow
Spring has a lot of built-in functionalities -> built-in triggers to check if something is inserted, updated, or
make a process which checks updates. Student is logged in -> show directly, if not just store the notification
and show it when they are logged in.
--- 
**Microservices Structure**
- use stand-alone modules: Student Service, Lecturer Service...
- the TA showed to Victoria how this should look like
---

**GitLab**
 - !! ADD ALL ISSUES TO GITLAB !!
 - add weights, especially to decide dependencies
 - add time you think it will take to finish the feature
 - at the end of the sprint also add the actual time spent
 - push the Sprint Retrospective Document on GitLab 
 - this should be updated each week with the issues in the respective sprint
 - add everything mentioned above in there too
 
---

**UML**
- for each entity, only add the important functions (communication)
- instead of arrows we could already use the Lollipop notation (Lecture 3)
- Entities as in Lecture 4
- add 2 appropriate design patterns in the UML Design

***Feedback***
- We have too many microservices => combine some of them (draw boundaries from database)
- we do not have primary and foreign keys
- each microservice has its own database
- to get information from another database, a microservice send HTTP Requests to get specific data
- minimum of 2 microservices => so we should have 4-5 maximum.
---

**Gradle Documentation (Modules)**
- with Gradle you can work with modules and sub-modules, making nice dependencies
- you can use a sub-module in another
---

**Notifications**
Spring Security: store hashed password, one-way encryption
Spring has a lot of built-in functionalities -> built-in triggers to check if something is inserted, updated, or 
make a process which checks updates. Student is logged in -> show directly, if not just store the notification 
and show it when they are logged in.
- Spring has a lot of built-in functionalities
- we can use a built-in trigger which checks specific insertions, updates in database
- there are 2 cases: Student is logged in or not
- if the Student is logged in you can show the notifications directly
- if not, store the notification and only show it when the Student logs in
- 
---

**Eureka?**
- we can use Eureka if we have some extra free time, but is optional
- we can use hardcoded ports
---

**Assignment 1**
- UML + 2 design patterns + Architecture
- OUR MAIN FOCUS FOR THIS WEEK!
- postpone coding and finish this assignment by Sunday

