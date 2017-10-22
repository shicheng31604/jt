package com.jt.manage.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.jt.common.vo.PicUploadResult;


@Controller
public class FileUploadController {
	
	private static Logger logger = Logger.getLogger(FileUploadController.class);
	
	/**
	 * 页面返回的JSON格式 {"error":0,"url":"图片的保存路径","width":图片的宽度,"height":图片的高度}
	 * error 0表示正确的图片   1表示非图片类型
	 * url:图片的浏览器访问地址        image.jt.com/images
	 * 真实路径：D:\jt-upload\images
	 * 文件上传具体步骤：
	 * 	1.获取文件全名   girl.jpg
	 * 	2.获取文件的类型进行判断，判断是否为一个真实的图片，如果不是则error为1
	 *  3.判断当前文件是否为木马（非法程序）imageBUffer
	 *  4.为了文件名尽可能的不重复手动的拼接文件夹
	 *  5.生成本地磁盘路径
	 *  6.生成url路径
	 *  7.磁盘写入
	 *  8.return 返回
	 */
	
	@RequestMapping("/pic/upload")
	@ResponseBody
	public PicUploadResult fileUpload(MultipartFile uploadFile){
		PicUploadResult result = new PicUploadResult();
		
		//1.获取文件的全名 girl.jpg
		String fileName = uploadFile.getOriginalFilename();
		
		//2.获取数据类型   .jpg|png|gif
		String fileType = fileName.substring(fileName.lastIndexOf("."));
		
		//3.判断是否为一个图片类型
		if(!fileType.matches("^.*(jpg|png|gif)$")){
			//表示当前文件类型 不是图片
			result.setError(1);
		}
		
		try {
			
			//4.表示数据类型为图片   判断是否为一个非法程序
			BufferedImage bufferedImage = ImageIO.read(uploadFile.getInputStream());
			String width = bufferedImage.getWidth() + "";
			String height = bufferedImage.getHeight()+ "";
			
			//为结果赋值宽度和高度
			result.setHeight(height);
			result.setWidth(width);
			
			//5.定义真实路径    虚拟路径
			String rPath = "D:/jt-upload/images/";
			String uPath = "http://image.jt.com/images/";
			
			//6防止文件名重复添加多重文件夹   按照 yyyy/MM/dd/HH
			String datePath = new SimpleDateFormat("yyyy/MM/dd/HH").format(new Date()); 
			
			//7.获取三随机数拼接文件名称 防止重复
			Random random = new Random();
			int randomNumber = (random.nextInt(900)) + 100;
			
			//8.拼接本地磁盘路径  D:/jt-upload/images/yyyy/MM/dd/HH/1.jpg
			String realPath = rPath + datePath;
			
			//9.新建文件夹
			File file  = new File(realPath);
		  	
			//10判断当前文件夹是否存在
			if(!file.exists()){
				file.mkdirs(); //表示新建文件夹
			}
		
			//11.写盘操作  拼接文件全路径  realPath  +"/"+ randomNumber+fileName
			uploadFile.transferTo(new File(realPath +"/" + randomNumber+fileName));
			
			
			//image.jt.com/images/yyyy/MM/dd/
			String urlPath = uPath + datePath +"/" + randomNumber + fileName;
			result.setUrl(urlPath);

		} catch (IOException e) {
			//当前文件不是图片
			result.setError(1);
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		
		return result;
	}
}
