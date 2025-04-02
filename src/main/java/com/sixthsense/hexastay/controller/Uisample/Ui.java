package com.sixthsense.hexastay.controller.Uisample;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class Ui {

    @GetMapping("/uisample")
    public String uiDesign() {

        return "uisample/uisample";
    }

}
