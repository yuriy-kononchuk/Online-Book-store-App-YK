package com.example.project.dto.book;

public record BookSearchParameters(String[] titles, String[] authors, String[] isbnNumbers) {
}
