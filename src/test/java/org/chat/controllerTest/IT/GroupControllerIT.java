package org.chat.controllerTest.IT;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.SecurityContext;
import org.chat.config.JwtService;
import org.chat.controller.GroupController;
import org.chat.converter.GroupConverter;
import org.chat.converter.GroupUserConverter;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.entity.User;
import org.chat.model.GroupDto;
import org.chat.model.GroupUserDto;
import org.chat.service.GroupService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@QuarkusTest
class GroupControllerIT {
    @Inject
    private GroupController groupController;

    @InjectMock
    private GroupService groupService;

    @InjectMock
    private GroupConverter groupConverter;

    @InjectMock
    private SecurityContext securityContext;

    @InjectMock
    private JsonWebToken token;

    @Inject
    private JwtService jwtService;

    @InjectMock
    private GroupUserConverter groupUserConverter;

    private Group group;

    private GroupDto groupDto;

    private User user;

    private User user2;

    private GroupUser groupUser;

    private GroupUserDto groupUserDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("username");
        user.setPassword("Password123+");

        user2 = new User();
        user2.setId(UUID.randomUUID().toString());
        user2.setUsername("username2");
        user2.setPassword("Password123+");


        group = new Group();
        group.setId(UUID.randomUUID().toString());
        group.setName("group");

        groupDto = new GroupDto();
        groupDto.setId(group.getId());
        groupDto.setName(group.getName());

        groupUser = new GroupUser(UUID.randomUUID().toString(), group, user2, true, false);
        groupUserDto =
                new GroupUserDto(
                        groupUser.getId(),
                        groupUser.getGroup().getId(),
                        groupUser.getGroup().getName(),
                        groupUser.getUser().getId(),
                        groupUser.getUser().getUsername(),
                        groupUser.getIsCreator(),
                        groupUser.getIsMember()
                );
    }

    @Test
    void create() {
        when(groupConverter.convertToModel(group)).thenReturn(groupDto);
        when(groupService.createGroup(group, new String[]{}, user.getId()))
                .thenReturn(group);

        String jwtToken = jwtService.generateToken(user.getUsername(), user.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .body(groupDto)
                .when()
                .post("/group/create")
                .then()
                .statusCode(201);
    }

    @Test
    void joinGroup() {
        when(groupUserConverter.convertToModel(groupUser)).thenReturn(groupUserDto);
        when(groupService.joinGroup(group.getId(), user.getId()))
                .thenReturn(new GroupUser());

        String jwtToken = jwtService.generateToken(user.getUsername(), user.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .post("/group/"+ group.getName() + "/join")
                .then()
                .statusCode(201);
    }

    @Test
    void leaveGroup() {
        when(groupService.leaveGroup(group.getId(), user.getId()))
                .thenReturn("you left the group");

        String jwtToken = jwtService.generateToken(user.getUsername(), user.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .delete("/group/"+ group.getId() + "/leave")
                .then()
                .statusCode(204);
    }

    @Test
    void acceptUserToGroup() {
        when(groupUserConverter.convertToModel(groupUser)).thenReturn(groupUserDto);
        when(groupService.acceptToGroup(group.getId(), user.getId(), user2.getId()))
                .thenReturn(groupUser);

        String jwtToken = jwtService.generateToken(user.getUsername(), user.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .put("/group/"+ group.getId() + "/accept/user/" + user2.getId())
                .then()
                .statusCode(200);
    }

    @Test
    void rejectUserFromGroup() {
        when(groupService.rejectFromEnteringGroup(group.getName(), user.getId(), "username"))
                .thenReturn("user has been rejected");

        String jwtToken = jwtService.generateToken(user.getUsername(), user.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .delete("/group/"+ group.getId() + "/reject/user/" + user2.getId())
                .then()
                .statusCode(204);
    }

    @Test
    void getWaitingUsers() {
        List<GroupUser> users = new ArrayList<>();
        users.add(new GroupUser());
        users.add(new GroupUser());
        users.add(new GroupUser());

        when(groupService.getWaitingUsers(group.getId(), user.getId()))
                .thenReturn(users);

        String jwtToken = jwtService.generateToken(user.getUsername(), user.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get("/group/"+ group.getId() + "/waiting/users")
                .then()
                .statusCode(200);
    }

    @Test
    void getJoinedGroups() {
        List<Group> groups = new ArrayList<>();
        groups.add(new Group());
        groups.add(new Group());
        groups.add(new Group());

        when(groupService.getUserJoinedGroups(user.getId()))
                .thenReturn(groups);

        String jwtToken = jwtService.generateToken(user.getUsername(), user.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get("/group/joined")
                .then()
                .statusCode(200);
    }

    @Test
    void getGroups() {
        List<Group> groups = new ArrayList<>();
        groups.add(new Group());
        groups.add(new Group());
        groups.add(new Group());

        when(groupService.getGroups("g"))
                .thenReturn(groups);

        String jwtToken = jwtService.generateToken(user.getUsername(), user.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get("/group/g/search")
                .then()
                .statusCode(200);
    }
}