package org.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "group_users")
public class GroupUser {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

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
