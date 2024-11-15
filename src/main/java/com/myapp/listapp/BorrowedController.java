package com.myapp.listapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class BorrowedController implements Initializable {

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    TableView<Book> tableView;

    public void setTableView(TableView<Book> tableView) {
        this.tableView = tableView;
    }

    DatabaseConnection dbConnection = new DatabaseConnection();
    Connection connectDB = dbConnection.getDBConnection();

    @FXML
    private TableView<Borrower> borrowerTableView;

    public void initialize(URL location, ResourceBundle resources) {

        TableColumn<Borrower, Integer> bookIDCol = new TableColumn<>("Book ID");
        bookIDCol.setCellValueFactory(new PropertyValueFactory<>("bookID"));

        TableColumn<Borrower, String> borrowedBookNameCol = new TableColumn<>("Book name");
        borrowedBookNameCol.setCellValueFactory(new PropertyValueFactory<>("borrowedBookName"));

        TableColumn<Borrower, String> borrowedBookAuthorCol = new TableColumn<>("Author");
        borrowedBookAuthorCol.setCellValueFactory(new PropertyValueFactory<>("borrowedBookAuthor"));

        TableColumn<Borrower, String> borrowerFullNameCol = new TableColumn<>("Borrower full name");
        borrowerFullNameCol.setCellValueFactory(new PropertyValueFactory<>("borrowerFullName"));

        TableColumn<Borrower, String> borrowDateCol = new TableColumn<>("Borrow Date");
        borrowDateCol.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));

        TableColumn<Borrower, String> returnDateCol = new TableColumn<>("Return Date");
        returnDateCol.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

        TableColumn<Borrower, Boolean> returnedCol = new TableColumn<>("Returned");
        returnedCol.setCellValueFactory(new PropertyValueFactory<>("isReturned"));

        configureReturnedColumn(returnedCol);

        borrowerTableView.getColumns().setAll(bookIDCol, borrowedBookNameCol, borrowedBookAuthorCol, borrowerFullNameCol, borrowDateCol, returnDateCol, returnedCol);

        String dbBorrowedQuery = "SELECT \"Book ID\", \"Book name\", \"Book author\", \"Borrower full name\", \"Borrow Date\", \"Return Date\", \"Returned\" FROM Borrows";


        try {
            Statement statement = connectDB.createStatement();
            ResultSet resultSet = statement.executeQuery(dbBorrowedQuery);

            while(resultSet.next()) {
                int bookID = resultSet.getInt("Book ID");
                String borrowedBookName = resultSet.getString("Book name");
                String borrowedBookAuthor = resultSet.getString("Book author");
                String borrowerFullName = resultSet.getString("Borrower full name");
                String borrowDate = resultSet.getString("Borrow Date");
                String returnDate = resultSet.getString("Return Date");
                boolean isReturned = resultSet.getBoolean("Returned");

                Borrower borrower = new Borrower(bookID, borrowedBookName,borrowedBookAuthor, borrowerFullName, borrowDate, returnDate, isReturned);

                borrowerTableView.getItems().add(borrower);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configureReturnedColumn(TableColumn<Borrower, Boolean> returnedCol) {
        returnedCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean isReturned, boolean empty) {
                super.updateItem(isReturned, empty);
                if (empty || isReturned == null) {
                    setText(null);
                } else {
                    Borrower borrower = getTableView().getItems().get(getIndex());
                    if (!isReturned && isOverdue(borrower.getReturnDate())) {
                        setText("OVERDUE");
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else if (isReturned) {
                        setText("True");
                    } else {
                        setText("False");
                    }
                }
            }
        });
    }

    private boolean isOverdue(String returnDate) {
        LocalDate dueDate = LocalDate.parse(returnDate);
        return LocalDate.now().isAfter(dueDate);
    }

    public void onNewBorrowButtonAction() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(MainController.class.getResource("addnewborrow-view.fxml"));
            Parent root1 = fxmlLoader.load();
            NewBorrowController newBorrowController = fxmlLoader.getController();
            newBorrowController.setMainController(this.mainController);
            newBorrowController.setBorrowerTableView(borrowerTableView);
            newBorrowController.setTableView(tableView);

            Stage stage = new Stage();
            stage.setTitle("Add Borrow");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading FXML: " + e.getMessage());
        }
    }

    @FXML
    public void onReturnBorrowButtonAction() {
        Borrower selectedBorrower = borrowerTableView.getSelectionModel().getSelectedItem();

        if (selectedBorrower == null) {
            System.out.println("No Selection, please select a borrower to return.");
            return;
        }

        if (selectedBorrower.getIsReturned()) {
            showAlert("Already Returned", "This book has already been returned.");
            return;
        }

        selectedBorrower.setIsReturned(true);
        borrowerTableView.refresh();

        updateBorrowReturnStatusInDatabase(selectedBorrower.getBookID());

        showAlert("Success", "The book has been successfully returned.");
    }

    private void updateBorrowReturnStatusInDatabase(int bookID) {
        String query = "UPDATE Borrows SET Returned = 1 WHERE \"Book ID\" = ?";

        try (PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {
            preparedStatement.setInt(1, bookID);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
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
