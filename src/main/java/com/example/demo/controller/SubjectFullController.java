package com.example.demo.controller;

import com.example.demo.dao.SubjectFullJdbc;
import com.example.demo.model.SubjectFullInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/subject")
public class SubjectFullController {

    private final SubjectFullJdbc subjectFullJdbc;

    public SubjectFullController(SubjectFullJdbc subjectFullJdbc) {
        this.subjectFullJdbc = subjectFullJdbc;
    }

    //Просмотр всех групп
    @GetMapping
    public List<SubjectFullInfo> studyGroupAll() {
        return subjectFullJdbc.getAll();
    }

}
