package com.tenco;

import com.tenco.view.LibraryView2;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        LibraryView2 libraryView2 = new LibraryView2();
        libraryView2.start();
    }
}