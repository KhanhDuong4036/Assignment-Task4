package task.core;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.ios.IOSDriver;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class PageFactory<T> {
    private static Map<String, Object> pages = new HashMap<>();
    private Class<T> clazz;

    public PageFactory(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T create() {
        String platform;
        if (AppiumController.instance.getDriver() instanceof IOSDriver) {
            platform = "IOS";
        } else {
            platform = "Android";
        }
        try {
            Class<?> newClazz = null;
            try {
                newClazz = Class.forName(clazz.getName() + platform);
            } catch (Exception e) {
                //No-opt
            }
            if (newClazz == null) {
                System.out.println(this.getClass().getSimpleName() + ": Can not create page from " + clazz.getName() + platform);
                newClazz = clazz;
            }
            String classHash = AppiumController.instance.getDriver().hashCode() + newClazz.getName();
            if (pages.containsKey(classHash)) {
                return (T) pages.get(classHash);
            }
            System.out.println(this.getClass().getSimpleName() + ": Create instance for " + newClazz.getSimpleName());
            Constructor<?> constructor = newClazz.getConstructor(MobileDriver.class);
            Object object = constructor.newInstance(AppiumController.instance.getDriver());
            pages.put(classHash, object);
            return (T) object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
