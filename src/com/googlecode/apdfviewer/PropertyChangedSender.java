package com.googlecode.apdfviewer;

public class PropertyChangedSender extends PropertyChangedSenderBase implements OnPropertyChangedListener{

	@Override
	public void OnPropertyChanged(Object source) {
		OnPropertyChanged();
	}

	public void OnPropertyChanged(){
		super.OnPropertyChanged();
	}
}
