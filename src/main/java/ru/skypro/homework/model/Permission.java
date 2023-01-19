package ru.skypro.homework.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum Permission {
    USER_BASIC_ACCESS("user_basic_access"),
    ADMIN_FULL_ACCESS("admin_full_access");
    private final String permission;


}
