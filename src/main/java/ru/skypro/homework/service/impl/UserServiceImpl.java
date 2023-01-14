package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.webjars.NotFoundException;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.CreateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.exception.PasswordException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserService;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(CreateUserDto userDto) {
        User newUser = userRepository.save(userMapper.toUser(userDto));
        newUser.setRole("USER");
        return userMapper.toDto(newUser);
    }


    @Override
    public UserDto getUserMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userMapper.toDto(getUser(authentication.getName()));
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        try {
            User user = getUser(userDto.getLogin());
            if (userDto.getFirstName() != null) {
                user.setFirstName(userDto.getFirstName());
            }

            if (userDto.getLastName() != null) {
                user.setLastName(userDto.getLastName());
            }

            if (userDto.getPhoneNumber() != null) {
                user.setPhoneNumber(userDto.getPhoneNumber());
            }

            User response = userRepository.save(user);
            return userMapper.toDto(response);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }

    }

    @Override
    public NewPasswordDto setPassword(NewPasswordDto newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = getUser(authentication.getName());
        if (!passwordEncoder.matches(newPassword.getCurrentPassword(), user.getPassword())) {
            throw new PasswordException();
        }

        String newPass = passwordEncoder.encode(newPassword.getNewPassword());
        user.setPassword(newPass);
        User response = userRepository.save(user);
        log.info("The user with login = {} was updated ", response.getLogin());

        return newPassword;

    }

    @Override
    public UserDto removeUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        userRepository.delete(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserDto getUserById(long id) {
        return userMapper.toDto(userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new));
    }

    @Override
    public User getUser(String login) {
        return userRepository.findUserByLoginIgnoreCase(login)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public boolean isAdmin(Authentication authentication) {
        return authentication != null
                && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }
}
