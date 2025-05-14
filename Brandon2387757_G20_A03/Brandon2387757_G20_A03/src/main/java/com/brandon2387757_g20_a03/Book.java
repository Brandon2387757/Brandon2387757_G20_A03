package com.brandon2387757_g20_a03;

public class Book {
    private String title;
    private String author;
    private Integer year;
    private String format;
    private Integer pagesAndDuration;
    private String extra;

    public Book() {
    }

    public Book(String title, String author, Integer year, String format, Integer pagesAndDuration, String extra) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.format = format;
        this.pagesAndDuration = pagesAndDuration;
        this.extra = extra;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getPagesAndDuration() {
        return pagesAndDuration;
    }

    public void setPagesAndDuration(Integer pagesAndDuration) {
        this.pagesAndDuration = pagesAndDuration;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
