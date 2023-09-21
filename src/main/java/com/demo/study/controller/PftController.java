package com.demo.study.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PftController {
    @GetMapping("/pft/{project}")
    public String project(@PathVariable String project, Model model){
        model.addAttribute("pft", project);
        return "pft";
    }
}
