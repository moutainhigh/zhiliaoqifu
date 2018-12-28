package com.ebeijia.zl.common.utils.enums;

/**
 * 图片上传种类
 * @author Administrator
 *
 */
public enum ImageTypeEnum {

	ImageTypeEnum_01("01", "goodsSpec", "商品规格"),
	ImageTypeEnum_02("02", "goodsSpecValues", "商品规格值"),
	ImageTypeEnum_03("03", "remitEvidencce", "上账凭证");

	private String code;
	private String value;
	private String name;

	ImageTypeEnum(String code, String value, String name){
		this.code=code;
		this.value=value;
		this.name=name;
	}
	public String getCode() {
		return code;
	}
	public String getValue() {
		return value;
	}
	public String getName() {
		return name;
	}
	public static ImageTypeEnum findByBId(String code) {
		for (ImageTypeEnum t : ImageTypeEnum.values()) {
			if (t.code.equalsIgnoreCase(code)) {
				return t;
			}
		}
		return null;
	}
}