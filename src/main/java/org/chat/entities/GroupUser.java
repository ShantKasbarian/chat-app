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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_creator")
    private Boolean isCreator;

    @Column(name = "is_member")
    private Boolean isMember;

    public GroupUser(Group group, User user, Boolean isCreator, Boolean isMember) {
        this.group = group;
        this.user = user;
        this.isCreator = isCreator;
        this.isMember = isMember;
    }
}
