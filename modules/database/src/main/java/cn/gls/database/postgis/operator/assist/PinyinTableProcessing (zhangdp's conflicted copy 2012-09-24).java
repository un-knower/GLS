package cn.gls.database.postgis.operator.assist;

import java.util.HashSet;
import java.util.Set;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import cn.gls.GLSDBSupport;
import cn.gls.data.PinyinPlace;
import cn.gls.database.operator.IAttributeOperatorDao;
import cn.gls.database.operator.assist.IGLSDatabaseAssistDao;
import cn.gls.util.StringUtils;

/**
 * @ClassName PinyinTableProcessing.java
 * @Createdate 2012-7-1
 * @Description 对拼音的处理类
 * @Version 1.0
 * @Update 2012-7-1
 * @author "Daniel Zhang"
 */
public class PinyinTableProcessing extends GLSDBSupport {

	private IAttributeOperatorDao attributeOperator;

	public void setAttributeOperator(IAttributeOperatorDao attributeOperator) {
		this.attributeOperator = attributeOperator;
	}
	     private IGLSDatabaseAssistDao databaseAssistDao;
	     public void setDatabaseAssistDao(IGLSDatabaseAssistDao databaseAssistDao) {
	         this.databaseAssistDao = databaseAssistDao;
	     }

	/**
	 * @Param PinyinTableProcessing
	 * @Description 插入拼音
	 * @return Set pinyin集合
	 * @param featureCollections
	 * @param FieldName
	 * @param cityName
	 * @param cityFieldName
	 */
	public Set<PinyinPlace> insertPinyinToTable(
			FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollections,
			String fieldName, String pinyinFieldName, String cityCodeFieldName,
			String cityName, String cityFieldName) {
		Set<PinyinPlace> places = new HashSet<PinyinPlace>();
		FeatureIterator<SimpleFeature> featureIterator = featureCollections
				.features();
		int cityCode = 0;
		if (cityName != null && !"".equalsIgnoreCase(cityName))
			cityCode = attributeOperator.selectCityCodeByCityName(cityName);
		
		while (featureIterator.hasNext()) {
			SimpleFeature feature = featureIterator.next();
			String name = (String) feature.getAttribute(fieldName);
			name = StringUtils.getPlaceName(name);
			if (name == null || "".equalsIgnoreCase(name))
				continue;
			 int cityCodeTemp=0;
			 if(cityCode==0&& cityCodeFieldName != null){
				 cityCodeTemp = (Integer) feature
							.getAttribute(cityCodeFieldName);
			 }
			 if (cityCode == 0 && cityCodeTemp == 0 && cityFieldName != null) {
					cityCodeTemp = (Integer) attributeOperator
							.selectCityCodeByCityName((String) feature
									.getAttribute(cityFieldName));
				}
			String pName = (String) feature.getAttribute(pinyinFieldName);
			
		//	pName=StringUtils.getPlaceName(pName);
			pName=pName.toLowerCase();
			//if(name.equalsIgnoreCase("意大利老船长"));
			PinyinPlace pinyinPlaces=new PinyinPlace(name, pName, cityCode == 0 ? cityCodeTemp : cityCode);
			places.add(pinyinPlaces);
		}
		featureIterator.close();
		return places;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see cn.gls.database.operator.assist.IPinyinOperator#insertPinyin(java.util.Set)
	 */
	public int insertPinyin(Set<PinyinPlace> places) {
                   return databaseAssistDao.insertPinyin(places);
	}


}
