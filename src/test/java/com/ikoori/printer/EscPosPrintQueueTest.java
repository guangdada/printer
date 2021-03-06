package com.ikoori.printer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ikoori.escp.PrintJob;
import com.ikoori.escp.PrintQueue;
import com.ikoori.escp.PrintThread;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class EscPosPrintQueueTest extends BaseTest {
	
	static{
		
		System.out.println("printThread is start : " + System.currentTimeMillis());
		PrintThread.start();
	}
	
	@Override
	public void test() {
		mySleep(2);
		System.out.println("add print job...");
		PrintQueue.add(new PrintJob(0, jsonParam(), "192.168.0.50"));
		mySleep(15);
	}
	
	public void mySleep(int i) {
		try {
			System.out.println("sleep" + i*1000 + "ms ...");
			Thread.sleep(i * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public Map<String, Object> jsonParam(){
		Map<String, Object> template = new HashMap<String, Object>();
		Map<String, Object> keys = new HashMap<String, Object>();
		String posResult = "重单 请不要重复下单，流水号1928347";
		keys.put("title", "网络订单");
		keys.put("brandName", "智慧餐厅");
		keys.put("shopName", "天山店");
		keys.put("tableNumb", "0102");
		keys.put("tableName", "魔都厅");
		keys.put("orderId", "1609101220001");
		keys.put("dateTime", "2016-09-10 12:21:00");
		keys.put("allPrice", "499.0");
		keys.put("barCode","8888");
		
		if(!posResult.contains("成功")){
			if(posResult.contains("重单")){
				keys.put("warnTitle", "提    示");
				keys.put("warnMsg", "该订单已处理");
			}else{
				keys.put("warnTitle", "异常提示");
				keys.put("warnMsg", "自动下单失败请人工处理");
			}
		}else{
			if(posResult.contains("无此") || posResult.contains("沽清")
					|| posResult.contains("不存在")){
				keys.put("warnTitle", "异常提示");
				keys.put("warnMsg", "部分菜品未下成功人工处理");
			}
		}
		
		keys.put("posTitle", "收银软件下单结果");
		keys.put("posMsg", posResult);
		
		
		List<Map<String, Object>> goods = new ArrayList<Map<String,Object>>();
		List<Record> menu = Db.find("select * from menu");
		for (Record record : menu) {
			Map<String, Object> good = new HashMap<String, Object>();
			good.put("code", record.get("code"));
			good.put("name", record.get("name"));
			good.put("quantity", "1.0");
			good.put("price", record.get("price"));
			goods.add(good);
		}
		
		template.put("goods", goods);
		template.put("keys", keys);
		System.out.println(JsonKit.toJson(template));
		return template;
	}

}
