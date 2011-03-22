package com.googlecode.apdfviewer;

import java.io.Serializable;

import android.graphics.Rect;
import android.graphics.RectF;

public class Point extends PropertyChangedSenderBase implements Serializable{
	private float x;
	private float y;
	
	public Point(){};
	public Point(float x, float y){
		this.x=x;
		this.y=y;
	}
	
	public Point(Point p){
		this(p.x,p.y);
	}
	
	public final boolean equals(float x, float y){
		return this.x==x && this.y==y;
	}

	public final float length(){
		return Point.length(x, y);
	}
	
	static float length(float x, float y){
		return (float) Math.sqrt(x*x+y+y);
	}
	
	public void negate(){
		x=-x;
		y=-y;
		OnPropertyChanged();
	}
	
	public final void offset(float x, float y){
		this.x+=x;
		this.y+=y;
		OnPropertyChanged();
	}
	
	public final void set (float x, float y){
		this.x=x;
		this.y=y;
		OnPropertyChanged();
	}
	
	public final void set (Point p){
		this.x=p.x;
		this.y=p.y;
		OnPropertyChanged();
	}
	
	
	public void setX(float x) {
		this.x = x;
		OnPropertyChanged();
	}
	
	public float getX() {
		return x;
	}
	
	public void setY(float y) {
		this.y = y;
		OnPropertyChanged();
	}
	public float getY() {
		return y;
	}
	
	public static RectF CreateRectF(Point p, Size s){
		return new RectF(
				p.getX(),p.getY(),
				p.getX()+s.getWidth(),
				p.getY()+s.getHeight());
	}
	
	public static Rect CreateRect(Point p, Size s){
		return new Rect(
				(int)p.getX(),(int)p.getY(),
				(int)(p.getX()+s.getWidth()),
				(int)(p.getY()+s.getHeight()));
	}
	
	public void add(Point o){
		x+=o.x;
		y+=o.y;
		OnPropertyChanged();
	}
	
	public void sub(Point o){
		x-=o.x;
		y-=o.y;
		OnPropertyChanged();
	}
	
	public void mul(float f){
		x*=f;
		y*=f;
		OnPropertyChanged();
	}
	
	@Override
	public String toString() {
		return "("+x+","+y+")";
	}
}
