//package com.mybatis.zip;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.io.IOException;
//
//public class NullAppendable implements Appendable {
//    static Appendable INSTANCE = new NullAppendable();
//
//    @NotNull
//    public Appendable append(CharSequence csq) throws IOException {
//        return this;
//    }
//
//    @NotNull
//    public Appendable append(CharSequence csq, int start, int end) throws IOException {
//        return this;
//    }
//
//    @NotNull
//    public Appendable append(char c) throws IOException {
//        return this;
//    }
//}
