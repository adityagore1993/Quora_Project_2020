package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.ZonedDateTime;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

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

    public void updateUser(final UserEntity updatedUserEntity) {
        entityManager.merge(updatedUserEntity);
    }


    public UserAuthTokenEntity getUserAuthToken(final String accessToken) throws SignOutRestrictedException {
        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class)
                    .setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException exc) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }

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

    //This implementation for CommonController
    public UserAuthTokenEntity isValidActiveAuthToken(final String accessToken) throws AuthorizationFailedException{
        try {
            UserAuthTokenEntity userAuthTokenEntity = entityManager.createNamedQuery("userByAccessToken", UserAuthTokenEntity.class)
                    .setParameter("accessToken", accessToken).getSingleResult();

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
}
