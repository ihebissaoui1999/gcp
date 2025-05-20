package com.example.chatintell.entity;

import com.example.chatintell.base.BaseAuditingEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
@NamedQuery(name = UserConstants.FIND_USER_BY_EMAIL,
        query = "SELECT u FROM User u WHERE u.email = :email"
)
@NamedQuery(name = UserConstants.FIND_ALL_USERS_EXCEPT_SELF,
        query = "SELECT u FROM User u WHERE u.id != :publicId")
@NamedQuery(name = UserConstants.FIND_USER_BY_PUBLIC_ID,
        query = "SELECT u FROM User u WHERE u.id = :publicId")

public class User extends BaseAuditingEntity {
    private static final int LAST_ACTIVATE_INTERVAL = 5;

    @Id

    private String userid;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime lastSeen;
    @OneToMany(mappedBy = "sender")
    private List<Chat> chatsAsSender;



    @OneToMany(mappedBy = "recipient")
    private List<Chat> chatsAsRecipient;
    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private Rewards rewards;

    @Transient
    public boolean isUserOnline() {
        return lastSeen != null && lastSeen.isAfter(LocalDateTime.now().minusMinutes(LAST_ACTIVATE_INTERVAL));
    }
    @ManyToOne( fetch = FetchType.EAGER)
    private Role roles;
    @ManyToMany(mappedBy = "users",cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private List<Ticket>tickets= new ArrayList<>();
    @OneToMany(mappedBy = "user")
    private List<Matiriel> matiriels= new ArrayList<>();
}
