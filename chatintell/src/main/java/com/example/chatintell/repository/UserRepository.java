package com.example.chatintell.repository;

import com.example.chatintell.entity.CategoryType;
import com.example.chatintell.entity.Ticket;
import com.example.chatintell.entity.User;
import com.example.chatintell.entity.UserConstants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.management.relation.Role;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    @Query(name = UserConstants.FIND_USER_BY_EMAIL)
    Optional<User> findByEmail(@Param("email") String userEmail);

    @Query(name = UserConstants.FIND_ALL_USERS_EXCEPT_SELF)
    List<User> findAllUsersExceptSelf(@Param("publicId") String publicId);

    @Query(name = UserConstants.FIND_USER_BY_PUBLIC_ID)
    Optional<User> findByPublicId(@Param("publicId") String senderId);

 /*   @Query("select DISTINCT  u from User u join u.roles r join u.tickets t where t.categoryType = :category and r.name = :category")
    List<User> findUserByCategoryMatchingRole(@Param("category") String category);*/

   // @Query("SELECT u FROM User u JOIN u.tickets t WHERE u.roles.name = t.categoryType AND t.categoryType = :category")
    //List<User> findUsersWithMatchingRoleAndCategory(@Param("category") CategoryType category);

    List<User> findByRoles_Name(String roleName);

/*    @Query("select u from User u join  u.roles r join u.tickets t where t.categoryType = :category and r = : category")
    List<User> findUserByCategoryMatchingRole(String category);*/

    User findByUserid(String userid);
}
