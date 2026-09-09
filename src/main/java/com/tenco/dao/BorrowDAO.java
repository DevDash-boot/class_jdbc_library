package com.tenco.dao;

import com.tenco.dto.Borrow;
import com.tenco.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {
    // 대출 현황 조회
    // JOIN 을 해서 도서 이름까지 출력
    public List<Borrow> getAllBorrows() {
        List<Borrow> borrowList = new ArrayList<>();
        String sql = """
                SELECT b.id, b.book_id, b.student_id, bk.title, b.borrow_date, b.return_date
                FROM borrows b
                INNER JOIN books bk
                ON bk.id = b.book_id;
                """;
        try (Connection conn = DatabaseUtil.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    borrowList.add(createBorrow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return borrowList;
    }

    // 도서 대출 기능(트랜잭션)
    // 빌리고자 하는 도서 대출 가능 여부 확인 - SELECT
    // 도서 대출 기록 - INSERT
    public void borrowBook(Borrow borrow) {
        int rows = 0;
        // 도서 대출 가능 여부 확인
        // available = true이면 도서 대출 가능
        String sql1 = """
                SELECT title, available FROM books
                WHERE id = ?;
                """;
        String sql2 = """
                INSERT INTO borrows(book_id, student_id, borrow_date) VALUES
                 (?, ?, ?)
                """;
        String sql3 = """
                UPDATE books
                SET available = FALSE
                WHERE id = ?
                """;
        try (Connection conn = DatabaseUtil.getConnection()) {
            try{
                // 트랜잭션
                conn.setAutoCommit(false);
                // 도서 대출 가능 여부 확인
                boolean available;
                try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
                    pstmt.setInt(1, borrow.getBookId());
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        available = rs.getBoolean("available");
                        if (!available) {
                            System.out.println("대출 불가능");
                            return;
                        }
                    } else {
                        System.out.println("존재하지 않는 도서입니다.");
                        return;
                    }
                }
                // 도서 대출 등록
                try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {
                    pstmt.setInt(1, borrow.getBookId());
                    pstmt.setInt(2, borrow.getStudentId());
                    pstmt.setDate(3, Date.valueOf(borrow.getBorrowDate()));

                    rows = pstmt.executeUpdate();
                    System.out.println(rows + "행이 추가 되었습니다.");
                }
                // 도서가 대출되면 도서 상태 available 를 0으로 변경
                try (PreparedStatement pstmt = conn.prepareStatement(sql3)) {
                    pstmt.setInt(1, borrow.getBookId());
                    pstmt.executeUpdate();
                }
                conn.commit();
                System.out.println("도서 대출 완료");
            } // end of try
            catch (SQLException e) {
                if (conn != null) {
                    conn.rollback(); // 실패 시 롤백
                    throw e;
                }
            } // end of catch
            finally {
                if (conn != null) {
                    conn.setAutoCommit(true); // 기본값 복원
                    conn.close();
                }
            }   // end of finally
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 도서 반납 기능(트랜잭션)
    // 대출 기록 확인 - SELECT
    // 반납 기록 등록 - UPDATE
    public void returnBook(Borrow borrow){
        int rows = 0;
        String sql1 = """
                SELECT * FROM borrows
                WHERE id = ?;
                """;
        String sql2 = """
                UPDATE borrows
                SET return_date = ?
                where id = ?;
                """;
        String sql3 = """
                UPDATE books
                SET available = TRUE
                WHERE id = ?
                """;
        try (Connection conn = DatabaseUtil.getConnection()) {
            try{
                // 트랜잭션
                conn.setAutoCommit(false);
                // 도서 대출 여부 확인
                try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
                    pstmt.setInt(1, borrow.getId());
                    ResultSet rs = pstmt.executeQuery();
                    if (!rs.next()) {
                        System.out.println("존재하지 않는 대출 기록입니다.");
                        return;
                    }
                }
                // 도서 반납 등록
                try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {
                    pstmt.setDate(1, Date.valueOf(borrow.getReturnDate()));
                    pstmt.setInt(2, borrow.getId());

                    rows = pstmt.executeUpdate();
                    System.out.println(rows + "행이 변경 되었습니다.");
                }
                // 도서가 반납되면 도서 상태 available 를 1로 변경
                try (PreparedStatement pstmt = conn.prepareStatement(sql3)) {
                    pstmt.setInt(1, borrow.getBookId());
                    pstmt.executeUpdate();
                }
                conn.commit();
                System.out.println("도서 반납 완료");
            } // end of try
            catch (SQLException e) {
                if (conn != null) {
                    conn.rollback(); // 실패 시 롤백
                    throw e;
                }
            } // end of catch
            finally {
                if (conn != null) {
                    conn.setAutoCommit(true); // 기본값 복원
                    conn.close();
                }
            }   // end of finally
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Borrow createBorrow(ResultSet rs) throws SQLException {
        Borrow borrow = new Borrow();
        borrow.setId(rs.getInt("id"));
        borrow.setBookId(rs.getInt("book_id"));
        borrow.setStudentId(rs.getInt("student_id"));
        borrow.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
        Date returnDate = rs.getDate("return_date");

        if (returnDate != null) {
            borrow.setReturnDate(returnDate.toLocalDate());
        }
        return borrow;
    }
}
