package com.tenco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Borrow {
    private int id;
    private int bookId;
    private int studentId;
    private LocalDate borrowDate;
    private LocalDate returnDate;

    public Borrow(int bookId, int studentId, LocalDate borrowDate, LocalDate returnDate) {
        this.bookId = bookId;
        this.studentId = studentId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    public Borrow(int id, LocalDate returnDate) {
        this.id = id;
        this.returnDate = returnDate;
    }
}
