package com.myapp.listapp;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.*;

public class AddBookController {

    @FXML
    private TextField bookNameField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField genreField;
    @FXML
    private TextField stockField;

    private TableView<Book> tableView;

    public void setTableView(TableView<Book> tableView) {
        this.tableView = tableView;
    }

    @FXML
    void ADDBooktoDBbutton() {

        DatabaseConnection dbConnection = new DatabaseConnection();
        Connection connectDB = dbConnection.getDBConnection();

        String bookName = bookNameField.getText().trim();
        String author = authorField.getText().trim();
        String genre = genreField.getText().trim();
        String stockText = stockField.getText().trim();

        if (bookName.isEmpty() || author.isEmpty() || genre.isEmpty() || stockText.isEmpty()) {
            showAlert("Input Error", "Please fill in all fields.");
            return;
        }
        int stock;
        try {
            stock = Integer.parseInt(stockText);
            if (stock < 0) {
                showAlert("Input Error", "Stock value must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Stock must be a valid number.");
            return;
        }


        String insertQuery = "INSERT INTO Books (\"Book name\", \"Author\", \"Genre\", \"Stock\") VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement statement = connectDB.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, bookName);
            statement.setString(2, author);
            statement.setString(3, genre);
            statement.setInt(4, stock);

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            int bookID = -1;
            if (generatedKeys.next()) {
                bookID = generatedKeys.getInt(1);
            }

            Book book = new Book(bookID, bookName, author, genre, stock);
            tableView.getItems().add(book);

            showAlert("Book added!","Book added with ID: " + bookID);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        bookNameField.clear();
        authorField.clear();
        genreField.clear();
        stockField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
