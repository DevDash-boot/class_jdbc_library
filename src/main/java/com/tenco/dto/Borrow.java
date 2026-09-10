package com.tenco.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Borrow {
    private int id;
    private int bookId;
    private int studentId;
    private LocalDate borrowDate;
    private Date returnDate;
    // DTO는 테이블과 꼭 1:1로 맞출 필요가 없다.
    // SQL 실행 결과를 담는 그릇이므로 JOIN으로 가져온 컬럼 결과도 담을 수 있다.
    private String bookTitle;
    private String studentName;

}