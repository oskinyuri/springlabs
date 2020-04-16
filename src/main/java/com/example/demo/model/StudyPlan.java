package com.example.demo.model;

public class StudyPlan {
    private int id;
    private int subjectId;
    private int examTypeId;

    public StudyPlan(int id, int subjectId, int examTypeId) {
        this.id = id;
        this.subjectId = subjectId;
        this.examTypeId = examTypeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getExamTypeId() {
        return examTypeId;
    }

    public void setExamTypeId(int examTypeId) {
        this.examTypeId = examTypeId;
    }
}
