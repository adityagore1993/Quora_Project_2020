package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;


@Repository
public class AdminDao {

    @PersistenceContext
    private EntityManager entityManager;

    /*Method to delete the user*/
    public String deleteUser(final String uuid) throws UserNotFoundException {
        try {
            UserEntity deletedUserEntity = entityManager.createNamedQuery("userByUuid", UserEntity.class)
                    .setParameter("uuid", uuid).getSingleResult();
            Integer deletedUserId = deletedUserEntity.getId();
            String deletedUserUuid = uuid;
            //We need to remove only on UserEntity as all other related table entries will be deleted
            // by @OnDelete annotation function defined for all foreign key fields
            entityManager.remove(deletedUserEntity);
            return deletedUserUuid;
        } catch (NullPointerException exc) {
            return null;
        }
    }

}
