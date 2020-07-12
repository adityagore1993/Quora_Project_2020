package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    final static String ADMIN_KEY_CODE = "admin";

    public Boolean deleteUserByUuid(
            String uuid, String authorizationCode
    ) throws UserNotFoundException, AuthorizationFailedException {

        UserEntity userToDelete = userDao.getUserById(uuid);

        UserAuthTokenEntity authToken = userDao.queryAuthToken(authorizationCode);

        if(authToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if(authToken.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        } else if(!ADMIN_KEY_CODE.equals(authToken.getUser().getRole())) {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        } else if(userToDelete == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }

        return userDao.deleteUserByUuid(userToDelete.getUuid());

    }
}
