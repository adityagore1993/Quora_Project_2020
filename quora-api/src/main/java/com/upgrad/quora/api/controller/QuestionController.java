package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionEditResponse;
import com.upgrad.quora.api.model.QuestionEditRequest;
import com.upgrad.quora.api.model.QuestionDeleteResponse;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/")
public class QuestionController {

    @Autowired
    QuestionService questionService;

    @RequestMapping(value = "/question/create", method = RequestMethod.POST)
    public ResponseEntity<QuestionResponse> createQuestion (
            final QuestionRequest questionRequest,
            @RequestHeader("authorization") final String authorizationHeader
    ) throws AuthorizationFailedException {

        String bearerToken = extractBearerToken(authorizationHeader);

        QuestionEntity newQuestion = new QuestionEntity();
        newQuestion.setUuid(UUID.randomUUID().toString());
        newQuestion.setContent(questionRequest.getContent());
        newQuestion.setDate(ZonedDateTime.now());

        final QuestionEntity createdQuestion = questionService.createNewQuestion(newQuestion, bearerToken);

        return new ResponseEntity<>(
                new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED"),
                HttpStatus.CREATED
        );

    }

    @RequestMapping(value = "/question/all", method = RequestMethod.GET)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions (
            @RequestHeader("authorization") final String authorizationHeader
    ) throws AuthorizationFailedException {

        String bearerToken = extractBearerToken(authorizationHeader);

        final List<QuestionEntity> listOfQuestions = questionService.getAllQuestions(bearerToken);

        return getQuestionListRespEntity(listOfQuestions);

    }

    @RequestMapping(value = "/question/all/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser (
            @PathVariable("userId") final String userUuid,
            @RequestHeader("authorization") final String authorizationHeader
    ) throws AuthorizationFailedException, UserNotFoundException {

        String bearerToken = extractBearerToken(authorizationHeader);

        final List<QuestionEntity> listOfQuestions = questionService.getQuestionByUserUuid(userUuid, bearerToken);

        return getQuestionListRespEntity(listOfQuestions);

    }

    @RequestMapping(value = "/question/edit/{questionId}", method = RequestMethod.PUT)
    public ResponseEntity<QuestionEditResponse> editQuestion(
        final QuestionEditRequest editRequest,
        @PathVariable("questionId") final String questionUuid,
        @RequestHeader("authorization") final String authorization
    ) throws AuthorizationFailedException, InvalidQuestionException {
        String bearerToken = extractBearerToken(authorization);

        //return value not needed
        final QuestionEntity questionAfterEdit = questionService.editQuestionContent(questionUuid, editRequest.getContent(), bearerToken);

        return new ResponseEntity<>(
                new QuestionEditResponse().id(questionAfterEdit.getUuid()).status("QUESTION EDITED"),
                HttpStatus.OK
        );

    }

    @RequestMapping(value = "/question/delete/{questionId}", method = RequestMethod.DELETE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(
        @PathVariable("questionId") final String questionUuid,
        @RequestHeader("authorization") final String authorizationHeader
    ) throws InvalidQuestionException, AuthorizationFailedException {

        String bearerToken = extractBearerToken(authorizationHeader);

        questionService.deleteQuestion(questionUuid, bearerToken);

        return new ResponseEntity<>(
                new QuestionDeleteResponse().id(questionUuid).status("QUESTION DELETED"),
                HttpStatus.CREATED
        );

    }

    private ResponseEntity<List<QuestionDetailsResponse>> getQuestionListRespEntity(List<QuestionEntity> listOfQuestions) {
        List<QuestionDetailsResponse> questionDetailsResponseList = new ArrayList<QuestionDetailsResponse>();

        for (QuestionEntity eachQuestion:
                listOfQuestions
        ) {
            questionDetailsResponseList.add(
                    new QuestionDetailsResponse().id(eachQuestion.getUuid()).content(eachQuestion.getContent())
            );
        }

        return new ResponseEntity<>(
                questionDetailsResponseList,
                HttpStatus.OK
        );
    }


    private String extractBearerToken(String authorizationCode) {

        try {
            if(authorizationCode!=null && authorizationCode.contains("Bearer ")) {
                return authorizationCode.split("Bearer ")[1]; // will throw Index OOB exception
            } else {
                return authorizationCode;
            }

        } catch (NullPointerException | ArrayIndexOutOfBoundsException aib) {
            // missing authCode
            return "";
        }

    }

}
