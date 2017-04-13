package com.fei.carFetcher.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class CarModel {
    private String id;//
    private String pId;//父ID
    private Integer type;//类型
    private String initialed;//首字母
    private String logoPath;//商标
    private String name;//名称
    private String grade;//级别
    private BigDecimal displacement;//排气量
    private Integer mannedNum;//载人数量
    private BigDecimal totalWeight;//总质量
    private BigDecimal curbWeight;//整备质量
    private BigDecimal loadWeight;//核定载质量
    private BigDecimal oilConsumption;//百公里油量
    private String gearbox;//变速箱
    private Integer energy;//能源种类
    private String envStandard;//环保标准
    private BigDecimal tankCapacity;//油箱容积
    private Integer sort;//排序
    private String createrId;//
    private Date createTime;//
    private Date modifyTime;//
    private String modifiterId;//
    private String carNum;//车牌号
    private String oilLv;//燃油标号
    /**
     * 2017/3/31
     */
    private String modelCommand;//MODEL_COMMAND 标定车型命令
    private String carStatus;//CAR_STATUS 车身状态命令（车灯、车门）
    
    /**
     * 品牌名称
     */
    private String brandName;
    
    /**
     * 厂家名称
     */
    private String factorName;
    
    /**
     * 车系名称
     */
    private String carName;

    public String getCarNum() {
		return carNum;
	}

	public void setCarNum(String carNum) {
		this.carNum = carNum;
	}

	public String getOilLv() {
		return oilLv;
	}

	public void setOilLv(String oilLv) {
		this.oilLv = oilLv;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId == null ? null : pId.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getInitialed() {
        return initialed;
    }

    public void setInitialed(String initialed) {
        this.initialed = initialed == null ? null : initialed.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade == null ? null : grade.trim();
    }

    public BigDecimal getDisplacement() {
        return displacement;
    }

    public void setDisplacement(BigDecimal displacement) {
        this.displacement = displacement;
    }

    public Integer getMannedNum() {
        return mannedNum;
    }

    public void setMannedNum(Integer mannedNum) {
        this.mannedNum = mannedNum;
    }

    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    public BigDecimal getCurbWeight() {
        return curbWeight;
    }

    public void setCurbWeight(BigDecimal curbWeight) {
        this.curbWeight = curbWeight;
    }

    public BigDecimal getLoadWeight() {
        return loadWeight;
    }

    public void setLoadWeight(BigDecimal loadWeight) {
        this.loadWeight = loadWeight;
    }

    public BigDecimal getOilConsumption() {
        return oilConsumption;
    }

    public void setOilConsumption(BigDecimal oilConsumption) {
        this.oilConsumption = oilConsumption;
    }

    public String getGearbox() {
        return gearbox;
    }

    public void setGearbox(String gearbox) {
        this.gearbox = gearbox == null ? null : gearbox.trim();
    }

    public Integer getEnergy() {
        return energy;
    }

    public void setEnergy(Integer energy) {
        this.energy = energy;
    }

    public String getEnvStandard() {
        return envStandard;
    }

    public void setEnvStandard(String envStandard) {
        this.envStandard = envStandard == null ? null : envStandard.trim();
    }

    public BigDecimal getTankCapacity() {
        return tankCapacity;
    }

    public void setTankCapacity(BigDecimal tankCapacity) {
        this.tankCapacity = tankCapacity;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath == null ? null : logoPath.trim();
    }

    public String getCreaterId() {
        return createrId;
    }

    public void setCreaterId(String createrId) {
        this.createrId = createrId == null ? null : createrId.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getModifiterId() {
        return modifiterId;
    }

    public void setModifiterId(String modifiterId) {
        this.modifiterId = modifiterId == null ? null : modifiterId.trim();
    }

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getFactorName() {
		return factorName;
	}

	public void setFactorName(String factorName) {
		this.factorName = factorName;
	}

	public String getCarName() {
		return carName;
	}

	public void setCarName(String carName) {
		this.carName = carName;
	}

	public String getModelCommand() {
		return modelCommand;
	}

	public void setModelCommand(String modelCommand) {
		this.modelCommand = modelCommand;
	}

	public String getCarStatus() {
		return carStatus;
	}

	public void setCarStatus(String carStatus) {
		this.carStatus = carStatus;
	}

	@Override
	public String toString() {
		return "CarModel [id=" + id + ", pId=" + pId + ", type=" + type + ", initialed=" + initialed + ", name=" + name
				+ ", grade=" + grade + ", displacement=" + displacement + ", mannedNum=" + mannedNum + ", totalWeight="
				+ totalWeight + ", curbWeight=" + curbWeight + ", loadWeight=" + loadWeight + ", oilConsumption="
				+ oilConsumption + ", gearbox=" + gearbox + ", energy=" + energy + ", envStandard=" + envStandard
				+ ", tankCapacity=" + tankCapacity + ", oilLv=" + oilLv + ", carStatus=" + carStatus + ", brandName="
				+ brandName + ", factorName=" + factorName + "]";
	}
	
	
}