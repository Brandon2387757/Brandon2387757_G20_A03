package com.brandon2387757_g20_a03;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.io.*;
import java.util.StringTokenizer;

public class BookHelper {

    public static void loadBooks(ObservableList<Book> bookList) {
        bookList.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader("library.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(line, "*");
                if (tokenizer.countTokens() >= 6) {
                    String type = tokenizer.nextToken().trim();
                    String title = tokenizer.nextToken().trim();
                    String author = tokenizer.nextToken().trim();
                    int year = Integer.parseInt(tokenizer.nextToken().trim());
                    int pageAndDuration = Integer.parseInt(tokenizer.nextToken().trim());
                    String extra = tokenizer.nextToken().trim();

                    Book book = new Book(title, author, year, type, pageAndDuration, extra);
                    bookList.add(book);
                }
            }
        } catch (IOException e) {
            showError("Error loading books from library.txt");
        } catch (NumberFormatException e) {
            showError("Invalid number format in library file.");
        }
    }

    public static void addBookToList(ObservableList<Book> bookList, TextField titleField, TextField authorField, TextField yearField, TextField formatField, TextField pageAndDurationField, TextField extraField) {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String yearText = yearField.getText().trim();
        String format = formatField.getText().trim();
        String pagesText = pageAndDurationField.getText().trim();
        String extra = extraField.getText().trim();

        if (title.isEmpty() || author.isEmpty() || yearText.isEmpty() || format.isEmpty() || pagesText.isEmpty()) {
            showError("Please fill in all required fields.");
            return;
        }

        if (!isValidType(format)) {
            showError("Invalid type. It must be either eBook, AudioBook, or Research Paper.");
            return;
        }

        if (!isValidExtra(format, extra)) {
            return;
        }

        try {
            int year = Integer.parseInt(yearText);
            int pagesAndDuration = Integer.parseInt(pagesText);

            Book newBook = new Book(title, author, year, format, pagesAndDuration, extra);
            bookList.add(newBook);
        } catch (NumberFormatException e) {
            showError("Year and Pages/Duration must be valid integers.");
        }
    }

    public static void updateBookInList(ObservableList<Book> bookList, Book selectedBook, String title, String author, Integer year, String format, Integer pageAndDuration, String extra) {
        if (selectedBook == null) {
            showError("No book selected for update.");
            return;
        }

        if (!isValidType(format)) {
            showError("Invalid type. It must be either eBook, AudioBook, or Research Paper.");
            return;
        }

        if (!isValidExtra(format, extra)) {
            return;
        }

        selectedBook.setTitle(title.trim());
        selectedBook.setAuthor(author.trim());
        selectedBook.setYear(year);
        selectedBook.setFormat(format.trim());
        selectedBook.setPagesAndDuration(pageAndDuration);
        selectedBook.setExtra(extra.trim());
    }

    public static void deleteBookFromList(ObservableList<Book> bookList, Book selectedBook) {
        if (selectedBook != null) {
            bookList.remove(selectedBook);
        } else {
            showError("No book selected for deletion.");
        }
    }

    public static void saveBooksToFile(ObservableList<Book> bookList, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Book book : bookList) {
                writer.write(book.getFormat() + "*" + book.getTitle() + "*" + book.getAuthor() + "*" + book.getYear() + "*" + book.getPagesAndDuration() + "*" + book.getExtra());
                writer.newLine();
            }
        } catch (IOException e) {
            showError("Failed to write to file: " + fileName);
        }
    }

    public static String getSummary(ObservableList<Book> bookList, boolean sortByYear) {
        StringBuilder report = new StringBuilder();

        if (sortByYear) {
            bubbleSortByYear(bookList);
        }

        int currentYear = -1;

        for (Book book : bookList) {
            int bookYear = book.getYear();

            if (sortByYear && bookYear != currentYear) {
                if (currentYear != -1) {
                    report.append("\n");
                }
                report.append("--- Year: ").append(bookYear).append(" ---\n");
                currentYear = bookYear;
            }

            String expiredStatus = isLicenseExpired(book) ? " [Expired]" : "";

            report.append("[").append(book.getFormat()).append("] ")
                    .append(book.getTitle()).append(" by ")
                    .append(book.getAuthor()).append(" (")
                    .append(book.getExtra()).append(", ")
                    .append(book.getYear()).append(", ")
                    .append(book.getPagesAndDuration()).append(" pages or minutes) - $")
                    .append(String.format("%.2f", getAccessCost(book)))
                    .append(expiredStatus)
                    .append("\n");
        }

        return report.toString();
    }

    public static double getAccessCost(Book book) {
        double cost = 0.0;
        switch (book.getFormat().toLowerCase()) {
            case "ebook":
                cost = book.getPagesAndDuration() * 0.02;
                break;
            case "audiobook":
                cost = book.getPagesAndDuration() * 0.05;
                break;
            case "research paper":
                cost = book.getPagesAndDuration() * 0.10;
                break;
            default:
                showError("Unknown format: " + book.getFormat());
                break;
        }
        return cost;
    }

    public static void bubbleSortByTitle(ObservableList<Book> bookList) {
        int n = bookList.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (bookList.get(j).getTitle().compareToIgnoreCase(bookList.get(j + 1).getTitle()) > 0) {
                    Book temp = bookList.get(j);
                    bookList.set(j, bookList.get(j + 1));
                    bookList.set(j + 1, temp);
                }
            }
        }
    }

    public static void bubbleSortByYear(ObservableList<Book> bookList) {
        int n = bookList.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (bookList.get(j).getYear() > bookList.get(j + 1).getYear()) {
                    Book temp = bookList.get(j);
                    bookList.set(j, bookList.get(j + 1));
                    bookList.set(j + 1, temp);
                }
            }
        }
    }

    private static boolean isValidType(String type) {
        return type.equalsIgnoreCase("eBook") || type.equalsIgnoreCase("AudioBook") || type.equalsIgnoreCase("Research Paper");
    }

    private static boolean isValidExtra(String type, String extra) {
        if (type.equalsIgnoreCase("eBook")) {
            if (extra.isEmpty()) {
                showError("Extra field must not be empty for eBook.");
                return false;
            }
        } else if (type.equalsIgnoreCase("AudioBook")) {
            String[] words = extra.split(" ");
            if (words.length != 2 || !isAlpha(words[0]) || !isAlpha(words[1])) {
                showError("AudioBook extra must contain exactly two alphabetical words.");
                return false;
            }
        } else if (type.equalsIgnoreCase("Research Paper")) {
            if (extra.isEmpty() || extra.matches("^[0-9]+$")) {
                showError("Research Paper extra must not be empty or digits only.");
                return false;
            }
        }
        return true;
    }

    private static boolean isAlpha(String str) {
        return str.matches("[a-zA-Z]+");
    }

    public static boolean isLicenseExpired(Book book) {
        int currentYear = book.getYear();
        String format = book.getFormat().toLowerCase();

        if (format.equals("ebook") && currentYear < 2013) {
            return true;
        } else if (format.equals("research paper") && currentYear < 2019) {
            return true;
        } else if (format.equals("audiobook") && currentYear < 2015) {
            return true;
        }

        return false;
    }

    public static void clearFields(TextField titleField, TextField authorField, TextField yearField, TextField formatField, TextField pagesAndDurationField, TextField extraField) {
        titleField.clear();
        authorField.clear();
        yearField.clear();
        formatField.clear();
        pagesAndDurationField.clear();
        extraField.clear();
    }

    public static void refreshTable(TableView<Book> tableView, ObservableList<Book> list) {
        tableView.setItems(FXCollections.observableArrayList(list));
    }

    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
