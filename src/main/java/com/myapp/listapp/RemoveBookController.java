package com.myapp.listapp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class RemoveBookController {

    @FXML
    private Button removeBookfromDBbutton;

    @FXML
    public void initialize() {
        Platform.runLater(() -> removeBookfromDBbutton.requestFocus());
    }

    @FXML
    private TextField removeBookName;

    private TableView<Book> tableView;

   public void setTableView(TableView<Book> tableView) {
       this.tableView = tableView;
    }

    @FXML
    void removeBookfromDB() {
        DatabaseConnection dbConnection = new DatabaseConnection();
        Connection connectDB = dbConnection.getDBConnection();

        String removableBookIDText = removeBookName.getText().trim();
        if (removableBookIDText.isEmpty()) {
            showAlert("Input Error", "Please enter a valid Book ID.");
            return;
        }

        int removableBookID;
        try {
            removableBookID = Integer.parseInt(removableBookIDText);
            if (removableBookID < 0) {
                showAlert("Input Error", "Book ID must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Book ID must be a valid number.");
            return;
        }

        String deleteQuery = "DELETE FROM Books WHERE \"Book ID\" = ?";

        try {
            PreparedStatement statement = connectDB.prepareStatement(deleteQuery);
            statement.setInt(1, removableBookID);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                showAlert("Error", "No book found with the provided ID.");
            } else {
                tableView.getItems().removeIf(book -> book.getBookID() == removableBookID);
                tableView.refresh();
                showAlert("Success", "Book removed successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database Error, could not remove the book from the database.");
        }

        removeBookName.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
