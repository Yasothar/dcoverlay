/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlay.dcapp;


/**
 *
 * @author user
 */
public class CommentManager {

    private LocalFile file;
    private Comment comment;

    public LocalFile getFile() {
        return file;
    }

    public void setFile(LocalFile file) {
        this.file = file;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}