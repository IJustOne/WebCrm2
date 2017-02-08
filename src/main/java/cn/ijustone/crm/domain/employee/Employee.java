package cn.ijustone.crm.domain.employee;

import java.util.Arrays;
import java.util.Date;

/**
 * 
 * @author iJustONE
 *2017-1-4
 */
public class Employee {
	
	/**主键id，bigint类型*/
	private Long id;
	/**用户名*/
	private String username;
	/**姓名*/
	private String realName;
	/**密码*/
	private String password;
	/**电话*/
	private String tel;
	/**邮箱*/
	private String email;
	/** 用户账号 **/
	private String account;
	/** 出生日期 **/
	private Date birthday;
	/** 性别 **/
	private String sex;
	/** 照片 **/
	private String photo;
	/** 备注 **/
	private String remark;
	/**创建时间*/
	private Date createDate;
	/**创建人*/
	private String createBy;
	/**修改时间*/
	private Date modefiedDate;
	/**修改人*/
	private String modefiedBy;
	/**状态:0-禁用，1-启用 **/
	private String state;
	
	/** 账号类型:0-管理员，1-普通账号 **/
	private Long accountType;
	/** 职务Str**/
	private String positionStr;
	/** 职称Str**/
	private String jobTitleStr;
	
	/**所属部门,一对多*/
	private Long[] departments;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public Date getModefiedDate() {
		return modefiedDate;
	}
	public void setModefiedDate(Date modefiedDate) {
		this.modefiedDate = modefiedDate;
	}
	public String getModefiedBy() {
		return modefiedBy;
	}
	public void setModefiedBy(String modefiedBy) {
		this.modefiedBy = modefiedBy;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Long[] getDepartments() {
		return departments;
	}
	public void setDepartments(Long[] departments) {
		this.departments = departments;
	}
	public Long getAccountType() {
		return accountType;
	}
	public void setAccountType(Long accountType) {
		this.accountType = accountType;
	}
	public String getPositionStr() {
		return positionStr;
	}
	public void setPositionStr(String positionStr) {
		this.positionStr = positionStr;
	}
	public String getJobTitleStr() {
		return jobTitleStr;
	}
	public void setJobTitleStr(String jobTitleStr) {
		this.jobTitleStr = jobTitleStr;
	}
}
