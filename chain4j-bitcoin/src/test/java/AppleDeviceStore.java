import org.apache.commons.lang3.StringUtils;


public class AppleDeviceStore {

  public AppleDevice createDevice(String type, String color) {
    Class.forName(StringUtils.capitalize(type) + StringUtils.capitalize(color)).newInstance();
  }
  
}
