package com.tenco.dao;

import com.tenco.dto.Student;
import com.tenco.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    // TODO - 추후 사용하는 측 확인해서 리턴 타입 결정
    // 학생 등록 기능
    public int addStudent(Student student) {
        int rows = 0;
        String sql = """
                INSERT INTO students (name, student_id) VALUES
                (?, ?)
                """;
        try (Connection conn = DatabaseUtil.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, student.getName());
                pstmt.setString(2, student.getStudentId());
                // executeUpdate() : insert, update, delete 할 때
                rows = pstmt.executeUpdate();
                System.out.println(rows + " 행이 추가 되었습니다.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rows;
    }

    // 학생 전체 조회 기능
    public List<Student> getAllStudent() {
        List<Student> studentList = new ArrayList<>();
        // 사용할 SQL 구문
        String sql = """
                SELECT * FROM students ORDER BY id
                """;

        // 사용자 객체 생성
        try (Connection conn = DatabaseUtil.getConnection()) {
            // 쿼리를 실행할 PreparedStatment 준비
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    Student student = createStudent(rs);
                    studentList.add(student);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return studentList;
    }

    // 학번으로 학생 조회 -> 로그인
    public Student getStudentByStudentId(String studentId) {
        // 사용할 SQL 구문
        String sql = """
                SELECT * FROM students
                WHERE student_id = ?
                """;

        // 사용자 객체 생성
        try (Connection conn = DatabaseUtil.getConnection()) {
            // 쿼리를 실행할 PreparedStatment 준비
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, studentId);

                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    // 정상 조회
                    Student student = createStudent(rs);
                    return student;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private static Student createStudent(ResultSet rs) throws SQLException {
        Student student = new Student(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("student_id"));
        return student;
    }
}
