package com.googlecode.apdfviewer;

public class Size extends PropertyChangedSenderBase implements Cloneable{
    private float width;
    private float height;
    
    public Size() {
        width = 0;
        height = 0;
    }
    
    public Size(float width, float height) {
        this.width = width;
        this.height = height;
    }
    
    public Size(Size size) {
		this(size.width,size.height);
	}

	@Override
    public Size clone() {
        return new Size(width, height);
    }

    public void setWidth(float width) {
		this.width = width;
		OnPropertyChanged();
	}

	public float getWidth() {
		return width;
	}

	public void setHeight(float height) {
		this.height = height;
		OnPropertyChanged();
	}

	public float getHeight() {
		return height;
	}

	public void set(Size size) {
		this.width=size.width;
		this.height=size.height;
		OnPropertyChanged();
	}

	public void mul(float f) {
		width*=f;
		height*=f;
		OnPropertyChanged();
	}

	public boolean anyZero() {
		if (getWidth()==0) return true;
		if (getHeight()==0) return true;
		return false;
	}
	
	@Override
	public String toString() {
		return "<"+width+","+height+">";
	}
}