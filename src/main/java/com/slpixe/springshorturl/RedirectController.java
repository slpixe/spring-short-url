package com.slpixe.springshorturl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

    @GetMapping("/a")
    public String redirectToGoogle() {
        return "redirect:https://www.google.com";
    }
}
