# taskanalyse

Based on Spring Boot.

## Endpoints in the application

**GET /tasks**  
Get all tasks

**GET /tasks/{taskId}**  
Get the task based on ID

**POST /tasks/{taskId}/perform**  
Form data: duration=decimal  

**GET /tasks/{taskId}/average**  
Get the current average running time
 
**POST /tasks/{taskId}/subscribe**  
Form data: url=http://...
  Subscribe to a task's finish and get updated on that URL.
 
 ## Implemented
 - **In-Memory storage**  
    that is replaceable with any database through plugging in different implementation to the interface with Dependnecy of Injection.
 - **Unit tests** for the TaskService and MemoryTaskRepository
 - **Integration tests** for /get, /perform and /average endpoints
 - **Endpoint Caching**
 - **Logging** (*more appenders should be added though*)
 - **Error handling**
 - **3-tier implementation.**
 - **Repository pattern**
 - **Publish/Subscribe pattern**
 - **Dependency Injection**
 
 ### Some of the best practices to follow all the time.
 
 - **Short methods**, as small as possible, possibly less than 20 lines each.
 - **Minimised nested logical blocks** i.e. no nesting if statements or loops.  
    This has a crucial impact on understandable code in long term.
 - **Understandable method names**. If the method changes, update the name immediately.
 - **One definition for one thing everywhere** i.e. not having multiple names for the same thing.
 - **Separation of layers**, 3-tier or n-tier architecture.
 - **Re-usability**. If a code is not re-usable, that should be a crucially important code to stay there, or it should't stay there.
 - **Unit tests** and **Integration tests**
 - **Dependency Injection** to be able to plugin different implementations easily
 - **Comments** to describe business logic where method name is not clear enough. One shouldn't have to read and parse the code in the head to understand what the method is for.
 - **Standardised error handling** everywhere that helps debugging the issues clearly. e.g. don't return HTTP 500 error, throw an exception that will mean HTTP 500 and re-use it.
 - **Continuous refactoring.** It can always be improved.
 
 ### TODO
 - Add Bean validation JSR-303 to validate incoming requests
 - Integrate CI to run tests automatically upon pushing to git repo
 - Setup automated deployments to UAT environment upon successfully passing of all tests after pushing to git repo
 - Add test coverage analysis maven mojos
 - Add PM, Maven code quality  / analysis plugin
 - Add MySQL db implementation for persistent storage i.e. MySQLTaskRepository implements TaskRepository 

PS: pub/sub can have good amount of different scenarios and it can have many tests to cover these scenarios, so skipped them for now. We can discuss about these verbally if you like.
 
 
 ### Diagram
 
 ![Diagram](/_docs/diagram.png)
 
 ### Postman
 Please use the below postman configuration to be able to post to /tasks/{taskId}/perform endpoint.
 
 ![Diagram](/_docs/postman.png)
 
