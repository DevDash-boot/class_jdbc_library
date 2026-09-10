package com.tenco.view;

import com.tenco.dto.Book;
import com.tenco.dto.Borrow;
import com.tenco.dto.Student;
import com.tenco.service.LibraryService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

// 사용자의 입출력을 처리하는 View 클래스
// [역할]
// 키보드의 입력을 받아 Service에 넘기고, 결과를 화면에 출력한다.
// SQL 을 직접 실행하지 않고, 업무 규칙도 판단하지 않는다.
// "빈 값인가?", "숫자인가?" 같은 입력 형식을 검사하고 서비스 단에 맞는 객채나 값을 구해서 일을 위임한다.
public class LibraryView2 {
    // 포함 관계
    // View는 Service만 알고 있으면 실행할 수 있는 역할
    private final LibraryService libraryService = new LibraryService();
    private final Scanner scanner = new Scanner(System.in);

    // 현재 로그인한 학생 정보가 NULL이 아니라면 로그인된 상태로 보면 된다.
    // 만약 NULL이라면 로그인이 필요한 기능에서 로그인 요청을 먼저 유도해야한다.
    private Integer currentStudentId = null;
    private String currentStudentName = null;
    private Student currentStudent = null;
    Scanner sc = new Scanner(System.in);

    // 프로그램 메인 루프
    // [처리 순서]
    // (1) 메뉴 출력
    // (2) 번호를 입력 받는다.
    // (3) 번호에 맞는 메서드를 호출한다.
    // (4) 호출 중 SQLExcetion이 발생하면 에러 메시지를 출력하고 다시 1번으로 돌아간다.
    // (5) 0번을 입력하면 프로그램 종료 또는 return 으로 루프를 빠져나간다.
    public void start() {
        while (true) {
            // 메뉴 출력
            System.out.println("===== 도서 관리 시스템 시작 =====");

            System.out.println("===== 메뉴 출력 =====");
            System.out.println("1. 도서 등록 | 2. 도서 목록 | 3. 도서 검색 | 4. 학생 등록 | 5. 학생 목록");
            System.out.println("6. 학생 검색 |7. 대출 중인 도서 목록 | 8. 대출 | 9. 반납 | 0. 종료");
            System.out.print("번호 선택 : ");

            // 로그인 되어있다면 로그인 표시, 안되어 있다면 로그인
            if (currentStudent != null) {
                System.out.println("[ 현재 로그인  : " + currentStudent.getName() + " ]");
            }
            // 사용자 입력값 받기
            int userInput = sc.nextInt();
            System.out.println("선택 : " + userInput);

            // 예외 처리가 반드시 필요함
            switch (userInput) {
                case 1:addBook();break;
                case 2: getAllBooks();break;
                case 3: searchBooksByTitle();break;
                case 4: addStudent();break;
                case 5: getAllStudents();break;
                case 6: getStudentByStudentId();break;
                case 7: getBorrowedBooks(); break;
                case 8:
                    if(currentStudent == null){
                        System.out.println("로그인 먼저 하세요");
                        break;
                    }
                    borrowBook();
                    break;
                case 9:
                    if(currentStudent == null){
                        System.out.println("로그인 먼저 하세요");
                        break;
                    }
                    returnBook();
                    break;
                case 0:
                    System.out.println("프로그램을 종료합니다.");
                    sc.close();
                    return;
                default:
                    System.out.println("잘못된 번호입니다.");
                    break;
            }
        }
    }

