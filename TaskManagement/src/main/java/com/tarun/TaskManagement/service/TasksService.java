package com.tarun.TaskManagement.service;

import com.tarun.TaskManagement.exception.MissingFieldException;
import com.tarun.TaskManagement.model.Tasks;
import com.tarun.TaskManagement.model.Users;
import com.tarun.TaskManagement.repository.TasksRepo;
import com.tarun.TaskManagement.repository.UsersRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    public void deleteTask(int id) throws IllegalAccessException {
        int userId = getUserInfo().getId();
        Tasks task = tasksRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("Task not found with this id : " + id));

        if(userId != task.getUser().getId()){
            throw new IllegalAccessException("Unauthorized");
        }
        tasksRepo.deleteById(task.getTaskId());
    }


    //** ADMIN SERVICES **


    //GET ONE

    @PreAuthorize("hasRole('ADMIN')")
    public Users getUser(int id) throws IllegalAccessException {
        Users user = usersRepo.findById(id).orElse(null);
        if(user == null){
            throw new UsernameNotFoundException("User not found with this id : " + id);
        }
        return user;
    }
    @PreAuthorize("hasRole('ADMIN')")
    public Tasks getTask(int userid, int taskid) throws IllegalAccessException {
        Tasks task = tasksRepo.findById(taskid).orElse(null);
        if(task == null){
            throw new EntityNotFoundException("Task not found with this id : " + taskid);
        }
        if(task.getUser().getId() != userid){
            throw new IllegalAccessException("The task with the id : " + taskid + " does not belong to this user id : " + userid);
        }
        return task;

    }

    //GET ALL

    // this is used to get all the users
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Users> getAllUsers(Pageable pageable){
            return usersRepo.findByRole("ROLE_USER",pageable);
    }

    //this is to find all tasks from all users
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Tasks> getAllTasks(Pageable pageable) {
        return tasksRepo.findAll(pageable);
    }

    //this is used to get all the tasks from the specific user
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Tasks> getAllTasksFromUser(int id,Pageable pageable) {
            return tasksRepo.findByUserIdByAdmin(id,pageable);
    }


    // SEARCHING

    //this is to search tasks from all the users
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Tasks> searchAllTasks(String title,String description,String status,String priority,Boolean overdue,Boolean excludeCompleted,Pageable pageable) {
        return tasksRepo.searchAllTasks(title,description,status,priority,overdue,excludeCompleted,pageable);
    }

    //this is to search tasks from specific user
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Tasks> searchAllTasksFromUser(String title,String description,String status,String priority,Boolean overdue,Boolean excludeCompleted,int id,Pageable pageable){
        return tasksRepo.searchAllTasksFromUser(title,description,status,priority,overdue,excludeCompleted,id,pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<Users> searchAllUsers(String email, String username, String name, Boolean active,String authProvider, Integer id,Pageable pageable) {
        return usersRepo.searchByUsers(email,username,name,active,authProvider,id,pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Users editUser(int id, Users user) throws IllegalAccessException {
        Users dbUser = usersRepo.findById(id).orElse(null);                 //get the user from db with user id

        if(dbUser == null){
            throw new EntityNotFoundException("User not found for the id : " + id);                       //if not found user with task id
        }
        if(user.getEmail() == null || user.getName() ==null || user.getUsername() == null || user.getActive()){
            throw new MissingFieldException("Missing field. All these fields are mandatory to update task : email, name, username, active. ");
        }
        if(user.getId() == dbUser.getId()){                                   //check whether user id is same in both the task and user principal
            dbUser.setActive(user.getActive());
            dbUser.setName(user.getName());
            dbUser.setEmail(user.getEmail());
            dbUser.setUsername(user.getUsername());

            return usersRepo.save(dbUser);
        }
        throw new IllegalAccessException("Unauthorized for updating the task with id : " + id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTaskByAdmin(int id) {
        Tasks task = tasksRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("Task not found with this id : " + id));
        tasksRepo.deleteById(task.getTaskId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Tasks editTaskByAdmin(int id, Tasks task) throws IllegalAccessException {
        Tasks dbTask = tasksRepo.findById(id).orElse(null);                  //get the task from db with task id

        if(dbTask == null){
            throw new EntityNotFoundException("Task not found for the id : " + id);                       //if not found task with task id
        }
        if(task.getStatus() == null || task.getPriority() ==null || task.getDueDate() == null|| task.getTitle() == null|| task.getDescription()==null){
            throw new MissingFieldException("Missing field. All these fields are mandatory to update task : dueDate, title, description, priority, status. ");
        }
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
}
