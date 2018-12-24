package com.qh.paythird.allipay;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qh.common.utils.R;
import com.qh.pay.api.Order;
import com.qh.pay.api.PayConstants;
import com.qh.pay.api.constenum.OrderState;
import com.qh.pay.api.constenum.OutChannel;
import com.qh.pay.api.utils.Md5Util;
import com.qh.pay.api.utils.ParamUtil;
import com.qh.pay.api.utils.RequestUtils;
import com.qh.pay.service.PayService;
import com.qh.paythird.allipay.utils.HttpConnectionUtil;
import com.qh.redis.service.RedisUtil;

/**
 * 通联支付
 *
 */
@Service
public class AllipayService {


	private static final Logger logger = LoggerFactory.getLogger(AllipayService.class);
	
	/**
	 * @Description 支付发起
	 * @param order
	 * @return
	 */
	public R order(Order order) {
		
		logger.info("通联支付 开始------------------------------------------------------");
		try {
			
			if (OutChannel.q.name().equals(order.getOutChannel())) {
				//快捷支付（收银台）
				return order_q(order);
			} else if (OutChannel.ali.name().equals(order.getOutChannel()) ||
					       OutChannel.wx.name().equals(order.getOutChannel()) ||
					           OutChannel.qq.name().equals(order.getOutChannel()) ||
					               OutChannel.gzh.name().equals(order.getOutChannel())) {
				return order_union(order);
			} else {
				logger.error("通联支付 不支持的支付渠道：{}", order.getOutChannel());
				return R.error("不支持的支付渠道");
			}

		} finally {
			logger.info("通联支付 结束------------------------------------------------------");
		}
	}
	
