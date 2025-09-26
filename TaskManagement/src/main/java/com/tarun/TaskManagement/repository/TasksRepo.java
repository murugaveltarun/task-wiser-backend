package com.tarun.TaskManagement.repository;

import com.tarun.TaskManagement.model.Tasks;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TasksRepo extends JpaRepository<Tasks,Integer> {

    // if request parameter is given, it will be considered for filtering. if not then it won't be considered. it is implemented using both "or" and "and".


    // search tasks with keyword and user id. it searches in title,description,status (used by both admin and user)
    @Query("SELECT t FROM Tasks t WHERE t.user.id = :user_id AND " +
            "(:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%',:title,'%'))) AND " +
            "(:description IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%',:description,'%'))) AND " +
            "(:priority IS NULL OR LOWER(t.priority) LIKE LOWER(CONCAT('%',:priority,'%'))) AND " +
            "(:status IS NULL OR LOWER(t.status) LIKE LOWER(CONCAT('%',:status,'%'))) AND " +
            "(:overdue IS NULL OR (:overdue = true AND t.dueDate < CURRENT_TIMESTAMP ) OR " +
                                " (:overdue = false AND t.dueDate >= CURRENT_TIMESTAMP )) AND " +
            "(:excludeCompleted IS NULL OR (:excludeCompleted = true AND t.status <> 'completed' ))" )
    List<Tasks> searchTasksFromUser(String title,String description,String status,String priority,Boolean overdue,Boolean excludeCompleted,int user_id);


    // search tasks from all the users (used by only admin)
    @Query("SELECT t FROM Tasks t WHERE " +
            "(:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%',:title,'%'))) AND " +
            "(:description IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%',:description,'%'))) AND " +
            "(:priority IS NULL OR LOWER(t.priority) LIKE LOWER(CONCAT('%',:priority,'%'))) AND " +
            "(:status IS NULL OR LOWER(t.status) LIKE LOWER(CONCAT('%',:status,'%'))) AND " +
            "(:overdue IS NULL OR (:overdue = true AND t.dueDate < CURRENT_TIMESTAMP ) OR " +
                                " (:overdue = false AND t.dueDate >= CURRENT_TIMESTAMP )) AND " +
            "(:excludeCompleted IS NULL OR (:excludeCompleted = true AND t.status <> 'completed' ))")
    Page<Tasks> searchAllTasks(String title,String description,String status,String priority,Boolean overdue,Boolean excludeCompleted,Pageable pageable);


    // search by user id
    @Query("SELECT t FROM Tasks t WHERE t.user.id = :user_id")
    Page<Tasks> findByUserIdByAdmin(int user_id, Pageable pageable);

    // search by user id
    List<Tasks> findByUserId(int user_id);

    @Query("SELECT t FROM Tasks t WHERE t.user.id = :user_id AND " +
            "(:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%',:title,'%'))) AND " +
            "(:description IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%',:description,'%'))) AND " +
            "(:priority IS NULL OR LOWER(t.priority) LIKE LOWER(CONCAT('%',:priority,'%'))) AND " +
            "(:status IS NULL OR LOWER(t.status) LIKE LOWER(CONCAT('%',:status,'%'))) AND " +
            "(:overdue IS NULL OR (:overdue = true AND t.dueDate < CURRENT_TIMESTAMP ) OR " +
            " (:overdue = false AND t.dueDate >= CURRENT_TIMESTAMP )) AND " +
            "(:excludeCompleted IS NULL OR (:excludeCompleted = true AND t.status <> 'completed' ))" )
    Page<Tasks> searchAllTasksFromUser(String title, String description, String status, String priority, Boolean overdue, Boolean excludeCompleted, int id, Pageable pageable);
}
