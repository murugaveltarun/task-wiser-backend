package com.tarun.TaskManagement.service;

import com.tarun.TaskManagement.exception.MissingFieldException;
import com.tarun.TaskManagement.model.Tasks;
import com.tarun.TaskManagement.model.Users;
import com.tarun.TaskManagement.repository.TasksRepo;
import com.tarun.TaskManagement.repository.UsersRepo;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.PropertyValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TasksService {

    private final TasksRepo tasksRepo;
    private final UsersRepo usersRepo;

    //this is the constructor function to import the repositories
    @Autowired
    TasksService(TasksRepo tasksRepo, UsersRepo usersRepo){
        this.tasksRepo = tasksRepo;
        this.usersRepo = usersRepo;
    }


    // this is the helper function which helps to authenticate any request. it is used for both user and admin validation.
    private Users getUserInfo(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Users user = usersRepo.findByUsername(username);
        return user;
    }

    //** USER SERVICES **

    //this is to get all tasks
    public List<Tasks> getTasks() {
        Users user = getUserInfo();
        return tasksRepo.findByUserId(user.getId());
    }

    //this is to add a new task
    public Tasks addTask(Tasks task) throws IllegalAccessException {
        Users user = getUserInfo();
        if(!user.getRole().equals("ROLE_USER")){
            throw new IllegalAccessException("Only users can create task.");
        }
        if(task.getDueDate() == null || task.getDescription() == null || task.getTitle() == null || task.getPriority() == null || task.getStatus() == null){
            throw new MissingFieldException("Missing field. These fields are mandatory : dueDate, title, description, priority, status.");
        }
        task.setUser(user);
        task.setCreatedAt(LocalDateTime.now());
        task.setLastModifiedAt(LocalDateTime.now());
        return tasksRepo.save(task);
    }

    //this is to search specific user's tasks
    public List<Tasks> searchTasksFromUser(String title,String description,String status,String priority,Boolean overdue,Boolean excludeCompleted) {
        Users user = getUserInfo();
        return tasksRepo.searchTasksFromUser(title,description,status,priority,overdue,excludeCompleted,user.getId());

    }

    //this is to get single task by id, also verifies whether the user id in task is same as user principal
    public Tasks getTaskById(int id) throws IllegalAccessException {
        int userId = getUserInfo().getId();
        Tasks task = tasksRepo.findById(id).orElse(null);
        if(task == null){
            throw new EntityNotFoundException("Task Not Found with the task id : " + id);
        }
        if(userId == task.getUser().getId()){
            return task;
        }
        throw new IllegalAccessException("Unauthorized for the task with task id : " + id);
    }

    //this is to update task as a user,
    @PreAuthorize("hasRole('USER')")
    public Tasks editTask(int id, Tasks task) throws IllegalAccessException {     //get task id and task details to update
        int userId = getUserInfo().getId();                                       //get id from user principal
        Tasks dbTask = tasksRepo.findById(id).orElse(null);                 //get the task from db with task id

        if(dbTask == null){
            throw new EntityNotFoundException("Task not found for the id : " + id);                       //if not found task with task id
        }
        if(task.getStatus() == null || task.getPriority() ==null || task.getDueDate() == null|| task.getTitle() == null|| task.getDescription()==null){
            throw new MissingFieldException("Missing field. All these fields are mandatory to update task : dueDate, title, description, priority, status. ");
        }
        if(userId == dbTask.getUser().getId()){                                   //check whether user id is same in both the task and user principal
            Users user = getUserInfo();
            task.setUser(user);
            dbTask.setTitle(task.getTitle());
            dbTask.setDescription(task.getDescription());
            dbTask.setStatus(task.getStatus());
            dbTask.setPriority(task.getPriority());
            dbTask.setLastModifiedAt(LocalDateTime.now());
            dbTask.setDueDate(task.getDueDate());

            return tasksRepo.save(dbTask);
        }
        throw new IllegalAccessException("Unauthorized for updating the task with id : " + id);
    }

    @PreAuthorize("hasRole('USER')")
    public void deleteTask(int id) {
        int userId = getUserInfo().getId();
        Tasks task = tasksRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("Task not found with this id : " + id));

        if(userId != task.getUser().getId()){
            throw new AccessDeniedException("Unauthorized");
        }
        tasksRepo.deleteById(task.getTaskId());
    }


    //** ADMIN SERVICES **


    //GET ALL

    // this is used to get all the users
    @PreAuthorize("hasRole('ADMIN')")
    public List<Users> getAllUsers(){
            return usersRepo.findByRole("ROLE_USER");
    }

    //this is used to get all the tasks from the specific user
    @PreAuthorize("hasRole('ADMIN')")
    public List<Tasks> getAllTasksFromUser(int id) {
            return tasksRepo.findByUserId(id);
    }


    // SEARCHING

    //this is to search tasks from all the users
    @PreAuthorize("hasRole('ADMIN')")
    public List<Tasks> searchAllTasks(String title,String description,String status, String priority) {
        return tasksRepo.searchAllTasks(title,description,status,priority);
    }

    //this is to search tasks from specific user
    @PreAuthorize("hasRole('ADMIN')")
    public List<Tasks> searchAllTasksFromUser(String title,String description,String status,String priority,Boolean overdue,Boolean excludeCompleted,int id){
            return tasksRepo.searchTasksFromUser(title,description,status,priority,overdue,excludeCompleted,id);
    }


}
