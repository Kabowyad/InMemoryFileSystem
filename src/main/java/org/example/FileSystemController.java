package org.example;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/filesystem")
public class FileSystemController {

    private final Directory root = new Directory("root", null);

    @Operation(summary = "Create a file")
    @PostMapping("/createFile")
    public String createFile(
            @Parameter(name = "name", description = "File name") @RequestParam String name,
            @Parameter(name = "parentName", description = "Parent directory") @RequestParam String parentName,
            @Parameter(name = "content", description = "File content", required = false) @RequestParam(required = false) String content) {

        Directory parent = (parentName.equals("root")) ? root : (Directory) root.find(parentName);

        if (parent != null) {
            createFileInFolder(name, parent, content);
            return "File created.";
        } else {
            return "Parent directory not found.";
        }
    }

    @Operation(summary = "Create a directory")
    @PostMapping("/createDirectory")
    public String createDirectory(
            @Parameter(name = "name", description = "Directory name") @RequestParam String name,
            @Parameter(name = "parentName", description = "Parent directory") @RequestParam String parentName)  {

        Directory parent = (parentName.equals("root")) ? root : (Directory) root.find(parentName);
        if (parent != null) {
            createDirectory(name, parent);
            return "Directory created.";
        }
        return "Parent directory not found.";
    }

    @Operation(summary = "Open a file")
    @GetMapping("/openFile")
    public ResponseEntity<String> openFile(
            @Parameter(name = "fileName", description = "File name") @RequestParam String fileName) {
        Entry entry = root.find(fileName);
        if (entry != null && entry instanceof File) {
            File file = (File) entry;
            return ResponseEntity.ok(file.getContent());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found.");
        }
    }

    @Operation(summary = "Edit the name of a file or directory")
    @PostMapping("/editName")
    public String editName(
            @Parameter(name = "oldName", description = "Old name") @RequestParam String oldName,
            @Parameter(name = "newName", description = "New name") @RequestParam String newName) {
        Entry entry = root.find(oldName);
        if (entry != null) {
            Directory parent = entry.getParent();
            if (parent.getEntry(newName) != null) {
                return "A file or folder with the new name already exists.";
            }
            entry.setName(newName);
            parent.removeEntry(oldName);
            parent.addEntry(entry);
            return "Name changed.";
        }
        return "File or folder not found.";
    }

    @Operation(summary = "Edit the content of a file")
    @PostMapping("/edit")
    public String editContent(
            @Parameter(name = "fileName", description = "File name") @RequestParam String fileName,
            @Parameter(name = "newContent", description = "New content") @RequestParam String newContent) {
        Entry entry = root.find(fileName);
        if (entry != null && entry instanceof File) {
            File file = (File) entry;
            file.setContent(newContent);
            return "File edited.";
        }
        return "Failed to edit file.";
    }

    @Operation(summary = "Find files by content")
    @GetMapping("/find")
    public String findEntryByContent(
            @Parameter(name = "content", description = "File content") @RequestParam String content) {
        List<File> files = root.findFilesByContent(content);
        if (!files.isEmpty()) {
            StringBuilder result = new StringBuilder("Files found containing the specified content:");
            for (File file : files) {
                result.append("\n").append(file.getName());
            }
            return result.toString();
        }
        return "No files found with the specified content.";
    }

    @Operation(summary = "Move a file or directory")
    @PostMapping("/move")
    public String move(
            @Parameter(name = "name", description = "Entry name") @RequestParam String name,
            @Parameter(name = "newParentName", description = "New parent directory") @RequestParam String newParentName) {
        Entry entry = root.find(name);
        Directory newParent = (Directory) root.find(newParentName);

        if (entry != null && newParent != null && !name.equals(newParentName)) {
            Directory oldParent = entry.getParent();
            oldParent.removeEntry(entry.getName());
            entry.setParent(newParent);
            newParent.addEntry(entry);
            return "Entry moved.";
        }
        return "Failed to move entry.";
    }

    @Operation(summary = "Delete a file")
    @PostMapping("/deleteFile")
    public String deleteFile(
            @Parameter(name = "fileName", description = "File name") @RequestParam String fileName) {
        Entry entry = root.find(fileName);
        if (entry != null && entry instanceof File) {
            Directory parent = entry.getParent();
            parent.removeEntry(entry.getName());
            return "File deleted.";
        }
        return "File not found.";
    }

    private Directory createDirectory(String name, Directory parent) {
        if (parent.getEntry(name) != null) {
            throw new IllegalArgumentException("A file or directory with the given name already exists.");
        }
        Directory directory = new Directory(name, parent);
        parent.addEntry(directory);
        return directory;
    }

    private File createFileInFolder(String name, Directory parent, String content) {

        if (parent.getEntry(name) != null) {
            throw new IllegalArgumentException(
                    "A file or directory with the given name already exists.");
        }
        File file = new File(name, parent);
        if (content != null) {
            file.appendContent(content);
        }
        parent.addEntry(file);
        return file;
    }

}
