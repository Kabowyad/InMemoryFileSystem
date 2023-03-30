package org.example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Directory extends Entry {
    private Map<String, Entry> entries;

    public Directory(String name, Directory parent) {
        super(name, parent);
        entries = new HashMap<>();
    }

    public boolean addEntry(Entry entry) {
        if (entries.containsKey(entry.name)) {
            return false;
        }
        entries.put(entry.name, entry);
        return true;
    }

    public Entry getEntry(String name) {
        return entries.get(name);
    }

    public boolean removeEntry(String name) {
        if (!entries.containsKey(name)) {
            return false;
        }
        entries.remove(name);
        return true;
    }

    public List<File> findFilesByContent(String content) {
        List<File> filesFound = new ArrayList<>();
        for (Entry entry : entries.values()) {
            if (entry instanceof File) {
                File file = (File) entry;
                if (file.getContent().contains(content)) {
                    filesFound.add(file);
                }
            } else if (entry instanceof Directory) {
                Directory directory = (Directory) entry;
                filesFound.addAll(directory.findFilesByContent(content));
            }
        }
        return filesFound;
    }

    public Collection<Entry> getEntries() {
        return entries.values();
    }

    // Recursive find method, as described in previous responses
    public Entry find(String name) {
        if (entries.containsKey(name)) {
            return entries.get(name);
        }

        for (Entry entry : entries.values()) {
            if (entry instanceof Directory) {
                Entry found = ((Directory) entry).find(name);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }
}
