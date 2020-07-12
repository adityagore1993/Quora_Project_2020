package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    AnswerBusinessService service;

    @RequestMapping(value = "/question/{questionId}/answer/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(
            final AnswerRequest newAnswerRequest,
            @PathVariable("questionId") final String questionUUID,
            @RequestHeader("authorization") final String authorization
    ) throws InvalidQuestionException, AuthorizationFailedException {

        String bearerToken = extractBearerToken(authorization);

        final AnswerEntity newAnswer = new AnswerEntity();
        newAnswer.setAnswer(newAnswerRequest.getAnswer());
        newAnswer.setUuid(UUID.randomUUID().toString());
        newAnswer.setCreatedDate(ZonedDateTime.now());

        final AnswerEntity createdAnswer = service.addNewAnswer(questionUUID, newAnswer, bearerToken);

        return new ResponseEntity<>(
                new AnswerResponse().id(createdAnswer.getUuid()).status("ANSWER CREATED"),
                HttpStatus.CREATED
        );

    }

    @RequestMapping(value = "/answer/edit/{answerId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswer(
            final AnswerEditRequest editRequest,
            @RequestParam("answerId") final String answerUuid,
            @RequestHeader("authorization") final String authorization
    ) throws AuthorizationFailedException, AnswerNotFoundException {
        String bearerToken = extractBearerToken(authorization);

        //return value not needed
        final AnswerEntity answerAfterEdit = service.editAnswer(answerUuid, editRequest.getContent(), bearerToken);

        return new ResponseEntity<>(
                new AnswerEditResponse().id(answerAfterEdit.getUuid()).status("ANSWER EDITED"),
                HttpStatus.OK
        );

    }

    private String extractBearerToken(String authorizationCode) {

        try {

            return authorizationCode.split("Bearer ")[1]; // will throw Index OOB exception

        } catch (NullPointerException | ArrayIndexOutOfBoundsException aib) {
            // missing authCode
            return "";
        }

    }

}
