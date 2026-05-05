package com.microfinanceBank.Customer.email;

import com.microfinanceBank.Customer.dto.CustomerDto;
import com.microfinanceBank.Customer.entity.Customer;
import com.microfinanceBank.Customer.service.Impl.Emailservice;
import com.sendgrid.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailQueue {
    private final Emailservice emailservice;

    @RabbitListener(queues = "welcomeMailQueue")
    public void welcomeMail(CustomerDto customerDto){
        log.info("Send welcome mail to ->{}", customerDto.getEmail());
        Response response = getWelcomeMailResponse(customerDto);
        System.out.println("emailservice "+response.getStatusCode());
        if(response.getStatusCode()==200||response.getStatusCode()==202)
            log.info("Account creation email was sent to "+ customerDto.getEmail());


    }


    public void birthdayMail(Customer customer){
        log.info("Send happy birthday mail to ->{}",customer);
        Response response = happyBirthDayMail(customer);
        if(response.getStatusCode()==200||response.getStatusCode()==202)
            log.info("Happy birthday  email was sent to -> "+customer.getEmail());

    }

    private Response happyBirthDayMail(Customer customer) {
        String name=customer.getFirstName().concat(" ").concat(customer.getLastName());
        StringBuilder message=new StringBuilder();
        message.append("<h5>").append("생일을 진심으로 축하드립니다, ")
                .append(name).append("님! 앞으로도 건강하고 행복한 나날이 가득하시길 바랍니다.");
        message.append("<h6>").append("")
                .append(customer.getCustomerDetails().getDob()).append(" 이후로도 항상 좋은 일들이 가득하시길 바랍니다. 즐거운 하루 보내세요!");

        EmailRequest emailRequest=new EmailRequest(customer.getEmail(),"생일 축하합니다!",message.toString() );
        com.sendgrid.Response response=emailservice.sendEmail(emailRequest);
        return response;
    }

    private com.sendgrid.Response getWelcomeMailResponse(CustomerDto customer) {
        EmailRequest emailRequest=new EmailRequest(customer.getEmail(),"가입을 환영합니다","계좌가 성공적으로 개설되었습니다.");
        com.sendgrid.Response response=emailservice.sendEmail(emailRequest);
        return response;
    }
}
