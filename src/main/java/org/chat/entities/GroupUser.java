package org.chat.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "group_users")
public class GroupUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "is_creator")
    private Boolean isCreator;

    @Column(name = "is_member")
    private Boolean isMember;

    public GroupUser(Group group, Integer userId, Boolean isCreator, Boolean isMember) {
        this.group = group;
        this.userId = userId;
        this.isCreator = isCreator;
        this.isMember = isMember;
    }
}
