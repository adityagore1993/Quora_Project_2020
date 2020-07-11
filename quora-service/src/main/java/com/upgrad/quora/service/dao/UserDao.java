package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;

import javax.validation.ConstraintViolationException;

import com.upgrad.quora.service.exception.UserNotFoundException;
import com.upgrad.quora.service.type.ActionType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.ZonedDateTime;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    //Persisting user entity. Done when the user signs up
    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    public UserEntity getUserByEmail(final String email) {
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class)
                    .setParameter("email", email).getSingleResult();
        } catch (NoResultException exc) {
            return null;
        }

    }

    //Get user details by user's username
    //Returns UserEntity
    public UserEntity getUserByUsername(final String username) {
        try {
            return entityManager.createNamedQuery("userByUsername", UserEntity.class)
                    .setParameter("username", username).getSingleResult();
        } catch (NoResultException exc) {
            return null;
        }

    }

    //Get user details by user UUID
    //Returns UserEntity
    public UserEntity getUserById(final String uuid) throws UserNotFoundException {
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class)
                    .setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException exc) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }

    }

    //Creating access token entry when the user signs in
    //Returns UserAuthTokenEntity
    public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthTokenEntity) {
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    //Signout function
    //Returns UUID of the signed out user
    public String signOut(final String accessToken) throws SignOutRestrictedException {
        UserAuthTokenEntity userAuthTokenEntity = entityManager.createNamedQuery("userByAccessToken", UserAuthTokenEntity.class)
                .setParameter("accessToken", accessToken).getSingleResult();
        final ZonedDateTime now = ZonedDateTime.now();

        Integer userId = userAuthTokenEntity.getUser().getId();
        userAuthTokenEntity.setLogoutAt(now);
        entityManager.merge(userAuthTokenEntity);
        UserEntity userEntity = entityManager.createNamedQuery("userById", UserEntity.class)
                .setParameter("id", userId).getSingleResult();
        return userEntity.getUuid();
    }

    //To check if the user with the access token is signed in / access token exists in the table
    //Returns boolean based on whether the access token is present in the table
    public boolean hasUserSignedIn(final String accessToken) {
        try {
            UserAuthTokenEntity userAuthTokenEntity = entityManager.createNamedQuery("userByAccessToken", UserAuthTokenEntity.class)
                    .setParameter("accessToken", accessToken).getSingleResult();
            return true;
        } catch (NoResultException exception) {
            return false;
        }

    }

    /*  Merged with isValidActiveAuthToken method with authroization and ActionType as parameters
        //Written isValidActiveAuthToken twice once for CommonController and once for AdminController
        //Reason: AdminController has one more check for admin role and Exception messages are slightly different
        //This implementation for CommonController
        public UserAuthTokenEntity isValidActiveAuthToken(final String accessToken) throws AuthorizationFailedException{
            try {
                UserAuthTokenEntity userAuthTokenEntity = entityManager.createNamedQuery("userByAccessToken", UserAuthTokenEntity.class)
                        .setParameter("accessToken", accessToken).getSingleResult();
                final ZonedDateTime now = ZonedDateTime.now();
                if(userAuthTokenEntity.getLogoutAt()==null){
                    return userAuthTokenEntity;
                } else {
                    //Exception message for CommonController
                    throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
                }
            } catch (NoResultException exception) {
                throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
            }

        }
    */

    /* Written separately for admin authorization. Then merged with isValidActiveAuthToken
     public UserAuthTokenEntity isValidActiveAuthTokenForAdmin(final String accessToken) throws AuthorizationFailedException {
        try {
            UserAuthTokenEntity userAuthTokenEntity = entityManager.createNamedQuery("userByAccessToken", UserAuthTokenEntity.class)
                    .setParameter("accessToken", accessToken).getSingleResult();
            final ZonedDateTime now = ZonedDateTime.now();
            if (userAuthTokenEntity.getLogoutAt() == null) {
                return userAuthTokenEntity;
            } else {
                //Exception message for CommonController
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.");
            }
        } catch (NoResultException exception) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

    }
    */

    //Written for all authorization calls for all controllers
    public UserAuthTokenEntity isValidActiveAuthToken(final String accessToken, Enum<ActionType> actionType) throws AuthorizationFailedException {
        try {
            UserAuthTokenEntity userAuthTokenEntity = entityManager.createNamedQuery("userByAccessToken", UserAuthTokenEntity.class)
                    .setParameter("accessToken", accessToken).getSingleResult();
            final ZonedDateTime now = ZonedDateTime.now();
            if (userAuthTokenEntity.getLogoutAt() == null) {
                return userAuthTokenEntity;
            } else {
                //Exception message for CommonController
                if (actionType.equals(ActionType.GET_USER_DETAILS)) {
                    throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
                } else if (actionType.equals(ActionType.DELETE_USER)) {
                    throw new AuthorizationFailedException("ATHR-002", "User is signed out");
                } else if (actionType.equals(ActionType.CREATE_QUESTION)) {
                    throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
                } else if (actionType.equals(ActionType.ALL_QUESTION)) {
                    throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
                } else if (actionType.equals(ActionType.EDIT_QUESTION)) {
                    throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
                } else if (actionType.equals(ActionType.DELETE_QUESTION)) {
                    throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
                } else if (actionType.equals(ActionType.ALL_QUESTION_FOR_USER)) {
                    throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
                } else if (actionType.equals(ActionType.CREATE_ANSWER)) {
                    throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
                } else if (actionType.equals(ActionType.EDIT_ANSWER)) {
                    throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
                } else if (actionType.equals(ActionType.DELETE_ANSWER)) {
                    throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
                } else if (actionType.equals(ActionType.GET_ALL_ANSWER_TO_QUESTION)) {
                    throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the answers");
                } else {
                    throw new AuthorizationFailedException("ATHR-002", "User is signed out.");
                }
            }
        } catch (NoResultException exception) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
    }

    /* Created initial for validating access token later combined into other methods
    //To check if the user has a valid acces token / access token exists and is valid
    //Returns boolean based on 2 factors: The expires_at time is greater than current time and LogoutAt is null
    public boolean isUserAccessTokenValid(final String accessToken) {
        UserAuthTokenEntity userAuthTokenEntity = entityManager.createNamedQuery("userByAccessToken", UserAuthTokenEntity.class)
                .setParameter("accessToken", accessToken).getSingleResult();
        final ZonedDateTime now = ZonedDateTime.now();
        if(userAuthTokenEntity.getLogoutAt()==null){
            return true;
        } else {
            return false;
        }
    }
    */
    //To check if the user corresponding to this access token is has admin role
    //Return boolean based on the value in the "role" field
    public boolean isRoleAdmin(final String accessToken) {
        UserAuthTokenEntity userAuthTokenEntity = entityManager.createNamedQuery("userByAccessToken", UserAuthTokenEntity.class)
                .setParameter("accessToken", accessToken).getSingleResult();
        UserEntity userEntity = userAuthTokenEntity.getUser();
        if (userEntity.getRole().equalsIgnoreCase("admin")) {
            return true;
        } else {
            return false;
        }
    }

    //Update user details
    public void updateUser(final UserEntity updatedUserEntity) {
        entityManager.merge(updatedUserEntity);
    }


    //To fetch UserAuthTokenEntity for particular acces token
    public UserAuthTokenEntity getUserAuthToken(final String accessToken) throws SignOutRestrictedException {
        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class)
                    .setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException exc) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }

    }
}