     /*
	 * 聚合支付（收银台）
	 * @param order
	 * @return
	 */
	private R order_union(Order order){
		String merchantCode = order.getMerchNo();
		String orderId = merchantCode + order.getOrderNo();
		String payMerch = order.getPayMerch();
		TreeMap<String, String> params = new TreeMap<String, String>();
		/* 商户号 */
		params.put("cusid", payMerch);
		/* 取对应商户号的APPID */
		params.put("appid", RedisUtil.getPayCommonValue(payMerch + "appid"));
		params.put("version", AllipayConst.version_no);
		params.put("trxamt", ParamUtil.yuanToFen(order.getAmount()));
		params.put("reqsn", orderId);
		if (OutChannel.gzh.name().equals(order.getOutChannel())) {
			params.put("paytype", "W02");
			// params.put("redirect_url",
			// payProperties.getTl_callback_url()+"?"+payFundrecord.getProfitLoss() +
			// PayConstants.split_quot + payFundrecord.getTranAmt());
			params.put("acct", "openid");
			params.put("limit_pay", AllipayConst.limit_pay_no_credit);
		} else if (OutChannel.ali.name().equals(order.getOutChannel())) {
			params.put("paytype", AllipayConst.paytype_ALI_QRCODE);
		} else if (OutChannel.qq.name().equals(order.getOutChannel())) {
			params.put("paytype", AllipayConst.paytype_QQ_QRCODE);
		} else if (OutChannel.wx.name().equals(order.getOutChannel())) {
			params.put("paytype", AllipayConst.paytype_WX_QRCODE);
		}
		params.put("randomstr", String.valueOf(ParamUtil.generateCode8()));
//		params.put("pageNotifyUrl", PayService.commonReturnUrl(order));
		params.put("body", order.getProduct());
		params.put("remark", order.getMemo());
		params.put("notify_url", PayService.commonNotifyUrl(order));
		/* 取对应商户号的APPKEY */
		String appkey = RedisUtil.getPayCommonValue(payMerch + "appkey");
		params.put("key", appkey);
		String sourceStr = ParamUtil.buildAllParams(params, false);
		logger.info("通联支付签名原串：" +sourceStr);
		String sign = Md5Util.MD5(sourceStr).toUpperCase();
		if (ParamUtil.isEmpty(sign)) {
			return R.error("通联支付MD5签名失败！");
		}
		
		params.remove("key");
		params.put("sign", sign);
		String payUrl = RedisUtil.getPayCommonValue(AllipayConst.pay_url);
		logger.info("通联支付请求地址：" + payUrl);
		logger.info("通联支付请求参数：" + params);
		
		HttpConnectionUtil http = new HttpConnectionUtil(payUrl);
		try {
			http.init();
		} catch (Exception e) {
			return R.error("支付请求失败！");
		}
		String result = null;
		try {
			byte[] bys = http.postParams(params, true);
			result = new String(bys, "utf-8");
			logger.info("通联支付请求结果:" + result);
		} catch (IOException e) {
			return R.error("请求结果获取失败！");
		}

		@SuppressWarnings("unchecked")
		Map<String, String> resMap = JSON.parseObject(result, Map.class);
		if (!AllipayConst.pay_succ.equals(resMap.get("retcode"))) {
			return R.error("通联支付请求失败!");
		}

		Map<String, String> data = new HashMap<>();
		if (OutChannel.gzh.name().equals(order.getOutChannel())) {
			JSONObject jo = JSON.parseObject((String) resMap.get("payinfo"));
			StringBuilder sb = new StringBuilder();
			sb.append("appId=" + jo.getString("appId"));
			sb.append("&timeStamp=" + jo.getString("timeStamp"));
			sb.append("&nonceStr=" + jo.getString("nonceStr"));
			try {
				sb.append("&package=" + URLEncoder.encode(jo.getString("package"), "utf-8"));
				sb.append("&signType=" + jo.getString("signType"));
				sb.append("&paySign=" + URLEncoder.encode(jo.getString("paySign"), "utf-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			data.put(PayConstants.web_payinfo, sb.toString());
		} else {
			try {
				data.put(PayConstants.web_qrcode_url, URLEncoder.encode(resMap.get("payinfo"), "utf-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return R.okData(data);
	 }
	 
	/**
	 * 快捷支付（收银台）
	 * @param order
	 * @return
	 */
	private R order_q(Order order){
		
		logger.info("通联快捷支付请求开始：");
		
		return R.error();
	}
	
	/**
	 * @Description 支付回调
	 * @param order
	 * @param request
	 * @return
	 */
	public R notify(Order order, HttpServletRequest request) {
		logger.info("通联支付异步通知开始-------------------------------------------------");
		TreeMap<String, String> params;
		try {
			// 通知传输的编码为GBK
			request.setCharacterEncoding("gbk");
			params = RequestUtils.getRequestParam(request);
			logger.info("通联支付异步通知参数：" + params);
		} catch (UnsupportedEncodingException e1) {
			return R.error("不支持GBK编码！");
		}
		
		if (ParamUtil.isEmpty(params)) {
			return R.error("通联支付异步通知参数为空！");
		}
		
		String sign = params.remove("sign").toUpperCase();
		String appkey = RedisUtil.getPayCommonValue(order.getPayMerch() + "appkey");
		params.put("key", appkey);
		String sourceStr = ParamUtil.buildAllParams(params, false);
		String localSign = Md5Util.MD5(sourceStr).toUpperCase();
		logger.info("通联支付异步通知，sourceStr=" + sourceStr);
		logger.info("通联支付异步通知, localSign=" + localSign);
		logger.info("通联支付异步通知, sign=" + sign);
		
		if (!localSign.equals(sign)) {
			return R.error("通联支付异步通知验签失败！");
		}
		
		String trxstatus = params.get("trxstatus");
		boolean paySuccess = AllipayConst.retcode_succ.equals(trxstatus);

		if (!paySuccess) {
			String notifyMsg = params.get("trxreserved");
			logger.info("通联支付异步通知返回描述：" + notifyMsg);
			return R.error(notifyMsg);
		}
		
		order.setOrderState(OrderState.succ.id());
		String trxamt = params.get("trxamt");
		if (ParamUtil.isNotEmpty(trxamt)) {
			order.setRealAmount(ParamUtil.fenToYuan(trxamt));
		}
	
		logger.info("{},{}", order.getMerchNo(), order.getOrderNo());
		logger.info("通联支付异步通知结束-------------------------------------------------");
		return R.ok();
	}
	
	/**
	 * @Description 支付查询
	 * @param order
	 * @return
	 */
	public R query(Order order) {
		logger.info("通联支付 查询 开始------------------------------------------------------------");
		String merchantCode = order.getMerchNo();
		String orderId = merchantCode + order.getOrderNo();
		String payMerch = order.getPayMerch();
		TreeMap<String, String> params = new TreeMap<String, String>();
		/* 商户号 */
		params.put("cusid", payMerch);
		/* 取对应商户号的APPID */
		params.put("appid", RedisUtil.getPayCommonValue(payMerch + "appid"));
		params.put("version", AllipayConst.version_no);
		params.put("reqsn", orderId);

		/* 取对应商户号的APPKEY */
		String appkey = RedisUtil.getPayCommonValue(payMerch + "appkey");
		params.put("key", appkey);
		String sourceStr = ParamUtil.buildAllParams(params, false);
		logger.info("通联支付 查询 签名原串：" +sourceStr);
		String sign = Md5Util.MD5(sourceStr).toUpperCase();
		if (ParamUtil.isEmpty(sign)) {
			return R.error("通联支付 查询 MD5签名失败！");
		}
		
		params.remove("key");
		params.put("sign", sign);
		String queryUrl = RedisUtil.getPayCommonValue(AllipayConst.query_url);
		logger.info("通联支付 查询 请求地址：" + queryUrl);
		logger.info("通联支付 查询 请求参数：" + params);
		
		HttpConnectionUtil http = new HttpConnectionUtil(queryUrl);
		try {
			http.init();
		} catch (Exception e) {
			return R.error("支付查询失败！");
		}
		String result = null;
		try {
			byte[] bys = http.postParams(params, true);
			result = new String(bys, "utf-8");
			logger.info("通联支付请求结果:" + result);
		} catch (IOException e) {
			return R.error("获取支付查询结果失败！");
		}

		@SuppressWarnings("unchecked")
		Map<String, String> resMap = JSON.parseObject(result, Map.class);
		if (!AllipayConst.pay_succ.equals(resMap.get("retcode"))) {
			return R.error("通联支付 查询 请求失败!");
		}
		
		String trxstatus = resMap.get("trxstatus");
		boolean paySuccess = AllipayConst.retcode_succ.equals(trxstatus);

		if (!paySuccess) {
			/* 订单已经超时关闭标志 */
			if (trxstatus.equals("3045")) {
				order.setOrderState(OrderState.close.id());
			}
			String errmsg = resMap.get("errmsg");
			logger.info("通联支付 查询 返回描述：" + errmsg);
			return R.error(errmsg);
		}

		order.setOrderState(OrderState.succ.id());
		String amount = resMap.get("trxamt");
		if (ParamUtil.isNotEmpty(amount)) {
			order.setRealAmount(ParamUtil.fenToYuan(amount));
		}
		order.setBusinessNo(resMap.get("trxid"));

		logger.info("{},{}", order.getMerchNo(), order.getOrderNo());
		logger.info("通联支付 聚合支付 查询 结束-------------------------------------------------");
		
		return R.ok();
	}

}
