package com.tenco;

import com.tenco.dao.BorrowDAO;
import com.tenco.dto.Borrow;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        BorrowDAO borrowDAO = new BorrowDAO();
//        // 대출 현황 조회
//        List<Borrow> borrowList = borrowDAO.getAllBorrows();
//        for(Borrow borrow : borrowList){
//            printBorrow(borrow);
//        }
//        // 대출 기능
//        Borrow borrow = new Borrow(2, 2, LocalDate.now(), null);
//        borrowDAO.borrowBook(borrow);
//
//        // 반납 기능
//        Borrow borrow = new Borrow(2, LocalDate.now());
//        borrowDAO.returnBook(borrow);
    }

    private static void printBorrow(Borrow borrow){
        int id = borrow.getId();
        int bookId = borrow.getBookId();
        int studentId = borrow.getStudentId();
        LocalDate borrowDate = borrow.getBorrowDate();
        LocalDate returnDate = borrow.getReturnDate();
        System.out.printf("%d | %d | %d | %s | %s\n",
                id, bookId, studentId, borrowDate, returnDate);

    }
}