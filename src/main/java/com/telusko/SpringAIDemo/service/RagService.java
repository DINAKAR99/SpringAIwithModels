// package com.telusko.SpringAIDemo.service;

// import org.springframework.stereotype.Service;

// import java.io.IOException;
// import java.nio.file.*;
// import java.util.*;
// import java.util.stream.Collectors;

// @Service
// public class RagService {

// private final List<String> documents = new ArrayList<>();

// // Load all .txt files from folder
// public void loadDocsFromFolder(String folderPath) {
// try {
// Files.list(Paths.get(folderPath))
// .filter(f -> f.toString().endsWith(".txt"))
// .forEach(f -> {
// try {
// documents.add(Files.readString(f));
// } catch (IOException e) {
// e.printStackTrace();
// }
// });
// } catch (IOException e) {
// e.printStackTrace();
// }
// }

// public List<String> getAllDocs() {
// return documents;
// }
// }
