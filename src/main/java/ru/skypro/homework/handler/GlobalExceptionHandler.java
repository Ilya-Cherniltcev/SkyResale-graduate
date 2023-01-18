package ru.skypro.homework.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.skypro.homework.exception.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> userNotFoundExceptionHandler(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Пользователь не найден");
    }

    @ExceptionHandler(AdsNotFoundException.class)
    public ResponseEntity<String> adsNotFoundExceptionHandler(AdsNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Объявление не найдено");
    }
    @ExceptionHandler(AdsCommentNotFoundException.class)
    public ResponseEntity<String> adsCommentNotFoundExceptionHandler(AdsCommentNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Комментарий к объявлению не найден");
    }

    @ExceptionHandler(ExtensionIsNotCorrectException.class)
    public ResponseEntity<String> extensionIsNotCorrectExceptionHandler(ExtensionIsNotCorrectException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Загруженный файл c некорректным расширением");
    }

    @ExceptionHandler(AdsImageNotFoundException.class)
    public ResponseEntity<String> adsImageNotFoundExceptionHandler(AdsImageNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Изображение для объявления не найдена");
    }
    @ExceptionHandler(UserAvatarNotFoundException.class)
    public ResponseEntity<String> userAvatarNotFoundExceptionHandler(UserAvatarNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Аватар не найден");
    }
    @ExceptionHandler(ItIsNotYourCommentException.class)
    public ResponseEntity<String> itIsNotYourCommentExceptionHandler(ItIsNotYourCommentException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Нельзя редактировать чужой комментарий");
    }
    @ExceptionHandler(ItIsNotYourAdsException.class)
    public ResponseEntity<String> itIsNotYourAdsExceptionHandler(ItIsNotYourAdsException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Нельзя редактировать чужое объявление");
    }
    @ExceptionHandler(CommentFromAnotherAdsException.class)
    public ResponseEntity<String> commentFromAnotherAdsExceptionHandler(CommentFromAnotherAdsException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Комментарий от другого объявления");
    }
    @ExceptionHandler(PasswordsAreNotEqualsException.class)
    public ResponseEntity<String> passwordsAreNotEqualsExceptionHandler(PasswordsAreNotEqualsException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Введен неверный текущий пароль");
    }
    @ExceptionHandler(PasswordsAreEqualsException.class)
    public ResponseEntity<String> passwordsAreEqualsExceptionHandler(PasswordsAreEqualsException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Старый и новый пароли одинаковые");
    }
    @ExceptionHandler(SaveFileException.class)
    public ResponseEntity<String> saveFileExceptionHandler(SaveFileException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Проблема о время сохранения файла");
    }
    @ExceptionHandler(ReadFileException.class)
    public ResponseEntity<String> readFileExceptionHandler(ReadFileException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Проблема о время чтения файла");
    }
    @ExceptionHandler(NoContentException.class)
    public ResponseEntity<String> noContentExceptionHandler(NoContentException e) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body("Нет необходимых данных");
    }
    @ExceptionHandler(LoginAlreadyUsedException.class)
    public ResponseEntity<String> loginAlreadyUsedExceptionHandler(LoginAlreadyUsedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Этот логин уже используется");
    }


}
