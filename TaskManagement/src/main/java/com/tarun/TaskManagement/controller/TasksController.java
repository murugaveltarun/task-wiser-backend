package com.tarun.TaskManagement.controller;

import com.tarun.TaskManagement.model.Tasks;
import com.tarun.TaskManagement.model.Users;
import com.tarun.TaskManagement.service.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Tasks> getTasks(){
        return service.getTasks();
    }

    @GetMapping("/task/{id}")
    public Tasks getTask(@PathVariable int id) throws IllegalAccessException {
        return service.getTaskById(id);
    }

    //to get all the tasks of the user, and then it is filtered with the given keyword
    @GetMapping("/tasks/search")
    public List<Tasks> searchTasks(@RequestParam(required = false) String title,
                                   @RequestParam(required = false) String description,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(required = false) String priority){
        return service.searchTasksFromUser(title,description,status,priority);
    }

    //to add new task of the user
    @PostMapping("/create")
    public Tasks addTask(@RequestBody Tasks task){
        return service.addTask(task);
    }

    //to update task of the user
    @PutMapping("/edit/{id}")
    public Tasks editTask(@PathVariable int id, @RequestBody Tasks task ) throws IllegalAccessException {
        return service.editTask(id,task);
    }

    //to delete task of the user
    @DeleteMapping("/task/{id}")
    public void deleteTask(@PathVariable int id) {
        service.deleteTask(id);
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
                                              @PathVariable int id){
        return service.searchAllTasksFromUser(title,description,status,priority,id);
    }

}
