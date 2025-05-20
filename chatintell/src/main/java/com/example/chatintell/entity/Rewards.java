package com.example.chatintell.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "rewards")
public class Rewards {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rewardsId;
    private String rewardsName;
    private Long points;
    @Enumerated(EnumType.STRING)
    private RewardsType  rewardsType;
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "rewards")
    private List<User> users;
}
