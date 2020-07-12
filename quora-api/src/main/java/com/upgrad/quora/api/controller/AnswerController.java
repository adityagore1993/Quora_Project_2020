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

        String[] authorizationSplitText = authorization.split("Bearer ");
        if(authorizationSplitText.length != 2) {
            //throw bad request error?
        }

        final AnswerEntity newAnswer = new AnswerEntity();
        newAnswer.setAnswer(newAnswerRequest.getAnswer());
        newAnswer.setUuid(UUID.randomUUID().toString());
        newAnswer.setCreatedDate(ZonedDateTime.now());

        final AnswerEntity createdAnswer = service.addNewAnswer(questionUUID, newAnswer, authorizationSplitText[2]);

        return new ResponseEntity<AnswerResponse>(
                new AnswerResponse().id(createdAnswer.getUuid()).status("ANSWER CREATED"),
                HttpStatus.CREATED
        );

    }
}
