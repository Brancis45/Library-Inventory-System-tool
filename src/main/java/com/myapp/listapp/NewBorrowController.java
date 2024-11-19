package com.myapp.listapp;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class NewBorrowController {

    private int generateRandomBookID() {
        Random random = new Random();
        return 10000 + random.nextInt(90000);
    }

    TableView<Book> tableView;
    TableView<Borrower> borrowerTableView;

    public void setBorrowerTableView(TableView<Borrower> borrowerTableView) {
        this.borrowerTableView = borrowerTableView;
    }
    public void setTableView(TableView<Book> tableView) {
        this.tableView = tableView;
    }

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private TextField borrowBookName;

    @FXML
    private TextField borrowBookAuthor;

    @FXML
    private TextField borrowerFullName;

    @FXML
    private DatePicker borrowDate;

    DatabaseConnection dbConnection = new DatabaseConnection();
    Connection connectDB = dbConnection.getDBConnection();

    public void onAddNewBorrowerButtonAction() {

        int borrowBookID = generateRandomBookID();

        String borrowBook = borrowBookName.getText().trim();
        String bBookAuthor = borrowBookAuthor.getText().trim();
        String borrower = borrowerFullName.getText().trim();
        LocalDate selectedDate = borrowDate.getValue();

        if (borrowBook.isEmpty() || bBookAuthor.isEmpty() || borrower.isEmpty() || selectedDate == null) {
            showAlert("Input Error", "All fields must be filled in to add a new borrower.");
            return;
        }

        LocalDate returnDate = selectedDate.plusDays(14);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String selectDate = selectedDate.format(formatter);
        String returnedDate = returnDate.format(formatter);
        int isReturned = 0;

        try {
            // Step 1: Check if the book exists in the Books table
            String getBookIDQuery = "SELECT \"Book ID\" FROM Books WHERE \"Book name\" = ? AND \"Author\" = ?";
            try (PreparedStatement getBookIDStatement = connectDB.prepareStatement(getBookIDQuery)) {
                getBookIDStatement.setString(1, borrowBook);
                getBookIDStatement.setString(2, bBookAuthor);
                try (ResultSet resultSet = getBookIDStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        // Book does not exist, show alert and exit
                        showAlert("Error", "No book found with the given name and author.");
                        return;
                    }
                }
            }

            // Step 2: Insert into the Borrows table
            String newBorrowQuery = "INSERT INTO Borrows (\"Book ID\", \"Book name\", \"Book author\", \"Borrower full name\", "
                    + "\"Borrow Date\", \"Return Date\", \"Returned\") VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connectDB.prepareStatement(newBorrowQuery)) {
                statement.setInt(1, borrowBookID);
                statement.setString(2, borrowBook);
                statement.setString(3, bBookAuthor);
                statement.setString(4, borrower);
                statement.setString(5, selectDate);
                statement.setString(6, returnedDate);
                statement.setInt(7, isReturned);

                statement.executeUpdate();
            }

            // Step 3: Decrease stock for the book
            String decreaseStockQuery = "UPDATE Books SET \"Stock\" = \"Stock\" - 1 WHERE \"Book name\" = ? AND \"Author\" = ?";
            try (PreparedStatement updateStatement = connectDB.prepareStatement(decreaseStockQuery)) {
                updateStatement.setString(1, borrowBook);
                updateStatement.setString(2, bBookAuthor);
                int rowsUpdated = updateStatement.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("Stock updated successfully for Book: " + borrowBook);
                } else {
                    showAlert("Error", "Failed to update stock for the book: " + borrowBook);
                }
            }

            // Step 4: Add the borrower to the TableView
            Borrower newBorrower = new Borrower(borrowBookID, borrowBook, bBookAuthor, borrower, selectDate, returnedDate, false);
            borrowerTableView.getItems().add(newBorrower);

            // Reload books in the main controller
            if (mainController != null) {
                mainController.loadBooksFromDatabase();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Clear input fields
        borrowBookName.clear();
        borrowBookAuthor.clear();
        borrowerFullName.clear();
        borrowDate.setValue(null);
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
