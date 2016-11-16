package cn.tf.ecps.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;



import cn.tf.ecps.dao.EbBrandDao;
import cn.tf.ecps.dao.EbShipAddrDao;
import cn.tf.ecps.dao.TsPtlUserDao;
import cn.tf.ecps.po.EbBrand;
import cn.tf.ecps.po.EbShipAddr;
import cn.tf.ecps.po.EbShipAddrBean;
import cn.tf.ecps.po.TsPtlUser;
import cn.tf.ecps.service.EbBrandService;
import cn.tf.ecps.service.EbShipAddrService;
import cn.tf.ecps.service.TsPtlUserService;
import cn.tf.ecps.utils.ECPSUtil;
import cn.tf.ecps.utils.MD5;


@Service
public class EbShipAddrServiceImpl implements EbShipAddrService {
	
	@Autowired
	private EbShipAddrDao shipAddrDao;

	
	public List<EbShipAddrBean> selectAddrByUserId(Long userId) {
		return shipAddrDao.selectAddrByUserId(userId);
	}


	public EbShipAddr selectAddrById(Long shipAddrId) {
		return shipAddrDao.selectAddrById(shipAddrId);
	}


	public void saveOrUpdateAddr(EbShipAddr addr) {
		//修改默认收货地址,把默认变为非默认
		shipAddrDao.updateDefaultAddr(addr.getPtlUserId());
		
		if(addr.getShipAddrId()==null){
			//说明是新增
			shipAddrDao.saveAddr(addr);
		}else{
			//说明是修改
			shipAddrDao.updateAddr(addr);
		}
		
		
		
	}


	public int deleteAddr(String id) {
		return shipAddrDao.deleteAddr(id);
		
	}


	public List<EbShipAddrBean> selectAddrByUserIdWithRedis(Long userId) {
		List<EbShipAddrBean> addrBeanList = new ArrayList<EbShipAddrBean>();
		String host = ECPSUtil.readProp("redis_path");
		String port = ECPSUtil.readProp("redis_port");
		Jedis je = new Jedis(host, new Integer(port));
		List<String> addrIdList = je.lrange("ptl_user:"+userId+"l:addrList", 0, -1);
		for(String addrId : addrIdList){
			String shipAddrId = je.hget("ship_addr:"+addrId, "shipAddrId");
			String shipName = je.hget("ship_addr:"+addrId, "shipName");
			
			
			
			String province = je.hget("ship_addr:"+addrId, "province");
			String city = je.hget("ship_addr:"+addrId, "city");
			String district = je.hget("ship_addr:"+addrId, "district");
			String addr = je.hget("ship_addr:"+addrId, "addr");
			String zipCode = je.hget("ship_addr:"+addrId, "zipCode");
			String phone = je.hget("ship_addr:"+addrId, "phone");
			String defaultAddr = je.hget("ship_addr:"+addrId, "defaultAddr");
			String provText = je.hget("ship_addr:"+addrId, "provText");
			String cityText = je.hget("ship_addr:"+addrId, "cityText");
			String distText = je.hget("ship_addr:"+addrId, "distText");
			EbShipAddrBean esa = new EbShipAddrBean();
			esa.setShipAddrId(new Long(shipAddrId));
			esa.setShipName(shipName);
			esa.setProvince(province);
			esa.setCity(city);
			esa.setDistrict(district);
			esa.setAddr(addr);
			esa.setZipCode(zipCode);
			esa.setPhone(phone);
			esa.setProvText(provText);
			esa.setCity(cityText);
			esa.setDistText(distText);
			
			esa.setDefaultAddr(new Short(defaultAddr));
			addrBeanList.add(esa);
		}
		
		return addrBeanList;
	}


	public EbShipAddr selectAddrByIdWithRedis(Long shipAddrId) {
		
		String host = ECPSUtil.readProp("redis_path");
		String port = ECPSUtil.readProp("redis_port");
		Jedis je = new Jedis(host, new Integer(port));
		String shipName = je.hget("ship_addr:"+shipAddrId, "shipName");
		
		String province = je.hget("ship_addr:"+shipAddrId, "province");
		String city = je.hget("ship_addr:"+shipAddrId, "city");
		String district = je.hget("ship_addr:"+shipAddrId, "district");
		String addr = je.hget("ship_addr:"+shipAddrId, "addr");
		String zipCode = je.hget("ship_addr:"+shipAddrId, "zipCode");
		String phone = je.hget("ship_addr:"+shipAddrId, "phone");
		String defaultAddr = je.hget("ship_addr:"+shipAddrId, "defaultAddr");
		
		EbShipAddrBean esa = new EbShipAddrBean();
		esa.setShipAddrId(new Long(shipAddrId));
		esa.setShipName(shipName);
		esa.setProvince(province);
		esa.setCity(city);
		esa.setDistrict(district);
		esa.setAddr(addr);
		esa.setZipCode(zipCode);
		esa.setPhone(phone);
		
		esa.setDefaultAddr(new Short(defaultAddr));
		
		return esa;
		
	}
	
	

}
