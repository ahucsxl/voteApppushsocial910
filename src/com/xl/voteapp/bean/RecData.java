package com.xl.voteapp.bean;

import java.util.ArrayList;
import java.util.List;

public class RecData extends Entity {

	private String StateFull;
	private List<Vote> listvote;

	

	public RecData(String stateFull, List<Vote> listvote) {
		super();
		StateFull = stateFull;
		this.listvote = listvote;
	}



	public RecData() {
		// TODO Auto-generated constructor stub
	}



	public String getStateFull() {
		return StateFull;
	}



	public void setStateFull(String stateFull) {
		StateFull = stateFull;
	}



	public List<Vote> getListvote() {
		return listvote;
	}



	public void setListvote(List<Vote> listvote) {
		this.listvote = listvote;
	}



	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Vote [StateFull=" + StateFull + ", listvote=" + listvote +  "]";
	}

}
