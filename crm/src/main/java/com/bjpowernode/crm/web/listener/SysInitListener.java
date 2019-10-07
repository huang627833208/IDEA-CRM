package com.bjpowernode.crm.web.listener;

import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.settings.service.impl.DicServiceImpl;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

/**
 * 作者：杜聚宾
 */
public class SysInitListener implements ServletContextListener {

    /*

        application对象===上下文对象===全局域对象

        该方法是用来做application对象创建时监听用的方法

        当服务启动，服务器为我们创建出来一个application对象，对象创建完毕后，则立即执行该方法

        参数event：我们可以通过该参数取得监听的域对象，例如我们现在监听的是application对象，
                    那么我们就可以通过event参数来取得application对象

     */
    @Override
    public void contextInitialized(ServletContextEvent event) {

        System.out.println("application对象创建了");

        ServletContext application = event.getServletContext();

        /*

            取得数据字典中的数据，保存到application域对象中

         */

        DicService ds = (DicService) ServiceFactory.getService(new DicServiceImpl());

        /*

            我们现在要让业务层取出7个dvList
            然后将7个dvList保存到map中
            Map map
            map.put("appellationList",dvList1);
            map.put("clueStateList",dvList2);
            map.put("stageList",dvList3);
            ...



         */
        Map<String,List<DicValue>> map = ds.getAll();

        //将map中的7组键值对，保存成application中的7组键值对

        Set<String> set = map.keySet();
        for(String key:set){

            application.setAttribute(key, map.get(key));

        }


        //================================================================

        System.out.println("处理阶段和可能性之间的对应关系");

        /*

            解析StageToPossibility.properties

            将该文件中的9组键值对取出，保存成java中的键值对map

            将map保存到application中


         */

        /*

            ResourceBundle是抽象类，不能直接new对象，要以类方法ResourceBundle.getBundle来取得对象
            参数中引入的文件，不要加后缀名

         */

        Map<String,String> pMap = new HashMap<>();

        ResourceBundle rb = ResourceBundle.getBundle("Stage2Possibility");

        Enumeration<String> e = rb.getKeys();

        while(e.hasMoreElements()){

            String stage = e.nextElement();

            String possibility = rb.getString(stage);

            pMap.put(stage, possibility);

        }

        application.setAttribute("pMap", pMap);


    }
}




























