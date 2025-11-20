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

import static org.mockito.ArgumentMatchers.any;
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
        sender.setId(UUID.randomUUID());
        sender.setUsername("username1");
        sender.setPassword("password");

        recipient = new User();
        recipient.setId(UUID.randomUUID());
        recipient.setUsername("username2");
        recipient.setPassword("password");

        message = new Message();
        message.setId(UUID.randomUUID());
        message.setSender(sender);
        message.setText("some message");
        message.setTime(LocalDateTime.now());

        messageDto = new MessageDto(
                message.getId(),
                sender.getId(),
                sender.getUsername(),
                recipient.getId(),
                recipient.getUsername(),
                message.getText(),
                message.getTime().toString()
        );

        group = new Group();
        group.setId(UUID.randomUUID());
        group.setName("group");

        groupMessageDto = new GroupMessageDto(
                message.getId(),
                sender.getId(),
                sender.getUsername(),
                message.getText(),
                group.getId(),
                group.getName(),
                message.getTime().toString()
        );
    }

    @Test
    void sendMessage() {
        when(messageConverter.convertToModel(message)).thenReturn(messageDto);
        when(messageService.sendMessage(message.getText(), recipient.getId(), sender.getId()))
                .thenReturn(message);

        String jwtToken = jwtService.generateToken(sender.getUsername(), String.valueOf(sender.getId()));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .body(messageDto)
                .when()
                .post("/messages")
                .then()
                .statusCode(201);
    }

    @Test
    void getMessages() {
        List<Message> messages = new ArrayList<>();
        messages.add(message);

        when(messageService.getMessages(sender.getId(), recipient.getId(), 0, 10))
                .thenReturn(messages);
        when(messageConverter.convertToModel(any(Message.class))).thenReturn(messageDto);

        String jwtToken = jwtService.generateToken(sender.getUsername(), String.valueOf(sender.getId()));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .body(messages)
                .when()
                .get("/messages/" + recipient.getUsername())
                .then()
                .statusCode(200);
    }

    @Test
    void messageGroup() {
        when(groupMessageConverter.convertToModel(message)).thenReturn(groupMessageDto);
        when(messageService.messageGroup(message.getText(), group.getId(), sender.getId()))
                .thenReturn(message);

        String jwtToken = jwtService.generateToken(sender.getUsername(), String.valueOf(sender.getId()));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .body(groupMessageDto)
                .when()
                .post("/messages/group")
                .then()
                .statusCode(201);
    }

    @Test
    void getGroupMessages() {
        List<Message> messages = new ArrayList<>();
        messages.add(message);

        when(messageService.getGroupMessages(group.getId(), sender.getId(), 0, 10))
                .thenReturn(messages);

        String jwtToken = jwtService.generateToken(sender.getUsername(), String.valueOf(sender.getId()));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .body(messages)
                .when()
                .get("/messages/group/" + group.getName())
                .then()
                .statusCode(200);
    }
}
