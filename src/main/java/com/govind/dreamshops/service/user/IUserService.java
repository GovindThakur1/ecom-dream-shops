package com.govind.dreamshops.service.user;

import com.govind.dreamshops.dto.UserDto;
import com.govind.dreamshops.model.User;
import com.govind.dreamshops.request.CreateUserRequest;
import com.govind.dreamshops.request.UpdateUserRequest;

public interface IUserService {
    User getUserById(Long userId);
    User createUser(CreateUserRequest request);
    User updateUser(UpdateUserRequest request, Long userId);
    void deleteUser(Long userId);


    UserDto convertUserToDto(User user);

    User getAuthenticatedUser();
}
