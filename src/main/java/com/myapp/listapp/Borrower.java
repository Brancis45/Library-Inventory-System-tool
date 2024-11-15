package com.myapp.listapp;

public class Borrower {

    private int bookID;
    private String borrowedBookName;
    private String borrowedBookAuthor;
    private String borrowerFullName;
    private String borrowDate;
    private String returnDate;
    private Boolean isReturned;

    public Borrower(int bookID, String borrowedBookName, String borrowedBookAuthor, String borrowerFullName, String borrowDate, String returnDate, Boolean isReturned) {
        this.bookID = bookID;
        this.borrowedBookName = borrowedBookName;
        this.borrowedBookAuthor = borrowedBookAuthor;
        this.borrowerFullName = borrowerFullName;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.isReturned = isReturned;
    }

    public int getBookID() {
        return bookID;
    }

    public String getBorrowedBookName() {
        return borrowedBookName;
    }
    public String getBorrowedBookAuthor() {
        return borrowedBookAuthor;
    }

    public String getBorrowerFullName() {
        return borrowerFullName;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public String getReturnDate() {
        return returnDate;
    }
    public Boolean getIsReturned() {
        return isReturned;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public void setBorrowedBookName(String borrowedBookName) {
        this.borrowedBookName = borrowedBookName;
    }

    public void setBorrowedBookAuthor(String borrowedBookAuthor) {
        this.borrowedBookAuthor = borrowedBookAuthor;
    }

    public void setBorrowerFullName(String borrowerFullName) {
        this.borrowerFullName = borrowerFullName;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }
    public void setIsReturned(Boolean isReturned) {
        this.isReturned = isReturned;
    }
}
