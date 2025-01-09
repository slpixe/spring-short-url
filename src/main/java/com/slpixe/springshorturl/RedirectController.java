package com.slpixe.springshorturl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class RedirectController {

    @GetMapping("/a")
    public String redirectToGoogle() {
        return "redirect:https://www.google.com";
    }

    @GetMapping("/s/{thing}")
    public String redirectToThing(@PathVariable String thing) {
        return "redirect:https://www.example.com/" + thing;
    }
}
