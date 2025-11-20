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
import org.chat.service.impl.GroupServiceImpl;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@QuarkusTest
class GroupControllerIT {
    @Inject
    private GroupController groupController;

    @InjectMock
    private GroupServiceImpl groupService;

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
        user.setId(UUID.randomUUID());
        user.setUsername("username");
        user.setPassword("Password123+");

        user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUsername("username2");
        user2.setPassword("Password123+");


        group = new Group();
        group.setId(UUID.randomUUID());
        group.setName("group");

        groupDto = new GroupDto();
        groupDto.setId(group.getId());
        groupDto.setName(group.getName());

        groupUser = new GroupUser(UUID.randomUUID(), group, user2, true, false);
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
        when(groupService.createGroup(group, new UUID[]{}, user.getId()))
                .thenReturn(group);

        String jwtToken = jwtService.generateToken(user.getUsername(), String.valueOf(user.getId()));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .body(groupDto)
                .when()
                .post("/groups")
                .then()
                .statusCode(201);
    }

    @Test
    void joinGroup() {
        when(groupUserConverter.convertToModel(groupUser)).thenReturn(groupUserDto);
        when(groupService.joinGroup(group.getId(), user.getId()))
                .thenReturn(new GroupUser());

        String jwtToken = jwtService.generateToken(user.getUsername(), String.valueOf(user.getId()));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .post("/groups/"+ group.getName() + "/join")
                .then()
                .statusCode(201);
    }

    @Test
    void leaveGroup() {
        when(groupService.leaveGroup(group.getId(), user.getId()))
                .thenReturn("you left the group");

        String jwtToken = jwtService.generateToken(user.getUsername(), String.valueOf(user.getId()));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .delete("/groups/"+ group.getId() + "/leave")
                .then()
                .statusCode(204);
    }

    @Test
    void acceptJoinGroup() {
        when(groupUserConverter.convertToModel(groupUser)).thenReturn(groupUserDto);
        when(groupService.acceptJoinGroup(user2.getId(), groupUser.getId()))
                .thenReturn(groupUser);

        String jwtToken = jwtService.generateToken(user.getUsername(), String.valueOf(user.getId()));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .put("/groups/accept/" + user2.getId())
                .then()
                .statusCode(200);
    }

    @Test
    void rejectJoinGroup() {
        when(groupService.rejectJoinGroup(any(UUID.class), any(UUID.class)))
                .thenReturn("user has been rejected");

        String jwtToken = jwtService.generateToken(user.getUsername(), String.valueOf(user.getId()));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .delete("/groups/reject/" + user2.getId())
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

        String jwtToken = jwtService.generateToken(user.getUsername(), String.valueOf(user.getId()));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get("/groups/"+ group.getId() + "/waiting/users")
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

        String jwtToken = jwtService.generateToken(user.getUsername(), String.valueOf(user.getId()));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get("/groups/joined")
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

        String jwtToken = jwtService.generateToken(user.getUsername(), String.valueOf(user.getId()));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get("/groups/g/search")
                .then()
                .statusCode(200);
    }
}