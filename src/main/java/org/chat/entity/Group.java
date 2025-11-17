package org.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "groups")
public class Group {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name", unique = true)
    private String name;

    @OneToMany(mappedBy = "group")
    private List<Message> messages;

    @OneToMany(mappedBy = "group")
    private List<GroupUser> users;
}