    // 1. 도서 등록
    public void addBook() {
        System.out.println("도서 등록 : ");
        System.out.print("제목 : ");
        String title = sc.next();
        System.out.print("저자 : ");
        String author = sc.next();
        System.out.print("출판사 : ");
        String publisher = sc.next();
        System.out.print("출판년도 : ");
        int publicationYear = sc.nextInt();
        System.out.print("ISBN : ");
        String isbn = sc.next();

        Book book = new Book(title, author, publisher, publicationYear, isbn);
        try {
            libraryService.addBook(book);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("도서 등록 완료");
        System.out.println();
    }

    // 2. 도서 전체 조회
    public void getAllBooks() {
        System.out.println("=== 도서 목록 ===");
        System.out.println("id |    제목    | 저자 | 출판사 | 출판년도 |    ISBN    | 이용여부");
        System.out.println("------------------------------------------------------------------");
        try {
            List<Book> bookList = libraryService.getAllBooks();
            for(Book book : bookList){
                printBook(book);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
    }

    // 3. 도서 제목으로 조회
    public void searchBooksByTitle() {
        System.out.print("=== 도서 검색 ===");
        String bookTitle = sc.next();

        System.out.println("id |    제목    | 저자 | 출판사 | 출판년도 |    ISBN    | 이용여부");
        System.out.println("------------------------------------------------------------------");
        try {
            List<Book> bookList = libraryService.searchBooksByTitle(bookTitle);
            if(bookTitle == null){
                System.out.println("해당 검색어를 가진 책이 없습니다.");
            }
            for(Book book : bookList){
                printBook(book);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
    }

    // 4. 학생 등록
    public void addStudent() {
        System.out.println("=== 학생 등록 ===");
        String studentName = sc.next();
        String studentId = sc.next();
        Student student = new Student(studentName, studentId);
        try {
            libraryService.addStudent(student);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
    }

    // 5. 학생 전체 조회
    public void getAllStudents() {
        System.out.println("=== 학생 목록 ===");
        System.out.println("id |  이름  | 학번 ");
        System.out.println("----------------------");
        try {
            List<Student> studentList = libraryService.getAllStudents();
            for(Student student : studentList){
                printStudent(student);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
    }

    // 6. 학생 학번으로 조회
    public void getStudentByStudentId() {
        System.out.print("학생 검색 : ");
        String studentId = sc.next();
        try {
            Student student = libraryService.getStudentByStudentId(studentId);
            if(student == null){
                System.out.println("해당 학번의 학생이 없습니다.");
            }
            System.out.println("id |  이름  | 학번 ");
            System.out.println("----------------------");
            printStudent(student);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
    }

    // 7. 대출 중인 도서 조회
    public void getBorrowedBooks() {
        System.out.println("=== 대출 중인 도서 목록 ===");
        System.out.println("id | 책 번호 |    책 제목    | 학생 번호 | 대출일 | 반납일");
        System.out.println("----------------------------------------------------------");
        try {
            List<Borrow> borrowList = libraryService.getBorrowedBooks();
            for (Borrow borrow : borrowList){
                printBorrow(borrow);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
    }

    // 8. 대출
    public void borrowBook() {
        System.out.println("=== 대출 ===");
        int bookId = sc.nextInt();
        int studentId = sc.nextInt();

        System.out.println("id | 책 번호 |    책 제목    | 학생 번호 | 대출일 | 반납일");
        System.out.println("----------------------------------------------------------");
        try {
            libraryService.borrowBook(bookId, studentId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
    }

    // 9. 반납
    public void returnBook() {
        System.out.println("=== 반납 ===");
        int bookId = sc.nextInt();
        int studentId = sc.nextInt();
        System.out.println("id | 책 번호 |    책 제목    | 학생 번호 | 대출일 | 반납일");
        System.out.println("----------------------------------------------------------");
        try {
            libraryService.returnBook(bookId, studentId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
    }

    private static void printBook(Book book) {
        int id = book.getId();
        String title = book.getTitle();
        String author = book.getAuthor();
        String publisher = book.getPublisher();
        int publicationYear = book.getPublicationYear();
        String isbn = book.getIsbn();
        boolean available = book.isAvailable();
        System.out.printf("%d | %s | %s | %s | %d | %s | %b\n",
                id, title, author, publisher, publicationYear, isbn, available);
    }

    private static void printStudent(Student student) {
        int id = student.getId();
        String name = student.getName();
        String studentId = student.getStudentId();
        System.out.printf("%d | %s | %s\n", id, name, studentId);
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

}   // end of class
