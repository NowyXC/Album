package com.nowy.baselib.utils;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射工具类
 */
public abstract class ReflectionUtils {
    private static String TAG = ReflectionUtils.class.getSimpleName();

    public static interface FieldCallback {
        public void doWith(Field field) throws Exception;
    }


    /**
     * 得到某个对象的公共属性
     *
     * @param owner, fieldName
     * @return 该属性对象
     * @throws Exception
     *
     */
    public Object getProperty(Object owner, String fieldName) throws Exception {
        Class ownerClass = owner.getClass();

        Field field = ownerClass.getField(fieldName);

        Object property = field.get(owner);

        return property;
    }


    /**
     * 得到某类的静态公共属性
     *
     * @param className   类名
     * @param fieldName   属性名
     * @return 该属性对象
     * @throws Exception
     */
    public Object getStaticProperty(String className, String fieldName)
            throws Exception {
        Class ownerClass = Class.forName(className);

        Field field = ownerClass.getField(fieldName);

        Object property = field.get(ownerClass);

        return property;
    }



    /**
     * 执行某对象方法
     *
     * @param owner
     *            对象
     * @param methodName
     *            方法名
     * @param args
     *            参数
     * @return 方法返回值
     * @throws Exception
     */
    public Object invokeMethod(Object owner, String methodName, Object[] args)
            throws Exception {

        Class ownerClass = owner.getClass();

        Class[] argsClass = new Class[args.length];

        for (int i = 0, j = args.length; i < j; i++) {
            argsClass[i] = args[i].getClass();
        }

        Method method = ownerClass.getMethod(methodName, argsClass);

        return method.invoke(owner, args);
    }



    /**
     * 执行某类的静态方法
     *
     * @param className
     *            类名
     * @param methodName
     *            方法名
     * @param args
     *            参数数组
     * @return 执行方法返回的结果
     * @throws Exception
     */
    public Object invokeStaticMethod(String className, String methodName,
                                     Object[] args) throws Exception {
        Class ownerClass = Class.forName(className);

        Class[] argsClass = new Class[args.length];


        for (int i = 0, j = args.length; i < j; i++) {
            argsClass[i] = args[i].getClass();
        }

        Method method = ownerClass.getMethod(methodName, argsClass);

        return method.invoke(null, args);
    }



    /**
     * 遍历所有field
     * @param clazz
     * @param fieldCallback
     */
    public static void doWithFields(Class<?> clazz, FieldCallback fieldCallback) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            try {
                fieldCallback.doWith(f);
            } catch (Exception ex) {
                Log.e(TAG, "ReflectionUtils.doWithFields error", ex);
            }
        }
    }

    /**
     * 遍历所有field，包括父类
     * @param clazz
     * @param fieldCallback
     */
    public static void doWithFieldsWithSuper(Class<?> clazz, FieldCallback fieldCallback) {
        while (!Object.class.equals(clazz)) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                try {
                    fieldCallback.doWith(f);
                } catch (Exception ex) {
                    Log.e(TAG, "ReflectionUtils.doWithFieldsWithSuper error", ex);
                }
            }
            clazz = clazz.getSuperclass();
        }

    }

    /**
     * ************************************** modifier ****************************************
     */
    public static boolean isModifier(Field field, int modifier) {
        return (field.getModifiers() & modifier) == modifier;
    }

    public static boolean isModifier(Method method, int modifier) {
        return (method.getModifiers() & modifier) == modifier;
    }

    public static boolean isModifier(Class clazz, int modifier) {
        return (clazz.getModifiers() & modifier) == modifier;
    }

    /**
     * field是否全部符合modifers
     *
     * @param field
     * @param modifiers
     * @return
     */
    public static boolean isModifierAnd(Field field, int... modifiers) {
        if (TextUtil.isEmpty(modifiers)) {
            return true;
        }
        boolean isModifierAnd = true;
        for (int m : modifiers) {
            isModifierAnd = isModifierAnd && isModifier(field, m);
        }
        return isModifierAnd;
    }

    /**
     * method是否全部符合modifers
     *
     * @param method
     * @param modifiers
     * @return
     */
    public static boolean isModifierAnd(Method method, int... modifiers) {
        if (TextUtil.isEmpty(modifiers)) {
            return true;
        }
        boolean isModifierAnd = true;
        for (int m : modifiers) {
            isModifierAnd = isModifierAnd && isModifier(method, m);
        }
        return isModifierAnd;
    }

    /**
     * class是否全部符合modifers
     *
     * @param clazz
     * @param modifiers
     * @return
     */
    public static boolean isModifierAnd(Class clazz, int... modifiers) {
        if (TextUtil.isEmpty(modifiers)) {
            return true;
        }
        boolean isModifierAnd = true;
        for (int m : modifiers) {
            isModifierAnd = isModifierAnd && isModifier(clazz, m);
        }
        return isModifierAnd;
    }

    /**
     * Field是否符合部分modifers
     *
     * @param field
     * @param modifiers
     * @return
     */
    public static boolean isModifierOr(Field field, int... modifiers) {
        if (TextUtil.isEmpty(modifiers)) {
            return true;
        }
        boolean isModifierAnd = false;
        for (int m : modifiers) {
            isModifierAnd = isModifierAnd || isModifier(field, m);
        }
        return isModifierAnd;
    }

    /**
     * Field是否符合部分modifers
     *
     * @param method
     * @param modifiers
     * @return
     */
    public static boolean isModifierOr(Method method, int... modifiers) {
        if (TextUtil.isEmpty(modifiers)) {
            return true;
        }
        boolean isModifierAnd = false;
        for (int m : modifiers) {
            isModifierAnd = isModifierAnd || isModifier(method, m);
        }
        return isModifierAnd;
    }

    /**
     * clazz是否符合部分modifers
     *
     * @param clazz
     * @param modifiers
     * @return
     */
    public static boolean isModifierOr(Class clazz, int... modifiers) {
        if (TextUtil.isEmpty(modifiers)) {
            return true;
        }
        boolean isModifierAnd = false;
        for (int m : modifiers) {
            isModifierAnd = isModifierAnd || isModifier(clazz, m);
        }
        return isModifierAnd;
    }



    /**
     * 新建实例
     * @param className  类名
     * @param args    构造函数的参数
     * 如果无构造参数，args 填写为 null
     * @return 新建的实例
     * @throws Exception
     */
    public Object newInstance(String className, Object[] args,Class[] argsType) throws NoSuchMethodException, SecurityException, ClassNotFoundException,
            InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException
    {
        Class newoneClass = Class.forName(className);

        if(args == null){
            return newoneClass.newInstance();

        }else{
            Constructor cons = newoneClass.getConstructor(argsType);

            return cons.newInstance(args);
        }

    }


    /**
     * 是不是某个类的实例
     * @param obj 实例
     * @param cls 类
     * @return 如果 obj 是此类的实例，则返回 true
     */
    public boolean isInstance(Object obj, Class cls) {
        return cls.isInstance(obj);
    }


    /**
     * 获取包名下的类集合
     * 此方法需要在multiDex下验证效果
     * @param pPackage
     * @return
     */
    public Class<?> getClassListByPackage(String pPackage) {
        Package _Package = Package.getPackage(pPackage);
        Class<?> _List =   _Package.getClass();

        return _List;
    }

}
