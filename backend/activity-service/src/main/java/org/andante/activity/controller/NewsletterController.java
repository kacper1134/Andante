package org.andante.activity.controller;

import lombok.RequiredArgsConstructor;
import org.andante.activity.controller.email.EmailSender;
import org.andante.activity.controller.mapper.NewsletterDTOModelMapper;
import org.andante.activity.dto.NewsletterDTO;
import org.andante.activity.logic.NewsletterService;
import org.andante.activity.logic.model.Newsletter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/activity/newsletter")
@Validated
public class NewsletterController {

    private static final String EMAIL_NOT_VALID_ERROR_MESSAGE = "Provided value '${validatedValue}' must be a valid email address";
    private static final String EMAIL_BLANK_ERROR_MESSAGE = "Provided email address must not be blank";

    private final NewsletterService newsletterService;
    private final NewsletterDTOModelMapper newsletterMapper;
    private final EmailSender emailSender;

    @PostMapping("/subscribe")
    public ResponseEntity<NewsletterDTO> subscribe(@RequestParam("email") @Email(message = EMAIL_NOT_VALID_ERROR_MESSAGE)
                                                   @NotBlank(message = EMAIL_BLANK_ERROR_MESSAGE) String emailAddress) {
        Newsletter serviceResponse = newsletterService.subscribe(emailAddress);
        NewsletterDTO newsletter = newsletterMapper.toDTO(serviceResponse);

        emailSender.sendNewsletter(emailAddress);

        return ResponseEntity.ok(newsletter);
    }

    @PutMapping("/subscription/confirm")
    public ResponseEntity<NewsletterDTO> confirmSubscription(@RequestParam("email") @Email(message = EMAIL_NOT_VALID_ERROR_MESSAGE)
                                                             @NotBlank(message = EMAIL_BLANK_ERROR_MESSAGE) String emailAddress) {
        Newsletter serviceResponse = newsletterService.confirmSubscription(emailAddress);
        NewsletterDTO newsletter = newsletterMapper.toDTO(serviceResponse);

        return ResponseEntity.ok(newsletter);
    }

    @DeleteMapping("/subscription/remove")
    public ResponseEntity<NewsletterDTO> unsubscribe(@RequestParam("email") @Email(message = EMAIL_NOT_VALID_ERROR_MESSAGE)
                                                     @NotBlank(message = EMAIL_BLANK_ERROR_MESSAGE) String emailAddress) {
        Newsletter serviceResponse = newsletterService.unsubscribe(emailAddress);
        NewsletterDTO newsletter = newsletterMapper.toDTO(serviceResponse);

        return ResponseEntity.ok(newsletter);
    }
}
