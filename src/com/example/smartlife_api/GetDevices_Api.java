package com.example.smartlife_api;

import java.util.List;

public class GetDevices_Api {
	private String status;
	private List<GetDevices_Api_Result> result;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<GetDevices_Api_Result> getResult() {
		return result;
	}
	public void setResult(List<GetDevices_Api_Result> result) {
		this.result = result;
	}

	
}
