package com.tarun.TaskManagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Tasks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int taskId;

    @Column(name = "title" , nullable = false)
    private String title;
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;
    @Column(name = "status" , nullable = false)
    private String status;
    @Column(name = "priority" , nullable = false)
    private String priority;
    @Column(name = "dueDate" , nullable = false)
    private LocalDateTime dueDate;
    @Column(name="lastModifiedAt")
    private LocalDateTime lastModifiedAt;
    @Column(name="createdAt")
    private LocalDateTime createdAt;



    //defining the relation with users. here we have one to many relation.
    //MANY is TASKS
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;


    //getters and setters

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(LocalDateTime lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    //overriding to string method to get values of all the given variable. it can be used to print on console.
    @Override
    public String toString() {
        return "Tasks{" +
                "taskId=" + taskId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", dueDate=" + dueDate + '\'' +
                ", lastModifiedAt=" + lastModifiedAt + '\'' +
                '}';
    }
}
/*
    SAMPLE DATA FOR POSTING TASK
{
    "title": "My Task",
    "description": "my task is to blah blah blah. and how to do my task is blah blah blah.",
    "status": "completed",
    "priority" : "high",
    "dueDate": "2004-01-01"
}


*/