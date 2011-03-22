package com.googlecode.apdfviewer;


/* 
 * Copyright 2010 nookDevs
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
//Code from apv project in google projects and modified for nook.

import java.io.File;
import java.io.FileDescriptor;
import java.util.List;

import com.googlecode.apdfviewer.PDF;


/**
 * Native PDF - interface to native code.
 */
public class PDF {
    static {
        System.loadLibrary("pdfview2");
    }
    
    /**
     * Simple size class used in JNI to simplify parameter passing. This
     * shouldn't be used anywhere outide of pdf-related code.
     */
    public static class Size implements Cloneable {
        public int width;
        public int height;
        
        public Size() {
            width = 0;
            height = 0;
        }
        
        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }
        
        @Override
        public Size clone() {
            return new Size(width, height);
        }
    }
    
    /**
     * Holds pointer to native pdf_t struct.
     */
    @SuppressWarnings("unused")
    private int pdf_ptr = 0;
    
    /**
     * Parse bytes as PDF file and store resulting pdf_t struct in pdf_ptr.
     * 
     * @return error code
     */
    synchronized private native int parseBytes(byte[] bytes);
    
    /**
     * Parse PDF file.
     * 
     * @param fileName
     *            pdf file name
     * @return error code
     */
    synchronized private native int parseFile(String fileName);
    
    /**
     * Parse PDF file.
     * 
     * @param fd
     *            opened file descriptor
     * @return error code
     */
    synchronized private native int parseFileDescriptor(FileDescriptor fd);
    
    /**
     * Construct PDF structures from bytes stored in memory.
     */
    public PDF(byte[] bytes) {
        parseBytes(bytes);
    }
    
    /**
     * Construct PDF structures from file sitting on local filesystem.
     */
    public PDF(File file) {
        parseFile(file.getAbsolutePath());
    }
    
    /**
     * Construct PDF structures from opened file descriptor.
     * 
     * @param file
     *            opened file descriptor
     */
    public PDF(FileDescriptor file) {
        parseFileDescriptor(file);
    }
    
    /**
     * Return page count from pdf_t struct.
     */
    synchronized public native int getPageCount();
    
    /**
     * Render a page.
     * 
     * @param n
     *            page number, starting from 0
     * @param zoom
     *            page size scalling
     * @param left
     *            left edge
     * @param right
     *            right edge
     * @param passes
     *            requested size, used for size of resulting bitmap
     * @return bytes of bitmap in Androids format
     */
    synchronized public native int[] renderPage(int n, int zoom, int left, int top, int rotation, PDF.Size rect);
    
    /**
     * Get PDF page size, store it in size struct, return error code.
     * 
     * @param n
     *            0-based page number
     * @param size
     *            size struct that holds result
     * @return error code
     */
    synchronized public native int getPageSize(int n, PDF.Size size);
    
    /**
     * Find text on given page, return list of find results.
     */
    synchronized public native List<FindResult> find(String text, int page);
    
    /**
     * Clear search.
     */
    synchronized public native void clearFindResult();
    
    /**
     * Find text on page, return find results.
     */
    synchronized public native List<FindResult> findOnPage(int page, String text);
    
    /**
     * Free memory allocated in native code.
     */
    synchronized public native void freeMemory();
    
}
