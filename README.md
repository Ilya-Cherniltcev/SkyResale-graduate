### Дипломный проект ###

(Еще не)Выполненный Command2 студентов Skypro JD 5.0 

Command2 это Чернильцев Илья, Государев Андрей, Лиджиев Вадим, Юков Сергей
***
### Функционал приложения ###
Мы создали backend-часть для платформы по перепродаже вещей, по предоставленной спецификации и frontend-части.

В этом приложении могут быть следующие типы пользователей:
1. **Анонимный пользователь** он может:
- получать список всех имеющихся объявлений.
2. **Авторизованный пользователь** с правами **USER** может:
- получать список всех имеющихся объявлений,
- получать конкретное объявление,
- создавать, редактировать и удалять свои объявления,
- получать список комментариев к любому объявлению,
- создавать, редактировать и удалять свои комментарии,
- редактировать/удалять свои комментарии.
3. **Авторизованный пользователь** с правами **ADMIN** может:
- выполнять все действия, что и авторизованный пользователь с правами **USER**,
- удалять любого пользователя (во frontend-части не реализовано),
- редактировать и удалять любые объявления и комментарии.

При первом запуске приложения создаются два стандартных пользователя: 

|  Role  |      Login       | Password  |
|:------:|:----------------:|:---------:|
|  USER  | user@gmail.com   |   user    |
| ADMIN  | admin@gmail.com  |   admin   |

***
### Запуск frontend-части через Docker ###

docker run --rm -p 3000:3000 adsclient:v16

***
But I ~~can't~~ see you every night &copy;