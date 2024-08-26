package admin.adminbackend.openapi.service.UtilService;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilService {  //0501 추가

    public static JSONObject stringToJson(String jsonString) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj;
        obj = parser.parse(jsonString);
        JSONObject jsonObj = (JSONObject) obj;
        return jsonObj;
    }

    public static void ExceptionValue(String toSTRING,Class c){
        Logger logger= LoggerFactory.getLogger(c.getClass());
        logger.warn("예외발생 변수:{}",toSTRING);
    }
}
