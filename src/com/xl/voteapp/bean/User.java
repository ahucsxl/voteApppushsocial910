package com.xl.voteapp.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 登录用户实体类
 * 
 */
@SuppressWarnings("serial")
@XStreamAlias("user")
public class User extends Entity {


	public User() {

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int u_no;
	private String u_pwd;
	private String u_name;
	private String u_email;
	private String u_tel;
	private String u_thirduid;
	private String _portrait;



	public User(int u_no, String u_pwd, String u_name, String u_email, String u_tel, String u_thirduid,
			String _portrait) {
		super();
		this.u_no = u_no;
		this.u_pwd = u_pwd;
		this.u_name = u_name;
		this.u_email = u_email;
		this.u_tel = u_tel;
		this.u_thirduid = u_thirduid;
		this._portrait = _portrait;
	}

	public String get_portrait() {
		return _portrait;
	}

	public void set_portrait(String _portrait) {
		this._portrait = _portrait;
	}

	public String getU_thirduid() {
		return u_thirduid;
	}

	public void setU_thirduid(String u_thirduid) {
		this.u_thirduid = u_thirduid;
	}

	@Override
	public String toString() {
		return "User [u_no="+u_no+"u_name=" + u_name + ", u_pwd=" + u_pwd + ", u_email="
				+ u_email + ", u_tel=" + u_tel + ", _portrait=" + _portrait +"]";
	}

	public int getU_no() {
		return u_no;
	}

	public void setU_no(int u_no) {
		this.u_no = u_no;
	}

	public String getU_pwd() {
		return u_pwd;
	}

	public void setU_pwd(String u_pwd) {
		this.u_pwd = u_pwd;
	}

	public String getU_name() {
		return u_name;
	}

	public void setU_name(String u_name) {
		this.u_name = u_name;
	}

	public String getU_email() {
		return u_email;
	}

	public void setU_email(String u_email) {
		this.u_email = u_email;
	}

	public String getU_tel() {
		return u_tel;
	}

	public void setU_tel(String u_tel) {
		this.u_tel = u_tel;
	}

}
