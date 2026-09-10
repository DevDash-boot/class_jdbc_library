package com.tenco;

import com.tenco.dao.BorrowDAO;
import com.tenco.dto.Borrow;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        BorrowDAO borrowDAO = new BorrowDAO();
        System.out.println("id | book_id | title | student_id | borrow_date | return_date");
        // 대출 현황 조회
        List<Borrow> borrowList = borrowDAO.getBorrowedBooks();
        for (Borrow borrow : borrowList) {
            printBorrow(borrow);
        }

        // 대출 기능
        // borrowDAO.borrowBook(1, 1);

        // 반납 기능
        // borrowDAO.returnBook(3, 1);
    }
    private static void printBorrow(Borrow borrow) {
        int id = borrow.getId();
        int bookId = borrow.getBookId();
        String bookTitle = borrow.getBookTitle();
        int studentId = borrow.getStudentId();
        String studentName = borrow.getStudentName();
        LocalDate borrowDate = borrow.getBorrowDate();
        Date returnDate = borrow.getReturnDate();
        System.out.printf("%d | %d | %s | %d | %s | %s | %s\n",
                id, bookId, bookTitle, studentId, studentName, borrowDate, returnDate);
    }
}

