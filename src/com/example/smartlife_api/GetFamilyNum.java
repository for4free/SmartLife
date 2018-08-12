package com.example.smartlife_api;

import java.util.List;

public class GetFamilyNum {

	private String status;
	private String Fid;
	private String Fname;
	private String CreatorId;
	private List<GetFamilyNum_Res> result;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFid() {
		return Fid;
	}
	public void setFid(String fid) {
		Fid = fid;
	}
	public String getFname() {
		return Fname;
	}
	public void setFname(String fname) {
		Fname = fname;
	}
	public List<GetFamilyNum_Res> getResult() {
		return result;
	}
	public void setResult(List<GetFamilyNum_Res> result) {
		this.result = result;
	}
	public String getCreatorId() {
		return CreatorId;
	}
	public void setCreatorId(String creatorId) {
		CreatorId = creatorId;
	}
	
	
}
