package com.myapp.listapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    DatabaseConnection dbConnection = new DatabaseConnection();
    Connection connectDB = dbConnection.getDBConnection();

    @FXML
    void onSyncStockButtonAction() {

        Book selectedBook = tableView.getSelectionModel().getSelectedItem();

        if (selectedBook != null) {
            int newStock = selectedBook.getStock();

            String updateStockQuery = "UPDATE \"Books\" SET \"Stock\" = " + newStock +
                    " WHERE \"Book ID\" = '" + selectedBook.getBookID() +
                    "' AND \"Book name\" = '" + selectedBook.getBookName() +
                    "' AND \"Author\" = '" + selectedBook.getAuthor() +
                    "' AND \"Genre\" = '" + selectedBook.getGenre() + "'";

            try {
                Statement statement = connectDB.createStatement();

                statement.executeUpdate(updateStockQuery);

                showAlert("Success!","Stock updated for " + selectedBook.getBookName());

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error!","Error updating stock for " + selectedBook.getBookName());
            }
        } else {
            showAlert("Please select a book!","No book selected.");
        }
    }

    @FXML
    private TableView<Book> tableView;

    public void initialize(URL location, ResourceBundle resources) {

        tableView.setEditable(true);

        TableColumn<Book,String> bookIDCol = new TableColumn<>("Book ID");
        bookIDCol.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        bookIDCol.setEditable(false);

        TableColumn<Book, String> nameColumn = new TableColumn<>("Book Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        nameColumn.setEditable(false);

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorColumn.setEditable(false);

        TableColumn<Book, String> genreColumn = new TableColumn<>("Genre");
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        genreColumn.setEditable(false);

        TableColumn<Book, Integer> stockColumn = new TableColumn<>("Stock");
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        stockColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        stockColumn.setEditable(true);
        stockColumn.setOnEditCommit(event -> {Book book = event.getRowValue(); book.setStock(event.getNewValue());});

        tableView.getColumns().setAll(bookIDCol, nameColumn, authorColumn, genreColumn, stockColumn);

        String dbGramatasQuery = "SELECT \"Book ID\", \"Book name\", \"Author\", \"Genre\", \"Stock\" FROM Books";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(dbGramatasQuery);

            while(queryOutput.next()){
                int bookID = queryOutput.getInt("Book ID");
                String bookName = queryOutput.getString("Book name");
                String author = queryOutput.getString("Author");
                String genre = queryOutput.getString("Genre");
                int stock = queryOutput.getInt("Stock");

                Book book = new Book(bookID,bookName, author, genre, stock);
                tableView.getItems().add(book);

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void addBookButtonAction() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(MainController.class.getResource("/com/myapp/listapp/addbook.fxml"));
            Parent root1 = fxmlLoader.load();
            AddBookController addBookController = fxmlLoader.getController();
            addBookController.setTableView(tableView);
            Stage stage = new Stage();
            stage.setTitle("Add Book");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading FXML: " + e.getMessage());
        }
    }

    @FXML
    void removeBookButtonAction() {
        try {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainController.class.getResource("removebook.fxml"));
        Parent root1 = fxmlLoader.load();
        RemoveBookController removeBookController = fxmlLoader.getController();
        removeBookController.setTableView(tableView);
        Stage stage = new Stage();
        stage.setTitle("Remove Book");
        stage.setScene(new Scene(root1));
        stage.show();
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Error loading FXML: " + e.getMessage());
        }
    }

    @FXML
    void borrowBookButtonAction() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(MainController.class.getResource("borrowed-main-view.fxml"));
            Parent root1 = fxmlLoader.load();
            BorrowedController borrowedController = fxmlLoader.getController();
            borrowedController.setMainController(this);
            Stage stage = new Stage();
            stage.setTitle("Borrowed Books");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading FXML: " + e.getMessage());
        }
    }

    public void loadBooksFromDatabase() {
        String dbGramatasQuery = "SELECT \"Book ID\", \"Book name\", \"Author\", \"Genre\", \"Stock\" FROM Books";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(dbGramatasQuery);

            tableView.getItems().clear();

            while (queryOutput.next()) {
                int bookID = queryOutput.getInt("Book ID");
                String bookName = queryOutput.getString("Book name");
                String author = queryOutput.getString("Author");
                String genre = queryOutput.getString("Genre");
                int stock = queryOutput.getInt("Stock");

                Book book = new Book(bookID, bookName, author, genre, stock);
                tableView.getItems().add(book);
            }
            queryOutput.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error loading books from the database.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}