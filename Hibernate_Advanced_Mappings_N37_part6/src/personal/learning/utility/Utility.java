package personal.learning.utility;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class Utility<T> {
	
	/**
	 * Method used to query in the database using native SQL
	 * 
	 * @param session
	 * @param sql
	 * @param view
	 * @return outputViewList
	 */
	public List<T> query(Session session, String sql, Class<T> clazz) {
		Query query = session.createNativeQuery(sql);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		List data = query.list();  // List of Maps  
		List<T> outputViewList = populateSummaryView(data, clazz);
		return outputViewList;
	}
	
	/**
	 * Populate List of summaryView
	 * 
	 * @param data : List
	 * @param view : SummaryView
	 * @return outputViewList
	 */
	public List<T> populateSummaryView(List data, Class<T> clazz) {
		List<T> outputViewList = new ArrayList<T>();
		for(Object object : data) {
			try {
				T view = clazz.newInstance();
	            Map row = (Map)object;
	            Set<String> keySet = row.keySet();
	            String keyFinal = null;
	            for(String key : keySet) {
	            	keyFinal = key.replaceAll("_", "");
	            	for (Field f : view.getClass().getDeclaredFields()) {
						if(StringUtils.equalsIgnoreCase(keyFinal, f.getName())) {
							f.set(view, row.get(key));
							break;
						}
					}
	            }
	            outputViewList.add(view);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		return outputViewList;
	}
}
