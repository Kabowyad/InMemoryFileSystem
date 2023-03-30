package org.example;

class File extends Entry {
    private StringBuilder content;

    public File(String name, Directory parent) {
        super(name, parent);
        this.content = new StringBuilder();
    }

    public String getContent() {
        return content.toString();
    }

    public void setContent(String content) {
        this.content = new StringBuilder(content);
    }

    public void appendContent(String newContent) {
        content.append(newContent);
    }
}
