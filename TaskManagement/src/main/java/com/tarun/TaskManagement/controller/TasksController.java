package com.tarun.TaskManagement.controller;

import com.tarun.TaskManagement.exception.ApiResponseModel;
import com.tarun.TaskManagement.model.Tasks;
import com.tarun.TaskManagement.model.Users;
import com.tarun.TaskManagement.service.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class TasksController {

    private final TasksService service;

    @Autowired
    TasksController(TasksService service){
        this.service = service;

    }



    // CONTROLLERS FOR USERS

    // to get all the tasks of the user
    @GetMapping("/tasks")
    public ResponseEntity<ApiResponseModel<List<Tasks>>> getTasks(){
        ApiResponseModel<List<Tasks>> response = new ApiResponseModel<>(true,"Fetched all Tasks", HttpStatus.OK.value(), service.getTasks());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/task/{id}")
    public ResponseEntity<ApiResponseModel<Tasks>>  getTask(@PathVariable int id) throws IllegalAccessException {
        ApiResponseModel<Tasks> response = new ApiResponseModel<>(true,"Task found.",HttpStatus.OK.value(), service.getTaskById(id));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    //to get all the tasks of the user, and then it is filtered with the given keyword
    @GetMapping("/tasks/search")
    public ResponseEntity<ApiResponseModel<List<Tasks>>> searchTasks(@RequestParam(required = false) String title,
                                   @RequestParam(required = false) String description,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(required = false) String priority,
                                   @RequestParam(required = false) Boolean overdue,
                                   @RequestParam(required = false) Boolean excludeCompleted)
    {
        ApiResponseModel<List<Tasks>> response = new ApiResponseModel<>(
                true,
                "Search completed for the given parameters. title : " + title +
                        ", description : " + description +
                        ", status : " + status +
                        ", priority : " + priority +
                        ", overdue : " + overdue +
                        ", excludeCompleted : " + excludeCompleted,
                HttpStatus.OK.value(),
                service.searchTasksFromUser(title,description,status,priority,overdue,excludeCompleted));
        return ResponseEntity.status(response.getStatus()).body(response) ;
    }

    //to add new task of the user
    @PostMapping("/create")
    public ResponseEntity<ApiResponseModel<Tasks>>  addTask(@RequestBody Tasks task) throws IllegalAccessException {
        ApiResponseModel<Tasks> response = new ApiResponseModel<>(true,"Task Created Successfully",HttpStatus.CREATED.value(), service.addTask(task));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    //to update task of the user
    @PutMapping("/edit/{id}")
    public ResponseEntity<ApiResponseModel<Tasks>>  editTask(@PathVariable int id, @RequestBody Tasks task ) throws IllegalAccessException {
        ApiResponseModel<Tasks> response = new ApiResponseModel<>(true,"Task Edited Successfully",HttpStatus.OK.value(), service.editTask(id,task));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    //to delete task of the user
    @DeleteMapping("/task/{id}")
    public ResponseEntity<ApiResponseModel<Void>> deleteTask(@PathVariable int id) throws IllegalAccessException {
        service.deleteTask(id);
        ApiResponseModel<Void> response = new ApiResponseModel<>(true,"Task Deleted Successfully.",HttpStatus.OK.value(),null);
        return ResponseEntity.status(response.getStatus()).body(response);
    }




    //ADMIN ONLY ACCESS CONTROLLERS. the access is defined in security config (in filters).

    // to get all the users
    @GetMapping("/users")
    public List<Users> getAllUsers() throws IllegalAccessException {
        return service.getAllUsers();
    }

    // to get the tasks of the specific user
    @GetMapping("/users/tasks/{id}")
    public List<Tasks> getAllTasksFromUser(@PathVariable int id)  {
        return service.getAllTasksFromUser(id);
    }

    //to search tasks from all the user
    @GetMapping("/users/tasks/search")
    public List<Tasks> searchAllTasks(@RequestParam(required = false) String title,
                                      @RequestParam(required = false) String description,
                                      @RequestParam(required = false) String status,
                                      @RequestParam(required = false) String priority) {
        return service.searchAllTasks(title,description,status,priority);
    }

    //to search tasks from a single user
    @GetMapping("/users/tasks/search/{id}")
    public List<Tasks> searchAllTasksFromUser(@RequestParam(required = false) String title,
                                              @RequestParam(required = false) String description,
                                              @RequestParam(required = false) String status,
                                              @RequestParam(required = false) String priority,
                                              @RequestParam(required = false) Boolean overdue,
                                              @RequestParam(required = false) Boolean excludeCompleted,
                                              @PathVariable int id){
        return service.searchAllTasksFromUser(title,description,status,priority,overdue,excludeCompleted,id);
    }

}
