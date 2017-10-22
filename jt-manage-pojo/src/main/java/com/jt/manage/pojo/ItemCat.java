package com.jt.manage.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jt.common.po.BasePojo;

@JsonIgnoreProperties(ignoreUnknown=true)
@Table(name="tb_item_cat")
public class ItemCat extends BasePojo{
	
	@Id		//表的主键
	@GeneratedValue(strategy=GenerationType.IDENTITY) //表示主键自增
	private Long id;	//商品分类的编号
	private Long parentId;  //商品上级编号
	private String name;	//商品名称
	private Integer status;	//1正常   2删除
	private Integer sortOrder; //商品类目排序号
	private Boolean isParent;  //表示是否为父级 true  1  不是父级false 0
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
	public Boolean getIsParent() {
		return isParent;
	}
	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}
	
	//为了实现分类列表展现 添加getXXX {"id":2,"text":"商品名",state:"closed"}
	
	public String getText(){
		return name;
	}
	
	//是父节点：closed   不是父节点：open
	public String getState(){
		
		return isParent ? "closed" : "open";
	}
	
	  
}
