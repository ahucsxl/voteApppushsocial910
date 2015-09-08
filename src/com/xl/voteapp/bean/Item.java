package com.xl.voteapp.bean;

public class Item extends Entity {

	private static final long serialVersionUID = 1L;
	private int i_id;	
	private String i_content;	
	private int v_id;	
	private int u_no;	
	private int i_num;	
	private String v_title;
	
	public Item(int i_id, String i_content, int v_id, int u_no, int i_num,String v_title) {
		super();
		this.i_id = i_id;
		this.i_content = i_content;
		this.v_id = v_id;
		this.u_no = u_no;
		this.i_num = i_num;
		this.v_title=v_title;
	}

	public Item() {
		// TODO Auto-generated constructor stub
	}

	public int getI_id() {
		return i_id;
	}

	public void setI_id(int i_id) {
		this.i_id = i_id;
	}

	public String getI_content() {
		return i_content;
	}

	public void setI_content(String i_content) {
		this.i_content = i_content;
	}

	public int getV_id() {
		return v_id;
	}

	public void setV_id(int v_id) {
		this.v_id = v_id;
	}

	public int getU_no() {
		return u_no;
	}

	public void setU_no(int u_no) {
		this.u_no = u_no;
	}

	public int getI_num() {
		return i_num;
	}

	public void setI_num(int i_num) {
		this.i_num = i_num;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getV_title() {
		return v_title;
	}

	public void setV_title(String v_title) {
		this.v_title = v_title;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Item [i_id=" + i_id + ", I_content=" + i_content + ", u_no=" + u_no
			+ ", i_num=" +i_num + ", v_id=" + v_id +  ", v_title=" + v_title+"]";
	}
}
