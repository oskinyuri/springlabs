package com.example.demo.dao;

import com.example.demo.model.ExamType;
import com.example.demo.model.StudyPlan;
import com.example.demo.model.Subject;
import com.example.demo.model.SubjectFullInfo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SubjectFullJdbc {
    private final JdbcTemplate jdbcTemplate;

    public SubjectFullJdbc(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<SubjectFullInfo> getAll() {
        List<StudyPlan> studyPlanList = jdbcTemplate.query(
                "SELECT * FROM STUDY_PLAN",
                this::mapStudyPlan);
        List<SubjectFullInfo> subjectFullInfoList = new ArrayList<>();
        for (StudyPlan item: studyPlanList) {
            Subject subject = jdbcTemplate.queryForObject(
                    "SELECT * FROM SUBJECT WHERE ID = ?",
                    this::mapSubject,
                    item.getSubjectId()
            );
            ExamType examType = jdbcTemplate.queryForObject(
                    "SELECT * FROM EXAM_TYPE WHERE ID = ?",
                    this::mapExamType,
                    item.getExamTypeId()
            );
            subjectFullInfoList.add(new SubjectFullInfo(subject.getName(), examType.getType()));
        }

        return subjectFullInfoList;
    }

    private StudyPlan mapStudyPlan(ResultSet rs, int i) throws SQLException {
        return new StudyPlan(
                rs.getInt("id"),
                rs.getInt("subject_id"),
                rs.getInt("exam_type_id")
        );
    }

    private Subject mapSubject(ResultSet rs, int i) throws SQLException {
        return new Subject(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("short_name")
        );
    }

    private ExamType mapExamType(ResultSet rs, int i) throws SQLException {
        return new ExamType(
                rs.getInt("id"),
                rs.getString("type")
        );
    }
}