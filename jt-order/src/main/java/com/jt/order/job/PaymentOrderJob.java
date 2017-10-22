package com.jt.order.job;

import java.util.Date;

import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.jt.order.mapper.OrderMapper;

public class PaymentOrderJob extends QuartzJobBean{

	//本例中，每隔一分钟，就会触发此方法
	//业务逻辑：通过quartz，每隔一分钟，
	//将tb_order里的订单 :status=1，订单生成日期是前两天的订单状态变为6 ，6表示订单关闭。
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		ApplicationContext applicationContext=(ApplicationContext) context.getJobDetail().
												getJobDataMap().get("applicationContext");
		//算出两天前的日期,下面代码的意思是以当前时间为基准，算出两天前的日期
		Date date=new DateTime().minusDays(2).toDate();
		//下面表示30分钟之前
		//Date data2=new DateTime().minusMinutes(30).toDate();
		
		//sql语句：update tb_order set status=6 
		//where status=1 and create_time < date
		
		//比如；今天是2017-10-19
		//date=2017-10-17
		
		applicationContext.getBean(OrderMapper.class).updateOrderStatus(date);
		
	}

}
