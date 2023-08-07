package com.mybatis.disposable;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.NotNull;

public class MyDisposable implements Disposable {
    public MyDisposable(@NotNull Disposable disposable) {
        Disposer.register(disposable, this);
    }

    @Override
    public void dispose() {

    }
}
