package com.pdmanager.views.call;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pdmanager.R;
import com.pdmanager.views.BasePDFragment;


public class WaitingFragment extends BasePDFragment
{
	private Runnable operationOnResume = null ;
	private String processName = null ;

	public WaitingFragment(){}
	
	public static final WaitingFragment newInstance(String processName)
	{
		WaitingFragment instance = new WaitingFragment();
	    instance.setProcessName(processName);
	    return instance;
	}
	
	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public void onResume(){
		super.onResume();
		//TODO: CHeck that
		//app().onProcessingStarted();
	}

	public void onPause(){
		super.onPause();
		
		this.processName = "";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.waiting_fragment_layout, container, false);
		TextView label = (TextView)view.findViewById(R.id.error_label) ;
		label.setText(processName);
		return view ;
	}

	public Runnable getOperationOnResume() {
		return operationOnResume;
	}

	public void setOperationOnResume(Runnable operationOnResume) {
		this.operationOnResume = operationOnResume;
	}
}
