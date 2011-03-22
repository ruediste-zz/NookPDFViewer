package com.googlecode.apdfviewer;

import java.io.Serializable;
import java.util.LinkedList;

public class PropertyChangedSenderBase implements Serializable{
	transient private LinkedList<OnPropertyChangedListener> listeners=new LinkedList<OnPropertyChangedListener>();
	
	transient int holdingDepth=0;
	
	public void AddListener(OnPropertyChangedListener listener){
		listeners.add(listener);
	}
	
	public boolean RemoveListener(OnPropertyChangedListener listener){
		return listeners.remove(listener);
	}
	
	protected void OnPropertyChanged(){
		if (holdingDepth>0) return;
		
		for (OnPropertyChangedListener listener: listeners){
			listener.OnPropertyChanged(this);
		}
	}
	
	public void HoldPropertyChangedEvents(){
		holdingDepth++;
	}
	
	public void ReleasePropertyChangedEvents(){
		if (holdingDepth==0) return;
		holdingDepth--;
		if (holdingDepth==0) OnPropertyChanged();
	}
}
