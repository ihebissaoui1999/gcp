package com.example.chatintell.repository;

import com.example.chatintell.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    @Query("SELECT t FROM Ticket t WHERE t.createdBy = :iduser ORDER BY t.creationdate DESC")
    List<Ticket> findByCreatedBy(@Param("iduser") String iduser);
    @Query("SELECT t FROM Ticket t JOIN t.users u WHERE u.userid = :userId ORDER BY t.creationdate DESC ")
    List<Ticket> findTicketsAssignedToUser(@Param("userId") String userId);


    List<Ticket> findByTicketid(Integer ticketid);


    List<Ticket> findAllByAccepted(Boolean accepted);

    @Query("select t  from Ticket t  join  t.users us  where  us.userid = :userId")
    List<Ticket> findTicketsByUserId(@Param("userId") String  userId);



    @Query("SELECT t FROM Ticket t JOIN t.users u WHERE u.userid = :userId AND SIZE(t.users) = 1")
    List<Ticket> findTicketsAssignedToOnlyOneUser(@Param("userId") String userId);




}
