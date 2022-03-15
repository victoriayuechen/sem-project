# Questions - Group 18A 

## Questions about Scenario Description 
According to the Scenario description:
> Lecturers can also ask the system to recommend the candidate TAs to hire based on their past grades and previous experience in TAing. 

- **Question**: How should the recommendation system prioritise for the recommendations? Should experience be more important or past grades be more important? Should it something that can be adjusted by the lecturer (change what the system recommends on)? 

> The total number of declared hours for a given course cannot be greater than the total amount of hours indicated in the TA contract. For the payment, the responsible lecturers will need to approve the hours declared by the TAs? 
- **Question**: Should the system be responsible for *sending* the payments and processing the payments? Or is it that the system is only responsible for storing payment information? 

## Questions about Architecture 
- What kind of database system is recommended and if so, does the choice affect our final grade? Does it need persistence storage? Should we use PostGres or H2? 
- What type of notification system is required? Should the system be able to send actual emails to real email addresses? Or is it okay for the system to only provide an notification to the user through the application(like a simple ping)? 
- Is an Admin role necessary in this case? If we, the developers, have access to the database and other functionality, is that sufficient? Or should we have a special role that allows access for database/other functionality? 

----
# Meeting Notes 

**Answer to q1**: 
- Not very relevant, freedo to choose the recommendation algorithm. 
- Could be adjusted by the lecture -> COULD HAVE 

**Answer to q2**:
- 8 hours -> Responsible lecturer. 
- Notified about declined. 
- Simply declared the number of hours. 
- Disclaim the message that it was approved and store in database

**Answer to q3**:
- Persistent storage - remote database
- Digital Ocean. Amazon AWS

**Answer to q4**: 
- Student logs in and receives a notification. 
- Real time: Push based notification. 
- Research how push based notifcations work. 

**Answer to q5**:
- Admin role is needed
- Admin should be could haves
- Must Have: Data on the database. Can access the database. 
- Adding the course => Admin open courses. 
- Admin after creating course can add the lecturer to the course. 