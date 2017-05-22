package com.ptc.taskanalyse.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by asasmaz on 21/05/2017.
 */
@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
