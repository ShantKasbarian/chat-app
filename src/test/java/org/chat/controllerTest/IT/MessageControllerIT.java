package org.chat.controllerTest.IT;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.SecurityContext;
import org.chat.config.JwtService;
import org.chat.controller.MessageController;
import org.chat.converter.GroupMessageConverter;
import org.chat.converter.MessageConverter;
import org.chat.entity.Group;
import org.chat.entity.Message;
import org.chat.entity.User;
import org.chat.model.GroupMessageDto;
import org.chat.model.MessageDto;
import org.chat.service.impl.MessageServiceImpl;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@QuarkusTest
class MessageControllerIT {
    @Inject
    private MessageController messageController;

    @InjectMock
    private MessageServiceImpl messageService;

    @InjectMock
    private MessageConverter messageConverter;

    @InjectMock
    private GroupMessageConverter groupMessageConverter;

    @InjectMock
    private SecurityContext securityContext;

    @InjectMock
    private JsonWebToken token;

    @Inject
    private JwtService jwtService;

    private Message message;

    private User sender;

    private User recipient;

    private MessageDto messageDto;

    private Group group;

    private GroupMessageDto groupMessageDto;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(UUID.randomUUID().toString());
        sender.setUsername("username1");
        sender.setPassword("password");

        recipient = new User();
        recipient.setId(UUID.randomUUID().toString());
        recipient.setUsername("username2");
        recipient.setPassword("password");

        message = new Message();
        message.setId(UUID.randomUUID().toString());
        message.setSender(sender);
        message.setMessage("some message");
        message.setTime(LocalDateTime.now());

        messageDto = new MessageDto(
                message.getId(),
                sender.getId(),
                sender.getUsername(),
                recipient.getId(),
                recipient.getUsername(),
                message.getMessage(),
                message.getTime().toString()
        );

        group = new Group();
        group.setId(UUID.randomUUID().toString());
        group.setName("group");

        groupMessageDto = new GroupMessageDto(
                message.getId(),
                sender.getId(),
                sender.getUsername(),
                message.getMessage(),
                group.getId(),
                group.getName(),
                message.getTime().toString()
        );
    }

    @Test
    void sendMessage() {
        when(messageConverter.convertToModel(message)).thenReturn(messageDto);
        when(messageService.writeMessage(message.getMessage(), recipient.getUsername(), sender.getId()))
                .thenReturn(message);

        String jwtToken = jwtService.generateToken(sender.getUsername(), sender.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .body(messageDto)
                .when()
                .post("/message/send")
                .then()
                .statusCode(201);
    }

    @Test
    void getMessages() {
        List<MessageDto> messages = new ArrayList<>();
        messages.add(messageDto);

        when(messageService.getMessages(sender.getId(), recipient.getUsername(), 0, 10))
                .thenReturn(messages);

        String jwtToken = jwtService.generateToken(sender.getUsername(), sender.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .body(messages)
                .when()
                .get("/message/" + recipient.getUsername())
                .then()
                .statusCode(200);
    }

    @Test
    void messageGroup() {
        when(groupMessageConverter.convertToModel(message)).thenReturn(groupMessageDto);
        when(messageService.messageGroup(message.getMessage(), group.getName(), sender.getId()))
                .thenReturn(message);

        String jwtToken = jwtService.generateToken(sender.getUsername(), sender.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .body(groupMessageDto)
                .when()
                .post("/message/group")
                .then()
                .statusCode(201);
    }

    @Test
    void getGroupMessages() {
        List<GroupMessageDto> messages = new ArrayList<>();
        messages.add(groupMessageDto);

        when(messageService.getGroupMessages(group.getName(), sender.getId(), 0, 10))
                .thenReturn(messages);

        String jwtToken = jwtService.generateToken(sender.getUsername(), sender.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .body(messages)
                .when()
                .get("/message/group/" + group.getName())
                .then()
                .statusCode(200);
    }
}