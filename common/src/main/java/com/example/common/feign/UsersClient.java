package com.example.common.feign;

import com.example.common.domain.entity.Result;
import com.example.users.domain.dto.SearchUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "users")
public interface UsersClient {

    @GetMapping("/users/user/searchUser")
    Result getUserById(@RequestBody SearchUserDTO searchUserDTO);
}
