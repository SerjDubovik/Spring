package ru.pufr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MailController {



    @GetMapping("/sendmail")
    public String blogAdd (Model model){
        sendEmail();
        return "about";
    }




    @Autowired
    private JavaMailSender javaMailSender;

    void sendEmail() {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("mail@pufr.ru");
        msg.setTo("mail@mail.ru");    //
        msg.setSubject("Testing from Spring Boot");
        msg.setText("Hello World \n Spring Boot Email");

        javaMailSender.send(msg);

    }

    void sendEmailRegister(String mail, String uuidString) {            // метод, для отправки письма для подтверждения аккаунта

        SimpleMailMessage msg = new SimpleMailMessage();

        String rnd =  "RegistrationNewUser/" + uuidString;
        //
        //http://localhost:59055/

        msg.setFrom("mail@pufr.ru");
        msg.setTo("mail@mail.com");          // mail
        msg.setSubject("Активация аккаунта на PUFR.RU");
        msg.setText("Это сообщение сформировано автоматически, не отвечайте на это письмо.\n " +
                "Если Вы не проходили регистрацию на сайте PUFR.RU, игнорируйте это письмо.\n" +
                "Для активации аккаунта, перейдите по ссылке: " + "http://pufr.ru/" + rnd +" регистрация завершится автоматически.\n"+
                "Ссылка будет активна не более суток с момента отправки сообщения");

        javaMailSender.send(msg);

    }


}
