package com.jt.manage.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.jt.common.po.BasePojo;
//采用JPA的形式操作数据库

@Table(name="tb_item")  //当前对象与数据表一一对应
public class Item extends BasePojo{
	
	@Id				//id属性作为tb_item表的主键信息
	@GeneratedValue(strategy=GenerationType.IDENTITY)     //表示主键自增
	private Long id;	//商品ID号
	
	
	private String title;	//商品标题
	private String sellPoint; //商品卖点
	private Long   price;		//价格
	private Integer num;		//商品数量
	private String barcode;		//二维码
	private String image;		//商品图片的URL
	private Long 	cid;		//商品分类id
	private Integer status;		//商品状态  1.表示正常   2 下架  3删除
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSellPoint() {
		return sellPoint;
	}
	public void setSellPoint(String sellPoint) {
		this.sellPoint = sellPoint;
	}
	public Long getPrice() {
		return price;
	}
	public void setPrice(Long price) {
		this.price = price;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Long getCid() {
		return cid;
	}
	public void setCid(Long cid) {
		this.cid = cid;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	
}
