package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    public static final String USER_HAS_NOT_SIGNED_IN = "User has not signed in";
    final static String ADMIN_KEY = "admin";

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;


    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createNewQuestion(
            QuestionEntity newQuestion,
            String authCode
    ) throws AuthorizationFailedException {

        UserAuthTokenEntity authTokenEntity = userDao.queryAuthToken(authCode);

        if(authTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if(authTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        UserEntity contextUser = authTokenEntity.getUser();

        newQuestion.setUser(contextUser);

        return questionDao.createQuestion(newQuestion);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestions(
            String authCode
    ) throws AuthorizationFailedException {

        UserAuthTokenEntity authTokenEntity = userDao.queryAuthToken(authCode);

        if(authTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if(authTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        }

        return questionDao.getAllQuestions();

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionContent(
            String questionUuid,
            String updatedContent,
            String authCode
    ) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity authTokenEntity = userDao.queryAuthToken(authCode);

        if(authTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if(authTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        }

        QuestionEntity questionToUpdate = questionDao.getQuestion(questionUuid);
        if(questionToUpdate == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        UserEntity currentUser = authTokenEntity.getUser();
        UserEntity recordOwner = questionToUpdate.getUser();

        if(
                currentUser.getUuid().equals(recordOwner.getUuid())
        ){
            questionToUpdate.setContent(updatedContent);

            final QuestionEntity updatedQuestion = questionDao.editQuestion(questionToUpdate);

            return updatedQuestion;
        } else {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }



    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteQuestion(
            final String answerUuid, final String authCode
    ) throws InvalidQuestionException, AuthorizationFailedException {

        QuestionEntity questionToDelete = questionDao.getQuestion(answerUuid);
        if(questionToDelete == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        // Authority check
        // may throw ATHR-001 or ATHR-002
        UserAuthTokenEntity authToken = userDao.queryAuthToken(authCode);//, ActionType.CREATE_ANSWER);
        if(authToken == null) {
            throw new AuthorizationFailedException("ATHR-001", USER_HAS_NOT_SIGNED_IN);
        } else if(authToken.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
        }

        UserEntity currentUser = authToken.getUser();

        UserEntity recordOwner = questionToDelete.getUser();

        if(
                currentUser.getUuid().equals(recordOwner.getUuid()) ||
                        ADMIN_KEY.equals(currentUser.getRole())
        ) {
            questionDao.deleteQuestion(questionToDelete);
        } else {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getQuestionByUserUuid (
            String uuid,
            String authCode
    ) throws AuthorizationFailedException, UserNotFoundException {

        UserAuthTokenEntity authTokenEntity = userDao.queryAuthToken(authCode);

        if(authTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if(authTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
        }

        UserEntity requestedUserContext = userDao.getUserById(uuid);
        if(requestedUserContext == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }

        return questionDao.getAllQuestionsForUser(uuid);

    }

}
