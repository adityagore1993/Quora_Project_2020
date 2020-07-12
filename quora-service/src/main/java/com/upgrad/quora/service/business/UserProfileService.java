package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUserProfile(final String userId, final String authorization) throws AuthorizationFailedException, UserNotFoundException {

        if(userDao.getUserById(userId) != null) {
            final UserAuthTokenEntity validActiveAuthToken = userDao.isValidActiveAuthToken(authorization);
            if(validActiveAuthToken!=null){
                return userDao.getUserById(userId);
            } else {
                throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
            }
        } else {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }

    }
}
