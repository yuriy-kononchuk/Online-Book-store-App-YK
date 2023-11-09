package com.example.project.dto;

public record BookSearchParameters(String[] titles, String[] authors, String[] isbnNumbers) {
}
