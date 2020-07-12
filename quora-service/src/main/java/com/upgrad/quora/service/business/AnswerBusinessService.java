package com.upgrad.quora.service.business;

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

        // Authority check
        UserAuthTokenEntity authToken = userDao.queryAuthToken(authorizationToken);
        if(authToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if(authToken.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        //question check
        QuestionEntity question = questionDao.getQuestion(questionUuid);
        if(question == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }

        newAnswer.setQuestion(question);

        newAnswer.setUser(authToken.getUser());

        return answerDao.createAnswer(newAnswer);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswer(
            final String answerUuid, final String content, final String authorization
    ) throws AnswerNotFoundException, AuthorizationFailedException {

        // Authority check
        // may throw ATHR-001 or ATHR-002
        UserAuthTokenEntity authToken = userDao.queryAuthToken(authorization);
        if(authToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if(authToken.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
        }

        AnswerEntity answerForEdit = answerDao.getAnswerByUuid(answerUuid);
        if(answerForEdit == null) {
            throw new AnswerNotFoundException("ANS-001", "User is signed out.Sign in first to post an answer");
        }

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

        // Authority check
        // may throw ATHR-001 or ATHR-002
        UserAuthTokenEntity authToken = userDao.queryAuthToken(authCode);
        if(authToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if(authToken.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        }

        AnswerEntity answerToDelete = answerDao.getAnswerByUuid(answerUuid);
        if(answerToDelete == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

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

        // Authority check
        // may throw ATHR-001 or ATHR-002
        UserAuthTokenEntity authToken = userDao.queryAuthToken(authCode);
        if(authToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if(authToken.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the answers");
        }

        QuestionEntity searchedQuestion = questionDao.getQuestion(uuid);
        if(searchedQuestion == null) {
            throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
        }

        return answerDao.getAllAnswersByQuestionUuid(searchedQuestion.getId());

    }

}
