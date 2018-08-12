package com.example.smartlife_api;

import java.util.List;

public class GetModelAPI {

	private String status;
	private String MODEL;
	private List<GetModelResultAPI> data;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<GetModelResultAPI> getData() {
		return data;
	}
	public void setData(List<GetModelResultAPI> data) {
		this.data = data;
	}
	public String getMODEL() {
		return MODEL;
	}
	public void setMODEL(String mODEL) {
		MODEL = mODEL;
	}
	
}
