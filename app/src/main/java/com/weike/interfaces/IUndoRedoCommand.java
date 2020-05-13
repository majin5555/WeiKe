package com.weike.interfaces;
public interface IUndoRedoCommand {

    public void pageDataRedo();//页面数据左回撤
    public void pageDataUndo();//页面数据右回撤

}
