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
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserService;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    /**
     * Метод создания пользователя.
     * @param userDto ДТО с информацией о новом пользователе. Пароль должен быть
     *                зашифрован
     * @return ДТО с дабавленной в базу информацией
     */
    @Override
    public UserDto createUser(UserDto userDto) {
        User response = userRepository.save(userMapper.toUser(userDto));
        return userMapper.toDto(response);
    }

    /**
     * Метод получения всех пользователей
     * @return возвращает список пользователей из базы данных
     */
    @Override
    public Collection<UserDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Метод изменения пользователя.
     * @param userDto принимает ДТО с обновленной информацией.
     *                Изменяются только заполненные поля
     * @return возвращает ДТО с актуализированной информацией
     */
    @Override
    public UserDto updateUser(UserDto userDto) {
        log.info("Пытаемся обновить ДТО с  username = {}", userDto.getEmail());
        try {
            User user = getUser(userDto.getEmail());
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
            log.info("Пользователь с  id = {} обновлен ", response.getId());
            return userMapper.toDto(response);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }

    }

    /**
     * Метод присваивания пароля.
     * @param newPassword Принимает ДТО с текущим и новым паролем
     * @return новый пароль, либо ResponseStatusException
     */
    @Override
    public NewPasswordDto setPassword(NewPasswordDto newPassword) {
        log.info("Присваивание нового пароля");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = getUser(authentication.getName());
        if (!passwordEncoder.matches(newPassword.getCurrentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        String newPass = passwordEncoder.encode(newPassword.getNewPassword());
        user.setPassword(newPass);
        User response = userRepository.save(user);
        log.info("Пароль у пользователя с username = {} изменен ", response.getEmail());

        return newPassword;

    }
    /**
     * Метод удаления пользователя, принимающий в параметры
     * @param id идентификатор пользователя и если пользователь найден
     * @return возвращает удаленного пользователя, либо исключение NotFoundException.
     */
    @Override
    public User removeUser(long id) {
        User user = getUserById(id);
        if (user != null) {
            userRepository.delete(user);
        }
        return user;
    }

    @Override
    public User getUserById(long id) {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow((Supplier<RuntimeException>) () -> new NotFoundException("The user with id = " + id + " doesn't exist"));

        return user;
    }
    /**
     * Метод получения пользователя по имени, принимающий в параметры
     * @param username имя пользователя и если найден,
     * @return возвращает пользователя, либо возвращает NotFoundException
     */
    @Override
    public User getUser(String username) {
        User user = userRepository
                .findUserByEmailIgnoreCase(username)
                .orElseThrow((Supplier<RuntimeException>) () -> new NotFoundException("The user with name = " + username + " doesn't exist"));
        return user;
    }
    /**
     * Проверка, является ли пользователь администратором
     * @param authentication принимает текущую аутентификауию
     * @return
     */
    @Override
    public boolean isAdmin(Authentication authentication) {
        return authentication != null
                && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }
}
