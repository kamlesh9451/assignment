package com.test.assignment;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailCountController {

    private final GmailService gmailService;

    public EmailCountController(GmailService gmailService) {
        this.gmailService = gmailService;
    }

    @GetMapping("/count")
    public int getEmailCount(@RequestParam String sender) throws Exception {
        return gmailService.countEmailsFrom(sender);
    }
}
