package cn.edu.fudan.se.clonedetector.util.test;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.edu.fudan.se.clonedetector.bean.CCStreamProperty;

public class TestJson {
	public static void main(String[] args) {
		String streamList = "[{\"stream\":\"result\",\"blstream\":\"result\",\"view\":\"result\",\"viewLocalPath\":\"result\"}]";

		try {
			JSONArray array = new JSONArray(streamList);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				obj.getString("blstream");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
