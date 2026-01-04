package com.development.expense.service;

import com.development.expense.constant.CodeConstant;
import com.development.expense.constant.MessageConstant;
import com.development.expense.dto.ApiResponse;
import com.development.expense.dto.ForgotPasswordDto;
import com.development.expense.dto.UserDto;
import com.development.expense.entity.UserEntity;
import com.development.expense.repository.UserRepository;
import com.development.expense.rest.dto.SendOTPRequest;
import com.development.expense.util.GlobalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final VerificationService verificationService;

    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    public String add(UserEntity userEntity) {
        if (userEntity.getUsername().isBlank()) {
            return "username is required";
        }
        if (userEntity.getPassword().isBlank()) {
            return "password is required";
        }
        if (userEntity.getUsername().contains(" ") || userEntity.getPassword().contains(" ")) {
            return "username or password contains whitespace";
        }
        var originalUsername = userEntity.getUsername();
        var lowerCaseUsername = userEntity.getUsername().toLowerCase();
        if (!originalUsername.equals(lowerCaseUsername)) {
            return "username must be lowercase";
        }
        if (userEntity.getPassword().length() < 6) {
            return "password must be at least 6 characters";
        }
        if (userEntity.getRole() == null) {
            return "role is required";
        }
        if (userEntity.getStatus() == null) {
            return "status is required";
        }
        UserEntity findUser = userRepository.findUserEntityByUsername(userEntity.getUsername());
        if (findUser != null) {
            if (findUser.getUsername().equals(userEntity.getUsername())) {
                return "username is already taken";
            }
        }
        userEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        userRepository.save(userEntity);
        return "inserted";
    }

    public String login(UserEntity userEntity) {
        if (userEntity.getUsername().isBlank()) {
            return "username is required";
        }
        if (userEntity.getPassword().isBlank()) {
            return "password is required";
        }
        if (userEntity.getUsername().contains(" ") || userEntity.getPassword().contains(" ")) {
            return "username or password contains whitespace";
        }
        UserEntity findUser = userRepository.findUserEntityByUsername(userEntity.getUsername());
        if (findUser == null) {
            return "username or password is incorrect";
        }
        if (!findUser.getUsername().equals(userEntity.getUsername())) {
            return "username is incorrect";
        }
        if (!findUser.getPassword().equals(userEntity.getPassword())) {
            return "password is incorrect";
        }
        return "logged in";
    }

    public String update(Long id, UserDto userDto) {
        UserEntity find = userRepository.findById(id).orElse(null);
        if (find == null) {
            return "user not found";
        }
        find.setId(id);
        if (userDto.fullName() != null && !userDto.fullName().isBlank()) {
            find.setFullName(userDto.fullName());
        }
        if (userDto.username() != null && !userDto.username().isBlank()) {
            find.setUsername(userDto.username());
        }
        if (userDto.status() != null) {
            find.setStatus(userDto.status());
        }
        if (userDto.telegramChatId() != null) {
            find.setTelegramChatId(userDto.telegramChatId());
        }
        find.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userRepository.save(find);
        return "update success";
    }

    public String delete(Long id) {
        UserEntity find = userRepository.findById(id).orElse(null);
        if (find == null) {
            return "user not found";
        }
        userRepository.delete(find);
        return "delete success";
    }

    public ApiResponse forgotPassword(ForgotPasswordDto forgotPasswordDto) {
        ApiResponse apiResponse = new ApiResponse();
        UserEntity find = userRepository.findUserEntityByUsername(forgotPasswordDto.username());
        if (find == null) {
            apiResponse.setCode(CodeConstant.NOT_FOUND);
            apiResponse.setMessage(MessageConstant.NOT_FOUND);
            return apiResponse;
        }
        if (find.getTelegramChatId() == null) {
            apiResponse.setCode(CodeConstant.NOT_FOUND);
            apiResponse.setMessage("telegram chat not found");
            return apiResponse;
        }
        SendOTPRequest sendOTPRequest = new SendOTPRequest();
        sendOTPRequest.setChatId(find.getTelegramChatId());
        String otp = GlobalUtil.generateOTP(6);
        sendOTPRequest.setOtp(otp);
        find.setOneTimePassword(otp);
        find.setExpiration(new Timestamp(System.currentTimeMillis() + 120000));
        userRepository.save(find);
        return verificationService.sendOTP(sendOTPRequest);
    }
}