package com.example.smartlife_api;

import java.util.List;

public class GetModelResultAPI {
	private String Mid;
	private String Mname;
	private String ModelTotal;
	private List<GetModelResultDataAPI> Mdata;
	public String getMid() {
		return Mid;
	}
	public void setMid(String mid) {
		Mid = mid;
	}
	public String getMname() {
		return Mname;
	}
	public void setMname(String mname) {
		Mname = mname;
	}
	public List<GetModelResultDataAPI> getMdata() {
		return Mdata;
	}
	public void setMdata(List<GetModelResultDataAPI> mdata) {
		Mdata = mdata;
	}
	public String getModelTotal() {
		return ModelTotal;
	}
	public void setModelTotal(String modelTotal) {
		ModelTotal = modelTotal;
	}
}
