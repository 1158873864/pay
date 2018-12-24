package com.qh.paythird.allipay;

import java.util.HashMap;
import java.util.Map;

public class AllipayConst {
	 /***通联支付 version**/
	public static final String version_no = "11";
    /***通联支付 微信JS支付*****/
    public static final String paytype_WX_JS = "W02";
    /***通联支付 支付宝JS支付*****/
    public static final String paytype_ALI_JS = "A02";
    /***通联支付 Qq钱包JS支付*****/
    public static final String paytype_QQ_JS = "Q02";
    /***通联支付 微信扫码*****/
    public static final String paytype_WX_QRCODE = "W01";
    /***通联支付 支付宝 扫码*****/
    public static final String paytype_ALI_QRCODE = "A01";
    /***通联支付 Qq钱包 扫码*****/
    public static final String paytype_QQ_QRCODE = "Q01";
    /***通联 支付限制 不支持信用卡 no_credit ****/
    public static final String limit_pay_no_credit = "no_credit";
    /***通联支付 返回成功***/
    public static final String retcode_succ = "0000";
    /***通联支付 qpay version v1.0***/
    public static final String version_v10 = "v1.0";
    /***通联支付 版本**/
    public static final String version_v15 = "v1.5";
    /***通联代付  编号****/
    public static final String df_code = "100014";
    /***通联批量代付  编号****/
    public static final String batch_df_code = "100002";
    /***通联批量代付 查询编号***/
    public static final String batch_df_query_code = "200005";
    /***通联代付 业务代码  保险理赔 00600**/
    public static final String df_business_code = "00600";
    /***通联代付 业务其他  保险理赔 09900**/
    public static final String df_business_code_other = "09900";
    /***通联代付 客户类型 对私 0 ***/
    public static final String account_pro_person = "0";
    /***通联代付 银行卡类型  00银行卡，01存折，02信用卡。不填默认为银行卡00。***/
    public static final String account_type_bank = "00";
    /***通联银行卡号映射**/
    public static final Map<String, String> tlBankMap = new HashMap<>();
    /***通联md5签名 0*/
    public static final String signtype_md5 = "0";
    /* 通联支付请求地址 */
    public static final String pay_url = "allipay_pay_url";
    /* 通联支付查询地址 */
    public static final String query_url = "allipay_query_url";
    /* 通联支付请求成功标志 */
    public static final String pay_succ = "SUCCESS";

    static {
        tlBankMap.put("建设银行", "0105");
        tlBankMap.put("农业银行", "0103");
        tlBankMap.put("工商银行", "0102");
        tlBankMap.put("中国银行", "0104");
        tlBankMap.put("浦发银行", "0310");
        tlBankMap.put("光大银行", "0303");
        tlBankMap.put("平安银行", "04105840");
        tlBankMap.put("兴业银行", "0309");
        tlBankMap.put("邮政储蓄银行", "0403");
        tlBankMap.put("中信银行", "0302");
        tlBankMap.put("华夏银行", "0304");
        tlBankMap.put("招商银行", "0308");
        tlBankMap.put("广发银行", "0306");
        tlBankMap.put("北京银行", "04031000");
        tlBankMap.put("上海银行", "04012900");
        tlBankMap.put("民生银行", "0305");
        tlBankMap.put("交通银行", "0301");
        tlBankMap.put("北京农村商业银行", "0314");
    }

    /***银行对应 行号 Map */
    public static final Map<String, String> bank_settle_no = new HashMap<>();

    static {
        bank_settle_no.put("建设银行", "105100000017");
        bank_settle_no.put("农业银行", "103100000026");
        bank_settle_no.put("工商银行", "102100099996");
        bank_settle_no.put("中国银行", "104100000004");
        bank_settle_no.put("浦发银行", "310290000013");
        bank_settle_no.put("光大银行", "303100000006");
        bank_settle_no.put("平安银行", "307584007998");
        bank_settle_no.put("兴业银行", "309391000011");
        bank_settle_no.put("邮储银行", "403100000004");
        bank_settle_no.put("邮政储蓄银行", "403100000004");
        bank_settle_no.put("中信银行", "302100011000");
        bank_settle_no.put("华夏银行", "304100040000");
        bank_settle_no.put("招商银行", "308584000013");
        bank_settle_no.put("广发银行", "306581000003");
        bank_settle_no.put("北京银行", "313100000013");
        bank_settle_no.put("上海银行", "325290000012");
        bank_settle_no.put("民生银行", "305100000013");
        bank_settle_no.put("交通银行", "301290000007");
        bank_settle_no.put("北京农村商业银行", "402100000018");

    }

    /***银行对应 银行编码 Map */
    public static final Map<String, String> bank_code = new HashMap<>();

    static {
        bank_code.put("建设银行", "CCB");
        bank_code.put("农业银行", "ABC");
        bank_code.put("工商银行", "ICBC");
        bank_code.put("中国银行", "BOC");
        bank_code.put("浦发银行", "");
        bank_code.put("光大银行", "CEB");
        bank_code.put("平安银行", "PINGANBANK");
        bank_code.put("兴业银行", "CIB");
        bank_code.put("邮储银行", "POST");
        bank_code.put("邮政储蓄银行", "POST");
        bank_code.put("中信银行", "ECITIC");
        bank_code.put("华夏银行", "HXB");
        bank_code.put("招商银行", "CMBCHINA");
        bank_code.put("广发银行", "CGB");
        bank_code.put("北京银行", "BCCB");
        bank_code.put("上海银行", "SHB");
        bank_code.put("民生银行", "CMBC");
        bank_code.put("交通银行", "BOCO");
        bank_code.put("北京农村商业银行", "BJRCB");
    }
}
