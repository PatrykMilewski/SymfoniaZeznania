package com.parser;

import java.io.File;

public class XmlFileOnList {
    
    private String text;
    private File file;
    private boolean isTransformed = false;
    
    XmlFileOnList(File file) {
        this.file = file;
    }
    
    public void setIsTransformed(boolean newValue) {
        isTransformed = newValue;
    }

    @Override
    public String toString() {
        return isTransformed ? "[T] " + file.getName() : "[N] " + file.getName();
    }
    
    public File getFile() {
        return file;
    }
}
