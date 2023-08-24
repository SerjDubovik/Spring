package ru.pufr.controllers;

import ru.pufr.models.User;
import ru.pufr.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SchedulerService {

    @Autowired
    private UserRepository userRepository;

// "0 0 * * * *" = the top of every hour of every day.
// "*/10 * * * * *" = every ten seconds.
// "0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.
// "0 0 6,19 * * *" = 6:00 AM and 7:00 PM every day.
// "0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30, 10:00 and 10:30 every day.
// "0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays
// "0 0 0 25 12 ?" = every Christmas Day at midnight

    // событие срабатывает каждые 24 часа
    @Scheduled(cron = "0 0 23 * * *")
    public void cleanBaseUser() {


        Iterable<User> users = userRepository.findAll();

        for (User user : users) {

            if (user.getFirstName().equals("test")) {             // проверим, есть ли в базе не активированные учётные записи.

                userRepository.delete(user);                      // если есть, то удалим такие учётки !!!! пока тестово ищет по имени !!!!! исправить !!!!!!
            }
        }



    }
}
