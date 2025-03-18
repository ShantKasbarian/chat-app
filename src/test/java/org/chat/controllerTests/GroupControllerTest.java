package org.chat.controllerTests;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.SecurityContext;
import org.chat.config.JwtService;
import org.chat.controllers.GroupController;
import org.chat.converters.GroupConverter;
import org.chat.entities.Group;
import org.chat.entities.User;
import org.chat.models.GroupDto;
import org.chat.repositories.GroupUserRepository;
import org.chat.services.GroupService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@QuarkusTest
class GroupControllerTest {
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

    @InjectMock
    private GroupUserRepository groupUserRepository;

    @Inject
    private JwtService jwtService;

    private Group group;

    private GroupDto groupDto;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("username");
        user.setPassword("Password123+");

        group = new Group();
        group.setId(UUID.randomUUID().toString());
        group.setName("group");

        groupDto = new GroupDto();
        groupDto.setId(group.getId());
        groupDto.setName(group.getName());
    }

    @Test
    @Transactional
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
        when(groupService.joinGroup(group.getName(), user.getId()))
                .thenReturn("request to join group has been submitted, waiting for one of the group creators to accept");

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
        when(groupService.leaveGroup(group.getName(), user.getId()))
                .thenReturn("you left the group");

        String jwtToken = jwtService.generateToken(user.getUsername(), user.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .delete("/group/"+ group.getName() + "/leave")
                .then()
                .statusCode(204);
    }

    @Test
    void acceptUserToGroup() {
        when(groupService.acceptToGroup(group.getName(), user.getId(), "username"))
                .thenReturn("user has been accepted");

        String jwtToken = jwtService.generateToken(user.getUsername(), user.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .put("/group/"+ group.getName() + "/accept/user/" + user.getUsername())
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
                .delete("/group/"+ group.getName() + "/reject/user/" + user.getUsername())
                .then()
                .statusCode(204);
    }

    @Test
    void getWaitingUsers() {
        List<String> users = new ArrayList<>();
        users.add("user1");
        users.add("user2");
        users.add("user3");

        when(groupService.getWaitingUsers(group.getName(), user.getId()))
                .thenReturn(users);

        String jwtToken = jwtService.generateToken(user.getUsername(), user.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get("/group/"+ group.getName() + "/waiting/users")
                .then()
                .statusCode(200);
    }

    @Test
    void getJoinedGroups() {
        List<String> groups = new ArrayList<>();
        groups.add("group1");
        groups.add("group2");
        groups.add("group3");

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
        List<String> groups = new ArrayList<>();
        groups.add("group1");
        groups.add("group2");
        groups.add("group3");

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