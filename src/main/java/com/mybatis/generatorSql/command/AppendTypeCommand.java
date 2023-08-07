package com.mybatis.generatorSql.command;


import com.mybatis.generatorSql.appender.SyntaxAppender;

import java.util.Optional;

public interface AppendTypeCommand {
    /**
     * Execute optional.
     *
     * @return the optional
     */
    Optional<SyntaxAppender> execute();
}
