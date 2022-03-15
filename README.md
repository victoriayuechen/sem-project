# CSE2115 - Project

### Contributors

1. Radu Mihalachiuta - R.G.Mihalachiuta@student.tudelft.nl
2. Victoria Yue Chen - Y.Chen-72@student.tudelft.nl
3. Alex Cazacu - A.C.Cazacu@student.tudelft.nl
4. Laurens de Swart - L.J.P.deSwart@student.tudelft.nl
5. Thomas de Valck - T.J.DeValck@student.tudelft.nl
6. Quinten Gevers - Q.M.Gevers@student.tudelft.nl

### Running 
1. Pray to your god(s).
2. Run the Main class in ```discovery-service```.
3. Run the Main class in ```gateway-service```.
4. Run the Main class in the rest of the services you will use.
5. Go to ```http://localhost:8761/``` in any browser.
6. Now you should see that all the microservices have been registered.

### Postman

1. After running the application accordingly, enter Postman.
2. You will first need to authenticate through a POST request with the following URL: http://localhost:8400/authentication/login.
3. For the Body of this request you can use the following User Object:
```json
   {
   "username": "admin",
   "password": "adminpassword",
   "roles": ["admin"]
   }
```
4. You will receive a JWT Token as a response. Copy-paste it in the authorization field. Make sure to choose the "Bearer Toeken" type.
5. Now you can access all other endpoints through this token. Change the URL with the desired endpoint and enjoy!

### Microservices
 
 - Use this server ports for the URL in specific microservices.

1. ApplicationService - 8500
2. AuthenticationService - 8400
3. CourseService - 8300
4. DiscoveryService - 8761 
5. GatewayService - 8762
6. NotificationService - 8200
7. TaService - 8100

### Testing
```
gradle test
```

To generate a coverage report:
```
gradle jacocoTestCoverageVerification
```


And
```
gradle jacocoTestReport
```
The coverage report is generated in: build/reports/jacoco/test/html, which does not get pushed to the repo. Open index.html in your browser to see the report. 

### Static analysis
```
gradle checkStyleMain
gradle checkStyleTest
gradle pmdMain
gradle pmdTest
```

### Notes
- You should have a local .gitignore file to make sure that any OS-specific and IDE-specific files do not get pushed to the repo (e.g. .idea). These files do not belong in the .gitignore on the repo.
- If you change the name of the repo to something other than template, you should also edit the build.gradle file.
- You can add issue and merge request templates in the .gitlab folder on your repo. 