package com.example.editpart.common;

import com.example.editpart.R;
import android.content.Context;

public class Common {

    public String brush;
    public String eraser;
    public String colors;
    public String background;
    public String _return;

    public String delete;
    public String image;
    public String text;

    public Common(String brush, String eraser, String colors, String background, String _return, String delete, String image, String text) {
        this.brush = brush;
        this.eraser = eraser;
        this.colors = colors;
        this.background = background;
        this._return = _return;
        this.delete = delete;
        this.image = image;
        this.text = text;
    }

    public String getBrush() {
        return brush;
    }

    public void setBrush(String brush) {
        this.brush = brush;
    }

    public String getEraser() {
        return eraser;
    }

    public void setEraser(String eraser) {
        this.eraser = eraser;
    }

    public String getColors() {
        return colors;
    }

    public void setColors(String colors) {
        this.colors = colors;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String get_return() {
        return _return;
    }

    public void set_return(String _return) {
        this._return = _return;
    }

    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
