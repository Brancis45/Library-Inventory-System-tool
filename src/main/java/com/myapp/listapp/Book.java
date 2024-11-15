package com.myapp.listapp;

public class Book {

    private int bookID;
    private String bookName;
    private String author;
    private String genre;
    private int stock;

    public Book(int bookID, String bookName, String author, String genre, int stock){
        this.bookID = bookID;
        this.bookName = bookName;
        this.author = author;
        this.genre = genre;
        this.stock = stock;
    }

    public int getBookID() {
        return bookID;
    }

    public String getBookName() {
        return bookName;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public int getStock() {
        return stock;
    }
    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
