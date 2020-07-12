package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.type.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerBusinessService {
    @Autowired
    AnswerDao answerDao;

    @Autowired
    QuestionDao questionDao;

    @Autowired
    UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity addNewAnswer(String questionUuid, AnswerEntity newAnswer, final String authorizationToken) throws InvalidQuestionException, AuthorizationFailedException {

        //question check
        Question question = questionDao.getQuestion(questionUuid);
        if(question == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }

        newAnswer.setQuestion(question);

        // Authority check
        UserAuthTokenEntity authToken = userDao.isValidActiveAuthToken(authorizationToken, ActionType.CREATE_ANSWER);
        newAnswer.setUser(authToken.getUser());

        return answerDao.createAnswer(newAnswer);

    }

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

}
