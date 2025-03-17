package org.chat.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
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
public class Group extends PanacheEntity {
    @Column(name = "name", unique = true)
    private String name;

    @OneToMany(mappedBy = "group")
    private List<Message> messages;

    @OneToMany(mappedBy = "group")
    private List<GroupUser> users;

    public Group(String name) {
        this.name = name;
    }
}
