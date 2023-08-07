//package com.mybatis.listener;
//
//import com.intellij.openapi.wm.ToolWindow;
//import com.intellij.openapi.wm.ToolWindowManager;
//import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.Arrays;
//
//public class MybatisToolWindowListener implements ToolWindowManagerListener {
//    @Override
//    public void stateChanged(@NotNull ToolWindowManager toolWindowManager) {
//        ToolWindowManagerListener.super.stateChanged(toolWindowManager);
//        System.out.println(Arrays.toString(toolWindowManager.getToolWindowIds()));
//    }
//
//    @Override
//    public void toolWindowShown(@NotNull ToolWindow toolWindow) {
//        ToolWindowManagerListener.super.toolWindowShown(toolWindow);
//        System.out.println(toolWindow.getTitle());
//    }
//}
