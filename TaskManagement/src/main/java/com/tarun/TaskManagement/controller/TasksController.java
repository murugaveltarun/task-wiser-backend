package com.tarun.TaskManagement.controller;

import com.tarun.TaskManagement.exception.ApiResponseModel;
import com.tarun.TaskManagement.model.Tasks;
import com.tarun.TaskManagement.model.Users;
import com.tarun.TaskManagement.service.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.AuthProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<ApiResponseModel<Map<String,Object>>>  getAllUsers(@RequestParam(defaultValue = "1") int page,
                                                                      @RequestParam(defaultValue = "10") int limit,
                                                                      @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                      @RequestParam(defaultValue = "desc") String direction) throws IllegalAccessException {

        Sort sort = direction.equalsIgnoreCase("asc")? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page-1,limit,sort);
        Page<Users> userPage = service.getAllUsers(pageable);

        Map<String,Object> result = new HashMap<>();
        result.put("users",userPage.getContent());
        result.put("currentPage",userPage.getNumber()+1);
        result.put("totalItems", userPage.getTotalElements());
        result.put("totalPages", userPage.getTotalPages());
        result.put("limit",limit);

        ApiResponseModel<Map<String,Object>> response = new ApiResponseModel<>(true,"Fetched all Users", HttpStatus.OK.value(), result);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    // to get all tasks
    @GetMapping("/all-tasks")
    public ResponseEntity<ApiResponseModel<Map<String,Object>>>  getAllTasks(@RequestParam(defaultValue = "1") int page,
                                                                      @RequestParam(defaultValue = "10") int limit,
                                                                      @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                      @RequestParam(defaultValue = "desc") String direction) throws IllegalAccessException {

        Sort sort = direction.equalsIgnoreCase("asc")? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page-1,limit,sort);
        Page<Tasks> taskPage = service.getAllTasks(pageable);

        Map<String,Object> result = new HashMap<>();
        result.put("tasks",taskPage.getContent());
        result.put("currentPage",taskPage.getNumber()+1);
        result.put("totalItems", taskPage.getTotalElements());
        result.put("totalPages", taskPage.getTotalPages());
        result.put("limit",limit);

        ApiResponseModel<Map<String, Object>> response = new ApiResponseModel<>(true,"Fetched all Tasks", HttpStatus.OK.value(), result);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // to get a specific user
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponseModel<Users>> getUser(@PathVariable int id) throws IllegalAccessException {
        ApiResponseModel<Users> response = new ApiResponseModel<>(true,"Fetched the user with user id : " + id,HttpStatus.OK.value(),service.getUser(id) );
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // to get the tasks of the specific user by page
    @GetMapping("/users/{id}/tasks")
    public ResponseEntity<ApiResponseModel<Map<String,Object>>> getAllTasksFromUser(@PathVariable int id,
                                                                             @RequestParam(defaultValue = "1") int page,
                                                                             @RequestParam(defaultValue = "10") int limit,
                                                                             @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                             @RequestParam(defaultValue = "desc") String direction)  {
        Sort sort = direction.equalsIgnoreCase("asc")? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page-1,limit,sort);
        Page<Tasks> taskPage = service.getAllTasksFromUser(id,pageable);

        Map<String,Object> result = new HashMap<>();
        result.put("tasks",taskPage.getContent());
        result.put("currentPage",taskPage.getNumber()+1);
        result.put("totalItems", taskPage.getTotalElements());
        result.put("totalPages", taskPage.getTotalPages());
        result.put("limit",limit);

        ApiResponseModel<Map<String, Object>> response = new ApiResponseModel<>(true,"Fetched all tasks from the user id : " + id,HttpStatus.OK.value(),result);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // to get the tasks of the specific user by page
    @GetMapping("/users/{id}/tasks/all")
    public ResponseEntity<ApiResponseModel<List<Tasks>>> getAllTasksFromUserAll(@PathVariable int id)  {
        List<Tasks> tasks = service.getAllTasksFromUserAll(id);
        ApiResponseModel<List<Tasks>> response = new ApiResponseModel<>(true,"Fetched all tasks from the user id : " + id,HttpStatus.OK.value(),tasks);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // to get a task from a user
    @GetMapping("/users/{userid}/tasks/{taskid}")
    public ResponseEntity<ApiResponseModel<Tasks>>  getTask(@PathVariable int userid, @PathVariable int taskid) throws IllegalAccessException {
        ApiResponseModel<Tasks> response = new ApiResponseModel<>(true, "Fetched task with task id : "+taskid + " from the user id : " + userid,HttpStatus.OK.value(), service.getTask(userid,taskid));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // edit user
    @PutMapping("/edit/user/{id}")
    public ResponseEntity<ApiResponseModel<Users>> editUser(@PathVariable int id,@RequestBody Users user) throws IllegalAccessException {
        ApiResponseModel<Users> response = new ApiResponseModel<>(true,"User Edited Successfully.",HttpStatus.OK.value(),service.editUser(id,user));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // to edit task of an user by admin
    @PutMapping("/edit/task/{id}")
    public ResponseEntity<ApiResponseModel<Tasks>> editUser(@PathVariable int id,@RequestBody Tasks task) throws IllegalAccessException {
        ApiResponseModel<Tasks> response = new ApiResponseModel<>(true,"Task Edited Successfully.",HttpStatus.OK.value(),service.editTaskByAdmin(id,task));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // to delete a task by admin
    @DeleteMapping("/delete/task/{id}")
    public ResponseEntity<ApiResponseModel<Void>> deleteTaskByAdmin(@PathVariable int id) throws IllegalAccessException {
        service.deleteTaskByAdmin(id);
        ApiResponseModel<Void> response = new ApiResponseModel<>(true, "Task Deleted Successfully", HttpStatus.OK.value(),null);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // to search users from all users
    @GetMapping("/users/search")
    public ResponseEntity<ApiResponseModel<Map<String,Object>>>  searchAllUsers(@RequestParam(required = false) String email,
                                                                         @RequestParam(required = false) String username,
                                                                         @RequestParam(required = false) String name,
                                                                         @RequestParam(required = false) Boolean active,
                                                                         @RequestParam(required = false) String authProvider,
                                                                         @RequestParam(required = false) Integer id,
                                                                         @RequestParam(defaultValue = "1") int page,
                                                                         @RequestParam(defaultValue = "10") int limit,
                                                                         @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                         @RequestParam(defaultValue = "desc") String direction ) throws IllegalAccessException {

        Sort sort = direction.equalsIgnoreCase("asc")? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page-1,limit,sort);
        Page<Users> taskPage = service.searchAllUsers(email,username,name,active,authProvider,id,pageable);

        Map<String,Object> result = new HashMap<>();
        result.put("users",taskPage.getContent());
        result.put("currentPage",taskPage.getNumber()+1);
        result.put("totalItems", taskPage.getTotalElements());
        result.put("totalPages", taskPage.getTotalPages());
        result.put("limit",limit);

        ApiResponseModel<Map<String, Object>> response = new ApiResponseModel<>(true,"Search users completed. ", HttpStatus.OK.value(), result);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    //to search tasks from all the user
    @GetMapping("/users/tasks/search")
    public ResponseEntity<ApiResponseModel<Map<String, Object>>> searchAllTasks(@RequestParam(required = false) String title,
                                                                        @RequestParam(required = false) String description,
                                                                        @RequestParam(required = false) String status,
                                                                        @RequestParam(required = false) String priority,
                                                                        @RequestParam(required = false) Boolean overdue,
                                                                        @RequestParam(required = false) Boolean excludeCompleted,
                                                                        @RequestParam(defaultValue = "1") int page,
                                                                        @RequestParam(defaultValue = "10") int limit,
                                                                        @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                        @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase("asc")? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page-1,limit,sort);
        Page<Tasks> taskPage = service.searchAllTasks(title,description,status,priority,overdue,excludeCompleted,pageable);

        Map<String,Object> result = new HashMap<>();
        result.put("tasks",taskPage.getContent());
        result.put("currentPage",taskPage.getNumber()+1);
        result.put("totalItems", taskPage.getTotalElements());
        result.put("totalPages", taskPage.getTotalPages());
        result.put("limit",limit);

        ApiResponseModel<Map<String, Object>> response = new ApiResponseModel<>(true, "Search completed for tasks from all users.",HttpStatus.OK.value(), result);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    //to search tasks from a single user
    @GetMapping("/users/{id}/tasks/search")
    public ResponseEntity<ApiResponseModel<Map<String, Object>>> searchAllTasksFromUser(@RequestParam(required = false) String title,
                                                                                @RequestParam(required = false) String description,
                                                                                @RequestParam(required = false) String status,
                                                                                @RequestParam(required = false) String priority,
                                                                                @RequestParam(required = false) Boolean overdue,
                                                                                @RequestParam(required = false) Boolean excludeCompleted,
                                                                                @PathVariable int id,
                                                                                @RequestParam(defaultValue = "1") int page,
                                                                                @RequestParam(defaultValue = "10") int limit,
                                                                                @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                                @RequestParam(defaultValue = "desc") String direction){

        Sort sort = direction.equalsIgnoreCase("asc")? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page-1,limit,sort);
        Page<Tasks> taskPage = service.searchAllTasksFromUser(title,description,status,priority,overdue,excludeCompleted,id,pageable);

        Map<String,Object> result = new HashMap<>();
        result.put("tasks",taskPage.getContent());
        result.put("currentPage",taskPage.getNumber()+1);
        result.put("totalItems", taskPage.getTotalElements());
        result.put("totalPages", taskPage.getTotalPages());
        result.put("limit",limit);

        ApiResponseModel<Map<String, Object>> response = new ApiResponseModel<>(true, "Search completed for tasks from all users.",HttpStatus.OK.value(),result);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @GetMapping("/stats")
    public ResponseEntity<ApiResponseModel<Map<String,?>>> getAllStats(){
        ApiResponseModel<Map<String,?>> response = new ApiResponseModel<>(true, "Search completed for tasks from all users.",HttpStatus.OK.value(),service.getAllStats());
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
