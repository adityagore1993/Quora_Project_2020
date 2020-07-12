package com.upgrad.quora.service.business;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerBusinessService {

    final static String ADMIN_KEY = "admin";

    @Autowired
    AnswerDao answerDao;

    @Autowired
    QuestionDao questionDao;

    @Autowired
    UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity addNewAnswer(String questionUuid, AnswerEntity newAnswer, final String authorizationToken) throws InvalidQuestionException, AuthorizationFailedException {

        //question check
        QuestionEntity question = questionDao.getQuestion(questionUuid);
        if(question == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }

        newAnswer.setQuestion(question);

        // Authority check
        UserAuthTokenEntity authToken = userDao.isValidActiveAuthToken(authorizationToken, ActionType.CREATE_ANSWER);
        newAnswer.setUser(authToken.getUser());

        return answerDao.createAnswer(newAnswer);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswer(
            final String answerUuid, final String content, final String authorization
    ) throws AnswerNotFoundException, AuthorizationFailedException {

        AnswerEntity answerForEdit = answerDao.getAnswerByUuid(answerUuid);
        if(answerForEdit == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        // Authority check
        // may throw ATHR-001 or ATHR-002
        UserAuthTokenEntity authToken = userDao.isValidActiveAuthToken(authorization, ActionType.EDIT_ANSWER);

        UserEntity currentUser = authToken.getUser();

        UserEntity answeredBy = answerForEdit.getUser();

        if(!currentUser.getUuid().equals(answeredBy.getUuid())) { // not owner
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }

        answerForEdit.setAnswer(content);

        return answerDao.updateAnswer(answerForEdit);

    }

    public Boolean deleteAnswer(
            final String answerUuid, final String authCode
    ) throws AnswerNotFoundException, AuthorizationFailedException {

        AnswerEntity answerToDelete = answerDao.getAnswerByUuid(answerUuid);
        if(answerToDelete == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        // Authority check
        // may throw ATHR-001 or ATHR-002
        UserAuthTokenEntity authToken = userDao.isValidActiveAuthToken(authCode, ActionType.DELETE_ANSWER);

        UserEntity currentUser = authToken.getUser();

        UserEntity recordOwner = answerToDelete.getUser();

        if(
                currentUser.getUuid().equals(recordOwner.getUuid()) ||
                        ADMIN_KEY.equals(currentUser.getRole())
        ) {
            return answerDao.deleteAnswer(answerToDelete);
        } else {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        }

    }

    public List<AnswerEntity> getAllAnswersByQuestionUuid(
            final String uuid, final String authCode
    ) throws AuthorizationFailedException, InvalidQuestionException{

        QuestionEntity searchedQuestion = questionDao.getQuestion(uuid);
        if(searchedQuestion == null) {
            throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
        }

        // Authority check
        // may throw ATHR-001 or ATHR-002
        userDao.isValidActiveAuthToken(authCode, ActionType.GET_ALL_ANSWER_TO_QUESTION);

        return answerDao.getAllAnswersByQuestionUuid(searchedQuestion.getId());

    }

}
