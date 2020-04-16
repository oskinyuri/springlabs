package com.example.demo.dao;

import com.example.demo.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JournalRecordJdbc {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public JournalRecordJdbc(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("journal")
                .usingGeneratedKeyColumns("id");
    }

    public long create(JournalRecord journalRecord) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("student_id", journalRecord.getStudentId());
        parameters.put("study_plan_id", journalRecord.getStudyPlanId());
        parameters.put("in_time", journalRecord.isInTime());
        parameters.put("count", journalRecord.getCount());
        parameters.put("mark_id", journalRecord.getMarkId());

        return simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
    }

    public JournalRecord get(int id) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM journal WHERE id = ?",
                this::mapJournalRecord,
                id
        );
    }

    public List<JournalRecord> getAll() {
        return jdbcTemplate.query("SELECT * FROM JOURNAL", this::mapJournalRecord);
    }

    public List<JournalRecord> getAllByStudent(int studentId) {
        return jdbcTemplate.query(
                "SELECT * FROM journal WHERE student_id = ?",
                this::mapJournalRecord,
                studentId
        );
    }

    public List<JournalRecordExpanded> getAllExpendedByStudent(int studentId) {
        List<JournalRecord> recordList = jdbcTemplate.query(
                "SELECT * FROM journal WHERE student_id = ?",
                this::mapJournalRecord,
                studentId
        );
        List<JournalRecordExpanded> recordExpandedList = new ArrayList<>();

        for (JournalRecord item : recordList) {
            StudyPlan studyPlan = jdbcTemplate.queryForObject(
                    "SELECT * FROM STUDY_PLAN WHERE ID = ?",
                    this::mapStudyPlan,
                    item.getStudyPlanId()
            );
            Subject subject = jdbcTemplate.queryForObject(
                    "SELECT * FROM SUBJECT WHERE ID = ?",
                    this::mapSubject,
                    studyPlan.getSubjectId()
            );
            ExamType examType = jdbcTemplate.queryForObject(
                    "SELECT * FROM EXAM_TYPE WHERE ID = ?",
                    this::mapExamType,
                    studyPlan.getExamTypeId()
            );
            Mark mark = getMark(item.getMarkId());

            recordExpandedList.add(new JournalRecordExpanded(
                    item.getId(),
                    item.getStudentId(),
                    item.getStudyPlanId(),
                    item.isInTime(),
                    item.getCount(),
                    item.getMarkId(),
                    subject.getName(),
                    subject.getShortName(),
                    examType.getType(),
                    mark.getName(),
                    mark.getValue()
            ));

        }
        return recordExpandedList;
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

    public List<JournalRecord> getAllByStudyGroup(int studyGroupId) {
        return jdbcTemplate.query(
                "SELECT journal.id, student_id, study_plan_id, in_time, count, mark_id " +
                        "FROM journal INNER JOIN student ON journal.student_id = student.id " +
                        "WHERE study_group_id = ?",
                this::mapJournalRecord,
                studyGroupId
        );
    }

    public int update(int id, JournalRecord journalRecord) {
        return jdbcTemplate.update(
                "UPDATE journal SET student_id = ?, study_plan_id = ?, in_time = ?, count = ?, mark_id = ?" +
                        "WHERE id = ?",
                journalRecord.getStudentId(),
                journalRecord.getStudyPlanId(),
                journalRecord.isInTime(),
                journalRecord.getCount(),
                journalRecord.getMarkId(),
                id
        );
    }

    public int delete(int id) {
        return jdbcTemplate.update(
                "DELETE FROM journal WHERE id = ?",
                id
        );
    }

    private JournalRecord mapJournalRecord(ResultSet rs, int i) throws SQLException {
        return new JournalRecord(
                rs.getInt("id"),
                rs.getInt("student_id"),
                rs.getInt("study_plan_id"),
                rs.getBoolean("in_time"),
                rs.getInt("count"),
                rs.getInt("mark_id")
        );
    }

    private Mark getMark(int id){
        return jdbcTemplate.queryForObject("SELECT * FROM MARK WHERE id = ?", this::mapMark, id);
    }
    private Mark mapMark(ResultSet rs, int i) throws SQLException {
        Mark mark = new Mark(
                rs.getInt( "id"),
                rs.getString( "name"),
                rs.getString( "value")
        );
        return mark;
    }
}
