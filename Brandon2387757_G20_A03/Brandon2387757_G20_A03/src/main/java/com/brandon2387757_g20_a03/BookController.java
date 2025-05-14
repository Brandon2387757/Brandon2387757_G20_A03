package com.brandon2387757_g20_a03;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;

import static com.brandon2387757_g20_a03.BookHelper.*;

public class BookController {

    @FXML
    private RadioButton sortByYear;
    @FXML
    private RadioButton sortByTitle;
    @FXML
    private RadioButton searchByTitle;
    @FXML
    private RadioButton searchByYear;
    @FXML
    private RadioButton searchByFormat;
    @FXML
    private ToggleGroup sortToggleGroup = new ToggleGroup();
    @FXML
    private ToggleGroup searchToggleGroup = new ToggleGroup();
    @FXML
    private TextField searchField;
    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField yearField;
    @FXML
    private TextField formatField;
    @FXML
    private TextField extraField;
    @FXML
    private TextField pagesAndDurationField;
    @FXML
    private TableView<Book> tableView;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, Integer> yearColumn;
    @FXML
    private TableColumn<Book, String> formatInfoColumn;
    @FXML
    private TextArea reportArea;

    private ObservableList<Book> bookList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        authorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthor()));
        yearColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getYear()).asObject());
        formatInfoColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFormat()));
        refreshTable(tableView, bookList);

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                titleField.setText(newValue.getTitle());
                authorField.setText(newValue.getAuthor());
                yearField.setText(String.valueOf(newValue.getYear()));
                formatField.setText(newValue.getFormat());
                pagesAndDurationField.setText(String.valueOf(newValue.getPagesAndDuration()));
                extraField.setText(newValue.getExtra());
            }
        });

        searchByTitle.setToggleGroup(searchToggleGroup);
        searchByYear.setToggleGroup(searchToggleGroup);
        searchByFormat.setToggleGroup(searchToggleGroup);
        sortByTitle.setToggleGroup(sortToggleGroup);
        sortByYear.setToggleGroup(sortToggleGroup);
    }

    @FXML
    public void loadItems(ActionEvent e) {
        loadBooks(bookList);
        refreshTable(tableView, bookList);
    }

    @FXML
    public void addItem(ActionEvent e) {
        addBookToList(bookList,
                titleField, authorField, yearField, formatField, pagesAndDurationField, extraField);
        refreshTable(tableView, bookList);
        clearFields(titleField, authorField, yearField, formatField, pagesAndDurationField, extraField);
    }

    @FXML
    public void updateItem(ActionEvent e) {
        Book selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            updateBookInList(bookList, selected,
                    titleField.getText(), authorField.getText(), Integer.valueOf(yearField.getText()), formatField.getText(), Integer.valueOf(pagesAndDurationField.getText()), extraField.getText());
        } else {
            showError("Please select a book to update.");
        }
        clearFields(titleField, authorField, yearField, formatField, pagesAndDurationField, extraField);
        refreshTable(tableView, bookList);
    }

    @FXML
    public void deleteItem(ActionEvent e) {
        Book selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            deleteBookFromList(bookList, selected);
            refreshTable(tableView, bookList);
        } else {
            showError("Please select a book to delete.");
        }
    }

    @FXML
    public void bubbleSort(ActionEvent e) {
        if (sortByTitle.isSelected()) bubbleSortByTitle(bookList);
        else if (sortByYear.isSelected()) bubbleSortByYear(bookList);
        refreshTable(tableView, bookList);
    }

    @FXML
    public void binarySearch(ActionEvent e) {
        String term = searchField.getText().trim().toLowerCase();
        ObservableList<Book> filteredList = FXCollections.observableArrayList();

        if (term.isEmpty()) {
            tableView.setItems(bookList);
            return;
        }

        for (Book book : bookList) {
            if (searchByTitle.isSelected() && book.getTitle().toLowerCase().contains(term)) {
                filteredList.add(book);
            } else if (searchByYear.isSelected()) {
                try {
                    if (book.getYear().toString().contains(term)) {
                        filteredList.add(book);
                    }
                } catch (NumberFormatException ex) {
                    showError("Invalid year input.");
                    return;
                }
            } else if (searchByFormat.isSelected() && book.getFormat().toLowerCase().contains(term)) {
                filteredList.add(book);
            }
        }

        tableView.setItems(filteredList);
    }

    @FXML
    public void saveToUnsorted(ActionEvent e) {
        saveBooksToFile(bookList, "library.txt");
    }

    @FXML
    public void saveToSorted(ActionEvent e) {
        saveBooksToFile(bookList, "sortedLibrary.txt");
    }

    @FXML
    public void generateReport(ActionEvent e) {
        boolean sortByYearSelected = sortByYear.isSelected();
        String report = getSummary(bookList, sortByYearSelected);
        reportArea.setText(report);
    }

    static void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setContentText(msg);
        a.showAndWait();
    }


    @FXML
    public void exitApp() {
        System.exit(0);
    }

    @FXML
    public void searchByTitleMenu() {
        searchByTitle.setSelected(true);
    }

    @FXML
    public void searchByYearMenu() {
        searchByYear.setSelected(true);
    }

    @FXML
    public void searchByFormatMenu() {
        searchByFormat.setSelected(true);
    }

    @FXML
    public void generateReportByYear() {
        bubbleSortByYear(bookList);
        String report = getSummary(bookList, true);
        reportArea.setText(report);
    }
}
