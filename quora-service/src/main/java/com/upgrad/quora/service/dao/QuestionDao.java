package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.Question;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;


// DAO class for Question- database operations.

@Repository
public class QuestionDao {

    private static final String GETQUESTION_OF_SAME_OWNER = "getquestionOfSameOwner";
    private static final String GET_ALL_QUESTIONS = "getAllQuestions";
    private static final String GET_ALL_QUESTIONS_FOR_USER = "getAllQuestionsForUser";
    private static final String GET_QUESTION = "getQuestion";

    // persitence for entity manager
    @PersistenceContext
    private EntityManager entityManager;

    // persistence entity to create question in database.

    public Question createQuestion(Question question) {
        entityManager.persist(question);
        return question;
    }


    //persistence entity to gets  all questions for a specific users uuid.

    public List<Question> getAllQuestionsForUser(String uuId) {
        try {
            return entityManager
                    .createNamedQuery(GET_ALL_QUESTIONS_FOR_USER)
                    .setParameter("uuid", uuId)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }


    // persistence entity to get all questions.

    public List<Question> getAllQuestions() {
        try {
            return entityManager
                    .createNamedQuery(GET_ALL_QUESTIONS)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    // persistence entity to get question for the same owner

    public Question getQuestion(String questionUuId) {
        try {
            return entityManager
                    .createNamedQuery(GET_QUESTION, Question.class)
                    .setParameter("uuid", questionUuId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    //persistence entity to edit the question details.

    public Question editQuestion(Question question) {
        entityManager.persist(question);
        return question;
    }


    // persistence entity to delete the question

    public void deleteQuestion(Question question) {
        entityManager.remove(question);
    }
}
