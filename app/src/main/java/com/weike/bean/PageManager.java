package com.weike.bean;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by majin on 2017/4/7.
 */

public class PageManager {


    private List<PageModel> listpage;//装所有页数数据的集合（每个listitempage就是一页的数据）
    private PageModel       prePageData, nextPageData, currentPageData;

    private Context context;
    private int     currentpage = 1;
    private int     totalpage   = 1;


    public PageManager(Context context) {
        this.context = context;

        this.listpage = new ArrayList<>();
        ModelBuilding(false);//初始化 增加一页pagemodle实例
    }


    /*增加页面*/
    public void add_pad() {
        ModelBuilding(true);
        currentpage++;
        totalpage++;
    }

    /*移除页面*/
    public void remove_pad() {
        PageModel removePageModel = null;
        if (currentpage > 1) {//总页数>1
            currentpage--;
            totalpage--;

            if (listpage.size() > 1) {
                //加页前模型
                removePageModel = listpage.get(currentpage);
            }
            ///TODO 保存前一页数据
            saveAddPageDate(removePageModel);

            ///TODO 清理前一页视图数据
            cleanAddPageModel(removePageModel);

            listpage.remove(currentpage);
            currentPageData = listpage.get(currentpage - 1);//上一页后的数据模型 显示页的数据
            currentPageData.showCurrentPage(currentPageData);


        }
    }

    /*上一 页面*/
    public void pre_pad() { //首先保存

        if (currentpage > 1) {//总页数>1
            prePageData = listpage.get(currentpage - 1);//上一页前的数据模型 销毁页的数据
            ///TODO 保存前一页数据

            ///TODO 删除前一页视图
            cleanAddPageModel(prePageData);
            currentpage--;
            //TODO 显示当前页数据
            currentPageData = listpage.get(currentpage - 1);//上一页后的数据模型 显示页的数据
            //先显示 显示是重新插入图片 在进行保存新的flag值  显示完毕后删除图片
            currentPageData.showCurrentPage(currentPageData);


        }


    }

    /*下一 页面*/
    public void next_pad() {
        if (currentpage < totalpage) {

            nextPageData = listpage.get(currentpage - 1);//上一页前的数据模型
            // /TODO 保存前一页数据

            ///TODO 删除前一页视图数据
            cleanAddPageModel(nextPageData);
            currentpage++;
            //TODO 显示当前页数据
            currentPageData = listpage.get(currentpage - 1);//上一页后的数据模型
            currentPageData.showCurrentPage(currentPageData);


        }


    }

    /*页面总数*/
    public int total() {

        return totalpage;
    }

    /*当前页面数*/
    public int currentPage() {
        return currentpage;

    }

    /*建立模型（加页或者插入一页）*/
    public void ModelBuilding(boolean addpage) {
        PageModel addPageDate = null;// 销毁頁的的數據
        PageModel pageModel = new PageModel(context );//增加一页就实例化一次

        if (listpage.size() > 0) {
            addPageDate = listpage.get(currentpage - 1);//加页前模型对象
        }
        ///TODO 保存前一页数据
        saveAddPageDate(addPageDate);

        ///TODO 清理前一页视图数据
        cleanAddPageModel(addPageDate);

        if (addpage == true) {   //插入一页  加入到管理pageModle的集合中
            listpage.add(currentpage, pageModel);
        } else {
            listpage.add(pageModel);//增加一页
        }

    }

    /*保存前一页数据*/
    private void saveAddPageDate(PageModel addPageDate) {
        //添加页前的模型  和 添加页后的模型


    }


    /*清理前一页视图数据 (加页或者插入页)*/
    private void cleanAddPageModel(PageModel addPageDate) {


    }
}
